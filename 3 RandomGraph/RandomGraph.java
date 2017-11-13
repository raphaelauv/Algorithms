import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

class ArrayListInteger extends ArrayList<Integer>{
	public ArrayListInteger(int capacity) {
		super(capacity);
	}
	public ArrayListInteger() {
		super();
	}
}

public class RandomGraph {

	public final static int flag_ERDOS = 1;
	public final static int flag_BARABASI = 2;
	
	public static String outputFileName;
	public static int nbVertex_ASK;
	public static double proba_ASK;;
	public static int k_ASK;
	public static int d_ASK;
	public static int n0_ASK;
	public static boolean oriented;
	public static boolean verbose;
	public static boolean emptyGraph = true;
	public static int flag_ask;
	
	public static ArrayList<Integer>[] erdosReny(int nbVertex, double p) {

			
		int[] degrees = new int[nbVertex];		//keep trace of degree of each node
		Arrays.fill(degrees, -1);				// degree at -1 mean the node do not exist yet		
		int maxDegree = 0;
		int defaultCapacity;	
		ArrayList<Integer>[] matrice = new ArrayListInteger[nbVertex]; // Adjacency list
	

		for (int i = 0; i < nbVertex; i++) {

			int j = i+1;				//a <-> b is the same than b <-> a , so we skip
			if(oriented) {	
				j=0;	
			}

			defaultCapacity = (int) ((nbVertex-j ) * p); //to init the array at is probably futur filled size
			if(defaultCapacity<10) {
				defaultCapacity =10;
			}
			matrice[i] = new ArrayListInteger(defaultCapacity);

			if (p == 0) {
				continue;
			}
			for (; j < nbVertex; j++) {
				
				if(i==j) {
					continue;
				}
				
				if (Math.random() < p) {
					emptyGraph = false;
					matrice[i].add(j);

					if(degrees[i]==-1) {
						degrees[i]=0;
					}
					
					if(!oriented) {
						if(degrees[j]==-1) {
							degrees[j]=0;
						}
					}

					degrees[i]++;
					if (degrees[i] > maxDegree) {
						maxDegree = degrees[i];
					}
					
					if(!oriented) {
						degrees[j]++;
						if (degrees[j] > maxDegree) {
							maxDegree = degrees[j];
						}
					}
				}
			}
		}
		return matrice;
	}

	public static ArrayList<Integer>[] barabasiAlbert(int d, int n0, int nbVertex) {


		ArrayList<Integer>[] matrice = new ArrayListInteger[nbVertex]; //Adjacency list
		int[] degreesOUT = new int[nbVertex];
		Arrays.fill(degreesOUT, -1);
		int[] degreesIN = new int[nbVertex];
		
		int n0Comple = n0 - 1;
		
		int maxDegree = n0Comple;
		if(oriented) {
			maxDegree*=2;
		}
		
		int nbAllDegrees = (n0 * n0Comple);
		if(!oriented){
			nbAllDegrees/=2;
		}

		int defaultCapacity =  d - n0 +1;
		if (defaultCapacity < 10) {
			defaultCapacity = 10;
		}
		
		for (int i = 0; i < nbVertex; i++) {
			
			if(i>=n0) {
				matrice[i] = new ArrayListInteger(d+1);
			}
			else {
				matrice[i] = new ArrayListInteger(defaultCapacity);
				
				degreesOUT[i] = n0Comple;
				
				if (oriented) {
					degreesIN[i] = n0Comple;
				}
				
				int j = i + 1;
				if (oriented) {
					j = 0;
				}

				for (; j < n0; j++) {

					if (i == j) {
						continue;
					}

					matrice[i].add(j);
				}
			}			
		}

		double proba = 0;

		int myDegree = 0;
		int degreeNeighbourOUT = 0;
		int degreeNeighbourIN = 0;

		for (int i = nbVertex-1; i >n0-1 ; i--) {
			myDegree = degreesOUT[i];
			while (myDegree < d) {

				for (int j=0; j < nbVertex; j++) {

					if (myDegree == d) {
						break;
					}

					if (i == j) {
						continue;
					}
					if (!oriented) {
						if (matrice[i].contains(j) || matrice[j].contains(i)) {
							continue;
						}
					} else {
						if (matrice[i].contains(j)) {
							continue;
						}
					}

					degreeNeighbourOUT = degreesOUT[j];
					degreeNeighbourIN = degreesIN[j];
					
					if (degreeNeighbourOUT == 0 || degreeNeighbourOUT==-1) {
						continue;
					}

					proba = (degreeNeighbourOUT+degreeNeighbourIN) / (double) (nbAllDegrees - myDegree);

					//System.out.println(proba);
					if (Math.random() < proba) {
						myDegree++;

						nbAllDegrees++;
						matrice[i].add(j);
						

						
						if(degreesOUT[i]==-1) {
							degreesOUT[i]=0;
						}
						if(!oriented) {
							if(degreesOUT[j]==-1) {
								degreesOUT[j]=0;
								degreesIN[j]=0;
							}
						}

						degreesOUT[i]++;
						if (degreesOUT[i] > maxDegree) {
							maxDegree = degreesOUT[i];
						}
						
						if(!oriented) {
							degreesOUT[j]++;
							if (degreesOUT[j] > maxDegree) {
								maxDegree = degreesOUT[j];
							}
						}else {
							degreesIN[j]++;
						}
					}
				}

			}
		}
		emptyGraph = false;
		return matrice;
	}

