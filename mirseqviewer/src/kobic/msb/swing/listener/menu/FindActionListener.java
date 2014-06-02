package kobic.msb.swing.listener.menu;

import java.awt.Dialog;
import java.awt.event.ActionEvent;

import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JMsbFindReadDailog;
import kobic.msb.swing.listener.projecttreepanel.ProjectActionListener;

public class FindActionListener extends ProjectActionListener {
	public FindActionListener(JMsbBrowserMainFrame frame) {
		super(frame);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String projectName = this.getFrame().getToolBar().getSelectedProject();
//		AbstractDockingWindowObj dockWindow = this.getFrame().getDockingWindowObjMap().get( projectName );
		AbstractDockingWindowObj dockWindow = this.getFrame().getAbstractDockingWindowObj( projectName );

		if( dockWindow != null ){ 
			JMsbFindReadDailog dialog = new JMsbFindReadDailog( dockWindow, "Find", Dialog.ModalityType.APPLICATION_MODAL);
			dialog.setVisible(true);
		}
	}
}
