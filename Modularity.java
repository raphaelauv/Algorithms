import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;


/*
 * for optimised version
 */
class Let_getQP_OPT implements Function<int[],double []> {
	
	Partition myParti;
	Let_getQP_OPT(Partition myParti){
		this.myParti=myParti;
	}
	
	@Override
	public double[] apply(int[] aAndB) {
		int a =aAndB[0];
		int b =aAndB[1];
		double result = myParti.getQP_OPT(a,b);
		return new double[] {result,a,b};
	}
}

/*
 * for basic version
 */
class Let_getPprime_QPprime implements Function<int[],double []> {
	
	Partition myParti;
	Let_getPprime_QPprime(Partition myParti){
		this.myParti=myParti;
	}
	
	@Override
	public double[] apply(int[] aAndB) {
		int a =aAndB[0];
		int b =aAndB[1];
		double [] result= myParti.getPprime_QPprimePAR(a,b);
		return new double[] {result[0],result[1],a,b};
	}
}


public class Modularity {
	
	public static void endAlgo(String algoName,long startTime,int[][] clusters,double qMax) {
		
		System.out.println("\nRESULT"+algoName);
		Partition.printClusters(clusters);
		System.out.println(" : "+qMax);
		writeFile(clusters,qMax);
		long endTime = System.nanoTime();
		System.out.println("TIME : "+ (endTime - startTime));
	}
	