	public static void printArrayListMatrice(ArrayList<Integer>[] matrice) {
		for (int i = 0; i < matrice.length; i++) {
			for (int j = 0; j < matrice.length; j++) {
				if (matrice[i].contains(j)) {
					System.out.print("* ");
				} else {
					System.out.print("0 ");
				}

			}
			System.out.println("\n");
		}
	}
	
	public static void printBooleanMatrice(boolean[][] matrice) {
		for (int i = 0; i < matrice.length; i++) {
			for (int j = 0; j < i; j++) {
				if (matrice[i][j]) {
					System.out.print("* ");
				} else {
					System.out.print("0 ");
				}

			}
			System.out.println("\n");
		}
	}

	
	public static void writeArrayListMatrice(ArrayList<Integer>[] matrice, OutputStream out) throws IOException {
		String ligneJ = "";
		byte[] byteJ = ligneJ.getBytes();
		byte[] jumpLine = "\n".getBytes();
		if(emptyGraph) {
			out.write("#empty Graph".getBytes());
			return;
		}
		if(oriented) {
			out.write("#oriented Graph\n".getBytes());
		}
		
		for (int i = 0; i < matrice.length; i++) {
			for (int j = 0; j < matrice[i].size(); j++) {
				ligneJ = "" + i + " " + matrice[i].get(j);
				byteJ = ligneJ.getBytes();
				out.write(byteJ, 0, byteJ.length);
				out.write(jumpLine);
			}
		}

	}
	
	
	public static void writeBooleanListMatrice(boolean[][] matrice, OutputStream out) throws IOException {
		String ligneJ = "";
		byte[] byteJ = ligneJ.getBytes();
		byte[] jumpLine = "\n".getBytes();
		if(oriented) {
			out.write("#oriented Graph\n".getBytes());
		}
		for (int i = 0; i < matrice.length; i++) {
			for (int j = 0; j < i; j++) {
				if (matrice[i][j]) {
					ligneJ = "" + i + " " + j;
					byteJ = ligneJ.getBytes();
					out.write(byteJ, 0, byteJ.length);
					out.write(jumpLine);
				}
			}

		}
	}

	
	public static Graph createGraphFromArrayListMatrice(ArrayList<Integer>[] matrice, boolean oriented) {
		Graph graph = new Graph(oriented,nbVertex_ASK);
		for (int i = 0; i < matrice.length; i++) {
			for (int j = 0; j < matrice[i].size(); j++) {
				graph.addEdgeModeArray(i, matrice[i].get(j));
			}
		}
		return graph;
	}
	
