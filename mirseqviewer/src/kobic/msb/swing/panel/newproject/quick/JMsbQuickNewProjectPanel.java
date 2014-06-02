package kobic.msb.swing.panel.newproject.quick;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;

import kobic.com.picard.PicardUtilities;
import kobic.com.util.Utilities;
import kobic.msb.common.SwingConst;
import kobic.msb.common.util.MsvUtilities;
import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.swing.filefilter.UserDataFileFilter;
import kobic.msb.swing.frame.dialog.JMsbQuickNewProjectDialog;
import kobic.msb.swing.panel.newproject.JMsbSampleTableCommonPanel;
import kobic.msb.system.engine.MsbEngine;

public class JMsbQuickNewProjectPanel extends JMsbSampleTableCommonPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	private final JPanel contentPanel = new JPanel();
	private final JLabel lblFile = new JLabel("File");

//	private int						nSamples;		// # of samples(if you add sample, this number are increased or you subract sample, this number are decreased)

	private JTextField				txtProjectName;
	private JTextField				txtFilePath;
	@SuppressWarnings("rawtypes")
	private JComboBox				cmbOrganism;

	private Msb						msb;
	
	private JProgressBar			progressBar;
	private JTextArea				txtAreaLog;
	
	private DefaultStyledDocument	doc;
	private StyleContext			sc;
	
	private int						sampleLastIndex;
	
	private JMsbQuickNewProjectPanel remote = JMsbQuickNewProjectPanel.this;
	private JTextField txtOrder;

	/**
	 * Create the dialog.
	 */
	@SuppressWarnings("rawtypes")
	public JMsbQuickNewProjectPanel( JMsbQuickNewProjectDialog dialog ) {
		super( dialog );

		this.msb				= new Msb();
		

//		this.nSamples			= 0;
		this.sampleLastIndex	= 0;

		{
			this.setLayout(null);
			JPanel panel_1 = new JPanel();
			panel_1.setBounds(6, 6, 511, 352);
			this.add(panel_1);
			panel_1.setLayout(null);
			
			txtProjectName = new JTextField();
			txtProjectName.setBounds(98, 6, 217, 28);
			panel_1.add(txtProjectName);
			txtProjectName.setColumns(10);
			
			JLabel lblOrganism = new JLabel("Organism");
			lblOrganism.setBounds(6, 39, 61, 16);
			panel_1.add(lblOrganism);
			
			cmbOrganism = new JComboBox();
			cmbOrganism.setBounds(98, 36, 217, 27);
			panel_1.add(cmbOrganism);
			
			JScrollPane sampleScrollPane = new JScrollPane();
			sampleScrollPane.setBounds(6, 68, 497, 109);
			panel_1.add(sampleScrollPane);
			
			JTable tblSampleList = this.getTable();
			tblSampleList.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if( e.getSource() instanceof JTable ) {
						JTable tblSampleList = (JTable)e.getSource();
						int row = tblSampleList.rowAtPoint( e.getPoint() );
	
						DefaultTableModel model = (DefaultTableModel)tblSampleList.getModel();
						
						if( row >= 0 && row < model.getRowCount() ) {
							/*** Over JDK 1.7 ***/
	//						Integer sampleNumber	= (int)model.getValueAt( row, 0 );
							/*** Under JDK 1.7 ***/
							Integer sampleNumber	= Integer.valueOf( model.getValueAt( row, 0 ).toString() );
							String samplePath		= (String)model.getValueAt( row, 3 );

							remote.txtOrder.setText( sampleNumber.toString() );
							remote.txtFilePath.setText( samplePath );
						}
					}
				}
			});
			sampleScrollPane.setViewportView( tblSampleList );
			
			lblFile.setBounds(6, 185, 35, 29);
			panel_1.add(lblFile);
			
			txtFilePath = new JTextField();
			txtFilePath.setEditable(false);
			txtFilePath.setBounds(42, 185, 229, 28);
			panel_1.add(txtFilePath);
			txtFilePath.setColumns(10);
			
			JButton btnFile = new JButton("File...");
			btnFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					//Handle open button action.
					final JFileChooser fc = new JFileChooser();
					fc.setAcceptAllFileFilterUsed(false);
					fc.addChoosableFileFilter( new UserDataFileFilter() );

		            int returnVal = fc.showOpenDialog( remote );
		 
		            if ( returnVal == JFileChooser.APPROVE_OPTION ) {
		                String samplePath = fc.getSelectedFile().getAbsolutePath();
		                remote.txtFilePath.setText( samplePath );
		            }
				}
			});
			btnFile.setBounds(269, 186, 85, 29);
			panel_1.add(btnFile);
			
			this.progressBar = new JProgressBar();
			this.progressBar.setIndeterminate(false);
			progressBar.setBounds(6, 314, 497, 20);
			panel_1.add(progressBar);

			JButton btnAdd = new JButton("Add");
			btnAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						// TODO Auto-generated method stub
						String group		= "group";
						String sampleName	= "sample";
						String samplePath	= Utilities.nulltoEmpty( remote.txtFilePath.getText() );

						if ( samplePath.equals("") ) {
							JOptionPane.showMessageDialog( remote, "Sample file path is empty!!");
							remote.txtFilePath.requestFocus();
							return;
						}
	
						if( !PicardUtilities.isSorted( samplePath) ) {
							JOptionPane.showMessageDialog( remote, "Sorry! This BAM file is not sorted!!");
							return;
						}
						String baiFilePath = samplePath + ".bai";
						if( !new File(baiFilePath).exists() ) {
							JOptionPane.showMessageDialog( remote, "Sorry! There is not index file!!");
							return;
						}
						
						List<Group> groupList = remote.getMsb().getProject().getSamples().getGroup();
						if( groupList.size() == 0 ) {
							Group newGroup = new Group();
							newGroup.setGroupId(group);
							newGroup.setId("Gid");
							groupList.add( newGroup );
						}
	
						Msb.Project.Samples.Group.Sample smp = new Msb.Project.Samples.Group.Sample();
						smp.setName( sampleName + remote.sampleLastIndex );
						smp.setSamplePath( samplePath );
						smp.setOrder( Integer.toString( remote.sampleLastIndex ) );
	
						Iterator<Group> iter = remote.msb.getProject().getSamples().getGroup().iterator();
						while( iter.hasNext() ) {
							Group grp = iter.next();
							if( grp.getGroupId().equals( group ) ) {
								grp.getSample().add( smp );
							}
						}
	
						remote.refreshSampleTable( groupList );
	
						remote.txtFilePath.setText("");
	
						remote.sampleLastIndex++;
