import java.io.IOException;
import java.io.OutputStream;
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

	public int nbActualClusters;
	
	int [][] partitions;
	
	//int [][] matrice;
	int nbEdges;
	int nbNodes;
	int equation4m2;
	
	ArrayList<Node> listNodes;
	
	Queue<Node> stackForBFS = new ArrayDeque<>();
	
	public Partition(Graph myGraph) {
		this.nbActualClusters=myGraph.nbNodes;
		this.nbNodes=nbActualClusters;
		this.nbEdges = myGraph.nbEdges;

		/*
		this.matrice = myGraph.getMatriceAdjency();
		Graph.printAdjency(matrice);
		*/
		this.listNodes = myGraph.getListeNodes();
	
		
		this.partitions = new int[nbNodes][6];
		Node tmp;
		for(int i=0; i<myGraph.nbNodes;i++) {
			tmp = listNodes.get(i);
			
			this.partitions[i][0] = tmp.id;
			this.partitions[i][1] = tmp.paritionId; // positionInArrayList , id of cluster
			this.partitions[i][2] = 1; //isChef
			this.partitions[i][3] = 0;//Eii
			this.partitions[i][4] = 1;// sizePartition
			this.partitions[i][5] = tmp.degree; // size degrees of cluster
		}
		
		this.equation4m2 = 4 *( nbEdges * nbEdges);
	}
	
	public boolean isChefs(int Va,int Vb) {
		if(partitions[Va][2]==1 && partitions[Vb][2]==1) {
			//System.out.print("\nchef "+Va+" "+Vb);
			return true;
		}else {
			//System.out.print("\nNOT chef "+Va+" "+Vb);
			return false;
		}
	}
	
	public double getEii(int Vi) {
		return partitions[Vi][3]/(double)nbEdges;
	}
	
	/*
	public double getEij(int Vi , int Vj) {
		return matrice[Vi][Vj]/(double)nbEdges;
	}
	*/
	public double getAii(int idChef) {
		int sumDegrees = partitions[idChef][5];
		return (sumDegrees*sumDegrees) / (double) equation4m2;
	}
	
	
	public int findSet(int a) {
		if(partitions[a][2]==1) {
			return partitions[a][1];
		}
		
		int setChef =partitions[a][2];

		while(partitions[setChef][2]!=1) {
			setChef=partitions[setChef][2];
		}
		
		partitions[a][1]=setChef;
		
		return setChef;
	}
	
	public int nbEii(int b) {
		
		//System.out.println();
		int nbEii=0;
		Node tmp;
		for(int i=0; i< nbNodes;i++) {
			if(findSet(partitions[i][1])==b) {
				tmp = listNodes.get(i);
				for(Node n:tmp.directNeighbours) {
					if(findSet(partitions[n.paritionId][1])==b) {
						//System.out.println(tmp.id+" CONNECTED "+n.id);
						nbEii++;
					}
				}
			}
		}
		 
		//System.out.println("interne "+b +" = "+nbEii/2);
		return nbEii/2;
	}
	
	
	
	public double[] getPprime_QPprime(int a ,int b){
		
		int actualClusterA =partitions[a][1];
		int actualEii_ofB = partitions[b][3];
		int actualDegreesOfB = partitions[b][5];
		int actualDegreesOfA = partitions[a][5];
		
		partitions[a][1] = partitions[b][1];
		partitions[b][3] = nbEii(b);
		partitions[b][5] += partitions[a][5];
		
		partitions[a][2] = 0;
		partitions[a][5] = 0;
		
			
		
		double [] result = new double[2];
		
		result[0]=getQP();
		result[1]=partitions[b][3];
				
		partitions[a][1] = actualClusterA;
		partitions[a][2] = 1;
		partitions[a][5] = actualDegreesOfA;
		
		partitions[b][3] = actualEii_ofB;
		partitions[b][5] = actualDegreesOfB;
		return result;
	}
	
	public double getQP() {

		double sum=0;
		for(int i=0; i<nbNodes;i++) {
			if(partitions[i][2]==1) {//isChef of partition
				sum+=(getEii(i)-getAii(i));
			}
		}
		return sum;
	}

	// all nodes inside set a go inside set b 
	public void performFusion(int a, int b, int totalNbEii) {
		if(partitions[a][2]!=1 || partitions[b][2]!=1) {
			System.out.println("NOT CHEF : "+a+" "+b);
			return;
		}
		partitions[b][3] = totalNbEii;
		partitions[b][5] += partitions[a][5];
		nbActualClusters--;
		int clusterIdA = partitions[a][1];
		int clusterIdB = partitions[b][1];
		
		partitions[a][2]=0;
		partitions[a][4]=0;
		partitions[a][5]=0;
		for(int i =0;i<nbNodes;i++) {
			if(partitions[i][1]==clusterIdA) {
				partitions[i][1] = clusterIdB;
				partitions[b][4]++;
			}
		}
	}
	
	public int [][] getClusters() {
		int [][] clusters = new int[nbNodes][];
		int [] indexUnderCluster = new int[nbNodes];
		
		for(int i =0;i<nbNodes;i++) {
			if(partitions[i][2]==1) {//ischef
				clusters[partitions[i][1]]= new int[partitions[i][4]];
			}
		}
		
		
		
		int cluster;
		for(int i =0;i<nbNodes;i++) {
			cluster = partitions[i][1];
			clusters[cluster][indexUnderCluster[cluster]]=partitions[i][0];
			indexUnderCluster[cluster]++;
		}
		return clusters;
	}
	
	public static void printClusters(int[][] clusters) {
		for(int i=0;i<clusters.length;i++) {
			if(clusters[i]==null) {
				continue;
			}
			System.out.print('[');
			for(int j=0;j<clusters[i].length;j++) {
				System.out.print(clusters[i][j]);
				if(j<clusters[i].length-1) {
					System.out.print(',');
				}
			}
			
			System.out.print(']');
		}
	}
	
	public static void writeClusters(int[][] clusters,double Q, OutputStream out) throws IOException  {
		
		String header = "# "+clusters.length+" clusters,Q="+Q+"\n";
		out.write(header.getBytes());
		for(int i=0;i<clusters.length;i++) {
			if(clusters[i]==null) {
				continue;
			}
			for(int j=0;j<clusters[i].length;j++) {
				if(i+1==clusters.length && j+1==clusters[i].length) { //avoid last jumpLine
					out.write((clusters[i][j]+" "+i).getBytes());
				}else {
					out.write((clusters[i][j]+" "+i+"\n").getBytes());
				}
			}
		}
		
		out.flush();
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
		this.directNeighbours = new ArrayList<>();
	}
	
	public void incrNeighbours() {
		this.degree++;
	}
	
	public void insertEdge(Node neighbour) {
		this.directNeighbours.add(neighbour);
	}
	
} 