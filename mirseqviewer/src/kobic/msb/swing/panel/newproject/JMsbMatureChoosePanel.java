package kobic.msb.swing.panel.newproject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import kobic.msb.common.SwingConst.Sorts;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.swing.comparator.MatureRnaReadCountComparator;
import kobic.msb.swing.component.TableColumnAdjuster;
import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.swing.listener.projectdialog.HeaderCheckBoxHandler;
import kobic.msb.swing.renderer.JMsbChooseMiRnaHeaderRenderer;
import kobic.msb.swing.renderer.NumberTableCellRenderer;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import javax.swing.JRadioButton;

public class JMsbMatureChoosePanel extends CommonAbstractNewProjectPanel implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField 							txtMirna;
	private JTable								tblMirna;
	
	private LinkedHashMap<String, Object[]> 	defaultMiRnas;
	private LinkedHashMap<String, Object[]>		choosedMiRnas;

	private String[]							columns;
	private TableColumnAdjuster					tca;

	private JMsbMatureChoosePanel				remote = JMsbMatureChoosePanel.this;

	public JMsbMatureChoosePanel( JProjectDialog owner ) {
		super( owner );

		this.columns		= null;

		this.defaultMiRnas	= new LinkedHashMap<String, Object[]>();
		this.choosedMiRnas	= new LinkedHashMap<String, Object[]>();

		JLabel lblMirna = new JLabel("Search");
		
		this.txtMirna = new JTextField();
		this.txtMirna.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				SwingUtilities.setWaitCursorFor( remote );
				String keyword = (remote.txtMirna.getText().toLowerCase() + e.getKeyChar()).trim();
				
				if( !keyword.equals("") ) {
					int numOfChar = 1;
//					int numOfChar = 8;
//					if( keyword.startsWith( "mir-") || keyword.startsWith( "let-") )
//						numOfChar = 4;

					if( keyword.length() > numOfChar )	remote.revalidateTable( keyword );
					else								remote.revalidateTable();
				}else {
					remote.revalidateTable();
				}
				SwingUtilities.setDefaultCursorFor( remote );
			}
		});
		this.txtMirna.setColumns(10);
		this.txtMirna.setEnabled( false );
		
		Object[][] data = null;
		UpdatableTableModel tblModel = new UpdatableTableModel(data, this.columns );

		this.tblMirna = new JTable( tblModel ) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
	        public Class<?> getColumnClass(int column) {
				if( column == 0 )	return Boolean.class;
				return String.class;
//				Class returnValue;
//	        	if ((column >= 0) && (column < getColumnCount())) {
//	        		returnValue = getValueAt(0, column).getClass();
//	        	} else {
//	        		returnValue = Object.class;
//	        	}
//	        	return returnValue;
	        }

	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return column == 0;
	        }
		};
		this.tblMirna.setDefaultRenderer( Object.class, new NumberTableCellRenderer() );
		this.tblMirna.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		this.tblMirna.setShowGrid( true );
		this.tblMirna.setRowSelectionAllowed(true);
		this.tblMirna.setAutoCreateRowSorter(true);
		this.tca = new TableColumnAdjuster(this.tblMirna);

		JScrollPane tblMirnaScrollPane = new JScrollPane( this.tblMirna );
		
		JPanel rootPanel = new JPanel();
		rootPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED ));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(rootPanel, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(rootPanel, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JLabel lblListOfMirna = new JLabel("Number of unique reads");
		
		JLabel lblSelectMirnaFor = new JLabel("Select miRNA(s) for display alignment view");
		
		JRadioButton rdbtnTop10 = new JRadioButton("Top 10");
		
		JRadioButton rdbtnTop50 = new JRadioButton("Top 50");
		
		JRadioButton rdbtnCustom = new JRadioButton("Custom");
		rdbtnCustom.setSelected(true);
		
		ButtonGroup group = new ButtonGroup();
		group.add( rdbtnCustom );
		group.add( rdbtnTop10 );
		group.add( rdbtnTop50 );
		
		rdbtnCustom.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int rowCount = remote.tblMirna.getRowCount();

				for(int i=0; i<50&&i<rowCount; i++) {
					remote.tblMirna.setValueAt(false, i, 0);
				}
			}
		});

		rdbtnTop50.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int rowCount = remote.tblMirna.getRowCount();

				for(int i=0; i<rowCount; i++) {
					if( i < 50 )	remote.tblMirna.setValueAt(true, i, 0);
					else			remote.tblMirna.setValueAt(false, i, 0);
				}
			}
		});
		
		rdbtnTop10.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int rowCount = remote.tblMirna.getRowCount();

				for(int i=0; i<rowCount; i++) {
					if( i < 10 )	remote.tblMirna.setValueAt(true, i, 0);
					else			remote.tblMirna.setValueAt(false, i, 0);
				}
			}
		});

		GroupLayout gl_rootPanel = new GroupLayout(rootPanel);
		gl_rootPanel.setHorizontalGroup(
			gl_rootPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_rootPanel.createSequentialGroup()
					.addGroup(gl_rootPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_rootPanel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_rootPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(tblMirnaScrollPane, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
								.addComponent(lblSelectMirnaFor, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
								.addGroup(gl_rootPanel.createSequentialGroup()
									.addComponent(lblListOfMirna)
									.addPreferredGap(ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
									.addComponent(rdbtnCustom)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rdbtnTop10)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rdbtnTop50))))
						.addGroup(gl_rootPanel.createSequentialGroup()
							.addComponent(lblMirna)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtMirna, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_rootPanel.setVerticalGroup(
			gl_rootPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_rootPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblSelectMirnaFor)
					.addGap(11)
					.addGroup(gl_rootPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtMirna, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMirna))
					.addGap(6)
					.addGroup(gl_rootPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblListOfMirna)
						.addComponent(rdbtnTop50)
						.addComponent(rdbtnCustom)
						.addComponent(rdbtnTop10))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tblMirnaScrollPane, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
					.addContainerGap())
		);
		rootPanel.setLayout(gl_rootPanel);
		setLayout(groupLayout);
	}

	private void revalidateTable( String keyword ) {
		try {
			UpdatableTableModel model = (UpdatableTableModel)this.tblMirna.getModel();
			model.removeAll();		// model initialization
	
			Iterator<String> miridList = this.defaultMiRnas.keySet().iterator();
			while( miridList.hasNext() ) {
				String mirid = miridList.next();
	
				if( keyword == null || keyword.isEmpty() )
					model.addRow( this.defaultMiRnas.get( mirid ) );
				else {
					 if( mirid.toLowerCase().contains( keyword ) ) {
						 model.addRow( this.defaultMiRnas.get(mirid) );
					 }
				}
			}

			if( model.getRowCount() > 0 ) {
				TableRowSorter<UpdatableTableModel> sorter = new TableRowSorter<UpdatableTableModel>( model );
//				sorter.setComparator( 2, JMsbMatureChoosePanel.tableComparator );
				for(int i=0; i<model.getColumnCount(); i++) {
//					sorter.setComparator( i, JMsbMatureChoosePanel.tableComparator );
					sorter.setComparator(i, new MatureRnaReadCountComparator( Sorts.LARGEST_TO_SMALLEST ) );
				}
				
				this.tblMirna.setRowSorter( sorter );
			}
			this.tblMirna.getRowSorter().toggleSortOrder(3);
			
			int modelColmunIndex = 0;
	        TableCellRenderer renderer = new JMsbChooseMiRnaHeaderRenderer( this.tblMirna.getTableHeader(), modelColmunIndex);
	        
	        this.tblMirna.getColumnModel().getColumn(modelColmunIndex).setHeaderRenderer(renderer);

	        model.addTableModelListener( new HeaderCheckBoxHandler(this.tblMirna, modelColmunIndex) );
			
			this.tca.adjustColumns();
		}catch(Exception e) {
			MsbEngine.logger.error("error : ", e);
		}
	}

	public void revalidateTable() {
		this.revalidateTable( null );
	}
	
	public void setTableHeader( Object[] header ) throws Exception{
		try {
			this.columns = new String[ header.length ];
			for(int i=0; i<header.length; i++)
				this.columns[i] = header[i].toString();

			UpdatableTableModel newModel = new UpdatableTableModel( null, this.columns );
			
			this.tblMirna.setModel( newModel );
		}catch(Exception e) {
			MsbEngine.logger.error("error : ", e);
		}
	}
	
	public void setTableModel( List<Object[]> listModel ){
		try {
			this.txtMirna.setEnabled( true );
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

	@Override
	public void updateCurrentState( ProjectMapItem projectMapItem ) {
		// All clear miRNA List //////////////////////////////////////////////////////////////////////////////////
		MsbEngine.logger.debug("Removing mirna ....");
		if( this.choosedMiRnas.size() > 0 ) this.choosedMiRnas.clear();
		if( this.defaultMiRnas.size() > 0 ) this.defaultMiRnas.clear();
		MsbEngine.logger.debug("Removed mirna ....");
		//////////////////////////////////////////////////////////////////////////////////////////////////////////

		try {
			this.getOwnerDialog().getThreadManager().goLoadingBamFilesToModel( projectMapItem );
//			JMsbNGSTestCaller<Void> worker = new JMsbNGSTestCaller<Void>( this.getOwnerDialog(), projectMapItem );
//			worker.run();
		}catch(Exception e) {
			MsbEngine.logger.error("error : ",  e );
		}
	}

	public HashMap<String, Object[]> getChoosedMiRna() {
		return this.choosedMiRnas;
	}

	public HashMap<String, Object[]> getDefaultMiRna() {
		return this.defaultMiRnas;
	}
	
	public JTable getMirnaTable() {
		return this.tblMirna;
	}

//	public static Comparator<Object> tableComparator = new Comparator<Object>() {
//	    public int compare( Object s1, Object s2 ) {
//	    	if( s1 instanceof Number && s2 instanceof Number ) {
//	    		Double a = (Double)s1;
//	    		Double b = (Double)s2;
//	    		return a.compareTo( b ); 
//	    	}else {
//	    		String[] strings1 = s1.toString().split("\\s");
//		        String[] strings2 = s2.toString().split("\\s");
//		        return strings1[strings1.length - 1].compareTo(strings2[strings2.length - 1]);
//	    	}
//	    }
//	};

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
	}
}