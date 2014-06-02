package kobic.msb.swing.thread.caller;

import java.awt.Cursor;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.server.obj.GroupSamInfo;
import kobic.msb.swing.frame.dialog.JCommonNewProjectDialog;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import kobic.msb.system.project.ProjectManager;

public abstract class AbstractThreadCaller<T> extends Observable{
	private JCommonNewProjectDialog		dialog;
	private double						progress;
	
	private ProjectMapItem				projectMapItem;

	private int							_NO_THREAD_PROCESS_;
	private int							_NO_COMPLETED_THREAD_;

	public AbstractThreadCaller(JCommonNewProjectDialog dialog) {
		this.dialog					= dialog;
		this.progress				= 0;
		this._NO_COMPLETED_THREAD_	= 0;
		this._NO_THREAD_PROCESS_	= 0;
	}
	
	public AbstractThreadCaller( JCommonNewProjectDialog dialog, ProjectMapItem projectMapItem ) {
		this(dialog);
		
		this.projectMapItem = projectMapItem;
	}

	public void setProjectMapItem( ProjectMapItem projectMapItem ) {
		this.projectMapItem = projectMapItem;
	}
	
	public ProjectMapItem getProjectMapItem() {
		return this.projectMapItem;
	}
	
	public String getProjectName() {
		return this.projectMapItem.getProjectName();
	}

	public int get_NO_THREAD_PROCESS_() {
		return _NO_THREAD_PROCESS_;
	}

	public void set_NO_THREAD_PROCESS_(int _NO_THREAD_PROCESS_) {
		this._NO_THREAD_PROCESS_ = _NO_THREAD_PROCESS_;
	}

	public int get_NO_COMPLETED_THREAD_() {
		return _NO_COMPLETED_THREAD_;
	}

	public void set_NO_COMPLETED_THREAD_(int _NO_COMPLETED_THREAD_) {
		this._NO_COMPLETED_THREAD_ = _NO_COMPLETED_THREAD_;
	}

	public int getThreadSize() {
		return this._NO_THREAD_PROCESS_;
	}
	public double getProcessingProgress(){
		return this.progress;
	}
	public void setProcessingProgress( double val ){
		this.progress = val;

		this.dialog.setProgressToGetMiRnas( (int)this.progress );
	}
	public JCommonNewProjectDialog getOwnerDialog() {
		return this.dialog;
	}
//	public JProgressBar getProgressBar() {
//		return this.dialog.getProgressBar();
//	}
	
	public abstract T run();
	public abstract void patch(JPanel japnel, ProjectMapItem projectMapItem);
//	public abstract void callback(Object[] header, List<Object[]> readedAllObjList, List<Object[]> choosedRnaObjList);
//	public abstract void callback( String group, String sample, Map<String, List<GroupSamInfo>> sequenceMap, Map<String, Integer> profileMap, List<HairpinVO> prematureList );
	public void callbackInterrupt() {
//		// TODO Auto-generated catch block
//		JOptionPane pane = new JOptionPane( "Background processes have been interrupted", JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION );
//		javax.swing.JDialog dialog = pane.createDialog( this.dialog, "Interrupted work" );
//		dialog.setModal(true);
//		dialog.setVisible(true);

		System.out.println("Interrupted");
		this.callbackStopAndMessage( "Interrupt thread" );
	}
	
	private void callbackStopAndMessage(String message) {
		if( this.dialog.getCursor().equals(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR ) ) )	
			kobic.msb.common.util.SwingUtilities.setDefaultCursorFor( this.dialog );

		MsbEngine.logger.error( "Callback stop and message " + message );

		if( this.dialog.isIndeterminate() )	{
			this.dialog.setIndeterminate( false );
		}else {
			this.dialog.setProgressToGetMiRnas(0);
		}

		this.dialog.getOwner().getToolBar().refreshProjectListForToolBar();
		this.dialog.getOwner().getTreePanel().refreshProjectTree();
		
		this.dialog.getNextButton().setEnabled(true);
	}
	
	public void callbackByException(String message, Exception e) {
		this.callbackStopAndMessage( "[Error happend] : (" + message + ") " + e.getMessage() );

		// If exception is happendg by any possibility, all thread will be forecely stoped.
		if( !MsbEngine.getExecutorService().isShutdown() )	MsbEngine.getExecutorService().shutdownNow();

		// TODO Auto-generated catch block
		JOptionPane pane = new JOptionPane( message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION );
		javax.swing.JDialog dialog = pane.createDialog( this.dialog, message );
//		dialog.setModal(true);
		dialog.setVisible(true);

////	Interrupt current thread
//		if( !Thread.currentThread().isInterrupted() )	Thread.currentThread().interrupt();
	}
	
	public void rollback( int status ) {
		if( status == JMsbSysConst.STS_DONE_CHOOSE_ORGANISM ) {
			this.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE_CHOOSE_ORGANISM );
			this.getProjectMapItem().getTotalModelMap().clear();
			this.getProjectMapItem().getModelMap().clear();
		}else if( status == JMsbSysConst.STS_DONE_INDEX_FILES ) {
			this.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE_CHOOSE_ORGANISM );
			this.getProjectMapItem().getTotalModelMap().clear();
			this.getProjectMapItem().getModelMap().clear();
		}else if( status == JMsbSysConst.STS_DONE_READ_FILES ) {
			this.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE_READ_FILES );
			this.getProjectMapItem().getTotalModelMap().clear();
			this.getProjectMapItem().getModelMap().clear();
		}else if( status == JMsbSysConst.STS_DONE_MAKE_MODELS ) {
			this.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE_MAKE_MODELS );
		}
		
		try {
			MsbEngine.logger.debug("Store ProjectMapItems");
			ProjectManager.storeProjectMapItem( this.getProjectMapItem() );
			MsbEngine.logger.debug("Completed ProjectMapItems storing");
		} catch (Exception ie) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error("Error ", ie);
		}
	}
}
