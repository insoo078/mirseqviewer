package kobic.msb.swing.comparator;

import java.util.Comparator;

public class MiRnaCountComparator  implements Comparator<Object[]> {
	 @Override
	 public int compare(Object[] o1, Object[] o2) {
		 if( o1[2] instanceof Number && o2[2] instanceof Number ) {
			 double o1Value = Double.valueOf( o1[2].toString() );
			 double o2Value = Double.valueOf( o2[2].toString() );
	
			 if( o1Value < o2Value )
				 return 1;
			 else if( o1Value == o2Value )
				 return 0;
			 return -1;
		 }else {
			 System.out.println( o1[2] + " " + o2[2] );
		 }

		 return 0;
	 }
}
