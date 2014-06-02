package kobic.msb.swing.panel.newproject;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.DefaultRowSorter;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import kobic.com.util.Utilities;
import kobic.msb.common.SwingConst;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.component.TableColumnAdjuster;
import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import net.infonode.util.ArrayUtil;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class JBamFilePreProcessingPanel extends CommonAbstractNewProjectPanel implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTable					tblSample;
	private javax.swing.JTextArea	textArea;
	/*** Over JDK 1.7 ***/
//	private JComboBox<String>		cmbProject;
	/*** Under JDK 1.7 ***/
	private JComboBox				cmbProject;
	private JScrollPane				txtLogScrollPane;

	private TableColumnAdjuster		tca;

	private StyledDocument			doc;
	private StyleContext			sc;

	private final JBamFilePreProcessingPanel remote = JBamFilePreProcessingPanel.this; 

	private final String[] TBL_COLUMNS = ArrayUtil.append( new String[]{"Done"}, SwingConst.SAMPLE_TABLE_COLUMN );

	/**
	 * Create the panel.
	 */
	public JBamFilePreProcessingPanel( JProjectDialog owner ) {
		super( owner );

		Border titlePanelBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED );
		JPanel titlePanel = new JPanel();
		titlePanel.setBorder( titlePanelBorder );
		titlePanel.setBounds(0, 0, 592, 57);
		titlePanel.setBackground(Color.WHITE);
		titlePanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("BAM Index file manager");
		lblNewLabel.setBounds(16, 6, 163, 16);
		titlePanel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("You can manage index files in the project");
		lblNewLabel_1.setBounds(26, 23, 273, 16);
		titlePanel.add(lblNewLabel_1);
	
		Border componentPanelBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED );
		JPanel componentPanel = new JPanel();
		componentPanel.setBorder( componentPanelBorder );

		this.sc = new StyleContext();
	    this.doc = new javax.swing.text.DefaultStyledDocument( sc );
	
		this.textArea = new javax.swing.JTextArea( doc );
		this.textArea.setText("");
		this.textArea.setEditable(false);
		this.textArea.setFont( SwingConst._9_FONT );
	
		this.txtLogScrollPane = new JScrollPane( this.textArea );
		this.txtLogScrollPane.setAutoscrolls(true);
	
		Object[][] data = null;
		UpdatableTableModel tblModel = new UpdatableTableModel( data, this.TBL_COLUMNS );
		
		this.tblSample = new JTable( tblModel ){
	        private static final long serialVersionUID = 1L;

	        @Override
	        public Class<?> getColumnClass(int column) {
	        	switch (column) {
	                case 0:
	                	return Boolean.class;
	                case 1:
	                    return Integer.class;
	                case 2:
	                    return String.class;
	                case 3:
	                    return String.class;
	                case 4:
	                    return String.class;
	                case 5:
	                	return String.class;
	                default:
	                    return String.class;
	        	}
	        }
	
	        @Override
	        public boolean isCellEditable(int row, int col) {
//	        	return col == 0;
	        	return false;
	       }
	    };
		this.tblSample.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.tca = new TableColumnAdjuster(this.tblSample);
		
		DefaultTableCellRenderer dtcr = ((DefaultTableCellRenderer)this.tblSample.getTableHeader().getDefaultRenderer());
		dtcr.setHorizontalAlignment(JLabel.CENTER);
		
		JScrollPane scrollPane = new JScrollPane( this.tblSample );
		GroupLayout gl_componentPanel = new GroupLayout(componentPanel);
		gl_componentPanel.setHorizontalGroup(
			gl_componentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_componentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_componentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(this.txtLogScrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
						.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_componentPanel.setVerticalGroup(
			gl_componentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_componentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(this.txtLogScrollPane, GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
					.addContainerGap())
		);
		componentPanel.setLayout(gl_componentPanel);
	
		Border projectPanelBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED );
		JPanel projectPanel = new JPanel();
		projectPanel.setBorder( projectPanelBorder );
		
		JLabel lblNewLabel_2 = new JLabel("Project");
		
		/*** Over JDK 1.7 ***/
//		this.cmbProject = new JComboBox<String>();
		/*** Under JDK 1.7 ***/
		this.cmbProject = new JComboBox();
		this.cmbProject.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( remote.cmbProject.getItemCount() > 1 ) {
					try {
						String projectName = remote.cmbProject.getSelectedItem().toString();
						ProjectMapItem projectMap = MsbEngine.engine.getProjectManager().getProjectMap().getProject( projectName );
		
						if( projectMap != null ) {
							remote.refreshSampleTable( projectMap.getProjectInfo().getSamples().getGroup() );
						}
					}catch(Exception ex) {
//						ex.printStackTrace();
						MsbEngine.logger.error( "Error : " + ex );
					}
				}
			}
		});
		GroupLayout gl_projectPanel = new GroupLayout(projectPanel);
		gl_projectPanel.setHorizontalGroup(
			gl_projectPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_projectPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(this.cmbProject, 0, 592, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_projectPanel.setVerticalGroup(
			gl_projectPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_projectPanel.createSequentialGroup()
					.addGap(4)
					.addGroup(gl_projectPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(this.cmbProject, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2)))
		);
		projectPanel.setLayout(gl_projectPanel);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addGap(6)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(componentPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 675, Short.MAX_VALUE)
						.addComponent(projectPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 675, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(projectPanel, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
					.addGap(3)
					.addComponent(componentPanel, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
					.addContainerGap())
		);
		setLayout(groupLayout);
	}

	@Override
	public void updateCurrentState( ProjectMapItem projectMapItem ) throws Exception{
		this.cmbProject.removeAllItems();
		this.cmbProject.addItem( projectMapItem.getProjectName() );
		
		if( projectMapItem != null ) {
			remote.refreshSampleTable( projectMapItem.getProjectInfo().getSamples().getGroup() );
		}

		this.cmbProject.setSelectedItem( projectMapItem.getProjectName() );
		this.cmbProject.revalidate();
	}
	
//	public void refreshBamIndexManager( ProjectMapItem item ) throws Exception{
////		this.engine.getProjectManager().getProjectMap().writeToFile( remote.engine.getProjectManager().getSystemFileToGetProjectList() );
//		UpdatableTableModel newModel = (UpdatableTableModel)this.tblSample.getModel();
//		newModel.removeAll();
////		this.refreshSampleTable( item.getProjectInfo().getSamples().getGroup() );
////		this.owner.getOwner().getTreePanel().refreshProjectTree();
//	}

	private synchronized void refreshSampleTable( List<Group> groupList ) throws Exception{
//		DefaultTableModel newModel = new DefaultTableModel( null, this.TBL_COLUMNS );
//		UpdatableTableModel newModel = (UpdatableTableModel)this.tblSample.getModel();
//		newModel.setRowCount(0);
//		newModel.removeAll();
		UpdatableTableModel newModel = new UpdatableTableModel( null, this.TBL_COLUMNS );
		try {
			// To re-order samples in the list
			Iterator<Group> iterGroup = groupList.iterator();
			while( iterGroup.hasNext() ) {
				Group grp = iterGroup.next();
				Iterator<Sample> iterSample = grp.getSample().iterator();
				while( iterSample.hasNext() ) {
					Sample smp = iterSample.next();
		
					Boolean chkBoolean = new Boolean(false);
		
					String indexFile = smp.getIndexPath();
					if( Utilities.nulltoEmpty( smp.getIndexPath() ).equals("") ) {
						indexFile = "<Empty>";
						chkBoolean = false;
					}else {
						chkBoolean = true;
					}
					Object[] innerObjs = new Object[]{ chkBoolean, new Integer(smp.getOrder()), grp.getGroupId(), smp.getName(), smp.getSamplePath(), indexFile };
	
					newModel.addRow( innerObjs );
				}
			}
		}catch(Exception e) {
			MsbEngine.logger.error("Error : ", e );
		}

		try {
			this.tblSample.setModel( newModel );

			if( newModel.getRowCount() > 0 ) {
				TableRowSorter<UpdatableTableModel> sorter = new TableRowSorter<UpdatableTableModel>( newModel );
				this.tblSample.setRowSorter( sorter );
			}
			this.tblSample.getRowSorter().toggleSortOrder(1);

		}catch(Exception e) {
			MsbEngine.logger.error("error : ", e);
		}
		this.tca.adjustColumns();
	}
	
	public javax.swing.JTextArea getTextPane() {
		return this.textArea;
	}

	public JTable getSampleTable() {
		return this.tblSample;
	}

	public String getProjectName() {
		return this.cmbProject.getSelectedItem().toString();
	}

	@Override
	public void update(Observable o, Object arg) {
		try {
			// TODO Auto-generated method stub
			if( arg instanceof ProjectMapItem ) {
				ProjectMapItem projectMapItem = (ProjectMapItem)arg;

				this.refreshSampleTable( projectMapItem.getProjectInfo().getSamples().getGroup() );
			}
		}catch(Exception e) {
			MsbEngine.logger.error( "error : ", e );
		}
	}
}
