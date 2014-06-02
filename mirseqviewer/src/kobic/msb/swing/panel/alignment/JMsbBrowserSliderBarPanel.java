package kobic.msb.swing.panel.alignment;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import kobic.msb.server.model.Model;
import kobic.msb.server.obj.GenomeReferenceObject;
import kobic.msb.server.obj.HairpinSequenceObject;
import kobic.msb.server.obj.SequenceObject;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;

public class JMsbBrowserSliderBarPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Rectangle2D.Double point;

	public static final int				STEPS = 15;
	public static final int				MARGIN = 2;
	public static final int				OFFSET_SIZE = 10;

	private double						UNIT;
	private double						sliderPos;

	private ProjectConfiguration		config;
	private AlignmentDockingWindowObj	dockWindow;
	
	private static final int ZOOM_IN		= 1;
	private static final int ZOOM_OUT		= 2;
	
	private double						range;

	private JMsbBrowserSliderBarPanel remote = this;

	public JMsbBrowserSliderBarPanel(AbstractDockingWindowObj dockingWindow, ProjectConfiguration config) {
		this.config 		= config;
		this.dockWindow 	= (AlignmentDockingWindowObj)dockingWindow;

		this.init();

		this.point = new Rectangle2D.Double();
		
		this.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				remote.currentPointerMove( e.getX() );
			}			
		});
		
		this.addMouseMotionListener( new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				remote.currentPointerMove( e.getX() );
			}
		});
	}
	
	public void init() {
		this.UNIT 			= -1;

		Model model = this.dockWindow.getCurrentModel();
		HairpinSequenceObject prematureSeq = model.getPrematureSequenceObject();
		int maxSequenceLength	= 3 * prematureSeq.getLength();

		this.range 			= Math.floor( maxSequenceLength / JMsbBrowserSliderBarPanel.STEPS );
		
		this.sliderPos		= Math.floor( model.getReferenceSequenceObject().getLength() / this.range );

		this.repaint();
	}

	private void repaintSlider() {
		double n_currentX = this.sliderPos * this.UNIT;

		if (this.sliderPos == 0 )										n_currentX = 1;
		else if( this.sliderPos == JMsbBrowserSliderBarPanel.STEPS )	n_currentX = this.getWidth() - 1;

		remote.point.setRect( n_currentX-1, JMsbBrowserSliderBarPanel.MARGIN, 2, remote.getSize().getHeight()-(2*JMsbBrowserSliderBarPanel.MARGIN) );
	}
	
	public double getPos() {
		return this.sliderPos;
	}

	public void setPos( double pos ) {
		this.sliderPos = pos;

		this.repaint();
	}

	public static int getNumberOfNucleotide( GenomeReferenceObject sequence, int direction ) {
		long diff = sequence.getEndPosition() - sequence.getStartPosition();

		if( direction == JMsbBrowserSliderBarPanel.ZOOM_IN ) {
			if( diff <= JMsbBrowserSliderBarPanel.OFFSET_SIZE )	return 0;
			else												return JMsbBrowserSliderBarPanel.OFFSET_SIZE;
		}else {
			return JMsbBrowserSliderBarPanel.OFFSET_SIZE;
		}
	}

	public void zoomIn() {
		this.sliderPos += 1;

		if( this.sliderPos > JMsbBrowserSliderBarPanel.STEPS )	this.sliderPos = JMsbBrowserSliderBarPanel.STEPS;
		else {
			if( remote.dockWindow instanceof AlignmentDockingWindowObj) {
				AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj)remote.dockWindow;
				Model model = dwo.getCurrentModel();
	
				model.reInitReferenceGenomeSequenceAndSomthing( JMsbBrowserSliderBarPanel.getNumberOfNucleotide( model.getReferenceSequenceObject(), JMsbBrowserSliderBarPanel.ZOOM_IN ) );
	
				float unit = dwo.getHistogramPanel().getWidth() / model.getReferenceSequenceObject().getLength();
				remote.config.setBlockWidth( unit );

				dwo.setMirid( dwo.getDefaultMirid() );
			}

			this.repaint();
		}
	}

	public void zoomOut() {
		this.sliderPos -= 1;

		if( this.sliderPos < 0 )	this.sliderPos = 0;
		else {
			if( remote.dockWindow instanceof AlignmentDockingWindowObj) {
				AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj)remote.dockWindow;
				Model model = dwo.getCurrentModel();
	
				model.reInitReferenceGenomeSequenceAndSomthing( -JMsbBrowserSliderBarPanel.getNumberOfNucleotide( model.getReferenceSequenceObject(), JMsbBrowserSliderBarPanel.ZOOM_OUT ) );
				
				float unit = dwo.getHistogramPanel().getWidth() / model.getReferenceSequenceObject().getLength();
				remote.config.setBlockWidth( unit );
	
				dwo.setMirid( dwo.getDefaultMirid() );
			}
	
			this.repaint();
		}
	}
	
	public void zoomIn( int offset ) {
		if( remote.dockWindow instanceof AlignmentDockingWindowObj) {
			MsbEngine.logger.error("ZoomIn " + offset );
			AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj)remote.dockWindow;
			Model model = dwo.getCurrentModel();

			model.reInitReferenceGenomeSequenceAndSomthing( offset );

			float unit = dwo.getHistogramPanel().getWidth() / model.getReferenceSequenceObject().getLength();
			remote.config.setBlockWidth( unit );

			dwo.setMirid( dwo.getDefaultMirid() );
		}

		this.repaint();
	}

	public void zoomOut( int offset ) {
		if( remote.dockWindow instanceof AlignmentDockingWindowObj) {
			MsbEngine.logger.error("ZoomOut " + offset );
			AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj)remote.dockWindow;
			Model model = dwo.getCurrentModel();

			model.reInitReferenceGenomeSequenceAndSomthing( -offset );
			
			float unit = dwo.getHistogramPanel().getWidth() / model.getReferenceSequenceObject().getLength();
			remote.config.setBlockWidth( unit );

			dwo.setMirid( dwo.getDefaultMirid() );
		}

		this.repaint();
	}

	private void currentPointerMove( double currentX ) {
		int currentPos = (int) Math.round( currentX / this.UNIT );

		if( currentPos < 0 )									currentPos = 0;
		else if( currentPos > JMsbBrowserSliderBarPanel.STEPS )	currentPos = JMsbBrowserSliderBarPanel.STEPS;

		int diff = (int)(Math.abs( currentPos - this.sliderPos ));
		if( remote.dockWindow instanceof AlignmentDockingWindowObj) {
			AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj)remote.dockWindow;
			Model model = dwo.getCurrentModel();
			long range = model.getReferenceSequenceObject().getEndPosition() - model.getReferenceSequenceObject().getStartPosition();

			int offset = 0;
			if( range > JMsbBrowserSliderBarPanel.OFFSET_SIZE )	offset = JMsbBrowserSliderBarPanel.OFFSET_SIZE * diff;

			if( this.getPos() < currentPos )	this.zoomIn( offset );
			else								this.zoomOut( offset );
		}

		this.sliderPos = currentPos;
		this.repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		Composite oldComposite = g2.getComposite();
		int type = AlphaComposite.SRC_OVER; 
		AlphaComposite composite = AlphaComposite.getInstance(type, 0.2f);

		Dimension dim = this.getSize();
		
		Polygon polygon = new Polygon();
		int startX = MARGIN;
		int startY = (int) (dim.getHeight() - MARGIN);
		
		polygon.addPoint( startX, startY );
		polygon.addPoint( (int) (dim.getWidth() - MARGIN) , startY );
		polygon.addPoint( (int) (dim.getWidth() - MARGIN) , MARGIN );
		polygon.addPoint( startX , startY );

		Rectangle rect = polygon.getBounds();

		Paint oldPaint = g2.getPaint();
		GradientPaint redtowhite = new GradientPaint( (int)rect.getMinX(), (int)rect.getY(), Color.white, (int)rect.getMaxX(), (int)rect.getY(), new Color(255, 175, 0) );
		g2.setPaint( redtowhite );

		g2.fill( polygon );
		g2.setPaint( oldPaint );
		g2.setColor( Color.gray );
