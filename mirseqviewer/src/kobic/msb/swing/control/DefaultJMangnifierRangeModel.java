package kobic.msb.swing.control;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultJMangnifierRangeModel implements JMagnifierRangeModel {
	protected EventListenerList listenerList = new EventListenerList();

	private boolean isAdjusting = false;

	private Value value = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.flamingo.slider.FlexiRangeModel#addChangeListener(javax.swing.event.ChangeListener)
	 */
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.flamingo.slider.FlexiRangeModel#removeChangeListener(javax.swing.event.ChangeListener)
	 */
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	/**
	 * Runs each <code>ChangeListener</code>'s <code>stateChanged</code>
	 * method.
	 */
	protected void fireStateChanged() {
		ChangeEvent event = new ChangeEvent(this);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				((ChangeListener) listeners[i + 1]).stateChanged(event);
			}
		}
	}

	/**
	 * Returns an array of all the change listeners registered on this
	 * <code>DefaultBoundedRangeModel</code>.
	 * 
	 * @return all of this model's <code>ChangeListener</code>s or an empty
	 *         array if no change listeners are currently registered
	 * 
	 * @see #addChangeListener
	 * @see #removeChangeListener
	 */
	public ChangeListener[] getChangeListeners() {
		return (ChangeListener[]) listenerList
				.getListeners(ChangeListener.class);
	}

	private Range[] ranges;

	public DefaultJMangnifierRangeModel() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.flamingo.slider.FlexiRangeModel#setRanges(org.jvnet.flamingo.slider.FlexiRangeModel.Range[])
	 */
	public void setRanges(Range... ranges) {
		this.ranges = new Range[ranges.length];
		// defensive array copy - so that changes in the application code
		// will not be reflected in the model
		for (int i = 0; i < ranges.length; i++) {
			this.ranges[i] = ranges[i];
		}
	}

	public int getRangeCount() {
		return this.ranges.length;
	}
	
	public Range getRange(int rangeIndex) {
		return this.ranges[rangeIndex];
	}
	
	protected boolean isValueLegal(Value value) {
		// try to find the value range in the range array
		boolean isRangeLegal = false;
		for (Range range : this.ranges) {
			if (range.equals(value.range)) {
				isRangeLegal = true;
			}
		}
		if (!isRangeLegal)
			return false;

		// check the range fraction
		if ((value.rangeFraction < 0.0) || (value.rangeFraction > 1.0))
			return false;

		// check discrete range
		if (value.range.isDiscrete()) {
			if ((value.rangeFraction != 0.0) && (value.rangeFraction != 1.0))
				return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.flamingo.slider.FlexiRangeModel#getValue()
	 */
	public Value getValue() {
		if (this.value == null)
			return null;
		return new Value(this.value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.flamingo.slider.FlexiRangeModel#setValue(org.jvnet.flamingo.slider.FlexiRangeModel.Value)
	 */
	public void setValue(Value value) throws IllegalArgumentException {
		if (value == null) {
			throw new IllegalArgumentException("Can't pass null value");
		}
		if (!value.equals(this.value)) {
			if (!this.isValueLegal(value))
				throw new IllegalArgumentException(
						"Value is not legal for the model");
			// create copy
			this.value = new JMagnifierRangeModel.Value(value);
			this.fireStateChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.flamingo.slider.FlexiRangeModel#setValueIsAdjusting(boolean)
	 */
	public void setValueIsAdjusting(boolean b) {
		if (this.isAdjusting != b) {
			this.isAdjusting = b;
			this.fireStateChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.flamingo.slider.FlexiRangeModel#getValueIsAdjusting()
	 */
	public boolean getValueIsAdjusting() {
		return isAdjusting;
	}
}
