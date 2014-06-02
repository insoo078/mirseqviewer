package kobic.msb.swing.comparator;

public class SortPair implements Comparable<SortPair> {
	private int originalIndex;
	private double value;
	private boolean isDecreasing;

	public SortPair(double value, int originalIndex, boolean isDecerasing) {
		this.value = value;
		this.originalIndex = originalIndex;
		this.isDecreasing	= isDecerasing;
	}

	@Override
	public int compareTo(SortPair o) {
		if( this.isDecreasing )
			return Double.compare(o.getValue(), value);
		return Double.compare(value, o.getValue());
	}

	public int getOriginalIndex() {
		return originalIndex;
	}

	public double getValue() {
		return value;
	}
}