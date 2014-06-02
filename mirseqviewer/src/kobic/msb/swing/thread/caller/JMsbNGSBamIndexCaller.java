package kobic.msb.swing.thread.caller;

import java.awt.EventQueue;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import javax.swing.JPanel;

import kobic.com.log.MessageConsole;
import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.SwingConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.server.obj.GroupSamInfo;
import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.swing.panel.newproject.JBamFilePreProcessingPanel;
import kobic.msb.swing.thread.callable.JMsbNGSBamIndexCallable;
import kobic.msb.swing.thread.callable.JMsbNGSBamIndexSwingWorker;
import kobic.msb.system.catalog.ProjectMap;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class JMsbNGSBamIndexCaller<T> extends AbstractThreadCaller<T>{
	private JBamFilePreProcessingPanel observerPanel;
	private CountDownLatch latch;
	
	JMsbNGSBamIndexCaller<T> remote = JMsbNGSBamIndexCaller.this;

	public JMsbNGSBamIndexCaller( JProjectDialog dialog ) {
		this( dialog, null );
	}

	public JMsbNGSBamIndexCaller( JProjectDialog dialog, ProjectMapItem projectMapItem ) {
		super( dialog, projectMapItem );

		this.observerPanel	= dialog.getBamFilePreProcessingPanel();
	}
	
	public JBamFilePreProcessingPanel getBamFilePreProcessingPanel() {
		return this.observerPanel;
	}

	@Override
	public T run() {
		SwingUtilities.setWaitCursorFor( this.getOwnerDialog() );
		this.getOwnerDialog().setIndeterminate(true);
		/********************************************************************************************************
		 * To read sample information from JTable on the JPanel
		 */
		UpdatableTableModel tableModel = (UpdatableTableModel)this.observerPanel.getSampleTable().getModel();
		
		// Initialize sort and index path of sample
		this.initSampleFilePathes( this.getProjectMapItem().getProjectInfo().getSamples().getGroup() );

		// Sort and Index a bam file and move to temporary directory (If there is not this, make it )
		String directory = MsbEngine.engine.getSystemProperties().getProperty("msb.project.workspace") + File.separator + this.getProjectName() + File.separator + "tmp";
		File baseDir = new File( directory );
		if( !baseDir.exists() )	baseDir.mkdir();

		// initialize completed thread count to 0
		// to get threads from thread pool
		this.set_NO_COMPLETED_THREAD_( 0 );
		this.set_NO_THREAD_PROCESS_( tableModel.getRowCount() );

		this.latch = new CountDownLatch( tableModel.getRowCount() );

		MessageConsole mc = new MessageConsole( this.observerPanel.getTextPane() );
		mc.redirectOut();
//		this.mc.redirectErr(new java.awt.Color(0, 53, 112), null);
		mc.setMessageLines( 20 );

//		List<Future<ProjectMapItem>> futureList = new ArrayList<Future<ProjectMapItem>>();
//		this.setProcessingProgress(0);
		for(int THREAD_NO=0; THREAD_NO<tableModel.getRowCount(); THREAD_NO++) {
			tableModel.setValueAt(false, THREAD_NO, 0);

			String groupId		= (String)tableModel.getValueAt( THREAD_NO, 2 );
			String sampleId		= (String)tableModel.getValueAt( THREAD_NO, 3 );

			String PID = SwingConst.PROCESS_HEADER + THREAD_NO;

			JMsbNGSBamIndexCallable worker = new JMsbNGSBamIndexCallable( this.latch, PID, THREAD_NO, MsbEngine.engine, this.getProjectName(), groupId, sampleId, directory, this, this.getProjectMapItem() );

			MsbEngine.getExecutorService().submit( worker );
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

					remote.callback( remote.getProjectMapItem() );
				}catch(InterruptedException e) {
					remote.rollback( JMsbSysConst.STS_DONE_CHOOSE_ORGANISM );
					remote.callbackInterrupt();
				}catch(Exception e) {
					remote.rollback( JMsbSysConst.STS_DONE_CHOOSE_ORGANISM );
					remote.callbackByException("Interrupted indexing (await) : ", e);
				}
				return null;
			}
		});
/***/

