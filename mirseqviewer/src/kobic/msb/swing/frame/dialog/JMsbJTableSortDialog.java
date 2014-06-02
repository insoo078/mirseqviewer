package kobic.msb.swing.frame.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTable;

import kobic.msb.common.SwingConst.Sorts;
import kobic.msb.server.model.Model;
import kobic.msb.server.model.MsbSortModel;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.component.TableColumnAdjuster;
import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.system.engine.MsbEngine;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JMsbJTableSortDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	private JTable					tblSortField;
	private JButton					btnAdd;
	private JButton					btnRemove;
	
	private TableColumnAdjuster		tca;
	
//	private Model					model;
	
	private JMsbJTableSortDialog	remote = JMsbJTableSortDialog.this;

	private final String[] 			headers = new String[]{"", "Column", "Sort On", "Order"};
	
	private AlignmentDockingWindowObj dockWindow;
	/**
	 * Create the dialog.
	 */
	public JMsbJTableSortDialog(AlignmentDockingWindowObj dockWindow, Model model, String title, Dialog.ModalityType modalType) {
		super(dockWindow.getMainFrame(), title, modalType);

//		this.model		= model;
		this.dockWindow	= dockWindow;

		this.setResizable(false);

		this.setBounds(100, 100, 450, 270);
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();

		this.btnAdd		= new JButton("+");
		this.btnAdd.addActionListener(new ActionListener() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void actionPerformed(ActionEvent e) {
				UpdatableTableModel tableModel = (UpdatableTableModel) remote.tblSortField.getModel();

				/*** Over JDK 1.7 ***/
//				JComboBox<String> comboBox = new JComboBox<String>();
				/*** Under JDK 1.7 ***/
				JComboBox comboBox = new JComboBox();
				List<String> sampleList = remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel().getHeatMapHeader();
				for(int i=0; i<sampleList.size(); i++)
					comboBox.addItem( sampleList.get(i) );

				/*** Over JDK 1.7 ***/
//				JComboBox<Object> cboOrder = new JComboBox<Object>();
				/*** Under JDK 1.7 ***/
				JComboBox cboOrder = new JComboBox();
				for(Sorts sort:Sorts.values()) {
					cboOrder.addItem( sort );
				}

				TableColumn column1 = remote.tblSortField.getColumnModel().getColumn(1);
				column1.setCellEditor(new DefaultCellEditor(comboBox) );
				
				TableColumn column2 = remote.tblSortField.getColumnModel().getColumn(3);
				column2.setCellEditor(new DefaultCellEditor(cboOrder) );
				
//				TableComboBoxCellRenderer renderer1 = new TableComboBoxCellRenderer( comboBox );
//				TableComboBoxCellRenderer renderer2 = new TableComboBoxCellRenderer( cboOrder );
//				column1.setCellRenderer(renderer1);
//				column2.setCellRenderer(renderer2);

				if( tableModel.getRowCount() > 0 )	tableModel.addRow( new Object[]{"Then by", null, "Values", null} );
				else								tableModel.addRow( new Object[]{"Sort by", null, "Values", null} );
			}
		});
		this.btnRemove	= new JButton("-");
		this.btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idx = remote.tblSortField.getSelectedRow();

				UpdatableTableModel tableModel = (UpdatableTableModel) remote.tblSortField.getModel();
				tableModel.removeAt( idx );
			}
		});
		
		JLabel lblAddLevelsTo = new JLabel("Add levels to sort by :");

		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnRemove, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblAddLevelsTo)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(12)
					.addComponent(lblAddLevelsTo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnAdd)
						.addComponent(btnRemove))
					.addContainerGap(17, Short.MAX_VALUE))
		);

		UpdatableTableModel tableModel = new UpdatableTableModel( null, this.headers );
		this.tblSortField = new JTable( tableModel ){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

//			@Override
//	        public Class<?> getColumnClass(int column) {
//				Object obj = getValueAt(0, column);
//				if( obj != null )
//					return getValueAt(0, column).getClass();
//				return null;
//	        }

	        @Override
	        public boolean isCellEditable(int row, int column) {
	        	if( column > 0 )	return true;
	        	return false;
	        }
		};

		this.tblSortField.setAutoCreateColumnsFromModel( false ); 
		this.tblSortField.setFillsViewportHeight(true);
		this.tblSortField.setAutoCreateRowSorter(false);
		this.tblSortField.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		this.tca = new TableColumnAdjuster( this.tblSortField );
		
		DefaultTableCellRenderer dtcr = ((DefaultTableCellRenderer)this.tblSortField.getTableHeader().getDefaultRenderer());
		dtcr.setHorizontalAlignment(JLabel.CENTER);
		
		scrollPane.setViewportView(this.tblSortField);
		
		this.initialize();

		this.contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						UpdatableTableModel tableModel = (UpdatableTableModel) remote.tblSortField.getModel();
						
						remote.dockWindow.getCurrentModel().getProjectMapItem().getMsbSortModel().clear();
						for(int i=0; i<tableModel.getRowCount(); i++) {
							Object[] columns = tableModel.getRecordAt(i);
							
							Sorts sort = (Sorts) columns[3];

							remote.dockWindow.getCurrentModel().getProjectMapItem().getMsbSortModel().addSortModel( i, columns[1].toString(), sort.getValue() );
						}

						try {
							remote.dockWindow.getCurrentModel().sortBySortModel( remote.dockWindow.getCurrentModel().getProjectMapItem().getMsbSortModel() );
							
							remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
						}catch(Exception exp) {
							MsbEngine.logger.error( "Error : ", exp );
						}

						remote.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						remote.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		/*** Over JDK 1.7 ***/
