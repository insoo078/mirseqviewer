package kobic.msb.swing.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import kobic.com.normalization.Normalization;
import kobic.msb.common.ImageConstant;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.filefilter.CSVFileFilter;
import kobic.msb.swing.frame.dialog.JMsbReadCountTableConfigurationDialog;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;

public class JMsbReadCountTableToolbar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton							btnSortAln;
	private JButton							btnSortAlnEnd;
	private JMsbOptionButton				btnSortBy;
	private JButton							btnNormalize;
	private JButton							btnRawCount;
	private JButton							btnDownload;
	private JButton							btnConfig;
	/*** Over JDK 1.7 ***/
//	private JComboBox<String>				cmbNormalizingMethod;
	/*** Under JDK 1.7 ***/
	@SuppressWarnings("rawtypes")
	private JComboBox						cmbNormalizingMethod;
	
//	private Model							model;
	private AlignmentDockingWindowObj		dockWindow;
	
	private ProjectConfiguration			config;
	
	private JMsbReadCountTableToolbar		remote = JMsbReadCountTableToolbar.this;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JMsbReadCountTableToolbar( AlignmentDockingWindowObj dockWindow, String projectName, String mirid ) {
		this.dockWindow				= dockWindow;
		
//		this.model					= MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName ).getProjectModel( mirid );
		this.config					= MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName ).getProjectConfiguration();

		FlowLayout layout = new FlowLayout();
		layout.setAlignment( FlowLayout.LEADING );
		this.setLayout( layout );

		JLabel lblSort				= new JLabel("Sort");
		JLabel lblNormalize 		= new JLabel("Normalize");
		
		/*** Over JDK 1.7 ***/
//		this.cmbNormalizingMethod	= new JComboBox<String>();
		/*** Under JDK 1.7 ***/
		this.cmbNormalizingMethod	= new JComboBox();
		
		for(int i=0; i<Normalization.methods.length; i++) {
			this.cmbNormalizingMethod.addItem( Normalization.methods[i] );
		}
		this.cmbNormalizingMethod.setSelectedItem( this.config.getNormalizationMethod() );
		this.cmbNormalizingMethod.setToolTipText("Normalization method");

		this.btnSortAln				= new JMsbNullBorderButton( ImageConstant.sortAlnIcon24,			"Sort from 5'" );
		this.btnSortAlnEnd  		= new JMsbNullBorderButton( ImageConstant.sortAlnEndIcon24, 		"Sort from 3'");
		this.btnSortBy 				= new JMsbOptionButton( ImageConstant.sortAscIcon24,				"Arrange read count data in ascending or descending order" );
		
		this.btnRawCount			= new JMsbNullBorderButton( ImageConstant.rawRaeadCountDataIcon,	"Raw data");
		this.btnNormalize			= new JMsbNullBorderButton( ImageConstant.normalizationDataIcon,	"Normalization");
		
		this.btnDownload			= new JMsbNullBorderButton( ImageConstant.download2Icon,			"Download Data" );
		this.btnConfig				= new JMsbNullBorderButton( ImageConstant.configIcon24,				"Configuration" );

		this.add( lblSort );
		this.add( this.btnSortAln );
		this.add( this.btnSortAlnEnd );
		this.add( this.btnSortBy );

		this.addSeparator(new Dimension(20,20));
		
		this.add( lblNormalize );
		this.add( this.cmbNormalizingMethod );
		this.add( this.btnNormalize );
		this.add( this.btnRawCount );
		this.add( new JPanel() );

		this.addSeparator(new Dimension(20,20));
		
		this.add( this.btnDownload );
		this.add( this.btnConfig );

		this.btnConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				JMsbReadCountTableConfigDialog dialog = new JMsbReadCountTableConfigDialog( remote.dockWindow.getMainFrame(), remote.dockWindow, "Read count table Configuration", Dialog.ModalityType.APPLICATION_MODAL);
				JMsbReadCountTableConfigurationDialog dialog = new JMsbReadCountTableConfigurationDialog( remote.dockWindow.getMainFrame(), remote.dockWindow, "Read count table Configuration", Dialog.ModalityType.APPLICATION_MODAL);
				dialog.setVisible(true);
			}
		});

		this.btnDownload.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed( false );
				fc.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
				fc.setDialogTitle("Download Expression Data");
				fc.setFileFilter( new CSVFileFilter() );
				int returnVal = fc.showSaveDialog( null );

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = new File(fc.getSelectedFile().getAbsoluteFile() + ".csv");
					
					try {
						BufferedWriter out = new BufferedWriter(new FileWriter( file ));
						
						double[][] data = remote.dockWindow.getCurrentModel().getExpressionProfile( remote.dockWindow.getCurrentModel().isNormalized() );

						List<String> headerList = remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel().getHeatMapHeader();
						for(int i=0; i<headerList.size(); i++) {
							out.write( headerList.get(i) );
							if( i < headerList.size() - 1 )	out.write(",");
						}
						out.newLine();
						
						for(int i=0; i<data.length; i++) {
							for(int j=0; j<data[0].length; j++) {
								String line = Double.toString( data[i][j] );
								if( j < data[0].length-1 )	line += ",";
								out.write( line );
							}
							out.newLine();
						}

						out.close();
					}catch(Exception ex) {
						MsbEngine.logger.error( "error : ", ex );
					}
				}
			}
		});
		
		this.btnNormalize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if( remote.btnNormalize.isEnabled() ) {
					if( remote.dockWindow.getCurrentModel().getHeatMapObject().getNubmerOfSamples() > 1 ) {
						String normalMethod = remote.cmbNormalizingMethod.getSelectedItem().toString();
	
						remote.config.setNormalizationMethod( normalMethod );
	
						remote.dockWindow.getCurrentModel().normalizeReadProfile( remote.config.getNormalizationMethod(), remote.config.getMissingValue() );
	
						remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
						
						remote.btnNormalize.setEnabled(false);
						remote.btnRawCount.setEnabled(true);
						
						remote.config.writeConfigFile();
					}else {
						JOptionPane.showMessageDialog( remote.dockWindow.getMainFrame(), "<html>You do not need to normalize your data<br>Because, you have just a one sample</html>", "Information", JOptionPane.INFORMATION_MESSAGE );
					}
				}
			}
		});

		this.btnRawCount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if( remote.btnRawCount.isEnabled() ) {
					remote.dockWindow.getCurrentModel().rawReadProfile();
					
					remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );

					remote.btnNormalize.setEnabled(true);
					remote.btnRawCount.setEnabled(false);
				}
			}
		});

		this.btnSortBy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				try {
					remote.dockWindow.getCurrentModel().sortBySortModel( remote.dockWindow.getCurrentModel().getProjectMapItem().getMsbSortModel() );
	
		        	remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
				}catch(Exception exp) {
					MsbEngine.logger.error( "Error : ", exp );
				}
			}
		});
		this.btnSortBy.addOptionActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				JMsbSortPopupMenu sortPopUp = new JMsbSortPopupMenu( remote.dockWindow, remote.dockWindow.getCurrentModel() );
				sortPopUp.show( remote.btnSortBy, 0, remote.btnSortBy.getY() + remote.btnSortBy.getHeight() );
			}
		});
		this.btnSortAlnEnd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remote.dockWindow.getCurrentModel().sortReadEndPosition();
				remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
			}
		});
		this.btnSortAln.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remote.dockWindow.getCurrentModel().sortReadPosition();
				remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
			}
		});
	}
}