/***
 * Case 2 :
 * 
		EventQueue.invokeLater( new Runnable() {
			public void run() {
				try {
					remote.latch.await();
					remote.callback( remote.getProjectMapItem() );
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					remote.callbackInterrupt();
				}  // wait until latch counted down to 0
				return;
			}
		});
***/

		return null;
	}

	public void callback( ProjectMapItem item ) {
		try {
//			MsbEngine.logger.debug( "Completed thread : " + this.get_NO_COMPLETED_THREAD_() + " " + this.get_NO_COMPLETED_THREAD_() );
//
//			this.set_NO_COMPLETED_THREAD_( this.get_NO_COMPLETED_THREAD_() + 1 );
//			if( this.get_NO_COMPLETED_THREAD_() == this.get_NO_THREAD_PROCESS_() ) {
				// If there are already choosed miRNAs
				// Remove all of them
				item.reset();
		
//				remove all future

//				synchronized( item ) {
					if( JMsbNGSBamIndexCaller.canDoNextStep( item.getProjectInfo().getSamples().getGroup() ) == false ) {
						item.setProjectStatus( JMsbSysConst.STS_DONE_CHOOSE_ORGANISM );
					}else{
						item.setProjectStatus( JMsbSysConst.STS_DONE_INDEX_FILES );
					}

					ProjectMap map = MsbEngine.engine.getProjectManager().getProjectMap();
					map.putProject( this.getProjectName(), item);
					map.writeToFile( MsbEngine.engine.getProjectManager().getSystemFileToGetProjectList() );

//					this.setProcessingProgress(100);

					// Call JMsbNGSTestCaller to read miRNA list
					if( this.getOwnerDialog() instanceof JProjectDialog ) {
						JProjectDialog dialog = (JProjectDialog) this.getOwnerDialog();
						
						MsbEngine.logger.debug("Finished sorting & indexing...");
						dialog.updateCurrentState( item );
					}
//					SwingUtilities.setDefaultCursorFor( this.getOwnerDialog() );
//				}
//			}
		}catch(Exception e) {
			this.callbackByException("BamIndexCaller Error", e);
		}finally{
			this.getOwnerDialog().setIndeterminate(false);

			this.setChanged();
			this.notifyObservers( item );
		}
	}

	public void callback( ProjectMapItem item, int id ) {
		if( this.latch.getCount() == 0 ) {
			this.callback(item);
		}
	}
/*
	public void callback( ProjectMapItem item, int id ) {
		try {
//			MsbEngine.logger.debug( "Completed thread : " + this.get_NO_COMPLETED_THREAD_() + " " + this.get_NO_COMPLETED_THREAD_() );
//
//			this.set_NO_COMPLETED_THREAD_( this.get_NO_COMPLETED_THREAD_() + 1 );
//			if( this.get_NO_COMPLETED_THREAD_() == this.get_NO_THREAD_PROCESS_() ) {
			MsbEngine.logger.debug( "Not completed thread : " + this.latch.getCount() );
			if( this.latch.getCount() == 0 ) {
				// If there are already choosed miRNAs
				// Remove all of them
				item.reset();
				
// remove all future
		
//				synchronized( item ) {
					if( this.canDoNextStep( item.getProjectInfo().getSamples().getGroup() ) == false ) {
						item.setProjectStatus( JMsbSysConst.READY_TO_FILE_PROCESS );
					}else{
						item.setProjectStatus( JMsbSysConst.READY_TO_CHOOSE_MIRNAS );
					}

					ProjectMap map = MsbEngine.engine.getProjectManager().getProjectMap();
					map.putProject( this.getProjectName(), item);
					map.writeToFile( MsbEngine.engine.getProjectManager().getSystemFileToGetProjectList() );

//					this.setProcessingProgress(100);

					// Call JMsbNGSTestCaller to read miRNA list
					if( this.getOwnerDialog() instanceof JProjectDialog ) {
						JProjectDialog dialog = (JProjectDialog) this.getOwnerDialog();
						dialog.updateCurrentState( item );
					}

					this.getProgressBar().setIndeterminate(false);
//					SwingUtilities.setDefaultCursorFor( this.getOwnerDialog() );
//				}
			}
		}catch(Exception e) {
			MsbEngine.logger.error("error : ", e);
		}finally{
			this.setChanged();
			this.notifyObservers( item );
		}
	}
*/

	/*******************************************************************
	 * If any file is not completed sorting and indexing
	 * Browser can not go next step
	 * 
	 * @param groupList
	 * @return
	 */
	public static boolean canDoNextStep(List<Group> groupList) {
		Iterator<Group> iterGroup = groupList.iterator();
		int total_count = 0;
		int true_count = 0;
		while( iterGroup.hasNext() ) {
			Group grp = iterGroup.next();
			Iterator<Sample> iterSample = grp.getSample().iterator();
			while( iterSample.hasNext() ) {
				Sample smp = iterSample.next();
	
				if( !Utilities.nulltoEmpty( smp.getIndexPath() ).equals("") ) {
					true_count++;
				}
				total_count++;
			}
		}
		return (total_count==true_count);
	}

	/*****************************************************************
	 * Initialize sample (sort * index) file path
	 * 
	 * @param groupList
	 * @return
	 */
	private boolean initSampleFilePathes(List<Group> groupList) {
		Iterator<Group> iterGroup = groupList.iterator();
		int total_count = 0;
		int true_count = 0;
		while( iterGroup.hasNext() ) {
			Group grp = iterGroup.next();
			Iterator<Sample> iterSample = grp.getSample().iterator();
			while( iterSample.hasNext() ) {
				Sample smp = iterSample.next();
				smp.setIndexPath("");
				smp.setSortedPath("");
			}
		}
		return (total_count==true_count);
	}

	@Override
	public void patch( JPanel japnel, ProjectMapItem projectMapItem ) {
		// TODO Auto-generated method stub
		this.observerPanel = (JBamFilePreProcessingPanel) japnel;
		this.setProjectMapItem( projectMapItem );
	}

//	@Override
//	public void callback(Object[] header, List<Object[]> readedAllObjList,
//			List<Object[]> choosedRnaObjList) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void callback(String group, String sample,
//			Map<String, List<GroupSamInfo>> sequenceMap,
//			Map<String, Integer> profileMap, List<HairpinVO> prematureList) {
//		// TODO Auto-generated method stub
//	}
}


