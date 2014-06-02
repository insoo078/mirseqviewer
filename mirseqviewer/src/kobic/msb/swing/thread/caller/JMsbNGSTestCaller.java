package kobic.msb.swing.thread.caller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.db.sqlite.MicroRnaDB;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.db.sqlite.vo.MatureVO;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.server.obj.GroupSamInfo;
import kobic.msb.swing.frame.dialog.JCommonNewProjectDialog;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.swing.panel.newproject.JMsbMatureChoosePanel;
import kobic.msb.swing.thread.callable.JMsbBrowserModelCallable;
import kobic.msb.swing.thread.callable.NGSFileReadCallable;
import kobic.msb.swing.thread.callable.NGSRnaListCallable;
import kobic.msb.swing.thread.callable.obj.NGSFileReadResultObj;
import kobic.msb.swing.thread.callable.obj.NGSRnaListObj;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import kobic.msb.system.project.ProjectManager;

public class JMsbNGSTestCaller<T> extends JMsbNewProjectCommonCaller<T>{	
	private JMsbMatureChoosePanel					choosePanel;
	private double									progressIncrement;

//	private	JProgressBar							progressBar;
	private CountDownLatch							latch;
	
	private JMsbNGSTestCaller<T> remote = JMsbNGSTestCaller.this;
	
	public JMsbNGSTestCaller( JCommonNewProjectDialog dialog ){
		this( dialog, null );
	}

	public JMsbNGSTestCaller( JCommonNewProjectDialog dialog, ProjectMapItem projectMapItem ) {
		super( dialog, projectMapItem );

		this.choosePanel	= dialog.getMirnaChoosePanel();
		
//		this.progressBar = this.getProgressBar();
	}

