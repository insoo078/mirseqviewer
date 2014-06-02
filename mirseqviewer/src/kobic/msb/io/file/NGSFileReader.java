package kobic.msb.io.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.util.MsvUtilities;
import kobic.msb.db.RecordsFileException;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.server.obj.ReadQuality;
import kobic.msb.server.obj.SAMInfo;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecord.SAMTagAndValue;
import net.sf.samtools.SAMRecordIterator;

public class NGSFileReader {

	public List<SAMInfo> _readBAM( InputStream fis, String[] conditions ) {
		SAMFileReader inputSam = new SAMFileReader( fis );

	    inputSam.setValidationStringency( SAMFileReader.ValidationStringency.SILENT );
		
		List<SAMInfo> list = new ArrayList<SAMInfo>();

	    SAMRecordIterator iter = inputSam.iterator();
	    while(iter.hasNext()) {
	    	SAMRecord rec=iter.next();

	        if(rec.getReadUnmappedFlag())	continue;	// Filter out unmapped read
	        if(rec.getDuplicateReadFlag())	continue;	// Filter out duplicated read

	        String reference	= rec.getReferenceName();
	        String readName 	= rec.getReadName();
	        String readSeq		= rec.getReadString();
	        int start			= rec.getAlignmentStart();
	        int end				= rec.getAlignmentEnd();
	        
	        Pattern p = Pattern.compile( "^(.+)_x(\\d+)$" );
	        Matcher m = p.matcher( readName );

	        String count = "0";
	        while( m.find() ) {
	        	count = m.group(2);
	        }
//	        log.debug( reference + " " + readName + " " + count );
	        if( count.equals("0") ) {
	        	p = Pattern.compile( "^(.+)_(\\d+)$" );
		        m = p.matcher( readName );

		        count = "0";
		        while( m.find() ) {
		        	count = m.group(2);
		        }
	        }
	        
	        char strand		= '+';
	        String binaryStr = Utilities.lpad( Integer.toBinaryString( rec.getFlags() ), 5, "0" );
//	        log.debug( binaryStr + " : " + rec.getFlags() );
	        char forward = binaryStr.charAt(4);		// 0 : forward (+), 1: reverse (-)
	        if( forward == '1' ) {
	        	strand = '-';
	        	readSeq = Utilities.getReverseComplementary( readSeq );
//	        	readSeq = readSeq;
	        }
	        
	        ReadQuality readQual = new ReadQuality( rec );

	        SAMInfo info = new SAMInfo( reference, readName, readSeq, strand, start, end, Integer.parseInt( count ), readQual );
	        
	        list.add( info );
	    }
	    iter.close();
	    inputSam.close();

	    return list;
	}
	
	public List<SAMInfo> _readBAM( InputStream fis, String keyword ) {
		if( keyword == null )	return this._readBAM(fis);
		else {
			SAMFileReader inputSam = new SAMFileReader( fis );

		    inputSam.setValidationStringency( SAMFileReader.ValidationStringency.SILENT );
			
			List<SAMInfo> list = new ArrayList<SAMInfo>();

		    SAMRecordIterator iter = inputSam.iterator();
		    while(iter.hasNext()) {
		    	SAMRecord rec=iter.next();

		        if(rec.getReadUnmappedFlag())	continue;	// Filter out unmapped read
		        if(rec.getDuplicateReadFlag())	continue;	// Filter out duplicated read

		        String reference	= rec.getReferenceName();
		        String readName 	= rec.getReadName();
		        String readSeq		= rec.getReadString();
		        int start			= rec.getAlignmentStart();
		        int end				= rec.getAlignmentEnd();
		        
		        Pattern p = Pattern.compile( "^(.+)_x(\\d+)$" );
		        Matcher m = p.matcher( readName );

		        String count = "0";
		        while( m.find() ) {
		        	count = m.group(2);
		        }
//		        log.debug( reference + " " + readName + " " + count );
		        if( count.equals("0") ) {
		        	p = Pattern.compile( "^(.+)_(\\d+)$" );
			        m = p.matcher( readName );

			        count = "0";
			        while( m.find() ) {
			        	count = m.group(2);
			        }
		        }

		        char strand		= '+';
		        String binaryStr = Utilities.lpad( Integer.toBinaryString( rec.getFlags() ), 5, "0" );
//		        log.debug( binaryStr + " : " + rec.getFlags() );
		        char forward = binaryStr.charAt(4);		// 0 : forward (+), 1: reverse (-)
		        if( forward == '1' ) {
		        	strand = '-';
		        	readSeq = Utilities.getReverseComplementary( readSeq );
//		        	readSeq = readSeq;
		        }

		        ReadQuality readQual = new ReadQuality( rec );

		        if( reference.equals( keyword ) ) {
			        SAMInfo info = new SAMInfo( reference, readName, readSeq, strand, start, end, Integer.parseInt( count ), readQual);
			        
			        list.add( info );
		        }
		    }
		    iter.close();
		    inputSam.close();

		    return list;
		}
	}
	
