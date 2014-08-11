package kobic.msb.swing.panel.newproject;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang.ArrayUtils;

import kobic.com.util.Utilities;
import kobic.msb.common.SwingConst;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.component.TableColumnAdjuster;
import kobic.msb.swing.frame.dialog.JCommonNewProjectDialog;
import kobic.msb.system.engine.MsbEngine;

public abstract class JMsbSampleTableCommonPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JCommonNewProjectDialog	owner;
	private JTable					tblSampleList;
	private TableColumnAdjuster		tca;
	private int[] 					allowedColumns;
	private int						nSamples;		// # of samples(if you add sample, this number are increased or you subract sample, this number are decreased)
	
	private JMsbSampleTableCommonPanel remote = JMsbSampleTableCommonPanel.this;

	public JMsbSampleTableCommonPanel(JCommonNewProjectDialog dialog) {
		this( dialog, null );
	}

	public JMsbSampleTableCommonPanel(JCommonNewProjectDialog dialog, int[] columns) {
		this.owner			= dialog;
		this.nSamples			= 0;
		
		this.allowedColumns = columns;
		Object[][] data		= null;
		DefaultTableModel tblModel = new DefaultTableModel( data, SwingConst.SAMPLE_TABLE_COLUMN );

		this.tblSampleList = new JTable( tblModel ){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
	        public boolean isCellEditable(int row, int col) {
				if( remote.allowedColumns == null )	return false;
				else {
					if( ArrayUtils.contains(remote.allowedColumns, col) )	return true;
					return false;
				}
	       }
		};
		this.tblSampleList.setPreferredScrollableViewportSize(new Dimension(500, 70));
		this.tblSampleList.setFillsViewportHeight(true);
		this.tblSampleList.setAutoCreateRowSorter(true);
		this.tblSampleList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		this.tca = new TableColumnAdjuster( this.tblSampleList );
	}

	public void refreshSampleTable( List<Group> groupList ) {
		DefaultTableModel newModel = new DefaultTableModel( null, SwingConst.SAMPLE_TABLE_COLUMN );
		
		// To re-order samples in the list
		Iterator<Group> iterGroup = groupList.iterator();
		while( iterGroup.hasNext() ) {
			Group grp = iterGroup.next();
			Iterator<Sample> iterSample = grp.getSample().iterator();
			while( iterSample.hasNext() ) {
				Sample smp = iterSample.next();

				String indexFile = smp.getIndexPath();
				if( Utilities.nulltoEmpty( smp.getIndexPath() ).equals("") )
					indexFile = "<Empty>";
				newModel.addRow( new Object[]{ new Integer(smp.getOrder()), grp.getGroupId(), smp.getName(), smp.getSamplePath(), indexFile } );
			}
		}

		this.tblSampleList.setModel( newModel );
		this.tblSampleList.getRowSorter().toggleSortOrder(0);
		this.tca.adjustColumns();
	}
	
	public JTable getTable() {
		return this.tblSampleList;
	}

	public void adustColumns() {
		this.tca.adjustColumns();
	}

	public JCommonNewProjectDialog getOwnerDialog() {
		return this.owner;
	}
	
	public int getNumberOfSample() {
		return this.nSamples;
	}
	
	public void	setNumberOfSample(int nSample) {
		this.nSamples = nSample;
	}
	
	public void incrementNoOfSample() {
		this.nSamples++;
	}
	public void decrementNoOfSamples() {
		this.nSamples--;
	}
	
	public abstract void setFocusProjectName();
}