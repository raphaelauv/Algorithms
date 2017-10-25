public class Main {
	public static void main(String[] args) {
		//Grid myLaby = Laby.makeLaby(200, 200);// two dimensional labyrinth
		Grid a2 = Laby.makeLaby(200,200);
		a2.showGrid();
		System.out.println();
		Grid a3 = Laby.makeLabyA(1, 5);
		a3.showGrid();
		System.out.println("Check passed");
	}
}
