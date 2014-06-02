package kobic.msb.swing.plot;

import java.awt.Color;
import java.awt.Graphics2D;

@SuppressWarnings("unused")
public class BoxPlot {
	private double[] data;
	
	private Scale scale;

	private Color boxColor;
	private Color lineColor;
	private int shapeType;
	
	public BoxPlot(Scale scale) {
		this.scale = scale;
	}

	public void paint(Graphics2D g) {
		
	}
}
