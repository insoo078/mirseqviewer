package kobic.msb.swing.frame.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTable;

import kobic.com.util.Utilities;
import kobic.msb.server.model.Model;
import kobic.msb.server.model.MsbFilterModel;
import kobic.msb.server.model.MsbFilterModel.FilterModel;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.component.TableColumnAdjuster;
import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.swing.editor.JMsbKeywordCellEditor;
import kobic.msb.swing.editor.JMsbOperatorCellEditor;
import kobic.msb.system.engine.MsbEngine;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class JMsbFilterDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTable tblFilters;
	
	private int selectedIndex;

	private AlignmentDockingWindowObj dockWindow;
	
	private TableColumnAdjuster		tca;
	
	private JMsbFilterDialog		remote = JMsbFilterDialog.this;

	private final String[] 			headers = new String[]{"Condition", "Filter", "Operator", "Keyword"};

	
	/*** Over JDK 1.7 ***/
//	final JComboBox<String> comboBox = new JComboBox<String>();
	/*** Under JDK 1.7 ***/
	String[] filterTypes = new String[]{"DEFAULT", "AND", "OR"};
	final JComboBox comboBox = new JComboBox( filterTypes );

	/*** Over JDK 1.7 ***/
//	JComboBox<String> comboFilter = new JComboBox<String>();
	/*** Under JDK 1.7 ***/
	final JComboBox comboFilter = new JComboBox( JMsbFindReadDailog.ITEMS_FILTER );
	/**
	 * Create the dialog.
	 */
	public JMsbFilterDialog(AlignmentDockingWindowObj dockWindow, Model model, String title, Dialog.ModalityType modalType) {
		super(dockWindow.getMainFrame(), title, modalType);
		
		this.dockWindow = dockWindow;
		
		this.selectedIndex = -1;

		this.setBounds(100, 100, 624, 300);
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		
		JLabel lblAddFilterTo	= new JLabel("Add filter(s) to filter by");
		
		JButton btnAdd			= new JButton("+");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UpdatableTableModel tableModel = (UpdatableTableModel) remote.tblFilters.getModel();

				if( tableModel.getRowCount() == 0 )		tableModel.addRow( new Object[]{ "DEFAULT", "count", "<", ""} );
				else									tableModel.addRow( new Object[]{ "AND", "count", "<", ""} );
			}
		});
		JButton btnRemove		= new JButton("-");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idx = remote.tblFilters.getSelectedRow();

				UpdatableTableModel tableModel = (UpdatableTableModel) remote.tblFilters.getModel();

				if( idx >=0 && idx < tableModel.getRowCount() ) {
					if( tableModel.getRecordAt(idx)[0].equals("DEFAULT") && tableModel.getRowCount() > 1 ) {
						tableModel.getRecordAt(idx+1)[0] = "DEFAULT";
					}
					tableModel.removeAt( idx );
				}
			}
		});

		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblAddFilterTo, GroupLayout.PREFERRED_SIZE, 168, GroupLayout.PREFERRED_SIZE)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnRemove, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblAddFilterTo)
					.addGap(5)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnAdd)
						.addComponent(btnRemove))
					.addContainerGap(8, Short.MAX_VALUE))
		);

		UpdatableTableModel tableModel = new UpdatableTableModel( null, this.headers );
		
		this.tblFilters = new JTable( tableModel ) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return true;
			}
		};
		this.tblFilters.setAutoCreateColumnsFromModel( false ); 
		this.tblFilters.setFillsViewportHeight(true);
		this.tblFilters.setAutoCreateRowSorter(false);
		this.tblFilters.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		tableModel.addTableModelListener( new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				// TODO Auto-generated method stub
				int row = e.getFirstRow();
				int col = e.getColumn();
				
				if( col == 1 ){
					String filter = remote.tblFilters.getValueAt(row, col).toString();
					
					if( filter.equals( "sequence" ) )			{
						remote.tblFilters.setValueAt("start with", row, col+1);
						remote.tblFilters.setValueAt("", row, col+2);
					}
					else if( filter.equals("reverse reads") )	{
						remote.tblFilters.setValueAt("remove", row, col+1);
						remote.tblFilters.setValueAt("true", row, col+2);
					}else{
						remote.tblFilters.setValueAt("<", row, col+1);
						remote.tblFilters.setValueAt("", row, col+2);
					}
				}
			}
		});
		
		scrollPane.setViewportView(tblFilters);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MsbFilterModel msbFilterModel = remote.dockWindow.getCurrentModel().getProjectMapItem().getMsbFilterModel();
						msbFilterModel.clear();

						UpdatableTableModel tableModel = (UpdatableTableModel) remote.tblFilters.getModel();

						for(int i=0; i<tableModel.getRowCount(); i++) {
							Object[] objs = tableModel.getRecordAt(i);

							// value check
							if( !(objs[1].equals("sequence") || objs[1].equals("reverse reads")) && objs[2].equals("between") ) {
								if(objs[3].toString().contains(",")){
									String[] args = objs[3].toString().split(",");
									
									if( !(Utilities.isNumeric( args[0] ) && Utilities.isNumeric( args[1] ) ) ) {
										JOptionPane.showMessageDialog( remote, "between operator needs 2 numeric value");
										MsbEngine.logger.error("between operator needs 2 numeric value");
										return;
									}
								}else {
									JOptionPane.showMessageDialog( remote, "between operator needs 2 numbers with comma");
									MsbEngine.logger.error("between operator needs 2 numbers with comma");
									return;
								}
							}else if( !(objs[1].equals("sequence") || objs[1].equals("reverse reads")) && !objs[2].equals("between") ) {
								if( !Utilities.isNumeric( objs[3].toString() ) ) {
									JOptionPane.showMessageDialog( remote, objs[1] + " operator needs numeric value");
									MsbEngine.logger.error(objs[1] + " operator needs numeric value");
									return;
								}
							}
							// value check

							int filterType = MsbFilterModel.AND;
							if( objs[0].toString().equals("OR") )		filterType = MsbFilterModel.OR;
							if( objs[0].toString().equals("DEFAULT"))	filterType = MsbFilterModel.DEFAULT;

							msbFilterModel.addModel( i, filterType, objs[1].toString(), objs[2].toString(), objs[3].toString() );
						}

						remote.dockWindow.getMainFrame().getToolBar().updateFilters( msbFilterModel );
						remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );

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
		
		this.tblFilters.getSelectionModel().addListSelectionListener( new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				remote.selectedIndex = remote.tblFilters.getSelectedRow();
			}
		});
		
		this.tca = new TableColumnAdjuster( this.tblFilters );
		
		this.initialize();
	}

	private void initialize() {
		MsbFilterModel msbFilterModel = this.dockWindow.getCurrentModel().getProjectMapItem().getMsbFilterModel();
		
		final JMsbKeywordCellEditor kce = new JMsbKeywordCellEditor();
		final JMsbOperatorCellEditor oce = new JMsbOperatorCellEditor();

		this.comboBox.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if( remote.selectedIndex != 0 &&  e.getItem().toString().equals("DEFAULT") ) {
						JOptionPane.showMessageDialog( remote, "Sorry! 'DEFAULT' keyword must be selected at first filter");
						remote.comboBox.setSelectedIndex(1);
						return;
					}else if( remote.selectedIndex == 0 && !e.getItem().toString().equals("DEFAULT") ) {
						JOptionPane.showMessageDialog( remote, "Sorry! you can not change first filter condition");
						remote.comboBox.setSelectedIndex(0);
						return;
					}
				}
			}
		});

		TableColumn column1 = remote.tblFilters.getColumnModel().getColumn(0);
		column1.setCellEditor(new DefaultCellEditor(this.comboBox) );

		TableColumn column2 = remote.tblFilters.getColumnModel().getColumn(1);
		column2.setCellEditor(new DefaultCellEditor(this.comboFilter) );

		TableColumn column3 = remote.tblFilters.getColumnModel().getColumn(2);
		column3.setCellEditor( oce );
		
		TableColumn column4 = remote.tblFilters.getColumnModel().getColumn(3);
		column4.setCellEditor( kce );

		UpdatableTableModel tableModel = (UpdatableTableModel) this.tblFilters.getModel();
		tableModel.removeAll();

		for(int i=0; i<msbFilterModel.size(); i++) {
			FilterModel filterModel = msbFilterModel.getFilterModelAt(i);

			String filterType = null;
			if( filterModel.getFilterType() == MsbFilterModel.AND )		filterType = "AND";
			else if(filterModel.getFilterType() == MsbFilterModel.OR)	filterType = "OR";
			else														filterType = "DEFAULT";

			String filter		= filterModel.getFilter();

			String operator		= filterModel.getOperator();
			
			String keyword		= filterModel.getKeyword();
			
			Object[] obArr		= new Object[]{ filterType, filter, operator, keyword };
			
			tableModel.addRow( obArr );
		}
		this.tblFilters.revalidate();

		this.tca.adjustColumns();
	}
}
