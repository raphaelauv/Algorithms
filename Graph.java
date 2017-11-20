import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Tuple{
	public Integer distance;
	public Integer nbcc;
	public Tuple(Integer distance, Integer nbcc) {
		this.distance = distance;
		this.nbcc = nbcc;
	}
}

public class Graph {
	
	HashMap<Integer, Node> mapNodes;
	boolean oriented;
	
	public int size() {
		return mapNodes.size();
	}
	
	public Node[] getListeIds() {
		Node [] list = new Node[mapNodes.size()];
		int i=0;
		for(Node t:mapNodes.values()) {
			list[i]=t;
			i++;
		}
		return list;
	}
	
	public Graph(boolean oriented) {
		this.mapNodes = new HashMap<>();
		this.oriented = oriented;
	}
	
	public void addEdge(int actualID, int neighbourID) {
		Node actualNode = this.mapNodes.computeIfAbsent(actualID,k ->new Node(actualID));
		if(actualID!=neighbourID) {
			Node neighbourNode = this.mapNodes.computeIfAbsent(neighbourID,k ->new Node(neighbourID));
			actualNode.insertEdge(neighbourNode);
			if(!oriented) {
				neighbourNode.insertEdge(actualNode);
			}
		}
	}
	
	public Node getNodeFromId(int t) {
		return this.mapNodes.get(t);
	}
}

class Node {
	
	final int id;
	Collection<Node> directNeighbours;
	Map<Node, Tuple> accessibleNeighboursInfo;
		
	public Node(int id) {
		this.id = id;
		this.directNeighbours = new ArrayList<>(); 			//better than linkedList for the parallele version without marqued technique
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
			str+=n.id+" "+tmp.distance+" "+tmp.nbcc+"\n";
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
		return accessibleNeighboursInfo.get(t).nbcc;
	}

}
