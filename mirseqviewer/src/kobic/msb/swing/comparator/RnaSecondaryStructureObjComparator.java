package kobic.msb.swing.comparator;

import java.util.Comparator;

import kobic.msb.server.obj.RnaSecondaryStructureObj;

public class RnaSecondaryStructureObjComparator implements Comparator<RnaSecondaryStructureObj> {
	private char strand;

	public RnaSecondaryStructureObjComparator(char strand) {
		this.strand = strand;
	}
    @Override
    public int compare(RnaSecondaryStructureObj o1, RnaSecondaryStructureObj o2) {
    	if( this.strand == '+') {
	    	long ro1 = o1.getStart();
	    	long ro2 = o2.getStart();
	
	    	if( ro2 > ro1 )
	    		return -1;
	    	return 1;
    	}else {
    		long ro1 = o1.getEnd();
	    	long ro2 = o2.getEnd();
	
	    	if( ro2 > ro1 )
	    		return 1;
	    	return -1;
    	}
    }

}
