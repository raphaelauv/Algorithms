import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RecursiveTask;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

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
		return "diameter : "+diameter+" | "+sumOfallVertices+" "+nbVertices+" | APL :"+APL;
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

	FixedDataStruckPool dataPoll;

	public Let_BFS(FixedDataStruckPool dataPoll) {
		this.dataPoll=dataPoll;
	}
	
	public ResultBFS apply(Node actualNode) {
		
		int nbVertices=0;
		int sumOfallVertices=0;
		
		Queue<Node> stack = new LinkedList<>();
		Node tmpNode=actualNode;
		stack.add(tmpNode);

		Integer actualDistance =0;
		int maxDistance_of_D = 0;
		HashMap<Node, Integer> nodesAlreadySeen= dataPoll.getStruck();
		nodesAlreadySeen.put(tmpNode, actualDistance);

		
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
		
		dataPoll.realease(nodesAlreadySeen);
		return new ResultBFS(maxDistance_of_D,nbVertices,sumOfallVertices);
		
	}
	
}



/*
 * Diameter and APL job
 * 
 */
@Deprecated 
class BFS_OfX implements Callable<ResultBFS>,Function<Node,ResultBFS> {
	
	Node actualNode;
	FixedDataStruckPool dataPool;

	public BFS_OfX(Node node,FixedDataStruckPool dataPool) {
		this.actualNode=node;
		this.dataPool=dataPool;
	}
	
	public BFS_OfX(FixedDataStruckPool dataPool) {
		this.dataPool=dataPool;
	}

	public ResultBFS apply(Node actualNode) {

		this.actualNode=actualNode;
		try {
			return call();
		} catch (Exception e) {
			System.out.println("ERREUR");
			System.out.flush();
			return null;
		}
	}
	
	public ResultBFS call() throws Exception {
		
		int nbVertices=0;
		int sumOfallVertices=0;
		
		Queue<Node> stack = new LinkedList<>();
		Node tmpNode=actualNode;
		stack.add(tmpNode);

		Integer actualDistance =0;
		int maxDistance_of_D = 0;
		HashMap<Node, Integer> nodesAlreadySeen= dataPool.getStruck();
		nodesAlreadySeen.put(tmpNode, actualDistance);

		
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
		
		dataPool.realease(nodesAlreadySeen);
		return new ResultBFS(maxDistance_of_D,nbVertices,sumOfallVertices);
	}

}


public class Diameter_APL_Graph {
	
	
	public static void diameter_and_APL_ofGraph(Graph myGraph) {
		
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		FixedDataStruckPool dataPool=new FixedDataStruckPool(corePoolSize);
		
		Stream<Node> stream =myGraph.mapNodes.values().stream().parallel();
		Stream<ResultBFS> stream2 = stream.map(new Let_BFS(dataPool));
		Optional<ResultBFS> rst=stream2.reduce(new sumResult());
		System.out.println(rst.get());
	
		/* Executor version
		
		ExecutorService execute = Executors.newFixedThreadPool(corePoolSize);
		CompletionService<ResultBFS> completion = new ExecutorCompletionService<>(execute);
		Iterator<Node> itNodes = myGraph.mapNodes.values().iterator();
		
		int nbTaskCreate = 0;
		while (itNodes.hasNext()) {
			completion.submit(new BFS_OfX(itNodes.next(),dataPool));
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
		*/
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			ManageInput.missingArgs();
			return;
		}
		Graph myGraph = ManageInput.creatGraph(args[0]);
		if(myGraph==null) {return;}
		
		ManageInput.printMemoryStart();
		diameter_and_APL_ofGraph(myGraph);
		ManageInput.printMemoryEND();
		
	}
}