package kobic.msb.swing.renderer;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("rawtypes")
//public class TableComboBoxCellRenderer extends JComboBox implements TableCellRenderer {
public class TableComboBoxCellRenderer implements TableCellRenderer {
	private TableCellRenderer	delegate;
	@SuppressWarnings("unused")
	private JComboBox			jComboBox;

	public TableComboBoxCellRenderer( TableCellRenderer defaultRenderer ) {
		this.delegate = defaultRenderer;
	}
	

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub
		
		Component c = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

//		if (isSelected) {
//			this.setForeground(table.getSelectionForeground());
//            super.setBackground(table.getSelectionBackground());
//        } else {
//        	this.setForeground(table.getForeground());
//        	this.setBackground(table.getBackground());
//        }
//
//		this.setSelectedItem( value );

		return c;
	}
}