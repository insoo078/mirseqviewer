package kobic.msb.swing.thread.callable.obj;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.server.obj.GroupSamInfo;

public class NGSFileReadResultObj {
	String group;
	String sample;
	Map<String, LinkedHashMap<String, GroupSamInfo>> sequenceMap;
//	LinkedHashMap<String, String> readMap;
	Map<String, Integer> matureProfileMap;
	List<HairpinVO> prematureList;
	
	public NGSFileReadResultObj(String group, String sample, Map<String, LinkedHashMap<String, GroupSamInfo>> sequenceMap, Map<String, Integer> matureProfileMap, List<HairpinVO> prematureList) {
//	public NGSFileReadResultObj(String group, String sample, LinkedHashMap<String, String> readMap, Map<String, Integer> matureProfileMap, List<HairpinVO> prematureList) {
		this.group = group;
		this.sample = sample;
//		this.readMap = readMap;
		this.sequenceMap = sequenceMap;
		this.matureProfileMap = matureProfileMap;
		this.prematureList = prematureList;
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

	public Map<String, LinkedHashMap<String, GroupSamInfo>> getSequenceMap() {
		return sequenceMap;
	}

	public void setSequenceMap(Map<String, LinkedHashMap<String, GroupSamInfo>> sequenceMap) {
		this.sequenceMap = sequenceMap;
	}
	
//	public LinkedHashMap<String, String> getReadMap() {
//		return this.readMap;
//	}
//
//	public void setReadMap(LinkedHashMap<String, String> readMap) {
//		this.readMap = readMap;
//	}

	public Map<String, Integer> getMatureProfileMap() {
		return matureProfileMap;
	}

	public void setMatureProfileMap(Map<String, Integer> profileMap) {
		this.matureProfileMap = profileMap;
	}

	public List<HairpinVO> getPrematureList() {
		return prematureList;
	}

	public void setPrematureList(List<HairpinVO> prematureList) {
		this.prematureList = prematureList;
	}
}