	@Override
	public T run() {
		kobic.msb.common.util.SwingUtilities.setWaitCursorFor( this.getOwnerDialog() );
/****
//			String mirbase = this.getProjectMapItem().getMiRBAseVersion();
//////			this.matureMap		= MsbEngine.getInstance().getMiRBaseMap().get( this.projectMapItem.getMiRBAseVersion() ).getAllMicroRnaMaturesByMirid();
//			LinkedHashMap<String, MicroRnaDB> hash = MsbEngine.getInstance().getMiRBaseMap();
////			Map<String, List<MatureVO>>	matureMap = hash.get( mirbase ).getAllMicroRnaMaturesByMirid();
////
////			String org_code		= MsbEngine.getInstance().getOrganismMap().get( this.getProjectMapItem().getOrganism() );
//////			this.hairpinMap		= MsbEngine.getInstance().getMiRBaseMap().get( this.projectMapItem.getMiRBAseVersion() ).getAllMicroRnaMaturesByChromosome( org_code );
////			
////			List<HairpinVO> lst = MsbEngine.getInstance().getMiRBaseMap().get( this.getProjectMapItem().getMiRBAseVersion() ).getMicroRnaPrematureList( org_code );

****/

		Map<String, List<MatureVO>> matureMap = null;
		List<HairpinVO> lst = null;
		String org_code		= MsbEngine.getInstance().getOrganismMap().get( this.getProjectMapItem().getOrganism() );
		try {
			matureMap	= MsbEngine.getInstance().getMiRBaseMap().get( this.getProjectMapItem().getMiRBAseVersion() ).getAllMicroRnaMaturesByMirid();
			lst = MsbEngine.getInstance().getMiRBaseMap().get( this.getProjectMapItem().getMiRBAseVersion() ).getMicroRnaPrematureList( org_code );
		}catch(Exception e) {
			MsbEngine.logger.error("Error : ", e);
			return null;
		}

		/*********************************************************************************************************
		 *  If there are not any model int the model object
		 */
//		if( this.getProjectMapItem().getTotalModelMap() == null || this.getProjectMapItem().getTotalModelMap().size() == 0 ) {
		if( this.getProjectMapItem().getProjectStatus() == JMsbSysConst.STS_DONE_INDEX_FILES ) {
			this.choosePanel.getOwnerDialog().setIndeterminate( false );
			this.choosePanel.getOwnerDialog().setProgressBarRange(0, this.getProjectMapItem().getSampleFileList().size() * lst.size() );
			this.choosePanel.getOwnerDialog().setProgressToGetMiRnas( 0 );

			SwingUtilities.setWaitCursorFor( this.getOwnerDialog() );

			MsbEngine.logger.debug("START Thread Manager for reading file(s)");

			List<Object[]> fileList = this.getProjectMapItem().getSampleFileList();
//				this.set_NO_THREAD_PROCESS_( fileList.size() );
//	
//				this.progressIncrement =	(double)this.get_NO_THREAD_PROCESS_()*lst.size() / this.get_NO_THREAD_PROCESS_();

			final List<Future<NGSFileReadResultObj>> list = new ArrayList<Future<NGSFileReadResultObj>>();
			this.latch = new CountDownLatch( fileList.size() );
			for(int i=0; i<fileList.size(); i++) {
				Object[] divs = fileList.get(i);
				String groupId = divs[0].toString();
				String sampleId = divs[1].toString();
				Sample sample = (Sample)divs[2];

				NGSFileReadCallable work = new NGSFileReadCallable( this.latch, groupId, sampleId, sample.getSortedPath(), sample.getIndexPath(), matureMap, lst, this.getProjectMapItem() );
				work.setNGSTestCaller( this );
				Future<NGSFileReadResultObj> future = MsbEngine.getExecutorService().submit( work );
				
				list.add( future );
			}
			
			/**
			 * Case 1 :
			 */
			MsbEngine.getExecutorService().submit( new java.util.concurrent.Callable<Void>(){
				@Override
				public Void call() {
					// TODO Auto-generated method stub
					try {
						remote.latch.await();

						for( Future<NGSFileReadResultObj> future:list ) {
							NGSFileReadResultObj obj = future.get();
	
							remote.updateDataFromFiles( obj.getGroup(), obj.getSample(), obj.getSequenceMap(), obj.getMatureProfileMap(), obj.getPrematureList() );
//							remote.updateDataFromFiles( obj.getGroup(), obj.getSample(), obj.getReadMap(), obj.getMatureProfileMap(), obj.getPrematureList() );
						}
						remote.choosePanel.getOwnerDialog().setIndeterminate( false );
						remote.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE_READ_FILES );

						remote.callback();
					}catch(InterruptedException e) {
						remote.rollback( JMsbSysConst.STS_DONE_INDEX_FILES );

						remote.callbackInterrupt();
					}catch(Exception e) {
						remote.rollback( JMsbSysConst.STS_DONE_INDEX_FILES );

						remote.callbackByException("Interrupted indexing (await) : ", e);
					}
					return null;
				}
			});
		}else if( this.getProjectMapItem().getProjectStatus() == JMsbSysConst.STS_DONE_READ_FILES ) {
			this.callback();
		}else if( this.getProjectMapItem().getProjectStatus() == JMsbSysConst.STS_DONE_MAKE_MODELS ) {
			try{
				/********* Rna list call to display in dialog *******/
				{
					NGSRnaListCallable callable = new NGSRnaListCallable( this.getProjectMapItem(), 0 );
					callable.setNGSTestCaller( this );
					
					Future<NGSRnaListObj> future = MsbEngine.getExecutorService().submit( callable );
		
					try {
						NGSRnaListObj obj = future.get();
		
						this.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE_SUMMARIZE_RNAS );
						
						this.callback( obj.getHeader(), obj.getReadedAllObjList(), obj.getChoosedRnaObjList() );
					}catch(InterruptedException ie) {
						throw ie;
					}catch(Exception e) {
						throw e;
					}
				}
			}catch(InterruptedException e) {
				remote.callbackInterrupt();
			}catch(Exception e) {
				remote.callbackByException("Interrupted indexing (await) : ", e);
			}finally{
				try {
					MsbEngine.logger.debug("Store ProjectMapItems");
					ProjectManager.storeProjectMapItem( this.getProjectMapItem() );
					MsbEngine.logger.debug("Completed ProjectMapItems storing");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					MsbEngine.logger.error("Error ", e);
				}
			}
		}else if( this.getProjectMapItem().getProjectStatus() >= JMsbSysConst.STS_DONE_SUMMARIZE_RNAS ) {
			this.callback( this.getProjectMapItem().getMiRnaTableHeader(), this.getProjectMapItem().getReadedAllObjList(), this.getProjectMapItem().getChoosedRnaObjList() );
		}

		return null;
	}

	public void callback( Object[] header, List<Object[]> readedAllObjList, List<Object[]> choosedRnaObjList ) {
		try {
//			if( this.choosePanel != null ) {
				this.choosePanel.setTableHeader( header );
				this.choosePanel.setTableModel( readedAllObjList );

				this.getProjectMapItem().setChoosedRnaObjList( choosedRnaObjList );
				this.getProjectMapItem().setReadedAllObjList( readedAllObjList );
				this.getProjectMapItem().setMiRnaTableHeader( header );
//			}

//			MsbEngine.logger.debug("Store ProjectMapItems");
//			ProjectManager.storeProjectMapItem( this.getProjectMapItem() );
//			MsbEngine.logger.debug("Completed ProjectMapItems storing");
			
			kobic.msb.common.util.SwingUtilities.setDefaultCursorFor( this.getOwnerDialog() );
		}catch(Exception e) {
			MsbEngine.logger.error( "error : ", e );
		}finally{
			this.choosePanel.getOwnerDialog().allActivateButtons();
		}
	}
	
	private void updateDataFromFiles(  String group, String sample, Map<String, LinkedHashMap<String, GroupSamInfo>> sequenceMap, Map<String, Integer> matureProfileMap, List<HairpinVO> prematureList ) throws Exception{
		this.updatePrematureList( prematureList );
		this.updateSequenceMap( sequenceMap );
		this.updateProfileMap( matureProfileMap, sample );
	}
	
