package kobic.msb.swing.thread.callable;

import java.io.File;
import java.util.concurrent.Callable;

import kobic.msb.server.model.StoreProjectMapItemModel;
import kobic.msb.swing.frame.dialog.JMsbProgressDialog;
import kobic.msb.swing.thread.caller.AbstractImExportCaller;
import kobic.msb.swing.thread.caller.JMsbProjectExportCaller;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class JMsbExportProjectCallable implements Callable<Void>{
	private File						file;
	private JMsbProgressDialog			dialog;
	private ProjectMapItem				item;
	private JMsbProjectExportCaller		caller;

	public JMsbExportProjectCallable(JMsbProgressDialog dialog, ProjectMapItem item, File file) {
		this.dialog = dialog;
		this.file	= file;
		this.item	= item;
	}

	@Override
	public Void call() throws Exception {
		try {
			this.dialog.setIndeterminate(true);
			// TODO Auto-generated method stub
	    	this.dialog.setLabelValue( "Loading miRNA models....");
			StoreProjectMapItemModel spmim = new StoreProjectMapItemModel( this.item );
	
			this.dialog.setLabelValue( "Exporting the miRseq project....");
	    	spmim.write( this.file );
	
	    	this.dialog.setIndeterminate(false);
		}catch(Exception e) {
			MsbEngine.logger.error("Error", e);
		}
    	this.caller.callback();

		return null;
	}

	public void patchCaller(AbstractImExportCaller caller) {
		this.caller = (JMsbProjectExportCaller) caller;
	}
}
