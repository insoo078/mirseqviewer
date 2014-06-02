package kobic.msb.io.file;

public class SAMObj {
	String premature_id;
    String readName;
    String readSeq;
    char strand;
    int start;
    int end;
    long count;
    
    public SAMObj(String premature_id, String readName, String readSeq, char strand, int start, int end) {
    	this.premature_id	= premature_id;
    	this.readName		= readName;
    	this.readSeq		= readSeq;
    	this.strand			= strand;
    	this.start			= start;
    	this.end			= end;
    	
    	this.count			= 1;
    }
    
    public void increase() {
    	this.count++;
    }

	public String getPremature_id() {
		return premature_id;
	}

	public String getReadName() {
		return readName;
	}

	public String getReadSeq() {
		return readSeq;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public long getCount() {
		return count;
	}
	
	public char getStrand() {
		return this.strand;
	}
}
