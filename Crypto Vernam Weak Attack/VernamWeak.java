import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;


/*
 * Keep a score and a value of underKey
 */
class Couple implements Comparable<Couple>{
	double score;
	char value;

	public Couple(double score, char value) {
		this.score = score;
		this.value = value;
	}

	@Override
	public int compareTo(Couple arg0) {
		if(arg0.score>this.score) {
			return -1;
		}else if(arg0.score<this.score) {
			return 1;
		}else {
			this.score+=-0.00000001d; //keep very close result
			return 1;
		}
	}
}

class VernamWeak {
	
	public static void main(String[] args) throws IOException {
		Key k;
		
		//k= new Key(new char[] {1,2,3,4});		
		k = g();
		System.out.println(k);
		
		/*
		String rst = c(k, "hello i will be in vacation tomorow");
		System.out.println(rst);
		String decoded = c(k, rst); 
		System.out.println(decoded);
		*/
		
		String englishtxt = readFile("EnglishTxt",StandardCharsets.UTF_8);
		
		if(txtIsInASCCI(englishtxt)) {
			System.out.println("is lATIN ALPHABET TEXTE");
		}else {
			System.out.println("is not lATIN ALPHABET TEXTE");
			return;
		}

		HashSet<String> hash = getHashSet(1000, "wordsEN.txt");
		String encodedText = c(k,englishtxt);
	
		int [][] allKeys = findKeys(encodedText);
		Key bestFind = getBetterKeyPossible(allKeys,encodedText,hash);
		
		String decodedText = c(bestFind,encodedText);
		float resultFind = getEnglishScore(hash, decodedText);
		System.out.println("score find "+resultFind+ " whith "+bestFind);
		System.out.println("----------------------------------------------");
		System.out.println(decodedText);
		
	}
	
	
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	static float[] frequency = { 8.167f, 1.492f, 2.782f, 4.253f, 12.702f, 2.228f, 2.015f, 6.094f, 6.966f, 0.153f, 0.772f,
			4.025f, 2.406f, 6.749f, 7.507f, 1.929f, 0.095f, 5.987f, 6.327f, 9.056f, 2.758f, 0.978f, 2.360f, 0.150f,
			1.974f, 0.074f };

	static int sizeAlphabet = 26;
	static int limitASSCI_hight = 128;
	static int limitASSCI_low = 31;
	static int ASSCI_A = 65;
	static int ASSCI_Z = 90;
	static int ASSCI_a = 97;
	static int ASSCI_z = 122;
	
	
	//not coherent
	static int ASSCII_GUILL_MIN =8216;
	static int ASSCII_GUILL_MAX =8220;
	
	public static int lenghKey = 4;
	
	private static int keepMaxValues = 20;
	
	public static String c(Key k, String t) {
		StringBuilder b = new StringBuilder(t.length());
		for (int i = 0; i < t.length(); i++) {
			b.append((char) (t.charAt(i) ^ k.get(i % lenghKey)));
		}
		return b.toString();
	}
		
	public static boolean txtIsInASCCI(String txt) {
		char tmp;
		
		for(int i=0; i<txt.length();i++) {
			tmp =txt.charAt(i); 
			if(tmp >limitASSCI_hight) {
				if(tmp< ASSCII_GUILL_MIN && tmp>ASSCII_GUILL_MAX) {
					System.out.println(" char not assci "+(int) tmp + " "+ tmp);
					return false;
				}
				
			}
		}
		
		return true;
	}
	
	
	/*
	 * return NULL if output is not in ALPHABET
	 */
	public static String cWithPosition(String t,char value,int position) {
		StringBuilder b = new StringBuilder((t.length()/ lenghKey) +1);
		char tmp;
		int result;
		for (int i = position; i < t.length(); i+=lenghKey) {
			tmp =(char) (t.charAt(i) ^ value);
			result=isInAlphabet(tmp);
			if(result==-2) {
				return null;
			}else if(result!=-1){
				b.append(tmp);	
			}
			
		}
		return b.toString();
	}
	
	
	public static Key g() {
		return new Key();

	}

