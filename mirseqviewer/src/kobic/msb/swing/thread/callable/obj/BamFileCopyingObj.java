package kobic.msb.swing.thread.callable.obj;

import java.util.List;
import java.util.Map;

import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.db.sqlite.vo.MatureVO;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;

public class BamFileCopyingObj {
	Sample sample;
	String groupId;
	String sampleId;
	Map<String, List<MatureVO>> matureMap;
	List<HairpinVO> lst;
	
	public BamFileCopyingObj(Sample sample, String groupId, String sampleId, Map<String, List<MatureVO>> matureMap, List<HairpinVO> lst){
		this.sample = sample;
		this.groupId = groupId;
		this.sampleId = sampleId;
		this.matureMap = matureMap;
		this.lst = lst;
	}

	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public Map<String, List<MatureVO>> getMatureMap() {
		return matureMap;
	}

	public void setMatureMap(Map<String, List<MatureVO>> matureMap) {
		this.matureMap = matureMap;
	}

	public List<HairpinVO> getLst() {
		return lst;
	}

	public void setLst(List<HairpinVO> lst) {
		this.lst = lst;
	}

	
}
