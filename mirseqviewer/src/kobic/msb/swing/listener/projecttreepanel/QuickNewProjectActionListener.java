package kobic.msb.swing.listener.projecttreepanel;

import java.awt.Dialog;
import java.awt.event.ActionEvent;

import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JMsbQuickNewProjectDialog;
import kobic.msb.system.engine.MsbEngine;

public class QuickNewProjectActionListener extends ProjectActionListener {
	public QuickNewProjectActionListener( JMsbBrowserMainFrame frame ) {
		this( frame, null );
	}
	
	public QuickNewProjectActionListener( JMsbBrowserMainFrame frame, String projectName ) {
		super( frame, projectName );
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			// TODO Auto-generated method stub
			JMsbQuickNewProjectDialog dialog = new JMsbQuickNewProjectDialog( this.getFrame(), "Quick New Project", false, Dialog.ModalityType.APPLICATION_MODAL );

			dialog.setVisible(true);
		}catch(Exception ex) {
//			ex.printStackTrace();
			MsbEngine.logger.error( "error : ", ex );
		}
	}
}