	@SuppressWarnings("unused")
	public void test(String sortFilePath, String indexFilePath) {
		SAMFileReader inputSam = new SAMFileReader( new File( sortFilePath ), new File( indexFilePath ) );
		inputSam.setValidationStringency( SAMFileReader.ValidationStringency.SILENT );
		
		HairpinVO hairpinVo = new HairpinVO();
		String id = "chr15";
		String hairpinStart = "34674260";
		String hairpinEnd = "34674361";
		hairpinVo.setChr( id );
		hairpinVo.setStart( hairpinStart );
		hairpinVo.setEnd( hairpinEnd );;
		hairpinVo.setStrand("-");

		SAMRecordIterator samIter = inputSam.queryOverlapping( id, Integer.parseInt( hairpinStart ), Integer.parseInt( hairpinEnd ) );

		int flagValue = 1;
		while( samIter.hasNext() ) {
			SAMRecord record = (SAMRecord)samIter.next();

			String premature_id	= id;
	        String readName 	= record.getReadName();
	        String readSeq		= flagValue==JMsbSysConst.WITH_GENOME&&hairpinVo.getStrand().equals("-")?Utilities.getReverseComplementary( record.getReadString() ):record.getReadString();
	        // If sequence are aligned to genome, we have to change ralative position
	        int rStart			= flagValue==JMsbSysConst.WITH_GENOME?record.getAlignmentStart()	- Integer.parseInt( hairpinStart ) + 1 : record.getAlignmentStart();		// relative start
	        int rEnd			= flagValue==JMsbSysConst.WITH_GENOME?record.getAlignmentEnd()		- Integer.parseInt( hairpinStart ) + 1 : record.getAlignmentEnd();			// relative end

	        if( flagValue==JMsbSysConst.WITH_GENOME && hairpinVo.getStrand().equals( "-" ) ) {
	        	rStart	= Integer.parseInt(hairpinEnd) - record.getAlignmentEnd() + 1;
	        	rEnd	= Integer.parseInt(hairpinEnd) - record.getAlignmentStart() + 1;
	        }
	        System.out.print("h_s=" + hairpinStart + " h_e=" + hairpinEnd + "   rs=" + record.getAlignmentStart() + " re=" + record.getAlignmentEnd() );
	        System.out.println( "     diff s=" + rStart + " e=" + rEnd);
		}
		samIter.close();
		inputSam.close();
	}
	
