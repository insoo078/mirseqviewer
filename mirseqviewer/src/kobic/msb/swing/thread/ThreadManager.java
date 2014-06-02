package kobic.msb.swing.thread;

import java.io.PrintStream;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import kobic.com.log.MessageConsole;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.swing.thread.caller.JMsbNGSBamIndexCaller;
import kobic.msb.swing.thread.caller.JMsbNGSTestCaller;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class ThreadManager extends Observable{
	private JProjectDialog			ownerDialog;
	
	private int						progress;

	private JMsbNGSBamIndexCaller	indexCaller;		// Excute Picard by multi-thread
	private JMsbNGSTestCaller		bamReadCaller;		// Read Sorted Bam file by multi-thread
	
	ThreadManager remote = ThreadManager.this;

	public ThreadManager( JProjectDialog ownerDialog ) {
		this.ownerDialog = ownerDialog;

		this.indexCaller	= new JMsbNGSBamIndexCaller( this.ownerDialog );
		this.bamReadCaller	= new JMsbNGSTestCaller( this.ownerDialog );
	}

	public void goSortingAndIndexing( ProjectMapItem projectMapItem ) {
		this.indexCaller.patch( this.ownerDialog.getBamFilePreProcessingPanel(), projectMapItem );
		this.indexCaller.run();
	}

	public void goLoadingBamFilesToModel( ProjectMapItem projectMapItem ) {
		this.bamReadCaller.patch( this.ownerDialog.getMirnaChoosePanel(), projectMapItem );
		this.bamReadCaller.run();
	}

	public JMsbNGSBamIndexCaller getBamIndexCaller() {
		return this.indexCaller;
	}
	
	public JMsbNGSTestCaller getBamReadCaller() {
		return this.bamReadCaller;
	}

	public void setProjectProgress(int progress){
		this.progress = progress;

		this.setChanged();
		this.notifyObservers( this.progress );
	}
}
