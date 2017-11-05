import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;


public class RandomGraph {

	private final static int flag_ERDOS = 1;
	private final static int flag_BARABASI =2;
	
	public static String outputFileName;
	public static int nbVertex_ASK;
	public static int proba_ASK;;
	public static int k_ASK;
	public static int d_ASK;
	public static int n0_ASK;
	public static boolean oriented;
	public static boolean verbose;
	
	public static int actualGraph_nbVertex = 0;
	public static int actualGraph_nbEdges = 0;
	public static int actualGraph_degreeMaximum = 0;
	public static boolean emptyGraph = true;
	
	public static boolean[][] erdosReny(int nbVertex, int p) {

		actualGraph_nbVertex = 0;
		actualGraph_nbEdges = 0;
		actualGraph_degreeMaximum = 0;
		
		int[] degrees = new int[nbVertex];
		int maxDegree = 0;

		boolean[][] matrice = new boolean[nbVertex][];

		Random rd = new Random();

		for (int i = 0; i < nbVertex; i++) {

			matrice[i] = new boolean[i];

			if (p == 0) {
				continue;
			}

			for (int j = 0; j < i; j++) {
				if (rd.nextInt(p) == 0) {
					emptyGraph = false;
					matrice[i][j] = true;

					if(degrees[i]==0) {
						actualGraph_nbVertex++;
					}
					if(degrees[j]==0) {
						actualGraph_nbVertex++;
					}
					
					actualGraph_nbEdges++;

					degrees[i]++;
					if (degrees[i] > maxDegree) {
						maxDegree = degrees[i];
					}
					degrees[j]++;
					if (degrees[j] > maxDegree) {
						maxDegree = degrees[j];
					}
				}
			}
		}
		actualGraph_degreeMaximum = maxDegree;
		return matrice;
	}

	public static boolean[][] barabasiAlbert(int d, int n0, int nbVertex) {

		boolean[][] matrice = new boolean[nbVertex][nbVertex];
		int[] degrees = new int[nbVertex];
		int n0Comple = n0 - 1;
		int maxDegree = n0Comple;
		int nbAllDegrees = (n0 * n0Comple) / 2;

		for (int i = 0; i < n0; i++) {
			degrees[i] = n0Comple;
			for (int j = i + 1; j < n0; j++) {
				matrice[i][j] = true;
			}
		}

		double proba = 0;

		int myDegree = 0;
		int degreeNeighbour = 0;

		for (int i = 0; i < nbVertex; i++) {
			myDegree = degrees[i];
			while (myDegree < d) {
				for (int j = 0; j < nbVertex; j++) {

					if (myDegree == d) {
						break;
					}
					
					if (i == j) {
						continue;
					} else if (matrice[i][j]) {
						continue;
					} else {

						degreeNeighbour = degrees[j];
						if (degreeNeighbour == 0) {
							continue;
						}

						proba = degreeNeighbour / (double) (nbAllDegrees - myDegree);

						System.out.println(proba);
						if (Math.random() < proba) {
							myDegree++;

							nbAllDegrees++;

							matrice[i][j] = true;
							actualGraph_nbEdges++;

							actualGraph_nbEdges++;

							degrees[i]++;
							if (degrees[i] > maxDegree) {
								maxDegree = degrees[i];
							}
							degrees[j]++;
							if (degrees[j] > maxDegree) {
								maxDegree = degrees[j];
							}

						}
					}
				}
			}
		}
		emptyGraph = false;
		actualGraph_degreeMaximum = maxDegree;
		return matrice;
	}

