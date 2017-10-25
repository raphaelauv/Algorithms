M2 INFORMATIQUE PARIS DIDEROT - 2017/2018
AUVERT RAPHAEL

# TP2 Clustering Coefficient (Global and Local) , Diameter and Average path length (APL) with All Pairs Shortest Path (APSP)

## for the two ClusteringCoefficient : Basic-MT-Skip

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

	java AverageClusteringCoefficient [FileName]

	java Diameter_APL_Graph [FileName]

### 4. EXPLICATION



#### 4.1 TIME COMPLEXITY

##### 4.1.1 Clustering (Global and Local)

	
	The complexity is the same for the both algorithms , only 2* nb(tri) will be calculated
	with p processors
	Worst case (Complete graph) :
		O(  ( m^(3/2) )/p  )
		-
	  	m : number of edges

##### 4.1.1 Diameter and APL

	The complexity is the same for the both algorithms and can be calculated at the same time
	with p processors
	Worst case (Complete graph) :
		O( (m*n) /p )
		-
	  	m : number of edges
	  	n : number of nodes

#### 4.2 SPACE COMPLEXITY
	
##### 4.2.1 Clustering (Global and Local)
	
	with p processors
	Worst case (Complete graph) :
		hs(n) + hs(2*m)
		-
		hs : HashSize coeff multiplicator , maximum 2


##### 4.2.1 Diameter and APL
	
	with p processors
	Worst case (Complete graph) :
		hs(n) + hs(2*m) + p*hs(m)
		-
		hs : HashSize coeff multiplicator , maximum 2


#### 4.3 IMPLEMENTATION IN JAVA

Toute les implementation passe a l'echelle , toutes les operations concurrentes sont atomiques et avec des struck lock-free
et sont les plus optimisé pour les cas de contention.

Un seul et unique parcout du fichier
Une implementation la plus legere possible en memoire est proposé avec comme contraine :
Nous l'avons adapté pour repondre au pire cas ( quand il y a enormement de trous d'id dans le graph avec un idMax trés grand , afin de ne pas avoir une complexcité en memoire qui puisse explosé )

une pool de job pour maximisé la parallelisation
une pool de struck pour minimusé les besoins en memoire

La structure de donnée est une :

	- LinkedHasMap de (clef,valeur) -> (idSommet , Sommet)
	- Nous avons un Ierateur en O(1) pour l'operation Next
	- Chaque sommet stock un linkedHashSet de ces voisins accessibles


#### 4.4 ALGORITHME

##### 4.4.1 Clustering (Global and Local)


##### 4.4.1 Diameter and APL


### 5. REFERENCES
**Tim Roughgarden† - CS167: Reading in Algorithms Counting Triangles**
Department of Computer Science, Stanford University, 462 Gates Building, 353 Serra Mall, Stanford - March 31, 2014
***
**Coen Boot and Prof. Dr. R.H. Bisseling - Algorithms for Determining the Clustering Coefficient in Large Graphs**
Bachelor Thesis - Utrecht University - Faculty of Science Department of Mathematics - January 25, 2016