package kobic.msb.server.model;

import java.util.ArrayList;
import java.util.List;

import kobic.msb.common.SwingConst.Sorts;

public class MsbSortModel implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<SortModel> sortModels;
	
	public MsbSortModel() {
		this.sortModels = new ArrayList<SortModel>();
	}
	
	public void addSortModel( SortModel model ) {
		this.sortModels.add( model );
	}
	
	public void addSortModel(int order, String column, int direction) {
		this.sortModels.add( new SortModel(order, column, direction) );
	}

	public boolean hasSortBy() {
		if( this.sortModels.size() > 0 )	return true;
		return false;
	}
	public void clear() {
		this.sortModels.clear();
	}
	public int size() {
		return this.sortModels.size();
	}
	public Object[] getSortModelArray(int i) {
		SortModel model = this.sortModels.get(i);
		Object[] ret = new Object[]{model.getOrder(), model.getColumn(), "Values", Sorts.values()[model.getDirection()]}; 
		return ret;
	}
	
	public SortModel getSortModel(int i) {
		SortModel model = this.sortModels.get(i); 
		return model;
	}
	
	public void removeAll() {
		this.sortModels.clear();
	}
	
	public void setAllSortModelAscending() {
		for(int i=0; i<this.sortModels.size(); i++) {
			this.sortModels.get(i).setDirection( 1 );
		}
	}
	
	public void setAllSortModelDescending() {
		for(int i=0; i<this.sortModels.size(); i++) {
			this.sortModels.get(i).setDirection( 0 );
		}
	}
}

class SortModel implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int order;
	String column;
	int direction;
	
	SortModel(int order, String column, int direction){
		this.order = order;
		this.column = column;
		this.direction = direction;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
}