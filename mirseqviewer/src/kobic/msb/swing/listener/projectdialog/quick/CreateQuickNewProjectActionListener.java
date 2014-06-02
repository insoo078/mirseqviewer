package kobic.msb.swing.listener.projectdialog.quick;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

import kobic.msb.common.util.SwingUtilities;
import kobic.msb.swing.frame.dialog.JMsbQuickNewProjectDialog;
import kobic.msb.swing.listener.projectdialog.CreateProjectListener;
import kobic.msb.swing.panel.newproject.quick.JMsbQuickNewProjectPanel;
import kobic.msb.swing.thread.caller.JMsbQuickNewProjectCaller;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class CreateQuickNewProjectActionListener  implements ActionListener {
	private JMsbQuickNewProjectPanel remote;
	
	public CreateQuickNewProjectActionListener( JMsbQuickNewProjectPanel frame ) {
		this.remote = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		SwingUtilities.setWaitCursorFor( remote.getOwnerDialog() );

		String projectName 			= remote.getProjectName();

		String organismId			= remote.getOrganismInfo();

		String miRBaseVersion		= Collections.max( MsbEngine.engine.getMiRBaseMap().keySet() );

		try {
	/*
	* 1. Create empty miRseq Project
	*/
//			mngProjectObj.createProject( remote.getMsb() );

			if( CreateProjectListener.createStep1( remote.getMsb(), projectName, remote, remote.getOwnerDialog().isEditDialog() ) ) {
	/*
	* 2. Set organims and mirBase version to the Project
	*/
				ProjectMapItem item = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );
				CreateProjectListener.createStep2( item, organismId, miRBaseVersion );

	/*
	* 3. Read Bam files by thread
	*/
				JMsbQuickNewProjectDialog dialog = (JMsbQuickNewProjectDialog) remote.getOwnerDialog();
				JMsbQuickNewProjectCaller caller = new JMsbQuickNewProjectCaller( dialog, item );
				caller.run();
			}
		}catch(Exception ixe) {
			MsbEngine.logger.error("Error", ixe);
		}
		
//		remote.setIndeterminate( false );
	}
}
