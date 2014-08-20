package kobic.msb.swing.filefilter;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.filechooser.FileFilter;

public class GffFilenameFilter implements FilenameFilter{
	private String ext;
	
	public GffFilenameFilter( String ext ) {
		this.ext = ext;
	}
//	@Override
//	public boolean accept(File f) {
//		if( f.isDirectory() )
//			return true;
//		return f.isFile() && f.getName().toLowerCase().endsWith("." + this.ext);
//	}
//
//	public String getDescription() {
//		return "*." + this.ext;
//	}
	@Override
	public boolean accept(File dir, String name) {
		// TODO Auto-generated method stub
		
		return name.toLowerCase().endsWith("." + this.ext);
	}
}
