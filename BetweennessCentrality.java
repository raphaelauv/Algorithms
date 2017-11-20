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
			Queue<Node> struck = new LinkedList<>();
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

class Let_Bet_V implements Function<Node,String> {
	
	Node[] listNodes;
	boolean oriented;
	
	
	public Let_Bet_V(Node[] listNodes,boolean oriented) {
		this.listNodes=listNodes;
		this.oriented=oriented;
		
	}
	
	@Override
	public String apply(Node nodeV) {
		
		double cmp=0;
		Node nodeS;
		Node nodeT;
		for(int i=0;i<listNodes.length;i++) {
			for(int j=0;j<listNodes.length;j++) {
				nodeS = listNodes[i];
				nodeT = listNodes[j];
				
				if(nodeS!=nodeT && nodeS!=nodeV && nodeT!=nodeV) {
					cmp+=bet_SVT(nodeS,nodeV,nodeT);
				}
			}
		}
		
		return "node: "+nodeV.id+" : "+BetweennessCentrality.firstPartEquation * cmp;
		 
	}

	private double bet_SVT(Node nodeS,Node nodeV,Node nodeT) {
		Tuple tupleT = nodeV.getTuple(nodeT);
		Tuple tupleS = nodeV.getTuple(nodeS);
		
		if(tupleT ==null || tupleS==null) {
			return (double) 0;
		}
		
		Integer distanceVT = tupleT.distance;
		Integer distanceVS = tupleS.distance;
		
		int distanceST = nodeS.getDistanceOf(nodeT);
		boolean vInsidePCC = false;
		int distanceVSandVT = distanceVS + distanceVT;
		

		boolean wayST = false;
		boolean wayTS = false;
		
		if(oriented) {
			int distanceTS = nodeT.getDistanceOf(nodeS);
			if(distanceVSandVT == distanceST) {
				vInsidePCC=true;
				wayST=true;
			}
			if(distanceVSandVT == distanceTS) {
				vInsidePCC=true;
				wayTS=true;
			}
			
			
		}else {
			if(distanceVSandVT == distanceST) {
				vInsidePCC=true;
			}
		}
		
		
		if(!vInsidePCC) {
			return (double) 0;
		}
		
		int nbpccSVT;
		int nbpccSV = nodeS.getNbpccOf(nodeV);
		int nbpccVT = nodeT.getNbpccOf(nodeV);
		if(oriented) {
			
			if(wayST && wayTS) {
				nbpccSVT = nbpccSV *nbpccVT;
			}else if(wayST){
				nbpccSVT=0; //TODO
			}else {
				nbpccSVT=0; //TODO
			}
			
		}else {
			
			nbpccSVT = nbpccSV *nbpccVT;
		}
				
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
						actualNode.insert(aNeighbour, new Tuple(maxDistance_of_D+1,actualtmpTuple.nbcc));
					}	
					
					stack.add(aNeighbour);
				}else {
					actualDistance=NtmpTuple.distance;
					if(actualDistance==maxDistance_of_D+1) {
						NtmpTuple.nbcc+=actualtmpTuple.nbcc;
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
		
		Node[] listNodes = myGraph.getListeIds();
		firstPartEquation = 1 / (double) ( (myGraph.size()-1) * (myGraph.size()-2) );
		
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		FixedDataStruckPool dataPool=new FixedDataStruckPool(corePoolSize);
		
		Stream<Node> streamOfNodes =myGraph.mapNodes.values().stream().parallel();
		Stream<EmptyResult> streamOfResults = streamOfNodes.map(new Let_BFS(dataPool));
		streamOfResults.forEach(x-> {});
		
		
		Stream<Node> streamOfNodes2 =myGraph.mapNodes.values().stream().parallel();
		Stream<String> streamOfResults2 = streamOfNodes2.map(new Let_Bet_V(listNodes,myGraph.oriented));
		streamOfResults2.forEach(s -> System.out.println(s));

		long endTime = System.nanoTime();
		System.out.println(endTime - startTime);
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