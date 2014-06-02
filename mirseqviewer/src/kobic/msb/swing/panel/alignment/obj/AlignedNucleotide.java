package kobic.msb.swing.panel.alignment.obj;

import java.awt.geom.RoundRectangle2D;

import kobic.msb.server.obj.NucleotideObject;

public class AlignedNucleotide extends NucleotideObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RoundRectangle2D.Double	block;

	public AlignedNucleotide( NucleotideObject nObj, RoundRectangle2D.Double rect ) {
		this( nObj );
		this.block			= rect;
	}
	
	public AlignedNucleotide( NucleotideObject nObj ) {
		this.copyFromParent( nObj );
	}

	public RoundRectangle2D.Double 	getBlock()							{	return this.block;			}
	public void					setBlock(RoundRectangle2D.Double rect)	{	this.block = rect;			}
	private void				copyFromParent(NucleotideObject nObj){
		this.setColor( nObj.getColor() );
		this.setCoordinate( nObj.getCoordinate() );
		this.setNucleotideType( nObj.getNucleotideType() );
		this.setPosition( nObj.getPosition() );
	}
}
