/**@deprecated preferer la version FORET
 * Pour implementer un noeud de listDouble
 * @author Raph
 *
 */
public class Noeud {

	public Noeud avant;
	public Noeud apres;
	public int val;
	
	public Noeud(int n, Noeud avant , Noeud apres){
		this.val=n;
		this.avant=avant;
		this.apres=apres;
	}
}
