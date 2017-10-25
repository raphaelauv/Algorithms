/*
 * 
 * Solution de l'algorithme Hongrois en n^4
 * 
 * COMPILATION : javac Hungarianalgorithm.java 
 * 
 * EXECUTION : java Hungarianalgorithm filename
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

public class Hungarianalgorithm {

	static int nbOfZeroSelect;
	static int [] alreadySelectInColomnOrder;
	static int [] alreadySelectInLineOrder;
	static boolean [] colomnsMarqued;
	static boolean [] lignesMarqued;
	static int sizeColomnsMarqued;
	static int sizeLignesMarqued;
	static LinkedList<Integer> ligneWithoutZeroSelected;
	static int [] nbZeroInLigne;
	static int nbLinesWithSelectableZero;
	static int[] nbZeroInColomn;
	static boolean [][] matrixZeroMarqued;
	static int [][] PrimedStarredZero;
	
	
	static int step;
	
	static int XStep4;
	static int YStep4;
	
	public static void main(String[] args) throws Exception {

		//long startTime = System.currentTimeMillis();
		
		
		int[][] matrix = getMatrix(args[0]);
		int result = hongrois(matrix);
		System.out.println(result);
		
		/*
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("TIME : "+elapsedTime);
		*/
	}

	/*
	 * Apply the hungarian algorithme maximize version
	 */
	public static int hongrois(int [][] matrix){
		
		//showMatrix(matrix);
		step=0;
		int [][] matrix2=null;
		int result = 0;
		boolean done=false;		
		while(!done){
			switch(step){
			case 0:
				matrix2=step0(matrix);
				showMatrix(matrix2);
				step=1;
				break;
			case 1:
				step1(matrix2);
				break;
			case 2:
				step2(matrix2);
				break;
			case 3:
				step3bis(matrix2);
				break;
			case 4:
				step4(matrix2);
				break;
			case 5:
				step5(matrix2);
				break;
			case 6:
				step6(matrix2);
				break;
			case 7:
				result=step7(matrix);
				showArray(alreadySelectInColomnOrder);
				done=true;
				break;
			}
		}
		return result;
	}

	public static void resetStaticVariable(int [][]matrix){
		//showMatrix(matrix);
		nbOfZeroSelect = 0;
		alreadySelectInColomnOrder = new int[matrix.length];

		alreadySelectInLineOrder = new int[matrix.length];

		//HashSet<PointPerso> zeroBarred = new HashSet<PointPerso>();

		colomnsMarqued = new boolean[matrix.length];
		lignesMarqued = new boolean[matrix.length];

		sizeColomnsMarqued = 0;
		sizeLignesMarqued = 0;

		ligneWithoutZeroSelected = new LinkedList<Integer>();

		nbZeroInLigne = new int[matrix.length];

		nbLinesWithSelectableZero = nbZeroInLigne.length;

		matrixZeroMarqued = new boolean[matrix.length][matrix.length];

		nbZeroInColomn = new int[matrix.length];
		
	}

	public static int[][] step0(int [][] matrix){
		return reduceMatrixByValue(matrix,findValueOfBiggerElement(matrix));
	}
	
	public static void step1(int[][] matrix) {
		// showMatrix(matrix2);

		for (int i = 0; i < matrix.length; i++) {
			int posL = findPositionOfSmallerElementLigne(matrix[i]);
			reduceLigne(matrix[i][posL], matrix[i]);
		}

		// showMatrix(matrix2);

		for (int i = 0; i < matrix.length; i++) {
			int posC = findPositionOfSmallerELementColumn(matrix, i);
			reduceColumn(matrix[posC][i], matrix, i);
		}
		
		PrimedStarredZero= new int[matrix.length][matrix.length];
		
		step=2;
	}
	
	public static void step2bis(int[][] matrix){
		
		resetStaticVariable(matrix);
		
		for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix.length; j++) {
					
					if(matrix[i][j]==0 && lignesMarqued[i]==false && colomnsMarqued[j]==false ){
						PrimedStarredZero[i][j]=1;
						nbOfZeroSelect++;
						lignesMarqued[i]=true;
						colomnsMarqued[j]=true;
						//alreadySelectInColomnOrder[j]=i;
						//alreadySelectInLineOrder[i]=j;
					}
				}
		}
		for (int i = 0; i < matrix.length; i++) {
			lignesMarqued[i]=false;
			colomnsMarqued[i]=false;
		}
		
		//reduceArrayOf1(alreadySelectInColomnOrder);// to get the -1 of
		// colomn without ZERO
		// selected
		
		//showArray(alreadySelectInColomnOrder);
		
		//reduceArrayOf1(alreadySelectInLineOrder);// to get the -1 of Line
		// without ZERO selected
		
		step=3;
	}
	
	public static void step2(int[][] matrix){
		
		resetStaticVariable(matrix);
		
		for (int i = 0; i < matrix.length; i++) {
			nbZeroInLigne[i] = numberOfZeroLigne(matrix[i]);
			//System.out.println(nbZeroInLigne[i]);
		}
		int lineSelect;
		int colomnSelect;
		do {
			//System.out.println("valeur nombre with selectable ZERO "+nbLinesWithSelectableZero);			
			lineSelect = findPositionOfSmallerElementMoreThan0(nbZeroInLigne);
			
			//System.out.println("line avec 0 minimums :"+lineSelect);
			if (nbZeroInLigne[lineSelect] == 1) {
				//System.out.println("ligne "+lineSelect+" avec 1 ZERO");
				colomnSelect = findPositionOfZeroUnmarqued(matrix[lineSelect],lineSelect,matrixZeroMarqued);
				selectZero(matrix, lineSelect, colomnSelect);

			} else if (nbZeroInLigne[lineSelect] > 1) {

				// We select the ZERO with the less other ZERO UNMARQUED in
				// the same COLOMN
				for (int j = 0; j < matrix.length; j++) {
					nbZeroInColomn[j] = numberOfZeroColumnUnmarqued(matrix,j,matrixZeroMarqued);
					//System.out.println("!!!!! NOMBRE de zero non marque :"+nbZeroInColomn[j] +" dans la colone "+j);
				}
				colomnSelect = getMinPositiveValue(nbZeroInColomn);
				selectZero(matrix, lineSelect, colomnSelect);
				
				for(int i=0;i<matrix.length;i++){
					if(matrix[lineSelect][i]==0){
						matrixZeroMarqued[lineSelect][i]=true;
					}
				}
			}
		} while (nbLinesWithSelectableZero > 0);

		reduceArrayOf1(alreadySelectInColomnOrder);// to get the -1 of
		// colomn without ZERO
		// selected
		reduceArrayOf1(alreadySelectInLineOrder);// to get the -1 of Line
		
		if (nbOfZeroSelect == matrix.length) {
			// System.out.println("nb of ZERO good");
			step=7;
			
		} else {
			step=4;
			
		}
	}
	
	public static void step3bis(int[][] matrix) {

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {

				if (PrimedStarredZero[i][j] == 1) {
					colomnsMarqued[j] = true;
				}
			}
		}
		
		int colcount=0;
		for (int i = 0; i < matrix.length; i++) {
			if(colomnsMarqued[i]){
				colcount++;
			}
		}
		
		//reduceArrayOf1(alreadySelectInColomnOrder);
		//reduceArrayOf1(alreadySelectInLineOrder);
		if(colcount>=matrix.length){
			step = 7;
		}else{
			step = 4;
		}
	}
	
	public static void step4(int [][]matrix){
		// System.out.println("nb of ZERO wrong");

		int TMPsizeColomnsMarqued;
		int TMPsizeLignesMarqued;
		
		int compteur = 0;
		do {

			TMPsizeColomnsMarqued = sizeColomnsMarqued;
			TMPsizeLignesMarqued = sizeLignesMarqued;
			compteur++;

			for (int i = 0; i < colomnsMarqued.length; i++) {
				if (!colomnsMarqued[i]) {
					for (int j = 0; j < lignesMarqued.length; j++) {
						if (!lignesMarqued[j]) {

							if (matrix[j][i] == 0) {
								//System.out.println("dans la cologne " + i + " dans la ligne " + j);
								lignesMarqued[j] = true;// we Marque the
														// line
								sizeLignesMarqued++;
								//System.out.println("zero primed "+j+" "+i);
								PrimedStarredZero[j][i]=2;
								
								/*
								int colomOfZeroSelect=-1;
								for(int c=0;c<matrix.length;c++){
									if(PrimedStarredZero[c][j]==1){
										colomOfZeroSelect=c;
										break;
									}
								}
								*/
								int colomOfZeroSelect=alreadySelectInLineOrder[j];
								
								//System.out.println("colom du zero selected dans la ligne du zero libre dans la colone vide :"+ colomOfZeroSelect);

								if (colomOfZeroSelect == -1) {
									//showMatrix(matrix);
									//showArray(alreadySelectInColomnOrder);
									//System.out.println("position bloquante "+j+" : "+i);
									
									XStep4=j;
									YStep4=i;
									step=5;
									return;
								}else{
									colomnsMarqued[colomOfZeroSelect] = false;// unmark the colomn
									sizeColomnsMarqued--;
								}
							}
						}
					}
				}
			}
		} while (sizeLignesMarqued > TMPsizeLignesMarqued || sizeColomnsMarqued > TMPsizeColomnsMarqued);

		// System.out.println("compteur de while de STEP B ET C :
		// "+compteur);
		// System.out.println("size colomns marqued
		// :"+sizeColomnsMarqued);
		// System.out.println("size lignes marqued
		// :"+sizeLignesMarqued);
		step=6;
	}
	
	public static void step5(int [][] matrix){
		// STEP2'
		class ZPerso{
			int X;
			int Y;
			boolean starred;
			
			public ZPerso(int x, int y, boolean starred) {
				super();
				X = x;
				Y = y;
				this.starred = starred;
			}
		}
		
		LinkedList<ZPerso> serie=new LinkedList<ZPerso>();
		ZPerso Zi = new ZPerso(XStep4,YStep4,false);
		serie.add(Zi);
		System.out.println("z0 add "+Zi.X+" : "+Zi.Y);
		boolean end=false;
		int nbBoucle=1;
		boolean find=false;
		while(!end){
			find=false;
			if((nbBoucle%2)==0){
				
				/*
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				int lineOfZiBefore=Zi.X;
				System.out.println("dans pair , ligne "+lineOfZiBefore);
				for (int jj= 0; jj < matrix.length; jj++) {
					if(PrimedStarredZero[lineOfZiBefore][jj]==1){
						Zi=new ZPerso(lineOfZiBefore,jj,false);
						serie.add(Zi);
						System.out.println("position de Z"+nbBoucle+" PAIR"+" add "+Zi.X+" : "+Zi.Y);
						find=true;
						break;
					}
				}
				if(!find){
					System.out.println("rien trouver !!");
				}
			}
			else{
				int colomnOfZibefore=Zi.Y;
					int val= alreadySelectInColomnOrder[colomnOfZibefore];
					System.out.println("valeur ligne : "+val+"   du 0 deja coch� dans la colonne ");
					if(val!=-1){
						Zi=new ZPerso(val,colomnOfZibefore,true);
						serie.add(Zi);
						System.out.println("position de Z"+nbBoucle+" IMPAIR"+" add "+Zi.X+" : "+Zi.Y);
					}else{
						System.out.println("fin");
						end=true;
					}
			}
			nbBoucle++;
		}

		System.out.println(" AVANT nb of zero selected: "+nbOfZeroSelect);
		for(ZPerso zeroPerso : serie){
			if(!zeroPerso.starred){
				PrimedStarredZero[zeroPerso.X][zeroPerso.Y]=1;
				
				alreadySelectInColomnOrder[zeroPerso.Y]=zeroPerso.X;
				alreadySelectInLineOrder[zeroPerso.X]=zeroPerso.Y;
				
				System.out.println("zero cocher "+zeroPerso.X+" : "+zeroPerso.Y);
				nbOfZeroSelect++;
				
			}else{
				PrimedStarredZero[zeroPerso.X][zeroPerso.Y]=2;
				alreadySelectInColomnOrder[zeroPerso.Y]=-1;
				alreadySelectInLineOrder[zeroPerso.X]=-1;
				nbOfZeroSelect--;

			}
		}
		
		//Starred become Primed and Primed become Starred
		//System.out.println(" APRES nb of zero selected: "+nbOfZeroSelect);
		lignesMarqued=new boolean[matrix.length];
		
		for(int i=0;i<matrix.length;i++){
			for(int j=0;j<matrix.length;j++){
				if(PrimedStarredZero[i][j]==2){
					PrimedStarredZero[i][j]=0;
				}
			}
		}
		step=2;
	}
	
	public static void step6(int [][]matrix){
		/**
		 * When k=0 , we just search for the minimum value of the
		 * Partial matrix. 
		 * When k=1 , we apply the reduction on the
		 * partial matrix and the addition on the other who are
		 * concerned
		 */

		int minValue = 0;
		boolean firstTime = true; // to initialise the minvalue with the
									// first value of the partial matrix

		for (int k = 0; k < 2; k++) {
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix.length; j++) {

					if (k == 1) {
						if (lignesMarqued[i]) {
							matrix[i][j] += minValue;
						}
						if (!colomnsMarqued[j]) {
							matrix[i][j] -= minValue;
						}
					} else {
						if (!lignesMarqued[i]) {
							if (!colomnsMarqued[j]) {
								if (firstTime) {
									minValue = matrix[i][j];
									firstTime = false;
								} else {
									if (matrix[i][j] < minValue) {
										minValue = matrix[i][j];
									}
								}

							}
						}
					}
				}
			}
		}
		step=2;
		//System.out.println("minimum value : " + minValue);
	}
	
	public static int step7bis(int[][] matrix){
		return SumStarredValues(matrix);
	}
	
	public static int step7(int[][] matrix){
		return SumSelectedValues(alreadySelectInColomnOrder,matrix);
		
	}
	
	public static int findPositionOfSmallerElementMoreThan0(int[] array) {
		int position = 0;
		int valMini = Integer.MAX_VALUE;
		for (int i = 0; i < array.length; i++) {
			//System.out.println("la ligne "+i+" a la valeur : "+array[i]);
			if (array[i] > 0 && array[i] <= valMini) {
				valMini = array[i];
				position = i;
			}
		}
		return position;
	}
	
	public static int findPositionOfSmallerElementLigne(int[] array){
		int position=0;
		int valMini=array[0];
		
		for(int i=1; i<array.length;i++){
			if(array[i]<valMini){
				position=i;
				valMini=array[i];
			}
		}
		return position;
	}

	public static int findPositionOfSmallerELementColumn(int [][] matrix, int column){
		int position=0;
		int valMini=matrix[0][column];
		for(int i=1; i<matrix.length;i++){
			if(matrix[i][column]<valMini){
				position=i;
				valMini=matrix[i][column];
			}
		}
		return position;
	}
		
	/*
	 * apply the reduction of all the content of the ligne with the value 
	 */
	public static void reduceLigne(int value, int[] array) {
		for (int k = 0; k < array.length; k++) {
			array[k] = array[k] - value;
			;
		}
	}
	
	/*
	 * apply the reduction of all the content of the colomn with the value 
	 */
	public static void reduceColumn(int value, int[][] matrix, int column) {
		for (int i = 0; i < matrix.length; i++) {
			matrix[i][column] = matrix[i][column] - value;
		}
	}
	
	/*
	 * Count the number of Zero in the array
	 * 
	 */
	public static int numberOfZeroLigne(int[] array) {
		int value=0;
		for (int k = 0; k < array.length; k++) {
			if(array[k]==0){
				value++;
			}
		}
		return value;
	}
	
	/*
	 * Count the number of Zero in the column from a ligne number in the matrix
	 */
	public static int numberOfZeroColumn(int[][] matrix, int column,int fromLigne) {
		int value=0;
		for (int i = fromLigne; i < matrix.length; i++) {
			if(matrix[i][column]==0){
				value++;
			}
		}
		//System.out.println("valeur retour "+value+" numberofzero colom "+column);
		return value;
	}

	public static int numberOfZeroColumnUnmarqued(int [][] matrix,int column,boolean [][] matrixZeroMarqued){
		int value=0;
		for (int i = 0; i < matrix.length; i++) {
			if(matrix[i][column]==0 && matrixZeroMarqued[i][column]==false){
				//System.out.println("zero encore ACTIF dans cologne"+column+" en ligne "+i);
				//there is a FREE ZERO
				value=value+1;
			}
		}
		//System.out.println("valeur retour "+value+" numberofzero colom "+column);
		return value;
	}
	
	public static int findPositionOfZeroUnmarqued(int[] array,int line,boolean [][] matrixZeroMarqued){
		int position=-1;
		for(int i=0; i<array.length;i++){
			if(array[i]==0 && matrixZeroMarqued[line][i]==false){
				position=i;
			}
		}
		//System.out.println("COLOGNE du ZERO choisit :"+position);
		return position;
	}
	
	public static int findValueOfBiggerElement(int[][] matrix){
		int valMax=matrix[0][0];
		for(int i=1; i<matrix.length;i++){
			for(int j=1; j<matrix.length;j++){
				if(matrix[i][j]>valMax){
					valMax=matrix[i][j];
				}
			}
		}
		return valMax;	
	}
	
	/*
	 * Reduce all the matrix of a value 
	 * we convert the matrix because we are searching for the maximize value
	 */
	public static int[][] reduceMatrixByValue(int [][] matrix , int value){
		
		int [][] matrix2=new int[matrix.length][matrix.length];
		for(int i=0; i<matrix.length;i++){
			for(int j=0; j<matrix.length;j++){
					matrix2[i][j]=value-matrix[i][j];
				}
			}
		return matrix2;
	}


	/*
	 * Print on the standart output the contents of array
	 */
	public static void showArray(int [] array){
		System.out.println("");
		for(int i=0;i<array.length;i++){
			System.out.print(array[i]+" ");
		}
		System.out.println("");
	}
	
	/*
	 * Get from the right of the array
	 * the minimum value who is positive but different of 0
	 * 
	 * like this we recover the colomn of the actual 0 with the less other zero below
	 */
	public static int getMinPositiveValue(int [] nbZeroInColomn){
		int result=nbZeroInColomn.length-1;
		int minValue=nbZeroInColomn.length; // we start with the maximum number of ZERO possible
		for(int i=nbZeroInColomn.length-2; i>-1; i--){
			
			if(nbZeroInColomn[i]>0 && nbZeroInColomn[i]<=minValue ){
				result=i;
				minValue=nbZeroInColomn[i];
			}else{
				//System.out.println("plus petit que 0 et valeur est "+nbZeroInColomn[i]);
			}
		}
		//System.out.println("cologne selectionner "+result+" pour valeur mini "+minValue);
		return result;
		
	}
	
	
	/**
	 * Apply the selection of the specified ZERO
	 * @param matrix
	 * @param lineSelect
	 * @param colomnSelect
	 */
	public static void selectZero(int [][] matrix , int lineSelect,int colomnSelect){
		
		//System.out.println("ZERO SELECTED : ligne "+lineSelect+" colomn "+colomnSelect);
		colomnsMarqued[colomnSelect]=true;
		
		matrixZeroMarqued[lineSelect][colomnSelect]=true;
		
		sizeColomnsMarqued++;
		alreadySelectInColomnOrder[colomnSelect]=lineSelect+1;
		alreadySelectInLineOrder[lineSelect]=colomnSelect+1;
		nbOfZeroSelect++;
		nbZeroInLigne[lineSelect]=0;
		nbLinesWithSelectableZero--;
		
		//we reduce the other lines with a ZERO in the i colomn because this coloms is now take
		for(int j=0;j<matrix.length;j++){
			if(j!=lineSelect){
				if(matrix[j][colomnSelect]==0){
					if(matrixZeroMarqued[j][colomnSelect]==false){
						matrixZeroMarqued[j][colomnSelect]=true;
						nbZeroInLigne[j]--;
						//System.out.println("position "+j+" : "+colomnSelect+" devient JAUNE + valeur restante"+nbZeroInLigne[j]);
						if(nbZeroInLigne[j]==0){
							//System.out.println("la ligne "+j+" n'a plus de ZERO disponible");
							nbLinesWithSelectableZero--;
						}
					}
				}
			}
		}
	}

	@Deprecated
	public static void step1bis(int [][] matrix){
		for(int i=0;i<matrix.length;i++){
			
			boolean placeForNewZero=false;
			
			nbZeroInColomn=new int[matrix.length];
			//int [] nbZeroInLigne=new int[matrix.length];
			
			
			nbZeroInLigne[i]=numberOfZeroLigne(matrix[i]);
			//nbZeroInColomn[i]=numberOfZeroColumn(matrix,i,0);
			
			//System.out.println("LIGNE "+i+" NOMBRE DE ZERO "+nbZeroInLigne[i]);
			
			for(int j=0;j<matrix.length;j++){
				/*
				if(matrix[i][j]==0){
					new Zero(i, j, nbZeroInColomn[i], nbZeroInLigne[i]);
				}
				*/
				if(matrix[i][j]==0){
					if(alreadySelectInColomnOrder[j]==0){
						
							//System.out.println("pour position "+i +":"+j+" libre");
							placeForNewZero=true;
							//System.out.println("valeur de J "+j +" valeur de i "+i);
							nbZeroInColomn[j]=numberOfZeroColumn(matrix, j,i);
							//System.out.println("nombre de 0 dans la cologne "+j +" : "+nbZeroInColomn[j]);
					}else{
						//System.out.println("COLOMN PRISE  : pour position "+i +":"+j+" un zero barre");
						//zeroBarred.add(new PointPerso(i, j));
					}
				}
			}
			if(placeForNewZero){
				int colomnSelect=getMinPositiveValue(nbZeroInColomn);
				//System.out.println("ZERO SELECTED : ligne "+i+" colomn "+colomnSelect);
				colomnsMarqued[colomnSelect]=true;
				sizeColomnsMarqued++;
				alreadySelectInColomnOrder[colomnSelect]=i+1;
				alreadySelectInLineOrder[i]=colomnSelect+1;
				nbOfZeroSelect++;
				
				for(int k=0;k<matrix.length;k++){
					if(k!=colomnSelect){
						if(matrix[i][k]==0){
							//System.out.println("LIGNE PRISE   : pour position "+i +":"+k+" un zero barre");
							//zeroBarred.add(new PointPerso(i,k));
							//System.out.println("contient deja ? :"+zeroBarred.contains(new PointPerso(i,k)));
						}
					}
				}
				//System.out.println("-----------------------");
				
			}else{
				
				/**
				 *	STEP  2.A  lignes without selected ZERO 
				 
				lignesMarqued[i]=true;
				sizeLignesMarqued++;
				ligneWithoutZeroSelected.add(i);
				System.out.println("LIGNE BARRE "+i+" without good ZERO");
				*/
			}
			
		}

	}
	
	
	/*
	 * Reduce all the value of an array of 1
	 */
	public static void reduceArrayOf1(int [] array){
		for(int i=0; i<array.length;i++){
			array[i]-=1;
		}
	}
	
	/*
	 * Do the sum of the selected value in the original Matrix
	 * the array of selected value contains the colomn position in ligne order of the best value to take
	 * 
	 */
	public static int SumSelectedValues(int [] values,int [][] matrix){
		int result=0;
		for(int i=0; i<values.length;i++){
			result+=matrix[values[i]][i];
		}
		
		return result;
	}
	
	public static int SumStarredValues(int [][] matrix){
		int result=0;
		for(int i=0;i<matrix.length;i++){
			for(int j=0;j<matrix.length;j++){
				if(PrimedStarredZero[i][j]==1){
					result+=matrix[i][j];
				}
			}
		}
		return result;
	}
	
	/*
	 * give a matrix representation of the content of the file specified
	 */
	public static int[][] getMatrix(String path) throws Exception {

		File file = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));

		
		String[] tab = br.readLine().replaceAll("^( )+","").split("\\s+");
		int size = tab.length;
		int[][] matrix = new int[size][size];
		int position = 0;
		
		for (int i = 0; i < tab.length; i++) {
			matrix[position][i] = Integer.parseInt(tab[i]);
		}
		position++;
		
		String line;
		while ( (line =br.readLine())!=null ) {
			
			tab = line.replaceAll("^( )+","").split("\\s+");
			if (tab.length != size) {
				throw new Exception("LIGNE DE MAUVAISE LONGUEUR");
			}
			for (int i = 0; i < tab.length; i++) {
				matrix[position][i] = Integer.parseInt(tab[i]);
			}
			position++;
			
		}
		if (position != size) {
			throw new Exception("ERREUR SUR NOMBRE DE LIGNE LUT");
		}
		br.close();
		
		return matrix;

	}
	
	/*
	 * Print on the standart output the matrix
	 */
	public static void showMatrix(int [][] matrix){
		System.out.println("");
		for(int i=0; i<matrix.length;i++){
			for(int j =0; j<matrix.length;j++){
				System.out.print(matrix[i][j]+" ");
			}
			System.out.println("");
		}
	}

}