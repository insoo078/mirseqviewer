package kobic.msb.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import kobic.com.edgeR.DGEList;
import kobic.com.edgeR.model.CountDataModel;
import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;

import weka.clusterers.HierarchicalClusterer;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.SparseInstance;

public class ClusterModel extends Observable implements java.io.Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CountDataModel	countModel;
	private Instances		instances;
	
	private boolean			isNormalized;
	@SuppressWarnings("unused")
	private String			normalizingMethod;
	private DGEList			dgeList;

	private DescritiveStatisticsModel statistics;
	
	public static EuclideanDistance ed = new EuclideanDistance();
	
	private HierarchicalClusterer clusterResult;
	
	public ClusterModel(CountDataModel model) {
		this.countModel = model;
		this.dgeList	= null;
		
		this.instances = ClusterModel.getInstancesByWeka( model );
	}

	@SuppressWarnings("unused")
	public void clusteringExpressionProfile() throws Exception {
		SelectedTag s = new SelectedTag(2, HierarchicalClusterer.TAGS_LINK_TYPE);
		
		ClusterModel.ed.setInstances( instances );
		ClusterModel.ed.setAttributeIndices("first-last");
		ClusterModel.ed.setDontNormalize(false);
		ClusterModel.ed.setInvertSelection(false);
		
		this.clusterResult = new HierarchicalClusterer();
		this.clusterResult.setPrintNewick( true );
		this.clusterResult.setNumClusters(1);
		this.clusterResult.setDistanceIsBranchLength( true );
		this.clusterResult.setDistanceFunction( ClusterModel.ed );
		this.clusterResult.setLinkType(s);

		this.clusterResult.buildClusterer( this.instances );
//		ArffSaver saver = new ArffSaver();
//		saver.setInstances( instances );
//		saver.setFile(new File("/Users/lion/Desktop/test.arff"));
//		saver.writeBatch();

		for (int i = 0; i < this.instances.numInstances(); i++){
		    int clusterNumber = this.clusterResult.clusterInstance( this.instances.instance(i) );
		}
	}

//	private void makeDataModelFor() {
//		Instances instances = this.getDatasetForWeka();
//		
//		SelectedTag s = new SelectedTag(2, HierarchicalClusterer.TAGS_LINK_TYPE);
//
//		this.ed.setInstances( instances );
//		this.ed.setAttributeIndices("first-last");
//		this.ed.setDontNormalize(false);
//		this.ed.setInvertSelection(false);
//	}
	
	public static Instances getInstancesByWeka( CountDataModel countDataModel ) {
		int numDimensions	= countDataModel.getColDimension();
		int numInstances	= countDataModel.getRowDimension();

		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		List<Instance> instances = new ArrayList<Instance>();

		for(int dim = 0; dim < numDimensions; dim++) {
		    Attribute current = new Attribute( countDataModel.getColumnNameAt(dim), dim );
		    if(dim == 0) {
		        for(int obj = 0; obj < numInstances; obj++) {
		        	instances.add( new SparseInstance(numDimensions) );
		        }
		    }

		    for(int obj = 0; obj < numInstances; obj++) {
		    	if(countDataModel.getCountDataAt(obj,  dim) ==0 )	countDataModel.setCountDataAt( obj,  dim,  JMsbSysConst.MISSING_VALUE );
		    	instances.get(obj).setValue(current, countDataModel.getCountDataAt(obj,  dim) );
		    }

		    atts.add(current);
		}

		Instances newDataset = new Instances("Dataset", atts, instances.size());

		for(Instance inst : instances)	newDataset.add(inst);

		return newDataset;
	}

