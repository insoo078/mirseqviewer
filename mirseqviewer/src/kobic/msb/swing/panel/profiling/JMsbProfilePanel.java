package kobic.msb.swing.panel.profiling;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.infonode.util.ArrayUtil;

import kobic.com.normalization.Normalization;
import kobic.com.util.Utilities;
import kobic.msb.common.ImageConstant;
import kobic.msb.common.SwingConst;
import kobic.msb.common.util.MsvUtilities;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.db.sqlite.vo.MatureVO;
import kobic.msb.server.model.ClusterModel;
import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.swing.filefilter.CSVFileFilter;
import kobic.msb.swing.renderer.NumberTableCellRenderer;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import weka.core.Instances;

public class JMsbProfilePanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int	sortDirection = SwingConst.SORT_DESC;

	private JTextField				txtKeyword;
	private JButton					btnNormalization;
	private JButton					btnRefresh;
	private JTable					tblExpressionProfile;
	private JTable					tblRnaInfo;
	private JSplitPane				verticalSplitPane;
	private JSplitPane				horizontalSplitPane;
	private JMsbHeatMapPanel		graphPanel;
	private JMsbBoxplotPanel		boxplotPanel;
	/*** Over JDK 1.7 ***/
//	private JComboBox<String>		cmbNormalizeMethod;
	/*** Under JDK 1.7 ***/
	private JComboBox				cmbNormalizeMethod;
	
	private JTabbedPane				tabbedPane;
	private JMsbGroupBoxplotPanel	groupedBoxPanel;
	
	private ClusterModel			clusterModel;

	private UpdatableTableModel		matureRnaTablemodel;
	private UpdatableTableModel		rnaInfoTableModel;
	
	private String[]				columns;
	private Object[][]				data;
	private ProjectMapItem			projectMapItem;
	
	private JMsbProfilePanel		remote = JMsbProfilePanel.this;

	public JMsbProfilePanel(ClusterModel clusterModel, ProjectMapItem item) {
		this.clusterModel	= clusterModel;
		this.columns		= ArrayUtil.append( new String[]{"miRNA", "Sum"}, this.clusterModel.getColNames() );
		this.data			= new Object[ this.clusterModel.getRowNames().length ][ this.clusterModel.getColNames().length + 2 ];
		this.projectMapItem	= item;

		this.makeHeatmapTable();

		String[] title = new String[]{"name", "value"};
		this.rnaInfoTableModel = new UpdatableTableModel(null, title);
		
		this.matureRnaTablemodel = new UpdatableTableModel( this.data, this.columns );

		JPanel profileGroupPanel = new JPanel();

		JPanel expressionProfileTitledPanel = new JPanel();
		expressionProfileTitledPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Expression Profile", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION ));
		this.tblExpressionProfile = new JTable( this.matureRnaTablemodel );
		this.tblExpressionProfile.setDefaultRenderer( Object.class, new NumberTableCellRenderer() );
		
		this.tblExpressionProfile.setDragEnabled( false );
		this.tblExpressionProfile.getTableHeader().setReorderingAllowed(false);
		
		this.tblExpressionProfile.getTableHeader().addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseClicked(MouseEvent mouseEvent) {
	        	int selectedColumnIndex = remote.tblExpressionProfile.convertColumnIndexToModel( remote.tblExpressionProfile.columnAtPoint(mouseEvent.getPoint()) );
	        	
	    		if( remote.sortDirection == SwingConst.SORT_ASC )	remote.sortDirection = SwingConst.SORT_DESC;
	    		else												remote.sortDirection = SwingConst.SORT_ASC;

	        	remote.sortByExpressionLevelAt( selectedColumnIndex, remote.sortDirection );
	        	remote.graphPanel.setData( remote.data );
	        };
	    });
				
		this.tblExpressionProfile.addMouseListener( new MouseAdapter() {
			@Override
	        public void mouseClicked(MouseEvent mouseEvent) {
				int miridIndex = remote.tblExpressionProfile.getSelectedRow();

				double[] array = new double[ remote.data[0].length - 2 ];
				for(int i=2; i<remote.data[0].length; i++) {
					/*** Over JDK 1.7 ***/
//					if( remote.data[miridIndex][i]  instanceof Number )		array[i-2] = (double)remote.data[ miridIndex ][i];
					/*** Under JDK 1.7 ***/
					if( remote.data[miridIndex][i]  instanceof Number )		array[i-2] = Double.valueOf( remote.data[ miridIndex ][i].toString() );
					else													array[i-2] = Double.NaN;
				}
				
				remote.boxplotPanel.setVectorData( array );
				
				String mirid = remote.data[ miridIndex ][0].toString();
				
				remote.rnaInfoTableModel.removeAll();

//				MatureVO mature		= MsbEngine._db.getMicroRnaMatureByMirid( mirid );
//				HairpinVO hairpin	= MsbEngine._db.getMicroRnaHairpinByMirid2( mature.getId() );
				
				MatureVO mature		= MsbEngine.getInstance().getMiRBaseMap().get( projectMapItem.getMiRBAseVersion() ).getMicroRnaMatureByMirid( mirid );
				HairpinVO hairpin	= MsbEngine.getInstance().getMiRBaseMap().get( projectMapItem.getMiRBAseVersion() ).getMicroRnaHairpinByMirid2( mature.getId() );

				remote.rnaInfoTableModel.addRow( new Object[]{ "Hairpin", hairpin.getId()} );
				remote.rnaInfoTableModel.addRow( new Object[]{ "Location", hairpin.getChr() + ":" + hairpin.getStart() + "-" + hairpin.getEnd()} );
				remote.rnaInfoTableModel.addRow( new Object[]{ "Mature", mature.getMirid()} );
				remote.rnaInfoTableModel.addRow( new Object[]{ "Mature sequence", mature.getSequence()} );
				
				remote.tblRnaInfo.setModel( remote.rnaInfoTableModel );
			}
		});
				
		final JScrollPane scrollExpressionProfile = new JScrollPane( this.tblExpressionProfile );
		
		this.txtKeyword = new JTextField();
		this.txtKeyword.setColumns(10);
		
		this.txtKeyword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				String keyword = (remote.txtKeyword.getText().toLowerCase() + e.getKeyChar()).trim();
				
				if( !keyword.equals("") ) {
					int numOfChar = 8;
					if( keyword.startsWith( "mir-") || keyword.startsWith( "let-") )
						numOfChar = 4;
					if( keyword.length() > numOfChar ) {
						for(int i=0; i<remote.tblExpressionProfile.getRowCount(); i++) {
							if( remote.tblExpressionProfile.getValueAt(i, 0).toString().toUpperCase().contains( keyword.toUpperCase() ) ) {
								remote.tblExpressionProfile.getSelectionModel().setSelectionInterval(i, i);
								remote.tblExpressionProfile.scrollRectToVisible( new Rectangle( remote.tblExpressionProfile.getCellRect(i, 0, true) ) );

								break;
							}
						}
					}
				}else {
					remote.tblExpressionProfile.getSelectionModel().setSelectionInterval(0, 0);
					remote.tblExpressionProfile.scrollRectToVisible( new Rectangle( remote.tblExpressionProfile.getCellRect(0, 0, true) ) );
				}
			}
		});
		
		JButton btnDownload = new JButton( ImageConstant.downArrowIcon );
		btnDownload.setToolTipText("Download Data");
		btnDownload.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed( false );
