package kobic.msb.swing.panel.summary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.SwingConst.Sorts;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.Model;
import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.server.model.jaxb.Msb.Project;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.comparator.MatureRnaReadCountComparator;
import kobic.msb.swing.component.TableColumnAdjuster;
import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.listener.projectdialog.HeaderCheckBoxHandler;
import kobic.msb.swing.renderer.JMsbChooseMiRnaHeaderRenderer;
import kobic.msb.swing.renderer.NumberTableCellRenderer;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import kobic.msb.system.project.ProjectManager;

import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JButton;

public class JMsbProjectReportPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String								projectName;
	private JTable 								tblSummary;
	private JTable 								tblRna;
	
	private JMsbProjectSummaryPanel				summaryPanel;
	
	private LinkedHashMap<String, Object[]> 	defaultMiRnas;
	private LinkedHashMap<String, Object[]>		choosedMiRnas;
	
	private String[]							columns;

	private TableColumnAdjuster					tca;
	private TableColumnAdjuster					tcaRna;
	
	private JMsbBrowserMainFrame				frame;
	
	private JMsbProjectReportPanel	remote = JMsbProjectReportPanel.this;

	/**
	 * Create the panel.
	 */
	public JMsbProjectReportPanel(JMsbBrowserMainFrame frame, String projectName) {
		this.frame			= frame;
		this.projectName	= projectName;

		this.defaultMiRnas	= new LinkedHashMap<String, Object[]>();
		this.choosedMiRnas	= new LinkedHashMap<String, Object[]>();

		try {
			ProjectMapItem projectItem = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );

			UpdatableTableModel tblModel	= this.getInitialize(projectItem);
			UpdatableTableModel tblRnaModel = new UpdatableTableModel( null, this.columns );
			
			JButton btnApply = new JButton("Apply");
			btnApply.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SwingUtilities.setWaitCursorFor( remote.frame );
					try {
						remote.choosedMiRnas.clear();

						UpdatableTableModel newModel = (UpdatableTableModel) remote.tblRna.getModel();
						for(int i=0; i<newModel.getRowCount(); i++) {
							Boolean bool = (Boolean)newModel.getValueAt(i, 0);
							String mirid = (String)newModel.getValueAt(i, 1);

							if( bool ){
								Object[] val = remote.getDefaultMiRna().get( mirid );
								remote.getDefaultMiRna().remove( mirid );
								remote.getChoosedMiRna().put(mirid, val);
							}
						}
						
						ProjectMapItem item = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( remote.projectName );
						item.initializeModelMap();

						Project project = item.getProjectInfo();

						Msb.Project.MiRnaList list = new Msb.Project.MiRnaList();

						HashMap<String, Object[]> map = remote.getChoosedMiRna();

						String defaultMirid = "";
						int idx = 0;
						for( java.util.Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
							String mature_id = iter.next();

							List<String> hairpindIds = null;
							if( mature_id.startsWith( JMsbSysConst.NOVEL_MICRO_RNA ) ) {
								hairpindIds = new ArrayList<String>();
								hairpindIds.add( mature_id );
							}else {
								hairpindIds = MsbEngine.getInstance().getMiRBaseMap().get(item.getMiRBAseVersion()).getHairpinIdFromMatureId( mature_id );
							}

							if( idx == 0 &&	hairpindIds.size() > 0 ) {
								defaultMirid = hairpindIds.get(0);
							}

							for(int i=0; i<hairpindIds.size(); i++) {
								String mirid = hairpindIds.get(i);

								if( !item.getModelMap().containsKey( mirid ) ) {
									Model model = item.getProjectModel( mirid );
									if( model != null ) {
										list.getMirnaList().add( model.getMirnaInfo() );
										
										item.addProjectModel( mirid, item.getTotalModelMap().get( mirid ) );
									}
								}
							}
							idx++;
						}
						project.setMirnaList( list );

						ProjectManager manager = MsbEngine.getInstance().getProjectManager();

						Msb msb = new Msb();
						msb.setProject( project );
						manager.writeXmlToProject( msb );

						manager.getProjectMap().writeToFile( manager.getSystemFileToGetProjectList() );

						remote.frame.getToolBar().refreshProjectListForToolBar();
						remote.frame.getTreePanel().refreshProjectTree();
						
						AbstractDockingWindowObj adw = remote.frame.getAbstractDockingWindowObj( remote.projectName );
						if( adw instanceof AlignmentDockingWindowObj ) {
							AlignmentDockingWindowObj alndw = (AlignmentDockingWindowObj)adw;
							String mirid = defaultMirid;
							
							alndw.setMirid( mirid );
						}
					}catch(Exception ex) {
						MsbEngine.logger.error( "error", ex );
					}finally{
						SwingUtilities.setDefaultCursorFor( remote.frame );
					}
				}
			});


			JLabel lblSamples				= new JLabel("Sample(s)");
			JLabel lblMirnas				= new JLabel("miRNA(s) abundance");

			this.summaryPanel				= new JMsbProjectSummaryPanel( projectItem );

			JPanel panel_1					= new JPanel();
			JPanel panel_2					= new JPanel();

			JSplitPane mainSplitPane		= new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
			JSplitPane splitPane			= new JSplitPane( JSplitPane.VERTICAL_SPLIT );
			
			mainSplitPane.setLeftComponent( this.summaryPanel );
			mainSplitPane.setRightComponent(splitPane);
			splitPane.setLeftComponent(panel_2);
			splitPane.setRightComponent(panel_1);
			
			this.tblSummary = new JTable( tblModel ){
		        private static final long serialVersionUID = 1L;
	
		        @Override
		        public Class<?> getColumnClass(int column) {
		        	switch (column) {
		                case 0:
		                	return String.class;
		                case 1:
		                    return String.class;
		                case 2:
		                    return String.class;
		                default:
		                    return String.class;
		        	}
		        }
		
		        @Override
		        public boolean isCellEditable(int row, int col) {
		        	return false;
		       }
		    };
		    
			this.tblRna = new JTable( tblRnaModel ) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
		        public Class<?> getColumnClass(int column) {
					if( column == 0 )	return Boolean.class;
					return String.class;
		        }

		        @Override
		        public boolean isCellEditable(int row, int column) {
		            return column == 0;
		        }
			};
		    
			JScrollPane summaryScrollPane	= new JScrollPane( this.tblSummary );
			JScrollPane rnaScrollPane	 	= new JScrollPane( this.tblRna );
		    
			this.tblSummary.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
			this.tblRna.setDefaultRenderer( Object.class, new NumberTableCellRenderer() );
			this.tblRna.setFillsViewportHeight(true);
			this.tblRna.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
			this.tblRna.setShowGrid( true );
			this.tblRna.setRowSelectionAllowed(true);
			this.tblRna.setAutoCreateRowSorter(true);

			this.tca = new TableColumnAdjuster(this.tblSummary);
			this.tcaRna = new TableColumnAdjuster(this.tblRna);

			GroupLayout gl_panel = new GroupLayout(panel_2);
			gl_panel.setHorizontalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
							.addComponent(summaryScrollPane, GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
							.addComponent(lblSamples))
						.addContainerGap())
			);
			gl_panel.setVerticalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblSamples)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(summaryScrollPane, GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
						.addContainerGap())
			);
			panel_2.setLayout(gl_panel);

			GroupLayout gl_panel_1 = new GroupLayout(panel_1);
			gl_panel_1.setHorizontalGroup(
				gl_panel_1.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel_1.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
							.addComponent(rnaScrollPane, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
							.addComponent(lblMirnas)
							.addComponent(btnApply, Alignment.TRAILING))
						.addContainerGap())
			);
			gl_panel_1.setVerticalGroup(
				gl_panel_1.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel_1.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblMirnas)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(rnaScrollPane, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnApply)
						.addContainerGap())
			);
			panel_1.setLayout(gl_panel_1);
			
			GroupLayout groupLayout = new GroupLayout(this);
			groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(mainSplitPane, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
						.addContainerGap())
			);
			groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(mainSplitPane, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
						.addContainerGap())
			);

			this.tca.adjustColumns();
			
			this.setTableHeader( projectItem.getMiRnaTableHeader() );
			this.setTableModel( projectItem.getReadedAllObjList() );
			
			mainSplitPane.setDividerLocation( 0.6f );
			splitPane.setDividerLocation( 0.5f );

			this.setLayout(groupLayout);
		}catch(Exception e) {
			MsbEngine.logger.error("Error : ", e);
		}
	}
	
	public UpdatableTableModel getInitialize( ProjectMapItem projectMapItem ) throws Exception {
		UpdatableTableModel tblModel = new UpdatableTableModel(null, new String[]{"group", "sample", "file", "size"} );

		for(int i=0; i<projectMapItem.getProjectInfo().getSamples().getGroup().size(); i++){
			for(int j=0;j<projectMapItem.getProjectInfo().getSamples().getGroup().get(i).getSample().size(); j++) {
				Object[] data = new Object[4];
				String path = projectMapItem.getProjectInfo().getSamples().getGroup().get(i).getSample().get(j).getSamplePath();
				data[0] = projectMapItem.getProjectInfo().getSamples().getGroup().get(i).getGroupId();
				data[1] = projectMapItem.getProjectInfo().getSamples().getGroup().get(i).getSample().get(j).getName();
				data[2] = path;

				DecimalFormat format = new DecimalFormat(".##");

				float byteF =  Utilities.getFileSize( path );
				float kilobyteF = byteF / 1024;
				float megabyteF = kilobyteF / 1024;
				float gigabyteF = megabyteF / 1024;

				if( gigabyteF >= 1 )							data[3] = format.format( gigabyteF ) + "GB";
				else if( megabyteF >= 1 && megabyteF < 1024 )	data[3] = format.format( megabyteF ) + "MB";
				else if( kilobyteF >= 1 && kilobyteF < 1024 )	data[3] = format.format( kilobyteF ) + "KB";
				else if( byteF >= 1 && byteF < 1024 )			data[3] = format.format( byteF ) + " byte";

				tblModel.addRow( data );
			}
		}

		return tblModel;
	}
	
	public void setTableHeader( Object[] header ) throws Exception{
		try {
			this.columns = new String[ header.length ];
			for(int i=0; i<header.length; i++)
				this.columns[i] = header[i].toString();

			UpdatableTableModel newModel = new UpdatableTableModel( null, this.columns );
			
			this.tblRna.setModel( newModel );
		}catch(Exception e) {
			MsbEngine.logger.error("error : ", e);
		}
	}
	
	public void setTableModel( List<Object[]> listModel ){
		try {
			this.tblRna.setEnabled( true );
			this.defaultMiRnas.clear();
			
			for(Object[] objs:listModel) {
				String mirid = objs[1].toString();
				this.defaultMiRnas.put( mirid, objs );
			}
		}catch(Exception e) {
			MsbEngine.logger.error("error : ", e);
		}
		this.revalidateTable();
	}
	
	public HashMap<String, Object[]> getDefaultMiRna() {
		return this.defaultMiRnas;
	}
	
	public HashMap<String, Object[]> getChoosedMiRna() {
		return this.choosedMiRnas;
	}
	
	private void revalidateTable() {
		try {
			UpdatableTableModel model = (UpdatableTableModel)this.tblRna.getModel();
			model.removeAll();		// model initialization
	
			Iterator<String> miridList = this.defaultMiRnas.keySet().iterator();
			while( miridList.hasNext() ) {
				String mirid = miridList.next();
	
				model.addRow( this.defaultMiRnas.get( mirid ) );
			}

			if( model.getRowCount() > 0 ) {
				TableRowSorter<UpdatableTableModel> sorter = new TableRowSorter<UpdatableTableModel>( model );
				for(int i=0; i<model.getColumnCount(); i++) {
					sorter.setComparator(i, new MatureRnaReadCountComparator(Sorts.LARGEST_TO_SMALLEST) );
				}
				this.tblRna.setRowSorter( sorter );
			}
			this.tblRna.getRowSorter().toggleSortOrder(3);

			int modelColmunIndex = 0;
	        TableCellRenderer renderer = new JMsbChooseMiRnaHeaderRenderer( this.tblRna.getTableHeader(), modelColmunIndex);
	        
	        this.tblRna.getColumnModel().getColumn(modelColmunIndex).setHeaderRenderer(renderer);

	        model.addTableModelListener( new HeaderCheckBoxHandler(this.tblRna, modelColmunIndex) );
			
			this.tcaRna.adjustColumns();
		}catch(Exception e) {
			MsbEngine.logger.error("error : ", e);
		}
	}
}
