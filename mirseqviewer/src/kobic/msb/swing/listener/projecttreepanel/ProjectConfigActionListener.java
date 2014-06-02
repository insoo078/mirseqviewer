package kobic.msb.swing.listener.projecttreepanel;

import java.awt.Dialog;
import java.awt.event.ActionEvent;

import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JDrawingConfigurationDialog;

public class ProjectConfigActionListener extends ProjectActionListener {
	public ProjectConfigActionListener( JMsbBrowserMainFrame frame ) {
		this( frame, null );
	}
	
	public ProjectConfigActionListener( JMsbBrowserMainFrame frame, String projectName ) {
		super( frame, projectName );
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if( this.getProjectName() == null || this.getProjectName().isEmpty() )	return;

		AbstractDockingWindowObj dwoWindow = this.getFrame().getAbstractDockingWindowObj(this.getProjectName());

		JDrawingConfigurationDialog dialog = new JDrawingConfigurationDialog( this.getFrame(), dwoWindow, this.getProjectName(), "Configuration", Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setVisible(true);
	}
}
