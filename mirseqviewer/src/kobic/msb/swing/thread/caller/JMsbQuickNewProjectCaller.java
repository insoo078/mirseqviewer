package kobic.msb.swing.thread.caller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kobic.com.log.MessageConsole;
import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.db.sqlite.vo.MatureVO;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.server.obj.GroupSamInfo;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JMsbQuickNewProjectDialog;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.swing.listener.projectdialog.CreateProjectListener;
import kobic.msb.swing.thread.callable.JMsbBrowserModelCallable;
import kobic.msb.swing.thread.callable.JMsbFileCopyingCallable;
import kobic.msb.swing.thread.callable.NGSFileReadCallable;
import kobic.msb.swing.thread.callable.NGSRnaListCallable;
import kobic.msb.swing.thread.callable.obj.NGSFileReadResultObj;
import kobic.msb.swing.thread.callable.obj.NGSRnaListObj;
import kobic.msb.system.catalog.ProjectMap;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import kobic.msb.system.project.ProjectManager;

public class JMsbQuickNewProjectCaller<T> extends JMsbNewProjectCommonCaller<T>{	
	private LinkedHashMap<String, Object[]> 	defaultMiRnas;
	private MessageConsole						mc;
	
	private CountDownLatch						latch;
	
	JMsbQuickNewProjectCaller remote = JMsbQuickNewProjectCaller.this;

	public JMsbQuickNewProjectCaller( JMsbQuickNewProjectDialog dialog ){
		this( dialog, null );
	}

	public JMsbQuickNewProjectCaller( JMsbQuickNewProjectDialog dialog, ProjectMapItem projectMapItem ) {
		super( dialog, projectMapItem );
		
		this.defaultMiRnas	= new LinkedHashMap<String, Object[]>();

		this.mc				= new MessageConsole( this.getOwnerDialog().getTextScrollPane() );
		this.mc.redirectOut();
		this.mc.redirectErr(new java.awt.Color(0, 53, 112), null);
		this.mc.setMessageLines( 100 );
	}

