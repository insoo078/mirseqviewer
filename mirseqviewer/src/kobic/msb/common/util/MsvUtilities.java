package kobic.msb.common.util;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.sf.samtools.AbstractBAMFileIndex;
import net.sf.samtools.BAMIndexMetaData;
import net.sf.samtools.SAMFileReader;
import kobic.com.util.Utilities;
import kobic.msb.common.SwingConst;
import kobic.msb.server.model.StoreProjectMapItemModel;
import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.server.obj.GeneralReadObject;
import kobic.msb.server.obj.MsvSamRecord;
import kobic.msb.server.obj.ReadObject;
import kobic.msb.server.obj.RnaStructureObj;
import kobic.msb.server.obj.SAMInfo;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class MsvUtilities {

	public static double getMaxX(List<RnaStructureObj> nucleotides) {
		double max = -999999;
		Iterator<RnaStructureObj> iter = nucleotides.iterator();
		while( iter.hasNext() ) {
			RnaStructureObj rna = iter.next();

			if( rna.getX() > max )	max = rna.getX();
		}

		return max;
	}

	public static double getMaxY(List<RnaStructureObj> nucleotides) {
		double max = -999999;
		Iterator<RnaStructureObj> iter = nucleotides.iterator();
		while( iter.hasNext() ) {
			RnaStructureObj rna = iter.next();

			if( rna.getY() > max )	max = rna.getY();
		}

		return max;
	}


	public static double getMinX(List<RnaStructureObj> nucleotides) {
		double min = 999999;
		Iterator<RnaStructureObj> iter = nucleotides.iterator();
		while( iter.hasNext() ) {
			RnaStructureObj rna = iter.next();

			if( rna.getX() < min )	min = rna.getX();
		}

		return min;
	}

	public static double getMinY(List<RnaStructureObj> nucleotides) {
		double min = 999999;
		Iterator<RnaStructureObj> iter = nucleotides.iterator();
		while( iter.hasNext() ) {
			RnaStructureObj rna = iter.next();

			if( rna.getY() < min )	min = rna.getY();
		}

		return min;
	}

	public static Rectangle2D.Double getRect(List<RnaStructureObj> list) {
		double x		= MsvUtilities.getMinX(list);
		double y		= MsvUtilities.getMinY(list);
		double width	= MsvUtilities.getMaxX(list) - x;
		double height	= MsvUtilities.getMaxY(list) - y;

		Rectangle2D.Double rect = new Rectangle2D.Double( x, y, width, height );
		return rect;
	}

	/****************************************************************************************
	 * To check sample name exist in the list
	 * 
	 * @param groupList
	 * @param sampleName
	 * @param remote
	 * @return
	 */
	public static boolean isExistSampleName( List<Group> groupList, String sampleName ) {
		Iterator<Group> tmpIter = groupList.iterator();
		while( tmpIter.hasNext() ) {
			Group grp = tmpIter.next();

			Iterator<Sample> samIter = grp.getSample().iterator();
			while( samIter.hasNext() ) {
				Sample s = samIter.next();
				if( s.getName().equals( sampleName ) ) {
					return true;
				}
			}
		}
		return false;
	}

	/***************************************************************************************
	 * To Find Sample for editting properties or deleting
	 * 
	 * @param list
	 * @param sampleNumber
	 * @return
	 */
	public static Sample findTargetToModify( List<Group> list, String sampleNumber ) {
		Iterator<Group> iter = list.iterator();
		while( iter.hasNext() ) {										// Group looping
			Group grp = iter.next();
			Iterator<Sample> iterSample = grp.getSample().iterator();
			while( iterSample.hasNext() ) {								// Sample looping
				Sample smp = iterSample.next();
				if( smp.getOrder().equals( sampleNumber ) ) {
					Sample nSmp = smp.clone();
					grp.getSample().remove( smp );
					return nSmp;
				}
			}
		}
		return null;
	}

	/***************************************************************************************
	 * To assign sample to new group
	 * 
	 * @param sample
	 * @param groupId
	 * @param list
	 */
	public static List<Group> assignSampleToGroup(Sample sample, String groupId, List<Group> list) {
		Iterator<Group> iter = list.iterator();
		while( iter.hasNext() ) {										// Group looping
			Group grp = iter.next();
			if( grp.getGroupId().equals( groupId ) ) {
				grp.getSample().add( sample );
			}
		}
		return list;
	}

	public static long getTotalReadCountWithIndex( SAMFileReader sam ) {
	    long count = 0;

	    AbstractBAMFileIndex index = (AbstractBAMFileIndex) sam.getIndex();
	    int nRefs = index.getNumberOfReferences();
	    for (int i = 0; i < nRefs; i++) {
	        BAMIndexMetaData meta = index.getMetaData(i);
	        count += meta.getAlignedRecordCount();
	    }

	    return count;
	}

	public static long getTotalReadCount( SAMFileReader sam ) {
		long nCount=0;
        net.sf.samtools.util.CloseableIterator<net.sf.samtools.SAMRecord> iter=null;

        iter = sam.iterator();
        while( iter.hasNext() ) {
        	iter.next();
        	nCount++;
        }
        iter.close();
        return nCount;
	}

	public synchronized static File writeXmlFile( Msb msbXmlFileObject, String path ) {
		// To Create XML file for PROJECT
		File projectXmlFile = new File( path );
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance( Msb.class );

			Marshaller jaxMarshaller = jaxbContext.createMarshaller();
			jaxMarshaller.marshal( msbXmlFileObject, projectXmlFile );

			return projectXmlFile;
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
			MsbEngine.logger.error( "error", e1 );
			throw new RuntimeException( e1.toString() );
		}
	}

	public synchronized static File writeXmlFile( ProjectMapItem projectMapItem, String path ) {
		Msb msbXmlFileObject = new Msb();
		msbXmlFileObject.setProject( projectMapItem.getProjectInfo() );
		// To Create XML file for PROJECT
		File projectXmlFile = new File( path );
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance( Msb.class );

			Marshaller jaxMarshaller = jaxbContext.createMarshaller();
			jaxMarshaller.marshal( msbXmlFileObject, projectXmlFile );

			return projectXmlFile;
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
			MsbEngine.logger.error( "error", e1 );
			throw new RuntimeException( e1.toString() );
		}
	}

	public static ReadObject translateFromSequenceStringToObject( SAMInfo info ) {
//		String[] strDivs = sequenceOrigin.split("\\^");
//		String sequence = strDivs[0];
//		String[] positions = strDivs[1].split("\\&");
//		String strand = positions[1]; 
//		
//		String[] pos = positions[0].split("\\:")[1].split("\\$");
//		int startPos = Integer.parseInt( pos[0] );
//
//		ReadObject readObj = new ReadObject(startPos, sequence, strand.charAt(0) );
		ReadObject readObj = new ReadObject( info.getStart(), info.getEnd(), info.getReadSeq(), info.getStrand() );
		SAMInfo matePair = info.getMatePair();
		if( matePair != null ) {
			readObj.setMatePair( new ReadObject(matePair.getStart(), matePair.getEnd(), matePair.getReadSeq(), matePair.getStrand()) );
		}

		return readObj;
	}

	public static ReadObject translateFromSequenceStringToObject( MsvSamRecord info ) {
//		ReadObject readObj = null;
//		if( !info.hasMultiFragments() )
//			readObj = new ReadObject( info.getStart(), info.getEnd(), info.getReadSeq(), info.getStrand() );
//		else
		ReadObject readObj = new GeneralReadObject( info );
		return readObj;
	}

	public static double getDistance( RnaStructureObj a, RnaStructureObj b ) {
		return Math.sqrt( Math.pow(a.getEllipse().getCenterX() - b.getEllipse().getCenterX(), 2) + Math.pow(a.getEllipse().getCenterY() - b.getEllipse().getCenterY(), 2) );
	}

	public static double getDistanceAt( RnaStructureObj a, RnaStructureObj b ) {
		return Math.sqrt( Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2) );
	}

	public static synchronized void writeProjectItemToFile( File file, ProjectMapItem item ) {
//		FileOutputStream fout;
//		try {
//			fout = new FileOutputStream( file );
//
//			ObjectOutputStream oos = new ObjectOutputStream( fout );
//			oos.writeObject( item );
//			oos.close();
//			fout.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
////			e.printStackTrace();
//			MsbEngine.logger.error( "error", e );
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
////			e.printStackTrace();
//			MsbEngine.logger.error( "error", e );
//		}
		StoreProjectMapItemModel model = new StoreProjectMapItemModel( item );
		model.write( file );
	}

	public static double getCoordinate(double x, int MARGIN, double UNIT) {
		double newX = Math.round( (x - MARGIN) / UNIT ) * UNIT;

		return newX;
	}

