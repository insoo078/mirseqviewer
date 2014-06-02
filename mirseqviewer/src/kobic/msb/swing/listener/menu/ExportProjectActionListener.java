package kobic.msb.swing.listener.menu;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import kobic.msb.server.model.StoreProjectMapItemModel;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.filefilter.MirSeqBrowserFileFilter;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JMsbProgressDialog;
import kobic.msb.swing.listener.projecttreepanel.ProjectActionListener;
import kobic.msb.swing.thread.caller.JMsbProjectExportCaller;
import kobic.msb.swing.thread.caller.JMsbProjectImportCaller;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class ExportProjectActionListener extends ProjectActionListener {
	public ExportProjectActionListener(JMsbBrowserMainFrame frame) {
		super(frame);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String projectName = this.getFrame().getTabWindow().getSelectedWindow().getTitle();

		AbstractDockingWindowObj obj = this.getFrame().getAbstractDockingWindowObj( projectName );

		if( obj != null ) { 
			final JFileChooser fc = new JFileChooser();
			fc.setAcceptAllFileFilterUsed( false );
	//		fc.setApproveButtonText("Save");
			fc.setDialogTitle("Export the miRseq project");
	//		fc.setDialogType( JFileChooser.SAVE_DIALOG );
			fc.setFileFilter( new MirSeqBrowserFileFilter() );
			fc.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );

			int returnVal = fc.showSaveDialog( this.getFrame() );
	
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = new File(fc.getSelectedFile().getAbsolutePath() + ".msv");

				if( projectName == null || projectName.isEmpty() || obj == null ) {
					JOptionPane.showMessageDialog( this.getFrame(), "You did not choose a project", "Error", JOptionPane.ERROR_MESSAGE );
				}else {
					ProjectMapItem mapItem = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );

					JMsbProgressDialog dialog = new JMsbProgressDialog(this.getFrame(), "Progress...", Dialog.ModalityType.MODELESS, "Exporting..." );
					JMsbProjectExportCaller caller = new JMsbProjectExportCaller( this.getFrame(), dialog, mapItem, file );
					dialog.attachThread( caller );
	        		caller.run();
				}
			}
		}else {
			JOptionPane.showMessageDialog( this.getFrame(), "There is not a instance of project!!", "Error", JOptionPane.ERROR_MESSAGE );
		}
	}
}
