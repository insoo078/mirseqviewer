package kobic.msb.server.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.util.MsvUtilities;
import kobic.msb.server.obj.MsvSamRecord;
import kobic.msb.server.obj.ReadObject;
import kobic.msb.server.obj.ReadQuality;
import kobic.msb.system.engine.MsbEngine;

public class ReadWithMatrix implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ReadObject 				read;
//	private String					keySequence;
	private Map<String, Double>		map;
	private Map<String, Double>		normMap;
	private int						misMatchedCount;
	private int						indexNo;


//	public ReadWithMatrix( String keySequence, Map<String, Double> map ) {
//		this( keySequence, map, new ReadQuality() );
//	}
	
	public ReadWithMatrix() {
		this.read = null;
		this.map = null;
		this.normMap = null;
		this.misMatchedCount = 0;
		this.indexNo = 0;
	}
	
	public ReadWithMatrix( Map<String, Double> map, MsvSamRecord info ) {
		this( map, new ReadQuality(), info );
	}

//	public ReadWithMatrix( String keySequence, Map<String, Double> map, int mapq, int nh ) {
//		this.keySequence	= keySequence;
//		this.read			= Utilities.translateFromSequenceStringToObject( this.keySequence );
//		this.map			= map;
//		this.normMap		= new HashMap<String, Double>();
//		
//		this.read.setNh( nh );
//		this.read.setMapq( mapq );
//	}
	
	public ReadWithMatrix( Map<String, Double> map, ReadQuality readQuality, MsvSamRecord info ) {
//		this.keySequence	= keySequence;
//		this.read			= Utilities.translateFromSequenceStringToObject( this.keySequence );
//		this.read			= Utilities.translateFromSequenceStringToObject( this.keySequence, info );

		this.read			= MsvUtilities.translateFromSequenceStringToObject( info );
		this.map			= map;
		this.normMap		= new HashMap<String, Double>();
		this.misMatchedCount	= info.getMismatchCount();
		
		this.read.setReadQuality( readQuality );
	}

	public ReadObject getReadObject() {
		return this.read;
	}
	
	public Map<String, Double> getCountData() {
		return this.map;
	}
	
	public Map<String, Double> getNormalizedCountData() {
		if( this.normMap == null )	this.normMap = new HashMap<String, Double>();
		return this.normMap;
	}
	
	public void setMisMatchedCount(int cnt) {
		this.misMatchedCount = cnt;
	}

	public int getMisMatchedCount() {
		return this.misMatchedCount;
	}

	public void setIndexNo(int indexNo){
		this.indexNo = indexNo;
	}

	public int getIndexNo() {
		return this.indexNo;
	}

