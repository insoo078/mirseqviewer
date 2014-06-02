package kobic.msb.swing.panel.track;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import kobic.msb.common.SwingConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.Model;
import kobic.msb.system.config.ProjectConfiguration;

public abstract class TrackItem implements SwingConst{
	private int								id;

	private String							title;
	private double							trackHeight;
	private int								order;
	private int								trackType;
	private String							trackAttr;
	private int								movableType;
	private double							verticalLocation;
	
	private ProjectConfiguration			config;

	public TrackItem(ProjectConfiguration config) {
		this.config = config;
	}
	
	public ProjectConfiguration getProjectConfiguration() {
		return this.config;
	}
	
	public String getTrackAttr() {
		return trackAttr;
	}

	public void setTrackAttr(String trackAttr) {
		this.trackAttr = trackAttr;
	}

	public void setId( int id ) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}

	public double getVerticalLocation() {
		return verticalLocation;
	}

	public void setVerticalLocation(double verticalLocation) {
		this.verticalLocation = verticalLocation;
	}

	public int getMovableType() {
		return movableType;
	}
	public void setMovableType(int movableType) {
		this.movableType = movableType;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getTrackHeight() {
		return trackHeight;
	}
	public void setTrackHeight(double trackHeight) {
		this.trackHeight = trackHeight;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getTrackType() {
		return trackType;
	}
	public void setTrackType(int trackType) {
		this.trackType = trackType;
	}

	public abstract String getRid();
	public abstract void draw(Graphics2D g2, Rectangle2D.Double baseRect);
	
	protected Color getColorByNucleotide( String nucleotide ) {
		ProjectConfiguration config = this.getProjectConfiguration();
		if( nucleotide.toUpperCase().equals("A") )
			return config.getAdenosineColor();
		else if( nucleotide.toUpperCase().equals("T") || nucleotide.toUpperCase().equals("U") )
			return config.getThymidineColor();
		else if( nucleotide.toUpperCase().equals("G") )
			return config.getGuanosineColor();
		else if( nucleotide.toUpperCase().equals("C") )
			return config.getCytidineColor();
		return config.getUnkowonNucleotideColor();
	}
	
	public static Color getColorByNucleotideFixed( String nucleotide ) {
		if( nucleotide.toUpperCase().equals("A") )
			return new Color(195, 255, 166);
		else if( nucleotide.toUpperCase().equals("T") || nucleotide.toUpperCase().equals("U") )
			return new Color(255, 170, 180);
		else if( nucleotide.toUpperCase().equals("G") )
			return new Color(255, 240, 77);
		else if( nucleotide.toUpperCase().equals("C") )
			return new Color(125, 191, 255);
		return new Color(205, 83, 255);
	}

	public void drawHeader(Graphics2D g2) {
		g2.setFont( this.config.getSystemBoldFont() );
		g2.setColor(Color.black);
		FontRenderContext frc = g2.getFontRenderContext();
		Rectangle2D.Double headerScaleRect = new Rectangle2D.Double(0, this.verticalLocation, this.config.getOffset() - (SwingConst.TRACK_HEIGHT/2), this.trackHeight);
		
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		Point2D.Double scaleLabelPosition = SwingUtilities.getLabelPositionOnRectangle(frc, g2.getFont(), headerScaleRect, this.getTitle(), SwingUtilities.ALIGN_RIGHT);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawString(this.getTitle(), (float)scaleLabelPosition.getX(), (float)scaleLabelPosition.getY());

		g2.setFont( this.config.getSystemFont() );
	}
	
	public abstract void setBaseDistanceToView(Model model);
}