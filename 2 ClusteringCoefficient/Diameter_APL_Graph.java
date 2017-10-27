import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ResultBFS {
	public final int diameter;
	public final int sommeOfallVertices;
	public final int nbVertices;
	public ResultBFS(int x, int y, int z) {
		this.diameter = x;
		this.sommeOfallVertices = y;
		this.nbVertices = z;
	}
}

/*
 * Diameter and APL job
 */
class BFS_OfX implements Callable<ResultBFS> {
	
	Node actualNode;
	FixedDataStruckPool dataPoll;

	public BFS_OfX(Node node,FixedDataStruckPool dataPoll) {
		this.actualNode=node;
		this.dataPoll=dataPoll;
	}
	

	public ResultBFS call() throws Exception {
		
		int nbVertices=0;
		int sommeOfallVertices=0;
		
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
					sommeOfallVertices+=maxDistance_of_D+1;
					nodesAlreadySeen.put(aNeighbour, maxDistance_of_D+1);
					stack.add(aNeighbour);
				}
			}
		}
		//System.out.println("node "+actualNode.id+" : "+maxDistance_of_D);
		
		//actualNode.setDiameterOfNode(maxDistance_of_D);
		
		dataPoll.realease(nodesAlreadySeen);
		return new ResultBFS(maxDistance_of_D,sommeOfallVertices,nbVertices);
	}
}


public class Diameter_APL_Graph {
	
	
	public static void diameter_and_APL_ofGraph(Graph myGraph) {
		
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		ExecutorService execute = Executors.newFixedThreadPool(corePoolSize);
		CompletionService<ResultBFS> completion = new ExecutorCompletionService<>(execute);

		
		FixedDataStruckPool dataPool=new FixedDataStruckPool(corePoolSize);
		Iterator<Node> itNodes = myGraph.mapNodes.values().iterator();
		
		int nbTaskCreate = 0;
		while (itNodes.hasNext()) {
			completion.submit(new BFS_OfX(itNodes.next(),dataPool));
			nbTaskCreate++;
		}

		int diameter = 0;
		long nbVertices=0;
		long sommeOfallVertices=0;
		//int acutalDistance =0;
		ResultBFS result=null;
		for (int i = 0; i < nbTaskCreate; i++) {
			try {
				result=completion.take().get();
				if(result.diameter>diameter) {
					diameter=result.diameter;
				}
				nbVertices+=result.nbVertices;
				sommeOfallVertices+=result.sommeOfallVertices;
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("ERROR OCCURED");
				execute.shutdownNow();
				return;
			}
		}
		execute.shutdown();
		
		double APL = sommeOfallVertices /(double) nbVertices;
		
		System.out.println("DIAMETER : " +diameter +" | Average path length : "+APL);
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