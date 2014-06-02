package kobic.msb.io.file.obj.mirbase;

import java.io.Serializable;

public class MirBaseRnaMatureInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String accession;
	private String mirid;
	private String evidence;
	private String experiment;
	private String start;
	private String end;

	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
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

	
	/*
FT                   /accession="MIMAT0000001"
FT                   /product="cel-let-7-5p"
FT                   /evidence=experimental
FT                   /experiment="cloned [1-3,5], Northern [1], PCR [4], Solexa
	 */
}
