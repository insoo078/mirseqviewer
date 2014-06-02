package kobic.msb.db.sqlite.vo;

public class MatureVO {
	private String id;
	private String accession;
	private String mirid;
	private String evidence;
	private String experiment;
	private int	start;
	private int end;
	private String sequence;

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
	public String getMirid() {
		return mirid;
	}
	public void setMirid(String mirid) {
		this.mirid = mirid;
	}
	public String getEvidence() {
		return evidence;
	}
	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}
	public String getExperiment() {
		return experiment;
	}
	public void setExperiment(String experiment) {
		this.experiment = experiment;
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
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public boolean isOverlaped(MatureVO tar) {
		int src_start = this.getStart();
		int src_end = this.getEnd();

		int tar_start = tar.getStart();
		int tar_end = tar.getEnd();
		
		if( src_end >= tar_start && src_start <= tar_start)	return true;
		if( src_start >= tar_start && src_end <= tar_end)	return true;
		if( src_start <= tar_start && src_end >= tar_start)	return true;
		if( src_start <= tar_end && src_end >= tar_end)		return true;

		return false;
	}
}
