import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.PriorityQueue;
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
		double result = myParti.getQP_OP_PART(a,b);
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
		double [] result= myParti.getPprime_QPprime_PAR(a,b);
		return new double[] {result[0],result[1],a,b};
	}
}

class Truple{
	int a;
	int b;
	double Qp;
	public Truple(int a, int b, double qp) {
		this.a = a;
		this.b = b;
		Qp = qp;
	}
}

public class Modularity {
	
	public static String nameOutput;
	public static boolean verbose=false;
	public static String nameGraphFile;
	
	public static void main(String[] args) {
		
		nameGraphFile =ManageInput.parseOptions(args);
		
		if(nameGraphFile==null) {
			ManageInput.missingArgs();
			return;
		}
		
		Graph myGraph = ManageInput.creatGraph(nameGraphFile);
		if(myGraph==null) {return;}
		
		ManageInput.printMemoryStart();
		
		
		printModularity(myGraph,true); //iterative faster in optimized mode
		//printModularity_PAR(myGraph,false); //parallel faster in non optimized mode
		
		ManageInput.printMemoryEND();
		
	}
	
	
	public static void editname(boolean optimized ,boolean parallel) {
		
		if(optimized) {
			nameOutput="OPTIMIZED ";
		}else {
			nameOutput="BASIC ";
		}
		if(parallel) {
			nameOutput+="PARALLEL";
		}else {
			nameOutput+="ITERATIVE";
		}
	}
	
	public static void endAlgo(long startTime,int[][] clusters,double qMax) {
		
		System.out.println("\nRESULT "+nameOutput);
		Partition.printClusters(clusters);
		System.out.println(" : "+qMax);
		writeFile(clusters,qMax);
		long endTime = System.nanoTime();
		System.out.println("TIME : "+ (endTime - startTime));
	}
	
	public static void doVerbose(boolean newBest,Partition myParti,int[][] clusters,double resultToShow) {
		
		if(newBest) {
			Partition.printClusters(clusters);	
		}else {
			Partition.printClusters(myParti.getClusters());
		}	
		System.out.println(" : "+resultToShow);
	
	}
	
	public static void writeFile(int[][] clusters,double qMax) {
		//String outputFileName ="out";
		
		Path file2 = Paths.get("./" + nameGraphFile+".clu");

		OutputStream out;
		try {
			out = new BufferedOutputStream(Files.newOutputStream(file2, CREATE, TRUNCATE_EXISTING));
			Partition.writeClusters(clusters,qMax,out);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
		
	/*
	 * parallelised version
	 */
	public static void printModularity_PAR(Graph myGraph,boolean optimisedVersion) {
		
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
		
		//faster than new int [nbClusters][2] every pass of the loop
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
			
			double newResult;
			
			if(optimisedVersion) {
				Stream<double[]> streamOfResults = streamOfNodes.map(new Let_getQP_OPT(myParti));
				Optional<double[]> qIterBestOP = streamOfResults.reduce((qIterReduce ,diffç_qpPrime_qIter) -> {
					if (diffç_qpPrime_qIter[0] > qIterReduce[0]) {
						qIterReduce[0] = diffç_qpPrime_qIter[0];
						qIterReduce[1] = diffç_qpPrime_qIter[1];
						qIterReduce[2] = diffç_qpPrime_qIter[2];
					}
					return qIterReduce;
				});
				
				double[] diffç_qpPrime_qIterBest= qIterBestOP.get();
				
				qIter = diffç_qpPrime_qIterBest[0];
				pSuiv[0]=(int) diffç_qpPrime_qIterBest[1];
				pSuiv[1]=(int) diffç_qpPrime_qIterBest[2];
				
				myParti.performFusion_OPT(pSuiv[0],pSuiv[1]);
				
				newResult=qIter+qMax;
				
			}else {
				Stream<double[]> streamOfResults = streamOfNodes.map(new Let_getPprime_QPprime(myParti));
				Optional<double[]> qIterBestOP = streamOfResults.reduce((qIterReduce,qpPrime) -> {
					if (qpPrime[0] > qIterReduce[0]) {
						qIterReduce[0] = qpPrime[0];
						qIterReduce[1] = qpPrime[1];
						qIterReduce[2] = qpPrime[2];	
						qIterReduce[3] = qpPrime[3];
						
					}
					return qIterReduce;
				});
				
				double[] qIterBest= qIterBestOP.get();
				
				qIter=qIterBest[0];
				pSuiv[0]=(int) qIterBest[2];
				pSuiv[1]=(int) qIterBest[3];
				nbEiiIter= (int) qIterBest[1];
				
				myParti.performFusion(pSuiv[0],pSuiv[1],nbEiiIter);
				
				newResult=qIter;
			}
			
			if(newResult>qMax) {
				if(optimisedVersion) {
					qMax += qIter;
				}else {
					qMax = qIter;
				}
				clusters=myParti.getClusters();
				newBest=true;
			}
			
			if(verbose) {
				doVerbose(newBest,myParti,clusters,newResult);
			}
		}
		editname(optimisedVersion,true);
		endAlgo(startTime,clusters,qMax);
	}
	
	/*
	 * iterative version
	 */
	public static void printModularity(Graph myGraph,boolean optimisedVersion) {
		
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
		double newResult;
		
		
		if(optimisedVersion) {
			qMax = 0;
		}else {
			qMax = -1;
		}
		
		/*
		 * Heap solution
		PriorityQueue<Truple> maxPQ = new PriorityQueue<Truple>((nbNodes*(nbNodes-1))/2,Collections.reverseOrder());
		for( int a=0;a<nbNodes;a++) {
			for( int b=a;b<nbNodes;b++) {
				double rst= myParti.getQP_OPT(a,b);
				maxPQ.add(new Truple(a,b,rst));
			}
		}
		Truple tmp;
		for( int a=0;a<nbNodes;a++) {
			tmp=maxPQ.poll();
			if(!myParti.isChefs(tmp.a,tmp.b)) {
				continue;
			}
		}
		*/
		
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
				newResult = qIter+qMax;
				
			}else {
				myParti.performFusion(pSuiv[0],pSuiv[1],nbEiiIter);
				newResult= qIter;
			}
			
			if(newResult>qMax) {
				if(optimisedVersion) {
					qMax += qIter;
				}else {
					qMax = qIter;
				}
				clusters=myParti.getClusters();
				newBest=true;
			}
			
			if(verbose) {
				doVerbose(newBest,myParti,clusters,newResult);
			}
		
		}
		editname(optimisedVersion,false);
		endAlgo(startTime,clusters,qMax);
	}
}
