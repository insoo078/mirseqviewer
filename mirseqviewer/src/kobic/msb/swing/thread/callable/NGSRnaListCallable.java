package kobic.msb.swing.thread.callable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import kobic.com.util.Utilities;
import kobic.msb.common.SwingConst.Status;
import kobic.msb.server.model.ClusterModel;
import kobic.msb.swing.comparator.MiRnaCountComparator;
import kobic.msb.swing.thread.callable.obj.NGSRnaListObj;
import kobic.msb.swing.thread.caller.AbstractThreadCaller;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class NGSRnaListCallable  implements Callable<NGSRnaListObj>{
	private ProjectMapItem			projectMapItem;
	private AbstractThreadCaller	test;
	private int						currentPos;

	public NGSRnaListCallable( ProjectMapItem projectMapItem, int currentPos ) {
		this.projectMapItem	= projectMapItem;
		this.currentPos		= currentPos;
	}
	
	public void setNGSTestCaller( AbstractThreadCaller caller ) {
		this.test = caller;
	}

	@Override
	public NGSRnaListObj call() throws Exception{
		List<Object[]> readedAllObjList = new ArrayList<Object[]>();
		List<Object[]> choosedRnaObjList = new ArrayList<Object[]>();

		ClusterModel clusterModel = this.projectMapItem.getClusterModel();

		Object[] header		= Utilities.append( new Object[]{ Status.INDETERMINATE, "miRNA", "Sum"}, clusterModel.getColNames() );

		RealMatrix matrix = clusterModel.getCountModel().getCountData();
		Object[][] data = new Object[matrix.getRowDimension()][];

		System.out.println("Summarize miRNA information" );
		double incre = (double)(100-this.currentPos)/ matrix.getRowDimension();
		double progressSum = 0;
//		this.test.setProcessingProgress(this.currentPos);
		this.test.getOwnerDialog().setProgressToGetMiRnas( this.currentPos );
//		MsbEngine.logger.debug("Before reconstruct profile data");
		for(int i=0; i<matrix.getRowDimension(); i++) {
			Double[] record = ArrayUtils.toObject( matrix.getRow(i) );
			Object[] rec = new Object[]{ Boolean.valueOf(false), clusterModel.getRowNames()[i], Utilities.sum( matrix.getRow(i) ) };
			data[i] = ArrayUtils.addAll( rec, record );

//			List<String> hairpinIds = MsbEngine._db.getHairpinIdFromMatureId( data[i][1].toString() );
			List<String> hairpinIds = MsbEngine.getInstance().getMiRBaseMap().get(this.projectMapItem.getMiRBAseVersion()).getHairpinIdFromMatureId( data[i][1].toString() );

			boolean isChoosed = false;
			for(int j=0; j<hairpinIds.size(); j++) {
				if( this.projectMapItem.getModelMap().get( hairpinIds.get(j) ) != null ) {
					isChoosed = true;
					break;
				}
			}
//			if( isChoosed )		choosedRnaObjList.add( data[i] );
//			else				readedAllObjList.add( data[i] );
			data[i][0] = isChoosed;
			readedAllObjList.add( data[i] );

			progressSum += incre;
//			this.test.setProcessingProgress( (int)(this.currentPos + progressSum) );
			this.test.getOwnerDialog().setProgressToGetMiRnas( (int)(this.currentPos + progressSum) );
		}

//		MsbEngine.logger.debug("Before sort profile by read count");
		Collections.sort( readedAllObjList, new MiRnaCountComparator() );
//		MsbEngine.logger.debug("After sort profile by read count");
		
//		this.test.callback(header, readedAllObjList, choosedRnaObjList);
		this.test.setProcessingProgress(100);

		return new NGSRnaListObj( header, readedAllObjList, choosedRnaObjList );
	}
}
