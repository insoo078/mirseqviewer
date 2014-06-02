package kobic.msb.swing.control;

import javax.swing.event.ChangeListener;

public interface JMagnifierRangeModel {
	public static class Range {
		private boolean isDiscrete;

		private double weight;

		private String name;

		public Range(boolean isDiscrete, double weight) {
			this(null, isDiscrete, weight);
		}

		public Range(String name, boolean isDiscrete, double weight) {
			this.name = name;
			this.isDiscrete = isDiscrete;
			this.weight = weight;
		}

		public Range(Range range) {
			this(range.name, range.isDiscrete, range.weight);
		}

		public boolean isDiscrete() {
			return isDiscrete;
		}

		public double getWeight() {
			return weight;
		}

		@Override
		public String toString() {
			String name = (this.name == null) ? "Range" : this.name;
			if (this.isDiscrete)
				return "{DSC}" + name + " [" + this.hashCode() + "]";
			else
				return "{CTG}" + name + " [" + this.hashCode() + "] " + this.weight;
		}
	}

	public static class Value {
		public Range range;

		public double rangeFraction;

		public Value(Range range, double rangeFraction) {
			this.range = range;
			this.rangeFraction = rangeFraction;
		}

		public Value(Value value) {
			this(value.range, value.rangeFraction);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Value) {
				Value value2 = (Value) obj;
				return ((this.range == value2.range) && (this.rangeFraction == value2.rangeFraction));
			}
			return false;
		}

		@Override
		public String toString() {
			return this.range.toString() + ":" + this.rangeFraction;
		}
	}

	public Value getValue();

	public void setValue(Value value);

	public void setRanges(Range... range);

	public int getRangeCount();

	public Range getRange(int rangeIndex);

	/**
	 * This attribute indicates that any upcoming changes to the value of the
	 * model should be considered a single event. This attribute will be set to
	 * true at the start of a series of changes to the value, and will be set to
	 * false when the value has finished changing. Normally this allows a
	 * listener to only take action when the final value change in committed,
	 * instead of having to do updates for all intermediate values.
	 * <p>
	 * 
	 * @param b
	 *            true if the upcoming changes to the value property are part of
	 *            a series
	 */
	void setValueIsAdjusting(boolean b);

	/**
	 * Returns true if the current changes to the value property are part of a
	 * series of changes.
	 * 
	 * @return the valueIsAdjustingProperty.
	 * @see #setValueIsAdjusting
	 */
	boolean getValueIsAdjusting();

	/**
	 * Adds a ChangeListener to the model's listener list.
	 * 
	 * @param x
	 *            the ChangeListener to add
	 * @see #removeChangeListener
	 */
	void addChangeListener(ChangeListener x);

	/**
	 * Removes a ChangeListener from the model's listener list.
	 * 
	 * @param x
	 *            the ChangeListener to remove
	 * @see #addChangeListener
	 */
	void removeChangeListener(ChangeListener x);
}
