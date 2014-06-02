package kobic.msb.swing.plot;

public class Scale {
	private double unit;
	private double from;
	private double to;
	private int direction;
	
	public static int DIR_VERTICAL = 1;
	public static int DIR_HORIZONtAL = 2;
	
	public double getUnit() {
		return this.unit;
	}
	
	public double getFrom() {
		return this.from;
	}
	
	public double getTo() {
		return this.to;
	}
	
	public int getDirection() {
		return this.direction;
	}
}
