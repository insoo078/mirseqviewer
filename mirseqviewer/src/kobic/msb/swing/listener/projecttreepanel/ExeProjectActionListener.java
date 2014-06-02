package kobic.msb.swing.listener.projecttreepanel;

import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import kobic.msb.system.project.ProjectManager;

public class ExeProjectActionListener extends ProjectActionListener {
	private JMsbBrowserMainFrame frame;

	public ExeProjectActionListener(JMsbBrowserMainFrame frame) {
		this(frame, null);
	}

	public ExeProjectActionListener(JMsbBrowserMainFrame frame, String projectName) {
		super(frame, projectName);
		// TODO Auto-generated constructor stub
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			SwingUtilities.setWaitCursorFor( this.getFrame() );
			// TODO Auto-generated method stub
			String projectName = this.getProjectName();
			
			if( projectName == null )	projectName = frame.getToolBar().getSelectedProject();
	
			if( projectName != null ) {
				ProjectManager mngProject = MsbEngine.getInstance().getProjectManager();				
				ProjectMapItem projectItem = mngProject.getProjectMap().getProject( projectName );

//				ExeProjectActionListener.execute( this.frame, projectName );
				ExeProjectActionListener.execute( this.frame, projectItem );
				
				SwingUtilities.setDefaultCursorFor( this.getFrame() );
			}
		}catch(Exception ex) {
//			ex.printStackTrace();
			MsbEngine.logger.error( "error : ", ex );
		}
	}
//	
//	public static void executeAtNewProjectDialog( JMsbBrowserMainFrame frame, String projectName ) throws Exception{
//		ProjectManager mngProject = MsbEngine.getInstance().getProjectManager();
//		int status = mngProject.getProjectMap().getProject( projectName ).getProjectStatus();
//		
//		ProjectMapItem projectItem = mngProject.getProjectMap().getProject( projectName );
//		projectItem.getProjectConfiguration().reloadConfiguration();
//
//		if( status == JMsbSysConst.DONE ) {
//			frame.addProjectViewToTabWindow( projectName, JMsbSysConst.ALIGNMENT_VIEW );
//			
//			if( frame.getAbstractDockingWindowObj(projectName) instanceof AlignmentDockingWindowObj ) {
//				AlignmentDockingWindowObj adwo = (AlignmentDockingWindowObj)frame.getAbstractDockingWindowObj(projectName);
//	
//				frame.getToolBar().updateFilters( adwo.getCurrentModel().getMsbFilterModel() );
//			}
//		}
//	}

//	public static void execute( final JMsbBrowserMainFrame frame, String projectName ) throws Exception{
	public static void execute( final JMsbBrowserMainFrame frame, final ProjectMapItem projectItem ) throws Exception{
//		ProjectManager mngProject = MsbEngine.getInstance().getProjectManager();
//		int status = mngProject.getProjectMap().getProject( projectName ).getProjectStatus();
//		
//		final ProjectMapItem projectItem = mngProject.getProjectMap().getProject( projectName );
//		projectItem.getProjectConfiguration().reloadConfiguration();
		int status = projectItem.getProjectStatus();
		String projectName = projectItem.getProjectName();

		if( status < JMsbSysConst.STS_DONE ) {
//			JProjectDialog dialog = new JProjectDialog( frame, "Edit Project", true, Dialog.ModalityType.APPLICATION_MODAL, true);
//			dialog.updateCurrentState( projectItem );
//			dialog.setVisible(true);

			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						JProjectDialog dialog = new JProjectDialog(frame, "Edit Project", true, Dialog.ModalityType.APPLICATION_MODAL, projectItem, true);
						dialog.setVisible(true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						MsbEngine.logger.error("Error ", e);
					}
				}
			});
		}else {
			frame.addProjectViewToTabWindow( projectName, JMsbSysConst.ALIGNMENT_VIEW );
			
			if( frame.getAbstractDockingWindowObj(projectName) instanceof AlignmentDockingWindowObj ) {
				AlignmentDockingWindowObj adwo = (AlignmentDockingWindowObj)frame.getAbstractDockingWindowObj(projectName);

				frame.getToolBar().updateFilters( adwo.getCurrentModel().getProjectMapItem().getMsbFilterModel() );
			}
		}
	}
}
