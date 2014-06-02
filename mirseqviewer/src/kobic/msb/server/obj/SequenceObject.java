package kobic.msb.server.obj;

import java.util.ArrayList;
import java.util.List;

import kobic.com.util.Utilities;

public abstract class SequenceObject implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String					chromosome;
	private long					startPosition;
	private char					strand;
	private long					endPosition;
	private List<NucleotideObject>	sequence;
	private String					strSeq;
	private String					sid;

	public SequenceObject() {
		this.sequence = null;
		this.chromosome = null;
		this.startPosition = 0;
		this.strand = 0;
		this.endPosition = 0;
		this.strSeq = null;
		this.sid = null;
	}

	public String getSequenceid() {
		return sid;
	}

	public void setSequenceid(String sid) {
		this.sid = sid;
	}

	public SequenceObject( String chromosome, long startPos, long endPos, long length, char strand, String sequence ) {
		this.sequence		= new ArrayList<NucleotideObject>();
		
		this.chromosome		= chromosome;

		this.strand			= strand;
		this.startPosition	= startPos;
		this.endPosition	= endPos;

		this.strSeq			= sequence;
		this.setSequence(sequence);
	}
	
	public SequenceObject(int startPos, int endPos, int length, String sequence, char strand) {
		this("", startPos, endPos, length, strand, sequence);
	}

	public SequenceObject(int startPos, int endPos, int length, String sequence) {
		this("", startPos, endPos, length, ' ', sequence);
	}

	public SequenceObject(int startPos, int endPos, String sequence) {
		this( startPos, endPos, sequence.length(), sequence);
	}
	
	public SequenceObject(int startPos, int endPos, String sequence, char strand) {
		this( startPos, endPos, sequence.length(), sequence, strand);
	}
	
//	public SequenceObject(String sequence) {
//		this( 1, sequence.length(), sequence );
//	}
//
//	public SequenceObject() {
//		this("");
//	}
	
	public int getLength() {
		return sequence.size();
	}

	public List<NucleotideObject> getSequence() {
		return this.sequence;
	}
	
	public void setStrand(char strand) {
		this.strand = strand;
	}
	
	public char getStrand() {
		return this.strand;
	}

	public String getSequenceByString() {
		return this.strSeq;
	}

	public void setSequence(String sequence) {
		this.strSeq = sequence;

		this.sequence = this.makeListFromSequence(this.startPosition, this.strSeq);
	}
	
	public void setStrSequence(String sequence) {
		this.strSeq = sequence;
	}
	
	public void setSequence(List<NucleotideObject> list) {
		this.sequence = list;
		this.strSeq = "";
		for(int i=0; i<list.size(); i++)
			this.strSeq += list.get(i).getNucleotideType();
	}
	
	public String getChromosome() {
		return this.chromosome;
	}
	
	public void setChromosome(String chromosome ) {
		this.chromosome = chromosome;
	}
	
	public void setStartPosition(int startPos) {
		this.startPosition = startPos;
	}
	public long getStartPosition() {
		return this.startPosition;
	}
	public void setEndPosition(int endPos) {
		this.endPosition = endPos;
	}
	public long getEndPosition() {
		return this.endPosition;
	}
//	private List<NucleotideObject> makeListFromSequence( long startPos, String sequence ) {
//		List<NucleotideObject> seqList = new ArrayList<NucleotideObject>();
//
//		sequence = sequence.trim();
//		if( this.strand == '-' )	sequence = Utilities.getComplementary(sequence);
//		for(int i=0; (sequence!=null && i<sequence.length()); i++) {
//			NucleotideObject nucleotide = new NucleotideObject();
//
//			nucleotide.setNucleotideType( Character.toString(sequence.charAt(i)) );
//			nucleotide.setPosition( (int)startPos + i );
//
//			seqList.add( nucleotide );
//		}
//		return seqList;
//	}
	public abstract List<NucleotideObject> makeListFromSequence( long startPos, String sequence );
}