package kobic.msb.swing.comparator;

import java.util.Comparator;

import kobic.msb.common.SwingConst.Sorts;

public class MatureRnaReadCountComparator implements Comparator<Object> {
	private Sorts direction;

	public MatureRnaReadCountComparator(Sorts direction) {
		this.direction = direction;
	}

	@Override
	public int compare( Object s1, Object s2 ) {
		if( this.direction == Sorts.SMALLEST_TO_LARGEST ) {
	    	if( s1 instanceof Number && s2 instanceof Number ) {
	    		Double a = (Double)s1;
	    		Double b = (Double)s2;
	    		return a.compareTo( b ); 
	    	}else {
	    		String[] strings1 = s1.toString().split("\\s");
		        String[] strings2 = s2.toString().split("\\s");
		        return strings1[strings1.length - 1].compareTo(strings2[strings2.length - 1]);
	    	}
		}else {
			if( s1 instanceof Number && s2 instanceof Number ) {
	    		Double a = (Double)s1;
	    		Double b = (Double)s2;
	    		return b.compareTo( a ); 
	    	}else {
	    		String[] strings1 = s1.toString().split("\\s");
		        String[] strings2 = s2.toString().split("\\s");
		        return strings2[strings2.length - 1].compareTo(strings1[strings1.length - 1]);
	    	}
		}
    }
}
