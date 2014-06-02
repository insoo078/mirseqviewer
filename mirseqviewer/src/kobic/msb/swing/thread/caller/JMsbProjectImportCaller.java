package kobic.msb.swing.thread.caller;

import java.io.File;
import java.util.concurrent.Future;

import kobic.com.util.Utilities;
import kobic.msb.server.model.StoreProjectMapItemModel;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JMsbProgressDialog;
import kobic.msb.swing.thread.callable.JMsbReadMsbFileCallable;
import kobic.msb.swing.thread.callable.JMsbWriteModelFromMsbFileCallable;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class JMsbProjectImportCaller extends AbstractImExportCaller{
	private File			file;
	private Future<Void> 	future1;
	private Future<Void> 	future2;
	
	public JMsbProjectImportCaller( JMsbBrowserMainFrame frame, JMsbProgressDialog dialog, File file ) {
		super(frame, dialog);

		this.file	= file;
	}

	@Override
	public void run() {
		JMsbReadMsbFileCallable work1 = new JMsbReadMsbFileCallable( this.getDialog(), this.file );
		work1.patchCaller( this );
		this.future1 = MsbEngine.getExecutorService().submit( work1 );
	}

	public void callback( StoreProjectMapItemModel spmim ) {
		JMsbWriteModelFromMsbFileCallable work2 = new JMsbWriteModelFromMsbFileCallable( this.getFrame(), this.getDialog(), spmim );
		work2.patchCaller( this );

		this.future2 = MsbEngine.getExecutorService().submit( work2 );
	}
	
	public void callback( ProjectMapItem item ) {
		try {
	    	if( item != null && !Utilities.nulltoEmpty( item.getProjectName() ).isEmpty() ) {
				this.getFrame().getToolBar().refreshProjectListForToolBar();
				this.getFrame().getTreePanel().refreshProjectTree();
	    	}
    	}catch(Exception e) {
    		MsbEngine.logger.error("Error", e);
    	}

		this.getDialog().dispose();
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		boolean result = true;
		if( this.future1 != null )	result &= this.future1.isDone();
		if( this.future2 != null )	result &= this.future2.isDone();
		
		return result;
	}
}
