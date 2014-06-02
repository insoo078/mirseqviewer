package kobic.msb.swing.listener.menu;

import java.awt.event.ActionEvent;
import java.util.List;

import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.Model;
import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.listener.projecttreepanel.ProjectActionListener;
import kobic.msb.system.catalog.ProjectMap;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import kobic.msb.system.project.ProjectManager;

public class SaveProjectActionListener  extends ProjectActionListener {
	
	public SaveProjectActionListener(JMsbBrowserMainFrame frame) {
		super(frame);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			ProjectManager manage = MsbEngine.getInstance().getProjectManager();
			ProjectMap map = manage.getProjectMap();
			map.writeToFile( manage.getSystemFileToGetProjectList() );

			SwingUtilities.setWaitCursorFor( this.getFrame() );
			List<String> projectList = map.getProjectNameList();
			for( String projectName : projectList ) {
				ProjectMapItem item = map.getProject( projectName );
				Msb msb = new Msb();
				msb.setProject( item.getProjectInfo() );
				manage.writeXmlToProject( msb );

				AbstractDockingWindowObj obj = this.getFrame().getAbstractDockingWindowObj(projectName);
				if( obj instanceof AlignmentDockingWindowObj ) {
					AlignmentDockingWindowObj adwo = (AlignmentDockingWindowObj)obj;
					Model model = adwo.getCurrentModel();
					model.writeModelToFile( projectName );

					MsbEngine.logger.info("Save " + projectName + " " + adwo.getDefaultMirid() + " model " );
				}
			}
			SwingUtilities.setDefaultCursorFor( this.getFrame() );

			this.getFrame().getTreePanel().refreshProjectTree();
		}catch(Exception ex) {
//			ex.printStackTrace();
			MsbEngine.logger.error( "error : ", ex );
		}
	}
}
