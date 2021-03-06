import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

class Graph {
	
	Node[] nodes;
	int size;
	boolean oriented;
	int nbEdges;
	public Graph(boolean oriented ,int fixedSize) {
		this.nodes = new Node[fixedSize];
		this.oriented= oriented;
	}
	
	public void addEdgeModeArray(int actualID, int neighbourID) {
		
		Node actualNode = this.nodes[actualID];
		if(actualNode==null) {
			actualNode = new Node(actualID);
			this.nodes[actualID]= actualNode;
			size++;
		}
		
		Node neighbourNode = this.nodes[neighbourID];
		if(neighbourNode==null) {
			neighbourNode = new Node(neighbourID);
			this.nodes[neighbourID] = neighbourNode;
			size++;
		}
		
		actualNode.insertEdge(neighbourNode);
		nbEdges++;
		if(!this.oriented) {
			neighbourNode.insertEdge(actualNode);	
		}
	}
	
	/*
	public void addEdge(int actualID, int neighbourID,boolean oriented) {
		Node actualNode = this.nodes.computeIfAbsent(actualID,k ->new Node(actualID));
		if(actualID!=neighbourID) {
			Node neighbourNode = this.nodes.computeIfAbsent(neighbourID,k ->new Node(neighbourID));
			actualNode.insertEdge(neighbourNode);
			if(!oriented) {
				neighbourNode.insertEdge(actualNode);	
			}
		}
	}
	*/
}

class Node {
	
	private CC cc;
	boolean alreadyAddToStackForVisite;						//only for CC

	final int id;
	Collection<Node> neighbours;
	
	private LongAdder nbInsideTriangle;						//only for AverageClusteringCoefficient
	
	public Node(int id) {
		this.id = id;
		this.neighbours = new ArrayList<>();
		this.nbInsideTriangle = new LongAdder();			//only for Average
	}

	public synchronized CC setAndGetCC(CC ccArg) {
		if (this.cc == null) {
			this.cc = ccArg;
		}
		return this.cc;
	}
	
	public void setCC(CC ccArg) {
		this.cc = ccArg;
	}
	
	public CC getCC() {
		return this.cc;
	}
	
	public void insertEdge(Node neighbour) {
		this.neighbours.add(neighbour);
	}
	
	/*
	 * only for AverageClusteringCoefficient
	 */
	public static void incrementNbTriangle(Node node0, Node node1, Node node2) {
		node0.nbInsideTriangle.increment();
		node1.nbInsideTriangle.increment();
		node2.nbInsideTriangle.increment();
	}
	public int getNbTriangle() {
		return ((int) (this.nbInsideTriangle.sum())) / 2;
	}


	public void resetNbTriangle() {
		this.nbInsideTriangle.reset();
	}

}


class ResultGloalAndLocal{
	int nbV;
	int nbTri;
	double cluG;
	double cluL;
	public ResultGloalAndLocal(ResulGlobal rst,double cluL) {
		if(rst != null) {
			this.nbV=rst.nbV;
			this.nbTri = rst.nbTri;
			this.cluG =  (3 * nbTri) /(double)nbV;
			this.cluL = cluL;
			
			if(Double.isNaN(cluG)) {
				cluG=0;
			}
		}
		
	}
	
	@Override
	public String toString() {
		return "nb Tri : "+nbTri+" \nnbV : "+nbV+" \nCLU_G : "+cluG + " \nCLU_L "+cluL;
	}
}

class ResulGlobal {
	int nbV;
	int nbTri;
	public ResulGlobal(int nbV, int nbTri) {
		this.nbV = nbV;
		this.nbTri = nbTri;
	}
}

class sumResultGlobal implements BinaryOperator<ResulGlobal>{

	@Override
	public ResulGlobal apply(ResulGlobal t, ResulGlobal u) {
		t.nbTri+=u.nbTri;
		t.nbV+=u.nbV;
		return t;
	}
}


class Let_FindNbTriangles implements Function<Node,ResulGlobal> {
	
	public Let_FindNbTriangles() {}
	
	boolean isAverageClusterMode;
	public Let_FindNbTriangles(boolean isAverageClusterMode) {
		this.isAverageClusterMode=isAverageClusterMode;
	}
	
	@Override
	public ResulGlobal apply(Node node) {
		
		int nbTri = 0;
		int myDegree = node.neighbours.size();
		if(myDegree<2) {
			return new ResulGlobal(0, 0);
		}
		
		int neighbourDegree;
		for (Node aNeighbour : node.neighbours) {
			neighbourDegree=aNeighbour.neighbours.size();
			
			if(neighbourDegree>myDegree) {
				continue;
			}else if(neighbourDegree==myDegree) {
				if(aNeighbour.id>node.id) {
					continue;
				}
			}
			for (Node aNN : aNeighbour.neighbours) {
				if(aNN.id==node.id) { 
					continue;//do not look for myself
				}
				
				neighbourDegree=aNN.neighbours.size();
				
				if(neighbourDegree>myDegree) {
					continue;
				}else if(neighbourDegree==myDegree) {
					if(aNN.id>node.id) {
						continue;
					}
				}
				if(node.neighbours.contains(aNN)) {
					nbTri++;
					if(isAverageClusterMode) {
						Node.incrementNbTriangle(node, aNeighbour, aNN);
					}
				}
			}
		}
		
		nbTri /=2;//because we count each triangle in the two ways possible
		int nbV = (myDegree * (myDegree - 1) / 2);
		return new ResulGlobal(nbV,nbTri);
		
	}
}


class FindNbTriangles implements Callable<Integer> {

	private Node node;
	boolean isAverageClusterMode;
	
	public FindNbTriangles(Node node,boolean isAverageClusterMode) {
		this.node = node;
		this.isAverageClusterMode=isAverageClusterMode;
	}
	
	@Override
	public Integer call() throws Exception {		
		return new Let_FindNbTriangles(isAverageClusterMode).apply(node).nbTri;
	}
}


public class ClusteringCoefficient {

	public static ResultGloalAndLocal globalAndLocal(Graph myGraph) {
		
		Stream<Node> streamOfNodes = Stream.of(myGraph.nodes).parallel().filter(Objects::nonNull);
		
		Stream<ResulGlobal> streamOfResults = streamOfNodes.map(new Let_FindNbTriangles(true));
		Optional<ResulGlobal> rst=streamOfResults.reduce(new sumResultGlobal());

		int nbTri_X = 0;
		int degree_X = 0;
		double sum_cluL_X = 0;
		
		int nbNodesInGraph = myGraph.size;
		
		for(Node actualNode : myGraph.nodes) {
			
			if(actualNode==null) {
				continue;
			}
			
			nbTri_X = actualNode.getNbTriangle() ;
			actualNode.resetNbTriangle(); 				//TODO necessary ??
			degree_X = actualNode.neighbours.size();
			if (degree_X < 2) {
				continue;
			}
			sum_cluL_X += (2 * nbTri_X) / (double) (degree_X * (degree_X - 1));
			
		}
		double oneOnN = 1 / (double) (nbNodesInGraph);
		double cluL_G = oneOnN * sum_cluL_X;
		if(Double.isNaN(cluL_G)) {
			cluL_G = 0;
		}
		if(rst.isPresent()) {
			return new ResultGloalAndLocal(rst.get(), cluL_G);
		}else {
			return new ResultGloalAndLocal(new ResulGlobal(0, 0), cluL_G);
		}
		
	}
}