import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

class CC{
	int id;
	private CC pere;
	
	CC(int id){
		this.id=id;
		this.pere=null;
	}
	
	void setPere(CC pere) {
		this.pere=pere;
	}
	
	CC getLastPere() {
		//System.out.println("RECHERCHE PERE");
		if(pere==null) {return null;}
		CC tmp=pere;
		//System.out.println("pour "+id+" pere-> "+pere.id);
		
		while(tmp.pere!=null) {
			
			tmp=tmp.pere;
			this.pere = tmp;
			//System.out.println("pour "+id+" pere-> "+pere.id);
		}
		return tmp;
	}
}

class Sommet {

	int id;
	CC id_CC;
	boolean alreadyAddToStackForVisite;
	LinkedList<Sommet> voisins;
	
	//int positionInArray;

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

	boolean isDirected;
	boolean isVerbose;
	boolean isFile;
	BufferedOutputStream out;
	LinkedHashMap<Integer, Sommet> mapSommets;
	
	
	/* MODE MAP + LIST
	ArrayList<Sommet> sommets;
	HashMap<Integer, Integer> positionInList;
	int size; //redondant avec positionInList.size()
	*/

	public Graph(boolean isDirected,boolean isVerbose,BufferedOutputStream out) {

		this.isDirected = isDirected;
		this.isVerbose= isVerbose;
		//this.size = 0;
		this.out=out;
		this.isFile=false;
		if(out!=null) {
			this.isFile=true;
		}
		
		/* MODE MAP + LIST
		this.sommets = new ArrayList<>();
		this.positionInList = new HashMap<Integer, Integer>();
		*/
		this.mapSommets = new LinkedHashMap<Integer,Sommet>();

	}

	public void insertSommet(Sommet toAd) {
		/*
		toAd.positionInArray = this.size;
		System.out.println("ON AJOUTE : " + toAd.id);
		this.size++;
		this.sommets.add(toAd);
		this.positionInList.put(toAd.id, this.size - 1);
		*/
		this.mapSommets.put(toAd.id, toAd);

	}

