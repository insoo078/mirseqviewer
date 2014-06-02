package kobic.msb.system.catalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitorInputStream;

import kobic.msb.io.MsbProgressMonitorInputStream;
import kobic.msb.swing.frame.splash.SplashScreen;
import kobic.msb.system.engine.MsbEngine;

public class ProjectMap extends Observable{
	private Map<String, ProjectMapItem> _projectMap;

	public ProjectMap() {
		this._projectMap = new HashMap<String, ProjectMapItem>();
	}

	public List<String> getProjectNameList() {
		List<String> list = new ArrayList<String>( this._projectMap.keySet() );
		Collections.sort( list );
		return list;
	}

	public int remove( String key ) {
		try {
			this._projectMap.remove( key );
			return 1;
		}catch(Exception e) {
			return 0;
		}
	}

	public void putProject( String key, ProjectMapItem item ) {
		this._projectMap.put(key, item);
	}

	public ProjectMapItem getProject( String key ) {
		return this._projectMap.get( key );
	}
	
	public synchronized void writeToFile( String filePath ) throws FileNotFoundException, IOException, Exception{
		File file = new File( filePath );
		if( new File(file.getParentFile().getAbsolutePath()).exists() == false )	{
			JOptionPane.showMessageDialog( null, "We can not write project to file. You should make sure the workspace.", "Workspace problem", JOptionPane.ERROR_MESSAGE );
			throw new RuntimeException("File problem");
		}else {
			FileOutputStream fout = new FileOutputStream( file );
	
			ObjectOutputStream oos = new ObjectOutputStream( fout );
			oos.writeObject( this._projectMap );
			oos.flush();
			oos.close();
			fout.close();
		}
	}

//	public synchronized void writeToFile( String filePath ) throws FileNotFoundException, IOException, Exception{
//		File file = new File( filePath );
//		if( new File(file.getParentFile().getAbsolutePath()).exists() == false )	{
//			JOptionPane.showMessageDialog( null, "We can not write project to file. You should make sure the workspace.", "Workspace problem", JOptionPane.ERROR_MESSAGE );
//			throw new RuntimeException("File problem");
//		}
//		else {
//			FileOutputStream fout;
//			try {
//				fout = new FileOutputStream( file );
//	
//				ObjectOutputStream oos = new ObjectOutputStream( fout );
//				oos.writeObject( this._projectMap );
//				oos.flush();
//				oos.close();
//				fout.close();
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
////				e.printStackTrace();
//				MsbEngine.logger.error( "Exception", e);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
////				e.printStackTrace();
//				MsbEngine.logger.error( "Exception", e);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
////				e.printStackTrace();
//				MsbEngine.logger.error( "error : ", e );
//			}
//		}
//	}
	
	@SuppressWarnings("unchecked")
	public synchronized void loadProjectListFromObjectFile( SplashScreen scr, String filePath ) {
		try {
			if( new File(filePath).exists() ) {
		        FileInputStream fis = new FileInputStream( filePath );
		        ObjectInputStream ois = null;
		        InputStream pmis = null;
	
		        if( scr != null )	pmis = new MsbProgressMonitorInputStream( null, "Reading... Project workspace files", fis, scr.getProgressBar() );
		        else				pmis = new ProgressMonitorInputStream( null, "Reading... Project workspace files", fis );
		        
	        	ois = new ObjectInputStream( pmis );
	
	        	this._projectMap = (Map<String, ProjectMapItem>)ois.readObject();
	
		        ois.close();
		        pmis.close();
		        fis.close();
			}
		}catch(java.io.NotSerializableException e) {
//			e.printStackTrace();
			MsbEngine.logger.error( "Exception", e);
			this.deleteBrokenFile( filePath );
		}catch(java.io.WriteAbortedException wre) {
//			wre.printStackTrace();
			MsbEngine.logger.error( "Exception", wre);
			this.deleteBrokenFile( filePath );
		}catch(java.io.InvalidClassException ice ) {
//			ice.printStackTrace();
			MsbEngine.logger.error( "Exception", ice);
			this.deleteBrokenFile( filePath );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( "Exception", e);
			this.deleteBrokenFile( filePath );
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( "Exception", e);
			this.deleteBrokenFile( filePath );
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( "Exception", e);
			this.deleteBrokenFile( filePath );
		}catch ( Exception e) {
//			e.printStackTrace();
			MsbEngine.logger.error( "Exception", e);
			this.deleteBrokenFile( filePath );
		}
	}

	public synchronized void loadProjectListFromObjectFile( String filePath ) {
		this.loadProjectListFromObjectFile(null, filePath);
	}
	
	private void deleteBrokenFile( String filename ) {
		File file = new File( filename );
		file.delete();
		System.out.println( filename + " file is deleted" );
	}
	
	public boolean isExistProjectName( String key ) {
		return this._projectMap.containsKey(key);
	}
}