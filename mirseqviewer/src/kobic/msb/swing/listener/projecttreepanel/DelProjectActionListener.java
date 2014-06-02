package kobic.msb.swing.listener.projecttreepanel;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JOptionPane;

import net.infonode.docking.TabWindow;

import kobic.com.util.Utilities;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.system.engine.MsbEngine;
import kobic.msb.system.project.ProjectManager;

public class DelProjectActionListener extends ProjectActionListener {
	private JMsbBrowserMainFrame frame;
	
	public DelProjectActionListener(JMsbBrowserMainFrame frame) {
		this(frame, null);
		// TODO Auto-generated constructor stub
	}
	
	public DelProjectActionListener(JMsbBrowserMainFrame frame, String projectName) {
		super(frame, projectName);
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String projectName = Utilities.nulltoEmpty( this.getProjectName() );
		ProjectManager mngProject = MsbEngine.getInstance().getProjectManager();

		String message = "Are you sure that you truly want to delete the project ?";
		if( projectName.equals("") )	message = "Are you want to remove all projects ?";
		
		try {
			this.doDeleteProject(mngProject, message, projectName);
		}catch(Exception ex) {
//			ex.printStackTrace();
			MsbEngine.logger.error( "error : ", ex );
		}
	}
	
	private void closeProjectView(String projectName ) {
		TabWindow tabWindow = this.frame.getTabWindow();
		for(int i=0; i<tabWindow.getChildWindowCount(); i++) {
			net.infonode.docking.View view = (net.infonode.docking.View) tabWindow.getChildWindow(i);
			String title = view.getTitle();
			if( title.equals(projectName) )	view.close();
		}
	}

	private void doDeleteProject( ProjectManager mngProject, String message, String projectName ) throws Exception{
		int confirmed = JOptionPane.showConfirmDialog( this.getFrame(), message, "User Confirmation", JOptionPane.YES_NO_OPTION );

		if ( confirmed == JOptionPane.YES_OPTION ) {
			if ( projectName.equals("") )	{
				List<String> projects = mngProject.getProjectMap().getProjectNameList();
				for(int i=0; i<projects.size(); i++) {
					String project = projects.get(i);
					
					this.closeProjectView( project );
				}
				mngProject.removeAllProject();

				this.frame.getToolBar().removeComboBoxAllItems();
				this.frame.removeAllTabbedProject();
			}else{
				mngProject.removeProject( projectName );
				this.frame.getToolBar().removeComboBoxItem( projectName );

				this.closeProjectView(projectName);

				this.frame.removeTabbedProject( projectName );
			}

			this.getFrame().getTreePanel().refreshProjectTree();
		}
	}
}