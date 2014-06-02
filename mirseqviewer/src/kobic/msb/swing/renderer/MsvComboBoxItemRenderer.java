package kobic.msb.swing.renderer;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import kobic.msb.server.obj.ComboBoxItem;

public class MsvComboBoxItemRenderer extends BasicComboBoxRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {  
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);  

		if (value != null) {  
			ComboBoxItem item = (ComboBoxItem)value;  
			setText( item.getId().toUpperCase() );  
		}  
  
		if (index == -1)  
		{  
			ComboBoxItem item = (ComboBoxItem)value;  
			setText( "" + item.getId() );
		}

		return this;  
	}
}
