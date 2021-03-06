# Betweenness Centrality - trivial Parallel version (MULTITHREADING)

## Oriented and Non oriented

### 1. PRECISIONS
	
	export produce a file with the stanford format -> https://snap.stanford.edu/data/
	-un sommet pour s'acceder lui meme doit definir ( par exemple pour le sommet 3) : 3 3
	(sinon impossible d'avoir un sommet seul sans voisins avec le format imposé par le sujet !!)

### 2. COMPILATION

	javac *.java


### 3. EXECUTION

	java BetweennessCentrality [InputFileName] [o]
	[o] is optional ( by default non oriented , with o it's oriented graph)
 

### 4. EXPLICATION


	In practice the floydWarshall implementation is faster (speedUp x2) because it only use arrays and do not suffer of cache miss


#### 4.1 TIME COMPLEXITY

##### 4.1.1 N-BFS + N-BET

		Worst case (Complete graph) : with p processors
		O( (n*m)/p + (n³/p) ) => 0(n³/p)
		-
	  	m : number of edges
	  	n : number of nodes


#### 4.2 SPACE COMPLEXITY
	
##### 4.2.1 N-BFS + N-BET
	
		Worst case (Complete graph) :
		O(n²)
		-
	  	n : number of nodes


#### 4.4 IMPLEMENTATION IN JAVA

	
	- The graph is an hashMap of id and Node
	- Each Node keep an arraylist of neighbours and an hashmap of accesibleNodes and a tuple of distance and number of Shortest paths
	- We parallelize thank to the API stream
	- A pool of ArrayDeque for the N-BFS ,to minimize the need of memory


### 5. REFERENCES

**Mirlayne Campuzano-Alvarez and Adrian Fonseca-Bruzón**
Distributed and Parallel Algorithm for Computing Betweenness Centrality - chapter 24 - Advances in Artificial Intelligence - IBERAMIA 2016
***
**Ulrik Brandes**
A Faster Algorithm for Betweenness Centrality - University of Konstanz