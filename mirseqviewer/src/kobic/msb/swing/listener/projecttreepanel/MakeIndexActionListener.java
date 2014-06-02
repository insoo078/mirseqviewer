package kobic.msb.swing.listener.projecttreepanel;

import java.awt.event.ActionEvent;

import kobic.msb.swing.frame.JMsbBrowserMainFrame;

public class MakeIndexActionListener extends ProjectActionListener {

	public MakeIndexActionListener(JMsbBrowserMainFrame frame) {
		this(frame, null);
		// TODO Auto-generated constructor stub
	}
	
	public MakeIndexActionListener(JMsbBrowserMainFrame frame, String projectName) {
		super(frame, projectName);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
//		JBamIndexManagerDialog dialog = new JBamIndexManagerDialog( this.getFrame(), this.getFrame().getMsbEngine() );
//		dialog.setProjectName( this.getProjectName() );
//		dialog.setVisible( true );
	}
}
