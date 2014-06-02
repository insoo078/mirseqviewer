//package kobic.msb.swing.thread.temp;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import kobic.msb.db.sqlite.vo.HairpinVO;
//import kobic.msb.server.model.Model;
//import kobic.msb.server.model.jaxb.Msb;
//import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
//import kobic.msb.swing.frame.dialog.JProjectDialog;
//import kobic.msb.swing.thread.NGSFileReadCallable;
//import kobic.msb.swing.thread.NGSRnaListCallable;
//import kobic.msb.system.catalog.ProjectMapItem;
//import kobic.msb.system.engine.MsbEngine;
//
//public class NGSTestCaller {
//	private int _THREAD_NUM_;
//	private int _COMPLETED_THREAD_CNT_;
//
//	private Map<String, List<Object[]>>				seqMap;
//	private Map<String, Map<String, Integer>>		profileMap;
//	private List<String>							prematureList;
//	private ProjectMapItem							projectMapItem;
//	
//	private JProjectDialog							dialog;
//	
//	private int										progressStatusValue;
//	private double									progressIncrement;
//
//	public NGSTestCaller( JProjectDialog dialog, ProjectMapItem projectMapItem ) throws Exception {
//		this._THREAD_NUM_			= 0;
//		this._COMPLETED_THREAD_CNT_	= 0;
//		this.progressStatusValue	= 0;
//
//		this.dialog			= dialog;
//		this.projectMapItem	= projectMapItem;
//		this.prematureList	= new ArrayList<String>();
//		this.profileMap 	= new LinkedHashMap<String, Map<String, Integer>>();
//	}
//
//	public void run() {
//		try {
//			/*********************************************************************************************************
//			 *  If there are not any model int the model object
//			 */
//			
//			if( this.projectMapItem.getTotalModelMap() == null || this.projectMapItem.getTotalModelMap().size() == 0 ) {
//				kobic.msb.com.util.SwingUtilities.setWaitCursorFor( this.dialog );
//				MsbEngine.logger.debug("START Reading Program");
//		
//				List<Object[]> fileList = this.projectMapItem.getSampleFileList();
//				this._THREAD_NUM_ = fileList.size();
//		
//				this.progressIncrement =	(double)25/this._THREAD_NUM_;
//		
//				this.dialog.setProgressToGetMiRnas( this.progressStatusValue );
//				for(int i=0; i<fileList.size(); i++) {
//					Object[] divs = fileList.get(i);
//					String groupId = divs[0].toString();
//					String sampleId = divs[1].toString();
//					Sample sample = (Sample)divs[2];
//		
//					NGSFileReadCallable work = new NGSFileReadCallable( groupId, sampleId, sample.getSortedPath(), sample.getIndexPath());
//					work.setNGSTestCaller( this );
//					MsbEngine.es.submit( work );
//				}
//		
//				MsbEngine.logger.debug("END PROGRAM");
//			}else {
//				// If there are model with profile in the model object
//				MsbEngine.logger.debug("Loading miRNA list");
//				
//				NGSRnaListCallable callable = new NGSRnaListCallable( this.dialog, this.projectMapItem, this.progressStatusValue );
//				callable.setNGSTestCaller( this );
//				MsbEngine.es.submit( callable );
//				MsbEngine.logger.debug("END PROGRAM");
//			}
//		}catch(Exception e) {
////			e.printStackTrace();
//			MsbEngine.logger.error("error : ", e);
//		}
//	}
//	
//	public void callback(String[] header, List<Object[]> readedAllObjList, List<Object[]> choosedRnaObjList) {
//		try {
//			this.dialog.getMirnaChoosePanel().setTableHeader( header );
//			this.dialog.getMirnaChoosePanel().setTableModel( readedAllObjList );
//			this.dialog.getMirnaChoosePanel().setListModel( choosedRnaObjList );
//			
//			this.storeProjectMapItem();
//			
//			kobic.msb.com.util.SwingUtilities.setDefaultCursorFor( this.dialog );
//		}catch(Exception e) {
//			MsbEngine.logger.error( "error : ", e );
//		}
//	}
//
//	public void callback( String group, String sample, Map<String, List<Object[]>> sequenceMap, Map<String, Integer> profileMap, List<String> prematureList ) {
//		this.updatePrematureList( prematureList );
//		this.updateSequenceMap( sequenceMap );
//		this.updateProfileMap( profileMap, sample );
//		
//		++this._COMPLETED_THREAD_CNT_;
//		this.progressStatusValue = (int)(this._COMPLETED_THREAD_CNT_ * this.progressIncrement);
//
//		this.dialog.setProgressToGetMiRnas( this.progressStatusValue );
//
//		try {
//			if(_COMPLETED_THREAD_CNT_ == _THREAD_NUM_) {
//				System.out.println( "NUM : " + _THREAD_NUM_ + "   current cnt : " + _COMPLETED_THREAD_CNT_ );
//				this.buildModel();
//				MsbEngine.logger.debug("Complete Build Model");
//				this.buildProfile();
//				MsbEngine.logger.debug("Complete Build Profile");
//				
//				NGSRnaListCallable callable = new NGSRnaListCallable( this.dialog, this.projectMapItem, this.progressStatusValue );
//				callable.setNGSTestCaller( this );
//				MsbEngine.es.submit( callable );
//				
//				kobic.msb.com.util.SwingUtilities.setDefaultCursorFor( this.dialog );
//			}
//		}catch(Exception e) {
////			e.printStackTrace();
//			MsbEngine.logger.error( "Error : ", e );
//		}
//	}
//
//	private void storeProjectMapItem() throws Exception{
//		String projectName = this.projectMapItem.getProjectName();
//		Msb msb = new Msb();
//		msb.setProject( this.projectMapItem.getProjectInfo() );
////		Utilities.writeXmlFile( msb, this.projectMapItem.getProjectLoadFilePath() );
//		MsbEngine.engine.getProjectManager().writeXmlToProject( msb );
//
//		MsbEngine engine = MsbEngine.getInstance();
//		engine.getProjectManager().getProjectMap().putProject( projectName, this.projectMapItem );
//		engine.getProjectManager().getProjectMap().writeToFile( engine.getProjectManager().getSystemFileToGetProjectList() );
//	}
//
//	private synchronized void updatePrematureList(List<String> paramPrematureList) {
//		for(int i=0; i<paramPrematureList.size(); i++) {
//			if( !this.prematureList.contains( paramPrematureList.get(i) ) )	
//				this.prematureList.add( paramPrematureList.get(i) );
//		}
//	}
//
//	private synchronized void updateSequenceMap( Map<String, List<Object[]>> sequenceMap ) {
//		if( this.seqMap == null )	this.seqMap = sequenceMap;
//		else {
//			Iterator<String> keyIter = sequenceMap.keySet().iterator();
//			while( keyIter.hasNext() ) {
//				String hairpin_id = keyIter.next();
//				if( this.seqMap.containsKey(hairpin_id) )	this.seqMap.get(hairpin_id).addAll( sequenceMap.get(hairpin_id) );
//				else										this.seqMap.put(hairpin_id, sequenceMap.get(hairpin_id) );
//			}
//		}
//	}
//
//	private synchronized void updateProfileMap( Map<String, Integer> localProfileMap, String sample ) {
//		Set<String> keySet = localProfileMap.keySet();
//		Iterator<String> iter = keySet.iterator();
//		while( iter.hasNext() ) {
//			String hairpin_id = iter.next();
//			if( this.profileMap.containsKey( hairpin_id) ) {
//				Map<String, Integer> countMap = this.profileMap.get( hairpin_id );
//				countMap.put(sample, localProfileMap.get(hairpin_id) );
//			}else {
//				Map<String, Integer> countMap = new HashMap<String, Integer>();
//				countMap.put(sample, localProfileMap.get(hairpin_id) );
//				this.profileMap.put(hairpin_id, countMap);
//			}
//		}
//	}
//
//	private void buildModel() throws Exception {
//		Iterator<String> iter = this.prematureList.iterator();
//		double incre = (double)25 / this.prematureList.size();
//		double progressSum = 0;
//		while( iter.hasNext() ) {
//			String hairpin_id = iter.next();
//
//			HairpinVO vo = MsbEngine._db.getMicroRnaHairpinByMirid2(hairpin_id);
//
//			Msb.Project.MiRnaList.MiRna obj = new Msb.Project.MiRnaList.MiRna();
//			obj.setAccession(	vo.getAccession() );
//			obj.setChromosome(	vo.getChr() );
//			obj.setMirid( hairpin_id );
//
//			// item have to initialize when project re-create
//			if( Model.isExistPremature( hairpin_id ) ) {
//				Model model = new Model( this.projectMapItem.getProjectInfo(), obj );
//				model.getHeatMapObject().doSampleMap( this.seqMap.get(hairpin_id) );
//				this.projectMapItem.addProjectAllModel( hairpin_id, model );
//				
//				progressSum += incre;
//				this.dialog.setProgressToGetMiRnas( (int)(this.progressStatusValue + progressSum) );
//			}
//		}
//		this.progressStatusValue += progressSum;
//	}
//
//	private Object[][] buildProfile() throws Exception {
//		List<Object[]> fileList		= this.projectMapItem.getSampleFileList();
//	
//		Object[][] profile = new Object[ this.profileMap.keySet().size() + 1 ][ fileList.size() + 1 ];
//		
//		int index = 1;
//		profile[0][0] = "ID";
//		for(Object[] sampleObj : fileList) {
//			profile[0][index++] = sampleObj[1].toString();
//		}
//
//		double incre = (double)25 / this.profileMap.keySet().size();
//		double progressSum = 0;
//		int i = 1;
//	    Iterator<String> miridKeyIter = this.profileMap.keySet().iterator();
//	    while( miridKeyIter.hasNext() ) {
//	    	String mirid = miridKeyIter.next();
//
//	    	profile[i][0] = mirid;
//	    	int j = 1;
//	    	for(Object[] sampleObj : fileList) {
//				String sampleId = sampleObj[1].toString();
//				int cnt = 0;
//				if( this.profileMap.get(mirid).containsKey( sampleId ) )
//					cnt = this.profileMap.get( mirid ).get( sampleId );
//	
//		    	profile[i][j] = cnt;
//				j++;
//	    	}
//	    	i++;
//	    	
//	    	progressSum += incre;
//			this.dialog.setProgressToGetMiRnas( (int)(this.progressStatusValue + progressSum) );
//	    }
//	    this.progressStatusValue += progressSum;
//
//	    try {
//	    	this.projectMapItem.setExpressionProfile(null, profile);
//	    }catch(Exception e) {
//	    	e.printStackTrace();
//	    	MsbEngine.logger.error( "error : ", e );
//	    }
//	    
//	    return profile;
//	}
//}
