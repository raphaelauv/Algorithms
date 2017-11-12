import java.io.BufferedReader;
import java.io.IOException;

class IncorrectArgs extends Exception{
}

public final class ManageInput{
	
	protected static void printMemory(String msg) {
		System.out.println(msg+" | Mémoire allouée : " +
		(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + "octets");
	}
	
	
	public static boolean parseAndFillGraph2(Graph graph, BufferedReader file,boolean oriented) throws IOException {
		String line = "";
		Long nbLine = 0l;
		String[] arrayOfLine;
		int actualId;
		int actualIdNeighbour;
		
		while ((line =file.readLine()) != null) {
			nbLine++;
			if (line.length()==0 || line.charAt(0) == '#') {
				continue;
			}
			
			arrayOfLine = line.split("\\s");
			if(arrayOfLine.length!=2) {
				System.out.println("ERREUR ligne "+nbLine+" format Invalide");
				return false;
			}

			//System.out.println(arrayOfLine[0] +" "+ arrayOfLine[1]);
			try {
				actualId = Integer.parseInt(arrayOfLine[0]);
				actualIdNeighbour = Integer.parseInt(arrayOfLine[1]);
				
				graph.addEdgeModeArray(actualId, actualIdNeighbour);
				//graph.addEdge(actualId, actualIdNeighbour,oriented);
			}catch(NumberFormatException e) {
				System.out.println("ERREUR ligne "+nbLine+" format Invalide");
				return false;
			}
			
		}	
		return true;

	}
	
	public static void incorrectArgs() {
		System.out.println("arguments incorect");
        System.out.println("Pour exécuter :\njava RandomGraph -e [n] [p] [OutputFileName] [k] [o] ");
        System.out.println("java RandomGraph -b [d] [n0] [n] [OutputFileName] [k] [o]");
        System.out.println("------------------\n[k] is optional , number of repetition to get Min , MAx and Average result\n" + 
        		"[o] is optional ( by default non oriented , with o it's oriented graph)");
        System.out.println("exemple : ");
        System.out.println("e 10 0.5 outFile1 10 o\nd 3 10 20 outFile2 1 o");
	}
	
	
	public static int getInt(String s) throws NumberFormatException{
		return Integer.parseInt(s);	
	}
	
	public static double getDouble(String s) throws NumberFormatException{
		return Double.parseDouble(s);
	}
	
	
	public static void correctArgs()throws IncorrectArgs {
		if(RandomGraph.flag_ask==RandomGraph.flag_ERDOS) {
			if(RandomGraph.proba_ASK<0 || RandomGraph.proba_ASK>1) {
				throw new IncorrectArgs();
			}
			if(RandomGraph.nbVertex_ASK<0) {
				throw new IncorrectArgs();
			}
		}else if(RandomGraph.flag_ask==RandomGraph.flag_ERDOS) {
			if(RandomGraph.d_ASK<0 ||RandomGraph.d_ASK>RandomGraph.n0_ASK || RandomGraph.n0_ASK > RandomGraph.nbVertex_ASK ) {
				throw new IncorrectArgs();
			}
		}else {
			
		}
	}
	
	public static void analyseArgs(String[] args) throws NumberFormatException, ArrayIndexOutOfBoundsException, IncorrectArgs {

		if (args.length < 1) {
			throw new IncorrectArgs();
		}
		
		int positionInArgs = 0;

		if (args[0].equals("e")) {
			if (args.length < 3 + 1) {
				throw new ArrayIndexOutOfBoundsException();
			}
			RandomGraph.flag_ask = RandomGraph.flag_ERDOS;
			RandomGraph.nbVertex_ASK = getInt(args[1]);
			RandomGraph.proba_ASK = getDouble(args[2]);
			positionInArgs = 3;
		} else if (args[0].equals("b")) {
			if (args.length < 5 + 1) {
				throw new ArrayIndexOutOfBoundsException();
			}
			RandomGraph.flag_ask = RandomGraph.flag_BARABASI;
			RandomGraph.d_ASK = getInt(args[1]);
			RandomGraph.n0_ASK = getInt(args[2]);
			RandomGraph.nbVertex_ASK = getInt(args[3]);
			positionInArgs = 4;
		} else {
			throw new IncorrectArgs();
		}
		correctArgs();
		
		RandomGraph.outputFileName = args[positionInArgs];
		positionInArgs++;
		

		if (args.length > positionInArgs) {
			RandomGraph.k_ASK = getInt(args[positionInArgs]);
			positionInArgs++;
		}else {
			RandomGraph.k_ASK=1;
		}

		if (args.length > positionInArgs) {
			if (args[positionInArgs].equals("o")) {
				RandomGraph.oriented = true;
				positionInArgs++;
			}else {
				throw new IncorrectArgs();
			}
			
			if(args.length>positionInArgs) {
				throw new IncorrectArgs();
			}
		}
		
	}
}
