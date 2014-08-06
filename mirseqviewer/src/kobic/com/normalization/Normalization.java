package kobic.com.normalization;

import java.util.Arrays;

import javax.swing.JOptionPane;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;
//import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import kobic.com.edgeR.BasicFunctions;
import kobic.com.edgeR.DGEList;
import kobic.com.edgeR.EdgeR;
import kobic.com.edgeR.model.CountDataModel;
import kobic.com.math.Lowess;
import kobic.com.math.Quantile;
import kobic.msb.db.sqlite.vo.MatureVO;
import kobic.msb.server.model.ClusterModel;
import kobic.msb.system.engine.MsbEngine;

public class Normalization {
	public static String[] methods = Normalization.init();

	private static String[] init() {
		String[] readBasedMethod = new String[]{"RPM", "TMM", "LOWESS", "Quantile"};
//		String[] edgeRmethod = EdgeR.normalizeMethods;

//		Object[] totalMethod = ArrayUtils.addAll( readBasedMethod, edgeRmethod );
		Object[] totalMethod = readBasedMethod;
		return Arrays.copyOf( totalMethod, totalMethod.length, String[].class );
	}
	
	public static double[][] doRpmNormalization(CountDataModel model ) throws DimensionMismatchException, NullArgumentException, NoDataException, Exception {
		double[][] mat = EdgeR.cpm( MatrixUtils.createRealMatrix( model.getCountData().getData() ), model.getLibSize(), null, null );

		return mat;
	}
	
	public static double[][] doRpkmNormalization(CountDataModel model, String version) throws Exception {
		String[] id = model.getRowNames();
		RealMatrix realMat = model.getCountData();
		double[] lengths = new double[realMat.getRowDimension()];
		for(int row=0; row<realMat.getRowDimension(); row++) {
			String matureId = id[row];

//			String hairpindId = MsbEngine._db.getHairpinIdFromMatureId( matureId );
//			MatureVO mature = MsbEngine._db.getMicroRnaMatureByMirid( matureId );
			MatureVO mature = MsbEngine.getInstance().getMiRBaseMap().get(version).getMicroRnaMatureByMirid( matureId );
//			int length = MsbEngine._db.getMicroRnaHairpinByMirid2( hairpindId ).getSequence().length();
			if( mature != null )	lengths[row] = mature.getSequence().length();
			else					lengths[row] = 27;
		}
		
		double[][] mat = EdgeR.rpkm( realMat, model.getLibSize(), lengths, null, null );
		return mat;
	}

	public static double[][] doLowessNormalization(CountDataModel model) throws OutOfRangeException, Exception {
		RealMatrix realMat = model.getCountData();
		RealMatrix newMatBak = MatrixUtils.createRealMatrix( realMat.getRowDimension(), realMat.getColumnDimension() );

		RealMatrix newMat = BasicFunctions.logb(newMatBak, 2);
//		realMat = Quartile.quartileNormalize( realMat, 50 );
//		realMat = BasicFunctions.log( realMat );
		RealVector baseVector = realMat.getColumnVector(0);
		newMat.setColumnVector(0, baseVector );
		for(int i=1; i<realMat.getColumnDimension();i++) {
//			RealVector a = realMat.getColumnVector(i).add( baseVector );		// x+y
//			RealVector b = realMat.getColumnVector(i).subtract( baseVector );	// x-y
//
//			Map<String, double[]> fit = Lowess.lowess( a, b, null, null, null );
//
//			double[] yout = Approx.approx( fit.get("x"), fit.get("y"), a.toArray(), null, null, null, null, null, null, Approx.TIE_MEAN );
//			RealVector out = realMat.getColumnVector(i).subtract( MatrixUtils.createRealVector(yout) );
			RealVector out = Lowess.lowessNormalization( baseVector, realMat.getColumnVector(i) );
			newMat.setColumn(i, out.toArray());
		}
		return newMat.getData();
	}

	public static double[][] doQuantileNormalization(CountDataModel model) {
		RealMatrix realMat = model.getCountData();
		
		RealMatrix newMat = Quantile.normalizeQuantiles( realMat, true );
		
		return newMat.getData();
	}
	public static double[][] doNormalize( String method, ClusterModel clustModel, String version ) throws Exception{
//		Model re initialize
		CountDataModel model = clustModel.getCountModel();

		double[][] mat = null;
		try {
			if( method.equals("RPM") ) {
				mat = Normalization.doRpmNormalization(model);
			}else if( method.equals("RPKM") ) {
				mat = Normalization.doRpkmNormalization(model, version);
			}else if( method.equals("LOWESS") ) {
				mat = Normalization.doLowessNormalization( model );
			}else if( method.equals("Quantile") ) {
				mat = Normalization.doQuantileNormalization(model);
			}else {
				try{
					DGEList dgeList = new DGEList( model );
			
					dgeList = EdgeR.calcNormFactors( dgeList, method );
			
					dgeList = EdgeR.estimateCommonDisp( dgeList, null, 0L );
					
					mat = dgeList.getPseudoCount().getData();
					
					clustModel.setDGEListObject( dgeList );
				}catch(Exception e) {
					JOptionPane.showMessageDialog(null, method + " There is a normalization problem with this data ", "Normalization problem", JOptionPane.ERROR_MESSAGE );
					mat = model.getCountData().getData();
				}
			}
		}catch(Exception e) {
//			e.printStackTrace();
			MsbEngine.logger.debug( e );
		}

		return mat;
	}
}
