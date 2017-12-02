import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Graph {

	private Map<Integer, Node> mapNodes;
	final boolean oriented;
	int nbEdges;
	
	private ArrayList<Node> listNodes;			//filed at the same time than the map , avoid to iterate later the map
	
	public Graph(boolean oriented) {
		this.mapNodes = new HashMap<>();
		this.listNodes = new ArrayList<>();
		this.oriented = oriented;
	}
	public ArrayList<Node> getListeNodes() {
		return listNodes;
	}
	public void addEdge(int actualID, int neighbourID) {
		Node actualNode = this.mapNodes.computeIfAbsent(actualID,k ->{
			listNodes.add(new Node(actualID));return listNodes.get(listNodes.size()-1);
		});
		if(actualID!=neighbourID) {
			Node neighbourNode = this.mapNodes.computeIfAbsent(neighbourID,k ->{
				listNodes.add(new Node(neighbourID));return listNodes.get(listNodes.size()-1);
			});
			actualNode.insertEdge(neighbourNode);
			nbEdges++;
			if(!oriented) {
				neighbourNode.insertEdge(actualNode);
				nbEdges++;
			}
		}
	}
}

class Tuple{
	public Integer distance;
	public Integer nbpcc;
	public Tuple(Integer distance, Integer nbpcc) {
		this.distance = distance;
		this.nbpcc = nbpcc;
	}
}

class Node {
	
	final int id;
	Collection<Node> directNeighbours;
	private Map<Node, Tuple> accessibleNeighboursInfo;
		
	public Node(int id) {
		this.id = id;
		this.directNeighbours = new ArrayList<>(); 	//better than linkedList for the parallele version without marqued technique
		this.accessibleNeighboursInfo = new ConcurrentHashMap<>();
	}
	
	/*
	 * only for test
	 */
	public void printfAllmyNeighboursInfo() {
		String str = id+"-----------\n";
		Tuple tmp;
		for(Node  n:accessibleNeighboursInfo.keySet() ) {
			tmp = accessibleNeighboursInfo.get(n);
			str+=n.id+" "+tmp.distance+" "+tmp.nbpcc+"\n";
		}		
		System.out.println(str);
	}

	public void insertEdge(Node neighbour) {
		this.directNeighbours.add(neighbour);
	}

	public void insert(Node aNeighbour, Tuple tpl) {
		accessibleNeighboursInfo.put(aNeighbour, tpl);
	}

	public Tuple getTuple(Node aNeighbour) {
		return accessibleNeighboursInfo.get(aNeighbour);
	}

	public Integer getDistanceOf(Node aNeighbour) {
		return accessibleNeighboursInfo.get(aNeighbour).distance;
	}

	public int getNbpccOf(Node t) {
		return accessibleNeighboursInfo.get(t).nbpcc;
	}
}