	@SuppressWarnings("unused")
	public List<SAMInfo> _readBAM( InputStream fis ) {
		SAMFileReader inputSam = new SAMFileReader( fis );
//		SAMFileReader cntSam = new SAMFileReader( fis );

	    inputSam.setValidationStringency( SAMFileReader.ValidationStringency.SILENT );
		
		List<SAMInfo> list = new ArrayList<SAMInfo>();
		long a = System.currentTimeMillis();
//		long total = Utilities.getTotalReadCount( inputSam );
		long b = System.currentTimeMillis();
		System.out.println( (b-a)/1000 + "sec" );

		int i = 0;
	    SAMRecordIterator iter = inputSam.iterator();
	    while(iter.hasNext()) {
	    	SAMRecord rec=iter.next();
	    	i++;

	        if(rec.getReadUnmappedFlag())	continue;	// Filter out unmapped read
	        if(rec.getDuplicateReadFlag())	continue;	// Filter out duplicated read

	        String reference	= rec.getReferenceName();
	        String readName 	= rec.getReadName();
	        String readSeq		= rec.getReadString();
	        int start			= rec.getAlignmentStart();
	        int end				= rec.getAlignmentEnd();
	
	        List<SAMTagAndValue> lst = rec.getAttributes();
	        
	        Integer obj = (Integer)rec.getAttribute( "NH" );
	        
	        System.out.println( obj + " " + reference + " " + readName + " " + rec.getReadUnmappedFlag() + " " + rec.getDuplicateReadFlag() + " " + rec.getMappingQuality() + " " + rec.getFlags() );
	        Pattern p = Pattern.compile( "^(.+)_x(\\d+)$" );
	        Matcher m = p.matcher( readName );

	        String count = "0";
	        while( m.find() ) {
	        	count = m.group(2);
	        }
//	        log.debug( reference + " " + readName + " " + count );
	        if( count.equals("0") ) {
	        	p = Pattern.compile( "^(.+)_(\\d+)$" );
		        m = p.matcher( readName );

		        count = "0";
		        while( m.find() ) {
		        	count = m.group(2);
		        }
	        }
	        
	        char strand		= '+';
	        String binaryStr = Utilities.lpad( Integer.toBinaryString( rec.getFlags() ), 5, "0" );
//	        log.debug( binaryStr + " : " + rec.getFlags() );
	        char forward = binaryStr.charAt(4);		// 0 : forward (+), 1: reverse (-)
	        if( forward == '1' ) {
	        	strand = '-';
	        	readSeq = Utilities.getReverseComplementary( readSeq );
//	        	readSeq = readSeq;
	        }

	        ReadQuality readQual = new ReadQuality( rec );
	        SAMInfo info = new SAMInfo( reference, readName, readSeq, strand, start, end, Integer.parseInt( count ), readQual );
	        
	        list.add( info );
	    }
	    iter.close();
	    inputSam.close();
	    
//	    System.out.println("total=" + total + " read size = " + i );

	    return list;
	}

	public List<SAMInfo> _readBAM( String fileName ) throws FileNotFoundException {
		SAMFileReader inputSam = new SAMFileReader( new FileInputStream( new File(fileName) ) );
		SAMFileReader cntSam = new SAMFileReader( new FileInputStream( new File(fileName) ) );

//		SAMFileReader cntSam = new SAMFileReader( fis );

	    inputSam.setValidationStringency( SAMFileReader.ValidationStringency.SILENT );
		
		List<SAMInfo> list = new ArrayList<SAMInfo>();
		long a = System.currentTimeMillis();
		long total = MsvUtilities.getTotalReadCount( cntSam );

		int i = 0;
	    SAMRecordIterator iter = inputSam.iterator();
	    while(iter.hasNext()) {
	    	SAMRecord rec=iter.next();
	    	i++;

	        if(rec.getReadUnmappedFlag())	continue;	// Filter out unmapped read
	        if(rec.getDuplicateReadFlag())	continue;	// Filter out duplicated read

	        String reference	= rec.getReferenceName();
	        String readName 	= rec.getReadName();
	        String readSeq		= rec.getReadString();
	        int start			= rec.getAlignmentStart();
	        int end				= rec.getAlignmentEnd();
	        
	        Pattern p = Pattern.compile( "^(.+)_x(\\d+)$" );
	        Matcher m = p.matcher( readName );

	        String count = "0";
	        while( m.find() ) {
	        	count = m.group(2);
	        }
//	        log.debug( reference + " " + readName + " " + count );
	        if( count.equals("0") ) {
	        	p = Pattern.compile( "^(.+)_(\\d+)$" );
		        m = p.matcher( readName );

		        count = "0";
		        while( m.find() ) {
		        	count = m.group(2);
		        }
	        }
	        
	        char strand		= '+';
	        String binaryStr = Utilities.lpad( Integer.toBinaryString( rec.getFlags() ), 5, "0" );
//	        log.debug( binaryStr + " : " + rec.getFlags() );
	        char forward = binaryStr.charAt(4);		// 0 : forward (+), 1: reverse (-)
	        if( forward == '1' ) {
	        	strand = '-';
	        	readSeq = Utilities.getReverseComplementary( readSeq );
//	        	readSeq = readSeq;
	        }

	        ReadQuality readQual = new ReadQuality( rec );

	        SAMInfo info = new SAMInfo( reference, readName, readSeq, strand, start, end, Integer.parseInt( count ), readQual );
	        
	        list.add( info );
	    }
	    iter.close();
	    inputSam.close();
	    
	    System.out.println("total=" + total + " read size = " + i );
		long b = System.currentTimeMillis();
		System.out.println( (b-a)/1000 + "sec" );

	    return list;
	}


