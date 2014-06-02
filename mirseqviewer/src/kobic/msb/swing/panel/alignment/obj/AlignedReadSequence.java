package kobic.msb.swing.panel.alignment.obj;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.obj.GenomeReferenceObject;
import kobic.msb.server.obj.NucleotideObject;
import kobic.msb.server.obj.ReadFragmentByCigar;
import kobic.msb.server.obj.ReadObject;


public class AlignedReadSequence {
	private List<AlignedNucleotide>	sequence;
	private String					strand;
	private double					currentRecordPos;
	private int						type;
	private int						start;
	private int						end;
	
	public static final int		OUT_LEFT_BLOCK = -2;
	public static final int		IN_BLOCK = 0;
	public static final int		OUT_RIGHT_BLOCK = -1;
	
	public AlignedReadSequence( String strand, int start, int end ) {
		this.sequence = new ArrayList<AlignedNucleotide>();
		this.strand = strand;
		this.start = start;
		this.end = end;
	}
	
	public void						add( AlignedNucleotide obj )	{	this.sequence.add( obj );		}
	public List<AlignedNucleotide>	getSequence()					{	return this.sequence;			}
	public int						getSequenceLength()				{	return this.sequence.size();	}
	public AlignedNucleotide		getAlignedNucleotide(int index)	{
		if( this.sequence.size()-1 < index || index < 0 )
			return null;
		return this.sequence.get(index);
	}
	public String				getSequenceString() {
		String str = "";
		for(AlignedNucleotide an:this.sequence) {
			str += an.getNucleotideType();
		}
		return str;
	}
//	public int getStart(){	return this.sequence.get(0).getPosition();	}
//	public int getEnd(){	return this.sequence.get( this.sequence.size() - 1 ).getPosition();	}
	
	public int getStart(){	
//		if( this.sequence == null )		return 0;
//		if( this.sequence.size() == 0 )	return 0;
//		return this.sequence.get(0).getPosition();
		return this.start;
	}
	public int getEnd(){
//		if( this.sequence == null )		return 0;
//		if( this.sequence.size() == 0 )	return 0;
//		return this.sequence.get( this.sequence.size() - 1 ).getPosition();
		return this.end;
	}

	public String getStrand(){	return this.strand;	}
	public void reOrderingTo(double yPos) {
		for(int i=0; i<this.sequence.size(); i++) {
			RoundRectangle2D.Double rect = this.sequence.get(i).getBlock();
			rect.y = yPos;
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getY() {
		return currentRecordPos;
	}

	public void setY(double y) {
		this.currentRecordPos = y;
	}
	
	public boolean isDrawable() {
		if( this.sequence.size() > 0 )	return true;
		return false;
	}

	public Rectangle2D.Double getBoundary() {
		if( this.sequence.size() > 0 ) {
			double minX = Double.MAX_VALUE; 
			double maxX = Double.MIN_VALUE;
			double minY = Double.MAX_VALUE;
			double maxY = Double.MIN_VALUE;
			for(int i=0; i<this.sequence.size(); i++) {
				if( this.sequence.get(i).getBlock().getMinX() < minX )	minX = this.sequence.get(i).getBlock().getMinX();
				if( this.sequence.get(i).getBlock().getMaxX() > maxX )	maxX = this.sequence.get(i).getBlock().getMaxX();
				if( this.sequence.get(i).getBlock().getMinY() < minY )	minY = this.sequence.get(i).getBlock().getMinY();
				if( this.sequence.get(i).getBlock().getMaxY() > maxY )	maxY = this.sequence.get(i).getBlock().getMaxY();
			}

			Rectangle2D.Double rect = new Rectangle2D.Double( minX, minY, (maxX-minX), (maxY-minY) );
			
			return rect;
		}
		return null;
	}
	
	public Shape getShape(int index) {
		return null;
	}
}