//	private Instances getDatasetForWeka() {
//		int numDimensions	= this.countModel.getColDimension();
//		int numInstances	= this.countModel.getRowDimension();
//
//		ArrayList<Attribute> atts = new ArrayList<Attribute>();
//		List<Instance> instances = new ArrayList<Instance>();
//
//		for(int dim = 0; dim < numDimensions; dim++) {
//		    Attribute current = new Attribute( this.countModel.getColumnNameAt(dim), dim );
//		    if(dim == 0) {
//		        for(int obj = 0; obj < numInstances; obj++) {
//		        	instances.add( new SparseInstance(numDimensions) );
//		        }
//		    }
//
//		    for(int obj = 0; obj < numInstances; obj++) {
//		    	if( this.countModel.getCountDataAt(obj,  dim) ==0 )	this.countModel.setCountDataAt( obj,  dim,  Double.NaN );
//		    	instances.get(obj).setValue(current, this.countModel.getCountDataAt(obj,  dim) );
//		    }
//
//		    atts.add(current);
//		}
//
//		Instances newDataset = new Instances("Dataset", atts, instances.size());
//
//		for(Instance inst : instances)	newDataset.add(inst);
//
//		return newDataset;
//	}

	public CountDataModel getCountModel() {
		return this.countModel;
	}

	public String[] getRowNames() {
		return this.countModel.getRowNames();
	}

	public String[] getColNames() {
		return this.countModel.getColNames();
	}

	public double[][] getOriginalData() {
		return this.countModel.getOriginalData();
	}

	public void setOriginalData(double[][] originalData) {
		this.countModel.updateCountData( originalData );

		this.setChanged();
		this.notifyObservers( this );
	}
	
	public void doBackupCountData() {
		this.countModel.backupData();
	}
	
	public void doUpdateNormalizedData(double[][] originalData, String normalizeMethod) {
//		this.countModel.backupData();
		this.countModel.updateCountData( originalData );

		this.isNormalized		= true;
		this.normalizingMethod	= normalizeMethod;

		this.setChanged();
		this.notifyObservers( this );
	}

	public boolean doRefreshData() {
		if( this.isNormalized ) {
			this.countModel.restoreData();

			this.isNormalized		= false;
			this.normalizingMethod	= "";
			this.dgeList			= null;
	
			this.setChanged();
			this.notifyObservers( this );
			
			return true;
		}
		return false;
	}

	public HierarchicalClusterer getHierachicalClusteringResult() {
		return this.clusterResult;
	}
	
	public Instances getInstances() {
		return this.instances;
	}
	
	public DescritiveStatisticsModel getDescritiveStatisticsModel() {
		return this.statistics;
	}
	
	public void setDGEListObject( DGEList obj ) {
		this.dgeList = obj;
	}
	
	public DGEList getDGEListObject() {
		return this.dgeList;
	}
	
	public void imputationWithDialog( JComponent component ) {
		double value = Double.NaN;

		String message = "Replace value from NA to :";
		int message_type = JOptionPane.INFORMATION_MESSAGE;
		while( Double.valueOf( value ).equals(Double.NaN) ) {
			String strValue = (String) JOptionPane.showInputDialog( component, message , "Missing Value Imputation", message_type, null, null, new Double(0.001) );

			if( strValue == null )		break;

			if( Utilities.isNumeric( strValue ) )	{
				value = Double.parseDouble( strValue );
				break;
			}
			message = "Your input was not numeric";
			message_type = JOptionPane.ERROR_MESSAGE;
		}

		if( !Double.valueOf( value ).equals(Double.NaN) ) {
			// Missing value processing
			RealMatrix matrix = MatrixUtils.createRealMatrix( this.getOriginalData() );
			double[][] mat = matrix.getData();
			for(int i=0; i<mat.length; i++) {
				for(int j=0; j<mat[0].length; j++) {
					if( Double.valueOf( mat[i][j] ).equals( Double.NaN ) ) {
						mat[i][j] = value;
					}
				}
			}
			// Original data backup
			this.doBackupCountData();
			
			// New data, which are imputated by user, apply to model
			this.setOriginalData( mat );
		}
	}
}
