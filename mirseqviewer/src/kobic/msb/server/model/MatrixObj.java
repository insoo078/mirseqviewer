package kobic.msb.server.model;

import java.awt.geom.RoundRectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import kobic.com.edgeR.model.CountDataModel;
import kobic.com.normalization.Normalization;
import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.SwingConst.Sorts;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.MsbFilterModel.FilterModel;
import kobic.msb.server.obj.GeneralReadObject;
import kobic.msb.server.obj.GenomeReferenceObject;
import kobic.msb.server.obj.GroupSamInfo;
import kobic.msb.server.obj.HairpinSequenceObject;
import kobic.msb.server.obj.MsvSamRecord;
import kobic.msb.server.obj.NucleotideObject;
import kobic.msb.server.obj.ReadFragmentByCigar;
import kobic.msb.server.obj.ReadObject;
import kobic.msb.server.obj.ReadQuality;
import kobic.msb.server.obj.SAMInfo;
import kobic.msb.server.obj.SequenceObject;
import kobic.msb.swing.comparator.ArrayComparator;
import kobic.msb.swing.comparator.ReadVectorComparator;
import kobic.msb.swing.comparator.ReadVectorEndPosComparator;
import kobic.msb.swing.panel.alignment.obj.AlignedNucleotide;
import kobic.msb.swing.panel.alignment.obj.AlignedReadSequence;
import kobic.msb.swing.panel.alignment.obj.GeneralAlignedReadSequence;
import kobic.msb.system.engine.MsbEngine;