//	private void updateDataFromFiles(  String group, String sample, LinkedHashMap<String, String> readMap, Map<String, Integer> matureProfileMap, List<HairpinVO> prematureList ) throws Exception{
//		this.updatePrematureList( prematureList );
//		this.updateReadMap( readMap );
//		this.updateProfileMap( matureProfileMap, sample );
//	}

	public void callback() {	
//		if( this.getReadMap().size() > 0 ) {
		if( this.getSequenceMap().size() > 0 ) {
			MsbEngine.logger.debug("Start model to write");
			System.out.println("Start writing models to project directory" );
			JMsbBrowserModelCallable mdlCallable = new JMsbBrowserModelCallable( this.getProjectMapItem(), this.getSequenceMap(), this.getPrematureList() );
//			JMsbBrowserModelCallable mdlCallable = new JMsbBrowserModelCallable( this.getProjectMapItem(), this.getReadMap(), this.getPrematureList() );
			mdlCallable.setNGSTestCaller( this );
			Future<Void> result = MsbEngine.getExecutorService().submit( mdlCallable );

			try {
				/********* Write Model files in the project directory *******/
				{
					result.get();
	
					MsbEngine.logger.debug("Complete Build Model");
					System.out.println("Build profile" );
					this.buildProfile();
					MsbEngine.logger.debug("Complete Build Profile");
	
					this.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE_MAKE_MODELS );
//					this.choosePanel.getOwnerDialog().setIndeterminate(false);
				}
			}catch(InterruptedException e) {
				this.rollback( JMsbSysConst.STS_DONE_INDEX_FILES );
				remote.callbackInterrupt();
			}catch(Exception e) {
				this.rollback( JMsbSysConst.STS_DONE_INDEX_FILES );
				remote.callbackByException("Interrupted indexing (await) : ", e);
			}

			if( this.getProjectMapItem().getProjectStatus() == JMsbSysConst.STS_DONE_MAKE_MODELS ) {
				try{
					/********* Rna list call to display in dialog *******/
					{
						NGSRnaListCallable callable = new NGSRnaListCallable( this.getProjectMapItem(), 0 );
						callable.setNGSTestCaller( this );
						
						Future<NGSRnaListObj> future = MsbEngine.getExecutorService().submit( callable );
			
						try {
							NGSRnaListObj obj = future.get();
			
							this.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE_SUMMARIZE_RNAS );
							
							this.callback( obj.getHeader(), obj.getReadedAllObjList(), obj.getChoosedRnaObjList() );
						}catch(InterruptedException ie) {
							throw ie;
						}catch(Exception e) {
							throw e;
						}
					}
				}catch(InterruptedException e) {
					this.rollback( JMsbSysConst.STS_DONE_MAKE_MODELS );
					remote.callbackInterrupt();
				}catch(Exception e) {
					this.rollback( JMsbSysConst.STS_DONE_MAKE_MODELS );
					remote.callbackByException("Interrupted indexing (await) : ", e);
				}finally{
					try {
						MsbEngine.logger.debug("Store ProjectMapItems");
						ProjectManager.storeProjectMapItem( this.getProjectMapItem() );
						MsbEngine.logger.debug("Completed ProjectMapItems storing");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						MsbEngine.logger.error("Error ", e);
					}
				}
			}
		}else {
			JOptionPane.showMessageDialog( this.choosePanel.getOwnerDialog(), "Sorry! We cannot find any miRna(s) for " + this.getProjectMapItem().getOrganism() + " organism", "Error organism problem", JOptionPane.ERROR_MESSAGE );
			try {
				this.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE_CREATE_EMPTY_PROJECT );
				MsbEngine.logger.debug("Store ProjectMapItems");
				ProjectManager.storeProjectMapItem( this.getProjectMapItem() );
				MsbEngine.logger.debug("Completed ProjectMapItems storing");

				this.choosePanel.getOwnerDialog().updateCurrentState( this.getProjectMapItem() );
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				MsbEngine.logger.error("Error ", e);
			}
			if( this.getOwnerDialog().isIndeterminate() )	this.getOwnerDialog().setIndeterminate( false );
			else											this.getOwnerDialog().setProgressToGetMiRnas(0);
			
			kobic.msb.common.util.SwingUtilities.setDefaultCursorFor( this.getOwnerDialog() );
		}
	}