	public static void main(String[] args) throws ClassNotFoundException, IOException, RecordsFileException {
		NGSFileReader reader = new NGSFileReader();
		
		reader.test("/Users/insujang/Desktop/prj3/tmp/sorted_wgEncodeCaltechRnaSeqGm12878R1x75dSplicesRep1V2.bam", "/Users/insujang/Desktop/prj3/tmp/wgEncodeCaltechRnaSeqGm12878R1x75dSplicesRep1V2.bam.bai");
//		reader.test("/Users/lion/Desktop/sorted_wgEncodeSydhRnaSeqK562Ifna6hPolyaAln.bam", "/Users/lion/Desktop/sorted_wgEncodeSydhRnaSeqK562Ifna6hPolyaAln.bai");
//		List<SAMInfo> list = reader._readBAM( new FileInputStream( new File("/Users/lion/Desktop/sorted.bam") ) );
////		List<SAMInfo> list = reader._readBAM( new FileInputStream( new File("/Users/lion/Downloads/sample_bam_file/sample.bam") ) );
////		List<SAMInfo> list = reader._readBAM( new FileInputStream( new File("/Volumes/Data/mirSeqBrowser/GSM416611.ms.bam") ) );
//
//		long a = System.currentTimeMillis();
////		String fullpath = "/Users/lion/Desktop/mirbase";
////		RecordsFile recordsBaseFile = new RecordsFile( fullpath + ".db",			"r" );
////		RecordsFile recordsFile		= new RecordsFile( fullpath + "_accession.db",	"r" );
////		RecordsFile recordsIdFile	= new RecordsFile( fullpath + "_id.db",			"r" );
////		
////		long b= System.currentTimeMillis();
////		System.out.println( "Read : " + (b-a) + "sec." );
////		
////		MirBaseLoader loader = new MirBaseLoader();
//		
//		Iterator<SAMInfo> iter = list.iterator();
//
//		int i = 0;
//		while( iter.hasNext() ) {
//			SAMInfo sam = iter.next();
////			String key = sam.getChr();
////			
////			RecordReader index = null;
////			if( recordsFile.recordExists( key ) )			index = recordsFile.readRecord( key );
////			else if( recordsIdFile.recordExists( key ) ) 	index = recordsIdFile.readRecord( key );
////
////			if( index != null ) {
////				String id = (String)index.readObject();
////
////				RecordReader aa = recordsBaseFile.readRecord( id );
////				
////				MirBaseRnaHairpinInfo info = (MirBaseRnaHairpinInfo)aa.readObject();
////			}
//			i++;
//		}
////		long c= System.currentTimeMillis();
////		System.out.println( "Find : " + (c-b) + "sec.   total count = " + i );
	}
}