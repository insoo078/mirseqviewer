package kobic.msb.swing.frame.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import kobic.com.normalization.Normalization;
import kobic.msb.common.ImageConstant;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.MSBReadCountTableColumnStructureModel;
import kobic.msb.server.model.MsbRCTColumnModel;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.component.TreeTransferHandler;
import kobic.msb.swing.listener.key.NumericKeyListener;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;
import kobic.msb.system.project.ProjectManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JComboBox;

@SuppressWarnings("rawtypes")
public class JMsbReadCountTableConfigDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	
	private JTree				treeColumnStructure;
	private JTree				treeUnusedSamples;
	
	private TreePath			currentTreePathToRemove;
	private TreePath			currentTreePathToAdd;
	
	private JFormattedTextField txtMissingValue;
	private JPanel				totalSumColorPanel;
	private JPanel				groupSumColorPanel;
	private JPanel				missingValueColorPanel;
	/*** Over JDK 1.7 ***/
//	private JComboBox<String>	cmbNormalizationMethod;
	/*** Under JDK 1.7 ***/
	private JComboBox	cmbNormalizationMethod;
	
	private AlignmentDockingWindowObj	dockWindow;
	private ProjectConfiguration		config;
	
	private MSBReadCountTableColumnStructureModel currentRCTColumnStructureModel;

	private JMsbReadCountTableConfigDialog remote = JMsbReadCountTableConfigDialog.this;

	/**
	 * Create the dialog.
	 */
	@SuppressWarnings("unchecked")
	public JMsbReadCountTableConfigDialog(Frame owner, AbstractDockingWindowObj dockWindow, String title, Dialog.ModalityType modalType) {
		super( owner, title, modalType );

		this.dockWindow = (AlignmentDockingWindowObj)dockWindow;

		this.config		= MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.dockWindow.getProjectName() ).getProjectConfiguration();

		this.setResizable(false);

		this.currentRCTColumnStructureModel = remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel();

		this.setBounds(100, 100, 578, 485);
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.getContentPane().add(this.contentPanel, BorderLayout.CENTER);

		JPanel columnPropertiesPanel = new JPanel();
		columnPropertiesPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED), "Properties of Read count table") );
		
		JPanel heatmapBackgroundColorPanel = new JPanel();
		heatmapBackgroundColorPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED), "Intensitiy color") );
		
		JPanel normalizationPanel = new JPanel();
		normalizationPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED), "Normalization"));

		GroupLayout gl_contentPanel = new GroupLayout( this.contentPanel );
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(normalizationPanel, GroupLayout.PREFERRED_SIZE, 556, GroupLayout.PREFERRED_SIZE)
						.addComponent(heatmapBackgroundColorPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
						.addComponent(columnPropertiesPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(columnPropertiesPanel, GroupLayout.PREFERRED_SIZE, 231, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(heatmapBackgroundColorPanel, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(normalizationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(31))
		);
		
		JLabel lblMissingValue_1 = new JLabel("Missing Value");
		
		this.missingValueColorPanel = new JPanel();
		this.missingValueColorPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );
		this.missingValueColorPanel.setBackground( this.config.get_missing_value_color_() );
		this.missingValueColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( remote.missingValueColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		
		JLabel lblTotalsum = new JLabel("Total_Sum");
		
		this.totalSumColorPanel = new JPanel();
		this.totalSumColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		this.totalSumColorPanel.setBackground( this.config.get_total_sum_color_() );
		this.totalSumColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( remote.totalSumColorPanel );
					dialog.setVisible( true );
				}
			}
		});

		JLabel lblGroupsum = new JLabel("Group_Sum");

		this.groupSumColorPanel = new JPanel();
		this.groupSumColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		this.groupSumColorPanel.setBackground( this.config.get_group_sum_color_() );
		this.groupSumColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( remote.groupSumColorPanel );
					dialog.setVisible( true );
				}
			}
		});

		GroupLayout gl_heatmapBackgroundColorPanel = new GroupLayout(heatmapBackgroundColorPanel);
		gl_heatmapBackgroundColorPanel.setHorizontalGroup(
			gl_heatmapBackgroundColorPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_heatmapBackgroundColorPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblMissingValue_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(missingValueColorPanel, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblTotalsum)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(totalSumColorPanel, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblGroupsum, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(groupSumColorPanel, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(130, Short.MAX_VALUE))
		);
		gl_heatmapBackgroundColorPanel.setVerticalGroup(
			gl_heatmapBackgroundColorPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_heatmapBackgroundColorPanel.createSequentialGroup()
					.addGroup(gl_heatmapBackgroundColorPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_heatmapBackgroundColorPanel.createSequentialGroup()
							.addGap(14)
							.addComponent(lblMissingValue_1))
						.addGroup(gl_heatmapBackgroundColorPanel.createSequentialGroup()
							.addGap(14)
							.addComponent(lblTotalsum))
						.addGroup(gl_heatmapBackgroundColorPanel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_heatmapBackgroundColorPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(totalSumColorPanel, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
								.addComponent(missingValueColorPanel, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
								.addComponent(groupSumColorPanel, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_heatmapBackgroundColorPanel.createSequentialGroup()
							.addGap(14)
							.addComponent(lblGroupsum)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		heatmapBackgroundColorPanel.setLayout(gl_heatmapBackgroundColorPanel);
		
		JLabel lblMethod = new JLabel("Method");

		/*** Over JDK 1.7 ***/
//		this.cmbNormalizationMethod = new JComboBox<String>();
		/*** Under JDK 1.7 ***/
		this.cmbNormalizationMethod = new JComboBox();
		for(int i=0; i<Normalization.methods.length; i++) {
			this.cmbNormalizationMethod.addItem( Normalization.methods[i] );
		}
		this.cmbNormalizationMethod.setSelectedItem( this.config.getNormalizationMethod() );
		
		JLabel lblMissingValue = new JLabel("Missing Value");
		
		this.txtMissingValue = new JFormattedTextField();
		this.txtMissingValue.setText( Double.toString( this.config.getMissingValue()  ) );
		this.txtMissingValue.setColumns(10);
		this.txtMissingValue.addKeyListener( new NumericKeyListener() );

		GroupLayout gl_normalizationPanel = new GroupLayout(normalizationPanel);
		gl_normalizationPanel.setHorizontalGroup(
			gl_normalizationPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_normalizationPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblMethod)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(cmbNormalizationMethod, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblMissingValue)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtMissingValue, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(159, Short.MAX_VALUE))
		);
		gl_normalizationPanel.setVerticalGroup(
			gl_normalizationPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_normalizationPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_normalizationPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMethod)
						.addComponent(cmbNormalizationMethod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMissingValue)
						.addComponent(txtMissingValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(9, Short.MAX_VALUE))
		);
		normalizationPanel.setLayout(gl_normalizationPanel);
		columnPropertiesPanel.setLayout(null);

		JScrollPane unsuedListPane = new JScrollPane( );
		unsuedListPane.setBounds(335, 50, 203, 160);
		columnPropertiesPanel.add(unsuedListPane);
		
		this.treeUnusedSamples = new JTree( JMsbReadCountTableConfigDialog.createUnusedSampleTreeModel(this.currentRCTColumnStructureModel) );
		this.treeUnusedSamples.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) ) {
					remote.currentTreePathToAdd = remote.treeUnusedSamples.getPathForLocation(e.getX(), e.getY());
				}
			}
		});
		unsuedListPane.setViewportView(treeUnusedSamples);
		this.treeUnusedSamples.setRootVisible( false );
		
		JLabel lblUnusedSamples = new JLabel("Unused column(s)");
		lblUnusedSamples.setBounds(347, 33, 119, 16);
		columnPropertiesPanel.add(lblUnusedSamples);
		
		JButton btnRemoveSample = new JButton(ImageConstant.rightArrowIcon);
		btnRemoveSample.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( remote.currentTreePathToRemove.getLastPathComponent() != null ) {
					String choosedItem = remote.currentTreePathToRemove.getLastPathComponent().toString();

					MsbRCTColumnModel column = remote.currentRCTColumnStructureModel.getChoosedGroupMap().get( choosedItem );
					if( column == null )	{
						String parentItem = remote.currentTreePathToRemove.getParentPath().getLastPathComponent().toString();
						column = remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel().getChoosedGroupMap().get( parentItem );
						remote.addToRemoveTreeAtLast( parentItem, choosedItem );
					}else {
						// IF Group is removed, Group sum column is automatically removing
						if( column.getColumnType().equals( JMsbSysConst.GROUP_HEADER_PREFIX ) ) {
							remote.addToRemoveTree( choosedItem + JMsbSysConst.SUM_SUFFIX );
						}
						remote.addToRemoveTree( choosedItem );
					}
					
					// If there is not sample in choosed column, the "Total_sum" column also remove
					if( remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel().getSampleSize() < 1 ) {
						if( remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel().getChoosedGroupMap().containsKey( JMsbSysConst.TOTAL_SUM_HEADER ) )
							remote.addToRemoveTree( JMsbSysConst.TOTAL_SUM_HEADER );
					}
				}
			}
		});
		btnRemoveSample.setBounds(283, 109, 47, 29);
		columnPropertiesPanel.add(btnRemoveSample);
		
		JButton btnAddSample = new JButton(ImageConstant.leftArrowIcon);
		btnAddSample.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( remote.currentTreePathToAdd.getLastPathComponent() != null ) {
					String choosedItem = remote.currentTreePathToAdd.getLastPathComponent().toString();

					MsbRCTColumnModel column = remote.currentRCTColumnStructureModel.getUnusedGroupMap().get( choosedItem );

					if( column == null )	{
						String parentItem = remote.currentTreePathToAdd.getParentPath().getLastPathComponent().toString();
						column = remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel().getChoosedGroupMap().get( parentItem );
						remote.addToUsedTreeAtLast( parentItem, choosedItem );
					}else {
						if( column.getColumnType().equals( JMsbSysConst.GROUP_SUM_HEADER_PREFIX) ) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) remote.treeColumnStructure.getModel().getRoot();
							
							boolean isGoing = false;
							for(int i=0; i<node.getChildCount(); i++) {
								DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) node.getChildAt( i );
								if( column.getGroup().equals( tmpNode.getUserObject() ) && tmpNode.getChildCount() > 0 ) {
									isGoing = true;
									break;
								}
							}
							if( isGoing ) {
								remote.addToUsedTree( choosedItem );
							}else {
								JOptionPane.showMessageDialog( remote, "There are not samples in " + column.getGroup() + " group", "Warning", JOptionPane.ERROR_MESSAGE );
							}
						}else {
							remote.addToUsedTree( choosedItem );
						}
					}
				}
			}
		});
		btnAddSample.setBounds(237, 109, 47, 29);
		columnPropertiesPanel.add(btnAddSample);
		
		JButton btnSampleUp = new JButton("Up");
		btnSampleUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel)remote.treeColumnStructure.getModel();

				DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)remote.currentTreePathToRemove.getLastPathComponent();

				int index = model.getIndexOfChild( model.getRoot(), dmtn );
				
				if( index > 0) {
					int newIndex = index-1;
					model.insertNodeInto( dmtn, (DefaultMutableTreeNode)model.getRoot(), newIndex );
				}else if( index < 0 ) {
					DefaultMutableTreeNode pdmtn = (DefaultMutableTreeNode)remote.currentTreePathToRemove.getParentPath().getLastPathComponent();
					
					if( pdmtn.getChildCount() > 1 ) {
						index = model.getIndexOfChild( pdmtn, dmtn );
						if( index > 0 ) {
							int newIndex = index-1;
							model.insertNodeInto( dmtn, pdmtn, newIndex );
						}
					}
				}

				model.reload();

				remote.makeChoosedColumnHashMapByJTree();
				
				SwingUtilities.expandTree( remote.treeColumnStructure );
			}
		});

		btnSampleUp.setBounds(237, 83, 95, 29);
		columnPropertiesPanel.add(btnSampleUp);
		
		JButton btnSampleDown = new JButton("Down");
		btnSampleDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel)remote.treeColumnStructure.getModel();

				DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)remote.currentTreePathToRemove.getLastPathComponent();

				int index = model.getIndexOfChild(model.getRoot(), dmtn);

				if( index != -1 ) {
					int newIndex = index+1;
					int nChild = model.getChildCount( model.getRoot() );
					if( nChild > newIndex ) {
						model.insertNodeInto(dmtn, (DefaultMutableTreeNode)model.getRoot(), newIndex );
					}
				}else {
					DefaultMutableTreeNode pdmtn = (DefaultMutableTreeNode)remote.currentTreePathToRemove.getParentPath().getLastPathComponent();
					
					if( pdmtn.getChildCount() > 1 ) {
						index = model.getIndexOfChild( pdmtn, dmtn );
						if( index >= 0 ) {
							int newIndex = index+1;
							if( pdmtn.getChildCount() <= newIndex )	newIndex = pdmtn.getChildCount() - 1;

							model.insertNodeInto( dmtn, pdmtn, newIndex );
						}
					}
				}
				
				model.reload();
				
				remote.makeChoosedColumnHashMapByJTree();
				
				SwingUtilities.expandTree( remote.treeColumnStructure );
			}
		});
		btnSampleDown.setBounds(237, 140, 95, 29);
		columnPropertiesPanel.add(btnSampleDown);
		JScrollPane columnStructureListPane = new JScrollPane( );
		columnStructureListPane.setBounds(20, 51, 216, 159);
		columnPropertiesPanel.add(columnStructureListPane);
		
		this.treeColumnStructure = new JTree( JMsbReadCountTableConfigDialog.createColumnStructureTreeModel(this.currentRCTColumnStructureModel) );
		this.treeColumnStructure.setDragEnabled(true);
		this.treeColumnStructure.setDropMode( DropMode.ON_OR_INSERT );
	    this.treeColumnStructure.setTransferHandler( new TreeTransferHandler( this ) );
	    this.treeColumnStructure.getSelectionModel().setSelectionMode( TreeSelectionModel.CONTIGUOUS_TREE_SELECTION );

		this.treeColumnStructure.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) ) {
					remote.currentTreePathToRemove = remote.treeColumnStructure.getPathForLocation(e.getX(), e.getY());
				}
			}
		});
		this.treeColumnStructure.setRootVisible( false );
		columnStructureListPane.setViewportView( this.treeColumnStructure );
		
		JLabel lblHeatColumnStructure = new JLabel("Column structure of Read count table");
		lblHeatColumnStructure.setBounds(20, 33, 255, 16);
		columnPropertiesPanel.add(lblHeatColumnStructure);

		this.contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton applyButton = new JButton("Apply");
				applyButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						remote.makeChoosedColumnHashMapByJTree();
						remote.makeUnUsedColumnHashMapByJTree();

						String missingValue = remote.txtMissingValue.getText();
						
						try {
							Double.parseDouble( missingValue );
						}catch(Exception exp) {
							JOptionPane.showMessageDialog( remote, "Missing Value is not a number", "Warning", JOptionPane.ERROR_MESSAGE);
							return;
						}

						if( remote.currentRCTColumnStructureModel.getSampleSize() == 0 ) {
							JOptionPane.showMessageDialog( remote, "You have to choice at least one sample", "Warning", JOptionPane.ERROR_MESSAGE);
							return;
						}else if( missingValue.isEmpty() ) {
							JOptionPane.showMessageDialog( remote, "Missing Value is empty", "Warning", JOptionPane.ERROR_MESSAGE);
							return;
						}else {
							SwingUtilities.setWaitCursorFor( remote );
							// To apply header style
						    remote.currentRCTColumnStructureModel.setCurrentHeatMapColumnStructure( remote.currentRCTColumnStructureModel.getChoosedGroupMap() );

						    // To apply Read Count Table style
						    remote.config.set_total_sum_color_( remote.totalSumColorPanel.getBackground() );
						    remote.config.set_group_sum_color_( remote.groupSumColorPanel.getBackground() );
						    remote.config.set_missing_value_color_( remote.missingValueColorPanel.getBackground() );
						    remote.config.setNormalizationMethod( remote.cmbNormalizationMethod.getSelectedItem().toString() );
						    remote.config.setMissingValue( Double.valueOf( remote.txtMissingValue.getText() ) );

						    ProjectManager manager = MsbEngine.getInstance().getProjectManager();

						    remote.config.writeConfigFile();
						    try {
								manager.getProjectMap().writeToFile( manager.getSystemFileToGetProjectList() );
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
						    
						    remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
						    
						    SwingUtilities.setDefaultCursorFor( remote );
						    remote.dispose();
						}
					}
				});
				applyButton.setActionCommand("Apply");
				buttonPane.add(applyButton);
				getRootPane().setDefaultButton(applyButton);
			}
			{
				JButton closeButton = new JButton("Close");
				closeButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						remote.dispose();
					}
				});
				closeButton.setActionCommand("Close");
				buttonPane.add(closeButton);
			}
		}
		
		SwingUtilities.expandTree( this.treeColumnStructure );
		SwingUtilities.expandTree( this.treeUnusedSamples );
	}
	
	private void makeChoosedColumnHashMapByJTree() {
		Map<String, MsbRCTColumnModel> choosedColumnMap = remote.currentRCTColumnStructureModel.getChoosedGroupMap();

		Map<String, MsbRCTColumnModel> tmpMap = new LinkedHashMap<String, MsbRCTColumnModel>();

		DefaultTreeModel model = (DefaultTreeModel)remote.treeColumnStructure.getModel();

		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> enumeration = root.children();
	    while( enumeration.hasMoreElements() ){
	    	DefaultMutableTreeNode node = (DefaultMutableTreeNode)enumeration.nextElement();
	    	if( node.getUserObject() != null ) {	    	
		    	if( node.getChildCount() > 0 ) {		// it is child of Group
		    		String key = node.getUserObject().toString();
		    		MsbRCTColumnModel column = choosedColumnMap.get( key ).clone();

		    		@SuppressWarnings("unchecked")
		    		Enumeration<DefaultMutableTreeNode> enumerationInner = node.children();
		    		while( enumerationInner.hasMoreElements() ) {
		    			DefaultMutableTreeNode innerNode = (DefaultMutableTreeNode)enumerationInner.nextElement();
		    			String key2 = innerNode.getUserObject().toString();

			    		MsbRCTColumnModel column2 = choosedColumnMap.get( key2 );
			    		if( column2 == null ) {
			    			column2 = remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel().getColumnModel( key2 );
			    		}

			    		column.addChildColumn( column2.clone() );
		    		}
		    		tmpMap.put( key, column );
		    	}else {									// Total_sum, group_sum, sample is child of Root
		    		String key = node.getUserObject().toString();

		    		MsbRCTColumnModel column = choosedColumnMap.get( key );
		    		if( column == null ) {
		    			column = remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel().getColumnModel( key );
		    		}
		    		if( column.getColumnType().equals( JMsbSysConst.SAMPLE_HEADER_PREFIX ) )	continue;

		    		tmpMap.put( key, column.clone() );
		    	}
	    	}
	    }

	    remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel().setChoosedGroupMap( tmpMap );
	}
	
	private void makeUnUsedColumnHashMapByJTree() {
		Map<String, MsbRCTColumnModel> unUsedColumnMap = remote.currentRCTColumnStructureModel.getUnusedGroupMap();

		Map<String, MsbRCTColumnModel> tmpMap = new LinkedHashMap<String, MsbRCTColumnModel>();

		DefaultTreeModel model = (DefaultTreeModel)remote.treeUnusedSamples.getModel();

		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> enumeration = root.children();
	    while( enumeration.hasMoreElements() ){
	    	DefaultMutableTreeNode node = (DefaultMutableTreeNode)enumeration.nextElement();
	    	if( node.getUserObject() != null ) {	    	
		    	if( node.getChildCount() > 0 ) {		// it is child of Group
		    		String key = node.getUserObject().toString();
		    		MsbRCTColumnModel column = unUsedColumnMap.get( key ).clone();

		    		@SuppressWarnings("unchecked")
		    		Enumeration<DefaultMutableTreeNode> enumerationInner = node.children();
		    		while( enumerationInner.hasMoreElements() ) {
		    			DefaultMutableTreeNode innerNode = (DefaultMutableTreeNode)enumerationInner.nextElement();
		    			String key2 = innerNode.getUserObject().toString();

			    		MsbRCTColumnModel column2 = unUsedColumnMap.get( key2 );
			    		if( column2 == null ) {
			    			column2 = remote.currentRCTColumnStructureModel.getOriginalColumnModel( key2 );
			    		}

			    		column.addChildColumn( column2.clone() );
		    		}
		    		tmpMap.put( key, column );
		    	}else {									// Total_sum, group_sum, sample is child of Root
		    		String key = node.getUserObject().toString();

		    		MsbRCTColumnModel column = unUsedColumnMap.get( key );
		    		if( column == null ) {
		    			column = remote.currentRCTColumnStructureModel.getOriginalColumnModel( key );
		    		}
//		    		if( column.getColumnType().equals( MSBConst.SAMPLE_HEADER_PREFIX ) )	continue;

		    		tmpMap.put( key, column.clone() );
		    	}
	    	}
	    }

	    remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel().setUnusedGroupMap( tmpMap );
	}

	public void addToRemoveTree( String key, MsbRCTColumnModel column ) {
		if( currentRCTColumnStructureModel.getUnusedGroupMap().containsKey( key ) ) {
			MsbRCTColumnModel grp = currentRCTColumnStructureModel.getUnusedGroupMap().get( key );
			List<MsbRCTColumnModel> sampleList = currentRCTColumnStructureModel.getChoosedGroupMap().get( column.getColumnId() ).getChildColumnList();
			for(int i=0; i<sampleList.size(); i++)	grp.addChildColumn( sampleList.get(i) );
		}else {
			currentRCTColumnStructureModel.getUnusedGroupMap().put( key, column );
		}
		currentRCTColumnStructureModel.getChoosedGroupMap().remove( key );

		this.treeColumnStructure.setModel( JMsbReadCountTableConfigDialog.createColumnStructureTreeModel(this.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel()) );
		this.treeUnusedSamples.setModel( JMsbReadCountTableConfigDialog.createUnusedSampleTreeModel(this.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel()) );

		this.treeColumnStructure.revalidate();
		this.treeUnusedSamples.revalidate();
		
		SwingUtilities.expandTree( this.treeColumnStructure );
	}
	
	public void addToRemoveTree( String key ) {
		this.exchangeGroupColumn(key, currentRCTColumnStructureModel.getUnusedGroupMap(), currentRCTColumnStructureModel.getChoosedGroupMap() );
	}

	public void addToUsedTree( String key ) {
		this.exchangeGroupColumn(key, currentRCTColumnStructureModel.getChoosedGroupMap(), currentRCTColumnStructureModel.getUnusedGroupMap() );
	}
	
	public void exchangeGroupColumn( String key, Map<String, MsbRCTColumnModel> src, Map<String, MsbRCTColumnModel> dest ) {
		if( src.containsKey( key ) ) {
			MsbRCTColumnModel grp = src.get(key);
			List<MsbRCTColumnModel> sampleList = dest.get(key).getChildColumnList();
			for(int i=0; i<sampleList.size(); i++)	grp.addChildColumn( sampleList.get(i) );
		}else {
			src.put( key, dest.get(key) );
		}
		dest.remove(key);

		this.treeColumnStructure.setModel( JMsbReadCountTableConfigDialog.createColumnStructureTreeModel(this.currentRCTColumnStructureModel) );
		this.treeUnusedSamples.setModel( JMsbReadCountTableConfigDialog.createUnusedSampleTreeModel(this.currentRCTColumnStructureModel) );

		this.treeColumnStructure.revalidate();
		this.treeUnusedSamples.revalidate();
		
		SwingUtilities.expandTree( this.treeColumnStructure );
	}
	
	
	public void addToRemoveTreeAtLast( String groupId, String key ) {
		this.exchangeSampleColumn(groupId, key, this.currentRCTColumnStructureModel.getChoosedGroupMap(), currentRCTColumnStructureModel.getUnusedGroupMap() );
	}
	
	public void exchangeSampleColumn( String groupId, String key, Map<String, MsbRCTColumnModel> src, Map<String, MsbRCTColumnModel> dest ) {
		MsbRCTColumnModel grp = src.get( groupId );

		MsbRCTColumnModel newGrp = new MsbRCTColumnModel( JMsbSysConst.GROUP_HEADER_PREFIX, groupId, null );
		if( dest.containsKey( groupId ) )
			newGrp = dest.get( groupId );

		List<MsbRCTColumnModel> sampleList = src.get( groupId ).getChildColumnList();
		for(int i=0; i<sampleList.size(); i++) {
			if( sampleList.get(i).getColumnId().equals( key) ) {
				newGrp.addChildColumn( sampleList.get(i) );
				grp.removeChildColumn( i );
				break;
			}
		}

		dest.put( newGrp.getColumnId(), newGrp );

		this.treeColumnStructure.setModel( JMsbReadCountTableConfigDialog.createColumnStructureTreeModel(this.currentRCTColumnStructureModel) );
		this.treeUnusedSamples.setModel( JMsbReadCountTableConfigDialog.createUnusedSampleTreeModel(this.currentRCTColumnStructureModel) );

		this.treeColumnStructure.revalidate();
		this.treeUnusedSamples.revalidate();
		
		SwingUtilities.expandTree( this.treeColumnStructure );
	}

	public void addToUsedTreeAtLast( String groupId, String key ) {
		this.exchangeSampleColumn(groupId, key, currentRCTColumnStructureModel.getUnusedGroupMap(), this.currentRCTColumnStructureModel.getChoosedGroupMap() );
	}
	
	private static DefaultTreeModel createUnusedSampleTreeModel(MSBReadCountTableColumnStructureModel currentRCTColumnStructureModel) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		
		Iterator<String> iterator = currentRCTColumnStructureModel.getUnusedGroupMap().keySet().iterator();

		while( iterator.hasNext()) {
			String key = iterator.next();
			MsbRCTColumnModel grp = currentRCTColumnStructureModel.getUnusedGroupMap().get(key);

			if( grp.getColumnType().equals( JMsbSysConst.TOTAL_SUM_HEADER_PREFIX ) || grp.getColumnType().equals( JMsbSysConst.GROUP_SUM_HEADER_PREFIX ) )	{
				DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode( key );
				root.add( groupNode );
			}else {
				List<MsbRCTColumnModel> sampleList = grp.getChildColumnList();
				if( sampleList != null ) {
					if( grp.getChildColumnList().size() > 0 ) {
						DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode( grp.getColumnId() );
						for(MsbRCTColumnModel smp:sampleList) {
							DefaultMutableTreeNode sampleNode = new DefaultMutableTreeNode( smp.getColumnId() );
							groupNode.add( sampleNode );
						}
						root.add( groupNode );
					}else {
						root.add( new DefaultMutableTreeNode( key ) );
					}
				}else {
					root.add( new DefaultMutableTreeNode( key) );
				}
			}
		}
		return new DefaultTreeModel(root);
	}

	private static DefaultTreeModel createColumnStructureTreeModel(MSBReadCountTableColumnStructureModel currentRCTColumnStructureModel) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		
		Iterator<String> iterator = currentRCTColumnStructureModel.getChoosedGroupMap().keySet().iterator();

		while( iterator.hasNext()) {
			String key = iterator.next();
			MsbRCTColumnModel grp = currentRCTColumnStructureModel.getChoosedGroupMap().get( key );

			if( grp.getColumnType().equals( JMsbSysConst.TOTAL_SUM_HEADER_PREFIX ) || grp.getColumnType().equals( JMsbSysConst.GROUP_SUM_HEADER_PREFIX ) )	{
				DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode( key );
				root.add( groupNode );
			}else {
				if( grp.getColumnType().equals( JMsbSysConst.GROUP_HEADER_PREFIX ) ) {
					List<MsbRCTColumnModel> sampleList = grp.getChildColumnList();
					if( sampleList != null ) {
						if( grp.getChildColumnList().size() > 0 ) {
							DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode( grp.getColumnId() );
							for(MsbRCTColumnModel smp:sampleList) {
								DefaultMutableTreeNode sampleNode = new DefaultMutableTreeNode( smp.getColumnId() );
								groupNode.add( sampleNode );
							}
							root.add( groupNode );
						}
					}else {
						root.add( new DefaultMutableTreeNode( key ) );
					}
				}else {
					root.add( new DefaultMutableTreeNode( key ) );
				}
			}
		}
		
		return new DefaultTreeModel(root);
	}
	
	public AbstractDockingWindowObj getDockingWindowObj() {
		return this.dockWindow;
	}

	public void setChangeNodeByDnD( DefaultMutableTreeNode node ) {
		/*****************************************************************************************
		 * Remove node @ choosed tree
		 */
		DefaultTreeModel choosedTreeModel = (DefaultTreeModel)this.treeColumnStructure.getModel();
		choosedTreeModel.removeNodeFromParent( node );
		
		/*****************************************************************************************
		 * Add node @ unused tree
		 */
		DefaultMutableTreeNode unusedNode = new DefaultMutableTreeNode( node.getUserObject() );
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)(this.treeUnusedSamples.getModel().getRoot());
		root.add( unusedNode );
		this.treeUnusedSamples.setModel( new DefaultTreeModel(root) );
		
		this.makeChoosedColumnHashMapByJTree();
		this.makeUnUsedColumnHashMapByJTree();
		
		this.treeUnusedSamples.revalidate();
		this.treeColumnStructure.revalidate();
	}
}
