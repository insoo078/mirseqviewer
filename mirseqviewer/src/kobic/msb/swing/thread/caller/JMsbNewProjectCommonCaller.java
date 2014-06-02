package kobic.msb.swing.thread.caller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.server.obj.GroupSamInfo;
import kobic.msb.swing.frame.dialog.JCommonNewProjectDialog;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public abstract class JMsbNewProjectCommonCaller<T> extends AbstractThreadCaller<T>{
	private Map<String, LinkedHashMap<String, GroupSamInfo>>			seqMap;
//	private LinkedHashMap<String, String>			readMap;
	private Map<String, Map<String, Integer>>		readProfileMap;
	private List<HairpinVO>							prematureList;
	
	public JMsbNewProjectCommonCaller( JCommonNewProjectDialog dialog, ProjectMapItem projectMapItem ) {
		super( dialog, projectMapItem );
		
		this.prematureList	= new ArrayList<HairpinVO>();
		this.readProfileMap = new LinkedHashMap<String, Map<String, Integer>>();
	}

	public synchronized void updatePrematureList(List<HairpinVO> paramPrematureList) throws Exception{
		for(int i=0; i<paramPrematureList.size(); i++) {
			if( !this.prematureList.contains( paramPrematureList.get(i) ) )	
				this.prematureList.add( paramPrematureList.get(i) );
		}
	}

	public synchronized void updateSequenceMap( Map<String, LinkedHashMap<String, GroupSamInfo>> sequenceMap ) throws Exception{
		if( this.seqMap == null )	this.seqMap = sequenceMap;
		else {
			Iterator<String> keyIter = sequenceMap.keySet().iterator();
			while( keyIter.hasNext() ) {
				String hairpin_id = keyIter.next();

				if( this.seqMap.containsKey(hairpin_id) )	this.seqMap.get(hairpin_id).putAll( sequenceMap.get(hairpin_id) );
				else										this.seqMap.put(hairpin_id, sequenceMap.get(hairpin_id) );
			}
		}
	}
	
//	public synchronized void updateReadMap( LinkedHashMap<String, String> readMap ) throws Exception{
//		this.readMap = readMap;
//	}

	public synchronized void updateProfileMap( Map<String, Integer> localMatureProfileMap, String sample ) throws Exception{
		Set<String> keySet = localMatureProfileMap.keySet();
		Iterator<String> iter = keySet.iterator();
		while( iter.hasNext() ) {
			String hairpin_id = iter.next();
			if( this.readProfileMap.containsKey( hairpin_id) ) {
				Map<String, Integer> countMap = this.readProfileMap.get( hairpin_id );
				countMap.put(sample, localMatureProfileMap.get(hairpin_id) );
			}else {
				Map<String, Integer> countMap = new HashMap<String, Integer>();
				countMap.put(sample, localMatureProfileMap.get(hairpin_id) );
				this.readProfileMap.put(hairpin_id, countMap);
			}
		}
	}

	public Object[][] buildProfile() throws Exception {
		List<Object[]> fileList		= this.getProjectMapItem().getSampleFileList();
	
		Object[][] profile = new Object[ this.readProfileMap.keySet().size() + 1 ][ fileList.size() + 1 ];
		
		int index = 1;
		profile[0][0] = "ID";
		for(Object[] sampleObj : fileList) {
			profile[0][index++] = sampleObj[1].toString();
		}

		double incre = (double)25 / this.readProfileMap.keySet().size();
		double progressSum = 0;
		int i = 1;
	    Iterator<String> miridKeyIter = this.readProfileMap.keySet().iterator();
	    while( miridKeyIter.hasNext() ) {
	    	String mirid = miridKeyIter.next();

	    	profile[i][0] = mirid;
	    	int j = 1;
	    	for(Object[] sampleObj : fileList) {
				String sampleId = sampleObj[1].toString();
				int cnt = 0;
				if( this.readProfileMap.get(mirid).containsKey( sampleId ) )
					cnt = this.readProfileMap.get( mirid ).get( sampleId );
	
		    	profile[i][j] = cnt;
				j++;
	    	}
	    	i++;
	    	
	    	progressSum += incre;
	    	this.setProcessingProgress( this.getProcessingProgress() + progressSum );
	    }

	    this.getProjectMapItem().setExpressionProfile(null, profile);
	    
	    return profile;
	}
	
	public List<HairpinVO> getPrematureList() {
		return this.prematureList;
	}
	
	public Map<String, LinkedHashMap<String, GroupSamInfo>> getSequenceMap() {
		return this.seqMap;
	}
	
//	public LinkedHashMap<String, String> getReadMap() {
//		return this.readMap;
//	}

	@Override
	public abstract T run();

	@Override
	public abstract void patch(JPanel japnel, ProjectMapItem projectMapItem);

//	@Override
//	public abstract void callback(Object[] header, List<Object[]> readedAllObjList, List<Object[]> choosedRnaObjList);
//
//	@Override
//	public abstract void callback(String group, String sample, Map<String, List<GroupSamInfo>> sequenceMap, Map<String, Integer> profileMap, List<HairpinVO> prematureList);
}
