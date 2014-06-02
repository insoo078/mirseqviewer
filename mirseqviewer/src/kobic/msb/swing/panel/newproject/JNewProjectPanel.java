package kobic.msb.swing.panel.newproject;

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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import kobic.msb.common.SwingConst;
import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.swing.filefilter.UserDataFileFilter;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.swing.listener.projectdialog.AddGroupActionListener;
import kobic.msb.swing.listener.projectdialog.AddSampleActionListener;
import kobic.msb.swing.listener.projectdialog.DelGroupActionListener;
import kobic.msb.swing.listener.projectdialog.DelSampleActionListener;
import kobic.msb.swing.listener.projectdialog.EditGroupActionListener;
import kobic.msb.swing.listener.projectdialog.EditSampleActionListener;
import kobic.msb.system.catalog.ProjectMapItem;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;

public class JNewProjectPanel extends JMsbSampleTableCommonPanel implements UpdateCurrentStateInterface{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String					choosedGroupNameToEdit;

	/*** Over JDK 1.7 ***/
//	private JComboBox<String>		cmbMngGroup;
//	private JComboBox<String>		cmbGroupSelect;
	/*** Under JDK 1.7 ***/
	@SuppressWarnings("rawtypes")
	private JComboBox				cmbMngGroup;
	@SuppressWarnings("rawtypes")
	private JComboBox				cmbGroupSelect;
	
	private JPanel					groupPanel;
	private JPanel					samplePanel;
	
//	private JScrollPopupMenu		popupMenu;
	
	private JTextField				txtSampleName;
	private JTextField				txtSampleFile;
	private JTextField				txtOrder;

	private Msb						msb;

	private JNewProjectPanel		remote = JNewProjectPanel.this;

