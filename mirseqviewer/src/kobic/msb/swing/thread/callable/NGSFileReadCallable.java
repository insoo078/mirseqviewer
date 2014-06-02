package kobic.msb.swing.thread.callable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;


import kobic.com.method.mirna.NovelMicroRnaFinding;
import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.db.sqlite.vo.MatureVO;
import kobic.msb.server.obj.GroupSamInfo;
import kobic.msb.server.obj.MsvSamRecord;
import kobic.msb.server.obj.NovelMiRnaObj;
import kobic.msb.swing.thread.callable.obj.NGSFileReadResultObj;
import kobic.msb.swing.thread.caller.AbstractThreadCaller;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import net.sf.samtools.AbstractBAMFileIndex;
import net.sf.samtools.BAMIndexMetaData;
import net.sf.samtools.BAMRecord;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

public class NGSFileReadCallable  implements Callable<NGSFileReadResultObj>{
//	private static Map<String, List<MatureVO>> matureMap = MsbEngine._db.getAllMicroRnaMaturesByMirid();

	private Map<String, Integer>					matureProfileMap;
//	private Map<String, List<Object[]>>				seqMap;
	private Map<String, LinkedHashMap<String, GroupSamInfo>>			seqMap;
//	private List<String>							prematureList;
	private List<HairpinVO>							prematureList;
	private String group;
	private String sample;
	
	private LinkedHashMap<String, String>			urlReadMap;

	private Map<String, List<MatureVO>>				matureMap;
//	private Map<String, List<HairpinVO>>			hairpinMap;
	private List<HairpinVO>							hairpinLst;
	
	private ProjectMapItem							projectMapItem;
	
	private CountDownLatch							cdl;
	
	private String sortFilePath;
	private String indexFilePath;

	private AbstractThreadCaller test;
	
	public NGSFileReadCallable(CountDownLatch latch, String group, String sample, String sortFile, String indexFile, Map<String, List<MatureVO>> matureMap, List<HairpinVO> hairpinLst, ProjectMapItem projectMapItem) {
		this.cdl			= latch;
		this.group			= group;
		this.sample			= sample;
		this.sortFilePath	= sortFile;
		this.indexFilePath	= indexFile;
		this.matureProfileMap		= new LinkedHashMap<String, Integer>();
//		this.seqMap			= new LinkedHashMap<String, List<Object[]>>();
		this.seqMap			= new LinkedHashMap<String, LinkedHashMap<String, GroupSamInfo>>();
		
		this.urlReadMap		= new LinkedHashMap<String, String>();
//		this.prematureList	= new ArrayList<String>();
		this.prematureList	= new ArrayList<HairpinVO>();

		this.matureMap		= matureMap;
//		this.hairpinMap		= hairpinMap;
		this.hairpinLst		= hairpinLst;
		
		this.projectMapItem	= projectMapItem;
	}

	public void setNGSTestCaller( AbstractThreadCaller caller ) {
		this.test = caller;
	}
	
	/*****************************************************************************
	 * To decide reference whether that is genome or miRBase sequence
	 * 
	 * @param sortFilePath : sorted BAM file path
	 * @param indexFilePath : index file path
	 * 
	 * @return
	 */
	private static int whichBamReader( String sortFilePath, String indexFilePath, List<HairpinVO> hairpinList ) {
		SAMFileReader inputSam = new SAMFileReader( new File( sortFilePath ), new File( indexFilePath ) );
		SAMRecordIterator samIter = inputSam.iterator();

		int type = JMsbSysConst.WITH_GENOME;

		if( samIter.hasNext() ) {
			SAMRecord record = samIter.next();
			
			for( HairpinVO vo:hairpinList ) {
				if( vo.getId().equals( record.getReferenceName() ) ) {
					type = JMsbSysConst.WITH_MIRBASE;
					break;
				}
/********************************
				if( record.getReferenceName().startsWith("chr") || record.getReferenceName().startsWith("contig") || record.getReferenceName().startsWith("super") )	{
					inputSam.close();
					return JMsbSysConst.WITH_GENOME;
				}
********************************/
			}
		}
		samIter.close();
		inputSam.close();

		return type;
	}
	
