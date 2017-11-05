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
	private double APL;
	public ResultBFS(int diameter, long nbVertices, long sumOfallVertices) {
		this.diameter = diameter;
		this.nbVertices = nbVertices;
		this.sumOfallVertices = sumOfallVertices;
	}
	public String toString() {
		getAPL();
		return "Diameter : "+diameter+"\nAPL :"+APL;
	}
	public double getAPL() {
		APL = sumOfallVertices /((double) nbVertices);
		return APL;
	}
}

class sumResult implements BinaryOperator<ResultBFS>{

	@Override
	public ResultBFS apply(ResultBFS t, ResultBFS u) {
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

public class Diameter_APL_Graph {
	
	public static ResultBFS diameter_and_APL_ofGraph_Stream(Graph myGraph) {
		
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		FixedDataStruckPool dataPool=new FixedDataStruckPool(corePoolSize);
		
		Stream<Node> streamOfNodes =myGraph.mapNodes.values().stream().parallel();
		Stream<ResultBFS> streamOfResults = streamOfNodes.map(new Let_BFS(dataPool));
		Optional<ResultBFS> rst=streamOfResults.reduce(new sumResult());
		return rst.get();	
	}

}