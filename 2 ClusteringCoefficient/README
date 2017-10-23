#########################################################################################
#							TP1 ClusteringCoefficient									#
#########################################################################################


M2 INFORMATIQUE PARIS DIDEROT - 2017/2018
AUVERT RAPHAEL

#########################################################################################
#									PRECISIONS											#

	
	CC -> Composante Connexe

	-On ne test pas les doublons dans le fichier d'input
	-un sommet pour s'acceder lui meme doit definir ( par exemple pour le sommet 3) :
		3 3
	(sinon impossible d'avoir un sommet seul sans voisins avec le format imposé par le sujet !!)


#########################################################################################
#									COMPILATION											#
	javac *.java

#########################################################################################
#									EXECUTION											#


	java GlobalClusteringCoefficient [FileName]

	java AverageClusteringCoefficient [FileName]


#########################################################################################
#									EXPLICATION 										#


#########################################
#			TIME COMPLEXITY				#

	Worst case :
		

	  	m : number of edges
	  	n : number of nodes
	  	w(n) complexité de UNION-FIND optimal
	 
	Dans le cas Amortie :
		


#########################################
#			SPACE COMPLEXITY			#


	
	



#########################################
#			IMPLEMENTATION				#

une implemntation JAVA est proposé :
	Un seul et unique parcout du fichier 
	

Une implementation la plus legere possible en memoire est proposé avec comme contraine :

	- Nous l'avons adapté pour repondre au pire cas ( quand il y a enormement de trous d'id dans le graph avec un idMax trés grand ,
		afin de ne pas avoir une complexcité en memoire qui puisse explosé )


La structure de donnée est uniquement une :

	LinkedHasMap de (clef,valeur) -> (idSommet , Sommet)
		Nous avons un Ierateur en O(1) pour l'operation Next


Chaque sommet stock un linkedHashSet de ces voisins accessibles 


#########################################
#		ALGORITHME 						#




#########################################################################################

