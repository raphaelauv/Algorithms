/**@deprecated preferer la version FORET
 * 
 * Car implementation de linkedlist de java non satisfesante en complexite pour addAll
 * @author Raph
 *
 */
public class ListDouble {
	public Noeud debut;
	public Noeud fin;
	public int size;

	public ListDouble() {
		this.size=0;
	}
	public ListDouble(int val) {
		this.size=0;
		debut=new Noeud(val,null,null);
		debut=fin;
	}
	public void add(int val){
		//addLast
		if(debut==null){
			this.debut=new Noeud(val, null, null);
			this.fin=this.debut;
		}else{

			this.fin=new Noeud(val, this.fin, null);
		}
		
		this.size++;
		
	}
	
	/**
	 * replace en premier le noeud avec la valeur val
	 * @param val
	 */
	public void replaceFirst(int val){
		if(this.debut.val==val){
			return;
		}
		Noeud tmp=this.debut.apres;
		while(tmp!=null){
			if (tmp.val==val){
				if(tmp==this.fin){
					this.fin=tmp.avant;
				}
				tmp.avant.apres=tmp.apres;
				tmp.apres.avant=tmp.avant;
				tmp.apres=this.debut;
				tmp.avant=null;
				this.debut=tmp;
				break;
			}
			tmp=tmp.apres;
		}
	}
	
	public void addAll(ListDouble list2){
		this.fin.apres=list2.debut;
		this.fin=list2.fin;
		
		this.size=this.size+list2.size;
		//temps constant
		
		/*
		 * java avec linkedlist fait une horreur
		 * http://www.docjar.com/html/api/java/util/LinkedList.java.html
		 * 
		 * la seconde list est converti en tableau
		 * et reconstitue en element uniques ...
		 */
		
	}
	public boolean contains(int id) {
		Noeud tmp=this.debut;
		while(tmp!=null){
			if(tmp.val==id){
				return true;
			}
			tmp=tmp.apres;
		}
		return false;
	}
}
