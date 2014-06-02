package kobic.msb.swing.panel.newproject.bak;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import kobic.com.util.Utilities;
import kobic.msb.common.SwingConst;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.component.JScrollPopupMenu;
import kobic.msb.swing.filefilter.UserDataFileFilter;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.swing.listener.projectdialog.AddGroupActionListener;
import kobic.msb.swing.listener.projectdialog.AddSampleActionListener;
import kobic.msb.swing.listener.projectdialog.DelGroupActionListener;
import kobic.msb.swing.listener.projectdialog.DelSampleActionListener;
import kobic.msb.swing.listener.projectdialog.EditGroupActionListener;
import kobic.msb.swing.listener.projectdialog.EditSampleActionListener;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import kobic.msb.server.model.jaxb.Msb;

public class JNewProjectPanel extends JPanel implements Observer{
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	private JTextField				txtRnaId		= new JTextField();
//	private JTextField				txtAccessionNo	= new JTextField();
//	private JTextField				txtChromosome	= new JTextField();
//	private JTextField				txtLocation		= new JTextField();
//
//	private JTextField				txtSamplePath	= new JTextField();
//	private JTextField				txtSampleName	= new JTextField();
//	private JComboBox<String>		cmbGroupSelect	= new JComboBox<String>();
//	private JComboBox<String>		cmbMngGroup		= new JComboBox<String>();
//
//	private JProjectDialog			owner;
//	private JTable					tblSampleList;
//	
//	private JCheckBox				chkEnable = new JCheckBox();
//	
//	private JScrollPopupMenu		popupMenu;
//	
//	private int						nSamples;		// # of samples(if you add sample, this number are increased or you subract sample, this number are decreased)
//	
//	private String					choosedGroupNameToEdit;
//
//	private JTextField				txtOrder;
//	private JPanel					referencePanel;
//	private JPanel					groupPanel;
//	private JPanel					samplePanel;
//	
//	private Msb						msb;
//
//	private JNewProjectPanel remote = JNewProjectPanel.this;
//
//	/**
//	 * Create the panel.
//	 */
//	public JNewProjectPanel( JProjectDialog owner ) {
//		this.nSamples	= 0;
//		this.owner		= owner;
//		
//		this.msb		= new Msb();
//
//		this.popupMenu = new JScrollPopupMenu();
//		
//		Object[][] data = null;
//
//		DefaultTableModel tblModel = new DefaultTableModel(data, SwingConst.SAMPLE_TABLE_COLUMN );
//		
//		this.tblSampleList = new JTable( tblModel );
//		
//		this.initContentRootPanelLayout();
//	}
//
//	public void					setRnaListPopupMenu( JScrollPopupMenu popupMenu )	{	this.popupMenu = popupMenu;							}
//	public JScrollPopupMenu		getRnaListPopupMenu()								{	return this.popupMenu;								}
//	public JTextField 			getRnaIdTextField()									{	return this.txtRnaId;								}
//
//	public JTextField			getRnaAccessionNoTextField()						{	return this.txtAccessionNo;							}
//	public JTextField			getChromosomeTextField()							{	return this.txtChromosome;							}
//	public void					setRnaIdTextField( String value )					{	this.txtRnaId.setText( value );						}
//	public void					setRnaAccessionNoTextField( String accession )		{	this.txtAccessionNo.setText( accession );			}
//	public void					setChromosomeTextField( String chr )				{	this.txtChromosome.setText( chr );					}
//	public void					setLocationTextField( String location )				{	this.txtLocation.setText( location );				}
//
//
//	public int					getNumberOfSample() 								{	return this.nSamples;								}
//	public void					setNumberOfSample(int nSample)						{	this.nSamples = nSample;							}
//
//	public JPanel createGroupManagePanel() {
//		TitledBorder groupsTitled = BorderFactory.createTitledBorder( SwingConst.BORDER_LOWERED_ETCHED, "Groups" );
//        this.groupPanel = new JPanel(false);
//		this.groupPanel.setBorder(groupsTitled);
//		
//		JLabel lblGroupId = new JLabel("Group ID");
//		lblGroupId.setFont( SwingConst.DEFAULT_DIALOG_FONT );
//
//		final JButton btnAdd = new JButton("Add");
//		btnAdd.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
//		JComboBox<String> cmbMngGroup = this.getCmbMngGroup();
//		cmbMngGroup.setFont(SwingConst.DEFAULT_DIALOG_FONT);
//		cmbMngGroup.setEditable(true);
//
//		cmbMngGroup.addItemListener(new ItemListener() {
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				if( e.getStateChange() == ItemEvent.SELECTED ) {
//					remote.setChoosedGroupNameToEdit( e.getItem().toString() );
//				}
//			}
//		});
//
//		cmbMngGroup.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyReleased(KeyEvent e) {
//				// TODO Auto-generated method stub
//				if( e.getKeyCode() == KeyEvent.VK_ENTER ){
//					btnAdd.doClick();
//				}
//			}
//		});
//		
//		JButton btnDel = new JButton("Del");
//		btnDel.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
//		
//		JButton btnEdit = new JButton("Edit");
//		btnEdit.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
//
//		MsbEngine engine = MsbEngine.getInstance();
//		
//		btnAdd.addActionListener(	new AddGroupActionListener( this ) );
//		btnEdit.addActionListener(	new EditGroupActionListener( this, engine ) );
//		btnDel.addActionListener(	new DelGroupActionListener( this ) );
//		GroupLayout gl_groupPanel_1 = new GroupLayout(groupPanel);
//		gl_groupPanel_1.setHorizontalGroup(
//			gl_groupPanel_1.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_groupPanel_1.createSequentialGroup()
//					.addGap(12)
//					.addComponent(lblGroupId, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
//					.addGap(46)
//					.addComponent(cmbMngGroup, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addComponent(btnEdit, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addComponent(btnDel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
//					.addContainerGap())
//		);
//		gl_groupPanel_1.setVerticalGroup(
//			gl_groupPanel_1.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_groupPanel_1.createSequentialGroup()
//					.addGroup(gl_groupPanel_1.createParallelGroup(Alignment.LEADING)
//						.addGroup(gl_groupPanel_1.createSequentialGroup()
//							.addGap(7)
//							.addComponent(lblGroupId))
//						.addGroup(gl_groupPanel_1.createSequentialGroup()
//							.addGap(2)
//							.addGroup(gl_groupPanel_1.createParallelGroup(Alignment.BASELINE)
//								.addComponent(btnEdit, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
//								.addComponent(btnDel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)))
//						.addGroup(gl_groupPanel_1.createSequentialGroup()
//							.addGap(2)
//							.addGroup(gl_groupPanel_1.createParallelGroup(Alignment.BASELINE)
//								.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
//								.addComponent(cmbMngGroup, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))))
//					.addContainerGap(9, Short.MAX_VALUE))
//		);
//		groupPanel.setLayout(gl_groupPanel_1);
//		
//		return groupPanel;
//	}
//
//	public void initContentRootPanelLayout() {
//		JPanel contentPanel = new JPanel();
//
//		this.referencePanel	= this.createReferenceInfoPanel();
//		this.groupPanel		= this.createGroupManagePanel();
//		this.samplePanel	= this.createSampleInfoPanel();
//
//		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
//		gl_contentPanel.setHorizontalGroup(
//			gl_contentPanel.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_contentPanel.createSequentialGroup()
//					.addContainerGap()
//					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
//						.addComponent(groupPanel, GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
//						.addComponent(samplePanel, GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
//						.addComponent(referencePanel, GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE))
//					.addContainerGap())
//		);
//		gl_contentPanel.setVerticalGroup(
//			gl_contentPanel.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_contentPanel.createSequentialGroup()
//					.addContainerGap()
//					.addComponent(groupPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addComponent(samplePanel, GroupLayout.PREFERRED_SIZE, 320, GroupLayout.PREFERRED_SIZE)
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addComponent(referencePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//					.addContainerGap(68, Short.MAX_VALUE))
//		);
//		contentPanel.setLayout(gl_contentPanel);
//		GroupLayout groupLayout = new GroupLayout(this);
//		groupLayout.setHorizontalGroup(
//			groupLayout.createParallelGroup(Alignment.LEADING)
//				.addComponent(contentPanel, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
//		);
//		groupLayout.setVerticalGroup(
//			groupLayout.createParallelGroup(Alignment.LEADING)
//				.addGroup(groupLayout.createSequentialGroup()
//					.addComponent(contentPanel, GroupLayout.PREFERRED_SIZE, 398, GroupLayout.PREFERRED_SIZE)
//					.addContainerGap(100, Short.MAX_VALUE))
//		);
//		this.setLayout(groupLayout);
//	}
//	
//	public JPanel createSampleInfoPanel() {
//		samplePanel = new JPanel(false);
//		samplePanel.setBorder( BorderFactory.createTitledBorder( SwingConst.BORDER_LOWERED_ETCHED, "Samples" ) );
//		
//		JLabel lblSampleId = new JLabel("Sample Name");
//		lblSampleId.setHorizontalAlignment(SwingConstants.LEFT);
//		lblSampleId.setFont(SwingConst.DEFAULT_DIALOG_FONT);
//		
//		JLabel lblPathFile = new JLabel("Path");
//		lblPathFile.setFont(SwingConst.DEFAULT_DIALOG_FONT);
//		
//		JLabel lblGroup = new JLabel("Group");
//		lblGroup.setFont(SwingConst.DEFAULT_DIALOG_FONT);
//		
//		JComboBox<String> cmbGroupSelect = this.getCmbGroupSelect();
//		cmbGroupSelect.setFont(SwingConst.DEFAULT_DIALOG_FONT);
//		
//		JTextField txtSamplePath = this.getTxtSamplePath();
//		txtSamplePath.setColumns(10);
//		
//		JTextField txtSampleName = this.getTxtSampleName();
//		txtSampleName.setColumns(10);
//		
//		JButton btnChoose = new JButton("Choose ...");
//		btnChoose.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
//		btnChoose.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated method stub
//				//Handle open button action.
//				final JFileChooser fc = new JFileChooser();
//				fc.setAcceptAllFileFilterUsed(false);
//				fc.addChoosableFileFilter( new UserDataFileFilter() );
//
//	            int returnVal = fc.showOpenDialog( remote );
//	 
//	            if ( returnVal == JFileChooser.APPROVE_OPTION ) {
//	                String samplePath = fc.getSelectedFile().getAbsolutePath();
//	                remote.getTxtSamplePath().setText( samplePath );
//	            }
//			}
//		});
//		btnChoose.setFont(SwingConst.DEFAULT_DIALOG_FONT);
//		
//		JTable tblSampleList = this.getTblSampleList();
//		tblSampleList.setCellSelectionEnabled(true);
//		tblSampleList.setEnabled(false);
//
//		tblSampleList.addMouseListener(new MouseAdapter() {
//
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				int row = remote.getTblSampleList().rowAtPoint( e.getPoint() );
//
//				DefaultTableModel model = (DefaultTableModel)remote.getTblSampleList().getModel();
//				
//				if( row >= 0 && row < model.getRowCount() ) {
//					Integer sampleNumber	= (int)model.getValueAt( row, 0 );
////					Boolean bool			= (Boolean)model.getValueAt( row, 1 );
//					String groupId			= (String)model.getValueAt( row, 1 );
//					String sampleName		= (String)model.getValueAt( row, 2 );
//					String samplePath		= (String)model.getValueAt( row, 3 );
//					String sampleIndex		= (String)model.getValueAt( row, 4 );
//					
//					remote.getTxtSampleName().setText( sampleName );
//					remote.getTxtSamplePath().setText( samplePath );
//					remote.getTxtOrder().setText( sampleNumber.toString() );
//					remote.getCmbGroupSelect().setSelectedItem( groupId );
//				}
//			}
//		});
//
//		tblSampleList.setPreferredScrollableViewportSize(new Dimension(500, 70));
//		tblSampleList.setFillsViewportHeight(true);
//		tblSampleList.setAutoCreateRowSorter(true);
//		tblSampleList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		
//		JScrollPane scrollPane = new JScrollPane( tblSampleList );
//		
//		JButton btnSampleAdd = new JButton("Add");
//		btnSampleAdd.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
//		btnSampleAdd.addActionListener( new AddSampleActionListener(this) );
//		btnSampleAdd.setFont(SwingConst.DEFAULT_DIALOG_FONT);
//		
//		JButton btnSampleDel = new JButton("Del");
//		btnSampleDel.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
//		btnSampleDel.addActionListener( new DelSampleActionListener( this ) );
//		btnSampleDel.setFont(SwingConst.DEFAULT_DIALOG_FONT);
//		
//		JButton btnSampleModify = new JButton("Edit");
//		btnSampleModify.addActionListener( new EditSampleActionListener( this ) );
//		btnSampleModify.setFont(SwingConst.DEFAULT_DIALOG_FONT_BOLD);
//		
//		JLabel lblOrder = new JLabel("Sample Number");
//		lblOrder.setFont(SwingConst.DEFAULT_DIALOG_FONT);
//		
//		this.txtOrder = new JTextField();
//		txtOrder.setEditable(false);
//		txtOrder.setColumns(10);
//		GroupLayout gl_samplePanel_1 = new GroupLayout(samplePanel);
//		gl_samplePanel_1.setHorizontalGroup(
//			gl_samplePanel_1.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_samplePanel_1.createSequentialGroup()
//					.addGap(12)
//					.addGroup(gl_samplePanel_1.createParallelGroup(Alignment.LEADING)
//						.addComponent(lblSampleId)
//						.addComponent(lblGroup)
//						.addComponent(lblOrder)
//						.addComponent(lblPathFile))
//					.addGroup(gl_samplePanel_1.createParallelGroup(Alignment.LEADING)
//						.addGroup(gl_samplePanel_1.createSequentialGroup()
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addGroup(gl_samplePanel_1.createParallelGroup(Alignment.TRAILING)
//								.addComponent(txtSampleName, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
//								.addComponent(txtOrder, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//								.addComponent(cmbGroupSelect, 0, 318, Short.MAX_VALUE)))
//						.addGroup(gl_samplePanel_1.createSequentialGroup()
//							.addGap(6)
//							.addComponent(txtSamplePath, GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)))
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addGroup(gl_samplePanel_1.createParallelGroup(Alignment.LEADING)
//						.addGroup(gl_samplePanel_1.createSequentialGroup()
//							.addComponent(btnSampleAdd)
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addComponent(btnSampleDel))
//						.addComponent(btnChoose)
//						.addComponent(btnSampleModify))
//					.addContainerGap())
//				.addGroup(gl_samplePanel_1.createSequentialGroup()
//					.addContainerGap()
//					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE))
//		);
//		gl_samplePanel_1.setVerticalGroup(
//			gl_samplePanel_1.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_samplePanel_1.createSequentialGroup()
//					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//					.addGroup(gl_samplePanel_1.createParallelGroup(Alignment.BASELINE)
//						.addComponent(txtSampleName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//						.addComponent(lblSampleId))
//					.addGroup(gl_samplePanel_1.createParallelGroup(Alignment.LEADING)
//						.addGroup(gl_samplePanel_1.createSequentialGroup()
//							.addGroup(gl_samplePanel_1.createParallelGroup(Alignment.LEADING)
//								.addGroup(gl_samplePanel_1.createSequentialGroup()
//									.addGap(12)
//									.addComponent(btnChoose))
//								.addGroup(gl_samplePanel_1.createSequentialGroup()
//									.addGap(11)
//									.addComponent(lblPathFile)))
//							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//							.addGroup(gl_samplePanel_1.createParallelGroup(Alignment.TRAILING, false)
//								.addGroup(gl_samplePanel_1.createSequentialGroup()
//									.addGroup(gl_samplePanel_1.createParallelGroup(Alignment.BASELINE)
//										.addComponent(btnSampleAdd)
//										.addComponent(btnSampleDel))
//									.addPreferredGap(ComponentPlacement.RELATED))
//								.addGroup(gl_samplePanel_1.createSequentialGroup()
//									.addComponent(lblGroup)
//									.addGap(11))))
//						.addGroup(gl_samplePanel_1.createSequentialGroup()
//							.addGap(9)
//							.addComponent(txtSamplePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//							.addGap(3)
//							.addComponent(cmbGroupSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
//					.addGap(6)
//					.addGroup(gl_samplePanel_1.createParallelGroup(Alignment.LEADING)
//						.addGroup(gl_samplePanel_1.createSequentialGroup()
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addComponent(btnSampleModify))
//						.addGroup(gl_samplePanel_1.createSequentialGroup()
//							.addGap(5)
//							.addComponent(lblOrder))
//						.addGroup(gl_samplePanel_1.createSequentialGroup()
//							.addGap(3)
//							.addComponent(txtOrder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
//					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE))
//		);
//		samplePanel.setLayout(gl_samplePanel_1);
//		
//		return samplePanel;
//	}
//	
//	private JPanel createReferenceInfoPanel() {
//		referencePanel = new JPanel();
//		referencePanel.setBorder(  BorderFactory.createTitledBorder( SwingConst.BORDER_LOWERED_ETCHED, "Reference information" ) );
//		GroupLayout gl_referencePanel_1 = new GroupLayout(referencePanel);
//		gl_referencePanel_1.setHorizontalGroup(
//			gl_referencePanel_1.createParallelGroup(Alignment.LEADING)
//				.addGap(0, 496, Short.MAX_VALUE)
//		);
//		gl_referencePanel_1.setVerticalGroup(
//			gl_referencePanel_1.createParallelGroup(Alignment.LEADING)
//				.addGap(0, 43, Short.MAX_VALUE)
//		);
//		referencePanel.setLayout(gl_referencePanel_1);
//		
//		return referencePanel;
//	}
//
//	public void initGroupInfo() {
//		remote.cmbMngGroup.setSelectedItem("");
//	}
//
//	public void initSampleInfo() {
//		remote.txtSampleName.setText("");
//		remote.txtSamplePath.setText("");
//		remote.cmbGroupSelect.setSelectedIndex(0);
//		remote.txtOrder.setText("");
//	}
//
//	public void refreshSampleTable( List<Group> groupList ) {
//		DefaultTableModel newModel = new DefaultTableModel( null, SwingConst.SAMPLE_TABLE_COLUMN );
//		
//		// To re-order samples in the list
//		Iterator<Group> iterGroup = groupList.iterator();
//		while( iterGroup.hasNext() ) {
//			Group grp = iterGroup.next();
//			Iterator<Sample> iterSample = grp.getSample().iterator();
//			while( iterSample.hasNext() ) {
//				Sample smp = iterSample.next();
//
//				String indexFile = smp.getIndexPath();
//				if( Utilities.nulltoEmpty( smp.getIndexPath() ).equals("") )
//					indexFile = "<Empty>";
////				newModel.addRow( new Object[]{ new Integer(smp.getOrder()), smp.getView(),	grp.getGroupId(), smp.getName(), smp.getSamplePath(), indexFile } );
//				newModel.addRow( new Object[]{ new Integer(smp.getOrder()), grp.getGroupId(), smp.getName(), smp.getSamplePath(), indexFile } );
//			}
//		}
//
//		remote.tblSampleList.setModel( newModel );
//		remote.tblSampleList.revalidate();
//	}
//
//	public void updateCurrentState( ProjectMapItem projectMapItem ) {
//		this.getOwner().getTxtProjectName().setText( projectMapItem.getProjectName() );
//
//		this.cmbMngGroup.removeAllItems();
//		this.cmbGroupSelect.removeAllItems();
//		
//		List<Group> groupList = projectMapItem.getProjectInfo().getSamples().getGroup();
//		Iterator<Group> iter = groupList.iterator();
//		while( iter.hasNext() ) {
//			Group group = iter.next();
//			this.cmbMngGroup.addItem( group.getGroupId() );
//			this.cmbGroupSelect.addItem( group.getGroupId() );
//
//			this.nSamples += group.getSample().size();
//		}
//		this.getOwner().getTxtProjectName().setEnabled(false);
//		this.msb.setProject( projectMapItem.getProjectInfo() );
//
//		this.refreshSampleTable( groupList );
//	}
//
//	public JTextField getTxtRnaId() {
//		return txtRnaId;
//	}
//	public void setTxtRnaId(JTextField txtRnaId) {
//		this.txtRnaId = txtRnaId;
//	}
//	public JTextField getTxtAccessionNo() {
//		return txtAccessionNo;
//	}
//	public void setTxtAccessionNo(JTextField txtAccessionNo) {
//		this.txtAccessionNo = txtAccessionNo;
//	}
//	public JTextField getTxtChromosome() {
//		return txtChromosome;
//	}
//	public void setTxtChromosome(JTextField txtChromosome) {
//		this.txtChromosome = txtChromosome;
//	}
//	public JTextField getTxtLocation() {
//		return txtLocation;
//	}
//	public void setTxtLocation(JTextField txtLocation) {
//		this.txtLocation = txtLocation;
//	}
//	public JTextField getTxtSamplePath() {
//		return txtSamplePath;
//	}
//	public void setTxtSamplePath(JTextField txtSamplePath) {
//		this.txtSamplePath = txtSamplePath;
//	}
//	public JTextField getTxtSampleName() {
//		return txtSampleName;
//	}
//	public void setTxtSampleName(JTextField txtSampleName) {
//		this.txtSampleName = txtSampleName;
//	}
//	public JTable getTblSampleList() {
//		return tblSampleList;
//	}
//	public void setTblSampleList(JTable tblSampleList) {
//		this.tblSampleList = tblSampleList;
//	}
//	public JComboBox<String> getCmbGroupSelect() {
//		return cmbGroupSelect;
//	}
//	public void setCmbGroupSelect(JComboBox<String> cmbGroupSelect) {
//		this.cmbGroupSelect = cmbGroupSelect;
//	}
//	public JComboBox<String> getCmbMngGroup() {
//		return cmbMngGroup;
//	}
//	public void setCmbMngGroup(JComboBox<String> cmbMngGroup) {
//		this.cmbMngGroup = cmbMngGroup;
//	}
//	public JProjectDialog getOwner() {
//		return owner;
//	}
//	public void setOwner(JProjectDialog owner) {
//		this.owner = owner;
//	}
//	public JCheckBox getChkEnable() {
//		return chkEnable;
//	}
//	public void setChkEnable(JCheckBox chkEnable) {
//		this.chkEnable = chkEnable;
//	}
//	public JScrollPopupMenu getPopupMenu() {
//		return popupMenu;
//	}
//	public void setPopupMenu(JScrollPopupMenu popupMenu) {
//		this.popupMenu = popupMenu;
//	}
//	public JTextField getTxtOrder() {
//		return txtOrder;
//	}
//	public void setTxtOrder(JTextField txtOrder) {
//		this.txtOrder = txtOrder;
//	}
//	public int getnSamples() {
//		return nSamples;
//	}
//	public void setnSamples(int nSamples) {
//		this.nSamples = nSamples;
//	}
//	public String getChoosedGroupNameToEdit() {
//		return choosedGroupNameToEdit;
//	}
//	public void setChoosedGroupNameToEdit(String choosedGroupNameToEdit) {
//		this.choosedGroupNameToEdit = choosedGroupNameToEdit;
//	}
//	public Msb getMsb() {
//		return this.msb;
//	}
//
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
	}
}