//		g2.draw( polygon );
		this.UNIT = (dim.getWidth() - (2*JMsbBrowserSliderBarPanel.MARGIN)) / JMsbBrowserSliderBarPanel.STEPS;

		// Draw uni Lines
		g2.setColor( new Color(220, 220, 220) );
		Stroke previousStroke = g2.getStroke();
		
		g2.setComposite( composite );
		g2.setStroke( new BasicStroke(2) );
		for(double i=this.UNIT; i<this.getWidth(); i+=this.UNIT){
			Line2D.Double line = new Line2D.Double(i, rect.getMinY()+2, i, rect.getMaxY()-2);
			g2.draw( line );
		}
		g2.setStroke( previousStroke );
		g2.setComposite( oldComposite );

		this.repaintSlider();
		
		g2.setColor( Color.LIGHT_GRAY );
		g2.draw( rect );

		g2.setColor( Color.red );
		g2.fill( this.point );

		Polygon arrowPolygon = new Polygon();
		arrowPolygon.addPoint( (int)this.point.getCenterX(), (int)this.point.getMaxY()-4 );
		arrowPolygon.addPoint( (int)this.point.getCenterX() - 4, (int)this.point.getMaxY() );
		arrowPolygon.addPoint( (int)this.point.getCenterX() + 4, (int)this.point.getMaxY() );
		arrowPolygon.addPoint( (int)this.point.getCenterX(), (int)this.point.getMaxY()-4 );
		
		g2.fill( arrowPolygon );
		
		Rectangle2D.Double tmp = new Rectangle2D.Double(0, 0, this.point.getCenterX(), this.getHeight());
		
		Area a = new Area( tmp );
		Area b = new Area( polygon );
		
		a.intersect( b );

		g2.setColor( Color.green );
		g2.setComposite( composite );
		g2.fill( a );
		
		g2.setComposite( oldComposite );
	}
}
