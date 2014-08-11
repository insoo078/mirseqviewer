package kobic.msb.swing.panel.newproject;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import kobic.msb.common.SwingConst;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.filefilter.UserDataFileFilter;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.swing.listener.projectdialog.AddGroupActionListener;
import kobic.msb.swing.listener.projectdialog.DelGroupActionListener;
import kobic.msb.swing.listener.projectdialog.EditGroupActionListener;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;

import net.infonode.util.ArrayUtil;

public class JMsvNewProjectPanel extends JMsvGroupControlPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JMsvNewProjectPanel		remote = JMsvNewProjectPanel.this;

	/**
	 * Create the panel.
	 */
	public JMsvNewProjectPanel( JProjectDialog owner ) {
		super( owner, new int[]{1, 2} );

		JPanel groupPanel = new JPanel();
		
		JPanel samplePanel = new JPanel();

		DefaultTableCellRenderer dtcr = ((DefaultTableCellRenderer)this.getTable().getTableHeader().getDefaultRenderer());
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
		
		JScrollPane scrollPane = new JScrollPane( this.getTable() );
		

		JButton btnFile = new JButton("File...");
		btnFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//Handle open button action.

				final JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter( new UserDataFileFilter() );
				fc.setMultiSelectionEnabled(true);

				int returnVal = fc.showOpenDialog( remote );

				Group group = new Group();
				
				List<Sample> sampleList = group.getSample();

				if(sampleList == null)	sampleList = new ArrayList<Sample>();

				if ( returnVal == JFileChooser.APPROVE_OPTION ) {
					File[] files = fc.getSelectedFiles();
					for(int i=0; i<files.length; i++) {
						Sample sample = new Sample();
						sample.setOrder( Integer.toString( i ) );

						String bamPath = files[i].getAbsolutePath();
						String baiPath = bamPath + ".bai";
						String nBaiPath = bamPath.replace(".bam", ".bai");

						if( new File( baiPath ).exists() ) {
							sample.setIndexPath( baiPath );
							sample.setSamplePath( bamPath );
							sample.setSortedPath(bamPath);
						}else if( new File( nBaiPath ).exists() ) {
							sample.setIndexPath( nBaiPath );
							sample.setSamplePath( bamPath );
							sample.setSortedPath(bamPath);
						}else {
							sample.setSamplePath( bamPath );
						}
						sampleList.add( sample );
//						MsbEngine.logger.debug( files[i].getAbsoluteFile() );
					}
				}
				List<Group> gList = new ArrayList<Group>();
				gList.add(group);
				remote.refreshSampleTable( gList );
				
				TableColumn column1 = remote.getTable().getColumnModel().getColumn(1);
				column1.setCellEditor(new DefaultCellEditor(remote.getCmbMngGroup()) );

				TableColumn column2 = remote.getTable().getColumnModel().getColumn(2);
				column2.setCellEditor(new DefaultCellEditor( new JTextField()) );

				column1.setCellRenderer(new CheckBoxCellRenderer( remote.getCmbMngGroup()));
			}
		});

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

		scrollPane.setViewportView( this.getTable() );
		samplePanel.setLayout(gl_samplePanel);
		
		JLabel lblGroupId = new JLabel("Group ID");
		
		JButton btnAdd = new JButton("Add");
		
		JButton btnEdit = new JButton("Edit");
		
		JButton btnDel = new JButton("Del");
		
		btnAdd.addActionListener(	new AddGroupActionListener( this ) );
		btnEdit.addActionListener(	new EditGroupActionListener( this ) );
		btnDel.addActionListener(	new DelGroupActionListener( this ) );

		GroupLayout gl_groupPanel = new GroupLayout(groupPanel);
		gl_groupPanel.setHorizontalGroup(
			gl_groupPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_groupPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblGroupId)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent( this.getCmbMngGroup(), 0, 185, Short.MAX_VALUE)
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
						.addComponent( this.getCmbMngGroup(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnDel)
						.addComponent(btnEdit)
						.addComponent(btnAdd))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupPanel.setLayout(gl_groupPanel);
		setLayout(groupLayout);

	}

	@Override
	public void setFocusProjectName() {
		// TODO Auto-generated method stub
		this.getOwnerDialog().setFocusProjectName();
	}

	@Override
	public void updateCurrentState(ProjectMapItem projectMapItem) throws Exception {
		// TODO Auto-generated method stub
		JDialog dialog = this.getOwnerDialog();
		if( dialog instanceof JProjectDialog ) {
			JProjectDialog projectDialog = (JProjectDialog)dialog;
			projectDialog.getTxtProjectName().setText( projectMapItem.getProjectName() );

			this.getCmbMngGroup().removeAllItems();
//			this.cmbGroupSelect.removeAllItems();
			
			List<Group> groupList = projectMapItem.getProjectInfo().getSamples().getGroup();
			Iterator<Group> iter = groupList.iterator();
			while( iter.hasNext() ) {
				Group group = iter.next();
				this.getCmbMngGroup().addItem( group.getGroupId() );
//				this.cmbGroupSelect.addItem( group.getGroupId() );

				this.setNumberOfSample( this.getNumberOfSample() + group.getSample().size() );
			}
			projectDialog.getTxtProjectName().setEnabled(false);
			this.getMsb().setProject( projectMapItem.getProjectInfo() );
	
			this.refreshSampleTable( groupList );
		}
	}
	
	class CheckBoxCellRenderer implements TableCellRenderer {
        JComboBox combo;
        public CheckBoxCellRenderer(JComboBox comboBox) {
            this.combo = new JComboBox();
            for (int i=0; i<comboBox.getItemCount(); i++){
                combo.addItem(comboBox.getItemAt(i));
            }
        }
        public Component getTableCellRendererComponent(JTable jtable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            combo.setSelectedItem(value);
            return combo;
        }
    }
}
