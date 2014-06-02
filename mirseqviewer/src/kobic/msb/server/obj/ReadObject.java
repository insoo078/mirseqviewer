package kobic.msb.server.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kobic.com.util.Utilities;


public class ReadObject extends SequenceObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String	groupId;
	private int		count;
	private int		experimentalMachine;
	private String	sampleId;
	private String	rid;
	private int		type;
	
	private ReadObject matePair;

	@SuppressWarnings("rawtypes")
	private HashMap hash;
	
	private ReadQuality				readQuality;
//	private int						nh;
//	private int						mapq;
	
//	public ReadObject() {
//		super();
//	}
	
	public ReadObject() {
		super();
		this.groupId = null;
		this.count = 0;
		this.experimentalMachine = 0;
		this.sampleId = null;
		this.rid = null;
		this.type = 0;
	}

	public ReadObject(int startPos, int endPos, String sequence) {
		super(startPos, endPos, sequence);
//		this.type = Const.READ_SEQ_TYPE;
	}
	
	public ReadObject(int startPos, int endPos, String sequence, char strand) {
		super(startPos, endPos, sequence, strand);
//		this.type = Const.READ_SEQ_TYPE;
	}
	
	public ReadObject(int startPos, int endPos, String sequence, String sampleId) {
		this(startPos, endPos, sequence);
		this.sampleId = sampleId;
	}

	public ReadObject(int startPos, int endPos, String sequence, String sampleId, char strand) {
		this(startPos, endPos, sequence);
		this.sampleId = sampleId;
		this.setStrand(strand);
	}

	public ReadObject(int startPos, int endPos, String sequence, String sampleId, char strand, String rid) {
		this(startPos, endPos, sequence, sampleId, strand);
		this.rid = rid;
	}
	
	public ReadObject(int startPos, int endPos, String sequence, String sampleId, char strand, String rid, int type) {
		this(startPos, endPos, sequence, sampleId, strand, rid);
		this.type = type;
	}
	
	public String getGroupId() {
		return groupId;
	}

	public int getType() {
		return this.type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}



	public int getCount() {
		return count;
	}



	public void setCount(int count) {
		this.count = count;
	}



	public int getExperimentalMachine() {
		return experimentalMachine;
	}



	public void setExperimentalMachine(int experimentalMachine) {
		this.experimentalMachine = experimentalMachine;
	}



	public String getSampleId() {
		return sampleId;
	}



	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}



	@SuppressWarnings("rawtypes")
	public HashMap getHash() {
		return hash;
	}



	@SuppressWarnings("rawtypes")
	public void setHash(HashMap hash) {
		this.hash = hash;
	}



	public boolean isEqual( ReadObject obj ) {
		long diffa = this.getEndPosition() - obj.getEndPosition();
		long diffb = this.getStartPosition() - obj.getStartPosition();
		boolean iseq = this.getSequenceByString().equals( obj.getSequenceByString() );
		
		if( diffa == 0 && diffb == 0 && iseq == true ) 
			return true;
		return false;
	}
	
	public boolean containsAt( List<ReadObject> list ) {
		for( ReadObject obj:list)  {
			if( obj.equals(this) )
				return true;
		}
		return false;
	}
//
//	public int getNh() {
//		return nh;
//	}
//
//	public void setNh(int nh) {
//		this.nh = nh;
//	}
//
//	public int getMapq() {
//		return mapq;
//	}
//
//	public void setMapq(int mapq) {
//		this.mapq = mapq;
//	}

	public ReadQuality getReadQuality() {
		return readQuality;
	}

	public void setReadQuality(ReadQuality readQuality) {
		this.readQuality = readQuality;
	}

	public ReadObject getMatePair() {
		return matePair;
	}

	public void setMatePair(ReadObject matePair) {
		this.matePair = matePair;
	}
	
	public boolean hasMatePair() {
		if( this.matePair != null )	return true;
		return false;
	}

	@Override
	public List<NucleotideObject> makeListFromSequence(long startPos, String sequence) {
		List<NucleotideObject> seqList = new ArrayList<NucleotideObject>();

		sequence = sequence.trim();
		if( this.getStrand() == '-' )	{
			sequence = Utilities.getReverseComplementary( sequence );
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