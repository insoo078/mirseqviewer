package kobic.msb.io.file.obj.bed;

public class BedFragment implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String chrom;
	private int chromStart;
	private int chromEnd;
	
	private String name;
	private int score;
	private String strand;
	private String thickStart;
	private String thickEnd;
	private String itemRgb;

	public String getChrom() {
		return chrom;
	}
	public void setChrom(String chrom) {
		this.chrom = chrom;
	}
	public int getChromStart() {
		return chromStart;
	}
	public void setChromStart(int chromStart) {
		this.chromStart = chromStart;
	}
	public int getChromEnd() {
		return chromEnd;
	}
	public void setChromEnd(int chromEnd) {
		this.chromEnd = chromEnd;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getStrand() {
		return strand;
	}
	public void setStrand(String strand) {
		this.strand = strand;
	}
	public String getThickStart() {
		return thickStart;
	}
	public void setThickStart(String thickStart) {
		this.thickStart = thickStart;
	}
	public String getThickEnd() {
		return thickEnd;
	}
	public void setThickEnd(String thickEnd) {
		this.thickEnd = thickEnd;
	}
	public String getItemRgb() {
		return itemRgb;
	}
	public void setItemRgb(String itemRgb) {
		this.itemRgb = itemRgb;
	}
}
