import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class Exo1 {

	int nbSommets;
	int maxIdFind;
	int degreeMax;
	int nbArcs;

	public void printExo1() {
		System.out.println("nb Sommets : " + nbSommets + "\nMaxIdFind : " + maxIdFind + "\ndegreeMax : " + degreeMax
				+ "\nNbArcs " + nbArcs);
	}

	public static void parseFile(BufferedReader br, Exo1 reponses, boolean oriented) throws IOException {

		String line = "";
		Long nbLine = 0l;
		String[] arrayOfLine;

		int actualId;

		int actualIdVoisin;

		HashMap<Integer, Integer> lesSommets = new HashMap<>();

		while (line != null) {
			line = br.readLine();

			if (line == null) {
				break;
			}
			nbLine++;

			if(line.charAt(0)=='#') {
				continue;
			}
			
			arrayOfLine = line.split(" ");

			actualId = Integer.parseInt(arrayOfLine[0]);
			actualIdVoisin = Integer.parseInt(arrayOfLine[1]);

			if (actualId > reponses.maxIdFind) {
				reponses.maxIdFind = actualId;
			}
			if (actualIdVoisin > reponses.maxIdFind) {
				reponses.maxIdFind = actualIdVoisin;
			}

			Integer newValue = null;

			newValue = lesSommets.computeIfPresent(actualId, (k, v) -> v + 1);

			if (newValue == null) {
				lesSommets.put(actualId, 1);
				reponses.nbSommets++;
			} else {
				if (newValue > reponses.degreeMax) {
					reponses.degreeMax = newValue;
				}
			}

			reponses.nbArcs++;

			if (!oriented) {

				// reponses.nbArcs++; TODO

				newValue = null;

				newValue = lesSommets.computeIfPresent(actualIdVoisin, (k, v) -> v + 1);

				if (newValue == null) {
					lesSommets.put(actualIdVoisin, 1);
					reponses.nbSommets++;
				}
				else {
					if (newValue > reponses.degreeMax) {
						reponses.degreeMax = newValue;
					}
				}
				
			}

		}

	}
	public static void main(String[] args) {

		Exo1 reponses = new Exo1();

		
		if (args.length < 1) {
			System.out.println("il manque arguments");
			return;
		}

		boolean oriented = false;
		if (args.length > 1) {
			System.out.println(args[2]);
		}

		try {
			BufferedReader br = GraphPerso.getFile(args[0]);

			parseFile(br, reponses, oriented);

			reponses.printExo1();

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
