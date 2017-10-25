public class Auxiliaire {
	public static String numInThree(int n) {
		if (n < 0) {
			if (n > -10)
				return (n + " ");
			if (n > -100)
				return Integer.toString(n);
			else
				return " - ";
		} else {
			if (n < 10)
				return (" " + n + " ");
			if (n < 100)
				return (" " + n);
			if (n < 1000)
				return Integer.toString(n);
			else
				return " + ";
		}
	}
}
