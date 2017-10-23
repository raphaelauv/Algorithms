# TP2 Clustering Coefficient (Global and Local)

Basic-MT-Skip

## MULTITHREADING Enumerating over Neighbor Pairs WITH Delegating Low-Degree Vertices AND Skipping Vertices with d(v) < 2

M2 INFORMATIQUE PARIS DIDEROT - 2017/2018
AUVERT RAPHAEL


### PRECISIONS

	CC -> Composante Connexe

	-On ne test pas les doublons dans le fichier d'input
	-un sommet pour s'acceder lui meme doit definir ( par exemple pour le sommet 3) :
		3 3
	(sinon impossible d'avoir un sommet seul sans voisins avec le format imposé par le sujet !!)


### COMPILATION

	javac *.java


### EXECUTION

	java GlobalClusteringCoefficient [FileName]

	java AverageClusteringCoefficient [FileName]



### EXPLICATION



#### TIME COMPLEXITY

	Worst case :
		
	  	m : number of edges
	  	n : number of nodes
	  	w(n) complexité de UNION-FIND optimal
	 
	Dans le cas Amortie :
		


#### SPACE COMPLEXITY
	
	



#### IMPLEMENTATION

une implemntation JAVA est proposé :
	Un seul et unique parcout du fichier 
	

Une implementation la plus legere possible en memoire est proposé avec comme contraine :

	- Nous l'avons adapté pour repondre au pire cas ( quand il y a enormement de trous d'id dans le graph avec un idMax trés grand ,
		afin de ne pas avoir une complexcité en memoire qui puisse explosé )


La structure de donnée est uniquement une :

	LinkedHasMap de (clef,valeur) -> (idSommet , Sommet)
		Nous avons un Ierateur en O(1) pour l'operation Next


Chaque sommet stock un linkedHashSet de ces voisins accessibles 


#### ALGORITHME


### REFERENCES

####Tim Roughgarden† - CS167: Reading in Algorithms Counting Triangles
Department of Computer Science, Stanford University, 462 Gates Building, 353 Serra Mall, Stanford - March 31, 2014

#### Coen Boot and Prof. Dr. R.H. Bisseling - Algorithms for Determining the Clustering Coefficient in Large Graphs
Bachelor Thesis - Utrecht University - Faculty of Science Department of Mathematics - January 25, 2016





