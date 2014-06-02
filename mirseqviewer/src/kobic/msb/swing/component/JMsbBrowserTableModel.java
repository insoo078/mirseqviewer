package kobic.msb.swing.component;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

public class JMsbBrowserTableModel extends DefaultTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<Integer, Boolean> canEditIndexMap;
	
	public JMsbBrowserTableModel() {
		super();
		this.canEditIndexMap = new HashMap<Integer, Boolean>();
	}

	public JMsbBrowserTableModel(Object[][] tableData, Object[] colNames) {
		super(tableData, colNames);
		this.canEditIndexMap = new HashMap<Integer, Boolean>();
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return this.canEditIndexMap.get(column);
	}

	public void setCanEditIndex(int index, boolean flag){
		this.canEditIndexMap.put(index, flag);
	}
}