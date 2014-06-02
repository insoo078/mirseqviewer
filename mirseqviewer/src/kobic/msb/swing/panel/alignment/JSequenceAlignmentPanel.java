package kobic.msb.swing.panel.alignment;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import javax.swing.JToolTip;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.server.model.Model;
import kobic.msb.server.obj.ReadObject;
import kobic.msb.server.obj.RnaSecondaryStructureObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.component.JMsbReadEditingPopupMenu;
import kobic.msb.swing.panel.alignment.obj.GeneralAlignedReadSequence;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;

public class JSequenceAlignmentPanel extends JAbstractSequenceRelatedPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	private List<AlignedReadSequence>	readResult;
	private List<GeneralAlignedReadSequence>	readResult;
	
	private int							pressedReadNo;
	
	private int							currentMouseX;
	private int							currentMouseY;
	
	private int							pressedPointedMouseX;
	
	private boolean 					isMouseOnAlignmentScreen;

	private List<Rectangle2D.Double>	matureAreaList;

	private JSequenceAlignmentPanel		remote = JSequenceAlignmentPanel.this;

	public JSequenceAlignmentPanel( AbstractDockingWindowObj dockingWindow, String projectName, String mirid ) {
		super( dockingWindow, projectName, mirid );
		
		ToolTipManager.sharedInstance().setInitialDelay(0);

		this.currentMouseX = 0;
		this.currentMouseY = 0;
		this.pressedPointedMouseX = 0;

		this.isMouseOnAlignmentScreen	= false;
//
//
//		if( this.getModel().getMirnaInfo().getMirid().equals("dme-mir-6-1") )
//			System.out.println("Hello");

		this.readResult		= this.getReadObjects( this.getModel().getReadList() );

		this.matureAreaList = this.getMatureAreaList();
		
		this.addMouseWheelListener( new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				remote.currentMouseX = e.getX();
				remote.currentMouseY = e.getY();
				
				remote.getDockingWindow().setIsMousePositionFixed( false );
				remote.getDockingWindow().getSsPanel().setMouseClicked( false );

				// TODO Auto-generated method stub
				if( remote.getDockingWindow().getIsMousePositionFixed() == false ) {
					int newPos = remote.getCurrentSelectedRead( remote.currentMouseY );

					if( newPos >=0 && newPos < remote.readResult.size() ) {
						remote.getDockingWindow().setSelectedRecordPos( newPos );
						
						GeneralAlignedReadSequence seq = remote.readResult.get( newPos );

						/** Show tooltip box **************************/
						if( seq.getSequence().size() > 0 ) {
							if( remote.getDockingWindow().isShowTooltip() ) {
								int pos = remote.getDockingWindow().getSelectedFieldPos();
								if( seq.getStart() <= pos && seq.getEnd() >= pos )	remote.showAlignmentTooltip( true );
								else												remote.showAlignmentTooltip( false );
							}else {
								remote.showAlignmentTooltip( false );
							}
						}
					}else {
						remote.getDockingWindow().setSelectedRecordPos( -1 );
						remote.showAlignmentTooltip(false);
					}

					remote.repaint();
				}

				e.getComponent().getParent().dispatchEvent(e);
			}
		});

		this.addMouseMotionListener( new MouseMotionAdapter() {
			@Override
			public void mouseMoved( MouseEvent e ) {
				remote.currentMouseY = e.getY();
				remote.currentMouseX = e.getX();

				if( remote.getDockingWindow().getIsMousePositionFixed() == false ) {
					int newPos = remote.getCurrentSelectedRead( remote.currentMouseY );

					if( newPos >=0 && newPos < remote.readResult.size() ){
						remote.getDockingWindow().setSelectedRecordPos( newPos );

						GeneralAlignedReadSequence seq = remote.readResult.get( newPos );

						/** Show tooltip box **************************/
						if( seq.getSequence().size() > 0 ) {
							if( remote.getDockingWindow().isShowTooltip() ) {
								int pos = remote.getDockingWindow().getSelectedFieldPos();
								if( seq.getStart() <= pos && seq.getEnd() >= pos )	remote.showAlignmentTooltip( true );
								else												remote.showAlignmentTooltip( false );
							}else {
								remote.showAlignmentTooltip( false );
							}
						}
					}else {
						remote.getDockingWindow().setSelectedRecordPos( -1 );
						remote.showAlignmentTooltip(false);
					}

					remote.repaint();
				}
			}

			@Override
			public void mouseDragged(MouseEvent e){
				remote.currentMouseX = e.getX();
				remote.currentMouseY = e.getY();

				if( SwingUtilities.isLeftMouseButton(e) ) {
					// This is for scrolling horizontally
					{
						int moveRange = Math.round( (e.getX() - remote.pressedPointedMouseX) / remote.getProjectConfiguration().getBlockWidth() );

						AlignmentDockingWindowObj adwo = remote.getDockingWindow();
						Model model = adwo.getCurrentModel();
						
						if( model.getReferenceSequenceObject().getStrand() == '+' ) {
							if( moveRange < 0 )		model.reInitReferenceGenomeSequenceByShift( Math.abs(moveRange), JMsbSysConst.SHIFT_TO_DOWNSTREAM );
							else					model.reInitReferenceGenomeSequenceByShift( Math.abs(moveRange), JMsbSysConst.SHIFT_TO_UPSTREAM );
						}else {
							if( moveRange < 0 )		model.reInitReferenceGenomeSequenceByShift( Math.abs(moveRange), JMsbSysConst.SHIFT_TO_UPSTREAM );
							else					model.reInitReferenceGenomeSequenceByShift( Math.abs(moveRange), JMsbSysConst.SHIFT_TO_DOWNSTREAM );
						}

						adwo.setMirid( adwo.getDefaultMirid() );

						remote.pressedPointedMouseX = e.getX();
					}
					// This is for scrolling vertically
					{
						int targetReadPos = remote.getCurrentSelectedRead( e.getY() );

						if( targetReadPos != remote.pressedReadNo ) {
							remote.reorderSequence( remote.pressedReadNo, targetReadPos );				
							remote.getDockingWindow().getHeatmapPane().reorderTable();
						}
	
						remote.getDockingWindow().setSelectedRecordPos( targetReadPos );
						
						remote.pressedReadNo = targetReadPos;
					}
				}
			}
		});
		
		this.addMouseListener( new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				remote.currentMouseX = e.getX();
				remote.currentMouseY = e.getY();

				if( SwingUtilities.isLeftMouseButton(e) ) {
					remote.setCursor( new Cursor(Cursor.HAND_CURSOR) );

					remote.getDockingWindow().setIsMousePositionFixed( false );
					remote.getDockingWindow().getSsPanel().setMouseClicked( false );
	
					int value = remote.getCurrentSelectedRead(currentMouseY);
					if( value > -1 && value < remote.readResult.size() ) {
						remote.pressedReadNo = value;
					}

					ProjectConfiguration config = remote.getProjectConfiguration();
					if( config.getOffset() + config.getMargin() <= e.getX() && remote.getBaseRect().getMaxX() >= e.getX() ) {
						remote.pressedPointedMouseX = e.getX();
					}
				}
			}
			
			@Override
			public void mouseReleased( MouseEvent e ) {
				remote.setCursor( Cursor.getDefaultCursor() );
				
				remote.currentMouseX = e.getX();
				remote.currentMouseY = e.getY();
			}
			
			@Override
			public void mouseClicked( MouseEvent e ) {
				remote.currentMouseX = e.getX();
				remote.currentMouseY = e.getY();

				int currentSelectedReadPos = remote.getCurrentSelectedRead( remote.currentMouseY );

				int currentSelectedReadField = remote.getDockingWindow().getSelectedFieldPos();

				// Mouse Right-click for Popup
				if( SwingUtilities.isRightMouseButton( e ) ) {
					if( currentSelectedReadPos >= 0 && currentSelectedReadPos < remote.readResult.size() ) {
						final JMsbReadEditingPopupMenu treePopup = new JMsbReadEditingPopupMenu( remote.getDockingWindow(), currentSelectedReadPos, currentSelectedReadField );

						remote.getDockingWindow().setIsMousePositionFixed( true );
						remote.getDockingWindow().getSsPanel().setMouseClicked( true );

						treePopup.show( remote, e.getX(), e.getY() );
					}
				}else if( SwingUtilities.isLeftMouseButton( e) ) {
					remote.getDockingWindow().setIsMousePositionFixed( false );
					remote.getDockingWindow().getSsPanel().setMouseClicked( false );
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				remote.isMouseOnAlignmentScreen = false;
				remote.getDockingWindow().setSelectedFieldPos(-1);
				remote.repaint();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				remote.isMouseOnAlignmentScreen = true;
				remote.repaint();
			}
		});
	}

	@Override
	public JToolTip createToolTip() {
		final JToolTip tip = super.createToolTip();
		tip.setFocusable(false);
		tip.addMouseMotionListener( new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				for(MouseMotionListener mml: remote.getMouseMotionListeners()) {
					MouseEvent nE = new MouseEvent( e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), remote.currentMouseX + e.getX(), remote.currentMouseY + e.getY(), e.getClickCount(), e.isPopupTrigger() );

					mml.mouseMoved( nE );
				}
			}
		});
		
		return tip;
	}

	private int getCurrentSelectedRead( int currentMouseY ) {
		return (int)((currentMouseY - this.getProjectConfiguration().getMargin()) / this.getProjectConfiguration().getBlockHeight());
	}

	public void reorderSequence(int from, int to) {
		this.readResult.removeAll( this.readResult );
		this.readResult	= this.getReadObjects( this.getModel().getReadList( from, to ) );
	}

	private List<Rectangle2D.Double> getMatureAreaList() {
		List<Rectangle2D.Double> matureAreaList = new ArrayList<Rectangle2D.Double>();

		if( this.getDockingWindow().isShowMatureBackground() && !this.getModel().isNovel() ) {
			String version = this.getModel().getProjectMapItem().getMiRBAseVersion();

			List<RnaSecondaryStructureObj> ssOList		= Model.getSeondaryStructuresByHairpinInfo( this.getModel().getMirnaInfo().getMirid(), version, this.getProjectConfiguration() );
			
			char strand = this.getModel().getReferenceSequenceObject().getStrand();

			for(RnaSecondaryStructureObj structure:ssOList) {
				long start		= structure.getStart();
				long end		= structure.getEnd();
				long length		= structure.getEnd() - structure.getStart() + 1;
				double unit		= this.getProjectConfiguration().getBlockWidth();
				double width	= length * unit;

				double x = this.getProjectConfiguration().getOffset() + this.getProjectConfiguration().getMargin();

				width = unit * length;

				if( strand == '+' ) x += (( start - this.getModel().getReferenceSequenceObject().getStartPosition() ) * unit );
				else				x += ((this.getModel().getReferenceSequenceObject().getEndPosition() - end) * unit);

				if( x < this.getProjectConfiguration().getOffset() ) {
					width = width - (this.getProjectConfiguration().getOffset() + this.getProjectConfiguration().getMargin() - x);
					x = this.getProjectConfiguration().getOffset() + this.getProjectConfiguration().getMargin();
				}

				Rectangle2D.Double rect = new Rectangle2D.Double( x, 0, width, 0 );
				matureAreaList.add( rect );
			}
		}
		return matureAreaList;
	}

	private List<GeneralAlignedReadSequence> getReadObjects( List<ReadObject> readList ) {
		List<GeneralAlignedReadSequence> result = new ArrayList<GeneralAlignedReadSequence>();

		double y = this.getProjectConfiguration().getMargin();
		for( ReadObject read:readList ) {
			GeneralAlignedReadSequence readAlignedToGenomeSequence	= new GeneralAlignedReadSequence( this.getReferenceObject(), this.getModel(), this.getProjectConfiguration(), read, y );

			result.add( readAlignedToGenomeSequence );

			y += this.getProjectConfiguration().getBlockHeight();
		}

		return result;
	}

	public void hilightCurrentRecord( Graphics2D g2 ) {
		if( this.getDockingWindow().getSelectedRecordPos() >= 0 && this.getDockingWindow().getSelectedRecordPos() < this.readResult.size() ) {
			Composite currentComposite = g2.getComposite();
			Color currentColor = g2.getColor();

			GeneralAlignedReadSequence seq = this.readResult.get( this.getDockingWindow().getSelectedRecordPos() );

			Rectangle2D.Double hilightRect = seq.getCurrentRecordObj( this.getBaseRect() );

			g2.setColor( MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.getProjectName() ).getProjectConfiguration().getRecordBarColor() );
			g2.draw( hilightRect );

			float alpha = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.getProjectName() ).getProjectConfiguration().getAlphaForHorizontalHilightBar();

			int type = AlphaComposite.SRC_OVER; 
			AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
			g2.setComposite( composite );

			g2.fill( hilightRect );

			this.getDockingWindow().getHeatmapPane().hilightAt( this.getDockingWindow().getSelectedRecordPos() );

			g2.setColor(currentColor);
			g2.setComposite(currentComposite);
		}else {
			this.showAlignmentTooltip( false );
		}
	}
	
	private void showAlignmentTooltip( boolean switchSystem ) {
//		MsbEngine.logger.debug("isShow : " + remote.getDockingWindow().isShowTooltip() + " " + switchSystem );

		if( switchSystem & this.isMouseOnAlignmentScreen ) {
			GeneralAlignedReadSequence gars = this.readResult.get( this.getDockingWindow().getSelectedRecordPos() );

			try {
				remote.setToolTipText( gars.getShowAlignmentTooltip( this.getDockingWindow().getSelectedFieldPos() ) );
			}catch(Exception e) {
				MsbEngine.logger.error("Error : focusable problem", e);
			}
		} else {
			remote.setToolTipText( null );
		}
	}

	@Override
	public void drawBaseRect(Graphics2D g2) {
		if( this.getBaseRect() == null )	this.setBaseRect( new Rectangle2D.Double() );
		
		JViewport viewport = this.getDockingWindow().getAlignmentScrollPane().getViewport();

		Rectangle visibleRect = viewport.getVisibleRect();

		double verticalHeight = (this.getModel().getReadList().size() * this.getProjectConfiguration().getBlockHeight());
		if( verticalHeight < visibleRect.getHeight() )
			verticalHeight = visibleRect.getHeight() - (this.getProjectConfiguration().getMargin() * 2);

		this.getBaseRect().setRect( this.getProjectConfiguration().getMargin(), this.getProjectConfiguration().getMargin(), this.getProjectConfiguration().getOffset() + this.getReferenceObject().size() * this.getProjectConfiguration().getBlockWidth(), verticalHeight );

//		g2.setColor( Color.white );
//		g2.fill( this.getBaseRect() );

		Color grayColor = new Color(240, 240, 240);
		int cnt = 0;

		Composite oldComposite = g2.getComposite();
		float alpha = 0.4f;
		int type = AlphaComposite.SRC_OVER; 
		AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
		g2.setComposite( composite );

		double x = this.getBaseRect().getMinX() + this.getProjectConfiguration().getOffset();
		double width = this.getBaseRect().getWidth() - this.getProjectConfiguration().getOffset();

		for(double i=this.getBaseRect().getMinY(); i<=this.getBaseRect().getMaxY(); i+= this.getProjectConfiguration().getBlockHeight() ) {
			double height = this.getProjectConfiguration().getBlockHeight();
			if( i + height > this.getBaseRect().getMaxY() ) {	height = this.getBaseRect().getMaxY() - (i+height);	}
			Rectangle2D.Double rect = new Rectangle2D.Double( x, i, width, height );

			if( cnt % 2 == 0 )	g2.setColor( Color.white );
			else				g2.setColor( grayColor );
			g2.fill( rect );

			cnt++;
		}
		g2.setComposite( oldComposite );

		g2.setColor( Color.gray );
//		g2.draw( this.getBaseRect() );

		
		this.drawBaseBackgroundLine(g2);
	}
	
	private void drawMatureArea( Graphics2D g2 ) {
		if( this.getDockingWindow().isShowMatureBackground() ) {
			Color currentColor = g2.getColor();
			Composite currentComposite = g2.getComposite();

			float alpha = 0.3f;
			int type = AlphaComposite.SRC_OVER; 
			AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
			g2.setComposite( composite );
			g2.setColor( new Color(222, 235, 185) );

			for(Iterator<Rectangle2D.Double> iter=this.matureAreaList.iterator(); iter.hasNext();){
				Rectangle2D.Double rect = iter.next();
				
				double tmpX = this.getProjectConfiguration().getMargin() + this.getProjectConfiguration().getOffset();

				double x = rect.getMinX();
				double width = rect.getWidth();
				if( tmpX > rect.getMinX() )	{
					width -= (tmpX-rect.getMinX());
					x = tmpX;
				}else if( x > this.getBaseRect().getMaxX() ) {
					width -= (x - this.getBaseRect().getMaxX());
					x = this.getBaseRect().getMaxX();
				}

				rect.setRect( x, this.getBaseRect().getMinY(), width, this.getBaseRect().getHeight() );

				g2.fill( rect );
			}
			g2.setColor(currentColor);
			g2.setComposite(currentComposite);
		}
	}

	@Override
	public void draw( Graphics2D g2 ) {
//		FontRenderContext frc = g2.getFontRenderContext();

		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2.setFont( MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.getProjectName() ).getProjectConfiguration().getSystemFont() );
		/// To Draw Read Sequence /////////////////////////////////////////
