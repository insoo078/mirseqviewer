package kobic.msb.db.sqlite.vo;

public class HairpinVO {
	private String id;
	private String accession;
	private String sequence;
	private String chr;
	private String strand;
	private String start;
	private String end;
	
	public HairpinVO() {
		this(null, null, null, null, null, null, null);
	}

	public HairpinVO(String id, String accession, String sequence, String chr, String strand, String start, String end) {
		this.id = id;
		this.accession = accession;
		this.sequence = sequence;
		this.chr = chr;
		this.strand = strand;
		this.start = start;
		this.end = end;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public String getChr() {
		return chr;
	}
	public void setChr(String chr) {
		this.chr = chr;
	}
	public String getStrand() {
		return strand;
	}
	public void setStrand(String strand) {
		this.strand = strand;
	}
	public String getStart() {
		return start;
	}
	public int getIntegerStart() {
		if( this.start == null )	return 0;
		if( this.start.isEmpty() )	return 0;
		
		return Integer.parseInt( this.start );
	}
	public void setStart(String start) {
		this.start = start;
	}
	public int getIntegerEnd() {
		if( this.end == null )		return 0;
		if( this.end.isEmpty() )	return 0;
		
		return Integer.parseInt( this.end );
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	
}
