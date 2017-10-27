import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

class Node {
	
	final int id;
	Collection<Node> neighbours;
	private LongAdder nbInsideTriangle;						//only for averageClusteringCoefficient
	
	public Node(int id) {
		this.id = id;
		this.neighbours = new LinkedHashSet<>(); 			//better than linkedList for the parallele version without marqued technique
		this.nbInsideTriangle = new LongAdder();
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


class FixedDataStruckPool{
	private int nbStruck;
	private int nbCreated;
	private ConcurrentLinkedDeque<HashMap<Node, Integer>> listStruck;
	 
	public FixedDataStruckPool(int nbStruck) {
		this.nbStruck = nbStruck+4;
		this.nbCreated = 0;
		this.listStruck = new ConcurrentLinkedDeque<>();
	}
	public HashMap<Node, Integer> getStruck() {
		if(nbCreated<nbStruck) {
			HashMap<Node, Integer> struck = new HashMap<>();
			nbCreated++;
			return struck;
		}else {
			return listStruck.poll();
		}
	}
	public void realease(HashMap<Node, Integer> struck) {
		struck.clear();
		listStruck.addFirst(struck);
	}
	
	public int getNbAvailable() {
		return listStruck.size();
	}
}



/*
 * Cluster coefficient job
 */
class FindNbTriangles_OfX_WithOptimisation implements Callable<Integer> {

	private Node node;
	boolean isAverageClusterMode;
	
	public FindNbTriangles_OfX_WithOptimisation(Node node,boolean isAverageClusterMode) {
		this.node = node;
		this.isAverageClusterMode=isAverageClusterMode;
	}

	public Integer call() throws Exception {
		int nb = 0;
	
		int myDegree = node.neighbours.size();
		if(myDegree<2) {
			return 0;
		}
		
		int neighbourDegree;
		for (Node aNeighbour : node.neighbours) {
			neighbourDegree=aNeighbour.neighbours.size();
			
			if(neighbourDegree>myDegree) {
				continue;
			}else if(neighbourDegree==myDegree) {
				if(aNeighbour.id>this.node.id) {
					continue;
				}
			}
			for (Node aNN : aNeighbour.neighbours) {
				if(aNN.id==this.node.id) { 
					continue;//do not look for myself
				}
				
				neighbourDegree=aNN.neighbours.size();
				
				if(neighbourDegree>myDegree) {
					continue;
				}else if(neighbourDegree==myDegree) {
					if(aNN.id>this.node.id) {
						continue;
					}
				}
				if(node.neighbours.contains(aNN)) {
					nb++;
					if(isAverageClusterMode) {
						Node.incrementNbTriangle(this.node, aNeighbour, aNN);
					}
				}
			}
		}
		//not use in case of enPlace
		return nb /2;//because we count each triangle in the two ways possible
	}
}

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

final class ManageInput{
	
	private static void printMemory(String msg) {
		System.out.println(msg+" | Mémoire allouée : " +
		(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + "octets");
	}
	
	public static void printMemoryStart() {
		printMemory("FIN LECTURE FICHIER + CREATION GRAPH");
	}
	
	public static void printMemoryEND() {
		printMemory("FIN PARCOUR");
	}
	
	public static boolean parseAndFillGraph2(Graph graph, BufferedReader file) throws IOException {
		String line = "";
		Long nbLine = 0l;
		String[] arrayOfLine;
		int actualId;
		int actualIdNeighbour;
		
		while ((line =file.readLine()) != null) {
			nbLine++;
			if (line.length()==0 || line.charAt(0) == '#') {
				continue;
			}
			
			arrayOfLine = line.split("\\s");
			if(arrayOfLine.length!=2) {
				System.out.println("ERREUR ligne "+nbLine+" format Invalide");
				return false;
			}

			//System.out.println(arrayOfLine[0] +" "+ arrayOfLine[1]);
			try {
				actualId = Integer.parseInt(arrayOfLine[0]);
				actualIdNeighbour = Integer.parseInt(arrayOfLine[1]);

				graph.addEdge(actualId, actualIdNeighbour);
			}catch(NumberFormatException e) {
				System.out.println("ERREUR ligne "+nbLine+" format Invalide");
				return false;
			}
			
		}	
		return true;

	}
	
	
	public static Graph creatGraph(String arg) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(arg));
			Graph myGraph = new Graph();

			if (!parseAndFillGraph2(myGraph, br)) {
				return null;
			}
			br.close();
			return myGraph;

		} catch (NumberFormatException e) {
			System.out.println("veuillez entrez un nombre valide");
		} catch (IOException e) {
			System.out.println("ERREUR DE FICHIER - LECTURE OU ECRITURE");
			System.out.println("verifier le nom du fichier d'input");
		}
		return null;
	}
	
	public static void missingArgs() {
		System.out.println("il manque arguments");
        System.out.println("Pour exécuter : java Exo2 [nom_du_fichier]");
	}

}

public class GlobalClusteringCoefficient {
	

	public static void globalClusteringCoefficient(Graph myGraph) {
		
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
			completion.submit(new FindNbTriangles_OfX_WithOptimisation(tmpNode,false));
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

	
	
	public static void main(String[] args) {
		if (args.length < 1) {
			ManageInput.missingArgs();
			return;
		}
		
		Graph myGraph = ManageInput.creatGraph(args[0]);
		if(myGraph==null) {return;}
		ManageInput.printMemoryStart();
		globalClusteringCoefficient(myGraph);
		ManageInput.printMemoryEND();
        	
	}
}