	public int getTotalReadCount( SAMFileReader sam ) {
	    int count = 0;
	 
	    AbstractBAMFileIndex index = (AbstractBAMFileIndex) sam.getIndex();

	    int nRefs = index.getNumberOfReferences();
	    for (int i = 0; i < nRefs; i++) {
	        BAMIndexMetaData meta = index.getMetaData(i);
	        count += meta.getAlignedRecordCount();
	    }
	 
	    return count;
	}
	
	@Override
	public NGSFileReadResultObj call() throws InterruptedException, Exception{
		try{
			MsbEngine.logger.debug("Start reading BAM file");
			int refFlag = NGSFileReadCallable.whichBamReader( this.sortFilePath, this.indexFilePath, this.hairpinLst );

			SAMFileReader inputSam = null;
			SAMRecordIterator samIter = null;
			
//			int index = 0;
			Iterator<HairpinVO> iter = this.hairpinLst.iterator();
			while( iter.hasNext() ) {
				this.test.getOwnerDialog().setProgressToGetMiRnas( this.test.getOwnerDialog().getProgressToGetMiRnas() + 1 );
//				index++;
				
				if( Thread.currentThread().isInterrupted() ) {
					if( samIter != null )	samIter.close();
					if( inputSam != null )	inputSam.close();

					throw new InterruptedException("Interrupted");
				}

				inputSam = new SAMFileReader( new File( this.sortFilePath ), new File( this.indexFilePath ) );
				inputSam.setValidationStringency( SAMFileReader.ValidationStringency.SILENT );

				HairpinVO hairpinVo = iter.next();
				String hairpinStart	= Integer.toString( hairpinVo.getIntegerStart() );
				String hairpinEnd	= Integer.toString( hairpinVo.getIntegerEnd() );
				String chromosome	= hairpinVo.getChr();
				String id			= hairpinVo.getId();

				// chromosome names is normally start with "chr" (eg. chr1, chr2, ... chr22)
				samIter = refFlag==JMsbSysConst.WITH_GENOME?inputSam.queryOverlapping( chromosome, Integer.parseInt( hairpinStart )-1, Integer.parseInt( hairpinEnd )-1 ):inputSam.queryOverlapping( id, 0, Integer.parseInt(hairpinEnd) - Integer.parseInt( hairpinStart ) + 1 );

				if( chromosome.toLowerCase().startsWith("chr") && !samIter.hasNext() ) {
					String new_chromosome = Utilities.toUppercaseFirstLetters( chromosome );
					samIter.close();

					// chromosome names is start with "Chr" is only caplitalized first character (eg. Chr1, Chr2, ... Chr22)
					samIter = refFlag==JMsbSysConst.WITH_GENOME?inputSam.queryOverlapping( new_chromosome, Integer.parseInt( hairpinStart )-1, Integer.parseInt( hairpinEnd )-1 ):inputSam.queryOverlapping( id, 0, Integer.parseInt(hairpinEnd) - Integer.parseInt( hairpinStart ) + 1 );

					if( !samIter.hasNext() ) {
						samIter.close();

						new_chromosome = chromosome.toUpperCase();
						// chromosome names is start with "CHR" is  caplitalized all characters (eg. CHR1, CHR2, ... CHR22)
						samIter = refFlag==JMsbSysConst.WITH_GENOME?inputSam.queryOverlapping( new_chromosome, Integer.parseInt( hairpinStart )-1, Integer.parseInt( hairpinEnd )-1 ):inputSam.queryOverlapping( id, 0, Integer.parseInt(hairpinEnd) - Integer.parseInt( hairpinStart ) + 1 );
						
						if( !samIter.hasNext() ) {
							samIter.close();
							
							new_chromosome = chromosome.replace("chr", "");

							// chromosome names is only start with number (eg. 1, 2, 3, 4 .... 22)
							samIter = refFlag==JMsbSysConst.WITH_GENOME?inputSam.queryOverlapping( new_chromosome, Integer.parseInt( hairpinStart )-1, Integer.parseInt( hairpinEnd )-1 ):inputSam.queryOverlapping( id, 0, Integer.parseInt(hairpinEnd) - Integer.parseInt( hairpinStart ) + 1 );
						}
					}
				}

				int count = 0;
				while( samIter.hasNext() ) {
					BAMRecord record = (BAMRecord)samIter.next();
					
			        count++;
					
					if( record.getReadUnmappedFlag() )	continue;

					MsvSamRecord readInfo = MsvSamRecord.getNewInstance( record, hairpinVo, refFlag );

					// If fragment is not overlapped, miRse viewer will skip this read
					if( readInfo.isOverlappedWithPremature( Integer.parseInt(hairpinStart), Integer.parseInt(hairpinEnd) ) == false )
						continue;

/********************
			        MsbEngine.logger.debug("test " + record.getAlignmentStart() + " " + record.getAlignmentEnd() + " vs " + readInfo.getChr() + " " + readInfo.getStart() + " " + readInfo.getEnd() );

			        this.toCumulateForMatureMicroRnaFromBamFileByHairpinId( hairpinVo, record.getAlignmentStart(), record.getAlignmentEnd(), false );
*********************/
			        this.preDoReadMapFromSAMRecord( this.seqMap, readInfo );  //<- Here has problems, so i have to modify this method

			        if( !this.prematureList.contains( hairpinVo ) )	this.prematureList.add( hairpinVo );
				}
				
//				MsbEngine.logger.info( id + " " + index + "/" + this.hairpinLst.size() + " " + count  );

//// 20140502 added by insujang
//				try {
//					if( this.seqMap.get(id) != null ) {
//						String filename = BamReadObject.writeBamReadObject( this.projectMapItem.getProjectName(), id, this.seqMap.get(id) );
//						this.urlReadMap.put(id, filename);
//						
//						if( this.seqMap.containsKey( id ) )	{
//							this.toCumulateForMatureMicroRnaFromBamFileByHairpinId( hairpinVo, false );
//						}
//					}
//
//					this.seqMap.clear();
//				}catch(Exception e) {
//					MsbEngine.logger.error( "Error", e);
//				}finally{
//					if( samIter != null )	samIter.close();
//					if( inputSam != null )	inputSam.close();
//				}

				if( samIter != null )	samIter.close();
				if( inputSam != null )	inputSam.close();
				
				if( this.seqMap.containsKey( id ) )	{
					this.toCumulateForMatureMicroRnaFromBamFileByHairpinId( hairpinVo, false );
				}
			}

			/***************************************************************************************
			 * To find novel miRNAs
			 */
			if( this.projectMapItem.getBedFormat() != null ) {
				NovelMicroRnaFinding nmrf = new NovelMicroRnaFinding( this.projectMapItem );

				Map<String, NovelMiRnaObj> novelMap = nmrf.findingNovelMiRna( this.sortFilePath, this.indexFilePath );
				
				Collection<NovelMiRnaObj> collect = novelMap.values();
				Iterator<NovelMiRnaObj> iterCollect = collect.iterator();
				while( iterCollect.hasNext() ) {
					NovelMiRnaObj vo = iterCollect.next();
					
					if( Thread.currentThread().isInterrupted() ) {
						throw new InterruptedException("Interrupted");
					}

					if( vo.getSize() > 0 ) {
						String sequence = vo.getSequence( vo.getStart(), vo.getEnd(), vo.getStrand().charAt(0) );
						HairpinVO hairpinVo = new HairpinVO( vo.getNovelMicroRnaId(), null, sequence, vo.getChromosome(), vo.getStrand(), Integer.toString( vo.getStart() ), Integer.toString( vo.getEnd() ) );
	
						this.matureProfileMap.put( vo.getNovelMicroRnaId(), vo.getSize() );
						
						for( int i=0; i<vo.getSize(); i++ ) {
//							SAMInfo si = vo.getSamInfoAt( i );
							MsvSamRecord si = vo.getSamInfoAt( i );
							si.setChr( vo.getNovelMicroRnaId() );
							si.setStrand( vo.getStrand().charAt(0) );

							this.doCollapsingReadFromSAMRecord( this.seqMap, si );

// 20140502 added by insujang
//							try {
//								String id			= hairpinVo.getId();
//								
//								String filename = BamReadObject.writeBamReadObject( this.projectMapItem.getProjectName(), id, this.seqMap.get(id) );
//								this.urlReadMap.put(id, filename);
//	
//								this.seqMap.clear();
//							}catch(Exception e) {
//								MsbEngine.logger.error( "Error", e);
//							}
						}
						if( !this.prematureList.contains( hairpinVo ) )	this.prematureList.add( hairpinVo );
					}
				}
			}

			MsbEngine.logger.debug("END reading BAM file");

			if( this.test != null )	{
//				MsbEngine.logger.debug("Call callbak function");
				this.cdl.countDown();

				NGSFileReadResultObj obj = new NGSFileReadResultObj( this.group, this.sample, this.seqMap, this.matureProfileMap, this.prematureList );
//				NGSFileReadResultObj obj = new NGSFileReadResultObj( this.group, this.sample, this.urlReadMap, this.matureProfileMap, this.prematureList );

//				this.test.callback( this.group, this.sample, this.seqMap, this.profileMap, this.prematureList );

				return obj;
			}
		}catch(InterruptedException ie) {
//			this.test.callbackInterrupt();
//
//			if( !MsbEngine.getExecutorService().isShutdown() )	MsbEngine.getExecutorService().shutdownNow();
			MsbEngine.logger.error("Error ", ie);
			throw ie;
		}catch(Exception iex) {
//			this.test.callbackByException("Exception : reading bam file", iex);
			MsbEngine.logger.error("Error", iex);
			throw iex;
		}

		return null;
	}

