package kobic.msb.server.model;

public class DescritiveStatisticsModel implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double mean;
	private double min;
	private double max;
	private double std;
	private double median;
	private double range;
	private double _1qt;
	private double _3qt;
	private double outlisterBoundary;
	
	public double getMean() {
		return mean;
	}
	public void setMean(double mean) {
		this.mean = mean;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getStd() {
		return std;
	}
	public void setStd(double std) {
		this.std = std;
	}
	public double getMedian() {
		return median;
	}
	public void setMedian(double median) {
		this.median = median;
	}
	public double getRange() {
		return range;
	}
	public void setRange(double range) {
		this.range = range;
	}
	public double get_1qt() {
		return _1qt;
	}
	public void set_1qt(double _1qt) {
		this._1qt = _1qt;
	}
	public double get_3qt() {
		return _3qt;
	}
	public void set_3qt(double _3qt) {
		this._3qt = _3qt;
	}
	public double getUpperOutlisterBoundary() {
		return this._3qt + outlisterBoundary;
	}
	public double getLowerOutlisterBoundary() {
		return this._1qt - outlisterBoundary;
	}
	public void setOutlisterBoundary(double outlisterBoundary) {
		this.outlisterBoundary = outlisterBoundary;
	}
}
