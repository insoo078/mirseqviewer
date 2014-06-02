package kobic.msb.swing.panel.alignment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.swing.JPanel;

import kobic.com.util.Utilities;
import kobic.msb.common.util.MsvUtilities;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.Model;
import kobic.msb.server.obj.RnaStructureObj;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class JSecondaryStructurePanel extends JPanel implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float						radius;
	private boolean						isDrawingEllipse;
	private Model						model;
	private Properties					property;
	private List<RnaStructureObj>		result;
	private AlignmentDockingWindowObj	dockWindow;
	private ProjectMapItem				projectMapItem;

	private String						projectName;

	private double						currentMouseX;
	private double						currentMouseY;
	
	private Font						drwFont						= new Font("Arial", Font.PLAIN, 10);
	private final float[]				GRADIENT_VALUE				= new float[] { 0.0f, 0.5f };
	private final Color[]				HILIGHTED_GRADIENT_COLOR	= new Color[] { Color.white, Color.red };

	private Rectangle2D.Double			baseRect;
	private RnaStructureObj				hilightedCircle;
	private boolean						isMouseClicked;

	private JSecondaryStructurePanel remote = JSecondaryStructurePanel.this;
	
	public JSecondaryStructurePanel( AbstractDockingWindowObj dockWindow, String projectName, String mirid ) {
		this.property			= MsbEngine.getInstance().getSystemProperties();
		this.dockWindow			= (AlignmentDockingWindowObj)dockWindow;
		
		this.model				= this.dockWindow.getCurrentModel();

		this.projectName		= projectName;

		this.radius				= Float.parseFloat( this.property.getProperty("msb.secondaryview.circle.radius") );
		this.isDrawingEllipse	= true;

		this.currentMouseX		= -1;
		this.currentMouseY		= -1;

		this.projectMapItem		= MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.projectName );
		
		this.result 			= SwingUtilities.getSecondaryStructureInfo( this.model.getDemoStructureSequence( projectMapItem.getMiRBAseVersion() ) );
		
		this.baseRect			= null;
		this.isMouseClicked		= false;
		
		this.hilightedCircle	= null;
		
		this.addMouseMotionListener( new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if( remote.dockWindow.getIsMousePositionFixed() == false ) {
					// TODO Auto-generated method stub
					if( remote.baseRect.contains( e.getX(), e.getY() ) ){
						remote.currentMouseX = e.getX();
						remote.currentMouseY = e.getY();

						try {
							Iterator<RnaStructureObj> iter = result.iterator();
							while( iter.hasNext() ) {
								RnaStructureObj rnaObj = iter.next();
								if( rnaObj.getEllipse().contains( remote.currentMouseX, remote.currentMouseY ) )	{
									remote.dockWindow.setSelectedFieldPos( rnaObj.getPosition() );
								}
							}
						}catch(Exception exp) {
							MsbEngine.logger.error("Error", exp);;
						}
					}
				}
			}
		});
		
		this.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) ) {
					if( remote.isMouseClicked == true )	{
						remote.isMouseClicked	= false;
						remote.dockWindow.setIsMousePositionFixed( false );
					}else								{
						remote.isMouseClicked	= true;
					}
				}
				remote.repaint();
			}
		});

		this.addComponentListener( new ComponentAdapter() {
			@Override
			public void componentResized( ComponentEvent e ) {
				remote.currentMouseX = -1;
				remote.currentMouseY = -1;
			}
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		this.setBackground( Color.white );

		Dimension dim = this.getSize();
		this.toDrawImage( g2, dim.getWidth(), dim.getHeight() );
		
		if( this.hilightedCircle != null ) {
			this.hilightedCircle.drawEllipse( g2, this.drwFont, Color.red, Color.black, this.radius, this.isDrawingEllipse) ;
		}
	}

	public void toDrawImage( Graphics2D g2, double width, double height ) {
		Dimension dim = new Dimension((int)width, (int)height);

		float margin = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.projectName ).getProjectConfiguration().getMargin();

		if( this.baseRect == null )	this.baseRect = new Rectangle2D.Double();
		this.baseRect.setRect( margin, margin, dim.getWidth() - ( 2 * margin ), dim.getHeight() - ( 2 * margin ) );
		
		g2.setColor( Color.white );
		g2.fill( this.baseRect );
		g2.setColor( Color.gray );
		g2.draw( this.baseRect );

		double unit = SwingUtilities.getVariableDrawUnit( this.baseRect, this.result );

		double distance = MsvUtilities.getDistanceAt( this.result.get(0), this.result.get(1) ) * unit;
		
		this.radius = (float)(distance / 3);
		
		this.result = SwingUtilities.makeNucleotidesInRnaStructure( this.baseRect, this.result, unit, this.radius );

		this.translateToDraw(g2, this.baseRect, this.moveCenterToOrigin( this.baseRect, this.result ) );
	}
	
	private List<RnaStructureObj> moveCenterToOrigin( Rectangle2D.Double baseRect, List<RnaStructureObj> result ) {
		Point2D.Double centerOfDrawingImage = SwingUtilities.getCenterOfDrawingImage( result );
		
		/**
		 * Center position of Canvas
		 */
		double canvasOriginX = baseRect.getCenterX();
		double canvasOriginY = baseRect.getCenterY();

		/**
		 * Center position on the drawing image
		 */
		double drawingImageCenterX = centerOfDrawingImage.getX();
		double drawingImageCenterY = centerOfDrawingImage.getY();
		
		centerOfDrawingImage = null;

		/**
		 * Differences between center of canvas and center of drawing image
		 */
		double dX = canvasOriginX - drawingImageCenterX;
		double dY = drawingImageCenterY - canvasOriginY;

		Iterator<RnaStructureObj> iter = result.iterator();

		while( iter.hasNext() ) {
			RnaStructureObj		rnaObj			= iter.next();
			Ellipse2D.Double	els				= rnaObj.getEllipse();
			
			double newPosX = els.getX() + dX;
			double newPosY = els.getY() - dY;
			
			els.x = newPosX;
			els.y = newPosY;
		}
		return result;
	}

	private void translateToDraw(Graphics2D g2, Rectangle2D.Double baseRect, List<RnaStructureObj> result ) {
		/**
		 * To translate a image to new position on Canvas using differences
		 * 
		 */
		Iterator<RnaStructureObj> iter = result.iterator();

		int pos = this.dockWindow.getSelectedFieldPos();
		while( iter.hasNext() ) {
			RnaStructureObj		rnaObj			= iter.next();
			Ellipse2D.Double	els				= rnaObj.getEllipse();
			Paint				currentPaint 	= g2.getPaint();

			float newRadius = this.radius;
			Color dColor	= rnaObj.getColor();

			this.HILIGHTED_GRADIENT_COLOR[1] = dColor;

			if( newRadius < 1 )	newRadius = 1;

			RadialGradientPaint p = new RadialGradientPaint(
					els.getBounds().getLocation()
					, newRadius
					, this.GRADIENT_VALUE,
					this.HILIGHTED_GRADIENT_COLOR );
			
			g2.setPaint(p);
			rnaObj.drawEllipse(g2, this.drwFont, rnaObj.getColor(), Color.black, this.radius, this.isDrawingEllipse);
			g2.setPaint(currentPaint);

			/**
			 * To draw a text on center of ellipse
			 */

			p	= null;
		}

		if( pos != -1 && this.isMouseClicked )	this.dockWindow.setIsMousePositionFixed( true );
	}

	@Override
	public void update(Observable o, Object arg) {
		if( arg instanceof Model ) {
			// TODO Auto-generated method stub
			Model model = (Model)arg;
	
			this.model		= null;
			this.model		= model;
	
			this.result		= null;
			this.result 	= SwingUtilities.getSecondaryStructureInfo( this.model.getDemoStructureSequence(this.projectMapItem.getMiRBAseVersion()) );
	
			this.repaint();
		}else if( arg instanceof Integer ) {
			Integer pos = (Integer)arg;

			int diff = pos - this.result.get(0).getPosition();
			if( this.model.getReferenceSequenceObject().getStrand() == '-' )
				diff = this.result.get(0).getPosition() - pos;

			if( diff >= 0 )	this.setHilightSelectedNucleotide( diff );
		}
	}

	public List<RnaStructureObj> getRnaEllipseList() {
		return this.result;
	}

	public void setHilightSelectedNucleotide( int pos ) {
		if( pos >= 0 && pos < this.result.size() ) {
			this.hilightedCircle = this.result.get(pos);
					
			this.repaint();
		}
	}

	public void setMouseClicked(boolean flag) {
		this.isMouseClicked = flag;
	}
}
