package kobic.msb.swing.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CSVFileFilter extends FileFilter{
	@Override
	public boolean accept(File f) {
		if( f.isDirectory() )
			return true;
		return f.isFile() && f.getName().toLowerCase().endsWith(".csv");
	}

	public String getDescription() {
		return "*.csv";
	}
}
