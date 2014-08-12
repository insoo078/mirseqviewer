package kobic.com.method.mirna;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.samtools.BAMRecord;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecordIterator;

import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.io.file.obj.bed.BedFormat;
import kobic.msb.io.file.obj.bed.BedFragment;
import kobic.msb.server.obj.MsvSamRecord;
import kobic.msb.server.obj.NovelMiRnaObj;
import kobic.msb.server.obj.ReadQuality;
import kobic.msb.server.obj.SAMInfo;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class NovelMicroRnaFinding {
	private ProjectMapItem	projectMapItem;

	public NovelMicroRnaFinding( ProjectMapItem item ) {
		this.projectMapItem	= item;
	}
	
	public Map<String, NovelMiRnaObj> findingNovelMiRna( String sortFilePath, String indexFilePath ) throws Exception{
		BedFormat bedFormat = this.projectMapItem.getBedFormat();

    	String organism	= MsbEngine.getInstance().getOrganismMap().get( this.projectMapItem.getOrganism() );

    	Map<String, NovelMiRnaObj> nMiRnaMap = new LinkedHashMap<String, NovelMiRnaObj>();
		int index = 0;
		List<BedFragment> list = bedFormat.getFragmentList();
		Iterator<BedFragment> iterCollect = list.iterator();
		while( iterCollect.hasNext() ) {
			BedFragment fragment = iterCollect.next();

			// If novel miRNA range is over 130bp, miRseq Browser will skip the this region
			int diff = fragment.getChromEnd() - fragment.getChromStart();
			if( diff > JMsbSysConst.MAX_NOVEL_PREMATURE_RANGE || diff < JMsbSysConst.MIN_NOVEL_PREMATURE_RANGE  ) continue;

			String premature_id = JMsbSysConst.NOVEL_MICRO_RNA + (index++);
			fragment.setName( premature_id );
			if( Utilities.nulltoEmpty( fragment.getStrand() ).isEmpty() )	fragment.setStrand( "+" );

			List<MsvSamRecord> samList = this.getRemovedDupReads( sortFilePath, indexFilePath, fragment );
			NovelMiRnaObj nMiRna = new NovelMiRnaObj( organism, fragment.getChrom(), premature_id, fragment.getStrand(), fragment.getChromStart(), fragment.getChromEnd(), this.projectMapItem.getMiRBAseVersion() );
			nMiRna.addSamInfo( samList );

			nMiRnaMap.put( nMiRna.getNovelMicroRnaId(), nMiRna );
		}

		return nMiRnaMap;
	}

	private List<MsvSamRecord> getRemovedDupReads( String sortFilePath, String indexFilePath , BedFragment fragment ) throws Exception{
		SAMFileReader inputSam = new SAMFileReader( new File( sortFilePath ), new File( indexFilePath ) );
		inputSam.setValidationStringency( SAMFileReader.ValidationStringency.SILENT );
		
		Map<String, MsvSamRecord> rmDupMap = new LinkedHashMap<String, MsvSamRecord>();
		
		HairpinVO hairpinVo = new HairpinVO();
		hairpinVo.setChr( fragment.getChrom() );
		hairpinVo.setId( fragment.getName() );
		hairpinVo.setStrand( fragment.getStrand() );
		hairpinVo.setStart( Integer.toString( fragment.getChromStart() ) );
		hairpinVo.setEnd( Integer.toString( fragment.getChromEnd() ) );

		SAMRecordIterator samIter = inputSam.queryOverlapping( hairpinVo.getChr(), fragment.getChromStart(), fragment.getChromEnd() );
		while( samIter.hasNext() ) {
			BAMRecord record = (BAMRecord)samIter.next();

//			String premature_id	= name;
//	        String readName 	= record.getReadName();
////	        String readSeq		= strand.equals( "-" )?Utilities.getReverseComplementary( record.getReadString() ):record.getReadString();
//	        String readSeq		= strand.equals( "-" )?Utilities.getComplementary( record.getReadString() ):record.getReadString();
//	        // If sequence are aligned to genome, we have to change ralative position
//	        int nstart			= (int) (record.getAlignmentStart()	- start + 1);		// relative start
//	        int nend			= (int) (record.getAlignmentEnd()	- start + 1);		// relative end
//	        
//	        if( strand.equals( "-" ) ) {
//	        	nstart	= end - record.getAlignmentEnd() + 1;
//	        	nend	= end - record.getAlignmentStart() + 1;
//	        }
//	        
//	        ReadQuality readQual = new ReadQuality( record );
//
//	        SAMInfo info = new SAMInfo( premature_id, readName, readSeq, strand.charAt(0), nstart, nend, 1, readQual );
			MsvSamRecord readInfo = MsvSamRecord.getNewInstance( record, hairpinVo, JMsbSysConst.WITH_GENOME );
	
	        // To remove duplicate reads
//	        String key = info.getReadSeq() + "^" + info.getChr() + ":" + info.getStart() + "$" + info.getEnd();
			String key = readInfo.getPublicKey();
	        if( !rmDupMap.containsKey(key) )	rmDupMap.put(key, readInfo);
	        else{
	        	MsvSamRecord item = rmDupMap.get(key);
	        	item.setCount( item.getCount() + 1 );
	        }
		}
		samIter.close();
		inputSam.close();
		
		return new ArrayList<MsvSamRecord>( rmDupMap.values() );
	}
}