	public synchronized void writeMap( String filePath, LinkedHashMap<String, GroupSamInfo> map ) throws FileNotFoundException, IOException, Exception{
		File file = new File( filePath );
		FileOutputStream fout = new FileOutputStream( file );
	
		ObjectOutputStream oos = new ObjectOutputStream( fout );
		oos.writeObject( map );
		oos.flush();
		oos.close();
		fout.close();
	}

	private void doCollapsingReadFromSAMRecord( Map<String, LinkedHashMap<String, GroupSamInfo>> map, MsvSamRecord info ) throws Exception{
		String key = info.getPublicKey();
		int hashCode = info.getPublicKeyHashCode();
		String keyWithoutCigar = info.getPublicKeyWithoutCigar();

        if( map.containsKey( info.getChr() ) ) {
//        	if( map.get( info.getChr() ).containsKey( key ) ) {
//        		MsvSamRecord msr = map.get( info.getChr() ).get(key).getSamInfo();
        	if( map.get( info.getChr() ).containsKey( keyWithoutCigar ) ) {
    		MsvSamRecord msr = map.get( info.getChr() ).get(keyWithoutCigar).getSamInfo();
        		msr.setCount( msr.getCount() + 1 );
        	}else {
//        		map.get( info.getChr() ).put( key, new GroupSamInfo( key, hashCode, this.group, this.sample, info ) );
        		map.get( info.getChr() ).put( keyWithoutCigar, new GroupSamInfo( key, hashCode, this.group, this.sample, info ) );
        	}
        }else {
        	GroupSamInfo gsi = new GroupSamInfo( key, hashCode, this.group, this.sample, info );
        	LinkedHashMap<String, GroupSamInfo> hhm = new LinkedHashMap<String, GroupSamInfo>();
//        	hhm.put(key, gsi);
        	hhm.put(keyWithoutCigar, gsi);

        	map.put( info.getChr(), hhm );
        }
	}

