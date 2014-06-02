package kobic.msb.swing.panel.track;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import kobic.com.util.Utilities;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.Model;
import kobic.msb.system.config.ProjectConfiguration;

public class ScaleTrack extends TrackItem{
	private long		baseDistanceToView;

	public ScaleTrack(ProjectConfiguration config) {
		super(config);
	}

	@Override
	public String getRid() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public long getBaseDistanceToView() {
		return baseDistanceToView;
	}
	
	public static double getRange( long baseDistanceToView ) {
		double _70P_range = baseDistanceToView * 0.7;

		return _70P_range;
	}

//	public void setBaseDistanceToView( long baseDistanceToView ) {
//		this.baseDistanceToView = baseDistanceToView;
//	}

	@Override
	public void setBaseDistanceToView( Model model ) {
		this.baseDistanceToView = model.getReferenceSequenceObject().getLength();
	}

	@Override
	public void draw(Graphics2D g2, Rectangle2D.Double baseRect ) {
		this.drawHeader(g2);

		// TODO Auto-generated method stub
		double drawing_length = this.getBaseDistanceToView() * this.getProjectConfiguration().getBlockWidth();

		double _70_per_range = drawing_length * 0.7;
		
		String baseLineLabel = "( " + Utilities.getNumberWithComma( (int)(this.baseDistanceToView * 0.7) ) + " bases )";
		FontRenderContext frc = g2.getFontRenderContext();

		Rectangle2D fontRect = g2.getFont().getStringBounds(baseLineLabel, frc);
		
		double half_width = (drawing_length - _70_per_range) / 2;

		Rectangle2D.Double baseLineRect = new Rectangle2D.Double( this.getProjectConfiguration().getMargin() + this.getProjectConfiguration().getOffset(), this.getVerticalLocation(), this.getBaseDistanceToView() * this.getProjectConfiguration().getBlockWidth(), this.getTrackHeight() );

		Rectangle2D.Double labelRect = new Rectangle2D.Double( baseLineRect.getCenterX() - (fontRect.getWidth()/2), this.getVerticalLocation(), fontRect.getWidth(), this.getTrackHeight() );
		
		double leftX	= (baseLineRect.getMinX() + half_width);
		double rightX	= (baseLineRect.getMaxX() - half_width);

		
		g2.setColor( new Color(9, 125, 199) );
		Area leftArrowLine = SwingUtilities.getArrowFromTo(
				new Point2D.Double( baseLineRect.getCenterX() - (labelRect.getWidth()/2),	baseLineRect.getCenterY())
			,	new Point2D.Double( leftX,													baseLineRect.getCenterY())
		);
		Area rightArrowLine = SwingUtilities.getArrowFromTo(
				new Point2D.Double( baseLineRect.getCenterX() + (labelRect.getWidth()/2),	baseLineRect.getCenterY())
			,	new Point2D.Double(rightX,													baseLineRect.getCenterY())
		);

		g2.fill( leftArrowLine );
		g2.fill( rightArrowLine );
		
		g2.setColor( Color.black );
		
		Line2D.Double left_line = new Line2D.Double( leftX, baseLineRect.getCenterY() - 5, leftX, baseLineRect.getCenterY() + 5 );
		Line2D.Double right_line = new Line2D.Double( rightX, baseLineRect.getCenterY() - 5, rightX, baseLineRect.getCenterY() + 5 );
		g2.draw( left_line );
		g2.draw( right_line );
		
		Point2D.Double labelPosition = SwingUtilities.getLabelPositionOnRectangle(frc, g2.getFont(), labelRect, baseLineLabel, SwingUtilities.ALIGN_CENTER);
		g2.drawString( baseLineLabel, (float)labelPosition.getX(), (float)labelPosition.getY());
	}

}