	public static void printMatrice(boolean[][] matrice) {
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

	public static void writeMatrice(boolean[][] matrice, OutputStream out) throws IOException {
		String ligneJ = "";
		byte[] byteJ = ligneJ.getBytes();
		byte[] jumpLine = "\n".getBytes();
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

	public static Graph createGraphFromMatrice(boolean[][] matrice, boolean oriented) {

		Graph graph = new Graph();
		for (int i = 0; i < matrice.length; i++) {
			for (int j = 0; j < i; j++) {
				if (matrice[i][j]) {
					graph.addEdge(i, j, oriented);
				}
			}
		}
		return graph;
	}

	
	public static void analyse(int flag) {
		
		Graph myGraph;
		ResultGloalAndLocal rstLG;
		ResultBFS rstBFS;

		INT_MinMaxAverage mma_nbVertex = new INT_MinMaxAverage(k_ASK, "nbVertex ");
		INT_MinMaxAverage mma_nbEdges = new INT_MinMaxAverage(k_ASK, "nbEdge   ");
		INT_MinMaxAverage mma_maxDegree = new INT_MinMaxAverage(k_ASK, "maxDeg   ");
		INT_MinMaxAverage mma_diameter = new INT_MinMaxAverage(k_ASK, "diameter ");
		DOUBLE_MinMaxAverage mma_APL = new DOUBLE_MinMaxAverage(k_ASK, "APL      ");
		INT_MinMaxAverage mma_nbTri = new INT_MinMaxAverage(k_ASK, "nbTri    ");
		INT_MinMaxAverage mma_nbV = new INT_MinMaxAverage(k_ASK, "nbV      ");
		DOUBLE_MinMaxAverage mma_cluG = new DOUBLE_MinMaxAverage(k_ASK, "Clu_G    ");
		DOUBLE_MinMaxAverage mma_cluL = new DOUBLE_MinMaxAverage(k_ASK, "Clu_L    ");

		
		boolean[][] matrice;
		
		
		for (int i = 0; i < k_ASK; i++) {

			if(verbose) {
				System.out.println(""+(i*100)/k_ASK+"% done");	
			}
			
			if(flag==flag_ERDOS) {
				matrice = erdosReny(nbVertex_ASK, proba_ASK);	
			}else if(flag==flag_BARABASI) {
				matrice = barabasiAlbert(d_ASK, n0_ASK, nbVertex_ASK);
			}else {
				System.out.println("ERREUR FLAG");
				return;
			}


			if (emptyGraph) {
				System.out.println("GRAPH is EMPTY");
				emptyGraph = true;
				continue;
			}
			//printMatrice(matrice);

			if (i == 0) {
				try {
					Path file2 = Paths.get("./" + outputFileName);
					OutputStream out = new BufferedOutputStream(Files.newOutputStream(file2, CREATE, TRUNCATE_EXISTING));
					writeMatrice(matrice, out);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}

			myGraph = createGraphFromMatrice(matrice, oriented);

			// ManageInput.printMemoryStart();

			rstBFS = Diameter_APL_Graph.diameter_and_APL_ofGraph_Stream(myGraph);
			rstLG = ClusteringCoefficient.globalAndLocal(myGraph);
			
			if (k_ASK == 1) {
				System.out.println("nbVertex "+actualGraph_nbVertex);
				System.out.println("nbEdge "+actualGraph_nbEdges);
				System.out.println("Max degree "+actualGraph_degreeMaximum);
				System.out.println(rstBFS);
				System.out.println(rstLG);				
				return;
			} else {
				mma_nbVertex.add(actualGraph_nbVertex);
				mma_nbEdges.add(actualGraph_nbEdges);
				mma_maxDegree.add(actualGraph_degreeMaximum);
				mma_diameter.add(rstBFS.diameter);
				mma_APL.add(rstBFS.getAPL());
				mma_nbTri.add(rstLG.nbTri);
				mma_nbV.add(rstLG.nbV);
				mma_cluG.add(rstLG.cluG);
				mma_cluL.add(rstLG.cluL);
			}
			
			if(i==k_ASK-1) {
				System.out.println(mma_nbVertex);
				System.out.println(mma_nbEdges);
				System.out.println(mma_maxDegree);
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

		outputFileName = "toto";
		nbVertex_ASK = 1000;
		proba_ASK = 5;
		k_ASK = 10;
		oriented = false;
		verbose = true;
		d_ASK=2;
		n0_ASK = 3;
		
		analyse(flag_ERDOS);
		// ManageInput.printMemoryEND();
	}
}