	public static Graph createGraphFromBooleanMatrice(boolean[][] matrice, boolean oriented) {

		Graph graph = new Graph(oriented,nbVertex_ASK);
		for (int i = 0; i < matrice.length; i++) {
			for (int j = 0; j < i; j++) {
				if (matrice[i][j]) {
					graph.addEdgeModeArray(i, j);
				}
			}
		}
		return graph;
	}

	
	public static void performRandomGraph() {
		
		Graph myGraph;
		ResultGloalAndLocal rstLG = null;
		ResultBFS rstBFS = null;
		RstBFS_CC rstCC = null;

		INT_MinMaxAverage mma_nbVertex = new INT_MinMaxAverage(k_ASK, "nbVertex ");
		INT_MinMaxAverage mma_nbEdges = new INT_MinMaxAverage(k_ASK, "nbEdge   ");
		INT_MinMaxAverage mma_maxDegree = new INT_MinMaxAverage(k_ASK, "maxDeg   ");
		INT_MinMaxAverage mma_nbCC = new INT_MinMaxAverage(k_ASK, "nbCC     ");
		INT_MinMaxAverage mma_diameter = new INT_MinMaxAverage(k_ASK, "diameter ");
		DOUBLE_MinMaxAverage mma_APL = new DOUBLE_MinMaxAverage(k_ASK, "APL      ");
		INT_MinMaxAverage mma_nbTri = new INT_MinMaxAverage(k_ASK, "nbTri    ");
		INT_MinMaxAverage mma_nbV = new INT_MinMaxAverage(k_ASK, "nbV      ");
		DOUBLE_MinMaxAverage mma_cluG = new DOUBLE_MinMaxAverage(k_ASK, "Clu_G    ");
		DOUBLE_MinMaxAverage mma_cluL = new DOUBLE_MinMaxAverage(k_ASK, "Clu_L    ");

		
		//boolean[][] matrice;
		ArrayList<Integer>[] matrice = null;
		
		
		for (int i = 0; i < k_ASK; i++) {

			if(verbose) {
				System.out.println(""+(i*100)/k_ASK+"% done");	
			}
			
			if(flag_ask==flag_ERDOS) {
				matrice = erdosReny(nbVertex_ASK, proba_ASK);	
			}else if(flag_ask==flag_BARABASI) {
				matrice = barabasiAlbert(d_ASK, n0_ASK, nbVertex_ASK);
			}else {
				System.out.println("ERREUR FLAG");
				return;
			}
			
			//printArrayListMatrice(matrice);

			if (i == 0) {
				ManageInput.printMemory("AfterCreation Adjacency list ");
				try {
					Path file2 = Paths.get("./" + outputFileName);
					OutputStream out = new BufferedOutputStream(Files.newOutputStream(file2, CREATE, TRUNCATE_EXISTING));
					writeArrayListMatrice(matrice, out);
					out.flush();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
			if (k_ASK==1 && emptyGraph) {
				System.out.println("GRAPH is EMPTY");
			}

			
			myGraph = createGraphFromArrayListMatrice(matrice, oriented);
			rstBFS = Diameter_APL_Graph.diameter_and_APL_ofGraph_Stream(myGraph);
			rstLG = ClusteringCoefficient.globalAndLocal(myGraph);
			rstCC = Let_BFS_CC.nbCCinGraph(myGraph);
			
		
			if(i==0) {
				ManageInput.printMemory("After Analyse ");
			}
			
			if (k_ASK == 1) {
				System.out.println(rstCC);
				System.out.println(rstBFS);
				System.out.println(rstLG);			
				return;
			} else {
				if(!emptyGraph) {
					mma_nbVertex.add(rstCC.nbVertex);
					mma_nbEdges.add(rstCC.nbEdges);
					mma_maxDegree.add(rstCC.maxDegree);
					mma_nbCC.add(rstCC.nbCC);
					mma_diameter.add(rstBFS.diameter);
					mma_APL.add(rstBFS.getAPL());
					mma_nbTri.add(rstLG.nbTri);
					mma_nbV.add(rstLG.nbV);
					mma_cluG.add(rstLG.cluG);
					mma_cluL.add(rstLG.cluL);
				}
			}
			
			if(i==k_ASK-1) {
				System.out.println(mma_nbVertex);
				System.out.println(mma_nbEdges);
				System.out.println(mma_maxDegree);
				System.out.println(mma_nbCC);
				System.out.println(mma_diameter);
				System.out.println(mma_APL);
				System.out.println(mma_nbTri);
				System.out.println(mma_nbV);
				System.out.println(mma_cluG);
				System.out.println(mma_cluL);
			}
		}
	}
	
	public static void main(String[] args) {

		try {
			ManageInput.analyseArgs(args);
		}catch (Exception e) {
			ManageInput.incorrectArgs();
			return;
		}
		
		
		/*
		outputFileName = "toto";
		nbVertex_ASK = 30;
		proba_ASK = 0.1;
		k_ASK = 1000;
		oriented = false;
		verbose = false;
		d_ASK = 15;
		n0_ASK = 20;
		flag_ask = flag_ERDOS;
		*/
		
		performRandomGraph();
		ManageInput.printMemory("END ");
	}
}
