package kobic.msb.io.file.obj.mirbase;

import java.io.Serializable;

public class ChromosomalCoordinate implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//	chr1	.	miRNA	30366	30503	.	+	.	ACC="MI0006363"; ID="hsa-mir-1302-2";
	private String chromosome;
	private String start;
	private String end;
	private String strand;
	private String accession;
	private String name;

	public String getChromosome() {
		return chromosome;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
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
	public String getStrand() {
		return strand;
	}
	public void setStrand(String strand) {
		this.strand = strand;
	}
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
