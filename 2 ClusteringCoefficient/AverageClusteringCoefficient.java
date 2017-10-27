import java.util.Iterator;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AverageClusteringCoefficient {


	public static void averageClusteringCoefficient(Graph myGraph) {
		
		int corePoolSize = Runtime.getRuntime().availableProcessors();
		ExecutorService execute = Executors.newFixedThreadPool(corePoolSize);
		CompletionService<Integer> completion = new ExecutorCompletionService<>(execute);

		int nbTaskCreate = 0;
		Iterator<Node> itNodes = myGraph.mapNodes.values().iterator();
		
		while (itNodes.hasNext()) {
			completion.submit(new FindNbTriangles_OfX_WithOptimisation(itNodes.next(), true));
			nbTaskCreate++;
		}

		int nbTri_X = 0;
		int degree_X = 0;
		double sum_cluL_X = 0;
		
		for (int i = 0; i < nbTaskCreate; i++) {
			try {
				completion.take().get();
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("ERROR OCCURED");
				execute.shutdownNow();
				return;
			}
		}
		execute.shutdown();
		
		itNodes = myGraph.mapNodes.values().iterator();
		Node actualNode = null;
		while (itNodes.hasNext()) {
			actualNode = itNodes.next();
			nbTri_X = actualNode.getNbTriangle();
			degree_X = actualNode.neighbours.size();
			if (degree_X < 2) {
				continue;
			}
			sum_cluL_X += (2 * nbTri_X) / (double) (degree_X * (degree_X - 1));
			
		}
		double oneOnN = 1 / (double) (myGraph.mapNodes.size());
		double cluL_G = oneOnN * sum_cluL_X;
		System.out.println("CLU_L : " + cluL_G);
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			ManageInput.missingArgs();
			return;
		}
		
		Graph myGraph = ManageInput.creatGraph(args[0]);
		if(myGraph==null) {return;}
		
		ManageInput.printMemoryStart();
		averageClusteringCoefficient(myGraph);
		ManageInput.printMemoryEND();
		
	}
}