	@Override
	public T run() {
		try {
			JMsbQuickNewProjectDialog dialog = (JMsbQuickNewProjectDialog) this.getOwnerDialog();
			
			dialog.setIndeterminate( true );

			kobic.msb.common.util.SwingUtilities.setWaitCursorFor( this.getOwnerDialog().getOwner() );

			System.out.println("doBackgroud process");
			Map<String, List<MatureVO>> matureMap		= MsbEngine.getInstance().getMiRBaseMap().get( this.getProjectMapItem().getMiRBAseVersion() ).getAllMicroRnaMaturesByMirid();

			String org_code		= MsbEngine.getInstance().getOrganismMap().get( this.getProjectMapItem().getOrganism() );

			List<HairpinVO> lst = MsbEngine.getInstance().getMiRBaseMap().get( this.getProjectMapItem().getMiRBAseVersion() ).getMicroRnaPrematureList( org_code );

			/*********************************************************************************************************
			 *  If there are not any model int the model object
			 */
			if( this.getProjectMapItem().getTotalModelMap() == null || this.getProjectMapItem().getTotalModelMap().size() == 0 ) {
				MsbEngine.logger.debug("START Reading Program");

				List<Object[]> fileList = this.getProjectMapItem().getSampleFileList();
				this.set_NO_THREAD_PROCESS_( fileList.size() );

				System.out.println("Add Sample file to miRseq project" );
				String projectBaseDir = MsbEngine.engine.getSystemProperties().getProperty("msb.project.workspace") + File.separator + this.getProjectName();
				String resourcesDirectory = projectBaseDir + File.separator + "tmp";

				File projectBaseDirFile = new File(projectBaseDir);
				File resourcesDirFile = new File(resourcesDirectory);

				if( !projectBaseDirFile.exists() )	{
					System.out.println("Make project base directory");
					projectBaseDirFile.mkdir();
				}
				if( !resourcesDirFile.exists() )	{
					System.out.println("Make project resources directory");
					resourcesDirFile.mkdir();
				}

				if( this.getProjectMapItem().getProjectStatus() < JMsbSysConst.STS_DONE_INDEX_FILES ) {
					this.latch = new CountDownLatch( fileList.size() );
					for(int i=0; i<fileList.size(); i++) {
						Object[] divs = fileList.get(i);
						String groupId = divs[0].toString();
						String sampleId = divs[1].toString();
						Sample sample = (Sample)divs[2];
	
						String samplePath = sample.getSamplePath();
						String baiFilePath = sample.getSamplePath() + ".bai";
						String newSampleFilePath	= resourcesDirectory + File.separator + Utilities.getOnlyFileName( samplePath );
						String newBaiFilePath		= resourcesDirectory + File.separator + Utilities.getOnlyFileName( baiFilePath );
	
//						JMsbFileCopyingCallable work = new JMsbFileCopyingCallable(
//								this.latch
//								, new File(samplePath), new File(newSampleFilePath)
//								, new File(baiFilePath), new File(newBaiFilePath), sample, sampleId, groupId, lst, matureMap, this
//						);
//	
//						MsbEngine.getExecutorService().submit( work );
						
						sample.setSortedPath( samplePath );
						sample.setIndexPath( baiFilePath );
					}
					
					remote.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE_INDEX_FILES );
					remote.callback( remote.getProjectMapItem() );
					
//					/**
//					 * Case 1 :
//					 */
//					MsbEngine.getExecutorService().submit( new java.util.concurrent.Callable<Void>(){
//						@Override
//						public Void call() {
//							// TODO Auto-generated method stub
//							try {
//								remote.latch.await();
//
//								System.out.println("Done copying files" );
//
//								remote.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE_INDEX_FILES );
//								remote.callback( remote.getProjectMapItem() );
//							}catch(InterruptedException e) {
//								remote.rollback( JMsbSysConst.STS_DONE_CHOOSE_ORGANISM );
//								remote.callbackInterrupt();
//							}catch(Exception e) {
//								remote.rollback( JMsbSysConst.STS_DONE_CHOOSE_ORGANISM );
//								remote.callbackByException("Interrupted indexing (await) : ", e);
//							}
//							return null;
//						}
//					});
//					/***/
				}else {
					remote.callback( remote.getProjectMapItem() );
				}

				MsbEngine.logger.debug("END PROGRAM");
			}
		}catch(Exception e) {
			MsbEngine.logger.error("error : ", e);
		}
		
