# TP1 Large Graph Informations

### PRECISIONS

	CC -> Composante Connexe

	-On ne test pas les doublons dans le fichier d'input
	-un sommet pour s'acceder lui meme doit definir ( par exemple pour le sommet 3) :
		3 3
	(sinon impossible d'avoir un sommet seul sans voisins avec le format imposé par le sujet !!)

WIkipedia : (https://fr.wikipedia.org/wiki/Graphe_connexe)

	Pour un graphe orienté, on parle de connexité si en oubliant l'orientation des arêtes, le graphe est connexe
	Donc nous considererons que :
	si il existe un chemin depuis U a V et un chemin depuis K a V alors K et U sont dans la meme CC



### COMPILATION

	javac *.java


### EXECUTION

	java GraphInformations [FileName] [-o]
		-o: for an oriented graph

	java GraphBFS [FileName] [sommet_D] [-o] [-v] [-f]
		-o: for an oriented graph
		-f: pour un fichier de sortie avec la liste des sommes dans l'ordre du parcours en largeur depuis le sommet D
		-v: verbose mode



### EXPLICATION Exo2



#### TIME COMPLEXITY

	Worst case :
		ou la hashmap est lourdement rempli et tres peu optimal

		O(m * log(n) * w(n) ) 

	  	m : number of edges
	  	n : number of nodes
	  	w(n) complexité de UNION-FIND optimal
	 
	Dans le cas Amortie :
		0( m * w(n) )



#### SPACE COMPLEXITY


	O(  m + k + p)
	
	m : number of edges
	k : number of CC
	p : stack ( maximum value = n )




#### IMPLEMENTATION

une implemntation JAVA est proposé :
	Un seul et unique parcout du fichier 
	Un seul et unique parcour en largeur du graph

Une implementation la plus legere possible en memoire est proposé avec comme contraine :

	- Nous l'avons adapté pour repondre au pire cas ( quand il y a enormement de trous d'id dans le graph avec un idMax trés grand ,
		afin de ne pas avoir une complexcité en memoire qui puisse explosé )

	- Pour le graph orienté : n'est pas stocker la liste des sommets entrants dans chaque sommet

		Chaque sommet stock un id de CC atribué et une reference vers la CC pere ( null au depart )
		un "UNION FIND" pendant le parcour en largeur permet le calcul des CC (avec compression du chemin vers la CC pere)


La structure de donnée est uniquement une :

	LinkedHasMap de (clef,valeur) -> (idSommet , Sommet)
		Nous avons un Ierateur en O(1) pour l'operation Next


Chaque sommet stock une liste de ces voisins accessibles 
		+ dans le cas orient :
			 une reference vers la CC associé , chaque CC a une reference vers sa CC pere associe durant le parcout



#### ALGORITHME EN LARGEUR


	Sommet S

	Tant que pas tout sommet visité:

		si premiere passe :
			S = sommet passé en argument

		sinon :
			S = un sommet pas deja visité (on parcour la LinkedhasMap juqu'au prochain sommet non deja visité)

		dans la cas orienté:
			on cree une nouvelle id de CC noté CC*
			on associe a S la CC*

		on lance un parcour en largeur depuis S:

			dans la cas orienté:
				si un sommet voisin Sv possede une id de CC
					
					pere =find(CC)    (pour trouver la CC pere)
					
					si pere differente de CC*
						Sv.CC.pere = CC*

				sinon lui associé la CC*