	private void preDoReadMapFromSAMRecord( Map<String, LinkedHashMap<String, GroupSamInfo>> map, MsvSamRecord info ) throws Exception{
		if( Utilities.nulltoEmpty( info.getStrand() ).isEmpty() )	info.setStrand('+');
//		for (ReadFragmentByCigar element :info.getCigarElements()) {
//			String readSeq = element.getReadSeq();
//
////			if( info.getStrand() == '-')	readSeq = Utilities.getReverseComplementary( readSeq );
//
//			element.setReadSeq( readSeq );
//		}
		this.doCollapsingReadFromSAMRecord( map, info );
	}

	private boolean toCumulateForMatureMicroRnaFromBamFileByHairpinId( HairpinVO hairpinVo, boolean isRelative ) throws Exception {
		List<GroupSamInfo> list = new ArrayList<GroupSamInfo>(this.seqMap.get( hairpinVo.getId() ).values());

		return this.toCumulateForMatureMicroRnaFromBamFileByHairpinId(hairpinVo, list, isRelative);
////		List<MatureVO> matures = NGSFileReadCallable.matureMap.get( hairpin_id );
//		List<MatureVO> matures = this.matureMap.get( hairpinVo.getId() );
//
//		List<GroupSamInfo> list = new ArrayList<GroupSamInfo>(this.seqMap.get( hairpinVo.getId() ).values());
//		for( GroupSamInfo gsi:list ) {
////			int readStartPos	= gsi.getSamInfo().getStart();
////			int readEndPos		= gsi.getSamInfo().getEnd();
////
////	        int maxOverlapSize = 0;
//	        if( matures != null ) {
//		        for(int i=0; i<matures.size(); i++) {
//		        	MatureVO mature = matures.get(i);
////		        	int matureStart	= isRelative?mature.getStart():hairpinVo.getIntegerStart() + mature.getStart();
////		        	int matureEnd	= isRelative?mature.getEnd():hairpinVo.getIntegerStart() + mature.getEnd();
////	
////		        	// which mature in the premature is more overlapped?
////		        	int matureOverlapSize = Utilities.getLengthOfOverlap( matureStart, matureEnd, readStartPos, readEndPos );
////	
////		        	if( maxOverlapSize < matureOverlapSize ) {
////		        		String key = mature.getMirid();
////	
////		        		if( this.matureProfileMap.containsKey( key ) )	{
////		        			int cnt = this.matureProfileMap.get( key );
////		        			this.matureProfileMap.put( key, cnt+1 );
////		        		}else {
////		        			this.matureProfileMap.put( key, 1 );
////		        		}
////			        	maxOverlapSize = matureOverlapSize;
////		        	}
//		        	
//		        	String key = mature.getMirid();
//		        	if( this.matureProfileMap.containsKey( key ) ) {
//		        		int cnt = this.matureProfileMap.get( key ) + gsi.getSamInfo().getCount();
//
//		        		this.matureProfileMap.put(key, cnt );
//		        	}else {
//		        		this.matureProfileMap.put(key, gsi.getSamInfo().getCount() );
//		        	}
//		        }
//		        return true;
//	        }
//		}
//        return false;
	}

