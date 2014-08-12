package kobic.msb.swing.listener.projecttreepanel;

import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class NewProjectActionListener extends ProjectActionListener {
	public NewProjectActionListener( JMsbBrowserMainFrame frame ) {
		this( frame, null );
	}
	
	public NewProjectActionListener( JMsbBrowserMainFrame frame, String projectName ) {
		super( frame, projectName );
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			final String nType = MsbEngine.getInstance().getSystemProperties().getProperty("msv.create.new.project.screen");

			// TODO Auto-generated method stub
			if( this.getProjectName() == null ) {
				final JMsbBrowserMainFrame nFrame = this.getFrame();
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							JProjectDialog dialog = new JProjectDialog( nFrame, "New Project", false, Dialog.ModalityType.APPLICATION_MODAL, false, nType );
			
							dialog.setVisible(true);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							MsbEngine.logger.error("Error ", e);
						}
					}
				});
			}else {
				String projectName = this.getProjectName();
				if( projectName == null )			{
					JOptionPane.showMessageDialog( this.getFrame(), "This is not a project");
					return;
				}else {
					final ProjectMapItem item = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );
					final JMsbBrowserMainFrame nFrame = this.getFrame();
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							try {
								JProjectDialog dialog = new JProjectDialog(nFrame, "Edit Project", true, Dialog.ModalityType.APPLICATION_MODAL, item, true, nType);
								dialog.setVisible(true);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								MsbEngine.logger.error("Error ", e);
							}
						}
					});
				}
			}
		}catch(Exception ex) {
//			ex.printStackTrace();
			MsbEngine.logger.error( "error : ", ex );
		}
	}
}
