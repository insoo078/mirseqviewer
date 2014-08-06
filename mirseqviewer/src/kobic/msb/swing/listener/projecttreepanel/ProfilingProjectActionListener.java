package kobic.msb.swing.listener.projecttreepanel;

import java.awt.event.ActionEvent;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JMsbBamFileInfoWithProfileDialog;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import kobic.msb.system.project.ProjectManager;

public class ProfilingProjectActionListener extends ProjectActionListener {

	public ProfilingProjectActionListener(JMsbBrowserMainFrame frame) {
		super(frame, null);
		// TODO Auto-generated constructor stub
	}

	public ProfilingProjectActionListener(JMsbBrowserMainFrame frame, String projectName) {
		super(frame, projectName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			String projectName = this.getProjectName();
			
			if( projectName == null )	projectName = this.getFrame().getToolBar().getSelectedProject();
	
			if( projectName != null ) {
				ProjectManager mngProject = MsbEngine.getInstance().getProjectManager();
				
				int status = mngProject.getProjectMap().getProject( projectName ).getProjectStatus();
				
				ProjectMapItem projectItem = mngProject.getProjectMap().getProject( projectName );
				projectItem.getProjectConfiguration().reloadConfiguration();
		
				if( status == JMsbSysConst.STS_DONE ) {
					JMsbBamFileInfoWithProfileDialog dialog = new JMsbBamFileInfoWithProfileDialog( this.getFrame(), "Expression Profile", projectItem.getClusterModel(), projectItem );
					dialog.setVisible(true);
				}
			}
		}catch(Exception ex) {
			MsbEngine.logger.error("Error : ", ex);
		}
	}
}