	/*
	 * return -2 if not good
	 * -1 if not char in alphabet but ok
	 * else position in array
	 */
	static int isInAlphabet(char a) {	
		if (a<ASSCI_A) {
			return -1;
		}
		
		if( ASSCII_GUILL_MIN<=a && a<=ASSCII_GUILL_MAX) {
			return -1;
		}
		
		else if(a>=ASSCI_A && a<=ASSCI_Z ){
			return a-ASSCI_A;
		}else if(a>=ASSCI_a && a<=ASSCI_z) {
			return a-ASSCI_a;
		}else {
			//System.out.println("val "+(int)a);
			return -2;
		}
	}
	
	static float [] getFrequencyFromTXT(String txt) {
		
		float [] frequency = new float[sizeAlphabet];
		
		int [] countPresence = new int[sizeAlphabet];
		char tmp;
		
		
		int position=0;
		
		int sizeTxt = txt.length();
		
		for(int i=0; i<sizeTxt;i++) {
			tmp =txt.charAt(i);
			position=isInAlphabet(tmp);
			if(position==-2) {
				return null;
			}else if(position==-1){
				
			}else {
				countPresence[position]++;
			}
		}

		
		for(int i =0 ; i<sizeAlphabet;i++) {
			frequency[i] =  (countPresence[i] *100) / (float) sizeTxt; 
		}
		
		return frequency;
	}
	
	static double getScoreFrequency(float [] frequencyFind) {
		float val = 0;
		
		//System.out.println(Arrays.toString(frequencyFind));
		//System.out.println(Arrays.toString(frequency));
		
		for(int i =0; i<frequency.length;i++) {
			val+= Math.abs(( frequencyFind[i] - frequency[i]));
		}
		
		return val/ (double) sizeAlphabet;
	}
	
	
	
	
	private static void addToThree(Couple cpl, TreeSet<Couple> set) {
		
		    if (set.size () < keepMaxValues) {
		       set.add (cpl);
		    } else {
		       Couple first = set.first();
		       if (first.score < cpl.score) {
		          set.pollFirst();
		          set.add (cpl);
		       }
		    }
		}
	
	
	public void addToArray(Couple cpl , Couple[] arr) {
		
		Arrays.sort(arr);
		
		for(int i=arr.length-1; i>1 ;i--) {
			
			if(arr[i-1].score<cpl.score) {
				
			}
			
		}
	}
	
	public static void printDoubleArray(int [][] allKeys) {
		System.out.println("-------------");
		for(int [] tmp : allKeys) {
			System.out.println(Arrays.toString(tmp));	
		}
		System.out.println("-------------");
	}
	
	public static int[][] getArrayFromArrayTreeSet(TreeSet<Couple> [] arrayOfBestUnderKeys){
		
		int [][] allKeys = new int[lenghKey][];
		
		for(int i =0; i<lenghKey;i++) {
			//System.out.print("[");
			allKeys[i]=new int[arrayOfBestUnderKeys[i].size()];
			int j=0;
			for(Couple a: arrayOfBestUnderKeys[i]) {
				//System.out.print((int)a.value+" ");
				
				allKeys[i][j]=a.value;
				j++;
			}
			//System.out.println("]");
		}
		
		return allKeys;
	}
	
