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
import kobic.msb.server.obj.HairpinSequenceObject;
import kobic.msb.server.obj.SequenceObject;
import kobic.msb.swing.panel.alignment.obj.AlignedNucleotide;
import kobic.msb.system.config.ProjectConfiguration;

public class SequenceTrack extends TrackItem{
	private List<AlignedNucleotide>			prematureResult;

	public SequenceTrack(ProjectConfiguration config) {
		super( config );
		// TODO Auto-generated constructor stub
		this.prematureResult = null;
	}

	@Override
	public String getRid() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<AlignedNucleotide> getReferenceObject() {
		return this.prematureResult;
	}

	public List<AlignedNucleotide> getReferenceObjectList( HairpinSequenceObject premature, GenomeReferenceObject genome, List<AlignedNucleotide> genomeResult ) {
		double yPos = this.getVerticalLocation();

		if( premature.getStrand() == '+' ) {
			double xPos = this.getProjectConfiguration().getOffset() + this.getProjectConfiguration().getMargin();
	
			for(int i=0; i<genomeResult.size(); i++) {
				int genomePos = genomeResult.get(i).getPosition();
				if( premature.getStartPosition() == genomePos ) {
					xPos = genomeResult.get(i).getBlock().getMinX();
					break;
				}
			}

			return SwingUtilities.getPrematureSequenceNucleotideObjectList( premature, genomeResult, yPos, this.getProjectConfiguration().getBlockWidth(), this.getProjectConfiguration().getBlockHeight() );
		}

		return SwingUtilities.getPrematureSequenceNucleotideObjectListReverse( premature, genomeResult, yPos, this.getProjectConfiguration().getBlockWidth(), this.getProjectConfiguration().getBlockHeight() );
	}

	@Override
	public void draw(Graphics2D g2, Rectangle2D.Double baseRect) {
		this.drawHeader(g2);
		// TODO Auto-generated method stub
		FontRenderContext frc = g2.getFontRenderContext();

		Color systemColor = g2.getColor();
		for(int i=0; i<this.getReferenceObject().size(); i++) {
			RoundRectangle2D.Double rect = this.getReferenceObject().get(i).getBlock();
			String strNucleotide	= this.getReferenceObject().get(i).getNucleotideType();

//			Color bgColor = this.getColorByNucleotide( strNucleotide );
			Color bgColor = TrackItem.getColorByNucleotideFixed( strNucleotide );
			if( bgColor.getRGB() < Color.white.getRGB() ) {
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

	@Override
	public void setBaseDistanceToView(Model model) {
		// TODO Auto-generated method stub
//		this.referenceResult		= this.getReferenceObjectList( model.getPrematureSequenceObject(), model.getReferenceSequenceObject(), genomeResult );
	}
	
	public void setBaseDistanceToView(Model model, List<AlignedNucleotide> genomeResult) {
		this.prematureResult		= this.getReferenceObjectList( model.getPrematureSequenceObject(), model.getReferenceSequenceObject(), genomeResult );
	}
}