//		JComboBox<String> comboBox = new JComboBox<String>();
		/*** Under JDK 1.7 ***/
		JComboBox comboBox = new JComboBox();
		List<String> sampleList = this.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel().getHeatMapHeader();
		for(int i=0; i<sampleList.size(); i++)
			comboBox.addItem( sampleList.get(i) );

		/*** Over JDK 1.7 ***/
//		JComboBox<Object> cboOrder = new JComboBox<Object>();
		/*** Under JDK 1.7 ***/
		JComboBox cboOrder = new JComboBox();
		for(Sorts sort:Sorts.values()) {
			cboOrder.addItem( sort );
		}
		
		TableColumn column1 = this.tblSortField.getColumnModel().getColumn(1);
		column1.setCellEditor(new DefaultCellEditor(comboBox) );
//		column1.setCellEditor( new JIDCellEditor( comboBox ) );
		
		TableColumn column2 = this.tblSortField.getColumnModel().getColumn(3);
		column2.setCellEditor(new DefaultCellEditor(cboOrder) );
		
//		final TableCellRenderer renderer = this.tblSortField.getDefaultRenderer(Object.class);
//
//		TableComboBoxCellRenderer renderer1 = new TableComboBoxCellRenderer( renderer );
////		TableComboBoxCellRenderer renderer2 = new TableComboBoxCellRenderer( cboOrder );
//		column1.setCellRenderer(renderer1);
//		column2.setCellRenderer( );

		if( !this.dockWindow.getCurrentModel().getProjectMapItem().getMsbSortModel().hasSortBy() ) {
			UpdatableTableModel tableModel = (UpdatableTableModel) this.tblSortField.getModel();

			tableModel.addRow( new Object[]{"Sort by", comboBox.getSelectedObjects()[0], "Values", Sorts.values()[0]} );
		}else {
			UpdatableTableModel tableModel = (UpdatableTableModel) this.tblSortField.getModel();
			
			MsbSortModel sortModel = this.dockWindow.getCurrentModel().getProjectMapItem().getMsbSortModel();
			for(int i=0; i<sortModel.size(); i++) {
				Object[] array = sortModel.getSortModelArray(i);
				
				/*** Over JDK 1.7 ***/
//				if( (int)array[0] == 0 )	array[0] = "Sort By";
				/*** Under JDK 1.7 ***/
				if( Integer.valueOf( array[0].toString() ) == 0 )	array[0] = "Sort By";
				else												array[0] = "Then By";
				tableModel.addRow( array );
			}
		}
		
		this.tca.adjustColumns();
	}
}
