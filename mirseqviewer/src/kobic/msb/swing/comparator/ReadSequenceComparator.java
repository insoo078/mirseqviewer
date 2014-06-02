package kobic.msb.swing.comparator;

import java.util.Comparator;

import kobic.msb.server.obj.ReadObject;

public class ReadSequenceComparator implements Comparator<ReadObject> {
    @Override
    public int compare(ReadObject o1, ReadObject o2) {
    	if( o1.getStartPosition() < o2.getStartPosition() )
    		return -1;
    	else if( o1.getStartPosition() == o2.getStartPosition() ) {
    		if( o1.getSequenceByString().length() > o2.getSequenceByString().length() )
    			return -1;
    		else
    			return 1;
    	}
    	return 1;
    }

}
