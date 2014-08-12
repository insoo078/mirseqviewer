package kobic.msb.swing.panel.newproject;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.ListSelectionModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import kobic.com.util.Utilities;
import kobic.msb.common.SwingConst;
import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.server.model.jaxb.Msb.Project;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.filefilter.UserDataFileFilter;
import kobic.msb.swing.filefilter.XmlFileFilter;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.swing.listener.projectdialog.AddGroupActionListener;
import kobic.msb.swing.listener.projectdialog.DelGroupActionListener;
import kobic.msb.swing.listener.projectdialog.EditGroupActionListener;
import kobic.msb.swing.thread.DownloadTask;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class JMsvNewProjectPanel extends JMsvGroupControlPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel groupPanel;
	private JPanel samplePanel;
	private JScrollPane scrollPane;
	
	private JButton btnFile;
	private JButton btnAdd;
	private JButton btnEdit;
	private JButton btnDel;
	private JButton btnDelete;

	private JMsvNewProjectPanel		remote = JMsvNewProjectPanel.this;
	private JButton btnImportFromXml;
	private JButton btnDownloadSampleXml;

	/**
	 * Create the panel.
	 */
	public JMsvNewProjectPanel( JProjectDialog owner ) {
		super( owner, new int[]{1, 2} );

		this.groupPanel = new JPanel();
		this.samplePanel = new JPanel();

		DefaultTableCellRenderer dtcr = ((DefaultTableCellRenderer)this.getTable().getTableHeader().getDefaultRenderer());
		dtcr.setHorizontalAlignment(JLabel.CENTER);

		this.groupPanel.setBorder( BorderFactory.createTitledBorder( SwingConst.BORDER_LOWERED_ETCHED, "Groups" ) );
		this.samplePanel.setBorder( BorderFactory.createTitledBorder( SwingConst.BORDER_LOWERED_ETCHED, "Samples" ) );
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(this.samplePanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
						.addComponent(this.groupPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(this.groupPanel, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(this.samplePanel, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		this.scrollPane = new JScrollPane( this.getTable() );
		
		this.getTable().setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		
		this.btnFile = new JButton("File...");
		this.btnFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//Handle open button action.

				final JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter( new UserDataFileFilter() );
				fc.setMultiSelectionEnabled(true);

				int returnVal = fc.showOpenDialog( remote );

				if (returnVal == JFileChooser.APPROVE_OPTION ) {
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
	
					JComboBox combo = new JComboBox();
					for(int i=0; i<remote.getCmbMngGroup().getItemCount();i++)
						combo.addItem( remote.getCmbMngGroup().getItemAt(i) );
	
					TableColumn column1 = remote.getTable().getColumnModel().getColumn(1);
					column1.setCellEditor( new DefaultCellEditor( combo ) );
					column1.setCellRenderer(new CheckBoxCellRenderer( combo ) );
	
					TableColumn column2 = remote.getTable().getColumnModel().getColumn(2);
					column2.setCellEditor( new MyTableCellEditor( remote.getTable() ) );
					
					remote.getOwnerDialog().getNextButton().setEnabled( true );
				}
			}
		});

		this.btnDelete = new JButton("Delete");
		this.btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remote.deleteSelectedRows( remote.getTable() );
			}
		});
		
		this.btnImportFromXml = new JButton("Import from XML");
		this.btnImportFromXml.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter( new XmlFileFilter() );
				
				int returnVal = fc.showOpenDialog( remote );
				
				if( returnVal == JFileChooser.APPROVE_OPTION ) {
					File file = fc.getSelectedFile();
					
					try {
						JAXBContext jaxbContext = JAXBContext.newInstance( Msb.class );

						Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
						Msb msb = (Msb) jaxbUnmarshaller.unmarshal( file );
						
						if( !remote.allrightAllFiles(msb) ) msb = new Msb();
						else								remote.getOwnerDialog().getNextButton().setEnabled( true );

						remote.loadFromMsbProject( msb.getProject() );
					}catch(Exception exp) {
						JOptionPane.showMessageDialog( remote, "Unmarshalling problem from XML to object : We can not read your XML file", "Loading error", JOptionPane.ERROR_MESSAGE );
						MsbEngine.logger.error("error : ", exp);
					}
				}
			}
		});
		
		this.btnDownloadSampleXml = new JButton("Download Sample XML");
		this.btnDownloadSampleXml.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter( new XmlFileFilter() );
				fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );

				int returnVal = fc.showSaveDialog( remote );

				if( returnVal == JFileChooser.APPROVE_OPTION ) {
					File file = fc.getSelectedFile();
					
					DownloadTask task = new DownloadTask( remote, MsbEngine.getInstance().getSystemProperties().getProperty("msb.http.url") + "/download/sample.xml", file.getAbsolutePath() );
					task.execute();
				}
			}
		});

		GroupLayout gl_samplePanel = new GroupLayout(this.samplePanel);
		gl_samplePanel.setHorizontalGroup(
			gl_samplePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_samplePanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_samplePanel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
						.addGroup(gl_samplePanel.createSequentialGroup()
							.addComponent(btnFile)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnImportFromXml)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDownloadSampleXml)
							.addPreferredGap(ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
							.addComponent(btnDelete)))
					.addContainerGap())
		);
		gl_samplePanel.setVerticalGroup(
			gl_samplePanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_samplePanel.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_samplePanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnFile)
						.addComponent(btnDelete)
						.addComponent(btnImportFromXml)
						.addComponent(btnDownloadSampleXml))
					.addContainerGap())
		);

		this.scrollPane.setViewportView( this.getTable() );
		this.samplePanel.setLayout(gl_samplePanel);
		
		JLabel lblGroupId = new JLabel("Group ID");
		
		this.btnAdd = new JButton("Add");
		this.btnEdit = new JButton("Edit");
		this.btnDel = new JButton("Del");
		
		this.btnAdd.addActionListener(	new AddGroupActionListener( this ) );
		this.btnEdit.addActionListener(	new EditGroupActionListener( this ) );
		this.btnDel.addActionListener(	new DelGroupActionListener( this ) );

		GroupLayout gl_groupPanel = new GroupLayout(groupPanel);
		gl_groupPanel.setHorizontalGroup(
			gl_groupPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_groupPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblGroupId)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent( this.getCmbMngGroup(), 0, 185, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(this.btnAdd)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(this.btnEdit)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(this.btnDel)
					.addContainerGap())
		);
		gl_groupPanel.setVerticalGroup(
			gl_groupPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_groupPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_groupPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblGroupId)
						.addComponent( this.getCmbMngGroup(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnDel)
						.addComponent(this.btnEdit)
						.addComponent(this.btnAdd))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupPanel.setLayout(gl_groupPanel);
		setLayout(groupLayout);

	}
	
	private boolean validateFile( String order, String path, String ext ) {
		File file = new File( path );

		if( !Utilities.getExtension(file).toLowerCase().equals( ext ) )		{
			JOptionPane.showMessageDialog( remote, order + ") " + file.getName() + " is not '" + ext.toUpperCase() + "' format file", "Loading error", JOptionPane.ERROR_MESSAGE );
			return false;
		}else if( !file.exists() ){
			JOptionPane.showMessageDialog( remote, order + ") " + file.getName() + " does not exist!", "Loading error", JOptionPane.ERROR_MESSAGE );
			return false;
		}

		return true;
	}

	private boolean allrightAllFiles( Msb msb ) {
		List<Group> groupList = msb.getProject().getSamples().getGroup();
		for (Iterator<Group>iter=groupList.iterator();iter.hasNext();) {
			Group group = iter.next();
			for(Iterator<Sample> iterSample = group.getSample().iterator(); iterSample.hasNext();) {
				Sample sample = iterSample.next();
				if( !Utilities.nulltoEmpty( sample.getSamplePath() ).isEmpty() )	{
					if( this.validateFile( sample.getOrder(), sample.getSamplePath(), "bam" ) == false )	return false;
				}
				
				if( !Utilities.nulltoEmpty( sample.getSortedPath() ).isEmpty() )	{
					if( this.validateFile( sample.getOrder(), sample.getSortedPath(), "bam" ) == false ) 	return false;
				}
				
				if( !Utilities.nulltoEmpty( sample.getIndexPath() ).isEmpty() )	{
					if( this.validateFile( sample.getOrder(), sample.getIndexPath(), "bai" ) == false ) 	return false;
				}
			}
		}
		
		return true;
	}

	private void deleteSelectedRows( JTable table ) {
        int[] selectedRows = table.getSelectedRows();
        table.clearSelection();

        // get model rows
        List<Integer> selectedModelRows = new LinkedList<Integer>();
        for (int i =0; i < selectedRows.length; i++) {
            selectedModelRows.add( table.convertRowIndexToModel(selectedRows[i]) );
        }

        Collections.sort(selectedModelRows, Collections.reverseOrder());

        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        for (int selectedModelRow : selectedModelRows) {
            tableModel.removeRow(selectedModelRow);
            tableModel.fireTableRowsDeleted(selectedModelRow, selectedModelRow);
        }
        tableModel.fireTableDataChanged();

        MsbEngine.logger.debug( tableModel.getRowCount() );
        table.revalidate();
	}

	@Override
	public void setFocusProjectName() {
		// TODO Auto-generated method stub
		this.getOwnerDialog().setFocusProjectName();
	}

	private void loadFromMsbProject(Project project) {
		JDialog dialog = this.getOwnerDialog();
		if( dialog instanceof JProjectDialog ) {
			JProjectDialog projectDialog = (JProjectDialog)dialog;
			projectDialog.getTxtProjectName().setText( project.getProjectName() );

			this.getCmbMngGroup().removeAllItems();
	//		this.cmbGroupSelect.removeAllItems();
			
			List<Group> groupList = project.getSamples().getGroup();
			Iterator<Group> iter = groupList.iterator();
			while( iter.hasNext() ) {
				Group group = iter.next();
				this.getCmbMngGroup().addItem( group.getGroupId() );
	//			this.cmbGroupSelect.addItem( group.getGroupId() );
	
				this.setNumberOfSample( this.getNumberOfSample() + group.getSample().size() );
			}
			projectDialog.getTxtProjectName().setEnabled(false);
			this.getMsb().setProject( project );
	
			this.refreshSampleTable( groupList );
		}
	}
	@Override
	public void updateCurrentState( ProjectMapItem projectMapItem ) throws Exception {
		// TODO Auto-generated method stub
		this.loadFromMsbProject( projectMapItem.getProjectInfo() );
	}

	class CheckBoxCellRenderer implements TableCellRenderer {
        JComboBox combo;

        public CheckBoxCellRenderer( JComboBox comboBox ) {
            this.combo = new JComboBox();
            for (int i=0; i<comboBox.getItemCount(); i++){
                combo.addItem( comboBox.getItemAt(i) );
            }
        }

        public Component getTableCellRendererComponent( JTable jtable, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
        	if( Utilities.emptyToNull(value) != null )
        		combo.setSelectedItem(value);
        	else if( combo.getModel().getSize() > 0 ){
        		combo.setSelectedIndex(0);
        		
        		DefaultTableModel tModel = (DefaultTableModel)jtable.getModel();
        		tModel.setValueAt( combo.getSelectedItem(), row, column);
        	}

            return combo;
        }
    }

	class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private JTextField component = new JTextField();
		private String str;
		private int row;
		private int col;
		private JTable table;
		
		public MyTableCellEditor(JTable table) {
			this.table = table;
			this.str = "";
			this.component.getDocument().addDocumentListener( new MyDocumentListener(this) );
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int columnIndex) {
			this.row = rowIndex;
			this.col = columnIndex;

			((JTextField) component).setText( Utilities.nulltoEmpty( value ).toString());
//			
//			MsbEngine.logger.debug("Called : " + component.getText() );

			return component;
		}

		public Object getCellEditorValue() {
			return ((JTextField) component).getText();
		}
		
		public JTextField getTextField() {
			return this.component;
		}
		
		public void updateValue(String str) {
			this.str = str;
//			MsbEngine.logger.debug("row : " + row + "  col : " + col );
			table.getModel().setValueAt( this.str, this.row, this.col);
		}
		public String getStr() {
			return this.str;
		}
	}
	
	private class MyDocumentListener implements DocumentListener {
		private MyTableCellEditor				tf;
		
		MyDocumentListener( MyTableCellEditor tf ) {
			this.tf				= tf;
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			String value = tf.getTextField().getText();
			
			tf.updateValue( value );
			
//			MsbEngine.logger.debug( tf.getStr() );
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			this.insertUpdate(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
		}
	}
}
