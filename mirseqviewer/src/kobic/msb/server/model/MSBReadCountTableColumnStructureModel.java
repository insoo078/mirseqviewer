package kobic.msb.server.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.server.model.jaxb.Msb.Project;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.system.engine.MsbEngine;

public class MSBReadCountTableColumnStructureModel extends Observable implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String>							heatMapHeader;
	private Map<String, MsbRCTColumnModel>			originalColumnStructureMap;

	private Map<String, MsbRCTColumnModel>			currentRCTColumnStructure;
	private Map<String, MsbRCTColumnModel>			temporaryUnusedGroupMap;
	
	public MSBReadCountTableColumnStructureModel() {
		this.heatMapHeader = null;
		this.currentRCTColumnStructure = null;
		this.temporaryUnusedGroupMap = null;
		this.originalColumnStructureMap = null;
	}

	public MSBReadCountTableColumnStructureModel( Project projectInfo ) {
		this.heatMapHeader				= new ArrayList<String>();
		this.currentRCTColumnStructure	= new LinkedHashMap<String, MsbRCTColumnModel>();
		this.temporaryUnusedGroupMap	= new LinkedHashMap<String, MsbRCTColumnModel>();

		try {
			this.initializeDefaultColumnStructure( projectInfo );
	
			this.setCurrentHeatMapColumnStructure( this.currentRCTColumnStructure );
			this.originalColumnStructureMap	= new LinkedHashMap<String, MsbRCTColumnModel>( this.currentRCTColumnStructure );
		}catch(Exception e) {
			MsbEngine.logger.error( "Error", e );
		}
	}

	private void initializeDefaultColumnStructure( Project projectInfo ) throws Exception{
		List<Group> tmpArray = new ArrayList<Group>( Arrays.asList(new Group[ projectInfo.getSamples().getGroup().size()]) );
		Collections.copy( tmpArray, projectInfo.getSamples().getGroup() );

		MsbRCTColumnModel totalSumGrpColumn = new MsbRCTColumnModel( JMsbSysConst.TOTAL_SUM_HEADER_PREFIX, JMsbSysConst.TOTAL_SUM_HEADER, null );

		/*****************************************************************************************
		 * Default column structure
		 * 1. Total sum
		 * 2. Each group sum
		 * 3. Each group(inc. samples)
		 */
		this.currentRCTColumnStructure.put( JMsbSysConst.TOTAL_SUM_HEADER, totalSumGrpColumn );

		for( Group grp:tmpArray ) {
			if( grp.getSample() != null ) {
				MsbRCTColumnModel groupSumColumn = new MsbRCTColumnModel( JMsbSysConst.GROUP_SUM_HEADER_PREFIX, grp.getGroupId() + JMsbSysConst.SUM_SUFFIX, null );
				groupSumColumn.setGroup( grp.getGroupId() );
				
				this.currentRCTColumnStructure.put( grp.getGroupId() + JMsbSysConst.SUM_SUFFIX, groupSumColumn );
			}
		}

		for( Group grp:tmpArray ) {
			MsbRCTColumnModel groupColumn = new MsbRCTColumnModel( JMsbSysConst.GROUP_HEADER_PREFIX, grp );
			this.currentRCTColumnStructure.put( grp.getGroupId(), groupColumn );
		}
	}

	public void setCurrentHeatMapColumnStructure( Map<String, MsbRCTColumnModel> currentChoosedColumnGroup ) {
		try {
			this.currentRCTColumnStructure	= this.getCurrentHeatMapColumnStructure( currentChoosedColumnGroup );
		}catch(Exception e) {
			MsbEngine.logger.error( "error : ", e );
		}
		
		this.setChanged();
		this.notifyObservers( this );
	}

	private Map<String, MsbRCTColumnModel> getCurrentHeatMapColumnStructure( Map<String, MsbRCTColumnModel> currentChoosedColumnGroup ) throws Exception {
		this.heatMapHeader.removeAll( this.heatMapHeader );		// Column header initializing (remove all items)

		for( Iterator<String> groupIterator = currentChoosedColumnGroup.keySet().iterator(); groupIterator.hasNext(); ) {
			String key	= groupIterator.next();
			MsbRCTColumnModel group	= currentChoosedColumnGroup.get( key );

			if( group.getColumnType().equals( JMsbSysConst.TOTAL_SUM_HEADER_PREFIX ) ) {
				this.heatMapHeader.add( JMsbSysConst.TOTAL_SUM_HEADER );
			}else if( group.getColumnType().equals( JMsbSysConst.GROUP_SUM_HEADER_PREFIX ) ) {
				this.heatMapHeader.add( group.getColumnId() );
			}else {
				if( group.isGroup() ) {
					List<MsbRCTColumnModel> list = group.getChildColumnList();
					for( Iterator<MsbRCTColumnModel> iter = list.iterator(); iter.hasNext(); ) {
						MsbRCTColumnModel column = iter.next();
						this.heatMapHeader.add( column.getColumnId() );
					}
				}
			}
		}

		return currentChoosedColumnGroup;
	}
	
	public Map<String, MsbRCTColumnModel> getChoosedGroupMap() {
		return this.currentRCTColumnStructure;
	}

	public Map<String, MsbRCTColumnModel> getUnusedGroupMap() {
		return this.temporaryUnusedGroupMap;
	}
	
	public void setChoosedGroupMap( Map<String, MsbRCTColumnModel> group ) {
		this.currentRCTColumnStructure.clear();
		this.currentRCTColumnStructure = group;
	}
	
	public void setUnusedGroupMap( Map<String, MsbRCTColumnModel> group ) {
		this.temporaryUnusedGroupMap.clear();
		this.temporaryUnusedGroupMap = group;
	}
	
	public List<String> getHeatMapHeader() {
		return this.heatMapHeader;
	}
	
	public Map<String, MsbRCTColumnModel> getHeatMapColumnStructure() {
		return this.currentRCTColumnStructure;
	}

	public int getSampleSize() {
		int sum = 0;
		try {
			Collection<MsbRCTColumnModel> collection = this.currentRCTColumnStructure.values();
			Iterator<MsbRCTColumnModel> iter = collection.iterator();
			while( iter.hasNext() ) {
				MsbRCTColumnModel model = iter.next();
				if( model.getColumnType().equals( JMsbSysConst.GROUP_HEADER_PREFIX ) ) {
					Iterator<MsbRCTColumnModel> sampleIter = model.getChildColumnList().iterator();
					if( sampleIter != null ) {
						while( sampleIter.hasNext() ) {
							MsbRCTColumnModel sampleColumn = sampleIter.next();
							if( sampleColumn.getColumnType().equals(JMsbSysConst.SAMPLE_HEADER_PREFIX ) )	sum++;
						}
					}
				}
			}
		}catch(Exception e) {
			MsbEngine.logger.error( "error : ", e );
		}
		return sum;
	}

	public static int getHeaderColumnSize( Map<String, MsbRCTColumnModel> map ) {
		int size = 0;
		try {
			for(Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
				List<String> sampleList = map.get(iter.next()).getChildColumnIdList();
				if( sampleList != null ) {
					for(int i=0; i<sampleList.size(); i++) {
						size++;
					}
				}else {
					size++;
				}
			}
		}catch(Exception e) {
			MsbEngine.logger.error( "error : ", e );
		}
		
		return size;
	}
	
	public Map<String, MsbRCTColumnModel> getOriginalColumnStructureMap() {
		return this.originalColumnStructureMap;
	}
	
	
	public MsbRCTColumnModel getColumnModel( String id ) {
		try {
			return MSBReadCountTableColumnStructureModel.getColumnModel( this.currentRCTColumnStructure, id );	
		}catch(Exception e) {
			MsbEngine.logger.error( "error : ", e );
		}
		return null;
	}
	
	public static MsbRCTColumnModel getColumnModel(Map<String, MsbRCTColumnModel> map, String id ) throws Exception {
		Collection<MsbRCTColumnModel> collection = map.values();
		Iterator<MsbRCTColumnModel> iter = collection.iterator();
		while( iter.hasNext() ) {
			MsbRCTColumnModel model = iter.next();
			if( model.getColumnId().equals( id ) ) {
				return model;
			}else {
				if( model.getChildColumnList() != null ) {
					Iterator<MsbRCTColumnModel> sampleIter = model.getChildColumnList().iterator();
					while( sampleIter.hasNext() ) {
						MsbRCTColumnModel sampleColumn = sampleIter.next();
						
						if( sampleColumn.getColumnId().equals( id ) )	return sampleColumn;
					}
				}
			}
		}
		return null;
	}

	public MsbRCTColumnModel getOriginalColumnModel( String id ) {
		try {
			return MSBReadCountTableColumnStructureModel.getColumnModel( this.originalColumnStructureMap, id);
		}catch(Exception e) {
			MsbEngine.logger.error( "error : ", e );
		}
		return null;
	}
}
