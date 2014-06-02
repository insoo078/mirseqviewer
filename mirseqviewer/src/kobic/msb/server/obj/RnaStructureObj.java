package kobic.msb.server.obj;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class RnaStructureObj {
	private double x;
	private double y;
	private String type;
	private Color color;
	private Ellipse2D.Double ellipse;
	private boolean isHover;
	private int position;

	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public boolean isHover() {
		return isHover;
	}
	public void setHover(boolean isHover) {
		this.isHover = isHover;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public Color getColor() {
		return this.color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setEllipse(Ellipse2D.Double ellipse) {
		this.ellipse = ellipse;
	}
	
	public Ellipse2D.Double getEllipse() {
		return this.ellipse;
	}
	
	public void drawEllipse(Graphics2D g2, Font font, Color bgColor, Color fontColor, double radius, boolean isDrawingEllipse) {
		g2.setColor( bgColor );
		this.ellipse.setFrame( this.ellipse.getX(), this.ellipse.getY(), radius*2, radius*2 );
		if( isDrawingEllipse )	g2.fill( this.ellipse );

		this.drawLabel(g2, font, fontColor);
	}

	private void drawLabel(Graphics2D g2, Font font, Color fontColor) {
		Rectangle ellipseBound = this.ellipse.getBounds();
		
		font = font.deriveFont( (float)(this.ellipse.getWidth()/1.5) );

		FontRenderContext frc = g2.getFontRenderContext();			
		Rectangle2D fontRect = font.getStringBounds(this.type, frc);

		LineMetrics lm = font.getLineMetrics(this.type, frc);

		g2.setFont( font );

		/**
		 *  To calculate the center position of ellipse drawing text on there
		 */
		double newX = ellipseBound.getMinX() + (ellipseBound.getWidth()/2)	- (fontRect.getWidth()/2);
		double newY = (float)(ellipseBound.getMinY() + (ellipseBound.getHeight() + fontRect.getHeight())/2 - lm.getDescent());

		g2.setColor( fontColor );
		g2.drawString( this.type, (float)newX, (float)newY );
		g2.setColor( Color.black );
	}
}
