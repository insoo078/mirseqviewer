package kobic.msb.io.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
//
//import uk.ac.roslin.ensembl.dao.database.DBRegistry;
//import uk.ac.roslin.ensembl.exception.DAOException;
//import uk.ac.roslin.ensembl.exception.NonUniqueException;
//import uk.ac.roslin.ensembl.model.core.Species;
//import uk.ac.roslin.ensembl.config.DBConnection.DataSource;

import net.sf.picard.liftover.LiftOver;
import net.sf.picard.util.Interval;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.SAMSequenceRecord;


@SuppressWarnings("unused")
public class NGSFileReadCallableTest {
	private String sortFilePath;
	private String indexFilePath;
	
	private String group;
	private String sample;
	
	private Map<String, List<Object[]>>				seqMap;
	
//	private DBRegistry eReg;
	
	public NGSFileReadCallableTest(String sortFilePath, String indexFilePath) throws Exception{
		this.group = "Default";
		this.sample = "S1";

		this.sortFilePath = sortFilePath;
		this.indexFilePath = indexFilePath;

		this.seqMap			= new LinkedHashMap<String, List<Object[]>>();

//		this.eReg = new DBRegistry(DataSource.ENSEMBLDB);
	}

//	public void test() {
//		LinkedHashMap<String, SAMObj> newSeqMap	= new LinkedHashMap<String, SAMObj>();
//
//		SAMFileReader inputSam = new SAMFileReader( new File( this.sortFilePath ), new File( this.indexFilePath ) );
//		SAMRecordIterator iter = inputSam.iterator();
//		MsbEngine.logger.debug("START reading BAM file");
//
//		inputSam.setValidationStringency( SAMFileReader.ValidationStringency.SILENT );
//
//		long cnt = 0;
//		while( iter.hasNext() ) {
//			SAMRecord record = iter.next();
//			
//			if(record.getReadUnmappedFlag())	continue;	// Filter out unmapped read
//	        if(record.getDuplicateReadFlag())	continue;	// Filter out duplicated read
//			
//			String premature_id	= record.getReferenceName();
//	        String readName 	= record.getReadName();
//	        String readSeq		= record.getReadString();
//	        int start			= record.getAlignmentStart();
//	        int end				= record.getAlignmentEnd();
//
//	        String key = readSeq + "^" + premature_id + ":" + start + "-" + end;
//	        if( newSeqMap.containsKey( key ) )	{
//	        	newSeqMap.get(key).increase();
//	        	newSeqMap.put( key, newSeqMap.get(key) );
//	        }
//	        else									newSeqMap.put( key,  new SAMObj(premature_id, readName, readSeq, start, end ) );
////	        System.out.println( premature_id + " " + readName + " " + readSeq + " " + start + " " + end );
////	        this.doMatureInfo(premature_id, start, end);
////	        this.doReadRead(this.seqMap, record, readName, premature_id, start, end, readSeq);
////	        this.prematureList.add( premature_id );
//	        cnt++;
//		}
//		inputSam.close();
//		
//		System.out.println("read_count : " + this.seqMap.size() );
//		System.out.println("total_count : " + cnt );
//		MsbEngine.logger.debug("END reading BAM file");
//	}
	
//	public void test() {
//		LinkedHashMap<String, SAMObj> collapsedSequence = this.getCollapsing();
//		
//		Iterator<String> keys = collapsedSequence.keySet().iterator();
//		while( keys.hasNext() ) {
//			String key = keys.next();
//
//			this.doReadRead(this.seqMap, collapsedSequence.get(key) );
//		}
//	}
	
//	private LinkedHashMap<String, SAMObj> getCollapsing() {
//		LinkedHashMap<String, SAMObj> newSeqMap	= new LinkedHashMap<String, SAMObj>();
//
//		SAMFileReader inputSam = new SAMFileReader( new File( this.sortFilePath ), new File( this.indexFilePath ) );
//		SAMRecordIterator iter = inputSam.iterator();
//		MsbEngine.logger.debug("START reading BAM file");
//
//		inputSam.setValidationStringency( SAMFileReader.ValidationStringency.SILENT );
//
//		while( iter.hasNext() ) {
//			SAMRecord record = iter.next();
//			
//			if(record.getReadUnmappedFlag())	continue;	// Filter out unmapped read
//	        if(record.getDuplicateReadFlag())	continue;	// Filter out duplicated read
//			
//			String premature_id	= record.getReferenceName();
//	        String readName 	= record.getReadName();
//	        String readSeq		= record.getReadString();
//	        int start			= record.getAlignmentStart();
//	        int end				= record.getAlignmentEnd();
//	        
////	        try {
//	        	if( premature_id.contains("chr") ) {
//	        		String strChr = premature_id.replace("chr","");
////					Species human = this.eReg.getSpeciesByAlias("human");
////	
////					uk.ac.roslin.ensembl.model.core.Chromosome chr = human.getChromosomeByName( String.valueOf( strChr ) );
////	
////			        String sequence = chr.getSequenceAsString(start - 100, end + 100);
////			        
////			        System.out.println( sequence );
//	        	}
//	        
////		        char strand		= '+';
////		        String binaryStr = Utilities.lpad( Integer.toBinaryString( record.getFlags() ), 5, "0" );
////		        char forward = binaryStr.charAt(4);		// 0 : forward (+), 1: reverse (-)
////		        if( forward == '1' ) {
////		        	strand = '-';
////		        	readSeq = Utilities.reverseComplement( readSeq );
////		        }
////	
////		        String key = readSeq + "^" + premature_id + ":" + start + "-" + end;
////		        if( newSeqMap.containsKey( key ) )	{
////		        	newSeqMap.get(key).increase();
////		        	newSeqMap.put( key, newSeqMap.get(key) );
////		        }else									newSeqMap.put( key,  new SAMObj(premature_id, readName, readSeq, strand, start, end ) );
////			} catch (NonUniqueException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			} catch (DAOException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//		}
//		inputSam.close();
//		
//		return newSeqMap;
//	}
	
//	private void doReadRead( Map<String, List<Object[]>> map, SAMObj samObj ) {
////        Pattern p = Pattern.compile( "^(.+)_x(\\d+)$" );
////        Matcher m = p.matcher( readName );
////
////        String count = "0";
////        while( m.find() ) {
////        	count = m.group(2);
////        }
////        if( count.equals("0") ) {
////        	p = Pattern.compile( "^(.+)_(\\d+)$" );
////	        m = p.matcher( readName );
////
////	        count = "0";
////	        while( m.find() ) {
////	        	count = m.group(2);
////	        }
////        }
////
////        char strand		= '+';
////        String binaryStr = Utilities.lpad( Integer.toBinaryString( record.getFlags() ), 5, "0" );
////        char forward = binaryStr.charAt(4);		// 0 : forward (+), 1: reverse (-)
////        if( forward == '1' ) {
////        	strand = '-';
////        	readSeq = Utilities.reverseComplement( readSeq );
////        }
//
//        SAMInfo info = new SAMInfo( samObj.getPremature_id(), samObj.getReadName(), samObj.getReadSeq(), samObj.getStrand(), samObj.getStart(), samObj.getEnd(), (int)samObj.getCount() );
//
//    	String key = info.getReadSeq() + "^" + info.getChr() + ":" + info.getStart() + "-" + info.getEnd();
//        if( map.containsKey( info.getChr() ) ) {
//        	map.get( info.getChr() ).add( new Object[]{ key, this.group, this.sample, info.getCount() } );
//        }else {
//        	List<Object[]> result = new ArrayList<Object[]>();
//        	result.add( new Object[]{ key, this.group, this.sample, info.getCount() } );
//        	map.put( info.getChr(), result );
//        }
//	}

