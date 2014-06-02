package kobic.msb.swing.thread.caller;

import java.io.File;
import java.util.concurrent.Future;

import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JMsbProgressDialog;
import kobic.msb.swing.thread.callable.JMsbExportProjectCallable;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class JMsbProjectExportCaller extends AbstractImExportCaller{
	private File file;
	private ProjectMapItem item;
	private Future<Void> 	future;

	public JMsbProjectExportCaller(JMsbBrowserMainFrame frame, JMsbProgressDialog dialog, ProjectMapItem item, File file) {
		super(frame, dialog);
		// TODO Auto-generated constructor stub
		this.file = file;
		this.item = item;
	}

	@Override
	public void run() {
		JMsbExportProjectCallable work = new JMsbExportProjectCallable( this.getDialog(), this.item, this.file );
		work.patchCaller( this );
		this.future = MsbEngine.getExecutorService().submit( work );
	}
	
	public void callback() {
		if( this.getDialog().isIndeterminate() )	this.getDialog().setIndeterminate(false);

		this.getDialog().dispose();
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return this.future.isDone();
	}
}
