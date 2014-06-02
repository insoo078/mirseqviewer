package kobic.msb.server.model;

import java.util.ArrayList;
import java.util.List;

public class MsbFilterModel implements java.io.Serializable{
	public static final int AND		= 0;
	public static final int OR		= 1;
	public static final int DEFAULT = 2; 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<FilterModel> list;
	
	public MsbFilterModel() {
		this.list = new ArrayList<FilterModel>();
	}
	
	public void addModel( FilterModel model ) {
		this.list.add( model );
	}
	
	public void addModel( int order, int filterType, String filter, String operator, String keyword ){
		this.list.add( new FilterModel(order, filterType, filter, operator, keyword) );
	}
	
	public FilterModel getFilterModelAt(int i) {
		return this.list.get(i);
	}
	
	public int size() {
		return this.list.size();
	}
	
	public void clear() {
		this.list.clear();
	}
	
	public boolean hasDefaultCondition() {
		for(int i=0; i<this.list.size(); i++)
			if( this.list.get(i).getFilterType() == MsbFilterModel.DEFAULT )
				return true;
		
		return false;
	}

	public class FilterModel implements java.io.Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		int order;
		
		int filterType;

		private String filter;
		private String operator;
		private String keyword;
		
		public FilterModel(int order, int filterType, String filter, String operator, String keyword){
			this.order		= order;
			this.filterType	= filterType;
			this.filter		= filter;
			this.operator	= operator;
			this.keyword	= keyword;
		}

		public int getOrder() {
			return order;
		}

		public void setOrder(int order) {
			this.order = order;
		}

		public String getFilter() {
			return filter;
		}

		public void setFilter(String filter) {
			this.filter = filter;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public String getKeyword() {
			return keyword;
		}

		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}

		public int getFilterType() {
			return filterType;
		}

		public void setFilterType(int filterType) {
			this.filterType = filterType;
		}
	}
}