import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Exo1 {

	int nbNodes;
	int maxIdFind;
	int degreeMax;
	int nbEdges;

	@Override
	public String toString() {
		return "nb Sommets : " + nbNodes + "\nMaxIdFind : " + maxIdFind + "\ndegreeMax : " + degreeMax + "\nNbArcs : "
				+ nbEdges;
	}

	public static void parseFile(BufferedReader br, Exo1 answers, boolean oriented) throws IOException {

		String line = "";
		Long nbLine = 0l;
		String[] arrayOfLine;

		int actualId;
		int actualIdNeighbour;

		HashMap<Integer, Integer> nodes_and_degree= new HashMap<>();

		Integer degree;
		while (line != null) {

			line = br.readLine();

			if (line == null) {
				break;
			}
			nbLine++;

			if (line.charAt(0) == '#') {
				continue;
			}

			arrayOfLine = line.split("\\s");

			actualId = Integer.parseInt(arrayOfLine[0]);
			actualIdNeighbour = Integer.parseInt(arrayOfLine[1]);

			if (actualId > answers.maxIdFind) {
				answers.maxIdFind = actualId;
			}
			if (actualIdNeighbour > answers.maxIdFind) {
				answers.maxIdFind = actualIdNeighbour;
			}

			degree = nodes_and_degree.computeIfPresent(actualId, (k, v) -> v + 1);

			if (degree == null) {
				nodes_and_degree.put(actualId, 1);
				answers.nbNodes++;
			} else {
				if (degree > answers.degreeMax) {
					answers.degreeMax = degree;
				}
			}

			answers.nbEdges++;

			if (oriented) {
				degree = nodes_and_degree.get(actualIdNeighbour);
				if (degree == null) {
					nodes_and_degree.put(actualIdNeighbour, 0);
					answers.nbNodes++;
				}
			} else {
				answers.nbEdges++;
				degree = nodes_and_degree.computeIfPresent(actualIdNeighbour, (k, v) -> v + 1);
				if (degree == null) {
					nodes_and_degree.put(actualIdNeighbour, 1);
					answers.nbNodes++;
					degree=1;
				}
				
				if (degree > answers.degreeMax) {
					answers.degreeMax = degree;
				}
			}

		}

	}

	public static void main(String[] args) {

		Exo1 answers = new Exo1();

		if (args.length < 1) {
			System.out.println("il manque arguments");
			System.out.println("Pour exécuter java Exo1 [nom_du_fichier] [-o] ");

			return;
		}

		boolean oriented = false;
		if (args.length > 1) {
			if (args[1].equals("-o"))
				oriented = true;
			else {
				System.out.println("option inexistante " + args[1] + " essayer -o pour un graphe orienté");
				return;
			}
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(args[0]));

			parseFile(br, answers, oriented);

			System.out.println(answers);

		} catch (NumberFormatException e) {
			System.out.println("veuillez entrez un nombre valide");
		} catch (IOException e) {
			System.out.println("ERREUR DE FICHIER - LECTURE OU ECRITURE");
            System.out.println("verifier le nom du fichier d'input");
		}

	}
}
