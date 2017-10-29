import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

class Graph {
	
	LinkedHashMap<Integer, Node> mapNodes;
	
	public Graph() {
		this.mapNodes = new LinkedHashMap<>();
	}
	
	public void addEdge(int actualID, int neighbourID) {
		Node actualNode = this.mapNodes.computeIfAbsent(actualID,k ->new Node(actualID));
		if(actualID!=neighbourID) {
			Node neighbourNode = this.mapNodes.computeIfAbsent(neighbourID,k ->new Node(neighbourID));
			actualNode.insertEdge(neighbourNode);
			neighbourNode.insertEdge(actualNode);
		}
	}
}

class Node {
	
	final int id;
	Collection<Node> neighbours;
	private LongAdder nbInsideTriangle;						//only for averageClusteringCoefficient
	
	public Node(int id) {
		this.id = id;
		this.neighbours = new LinkedHashSet<>(); 			//better than linkedList for the parallele version without marqued technique
		this.nbInsideTriangle = new LongAdder();			//only for Average
	}

	
	public void insertEdge(Node neighbour) {
		this.neighbours.add(neighbour);
	}
	
	/*
	 * only for averageClusteringCoefficient
	 */
	public static void incrementNbTriangle(Node node0, Node node1, Node node2) {
		node0.nbInsideTriangle.increment();
		node1.nbInsideTriangle.increment();
		node2.nbInsideTriangle.increment();
	}
	public int getNbTriangle() {
		return ((int) (this.nbInsideTriangle.sum())) / 2;
	}

}

class ResulGlobal {
	int nbV;
	int nbTri;
	public ResulGlobal(int nbV, int nbTri) {
		this.nbV = nbV;
		this.nbTri = nbTri;
	}
	@Override
	public String toString() {
		double cluGraph = (3 * nbTri) /(double)nbV;
		return "nb Tri : "+nbTri+" | nb V : "+nbV+" | CLU_G : "+cluGraph;
	}
	
	
}

class sumResultGlobal implements BinaryOperator<ResulGlobal>{

	@Override
	public ResulGlobal apply(ResulGlobal t, ResulGlobal u) {
		//return new ResulGlobal(t.nbV+u.nbV, t.nbTri+u.nbTri);
		t.nbTri+=u.nbTri;
		t.nbV+=u.nbV;
		return t;
	}
}


class Let_FindNbTriangles implements Function<Node,ResulGlobal> {
	
	public Let_FindNbTriangles() {}
	
	boolean isAverageClusterMode;
	public Let_FindNbTriangles(boolean isAverageClusterMode) {
		this.isAverageClusterMode=isAverageClusterMode;
	}
	
	@Override
	public ResulGlobal apply(Node node) {
		
		int nbTri = 0;
		int myDegree = node.neighbours.size();
		if(myDegree<2) {
			return new ResulGlobal(0, 0);
		}
		
		int neighbourDegree;
		for (Node aNeighbour : node.neighbours) {
			neighbourDegree=aNeighbour.neighbours.size();
			
			if(neighbourDegree>myDegree) {
				continue;
			}else if(neighbourDegree==myDegree) {
				if(aNeighbour.id>node.id) {
					continue;
				}
			}
			for (Node aNN : aNeighbour.neighbours) {
				if(aNN.id==node.id) { 
					continue;//do not look for myself
				}
				
				neighbourDegree=aNN.neighbours.size();
				
				if(neighbourDegree>myDegree) {
					continue;
				}else if(neighbourDegree==myDegree) {
					if(aNN.id>node.id) {
						continue;
					}
				}
				if(node.neighbours.contains(aNN)) {
					nbTri++;
					if(isAverageClusterMode) {
						Node.incrementNbTriangle(node, aNeighbour, aNN);
					}
				}
			}
		}
		//not use in case of isAverageClusterMode
		
		nbTri /=2;//because we count each triangle in the two ways possible
		int nbV = (myDegree * (myDegree - 1) / 2);
		return new ResulGlobal(nbV,nbTri);
		
	}
}


class FindNbTriangles implements Callable<Integer> {

	private Node node;
	boolean isAverageClusterMode;
	
	public FindNbTriangles(Node node,boolean isAverageClusterMode) {
		this.node = node;
		this.isAverageClusterMode=isAverageClusterMode;
	}
	
	@Override
	public Integer call() throws Exception {		
		return new Let_FindNbTriangles(isAverageClusterMode).apply(node).nbTri;
	}
}


public class GlobalClusteringCoefficient {
	
	public static void globalClusteringCoefficient_Executor(Graph myGraph) {
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		ExecutorService execute = Executors.newFixedThreadPool(corePoolSize);
		CompletionService<Integer> completion = new ExecutorCompletionService<>(execute);
	
		Node tmpNode;
		int degreeTmpNode;
		long nbV = 0;
		int nbTri = 0;
		int nbTaskToManage = 0;
		
		/*
		int nbNodeToDo = this.mapNodes.values().size();
		int nbResult=0;
		int pourcent = nbNodeToDo/100;
		int pourcentPrinted=0;
		Future<Integer> result;
		*/
		
		Iterator<Node> itNodes = myGraph.mapNodes.values().iterator();
		while (itNodes.hasNext()) {
			tmpNode = itNodes.next();
			degreeTmpNode = tmpNode.neighbours.size();
			if (degreeTmpNode > 0) {
				nbV += (degreeTmpNode * (degreeTmpNode - 1) / 2);
			}
			completion.submit(new FindNbTriangles(tmpNode,false));
			nbTaskToManage++;
			/*
			 * unnecessary memory optimisation -> to limit the number of futurs inside CompletionService
			try {
				 if(nbTaskToManage>20){
				 	result=completion.take();
				 }else{
				 	result=completion.poll();	
				 }
				 if(result!=null) {
					 nbTaskToManage--;
					 nbTri += result.get();
					 nbResult++;
				}
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("ERROR OCCURED");
				execute.shutdownNow();
			}

			if (nbResult % pourcent == 0) {
				pourcentPrinted++;
				//System.out.println(pourcentPrinted + "% done");
			}
			*/
			
		}

		for(int i =0;i<nbTaskToManage;i++) {
			try {
				nbTri += completion.take().get();
				//nbResult++;
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("ERROR OCCURED");
				execute.shutdownNow();
				return;
			}
			/*
			if(nbResult% pourcent==0){
				pourcentPrinted++;
				System.out.println(pourcentPrinted+"% done");
			}
			*/
		}
		
		execute.shutdown();
		
		double cluGraph = (3 * nbTri) /(double)nbV;
		System.out.println("nb Tri : "+nbTri+" | nb V : "+nbV+" | CLU_G : "+cluGraph );

	}
	
	public static void globalClusteringCoefficient_Stream(Graph myGraph) {
		
		Stream<Node> streamOfNodes =myGraph.mapNodes.values().stream().parallel();
		Stream<ResulGlobal> streamOfResults = streamOfNodes.map(new Let_FindNbTriangles());
		Optional<ResulGlobal> rst=streamOfResults.reduce(new sumResultGlobal());
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
		globalClusteringCoefficient_Stream(myGraph);
		//globalClusteringCoefficient_Executor(myGraph);
		ManageInput.printMemoryEND();
        	
	}
}