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
	
	public static String parseOptions(String[] args) {
		
		if (args.length < 1 || args.length>2) {
			ManageInput.missingArgs();
			return null;
		}
		String nameFileInput=null;
		for(int i=0;i<args.length;i++) {
			if(args[i].equals("-v")) {
				Modularity.verbose=true;
			}else {
				nameFileInput=args[i];
			}
		}
		return nameFileInput;
	}
	
	public static Graph creatGraph(String args) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(args));
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
		System.out.println("erreur arguments");
        System.out.println("Pour exécuter : java Modularity [graph_name_file] [-v]");
	}
}