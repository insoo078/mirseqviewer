package kobic.msb.swing.panel.alignment;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.SwingConst;
import kobic.msb.server.model.Model;
import kobic.msb.server.obj.HairpinSequenceObject;
import kobic.msb.server.obj.RnaSecondaryStructureObj;
import kobic.msb.swing.panel.track.GenomeReferenceTrack;
import kobic.msb.swing.panel.track.HistogramTrack;
import kobic.msb.swing.panel.track.MicroRnaStructureTrack;
import kobic.msb.swing.panel.track.ScaleTrack;
import kobic.msb.swing.panel.track.SequenceTrack;
import kobic.msb.swing.panel.track.TrackItem;

import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;

public class JSequenceHistogramPanel extends JAbstractSequenceRelatedPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<TrackItem>				tracks;
	private int							sequenceLength;
	
	private int							pressedMouseX;
	
	private JSequenceHistogramPanel remote = JSequenceHistogramPanel.this;

	public JSequenceHistogramPanel( AbstractDockingWindowObj dockingWindow, String projectName, String mirid ) {
		super( dockingWindow, projectName, mirid );
		
		this.tracks = new ArrayList<TrackItem>();

		this.sequenceLength = this.getModel().getReferenceSequenceObject().getSequence().size();
		this.initialize();
		
		this.addMouseListener( new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ProjectConfiguration config = remote.getProjectConfiguration();
				// TODO Auto-generated method stub
				if( config.getOffset() + config.getMargin() <= e.getX() && remote.getBaseRect().getMaxX() >= e.getX() ) {
					remote.setCursor( new Cursor(Cursor.HAND_CURSOR) );

					remote.pressedMouseX = e.getX();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				remote.setCursor( Cursor.getDefaultCursor() );
			}
		});
		
		this.addMouseMotionListener( new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				int moveRange = Math.round( (e.getX() - remote.pressedMouseX) / remote.getProjectConfiguration().getBlockWidth() );
				
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

				remote.pressedMouseX = e.getX();
			}
		});
	}
	
	public double getTotalTracksHeight() {
		double sum = 0;
		for(int i=0; i<this.tracks.size(); i++) {
			sum += this.tracks.get(i).getTrackHeight();
		}
		return sum;
	}

	@Override
	public void drawBaseRect( Graphics2D g2 ) {
		ProjectConfiguration config = this.getProjectConfiguration();
		if( this.getBaseRect() == null )	this.setBaseRect( new Rectangle2D.Double() );

		/**** Allowhorizontal scrollbar ****/
//		this.getBaseRect().setRect( this.getProjectConfiguration().getMargin(), config.getMargin(), config.getOffset() + (this.sequenceLength * config.getBlockWidth()), this.getTotalTracksHeight() );
//		if( this.getBaseRect().getWidth() < this.getDockingWindow().getHistogramScrollPane().getVisibleRect().getWidth() ) {
//			this.getBaseRect().setRect( this.getBaseRect().getMinX(), this.getBaseRect().getMinY(), this.getDockingWindow().getHistogramScrollPane().getVisibleRect().getWidth() , this.getBaseRect().getHeight() );
//		}

		/**** Not allow horizontal scrollbar ****/
//		this.getBaseRect().setRect( config.getMargin(), config.getMargin(), this.getWidth() - (2*config.getMargin()), this.getTotalTracksHeight() );
		this.getBaseRect().setRect( config.getMargin(), config.getMargin(), this.getWidth() - (2*config.getMargin()), this.getHeight() - (config.getMargin() * 2) );

		float unit = (float)(this.getBaseRect().getWidth() - config.getOffset()) / this.sequenceLength;
		if( unit != config.getBlockWidth() ) {
			config.setBlockWidth( unit );
			this.getDockingWindow().setMirid( this.getDockingWindow().getDefaultMirid() );
		}

//		g2.setColor( Color.white );
//		g2.fill( this.getBaseRect() );
//		g2.setColor( Color.gray );
//		g2.draw( this.getBaseRect() );

		this.drawBaseBackgroundLine(g2);
	}

	private void drawVerticalHilightBox( Graphics2D g2 ) {
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.getProjectName() ).getProjectConfiguration().getAlphaForVerticalHilightBar()));
		g2.setColor( MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.getProjectName() ).getProjectConfiguration().getFieldBarColor() );
		g2.fill( this.getHilightBlock() );
	}

	@Override
	public void draw(Graphics2D g2) {
		for(int i=0; i<this.tracks.size(); i++) {
			this.tracks.get(i).draw(g2, this.getBaseRect() );
		}

		this.drawVerticalHilightBox( g2 );

//		this.setPreferredSize( new Dimension((int)( (2*config.getMargin() ) + config.getOffset() + this.sequenceLength * config.getBlockWidth()), (int)(this.getTotalTracksHeight() + config.getMargin()) ) );
//		this.revalidate();
	}

	@Override
	public void update(Observable o, Object arg) {
		if( arg instanceof Model ) {
			// TODO Auto-generated method stub
			Model model = (Model)arg;
			this.setModel( model );
			
			this.sequenceLength = this.getModel().getReferenceSequenceObject().getSequence().size();
			
			float unit = (float)(this.getBaseRect().getWidth() - this.getProjectConfiguration().getOffset()) / this.sequenceLength;
			this.getProjectConfiguration().setBlockWidth( unit );
			this.getProjectConfiguration().writeConfigFile();

			this.updateConfiguration();

			this.rePositioningAboutReference();
	
			this.tracks.clear();
			this.initialize();

			this.repaint();
		}else if( arg instanceof Integer ) {
			Integer pos = (Integer)arg;
			
			this.paintHoverForHilightNucleotides( true,	pos, (int)(this.getTotalTracksHeight()) );
		}
	}
	
	public void initialize() {
		ProjectConfiguration config = this.getProjectConfiguration();

		ScaleTrack scaleTrack = new ScaleTrack( config );
		scaleTrack.setTrackHeight( config.getBlockHeight() );
		scaleTrack.setTitle("Scale");
		scaleTrack.setMovableType( SwingConst.TRACK_FIXED );
		scaleTrack.setTrackType( SwingConst.SCALE_TYPE );
		
		this.tracks.add( scaleTrack );

		HistogramTrack histTrack = new HistogramTrack( config );
		histTrack.setTrackHeight( config.getBlockHeight() * 4 );
		histTrack.setTitle("Histogram");
		histTrack.setMovableType( SwingConst.TRACK_FIXED );
		histTrack.setTrackType( SwingConst.SCALE_TYPE );
		
		this.tracks.add( histTrack );

		GenomeReferenceTrack referenceTrack = new GenomeReferenceTrack( config );
		referenceTrack.setTrackHeight( config.getBlockHeight() );
		referenceTrack.setTitle("Reference");
		referenceTrack.setMovableType( SwingConst.TRACK_FIXED );
		referenceTrack.setTrackType( SwingConst.SCALE_TYPE );
		
		this.tracks.add( referenceTrack );

		SequenceTrack prematureTrack = new SequenceTrack( config );
		prematureTrack.setTrackHeight( config.getBlockHeight() );
		prematureTrack.setTitle("Pre-miRNA");
		prematureTrack.setMovableType( SwingConst.TRACK_FIXED );
		prematureTrack.setTrackType( SwingConst.SCALE_TYPE );

		this.tracks.add( prematureTrack );

		ProjectMapItem projectMapItem = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.getProjectName() );
