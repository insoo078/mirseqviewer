package kobic.msb.io.file.obj.bed;

import java.util.HashMap;
import java.util.Map;

public class BedTrack implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, String> attributes;

	public BedTrack() {
		this.attributes = new HashMap<String, String>();
	}
	
	public void addAttributes(String attribute, String value) {
		this.attributes.put(attribute, value);
	}
}
