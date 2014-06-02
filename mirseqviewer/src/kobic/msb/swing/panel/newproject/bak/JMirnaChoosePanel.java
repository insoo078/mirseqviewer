package kobic.msb.swing.panel.newproject.bak;
//package kobic.msb.swing.panel.newproject;
//
//import javax.swing.BorderFactory;
//import javax.swing.JPanel;
//import javax.swing.GroupLayout;
//import javax.swing.GroupLayout.Alignment;
//import javax.swing.border.EtchedBorder;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.TableRowSorter;
//import javax.swing.JLabel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextField;
//import javax.swing.LayoutStyle.ComponentPlacement;
//import javax.swing.JTable;
//
//import kobic.msb.com.ImageConstant;
//import kobic.msb.com.util.SwingUtilities;
//import kobic.msb.swing.frame.dialog.JProjectDialog;
//import kobic.msb.system.catalog.ProjectMapItem;
//import kobic.msb.system.engine.MsbEngine;
//
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import javax.swing.JButton;
//import java.awt.event.ActionListener;
//import java.awt.event.ActionEvent;
//
//public class JMirnaChoosePanel extends JPanel {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	private JTextField 							txtMirna;
//	private JTable								tblMirna;
//	private JTable								tblChoosedMirnas;
//	private JProjectDialog						owner;
//	
//	private LinkedHashMap<String, Object[]> 	defaultMiRnas;
//	private LinkedHashMap<String, Object[]>		choosedMiRnas;
//	
//	private final String[] columns = { "check", "accession", "mirid", "location", "chromosome", "strand", "count" };
//
//	private JMirnaChoosePanel remote = JMirnaChoosePanel.this;
//
//	public JMirnaChoosePanel( JProjectDialog owner ) {
//		this.owner			= owner;
//		
//		this.defaultMiRnas	= new LinkedHashMap<String, Object[]>();
//		this.choosedMiRnas	= new LinkedHashMap<String, Object[]>();
//
//		JLabel lblMirna = new JLabel("miRNA");
//		
//		this.txtMirna = new JTextField();
//		this.txtMirna.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyTyped(KeyEvent e) {
//				SwingUtilities.setWaitCursorFor( remote );
//				String keyword = (remote.txtMirna.getText().toLowerCase() + e.getKeyChar()).trim();
//				
//				if( !keyword.equals("") ) {
//					int numOfChar = 8;
//					if( keyword.startsWith( "mir-") || keyword.startsWith( "let-") )
//						numOfChar = 4;
//
//					if( keyword.length() > numOfChar ) {
//						remote.revalidateTable( keyword );
//					}
//				}else {
//					remote.revalidateTable();
//				}
//				SwingUtilities.setDefaultCursorFor( remote );
//			}
//		});
//		this.txtMirna.setColumns(10);
//		this.txtMirna.setEnabled( false );
//		
//		Object[][] data = null;
//		DefaultTableModel tblModel = new DefaultTableModel(data, this.columns );
//
//		this.tblMirna = new JTable( tblModel ) {
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			@Override
//	        public Class<?> getColumnClass(int column) {
//				if( column == 0 )
//					return Boolean.class;
//	            return getValueAt(0, column).getClass();
//	        }
//
//	        @Override
//	        public boolean isCellEditable(int row, int column) {
//	            return column == 0;
//	        }
//		};
//
//		DefaultTableModel tblModel2 = new DefaultTableModel(data, this.columns );
//		this.tblChoosedMirnas = new JTable( tblModel2 ) {
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			@Override
//	        public Class<?> getColumnClass(int column) {
////				if( column == 0 )
////					return Boolean.class;
//	            return getValueAt(0, column).getClass();
//	        }
//
//	        @Override
//	        public boolean isCellEditable(int row, int column) {
//	            return column == 0;
//	        }
//		};
//
//		this.tblMirna.setShowGrid( true );
//		this.tblMirna.setRowSelectionAllowed(true);
//		
//		this.tblMirna.addMouseListener( new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				// TODO Auto-generated method stub
////				if( e.getClickCount() == 2 ) {
////					JTable target = (JTable)e.getSource();
////					int row = target.getSelectedRow();
////
//////					DefaultTableModel model = (DefaultTableModel) remote.tblMirna.getModel();
////
////					String accession	= (String) target.getValueAt(row, 1);
////					String mirid		= (String) target.getValueAt(row, 2);
////					String location		= (String) target.getValueAt(row, 3);
////					String chromosome	= (String) target.getValueAt(row, 4);
////					String strand		= (String) target.getValueAt(row, 5);
////					String count		= (String) target.getValueAt(row, 6);
////
////					remote.choosedMiRnas.put( mirid, new Object[]{ accession, mirid, location, chromosome, strand, count } );
////					remote.defaultMiRnas.remove( mirid );
////
////					remote.revalidateList();
////					remote.revalidateTable( remote.txtMirna.getText() );
////				}
//			}
//		});
//
//		JScrollPane tblMirnaScrollPane = new JScrollPane( this.tblMirna );
//		JScrollPane lstMirnaScrollPane = new JScrollPane( this.tblChoosedMirnas );
//		
//		JPanel rootPanel = new JPanel();
//		rootPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED ));
//		GroupLayout groupLayout = new GroupLayout(this);
//		groupLayout.setHorizontalGroup(
//			groupLayout.createParallelGroup(Alignment.LEADING)
//				.addGroup(groupLayout.createSequentialGroup()
//					.addContainerGap()
//					.addComponent(rootPanel, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
//					.addContainerGap())
//		);
//		groupLayout.setVerticalGroup(
//			groupLayout.createParallelGroup(Alignment.LEADING)
//				.addGroup(groupLayout.createSequentialGroup()
//					.addContainerGap()
//					.addComponent(rootPanel, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
//					.addContainerGap())
//		);
//		
//		JLabel lblListOfMirna = new JLabel("List of miRNA in BAM");
//		
//		JLabel lblSelectedMirnas = new JLabel("Selected miRNA(s)");
//		
//		JButton btnUp = new JButton("");
//		btnUp.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				DefaultTableModel model = (DefaultTableModel) remote.tblChoosedMirnas.getModel();
//				
//				for(int i=0; i<model.getRowCount(); i++) {
//					int row = i;
//					
//					if( ((Boolean)model.getValueAt(i, 0)) == true ) {
//						String accession	= (String) model.getValueAt(row, 1);
//						String mirid		= (String) model.getValueAt(row, 2);
//						String location		= (String) model.getValueAt(row, 3);
//						String chromosome	= (String) model.getValueAt(row, 4);
//						String strand		= (String) model.getValueAt(row, 5);
//						Long count			= (Long)model.getValueAt(row, 6);
//
//						remote.choosedMiRnas.remove( mirid );
//						remote.defaultMiRnas.put( mirid, new Object[]{ new Boolean(false), accession, mirid, location, chromosome, strand, count } );
//					}
//				}
//
//				remote.revalidateChoosedMiRnasTable();
//				remote.revalidateTable( remote.txtMirna.getText() );
//			}
//		});
//		btnUp.setIcon( ImageConstant.upArrowIcon );
//		
//		JButton btnDown = new JButton("");
//		btnDown.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				DefaultTableModel model = (DefaultTableModel) remote.tblMirna.getModel();
//				
//				for(int i=0; i<model.getRowCount(); i++) {
//					int row = i;
//
//					if( ((Boolean)model.getValueAt(i, 0)) == true ) {
//						String accession	= (String) model.getValueAt(row, 1);
//						String mirid		= (String) model.getValueAt(row, 2);
//						String location		= (String) model.getValueAt(row, 3);
//						String chromosome	= (String) model.getValueAt(row, 4);
//						String strand		= (String) model.getValueAt(row, 5);
//						Long count			= (Long)model.getValueAt(row, 6);
//		
//						remote.choosedMiRnas.put( mirid, new Object[]{ new Boolean(false), accession, mirid, location, chromosome, strand, count } );
//						remote.defaultMiRnas.remove( mirid );
//					}
//				}
//
//				remote.revalidateChoosedMiRnasTable();
//				remote.revalidateTable( remote.txtMirna.getText() );
//			}
//		});
//		btnDown.setIcon( ImageConstant.downArrowIcon );
//
//		GroupLayout gl_rootPanel = new GroupLayout(rootPanel);
//		gl_rootPanel.setHorizontalGroup(
//			gl_rootPanel.createParallelGroup(Alignment.TRAILING)
//				.addGroup(gl_rootPanel.createSequentialGroup()
//					.addContainerGap()
//					.addGroup(gl_rootPanel.createParallelGroup(Alignment.TRAILING)
//						.addComponent(tblMirnaScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
//						.addComponent(lstMirnaScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
//						.addComponent(lblListOfMirna, Alignment.LEADING)
//						.addGroup(gl_rootPanel.createSequentialGroup()
//							.addComponent(lblMirna)
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addComponent(txtMirna, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//						.addGroup(Alignment.LEADING, gl_rootPanel.createSequentialGroup()
//							.addGroup(gl_rootPanel.createParallelGroup(Alignment.TRAILING)
//								.addGroup(gl_rootPanel.createSequentialGroup()
//									.addComponent(lblSelectedMirnas, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
//									.addPreferredGap(ComponentPlacement.RELATED, 66, Short.MAX_VALUE))
//								.addGroup(gl_rootPanel.createSequentialGroup()
//									.addComponent(btnUp)
//									.addPreferredGap(ComponentPlacement.RELATED)))
//							.addComponent(btnDown)
//							.addGap(182)))
//					.addContainerGap())
//		);
//		gl_rootPanel.setVerticalGroup(
//			gl_rootPanel.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_rootPanel.createSequentialGroup()
//					.addContainerGap()
//					.addGroup(gl_rootPanel.createParallelGroup(Alignment.LEADING)
//						.addComponent(txtMirna, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//						.addGroup(gl_rootPanel.createSequentialGroup()
//							.addGap(6)
//							.addComponent(lblMirna)))
//					.addGap(5)
//					.addComponent(lblListOfMirna)
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addComponent(tblMirnaScrollPane, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addGroup(gl_rootPanel.createParallelGroup(Alignment.LEADING)
//						.addGroup(gl_rootPanel.createSequentialGroup()
//							.addComponent(btnDown)
//							.addGap(11)
//							.addComponent(lblSelectedMirnas))
//						.addComponent(btnUp))
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addComponent(lstMirnaScrollPane, GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
//					.addContainerGap())
//		);
//		rootPanel.setLayout(gl_rootPanel);
//		setLayout(groupLayout);
//	}
//
//	public void revalidateChoosedMiRnasTable() {
//		DefaultTableModel model = (DefaultTableModel)this.tblChoosedMirnas.getModel();
//		model.setRowCount(0);
//
//		Iterator<String> miridList = this.choosedMiRnas.keySet().iterator();
//		while( miridList.hasNext() ) {
//			String mirid = miridList.next();
//		
//			model.addRow( this.choosedMiRnas.get( mirid ) );
//		}
//		
//		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>( model );
//		sorter.setComparator( model.getColumnCount() - 1, JMirnaChoosePanel.tableComparator );
//		
//		this.tblChoosedMirnas.setRowSorter( sorter );
//	}
//	
//	private void revalidateTable(String keyword) {
//		DefaultTableModel model = (DefaultTableModel)this.tblMirna.getModel();
//		model.setRowCount( 0 );		// model initialization
//
//		Iterator<String> miridList = this.defaultMiRnas.keySet().iterator();
//		while( miridList.hasNext() ) {
//			String mirid = miridList.next();
//
//			if( keyword == null || keyword.isEmpty() )
//				model.addRow( this.defaultMiRnas.get( mirid ) );
//			else {
//				 if( mirid.contains( keyword ) ) {
//					 model.addRow( this.defaultMiRnas.get(mirid) );
//				 }
//			}
//		}
//		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>( model );
//		sorter.setComparator( model.getColumnCount() - 1, JMirnaChoosePanel.tableComparator );
//		
//		this.tblMirna.setRowSorter( sorter );
//	}
//
//	public void revalidateTable() {
//		this.revalidateTable( null );
//	}
//	
//	public void setTableModel(  List<Object[]> listModel ) {
//		this.txtMirna.setEnabled( true );
//		this.defaultMiRnas.clear();
//		
//		for(Object[] objs:listModel) {
//			String mirid = objs[2].toString();
//			this.defaultMiRnas.put( mirid, objs );
//		}
//
//		this.revalidateTable();
//	}
//
//	public void setListModel( List<Object[]> listModel ) {
//		this.choosedMiRnas.clear();
//		for( Object[] objs:listModel ) {
//			String mirid = objs[2].toString();
//			this.choosedMiRnas.put( mirid, objs );
//		}
//
//		this.revalidateChoosedMiRnasTable();
//	}
//
//	public void setProjectName( String projectName ) {
//		ProjectMapItem item = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );
//		
//		BamReaderToChooseMiRnasSwingWorker worker = new BamReaderToChooseMiRnasSwingWorker( this.owner, item );
//		worker.execute();
//	}
//	
//	public HashMap<String, Object[]> getChoosedMiRna() {
//		return this.choosedMiRnas;
//	}
//
//	public static Comparator<Object> tableComparator = new Comparator<Object>() {
//	    public int compare( Object s1, Object s2 ) {
//	    	if( s1 instanceof Number && s2 instanceof Number ) {
//	    		Double a = (Double)s1;
//	    		Double b = (Double)s2;
//	    		return a.compareTo( b ); 
//	    	}else {
//	    		String[] strings1 = s1.toString().split("\\s");
//		        String[] strings2 = s2.toString().split("\\s");
//		        return strings1[strings1.length - 1]
//		            .compareTo(strings2[strings2.length - 1]);
//	    	}
//	    }
//	};
//}
