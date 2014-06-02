package kobic.msb.swing.thread.callable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
//import java.io.RandomAccessFile;
import java.util.concurrent.Callable;

//import com.esotericsoftware.kryo.Kryo;
//import com.esotericsoftware.kryo.io.Input;

import kobic.msb.server.model.StoreProjectMapItemModel;
import kobic.msb.swing.frame.dialog.JMsbProgressDialog;
import kobic.msb.swing.thread.caller.JMsbProjectImportCaller;
import kobic.msb.system.engine.MsbEngine;

public class JMsbReadMsbFileCallable implements Callable<Void>{
	private File						file;
	private JMsbProgressDialog			dialog;
	private JMsbProjectImportCaller		caller;
	
//	private static Kryo kryo = new Kryo();

	public JMsbReadMsbFileCallable( JMsbProgressDialog dialog, File file ) {	
		this.file	= file;
		this.dialog = dialog;
		this.dialog.setLabelValue("Reading... msv file");
	}

	@Override
	public Void call() {
		try {
			this.dialog.setIndeterminate(true);
	    	FileInputStream fis = new FileInputStream( file );
	    	BufferedInputStream bufferedInputStream = new BufferedInputStream(fis);
	    	ObjectInputStream ois = new ObjectInputStream( bufferedInputStream );
	    	StoreProjectMapItemModel spmim = (StoreProjectMapItemModel)ois.readObject();
	        ois.close();
	        fis.close();

//			RandomAccessFile raf = new RandomAccessFile( this.file, "rw");
//
//			FileInputStream fis = new FileInputStream( raf.getFD() );
//	    	
//	        Input input = new Input( fis, 4096 );
//	        
//	        StoreProjectMapItemModel spmim = (StoreProjectMapItemModel)kryo.readObject(input, StoreProjectMapItemModel.class);
//
//	        input.close();
//	        raf.close();
//	        fis.close();
	        
	        this.dialog.setIndeterminate(false);
	        
	        this.caller.callback(spmim);
		}catch(Exception e) {
			MsbEngine.logger.error("Error", e);
		}
		return null;
	}
	
	public void patchCaller(JMsbProjectImportCaller caller) {
		this.caller = caller;
	}
}