//				fc.setApproveButtonText("Save");
				fc.setDialogTitle("Download Expression Data");
//				fc.setDialogType( JFileChooser.SAVE_DIALOG );
				fc.setFileFilter( new CSVFileFilter() );
				int returnVal = fc.showSaveDialog( null );
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = new File(fc.getSelectedFile().getAbsoluteFile() + ".csv");
					
					try {
						BufferedWriter out = new BufferedWriter(new FileWriter( file ));
						
						String[] colsName = remote.clusterModel.getColNames();
						out.write("miRNA,");
						out.write("Sum,");
						for(int i=0; i<colsName.length; i++) {	
							out.write( colsName[i] );
							if( i < colsName.length-1 )	out.write(",");
						}
						out.newLine();
						for(int i=0; i<remote.data.length; i++) {
							for(int j=0; j<remote.data[0].length; j++) {
								String line = remote.data[i][j].toString();
								if( j < remote.data[0].length-1 )	line += ",";
								out.write( line );
							}
							out.newLine();
						}

						out.close();
					}catch(Exception ex) {
						MsbEngine.logger.error( ex );
//						ex.printStackTrace();
					}
				}
			}
		});
		
		/*** Over JDK 1.7 ***/
//		this.cmbNormalizeMethod = new JComboBox<String>();
		/*** Under JDK 1.7 ***/
		this.cmbNormalizeMethod = new JComboBox();
		for(int i=0; i<Normalization.methods.length; i++) {
			this.cmbNormalizeMethod.addItem( Normalization.methods[i] );
		}
		this.cmbNormalizeMethod.setSelectedIndex(2);
		
		this.btnNormalization = new JButton("Normalize");
		this.btnNormalization.setToolTipText("Normalization with missing value imputation");

		if( this.clusterModel.getDGEListObject() != null ) {
			this.cmbNormalizeMethod.setEnabled(false);
			this.btnNormalization.setEnabled(false);
		}
		this.btnNormalization.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// TODO Auto-generated method stub
					String method = remote.cmbNormalizeMethod.getSelectedItem().toString();
	
					// To impute NA cell to your input
					remote.clusterModel.imputationWithDialog( remote );
					
					// do normalization
					double[][] mat = Normalization.doNormalize( method, remote.clusterModel, projectMapItem.getMiRBAseVersion() );
	
					// To update model and screen
					remote.clusterModel.doUpdateNormalizedData( mat, method );
	
					remote.makeHeatmapTable();
					remote.updateBoxplot();
					
					remote.matureRnaTablemodel = new UpdatableTableModel( remote.data, remote.columns );
					remote.tblExpressionProfile.setModel( remote.matureRnaTablemodel );
					
					remote.btnNormalization.setEnabled( false );
					remote.cmbNormalizeMethod.setEnabled( false );
				}catch(Exception exp) {
					MsbEngine.logger.error( exp );
//					exp.printStackTrace();
				}
			}
		});
		
		JLabel lblMirnaId = new JLabel("miRNA ID");
		
		this.btnRefresh = new JButton( ImageConstant.refreshIcon );
		this.btnRefresh.setToolTipText("Reload data");
		this.btnRefresh.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				boolean result = remote.clusterModel.doRefreshData();
				if( result ) {
					remote.makeHeatmapTable();
					remote.updateBoxplot();

					remote.matureRnaTablemodel = new UpdatableTableModel( remote.data, remote.columns );
					remote.tblExpressionProfile.setModel( remote.matureRnaTablemodel );

					remote.btnNormalization.setEnabled(true);
					remote.cmbNormalizeMethod.setEnabled(true);
				}
			}
		});
		GroupLayout gl_panel_6 = new GroupLayout(expressionProfileTitledPanel);
		gl_panel_6.setHorizontalGroup(
			gl_panel_6.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_6.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_6.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollExpressionProfile, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
						.addGroup(gl_panel_6.createSequentialGroup()
							.addComponent(btnDownload)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnRefresh)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(cmbNormalizeMethod, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNormalization)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(lblMirnaId)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtKeyword, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_panel_6.setVerticalGroup(
			gl_panel_6.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_6.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_6.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panel_6.createParallelGroup(Alignment.BASELINE)
							.addComponent(txtKeyword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnDownload)
							.addComponent(cmbNormalizeMethod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnNormalization)
							.addComponent(lblMirnaId))
						.addComponent(btnRefresh))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollExpressionProfile, GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
					.addContainerGap())
		);
		expressionProfileTitledPanel.setLayout(gl_panel_6);
		
		JPanel miRnaInfoTitledPanel = new JPanel();
		miRnaInfoTitledPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "miRNA Info.", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION ));
		
		this.tblRnaInfo = new JTable();
		JScrollPane scrollRnaInfoPane = new JScrollPane(this.tblRnaInfo);
		GroupLayout gl_miRnaInfoTitledPanel = new GroupLayout(miRnaInfoTitledPanel);
		gl_miRnaInfoTitledPanel.setHorizontalGroup(
			gl_miRnaInfoTitledPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_miRnaInfoTitledPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollRnaInfoPane, GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_miRnaInfoTitledPanel.setVerticalGroup(
			gl_miRnaInfoTitledPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_miRnaInfoTitledPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollRnaInfoPane, GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
					.addContainerGap())
		);
		miRnaInfoTitledPanel.setLayout(gl_miRnaInfoTitledPanel);
		GroupLayout gl_panel = new GroupLayout(profileGroupPanel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(miRnaInfoTitledPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
						.addComponent(expressionProfileTitledPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(expressionProfileTitledPanel, GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(miRnaInfoTitledPanel, GroupLayout.PREFERRED_SIZE, 196, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		profileGroupPanel.setLayout(gl_panel);

		JPanel selectedMiRnaTitledPanel = new JPanel();
		selectedMiRnaTitledPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Boxplot for a miRNA", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION ));
		
		this.boxplotPanel = new JMsbBoxplotPanel();
		GroupLayout gl_panel_7 = new GroupLayout(selectedMiRnaTitledPanel);
		gl_panel_7.setHorizontalGroup(
			gl_panel_7.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_7.createSequentialGroup()
					.addContainerGap()
					.addComponent(boxplotPanel, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel_7.setVerticalGroup(
			gl_panel_7.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_7.createSequentialGroup()
					.addContainerGap()
					.addComponent(boxplotPanel, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
					.addContainerGap())
		);
		selectedMiRnaTitledPanel.setLayout(gl_panel_7);

		this.tabbedPane = new JTabbedPane();

		this.graphPanel			= new JMsbHeatMapPanel( this.data );
		this.groupedBoxPanel	= new JMsbGroupBoxplotPanel();
		this.updateBoxplot();

		this.tabbedPane.addTab("Expression Profile", this.graphPanel);
		this.tabbedPane.addTab("Boxplot for nomalized samples", this.groupedBoxPanel );

		this.verticalSplitPane		= new JSplitPane(JSplitPane.VERTICAL_SPLIT, selectedMiRnaTitledPanel, this.tabbedPane);
		
		this.horizontalSplitPane	= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, profileGroupPanel, this.verticalSplitPane);
		this.horizontalSplitPane.setDoubleBuffered(true);
		this.horizontalSplitPane.setOneTouchExpandable(true);
		this.horizontalSplitPane.setContinuousLayout(true);

		GroupLayout gl_panel_5 = new GroupLayout( this );
		gl_panel_5.setHorizontalGroup(
			gl_panel_5.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_5.createSequentialGroup()
					.addContainerGap()
					.addComponent(this.horizontalSplitPane, GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel_5.setVerticalGroup(
			gl_panel_5.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_5.createSequentialGroup()
					.addContainerGap()
					.addComponent(this.horizontalSplitPane, GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
					.addContainerGap())
		);		

		this.setLayout(gl_panel_5);
		
		this.clusterModel.addObserver( this.graphPanel );
	}
	
	public void multiArrayCopy(Object[][] source, Object[][] destination){
		destination=source.clone();
	}
	
	public void sortByExpressionLevelAt( int pos, int direction ) {
		try {
	    	if( pos >= 0 ) {
	    		Object[][] matrix = this.data;

	    		List<Object> list = new ArrayList<Object>();
	    		for(int i=0; i<matrix.length; i++) {
	    			list.add( matrix[i][pos] );
	    		}
	
	    		Integer[]	indices	= new Integer[ list.size() ];
	    		Object[]	data	= new Object[ list.size() ];
	    		list.toArray( data );
	    		for(int i=0; i<indices.length; i++)	indices[i] = i;
	
	    		MsvUtilities.SortWithIndex( data, indices, direction );
	
	    		Object[][] newData = new Object[matrix.length][];
	    		for(int i=0; i<indices.length; i++) {
	    			newData[i] = matrix[indices[i]];
	    		}
	    		this.data = newData;

		    	this.matureRnaTablemodel.removeAll();
	    		for(int i=0; i<this.data.length; i++) {
	    			this.matureRnaTablemodel.addRow( this.data[i] );
	    		}
	    	}
		}catch(Exception e) {
			MsbEngine.logger.error( e );
		}
	}
	
	public void makeHeatmapTable() {
		for(int i=0; i<this.data.length; i++) {
			this.data[i][0] = this.clusterModel.getRowNames()[i];
			double sum = 0;
			for(int j=0; j<this.clusterModel.getOriginalData()[i].length; j++) {
				Double value = this.clusterModel.getOriginalData()[i][j];
				if( value.equals( Double.NaN ) )	this.data[i][j+2] = Double.NaN;
				else								this.data[i][j+2] = this.clusterModel.getOriginalData()[i][j];

				if( Double.valueOf( this.clusterModel.getOriginalData()[i][j] ).equals( Double.NaN ) == false )
					sum += this.clusterModel.getOriginalData()[i][j];
			}
			this.data[i][1] = sum;
		}
	}
	
	public void updateBoxplot() {
		Instances instances = ClusterModel.getInstancesByWeka( this.clusterModel.getCountModel() );
		this.groupedBoxPanel.setInstances( instances );
		this.groupedBoxPanel.setLogScaling( true );
		this.groupedBoxPanel.repaint();
	}
}
