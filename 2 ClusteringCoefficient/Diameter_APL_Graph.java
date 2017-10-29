import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

class FixedDataStruckPool{
	private int nbStruck;
	private int nbHashMapCreated;
	private int nbStackCreated;
	
	private ConcurrentLinkedDeque<HashMap<Node, Integer>> listMap;
	private ConcurrentLinkedDeque<Queue<Node>> listStack;
	
	 
	public FixedDataStruckPool(int nbStruck) {
		this.nbStruck = nbStruck+1;
		this.nbHashMapCreated = 0;
		this.listMap = new ConcurrentLinkedDeque<>();
		this.listStack = new ConcurrentLinkedDeque<>();
	}
	
	public HashMap<Node, Integer> getHashMap() {
		if(nbHashMapCreated<nbStruck) {
			HashMap<Node, Integer> struck = new HashMap<>();
			nbHashMapCreated++;
			return struck;
		}else {
			return listMap.poll();
		}
	}
	public Queue<Node> getStack() {
		if(nbStackCreated<nbStruck) {
			Queue<Node> struck = new LinkedList<>();
			nbStackCreated++;
			return struck;
		}else {
			return listStack.poll();
		}
	}
	
	public void realeaseHashMap(HashMap<Node, Integer> struck) {
		struck.clear();
		listMap.addFirst(struck);
	}
	
	public void realeaseStack(Queue<Node>struck) {
		
		struck.clear();
		
		listStack.addFirst(struck);
	}
}

class ResultBFS {
	public int diameter;
	public long nbVertices;
	public long sumOfallVertices;
	public ResultBFS(int diameter, long nbVertices, long sumOfallVertices) {
		this.diameter = diameter;
		this.nbVertices = nbVertices;
		this.sumOfallVertices = sumOfallVertices;
	}
	public String toString() {
		double APL = sumOfallVertices /((double) nbVertices);
		return "Diameter : "+diameter+" | SumAllVertices : "+sumOfallVertices+" | NbVertices : "+nbVertices+" | APL :"+APL;
	}
}

class sumResult implements BinaryOperator<ResultBFS>{

	@Override
	public ResultBFS apply(ResultBFS t, ResultBFS u) {
		//return new ResultBFS((t.diameter>u.diameter) ? t.diameter : u.diameter, t.nbVertices+u.nbVertices,t.sumOfallVertices+u.sumOfallVertices);
		
		if(t.diameter<u.diameter) {
			t.diameter=u.diameter;
		}
		t.nbVertices+=u.nbVertices;
		t.sumOfallVertices+=u.sumOfallVertices;
		
		return t;

	}
}


class Let_BFS implements Function<Node,ResultBFS>{

	FixedDataStruckPool dataPool;

	public Let_BFS(FixedDataStruckPool dataPool) {
		this.dataPool=dataPool;
	}
	
	public ResultBFS apply(Node actualNode) {
		
		int nbVertices=0;
		int sumOfallVertices=0;
		
		Queue<Node> stack = dataPool.getStack();
		stack.add(actualNode);

		Integer actualDistance =0;
		int maxDistance_of_D = 0;
		HashMap<Node, Integer> nodesAlreadySeen= dataPool.getHashMap();
		nodesAlreadySeen.put(actualNode, actualDistance);

		Node tmpNode;
		while (!stack.isEmpty()) {
			tmpNode = stack.poll();
			actualDistance=nodesAlreadySeen.get(tmpNode);
			if(actualDistance>maxDistance_of_D) {
				maxDistance_of_D++;
			}
			
			for (Node aNeighbour : tmpNode.neighbours) {
				actualDistance=nodesAlreadySeen.get(aNeighbour);
				if(actualDistance==null) {
					nbVertices++;
					sumOfallVertices+=maxDistance_of_D+1;
					nodesAlreadySeen.put(aNeighbour, maxDistance_of_D+1);
					stack.add(aNeighbour);
				}
			}
		}
		dataPool.realeaseStack(stack);
		dataPool.realeaseHashMap(nodesAlreadySeen);
		return new ResultBFS(maxDistance_of_D,nbVertices,sumOfallVertices);
	}
}


class BFS implements Callable<ResultBFS> {
	
	Node actualNode;
	FixedDataStruckPool dataPool;

	public BFS(Node node,FixedDataStruckPool dataPool) {
		this.actualNode=node;
		this.dataPool=dataPool;
	}
	
	public ResultBFS call() throws Exception {
		return new Let_BFS(dataPool).apply(actualNode);
	}

}


public class Diameter_APL_Graph {
	
	public static void diameter_and_APL_ofGraph_Executor(Graph myGraph) {
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		FixedDataStruckPool dataPool=new FixedDataStruckPool(corePoolSize);
		
		ExecutorService execute = Executors.newFixedThreadPool(corePoolSize);
		CompletionService<ResultBFS> completion = new ExecutorCompletionService<>(execute);
		Iterator<Node> itNodes = myGraph.mapNodes.values().iterator();
		
		int nbTaskCreate = 0;
		while (itNodes.hasNext()) {
			completion.submit(new BFS(itNodes.next(),dataPool));
			nbTaskCreate++;
		}

		int diameter = 0;
		long nbVertices=0;
		long sumOfallVertices=0;
		ResultBFS result=null;
		for (int i = 0; i < nbTaskCreate; i++) {
			try {
				result=completion.take().get();
				if(result.diameter>diameter) {
					diameter=result.diameter;
				}
				nbVertices+=result.nbVertices;
				sumOfallVertices+=result.sumOfallVertices;
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("ERROR OCCURED");
				execute.shutdownNow();
				return;
			}
		}
		execute.shutdown();
		System.out.println(new ResultBFS(diameter, nbVertices, sumOfallVertices));
		
	}
	
	public static void diameter_and_APL_ofGraph_Stream(Graph myGraph) {
		
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		
		FixedDataStruckPool dataPool=new FixedDataStruckPool(corePoolSize);
		Stream<Node> streamOfNodes =myGraph.mapNodes.values().stream().parallel();
		Stream<ResultBFS> streamOfResults = streamOfNodes.map(new Let_BFS(dataPool));
		Optional<ResultBFS> rst=streamOfResults.reduce(new sumResult());
		System.out.println(rst.get());
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			ManageInput.missingArgs();
			return;
		}
		Graph myGraph = ManageInput.creatGraph(args[0]);
		if(myGraph==null) {return;}
		
		ManageInput.printMemoryStart();
		diameter_and_APL_ofGraph_Stream(myGraph);
		//diameter_and_APL_ofGraph_Executor(myGraph);
		ManageInput.printMemoryEND();
		
	}
}