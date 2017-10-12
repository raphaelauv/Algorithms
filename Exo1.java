import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Exo1 {

	int nbSommets;
	int maxIdFind;
	int degreeMax;
	int nbArcs;

	@Override
	public String toString() {
		return "nb Sommets : " + nbSommets + "\nMaxIdFind : " + maxIdFind + "\ndegreeMax : " + degreeMax
				+ "\nNbArcs " + nbArcs;
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
			
			arrayOfLine = line.split("\\s");

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
            System.out.println("Pour exécuter java Exo1 [nom_du_fichier] [-o] ");

			return;
		}

		boolean oriented = false;
		if (args.length > 1) {
			if(args[1].equals("-o"))
                oriented=true;
            else{
                System.out.println("option inexistante "+args[1]+" essayer -o pour un graphe orienté");
                return;
            }
                
		}
        
        
		try {
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			
			parseFile(br, reponses, oriented);

			System.out.println(reponses);

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
