package kobic.msb.swing.listener.projecttreepanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import kobic.msb.swing.frame.JMsbBrowserMainFrame;

public class ProjectActionListener implements ActionListener {
	private JMsbBrowserMainFrame	frame;
	private String					projectName;

	public ProjectActionListener( JMsbBrowserMainFrame frame ) {
		this( frame, null );
	}
	
	public ProjectActionListener( JMsbBrowserMainFrame frame, String projectName ) {
		this.frame = frame;
		this.projectName = projectName;
	}
	
	public JMsbBrowserMainFrame getFrame() {
		return this.frame;
	}

	public String getProjectName() {
		return this.projectName;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	}
}