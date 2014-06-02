package kobic.msb.swing.component;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class UpdatableTableModel extends AbstractTableModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] columnNames;
	private Vector<Object[]> data;
	
	public UpdatableTableModel( Object[][] data, String[] columnNames ) {
		this.columnNames = columnNames;
		this.data = new Vector<Object[]>();

		for(int i=0; data != null && i<data.length; i++) {
			this.data.add( data[i] );
		}
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		if( this.data == null )	return 0;
		return this.data.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		if( this.columnNames == null )	return 0;
		return this.columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		if( this.data == null )	return null;
		return this.data.get(rowIndex)[columnIndex];
	}
	
	public Object[] getRecordAt(int i) {
		return this.data.get(i);
	}

	@Override
	public String getColumnName(int col) {
        return columnNames[col];
    }

	@Override
	public void setValueAt( Object value, int row, int col ) {
		if( row < this.data.size() ) {
			this.data.get(row)[col] = value;
	        this.fireTableCellUpdated(row, col);
		}
    }

	public void addRow( Object[] row ) {
		if( this.data == null ) {
			this.data = new Vector<Object[]>();
			this.data.add( row );
		}else {
			this.data.add( row );
		}
		this.fireTableDataChanged();
	}

	public void removeAt(int i) {
		if( i < this.data.size() ) {
			this.data.removeElementAt(i);
			this.fireTableRowsDeleted(i, i);
			this.fireTableDataChanged();
		}
	}

	public void refresh() {
		this.fireTableDataChanged();
	}
	
	public void removeAll() {
		if( this.data != null ){
			int lastIndex = this.data.size() - 1;
			this.data.removeAllElements();

			if( lastIndex > 0 )	this.fireTableRowsDeleted(0, lastIndex);
			this.fireTableDataChanged();
		}
	}
}
