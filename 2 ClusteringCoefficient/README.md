# TP2 Clustering Coefficient (Global and Local) , Diameter and Average path length (APL) with All Pairs Shortest Path (APSP)

## for Global and Local ClusteringCoefficient : Basic-MT-Skip

> MULTITHREADING (parallel) version : Enumerating over Neighbor Pairs WITH Delegating Low-Degree Vertices AND Skipping Vertices with d(v) < 2

## for the diameter and Average path length : BFS/APL-MT

> MULTITHREADING (parallel) version : Calculate BFS on all Nodes

### 1. PRECISIONS
	
	DATA FROM -> https://snap.stanford.edu/data/
	-un sommet pour s'acceder lui meme doit definir ( par exemple pour le sommet 3) : 3 3
	(sinon impossible d'avoir un sommet seul sans voisins avec le format imposé par le sujet !!)

### 2. COMPILATION

	javac *.java


### 3. EXECUTION

	java GlobalClusteringCoefficient [FileName]

	java LocalClusteringCoefficient [FileName]

	java Diameter_APL_Graph [FileName]

### 4. EXPLICATION



#### 4.1 TIME COMPLEXITY

##### 4.1.1 Clustering (Global and Local)

	
	The complexity is the same for the both algorithms , only 2 * nb(tri) will be calculated.
	with p processors
	Worst case (Complete graph) :
		O(  ( m^(3/2) )/p  ) + Prefix_sum
		-
	  	m : number of edges
	  	Prefix_sum : impossible for Average so it cost n , for Global it cost logp(n)
	  	-
	  	Global : We look for m^(3/2) neighour on each node with p processors
	  	Local : We look for m^(3/2) neighour on each node with p processors and update a concurrent variable on each node of the number of triangles to which it belongs

##### 4.1.1 Diameter and APL

	The complexity and algorithm is the same for the both so they can be calculated at the same time.
	with p processors
	Worst case (Complete graph) :
		O( (m*n) /p ) + Prefix_sum
		-
	  	m : number of edges
	  	n : number of nodes
	  	Prefix_sum : it cost logp(n)
	  	-
	  	We do a BFS on each node with p processors

#### 4.2 SPACE COMPLEXITY
	
##### 4.2.1 Clustering (Global and Local)
	
	with p processors
	Worst case (Complete graph) :
		hs(n) * hs(m)
		-
		n : number of nodes
		m : number of edges
		hs : HashSize coeff multiplicator , maximum 2
		-
		We have an hashMap of all the Nodes , each node have an hashSet of all neighbours


##### 4.2.1 Diameter and APL
	
	with p processors
	Worst case (Complete graph) :
		(hs(n) * hs(m)) + p*hs(m)
		-
		n : number of nodes
		m : number of edges
		hs : HashSize coeff multiplicator , maximum 2
		-
		We have an hashMap of all the Nodes , each node have an hashSet of all neighbours and we have p hashmap of size max : m


#### 4.4 IMPLEMENTATION IN JAVA

2 implementations Parallele :
	Executor
	Stream

Toute les implementation passe a l'echelle , toutes les operations concurrentes : sont atomiques et avec des struck lock-free
et sont les plus optimisé possible pour les cas de contention (voir longAdder)

les versions avec Executor ne profite pas du FORK/JOIN et donc le prefimSum est fait en serie
les versions avec Stream profite du FORK/JOIN et donc le prefimSum est fait en parallele

Un seul et unique parcout du fichier
Une implementation la plus legere possible en memoire est proposé avec comme contraine :
Nous l'avons adapté pour repondre au pire cas ( quand il y a enormement de trous d'id dans le graph avec un idMax trés grand , afin de ne pas avoir une complexcité en memoire qui puisse explosé )

une pool de job pour maximisé la parallelisation
une pool de struck pour minimusé les besoins en memoire

La structure de donnée est une :

	- LinkedHasMap de (clef,valeur) -> (idSommet , Sommet)
	- Nous avons un Ierateur en O(1) pour l'operation Next
	- Chaque sommet stock un linkedHashSet de ces voisins accessibles



### 5. REFERENCES
**Tim Roughgarden† - CS167: Reading in Algorithms Counting Triangles**
Department of Computer Science, Stanford University, 462 Gates Building, 353 Serra Mall, Stanford - March 31, 2014
***
**Coen Boot and Prof. Dr. R.H. Bisseling - Algorithms for Determining the Clustering Coefficient in Large Graphs**
Bachelor Thesis - Utrecht University - Faculty of Science Department of Mathematics - January 25, 2016