import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Laby {
	
	/**
	 * @deprecated prefere la version FORET
	 * 
	 * Solution utilisant des listes chainees
	 * liste de liste , un objet aurait pu etre creer pour plus de simplicite ...
	 * contient les sous-ensembles d ID reuni
	 * @param size le nombre de case au debut
	 * @return une liste de listes compose chucune d un ID
	 */
	public static LinkedList<ListDouble> listPartitions(int size){
		LinkedList<ListDouble> tmp = new LinkedList<ListDouble>();
		for (int i=0 ; i<size ; i++){
			ListDouble tmp2= new ListDouble();
			tmp2.add(i);
			tmp.add(tmp2);
		}
		return tmp;
	}
	
	/**
	 * LA version ALEATOIRE
	 * 
	 * @param n
	 *            nombre de lignes
	 * @param m
	 *            nombre de colones
	 * @return une grille labyrinthe
	 */
	public static Grid makeLaby(int n, int m) {
		Grid grille = new Grid(n, m);
		
		Random random = new Random();
		Foret foret= new Foret(n*m);
		//inutile de commencer avec le while car au debut la grille ne peut pas etre connexe
		do{
			int[] permutations = getTabPermutations(n * m);
			for (int i = 0; i < permutations.length; i++) {
				
				int columns=i/(grille.columns);
				int ligne=i-(columns*grille.columns);
				Cellule celluleActive = grille.cell[columns][ligne];
				
				LinkedList<Integer> lesVoisins = getVoisinsNonConnect(celluleActive);

				if (lesVoisins.size() != 0) {
					int alea = random.nextInt(lesVoisins.size());
					int valeurVoisinChoisi = lesVoisins.get(alea);

					union(celluleActive,valeurVoisinChoisi ,grille, foret);

				}
			}
		}while(!connex(foret));
		//while (!connex(listPartitions));
		//while (!connex(grille));
		return grille;
	}

	/**
	 * La version NON ALEATOIRE
	 * 
	 * 
	 * @param n
	 *            nombre de lignes
	 * @param m
	 *            nombre de colones
	 * @return
	 */
	public static Grid makeLabyA(int n, int m) {
		Random random = new Random();
		Grid grille = new Grid(n, m);
		//LinkedList<ListDouble> listPartitions = listPartitions(n*m);
		Foret foret= new Foret(n*m);
		
		//inutile de commencer avec le while car au debut la grille ne peut pas etre connexe
		do{
			for (int i = 0; i < grille.rows; i++) {
				//de gauche a droite
				for (int j = 0; j < grille.columns; j++) {
					//de haut en bas
					
					Cellule celluleActive = grille.cell[i][j];

					LinkedList<Integer> lesVoisins = getVoisinsNonConnect(celluleActive);

					if (!lesVoisins.isEmpty()) {
						int alea = random.nextInt(lesVoisins.size());//choix aleatoire d un voisin
						int valeurVoisinChoisi=lesVoisins.get(alea);
						
						//union(celluleActive,valeurVoisinChoisi , grille, listPartitions);
						//union(celluleActive,valeurVoisinChoisi , grille);
						
						union(celluleActive,valeurVoisinChoisi ,grille, foret);
					}
				}
			}
		}while(!connex(foret));
		//while (!connex(listPartitions));
		//while (!connex(grille));
		return grille;
	}
	
	/**
	 * Version foret
	 * @param celluleActive
	 * @param valVoisin
	 * @param grille 
	 * @param foret
	 */
	private static void union(Cellule celluleActive, int valVoisin, Grid grille, Foret foret) {
		celluleActive.breakWallWith(valVoisin);
		
		Cellule celluleVoisine =celluleActive.getNeighbor(valVoisin).getCell();
		
		Arbre pereActive=findCompress(celluleActive.getId(), foret);
		Arbre pereVoisine=findCompress(celluleVoisine.getId(), foret);
		if (pereActive == pereVoisine) return;
		if (pereActive.taille > pereVoisine.taille) {

			pereVoisine.pere = pereActive;
			pereActive.addFils(pereVoisine);
			
			pereActive.taille = pereActive.taille + pereVoisine.taille;
			
			appliquerUnion(pereVoisine,pereActive.val,grille);
		
		} else {
			pereActive.pere=pereVoisine;
			pereVoisine.addFils(pereActive);
			
			pereVoisine.taille = pereActive.taille + pereVoisine.taille;
			
			appliquerUnion(pereActive,pereVoisine.val,grille);
		}
		
	}
	
	/**
	 * change le ID de chaque cellule de l arbre inserer (dans le plus grand arbre des deux)
	 *  par le ID du sommet du grand arbre
	 * 
	 * @param manger
	 * @param nouvelID
	 * @param grille
	 */
	private static void appliquerUnion(Arbre manger,int nouvelID,Grid grille){
		LinkedList<Arbre> liste= new LinkedList<>();
		liste.add(manger);
		while(!liste.isEmpty()){
			Arbre first=liste.remove();
			changerIDCellule(first.val,nouvelID,grille);
			for(Arbre tmp:first.fils){
				liste.add(tmp);
			}
		}
		//System.out.println("appliquer union");
		
		//System.out.println("la case val "+manger.val+" devient :"+nouvelID);
		
		//	System.out.println("la case val "+tmp.val+" devient :"+nouvelID);
			//System.out.println("val a changer : "+tmp.val+" en : "+nouvelID);
			//changerIDCellule(tmp.val,nouvelID,grille);
			//appliquerUnion(tmp,nouvelID,grille);
	}

	/**
	 * @deprecated preferer la version FORET
	 * Version liste double
	 * @param grille
	 * @param valVoisin
	 * @param listPartitions 
	 */
	public static void union(Cellule celluleActive, int valVoisin,Grid grille, LinkedList<ListDouble> listPartitions) {
		celluleActive.breakWallWith(valVoisin);
		Cellule celluleVoisine =celluleActive.getNeighbor(valVoisin).getCell();
		appliquerUnion(celluleActive,celluleVoisine, listPartitions,grille);
	}
	
	/**@deprecated preferer la version FORET
	 * 
	 * Version en place 
	 * 
	 * @param celluleActive
	 * @param valVoisin
	 * @param grille
	 */
	public static void union(Cellule celluleActive, int valVoisin,Grid grille) {
		
		celluleActive.breakWallWith(valVoisin);
		appliquerUnion(celluleActive, new ArrayList<Cellule>(),grille);
	}
	
	/**
	 * Version foret
	 * @param foret.arbres
	 */
	private static Arbre find(int cellule, Foret foret) {
		Arbre abPere=foret.arbres[cellule];
		while(abPere.pere!=null){
			
			abPere=abPere.pere;
		}
		return abPere;
	}
	
	/**
	 * On remonte du noeud courant a sa racine r,puis on refait le parcours en faisant de chaque 
	 * noeud rencontre un fils de r.
	 * @param cellule
	 * @param foret
	 * @return
	 */
	public static Arbre findCompress(int cellule, Foret foret){
		Arbre r = find( cellule,  foret);
		Arbre courant=foret.arbres[cellule];
		
		while (courant!= r)
		{
			Arbre y = courant.pere;
			courant.pere = r; // x devient fils de r
			r.addFils(courant);
			if(y.pere!=null){
				y.removesFils(courant);
			}
			courant = y;
		}
		return r;
	}
	
	/**@deprecated preferer la version FORET
	 * Version en place
	 * @param c
	 * @param grille
	 * @param liste
	 */
	public static void appliquerUnion(Cellule c, ArrayList<Cellule> liste,Grid grille) {
		if (liste.contains(c)) {
			return;
		}
		liste.add(c);
		for (int v = 0; v < 6; v++) {
			//pour chaque cellule connecter j applique l union
			if (c.hasNeighbor(v) && !c.getNeighbor(v).isWall()) {
				
				c.getNeighbor(v).getCell().setId(c.getId());
				
				appliquerUnion(c.getNeighbor(v).getCell(), liste,grille);
			}
		}
	}

	/**@deprecated preferer la version FORET
	 * Version avec les liste double
	 * @param celluleActive
	 * @param celluleVoisine
	 * @param listPartitions
	 */
	private static void appliquerUnion(Cellule celluleActive, Cellule celluleVoisine,
			LinkedList<ListDouble> listPartitions,Grid grille) {
		//System.out.println("---");
		//System.out.println("on cherche :"+celluleActive.getId()+" et "+celluleVoisine.getId());
		ListDouble listActive=null;
		ListDouble listVoisine=null;
		
		for(ListDouble tmp : listPartitions){
			
			if(listActive!=null && listVoisine!=null){
				break;
			}
			if(tmp.contains(celluleActive.getId())){
			//	System.out.println("trouve la active");
				listActive=tmp;
			}
			if(tmp.contains(celluleVoisine.getId())){

				//System.out.println("trouve la voisine");
				listVoisine=tmp;
			}
		}
		if (listActive==listVoisine){
			return;
		}
		/*
		System.out.print("active :");
		System.out.println(listActive);
		System.out.print("voisine :");
		System.out.println(listVoisine);
		*/
		
		Noeud tmp= listVoisine.debut;
		int columns=0;
		while(tmp!=null){
			
			changerIDCellule(tmp.val,celluleActive.getId(),grille);
			tmp=tmp.apres;
		}
		listActive.addAll(listVoisine);
		listActive.replaceFirst(celluleActive.getId());
		listPartitions.remove(listVoisine);
		
	}
	/**
	 * Applique le changement d ID de la val d une cellule dans la grille
	 * @param valCellule
	 * @param nouvelID
	 * @param grille
	 */
	public static void changerIDCellule(int valCellule,int nouvelID, Grid grille){
		int columns=0;
		//System.out.println("valeur ID voisin = "+tmp.val);
		columns=valCellule/(grille.columns);
		if(columns>0){
			int ligne=valCellule-(columns*grille.columns);
			grille.getCel(columns, ligne).setId(nouvelID);
		}else{
			grille.getCel(0, valCellule).setId(nouvelID);
		}
	}
	
	/**
	 * @deprecated preferer la version FORET
	 * version en place
	 * 
	 * @param grille
	 *            la grille qui doit etre analyse
	 * @return false si il existe deux cellules non connectees sinon true
	 */
	public static boolean connex(Grid grille) {
		int tmp = grille.cell[0][0].getId();
		for (int i = 0; i < grille.cell.length; i++) {
			for (int j = 0; j < grille.cell[0].length; j++) {
				if (tmp != grille.cell[i][j].getId())
					return false;
			}
		}
		return true;
	}
	
	/**@deprecated preferer la version FORET
	 * version avec liste double
	 * @param listPartitions
	 * @return
	 */
	public static boolean connex(LinkedList<ListDouble> listPartitions) {
			return listPartitions.size()==1;
	}
	
	/**
	 * Version foret
	 * @param foret
	 * @return si la foret est connex alors tous les points ont le meme pere
	 */
	private static boolean connex(Foret foret) {
		Arbre tmp=findCompress(0, foret);
		for(int i=1; i<foret.arbres.length ; i++){
			if(findCompress(i, foret)!=tmp){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Tableau de permutations alea
	 * @param tabPermutations
	 *            liste des valeurs a permuter
	 * @return un tableau dont les valeurs ont ete permutees
	 */
	public static int[] getTabPermutations(int size) {
		Random random = new Random();
		int[] tabPermutations=new int[size];
		for (int i = 0; i < tabPermutations.length; i++) {
			tabPermutations[i] = i;
			//initialise de 0 a i le tab de permutations
		}
		for (int j = 0; j < tabPermutations.length; j++) {
			int alea = random.nextInt(tabPermutations.length - j) + j;
			int tmp = tabPermutations[j];
			tabPermutations[j] = tabPermutations[alea];
			tabPermutations[alea] = tmp;
		}
		return tabPermutations;
	}

	/**
	 * Recuperer les voisins non connect
	 * @param c
	 *            la cellule dont on cherche les voisins non connecte
	 * @return une liste avec les val des voisins non connecte
	 */
	public static LinkedList<Integer> getVoisinsNonConnect(Cellule c) {

		LinkedList<Integer> lesVoisins = new LinkedList<Integer>();

		for (int i = 0; i < 6; i++) {
			if (
					c.hasNeighbor(i) // le voisin existe
					&& c.getNeighbor(i).isWall() // bloque par un mur
					&&c.getId() != c.getNeighborId(i) // pas du meme ID
				) {
				lesVoisins.add(i);
			}
		}
		return lesVoisins;
	}

	public static void main(String[] args) {
		
		long debut=System.currentTimeMillis();
		Grid coco=Laby.makeLaby(1000,1000);

		long fin=System.currentTimeMillis();
		//coco.showGrid();
		System.out.println(fin-debut);
		
	
	}
}