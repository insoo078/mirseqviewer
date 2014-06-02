package kobic.msb.swing.panel.profile.obj;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import kobic.msb.common.util.SwingUtilities;
import kobic.msb.system.config.ProjectConfiguration;


public class ProfileTrack {
	private Object					label;
	private List<ProfileItem>		columns;
	private ProjectConfiguration 	config;
	private int						order;

	public ProfileTrack(int order, Object label, ProjectConfiguration configuration) {
		this.order = order;
		this.label = label;
		this.config = configuration;
		this.columns = new ArrayList<ProfileItem>();
	}
	
	public double getTrackWidth() {
		return this.config.getExpressionProfileLabelOffset() + (this.columns.size() * this.config.getExpressionProfileBlockWidth());
	}

	public void add( ProfileItem item ) {
		this.columns.add( item );
	}
	
	public void draw(Graphics2D g2) {
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		this.drawHeader(g2);

		for(int i=0; i<this.columns.size(); i++) {
			this.columns.get(i).draw( g2 );
		}
	}

	public void drawHeader( Graphics2D g2 ) {
		Font originalFont = g2.getFont();
		Font systemFont = this.config.getSystemFont().deriveFont( Font.PLAIN, (float)this.config.getExpressionProfileBlockHeight()/1.5f);

		g2.setFont( systemFont );
		g2.setColor(Color.black);
		FontRenderContext frc = g2.getFontRenderContext();
		Rectangle2D.Double headerScaleRect = new Rectangle2D.Double(0, this.order * this.config.getExpressionProfileBlockHeight(), this.config.getExpressionProfileLabelOffset(), this.config.getExpressionProfileBlockHeight() );

		Point2D.Double scaleLabelPosition = SwingUtilities.getLabelPositionOnRectangle(frc, g2.getFont(), headerScaleRect, this.label.toString(), SwingUtilities.ALIGN_RIGHT);
		g2.drawString(this.label.toString(), (float)scaleLabelPosition.getX(), (float)scaleLabelPosition.getY());

		g2.setFont( originalFont );
	}
}