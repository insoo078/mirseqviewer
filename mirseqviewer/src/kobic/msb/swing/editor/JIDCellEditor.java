package kobic.msb.swing.editor;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;

public class JIDCellEditor extends AbstractCellEditor implements TableCellEditor {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	private JComboBox jComboBox;
    boolean cellEditingStopped = false;

    @SuppressWarnings("rawtypes")
	public JIDCellEditor(JComboBox comboBox) {
    	this.jComboBox = comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        return jComboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    	this.jComboBox.addItemListener(new ItemListener() {
    		@Override
    		public void itemStateChanged(ItemEvent e) {
    			if (e.getStateChange() == ItemEvent.SELECTED) {
    				fireEditingStopped();
    			}
    		}
        });
    	this.jComboBox.addPopupMenuListener(new PopupMenuListener() {
    		@Override
    		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    			cellEditingStopped = false;
    		}

    		@Override
    		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    			cellEditingStopped = true;
    			fireEditingCanceled();
    		}

    		@Override
    		public void popupMenuCanceled(PopupMenuEvent e) {

    		}
    	});
    	return jComboBox;
    }

    @Override
    public boolean stopCellEditing() {
        return cellEditingStopped;
    }
}