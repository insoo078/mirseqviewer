package kobic.msb.server.obj;

import java.awt.Color;

public class RnaSecondaryStructureObj implements java.io.Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String type;
	private long start;
	private long end;
	private Color color;
	
	public RnaSecondaryStructureObj(String name, long start, long end, Color color ) {
		this(name, "", start, end, color);
	}
	
	public RnaSecondaryStructureObj(String name, String id, long start, long end, Color color ) {
		this.name	= name;
		this.start	= start;
		this.end	= end;
		this.color	= color;
		this.type	= id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public String getType() {
		return this.type;
	}
	public void setType(String id) {
		this.type = id;
	}
}
