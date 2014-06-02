package kobic.msb.server.obj;

public class ReadFragmentByCigar implements java.io.Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String readSeq;
    private  int start;
    private  int end;
    private byte[] baseQuality;
    
    public ReadFragmentByCigar(){
    	this.readSeq = "";
    	this.start = -1;
    	this.end = -1;
    	this.baseQuality = null;
    }
    
    public ReadFragmentByCigar(String seq, int start, int end, byte[] baseQuality) {
    	this.readSeq = seq;
    	this.start = start;
    	this.end = end;
    	this.baseQuality = baseQuality;
    }
    
    public boolean equals( ReadFragmentByCigar fragment ) {
    	int result = 0;
    	if( readSeq.equals( fragment.getReadSeq() ) )	result++;
    	if( start == fragment.getStart() )				result++;
    	if( end == fragment.getEnd() )					result++;

    	return result==3?true:false;
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

	public byte[] getBaseQuality() {
		return baseQuality;
	}

	public void setBaseQuality(byte[] baseQuality) {
		this.baseQuality = baseQuality;
	}
}
