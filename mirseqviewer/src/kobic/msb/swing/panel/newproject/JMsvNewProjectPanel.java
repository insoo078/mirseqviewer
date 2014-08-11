package kobic.msb.swing.panel.newproject;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import kobic.msb.common.SwingConst;
import kobic.msb.swing.component.TableColumnAdjuster;
import kobic.msb.swing.frame.dialog.JCommonNewProjectDialog;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;

public class JMsvNewProjectPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JCommonNewProjectDialog	owner;
	private JTable					tblSampleList;
	private TableColumnAdjuster		tca;

	private JComboBox cmbMngGroup;
	private JTable table;

	/**
	 * Create the panel.
	 */
	public JMsvNewProjectPanel() {
		
		JPanel groupPanel = new JPanel();
		
		JPanel samplePanel = new JPanel();
		
		this.cmbMngGroup = new JComboBox();
		this.cmbMngGroup.setToolTipText("Group ID");
		this.cmbMngGroup.setEditable(true);
		
		Object[][] data		= null;
		DefaultTableModel tblModel = new DefaultTableModel( data, SwingConst.SAMPLE_TABLE_COLUMN );

		this.tblSampleList = new JTable( tblModel ){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
	        public boolean isCellEditable(int row, int col) {
	        	return false;
	       }
		};
		this.tblSampleList.setPreferredScrollableViewportSize(new Dimension(500, 70));
		this.tblSampleList.setFillsViewportHeight(true);
		this.tblSampleList.setAutoCreateRowSorter(true);
		this.tblSampleList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		this.tca = new TableColumnAdjuster( this.tblSampleList );

		DefaultTableCellRenderer dtcr = ((DefaultTableCellRenderer)tblSampleList.getTableHeader().getDefaultRenderer());
		dtcr.setHorizontalAlignment(JLabel.CENTER);

		
		groupPanel.setBorder( BorderFactory.createTitledBorder( SwingConst.BORDER_LOWERED_ETCHED, "Groups" ) );
		samplePanel.setBorder( BorderFactory.createTitledBorder( SwingConst.BORDER_LOWERED_ETCHED, "Samples" ) );
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(samplePanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
						.addComponent(groupPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(groupPanel, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(samplePanel, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JScrollPane scrollPane = new JScrollPane( this.tblSampleList );
		
		JButton btnFile = new JButton("File...");
		GroupLayout gl_samplePanel = new GroupLayout(samplePanel);
		gl_samplePanel.setHorizontalGroup(
			gl_samplePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_samplePanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_samplePanel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
						.addComponent(btnFile))
					.addContainerGap())
		);
		gl_samplePanel.setVerticalGroup(
			gl_samplePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_samplePanel.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnFile)
					.addContainerGap())
		);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		samplePanel.setLayout(gl_samplePanel);
		
		JLabel lblGroupId = new JLabel("Group ID");
		
		JButton btnAdd = new JButton("Add");
		
		JButton btnEdit = new JButton("Edit");
		
		JButton btnDel = new JButton("Del");

		GroupLayout gl_groupPanel = new GroupLayout(groupPanel);
		gl_groupPanel.setHorizontalGroup(
			gl_groupPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_groupPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblGroupId)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(cmbMngGroup, 0, 185, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnAdd)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnEdit)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnDel)
					.addContainerGap())
		);
		gl_groupPanel.setVerticalGroup(
			gl_groupPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_groupPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_groupPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblGroupId)
						.addComponent(cmbMngGroup, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnDel)
						.addComponent(btnEdit)
						.addComponent(btnAdd))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupPanel.setLayout(gl_groupPanel);
		setLayout(groupLayout);

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Test");

		JMsvNewProjectPanel panel = new JMsvNewProjectPanel();
		frame.add(panel);
		
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize(800, 600);
		
		frame.setVisible( true );
	}
}
