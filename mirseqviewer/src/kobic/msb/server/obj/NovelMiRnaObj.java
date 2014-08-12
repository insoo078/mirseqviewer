package kobic.msb.server.obj;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import kobic.com.network.HTTPRequester;
import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.system.engine.MsbEngine;

public class NovelMiRnaObj {
	private String	id;
	private String	organism;
	private String	chr;
	private String	strand;
	private int		start;
	private int		end;
	private String mirbaseVersion;

//	private List<SAMInfo> list;
	private List<MsvSamRecord> list;

	public NovelMiRnaObj( String organism, String chr, String id, String strand, int start, int end, String mirbaseVersion ) {
		this.id			= id;
		this.start		= start;
		this.end		= end;
		this.organism	= organism;
		this.chr		= chr;
		this.strand		= strand;
		this.mirbaseVersion = mirbaseVersion;

//		this.list = new ArrayList<SAMInfo>();
		this.list = new ArrayList<MsvSamRecord>();
	}
	
//	public List<NovelMiRnaObj> divideMicroRnas() {
//		List<Integer> pos = this.getCountList( JMsbSysConst.ABUNDANCE_FOR_NOVEL_MICRO_RNA );
//
//		List<NovelMiRnaObj> nNovelMiRnaList = new ArrayList<NovelMiRnaObj>();
//
//		int MAX_GAP_LENGTH = 5;
//
//		if( pos.size() > 0 ) {
//			int previous	= pos.get(0);
//			int last		= pos.get(0);
//	
//			int subIndex = 0;
//			for(int i=1; i<pos.size(); i++) {
//				int index = pos.get(i);
//				boolean cango = false;
//
//				if( last <= index && index-MAX_GAP_LENGTH <= last )	last = pos.get(i);
//				else												cango = true;
//				if( i == pos.size() - 1 )							cango = true;
//
//				if( cango ){
//					int range_start	= previous;
//					int range_end	= last;
//					int diff = range_end - range_start;
//	
//					if( diff <= JMsbSysConst.MAX_NOVEL_PREMATURE_RANGE && diff >= JMsbSysConst.MIN_NOVEL_PREMATURE_RANGE ) {
//						NovelMiRnaObj obj = new NovelMiRnaObj( this.organism, this.chr, id + "-" + subIndex, range_start, range_end );
//						
//						for(int j=0; j<this.list.size(); j++) {
//							SAMInfo info = this.list.get(j);
//							if( obj.isOverlapped( info.getStart(), info.getEnd() ) ){
//								info.setStart( info.getStart() - obj.getStart() );
//								info.setEnd( info.getEnd() - obj.getStart() );
//
//								obj.addSamInfo( info );
//							}
//						}
//						
//						nNovelMiRnaList.add( obj );
//		
//						previous = pos.get(i);
//						last = pos.get(i);
//						subIndex++;
//					}
//				}
//			}
//		}
//		
//		return nNovelMiRnaList;
//	}
//	
//	public List<Integer> getCountList( int MAX_DEPTH ) {
//		Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
//		for(int j=0; j<this.list.size(); j++) {
//			SAMInfo si = this.list.get(j);
//			int read_start	= si.getStart();
//			int read_end	= si.getEnd();
//
//			for(int i=this.start; i<=this.end; i++) {
//				if( i >= read_start && i <= read_end )	{
//					if( countMap.containsKey(i) )	countMap.put(i, countMap.get(i) + 1);
//					else							countMap.put(i, 1);
//				}
//			}
//		}
//
//		List<Integer> pos = new ArrayList<Integer>();
//		Iterator<Integer> keyIter = countMap.keySet().iterator();
//		while( keyIter.hasNext() ) {
//			Integer key = keyIter.next();
//			if( countMap.get(key) > MAX_DEPTH )	pos.add( key );
//		}
//		Collections.sort(pos);
//
//		return pos;
//	}
//
//	public void filterOutReadsOverlappedToHairpin( int start, int end ) {
//		List<SAMInfo> lst = new ArrayList<SAMInfo>();
//		for(int i=0; i<this.list.size(); i++) {
//			if( !Utilities.isOverlapped(this.list.get(i).getStart(), this.list.get(i).getEnd(), start, end) )	lst.add( this.list.get(i) );
//		}
//		this.list = lst;
//	}
//
//	public boolean isOverlapped( int contig_start, int contig_end ) {
//		if( Utilities.isOverlapped(this.start, this.end, contig_start, contig_end) )
//			return true;
//		
//		return false;
//	}
	
	public String getSequence( int start, int end, char strand ) {
		try {
			return HTTPRequester.getInstance().getReferenceSequence( this.organism, this.chr, start, end, strand, this.mirbaseVersion );
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error("Error : ", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error("Error : ", e);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error("Error : ", e);
		}
		return null;
	}

//	public void addSamInfo( List<SAMInfo> list ) {
//		if( this.list == null )	this.list = new ArrayList<SAMInfo>();
//		for(Iterator<SAMInfo> iter = list.iterator(); iter.hasNext();)
//			this.addSamInfo( iter.next() );
//	}
	
	public void addSamInfo( List<MsvSamRecord> list ) {
		if( this.list == null )	this.list = new ArrayList<MsvSamRecord>();
		for(Iterator<MsvSamRecord> iter = list.iterator(); iter.hasNext();)
			this.addSamInfo( iter.next() );
	}

//	public void addSamInfo( SAMInfo info ) {
//		if( this.list == null )	this.list = new ArrayList<SAMInfo>();
//
//		this.list.add(info);
//	}
	public void addSamInfo( MsvSamRecord info ) {
		if( this.list == null )	this.list = new ArrayList<MsvSamRecord>();

		this.list.add(info);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public String getChr() {
		return chr;
	}

	public void setChr(String chr) {
		this.chr = chr;
	}

	public String getStrand() {
		return strand;
	}

	public void setStrand(String strand) {
		this.strand = strand;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getNovelMicroRnaId() {
		return this.id;
	}
	
	public String getChromosome() {
		return this.chr;
	}
	
	public int getStart() {
		return this.start;
	}
	
	public int getEnd() {
		return this.end;
	}
	
	public void setNovelMicroRnaId(String id) {
		this.id = id;
	}
	
	public int getSize() {
		return this.list.size();
	}
	
//	public SAMInfo getSamInfoAt(int idx) {
//		return this.list.get(idx);
//	}
	public MsvSamRecord getSamInfoAt(int idx) {
		return this.list.get(idx);
	}
}
