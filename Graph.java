import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.sql.rowset.spi.SyncResolver;


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
			neighbourNode.insertEdge(actualNode);
		}
	}
	
	public Node getNodeFromId(int t) {
		return this.mapNodes.get(t);
	}
}

class Node {
	
	final int id;
	Collection<Node> neighbours;
	HashMap<Node, Tuple> neighboursInfo;
		
	public Node(int id) {
		this.id = id;
		this.neighbours = new ArrayList<>(); 			//better than linkedList for the parallele version without marqued technique
		this.neighboursInfo = new HashMap<>();
	}
	
	/*
	 * only for test
	 */
	public void printfAllmyNeighboursInfo() {
		String str = id+"-----------\n";
		Tuple tmp;
		for(Node  n:neighboursInfo.keySet() ) {
			tmp = neighboursInfo.get(n);
			str+=n.id+" "+tmp.distance+" "+tmp.nbcc+"\n";
		}
		
		System.out.println(str);
	}

	public void insertEdge(Node neighbour) {
		this.neighbours.add(neighbour);
	}
	
	public synchronized void insert(Node aNeighbour,Tuple tpl) {
		neighboursInfo.put(aNeighbour, tpl);
	}
	
	public synchronized Tuple getTuple(Node aNeighbour) {
		return neighboursInfo.get(aNeighbour); 
	}
	
	public synchronized Integer getDistanceOf(Node aNeighbour) {
		return neighboursInfo.get(aNeighbour).distance;
	}
	
	public synchronized int getNbpccOf(Node t) {
		return neighboursInfo.get(t).nbcc; 
	}

}
