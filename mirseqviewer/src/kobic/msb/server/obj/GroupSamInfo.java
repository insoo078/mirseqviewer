package kobic.msb.server.obj;

public class GroupSamInfo implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String	key;
	private int hashCode;
	private String	group;
	private String	sample;
//	private SAMInfo samInfo;
	private MsvSamRecord samInfo;
	
//	public GroupSamInfo(String key, String group, String sample, SAMInfo samInfo) {
//		this.key		= key;
//		this.group		= group;
//		this.sample		= sample;
//		this.samInfo	= samInfo;
//	}
	
	public GroupSamInfo() {
		this.key = null;
		this.group = null;
		this.samInfo = null;
		this.sample = null;
		this.hashCode = -1;
	}
	public GroupSamInfo(String key, int hashCode, String group, String sample, MsvSamRecord samInfo) {
		this.key		= key;
		this.group		= group;
		this.sample		= sample;
		this.samInfo	= samInfo;
		this.hashCode	= hashCode;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getSample() {
		return sample;
	}

	public void setSample(String sample) {
		this.sample = sample;
	}

//	public SAMInfo getSamInfo() {
//		return samInfo;
//	}
//
//	public void setSamInfo(SAMInfo samInfo) {
//		this.samInfo = samInfo;
//	}
	
	public MsvSamRecord getSamInfo() {
		return samInfo;
	}

	public void setSamInfo(MsvSamRecord samInfo) {
		this.samInfo = samInfo;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
}
