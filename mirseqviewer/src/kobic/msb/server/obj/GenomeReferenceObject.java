//package kobic.msb.server.obj;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import kobic.com.util.Utilities;
//
//public class GenomeReferenceObject extends SequenceObject {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	public GenomeReferenceObject( String chromosome, long startPos, long length, char strand, String sequence ) {
//		super(chromosome, startPos, length, strand, sequence);
//	}
//	
//	public GenomeReferenceObject(int startPos, int length, String sequence, char strand) {
//		this("", startPos, length, strand, sequence);
//	}
//
//	public GenomeReferenceObject(int startPos, int length, String sequence) {
//		this("", startPos, length, ' ', sequence);
//	}
//
//	public GenomeReferenceObject(int startPos, String sequence) {
//		this( startPos, sequence.length(), sequence);
//	}
//	
//	public GenomeReferenceObject(int startPos, String sequence, char strand) {
//		this( startPos, sequence.length(), sequence, strand);
//	}
//	
//	public GenomeReferenceObject(String sequence) {
//		this( 1, sequence.length(), sequence );
//	}
//
//	public GenomeReferenceObject() {
//		this("");
//	}
//}

package kobic.msb.server.obj;

import java.util.ArrayList;
import java.util.List;

import kobic.com.util.Utilities;

public class GenomeReferenceObject extends SequenceObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public GenomeReferenceObject( String chromosome, long startPos, long endPos, long length, char strand, String sequence ) {
		super( chromosome, startPos, endPos, length, strand, sequence );
	}
	
	public GenomeReferenceObject(int startPos, int endPos, int length, String sequence, char strand) {
		this("", startPos, endPos, length, strand, sequence);
	}

	public GenomeReferenceObject(int startPos, int endPos, int length, String sequence) {
		this("", startPos, endPos, length, ' ', sequence);
	}

	public GenomeReferenceObject(int startPos, int endPos, String sequence) {
		this( startPos, endPos, sequence.length(), sequence);
	}
	
	public GenomeReferenceObject(int startPos, int endPos, String sequence, char strand) {
		this( startPos, endPos, sequence.length(), sequence, strand);
	}
	
	public GenomeReferenceObject(String sequence) {
		this( 1, sequence.length(), sequence );
	}

	public GenomeReferenceObject() {
		this("");
	}

	@Override
	public List<NucleotideObject> makeListFromSequence( long startPos, String sequence ) {
		List<NucleotideObject> seqList = new ArrayList<NucleotideObject>();

		sequence = sequence.trim();
		if( this.getStrand() == '-' )	{
//			sequence = Utilities.reverseSequence( sequence );
			this.setStrSequence( sequence );
		}
		for(int i=0; (sequence!=null && i<sequence.length()); i++) {
			NucleotideObject nucleotide = new NucleotideObject();

			nucleotide.setNucleotideType( Character.toString(sequence.charAt(i)) );
			if( this.getStrand() == '-' )	nucleotide.setPosition( (int)this.getEndPosition() - i );
			else							nucleotide.setPosition( (int)startPos + i );
//			nucleotide.setPosition( (int)startPos + i );

			seqList.add( nucleotide );
		}
		return seqList;
	}
}
