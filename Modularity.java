import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;


class Let_getPprime_QPprime implements Function<int[],double []> {
	
	ArrayList<Node> listNodes;
	public Let_getPprime_QPprime(ArrayList<Node> listNodes) {
		this.listNodes=listNodes;
	}
	
	@Override
	public double[] apply(int[] aAndB) {
	
		return new double[2];
	}
}


public class Modularity {
	
	
	public static void printAllBetweennessCentrality(Graph myGraph) {
		
		//long startTime = System.nanoTime();
	
		
		ArrayList<Node> listNodes = myGraph.getListeNodes();
	
		int nbNodes = listNodes.size();
		Partition myParti = new Partition(myGraph);

		double qIter;
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
		
		
		return;
		
		
		
		/*
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		
		FixedDataStruckPool dataPool=new FixedDataStruckPool(corePoolSize);
		
		Stream<Node> streamOfNodes = listNodes.stream().parallel();
		Stream<EmptyResult> streamOfResults = streamOfNodes.map(new Let_BFS(dataPool));
		streamOfResults.forEach(x-> {});
		
		Stream<Node> streamOfNodes2 = listNodes.stream().parallel();
		Stream<EmptyResult> streamOfResults2 = streamOfNodes2.map(new Let_Bet_V(listNodes));
		streamOfResults2.forEach(x -> {});
		*/
		//long endTime = System.nanoTime();
		//System.out.println(endTime - startTime);
		
		
		//System.out.println("nb nodes : "+listNodes.size());
		//System.out.println("nb edges : "+myGraph.nbEdges);
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
		ManageInput.printMemoryEND();
		
	}
}