	private boolean toCumulateForMatureMicroRnaFromBamFileByHairpinId( HairpinVO hairpinVo, List<GroupSamInfo> list, boolean isRelative ) throws Exception {
//		List<MatureVO> matures = NGSFileReadCallable.matureMap.get( hairpin_id );
		List<MatureVO> matures = this.matureMap.get( hairpinVo.getId() );

		for( GroupSamInfo gsi:list ) {
//			int readStartPos	= gsi.getSamInfo().getStart();
//			int readEndPos		= gsi.getSamInfo().getEnd();
//
//	        int maxOverlapSize = 0;
	        if( matures != null ) {
		        for(int i=0; i<matures.size(); i++) {
		        	MatureVO mature = matures.get(i);
//		        	int matureStart	= isRelative?mature.getStart():hairpinVo.getIntegerStart() + mature.getStart();
//		        	int matureEnd	= isRelative?mature.getEnd():hairpinVo.getIntegerStart() + mature.getEnd();
//	
//		        	// which mature in the premature is more overlapped?
//		        	int matureOverlapSize = Utilities.getLengthOfOverlap( matureStart, matureEnd, readStartPos, readEndPos );
//	
//		        	if( maxOverlapSize < matureOverlapSize ) {
//		        		String key = mature.getMirid();
//	
//		        		if( this.matureProfileMap.containsKey( key ) )	{
//		        			int cnt = this.matureProfileMap.get( key );
//		        			this.matureProfileMap.put( key, cnt+1 );
//		        		}else {
//		        			this.matureProfileMap.put( key, 1 );
//		        		}
//			        	maxOverlapSize = matureOverlapSize;
//		        	}
		        	
		        	String key = mature.getMirid();
		        	if( this.matureProfileMap.containsKey( key ) ) {
		        		int cnt = this.matureProfileMap.get( key ) + gsi.getSamInfo().getCount();

		        		this.matureProfileMap.put(key, cnt );
		        	}else {
		        		this.matureProfileMap.put(key, gsi.getSamInfo().getCount() );
		        	}
		        }
		        return true;
	        }
		}
        return false;
	}
}