@SuppressWarnings("unused")
public class MatrixObj implements java.io.Serializable, Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int												nRows;
	private int												nCols;
	private Map<String, Map<String, Map<String, Long>>>		sequenceMap;
	private Map<String, ReadQuality>						readQualityMap;
	private Map<String, MsvSamRecord>						readObjMap;
	private List<String>									groupList;
	private List<String>									sampleList;

	private MSBReadCountTableColumnStructureModel			currentColumnStructure;

	private List<ReadWithMatrix>							readVectorList;

	private Model											model;
	
	private double[][]										profile;
	
	public MatrixObj() {
		this.nRows			= 0;
		this.nCols			= 0;
		this.groupList		= new ArrayList<String>();
		this.sampleList		= new ArrayList<String>();

		this.readQualityMap	= new LinkedHashMap<String, ReadQuality>();
		this.readObjMap		= new LinkedHashMap<String, MsvSamRecord>();
		
		this.readVectorList	= new ArrayList<ReadWithMatrix>();
													//  Sequence,    Group,     Sample, Count
		this.sequenceMap	= new LinkedHashMap<String, Map<String, Map<String, Long>>>();
	}

	public MatrixObj( Model model ) {
		this();

		this.model					= model;
		this.currentColumnStructure	= this.model.getMSBReadCountTableColumnStructureModel();
	}

	private void initReadList(String strand) throws Exception{
		this.readVectorList.clear();
		this.readVectorList = this.getReadObjectWithCountData();
		
		// 2014 05 03 modified by Insu Jang
		this.readObjMap.clear();
		this.sequenceMap.clear();
		this.readQualityMap.clear();
		// 2014 05 03 modified by Insu Jang

		Comparator<ReadWithMatrix> comparator = new ReadVectorComparator( strand, ReadVectorComparator.PRIORITY_LEFT_SIDE );
		Collections.sort( this.readVectorList, comparator );
	}

	public void doSampleMap( List<GroupSamInfo> list, String strand ) throws Exception{
		this.sequenceMap.clear();
		this.groupList.clear();
		this.sampleList.clear();

		try {
			for( GroupSamInfo info : list ) {
				String key = info.getKey();
				MsvSamRecord samInfo = info.getSamInfo();
				this.makeSampleMap( key, info.getGroup(), info.getSample(), samInfo );
			}
		} catch(Exception e) {
			MsbEngine.logger.error( "Error", e );
		}

		this.initReadList( strand );
	}

	private void makeSampleMap( String sequence, String groupId, String sampleId, MsvSamRecord samInfo ) throws Exception {
		if( !this.groupList.contains( groupId ) )	this.groupList.add( groupId );
		if( !this.sampleList.contains( sampleId ) )	this.sampleList.add( sampleId );

		this.readQualityMap.put( sequence, samInfo.getReadQuality() );
		this.readObjMap.put( sequence, samInfo );

		if( this.sequenceMap.containsKey( sequence ) ) {
			if( this.sequenceMap.get( sequence ).containsKey( groupId ) ) {
				if( this.sequenceMap.get( sequence ).get( groupId ).containsKey( sampleId ) ) {
					
					long cnt = this.sequenceMap.get( sequence ).get( groupId ).get( sampleId ) + samInfo.getCount();
					this.sequenceMap.get( sequence ).get( groupId ).put( sampleId, cnt );
				}else {
					this.sequenceMap.get( sequence ).get( groupId ).put( sampleId, Long.valueOf( samInfo.getCount() ) );
				}
			}else {
				Map<String, Long> nSampleMap = new LinkedHashMap<String, Long>();
				nSampleMap.put( sampleId, Long.valueOf( samInfo.getCount() ) );

				this.sequenceMap.get( sequence ).put( groupId, nSampleMap );
			}
		}else {
			Map<String, Long> nSampleMap = new LinkedHashMap<String, Long>();
			nSampleMap.put( sampleId, Long.valueOf( samInfo.getCount() ) );
			
			Map<String, Map<String, Long>> nGroupMap = new LinkedHashMap<String, Map<String, Long>>();
			nGroupMap.put( groupId, nSampleMap );
			
			this.sequenceMap.put( sequence, nGroupMap );
		}
		this.updateDimension();
	}
	
	private void updateDimension() {
		this.nRows = this.sequenceMap.keySet().size();
		this.nCols = this.sampleList.size();
	}
	
	public int getRowsNumber() {
		return this.nRows;
	}
	
	public int getColsNumber() {
		return this.nCols;
	}
	
	public int getNumberOfGroups() {
		return this.groupList.size();
	}
	
	public int getNubmerOfSamples() {
		return this.sampleList.size();
	}

	/******************************************************************************************************
	 * To get GroupNames and SampleNames
	 * 
	 * @param cStructureModel column structure model
	 * @return String[][] String[0] : group names, String[1] : sample names
	 */
	private static String[][] getNamesAboutGroupAndSample(Map<String, MsbRCTColumnModel> cStructureModel) {
		List<String> groups = new ArrayList<String>();
		List<String> samples = new ArrayList<String>();
		Iterator<MsbRCTColumnModel> iter = cStructureModel.values().iterator();
		while( iter.hasNext() ) {
			MsbRCTColumnModel model = iter.next();
			if( model.getColumnType().equals( JMsbSysConst.GROUP_HEADER_PREFIX ) ) {
				List<String> columnId = model.getChildColumnIdList();
				for(int i=0; i<columnId.size(); i++) {
					groups.add( model.getColumnId() );
					samples.add( columnId.get(i) );
				}
			}
		}

		String[] strGrpArray = new String[ groups.size() ];		// Group(Normal, Normal, Normal, Tumour, Tumour, Tumour)
		String[] strSmpArray = new String[ samples.size() ];	// Sample(Pat1N, Pat3N, Pat4N, Pat1T, Pat3T, Pat4T)

		String[][] ret = new String[2][];
		ret[0] = groups.toArray( strGrpArray );
		ret[1] = samples.toArray( strSmpArray );
		
		return ret;
	}

	public void normalizeReadProfile( String normalizationMethod, double imputeVal ) {
		Map<String, MsbRCTColumnModel> cStructureModel = this.currentColumnStructure.getHeatMapColumnStructure();
		String[][] groups = MatrixObj.getNamesAboutGroupAndSample( cStructureModel );

		double[][] mat = new double[ this.readVectorList.size() ][]; 
		Iterator<ReadWithMatrix> iter = this.readVectorList.iterator();
		int idx = 0;
		while( iter.hasNext() ) {
			ReadWithMatrix matrix = iter.next();
			mat[idx++] = Utilities.toConvertFromObjectToPrimitiveArray( matrix.getCountData().values().toArray() );
		}

		try {
			/************************************************************************************************
			 * To Create CountDataModel and ClusterModel
			 */
			CountDataModel countDataModel = new CountDataModel( Utilities.toObject( mat ), groups[0], null, null );
			ClusterModel clusterModel = new ClusterModel( countDataModel );
			
			double[][] tmp = clusterModel.getOriginalData();
			
			/************************************************************************************************
			 * To Impute Missing value in the Count Table
			 */
			for(int i=0; i<tmp.length; i++) {
				for(int j=0; j<tmp[0].length; j++) {
					if( Double.isNaN( tmp[i][j] ) )	{
						tmp[i][j] = imputeVal;
					}
				}
			}
			clusterModel.setOriginalData( tmp );

			/************************************************************************************************
			 * To Normalize the Count Table
			 */
			double[][] result = Normalization.doNormalize( normalizationMethod, clusterModel, this.model.getProjectMapItem().getMiRBAseVersion() );

			iter = this.readVectorList.iterator();
			idx = 0;
			if( result != null ) {
				while( iter.hasNext() ) {
					ReadWithMatrix matrix = iter.next();
					for(int j=0; j<groups[1].length; j++) {
						matrix.getNormalizedCountData().put( groups[1][j], result[idx][j] );
					}
					idx++;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog( null, "There is a normalization problem, You should change the normalization method");
			MsbEngine.logger.error( "error : ", e );
//			e.printStackTrace();
		}
	}

	public double[][] getExpressionProfile( boolean isNormalized ) {
//		return this.getExpressionProfile( this.filter, this.operator, this.query, isNormalized );
		return this.getExpressionProfile( this.model.getProjectMapItem().getMsbFilterModel(), isNormalized );
	}

	private double[][] getExpressionProfile( String filter, String operator, String query, boolean isNormalized ) {
//		List<ReadWithMatrix> resultList = MatrixObj.getFilterOutResultByQuery( this.readVectorList, this.model.getReferenceSequenceObject(), this.currentColumnStructure.getHeatMapColumnStructure(), filter, operator, query, isNormalized );
		List<ReadWithMatrix> resultList = MatrixObj.getFilterOutResultByQuery( this.readVectorList, this.model.getPrematureSequenceObject(), this.model.getReferenceSequenceObject(), this.currentColumnStructure.getHeatMapColumnStructure(), this.model.getProjectMapItem().getMsbFilterModel(), isNormalized );
	
		return MatrixObj.getFinalExpressionProfile( resultList, this.currentColumnStructure.getHeatMapColumnStructure(), isNormalized );
	}

	private double[][] getExpressionProfile( MsbFilterModel filterModel, boolean isNormalized ) {
//		List<ReadWithMatrix> resultList = MatrixObj.getFilterOutResultByQuery( this.readVectorList, this.model.getReferenceSequenceObject(), this.currentColumnStructure.getHeatMapColumnStructure(), filter, operator, query, isNormalized );
		List<ReadWithMatrix> resultList = MatrixObj.getFilterOutResultByQuery( this.readVectorList, this.model.getPrematureSequenceObject(), this.model.getReferenceSequenceObject(), this.currentColumnStructure.getHeatMapColumnStructure(), filterModel, isNormalized );
	
		return MatrixObj.getFinalExpressionProfile( resultList, this.currentColumnStructure.getHeatMapColumnStructure(), isNormalized );
	}

	private static double[][] getFinalExpressionProfile( List<ReadWithMatrix> resultList, Map<String, MsbRCTColumnModel> currentColumnStructure, boolean isNormalized ) {
		try {
			if( resultList.size() > 0 ) {
				int nRows = resultList.size() + 1;
				double[][] mat = new double[ nRows ][ MSBReadCountTableColumnStructureModel.getHeaderColumnSize( currentColumnStructure ) ];
	
				int currentRow = 0;
				for( ReadWithMatrix obj : resultList ) {
					if( isNormalized )	mat[ currentRow++ ] = ReadWithMatrix.getVectorData( currentColumnStructure, obj.getNormalizedCountData() );
					else				mat[ currentRow++ ] = ReadWithMatrix.getVectorData( currentColumnStructure, obj.getCountData() );
				}
	
				// total column sum
				double[] colSumVector = new double[mat[0].length];
		
				for(int i=0; i<mat[0].length; i++) {
					long sum = 0;
					for(int j=0; j<currentRow; j++) {
						sum += mat[j][i];
					}
					colSumVector[i] = sum;
				}
				mat[currentRow] = colSumVector;
				
				return mat;
			}
		}catch(Exception e) {
//			e.printStackTrace();
			MsbEngine.logger.error( "Error", e );
		}

		return null;
	}

	private List<ReadWithMatrix> getReadObjectWithCountData() throws Exception{
		List<ReadWithMatrix> matrixObjList = new ArrayList<ReadWithMatrix>();

//		MsbEngine.logger.debug( "read count table object from Sequence Object" );
		try {
			// 1: sequence
			for( Iterator<String> iterSeq = this.sequenceMap.keySet().iterator(); iterSeq.hasNext(); ) {
				String sequence = iterSeq.next();
	
				Map<String, Double>	sumMap = new LinkedHashMap<String, Double>();

				// 2: group
				for(Iterator<String> iterGrp = this.currentColumnStructure.getHeatMapColumnStructure().keySet().iterator(); iterGrp.hasNext(); ) {
					String 		originalKey		= iterGrp.next();
					MsbRCTColumnModel column	= this.currentColumnStructure.getHeatMapColumnStructure().get( originalKey );
					String		groupId			= column.getColumnId();
					String		type			= column.getColumnType();
	
					// IF sequence is exist, get count data or not then all record values are setting to 0
					if( this.sequenceMap.get( sequence ).containsKey( groupId ) ) {
						// 3: sample
						List<String> subColumnList = column.getChildColumnIdList();
						
						for(Iterator<String> iterator = subColumnList.iterator(); iterator.hasNext(); ) {
							String subColumnName = iterator.next();
							
							if( this.sequenceMap.get(sequence).get( groupId ).containsKey( subColumnName ) ) {
								double value = this.sequenceMap.get( sequence ).get( groupId ).get( subColumnName );
								sumMap.put( subColumnName, value );
							}else {
								sumMap.put( subColumnName, JMsbSysConst.MISSING_VALUE );
							}
						}
					}else {
						// Insu jang 20130529 modified
						if( type.equals( JMsbSysConst.GROUP_HEADER_PREFIX ) ) {
							for(Iterator<String> iterSmp = this.currentColumnStructure.getHeatMapColumnStructure().get(groupId).getChildColumnIdList().iterator(); iterSmp.hasNext(); ) {
								String sampleId = iterSmp.next();
								sumMap.put( sampleId, JMsbSysConst.MISSING_VALUE );
							}
						}
					}
				}

				ReadWithMatrix newObj = new ReadWithMatrix( sumMap, this.readQualityMap.get(sequence), this.readObjMap.get(sequence) );

				matrixObjList.add( newObj );
//				MsbEngine.logger.debug( "Record " + matrixObjList.size() );
			}
		}catch(Exception e) {
//			e.printStackTrace();
			MsbEngine.logger.error( "Error", e );
		}

		return matrixObjList;
	}

	public List<ReadObject> getReadList() {
//		return this.getReadList( this.filter, this.operator, this.query );
		return this.getReadList( this.model.getProjectMapItem().getMsbFilterModel() );
	}
	
	public ReadWithMatrix getReadWithMatrixList( MsbFilterModel filterModel, int index ) {
//		List<ReadWithMatrix> resultList = MatrixObj.getFilterOutResultByQuery( this.readVectorList, this.model.getReferenceSequenceObject(), this.currentColumnStructure.getHeatMapColumnStructure(), filter, operator, query, this.model.isNormalized() );
		List<ReadWithMatrix> resultList = MatrixObj.getFilterOutResultByQuery( this.readVectorList, this.model.getPrematureSequenceObject(), this.model.getReferenceSequenceObject(), this.currentColumnStructure.getHeatMapColumnStructure(), filterModel, this.model.isNormalized() );
		
		return resultList.get(index);
	}
	
	public ReadWithMatrix getReadWithMatrixList( int index ) {
		return this.getReadWithMatrixList( this.model.getProjectMapItem().getMsbFilterModel(), index );
	}

	public List<ReadObject> getReadList( String filter, String operator, String query ) {
//		List<ReadWithMatrix> resultList = MatrixObj.getFilterOutResultByQuery( this.readVectorList, this.model.getReferenceSequenceObject(), this.currentColumnStructure.getHeatMapColumnStructure(), filter, operator, query, this.model.isNormalized() );
		List<ReadWithMatrix> resultList = MatrixObj.getFilterOutResultByQuery( this.readVectorList, this.model.getPrematureSequenceObject(), this.model.getReferenceSequenceObject(), this.currentColumnStructure.getHeatMapColumnStructure(), this.model.getProjectMapItem().getMsbFilterModel(), this.model.isNormalized() );

		return MatrixObj.getOnlyReadList( resultList );
	}
	
	public List<ReadObject> getReadList( MsbFilterModel filterModel ) {
//		List<ReadWithMatrix> resultList = MatrixObj.getFilterOutResultByQuery( this.readVectorList, this.model.getReferenceSequenceObject(), this.currentColumnStructure.getHeatMapColumnStructure(), filter, operator, query, this.model.isNormalized() );
		List<ReadWithMatrix> resultList = MatrixObj.getFilterOutResultByQuery( this.readVectorList, this.model.getPrematureSequenceObject(), this.model.getReferenceSequenceObject(), this.currentColumnStructure.getHeatMapColumnStructure(), filterModel, this.model.isNormalized() );

		return MatrixObj.getOnlyReadList( resultList );
	}
	
	private static List<ReadObject> getOnlyReadList( List<ReadWithMatrix> resultList ) {
		List<ReadObject> list = new ArrayList<ReadObject>();

		if( resultList != null ) {
			for( ReadWithMatrix obj : resultList )
				list.add( obj.getReadObject() );
		}
		
		return list;
	}
	
	// 0 -> 5
	// 5 -> 0
	public List<ReadObject> getReadList( int from, int to ) {
		List<ReadWithMatrix> resultList = this.readVectorList;
		try {
			Collections.swap( resultList, from, to );
			this.readVectorList = resultList;
		}catch(Exception e) {
			MsbEngine.logger.error( "Error", e );
		}

		return this.getReadList();
	}
	
	public double maxCountValue() {
		Iterator<ReadWithMatrix> iter = this.readVectorList.iterator();
		double max = -99999;
		while( iter.hasNext() ) {
			ReadWithMatrix rwm = iter.next();

			double localMax = 0;
			if( this.model.isNormalized() )	localMax = Collections.max( rwm.getNormalizedCountData().values() );
			else							localMax = Collections.max( rwm.getCountData().values() );
			if( max < localMax )	max = localMax;
		}
		return max;
	}

	public double minCountValue() {
		Iterator<ReadWithMatrix> iter = this.readVectorList.iterator();
		double min = 99999999999d;
		while( iter.hasNext() ) {
			ReadWithMatrix rwm = iter.next();

			double localMin = 0;
			if( this.model.isNormalized() )	localMin = Collections.min( rwm.getNormalizedCountData().values() );
			else							localMin = Collections.min( rwm.getCountData().values() );
			if( min > localMin )	min = localMin;
		}
		return min;
	}

	public void removeRecord( int pos ) {
		this.readVectorList.remove( pos );
	}

	public ReadWithMatrix getRecord( int pos ) {
		return this.readVectorList.get( pos );
	}

	public void addRecord( ReadWithMatrix addItem ) {
		this.readVectorList.add( addItem );
	}
	
	public void addRecord( ReadWithMatrix addItem, int pos ) {
		this.readVectorList.add( pos, addItem );
	}

	public List<ReadWithMatrix> getReadVectorListByFiltered() {
//		return MatrixObj.getFilterOutResultByQuery( this.readVectorList, this.model.getReferenceSequenceObject(), this.currentColumnStructure.getHeatMapColumnStructure(), this.filter, this.operator, this.query, this.model.isNormalized() );
		return MatrixObj.getFilterOutResultByQuery( this.readVectorList, this.model.getPrematureSequenceObject(), this.model.getReferenceSequenceObject(), this.currentColumnStructure.getHeatMapColumnStructure(), this.model.getProjectMapItem().getMsbFilterModel(), this.model.isNormalized() );
	}

	public void sortReadPosition() {
		MsbSortModel sortModel = this.model.getProjectMapItem().getMsbSortModel();
		sortModel.clear();
		
		String strand = Character.toString(this.model.getReferenceSequenceObject().getStrand());

		Collections.sort( this.readVectorList, new ReadVectorComparator( strand, ReadVectorComparator.PRIORITY_LEFT_SIDE) );
	}
	
	public void sortReadEndPosition() {
		MsbSortModel sortModel = this.model.getProjectMapItem().getMsbSortModel();
		sortModel.clear();
		
		String strand = Character.toString(this.model.getReferenceSequenceObject().getStrand());

		Collections.sort( this.readVectorList, new ReadVectorComparator( strand, ReadVectorComparator.PRIORITY_RIGHT_SIDE) );
	}

	public void sortReadCountAt( int pos, int direction ) {
		try {
	    	if( pos >= 0 ) {
	    		MsbSortModel sortModel = this.model.getProjectMapItem().getMsbSortModel();
	    		sortModel.clear();
	    		
	    		String columnName = this.model.getMSBReadCountTableColumnStructureModel().getHeatMapHeader().get(pos);
	    		sortModel.addSortModel( 0, columnName, direction );
	    		
	    		this.sortReadCountBySortModel( sortModel );
////	    		double[][] matrix = this.getExpressionProfile( null, null, null );
//	    		double[][] matrix = MatrixObj.getFinalExpressionProfile( this.readVectorList, this.currentColumnStructure.getHeatMapColumnStructure(), this.model.isNormalized() );
//
//	    		List<Double> list = new ArrayList<Double>();
//	    		for(int i=0; i<matrix.length-1; i++) {
//	    			list.add( matrix[i][pos] );
//	    		}
//	
//	    		Integer[]	indices	= new Integer[ list.size() ];
//	    		Double[]	data	= new Double[ list.size() ];
//	    		list.toArray( data );
//	    		for(int i=0; i<indices.length; i++)	indices[i] = i;
//
//	    		Utilities.SortWithIndex( data, indices, direction );
//	
//	    		List<ReadWithMatrix> tmpList = new ArrayList<ReadWithMatrix>();
//	    		for(int i=0; i<indices.length; i++) {
//	    			tmpList.add( this.readVectorList.get( indices[i] ) );
//	    		}
//	    		this.readVectorList.clear();
//	    		this.readVectorList = tmpList;
	    	}
		}catch(Exception e) {
			MsbEngine.logger.error( "Error", e );
		}
	}
	
	public void sortReadCountBySortModel( MsbSortModel model ) throws Exception {
		if( model != null && model.size() > 0 ) {
    		double[][] matrix = MatrixObj.getFinalExpressionProfile( this.readVectorList, this.currentColumnStructure.getHeatMapColumnStructure(), this.model.isNormalized() );

			Double[][] nArray = new Double[matrix.length][matrix[0].length+1];

			for(int i=0; i<matrix.length; i++) {
				nArray[i][0] = (double)i;
				for(int j=0; j<matrix[i].length; j++)
					nArray[i][j+1] = matrix[i][j];
			}
    		
//    		List<String> columnList = new ArrayList<String>(this.currentColumnStructure.getHeatMapColumnStructure().keySet());
			List<String> columnList = this.currentColumnStructure.getHeatMapHeader();

    		ArrayComparator ac = new ArrayComparator();
    		for(int i=0; i<model.size(); i++) {
    			SortModel smodel = model.getSortModel(i);
    			int index = columnList.indexOf( smodel.getColumn() );

    			if( Sorts.LARGEST_TO_SMALLEST.getValue() == smodel.getDirection() )	ac.addSortCol( index+1, "desc" );
    			else																ac.addSortCol( index+1, "asc" );
    		}
    		ac.sort( nArray );
    		
    		RealMatrix realMatrix = MatrixUtils.createRealMatrix( Utilities.toConvertFromDoubleToPrimitiveArray( nArray ) );
    		double[] indices = realMatrix.getColumn(0);
    		
    		List<ReadWithMatrix> tmpList = new ArrayList<ReadWithMatrix>();
    		for(int i=0; i<indices.length; i++) {
    			if( (int)indices[i] < this.readVectorList.size() )	tmpList.add( this.readVectorList.get( (int)indices[i] ) );
    		}
    		this.readVectorList.clear();
    		this.readVectorList = tmpList;
		}
	}

	private static List<ReadWithMatrix> getSequenceBy( List<ReadWithMatrix> readVectorList, String operator, String sequence ) {
		List<ReadWithMatrix> newReadVectorList = new ArrayList<ReadWithMatrix>();

		Iterator<ReadWithMatrix> iter = readVectorList.iterator();
		while( iter.hasNext() ) {
			ReadWithMatrix obj = iter.next();

//			if( operator.equals("start with") ){
//				if( obj.getReadObject().getSequenceByString().startsWith( sequence ) )	{	newReadVectorList.add( obj );	}
//			}else if( operator.equals("end with") ){
//				if( obj.getReadObject().getSequenceByString().endsWith( sequence) )		{	newReadVectorList.add( obj );	}
//			}else if( operator.equals("equal") ) {
//				if( obj.getReadObject().getSequenceByString().equals( sequence ) )		{	newReadVectorList.add( obj );	}
//			}
			if( MatrixObj.isContainSequence( obj, operator, sequence) )	newReadVectorList.add( obj );
		}
		return newReadVectorList;
	}
	
	private static boolean isContainSequence(ReadWithMatrix readInfo, String operator, String sequence) {
		ReadObject ro = readInfo.getReadObject();
		
		String refSeq = "";
		if( ro instanceof GeneralReadObject) {
			GeneralReadObject gro = (GeneralReadObject)ro;
			
			Iterator<ReadObject> iter = gro.getRecordElements().iterator();
			while( iter.hasNext() ) {
				ReadObject tro = iter.next();
				
				refSeq += tro.getSequenceByString();
			}
		}else {
			refSeq = readInfo.getReadObject().getSequenceByString().toUpperCase();
		}
		String seq = sequence.toUpperCase();
		if( operator.equals("start with") ){
			if( refSeq.startsWith( seq ) )		{	return true;	}
		}else if( operator.equals("end with") ){
			if( refSeq.endsWith( seq) )			{	return true;	}
		}else if( operator.equals("equal") ) {
			if( refSeq.equals( seq ) )			{	return true;	}
		}

		return false;
	}

	private static boolean isContainOk( String operator, double value, long cutoff ) {
		if( operator.equals("<") ) {
			if( value < cutoff )				return true;
		}else if( operator.equals("<=") ) {
			if( value <= cutoff ) 				return true;
		}else if( operator.equals("=") ) {
			if( value == cutoff )				return true;
		}else if( operator.equals(">") ) {
			if( value > cutoff )				return true;
		}else if( operator.equals(">=") ) {
			if( value >= cutoff )				return true;
		}
		return false;
	}

	private static int getFilterByMismatchedCount(ReadWithMatrix readInfo, HairpinSequenceObject prematureSeq, GenomeReferenceObject refSeq, String operator) throws Exception {
		List<NucleotideObject> lstPrematureRef	= prematureSeq.getSequence();					// premature sequence
		
		int misMatchedCount = 0;
		GeneralReadObject gro = (GeneralReadObject)readInfo.getReadObject();
//		for(ReadFragmentByCigar cigar:gro.getMsvSamRecord().getCigarElements() ) {
		for(ReadObject read:gro.getRecordElements()) {
//			AlignedReadSequence readAlignedToGenomeSequence	= new AlignedReadSequence( gro.getReadQuality().getOrientation(), cigar.getStart(), cigar.getEnd() );
//			ReadObject read = new ReadObject( cigar.getStart(), cigar.getEnd(), cigar.getReadSeq(), refSeq.getStrand() );

//			for(int i=0; i<read.getSequence().size(); i++) {
//				NucleotideObject nObj = read.getSequence().get(i);
//				int newPos = SwingUtilities.getGenomePos( refSeq.getSequence(), nObj.getPosition() );
//
//				if( newPos >= 0 && newPos < refSeq.getSequence().size() ) {
//					NucleotideObject refNT = refSeq.getSequence().get( newPos );
//					
//					if( nObj.getNucleotideType().equals( refNT.getNucleotideType() ) )							misMatchedCount+=0;
//					else if( nObj.getNucleotideType().equals("U") && refNT.getNucleotideType().equals("T") )	misMatchedCount+=0;
//					else if( nObj.getNucleotideType().equals("T") && refNT.getNucleotideType().equals("U") )	misMatchedCount+=0;
//					else																						{
//						misMatchedCount++;
//					}
//				}
//			}
			misMatchedCount += MatrixObj.getMismatchedCount( read, refSeq );
		}

		return misMatchedCount;
	}

	private static boolean isFilterByMismatchedData(ReadWithMatrix readInfo, HairpinSequenceObject prematureSeq, GenomeReferenceObject refSeq, String operator, long count) throws Exception {
		int misMatchedCount = MatrixObj.getFilterByMismatchedCount(readInfo, prematureSeq, refSeq, operator);

		return MatrixObj.isContainOk( operator, misMatchedCount, count );
	}

	private static boolean isFilterByMismatchedBetweenData(ReadWithMatrix readInfo, HairpinSequenceObject prematureSeq, GenomeReferenceObject refSeq, String operator, long start, long end) throws Exception {			
		int misMatchedCount = MatrixObj.getFilterByMismatchedCount( readInfo, prematureSeq, refSeq, operator );

		if( misMatchedCount >= start && misMatchedCount <= end )
			return true;
		
		return false;
	}

	public static int getMismatchedCount( ReadObject readInfo, GenomeReferenceObject refSeq ) throws Exception {			
		List<NucleotideObject> lstRef = refSeq.getSequence();
		List<NucleotideObject> lst = readInfo.getSequence();
		int misMatchedCount = 0;
		
		for(int i=0; i<readInfo.getSequence().size(); i++) {
			NucleotideObject nObj = readInfo.getSequence().get(i);
			int newPos = SwingUtilities.getGenomePos( refSeq.getSequence(), nObj.getPosition() );

			if( newPos >= 0 && newPos < refSeq.getSequence().size() ) {
				NucleotideObject refNT = refSeq.getSequence().get( newPos );
				
				if( nObj.getNucleotideType().equals( refNT.getNucleotideType() ) )							misMatchedCount+=0;
				else if( nObj.getNucleotideType().equals("U") && refNT.getNucleotideType().equals("T") )	misMatchedCount+=0;
				else if( nObj.getNucleotideType().equals("T") && refNT.getNucleotideType().equals("U") )	misMatchedCount+=0;
				else																						{
					misMatchedCount++;
				}
			}
		}

//		for(int i=0; i<lst.size(); i++) {
//			String readNuc	= lst.get(i).getNucleotideType();
//			String refNuc	= lstRef.get( lst.get(i).getPosition() - 1 ).getNucleotideType();
//			if( (readNuc.toUpperCase().equals("U") && refNuc.toUpperCase().equals("T")) || (readNuc.toUpperCase().equals("T") && refNuc.toUpperCase().equals("U")) )
//				continue;
//			if( !readNuc.toUpperCase().equals( refNuc.toUpperCase() ) )	misMatchedCount++;
//		}
		return misMatchedCount;
	}

	private static List<ReadWithMatrix> getFilterByCountData(List<ReadWithMatrix> readVectorList, Map<String, MsbRCTColumnModel> columnStructure, String operator, long count, boolean isNormalized) throws Exception {
		List<ReadWithMatrix> newReadVectorList = new ArrayList<ReadWithMatrix>();
		
		int indexNo = 0;
		Iterator<ReadWithMatrix> iter = readVectorList.iterator();
		while( iter.hasNext() ) {
			ReadWithMatrix obj = iter.next();
			obj.setIndexNo( indexNo );

//			Map<String, Double> nMap = obj.getCountData();
//			if( isNormalized )	nMap = obj.getNormalizedCountData();
//			
//			Iterator<String> iterSampleIter = nMap.keySet().iterator();
//			while( iterSampleIter.hasNext() ) {
//				String columnName = iterSampleIter.next();
//				Double val = nMap.get( columnName );
//				
//				MsbRCTColumnModel column = MSBReadCountTableColumnStructureModel.getColumnModel( columnStructure, columnName );
//				if( column != null ) {
//					if( MatrixObj.isContainOk( operator, val, count ) ) {
//						newReadVectorList.add( obj );
//						break;
//					}
//				}
//			}
			if( MatrixObj.isFilterByCountData(obj, columnStructure, operator, count, isNormalized) ) {
				newReadVectorList.add( obj );
			}

//			Collection<Double> collection = obj.getCountData().values();
//			Iterator<Double> iterSampleIter = collection.iterator();
//			while( iterSampleIter.hasNext() ) {
//				if( MatrixObj.isContainOk( operator, iterSampleIter.next(), count ) ) {
//					newReadVectorList.add( obj );
//					break;
//				}
//			}
			indexNo++;
		}
		return MatrixObj.trimmedNullSequences( newReadVectorList, columnStructure );
//		return newReadVectorList;
	}


	private static boolean isFilterByCountData(ReadWithMatrix readInfo, Map<String, MsbRCTColumnModel> columnStructure, String operator, long count, boolean isNormalized) throws Exception {
		Map<String, Double> nMap = readInfo.getCountData();
		if( isNormalized )	nMap = readInfo.getNormalizedCountData();
		
		Iterator<String> iterSampleIter = nMap.keySet().iterator();
		while( iterSampleIter.hasNext() ) {
			String columnName = iterSampleIter.next();
			Double val = nMap.get( columnName );
			
			MsbRCTColumnModel column = MSBReadCountTableColumnStructureModel.getColumnModel( columnStructure, columnName );
			if( column != null ) {
				if( MatrixObj.isContainOk( operator, val, count ) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static List<ReadWithMatrix> getFilterByCountData(List<ReadWithMatrix> readVectorList, Map<String, MsbRCTColumnModel> columnStructure, String operator, long start, long end, boolean isNormalized) throws Exception {
		List<ReadWithMatrix> newReadVectorList = new ArrayList<ReadWithMatrix>();

		int indexNo = 0;
		Iterator<ReadWithMatrix> iter = readVectorList.iterator();
		while( iter.hasNext() ) {
			ReadWithMatrix obj = iter.next();
			obj.setIndexNo( indexNo );
			
//			Map<String, Double> nMap = obj.getCountData();
//			if( isNormalized )	nMap = obj.getNormalizedCountData();
//			
//			Iterator<String> iterSampleIter = nMap.keySet().iterator();
//			while( iterSampleIter.hasNext() ) {
//				String columnName = iterSampleIter.next();
//				Double val = nMap.get( columnName );
//				
//				MsbRCTColumnModel column = MSBReadCountTableColumnStructureModel.getColumnModel( columnStructure, columnName );
//				if( column != null ) {
//					if( operator.equals("between") ) {
//						if( val >= start && val <= end ) {
//							newReadVectorList.add( obj );
//							break;
//						}
//					}
//				}
//			}
			if( MatrixObj.isFilterByCountData( obj, columnStructure, operator, start, end, isNormalized ) ) {
				newReadVectorList.add( obj );
			}

//			Collection<Double> collection = obj.getCountData().values();
//			Iterator<Double> iterSampleIter = collection.iterator();
//			while( iterSampleIter.hasNext() ) {
//				if( operator.equals("between") ) {
//					double value = iterSampleIter.next();
//					if( value >= start && value <= end ) {
//						newReadVectorList.add( obj );
//						break;
//					}
//				}
//			}
			indexNo++;
		}
		
		return MatrixObj.trimmedNullSequences( newReadVectorList, columnStructure );
//		return newReadVectorList;
	}
	
	private static boolean isFilterByCountData(ReadWithMatrix readInfo, Map<String, MsbRCTColumnModel> columnStructure, String operator, long start, long end, boolean isNormalized) throws Exception {			
		Map<String, Double> nMap = readInfo.getCountData();
		if( isNormalized )	nMap = readInfo.getNormalizedCountData();
		
		Iterator<String> iterSampleIter = nMap.keySet().iterator();
		while( iterSampleIter.hasNext() ) {
			String columnName = iterSampleIter.next();
			Double val = nMap.get( columnName );
			
			MsbRCTColumnModel column = MSBReadCountTableColumnStructureModel.getColumnModel( columnStructure, columnName );
			if( column != null ) {
				if( operator.equals("between") ) {
					if( val >= start && val <= end ) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static List<ReadWithMatrix> getFilterByStartPointData(List<ReadWithMatrix> readVectorList, String operator, long count) throws Exception{
		List<ReadWithMatrix> newReadVectorList = new ArrayList<ReadWithMatrix>();

		int indexNo = 0;
		Iterator<ReadWithMatrix> iter = readVectorList.iterator();
		while( iter.hasNext() ) {
			ReadWithMatrix obj = iter.next();
			obj.setIndexNo( indexNo );

//			if( MatrixObj.isContainOk( operator, obj.getReadObject().getStartPosition(), count) ) {
			if( MatrixObj.isFilterByStartPointData( obj, operator, count ) ) {
				newReadVectorList.add( obj );
			}
			indexNo++;
		}
		return newReadVectorList;
	}
	
	private static boolean isFilterByStartPointData(ReadWithMatrix readInfo, String operator, long count) throws Exception{
		if( MatrixObj.isContainOk( operator, readInfo.getReadObject().getStartPosition(), count) )
			return true;
		
		return false;
	}

	private static List<ReadWithMatrix> getFilterByStartPointData(List<ReadWithMatrix> readVectorList, String operator, long start, long end) throws Exception{
		List<ReadWithMatrix> newReadVectorList = new ArrayList<ReadWithMatrix>();

		int indexNo = 0;
		Iterator<ReadWithMatrix> iter = readVectorList.iterator();
		while( iter.hasNext() ) {
			ReadWithMatrix obj = iter.next();
			obj.setIndexNo( indexNo );

//			if( obj.getReadObject().getStartPosition() >= start && obj.getReadObject().getStartPosition() <= end ) {
			if( MatrixObj.isFilterByStartPointData( obj, operator, start, end ) ) {
				newReadVectorList.add( obj );
			}
			indexNo++;
		}
		return newReadVectorList;
	}

	private static boolean isFilterByStartPointData(ReadWithMatrix readInfo, String operator, long start, long end) throws Exception{
		if( readInfo.getReadObject().getStartPosition() >= start && readInfo.getReadObject().getStartPosition() <= end ) {
			return true;
		}
		return false;
	}
	
	private static List<ReadWithMatrix> getFilterBySequenceLengthData(List<ReadWithMatrix> readVectorList, String operator, long count) throws Exception {
		List<ReadWithMatrix> newReadVectorList = new ArrayList<ReadWithMatrix>();

		int indexNo = 0;
		Iterator<ReadWithMatrix> iter = readVectorList.iterator();
		while( iter.hasNext() ) {
			ReadWithMatrix obj = iter.next();
			obj.setIndexNo( indexNo );

//			if( MatrixObj.isContainOk( operator, obj.getReadObject().getSequenceByString().length(), count) ) {
			if( MatrixObj.isFilterBySequenceLengthData( obj, operator, count ) ) {
				newReadVectorList.add( obj );
			}
			indexNo++;
		}
		return newReadVectorList;
	}
	
	private static boolean isFilterBySequenceLengthData(ReadWithMatrix readInfo, String operator, long count) throws Exception {
		if( MatrixObj.isContainOk( operator, readInfo.getReadObject().getSequenceByString().length(), count) ) {
			return true;
		}
		return false;
	}

	private static List<ReadWithMatrix> getFilterBySequenceLengthData(List<ReadWithMatrix> readVectorList, String operator, long start, long end) throws Exception {
		List<ReadWithMatrix> newReadVectorList = new ArrayList<ReadWithMatrix>();

		int indexNo = 0;
		Iterator<ReadWithMatrix> iter = readVectorList.iterator();
		while( iter.hasNext() ) {
			ReadWithMatrix obj = iter.next();
			obj.setIndexNo( indexNo );

//			if( obj.getReadObject().getStartPosition() >= start && obj.getReadObject().getSequenceByString().length() <= end ) {
			if( MatrixObj.isFilterBySequenceLengthData( obj, operator, start, end ) ) {
				newReadVectorList.add( obj );
			}
			indexNo++;
		}
		return newReadVectorList;
	}

	private static boolean isFilterBySequenceLengthData(ReadWithMatrix readInfo, String operator, long start, long end) throws Exception {
//		if( readInfo.getReadObject().getStartPosition() >= start && readInfo.getReadObject().getSequenceByString().length() <= end ) {
		int length = readInfo.getReadObject().getSequenceByString().length(); 
		if( length >= start && length <= end ) {
			return true;
		}
		return false;
	}
	
	private static boolean isFilterIn( ReadWithMatrix readInfo, HairpinSequenceObject prematureSeq, GenomeReferenceObject refSeq, Map<String, MsbRCTColumnModel> columnStructure, MsbFilterModel filterModel, boolean isNormalized ) {
		if( filterModel == null )		return true;
		if( filterModel.size() == 0 )	return true;

		int COND_OPR = 1;
		for(int i=0; i<filterModel.size(); i++) {
			FilterModel fModel = filterModel.getFilterModelAt(i);
			
			String filter	= fModel.getFilter();
			String opr		= fModel.getOperator();
			String keyword	= fModel.getKeyword();

			int result = 1;
			if( filter.equals("sequence") ) {
				if( MatrixObj.isContainSequence( readInfo, opr, keyword ) )	result = 1;
				else														result = 0;
			}else if( filter.equals("reverse reads" ) ){
				String strand = readInfo.getReadObject().getReadQuality().getOrientation();

				if( keyword.toUpperCase().equals("TRUE") ) {
					if( strand.equals("+") )	result = 1;
					else						result = 0;
				}else {
					result = 1;
				}
			} else {
				try {
					if( opr.equals("between") && keyword.contains(",") ) {
						String[] argv = keyword.split(",");
						if( argv.length == 2 ) {
							long start	= Long.parseLong( argv[0].trim() );
							long end	= Long.parseLong( argv[1].trim() );

							if(			filter.equals("count")			)		result = Utilities.getIntegerFromBoolean( MatrixObj.isFilterByCountData(readInfo, columnStructure, opr, start, end, isNormalized ) );
							else if(	filter.equals("mis match") 		)		result = Utilities.getIntegerFromBoolean( MatrixObj.isFilterByMismatchedBetweenData( readInfo, prematureSeq, refSeq, opr, start, end ) );
							else if(	filter.equals("start position")	)		result = Utilities.getIntegerFromBoolean( MatrixObj.isFilterByStartPointData( readInfo, opr, start, end ) );
							else if(	filter.equals("length")			)		result = Utilities.getIntegerFromBoolean( MatrixObj.isFilterBySequenceLengthData( readInfo, opr,  start, end ) );
							else												result = 1;
						}else {
							return true;
//							System.out.println("If u choose 'between' operator, you should query like '1232, 2323'");
//
//	//						return readVectorList;
//							return MatrixObj.trimmedNullSequences( readVectorList, columnStructure );
						}
					}else{
						if( !keyword.isEmpty() ) {
							if( Utilities.isNumeric(keyword) ) {
								long count = Long.parseLong( keyword );
			
								if(			filter.equals("count") 			)	result = Utilities.getIntegerFromBoolean(  MatrixObj.isFilterByCountData(readInfo, columnStructure, opr, count, isNormalized ) );
								else if(	filter.equals("mis match") 		)	result = Utilities.getIntegerFromBoolean(  MatrixObj.isFilterByMismatchedData( readInfo, prematureSeq, refSeq, opr, count ) );
								else if(	filter.equals("start position")	)	result = Utilities.getIntegerFromBoolean(  MatrixObj.isFilterByStartPointData( readInfo, opr, count ) );		
								else if(	filter.equals("length")			)	result = Utilities.getIntegerFromBoolean(  MatrixObj.isFilterBySequenceLengthData( readInfo, opr,  count ) );
								else											result = 1;
							}else {
								result = 1;
							}
						}else {
							result = 1;
						}
					}
				}catch(Exception exp) {
					System.out.println("it is not numeric");
					MsbEngine.logger.error( "Error", exp );
					
					return true;
				}
			}
			
			if( fModel.getFilterType() == MsbFilterModel.AND )		COND_OPR = COND_OPR & result;
			else if( fModel.getFilterType() == MsbFilterModel.OR )	COND_OPR = COND_OPR | result;
			else													COND_OPR = COND_OPR & result;
		}

		if( COND_OPR > 0 )	return true;
		return false;
	}

	private static List<ReadWithMatrix> getFilterOutResultByQuery( List<ReadWithMatrix> readVectorList, HairpinSequenceObject prematureSeq, GenomeReferenceObject refSeq, Map<String, MsbRCTColumnModel> columnStructure, MsbFilterModel filterModel, boolean isNormalized ) {
//		if( filterModel.size() > 0 )
//			System.out.println("Hello");
		
		List<ReadWithMatrix> newReadVectorList = new ArrayList<ReadWithMatrix>();

		int indexNo = 0;
		Iterator<ReadWithMatrix> iter = readVectorList.iterator();
		while( iter.hasNext() ) {
			ReadWithMatrix obj = iter.next();

			if( MatrixObj.isFilterIn( obj, prematureSeq, refSeq, columnStructure, filterModel, isNormalized) ) {
				obj.setIndexNo( indexNo++ );
				newReadVectorList.add( obj );
			}
		}
		return newReadVectorList;
	}

//	private static List<ReadWithMatrix> getFilterOutResultByQuery( List<ReadWithMatrix> readVectorList, SequenceObject refSeq, Map<String, MsbRCTColumnModel> columnStructure, String filter, String operator, String query, boolean isNormalized ) {
//		if( query == null ) {
//			for(int i=0; i<readVectorList.size(); i++) {
//				readVectorList.get(i).setIndexNo( i );
//			}
//
////			return readVectorList;
//			return MatrixObj.trimmedNullSequences( readVectorList, columnStructure );
//		}else {
//			if( filter.equals("sequence") ) {
//				return MatrixObj.getSequenceBy( readVectorList, operator, query );
//			}else {
//				try {
//					if( operator.equals("between") && query.contains(",") ) {
//						String[] argv = query.split(",");
//						if( argv.length == 2 ) {
//							long start	= Long.parseLong( argv[0].trim() );
//							long end	= Long.parseLong( argv[1].trim() );
//							
//							if(			filter.equals("count")			)		return MatrixObj.getFilterByCountData( readVectorList, columnStructure, operator, start, end, isNormalized );
//							else if(	filter.equals("mis match") 		)		return MatrixObj.getFilterByMismatchedData( readVectorList, refSeq, operator, start, end );
//							else if(	filter.equals("start position")	)		return MatrixObj.getFilterByStartPointData( readVectorList, operator, start, end );
//							else if(	filter.equals("length")			)		return MatrixObj.getFilterBySequenceLengthData( readVectorList, operator,  start, end );
//						}else {
//							System.out.println("If u choose 'between' operator, you should query like '1232, 2323'");
//							
////							return readVectorList;
//							return MatrixObj.trimmedNullSequences( readVectorList, columnStructure );
//						}
//					}else{
//						long count = Long.parseLong( query );
//
//						if(			filter.equals("count") 			)	return MatrixObj.getFilterByCountData(readVectorList, columnStructure, operator, count, isNormalized );
//						else if(	filter.equals("mis match") 		)	return MatrixObj.getFilterByMismatchedData( readVectorList, refSeq, operator, count );
//						else if(	filter.equals("start position")	)	return MatrixObj.getFilterByStartPointData( readVectorList, operator, count );		
//						else if(	filter.equals("length")			)	return MatrixObj.getFilterBySequenceLengthData( readVectorList, operator,  count );
//					}
//				}catch(Exception e) {
//					System.out.println("it is not numeric");
//					MsbEngine.logger.error( "Error", e );
////					return readVectorList;
//					return MatrixObj.trimmedNullSequences( readVectorList, columnStructure );
//				}
//			}
//		}
//
//		return MatrixObj.trimmedNullSequences( readVectorList, columnStructure );
//	}
	
	private static List<ReadWithMatrix> trimmedNullSequences( List<ReadWithMatrix> readVectorList, Map<String, MsbRCTColumnModel> columnStructure ) {
		List<ReadWithMatrix> newList = new ArrayList<ReadWithMatrix>();
		for(int i=0; i<readVectorList.size(); i++) {
			double sum = 0;

			Iterator<String> keySet = readVectorList.get(i).getCountData().keySet().iterator();
			while( keySet.hasNext() ) {
				String name = keySet.next();
				try {
					MsbRCTColumnModel cModel = MSBReadCountTableColumnStructureModel.getColumnModel( columnStructure, name );
					if( cModel != null )	sum += readVectorList.get(i).getCountData().get( name );
				}catch(Exception e) {
					MsbEngine.logger.error("Error : ", e);
				}
			}
			if( sum > 0 )	newList.add( readVectorList.get(i) );
		}
		return newList;
	}

//	public String getFilter() {
//		return filter;
//	}
//
//	public String getOperator() {
//		return operator;
//	}
//
//	public String getQuery() {
//		return query;
//	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		this.currentColumnStructure = (MSBReadCountTableColumnStructureModel)arg;
	}
}
