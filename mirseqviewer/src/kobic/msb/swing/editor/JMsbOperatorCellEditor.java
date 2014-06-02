package kobic.msb.swing.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import kobic.msb.swing.frame.dialog.JMsbFindReadDailog;

public class JMsbOperatorCellEditor  extends AbstractCellEditor implements TableCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TableCellEditor editor;

	final JComboBox comboOptr = new JComboBox(JMsbFindReadDailog.ITEMS_ABOUT_COUNT);
	/*** Over JDK 1.7 ***/
//	final JComboBox<String> comboSeqFilter = new JComboBox<String>();
	/*** Under JDK 1.7 ***/
	final JComboBox comboSeqFilter = new JComboBox(JMsbFindReadDailog.ITEMS_ABOUT_SEQUENCE);
	final JComboBox comboRemoveFilter = new JComboBox(JMsbFindReadDailog.ITEMS_ABOUT_REMOVE);
	
	@Override
	public Object getCellEditorValue() {
		if (editor != null) {
			return editor.getCellEditorValue();
		}

		return null;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if( table.getValueAt(row, 1).toString().equals("sequence") )
			editor = new DefaultCellEditor( this.comboSeqFilter );
		else if( table.getValueAt(row, 1).toString().equals("reverse reads") )
			editor = new DefaultCellEditor( this.comboRemoveFilter );
		else
			editor = new DefaultCellEditor( this.comboOptr );

		return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
	}
}
