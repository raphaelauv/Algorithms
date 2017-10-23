
public class AverageClusteringCoefficient {

	public static void main(String[] args) {
		if (args.length < 1) {
			GlobalClusteringCoefficient.missingArgs();
			return;
		}
		
		Graph myGraph = GlobalClusteringCoefficient.creatGraph(args[0]);
		if(myGraph==null) {return;}
		myGraph.averageClusteringCoefficient();
		
	}
}