	@SuppressWarnings("rawtypes")
	public JNewProjectPanel(JProjectDialog owner) {
		super( owner );
		
		this.msb			= new Msb();

//		this.popupMenu = new JScrollPopupMenu();
		
		this.groupPanel		= new JPanel();
		this.samplePanel	= new JPanel();

		this.groupPanel.setBorder( BorderFactory.createTitledBorder( SwingConst.BORDER_LOWERED_ETCHED, "Groups" ) );
		this.samplePanel.setBorder( BorderFactory.createTitledBorder( SwingConst.BORDER_LOWERED_ETCHED, "Samples" ) );
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(groupPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
						.addComponent(samplePanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(groupPanel, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(samplePanel, GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JLabel lblSampleName = new JLabel("Name");
		lblSampleName.setBounds(12, 189, 61, 16);
		
		this.txtSampleName = new JTextField();
		this.txtSampleName.setToolTipText("Sample Name or ID");
		this.txtSampleName.setBounds(86, 186, 287, 22);
		this.txtSampleName.setColumns(10);
		
		JLabel lblFile = new JLabel("File Path");
		lblFile.setBounds(12, 223, 61, 16);

		this.txtSampleFile = new JTextField();
		txtSampleFile.setEditable(false);
		this.txtSampleFile.setToolTipText("Sample file path");
		this.txtSampleFile.setBounds(86, 220, 338, 22);
		this.txtSampleFile.setColumns(10);

		JButton btnFile = new JButton("File...");
		btnFile.setToolTipText("Choose sample file");
		btnFile.setBounds(430, 220, 75, 22);
		btnFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//Handle open button action.
				final JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter( new UserDataFileFilter() );

	            int returnVal = fc.showOpenDialog( remote );
	 
	            if ( returnVal == JFileChooser.APPROVE_OPTION ) {
	                String samplePath = fc.getSelectedFile().getAbsolutePath();
	                remote.txtSampleFile.setText( samplePath );
	            }
			}
		});
		
		JLabel lblGroup = new JLabel("Group");
		lblGroup.setBounds(12, 260, 56, 16);

		/*** Over JDK 1.7 ***/
//		this.cmbGroupSelect = new JComboBox<String>();
		/*** Under JDK 1.7 ***/
		this.cmbGroupSelect = new JComboBox();
		this.cmbGroupSelect.setToolTipText("Group of sample");
		this.cmbGroupSelect.setBounds(86, 257, 208, 22);
		this.cmbGroupSelect.setEditable(false);

		JLabel lblNo = new JLabel("No.");
		lblNo.setBounds(12, 155, 56, 16);
		
		this.txtOrder = new JTextField();
		this.txtOrder.setBounds(86, 152, 134, 22);
		this.txtOrder.setEditable(false);
		this.txtOrder.setColumns(10);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 24, 501, 116);
		
		JButton btnSampleEdit = new JButton("Edit");
		btnSampleEdit.setToolTipText("Edit Sample");
		btnSampleEdit.setBounds(370, 144, 70, 22);
		
		JButton btnSampleDel = new JButton("Del");
		btnSampleDel.setToolTipText("Delete Sample");
		btnSampleDel.setBounds(443, 144, 70, 22);
		
		JButton btnSampleAdd = new JButton("Add");
		btnSampleAdd.setToolTipText("Add Sample");
		btnSampleAdd.setBounds(297, 144, 70, 22);
		
		btnSampleAdd.addActionListener( new AddSampleActionListener(this) );
		btnSampleEdit.addActionListener( new EditSampleActionListener( this ) );
		btnSampleDel.addActionListener( new DelSampleActionListener( this ) );
		
		this.samplePanel.setLayout(null);
		this.samplePanel.add(scrollPane);

		scrollPane.setViewportView( this.getTable() );

		final JTable tblSampleList = this.getTable();
		DefaultTableCellRenderer dtcr = ((DefaultTableCellRenderer)tblSampleList.getTableHeader().getDefaultRenderer());
		dtcr.setHorizontalAlignment(JLabel.CENTER);
		
		tblSampleList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblSampleList.rowAtPoint( e.getPoint() );

				DefaultTableModel model = (DefaultTableModel)tblSampleList.getModel();
				
				if( row >= 0 && row < model.getRowCount() ) {
					/*** Over JDK 1.7 ***/
//					Integer sampleNumber	= (int)model.getValueAt( row, 0 );
					/*** Under JDK 1.7 ***/
					Integer sampleNumber	= Integer.valueOf( model.getValueAt( row, 0 ).toString() );
					String groupId			= (String)model.getValueAt( row, 1 );
					String sampleName		= (String)model.getValueAt( row, 2 );
					String samplePath		= (String)model.getValueAt( row, 3 );

					remote.txtSampleName.setText( sampleName );
					remote.txtSampleFile.setText( samplePath );
					remote.txtOrder.setText( sampleNumber.toString() );
					remote.cmbGroupSelect.setSelectedItem( groupId );
				}
			}
		});
		
		this.samplePanel.add(lblSampleName);
		this.samplePanel.add(lblFile);
		this.samplePanel.add(this.txtSampleFile);
		this.samplePanel.add(this.txtSampleName);
		this.samplePanel.add(lblGroup);
		this.samplePanel.add(this.txtOrder);
		this.samplePanel.add(this.cmbGroupSelect);
		this.samplePanel.add(lblNo);
		this.samplePanel.add(btnSampleAdd);
		this.samplePanel.add(btnSampleEdit);
		this.samplePanel.add(btnSampleDel);
		this.samplePanel.add(btnFile);
		
		JLabel lblExNormalTumour = new JLabel("Ex) Sample 1");
		lblExNormalTumour.setForeground(new Color(0, 51, 102));
		lblExNormalTumour.setBounds(385, 189, 128, 16);
		this.samplePanel.add(lblExNormalTumour);
		
		JLabel lblchooseOneOf = new JLabel("( Choose one of Group ID(s) )");
		lblchooseOneOf.setForeground(new Color(0, 51, 102));
		lblchooseOneOf.setBounds(306, 260, 208, 16);
		this.samplePanel.add(lblchooseOneOf);
		
		JLabel lblNewLabel = new JLabel("Group ID");
		lblNewLabel.setBounds(12, 27, 56, 16);
		
		final JButton btnAdd	= new JButton("Add");
		btnAdd.setToolTipText("Add Group");
		btnAdd.setBounds(297, 24, 70, 22);
		final JButton btnEdit	= new JButton("Edit");
		btnEdit.setToolTipText("Edit Group");
		btnEdit.setBounds(370, 24, 70, 22);
		final JButton btnDel	= new JButton("Del");
		btnDel.setToolTipText("Delete Group");
		btnDel.setBounds(443, 24, 70, 22);
		
		btnAdd.addActionListener(	new AddGroupActionListener( this ) );
		btnEdit.addActionListener(	new EditGroupActionListener( this ) );
		btnDel.addActionListener(	new DelGroupActionListener( this ) );

		/*** Over JDK 1.7 ***/
