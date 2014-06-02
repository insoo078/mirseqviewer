package kobic.msb.swing.component;

import java.util.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.*;

import kobic.msb.common.SwingConst.Sorts;
import kobic.msb.server.model.Model;
import kobic.msb.server.model.MsbSortModel;

public class GroupableTableHeaderUI extends BasicTableHeaderUI {
	@Override
	public void paint( Graphics g, JComponent c ) {
		Rectangle clipBounds = g.getClipBounds();
		if (this.header.getColumnModel() == null) return;
		((GroupableTableHeader)this.header).setColumnMargin();
		int column = 0;
		Dimension size = this.header.getSize();
		Rectangle cellRect  = new Rectangle(0, 0, size.width, size.height);
		Hashtable<ColumnGroup, Rectangle> h = new Hashtable<ColumnGroup, Rectangle>();
		int columnMargin = this.header.getColumnModel().getColumnMargin();

		Enumeration<TableColumn> enumeration = header.getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			cellRect.height = size.height;
			cellRect.y      = 0;
			TableColumn aColumn = enumeration.nextElement();
			Enumeration<Object> cGroups = ((GroupableTableHeader)header).getColumnGroups(aColumn);
			if ( cGroups != null ) {
				int groupHeight = 0;
				while (cGroups.hasMoreElements()) {
					ColumnGroup cGroup = (ColumnGroup)cGroups.nextElement();
					Rectangle groupRect = h.get(cGroup);
					if (groupRect == null) {
						groupRect = new Rectangle(cellRect);
						Dimension d = cGroup.getSize(header.getTable());
						groupRect.width  = d.width;
						groupRect.height = d.height;    
						h.put(cGroup, groupRect);
					}
					this.paintCell(g, groupRect, cGroup);
					groupHeight += groupRect.height;
					cellRect.height = size.height - groupHeight;
					cellRect.y      = groupHeight;
				}
			}      
			cellRect.width = aColumn.getWidth() + columnMargin;
			if (cellRect.intersects( clipBounds )) {
				this.paintCell( g, cellRect, column );
			}
			cellRect.x += cellRect.width;
			column++;
		}
	}

	private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
		TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);

		TableCellRenderer renderer = aColumn.getHeaderRenderer();
		//revised by Java2s.com
		renderer = new DefaultTableCellRenderer(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				final JLabel header = new JLabel();
				header.setForeground(table.getTableHeader().getForeground());
				header.setBackground( Color.green );
				header.setFont(table.getTableHeader().getFont());

				header.setHorizontalAlignment(SwingConstants.CENTER);
				header.setText(value.toString());
				header.setBorder( UIManager.getBorder("TableHeader.cellBorder") );

				if( table.getModel() instanceof SortableTableModel ) {
					SortableTableModel sortableTableModel = (SortableTableModel)table.getModel();
					Model model = sortableTableModel.getModel();
					
					MsbSortModel sortModel = model.getProjectMapItem().getMsbSortModel();
	
					for(int i=0; i<sortModel.size(); i++) {
						Object[] arrays = sortModel.getSortModelArray(i);
						String columnName	= arrays[1].toString();

						// Header Icon
						if( header.getText().equals( columnName ) ) {
							Sorts sorts			= (Sorts) arrays[3];
							if( sorts == Sorts.LARGEST_TO_SMALLEST )	header.setIcon( UIManager.getIcon("Table.ascendingSortIcon") );
							else										header.setIcon( UIManager.getIcon("Table.descendingSortIcon") );
						}
					}
				}

				return header;
			}   
		};

		// ��� ��������� ��������� Cell ��� ������������ ������
		Component c = renderer.getTableCellRendererComponent( this.header.getTable(), aColumn.getHeaderValue(), false, false, -1, columnIndex);
        
		c.setBackground(UIManager.getColor("control"));
		
		this.rendererPane.add(c);
		this.rendererPane.paintComponent( g, c, this.header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true );
	}

	private void paintCell(Graphics g, Rectangle cellRect, ColumnGroup cGroup) {
		TableCellRenderer renderer = cGroup.getHeaderRenderer();
		//revised by Java2s.com
		// if(renderer == null){
		//   	   return ;
		//    }

		// Group ������������ ������������ ������ 
		Component component = renderer.getTableCellRendererComponent( this.header.getTable(), cGroup.getHeaderValue(), false, false, -1, -1);

		this.rendererPane.add(component);
		this.rendererPane.paintComponent(g, component, this.header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true );
	}

	private int getHeaderHeight() {
		int height = 0;
		TableColumnModel columnModel = this.header.getColumnModel();
		for(int column = 0; column < columnModel.getColumnCount(); column++) {
			TableColumn aColumn = columnModel.getColumn(column);
			TableCellRenderer renderer = aColumn.getHeaderRenderer();
			//revised by Java2s.com
			if(renderer == null){
				return 60;
			}
      
			Component comp = renderer.getTableCellRendererComponent( this.header.getTable(), aColumn.getHeaderValue(), false, false,-1, column);
			int cHeight = comp.getPreferredSize().height;
			Enumeration<Object> e = ((GroupableTableHeader)this.header).getColumnGroups(aColumn);      
			if (e != null) {
				while (e.hasMoreElements()) {
					ColumnGroup cGroup = (ColumnGroup)e.nextElement();
					cHeight += cGroup.getSize(this.header.getTable()).height;
				}
			}
			height = Math.max(height, cHeight);
		}
		return height;
	}

	private Dimension createHeaderSize(long width) {
		TableColumnModel columnModel = this.header.getColumnModel();
		width += columnModel.getColumnMargin() * columnModel.getColumnCount();
		if (width > Integer.MAX_VALUE) {
			width = Integer.MAX_VALUE;
		}
		return new Dimension((int)width, getHeaderHeight());
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		long width = 0;
		Enumeration<TableColumn> enumeration = this.header.getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			TableColumn aColumn = (TableColumn)enumeration.nextElement();
			width = width + aColumn.getPreferredWidth();
		}
		return createHeaderSize(width);
	}
}