//	public double[] getVectorData( Map<String, MsbRCTColumnModel> columnMap ) {
////		long a = System.currentTimeMillis();
//
//		int columnSize = MSBReadCountTableColumnStructureModel.getHeaderColumnSize( columnMap );
//		
//		List<Double> dlist = new ArrayList<Double>();
//		Iterator<String> iter = columnMap.keySet().iterator();
//		while( iter.hasNext() ) {
//			String columnName = iter.next();
//			MsbRCTColumnModel column = columnMap.get( columnName );
//			
//			if( column.getColumnType().equals( JMsbSysConst.TOTAL_SUM_HEADER_PREFIX ) ) {
//				Collection<Double> lst = (Collection<Double>) this.map.values();
//				Iterator<Double> iter2 = lst.iterator();
//				double sum = 0;
//				while( iter2.hasNext() ) {
//					Double val = iter2.next();
//					if( !val.equals( Double.NaN ) )	sum += val;
//				}
//				dlist.add( sum );
//			}else if(column.getColumnType().equals( JMsbSysConst.GROUP_SUM_HEADER_PREFIX ) ) {
//				List<MsbRCTColumnModel> list = columnMap.get( column.getGroup() ).getChildColumnList();
//				double sum = 0;
//				for(int i=0; i<list.size(); i++) {
//					MsbRCTColumnModel sampleColumn = list.get(i);
//					if( sampleColumn.getColumnType().equals( JMsbSysConst.SAMPLE_HEADER_PREFIX ) ) {
//						Double val = this.map.get( sampleColumn.getColumnId() );
//						if( !val.equals( Double.NaN) )	sum += val;
//					}
//				}
//				dlist.add( sum );
//			}else if( column.getColumnType().equals( JMsbSysConst.GROUP_HEADER_PREFIX ) ) {
//				List<MsbRCTColumnModel> list = columnMap.get( column.getColumnId() ).getChildColumnList();
//				for(int i=0; i<list.size(); i++) {
//					MsbRCTColumnModel sampleColumn = list.get(i);
//					if( sampleColumn.getColumnType().equals( JMsbSysConst.SAMPLE_HEADER_PREFIX ) ) {
//						double value = this.map.get( sampleColumn.getColumnId() );
//						dlist.add( value );
//					}else if( sampleColumn.getColumnType().equals( JMsbSysConst.GROUP_SUM_HEADER_PREFIX ) ) {
//						List<MsbRCTColumnModel> grouplist = columnMap.get( sampleColumn.getGroup() ).getChildColumnList();
//						double sum = 0;
//						for(int j=0; j<grouplist.size(); j++) {
//							MsbRCTColumnModel sampleColumn2 = grouplist.get(j);
//							if( sampleColumn2.getColumnType().equals( JMsbSysConst.SAMPLE_HEADER_PREFIX ) ) {
//								Double val = this.map.get( sampleColumn2.getColumnId() );
//								if( !val.equals( Double.NaN) )	sum += val;
//							}
//						}
//						dlist.add( sum );
//					}else if( sampleColumn.getColumnType().equals( JMsbSysConst.TOTAL_SUM_HEADER_PREFIX ) ) {
//						Collection<Double> lst = (Collection<Double>) this.map.values();
//						Iterator<Double> iter2 = lst.iterator();
//						double sum = 0;
//						while( iter2.hasNext() ) {
//							Double val = iter2.next();
//							if( !val.equals( Double.NaN) )	sum += val;
//						}
//						dlist.add( sum );
//					}
//				}
//			}
//		}
//
////		long b = System.currentTimeMillis();
////
////		System.out.println( (double)(b-a)/1000 + " call makingArray" );
//		
//		if( dlist.size() == columnSize ) {
//			double[] array = ArrayUtils.toPrimitive( dlist.toArray( new Double[ columnSize ] ) );
//		
//			return array;
//		}
//		return new double[columnSize];
//	}

	public static double[] getVectorData( Map<String, MsbRCTColumnModel> columnMap, Map<String, Double> hashMap ) {
//		long a = System.currentTimeMillis();

		int columnSize = MSBReadCountTableColumnStructureModel.getHeaderColumnSize( columnMap );
		
		List<Double> dlist = new ArrayList<Double>();
		Iterator<String> iter = columnMap.keySet().iterator();
		while( iter.hasNext() ) {
			String columnName = iter.next();
			MsbRCTColumnModel column = columnMap.get( columnName );
			
			if( column.getColumnType().equals( JMsbSysConst.TOTAL_SUM_HEADER_PREFIX ) ) {
				double sum = 0;

				Iterator<String> keySet = hashMap.keySet().iterator();
				while( keySet.hasNext() ) {
					String name = keySet.next();
					try {
						MsbRCTColumnModel cModel = MSBReadCountTableColumnStructureModel.getColumnModel( columnMap, name );
						if( cModel != null )	sum += hashMap.get( name );
					}catch(Exception e) {
						MsbEngine.logger.error("Error : ", e);
					}
				}

				dlist.add( sum );
			}else if(column.getColumnType().equals( JMsbSysConst.GROUP_SUM_HEADER_PREFIX ) ) {
				double sum = 0;
				if( columnMap.containsKey( column.getGroup() ) ) {
					List<MsbRCTColumnModel> list = columnMap.get( column.getGroup() ).getChildColumnList();
					for(int i=0; i<list.size(); i++) {
						MsbRCTColumnModel sampleColumn = list.get(i);
						if( sampleColumn.getColumnType().equals( JMsbSysConst.SAMPLE_HEADER_PREFIX ) ) {
							Double val = hashMap.get( sampleColumn.getColumnId() );
							if( !val.equals( Double.NaN) )	sum += val;
						}
					}
				}
				dlist.add( sum );
			}else if( column.getColumnType().equals( JMsbSysConst.GROUP_HEADER_PREFIX ) ) {
				List<MsbRCTColumnModel> list = columnMap.get( column.getColumnId() ).getChildColumnList();
				for(int i=0; i<list.size(); i++) {
					MsbRCTColumnModel sampleColumn = list.get(i);
					if( sampleColumn.getColumnType().equals( JMsbSysConst.SAMPLE_HEADER_PREFIX ) ) {
						double value = hashMap.get( sampleColumn.getColumnId() );
						dlist.add( value );
					}else if( sampleColumn.getColumnType().equals( JMsbSysConst.GROUP_SUM_HEADER_PREFIX ) ) {
						List<MsbRCTColumnModel> grouplist = columnMap.get( sampleColumn.getGroup() ).getChildColumnList();
						double sum = 0;
						for(int j=0; j<grouplist.size(); j++) {
							MsbRCTColumnModel sampleColumn2 = grouplist.get(j);
							if( sampleColumn2.getColumnType().equals( JMsbSysConst.SAMPLE_HEADER_PREFIX ) ) {
								Double val = hashMap.get( sampleColumn2.getColumnId() );
								if( !val.equals( Double.NaN) )	sum += val;
							}
						}
						dlist.add( sum );
					}else if( sampleColumn.getColumnType().equals( JMsbSysConst.TOTAL_SUM_HEADER_PREFIX ) ) {
						Collection<Double> lst = (Collection<Double>) hashMap.values();
						Iterator<Double> iter2 = lst.iterator();
						double sum = 0;
						while( iter2.hasNext() ) {
							Double val = iter2.next();
							if( !val.equals( Double.NaN) )	sum += val;
						}
						dlist.add( sum );
					}
				}
			}
		}

//		long b = System.currentTimeMillis();
//
//		System.out.println( (double)(b-a)/1000 + " call makingArray" );
		
		if( dlist.size() == columnSize ) {
			double[] array = ArrayUtils.toPrimitive( dlist.toArray( new Double[ columnSize ] ) );
		
			return array;
		}
		return new double[columnSize];
	}
}