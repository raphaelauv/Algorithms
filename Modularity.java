import java.util.ArrayDeque;
import java.util.ArrayList;
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
	public Let_Bet_V(ArrayList<Node> listNodes) {
		this.listNodes=listNodes;
	}
	
	@Override
	public EmptyResult apply(Node nodeV) {
	
		
		return null;
		 
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

		dataPool.realeaseStack(stack);
		//actualNode.printfAllmyNeighboursInfo();
		return null;
	}
}


public class Modularity {
	
	
	public static void printAllBetweennessCentrality(Graph myGraph) {
		
		//long startTime = System.nanoTime();
	
		
		ArrayList<Node> listNodes = myGraph.getListeNodes();
	
		int nbNodes = listNodes.size();
		Partition myParti = new Partition(myGraph);
		
		double qIter;
		double qMax=0;
		
		int nbParitions=0;
		double qpPrime;
		int [] pSuiv= new int[2];
		
		for (int i=0;i<nbNodes;i++) {
			qIter=-1;
			for( int a=0;a<nbNodes;a++) {
				for( int b=0;b<nbNodes;b++) {
					if(a==b) {
						continue;
					}
					if(!myParti.isChefs(a,b)) {
						continue;
					}
					qpPrime = myParti.getQP(a,b);
					if(qpPrime>qIter) {
						pSuiv[0]=a;
						pSuiv[1]=b;
						qIter=qpPrime;
					}
					
				}
			}
			
			myParti.performFusion(pSuiv[0],pSuiv[1]);
			if(qIter>qMax) {
				qMax=qIter;
			}
			
		}
		
		
		//print(myParti);
		return null;
		/*
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		
		FixedDataStruckPool dataPool=new FixedDataStruckPool(corePoolSize);
		
		Stream<Node> streamOfNodes = listNodes.stream().parallel();
		Stream<EmptyResult> streamOfResults = streamOfNodes.map(new Let_BFS(dataPool));
		streamOfResults.forEach(x-> {});
		
		Stream<Node> streamOfNodes2 = listNodes.stream().parallel();
		Stream<EmptyResult> streamOfResults2 = streamOfNodes2.map(new Let_Bet_V(listNodes));
		streamOfResults2.forEach(x -> {});
		*/
		//long endTime = System.nanoTime();
		//System.out.println(endTime - startTime);
		
		
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