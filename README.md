M2 INFORMATIQUE PARIS DIDEROT - 2017/2018
AUVERT RAPHAEL

TP avec les 2 extensions

# TP3 Random Graph ErdosReny BarabasiAlbert and associated informations

## Oriented and Non oriented for both algorithms

### 1. PRECISIONS
	
	export produce a file with the stanford format -> https://snap.stanford.edu/data/
	-un sommet pour s'acceder lui meme doit definir ( par exemple pour le sommet 3) : 3 3
	(sinon impossible d'avoir un sommet seul sans voisins avec le format imposé par le sujet !!)

### 2. COMPILATION

	javac *.java


### 3. EXECUTION

	java RandomGraph -e [n] [p] [OutputFileName] [k] [o]  
	java RandomGraph -b [d] [n0] [n] [OutputFileName] [k] [o]
	-e
	[k] is optional , number of repetition to get Min , MAx and Average result
	[o] is optional ( by default non oriented , with o it's oriented graph)

	exemple : e 10 0.5 outFile1 10 o
			  d 3 10 20 outFile2

 

### 4. EXPLICATION

#### 4.1 TIME COMPLEXITY (without analyse)

##### 4.1.1 ErdosReny

		Ө(m')
		-
		n : number of nodes
	  	m' : number of vertex possible ( n² (if oriented)  or  n²/2 (if not oriented) )


##### 4.1.1 BarabasiAlbert

		
		erdos(n0) + p*d*n'
		-
		n : number of nodes
		n' : n - n0
	  	n0 : number of nodes in the initial connected graph
	  	erdos(x) : complexity of erdos

#### 4.2 SPACE COMPLEXITY (without analyse)
	
##### 4.2.1 ErdosReny
	
		O( Array(m) + n')
		-
		m' : number of Edges
		n' : number of VertexAsked
		Array(m) : amorted case -> equal to m


##### 4.2.1 BarabasiAlbert
	
	
		Ө( n0' + (n'*d) ) 
		
		n : number of nodes
		m : number of edges
		d : degree minimum of each node
		n0 : number of nodes in the initial connected graph
		n0' : n0*(n0-1)/2 (not oriented , else wihtout '/2')
		n' : n - n0


#### 4.4 IMPLEMENTATION IN JAVA

nbVertex ,nbEdges and degreeMaximum sont calculé durant la creation du graph

La structure de donnée pour le graph est une liste d'adjacences :

	- le graph est un tableau d'objet Node
	- Chaque objet Node stock une arraylist de ces voisins accessibles


Pour les triangles et V : implementations Parallele grace a l'API Stream

Pour le calcul du nombre de CC : calcul BFS lineaire

Toute les implementation passe a l'echelle , toutes les operations concurrentes : sont atomiques et avec des struck lock-free
et sont les plus optimisé possible pour les cas de contention (voir longAdder)

les versions avec Stream profite du FORK/JOIN et donc le prefimSum est fait en parallele

une pool de struck pour minimusé les besoins en memoire


### 5. REFERENCES