	public static int[][] findKeys(String txt) {
		
		TreeSet<Couple> [] arrayOfBestUnderKeys = new TreeSet[lenghKey];
		for(int i=0; i<lenghKey;i++) {
			arrayOfBestUnderKeys[i]= new TreeSet<Couple>();
		}
		
		
		String txtDecode="";
		char key[] = new char[lenghKey];
		float [] arrayFreq;
		double score;
		
		for(int i=0; i<lenghKey;i++) {
			for(int j=0; j<Character.MAX_VALUE; j++) {
				key[i]=(char)j;
				//txtDecode = c(k, txt);
				txtDecode = cWithPosition(txt,(char)j,i);

				if(txtDecode!=null) {
					//System.out.println("finded ");	
				}
				
				if(txtDecode==null) {
					//System.out.println("BAD LOKKING");
					//System.out.println(k);
					continue;
				}
				
				//System.out.println("GOOD LOKKING");
				//System.out.println(txtDecode);
				
				arrayFreq = getFrequencyFromTXT(txtDecode);
				
				if(arrayFreq==null) {
					//System.err.println("HERE");
					continue;
				}
				
				score = getScoreFrequency(arrayFreq);
				
				//System.out.println("pour "+i+"-> "+(int)k.array[i]+" score "+score);
				
				addToThree(new Couple(score,(char)j),arrayOfBestUnderKeys[i]);
				
			}
			
		}
		
		int [][] allKeys= getArrayFromArrayTreeSet(arrayOfBestUnderKeys);
		printDoubleArray(allKeys);
		return allKeys;
	}



	
	public static Key getBetterKeyPossible(int [][] allKeys, String encodedText ,HashSet<String> hash) {
		
		float resultFind;
		float bestResultFind =0f;
		Key bestKey= new Key(new char[lenghKey]);
		
		
		char[]tmpArray=new char[lenghKey];
		Key tmpKey=new Key(tmpArray);
		
		int [] actualIndexs = new int[lenghKey];
		
		boolean notFinish=true;
		
		
		int cmp=0;
		
		String decodedText ="";
			
		while(notFinish) {

			for(int i =0; i<tmpArray.length;i++) {
				tmpArray[i]=(char) allKeys[i][actualIndexs[i]];
			}
			
			//System.out.println(Arrays.toString(actualIndexs));
			//System.out.println(tmpKey);
			
			do {
				if(allKeys[cmp].length > actualIndexs[cmp] +1) {
					actualIndexs[cmp]++;
					break;
				}
				else {
					actualIndexs[cmp]=0;
					cmp++;
					if(cmp==lenghKey) {
						break;
					}
				}
			}while(true);
			if(cmp==lenghKey) {
				break;
			}
			cmp=0;
			
			decodedText = c(tmpKey,encodedText);
			
			resultFind = getEnglishScore(hash, decodedText);
			//System.out.println(resultFind+" "+tmpKey);
			if(resultFind>bestResultFind) {
				bestResultFind=resultFind;
				System.arraycopy(tmpArray,0,bestKey.array,0,tmpArray.length);
			}
			
		}
		
		return bestKey;
		
	}
	
	public static float getEnglishScore(HashSet<String> hash , String decodedText ) {
		String[] arrayOfTxt;
		int nbPresent = 0;
		arrayOfTxt = decodedText.split(" ");
		nbPresent = 0;
		
		int sizeFind=0;
		
		String tmp;
		for (int i = 0; i < arrayOfTxt.length; i++) {
			
			tmp = arrayOfTxt[i].toLowerCase();
			
			if (hash.contains(tmp)) {
				//System.out.println("present "+tmp);
				nbPresent++;
				sizeFind+=tmp.length();
			}else {
				//System.out.println("NOT present "+tmp);
			}
		}

	
		return (nbPresent*100) / (float) arrayOfTxt.length;	
	}
	
	public static HashSet<String> getHashSet(int nbWords, String name) throws IOException {

		File file = new File(name);
		 

		HashSet<String> hash = new HashSet<String>(nbWords);
		BufferedReader bf = new BufferedReader(new FileReader(file));
		String line;

		while ((line = bf.readLine()) != null) {
			hash.add(line);
		}
		bf.close();
		return hash;
	}
}

class Key {

	char[] array;

	public char get(int i) {
		return array[i];
	}

	@Override
	public String toString() {
		String a = "key : [";
		for (int i = 0; i < VernamWeak.lenghKey; i++) {
			if (i > 0) {
				a += ", ";
			}
			a += (int) array[i];
		}
		a += "]";
		return a;
	}

	public Key(char []array) {
		this.array=array;
	}
	
	public Key(char i, char i2, char i3 , char i4) {
		array[0]=i;
		array[1]=i2;
		array[2]=i3;
		array[3]=i4;
	}
	
	public Key() {

		array =new char[VernamWeak.lenghKey];
		for (int i = 0; i < VernamWeak.lenghKey; i++) {

			array[i] = (char) ThreadLocalRandom.current().nextInt(0, Character.MAX_VALUE + 1);
			// System.out.println("Character Value: " + (int) (array[i]));

		}

	}
}
