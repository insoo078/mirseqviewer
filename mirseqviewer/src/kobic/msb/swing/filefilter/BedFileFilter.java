package kobic.msb.swing.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class BedFileFilter extends FileFilter{
	@Override
	public boolean accept(File f) {
		if( f.isDirectory() )
			return true;
		return f.isFile() && f.getName().toLowerCase().endsWith(".bed");
	}

	public String getDescription() {
		return "*.bed";
	}
}
