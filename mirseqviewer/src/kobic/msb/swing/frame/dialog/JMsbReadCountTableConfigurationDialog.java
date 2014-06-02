package kobic.msb.swing.frame.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableCellRenderer;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.MSBReadCountTableColumnStructureModel;
import kobic.msb.server.model.MsbRCTColumnModel;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.component.TableColumnAdjuster;
import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.swing.listener.projectdialog.HeaderCheckBoxHandler;
import kobic.msb.swing.renderer.JMsbChooseMiRnaHeaderRenderer;
import kobic.msb.system.engine.MsbEngine;
import kobic.msb.system.project.ProjectManager;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

public class JMsbReadCountTableConfigurationDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTable 						tblColumn;
	private AlignmentDockingWindowObj	dockWindow;
	private TableColumnAdjuster			tca;
	
	private MSBReadCountTableColumnStructureModel currentRCTColumnStructureModel;

	private JMsbReadCountTableConfigurationDialog remote = JMsbReadCountTableConfigurationDialog.this;
	
	private final String[] columns = new String[]{"Chk", "Order", "Group Name", "Column Name"};

	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public JMsbReadCountTableConfigurationDialog(Frame owner, AbstractDockingWindowObj dockWindow, String title, Dialog.ModalityType modalType) {
		super( owner, title, modalType );
		
		this.dockWindow = (AlignmentDockingWindowObj)dockWindow;

		this.setResizable(false);

		this.currentRCTColumnStructureModel = remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel();

		this.setBounds(100, 100, 450, 300);
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		
		JPanel columnPropertiesPanel = new JPanel();
		columnPropertiesPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED), "Properties of Read count table") );
		GroupLayout gl_contentPanel = new GroupLayout(this.contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(columnPropertiesPanel, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(columnPropertiesPanel, GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
		);

		Object[][] data = null;
		UpdatableTableModel tblModel = new UpdatableTableModel(data, this.columns );

		this.tblColumn = new JTable( tblModel ) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
	        public Class<?> getColumnClass(int column) {
				if( column == 0 )	return Boolean.class;
				return String.class;
	        }

	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return column == 0;
	        }
		};
		
		this.tblColumn.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		this.tblColumn.setShowGrid( true );
		this.tblColumn.setRowSelectionAllowed(true);
		this.tblColumn.setAutoCreateRowSorter(true);
		this.tca = new TableColumnAdjuster(this.tblColumn);
		
		JScrollPane scrollPane = new JScrollPane( this.tblColumn );
		
		JLabel lblColumnSturctureOf = new JLabel("Column sturcture of Read Count Table");
		GroupLayout gl_columnPropertiesPanel = new GroupLayout(columnPropertiesPanel);
		gl_columnPropertiesPanel.setHorizontalGroup(
			gl_columnPropertiesPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_columnPropertiesPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_columnPropertiesPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
						.addComponent(lblColumnSturctureOf))
					.addContainerGap())
		);
		gl_columnPropertiesPanel.setVerticalGroup(
			gl_columnPropertiesPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_columnPropertiesPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblColumnSturctureOf)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
					.addContainerGap())
		);
		columnPropertiesPanel.setLayout(gl_columnPropertiesPanel);
		this.contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Apply");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						SwingUtilities.setWaitCursorFor( remote );

						Map<String, MsbRCTColumnModel> choosedMap = remote.currentRCTColumnStructureModel.getChoosedGroupMap();
						choosedMap.clear();

						remote.currentRCTColumnStructureModel.setChoosedGroupMap( remote.toMakeChoosedColumnStructures() );

						remote.currentRCTColumnStructureModel.setCurrentHeatMapColumnStructure( remote.currentRCTColumnStructureModel.getChoosedGroupMap() );

						ProjectManager manager = MsbEngine.getInstance().getProjectManager();

						try {
							manager.getProjectMap().writeToFile( manager.getSystemFileToGetProjectList() );

						    remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							MsbEngine.logger.error("Error ", e1);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							MsbEngine.logger.error("Error ", e1);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							MsbEngine.logger.error("Error ", e1);
						}
					    
					    SwingUtilities.setDefaultCursorFor( remote );

					    remote.dispose();
					}
				});
				okButton.setActionCommand("Apply");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						remote.dispose();
					}
				});
				cancelButton.setActionCommand("Close");
				buttonPane.add(cancelButton);
			}
		}
		
		this.initTable();
	}
	
	private Map<String, MsbRCTColumnModel> toMakeChoosedColumnStructures() {
		UpdatableTableModel tblModel = (UpdatableTableModel)remote.tblColumn.getModel();
		
		Map<String, MsbRCTColumnModel> map = new LinkedHashMap<String, MsbRCTColumnModel>();

		for(int i=0; i<tblModel.getRowCount(); i++) {
			Object[] record = tblModel.getRecordAt(i);
			if( (Boolean)record[0] == true ) {
				if( record[2].equals("") ) {
					if( record[3].equals( JMsbSysConst.TOTAL_SUM_HEADER ) ) {
						// Total_sum
						MsbRCTColumnModel model = new MsbRCTColumnModel( JMsbSysConst.TOTAL_SUM_HEADER_PREFIX, record[3].toString(), null );
						map.put( record[3].toString(), model );
					}else {
						// Group_sum
						MsbRCTColumnModel model = new MsbRCTColumnModel( JMsbSysConst.GROUP_SUM_HEADER_PREFIX, record[3].toString(), null );
						model.setGroup( remote.currentRCTColumnStructureModel.getOriginalColumnModel( record[3].toString() ).getGroup() );
						map.put( record[3].toString(), model );
					}
				}else {
					// Sample
					MsbRCTColumnModel model = new MsbRCTColumnModel( JMsbSysConst.SAMPLE_HEADER_PREFIX, record[3].toString(), null );

					if( map.containsKey( record[2]) ){
						model.setGroup( record[2].toString() );
						map.get( record[2].toString() ).addChildColumn( model );
					}else {
						MsbRCTColumnModel groupModel = new MsbRCTColumnModel( JMsbSysConst.GROUP_HEADER_PREFIX, record[2].toString(), null );
						groupModel.addChildColumn( model );
						model.setGroup( record[2].toString() );

						map.put( record[2].toString(), groupModel );
					}
				}
			}
		}

		return map;
	}

	private void initTable() {
		Map<String, MsbRCTColumnModel> originalColumnStructure = this.currentRCTColumnStructureModel.getOriginalColumnStructureMap();
		
		UpdatableTableModel tblModel = (UpdatableTableModel)this.tblColumn.getModel();
		
		TableCellRenderer renderer = new JMsbChooseMiRnaHeaderRenderer( this.tblColumn.getTableHeader(), 0);
        
        this.tblColumn.getColumnModel().getColumn(0).setHeaderRenderer(renderer);

        tblModel.addTableModelListener( new HeaderCheckBoxHandler(this.tblColumn, 0) );

		int index = 1;
		Iterator<String> columnNames = originalColumnStructure.keySet().iterator();
		while( columnNames.hasNext() ) {
			String columnName = columnNames.next();

			String columnType = originalColumnStructure.get( columnName ).getColumnType();
			List<MsbRCTColumnModel> subList = originalColumnStructure.get( columnName ).getChildColumnList();

			if( !columnType.equals( JMsbSysConst.GROUP_HEADER_PREFIX ) ) {
				boolean isContain = this.currentRCTColumnStructureModel.getChoosedGroupMap().containsKey( columnName );
				if( isContain )	{
					tblModel.addRow( new Object[]{ Boolean.valueOf(true), Integer.valueOf(index), "", columnName } );
				}else {
					tblModel.addRow( new Object[]{ Boolean.valueOf(false), Integer.valueOf(index), "", columnName } );
				}
				index++;
			}else {
				// Group
				for(int i=0; i<subList.size(); i++) {
					String subClassName = subList.get(i).getColumnId();
					
					boolean isContain = false;
					if( this.currentRCTColumnStructureModel.getChoosedGroupMap().containsKey( columnName ) ) {
						if( this.currentRCTColumnStructureModel.getChoosedGroupMap().get( columnName).hasSubNode( subClassName ) ) {
							isContain = true;
						}
					}

					if( isContain )	{
						tblModel.addRow( new Object[]{ Boolean.valueOf(true), Integer.valueOf(index), columnName, subClassName } );
					}else {
						tblModel.addRow( new Object[]{ Boolean.valueOf(false), Integer.valueOf(index), columnName, subClassName } );
					}
					index++;
				}
			}
		}
		this.tca.adjustColumns();
	}
}
