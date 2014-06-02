package kobic.msb.server.obj;

public class SAMInfo implements java.io.Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String chr;
    private String readName;
    private String readSeq;
    private  int start;
    private  int end;
    private char strand;
    private int count;

//    private int originalStart;
//    private int originalEnd;
    
    private SAMInfo matePair;
    
    private ReadQuality readQuality;
    private int mapq;
    private String nh;
    
    public SAMInfo() {
    	this("", "", "", '+', 0, 0, "0", 0);
    }
    
    public SAMInfo(String chr, String readName, String readSeq, char strand, int start, int end, String nh, int mapq) {
    	this( chr, readName, readSeq, strand, start, end, 1, nh, mapq );
    }
    
    public SAMInfo(String chr, String readName, String readSeq, char strand, int start, int end, int count, String nh, int mapq) {
    	this.chr = chr;
    	this.readName = readName;
    	this.readSeq = readSeq;
    	this.start = start;
    	this.end = end;
    	this.strand = strand;
    	this.mapq = mapq;
    	this.nh = nh;
    	this.count = count;
    	this.matePair= null;
    }
    
    public SAMInfo(String chr, String readName, String readSeq, char strand, int start, int end, int count, ReadQuality readQuality) {
    	this.chr = chr;
    	this.readName = readName;
    	this.readSeq = readSeq;
    	this.start = start;
    	this.end = end;
    	this.strand = strand;
    	this.readQuality = readQuality;
    	this.mapq = readQuality.getMapq();
    	this.nh = readQuality.getNh();
    	this.count = count;
    	this.matePair = null;
    }
    
    

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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

	public int getMapq() {
		return mapq;
	}

	public void setMapq(int mapq) {
		this.mapq = mapq;
	}

	public String getNh() {
		return nh;
	}

	public void setNh(String nh) {
		this.nh = nh;
	}

	public char getStrand() {
		return strand;
	}

	public void setStrand(char strand) {
		this.strand = strand;
	}

	public ReadQuality getReadQuality() {
		return readQuality;
	}

	public void setReadQuality(ReadQuality readQuality) {
		this.readQuality = readQuality;
	}

//	public int getOriginalStart() {
//		return originalStart;
//	}
//
//	public void setOriginalStart(int originalStart) {
//		this.originalStart = originalStart;
//	}
//
//	public int getOriginalEnd() {
//		return originalEnd;
//	}
//
//	public void setOriginalEnd(int originalEnd) {
//		this.originalEnd = originalEnd;
//	}

	public SAMInfo getMatePair() {
		return matePair;
	}

	public void setMatePair(SAMInfo matePair) {
		this.matePair = matePair;
	}
	
	public String getPublicKey() {
		return this.getReadSeq() + "^" + this.getChr() + ":" + this.getStart() + "$" + this.getEnd() + "&" + this.getStrand();
	}
}