//		List<RnaSecondaryStructureObj> ssOList		= Model.getSeondaryStructures( this.getModel().getMirnaInfo().getMirid(), projectMapItem.getMiRBAseVersion() );
		
		List<RnaSecondaryStructureObj> ssOList = null;
		if( this.getModel().isNovel() ) {
			ssOList = new ArrayList<RnaSecondaryStructureObj>();
			
			HairpinSequenceObject premature = this.getModel().getPrematureSequenceObject();
			RnaSecondaryStructureObj obj = new RnaSecondaryStructureObj( this.getModel().getMirnaInfo().getMirid(), "", premature.getStartPosition(), premature.getEndPosition(), new Color(210, 210, 210) );
			ssOList.add( obj );
		}else {
			ssOList		= Model.getSeondaryStructuresByHairpinInfo( this.getModel().getMirnaInfo().getMirid(), projectMapItem.getMiRBAseVersion(), config );
		}

		MicroRnaStructureTrack ssTrack = new MicroRnaStructureTrack( config );
		ssTrack.setTrackHeight( config.getBlockHeight() * 1.2 );
		ssTrack.setTitle("2nd. Str.");
		ssTrack.setMovableType( SwingConst.TRACK_FIXED );
		ssTrack.setTrackType( SwingConst.SCALE_TYPE );
		ssTrack.setReference( this.getModel().getReferenceSequenceObject() );
		ssTrack.setMirid( this.getModel().getMirnaInfo().getMirid() );
		ssTrack.setStrand( this.getModel().getReferenceSequenceObject().getStrand() );
		ssTrack.setRnaStructureList( ssOList );

		this.tracks.add( ssTrack );
		
		for(int i=0; i<this.tracks.size(); i++) {
			if( i==0 )	this.tracks.get(i).setVerticalLocation( config.getMargin() );
			else {
				double newY = this.tracks.get(i-1).getVerticalLocation() + this.tracks.get(i-1).getTrackHeight();
				this.tracks.get(i).setVerticalLocation( newY );
			}

			if( this.tracks.get(i) instanceof SequenceTrack )	((SequenceTrack)this.tracks.get(i)).setBaseDistanceToView( this.getModel(), referenceTrack.getReferenceObject() );
			else												this.tracks.get(i).setBaseDistanceToView( this.getModel() );
			
			this.tracks.get(i).setOrder(i);
		}
	}
}
