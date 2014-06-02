package kobic.msb.swing.panel.profiling;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;

import org.apache.commons.math3.stat.StatUtils;

import weka.core.Attribute;
import weka.core.AttributeStats;

import kobic.com.util.Utilities;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.ClusterModel;
import kobic.msb.server.model.DescritiveStatisticsModel;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.system.catalog.ProjectMapItem;

import java.awt.Font;
import javax.swing.JCheckBox;

public class JMsbProfileDialogFileInfoPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable				tblSamples;
	private JTable				tblSelectedSampleStatistics;

//	private AttributeVisualizationPanel drawingPanel;
//	private JMsbBoxplotPanel drawingPanel;
	private JMsbGroupBoxplotPanel drawingPanel;
//	private JPanel				drawingPanel;
	
	private JLabel lblName;
	private JLabel lblType;
	private JLabel lblMissing;
	private JLabel lblDistinct;
	private JLabel lblUnique;
	
//	private JMsbBoxplotPanel	boxplotPanel;
	
	private ClusterModel 		clusterModel;

	private UpdatableTableModel tblStatisticsModel;
	private DescritiveStatisticsModel statModel;

	private final String[] tableColumns			= { "No.", "Group", "Sample" };
	private final String[] statisticsColumns	= { "Statistics", "Value" };
	private List<Group> sampleState;
	
	private ProjectMapItem projectItem;
	
	private JMsbProfileDialogFileInfoPanel remote = JMsbProfileDialogFileInfoPanel.this;

	public JMsbProfileDialogFileInfoPanel( ClusterModel clusterModel, ProjectMapItem projectItem ) {
		this.clusterModel	= clusterModel;
		this.projectItem	= projectItem;
		
		this.sampleState = this.projectItem.getProjectInfo().getSamples().getGroup();

//		this.drawingPanel = new AttributeVisualizationPanel();
//		this.drawingPanel = new JMsbBoxplotPanel();
		this.drawingPanel	= new JMsbGroupBoxplotPanel();
//		this.drawingPanel	= new JPanel();

		this.drawingPanel.setInstances( this.clusterModel.getInstances() );
		
		int index = 0;
		int size = clusterModel.getInstances().numAttributes();
		Object[][] data = new Object[size][3];
		Enumeration<Attribute> enumer = this.clusterModel.getInstances().enumerateAttributes();
		while( enumer.hasMoreElements() ) {
			Attribute attr = enumer.nextElement();
			
			String group = this.findGroupBySampleName(attr.name());

			data[index] = new Object[]{ (index+1), group, attr.name()};
			index++;
		}

		UpdatableTableModel tblModel = new UpdatableTableModel( data, this.tableColumns );

		this.tblSamples = new JTable( tblModel ) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
	        public Class<?> getColumnClass(int column) {
	            return getValueAt(0, column).getClass();
	        }

	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return column == 1;
	        }
		};
		
		/*** Over JDK 1.7 ***/
