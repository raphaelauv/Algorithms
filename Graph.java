import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Graph {

	private ArrayList<Node> listNodes;			//filed at the same time than the map , avoid to iterate later the map
	private Map<Integer, Node> mapNodes;
	int nbEdges;
	int nbNodes;
	
	public Graph() {
		this.mapNodes = new HashMap<>();
		this.listNodes = new ArrayList<>();
	}
	public ArrayList<Node> getListeNodes() {
		return listNodes;
	}
	public void addEdge(int actualID, int neighbourID) {
		Node actualNode =this.mapNodes.computeIfAbsent(actualID,k ->{
			listNodes.add(new Node(actualID,nbNodes));nbNodes++;return listNodes.get(nbNodes-1);
		});
		if(actualID!=neighbourID) {
			Node neighbourNode = this.mapNodes.computeIfAbsent(neighbourID,k ->{
				listNodes.add(new Node(neighbourID,nbNodes));nbNodes++;return listNodes.get(nbNodes-1);
			});
			actualNode.insertEdge(neighbourNode);
			neighbourNode.insertEdge(actualNode);
			nbEdges++;
		
		}
	}
	
	public int[][] getMatriceAdjency(){
		int[][] adjencyMatrice = new int[nbNodes][nbNodes];
		
		for(Node t : listNodes) {
			for(Node n : t.directNeighbours) {
				adjencyMatrice[t.positionInArrayList][n.positionInArrayList]=1;
			}
			t.directNeighbours.clear(); //TODO
		}
		return adjencyMatrice;
	}
	
	public static void printAdjency(int[][] adjency) {
		for(int i=0; i<adjency.length;i++) {
			for(int j=0; j<adjency.length;j++) {
				System.out.print(adjency[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println("----------------");
	}
}



class Partition{

	int nbActualPartitions;
	
	int [][] partitions;
	int [] degrees;
	
	int [][] matrice;
	int nbEdges;
	int equation4m2;
	
	public Partition(Graph myGraph) {
		this.nbActualPartitions=myGraph.nbNodes;
		this.degrees = new int[nbActualPartitions];
		this.nbEdges = myGraph.nbEdges;
		
		this.matrice = myGraph.getMatriceAdjency();
		Graph.printAdjency(matrice);
		
		ArrayList<Node> listNodes = myGraph.getListeNodes();
	
		
		
		this.partitions = new int[nbActualPartitions][3]; //1 -> numero sommet , 2) numero cluster , 3) chef ?
		Node tmp;
		for(int i=0; i<myGraph.nbNodes;i++) {
			tmp = listNodes.get(i);
			this.degrees[i]= tmp.degree;
			this.partitions[i][0] = tmp.id;
			this.partitions[i][1] = tmp.paritionId;
			this.partitions[i][2] = 1; //chef
		}
		
		this.equation4m2 = 4 *( nbEdges * nbEdges);
	}
	
	public double geteij(int Vi , int Vj) {
		return matrice[Vi][Vj]/(double)nbEdges;
	}
	
	public double getaij(int Vi) {
		return (degrees[Vi]*degrees[Vi]) / (double) equation4m2;
	}

}

class Node {
	
	final int id;
	final int positionInArrayList;
	int degree;
	
	
	Collection<Node> directNeighbours;
	int paritionId;
	boolean isChefCluster=true;
		
	public Node(int id,int positionInArrayList) {
		this.id = id;
		this.positionInArrayList = positionInArrayList;
		this.paritionId=positionInArrayList;
		this.directNeighbours = new ArrayList<>(); 	//better than linkedList for the parallele version without marqued technique
	}
	
	public void incrNeighbours() {
		this.degree++;
	}
	
	public void insertEdge(Node neighbour) {
		this.directNeighbours.add(neighbour);
	}
	
} 