package kobic.msb.swing.panel.track;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kobic.msb.common.SwingConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.Model;
import kobic.msb.server.obj.GenomeReferenceObject;
import kobic.msb.server.obj.RnaSecondaryStructureObj;
import kobic.msb.swing.comparator.RnaSecondaryStructureObjComparator;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;

public class MicroRnaStructureTrack extends TrackItem{
	private GenomeReferenceObject			reference;
	private List<RnaSecondaryStructureObj>	rnaStructureList;
	private String							mirid;
	private char							strand;
	private Rectangle2D.Double				baseRect;

	public MicroRnaStructureTrack( ProjectConfiguration config ) {
		super(config);
		// TODO Auto-generated constructor stub
		
		this.rnaStructureList = null;
	}
	
	public void setReference(GenomeReferenceObject reference) {
		this.reference = reference;
	}
	
	public void setMirid(String mirid) {
		this.mirid = mirid;
	}
	
	public void setStrand(char strand) {
		this.strand = strand;
	}
	
	public void setRnaStructureList( List<RnaSecondaryStructureObj> list ) {
		this.rnaStructureList = this.interpolateBlocks( list );
	}
	
	private List<RnaSecondaryStructureObj> sortByLocation(List<RnaSecondaryStructureObj> list, char strand) {
		Collections.sort( list, new RnaSecondaryStructureObjComparator(strand) );
		
		return list;
	}
	
	private List<RnaSecondaryStructureObj> interpolateBlocks( List<RnaSecondaryStructureObj> list ) {
		List<RnaSecondaryStructureObj> newList = new ArrayList<RnaSecondaryStructureObj>();

		if( list.size() == 1 ){
			RnaSecondaryStructureObj sso = list.get(0);

			if( this.reference.getStrand() == '+' ) {
				long up_start = this.reference.getStartPosition();
				long up_end = sso.getStart() - 1;

				newList.add( Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_5PMOR, SwingConst.MiRNA_2ND_STRUCTURE_5PMOR, (int)up_start, (int)up_end, this.getProjectConfiguration() ) );
				
				newList.add(list.get(0) );
				long down_start = sso.getEnd() + 1;
				long down_end = this.reference.getEndPosition();

				if( mirid.startsWith("Novel") )
					newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_3PMOR, SwingConst.MiRNA_2ND_STRUCTURE_3PMOR, (int)down_start, (int)down_end, this.getProjectConfiguration() ) );
				else
					newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_LOOP, SwingConst.MiRNA_2ND_STRUCTURE_LOOP, (int)down_start, (int)down_end, this.getProjectConfiguration() ) );
			}else {
//				long up_start		= this.reference.getStartPosition();
//				long up_end			= sso.getStart() - 1;
				
				long up_start	 	= sso.getEnd() + 1;
				long up_end			= this.reference.getEndPosition();
				
				newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_5PMOR, SwingConst.MiRNA_2ND_STRUCTURE_5PMOR, (int)up_start, (int)up_end, this.getProjectConfiguration() ) );
				
				newList.add(list.get(0) );
//				long down_start		= sso.getEnd() + 1;
//				long down_end		= this.reference.getEndPosition();
				long down_start		= this.reference.getStartPosition() - 1;
				long down_end		= sso.getStart() - 1;
				
				newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_LOOP, SwingConst.MiRNA_2ND_STRUCTURE_LOOP, (int)down_start, (int)down_end, this.getProjectConfiguration() ) );
			}
		}else if( list.size() > 1 ){
			this.sortByLocation(list, this.reference.getStrand() );

			RnaSecondaryStructureObj _first_sso = list.get(0);
			RnaSecondaryStructureObj _last_sso	= list.get( list.size() - 1 );

			if( this.reference.getStrand() == '+' ) {
				long up_start	 	= this.reference.getStartPosition();
				long up_end			= _first_sso.getStart() - 1;
				
				newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_5PMOR, SwingConst.MiRNA_2ND_STRUCTURE_5PMOR, (int)up_start, (int)up_end, this.getProjectConfiguration() ) );
				newList.add( _first_sso );

				for(int i=1; i<list.size() - 1; i++) {
					long hairpin_start	= list.get(i-1).getEnd() + 1;
					long hairpin_end	= list.get(i).getStart() - 1;
					
//					newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_HAIRPIN, SwingConst.MiRNA_2ND_STRUCTURE_HAIRPIN, (int)hairpin_start, (int)hairpin_end, this.getProjectConfiguration() ) );
					newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_LOOP, SwingConst.MiRNA_2ND_STRUCTURE_LOOP, (int)hairpin_start, (int)hairpin_end, this.getProjectConfiguration() ) );
					newList.add( list.get(i) );
				}
				long hairpin_start	= list.get(list.size() - 2).getEnd() + 1;
				long hairpin_end	= _last_sso.getStart() - 1;
