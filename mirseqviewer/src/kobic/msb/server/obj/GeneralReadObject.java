package kobic.msb.server.obj;

import java.util.ArrayList;
import java.util.List;

import kobic.com.util.Utilities;

public class GeneralReadObject extends ReadObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	private MsvSamRecord record;
	private List<ReadObject> elements;
	
	public GeneralReadObject() {
		super();
		this.elements = null;
	}

	public GeneralReadObject( MsvSamRecord record ) {
		super( record.getStart(), record.getEnd(), record.getReadSeq(), record.getStrand() );

//		this.record = this.validation( record );
		this.elements = this.validation(record);
	}

	private List<ReadObject> validation( MsvSamRecord record) {
		List<ReadObject> elements = new ArrayList<ReadObject>();

		
		for(ReadFragmentByCigar cigar : record.getCigarElements()) {
			int start = cigar.getStart();
			int end = cigar.getEnd();
			if( record.getStrand() == '-' ) {
				start	= cigar.getStart() - 1;
				end		= cigar.getEnd() - 1;
			}

			ReadObject read = new ReadObject( start, end, cigar.getReadSeq(), record.getStrand() );
			read.setReadQuality( record.getReadQuality() );
			
			elements.add( read );
		}
		
		return elements;
	}
	
//	public List<ReadFragmentByCigar> getReadFragments() {
//		return this.record.getCigarElements();
//	}
//	
//	@Override
//	public List<NucleotideObject> makeListFromSequence(long startPos, String sequence) {
//		List<NucleotideObject> seqList = new ArrayList<NucleotideObject>();
//
//		sequence = sequence.trim();
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
//	
//	public MsvSamRecord getMsvSamRecord() {
//		return this.record;
//	}
	
	public List<ReadObject> getRecordElements() {
		return this.elements;
	}
}
