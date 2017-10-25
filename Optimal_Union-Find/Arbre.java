import java.util.ArrayList;

public class Arbre {

	public Arbre pere;
	public ArrayList<Arbre> fils;
	public int val;
	public int taille;
	
	public Arbre(int val){
		this.val=val;
		this.taille=1;
		this.pere=null;
		fils=new ArrayList<>();
	}
	
	public void setPere(Arbre pere){
		this.pere=pere;
	}
	
	public void addFils(Arbre fils){
		this.fils.add(fils);
	}
	public void removesFils(){
		this.fils.clear();
	}
	public void removesFils(Arbre Fils){
		this.fils.remove(fils);
		
	}
}
