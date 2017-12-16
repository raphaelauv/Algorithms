import java.io.IOException;
import java.io.OutputStream;
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
	
	
	public int[][] getMatrix(){
		int[][] matrix = new int[nbNodes][nbNodes];
		for(Node t : listNodes) {
			for(Node n : t.directNeighbours) {
				matrix[t.positionInArrayList][n.positionInArrayList]=1;
			}
		}
		return matrix;
	}
	
	public static void printMatrix(int[][] matrix) {
		System.out.println("\n----------------");
		for(int i=0; i<matrix.length;i++) {
			for(int j=0; j<matrix.length;j++) {
				System.out.print(matrix[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println("----------------");
	}

}



class Partition{

	int [][] partitions;
	int nbActualClusters;
	int nbEdges;
	int nbNodes;
	int equation4m2;
	int [][] matrix;
	ArrayList<Node> listNodes;
	
	public Partition(Graph myGraph) {
		this.nbActualClusters=myGraph.nbNodes;
		this.nbNodes=nbActualClusters;
		this.nbEdges = myGraph.nbEdges;
		this.listNodes = myGraph.getListeNodes();
		this.matrix= myGraph.getMatrix();
		this.equation4m2 = 4 *( nbEdges * nbEdges);
		
		this.partitions = new int[nbNodes][6];
		Node tmp;
		for(int i=0; i<myGraph.nbNodes;i++) {
			tmp = listNodes.get(i);
			
			this.partitions[i][0] = tmp.id; // id in input graph file
			this.partitions[i][1] = tmp.positionInArrayList; // positionInArrayList , id of cluster
			this.partitions[i][2] = 1; // isChef of cluster
			this.partitions[i][3] = 0; // Eii
			this.partitions[i][4] = 1; // sizePartition
			this.partitions[i][5] = tmp.degree; // size degrees of cluster
		}
		
		
	}
	
	/*
	 * retur True if a and b are chef of clusters
	 */
	public boolean isChefs(int a,int b) {
		
		int isChefA;
		synchronized(partitions[a]) {
			isChefA=partitions[a][2];
		}
		int isChefB;
		synchronized(partitions[b]) {
			isChefB=partitions[b][2];
		}
		
		if(isChefA==1 && isChefB==1) {
			//System.out.print("\nchef "+Va+" "+Vb);
			return true;
		}else {
			//System.out.print("\nNOT chef "+Va+" "+Vb);
			return false;
		}
	}
	
	public double getEii(int Vi) {
		int val;
		synchronized (partitions[Vi]) {
			val =partitions[Vi][3]; 
		}
		return val/(double)nbEdges;
		
	}
	
	
	public double getAii(int idChef) {
		
		int sumDegrees;
		synchronized (partitions[idChef]) {
			sumDegrees = partitions[idChef][5]; 
		}

		return (sumDegrees*sumDegrees) / (double) equation4m2;
	}
	
	/*
	 * find the reel cluster of the node a
	 * non conccurent version
	 */
	public int findSet2(int a) {
		
		if(partitions[a][2]==1) {
			return a;
		}
		int setChef = partitions[a][1];
		while(partitions[setChef][2]!=1) {
			setChef=partitions[setChef][1];
		}
		partitions[a][1]=setChef;
		return setChef;

	}
	
	
	/*
	 * find the reel cluster of the node a
	 * concurrent
	 */
	public int findSet(int a) {
		
		int setChef;
		synchronized(partitions[a]) {
			setChef=partitions[a][2];
		}
		if(setChef==1) {
			return a;
		}
	
		boolean notFind=true;
		while(notFind) {
			synchronized (partitions[setChef]) {
				if(partitions[setChef][2]==1) {
					notFind=false;
				}else {
					setChef=partitions[setChef][1];
				}
			}
		}
		
		synchronized(partitions[a]) {
			partitions[a][1]=setChef;
		}
		return setChef;
		

	}
	
	
	/*
	 * for the aggregative algorithm 
	 */
	public int nbEii(int a,int b) {
		
		//System.out.println();
		int nbEii=0;
		Node tmp;
		int idSet =0;
		for(int i=0; i< nbNodes;i++) {
			
			idSet = findSet(partitions[i][1]);
			if(idSet==b || idSet==a) {
				tmp = listNodes.get(i);
				for(Node n:tmp.directNeighbours) {
					idSet=findSet(partitions[n.positionInArrayList][1]);
					if(idSet==b || idSet==a) {
						//System.out.println(tmp.id+" CONNECTED "+n.id);
						nbEii++;
					}
				}
			}
		}
		 
		//System.out.println("interne "+b +" = "+nbEii/2);
		return nbEii/2;
	}
	

	/*
	 * iterative version
	 */
	public double[] getPprime_QPprime(int a ,int b){
		
		int actualClusterA =partitions[a][1];
		int actualEii_ofB = partitions[b][3];
		int actualDegreesOfB = partitions[b][5];
		int actualDegreesOfA = partitions[a][5];
		partitions[a][1] = partitions[b][1];
		partitions[b][3] = nbEii(a,b);
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
	
	/*
	 * parallelizable version
	 */
	public double[] getPprime_QPprimePAR(int a ,int b){
	
		
		double [] result = new double[2];
		
		result[0]=getQP(a,b);
		synchronized (partitions[b]) {
			result[1]=partitions[b][3];
		}
		
		return result;
	}
	
	
	/*
	 * iterative optimised version
	 * O(1)
	 */
	public double getQP_OPT(int a , int b) {
		
		int degA =  partitions[a][5];
		int degB =  partitions[b][5];
		int mAB = matrix[a][b];
		return (mAB/(double)nbEdges) - ((degA*degA)/(double)equation4m2 ) + ((degB*degB)/(double)equation4m2 );
		
	}
	
	
	/*
	 * iterative aggregative version
	 * complexity : O(m) 
	 */
	public double getQP() {
		double sum=0;
		for(int i=0; i<nbNodes;i++) {
				if(partitions[i][2]==1) {//isChef of partition
					sum+=(getEii(i)-getAii(i));
				}
			}
		return sum;
	}
	
	/*
	 * parallelizable aggregative version
	 * complexity : O(m) 
	 */
	public double getQP(int a , int b) {

		int realnbEIIofB = nbEii(a,b);
		int realDegreeB;
		int degreeA;
		synchronized (partitions[a]) {
			degreeA=partitions[a][5];
		}
		
		synchronized (partitions[b]) {
			realDegreeB = partitions[b][5]; 
		}
		realDegreeB+=degreeA;
		
		double sum=0;
		boolean itsB;
		boolean itsChef;
		for(int i=0; i<nbNodes;i++) {
			itsB=false;
			itsChef=false;
			synchronized (partitions[i]) {
				if(partitions[i][1]==a) {
					continue;//a is not a chef during calculation
				}
				else if(partitions[i][1]==b) {
					itsB=true;
				}
				else if(partitions[i][2]==1) {//isChef of partition
					itsChef=true;
				}
			}
			
			if(itsB) {
				sum+=(   realnbEIIofB/(double)nbEdges   -   ((realDegreeB*realDegreeB) / (double) equation4m2 ) );
			}else if(itsChef) {
				sum+=(getEii(i)-getAii(i));
			}
		}
		return sum;
	}

	public synchronized void performFusion_OPT(int a, int b) {
		if(partitions[a][2]!=1 || partitions[b][2]!=1) {
			System.out.println("NOT CHEF : "+a+" "+b);
			return;
		}
		
		for(int i=0;i<nbNodes;i++) {
			
			if(i==b) {
				continue;
			}
			
			if(findSet(partitions[i][1])==a) {
				partitions[i][1]=b;
			}
			
			matrix[b][i]+=matrix[a][i];
			matrix[i][b]=matrix[b][i];
			
			//System.out.println("a :"+a+" i:"+i);
			//System.out.println("i :"+i+" a:"+a);
			matrix[i][a]=0;
			matrix[a][i]=0;
		
		}
		matrix[a][b]=0;
		matrix[b][a]=0;

		nbActualClusters--;
		partitions[b][5]+= partitions[a][5];
		partitions[b][4]+=partitions[a][4];
		//partitions[a][1]=b;
		partitions[a][2]=0;
		partitions[a][4]=0;
		partitions[a][5]=0;
		
		
		//Graph.printMatrix(matrix);
		//System.out.println("a :"+a+" b:"+b);
		
	}
	
	
	/*
	 * all nodes inside set A go inside set B
	 * complexity 0(1) 
	 */
	public void performFusion(int a, int b, int totalNbEii) {
		/*
		if(partitions[a][2]!=1 || partitions[b][2]!=1) {
			System.out.println("NOT CHEF : "+a+" "+b);
			return;
		}
		*/
		
		int clusterIdB;
		synchronized (partitions[b]) {
			clusterIdB = partitions[b][1];
			partitions[b][3] = totalNbEii;
		}
		int a4;
		int a5;
		
		synchronized (partitions[a]) {
			a4 = partitions[a][4];
			a5 = partitions[a][5];
			partitions[a][1]=clusterIdB;
			partitions[a][2]=0;
			partitions[a][4]=0;
			partitions[a][5]=0;
		}
		
		synchronized (partitions[b]) {
			partitions[b][4]+=a4;
			partitions[b][5] += a5;
		}
		
		nbActualClusters--;
		
		//int clusterIdA = partitions[a][1];
		
		/*
		for(int i =0;i<nbNodes;i++) {
			if(partitions[i][1]==clusterIdA) {
				partitions[i][1] = clusterIdB;
				partitions[b][4]++;
			}
		}
		*/
		
	}
	
	/*
	 * return an array of clusters
	 * complexity : 2*n ( n number of nodes)
	 */
	public synchronized int [][] getClusters() {
		int [][] clusters = new int[nbNodes][];
		int [] indexUnderCluster = new int[nbNodes];
		
		for(int i =0;i<nbNodes;i++) {
			if(partitions[i][2]==1) {//ischef
				clusters[partitions[i][1]]= new int[partitions[i][4]];
			}
		}
		
		int cluster;
		for(int i =0;i<nbNodes;i++) {
			cluster = findSet(partitions[i][1]);
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
		
	public Node(int id,int positionInArrayList) {
		this.id = id;
		this.positionInArrayList = positionInArrayList;
		this.directNeighbours = new ArrayList<>();
	}
	
	public void incrNeighbours() {
		this.degree++;
	}
	
	public void insertEdge(Node neighbour) {
		this.directNeighbours.add(neighbour);
	}
} 