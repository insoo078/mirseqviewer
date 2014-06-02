package kobic.msb.swing.panel.newproject.bak;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableRowSorter;


import kobic.msb.common.ImageConstant;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.swing.panel.newproject.CommonAbstractNewProjectPanel;
import kobic.msb.swing.renderer.NumberTableCellRenderer;
import kobic.msb.swing.thread.caller.JMsbNGSTestCaller;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class JMsbMatureChooseBakPanel extends CommonAbstractNewProjectPanel implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField 							txtMirna;
	private JTable								tblMirna;
	private JTable								tblChoosedMirnas;
	
	private LinkedHashMap<String, Object[]> 	defaultMiRnas;
	private LinkedHashMap<String, Object[]>		choosedMiRnas;

	private String[]							columns;

	private JMsbMatureChooseBakPanel remote = JMsbMatureChooseBakPanel.this;

	public JMsbMatureChooseBakPanel( JProjectDialog owner ) {
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
					int numOfChar = 8;
					if( keyword.startsWith( "mir-") || keyword.startsWith( "let-") )
						numOfChar = 4;

					if( keyword.length() > numOfChar ) {
						remote.revalidateTable( keyword );
					}
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
//				if( column == 0 )	return Boolean.class;
//				return String.class;
				Class returnValue;
	        	if ((column >= 0) && (column < getColumnCount())) {
	        		returnValue = getValueAt(0, column).getClass();
	        	} else {
	        		returnValue = Object.class;
	        	}
	        	return returnValue;
	        }

	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return column == 0;
	        }
		};
		this.tblMirna.setDefaultRenderer( Object.class, new NumberTableCellRenderer() );

		UpdatableTableModel tblModel2 = new UpdatableTableModel(data, this.columns );
		this.tblChoosedMirnas = new JTable( tblModel2 ) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
	        public Class<?> getColumnClass(int column) {
//				if( column == 0 )	return Boolean.class;
//				return String.class;
				Class returnValue;
	        	if ((column >= 0) && (column < getColumnCount())) {
	        		returnValue = getValueAt(0, column).getClass();
	        	} else {
	        		returnValue = Object.class;
	        	}
	        	return returnValue;
	        }

	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return column == 0;
	        }
		};
		this.tblChoosedMirnas.setDefaultRenderer( Object.class, new NumberTableCellRenderer() );

		this.tblMirna.setShowGrid( true );
		this.tblMirna.setRowSelectionAllowed(true);

		JScrollPane tblMirnaScrollPane = new JScrollPane( this.tblMirna );
		JScrollPane lstMirnaScrollPane = new JScrollPane( this.tblChoosedMirnas );
		
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
		
		JLabel lblSelectedMirnas = new JLabel("Selected miRNA(s)");
		
		JButton btnUp = new JButton("");
		btnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UpdatableTableModel model = (UpdatableTableModel) remote.tblChoosedMirnas.getModel();
				
				for(int i=0; i<model.getRowCount(); i++) {
					int row = i;
					
					if( ((Boolean)model.getValueAt(i, 0)) == true ) {
						String mirid		= (String) model.getValueAt(row, 1);
						Object[] val		= remote.choosedMiRnas.get(mirid).clone();

						remote.choosedMiRnas.remove( mirid );
						remote.defaultMiRnas.put( mirid, val );
					}
				}

				remote.revalidateChoosedMiRnasTable();
				remote.revalidateTable( remote.txtMirna.getText() );
			}
		});
		btnUp.setIcon( ImageConstant.upArrowIcon );
		
		JButton btnDown = new JButton("");
		btnDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UpdatableTableModel model = (UpdatableTableModel) remote.tblMirna.getModel();
				
				for(int i=0; i<model.getRowCount(); i++) {
					int row = i;

					if( ((Boolean)model.getValueAt(i, 0)) == true ) {
						String mirid		= (String) model.getValueAt(row, 1);
						Object[] val		= remote.defaultMiRnas.get(mirid).clone();
		
						remote.choosedMiRnas.put( mirid, val );
						remote.defaultMiRnas.remove( mirid );
					}
				}

				remote.revalidateChoosedMiRnasTable();
				remote.revalidateTable( remote.txtMirna.getText() );
			}
		});
		btnDown.setIcon( ImageConstant.downArrowIcon );
		
		JLabel lblSelectMirnaFor = new JLabel("Select miRNA(s) for display alignment view");

		GroupLayout gl_rootPanel = new GroupLayout(rootPanel);
		gl_rootPanel.setHorizontalGroup(
			gl_rootPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_rootPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_rootPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(tblMirnaScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
						.addComponent(lblSelectMirnaFor, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
						.addComponent(lstMirnaScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_rootPanel.createSequentialGroup()
							.addGroup(gl_rootPanel.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_rootPanel.createSequentialGroup()
									.addComponent(lblSelectedMirnas, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 72, Short.MAX_VALUE))
								.addGroup(gl_rootPanel.createSequentialGroup()
									.addComponent(btnUp)
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addComponent(btnDown)
							.addGap(182))
						.addGroup(gl_rootPanel.createSequentialGroup()
							.addComponent(lblListOfMirna)
							.addPreferredGap(ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
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
					.addGroup(gl_rootPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_rootPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(txtMirna, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblMirna))
						.addComponent(lblListOfMirna))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tblMirnaScrollPane, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_rootPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_rootPanel.createSequentialGroup()
							.addComponent(btnDown)
							.addGap(11)
							.addComponent(lblSelectedMirnas))
						.addComponent(btnUp))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lstMirnaScrollPane, GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
					.addContainerGap())
		);
		rootPanel.setLayout(gl_rootPanel);
		setLayout(groupLayout);
	}

	public void revalidateChoosedMiRnasTable() {
		UpdatableTableModel model = (UpdatableTableModel)this.tblChoosedMirnas.getModel();
		model.removeAll();		// model initialization

		Iterator<String> miridList = this.choosedMiRnas.keySet().iterator();
		while( miridList.hasNext() ) {
			String mirid = miridList.next();
			model.addRow( this.choosedMiRnas.get( mirid ) );
		}
		if( model.getRowCount() > 0 )  {
			TableRowSorter<UpdatableTableModel> sorter = new TableRowSorter<UpdatableTableModel>( model );
			sorter.setComparator( 2, JMsbMatureChooseBakPanel.tableComparator );
			
			this.tblChoosedMirnas.setRowSorter( sorter );
		}
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
				sorter.setComparator( 2, JMsbMatureChooseBakPanel.tableComparator );
				
				this.tblMirna.setRowSorter( sorter );
			}
		}catch(Exception e) {
//			e.printStackTrace();
			MsbEngine.logger.error(e);
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
			UpdatableTableModel newModel2 = new UpdatableTableModel( null, this.columns );
			this.tblMirna.setModel( newModel );
			this.tblChoosedMirnas.setModel( newModel2 );
		}catch(Exception e) {
			MsbEngine.logger.error(e);
		}
	}
	
	public void setTableModel(  List<Object[]> listModel ){
		try {
			this.txtMirna.setEnabled( true );
			this.defaultMiRnas.clear();
			
			for(Object[] objs:listModel) {
				String mirid = objs[1].toString();
				this.defaultMiRnas.put( mirid, objs );
			}
		}catch(Exception e) {
			MsbEngine.logger.error(e);
		}
		this.revalidateTable();
	}

	public void setListModel( List<Object[]> listModel ) {
		this.choosedMiRnas.clear();
		for( Object[] objs:listModel ) {
			String mirid = objs[1].toString();
			this.choosedMiRnas.put( mirid, objs );
		}

		this.revalidateChoosedMiRnasTable();
	}

	@Override
	public void updateCurrentState( ProjectMapItem projectMapItem ) {
		// All clear miRNA List //////////////////////////////////////////////////////////////////////////////////
		this.choosedMiRnas.clear();
		this.defaultMiRnas.clear();
		this.revalidateChoosedMiRnasTable();
		//////////////////////////////////////////////////////////////////////////////////////////////////////////

		try {
			JMsbNGSTestCaller worker = new JMsbNGSTestCaller( this.getOwnerDialog(), projectMapItem );
			worker.run();
		}catch(Exception e) {
			MsbEngine.logger.error( e );
		}
	}

	public HashMap<String, Object[]> getChoosedMiRna() {
		return this.choosedMiRnas;
	}

	public static Comparator<Object> tableComparator = new Comparator<Object>() {
	    public int compare( Object s1, Object s2 ) {
	    	if( s1 instanceof Number && s2 instanceof Number ) {
	    		Double a = (Double)s1;
	    		Double b = (Double)s2;
	    		return a.compareTo( b ); 
	    	}else {
	    		String[] strings1 = s1.toString().split("\\s");
		        String[] strings2 = s2.toString().split("\\s");
		        return strings1[strings1.length - 1]
		            .compareTo(strings2[strings2.length - 1]);
	    	}
	    }
	};

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}