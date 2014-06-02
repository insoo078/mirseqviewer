package kobic.msb.swing.listener.menu;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import kobic.msb.swing.filefilter.MirSeqBrowserFileFilter;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JMsbProgressDialog;
import kobic.msb.swing.listener.projecttreepanel.ProjectActionListener;
import kobic.msb.swing.thread.caller.JMsbProjectImportCaller;
import kobic.msb.system.engine.MsbEngine;

public class ImportProjectActionListener extends ProjectActionListener {
	
	public ImportProjectActionListener(JMsbBrowserMainFrame frame) {
		super(frame);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Import the miRseq project");
		fc.setFileFilter( new MirSeqBrowserFileFilter() );
		int returnVal = fc.showOpenDialog( this.getFrame() );

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			try {
        		JMsbProgressDialog dialog = new JMsbProgressDialog(this.getFrame(), "Progress...", Dialog.ModalityType.MODELESS, "Importing..." );
        		JMsbProjectImportCaller caller = new JMsbProjectImportCaller( this.getFrame(), dialog, file );
        		dialog.attachThread( caller );
        		caller.run();
			}catch(Exception exp) {
//				exp.printStackTrace();
				MsbEngine.logger.error( "error : ", exp );
			}
		}
	}
}
