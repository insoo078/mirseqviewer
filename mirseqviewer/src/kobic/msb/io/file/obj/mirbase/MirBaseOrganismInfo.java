package kobic.msb.io.file.obj.mirbase;

public class MirBaseOrganismInfo {
	private String organism;
	private String division;
	private String name;
	private String tree;
	private String ncbi_taxid;

	public String getOrganism() {
		return organism;
	}
	public void setOrganism(String organism) {
		this.organism = organism;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTree() {
		return tree;
	}
	public void setTree(String tree) {
		this.tree = tree;
	}
	public String getNcbi_taxid() {
		return ncbi_taxid;
	}
	public void setNcbi_taxid(String ncbi_taxid) {
		this.ncbi_taxid = ncbi_taxid;
	}
}
