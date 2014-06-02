package kobic.msb.server.obj;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.system.engine.MsbEngine;
import net.sf.samtools.BAMRecord;
import net.sf.samtools.Cigar;
import net.sf.samtools.CigarElement;
import net.sf.samtools.CigarOperator;
import net.sf.samtools.SAMRecord;

public class MsvSamRecord implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String			chr;
    private String			readName;
    private char			strand;
    private String			readSeq;
    private int				start;
    private int				end;
    private int				count;
    private int				mismatch;
    private ReadQuality 	readQuality;
    
    private List<ReadFragmentByCigar> cigarElements;
    
    public MsvSamRecord() {
    	this.chr = null;
    	this.readName = null;
    	this.strand = 0;
    	this.readSeq = null;
    	this.start = -1;
    	this.end = -1;
    	this.count = 0;
    	this.mismatch = 0;
    	this.readQuality = null;
    }

	private static String getReadCount( String readName ) {
        Pattern p = Pattern.compile( "^(.+)_x(\\d+)$" );
        Matcher m = p.matcher( readName );

        String count = "0";
        while( m.find() ) {
        	count = m.group(2);
        }
        if( count.equals("0") ) {
        	p = Pattern.compile( "^(.+)_(\\d+)$" );
	        m = p.matcher( readName );

	        count = "1";
	        while( m.find() ) {
	        	count = m.group(2);
	        }
        }
        if( count == "0" )	count = "1";
        
        return count;
	}
	
	private static MsvSamRecord getMsvSamWithGenome( BAMRecord record, HairpinVO hairpinVo ) throws Exception{
		char	strand		= hairpinVo.getStrand().charAt(0);

		String premature_id	= hairpinVo.getId();
        String readName 	= record.getReadName();
        String readSeq		= record.getReadString();

        ReadQuality readQual = new ReadQuality( record );

        int readStart	= record.getAlignmentStart();
        int readEnd 	= record.getAlignmentEnd();
        
        int mismatchCount = 0;
        try {
			mismatchCount = MsvSamRecord.countMismatches( record );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			mismatchCount = 0;
			MsbEngine.logger.error("Error", e);
		}

        MsvSamRecord msr = new MsvSamRecord( premature_id, readName, readSeq, strand, readStart, readEnd, Integer.parseInt( MsvSamRecord.getReadCount(readName) ), readQual, mismatchCount );

        Integer currentReferencePos = readStart;

//        List<AlignmentBlock> blocks = record.getAlignmentBlocks();
//
//        for(int i=0; i<blocks.size(); i++) {
//        	AlignmentBlock block = blocks.get(i);
//        	
//        	int rStart = block.getReferenceStart();
//        	int rEnd = block.getReferenceStart() + block.getLength() - 1;
//        	
//        	String subReadSeq = readSeq.substring( block.getReadStart()-1, block.getReadStart() + block.getLength() - 1 );
//        	
//        	ReadFragmentByCigar info = new ReadFragmentByCigar( subReadSeq, rStart, rEnd, null );
//    		msr.addReadFragmentByCigar( info );
//        }

        readSeq = msr.getReadSeq();
        int offSet = 0;
        for (CigarElement cigarElement :record.getCigar().getCigarElements()) {
        	if( cigarElement.getOperator() == CigarOperator.M ) {
        		int rStart	= currentReferencePos;
        		int rEnd	= currentReferencePos + cigarElement.getLength() ;

        		String subReadSeq = readSeq.substring( offSet, offSet + cigarElement.getLength() );

//        		try {
//    			byte[] baseQuality = Arrays.copyOfRange( readQual.getBasePhredQuality(), offSet, offSet+cigarElement.getLength()-1 );
    			ReadFragmentByCigar info = new ReadFragmentByCigar( subReadSeq, rStart, rEnd, null );
        		msr.addReadFragmentByCigar( info );
//        		}catch(Exception e) {
//        			MsbEngine.logger.error("error", e);
//        		}

                offSet += cigarElement.getLength();

                currentReferencePos = rEnd;
        	}else if( cigarElement.getOperator() == CigarOperator.N || cigarElement.getOperator() == CigarOperator.D ) {
                currentReferencePos += cigarElement.getLength();
        	}else if( cigarElement.getOperator() == CigarOperator.I ) {
        		currentReferencePos -= cigarElement.getLength();
        	}else if( cigarElement.getOperator() == CigarOperator.S || cigarElement.getOperator() == CigarOperator.H ) {
        		offSet += cigarElement.getLength();
        	}
        }

        return msr;
	}

	private static MsvSamRecord getMsvSamWithMirBase( BAMRecord record, HairpinVO hairpinVo ) {
		char	strand		= hairpinVo.getStrand().charAt(0);

		String premature_id	= hairpinVo.getId();
        String readName 	= record.getReadName();
        String readSeq		= record.getReadString();

        ReadQuality readQual = new ReadQuality( record );

        int readStart	= record.getAlignmentStart() + hairpinVo.getIntegerStart() - 1;
        int readEnd 	= record.getAlignmentEnd() + hairpinVo.getIntegerStart() - 1;

        if ( hairpinVo.getStrand().equals("-") ) {
        	readStart	= hairpinVo.getIntegerEnd() - record.getAlignmentEnd() + 1;
        	readEnd		= hairpinVo.getIntegerEnd() - record.getAlignmentStart() + 1;

        	readSeq = Utilities.getReverseComplementary( readSeq );
        }

        int mismatchCount = 0;
        try {
			mismatchCount = MsvSamRecord.countMismatches( record );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			mismatchCount = 0;
		}

        MsvSamRecord msr = new MsvSamRecord( premature_id, readName, readSeq, strand, readStart, readEnd, Integer.parseInt( MsvSamRecord.getReadCount(readName) ), readQual, mismatchCount );
        
        Integer currentReferencePos = readStart;

        readSeq = msr.getReadSeq();
        int offSet = 0;
        if( hairpinVo.getStrand().equals("-") ) {
//            List<AlignmentBlock> blocks = record.getAlignmentBlocks();
//
//            for(int i=blocks.size()-1; i>=0; i--) {
//            	AlignmentBlock block = blocks.get(i);
//            	
//            	int rStart = hairpinVo.getIntegerEnd() - (block.getReferenceStart() + block.getLength()-1) + 2;
//            	int rEnd = hairpinVo.getIntegerEnd() - block.getReferenceStart() + 2;
//
//            	String subReadSeq = readSeq.substring( block.getReadStart()-1, block.getReadStart() + block.getLength() - 1 );
//            	
//            	ReadFragmentByCigar info = new ReadFragmentByCigar( subReadSeq, rStart, rEnd, null );
//        		msr.addReadFragmentByCigar( info );
//            }

        	for(int i=record.getCigar().getCigarElements().size()-1; i>=0; i--) {
        		CigarElement cigarElement = record.getCigar().getCigarElement(i);

	        	if( cigarElement.getOperator() == CigarOperator.M ) {
	        		int rStart	= currentReferencePos;
	        		int rEnd	= currentReferencePos + cigarElement.getLength()-1;

	        		String subReadSeq = readSeq.substring( offSet, offSet + cigarElement.getLength()-1 );

	//        		try {
	//    			byte[] baseQuality = Arrays.copyOfRange( readQual.getBasePhredQuality(), offSet, offSet+cigarElement.getLength()-1 );
	    			ReadFragmentByCigar info = new ReadFragmentByCigar( subReadSeq, rStart, rEnd, null );
	        		msr.addReadFragmentByCigar( info );
	//        		}catch(Exception e) {
	//        			MsbEngine.logger.error("error", e);
	//        		}
	
	                offSet += cigarElement.getLength();
	
	                currentReferencePos = rEnd;
	        	}else if( cigarElement.getOperator() == CigarOperator.N || cigarElement.getOperator() == CigarOperator.D ) {
	                currentReferencePos += cigarElement.getLength();
	        	}else if( cigarElement.getOperator() == CigarOperator.I ) {
	        		currentReferencePos -= cigarElement.getLength();
	        	}else if( cigarElement.getOperator() == CigarOperator.S || cigarElement.getOperator() == CigarOperator.H ) {
	        		offSet += cigarElement.getLength();
	        	}
	        }
        }else {
//            List<AlignmentBlock> blocks = record.getAlignmentBlocks();
//
//            for(int i=0; i<blocks.size(); i++) {
//            	AlignmentBlock block = blocks.get(i);
//            	
//            	int rStart = hairpinVo.getIntegerStart() + block.getReferenceStart();
//            	int rEnd = rStart + block.getLength() - 1;
//            	
//            	String subReadSeq = readSeq.substring( block.getReadStart()-1, block.getReadStart() + block.getLength() - 1 );
//            	
//            	ReadFragmentByCigar info = new ReadFragmentByCigar( subReadSeq, rStart, rEnd, null );
//        		msr.addReadFragmentByCigar( info );
//            }
        	
	        for (CigarElement cigarElement :record.getCigar().getCigarElements()) {
	        	if( cigarElement.getOperator() == CigarOperator.M ) {
	        		int rStart	= currentReferencePos;
	        		int rEnd	= currentReferencePos + cigarElement.getLength()-1;
	 
	        		String subReadSeq = readSeq.substring( offSet, offSet + cigarElement.getLength()-1 );
	        		
	//              if ( flagValue == JMsbSysConst.WITH_MIRBASE && hairpinVo.getStrand().equals("-") )
	//            	  subReadSeq = Utilities.getReverseComplementary( subReadSeq );
	              
	
	//        		try {
	//    			byte[] baseQuality = Arrays.copyOfRange( readQual.getBasePhredQuality(), offSet, offSet+cigarElement.getLength()-1 );
	    			ReadFragmentByCigar info = new ReadFragmentByCigar( subReadSeq, rStart, rEnd, null );
	        		msr.addReadFragmentByCigar( info );
	//        		}catch(Exception e) {
	//        			MsbEngine.logger.error("error", e);
	//        		}
	
	                offSet += cigarElement.getLength();
	
	                currentReferencePos = rEnd;
	        	}else if( cigarElement.getOperator() == CigarOperator.N || cigarElement.getOperator() == CigarOperator.D ) {
	                currentReferencePos += cigarElement.getLength();
	        	}else if( cigarElement.getOperator() == CigarOperator.I ) {
	        		currentReferencePos -= cigarElement.getLength();
	        	}else if( cigarElement.getOperator() == CigarOperator.S || cigarElement.getOperator() == CigarOperator.H ) {
	        		offSet += cigarElement.getLength();
	        	}
	        }
        }

        return msr;
	}

    public static MsvSamRecord getNewInstance( BAMRecord record, HairpinVO hairpinVo, int flagValue ) throws Exception{
    	if( flagValue == JMsbSysConst.WITH_GENOME )		return MsvSamRecord.getMsvSamWithGenome(record, hairpinVo);
    	
    	return MsvSamRecord.getMsvSamWithMirBase(record, hairpinVo);
    }

    public MsvSamRecord(String chr, String readName, String readSeq, char strand, int start, int end, int count, ReadQuality readQuality, int mismatch) {
    	this.chr = chr;
    	this.readName = readName;
//    	this.readSeq = strand=='+'?readSeq:Utilities.getComplementary(readSeq);
    	this.readSeq = readSeq;
    	this.start = start;
    	this.end = end;
    	this.strand = strand;
    	this.readQuality = readQuality;
    	this.count = count;
    	this.mismatch = mismatch;
    	
    	this.cigarElements = new ArrayList<ReadFragmentByCigar>();
    }

    public MsvSamRecord(String chr, String readName, String readSeq, char strand, int start, int end, int mismatch) {
    	this( chr, readName, readSeq, strand, start, end, 0, new ReadQuality(), mismatch );
    }

	public String getChr() {
		return chr;
	}

	public void setChr(String chr) {
		this.chr = chr;
	}

	public String getReadName() {
		return readName;
	}

	public void setReadName(String readName) {
		this.readName = readName;
	}

	public char getStrand() {
		return strand;
	}

	public void setStrand(char strand) {
		this.strand = strand;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public ReadQuality getReadQuality() {
		return readQuality;
	}

	public String getReadSeq() {
		return readSeq;
	}

	public void setReadSeq(String readSeq) {
		this.readSeq = readSeq;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public void setReadQuality(ReadQuality readQuality) {
		this.readQuality = readQuality;
	}
	
	public void addReadFragmentByCigar( ReadFragmentByCigar fragment ) {
		this.cigarElements.add( fragment );
	}

	public List<ReadFragmentByCigar> getCigarElements() {
		return cigarElements;
	}

	public void setCigarElements(List<ReadFragmentByCigar> cigarElements) {
		this.cigarElements = cigarElements;
	}
	
	public String getPublicKey() {
		return this.getReadSeq() + "^" + this.getChr() + ":" + this.getStart() + "$" + this.getEnd() + "&" + this.getStrand();
	}
	
	public String getPublicKeyWithoutCigar() {
		String code = this.chr + "&";
		for(int i=0; i<this.cigarElements.size(); i++){
			ReadFragmentByCigar fragment = this.cigarElements.get(i);
			code += fragment.getReadSeq() + ":" + fragment.getStart() + "-" + fragment.getEnd();
			if( i < this.cigarElements.size() - 1 )	code += "^";
		}
		return code;
	}

	public int getPublicKeyHashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		
		builder.append( this.getReadSeq() );
		builder.append( this.getChr() );
		builder.append( this.getStart() );
		builder.append( this.getEnd() );
		builder.append( this.getStrand() );

		return builder.toHashCode();
	}

	public boolean hasMultiFragments() {
		if( this.cigarElements.size() > 1 )	return true;
		return false;
	}
	
	public boolean isOverlappedWithPremature(int start, int end) {
		for(int i=0; i<this.cigarElements.size(); i++) {
			ReadFragmentByCigar element = this.cigarElements.get(i);
			if( Utilities.isOverlapped( start, end, element.getStart(), element.getEnd() ) )
				return true;
		}
		return false;
	}
	
	public int getMismatchCount() {
		return this.mismatch;
	}
	
	public static int countMismatches( SAMRecord samRecord ) throws Exception {
		int numMismatches = 0;
	    
		if( samRecord.getReadUnmappedFlag() ) {
			throw new Exception("Error : Cannot count mapping mismatch for unmapped read");
		}
		String mdTag = (String) samRecord.getAttribute("MD");

		int sum = 0;
		if( mdTag == null || mdTag.isEmpty() ) {
//			throw new Exception("Error : MD tag is NULL / Empty");
//			MsbEngine.logger.error("Error : MD tag is NULL / Empty");
			sum = 0;
		}else {
			sum += MsvSamRecord.countMismatchesInMDTag(mdTag);
		}

		numMismatches = sum + MsvSamRecord.countMismatchesInCIGAR(samRecord.getCigar());
		return numMismatches;
	}
	  
	  /**
	   * Method to count the number of mismatches in the MD tag.
	   * We use MD tag to count the number of mismatches and deletions.
	   * @param mdTag
	   * @return
	   */
	private static int countMismatchesInMDTag(String mdTag) {
		int mismatches = 0;
	    
	    /**
	     * In the MD tag, a base (i.e. a letter character) implies either a
	     * mismatch or a deletion.
	     */
		for(int i = 0; i < mdTag.length(); i++) {
			if(mdTag.charAt(i) >= 'A' && mdTag.charAt(i) <= 'Z' || mdTag.charAt(i) >= 'a' && mdTag.charAt(i) <= 'z') {
				mismatches++;
			}
		}
		return mismatches;
	}
	  
	  /**
	   * Method to count number of mismatches from the CIGAR string.
	   * We count the number of insertions, padding, soft and hard clips.
	   * @param cg
	   * @return
	   */
	private static int countMismatchesInCIGAR(Cigar cigar) {
		int numMismatches = 0;
	    
		List<CigarElement> cigars = cigar.getCigarElements();

		for (CigarElement cig : cigars) {
			if(cig.getOperator().name().equals("I") || // Insertion
					cig.getOperator().name().equals("N") || // Skipped region from reference
					cig.getOperator().name().equals("H") || // Hard-clip
					cig.getOperator().name().equals("S") || // Soft-clip
					cig.getOperator().name().equals("P"))   // Padding
			{
				numMismatches += cig.getLength();
			}
		}
		return numMismatches;
	}

	public boolean equals( MsvSamRecord info ) {
		if( this.cigarElements.size() != info.getCigarElements().size() )	return false;

		int result = 0;
		for(int i=0; i<this.cigarElements.size(); i++) {
			List<ReadFragmentByCigar> lst = info.getCigarElements();
			for(int j=0; j<lst.size(); j++) {
				if( this.cigarElements.get(i).equals( lst.get(j) ) )	result++;
			}
		}

		return result==this.cigarElements.size()?true:false;
	}
}
