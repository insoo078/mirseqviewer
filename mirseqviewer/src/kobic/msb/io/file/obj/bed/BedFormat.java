package kobic.msb.io.file.obj.bed;

import java.util.ArrayList;
import java.util.List;

public class BedFormat implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BedBrowser browser;
	private BedTrack track;
	
	private List<BedFragment> list;
	
	public BedFormat() {
		this( null,  null );
	}

	public BedFormat( BedBrowser browser, BedTrack track ) {
		this.browser = browser;
		this.track = track;
		
		this.list = new ArrayList<BedFragment>();
	}
	
	public void addBedFragment( BedFragment bed ) {
		this.list.add( bed );
	}

	public BedBrowser getBrowser() {
		return browser;
	}

	public void setBrowser(BedBrowser browser) {
		this.browser = browser;
	}

	public BedTrack getTrack() {
		return track;
	}

	public void setTrack(BedTrack track) {
		this.track = track;
	}
	
	public List<BedFragment> getFragmentList() {
		return this.list;
	}
}