/*** Under JDK 1.7 ***/
	public static Integer [] SortWithIndex(Object[] data, Integer [] index, int direction) {
		int len = data.length;
		Object temp1[]	= new Object[len];
		int temp2[]		= new int[len];

		for (int i = 0; i <len; i++) {
			for (int j = i + 1; j < len; j++) {
				if( direction == SwingConst.SORT_DESC ) {
					if( data[i] instanceof Number && data[j] instanceof Number ) {
						if( Double.isNaN( Double.valueOf( data[i].toString() ) ) && !Double.isNaN( Double.valueOf(data[j].toString()) ) )
							Utilities.exchange(temp1, temp2, data, index, i, j);
						else if( !Double.isNaN( Double.valueOf(data[i].toString()) ) && !Double.isNaN( Double.valueOf(data[j].toString()) ) ) {
							if( Double.valueOf(data[i].toString())<Double.valueOf(data[j].toString())) {
								Utilities.exchange(temp1, temp2, data, index, i, j);
							}
						}
					}else if( data[i] instanceof String && data[j] instanceof String ) {
						if( data[i].toString().compareTo( data[j].toString() ) < 0 ) 
							Utilities.exchange( temp1, temp2, data, index, i, j);
					}
				}else {
					if( data[i] instanceof Number && data[j] instanceof Number ) {
						if( !Double.isNaN( Double.valueOf(data[i].toString()) ) && Double.isNaN( Double.valueOf(data[j].toString()) ) )
							Utilities.exchange(temp1, temp2, data, index, i, j);
						else if( !Double.isNaN( Double.valueOf(data[i].toString()) ) && !Double.isNaN( Double.valueOf(data[j].toString()) ) ) {
							if( Double.valueOf(data[i].toString())>Double.valueOf(data[j].toString())) {
								Utilities.exchange(temp1, temp2, data, index, i, j);
							}
						}
					}else if( data[i] instanceof String && data[j] instanceof String ) {
						if( data[i].toString().compareTo( data[j].toString() ) > 0 ) 
							Utilities.exchange( temp1, temp2, data, index, i, j);
					}
				}
			}
		}

		return index;
    }

