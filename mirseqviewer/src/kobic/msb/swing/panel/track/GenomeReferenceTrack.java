package kobic.msb.swing.panel.track;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.Model;
import kobic.msb.server.obj.GenomeReferenceObject;
import kobic.msb.server.obj.SequenceObject;
import kobic.msb.swing.panel.alignment.obj.AlignedNucleotide;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;


public class GenomeReferenceTrack  extends TrackItem{
	private List<AlignedNucleotide>			referenceResult;
	private GenomeReferenceObject			referenceSequenceObject;

	public GenomeReferenceTrack(ProjectConfiguration config) {
		super( config );
		// TODO Auto-generated constructor stub
		this.referenceResult = null;
	}

	@Override
	public String getRid() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<AlignedNucleotide> getReferenceObject() {
		return this.referenceResult;
	}
	
	public GenomeReferenceObject getReferenceSequenceObject() {
		return this.referenceSequenceObject;
	}
	
//	public void doCalculateGenomeBase(Model model) {
//		this.referenceSequenceObject	= model.getReferenceSequenceObject();
//		this.referenceResult			= this.getReferenceObjectList( this.referenceSequenceObject );
//	}
	
	@Override
	public void setBaseDistanceToView(Model model) {
		// TODO Auto-generated method stub
		this.referenceSequenceObject	= model.getReferenceSequenceObject();
		this.referenceResult			= this.getReferenceObjectList( this.referenceSequenceObject );
	}

	public List<AlignedNucleotide> getReferenceObjectList( GenomeReferenceObject reference ) {
		double xPos = this.getProjectConfiguration().getOffset() + this.getProjectConfiguration().getMargin();
		double yPos = this.getVerticalLocation();
		return SwingUtilities.getReferenceNucleotideObjectList( reference, xPos, yPos, this.getProjectConfiguration().getBlockWidth(), this.getProjectConfiguration().getBlockHeight() );
	}

	@Override
	public void draw(Graphics2D g2, Rectangle2D.Double baseRect) {
		this.drawHeader(g2);

		// TODO Auto-generated method stub
		FontRenderContext frc = g2.getFontRenderContext();

//		if( this.referenceSequenceObject.getStrand() == '-' ) {
//			System.out.println("Hello");
//		}
		Color systemColor = g2.getColor();
		for(int i=0; i<this.getReferenceObject().size(); i++) {
			RoundRectangle2D.Double rect = this.getReferenceObject().get(i).getBlock();
			rect.setRoundRect( rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), 0, 0);
			String strNucleotide	= this.getReferenceObject().get(i).getNucleotideType();

			Color bgColor = TrackItem.getColorByNucleotideFixed( strNucleotide );
//			Color bgColor = Color.white;
//			if( i % 2 == 0 )	bgColor = new Color(200, 200, 200);
//			else				bgColor = new Color(230, 230, 230);

			if( bgColor.getRGB() < Color.white.getRGB() ) {
				float alpha = 0.6f;
				int type = AlphaComposite.SRC_OVER; 
				AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
				g2.setComposite( composite );

				g2.setColor( bgColor );
				g2.fill( rect );
			}
//			g2.setColor( Color.LIGHT_GRAY );
//			g2.draw( rect );
			g2.setColor( systemColor );

			float alpha = 0.99f;
			int type = AlphaComposite.SRC_OVER; 
			AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
			g2.setComposite( composite );
			g2.setColor( Color.black );
			Point2D.Double newLabelPosition = SwingUtilities.getLabelPositionOnRectangle(frc, g2.getFont(), rect, strNucleotide, SwingUtilities.ALIGN_CENTER);

			g2.drawString( strNucleotide, (float)newLabelPosition.getX(), (float)newLabelPosition.getY());
		}
	}
}