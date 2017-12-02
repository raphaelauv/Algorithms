import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class ManageInput{
	
	private static void printMemory(String msg) {
		System.out.println(msg+" | Mémoire allouée : " +
		(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + "octets");
	}
	
	public static void printMemoryStart() {
		printMemory("FIN LECTURE FICHIER + CREATION GRAPH");
	}
	
	public static void printMemoryEND() {
		printMemory("FIN PARCOUR");
	}
	
	public static boolean parseAndFillGraph2(Graph graph, BufferedReader file) throws IOException {
		String line = "";
		Long nbLine = 0l;
		String[] arrayOfLine;
		int actualId;
		int actualIdNeighbour;
		
		while ((line =file.readLine()) != null) {
			nbLine++;
			if (line.length()==0 || line.charAt(0) == '#') {
				continue;
			}
			
			arrayOfLine = line.split("\\s");
			if(arrayOfLine.length!=2) {
				System.out.println("ERREUR ligne "+nbLine+" format Invalide");
				return false;
			}

			//System.out.println(arrayOfLine[0] +" "+ arrayOfLine[1]);
			try {
				actualId = Integer.parseInt(arrayOfLine[0]);
				actualIdNeighbour = Integer.parseInt(arrayOfLine[1]);

				graph.addEdge(actualId, actualIdNeighbour);
			}catch(NumberFormatException e) {
				System.out.println("ERREUR ligne "+nbLine+" format Invalide");
				return false;
			}
			
		}	
		return true;

	}
		
	public static Graph creatGraph(String[] args) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			Graph myGraph = new Graph();

			if (!parseAndFillGraph2(myGraph, br)) {
				return null;
			}
			br.close();
			return myGraph;

		} catch (NumberFormatException e) {
			System.out.println("veuillez entrez un nombre valide");
		} catch (IOException e) {
			System.out.println("ERREUR DE FICHIER - LECTURE OU ECRITURE");
			System.out.println("verifier le nom du fichier d'input");
		}
		return null;
	}
	
	public static void missingArgs() {
		System.out.println("il manque arguments");
        System.out.println("Pour exécuter : java BetweennessCentrality [nom_du_fichier] [o]");
	}
}