//				newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_HAIRPIN, SwingConst.MiRNA_2ND_STRUCTURE_HAIRPIN, (int)hairpin_start, (int)hairpin_end, this.getProjectConfiguration() ) );
				newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_LOOP, SwingConst.MiRNA_2ND_STRUCTURE_LOOP, (int)hairpin_start, (int)hairpin_end, this.getProjectConfiguration() ) );

				long down_start		= _last_sso.getEnd() + 1;
				long down_end		= this.reference.getEndPosition();
				newList.add( _last_sso );
				newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_3PMOR, SwingConst.MiRNA_2ND_STRUCTURE_3PMOR, (int)down_start, (int)down_end, this.getProjectConfiguration() ) );
			}else {
				long up_start	 	= _first_sso.getEnd() + 1;
				long up_end			= this.reference.getEndPosition();
				
//				newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_3PMOR, SwingConst.MiRNA_2ND_STRUCTURE_3PMOR, (int)up_start, (int)up_end, this.getProjectConfiguration() ) );
				newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_5PMOR, SwingConst.MiRNA_2ND_STRUCTURE_5PMOR, (int)up_start, (int)up_end, this.getProjectConfiguration() ) );

				newList.add( _first_sso );

				for(int i=1; i<list.size() - 2; i++) {
					long hairpin_start	= list.get(i).getEnd() + 1;
					long hairpin_end	= list.get(i-1).getStart() - 1;
					
//					newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_HAIRPIN, SwingConst.MiRNA_2ND_STRUCTURE_HAIRPIN, (int)hairpin_start, (int)hairpin_end, this.getProjectConfiguration() ) );
					newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_LOOP, SwingConst.MiRNA_2ND_STRUCTURE_LOOP, (int)hairpin_start, (int)hairpin_end, this.getProjectConfiguration() ) );
					newList.add( list.get(i) );
				}
				long hairpin_start	= _last_sso.getEnd() +1 ;
				long hairpin_end	= list.get(list.size() - 2).getStart() - 1;
//				newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_HAIRPIN, SwingConst.MiRNA_2ND_STRUCTURE_HAIRPIN, (int)hairpin_start, (int)hairpin_end, this.getProjectConfiguration() ) );
				newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_LOOP, SwingConst.MiRNA_2ND_STRUCTURE_LOOP, (int)hairpin_start, (int)hairpin_end, this.getProjectConfiguration() ) );

				long down_start		= this.reference.getStartPosition() - 1;
				long down_end		= _last_sso.getStart() - 1;
				newList.add( _last_sso );
//				newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_5PMOR, SwingConst.MiRNA_2ND_STRUCTURE_5PMOR, (int)down_start, (int)down_end, this.getProjectConfiguration() ) );
				newList.add(Model.makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_3PMOR, SwingConst.MiRNA_2ND_STRUCTURE_3PMOR, (int)down_start, (int)down_end, this.getProjectConfiguration() ) );
			}
		}
		
		return newList;
	}


	@Override
	public void draw(Graphics2D g2, Rectangle2D.Double baseRect) {
		// TODO Auto-generated method stub
		this.drawHeader(g2);

		this.baseRect = baseRect;
		FontRenderContext frc = g2.getFontRenderContext();
		
		for(RnaSecondaryStructureObj structure:this.rnaStructureList)
			this.drawMicroRnaStructureRectangle(structure, g2, frc);
	}

	private void drawMicroRnaStructureRectangle( RnaSecondaryStructureObj structure, Graphics2D g2, FontRenderContext frc ) {
		long start		= structure.getStart();
		long end		= structure.getEnd();
		long length		= structure.getEnd() - structure.getStart() + 1;
		double unit		= this.getProjectConfiguration().getBlockWidth();
		double width	= length * unit;

		double x = this.getProjectConfiguration().getOffset() + this.getProjectConfiguration().getMargin();

		width = unit * length;

		if( strand == '+' ) x += (( start - this.reference.getStartPosition() ) * unit );
		else				x += ((this.reference.getEndPosition() - end) * unit);

		double y		= this.getVerticalLocation() + 1;
		double height	= this.getTrackHeight() - 2;

		if( x < this.getProjectConfiguration().getOffset() ) {
			width = width - (this.getProjectConfiguration().getOffset() + this.getProjectConfiguration().getMargin() - x);
			x = this.getProjectConfiguration().getOffset() + this.getProjectConfiguration().getMargin();
		}
		
		if( x + width > this.baseRect.getMaxX() ) {
			width = width - ((x+width) - this.baseRect.getMaxX());
		}
		
		Composite currentComposite = g2.getComposite();
		float alpha = 0.5f;
		int type = AlphaComposite.SRC_OVER; 
		AlphaComposite composite = AlphaComposite.getInstance(type, alpha);

		RoundRectangle2D.Double labelRect = new RoundRectangle2D.Double(x,  y, width, height, 10, 10);
		g2.setColor( Color.LIGHT_GRAY );
		g2.draw( labelRect );
		
		g2.setComposite( composite );
		g2.setColor( structure.getColor() );
		g2.fill( labelRect );

		g2.setColor( Color.black );
		g2.setComposite( currentComposite );
		
		String name = structure.getName();
		if( structure.getName().equals( SwingConst.MiRNA_2ND_STRUCTURE_5P ) || structure.getName().equals( SwingConst.MiRNA_2ND_STRUCTURE_3P ) )
			name = this.mirid + "-" + name;
		Point2D.Double newLabelPosition = SwingUtilities.getLabelPositionOnRectangle(frc, g2.getFont(), labelRect, name, SwingUtilities.ALIGN_CENTER);
		
		if( newLabelPosition.getX() > labelRect.getMinX() ) {
			g2.drawString( name, (float)newLabelPosition.getX(), (float)newLabelPosition.getY());
		}

	}
	
	@Override
	public String getRid() {
		return null;
	}

	@Override
	public void setBaseDistanceToView(Model model) {
		// TODO Auto-generated method stub
		
	}
}
