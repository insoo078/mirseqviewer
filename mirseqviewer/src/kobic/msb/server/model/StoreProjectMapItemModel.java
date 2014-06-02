package kobic.msb.server.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
//import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import com.esotericsoftware.kryo.Kryo;
//import com.esotericsoftware.kryo.io.Output;

import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class StoreProjectMapItemModel implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ProjectMapItem		projectMapItem;
	private Map<String, Model>	modelMap;
	
//	private static Kryo kryo = new Kryo();
	
	public StoreProjectMapItemModel() {
		this.projectMapItem = null;
		this.modelMap = null;
	}

	public StoreProjectMapItemModel( ProjectMapItem projectMapItem ) {
		this.projectMapItem = projectMapItem;
		this.modelMap = new HashMap<String, Model>();
		
		Iterator<String> keyIter = this.projectMapItem.getModelMap().keySet().iterator();
		while(keyIter.hasNext() ) {
			String key = keyIter.next();

			this.modelMap.put(key, this.projectMapItem.getProjectModel( key ) );
		}
	}
	
	public ProjectMapItem getProjectMapItem() {
		return this.projectMapItem;
	}
	
	public Map<String, Model> getModelMap() {
		return this.modelMap;
	}
	
	public Model getModel(String mirid) {
		if( this.modelMap != null ) {
			return this.modelMap.get(mirid);
		}
		return null;
	}
	
	public void write(File file) {
		FileOutputStream fout;
		try {
			fout = new FileOutputStream( file );

			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fout);

			ObjectOutputStream oos = new ObjectOutputStream( bufferedOutputStream );
			oos.writeObject( this );
			oos.close();
			fout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( "error", e );
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( "error", e );
		}
	}
	
//	public void write(File file) {
//		FileOutputStream fout;
//		try {
//			kryo.register( StoreProjectMapItemModel.class );
//			RandomAccessFile raf = new RandomAccessFile( file, "rw");
//
//			fout = new FileOutputStream(raf.getFD());
//			
//			Output output = new Output( fout, 4096 );
//			kryo.writeObject(output, this );
//			
//			output.flush();
//			output.close();
//			raf.close();
//			fout.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//	//		e.printStackTrace();
//			MsbEngine.logger.error( "error", e );
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//	//		e.printStackTrace();
//			MsbEngine.logger.error( "error", e );
//		}
//	}
}
