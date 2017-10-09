import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

class Sommet {

	int id;
	
	int positionInArray;

	boolean alreadyAddToStackForVisite;
	int id_CC;
	
	LinkedList<Sommet> voisins;

	public Sommet(int id) {

		this.alreadyAddToStackForVisite = false;
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

	int nb_CC;
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

		//System.out.println("ON AJOUTE : " + toAd.id);

		this.sommets.add(toAd);

		this.size++;
		this.positionInList.put(toAd.id, this.size - 1);

	}

	public void PFS(int idSommetD) {
		
		int id_CC_Actual=1; // important de commencer a 1 car 0 veut dire null
		
		int positionD=this.getPositionInList(idSommetD);
		
		System.out.println("NOUS COMMENCER EN POSITION : "+positionD +" pour le sommet de ID : "+idSommetD);
		
		int nbvue = 0;
		
		int minUnvisited = 0;
		
		int minUnvisred_AfterD = positionD+1;
		
		if(positionD==0) {
			minUnvisited=1;
		}
		
		this.nb_CC=1;

		// Ajouter du sommet D d'une premiere composante connexe
		
		boolean inCCofD=true;
		
		int nb_Sommet_accessibleFromD=0;
		
		Sommet actualSommet = this.sommets.get(positionD);

		Queue<Sommet> pile = new LinkedList<>();

		pile.add(actualSommet);
		actualSommet.alreadyAddToStackForVisite=true;

		while (nbvue <= this.size) {

			while (!pile.isEmpty()) {

				actualSommet = pile.remove();

				// actualiser debut autre composante connexe
				if (actualSommet.positionInArray == minUnvisited) {
					minUnvisited++;
					
					
					// beacause we already start with the position of D  , we go to the following one 
					if(minUnvisited==positionD) {
						minUnvisited=minUnvisred_AfterD;
					}
					
					
				}

				//TODO a verifier
				if(actualSommet.positionInArray == minUnvisred_AfterD) {
					minUnvisred_AfterD++;
				}
				
				
				actualSommet.id_CC=id_CC_Actual;

				System.out.println("sommet visiter :" + actualSommet.id +" composante associé "+id_CC_Actual);

				nbvue++;

				if(this.isDirected) {
				
					HashSet<Integer> CC_AlreadySeen = new HashSet<>(); 
					
					for (Sommet unVoisin : actualSommet.voisins) {

						if(unVoisin.id_CC!=0) {
							
							if(id_CC_Actual != unVoisin.id_CC) {
								
								if(! CC_AlreadySeen.contains(unVoisin.id_CC)) { //TODO
									this.nb_CC--;
									System.out.println("ON IDENTIFIE UNE ANCIENNE CC");
									CC_AlreadySeen.add(unVoisin.id_CC);
								}
								
							}
						}
						
						if (!unVoisin.alreadyAddToStackForVisite) {
							
							if(inCCofD) {
								nb_Sommet_accessibleFromD++;
							}
							
							pile.add(unVoisin);
							unVoisin.alreadyAddToStackForVisite = true;
						}
						
					}
					
					
				}else {
					for (Sommet unVoisin : actualSommet.voisins) {

						if (!unVoisin.alreadyAddToStackForVisite) {
							
							if(inCCofD) {
								nb_Sommet_accessibleFromD++;
							}
							
							pile.add(unVoisin);
							unVoisin.alreadyAddToStackForVisite = true;
						}
						
					}
				}
			}
			
			inCCofD=false;

			if (nbvue >= this.size) {
				break;
			}
			
			
			// Ajouter premier noeud d'une autre composante connexe
			actualSommet = this.sommets.get(minUnvisited);
			minUnvisited++;
			
			System.out.println("NOUS DEPLACONS EN POSITION : "+minUnvisited +" pour le sommet de ID : "+actualSommet.id);
			pile.add(actualSommet);
			actualSommet.alreadyAddToStackForVisite=true;
			this.nb_CC++;
			
			id_CC_Actual++;

		}
		
		System.out.println("\nnb Sommet accessible from Id "+idSommetD+" : "+nb_Sommet_accessibleFromD);
		
		System.out.println("nb composantes connex "+this.nb_CC);

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
	
	public int getPositionInList(int idToFind) {
		if (this.positionInList.containsKey(idToFind)) {
			return this.positionInList.get(idToFind);
		}

		return -1;
	}

}


public class Exo2 {

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

		if (args.length < 1) {
			System.out.println("il manque arguments");
			return;
		}

		System.out.println(args[0]);

		try {
			BufferedReader br = GraphPerso.getFile(args[0]);

			Graph monGraph = new Graph(true);

			parseAndFillGraph(monGraph, br);

			monGraph.PFS(2);

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
