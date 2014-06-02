package kobic.msb.swing.thread.callable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.server.model.Model;
import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.server.obj.GenomeReferenceObject;
import kobic.msb.server.obj.GroupSamInfo;
import kobic.msb.server.obj.HairpinSequenceObject;
import kobic.msb.server.obj.SequenceObject;
import kobic.msb.swing.thread.caller.AbstractThreadCaller;
import kobic.msb.swing.thread.caller.JMsbNGSTestCaller;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class JMsbBrowserModelCallable implements Callable<Void>{
	private ProjectMapItem										projectMapItem;
	private Map<String, LinkedHashMap<String, GroupSamInfo>> 	seqMap;
	
//	2014 0502 modified by insu jang
//	private LinkedHashMap<String, String>						readMap;
	private List<HairpinVO>										prematureList;
	private AbstractThreadCaller								test;

//	public JMsbBrowserModelCallable(ProjectMapItem projectMapItem, Map<String, List<Object[]>>	seqMap, List<HairpinVO> prematureList) {
//	public JMsbBrowserModelCallable(ProjectMapItem projectMapItem, Map<String, LinkedHashMap<String, GroupSamInfo>>	seqMap, List<HairpinVO> prematureList) {	
//		this.projectMapItem	= projectMapItem;
//		this.seqMap			= seqMap;
//		this.prematureList	= prematureList;
//	}
	
	private JMsbBrowserModelCallable remote = JMsbBrowserModelCallable.this;
		
	public JMsbBrowserModelCallable(ProjectMapItem projectMapItem, Map<String, LinkedHashMap<String, GroupSamInfo>> seqMap, List<HairpinVO> prematureList) {
		this.projectMapItem	= projectMapItem;
		this.seqMap			= seqMap;
		this.prematureList	= prematureList;
	}
	
//	public JMsbBrowserModelCallable(ProjectMapItem projectMapItem, LinkedHashMap<String, String> readMap, List<HairpinVO> prematureList) {	
//		this.projectMapItem	= projectMapItem;
//		this.readMap		= readMap;
//		this.prematureList	= prematureList;
//	}
	
	public void setNGSTestCaller( AbstractThreadCaller caller ) {
		this.test = caller;
	}

	@Override
	public Void call() throws InterruptedException, Exception{
//		try {
			double incre = (double)100 / this.prematureList.size();
			double progressSum = 0;
	
			Iterator<HairpinVO> iter = this.prematureList.iterator();

			this.test.getOwnerDialog().setIndeterminate(false);

			this.test.getOwnerDialog().setProgressBarRange(0, 100);
			this.test.getOwnerDialog().setProgressToGetMiRnas(0);
	
			while( iter.hasNext() ) {
				HairpinVO vo = iter.next();
	
				if( Thread.currentThread().isInterrupted() )
					throw new InterruptedException("Interrupted modelling");

	//			HairpinVO vo = MsbEngine._db.getMicroRnaHairpinByMirid2(hairpin_id);
//				HairpinVO vo = MsbEngine.getInstance().getMiRBaseMap().get( this.projectMapItem.getMiRBAseVersion()).getMicroRnaHairpinByMirid2(hairpin_id);

				String hairpin_id = vo.getId();
				Msb.Project.MiRnaList.MiRna obj = new Msb.Project.MiRnaList.MiRna();
				obj.setAccession(	vo.getAccession() );
				obj.setChromosome(	vo.getChr() );
				obj.setMirid( hairpin_id );

//				String readFilename = this.readMap.get( hairpin_id );

				// item have to initialize when project re-create
				if( Model.isExistPremature( hairpin_id, this.projectMapItem.getMiRBAseVersion() ) || hairpin_id.startsWith( JMsbSysConst.NOVEL_MICRO_RNA ) ) {
					Model model = new Model( this.projectMapItem.getProjectInfo(), obj );

					model.getHeatMapObject().doSampleMap( new ArrayList<GroupSamInfo>(this.seqMap.get(hairpin_id).values()), vo.getStrand() );
//					LinkedHashMap<String, GroupSamInfo> lhm = BamReadObject.readBamReadObject( readFilename );
//					
//					model.getHeatMapObject().doSampleMap( new ArrayList<GroupSamInfo>(lhm.values()), vo.getStrand() );

					if( hairpin_id.startsWith( JMsbSysConst.NOVEL_MICRO_RNA ) ) {
						HairpinSequenceObject premature = new HairpinSequenceObject( vo );
						model.setPrematureSequenceObject( premature );
					}

					String path = model.writeModelToFile( this.projectMapItem.getProjectName() );

					if( new File(path).exists() ) {
						this.projectMapItem.addProjectAllModel( hairpin_id, path );
					}

	//				this.projectMapItem.addProjectAllModel( hairpin_id, model );
				}

				progressSum += incre;
	
				this.test.getOwnerDialog().setProgressToGetMiRnas( (int)progressSum );
	//			MsbEngine.logger.debug( this.test.getProgressBar().getValue() );
			}
			
			// Remove read files
			Thread delThread = new Thread( new Runnable(){
				public void run() {
					String root_dir = MsbEngine.getInstance().getSystemProperties().getProperty("msb.project.workspace") + File.separator + remote.projectMapItem.getProjectName();
					String tmp_dir = root_dir + File.separator + "readsMap";

					File dir = new File( tmp_dir );
					if( dir.exists() && dir.isDirectory() ) {
						try {
							FileUtils.deleteDirectory( dir );
						} catch (IOException e) {
							// TODO Auto-generated catch block
							MsbEngine.logger.error("Error", e);
						}
					}
				}
			});
			
			delThread.start();
//		}catch(Exception e) {
//			MsbEngine.logger.error("Error ", e);
//		}

		return null;
	}
}
