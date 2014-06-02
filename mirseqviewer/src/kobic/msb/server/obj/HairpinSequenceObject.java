package kobic.msb.server.obj;

import kobic.msb.db.sqlite.vo.HairpinVO;

public class HairpinSequenceObject extends GenomeReferenceObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HairpinSequenceObject( HairpinVO vo ) {
		super(vo.getChr(), (long)vo.getIntegerStart(), (long)vo.getIntegerEnd(), (long)(vo.getIntegerEnd() - vo.getIntegerStart() + 1), vo.getStrand().charAt(0), vo.getSequence() );
	}
	
	public HairpinSequenceObject( String chr, long start, long endPos, long length, char strand, String sequence ) {
		super(chr, start, endPos, length, strand, sequence);
	}
	
	public HairpinSequenceObject() {
		super();
	}
}