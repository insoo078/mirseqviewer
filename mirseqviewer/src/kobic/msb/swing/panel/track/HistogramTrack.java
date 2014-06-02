package kobic.msb.swing.panel.track;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import kobic.com.util.Utilities;
import kobic.msb.server.model.Model;
import kobic.msb.system.config.ProjectConfiguration;


public class HistogramTrack extends TrackItem{
	private List<Rectangle2D.Double>	histogramResult;
	
	public HistogramTrack( ProjectConfiguration config ) {
		super( config );

		this.histogramResult	= null;
	}
	
//	public void doCalculatingPosition( Model model ) {
//		this.histogramResult = this.getHistrogramRectList( model.getHistogramBinVector() );
//	}
	
	@Override
	public void setBaseDistanceToView(Model model) {
		// TODO Auto-generated method stub
		this.histogramResult = this.getHistrogramRectList( model.getHistogramBinVector() );
	}

	private List<Rectangle2D.Double> getHistrogramRectList( long[] histogramBinVector ) {
		List<Rectangle2D.Double> list = new ArrayList<Rectangle2D.Double>();

		double drawingBinHeight = (this.getProjectConfiguration().getBlockHeight() * 4);

		double unitY = (drawingBinHeight - this.getProjectConfiguration().getMargin()) / Utilities.getMax( histogramBinVector );

		for(int i=0; i<histogramBinVector.length; i++) {
			double x = this.getProjectConfiguration().getOffset() + this.getProjectConfiguration().getMargin() + (i* this.getProjectConfiguration().getBlockWidth());

			double height	= (unitY * histogramBinVector[i]);
			double y = drawingBinHeight + this.getVerticalLocation()  - height;

			Rectangle2D.Double labelRect = new Rectangle2D.Double( x+1,  y, this.getProjectConfiguration().getBlockWidth()-1, height );

			list.add( labelRect );
		}
		return list;
	}

	@Override
	public void draw( Graphics2D g2, Rectangle2D.Double baseRect ) {
		this.drawHeader(g2);
		// TODO Auto-generated method stub

		if( this.histogramResult != null ) {
			for(int i=0; i<this.histogramResult.size(); i++) {
				Rectangle2D.Double rect = this.histogramResult.get(i);
				
				if( rect.getMinX() < baseRect.getMinX() || rect.getMaxX() > baseRect.getMaxX() )	continue;

				GradientPaint redtowhite = new GradientPaint( (int)rect.getX(), (int)rect.getY(), this.getProjectConfiguration().getHistogramBarColorStart(), (int)rect.getX(), (int)rect.getY() + 40, this.getProjectConfiguration().getHistogramBarColorEnd());
				g2.setPaint(redtowhite);
				g2.fill( rect );
//				g2.setColor( Color.LIGHT_GRAY );
//				g2.draw( rect );
			}
		}
	}
	
	@Override
	public String getRid() {
		return null;
	}
}
