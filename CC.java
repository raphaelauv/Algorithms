import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/*
 * Not use , it's only necessary if we parallelize the CC counter
 */
class forestCC {

	Collection<CC> list;

	public forestCC() {
		this.list = new ArrayList<>();
	}

	public synchronized void addCC(CC toAd) {
		this.list.add(toAd);
	}

	public synchronized int getNbCC() {
		ArrayList<CC> fatherList = new ArrayList<>();
		CC actualfather;

		for (CC child : list) {
			actualfather = child.getLastPere();
			if (actualfather == null) {
				actualfather = child;
			}
			if (!fatherList.contains(actualfather)) {
				fatherList.add(actualfather);
			}
		}
		return fatherList.size();
	}
}

class CC {
	int id;
	private CC father;

	CC(int id) {
		this.id = id;
		this.father = null;
	}

	void setPere(CC father) {
		this.father = father;
	}

	CC getLastPere() {
		if (father == null) {
			return null;
		}
		CC tmp = father;

		while (tmp.father != null) {
			tmp = tmp.father;
			this.father = tmp; // compression du chemin
		}
		return tmp;
	}
}

class Let_BFS_CC {

	public static int nbCCinGraph(Graph graph) {

		int id_CC_Actual = 0;
		int nb_Nodes_seen = 0;

		int nb_CC = 0;
		int nb_NodesInGraph = graph.nodes.length; //graph.nodes.size();

		CC actualCC = null;

		Queue<Node> stack = new LinkedList<>();
		//Iterator<Node> iter = graph.nodes.values().iterator();

		int index=0;
		
		Node actualNode = graph.nodes[index]; //iter.next();

		stack.add(actualNode);
		actualNode.alreadyAddToStackForVisite = true;

		while (nb_Nodes_seen < nb_NodesInGraph) {

			nb_CC++;
			id_CC_Actual++;

			if (graph.oriented) {
				actualCC = new CC(id_CC_Actual);
				actualNode.setAndGetCC(actualCC);

			}

			while (!stack.isEmpty()) {

				actualNode = stack.poll();
				nb_Nodes_seen++;

				for (Node aNeighbour : actualNode.neighbours) {
					// System.out.println("\nParcour des voisins de "+actualSommet.id+" voisin :
					// "+unVoisin.id);
					if (graph.oriented) {
						if (aNeighbour.getCC() != null) {

							if (aNeighbour.getCC().id != actualCC.id) {
								CC aNeighbourLastFather = aNeighbour.getCC().getLastPere();

								if (aNeighbourLastFather == null) {
									// ancienne CC jamais rataché
									// System.out.println("SET-PERE ancien "+unVoisin.id +" MIS dans la CC
									// "+actualCC.id);
									aNeighbour.getCC().setPere(actualCC);
									nb_CC--;
								} else if (aNeighbourLastFather.id != actualCC.id) {
									// ancienne CC deja rataché a un autre
									aNeighbourLastFather.setPere(actualCC);
									// System.out.println("ON IDENTIFIE UNE ANCIENNE CC pour le sommet " +
									// unVoisin.id);
									nb_CC--;
								}
							}
						} else {
							aNeighbour.setAndGetCC(actualCC);

						}
					}

					if (!aNeighbour.alreadyAddToStackForVisite) {

						stack.add(aNeighbour);
						aNeighbour.alreadyAddToStackForVisite = true;
					}

				}
			}

			if (nb_Nodes_seen == nb_NodesInGraph) {
				break;
			}
			do {
				index++;
				actualNode = graph.nodes[index]; //(Node) iter.next();

			} while (actualNode.alreadyAddToStackForVisite);
			// System.out.println("FIN CC , CHANGEMENT");

			// Ajouter premier sommet d'une autre composante connexe
			stack.add(actualNode);
			actualNode.alreadyAddToStackForVisite = true;
		}
		return nb_CC;
	}
}