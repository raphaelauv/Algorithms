import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GraphPerso {

	public static BufferedReader getFile(String file) throws NumberFormatException, IOException {

		BufferedReader br = new BufferedReader(new FileReader(file));
		
		return br;
	}

}