//		JComboBox<String> comboBox = new JComboBox<String>();
		/*** Under JDK 1.7 ***/
		JComboBox comboBox = new JComboBox();
		for(int i=0; i<this.sampleState.size(); i++)
			comboBox.addItem( this.sampleState.get(i).getGroupId() );
		comboBox.addItem("None");
		
		TableColumn column = this.tblSamples.getColumnModel().getColumn(1);
		column.setCellEditor(new DefaultCellEditor(comboBox) );

		
		Object[][] value = null;
		this.tblStatisticsModel = new UpdatableTableModel( value, this.statisticsColumns );

		this.tblSelectedSampleStatistics = new JTable(this.tblStatisticsModel);

		JScrollPane tblSamplesScrollPane	= new JScrollPane( this.tblSamples );
		JScrollPane tblStatisticsScrollPane	= new JScrollPane( this.tblSelectedSampleStatistics );

		this.lblName		= new JLabel("Name :");
		lblName.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		this.lblType		= new JLabel("Type :");
		lblType.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		this.lblMissing		= new JLabel("Missing :");
		lblMissing.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		this.lblDistinct	= new JLabel("Distinct :");
		lblDistinct.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		this.lblUnique		= new JLabel("Unique :");
		lblUnique.setFont(new Font("Lucida Grande", Font.PLAIN, 11));

		this.tblSelectedSampleStatistics = new JTable();
		
		this.tblSamples.setRowSelectionInterval(0, 0);
		this.changeAttribute( 0 );
		JButton btnRemove				= new JButton("Apply");

		this.tblSamples.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) ) {
					int attributeIndex = remote.tblSamples.getSelectedRow();
					
					remote.changeAttribute( attributeIndex );
				}
			}
		});
		
		JPanel fileInfoTitledPanel = new JPanel();
		fileInfoTitledPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "File info.", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION ) );

		JPanel sampleTitledPanel = new JPanel();
		sampleTitledPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Samples", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION ));

		GroupLayout gl_panel_2 = new GroupLayout(sampleTitledPanel);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(btnRemove, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
							.addGap(9))
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(tblSamplesScrollPane, GroupLayout.PREFERRED_SIZE, 282, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(tblSamplesScrollPane, GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnRemove)
					.addContainerGap())
		);
		sampleTitledPanel.setLayout(gl_panel_2);
		
		JPanel selectedSampleTitiledPanel = new JPanel();
		selectedSampleTitiledPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Selected sample", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION ));
		GroupLayout gl_panel_3 = new GroupLayout(selectedSampleTitiledPanel);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addComponent(tblStatisticsScrollPane, GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addComponent(lblMissing, GroupLayout.PREFERRED_SIZE, 122, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblDistinct, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblUnique, GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))
						.addGroup(gl_panel_3.createSequentialGroup()
							.addComponent(lblName, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblType, GroupLayout.PREFERRED_SIZE, 304, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblName)
						.addComponent(lblType))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMissing)
						.addComponent(lblDistinct)
						.addComponent(lblUnique))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tblStatisticsScrollPane, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
					.addContainerGap())
		);
		selectedSampleTitiledPanel.setLayout(gl_panel_3);
		
		JPanel boxplotTitledPanel = new JPanel();
		boxplotTitledPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Boxplot for samples", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION ));

		GroupLayout gl_panel = new GroupLayout( this );
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(fileInfoTitledPanel, GroupLayout.PREFERRED_SIZE, 303, GroupLayout.PREFERRED_SIZE)
						.addComponent(sampleTitledPanel, GroupLayout.PREFERRED_SIZE, 303, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(boxplotTitledPanel, GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
						.addComponent(selectedSampleTitiledPanel, GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(selectedSampleTitiledPanel, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(boxplotTitledPanel, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(fileInfoTitledPanel, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(sampleTitledPanel, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)))
					.addContainerGap())
		);
		
		JCheckBox chkLogScale = new JCheckBox("Log scaling");
		chkLogScale.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				if( e.getSource() instanceof JCheckBox ) {
					JCheckBox chkBox = (JCheckBox)e.getSource();
					remote.drawingPanel.setLogScaling( chkBox.isSelected() );
				}
			}
		});

		GroupLayout gl_boxplotTitledPanel = new GroupLayout(boxplotTitledPanel);
		gl_boxplotTitledPanel.setHorizontalGroup(
			gl_boxplotTitledPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_boxplotTitledPanel.createSequentialGroup()
					.addGroup(gl_boxplotTitledPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(chkLogScale)
						.addGroup(gl_boxplotTitledPanel.createSequentialGroup()
							.addGap(6)
							.addComponent(drawingPanel, GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_boxplotTitledPanel.setVerticalGroup(
			gl_boxplotTitledPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_boxplotTitledPanel.createSequentialGroup()
					.addComponent(chkLogScale)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(drawingPanel, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
					.addContainerGap())
		);
		boxplotTitledPanel.setLayout(gl_boxplotTitledPanel);
		
		JLabel lblNoOfSamples	= new JLabel( "No. of Samples : " + clusterModel.getColNames().length );
		JLabel lblNoOfMirnas	= new JLabel( "No. of miRNAs : " + clusterModel.getRowNames().length );
		JLabel lblNoOfGroups = new JLabel("No. of Groups : " + this.sampleState.size() );

		GroupLayout gl_panel_1 = new GroupLayout(fileInfoTitledPanel);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblNoOfGroups, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
								.addComponent(lblNoOfSamples, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addContainerGap(31, Short.MAX_VALUE))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblNoOfMirnas, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(31))))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
					.addContainerGap(8, Short.MAX_VALUE)
					.addComponent(lblNoOfGroups)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNoOfSamples)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNoOfMirnas)
					.addGap(12))
		);
		fileInfoTitledPanel.setLayout(gl_panel_1);
		this.setLayout(gl_panel);
	}
	
	public String findGroupBySampleName(String sampleId) {
		Iterator<Group> iter = this.sampleState.iterator();
		while( iter.hasNext() ) {
			Group grp = iter.next();
			
			Iterator<Sample> iterSample = grp.getSample().iterator();
			while( iterSample.hasNext() ) {
				Sample sample = iterSample.next();
				if( sample.getName().equals( sampleId ) )
					return grp.getGroupId();
			}
		}
		return null;
	}
	
	private void changeAttribute(int attributeIndex) {
		this.drawingPanel.setAttribute( attributeIndex );

		double[] columnData = Utilities.getVectorWithoutNan( this.clusterModel.getInstances().attributeToDoubleArray( attributeIndex ) );

		double mean		= StatUtils.mean( columnData );
		double std		= Math.sqrt( StatUtils.variance( columnData ) );
		double min		= StatUtils.min( columnData );
		double max		= StatUtils.max( columnData );
		double median	= StatUtils.percentile( columnData, 50 );

		this.statModel = new DescritiveStatisticsModel();

		UpdatableTableModel tableModel = this.tblStatisticsModel;
		tableModel.removeAll();

		tableModel.addRow( new Object[]{"Mean",		SwingUtilities.getRealNumberString( new Double(mean) )} );
		tableModel.addRow( new Object[]{"Std.",		SwingUtilities.getRealNumberString( new Double(std) )} );
		tableModel.addRow( new Object[]{"Median",	SwingUtilities.getRealNumberString( new Double(median) )} );
		tableModel.addRow( new Object[]{"Min",		SwingUtilities.getRealNumberString( new Double(min) )} );
		tableModel.addRow( new Object[]{"Max",		SwingUtilities.getRealNumberString( new Double(max) )} );

		AttributeStats stats = this.clusterModel.getInstances().attributeStats(attributeIndex);
		
		this.lblName.setText( "Name : " + this.clusterModel.getInstances().attribute( attributeIndex ).name() );
		this.lblType.setText( "Type : " + Attribute.typeToString( this.clusterModel.getInstances().attribute( attributeIndex ).type() ) );

		long percent = Math.round(100.0 * stats.missingCount / stats.totalCount);
		this.lblMissing.setText("Missing : " + stats.missingCount + " (" + percent + "%)");
	    percent = Math.round(100.0 * stats.uniqueCount / stats.totalCount);
	    this.lblUnique.setText("Unique : " + stats.uniqueCount + " (" + percent + "%)");
	    this.lblDistinct.setText("Distinct : " + stats.distinctCount);
	    
//	    this.boxplotPanel.setAttribute( attributeIndex );
	}
}