	public static void main(String[] args) throws Exception {
//		NGSFileReadCallableTest test = new NGSFileReadCallableTest("/Users/lion/Desktop/sort2.bam", "/Users/lion/Desktop/sort2.bam.bai");
//		
//		test.test();
		
		LiftOver liftOver = new LiftOver( new File("/Users/lion/Desktop/hg19.hg19.all.chain" ) );
//		Interval interval = new Interval("AUGCUUCCGGCCUGUUCCCUGAGACCUCAAGUGUGAGUGUACUAUUGAUGCUUCACACCUGGGCUCUCCGGGUACCAGGACGGUUUGAGCAGAU", 5902231, 5902324);

//		Interval interval = new Interval("", 1, 100);
		
//		Interval result = liftOver.liftOver( interval );
		
//		System.out.println( result.getSequence() );
//		System.out.println( liftOver.getLiftOverMinMatch() );
//		System.out.println( liftOver.)
		
		SAMFileReader inputSam = new SAMFileReader( new File( "/Users/lion/Desktop/sort2.bam" ), new File( "/Users/lion/Desktop/sort2.bam.bai" ) );

		try {
//			SAMRecordIterator iter = inputSam.iterator();
//			MsbEngine.logger.debug("START reading BAM file");
	
			SAMFileHeader fileHeader = inputSam.getFileHeader();
	        SAMSequenceDictionary dictionary = fileHeader.getSequenceDictionary();
		
			liftOver.validateToSequences( dictionary );
			
			List<SAMSequenceRecord> sequences = dictionary.getSequences();
			for (SAMSequenceRecord sequence : sequences) {
				Interval interval = new Interval( sequence.getSequenceName(), 10, 1000);

				Interval result = liftOver.liftOver( interval );
				
				if( result != null)
					System.out.println( result.getSequence() );
			}
//			Interval result = liftOver.liftOver( new Interval( dic) );
//			System.out.println( result.getSequence() );
		}catch(Exception e){
			e.printStackTrace();
		}finally{
//		for (SAMSequenceRecord sequence : sequences) {
//			liftOver.validateToSequences( sequence );
//		}
//		inputSam.setValidationStringency( SAMFileReader.ValidationStringency.SILENT );
//
//		while( iter.hasNext() ) {
//			SAMRecord record = iter.next();
//			
//			if(record.getReadUnmappedFlag())	continue;	// Filter out unmapped read
//	        if(record.getDuplicateReadFlag())	continue;	// Filter out duplicated read
//	        
//	        
//		}
			inputSam.close();
		}
	}
}
