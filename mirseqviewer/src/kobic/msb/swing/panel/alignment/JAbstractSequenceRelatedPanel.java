package kobic.msb.swing.panel.alignment;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.List;
import java.util.Observer;

import javax.swing.JPanel;

import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.Model;
import kobic.msb.server.obj.GenomeReferenceObject;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.panel.alignment.obj.AlignedNucleotide;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;

public abstract class JAbstractSequenceRelatedPanel extends JPanel implements Observer, Printable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Model							model;
	private AlignmentDockingWindowObj		dockingWindow;
	private String							projectName;
	private Rectangle2D.Double				baseRect;
	
	private ProjectConfiguration			config;
	
	private List<AlignedNucleotide>			referenceResult;
	
	private Rectangle2D.Double				fieldHilightBar;

	private JAbstractSequenceRelatedPanel	remote = JAbstractSequenceRelatedPanel.this;
	
//	private int mx;
//	private int my;

	public JAbstractSequenceRelatedPanel( AbstractDockingWindowObj dockingWindow, String projectName, String mirid ) {
		this.dockingWindow			= (AlignmentDockingWindowObj)dockingWindow;
//		this.model					= MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName ).getProjectModel( mirid );
		this.model					= this.dockingWindow.getCurrentModel();
		
		this.config					= MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName ).getProjectConfiguration();

		this.projectName			= projectName;
		this.baseRect				= null;
		
		this.referenceResult		= this.getReferenceObjectList( this.model.getReferenceSequenceObject() );

		this.fieldHilightBar		= new Rectangle2D.Double();
		
		this.addMouseMotionListener( new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if( remote.dockingWindow.getIsMousePositionFixed() == false ) {
					int currentMouseX	= -1;

					// TODO Auto-generated method stub
					if( remote.getBaseRect().getMinX() + config.getOffset() <= e.getX() && remote.getBaseRect().getMaxX() >= e.getX() &&
							remote.getBaseRect().getMinY() < e.getY() && remote.getBaseRect().getMaxY() > e.getY() ) {
						currentMouseX = e.getX();

						int pos = -1;
						double val = ((double)(currentMouseX - remote.config.getMargin() - remote.config.getOffset()) / remote.config.getBlockWidth());
		
						if( val >= 0 )	pos = (int)val;
	
						if( pos >= 0 && pos < remote.model.getReferenceSequenceObject().getLength() ) {
							int newPos = remote.model.getReferenceSequenceObject().getSequence().get( pos ).getPosition();
						
							remote.dockingWindow.setSelectedFieldPos( newPos );
						}
					}else {
						currentMouseX = -1;
						remote.dockingWindow.setSelectedFieldPos( currentMouseX );
					}
	
					remote.repaint();
				}
			}
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		this.setBackground( Color.white );

		this.drawBaseRect(g2);
		this.draw(g2);
		
//		g2.drawString("(" + this.mx + "," + this.my+")", this.mx, this.my);
//		g2.drawString("(" + remote.dockingWindow.getSelectedFieldPos() +")", this.mx, this.my);
	}
	
	public void drawBaseBackgroundLine(Graphics2D g2) {
		g2.setColor( new Color(235, 235, 235) );
		for(double i= this.config.getOffset() + this.config.getMargin(); i<this.getBaseRect().getMaxX(); i+= 20 ) {
			Line2D.Double vLine = new Line2D.Double( i, this.getBaseRect().getMinY()+1, i, this.getBaseRect().getMaxY()-1 );
			g2.draw( vLine );
		}
	}
	
	public abstract void drawBaseRect(Graphics2D g);
	public abstract void draw(Graphics2D g);
	
	public List<AlignedNucleotide> getReferenceObjectList( GenomeReferenceObject reference ) {
//		if( this.getModel().getMirnaInfo().getMirid().equals("hsa-mir-6723") ) {
//			System.out.println("Hello");
//		}
		double xPos = this.config.getOffset() + this.config.getMargin();
		double yPos = (4*this.config.getBlockHeight()) + this.config.getMargin();
		return SwingUtilities.getReferenceNucleotideObjectList( reference, xPos, yPos, this.config.getBlockWidth(), this.config.getBlockHeight() );
	}

	public List<AlignedNucleotide> getReferenceObject() {
		return this.referenceResult;
	}
	
	public void setBaseRect( Rectangle2D.Double rect ) {
		this.baseRect = rect;
	}
	
	public Rectangle2D.Double getBaseRect() {
		return this.baseRect;
	}

	public Model getModel() {
		return this.model;
	}
	
	public void setModel(Model model) {
		this.model = model;
	}

	public String getProjectName() {
		return this.projectName;
	}

	public void setReferenceObject( List<AlignedNucleotide> referenceResult ) {
		if( this.referenceResult != null )	this.referenceResult.removeAll( this.referenceResult );

		this.referenceResult = referenceResult;
	}
	
	public ProjectConfiguration getProjectConfiguration() {
		return this.config;
	}
	
	public AlignmentDockingWindowObj getDockingWindow() {
		return this.dockingWindow;
	}

	public void paintHoverForHilightNucleotides( boolean flag, int pos, double height ) {
		int index = pos - this.referenceResult.get(0).getPosition();
		if( this.model.getReferenceSequenceObject().getStrand() == '-')
			index = this.referenceResult.get(0).getPosition() - pos;

		if( index >= 0 ) {
			double x = this.config.getMargin() + this.config.getOffset() + (index * this.config.getBlockWidth());
			double y = this.config.getMargin();
	
			this.fieldHilightBar.setRect( x, y, this.config.getBlockWidth(), height );
		}else {
			this.fieldHilightBar.setRect( -100, -100, 1, 1);
		}
		this.repaint();
	}
	
	public Rectangle2D.Double getHilightBlock() {
		return this.fieldHilightBar;
	}

	public void setSelectedSequenceForHilighting( int pos ) {
		this.dockingWindow.setSelectedRecordPos( pos );
		this.repaint();
	}
	
	public void rePositioningAboutReference() {
		if( this.referenceResult != null )	this.referenceResult.removeAll( this.referenceResult );
		this.referenceResult =  this.getReferenceObjectList( this.model.getReferenceSequenceObject() );
	}
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if( pageIndex > 0 ) {
			return NO_SUCH_PAGE; 
		}

		Graphics2D g2 = (Graphics2D)graphics;
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		this.drawBaseRect(g2);
		this.draw(g2);

		return PAGE_EXISTS;
	}
	
	public void updateConfiguration() {
		this.config = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.projectName ).getProjectConfiguration();
	}
}
