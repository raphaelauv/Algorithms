public class Neighbor {
	private Cellule cell;
	private Boolean wall;

	/* Constructors */
	public Neighbor(Cellule c) {
		this.cell = c;
		this.wall = true;
	}

	public Neighbor(Cellule c, Boolean b) {
		this.cell = c;
		this.wall = b;
	}

	/* Get methods */
	public Cellule getCell() {
		return this.cell;
	}

	public Boolean isWall() {
		return this.wall;
	}

	/* Modification methods */
	public void breakWall() {
		this.wall = false;
	}
}
