package kobic.msb.swing.comparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings({"rawtypes", "unused"})
public class ArrayComparator extends java.lang.Object implements Comparator {
	private List<ElementComparator>	sortCols = new ArrayList<ElementComparator>();
	private String	sortStr;
    private boolean	isFirstOrderBy=true;

    public ArrayComparator() {}

	public ArrayComparator(int sortCol, String direction) {
		this.addSortCol(sortCol, direction);
	}

	public void addSortCol(int sortCol, String direction) {
		this.addToString("col"+sortCol, direction);
		this.sortCols.add( new ElementComparator(sortCol, direction) );
	}

	private void addToString(String sortColName, String direction) {
		String comma = this.isFirstOrderBy ? "order by " : ", ";
		this.sortStr += comma + sortColName+" " + direction;
		this.isFirstOrderBy = false;
	}

	public int compare( Object o1, Object o2 ) {
		int compareVal=0;
		Iterator<ElementComparator> iter=this.sortCols.iterator();
		while (iter.hasNext()) {
			ElementComparator elementComp=(ElementComparator) iter.next();
			Object element1=((Object[]) o1)[elementComp.getSortCol()];
			Object element2=((Object[]) o2)[elementComp.getSortCol()];

			compareVal = elementComp.compare(element1,element2);
			if (compareVal!=0) // if one of the elments is not equal we know all we need to know and can return
				break;
		}

		return compareVal;
	}

	// java.util.Arrays.equals(data,copy(data));  The row pointers and data in them are the same.
	public static Object[][] copy(Object[][] data) {
		if (data==null)
			return null;
		else {
			Object[][] copyData=new Object[data.length][];
			System.arraycopy(data,0, copyData,0,data.length);
			return copyData;
		}
	}
	
	public static void sort(Object[][] array, int sortIndex, String sortDir)throws Exception {
		new ArrayComparator(sortIndex, sortDir).sort(array);
	}

	@SuppressWarnings("unchecked")
	public void sort(Object[][] array) throws Exception {
		try {
			Arrays.sort(array, this);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*** Start ElementComparator inner class ***/
	// Used to compare elements of an array that reside within the same column.  The Object[][] array itself
	// will be sorted according to this algorithm.
	private static class ElementComparator implements Comparator {
		private int					sortCol;
		private static final int 	ASC		= 0;
		private static final int 	DESC	= 1;
		private int 				dir;

		public ElementComparator(int sortCol, String direction) {
			this.sortCol=sortCol;
			if ("asc".equalsIgnoreCase(direction) || "ascending".equalsIgnoreCase(direction))
				this.dir=ASC;
			else if ("desc".equalsIgnoreCase(direction) || "descending".equalsIgnoreCase(direction))
				this.dir=DESC;
			else
				throw new IllegalArgumentException("'asc' or 'desc' must be passed to the constructor.  The argument passed was: "+direction);
		}

		private boolean isAscending()  {
			return dir==ASC;   
		}
		
		public int getSortCol() {
	        return sortCol;
	    }

		@SuppressWarnings("unchecked")
		public int compare(Object o1, Object o2) {
			Comparable comparable=null;
			Object element1=o1;
			Object element2=o2;

    // Note the following if condition ensures that nulls may be in the array.  Nulls will always
    // be placed after all other data with the following logic.
			if (element1==null && element2==null) 
				return 0;
			else if (element1==null) 
				return 1;
			else if (element2==null)
				return -1;
			else 
				comparable = (Comparable) element1;


			if (isAscending())
				return comparable.compareTo(element2);
			else
				return -comparable.compareTo(element2);
		}

	}

	public static void main(String[] args) throws Exception {
		Double[][] array = new Double[][]{
				{0d, 10d,	2d, 1d},
				{1d, 4d, 	6d, 2d},
				{2d, 1d,	1d, 4d},
				{3d, 5d,	8d, 5d},
				{4d, 9d,	3d, 6d},
				{5d, 2d,	9d, 7d},
				{6d, 8d,	10d, 8d},
				{7d, 6d,	4d, 9d},
				{8d, 1d,	5d, 10d},
				{9d, 0d,	7d, 11d},
				{10d, 5d,	8d, 12d}
		};

		ArrayComparator ac = new ArrayComparator();
		ac.addSortCol(1,"asc");
		ac.addSortCol(2,"asc");
		ac.addSortCol(3,"asc");
		ac.sort(array);  //
		
//		System.out.println("Hello");
	}
}