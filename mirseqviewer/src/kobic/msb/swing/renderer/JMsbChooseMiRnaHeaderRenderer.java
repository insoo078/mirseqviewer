package kobic.msb.swing.renderer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import kobic.msb.common.SwingConst.Status;

public class JMsbChooseMiRnaHeaderRenderer extends JCheckBox implements TableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final JLabel label = new JLabel("Check All");
	private int targetColumnIndex;
	  
	public JMsbChooseMiRnaHeaderRenderer(JTableHeader header, int index) {
		super((String)null);
		this.targetColumnIndex = index;
		this.setOpaque(false);
		this.setFont(header.getFont());

		header.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTableHeader header = (JTableHeader)e.getSource();
				JTable table = header.getTable();
				TableColumnModel columnModel = table.getColumnModel();
				int vci = columnModel.getColumnIndexAtX(e.getX());
				int mci = table.convertColumnIndexToModel(vci);
				if(mci == targetColumnIndex) {
					TableColumn column = columnModel.getColumn(vci);
					Object v = column.getHeaderValue();
					boolean b = Status.DESELECTED.equals(v)?true:false;
					TableModel m = table.getModel();
					for(int i=0; i < m.getRowCount(); i++) m.setValueAt(b, i, mci);
					column.setHeaderValue(b?Status.SELECTED:Status.DESELECTED);
//					header.repaint();
				}
			}
		});
		this.label.setFont( header.getFont() );
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//		this.setHorizontalAlignment(JLabel.CENTER);
		// TODO Auto-generated method stub
		TableCellRenderer r = table.getTableHeader().getDefaultRenderer();
		JLabel l =(JLabel)r.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if( targetColumnIndex == table.convertColumnIndexToModel(column) ) {
			if(value instanceof Status) {
				switch((Status)value) {
					case SELECTED:		this.setSelected(true);		this.setEnabled(true);  break;
					case DESELECTED: 	this.setSelected(false);	this.setEnabled(true);  break;
					case INDETERMINATE:	this.setSelected(true); 	this.setEnabled(false); break;
				}
			}else{
				this.setSelected(true);
				this.setEnabled(false);
			}
			this.label.setIcon(new ComponentIcon(this));
			l.setIcon(new ComponentIcon(this.label));
			l.setText(null); //XXX: Nimbus???
			
			l.setHorizontalAlignment( SwingConstants.CENTER );
		}
		return l;
	}

	@Override
    public void updateUI() {
		this.setText(null); //XXX: Nimbus??? Header height bug???
        super.updateUI();
    }
}

class CheckBoxIcon implements Icon{
	private final JCheckBox check;
	public CheckBoxIcon(JCheckBox check) {
		this.check = check;
	}
	@Override
	public int getIconWidth() {
		return check.getPreferredSize().width;
	}
	@Override public int getIconHeight() {
		return check.getPreferredSize().height;
	}
	@Override public void paintIcon(Component c, Graphics g, int x, int y) {
		SwingUtilities.paintComponent( g, check, (Container)c, x, y, getIconWidth(), getIconHeight());
	}
}

class ComponentIcon implements Icon{
    private final JComponent cmp;
    public ComponentIcon(JComponent cmp) {
        this.cmp = cmp;
    }
    @Override
    public int getIconWidth() {
        return cmp.getPreferredSize().width;
    }
    @Override 
    public int getIconHeight() {
        return cmp.getPreferredSize().height;
    }
    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        SwingUtilities.paintComponent(g, cmp, (Container)c, x, y, getIconWidth(), getIconHeight());
    }
}