		return null;
	}
	
	public void callback( ProjectMapItem item ) {
		kobic.msb.common.util.SwingUtilities.setWaitCursorFor( this.getOwnerDialog() );

		Map<String, List<MatureVO>> matureMap		= MsbEngine.getInstance().getMiRBaseMap().get( this.getProjectMapItem().getMiRBAseVersion() ).getAllMicroRnaMaturesByMirid();

		String org_code		= MsbEngine.getInstance().getOrganismMap().get( this.getProjectMapItem().getOrganism() );

		List<HairpinVO> lst = MsbEngine.getInstance().getMiRBaseMap().get( this.getProjectMapItem().getMiRBAseVersion() ).getMicroRnaPrematureList( org_code );

		/*********************************************************************************************************
		 *  If there are not any model int the model object
		 */
		if( this.getProjectMapItem().getProjectStatus() == JMsbSysConst.STS_DONE_INDEX_FILES ) {
			SwingUtilities.setWaitCursorFor( this.getOwnerDialog() );

			MsbEngine.logger.debug("START Thread Manager for reading file(s)");

			List<Object[]> fileList = this.getProjectMapItem().getSampleFileList();

			final List<Future<NGSFileReadResultObj>> list = new ArrayList<Future<NGSFileReadResultObj>>();
			this.latch = new CountDownLatch( fileList.size() );
			for(int i=0; i<fileList.size(); i++) {
				Object[] divs = fileList.get(i);
				String groupId = divs[0].toString();
				String sampleId = divs[1].toString();
				Sample sample = (Sample)divs[2];

				System.out.println("Reading " + sample.getSortedPath() + " file" );
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
						
						System.out.println("Done reading all bam files" );
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
						
						System.out.println("Storing miRseq project...");
						this.goNext( this.getProjectName() );
			
						JMsbQuickNewProjectDialog dialog = (JMsbQuickNewProjectDialog) this.getOwnerDialog();
						kobic.msb.common.util.SwingUtilities.setDefaultCursorFor( this.getOwnerDialog() );
			
						dialog.setIndeterminate( false );
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
		}else if( this.getProjectMapItem().getProjectStatus() == JMsbSysConst.STS_DONE_SUMMARIZE_RNAS ) {
			this.callback( this.getProjectMapItem().getMiRnaTableHeader(), this.getProjectMapItem().getReadedAllObjList(), this.getProjectMapItem().getChoosedRnaObjList() );
		}
	}
	
	public void updateDataFromFiles(  String group, String sample, Map<String, LinkedHashMap<String, GroupSamInfo>> sequenceMap, Map<String, Integer> profileMap, List<HairpinVO> prematureList ) throws Exception{
		this.updatePrematureList( prematureList );
		this.updateSequenceMap( sequenceMap );
		this.updateProfileMap( profileMap, sample );
	}
	
//	public void updateDataFromFiles(  String group, String sample, LinkedHashMap<String, String> readMap, Map<String, Integer> profileMap, List<HairpinVO> prematureList ) throws Exception{
//		this.updatePrematureList( prematureList );
//		this.updateReadMap( readMap );
//		this.updateProfileMap( profileMap, sample );
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
				}
			}catch(InterruptedException e) {
				this.rollback( JMsbSysConst.STS_DONE_READ_FILES );
				remote.callbackInterrupt();
			}catch(Exception e) {
				this.rollback( JMsbSysConst.STS_DONE_READ_FILES );
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
			JOptionPane.showMessageDialog( this.getOwnerDialog(), "Sorry! We cannot find any miRna(s) for " + this.getProjectMapItem().getOrganism() + " organism", "Error organism problem", JOptionPane.ERROR_MESSAGE );
			
			kobic.msb.common.util.SwingUtilities.setDefaultCursorFor( this.getOwnerDialog() );
		}
	}

	public void callback( Object[] header, List<Object[]> readedAllObjList,	List<Object[]> choosedRnaObjList ) {
		// TODO Auto-generated method stub
		try {
			this.getProjectMapItem().setChoosedRnaObjList( choosedRnaObjList );
			this.getProjectMapItem().setReadedAllObjList( readedAllObjList );
			this.getProjectMapItem().setMiRnaTableHeader( header );
			
			this.setTableModel( readedAllObjList );

			MsbEngine.logger.debug("Store ProjectMapItems");
			ProjectManager.storeProjectMapItem( this.getProjectMapItem() );
			MsbEngine.logger.debug("Completed ProjectMapItems storing");
	
			System.out.println("Storing miRseq project...");
			this.goNext( this.getProjectName() );
	
			JMsbQuickNewProjectDialog dialog = (JMsbQuickNewProjectDialog) this.getOwnerDialog();
			kobic.msb.common.util.SwingUtilities.setDefaultCursorFor( this.getOwnerDialog() );
	
			dialog.setIndeterminate( false );
		}catch(Exception e) {
			MsbEngine.logger.error( "error : ", e );
		}
	}


	@Override
	public void patch(JPanel japnel, ProjectMapItem projectMapItem) {
		// TODO Auto-generated method stub
	}

	private void goNext(String projectName) {
		try {
			HashMap<String, Object[]> map = this.defaultMiRnas;
			JMsbBrowserMainFrame controllerFrame = (JMsbBrowserMainFrame) this.getOwnerDialog().getOwner();
			CreateProjectListener.createStep4( map, projectName, controllerFrame );

			this.getOwnerDialog().dispose();
		}catch(Exception e) {
			MsbEngine.logger.error("Error", e);
		}
	}

	private void setTableModel( List<Object[]> listModel ){
		try {
			this.defaultMiRnas.clear();
			
			for(Object[] objs:listModel) {
				String mirid = objs[1].toString();
				this.defaultMiRnas.put( mirid, objs );
			}
		}catch(Exception e) {
			MsbEngine.logger.error("error : ", e);
		}
	}
}
