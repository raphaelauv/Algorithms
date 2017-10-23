import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/*
class Tuple<X, Y> { 
	  public final X x; 
	  public final Y y; 
	  public Tuple(X x, Y y) { 
	    this.x = x; 
	    this.y = y; 
	  } 
}
*/

class Node {
	
	int id;
	//Object locker; //not necessary with the AtomicInteger
	AtomicInteger nbInsideTriangle;
	
	Collection<Node> neighbours;
	
	public Node(int id) {
		this.id = id;
		this.neighbours = new LinkedHashSet<>(); //better for the parallele version without marqued technique
		//this.neighbours = new LinkedList<>(); //better for the sequential version with marqued technique
		
		this.nbInsideTriangle =  new AtomicInteger(0);
		//this.locker = new Object();
	}
	
	public void insertEdge(Node neighbour) {
		this.neighbours.add(neighbour);
	}
	
	public static void incrementNbTriangle(Node node0, Node node1, Node node2) {
		/*
		synchronized (node0.locker) {node0.nbInsideTriangle++;}
		synchronized (node1.locker) {node1.nbInsideTriangle++;}
		synchronized (node2.locker) {node2.nbInsideTriangle++;}
		*/
		node0.nbInsideTriangle.incrementAndGet();
		node1.nbInsideTriangle.incrementAndGet();
		node2.nbInsideTriangle.incrementAndGet();
	}
}

class FindNbTrianglesOfX_WithOptimisation implements Callable<Integer> {

	private Node node;
	boolean enPlace;
	
	public FindNbTrianglesOfX_WithOptimisation(Node node,boolean enPlace) {
		this.node = node;
		this.enPlace=enPlace;
	}

	public Integer call() throws Exception {
		int nb = 0;
	
		int myDegree = node.neighbours.size();
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
					if(enPlace) {
						Node.incrementNbTriangle(this.node, aNeighbour, aNN);
					}
				}
			}
		}
		//not use in case of enPlace
		return nb /2;//because we count each triangle in the two ways possible
	}
}

/*
class FindNbTiranglesAndDegreeOfX implements Callable<Tuple<Integer,Integer>> {
	
	private Node2 node;
	public FindNbTiranglesAndDegreeOfX(Node2 node) {
		this.node = node;
	}

	public Tuple<Integer,Integer> call() throws Exception {
		int nb = 0;
		for (Node2 aNeighbour : node.neighbours) {
			for (Node2 aNN : aNeighbour.neighbours) {
				if(aNN.id==this.node.id) {
					continue;//do not look for myself
				}
				if(node.neighbours.contains(aNN)) {
					nb++;
				}
			}
		}
		return new Tuple<Integer, Integer>(nb/2, node.neighbours.size());
	}
}
*/


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

	public void averageClusteringCoefficient() {
		
		
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		ExecutorService execute = Executors.newFixedThreadPool(corePoolSize);
		CompletionService<Integer> completion = new ExecutorCompletionService<>(execute);
		
		int nbTaskCreate=0;
		Iterator<Node> itNodes = this.mapNodes.values().iterator();
		
		while(itNodes.hasNext()) {
			
			completion.submit(new FindNbTrianglesOfX_WithOptimisation(itNodes.next(),true));
			//completion.submit(new FindNbTiranglesAndDegreeOfX(itNodes.next()));
			nbTaskCreate++;
		}

		int nbTri_X = 0;
		int degree_X=0;
		double sum_cluL_X = 0;

		
		for(int i =0;i<nbTaskCreate;i++) {
			try {
				completion.take().get();
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("ERROR OCCURED");
				execute.shutdownNow();
				return;
			}
		}
		execute.shutdown();
		
		itNodes = this.mapNodes.values().iterator();
		Node actualNode=null;
		while(itNodes.hasNext()) {
			actualNode=itNodes.next();
			nbTri_X=actualNode.nbInsideTriangle.get()/2;
			degree_X=actualNode.neighbours.size();
			if(degree_X <2) {
				continue;
			}
			sum_cluL_X += (2 * nbTri_X ) / (double)  (degree_X * (degree_X-1));
			
		}
		
		/*
		Tuple<Integer,Integer> result;
		for(int i =0;i<nbTaskCreate;i++) {
			try {
				
				
				result = completion.take().get();
				nbTri_X=result.x;
				degree_X=result.y;
							
				if(degree_X <2) {
					continue;
				}

				sum_cluL_X += (2 * nbTri_X ) / (double)  (degree_X * (degree_X-1));
				
				
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("ERROR OCCURED");
				execute.shutdownNow();
				return;
			}
		}
		execute.shutdown();
		*/
		
		double oneOnN= 1 / (double)(this.mapNodes.size());
	
		double cluL_G = oneOnN  *sum_cluL_X ;
		
		System.out.println("CLU_L : "+cluL_G );
		
	}
	
	public void globalClusteringCoefficient() {
		
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		ExecutorService execute = Executors.newFixedThreadPool(corePoolSize);
		CompletionService<Integer> completion = new ExecutorCompletionService<>(execute);
		
		int nbTaskToManage=0;
		Iterator<Node> itNodes = this.mapNodes.values().iterator();
		
	
		Node tmpNode;
		int degreeTmpNode;
		long nbV = 0;
		int nbTri = 0;
		
		/*
		int nbNodeToDo = this.mapNodes.values().size();
		int nbResult=0;
		int pourcent = nbNodeToDo/100;
		int pourcentPrinted=0;
		Future<Integer> result;
		*/
		
		while(itNodes.hasNext()) {
			tmpNode=itNodes.next();
			degreeTmpNode=tmpNode.neighbours.size();
			if(degreeTmpNode>0){
				nbV+= (degreeTmpNode*(degreeTmpNode-1) /2  );
			}
			completion.submit(new FindNbTrianglesOfX_WithOptimisation(tmpNode,false));
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
		//nbTri/=3; //without the optimisation with ids in findNbTriangles
		double cluGraph = (3 * nbTri) /(double)nbV;
		System.out.println("nb Tri : "+nbTri+" | nb V : "+nbV+" | CLU_G : "+cluGraph );
		
	}

}


public class GlobalClusteringCoefficient {
	
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
	
	public static void main(String[] args) {
		if (args.length < 1) {
			missingArgs();
			return;
		}
		
		Graph myGraph = creatGraph(args[0]);
		if(myGraph==null) {return;}
		myGraph.globalClusteringCoefficient();
        	
	}
}
