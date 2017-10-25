public class Grid {
	public final int rows, columns;
	final Cellule[][] cell;

	public Grid(int n, int m) {
		this.rows = n;
		this.columns = m;
		this.cell = new Cellule[n][m];

		/* Initialize all the cells */
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				cell[i][j] = new Cellule(this.columns * i + j);

		/* Set the neighbors */
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++) {

				/* Set EAST */
				this.cell[i][j].setNeighbor(Cellule.EAST, (j == this.columns - 1) ? null : this.cell[i][j + 1]);

				/* Set WEST */
				this.cell[i][j].setNeighbor(Cellule.WEST, (j == 0) ? null : this.cell[i][j - 1]);

				/* Set SOUTHEAST */
				if (i == this.rows - 1)
					this.cell[i][j].setNeighbor(Cellule.SOUTHEAST, null);
				else if (i % 2 == 0)
					this.cell[i][j].setNeighbor(Cellule.SOUTHEAST, this.cell[i + 1][j]);
				else if (i % 2 == 1 && j < this.columns - 1)
					this.cell[i][j].setNeighbor(Cellule.SOUTHEAST, this.cell[i + 1][j + 1]);
				else
					this.cell[i][j].setNeighbor(Cellule.SOUTHEAST, null);

				/* Set SOUTHWEST */
				if (i == this.rows - 1)
					this.cell[i][j].setNeighbor(Cellule.SOUTHWEST, null);
				else if (i % 2 == 0 && j != 0)
					this.cell[i][j].setNeighbor(Cellule.SOUTHWEST, this.cell[i + 1][j - 1]);
				else if (i % 2 == 1)
					this.cell[i][j].setNeighbor(Cellule.SOUTHWEST, this.cell[i + 1][j]);
				else
					this.cell[i][j].setNeighbor(Cellule.SOUTHWEST, null);

				/* Set NORTHEAST */
				if (i <= 0)
					this.cell[i][j].setNeighbor(Cellule.NORTHEAST, null);
				else if (i % 2 == 0)
					this.cell[i][j].setNeighbor(Cellule.NORTHEAST, this.cell[i - 1][j]);
				else if (i % 2 == 1 && j < this.columns - 1)
					this.cell[i][j].setNeighbor(Cellule.NORTHEAST, this.cell[i - 1][j + 1]);
				else
					this.cell[i][j].setNeighbor(Cellule.NORTHEAST, null);

				/* Set NORTHWEST */
				if (i <= 0)
					this.cell[i][j].setNeighbor(Cellule.NORTHWEST, null);
				else if (i % 2 == 0 && j != 0)
					this.cell[i][j].setNeighbor(Cellule.NORTHWEST, this.cell[i - 1][j - 1]);
				else if (i % 2 == 1)
					this.cell[i][j].setNeighbor(Cellule.NORTHWEST, this.cell[i - 1][j]);
				else
					this.cell[i][j].setNeighbor(Cellule.NORTHWEST, null);
			}
	}

	public String showContent(boolean show, int i, int j) {
		if (show)
			return Auxiliaire.numInThree(this.cell[i][j].getId());
		else
			return "   ";
	}

	/* Don't judge me */
	public void showGrid(boolean show) {
		for (int j = 0; j < this.columns; j++)
			System.out.print(" / \\");
		System.out.println("");
		for (int i = 0; i < this.rows; i++) {
			if (i % 2 == 1)
				System.out.print("  ");
			for (int j = 0; j < this.columns; j++)
				System.out
						.print(((this.cell[i][j].getNeighbor(Cellule.WEST).isWall() && (i != 0 || j != 0)) ? "|" : " ")
								+ showContent(show, i, j));
			if (i != this.rows - 1)
				System.out.println("|");
			else
				System.out.println(" ");
			if (i < this.rows - 1)
				if (i % 2 == 1) {
					for (int j = 0; j < this.columns; j++)
						System.out
								.print(" "
										+ ((j == 0 || this.cell[i][j - 1].getNeighbor(Cellule.SOUTHEAST).isWall() ? "/"
												: " "))
										+ " " + (this.cell[i][j].getNeighbor(Cellule.SOUTHWEST).isWall() ? "\\" : " "));
					System.out.println(" /");
				} else {
					for (int j = 0; j < this.columns; j++)
						System.out
								.print(" "
										+ ((j == 0 || this.cell[i][j].getNeighbor(Cellule.SOUTHWEST).isWall() ? "\\"
												: " "))
										+ " " + (this.cell[i][j].getNeighbor(Cellule.SOUTHEAST).isWall() ? "/" : " "));
					System.out.println(" \\");
				}
		}
		if (this.rows % 2 == 0)
			System.out.print("  ");
		for (int j = 0; j < this.columns; j++)
			System.out.print(" \\ /");
		System.out.println("");

	}

	public void showGrid() {
		this.showGrid(false);
	}
	/**
	 * 
	 * 
	 * @param n
	 * @param m
	 * @return la cellule a position n m dans la grille
	 */
	public Cellule getCel(int n , int m){
		if(n<rows && m<columns){
			return this.cell[n][m];
		}
		else{
			return null;
		}
	}
}

