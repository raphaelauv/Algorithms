
public class Foret {
	public Arbre[] arbres;
	public int[] tailles;
	
	public Foret(int taille){
		this.arbres= new Arbre[taille];
		//this.tailles= new int[taille];
		for (int i =0 ; i< arbres.length ; i++){
			arbres[i]=new Arbre(i);
			//tailles[i]=1;
		}
		
	}
	
	

}
