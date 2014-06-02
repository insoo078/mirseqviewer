package kobic.msb.server.obj;

import java.awt.Color;
import java.awt.geom.Point2D;

public class NucleotideObject  implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String			nucleotideType;
	private Point2D.Double	coordinate;
	private int				position;
	private Color			color;
	private boolean			isDifferentWithRef;
	private byte			baseQuality;

	public NucleotideObject() {
		this.nucleotideType = null;
		this.coordinate = null;
		this.position = -1;
		this.color = null;
		this.isDifferentWithRef = false;
	}
	public NucleotideObject( String nucleotideType, Point2D.Double coordinate, int pos, Color color, boolean isDifferentWithRef ) {
		this.nucleotideType = nucleotideType;
		this.coordinate = coordinate;
		this.position = pos;
		this.isDifferentWithRef = isDifferentWithRef;
		this.color = color;
	}

	public String getNucleotideType() {
		return nucleotideType;
	}
	public void setNucleotideType(String nucleotideType) {
		this.nucleotideType = nucleotideType;
	}
	public Point2D.Double getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(Point2D.Double coordinate) {
		this.coordinate = coordinate;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int index) {
		this.position = index;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Color getColor() {
		return this.color;
	}
	public boolean isDifferentWithRef() {
		return isDifferentWithRef;
	}
	public void setDifferentWithRef(boolean isDifferentWithRef) {
		this.isDifferentWithRef = isDifferentWithRef;
	}
	
	public byte getBaseQuality() {
		return baseQuality;
	}
	public void setBaseQuality(byte baseQuality) {
		this.baseQuality = baseQuality;
	}
	public NucleotideObject clone() {
		return new NucleotideObject(this.nucleotideType, this.coordinate, this.position, this.color, this.isDifferentWithRef );
	}
}