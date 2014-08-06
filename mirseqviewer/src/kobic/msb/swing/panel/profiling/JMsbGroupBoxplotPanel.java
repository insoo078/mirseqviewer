package kobic.msb.swing.panel.profiling;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import kobic.com.edgeR.BasicFunctions;
import kobic.com.util.Utilities;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.DescritiveStatisticsModel;
import kobic.msb.system.engine.MsbEngine;

import org.apache.commons.math3.stat.StatUtils;

import weka.core.Instances;
import weka.gui.visualize.PrintablePanel;

public class JMsbGroupBoxplotPanel extends PrintablePanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected int							m_classIndex;
	private Instances						instances;
	private double							margin = 5f;
	private List<DescritiveStatisticsModel> statModel;
	private	int								hilightIndex;
	private boolean							isLogScaling;

	private double[][] data;

	public JMsbGroupBoxplotPanel() {
		this.isLogScaling	= false;
		this.instances		= null;
		this.data			= null;
		this.statModel		= new ArrayList<DescritiveStatisticsModel>();
		this.hilightIndex	= -1;
	}
	
	private double getMinBaseY() {
		double minBaseY = Double.MAX_VALUE;
		for(int idx=0; idx<this.instances.numAttributes();idx++) {			
			double baseY = Math.ceil( JMsbGroupBoxplotPanel.getBaseY( this.statModel.get(idx) )	);
			if( baseY < minBaseY )	minBaseY = baseY;
		}
		return minBaseY;
	}
	
	private double getMaxBaseY() {
		double maxBaseY = Double.MIN_VALUE;
		for(int idx=0; idx<this.instances.numAttributes();idx++) {			
			double baseY = Math.ceil( JMsbGroupBoxplotPanel.getBaseMaxY( this.statModel.get(idx) ) );
			if( baseY > maxBaseY )	maxBaseY = baseY;
		}
		return maxBaseY;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent( g );

		if( this.statModel != null ) {
			Graphics2D g2 = (Graphics2D)g;

			g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

			FontRenderContext frc = g2.getFontRenderContext();
			
			Rectangle2D.Double baseRect = new Rectangle2D.Double( this.margin, this.margin, (double)(this.getWidth() - 2*this.margin), (double)(this.getHeight() - 2*this.margin) );

			g2.draw( baseRect );
			
			Rectangle2D.Double labelRect = new Rectangle2D.Double( baseRect.getMinX(), 0, 70, 10 );
			int RULER_MARGIN = 10;
			
			Rectangle2D.Double wholeDrawRect = new Rectangle2D.Double( labelRect.getMaxX() + RULER_MARGIN, baseRect.getMinY() + RULER_MARGIN, baseRect.getWidth() - (labelRect.getMaxX() + RULER_MARGIN), baseRect.getHeight() - (2*RULER_MARGIN) );
			
			double width = wholeDrawRect.getWidth()/this.instances.numAttributes();
			
			double baseY = this.getMinBaseY();
			double baseMaxY = this.getMaxBaseY();

			double unit = wholeDrawRect.getHeight() / (baseMaxY - baseY);
			
			double interval = (baseMaxY - baseY) / 4;

			double tmp = baseY * unit;

			g2.setColor( Color.white );
			g2.fill( wholeDrawRect );

			g2.setFont( SwingUtilities.scaleFont( "9999999.0", labelRect, g2, g2.getFont() ) );

			for( double i=baseY; i<=baseMaxY; i+=interval ) {
				double oldPos = (i * unit) - tmp;
				double newPos = SwingUtilities.getMonitorYCoordinateSystem( oldPos, wholeDrawRect );
				
				g2.setColor( Color.gray );
				labelRect.setFrame( baseRect.getMinX(), newPos - (RULER_MARGIN/2), labelRect.getWidth(), labelRect.getHeight() );

				String label = SwingUtilities.getRealNumberString( i );
				Point2D.Double newLabelPosition = SwingUtilities.getLabelPositionOnRectangle(frc, g2.getFont(), labelRect, label, SwingUtilities.ALIGN_RIGHT);

				g2.drawString( label, (float)newLabelPosition.getX(), (float)newLabelPosition.getY());

				g2.setColor( Color.LIGHT_GRAY );
				Line2D.Double tmpLine = new Line2D.Double( labelRect.getMaxX(), newPos, wholeDrawRect.getMinX(), newPos);
				g2.draw( tmpLine );
			}

			for(int idx=0; idx<this.instances.numAttributes();idx++) {
				DescritiveStatisticsModel model = this.statModel.get(idx);

				double xPos = wholeDrawRect.getMinX() + (idx*width);
				Rectangle2D.Double drawRect = new Rectangle2D.Double(xPos, wholeDrawRect.getMinY(), width, wholeDrawRect.getHeight());

				if( this.hilightIndex == idx ){
					g2.setColor(new Color(255, 244, 209));
					g2.fill( drawRect );
				}

				final double RELATIVE_BOX_WIDTH = drawRect.getWidth() / 5;
	
				Rectangle2D.Double box = new Rectangle2D.Double( 
							drawRect.getCenterX() - RELATIVE_BOX_WIDTH
							, SwingUtilities.getMonitorYCoordinateSystem( (model.get_3qt() - baseY) * unit, drawRect )
							, RELATIVE_BOX_WIDTH * 2
							, (unit * (model.get_3qt()-model.get_1qt()) )
						);
				g2.setColor( new Color(207, 212, 255) );
				g2.fill( box );
				g2.setColor( Color.gray );
				g2.draw( box );

				g2.setColor( new Color(94, 104, 255) );
				Line2D.Double medianLine = new Line2D.Double(
						box.getMinX()
						, SwingUtilities.getMonitorYCoordinateSystem( (model.getMedian() - baseY) * unit, drawRect )
						, box.getMaxX()
						, SwingUtilities.getMonitorYCoordinateSystem( (model.getMedian() - baseY) * unit, drawRect )
				);
				g2.draw( medianLine );
	
				g2.setColor( Color.black );
				Line2D.Double outlierLine1 = new Line2D.Double();
				Line2D.Double outlierLine2 = new Line2D.Double();
	
				double outlier = (model.get_3qt() - model.get_1qt()) * 1.5;
				double max = Math.abs(model.getMax());
				double y11p = box.getMinY() - (unit * max);
				double y11o = box.getMinY() - (unit * outlier);
				double y12 = box.getMinY();
				
				double y11 = y11p;
				if( y11p < y11o )	y11 = y11o;

				double min = Math.abs(model.getMin());
				double y21 = box.getMaxY();
				double y22o = box.getMaxY() + (unit * outlier);
				double y22p = box.getMaxY() + (unit * min);
				
				double y22 = y22p;
				if( y22p > y22o )	y22 = y22o;
	
				outlierLine1.setLine( box.getCenterX(), y11, box.getCenterX(), y12 );
				outlierLine2.setLine( box.getCenterX(), y21, box.getCenterX(), y22 );
				
				g2.draw( outlierLine1 );
				g2.draw( outlierLine2 );

				Line2D.Double outlierLine1Bound = new Line2D.Double( box.getCenterX()-10, y11, box.getCenterX() + 10, y11 );
				Line2D.Double outlierLine2Bound = new Line2D.Double( box.getCenterX()-10, y22, box.getCenterX() + 10, y22 );
				
				g2.draw( outlierLine1Bound );
				g2.draw( outlierLine2Bound );
	
				for(int i=0; i<this.data[idx].length; i++) {
					double y = SwingUtilities.getMonitorYCoordinateSystem( (this.data[idx][i] - baseY ) * unit , drawRect );
	
					if( y < y11 || y > y22 ){
						Rectangle2D.Double rect = new Rectangle2D.Double(box.getCenterX() -2, y+2, 4, 4);
						Line2D.Double cross1 = new Line2D.Double( rect.getMinX(), rect.getMinY(), rect.getMaxX(), rect.getMaxY() );
						Line2D.Double cross2 = new Line2D.Double( rect.getMinX(), rect.getMaxY(), rect.getMaxX(), rect.getMinY() );
						
						g2.setColor( new Color(13, 115, 2) );
						g2.draw(cross1);
						g2.draw(cross2);
					}else{
						Ellipse2D.Double point = new Ellipse2D.Double( box.getCenterX() -1, y -1, 2, 2);
						g2.setColor( Color.red );
						g2.draw( point );
					}
				}
			}
			g2.setColor( Color.gray );
			g2.draw( wholeDrawRect );
			
			if( this.isLogScaling ) {
				g2.drawString("Log2-scale", (float)wholeDrawRect.getMinX() + 10, (float)wholeDrawRect.getMaxY() - 10 );
			}
		}
	}
	
	public static double getBaseY(DescritiveStatisticsModel stat) {
		double min = stat.getMin();
		double lowerOutlier = stat.getLowerOutlisterBoundary();
		
		double realMin = min;
		if( lowerOutlier < min )	realMin = lowerOutlier;
		
		return realMin;
	}

	public static double getBaseMaxY(DescritiveStatisticsModel stat) {
		double max = stat.getMax();
		double upperOutlier = stat.getUpperOutlisterBoundary();
		
		double realMax = max;
		if( upperOutlier > max )	realMax = upperOutlier;
		return realMax;
	}
	
	
	public static double getScreenRange(DescritiveStatisticsModel stat) {
		double min = stat.getMin();
		double lowerOutlier = stat.getLowerOutlisterBoundary();
		
		double realMin = min;
		if( lowerOutlier < min )	realMin = lowerOutlier;

		double max = stat.getMax();
		double upperOutlier = stat.getUpperOutlisterBoundary();
		
		double realMax = max;
		if( upperOutlier > max )	realMax = upperOutlier;
		
		return (realMax - realMin);
	}

	public void setInstances( Instances instances ) {
		try {
			this.instances = instances;
	
			this.data = new double[this.instances.numAttributes()][];
			for(int i=0; i<this.instances.numAttributes(); i++) {
				if( this.isLogScaling )
					this.data[i] = BasicFunctions.log( Utilities.getVectorWithoutNan( this.instances.attributeToDoubleArray( i ) ) );
				else
					this.data[i] = Utilities.getVectorWithoutNan( this.instances.attributeToDoubleArray( i ) );
	
				this.statModel.add( JMsbGroupBoxplotPanel.getStatisticsModel( this.data[i] ) );
			}
		}catch(Exception e) {
			MsbEngine.logger.error("Error : ", e);
		}
	}
	
	public void setLogScaling(boolean logScale) {
		this.isLogScaling = logScale;
		this.data = null;
		this.statModel.clear();
		
		this.setInstances( this.instances );

		this.repaint();
	}

	public void setAttribute( int attributeIndex ) {
		this.hilightIndex = attributeIndex;
		this.repaint();
	}

	public static DescritiveStatisticsModel getStatisticsModel( double[] data ) {
		double mean			= StatUtils.mean( data );
		double std			= Math.sqrt( StatUtils.variance( data ) );
		double min			= StatUtils.min( data );
		double max			= StatUtils.max( data );
		double median		= StatUtils.percentile( data, 50 );
		double _1qt			= StatUtils.percentile( data, 25 );
		double _3qt			= StatUtils.percentile( data, 75 );
		double range		= max - min;
		double out_bound	= 1.5 * (_3qt - _1qt);
		
		DescritiveStatisticsModel statModel = new DescritiveStatisticsModel();
		statModel.setMean( mean );
		statModel.setMedian( median );
		statModel.setMin( min );
		statModel.setMax( max );
		statModel.setStd( std );
		statModel.set_1qt( _1qt );
		statModel.set_3qt( _3qt );
		statModel.setRange( range );
		statModel.setOutlisterBoundary( out_bound );

		return statModel;
	}
	
	public int getColoringIndex() {
		return m_classIndex; //m_colorAttrib.getSelectedIndex();
	}
}