	public static void writeFile(int[][] clusters,double qMax) {
		String outputFileName ="out";
		Path file2 = Paths.get("./" + outputFileName+".clu");

		OutputStream out;
		try {
			out = new BufferedOutputStream(Files.newOutputStream(file2, CREATE, TRUNCATE_EXISTING));
			Partition.writeClusters(clusters,qMax,out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		
	/*
	 * parallelised version
	 */
	public static void printAllModularity_PAR(Graph myGraph,boolean optimisedVersion) {
		
		long startTime = System.nanoTime();
		
		int nbNodes= myGraph.nbNodes;
		Partition myParti = new Partition(myGraph);
		
		int nbEiiIter;
		double qMax;
		int [] pSuiv= new int[2];
		double qIter=-1;
		int[][] clusters =null;
		boolean newBest;
		int nbClusters=nbNodes;
		
		//faster than new array of array every pass of the loop
		ArrayList<int[]> arrayofTuples = new ArrayList<>(nbClusters); 
		
		if(optimisedVersion) {
			qMax = 0;
		}else {
			qMax = -1;
		}
		
		for (int i=0;i<nbNodes-1;i++) {
			
			arrayofTuples.clear();
			newBest=false;
			qIter=-1;
			for( int a=0;a<nbNodes;a++) {
				for( int b=a;b<nbNodes;b++) {
					if(a==b) {
						continue;
					}
					if(!myParti.isChefs(a,b)) {
						continue;
					}
					arrayofTuples.add(new int[] {a,b});
				}
			}
			nbClusters--;
			
			Stream<int[]> streamOfNodes = arrayofTuples.stream().parallel();
			
			double resultToShow;
			
			if(optimisedVersion) {
				Stream<double[]> streamOfResults = streamOfNodes.map(new Let_getQP_OPT(myParti));
				double[] qIterBestOP = streamOfResults.reduce(new double[]{-1,0,0},(qIterReduce ,diffç_qpPrime_qIter) -> {
					if (diffç_qpPrime_qIter[0] > qIterReduce[0]) {
						qIterReduce[0] = diffç_qpPrime_qIter[0];
						qIterReduce[1] = diffç_qpPrime_qIter[1];
						qIterReduce[2] = diffç_qpPrime_qIter[2];
					}
					return Arrays.copyOf(qIterReduce,qIterReduce.length);
					
				});
				
				double[] diffç_qpPrime_qIterBest= qIterBestOP;
				
				qIter = diffç_qpPrime_qIterBest[0];
				pSuiv[0]=(int) diffç_qpPrime_qIterBest[1];
				pSuiv[1]=(int) diffç_qpPrime_qIterBest[2];
				
				myParti.performFusion_OPT(pSuiv[0],pSuiv[1]);
				
				resultToShow=qIter+qMax;
				
			}else {
				Stream<double[]> streamOfResults = streamOfNodes.map(new Let_getPprime_QPprime(myParti));
				Optional<double[]> qIterBestOP = streamOfResults.reduce((qIterReduce,qpPrime) -> {
					if (qpPrime[0] > qIterReduce[0]) {
						if(qpPrime[0]>1) {
							//TODO bug
							System.out.println("ICI :"+qpPrime[0]);
						}
						qIterReduce[0] = qpPrime[0];
						qIterReduce[1] = qpPrime[1];
						qIterReduce[2] = qpPrime[2];	
						qIterReduce[3] = qpPrime[3];
						
					}
					return Arrays.copyOf(qIterReduce,qIterReduce.length);
					//return qIterReduce;
				});
				
				double[] qIterBest= qIterBestOP.get();
				
				qIter=qIterBest[0];
				pSuiv[0]=(int) qIterBest[2];
				pSuiv[1]=(int) qIterBest[3];
				nbEiiIter= (int) qIterBest[1];
				
				myParti.performFusion(pSuiv[0],pSuiv[1],nbEiiIter);
				
				resultToShow=qIter;
			}
			
			if(resultToShow>qMax) {
				if(optimisedVersion) {
					
					qMax += qIter;
				}else {
					System.out.println("qMax : "+qMax +" Qiter: "+qIter);
					qMax = qIter;
					System.out.println(qMax);
				}
				clusters=myParti.getClusters();
				newBest=true;
			}
			
			
			if(verbose) {
				doVerbose(newBest,myParti,clusters,resultToShow);
			}
		}
		
		endAlgo("OPT PAR",startTime,clusters,qMax);
	}

	public static void doVerbose(boolean newBest,Partition myParti,int[][] clusters,double resultToShow) {
		
		if(newBest) {
			Partition.printClusters(clusters);	
		}else {
			Partition.printClusters(myParti.getClusters());
		}	
		System.out.println(" : "+resultToShow);
	
	}
	
	/*
	 * iterative version
	 */
	public static void printAllModularity(Graph myGraph,boolean optimisedVersion) {
		
		long startTime = System.nanoTime();
		
		int nbNodes= myGraph.nbNodes;
		Partition myParti = new Partition(myGraph);
	
		// for basic version
		int nbEiiIter=0;
		double [] qpPrime;
		
		// for OPT version
		double diffç_qpPrime_qIter=0;
		
		
		// commum to both version
		double qIter;
		double qMax;
		int [] pSuiv= new int[2];
		int[][] clusters =null;
		boolean newBest;
		double resultToShow;
		
		if(optimisedVersion) {
			qMax = 0;
		}else {
			qMax = -1;
		}
		
		for (int i=0;i<nbNodes-1;i++) {
			qIter=-1;
			newBest=false;
			for( int a=0;a<nbNodes;a++) {
				for( int b=a;b<nbNodes;b++) {
					if(a==b) {
						continue;
					}
					if(!myParti.isChefs(a,b)) {
						continue;
					}
					
					if(optimisedVersion) {
						diffç_qpPrime_qIter=myParti.getQP_OPT(a,b);
						if(diffç_qpPrime_qIter>qIter) {
							pSuiv[0]=a;
							pSuiv[1]=b;
							qIter=diffç_qpPrime_qIter;
						}
					}else {
						qpPrime = myParti.getPprime_QPprime(a,b);
						if(qpPrime[0]>qIter) {
							pSuiv[0]=a;
							pSuiv[1]=b;
							qIter=qpPrime[0];
							nbEiiIter=(int) qpPrime[1];
						}
					}
					
					
				}
			}
			
			if(optimisedVersion) {
				myParti.performFusion_OPT(pSuiv[0],pSuiv[1]);
				resultToShow = qIter+qMax;
				
			}else {
				myParti.performFusion(pSuiv[0],pSuiv[1],nbEiiIter);
				resultToShow= qIter;
			}
			
			if(resultToShow>qMax) {
				if(optimisedVersion) {
					qMax += qIter;
				}else {
					qMax = qIter;
				}
				clusters=myParti.getClusters();
				newBest=true;
			}
			
			if(verbose) {
				doVerbose(newBest,myParti,clusters,resultToShow);
			}
		
		}
		
		endAlgo("OPT",startTime,clusters,qMax);
		
	}

	
	public static boolean verbose=false;
	
	public static void main(String[] args) {
		
		String nameGraphFile =ManageInput.parseOptions(args);
		
		if(nameGraphFile==null) {
			ManageInput.missingArgs();
			return;
		}
		
		Graph myGraph = ManageInput.creatGraph(nameGraphFile);
		if(myGraph==null) {return;}
		
		ManageInput.printMemoryStart();
		printAllModularity(myGraph,true); //faster
		//printAllModularity_PAR(myGraph,true);
		
		ManageInput.printMemoryEND();
		
	}
}