//		this.cmbMngGroup = new JComboBox<String>();
		/*** Under JDK 1.7 ***/
		this.cmbMngGroup = new JComboBox();
		this.cmbMngGroup.setToolTipText("Group ID");
		this.cmbMngGroup.setBounds(86, 24, 202, 22);
		this.cmbMngGroup.setEditable(true);

		this.cmbMngGroup.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange() == ItemEvent.SELECTED ) {
					remote.setChoosedGroupNameToEdit( e.getItem().toString() );
				}
			}
		});

		this.cmbMngGroup.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				if( e.getKeyCode() == KeyEvent.VK_ENTER ){
					btnAdd.doClick();
				}
			}
		});

		this.groupPanel.setLayout(null);
		this.groupPanel.add(lblNewLabel);
		this.groupPanel.add(this.cmbMngGroup);
		this.groupPanel.add(btnAdd);
		this.groupPanel.add(btnEdit);
		this.groupPanel.add(btnDel);
		this.setLayout(groupLayout);
	}

	public void initGroupInfo() {
		this.cmbMngGroup.setSelectedItem("");
	}

	public void initSampleInfo() {
		this.txtSampleName.setText("");
		this.txtSampleFile.setText("");
		this.cmbGroupSelect.setSelectedIndex(0);
		this.txtOrder.setText("");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateCurrentState( ProjectMapItem projectMapItem ) throws  Exception{
		JDialog dialog = this.getOwnerDialog();
		if( dialog instanceof JProjectDialog ) {
			JProjectDialog projectDialog = (JProjectDialog)dialog;
			projectDialog.getTxtProjectName().setText( projectMapItem.getProjectName() );

			this.cmbMngGroup.removeAllItems();
			this.cmbGroupSelect.removeAllItems();
			
			List<Group> groupList = projectMapItem.getProjectInfo().getSamples().getGroup();
			Iterator<Group> iter = groupList.iterator();
			while( iter.hasNext() ) {
				Group group = iter.next();
				this.cmbMngGroup.addItem( group.getGroupId() );
				this.cmbGroupSelect.addItem( group.getGroupId() );

				this.setNumberOfSample( this.getNumberOfSample() + group.getSample().size() );
			}
			projectDialog.getTxtProjectName().setEnabled(false);
			this.msb.setProject( projectMapItem.getProjectInfo() );
	
			this.refreshSampleTable( groupList );
		}
	}

	public Msb getMsb() {
		return this.msb;
	}

	public JTextField getTxtSamplePath() {
		return this.txtSampleFile;
	}
	
	public JTextField getTxtSampleName() {
		return this.txtSampleName;
	}

	public JTable getTblSampleList() {
		return this.getTable();
	}
	
	public JTextField getTxtOrder() {
		return this.txtOrder;
	}
	
	public String getChoosedGroupNameToEdit() {
		return choosedGroupNameToEdit;
	}
	public void setChoosedGroupNameToEdit(String choosedGroupNameToEdit) {
		this.choosedGroupNameToEdit = choosedGroupNameToEdit;
	}

	/*** Over JDK 1.7 ***/
//	public JComboBox<String> getCmbMngGroup() {
//		return this.cmbMngGroup;
//	}
//
//	public JComboBox<String> getCmbGroupSelect() {
//		return this.cmbGroupSelect;
//	}
	/*** Under JDK 1.7 ***/
	@SuppressWarnings("rawtypes")
	public JComboBox getCmbMngGroup() {
		return this.cmbMngGroup;
	}

	@SuppressWarnings("rawtypes")
	public JComboBox getCmbGroupSelect() {
		return this.cmbGroupSelect;
	}

	@Override
	public void setFocusProjectName() {
		// TODO Auto-generated method stub
		this.getOwnerDialog().setFocusProjectName();
	}
}
