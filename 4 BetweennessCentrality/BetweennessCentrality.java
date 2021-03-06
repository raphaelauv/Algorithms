import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;
import java.util.stream.Stream;

class FixedDataStruckPool{
	private int nbStruck;	
	private int nbStackCreated;
	private ConcurrentLinkedDeque<Queue<Node>> listStack;

	public FixedDataStruckPool(int nbStruck) {
		this.nbStruck = nbStruck+1;
		this.listStack = new ConcurrentLinkedDeque<>();
	}
	public Queue<Node> getStack() {
		if(nbStackCreated<nbStruck) {
			Queue<Node> struck = new ArrayDeque<>();
			nbStackCreated++;
			return struck;
		}else {
			return listStack.poll();
		}
	}
	
	public void realeaseStack(Queue<Node>struck) {
		struck.clear();
		listStack.addFirst(struck);
	}
}



class EmptyResult{
}

class Let_Bet_V implements Function<Node,EmptyResult> {
	
	ArrayList<Node> listNodes;
	boolean oriented;
	public Let_Bet_V(ArrayList<Node> listNodes,boolean oriented) {
		this.listNodes=listNodes;
		this.oriented=oriented;
	}
	
	@Override
	public EmptyResult apply(Node nodeV) {
		
		double cmp=0;
		Node nodeS;
		Node nodeT;
		for(int i=0;i<listNodes.size();i++) {
			for(int j=0;j<listNodes.size();j++) {
				if(i==j) {
					continue;
				}
				nodeS = listNodes.get(i);
				nodeT = listNodes.get(j);
				
				if(nodeS!=nodeV) {
					cmp+=bet_SVT(nodeS,nodeV,nodeT);
				}
			}
		}
		
		System.out.println("node: "+nodeV.id+" : "+BetweennessCentrality.firstPartEquation * cmp);
		return null;
		 
	}

	private double bet_SVT(Node nodeS,Node nodeV,Node nodeT) {
		Tuple tupleT = nodeV.getTuple(nodeT);
		Tuple tupleS = nodeV.getTuple(nodeS);
		if(tupleT ==null || tupleS==null) {
			return (double) 0;
		}
		
		int distanceST = nodeS.getDistanceOf(nodeT);
		
		int distanceVSandVT = tupleS.distance + tupleT.distance;
		
		if(distanceVSandVT != distanceST) {
			return (double) 0;
		}
		
		int nbpccSV = nodeS.getNbpccOf(nodeV);
		int nbpccVT = nodeT.getNbpccOf(nodeV);

		int nbpccSVT = nbpccSV *nbpccVT;
		
		int nbpccST = nodeS.getNbpccOf(nodeT);	
		return nbpccSVT / (double) (nbpccST);
	}
}

class Let_BFS implements Function<Node,EmptyResult>{

	FixedDataStruckPool dataPool;
	public Let_BFS(FixedDataStruckPool dataPool) {
		this.dataPool=dataPool;
	}
	
	public EmptyResult apply(Node actualNode) {
		
		Queue<Node> stack = dataPool.getStack();
		stack.add(actualNode);

		Integer actualDistance =0;
		int maxDistance_of_D = 0;
		
		actualNode.insert(actualNode, new Tuple(actualDistance,0));

		Node tmpNode;
		Tuple actualtmpTuple = null;
		Tuple NtmpTuple;
		boolean first=true;
		
		while (!stack.isEmpty()) {
			tmpNode = stack.poll();
			actualtmpTuple=actualNode.getTuple(tmpNode);
			actualDistance=actualtmpTuple.distance;
			
			if(actualDistance>maxDistance_of_D) {
				maxDistance_of_D++;
			}
			
			for (Node aNeighbour : tmpNode.directNeighbours) {
				NtmpTuple = actualNode.getTuple(aNeighbour);
				
				if(NtmpTuple==null) {
					if(first) {
						actualNode.insert(aNeighbour, new Tuple(maxDistance_of_D+1,1));	
					}else {
						actualNode.insert(aNeighbour, new Tuple(maxDistance_of_D+1,actualtmpTuple.nbpcc));
					}
					stack.add(aNeighbour);
				}else {
					actualDistance=NtmpTuple.distance;
					if(actualDistance==maxDistance_of_D+1) {
						NtmpTuple.nbpcc+=actualtmpTuple.nbpcc;
					}
				}
			}
			first=false;
		}
		dataPool.realeaseStack(stack);
		//actualNode.printfAllmyNeighboursInfo();
		return null;
	}
}


public class BetweennessCentrality {
	
	public static double firstPartEquation;
	
	public static void printAllBetweennessCentrality(Graph myGraph) {
		
		long startTime = System.nanoTime();
		
		ArrayList<Node> listNodes = myGraph.getListeNodes();
		
		firstPartEquation = 1 / (double) ( (listNodes.size()-1) * (listNodes.size()-2) );
		
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		
		FixedDataStruckPool dataPool=new FixedDataStruckPool(corePoolSize);
		
		Stream<Node> streamOfNodes = listNodes.stream().parallel();
		Stream<EmptyResult> streamOfResults = streamOfNodes.map(new Let_BFS(dataPool));
		streamOfResults.forEach(x-> {});
		
		Stream<Node> streamOfNodes2 = listNodes.stream().parallel();
		Stream<EmptyResult> streamOfResults2 = streamOfNodes2.map(new Let_Bet_V(listNodes,myGraph.oriented));
		streamOfResults2.forEach(x -> {});
		
		long endTime = System.nanoTime();
		System.out.println(endTime - startTime);
		
		
		//System.out.println("nb nodes : "+listNodes.size());
		//System.out.println("nb edges : "+myGraph.nbEdges);
	}

	
	
	public static void main(String[] args) {
		if (args.length < 1) {
			ManageInput.missingArgs();
			return;
		}
		Graph myGraph = ManageInput.creatGraph(args);
		if(myGraph==null) {return;}
		
		ManageInput.printMemoryStart();
		printAllBetweennessCentrality(myGraph);
		ManageInput.printMemoryEND();
		
	}
}