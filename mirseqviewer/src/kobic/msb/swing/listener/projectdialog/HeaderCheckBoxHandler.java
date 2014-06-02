package kobic.msb.swing.listener.projectdialog;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import kobic.msb.common.SwingConst.Status;

public class HeaderCheckBoxHandler implements TableModelListener{
	private final		JTable table;
	private final int 	targetColumnIndex;

	public HeaderCheckBoxHandler(JTable table, int index) {
		this.table				= table;
		this.targetColumnIndex	= index;
	}
    
    @Override
    public void tableChanged(TableModelEvent e) {
    	if(e.getType()==TableModelEvent.UPDATE && e.getColumn()==targetColumnIndex) {
    		int vci = table.convertColumnIndexToView(targetColumnIndex);
    		TableColumn column = table.getColumnModel().getColumn(vci);
    		if(!Status.INDETERMINATE.equals(column.getHeaderValue())) {
    			column.setHeaderValue(Status.INDETERMINATE);
    		}else{
    			boolean selected = true, deselected = true;
    			TableModel m = table.getModel();
    			for(int i=0; i<m.getRowCount(); i++) {
    				Boolean b = (Boolean)m.getValueAt(i, targetColumnIndex);
    				selected &= b; deselected &= !b;
    				if(selected==deselected) return;
    			}
    			if(selected) {
    				column.setHeaderValue(Status.SELECTED);
    			}else if(deselected) {
    				column.setHeaderValue(Status.DESELECTED);
    			}else{
    				return;
    			}
    		}
    		JTableHeader h = table.getTableHeader();
    		h.repaint(h.getHeaderRect(vci));
    	}
	}
}