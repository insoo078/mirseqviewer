package kobic.msb.swing.comparator;

import java.util.Comparator;

import kobic.msb.server.model.ReadWithMatrix;
import kobic.msb.server.obj.ReadObject;

public class ReadVectorComparator implements Comparator<ReadWithMatrix> {
	public static final int PRIORITY_LEFT_SIDE	= 0;
	public static final int PRIORITY_RIGHT_SIDE	= 1;

	private String strand;
	private int direction;
	

	public ReadVectorComparator( String strand, int directionType ) {
		this.strand = strand;
		this.direction = directionType;
	}
	
	private static int leftSide(ReadObject ro1, ReadObject ro2) {
		if( ro1.getStartPosition() < ro2.getStartPosition() )
    		return -1;
    	else if( ro1.getStartPosition() == ro2.getStartPosition() ) {
    		return Integer.valueOf(ro1.getSequenceByString().length()).compareTo( Integer.valueOf( ro2.getSequenceByString().length() ) );
    	}
    	return 1;
	}
	
	private static int rightSide(ReadObject ro1, ReadObject ro2) {
		long endPosA = ro1.getEndPosition();
		long endPosB = ro2.getEndPosition();

    	if( endPosA > endPosB )
    		return -1;
    	else if( endPosA == endPosB ) {
    		return Integer.valueOf(ro1.getSequenceByString().length()).compareTo( Integer.valueOf( ro2.getSequenceByString().length() ) );
    	}
    	return 1;
	}

    @Override
    public int compare(ReadWithMatrix o1, ReadWithMatrix o2) {
    	ReadObject ro1 = o1.getReadObject();
    	ReadObject ro2 = o2.getReadObject();

    	if( this.strand.equals("+") ) {
	    	if( this.direction == ReadVectorComparator.PRIORITY_LEFT_SIDE )	return ReadVectorComparator.leftSide(ro1, ro2);
	    	else												        	return ReadVectorComparator.rightSide(ro1, ro2);
    	}else {
	    	if( this.direction == ReadVectorComparator.PRIORITY_LEFT_SIDE )	return ReadVectorComparator.rightSide(ro1, ro2);
	    	else												    		return ReadVectorComparator.leftSide(ro1, ro2);
    	}
    }
}
