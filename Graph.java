import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

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
			//t.directNeighbours.clear(); //TODO
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

	public int nbActualPartitions;
	
	int [][] partitions;
	int [] degrees;
	
	int [][] matrice;
	int nbEdges;
	int nbNodes;
	int equation4m2;
	
	ArrayList<Node> listNodes;
	
	Queue<Node> stackForBFS = new ArrayDeque<>();
	
	public Partition(Graph myGraph) {
		this.nbActualPartitions=myGraph.nbNodes;
		this.nbNodes=nbActualPartitions;
		this.nbEdges = myGraph.nbEdges;
		
		this.degrees = new int[nbActualPartitions];
		
		
		this.matrice = myGraph.getMatriceAdjency();
		Graph.printAdjency(matrice);
		
		this.listNodes = myGraph.getListeNodes();
	
		
		
		this.partitions = new int[nbNodes][4]; //1 -> numero sommet , 2) numero cluster , 3) chef  , 4) degreeCluster
		Node tmp;
		for(int i=0; i<myGraph.nbNodes;i++) {
			tmp = listNodes.get(i);
			this.degrees[i]= tmp.degree;
			this.partitions[i][0] = tmp.id;
			this.partitions[i][1] = tmp.paritionId; // positionInArrayList
			this.partitions[i][2] = tmp.id; //chef
			this.partitions[i][3] = 1;//Eii
		}
		
		this.equation4m2 = 4 *( nbEdges * nbEdges);
	}
	
	public boolean isChefs(int Va,int Vb) {
		if(partitions[Va][1]==partitions[Va][2]) {
			if(partitions[Vb][1]==partitions[Vb][2]) {
				return true;
			}
		}
		return false;
	}
	
	public double getEii(int Vi) {
		return partitions[Vi][3]/nbEdges;
	}
	
	public double getEij(int Vi , int Vj) {
		return matrice[Vi][Vj]/(double)nbEdges;
	}
	
	public double getAii(int idChef) {
		int sumDegrees = degrees[idChef];
		return (sumDegrees*sumDegrees) / (double) equation4m2;
	}
	
	
	public int findUltimeChef(int id) {
		int chef =partitions[id][]
		//todo
	}
	
	public int nbEii(int a , int b) {
		
		for(int i=0; i< nbNodes;i++) {
			
		}
		Node tmp = listNodes.get(a);
		
		for(Node n:tmp.directNeighbours) {
			if(n.id)
		}
	}
	
	public double[] getPprime_QPprime(int a ,int b){
		
		partitions[a][2] = partitions[b][0];
		int actualEii_ofB = partitions[b][3];
		
		partitions[b][3] = nbEii(a, b);
		
		
		double QPprime = getQP();
		
		double [] result = new double[2];
		result[0]=QPprime;
		result[1]=partitions[b][3];
				
		partitions[a][2]=partitions[a][0];
		partitions[b][3] = actualEii_ofB;
		return result;
	}
	
	public double getQP() {

		double sum=0;
		for(int i=0; i<nbNodes;i++) {
			if(partitions[i][1]==partitions[i][2]) {//isChef of partition
				sum+=(getEii(i)-getAii(i));
			}
		}
		return sum;
	}

	public void performFusion(int i, int j) {
		// TODO Auto-generated method stub
		
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