//		Color systemColor = g2.getColor();

		JViewport viewport = this.getDockingWindow().getAlignmentScrollPane().getViewport();

		Rectangle visibleRect = viewport.getVisibleRect();
		
		java.awt.Point p = viewport.getViewPosition();

		visibleRect.setLocation((int)p.getX(), (int)p.getY());

		this.drawMatureArea(g2);

		/********************************/		
		for( GeneralAlignedReadSequence readSequence:this.readResult ) {
			Rectangle2D.Double rectChk = readSequence.getCurrentRecordObj( this.getBaseRect() );
			if( rectChk.getMaxY() >= visibleRect.getMinY() - rectChk.getHeight() && rectChk.getMinY() <= visibleRect.getMaxY() + rectChk.getHeight() ) {
				readSequence.draw( this.getBaseRect(), g2 );
			}
		}

		this.hilightCurrentRecord( g2 );

		this.setPreferredSize( new Dimension((int)( (2*this.getProjectConfiguration().getMargin() ) + this.getProjectConfiguration().getOffset() + this.getReferenceObject().size() * this.getProjectConfiguration().getBlockWidth()), (int)(this.getBaseRect().getHeight() + (2*this.getProjectConfiguration().getMargin()) )) );
		this.revalidate();

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.getProjectName() ).getProjectConfiguration().getAlphaForVerticalHilightBar()));
		g2.setColor( MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.getProjectName() ).getProjectConfiguration().getFieldBarColor() );
		g2.fill( this.getHilightBlock() );
		/******************************************/
	}

	@Override
	public void update(Observable o, Object arg) {
		if( arg instanceof Model ) {
			// TODO Auto-generated method stub
			Model model = (Model)arg;

			this.setModel( model );

			this.rePositioningAboutReference();

			this.setReferenceObject( this.getReferenceObjectList( this.getModel().getReferenceSequenceObject() ) );

			this.updateConfiguration();

			if( this.readResult != null )		this.readResult.clear();
			this.readResult		= this.getReadObjects( this.getModel().getReadList() );
			this.matureAreaList	= this.getMatureAreaList();
	
			this.repaint();
		}else if( arg instanceof Integer  ) {
			Integer pos = (Integer)arg;

			this.paintHoverForHilightNucleotides( true,	pos, this.getBaseRect().getHeight() );
		}
	}

	public List<GeneralAlignedReadSequence> getReadSequenceList() {
		return this.readResult;
	}
}
