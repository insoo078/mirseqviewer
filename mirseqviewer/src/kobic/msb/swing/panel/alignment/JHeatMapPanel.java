package kobic.msb.swing.panel.alignment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.SwingConst.Sorts;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.MSBReadCountTableColumnStructureModel;
import kobic.msb.server.model.Model;
import kobic.msb.server.model.MsbRCTColumnModel;
import kobic.msb.server.model.MsbSortModel;
import kobic.msb.server.obj.ReadObject;
import kobic.msb.server.obj.GeneralReadObject;
import kobic.msb.swing.renderer.HeatMapTableCellColorRenderer;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.component.ColumnGroup;
import kobic.msb.swing.component.GroupableTableHeader;
import kobic.msb.swing.component.JMsbReadCountTableToolbar;
import kobic.msb.swing.component.JMsbReadEditingPopupMenu;
import kobic.msb.swing.component.SortableTableModel;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;

public class JHeatMapPanel extends JPanel implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Model							model;
	private AlignmentDockingWindowObj		dockWindow;
	private String							projectName;
	private JTable							heatMapTable;
	private JScrollPane 					scrollPane;

	private int								pressedReadNo;
	
	private List<Integer>					groupSumIndexes;
	private List<Integer>					sampleIndexes;
	private int								totalSumIndex;
	
	private final JHeatMapPanel				remote = JHeatMapPanel.this;
	
	String[]								columnNames			= null;
	Object[][]								data				= null;

	public JHeatMapPanel( AbstractDockingWindowObj dockWindow, String projectName, String mirid ) {
//		this.model				= MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName ).getProjectModel( mirid );
		
		this.projectName		= projectName;
		
		this.dockWindow			= (AlignmentDockingWindowObj)dockWindow;
		this.model				= this.dockWindow.getCurrentModel();
		
//	    if( this.model.getMsbSortModel() == null ) {
//	    	MsbSortModel model = new MsbSortModel();
//	    	model.addSortModel( 0, "Total_sum", Sorts.LARGEST_TO_SMALLEST.getValue() );
//	    	try {
//				this.model.sortBySortModel( model );
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				MsbEngine.logger.error("Error", e);
//			}
//	    }
		
		this.groupSumIndexes	= new ArrayList<Integer>();
		this.sampleIndexes		= new ArrayList<Integer>();
		
	    this.heatMapTable = new JTable() {
	    	/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected JTableHeader createDefaultTableHeader() {
	    		return new GroupableTableHeader( this.columnModel );
	    	}

			@Override
            public boolean isCellEditable ( int row, int column ) {
                return false;
            }
	    };

		this.heatMapTable.setShowGrid( true );
		this.heatMapTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		this.heatMapTable.setRowHeight( 15 );
		this.heatMapTable.setDragEnabled(false);
		this.heatMapTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
	
		this.heatMapTable.setFillsViewportHeight(true);
		
		this.heatMapTable.addMouseMotionListener( new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if( remote.dockWindow.getIsMousePositionFixed() == false ) {
					int row = remote.heatMapTable.rowAtPoint(e.getPoint());  
					if (row > -1)  {  
				         // easiest way:  
						remote.heatMapTable.clearSelection();
						remote.heatMapTable.setRowSelectionInterval(row, row);
						remote.dockWindow.getAlignmentPane().setSelectedSequenceForHilighting( row );
					}
				}
	        }
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if( remote.dockWindow.getIsMousePositionFixed() == false ) {
					int targetReadPos = remote.heatMapTable.getSelectedRow();

					remote.dockWindow.getAlignmentPane().reorderSequence( remote.pressedReadNo, targetReadPos );
					remote.reorderTable();

					int row = remote.heatMapTable.rowAtPoint(e.getPoint()); 
					remote.heatMapTable.clearSelection();
					remote.heatMapTable.setRowSelectionInterval(row, row);
					remote.dockWindow.getAlignmentPane().setSelectedSequenceForHilighting( row );

					remote.pressedReadNo = targetReadPos;
					
					remote.dockWindow.getAlignmentPane().repaint();
				}else {
					remote.dockWindow.setIsMousePositionFixed( false );
				}
			}
		});
		
		this.heatMapTable.addMouseListener( new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if( remote.dockWindow.getIsMousePositionFixed() == false ) {
					remote.pressedReadNo = remote.heatMapTable.getSelectedRow();
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isRightMouseButton(e) ) {
					int currentReadPos = remote.heatMapTable.getSelectedRow();
					if( currentReadPos >=0 && currentReadPos < remote.model.getReadList().size() ) {
						ReadObject tmpRead = remote.model.getRead( currentReadPos ).getReadObject();
						if( tmpRead instanceof GeneralReadObject ) {
							GeneralReadObject gro = (GeneralReadObject)tmpRead;
							
							int currentReadField = (int) gro.getRecordElements().get(0).getStartPosition();
							
							final JMsbReadEditingPopupMenu treePopup = new JMsbReadEditingPopupMenu( remote.dockWindow, currentReadPos, currentReadField );
		
							remote.dockWindow.setIsMousePositionFixed( true );
							remote.dockWindow.getSsPanel().setMouseClicked( true );
		
							treePopup.show( remote, e.getX(), e.getY() );
						}
					}
				}
			}
		});

		this.scrollPane = new JScrollPane( this.heatMapTable );

		this.setLayout( new BorderLayout() );

		this.add( this.scrollPane, BorderLayout.CENTER );
		this.add( new JMsbReadCountTableToolbar(this.dockWindow, this.projectName, mirid), BorderLayout.PAGE_START );

		this.initializePanel( this.model.getExpressionProfile( this.model.isNormalized() ) );
		
	    this.heatMapTable.getTableHeader().addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseClicked(MouseEvent mouseEvent) {
	        	int selectedColumnIndex = remote.heatMapTable.convertColumnIndexToModel( remote.heatMapTable.columnAtPoint(mouseEvent.getPoint()) );

	        	remote.dockWindow.getCurrentModel().sortReadCountAt( selectedColumnIndex );

	        	remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
	        };
	    });

		this.validate();
	}

	private void initializePanel( double[][] mat ) {
		this.totalSumIndex = -1;
		this.groupSumIndexes.removeAll( this.groupSumIndexes );
		this.sampleIndexes.removeAll( this.sampleIndexes );
		
		MSBReadCountTableColumnStructureModel currentRCTColumnStructureModel = remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel();

		String[] text = new String[currentRCTColumnStructureModel.getHeatMapHeader().size()];

		List<String> srcList = currentRCTColumnStructureModel.getHeatMapHeader();

		srcList.toArray( text );
		Map<String, MsbRCTColumnModel> columnStructure = currentRCTColumnStructureModel.getHeatMapColumnStructure();

		if( mat == null )	this.data = null;
		else {
			this.data = new Object[ mat.length ][ mat[0].length ];
			for(int i=0; i<mat.length; i++) {
				for(int j=0; j<mat[0].length; j++) {
					if( Double.isNaN( mat[i][j] ) )	this.data[i][j] = "NA";
					else{					
//						String value = NumberTableCellRenderer.numberFormat.format( mat[i][j] );
						String value = SwingUtilities.getNumberWithFractionDigit( mat[i][j], JMsbSysConst.FRACTION_LENGTH );
						this.data[i][j] = value;
					}
				}
			}
		}

//		DefaultTableModel dm = new DefaultTableModel();
		SortableTableModel dm = new SortableTableModel( this.model );
	    dm.setDataVector( this.data, text );

	    this.heatMapTable.setModel( dm );

	    TableColumnModel cm = this.heatMapTable.getColumnModel();
		GroupableTableHeader header = (GroupableTableHeader)this.heatMapTable.getTableHeader();

        ProjectConfiguration config		= MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.dockWindow.getProjectName() ).getProjectConfiguration();

		String groupLabel = "Raw count";
		if( this.model.isNormalized() )		groupLabel = "[" + config.getNormalizationMethod() + "] Normalized Read Count";
		else								groupLabel = "Raw count";
		
		//// Create group header /////////////////////////////////////////////////////////////////
		ColumnGroup titleGrp = new ColumnGroup( groupLabel );

		int i = 0;
		for( Iterator<String> iter = columnStructure.keySet().iterator(); iter.hasNext(); ) {
			String group = iter.next();
			
			MsbRCTColumnModel column = columnStructure.get( group );

			if( column.isGroup() ) {
				ColumnGroup g_name = new ColumnGroup( column.getColumnId() );

				List<String> columns = columnStructure.get( group ).getChildColumnIdList();

				// 1 : JTree tree structure loop
				for(int idx=0; idx<cm.getColumnCount(); idx++) {
				// 2 : Column structure loop
 					for( int j=0; j<columns.size(); j++ ) {
 						MsbRCTColumnModel subColumnModel = currentRCTColumnStructureModel.getColumnModel( columns.get(j) );
						if( cm.getColumn(idx).getHeaderValue().equals( columns.get(j) ) ) {
							if( subColumnModel.getColumnType().equals( JMsbSysConst.TOTAL_SUM_HEADER_PREFIX ) ) {
								this.totalSumIndex = i;
							}else if( subColumnModel.getColumnType().equals( JMsbSysConst.GROUP_SUM_HEADER_PREFIX ) ){
								this.groupSumIndexes.add( i );
							}else {
								this.sampleIndexes.add( i );
							}
							g_name.add( cm.getColumn( idx ) );
							i++;
						}
					}
				}
				titleGrp.add( g_name );
			}else {
				group = column.getColumnId();
				
				if( group.equals( JMsbSysConst.TOTAL_SUM_HEADER ) )	this.totalSumIndex = i;
				else												this.groupSumIndexes.add( i );

				titleGrp.add( cm.getColumn( SwingUtilities.getIndexAtColumnModel(cm, group) ) );
				i++;
			}
		}
		header.addColumnGroup( titleGrp );
		//// Create group header /////////////////////////////////////////////////////////////////
		
	    this.initColumnSizes( this.heatMapTable );
	}
	
	public void reorderTable() {
//		if( remote.btnRaw.isSelected() ) {
//			this.initializePanel( this.model.getNormalizedHeatMapDataObject(this.config.getNormalizationMethod(), this.config.getMissingValue()) );
//		}else {
//			this.initializePanel( this.model.getHeatMapDataObject() );
//		}
		this.initializePanel( this.model.getExpressionProfile( this.model.isNormalized() ) );
		this.heatMapTable.revalidate();
	}

	/*****************************************************************************************************
	 * To fit cell width
	 * 
	 * @param table
	 */
	private void initColumnSizes( JTable table ) {
		DefaultTableModel model = (DefaultTableModel)table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
 
        for (int i = 0; i < table.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
 
            comp = headerRenderer.getTableCellRendererComponent( null, column.getHeaderValue(), false, false, 0, 0 );
            headerWidth = comp.getPreferredSize().width;
 
            comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(table, model.getColumnClass(i).getName(), false, false, 0, i);

            table.getDefaultRenderer(model.getColumnClass(i));
            cellWidth = comp.getPreferredSize().width>100?comp.getPreferredSize().width:100;

            column.setWidth( Math.max( headerWidth, cellWidth ) );
            column.setPreferredWidth( Math.max(headerWidth, cellWidth) );
        }

        double maxValue = this.dockWindow.getCurrentModel().maxCountValue();
        double minValue = this.dockWindow.getCurrentModel().minCountValue();
        
        ProjectConfiguration config		= MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.dockWindow.getProjectName() ).getProjectConfiguration();

        table.setDefaultRenderer( Object.class, new HeatMapTableCellColorRenderer( minValue, maxValue, this.totalSumIndex, this.groupSumIndexes, config ) );
    }

	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}

	public void hilightAt( int row ) {
		if( this.heatMapTable.getRowCount() > row ) {
			this.heatMapTable.setRowSelectionInterval(row, row);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if( arg instanceof Model ) {
			// TODO Auto-generated method stub
			Model model = (Model)arg;
	
			this.model = model;
	
			this.initializePanel( this.model.getExpressionProfile( this.model.isNormalized() ) );
		}
	}
}
