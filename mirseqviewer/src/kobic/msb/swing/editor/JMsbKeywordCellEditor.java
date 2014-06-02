package kobic.msb.swing.editor;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;

import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.swing.frame.dialog.JMsbFindReadDailog;

public class JMsbKeywordCellEditor extends AbstractCellEditor implements TableCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TableCellEditor editor;
	
	final JComboBox comboBooleanFilter 	= new JComboBox(JMsbFindReadDailog.ITEMS_BOOLEAN);
	final JTextField txtKeyword			= new JTextField();
	
	final JMsbKeywordCellEditor remote = JMsbKeywordCellEditor.this;

	public void initCombobox(final JTable table, final int row, final int col) {
		this.comboBooleanFilter.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				table.setValueAt( remote.comboBooleanFilter.getSelectedItem(), row, col );
			}
		});
	}
	public void initTextField( final UpdatableTableModel tableModel, final JTextField txtKeyword, final int selectedIndex ) {
		txtKeyword.getDocument().addDocumentListener( new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				tableModel.setValueAt( txtKeyword.getText(), selectedIndex, 3 );
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				tableModel.setValueAt( txtKeyword.getText(), selectedIndex, 3 );
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				tableModel.setValueAt( txtKeyword.getText(), selectedIndex, 3 );
			}
		});
	}
	
	@Override
	public Object getCellEditorValue() {
		if (editor != null) {
			return editor.getCellEditorValue();
		}

		return null;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if( table.getValueAt(row, 2).toString().equals("remove") ){
			this.initCombobox(table, row, column);
			editor = new DefaultCellEditor(this.comboBooleanFilter);
		}else {
			UpdatableTableModel tableModel = (UpdatableTableModel) table.getModel();
			this.initTextField(tableModel, txtKeyword, row);
			editor = new DefaultCellEditor(this.txtKeyword);
		}

		return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
	}
}