/*** Over JDK 1.7 ***/
//	public static Integer [] SortWithIndex(Object[] data, Object[] data2, Integer [] index, int direction) {
//		int len = data.length;
//		Object temp1[]	= new Object[len];
//		int temp2[]		= new int[len];
//
//		for (int i = 0; i <len; i++) {
//			for (int j = i + 1; j < len; j++) {
//				if( direction == SwingConst.SORT_DESC ) {
//					if( data[i] instanceof Number && data[j] instanceof Number ) {
//						if( Double.isNaN( (double)data[i] ) && !Double.isNaN( (double)data[j] ) )
//							Utilities.exchange(temp1, temp2, data, index, i, j);
//						else if( !Double.isNaN( (double)data[i] ) && !Double.isNaN( (double)data[j] ) ) {
//							if( (double)data[i]<(double)data[j]) {
//								Utilities.exchange(temp1, temp2, data, data2, index, i, j);
//							}else if( (double)data[i] == (double)data[j]) {
//								if( (double)data2[i]<(double)data2[j]) {
//									Utilities.exchange(temp1, temp2, data, data2, index, i, j);
//								}
//							}
//						}
//					}else if( data[i] instanceof String && data[j] instanceof String ) {
//						if( data[i].toString().compareTo( data[j].toString() ) < 0 ) 
//							Utilities.exchange( temp1, temp2, data, index, i, j);
//					}
//				}else {
//					if( data[i] instanceof Number && data[j] instanceof Number ) {
//						if( !Double.isNaN( (double)data[i] ) && Double.isNaN( (double)data[j] ) )
//							Utilities.exchange(temp1, temp2, data, index, i, j);
//						else if( !Double.isNaN( (double)data[i] ) && !Double.isNaN( (double)data[j] ) ) {
//							if( (double)data[i]>(double)data[j]) {
//								Utilities.exchange(temp1, temp2, data, data2, index, i, j);
//							}else if( (double)data[i] == (double)data[j]) {
//								if( (double)data2[i]>(double)data2[j]) {
//									Utilities.exchange(temp1, temp2, data, data2, index, i, j);
//								}
//							}
//						}
//					}else if( data[i] instanceof String && data[j] instanceof String ) {
//						if( data[i].toString().compareTo( data[j].toString() ) > 0 ) 
//							Utilities.exchange( temp1, temp2, data, index, i, j);
//					}
//				}
//			}
//		}
//
//		return index;
//    }

	/*** Under JDK 1.7 ***/
	public static Integer [] SortWithIndex(Object[] data, Object[] data2, Integer [] index, int direction) {
		int len = data.length;
		Object temp1[]	= new Object[len];
		int temp2[]		= new int[len];

		for (int i = 0; i <len; i++) {
			for (int j = i + 1; j < len; j++) {
				if( direction == SwingConst.SORT_DESC ) {
					if( data[i] instanceof Number && data[j] instanceof Number ) {
						if( Double.isNaN( Double.valueOf(data[i].toString()) ) && !Double.isNaN( Double.valueOf(data[j].toString()) ) )
							Utilities.exchange(temp1, temp2, data, index, i, j);
						else if( !Double.isNaN( Double.valueOf(data[i].toString()) ) && !Double.isNaN( Double.valueOf(data[j].toString()) ) ) {
							if( Double.valueOf(data[i].toString())<Double.valueOf(data[j].toString())) {
								Utilities.exchange(temp1, temp2, data, data2, index, i, j);
							}else if( Double.valueOf(data[i].toString()) == Double.valueOf(data[j].toString())) {
								if( Double.valueOf(data2[i].toString())<Double.valueOf(data2[j].toString())) {
									Utilities.exchange(temp1, temp2, data, data2, index, i, j);
								}
							}
						}
					}else if( data[i] instanceof String && data[j] instanceof String ) {
						if( data[i].toString().compareTo( data[j].toString() ) < 0 ) 
							Utilities.exchange( temp1, temp2, data, index, i, j);
					}
				}else {
					if( data[i] instanceof Number && data[j] instanceof Number ) {
						if( !Double.isNaN( Double.valueOf(data[i].toString()) ) && Double.isNaN( Double.valueOf(data[j].toString()) ) )
							Utilities.exchange(temp1, temp2, data, index, i, j);
						else if( !Double.isNaN( Double.valueOf(data[i].toString()) ) && !Double.isNaN( Double.valueOf(data[j].toString()) ) ) {
							if( Double.valueOf(data[i].toString())>Double.valueOf(data[j].toString())) {
								Utilities.exchange(temp1, temp2, data, data2, index, i, j);
							}else if( Double.valueOf(data[i].toString()) == Double.valueOf(data[j].toString())) {
								if( Double.valueOf(data2[i].toString())>Double.valueOf(data2[j].toString())) {
									Utilities.exchange(temp1, temp2, data, data2, index, i, j);
								}
							}
						}
					}else if( data[i] instanceof String && data[j] instanceof String ) {
						if( data[i].toString().compareTo( data[j].toString() ) > 0 ) 
							Utilities.exchange( temp1, temp2, data, index, i, j);
					}
				}
			}
		}

		return index;
    }

	public static void main(String[] args) throws FileNotFoundException {

//		Object[][] tmp = new Object[][]{{2, 34, 2}, {3, 23, 2}};
//		String[] args2 = new String[]{ "INPUT=/Users/lion/Desktop/sample_test.bam", "OUTPUT=/Users/lion/Desktop/sorted_sample_test.bam", "SORT_ORDER=coordinate" };
//		net.sf.picard.sam.SortSam sort = new net.sf.picard.sam.SortSam();
//		sort.instanceMain( args2 );

//		BuildBamIndex index = new BuildBamIndex();
//		index.instanceMain( new String[]{"INPUT=/Users/lion/Desktop/sample_test.bam", "OUTPUT=/Users/lion/Desktop/sorted_sample_test.bai"} );

//		SAMFileReader inputSam = new SAMFileReader( new File("/Users/lion/Desktop/sample_test.bam"), new File("/Users/lion/Desktop/sorted_sample_test.bai") );
//		
//		System.out.println( Utilities.getTotalReadCountWithIndex( inputSam ) );

//		java.awt.Color c = new java.awt.Color(231, 231, 231);
//		System.out.println( c.hashCode() );
//		Object a = Double.NaN;
//		System.out.println( a instanceof Number );

//		Object[] data	= new Object[]{3d,	3d,	3d,	3d,		3d,	3d,	3d,	3d,	1d};
//		Object[] data2	= new Object[]{1d,	0d,	5d,	12d,	2d,	1d,	3d,	1d,	21d};
//		Object[] data3	= new Object[]{1d,	2d,	3d,	4d,		5d,	6d,	7d,	8d,	9d};
//
//		Integer[] index = new Integer[data.length];
//		for(int i=0; i<index.length; i++)	index[i] = i;
//		
//		Utilities.SortWithIndex(data, data2, index, SwingConst.SORT_ASC );
//
//		for(int i=0; i<index.length; i++) {
//			System.out.print( data[i] + " " );
//		}
//		System.out.println("");
//
//		for(int i=0; i<index.length; i++) {
//			System.out.print( data2[i] + " " );
//		}
//		System.out.println("");
//
//		for(int i=0; i<index.length; i++) {
//			System.out.print( index[i] + " " );
//		}

		System.out.println(Utilities.isNumeric("5"));
	}
}