import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

class Node {
	
	final int id;
	Collection<Node> neighbours;
	private LongAdder nbInsideTriangle;						//only for averageClusteringCoefficient
	//private AtomicInteger diameter;						//only for BFS
	
	public Node(int id) {
		this.id = id;
		this.neighbours = new LinkedHashSet<>(); 	//better for the parallele version without marqued technique
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
	
	/*
	public AtomicInteger getDiameter() {
		return this.diameter;
	}
	
	public void setDiameterOfNode(int val) {
		if(this.diameter==null) {
			this.diameter = new AtomicInteger(val);
		}else {
			this.diameter.set(val);
		}
	}
	*/

}


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
 * Diameter job
 */
class BFS_OfX implements Callable<ResultBFS> {
	
	Node actualNode;
	
	/*
	boolean all_AlreadyCalculated;		//all the neighbour alredy have their diameter calculated
	boolean allTheSame=false;			//all the neighbour have the same diameter
	
	int maxNeighbourDiameter=0;
	int minNeighbourDiameter=0;
	*/
	public BFS_OfX(Node node) {
		this.actualNode=node;
	}
	
	/*
	public void findMaxAndMinDiameterOfNeighbour() {
		
		AtomicInteger tmpDiameter;
		int valTmpDiameter;
		
		boolean first=true;
		
		all_AlreadyCalculated=true;
		for (Node aNeighbour : actualNode.neighbours) {
			tmpDiameter=aNeighbour.getDiameter();
			if(tmpDiameter!=null) {
				valTmpDiameter=tmpDiameter.get();
				if(first) {
					minNeighbourDiameter=valTmpDiameter;
					maxNeighbourDiameter=valTmpDiameter;
					first=false;
					continue;
				}
				
				if(valTmpDiameter>maxNeighbourDiameter) {
					if(!first) {
						allTheSame=false;
					}
					maxNeighbourDiameter=valTmpDiameter;
				}else if(valTmpDiameter<minNeighbourDiameter) {
					if(!first) {
						allTheSame=false;
					}
					minNeighbourDiameter=valTmpDiameter;
				}
				
			}else {
				all_AlreadyCalculated=false;
				allTheSame=false;
			}
		}
		
	}
	*/
	
	public ResultBFS call() throws Exception {
		
		/*
		findMaxAndMinDiameterOfNeighbour();
		
		//All my neighbour(s) have the same Degree , so i have the same degree
		if(allTheSame) {
			return maxNeighbourDiameter;
		}

		//I only have 1 neighbour with an already diameter calculated
		if(actualNode.neighbours.size()==1 && maxNeighbourDiameter!=0) {
			actualNode.setDiameterOfNode(maxNeighbourDiameter+1);
			return maxNeighbourDiameter+1;
		}
		*/
		
		int nbVertices=0;
		int sommeOfallVertices=0;
		
		Queue<Node> stack = new LinkedList<>();
		Node tmpNode=actualNode;
		stack.add(tmpNode);

		Integer actualDistance =0;
		int maxDistance_of_D = 0;
		HashMap<Node, Integer> nodesAlreadySeen= new HashMap<>();
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
		
		return new ResultBFS(maxDistance_of_D,sommeOfallVertices,nbVertices);
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

	
	public void diameter_and_Averagepathlength_ofGraph() {
		
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		ExecutorService execute = Executors.newFixedThreadPool(corePoolSize);
		CompletionService<ResultBFS> completion = new ExecutorCompletionService<>(execute);

		int nbTaskCreate = 0;
		Iterator<Node> itNodes = this.mapNodes.values().iterator();
		while (itNodes.hasNext()) {
			completion.submit(new BFS_OfX(itNodes.next()));
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
	
	
	
	public void averageClusteringCoefficient() {
		
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		ExecutorService execute = Executors.newFixedThreadPool(corePoolSize);
		CompletionService<Integer> completion = new ExecutorCompletionService<>(execute);

		int nbTaskCreate = 0;
		Iterator<Node> itNodes = this.mapNodes.values().iterator();
		
		while (itNodes.hasNext()) {
			completion.submit(new FindNbTriangles_OfX_WithOptimisation(itNodes.next(), true));
			nbTaskCreate++;
		}

		int nbTri_X = 0;
		int degree_X = 0;
		double sum_cluL_X = 0;
		
		for (int i = 0; i < nbTaskCreate; i++) {
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
		Node actualNode = null;
		while (itNodes.hasNext()) {
			actualNode = itNodes.next();
			nbTri_X = actualNode.getNbTriangle();
			degree_X = actualNode.neighbours.size();
			if (degree_X < 2) {
				continue;
			}
			sum_cluL_X += (2 * nbTri_X) / (double) (degree_X * (degree_X - 1));
			
		}
		double oneOnN = 1 / (double) (this.mapNodes.size());
		double cluL_G = oneOnN * sum_cluL_X;
		System.out.println("CLU_L : " + cluL_G);
	}
	
	public void globalClusteringCoefficient() {
		
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
		
		Iterator<Node> itNodes = this.mapNodes.values().iterator();
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


}

final class ManageInput{
	
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
	
	public static void main(String[] args) {
		if (args.length < 1) {
			ManageInput.missingArgs();
			return;
		}
		
		Graph myGraph = ManageInput.creatGraph(args[0]);
		if(myGraph==null) {return;}
		myGraph.globalClusteringCoefficient();
        	
	}
}
