package kobic.msb.io.file.obj.mirbase;

import java.io.Serializable;

public class PubmedInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pubmed;
	private int order;
	private String authors;
	private String title;
	private String journal;
	
	public PubmedInfo() {
		this.pubmed = "";
		this.order = -1;
		this.authors = "";
		this.title = "";
		this.journal = "";
	}

	public String getPubmed() {
		return pubmed;
	}
	public void setPubmed(String pubmed) {
		this.pubmed = pubmed;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getAuthors() {
		return authors;
	}
	public void setAuthors(String authors) {
		this.authors = authors;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getJournal() {
		return journal;
	}
	public void setJournal(String journal) {
		this.journal = journal;
	}
}
