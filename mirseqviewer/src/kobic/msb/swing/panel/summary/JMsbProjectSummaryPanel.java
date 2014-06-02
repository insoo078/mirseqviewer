package kobic.msb.swing.panel.summary;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.project.ProjectManager;

import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class JMsbProjectSummaryPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ProjectMapItem projectMapItem;
	private JTable table;

	/**
	 * Create the panel.
	 */
	public JMsbProjectSummaryPanel( ProjectMapItem projectMapItem ) {
		this.projectMapItem = projectMapItem;

		JLabel lblSummary = new JLabel("Summary");
		lblSummary.setHorizontalAlignment(SwingConstants.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
						.addComponent(lblSummary, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblSummary)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(93, Short.MAX_VALUE))
		);

		Object[][] data = {
				{"Project name",		projectMapItem.getProjectName()},
				{"Project status",		ProjectManager.getStrProjectStatus( projectMapItem.getProjectStatus() )},
				{"Reference",			projectMapItem.getOrganism()},
				{"miRBase version",		projectMapItem.getMiRBAseVersion()},
				{"No. of known pre-miRNAs",	projectMapItem.getTotalModelMap().size()},
				{"No. of known mature miRNAs", projectMapItem.getReadedAllObjList().size()}
		};
		UpdatableTableModel tblModel = new UpdatableTableModel( data, new String[]{"Name", "Value"} );

		this.table = new JTable( tblModel ){
			private static final long serialVersionUID = 1L;

	        @Override
	        public boolean isCellEditable(int row, int col) {
	        	return false;
	       }
		};

		this.table.setDefaultRenderer( Object.class, new TableCellRenderer(){
			private DefaultTableCellRenderer DEFAULT_RENDERER =  new DefaultTableCellRenderer();
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if( column == 0 ){	
					c.setFont( c.getFont().deriveFont(Font.BOLD) );
				}else{
					JLabel label = (JLabel) c;
			        label.setHorizontalAlignment(SwingConstants.CENTER);
				}

				table.setRowHeight( 25 );
				
				if (isSelected){
		            c.setBackground(table.getSelectionBackground());
		            c.setForeground(table.getSelectionForeground());
		        }else{
					if (row%2 == 0)	c.setBackground(Color.WHITE);
					else			c.setBackground( new Color(235, 235, 245) );
					c.setForeground( table.getForeground() );
		        }

				return c;
			}
		});
		this.table.setFillsViewportHeight(true);
		this.table.setShowGrid( true );
		this.table.setRowSelectionAllowed(true);

		final TableCellRenderer hr = table.getTableHeader().getDefaultRenderer();

		JTableHeader header = this.table.getTableHeader();
		header.setDefaultRenderer( new TableCellRenderer(){
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = hr.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
				if(c instanceof JLabel) {
			        JLabel label = (JLabel) c;
			        label.setHorizontalAlignment(SwingConstants.CENTER);
			    }

				table.setRowHeight( 25 );
				return c;
			}
		});

		scrollPane.setViewportView(table);
		setLayout(groupLayout);

	}
}
