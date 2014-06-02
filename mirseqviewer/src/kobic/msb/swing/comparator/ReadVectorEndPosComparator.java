package kobic.msb.swing.comparator;

import java.util.Comparator;

import kobic.msb.server.model.ReadWithMatrix;
import kobic.msb.server.obj.ReadObject;

public class ReadVectorEndPosComparator implements Comparator<ReadWithMatrix> {
    @Override
    public int compare(ReadWithMatrix o1, ReadWithMatrix o2) {
    	ReadObject ro1 = o1.getReadObject();
    	ReadObject ro2 = o2.getReadObject();

    	long endPosA = ro1.getStartPosition() + ro1.getSequenceByString().length();
    	long endPosB = ro2.getStartPosition() + ro2.getSequenceByString().length();

    	if( endPosA > endPosB )
    		return -1;
    	else if( endPosA == endPosB ) {
    		return Integer.valueOf(ro1.getSequenceByString().length()).compareTo( Integer.valueOf( ro2.getSequenceByString().length() ) );
    	}
    	return 1;
    }
}