	public void PFS(int idSommetD) throws IOException {

		/*
		 * MODE MAP + LIST int positionD = this.getPositionInList(idSommetD);
		 * if(positionD<0) { System.out.println("ID sommet unknow"); return; }
		 * 
		 * //System.out.println("NOUS COMMENCER EN POSITION : " + positionD +
		 * " pour le sommet de ID : " + idSommetD);
		 * 
		 * int minUnvisited = 0; int minUnvisred_AfterD = positionD + 1; if (positionD
		 * == 0) { minUnvisited = 1; }
		 * 
		 * // Ajouter du sommet D d'une premiere composante connexe Sommet actualSommet
		 * = this.sommets.get(positionD); Sommet
		 * actualSommet=this.mapSommets.get(positionD);
		 */

		Sommet actualSommet = this.mapSommets.get(idSommetD);

		if (actualSommet == null) {
			System.out.println("ID sommet unknow");
			return;
		}

		Queue<Sommet> pile = new LinkedList<>();
		Iterator<Sommet> iter = this.mapSommets.values().iterator();

		pile.add(actualSommet);
		actualSommet.alreadyAddToStackForVisite = true;

		int id_CC_Actual = 0;
		int nb_Sommet_vue = 0;

		int nb_CC = 0;
		boolean in_CC_ofD = true;
		int maxDistance_of_D = 0;
		int id_maxDistance_of_D = idSommetD;
		int nb_Sommet_accessibleFromD = 0;
		int nb_SommetInGraph = this.mapSommets.size();

		/*
		Collection<CC> CC_AlreadySeen=null;
		HashSet<CC> H_CC_AlreadySeen = new HashSet<CC>();
		ArrayList<CC> A_CC_AlreadySeen = new ArrayList<CC>();
		*/
		
		CC actualCC = null;

		while (nb_Sommet_vue < nb_SommetInGraph) {

			nb_CC++;
			id_CC_Actual++;
			
			if (this.isDirected) {
				actualCC = new CC(id_CC_Actual);
				/*
				if(id_CC_Actual>10) {//si il y a deja un nombre important de CC
					H_CC_AlreadySeen.clear();
					CC_AlreadySeen = H_CC_AlreadySeen;
				}else {
					A_CC_AlreadySeen.clear();
					CC_AlreadySeen = A_CC_AlreadySeen;//evite le surcout quand il y a peu de CC
				}
				CC_AlreadySeen.add(actualCC);
				*/
			}
			

			while (!pile.isEmpty()) {

				actualSommet = pile.remove();
				nb_Sommet_vue++;
				/*
				if (isVerbose) {
					System.out.println("\nSommet visiter :" + actualSommet.id);// + " composante associé " +
																		// id_CC_Actual);
				}
				*/
				
				if (this.isDirected) {
					if(actualSommet.id_CC==null) {
						actualSommet.id_CC = actualCC;
						//System.out.println("nouveau "+actualSommet.id +" dans la CC "+actualCC.id);
					}
					
				}

				
				if (isFile) {
					String tmp = actualSommet.id + "\n";
					byte[] tmpB = tmp.getBytes();
					out.write(tmpB, 0, tmpB.length);
				}

				boolean newLevel = true;
				for (Sommet unVoisin : actualSommet.voisins) {
					//System.out.println("\nParcour des voisins de "+actualSommet.id+" voisin : "+unVoisin.id);
					if (this.isDirected) {
						if (unVoisin.id_CC != null) {
							
							if(unVoisin.id_CC!=actualCC) {
								CC unVoisinLastPere = unVoisin.id_CC.getLastPere();
								
								if (unVoisinLastPere == null) {
									// ancienne CC jamais rataché
									//System.out.println("SET-PERE ancien "+unVoisin.id +" MIS dans la CC "+actualCC.id);
									unVoisin.id_CC.setPere(actualCC);
									nb_CC--;
								} else {
									if (unVoisinLastPere != actualCC) {
										// ancienne CC deja rataché a un autre
										unVoisin.id_CC.setPere(actualCC);
										//System.out.println("ON IDENTIFIE UNE ANCIENNE CC pour le sommet " + unVoisin.id);
										nb_CC--;
									}
								}
							}
							// Pour ne faire le travail que une fois par CC croisé durant le parcout de la CC actuel
							//if (!CC_AlreadySeen.contains(unVoisin.id_CC)) {
								//CC_AlreadySeen.add(unVoisin.id_CC);
						} 
						else {
							unVoisin.id_CC = actualCC;
							//System.out.println("nouveau "+unVoisin.id +" dans la CC "+actualCC.id);
						}
					}

					if (!unVoisin.alreadyAddToStackForVisite) {
						if (in_CC_ofD) {
							nb_Sommet_accessibleFromD++;
							if (newLevel) {
								id_maxDistance_of_D = unVoisin.id;
								maxDistance_of_D++;
								newLevel = false;
							}
						}

						pile.add(unVoisin);
						unVoisin.alreadyAddToStackForVisite = true;
					}

				}
			}

			in_CC_ofD = false;
			if (nb_Sommet_vue == nb_SommetInGraph) {
				break;
			}
			do {
				/*
				 * MODE MAP + LIST actualSommet = this.sommets.get(minUnvisited);
				 * minUnvisited++;
				 */
				actualSommet = (Sommet) iter.next();

			} while (actualSommet.alreadyAddToStackForVisite);
			//System.out.println("FIN CC , CHANGEMENT");

			// Ajouter premier sommet d'une autre composante connexe
			// System.out.println("NOUS DEPLACONS EN POSITION : " + minUnvisited + " pour le
			// sommet de ID : " + actualSommet.id);
			pile.add(actualSommet);
			actualSommet.alreadyAddToStackForVisite = true;
		}

		System.out.println("\nnb Sommet accessible from Id " + idSommetD + " : " + nb_Sommet_accessibleFromD);
		System.out.println("nb composantes connex " + nb_CC);
		System.out.println("Sommet le plus eloigné de ID: " + id_maxDistance_of_D + " | Distance: " + maxDistance_of_D);

	}

	public void addArc(int actualId, int actualIdVoisin) {

		Sommet actualSommet = this.getSommet(actualId);

		if (actualSommet == null) {
			actualSommet = new Sommet(actualId);
			this.insertSommet(actualSommet);
		}

		Sommet actualSommetVoisin = this.getSommet(actualIdVoisin);

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

		if(this.mapSommets.containsKey(idToFind)) {
			return this.mapSommets.get(idToFind);
		}
		
		/* MODE MAP + LIST
		if (this.positionInList.containsKey(idToFind)) {
			int position = this.positionInList.get(idToFind);
			return this.sommets.get(position);
		}
		*/

		return null;
	}

