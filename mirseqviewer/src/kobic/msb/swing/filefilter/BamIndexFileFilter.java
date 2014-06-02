package kobic.msb.swing.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import kobic.com.util.Utilities;

public class BamIndexFileFilter extends FileFilter{
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
            return true;
        }
 
        String extension = Utilities.getExtension(f);
        if (extension != null) {
            if (extension.toUpperCase().equals("BAI") ) {
                    return true;
            } else {
                return false;
            }
        }
 
        return false;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Just BAM Index (BAI) format";
	}
}
