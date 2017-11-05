
abstract class MinMaxAverage<T> {
	int nb;
	String name;
	T min;
	T max;
	T sum;
	boolean first;
	
	public MinMaxAverage(int k, String name) {
		this.nb = k;
		this.name = name;
		first = true;
	}

	@Override
	public String toString() {
		return name + "-> min :" + min + " | max : " + max;
	}
}

class DOUBLE_MinMaxAverage extends MinMaxAverage<Double> {

	public DOUBLE_MinMaxAverage(int k, String name) {
		super(k, name);
		min = (double) 0;
		max = (double) 0;
		sum = (double) 0;
	}

	public void add(double val) {
		if(first) {
			min =val;
			max = val;
			first=false;
		}else {
			if (val < min) {
				min = val;
			} else if (val > max) {
				max = val;
			}
		}
		sum += val;
	}

	@Override
	public String toString() {
		return super.toString() + "| average : " + sum /(double) nb;
	}
}

class INT_MinMaxAverage extends MinMaxAverage<Integer> {

	public INT_MinMaxAverage(int k, String name) {
		super(k, name);
		min = (int) 0;
		max = (int) 0;
		sum = (int) 0;
	}

	public void add(int val) {
		
		if (first) {
			min = val;
			max = val;
			first=false;
		} else {
			if (val < min) {
				min = val;
			} else if (val > max) {
				max = val;
			}
		}
		
		sum += val;
	}

	@Override
	public String toString() {
		return super.toString() + "| average : " + sum /(double) nb;
	}
}