	/*
	public int getPositionInList(int idToFind) {
		if (this.positionInList.containsKey(idToFind)) {
			return this.positionInList.get(idToFind);
		}
		return -1;
	}
	*/

}

public class Exo2 {

	public static boolean parseAndFillGraph(Graph graph, BufferedReader file) throws IOException {
		String line = "";
		Long nbLine = 0l;
		String[] arrayOfLine;
		int actualId;
		int actualIdVoisin;

		while (line != null) {
			line = file.readLine();

			nbLine++;
			if (line == null) {
				break;
			}
			
			
			if (line.length()==0 || line.charAt(0) == '#') {
				continue;
			}
			
			arrayOfLine = line.split("\\s");
			if(arrayOfLine.length!=2) {
				System.out.println("ERREUR ligne "+nbLine+" format Invalide");
				return false;
			}

			// System.out.println(arrayOfLine[0]);
			// System.out.println(arrayOfLine[1]);
			try {
			actualId = Integer.parseInt(arrayOfLine[0]);
			actualIdVoisin = Integer.parseInt(arrayOfLine[1]);
			
			graph.addArc(actualId, actualIdVoisin);
			}catch(NumberFormatException e) {
				System.out.println("ERREUR ligne "+nbLine+" format Invalide");
				return false;
			}
			
		}
		
		return true;

	}
	
	/*
	 * Uniquement pour tester le fichier output creer
	 */
	public static void testOutput(BufferedReader br) throws IOException {
		String line="";
		HashSet<Integer> listsommet= new HashSet<Integer>();
		int nbVue=0;
		int a ;
		while (line != null) {
			
			line=br.readLine();
			if(line==null) {
				break;
			}
			a=Integer.parseInt(line);
			nbVue++;
			if(listsommet.contains(a)) {
				System.out.println("ERREUR test output");
				return;
			}else {
				listsommet.add(a);
			}
			
		}
		
		System.out.println("NB sommet vue :"+nbVue);
	}

	public static void main(String[] args) {
        
		if (args.length < 2) {
			System.out.println("il manque arguments");
            System.out.println("Pour exécuter java Exo2 [nom_du_fichier] [sommet_D] [-o] [-v] [-f]");
			return;
		}
		
		BufferedOutputStream out=null;
		try {
            int id = Integer.parseInt(args[1]);
            boolean oriented = false;
            boolean verbose = false;
            boolean file = false;
            if(args.length>2){
                for(int i=2;i<args.length;i++){
                    if(args[i].equals("-o")){
                        oriented=true;
                    }
                    else if(args[i].equals("-v")){
                        verbose=true;
                    }
                    else if(args[i].equals("-f")){
                        file=true;
                    }
                    else{
                        System.out.println("arguments invalide,essayer -o or -v");
                        return;
                    }
                }
            }
            
            if(file) {
            	Path file2 = Paths.get("./"+args[0]+"-output_exo2");
                out = new BufferedOutputStream(Files.newOutputStream(file2, CREATE, TRUNCATE_EXISTING));
            }
            
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			Graph monGraph = new Graph(oriented,verbose,out);

			if(!parseAndFillGraph(monGraph, br)) {
				return;
			}
			br.close();

			System.out.println("FIN LECTURE FICHIER + CREATION GRAPH\nMémoire allouée : " +
			(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + "octets");

			monGraph.PFS(id);
            if(out != null){
                out.flush();
                out.close();
            }

			System.out.println("FIN PARCOUR\nMémoire allouée : " +
			(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + "octets");
			
			/*
			BufferedReader br2 = new BufferedReader(new FileReader("./"+args[0]+"-output_exo2"));
			testOutput(br2);
			*/

		} catch (NumberFormatException e) {
            System.out.println("veuillez entrez un nombre valide");
		} catch (IOException e) {
			System.out.println("ERREUR DE FICHIER - LECTURE OU ECRITURE");
            System.out.println("verifier le nom du fichier d'input");
		}

	}
}
