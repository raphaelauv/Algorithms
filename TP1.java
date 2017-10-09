import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

class Sommet {

	int id;

	int positionInArray;

	boolean alreadyVisited;
	LinkedList<Sommet> voisins;

	public Sommet(int id) {

		this.alreadyVisited = false;
		this.voisins = new LinkedList<>();
		this.id = id;
	}

	public void insertArc(Sommet idVoisin) {
		this.voisins.add(idVoisin);
	}

}

class Graph {

	int size;
	boolean isDirected;

	ArrayList<Sommet> sommets;
	HashMap<Integer, Integer> positionInList;

	Sommet firstSommet;

	public Graph(boolean isDirected) {

		this.isDirected = isDirected;
		this.size = 0;
		this.sommets = new ArrayList<>();
		this.positionInList = new HashMap<Integer, Integer>();

	}

	public void insertSommet(Sommet toAd) {
		if (firstSommet == null) {
			firstSommet = toAd;
		}

		toAd.positionInArray = this.size;

		System.out.println("ON AJOUTE : " + toAd.id);

		this.sommets.add(toAd);

		this.size++;
		this.positionInList.put(toAd.id, this.size - 1);

	}

	public void PFS() {

		int nbvue = 0;

		int minUnvisited = 1;

		// Ajouter premier noeud d'une premiere composante connexe
		Sommet actualSommet = this.sommets.get(0);

		Queue<Sommet> pile = new LinkedList<>();

		pile.add(actualSommet);

		while (nbvue <= this.size) {

			while (!pile.isEmpty()) {

				actualSommet = pile.remove();

				// actualiser debut autre composante connexe
				if (actualSommet.positionInArray == minUnvisited) {
					minUnvisited++;
				}

				actualSommet.alreadyVisited = true;

				System.out.println("sommet visiter :" + actualSommet.id);

				nbvue++;

				for (Sommet unVoisin : actualSommet.voisins) {

					if (!unVoisin.alreadyVisited) {
						pile.add(unVoisin);
					}
				}
			}

			// System.out.println("SIZE MIN : "+minUnvisited);

			if (nbvue >= this.size) {
				break;
			}
			// Ajouter premier noeud d'une autre composante connexe
			actualSommet = this.sommets.get(minUnvisited);
			pile.add(actualSommet);

		}

	}

	public void addArc(int actualId, int actualIdVoisin) {

		Sommet actualSommet;

		Sommet actualSommetVoisin;

		actualSommet = this.getSommet(actualId);

		if (actualSommet == null) {
			actualSommet = new Sommet(actualId);
			this.insertSommet(actualSommet);
		}

		actualSommetVoisin = this.getSommet(actualIdVoisin);

		if (actualSommetVoisin == null) {
			actualSommetVoisin = new Sommet(actualIdVoisin);
			this.insertSommet(actualSommetVoisin);
		}

		actualSommet.insertArc(actualSommetVoisin);

		if (!this.isDirected) {
			actualSommetVoisin.insertArc(actualSommet);
		}

	}

	public Sommet getSommet(int idToFind) {

		if (this.positionInList.containsKey(idToFind)) {
			int position = this.positionInList.get(idToFind);

			return this.sommets.get(position);
		}

		return null;
	}

}


public class TP1 {

	public static int findDegreeOfSommet(Graph graph, int idSommet) {

		Sommet actualSommet;

		actualSommet = graph.getSommet(idSommet);

		if (actualSommet == null) {

		}

		int nbVoisins=0;

		for (Sommet unVoisin : actualSommet.voisins) {

			nbVoisins++;

		}
		
		return nbVoisins;

	}

	public static void parseAndFillGraph(Graph graph, BufferedReader file) throws IOException {

		String line = "";
		Long nbLine = 0l;
		String[] arrayOfLine;

		int actualId;

		int actualIdVoisin;

		while (line != null) {
			line = file.readLine();

			if (line == null) {
				break;
			}

			if (line.charAt(0) == '#') {
				continue;
			}

			nbLine++;

			// System.out.println(line);
			arrayOfLine = line.split(" ");

			// System.out.println(arrayOfLine[0]);
			// System.out.println(arrayOfLine[1]);

			actualId = Integer.parseInt(arrayOfLine[0]);

			actualIdVoisin = Integer.parseInt(arrayOfLine[1]);

			graph.addArc(actualId, actualIdVoisin);
		}

	}

	public static void main(String[] args) {

		System.out.println(Integer.MAX_VALUE);
		if (args.length < 1) {
			System.out.println("il manque arguments");
			return;
		}

		System.out.println(args[0]);

		try {
			BufferedReader br = GraphPerso.getFile(args[0]);

			Graph monGraph = new Graph(false);

			parseAndFillGraph(monGraph, br);

			monGraph.PFS();

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
