
public class AverageClusteringCoefficient {

	public static void main(String[] args) {
		if (args.length < 1) {
			ManageInput.missingArgs();
			return;
		}
		
		Graph myGraph = ManageInput.creatGraph(args[0]);
		if(myGraph==null) {return;}
		myGraph.averageClusteringCoefficient();
		
	}
}
