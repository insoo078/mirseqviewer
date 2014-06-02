package kobic.msb.swing.comparator;

public class SortPairWithTie implements Comparable<SortPairWithTie> {
	private int originalIndex;
	private double value;
	private double secondValue;
	private boolean isDecreasing;

	public SortPairWithTie(double value, double secValue, int originalIndex, boolean isDecerasing) {
		this.value = value;
		this.secondValue = secValue;
		this.originalIndex = originalIndex;
		this.isDecreasing	= isDecerasing;
	}

	@Override
	public int compareTo(SortPairWithTie o) {
		int compVal = 0;
		if( this.isDecreasing ) {
			compVal = Double.compare(o.getValue(), value);
			if( compVal == 0 ) {
				compVal = Double.compare(o.get2ndValue(), secondValue);
			}
			return compVal;
		}
		compVal = Double.compare(value, o.getValue());
		if( compVal == 0 )
			compVal = Double.compare(secondValue, o.get2ndValue());
		return compVal;
	}

	public int getOriginalIndex() {
		return originalIndex;
	}

	public double getValue() {
		return value;
	}
	
	public double get2ndValue() {
		return this.secondValue;
	}
}