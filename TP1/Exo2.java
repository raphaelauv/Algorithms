import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

class CC{
	int id;
	private CC father;
	
	CC(int id){
		this.id=id;
		this.father=null;
	}
	
	void setPere(CC father) {
		this.father=father;
	}
	
	CC getLastPere() {
		if(father==null) {return null;}
		CC tmp=father;
		
		while(tmp.father!=null) {
			tmp=tmp.father;
			this.father = tmp; //compression du chemin
		}
		return tmp;
	}
}

class Node {

	int id;
	int distance;
	CC id_CC;
	boolean alreadyAddToStackForVisite;
	LinkedList<Node> neighbours;

	public Node(int id) {
		this.alreadyAddToStackForVisite = false;
		this.neighbours = new LinkedList<>();
		this.id = id;
	}

	public void insertEdge(Node neighbour) {
		this.neighbours.add(neighbour);
	}

}

class Graph {

	boolean isDirected;
	boolean isVerbose;
	boolean isFile;
	BufferedOutputStream out;
	LinkedHashMap<Integer, Node> mapNodes;

	public Graph(boolean isDirected,boolean isVerbose,BufferedOutputStream out) {

		this.isDirected = isDirected;
		this.isVerbose= isVerbose;
		this.out=out;
		this.isFile=false;
		if(out!=null) {
			this.isFile=true;
		}
		this.mapNodes = new LinkedHashMap<Integer,Node>();
	}

	public void insertNode(Node toAd) {
		this.mapNodes.put(toAd.id, toAd);
	}

	public void PFS(int idSommetD) throws IOException {

		Node actualNode = this.mapNodes.get(idSommetD);
		if (actualNode == null) {
			System.out.println("ID sommet unknow");
			return;
		}

		int id_CC_Actual = 0;
		int nb_Nodes_seen = 0;

		int nb_CC = 0;
		boolean in_CC_ofD = true;
		int maxDistance_of_D = 0;
		int id_maxDistance_of_D = idSommetD;
		int nb_Nodes_accessibleFromD = 0;
		int nb_NodesInGraph = this.mapNodes.size();
		//boolean firstVoisin = true;

		CC actualCC = null;
		
		Queue<Node> stack = new LinkedList<>();
		Iterator<Node> iter = this.mapNodes.values().iterator();

		stack.add(actualNode);
		actualNode.alreadyAddToStackForVisite = true;
		actualNode.distance=maxDistance_of_D;

		while (nb_Nodes_seen < nb_NodesInGraph) {
			
			
			nb_CC++;
			id_CC_Actual++;
			
			if (this.isDirected) {
				actualCC = new CC(id_CC_Actual);
				actualNode.id_CC = actualCC;
				//System.out.println("nouveau "+actualSommet.id +" dans la CC "+actualCC.id);
			}
			
			while (!stack.isEmpty()) {

				actualNode = stack.poll();
				nb_Nodes_seen++;
				
				if (isVerbose) {
					System.out.println("\nSommet visiter :" + actualNode.id);
				}
				
				if (isFile) {
					String tmp = actualNode.id + "\n";
					byte[] tmpB = tmp.getBytes();
					out.write(tmpB, 0, tmpB.length);
				}

				//firstVoisin = true;
				if(in_CC_ofD) {
					if(actualNode.distance>maxDistance_of_D) {
						maxDistance_of_D++;
					}
				}
				for (Node aNeighbour : actualNode.neighbours) {
					//System.out.println("\nParcour des voisins de "+actualSommet.id+" voisin : "+unVoisin.id);
					if (this.isDirected) {
						if (aNeighbour.id_CC != null) {
							
							if(aNeighbour.id_CC.id!=actualCC.id) {
								CC aNeighbourLastFather = aNeighbour.id_CC.getLastPere();
								
								if (aNeighbourLastFather == null) {
									// ancienne CC jamais rataché
									//System.out.println("SET-PERE ancien "+unVoisin.id +" MIS dans la CC "+actualCC.id);
									aNeighbour.id_CC.setPere(actualCC);
									nb_CC--;
								} else if (aNeighbourLastFather.id != actualCC.id) {
									// ancienne CC deja rataché a un autre
									aNeighbourLastFather.setPere(actualCC);
									//System.out.println("ON IDENTIFIE UNE ANCIENNE CC pour le sommet " + unVoisin.id);
									nb_CC--;
								}
							}
						}
						else {
							aNeighbour.id_CC = actualCC;
							//System.out.println("nouveau "+unVoisin.id +" dans la CC "+actualCC.id);
						}
					}

					if (!aNeighbour.alreadyAddToStackForVisite) {
						
						if (in_CC_ofD) {
							aNeighbour.distance=maxDistance_of_D+1;
							nb_Nodes_accessibleFromD++;
							//if (firstVoisin) {
								id_maxDistance_of_D = aNeighbour.id;
								//firstVoisin = false;
							//}
						}

						stack.add(aNeighbour);
						aNeighbour.alreadyAddToStackForVisite = true;
					}

				}
			}

			in_CC_ofD = false;
			if (nb_Nodes_seen == nb_NodesInGraph) {
				break;
			}
			do {
				actualNode = (Node) iter.next();

			} while (actualNode.alreadyAddToStackForVisite);
			//System.out.println("FIN CC , CHANGEMENT");

			// Ajouter premier sommet d'une autre composante connexe
			stack.add(actualNode);
			actualNode.alreadyAddToStackForVisite = true;
		}

		System.out.println("\nnb Sommet accessible from Id " + idSommetD + " : " + nb_Nodes_accessibleFromD);
		System.out.println("nb composantes connex : " + nb_CC);
		System.out.println("Sommet le plus eloigné de "+idSommetD+" est le sommet ID : " + id_maxDistance_of_D + " | Distance: " + maxDistance_of_D);

	}

	public void addEdge(int actualId, int actualIdNeighbour) {

		Node actualNode = this.getSommet(actualId);

		if (actualNode == null) {
			actualNode = new Node(actualId);
			this.insertNode(actualNode);
		}

		if(actualId!=actualIdNeighbour) {
			Node actualNodeNeighbour = this.getSommet(actualIdNeighbour);

			if (actualNodeNeighbour == null) {
				actualNodeNeighbour = new Node(actualIdNeighbour);
				this.insertNode(actualNodeNeighbour);
			}

			actualNode.insertEdge(actualNodeNeighbour);

			if (!this.isDirected) {
				actualNodeNeighbour.insertEdge(actualNode);
			}
		}
	}

	public Node getSommet(int idToFind) {
		if (this.mapNodes.containsKey(idToFind)) {
			return this.mapNodes.get(idToFind);
		}
		return null;
	}
}

public class Exo2 {

	public static boolean parseAndFillGraph(Graph graph, BufferedReader file) throws IOException {
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

			// System.out.println(arrayOfLine[0] + arrayOfLine[1]);
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
                        System.out.println("arguments invalide,essayer -o -v -f");
                        return;
                    }
                }
            }
            
            if(file) {
            	Path file2 = Paths.get("./"+args[0]+"-output_exo2");
                out = new BufferedOutputStream(Files.newOutputStream(file2, CREATE, TRUNCATE_EXISTING));
            }
            
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			Graph myGraph = new Graph(oriented,verbose,out);

			if(!parseAndFillGraph(myGraph, br)) {
				return;
			}
			br.close();

			System.out.println("FIN LECTURE FICHIER + CREATION GRAPH\nMémoire allouée : " +
			(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + "octets");

			myGraph.PFS(id);
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