//						remote.nSamples++;
						remote.incrementNoOfSample();
					}catch(Exception ixe) {
						MsbEngine.logger.error("Error", ixe);
					}
				}
			});
			btnAdd.setBounds(381, 185, 61, 29);
			panel_1.add(btnAdd);
			
			JButton btnDel = new JButton("Del");
			btnDel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String sampleNumber	= Utilities.nulltoZero( remote.txtOrder.getText() );
//					remote.nSamples--;
					remote.decrementNoOfSamples();

					if( sampleNumber != null && !sampleNumber.equals("") ) {
						remote.txtFilePath.setText("");
//						int NUMBER = Integer.parseInt( sampleNumber );
//						DefaultTableModel newModel = new DefaultTableModel(null, col);
						List<Group> groupList = remote.getMsb().getProject().getSamples().getGroup();
						
						// To find and delete a sample
						MsvUtilities.findTargetToModify( groupList, sampleNumber );

						remote.refreshSampleTable( groupList );
					}
				}
			});
			btnDel.setBounds(442, 185, 61, 29);
			panel_1.add(btnDel);
			
			JScrollPane logScrollPane = new JScrollPane();
			logScrollPane.setBounds(6, 226, 497, 83);
			panel_1.add(logScrollPane);
			
			this.sc = new StyleContext();
		    this.doc = new javax.swing.text.DefaultStyledDocument( sc );

			this.txtAreaLog = new javax.swing.JTextArea( doc );
			this.txtAreaLog.setText("");
			this.txtAreaLog.setEditable(false);
			this.txtAreaLog.setFont( SwingConst._9_FONT );
			logScrollPane.setViewportView( this.txtAreaLog );

			JLabel lblProjectName = new JLabel("Project Name");
			lblProjectName.setBounds(6, 12, 85, 16);
			panel_1.add(lblProjectName);
			
			txtOrder = new JTextField();
			txtOrder.setBounds(369, 6, 134, 28);
			panel_1.add(txtOrder);
			txtOrder.setColumns(10);
			txtOrder.setVisible(false);
		}

		this.initialize();
	}
	
	@SuppressWarnings("unchecked")
	public void initialize() {
		LinkedHashMap<String, String>		organMap		= MsbEngine.getInstance().getOrganismMap();

		Iterator<String> iter = organMap.keySet().iterator();
		while( iter.hasNext() ) {
			String organism = iter.next();
			this.cmbOrganism.addItem( organism );
		}
		this.cmbOrganism.setSelectedItem("human");
	}
	
	public JTextField getTextProjectName() {
		return this.txtProjectName;
	}
	

	public Msb getMsb() {
		return this.msb;
	}
	
	public void setFocusProjectName() {
		this.txtProjectName.requestFocus();
	}
	
	public String getProjectName() {
		return this.txtProjectName.getText();
	}
	
	public String getOrganismInfo() {
		return this.cmbOrganism.getSelectedItem().toString();
	}
	
	public void setIndeterminate( final boolean value ) {
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				remote.progressBar.setIndeterminate( value );
			}
		});
	}
	
	public void setProgressToGetMiRnas(final int value) {
		// TODO Auto-generated method stub	
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	remote.progressBar.setValue( value );
	        }
		});
	}
	
	public void setProgressBarRange(final int a, final int b) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	remote.progressBar.setMaximum(b);
	        	remote.progressBar.setMinimum(a);
	        }
		});
	}
	
	public int getProgressToGetMiRnas() {
		return remote.progressBar.getValue();
	}
	
	public JTextArea getScrollTextPane() {
		return this.txtAreaLog;
	}
}