/*
	@Override
	public void callback( String group, String sample, Map<String, List<GroupSamInfo>> sequenceMap, Map<String, Integer> profileMap, List<HairpinVO> prematureList ) {
		this.updateDataFromFiles(group, sample, sequenceMap, profileMap, prematureList);

//		this.set_NO_COMPLETED_THREAD_( this.get_NO_COMPLETED_THREAD_() + 1 );
//		this.setProcessingProgress( (int)(this.getProcessingProgress() * this.progressIncrement) );

		try {
//			if( this.get_NO_COMPLETED_THREAD_() == this.get_NO_THREAD_PROCESS_() ) {
			if( this.latch.getCount() == 0 ) {
				this.choosePanel.getOwnerDialog().setIndeterminate(false);

				if( this.getSequenceMap().size() > 0 ) {
					MsbEngine.logger.debug("Start model to write");
					JMsbBrowserModelCallable mdlCallable = new JMsbBrowserModelCallable( this.getProjectMapItem(), this.getSequenceMap(), this.getPrematureList() );
					mdlCallable.setNGSTestCaller( this );
					Future<Void> result = MsbEngine.getExecutorService().submit( mdlCallable );
	
					result.get();
	
					MsbEngine.logger.debug("Complete Build Model");
					this.buildProfile();
					MsbEngine.logger.debug("Complete Build Profile");

					NGSRnaListCallable callable = new NGSRnaListCallable( this.getProjectMapItem(), 0 );
					callable.setNGSTestCaller( this );
					Future<NGSRnaListObj> future = MsbEngine.getExecutorService().submit( callable );
					
					NGSRnaListObj obj = future.get();
					
					this.callback( obj.getHeader(), obj.getReadedAllObjList(), obj.getChoosedRnaObjList() );
				}else {
					JOptionPane.showMessageDialog( this.choosePanel.getOwnerDialog(), "Sorry! We cannot find any miRna(s) for " + this.getProjectMapItem().getOrganism() + " organism", "Error organism problem", JOptionPane.ERROR_MESSAGE );
					this.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE_SUMMARIZE_RNAS );
					this.choosePanel.getOwnerDialog().updateCurrentState( this.getProjectMapItem() );
					
					kobic.msb.common.util.SwingUtilities.setDefaultCursorFor( this.getOwnerDialog() );
				}
			}
		}catch(InterruptedException e) {
			this.callbackInterrupt();
		}catch(Exception e) {
			this.callbackByException("Callback & NGSRnaListCallable Error : ", e);
		}
	}
*/

	@Override
	public void patch(JPanel japnel, ProjectMapItem projectMapItem) {
		// TODO Auto-generated method stub
		this.choosePanel = (JMsbMatureChoosePanel) japnel;
		this.setProjectMapItem( projectMapItem );
	}
	
//	private void rollback( int status ) {
//		if( status == JMsbSysConst.STS_DONE_READ_FILES ) {
//			this.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE_READ_FILES );
//			this.getProjectMapItem().getTotalModelMap().clear();
//			this.getProjectMapItem().getModelMap().clear();
//			try {
//				MsbEngine.logger.debug("Store ProjectMapItems");
//				ProjectManager.storeProjectMapItem( this.getProjectMapItem() );
//				MsbEngine.logger.debug("Completed ProjectMapItems storing");
//			} catch (Exception ie) {
//				// TODO Auto-generated catch block
//				MsbEngine.logger.error("Error ", ie);
//			}
//		}
//	}
}
