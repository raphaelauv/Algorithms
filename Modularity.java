import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

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
	public static void printAllBetweennessCentralityPAR(Graph myGraph) {
		
		long startTime = System.nanoTime();
		
		int nbNodes= myGraph.nbNodes;
		Partition myParti = new Partition(myGraph);
		

		int nbEiiIter;
		double qMax=-1;
		
		//double [] qpPrime;
		int [] pSuiv= new int[2];
		 
		int[][] clusters =null;
		boolean newBest;
		int nbClusters=nbNodes;
		for (int i=0;i<nbNodes-1;i++) {
			
			int nbComparaisons = (nbClusters*(nbClusters-1))/2;
			int[][] arrayOfTuple = new int[nbComparaisons][2];
			int cmp=0;
			for( int a=0;a<nbNodes;a++) {
				for( int b=a;b<nbNodes;b++) {
					if(a==b) {
						continue;
					}
					if(!myParti.isChefs(a,b)) {
						continue;
					}
					arrayOfTuple[cmp][0]=a;
					arrayOfTuple[cmp][1]=b;
					cmp++;
				}
			}
			nbClusters--;
			
			
			
			nbEiiIter=-1;
			newBest=false;
			//double[] qIterInit=new double[] {-1,0,0,0};
			
			Stream<int[]> streamOfNodes =Stream.of(arrayOfTuple).parallel();
			Stream<double[]> streamOfResults = streamOfNodes.map(new Let_getPprime_QPprime(myParti));
			Optional<double[]> qIterBestOP = streamOfResults.reduce((qIter ,qpPrime) -> {
				if (qpPrime[0] > qIter[0]) {
					qIter[0] = qpPrime[0];
					qIter[1] = (int) qpPrime[1];
					qIter[2] = (int) qpPrime[2];
					qIter[3] = (int) qpPrime[3];
					
				}
				return qIter;
			});
			
			double[] qIterBest= qIterBestOP.get();
			
			pSuiv[0]=(int) qIterBest[2];
			pSuiv[1]=(int) qIterBest[3];
			nbEiiIter= (int) qIterBest[1];
			myParti.performFusion(pSuiv[0],pSuiv[1],nbEiiIter);
			//System.out.println("FUSION "+pSuiv[0]+" "+pSuiv[1]);
			if(qIterBest[0]>qMax) {
				qMax=qIterBest[0];
				clusters=myParti.getClusters();
				newBest=true;
			}
			if(verbose) {
				if(newBest) {
					Partition.printClusters(clusters);	
				}else {
					Partition.printClusters(myParti.getClusters());
				}
				
				System.out.println(" : "+qIterBest[0]);
			}
			
		}
		System.out.println("RESULT");
		Partition.printClusters(clusters);
		System.out.println(" : "+qMax);
		
		long endTime = System.nanoTime();
		System.out.println(endTime - startTime);
	}
	
	public static void printAllBetweennessCentrality(Graph myGraph) {
		
		long startTime = System.nanoTime();
		
		int nbNodes= myGraph.nbNodes;
		Partition myParti = new Partition(myGraph);

		double qIter=-1;
		int nbEiiIter;
		double qMax=-1;
		
		double [] qpPrime;
		int [] pSuiv= new int[2];
		 
		int[][] clusters =null;
		
		boolean newBest;
		for (int i=0;i<nbNodes-1;i++) {
			qIter=-1;
			nbEiiIter=-1;
			newBest=false;
			for( int a=0;a<nbNodes;a++) {
				for( int b=a;b<nbNodes;b++) {
					if(a==b) {
						continue;
					}
					if(!myParti.isChefs(a,b)) {
						continue;
					}
					qpPrime = myParti.getPprime_QPprime(a,b);
					if(qpPrime[0]>qIter) {
						pSuiv[0]=a;
						pSuiv[1]=b;
						qIter=qpPrime[0];
						nbEiiIter=(int) qpPrime[1];
					}
				}
			}
			
			myParti.performFusion(pSuiv[0],pSuiv[1],nbEiiIter);
			//System.out.println("FUSION "+pSuiv[0]+" "+pSuiv[1]);
			if(qIter>qMax) {
				qMax=qIter;
				clusters=myParti.getClusters();
				newBest=true;
			}
			if(verbose) {
				if(newBest) {
					Partition.printClusters(clusters);	
				}else {
					Partition.printClusters(myParti.getClusters());
				}
				
				System.out.println(" : "+qIter);
			}
			
		}
		System.out.println("RESULT");
		Partition.printClusters(clusters);
		System.out.println(" : "+qMax);
		writeFile(clusters,qMax);
		
		long endTime = System.nanoTime();
		System.out.println(endTime - startTime);
		
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
		printAllBetweennessCentrality(myGraph);
		//printAllBetweennessCentralityPAR(myGraph);
		ManageInput.printMemoryEND();
		
	}
}
