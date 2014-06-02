package kobic.msb.swing.frame.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;


import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.server.model.MSBReadCountTableColumnStructureModel;
import kobic.msb.server.model.MatrixObj;
import kobic.msb.server.model.MsbRCTColumnModel;
import kobic.msb.server.model.ReadWithMatrix;
import kobic.msb.server.obj.ComboBoxItem;
import kobic.msb.server.obj.MsvSamRecord;
import kobic.msb.server.obj.ReadObject;
import kobic.msb.server.obj.GeneralReadObject;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.component.ColumnGroup;
import kobic.msb.swing.component.GroupableTableHeader;
import kobic.msb.swing.component.JMsbBrowserTableModel;
import kobic.msb.swing.listener.key.NumericKeyListener;
import kobic.msb.swing.renderer.MsvComboBoxItemRenderer;

@SuppressWarnings("rawtypes")
public class JMsbReadManageDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel				contentPanel = new JPanel();

	private JTextField					txtSequence;
	private JTextField					txtStart;
	private JTextField					txtEnd;
	/*** Over JDK 1.7 ***/
//	private JComboBox<String>			cmbStrand;
	/*** Under JDK 1.7 ***/
	private JComboBox					cmbStrand;
	private JTable						tblReadCount;
	private JScrollPane					tableScrollPane;
	private Integer						currentPos;
	private Integer						currentField;

	private AlignmentDockingWindowObj	dockWindow;
	
	private int							manageType;
	
	public static final int ADD			= 1;
	public static final int MODIFY		= 2;

	private JMsbReadManageDialog remote = JMsbReadManageDialog.this;
	/**
	 * Create the dialog.
	 */
	@SuppressWarnings("unchecked")
	public JMsbReadManageDialog(Frame frame, AbstractDockingWindowObj dockWindow, Integer currentPos, Integer currentField, String title, Dialog.ModalityType modalType, int manageType ) {
		super( frame, title, modalType );
		
		this.manageType	= manageType;

		this.dockWindow = (AlignmentDockingWindowObj)dockWindow;
		
		this.currentPos = currentPos;
		this.currentField = currentField;

		this.setResizable(false);

		setBounds(100, 100, 674, 292);
		getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JPanel panel = new JPanel();
		panel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED) );
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
		);
		panel.setLayout(null);
		{
			JLabel lblSequence = new JLabel("Sequence");
			lblSequence.setBounds(21, 17, 61, 16);
			panel.add(lblSequence);
		}
		{
			this.txtSequence = new JTextField();
			this.txtSequence.setDocument( new UpperCaseDocument() );
			this.txtSequence.addKeyListener( new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					String strKey = Character.toString( e.getKeyChar() ).toUpperCase();

					if ( !(strKey.equals("A") || strKey.equals("T") || strKey.equals("G") || strKey.equals("C")) ) {
						Toolkit.getDefaultToolkit().beep();
						e.consume();
					}
				}
			});

			this.txtSequence.setBounds(94, 11, 548, 28);
			this.txtSequence.getDocument().addDocumentListener( new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub
					remote.setInitializeEndPos();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub
					remote.setInitializeEndPos();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			panel.add(this.txtSequence);
			this.txtSequence.setColumns(10);
		}
		{
			JLabel lblStart = new JLabel("Start pos.");
			lblStart.setBounds(21, 47, 61, 16);
			panel.add(lblStart);
		}
		{
			this.txtStart = new JTextField();
			this.txtStart.setEditable(false);
			this.txtStart.setBounds(94, 41, 156, 28);
			panel.add(this.txtStart);
			this.txtStart.setColumns(10);
		}
		{
			JLabel lblEnd = new JLabel("End pos.");
			lblEnd.setBounds(21, 81, 61, 16);
			panel.add(lblEnd);
		}
		{
			this.txtEnd = new JTextField();
			this.txtEnd.setEditable(false);
			this.txtEnd.setColumns(10);
			this.txtEnd.setBounds(94, 75, 156, 28);
			panel.add(this.txtEnd);
		}
		{
			JLabel lblStrand = new JLabel("Strand");
			lblStrand.setBounds(21, 110, 61, 16);
			panel.add(lblStrand);
		}
		{
			/*** Over JDK 1.7 ***/
//			this.cmbStrand = new JComboBox<String>();
			/*** Under JDK 1.7 ***/
			this.cmbStrand = new JComboBox();
			
			/*** Over JDK 1.7 ***/
//			this.cmbStrand.setModel(new DefaultComboBoxModel<String>(new String[] {"Sense(+)", "Anti-sense(-)"}));
			/*** Under JDK 1.7 ***/
			this.cmbStrand.setRenderer( new MsvComboBoxItemRenderer() );
			this.cmbStrand.setModel( new DefaultComboBoxModel(new ComboBoxItem[] { new ComboBoxItem("Forward", "+"), new ComboBoxItem("Reverse", "-")} ) );
			this.cmbStrand.setBounds(94, 106, 156, 27);
			panel.add(this.cmbStrand);
		}
		
		this.tblReadCount = new JTable() {
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
				String colHeader = this.getColumnModel().getColumn(column).getHeaderValue().toString();
				MsbRCTColumnModel colModel = remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel().getColumnModel( colHeader );
				
				if( colModel.getColumnType().equals( JMsbSysConst.SAMPLE_HEADER_PREFIX ) )
					return true;

                return false;
            }
	    };

	    this.tblReadCount.setDefaultRenderer( Object.class, new RightTableCellRenderer() );

	    this.tblReadCount.setShowGrid( true );
		this.tblReadCount.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
		this.tblReadCount.setRowHeight( 15 );
		this.tblReadCount.setDragEnabled(false);
		this.tblReadCount.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		
		this.tblReadCount.setFillsViewportHeight(true);

		this.setChoosedReadPos( currentPos );
	    
	    this.tableScrollPane = new JScrollPane( this.tblReadCount );
	    
		this.tableScrollPane.setBounds(21, 138, 623, 77);
		panel.add(this.tableScrollPane);
		
		JButton btnPlusPos = new JButton("+");
		btnPlusPos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long startPos = Long.parseLong( Utilities.nulltoZero( remote.txtStart.getText() ) ) + 1;
				remote.txtStart.setText( Long.toString( startPos ) );
				remote.setInitializeEndPos();
			}
		});
		btnPlusPos.setBounds(299, 42, 49, 29);
		panel.add(btnPlusPos);
		
		JButton btnMinusPos = new JButton("-");
		btnMinusPos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long startPos = Long.parseLong( Utilities.nulltoZero( remote.txtStart.getText() ) ) - 1;
				remote.txtStart.setText( Long.toString( startPos ) );
				remote.setInitializeEndPos();
			}
		});
		
		btnMinusPos.setBounds(249, 42, 49, 29);
		panel.add(btnMinusPos);
		this.contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton applyButton = new JButton("Apply");
				applyButton.addActionListener( new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if( remote.manageType == JMsbReadManageDialog.MODIFY ) {
							// TODO Auto-generated method stub
							ReadWithMatrix matrix = remote.dockWindow.getCurrentModel().getReadVectorListByFiltered().get( remote.currentPos );
							ReadObject readObj = matrix.getReadObject();

							if( readObj instanceof GeneralReadObject ) {
								GeneralReadObject tmpGro = (GeneralReadObject)readObj;
								
								ReadObject pickedRead = remote.getReadObjectAt( tmpGro, remote.currentField.intValue() );

								pickedRead.setStartPosition( Integer.parseInt( remote.txtStart.getText() ) );
								pickedRead.setEndPosition( Integer.parseInt( remote.txtEnd.getText() ) );

								char originalStrand = pickedRead.getReadQuality().getOrientation().charAt(0);
								
								ComboBoxItem item = (ComboBoxItem) remote.cmbStrand.getSelectedItem();
								char changeStrand = item.getValue().charAt(0);

								String seq = remote.txtSequence.getText();
								if( originalStrand != changeStrand )	seq = Utilities.getReverseComplementary( seq );

								pickedRead.setSequence( seq );

								if( changeStrand == '+' )	pickedRead.getReadQuality().setStrand('1');
								else						pickedRead.getReadQuality().setStrand('0');
	
								Map<String, Double> countDataMap = JMsbReadManageDialog.getRecordArrayAtJTable( remote.tblReadCount, 0 );
		
								Map<String, Double> realMap = matrix.getCountData();
								
								Iterator<String> keyIter = countDataMap.keySet().iterator();
								while( keyIter.hasNext() ) {
									String editedKey = keyIter.next();
									if( realMap.containsKey( editedKey ) )	realMap.put( editedKey, countDataMap.get(editedKey) );
								}
								countDataMap = null;
							}
						}else {
							Map<String, Double> countDataMap = JMsbReadManageDialog.getRecordArrayAtJTable( remote.tblReadCount, 0 );

							int readStart = 0;
							int readEnd = 0;
							try {
								readStart = Integer.parseInt( remote.txtStart.getText() );
							}catch(Exception ex) {
								JOptionPane.showMessageDialog(remote, "Start is only numeric", "Error", JOptionPane.ERROR_MESSAGE );
								remote.txtStart.requestFocus();
								return;
							}
							try {
								readEnd = Integer.parseInt( remote.txtEnd.getText() );
							}catch(Exception ex) {
								JOptionPane.showMessageDialog(remote, "End is only numeric", "Error", JOptionPane.ERROR_MESSAGE );
								remote.txtEnd.requestFocus();
								return;
							}

							char strand			= remote.dockWindow.getCurrentModel().getReferenceSequenceObject().getStrand();
							String mirid		= remote.dockWindow.getDefaultMirid();
							
							ReadObject inputRead = new ReadObject(readStart, readEnd, remote.txtSequence.getText(), strand);
							
							int mismatch = 0;
							try {
								mismatch = MatrixObj.getMismatchedCount( inputRead, remote.dockWindow.getCurrentModel().getReferenceSequenceObject() );
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								mismatch = 0;
							}

							MsvSamRecord info = new MsvSamRecord( mirid, "", remote.txtSequence.getText(), strand, readStart, readEnd, mismatch );

							ReadWithMatrix matrix = remote.dockWindow.getCurrentModel().getReadVectorListByFiltered().get(0);
							Map<String, Double> realMap = new HashMap<String, Double>();
							
							Iterator<String> headerIter = matrix.getCountData().keySet().iterator();
							while( headerIter.hasNext() ) {	
								String editedKey = headerIter.next();
								if( matrix.getCountData().containsKey( editedKey ) )	realMap.put( editedKey, countDataMap.get(editedKey) );
								else													realMap.put( editedKey,  0d );
							};

							ReadWithMatrix newReadWithMatrix = new ReadWithMatrix( realMap, info );
							
							remote.dockWindow.getCurrentModel().addRead( newReadWithMatrix, remote.currentPos );
						}

						remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
						
						remote.dockWindow.setIsMousePositionFixed( false );
						remote.dockWindow.getSsPanel().setMouseClicked( false );
						
						remote.dispose();
					}
				});
				applyButton.setActionCommand("Apply");
				buttonPane.add(applyButton);
				getRootPane().setDefaultButton(applyButton);
			}
			{
				JButton closeButton = new JButton("Close");
				closeButton.addActionListener( new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						remote.dispose();
						
						remote.dockWindow.setIsMousePositionFixed( false );
						remote.dockWindow.getSsPanel().setMouseClicked( false );
					}
				});
				closeButton.setActionCommand("Close");
				buttonPane.add(closeButton);
			}
		}
	}
	
	private static Map<String, Double> getRecordArrayAtJTable(JTable table, int rowIndex) {
		TableColumnModel cm = table.getColumnModel();
		Map<String, Double> rowData = new LinkedHashMap<String, Double>();  
		for (int i = 0; i < cm.getColumnCount(); i++) {
			String key = cm.getColumn(i).getHeaderValue().toString();
			double value = Double.parseDouble( Utilities.nulltoZero( table.getValueAt( rowIndex, i ) ) );
			rowData.put( key, value );
		}
		return rowData;
	}

	private void setInitializeEndPos() {
		String sequence = this.txtSequence.getText();

		String strStartPos = Utilities.emptyToZero( this.txtStart.getText() );
		long endPos = Long.parseLong( strStartPos ) + sequence.length() - 1;
		this.txtEnd.setText( Long.toString( endPos ) );
	}
	
	private void setChoosedReadPos( Integer pos ) {
		MSBReadCountTableColumnStructureModel currentRCTColumnStructureModel = remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel();

	    String[] text = new String[currentRCTColumnStructureModel.getHeatMapHeader().size()];
	    List<String> srcList = currentRCTColumnStructureModel.getHeatMapHeader();

		srcList.toArray( text );

		if( this.manageType == JMsbReadManageDialog.MODIFY ) {
			ReadWithMatrix obj = this.dockWindow.getCurrentModel().getReadWithMatrix( pos );
			
			if( obj.getReadObject() instanceof GeneralReadObject ) {
				GeneralReadObject gro = (GeneralReadObject) obj.getReadObject();
				
				ReadObject pickedRead = this.getReadObjectAt( gro, this.currentField );
				
				if( pickedRead != null ) {
					this.txtSequence.setText( pickedRead.getSequenceByString() );
					this.txtStart.setText( Long.toString(pickedRead.getStartPosition()) );
					this.txtEnd.setText( Long.toString(pickedRead.getEndPosition()) );

					if(pickedRead.getReadQuality().getOrientation().equals("+"))	this.cmbStrand.setSelectedIndex(0);
					else															this.cmbStrand.setSelectedIndex(1);
		
					double[] rct = ReadWithMatrix.getVectorData( currentRCTColumnStructureModel.getChoosedGroupMap(), obj.getCountData() );
				    Double[][] data = new Double[1][ rct.length ];
				    for(int i=0; i<rct.length; i++)
				    	data[0][i] = new Double( rct[i] );
				    
				    JMsbBrowserTableModel dm = new JMsbBrowserTableModel();
		
				    dm.setDataVector( data, text );
		
				    this.tblReadCount.setModel( dm );
				}
			}
		}else {
			JMsbBrowserTableModel dm = new JMsbBrowserTableModel();

			Double[][] data = new Double[1][srcList.size()];
			for(int i=0; i<data[0].length; i++)	data[0][i] = 0d;
		    dm.setDataVector( data, text );
		    
		    this.txtStart.setText( "0" );

		    this.tblReadCount.setModel( dm );
		}

	    TableColumnModel		cm 		= this.tblReadCount.getColumnModel();
		GroupableTableHeader	header	= (GroupableTableHeader)this.tblReadCount.getTableHeader();
		
		Dimension dim = header.getPreferredSize();
		dim.setSize( dim.getWidth(), 40 );
		
		header.setPreferredSize( dim );
		Map<String, MsbRCTColumnModel> columnStructure = currentRCTColumnStructureModel.getHeatMapColumnStructure();

		for( Iterator<String> iter = columnStructure.keySet().iterator(); iter.hasNext(); ) {
			String group = iter.next();
			
			MsbRCTColumnModel groupColumn = columnStructure.get( group );

			if( groupColumn.getChildColumnList() != null ) {
				ColumnGroup g_name = new ColumnGroup( groupColumn.getColumnId() );

				// To get child list (columns in group)
				List<String> columns = groupColumn.getChildColumnIdList();

				// 1 : JTree tree structure loop
				for(int idx=0; idx<cm.getColumnCount(); idx++) {
 					for( int j=0; j<columns.size(); j++ ) {
						if( cm.getColumn(idx).getHeaderValue().equals( columns.get(j) ) ) {
							g_name.add( cm.getColumn( idx ) );

							MsbRCTColumnModel subColumn = currentRCTColumnStructureModel.getColumnModel( columns.get(j) );
							if( subColumn.getColumnType().equals( JMsbSysConst.SAMPLE_HEADER_PREFIX ) ){
								cm.getColumn(idx).setCellEditor( new MyCellEditor( this.tblReadCount, header, g_name, idx ) );
							}
						    break;
						}
					}
				}
				header.addColumnGroup( g_name );
			}else {			
				group = groupColumn.getColumnId();

				ColumnGroup tmpGroup = new ColumnGroup( group );
				header.addColumnGroup( tmpGroup );
			}
		}
	}
	
	private ReadObject getReadObjectAt( GeneralReadObject obj, int fieldsAt ) { 
		for(int i=0; i<obj.getRecordElements().size(); i++) {
			if( obj.getRecordElements().get(i).getStartPosition() <= remote.currentField && obj.getRecordElements().get(i).getEndPosition() >= remote.currentField ) {
				return obj.getRecordElements().get(i);
			}
		}
		return null;
	}

	private class UpperCaseDocument extends PlainDocument {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) {
                return;
            }

            char[] upper = str.toCharArray();
            for (int i = 0; i < upper.length; i++) {
                upper[i] = Character.toUpperCase(upper[i]);
            }
            super.insertString(offs, new String(upper), a);
        }
    }
	
	private class MyCellEditor extends DefaultCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final JTextField tf;

		public MyCellEditor(JTable table, GroupableTableHeader header, ColumnGroup g_name, int columnIndex) {
			super(new JTextField());
			this.tf = (JTextField) getComponent();
			this.tf.addKeyListener(new NumericKeyListener() );
			this.tf.getDocument().addDocumentListener( new MyDocumentListener( table, this.tf, header, g_name, columnIndex ) );
			this.setClickCountToStart(1);
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			String strVal = value.toString();
			if(strVal.equals("NaN"))	value = "0";
			BigDecimal bd = new BigDecimal( value.toString() );
			long val = bd.longValue();

			this.tf.setText( new Long( val ).toString() );
			return tf;
		}

		public Object getCellEditorValue() {
			return tf.getText();
		}
	}
	
//	private class MyKeyListener implements KeyListener{
//		public void keyPressed(KeyEvent e) {}
//		public void keyReleased(KeyEvent e) {}
//		public void keyTyped(KeyEvent e) {
//			if ( !Character.isDigit(e.getKeyChar()) ) {
//				Toolkit.getDefaultToolkit().beep();
//				e.consume();
//			}
//		}
//	}

	private class MyDocumentListener implements DocumentListener {
		private ColumnGroup				g_name;
		private GroupableTableHeader	header;
		private JTable					table;
		private JTextField				tf;
		private int columnIndex;
		
		MyDocumentListener( JTable table, JTextField tf, GroupableTableHeader header, ColumnGroup g_name, int columnIndex ) {
			this.table			= table;
			this.tf				= tf;
			this.columnIndex	= columnIndex;
			this.g_name			= g_name;
			this.header			= header;
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			String value = tf.getText();

			BigDecimal bd = new BigDecimal( value );
			long val = bd.longValue();

			this.table.getModel().setValueAt( val, 0, this.columnIndex );

			this.doCalcGroupSum();
			this.doCalcTotalSum();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			String value = Utilities.emptyToZero( tf.getText() );
			
			BigDecimal bd = new BigDecimal( value );
			long val = bd.longValue();

			this.table.getModel().setValueAt( val, 0, this.columnIndex );

			this.doCalcGroupSum();
			this.doCalcTotalSum();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
		}
		
		private void doCalcTotalSum() {
			int countSum = 0;
			Vector<Object> vec = this.header.getColumnGroup();
			for(int i=0; i<vec.size(); i++) {
				ColumnGroup cg = (ColumnGroup) vec.get(i);
				if( cg.getColumGroups().size() != 0 ) {
					countSum += this.getCount( cg.getColumGroups() );
				}
			}

			int index = header.getColumnModel().getColumnIndex( JMsbSysConst.TOTAL_SUM_HEADER );
			this.table.getModel().setValueAt( countSum, 0, index);
		}
		
		private double getCount( Vector<Object> vec ) {
			double sum = 0;
			for(int i=0; i<vec.size(); i++) {
				TableColumn ttc = (TableColumn)vec.get(i);
				
				MsbRCTColumnModel colModel = remote.dockWindow.getCurrentModel().getMSBReadCountTableColumnStructureModel().getColumnModel( ttc.getHeaderValue().toString() );
				
				if( colModel.getColumnType().equals( JMsbSysConst.SAMPLE_HEADER_PREFIX ) ) {
					int index = this.header.getColumnModel().getColumnIndex( ttc.getHeaderValue() );
					String v = this.table.getModel().getValueAt(0, index).toString();
					if( v.equals("NaN") ) v = "0";
					sum += Double.parseDouble( v );
				}
			}
			return sum;
		}

		private void doCalcGroupSum() {			
			double countSum = this.getCount( this.g_name.getColumGroups() );

			String labelGroupSum = this.g_name.getHeaderValue().toString() + JMsbSysConst.SUM_SUFFIX;

			int index = this.header.getColumnModel().getColumnIndex( labelGroupSum );
			this.table.getModel().setValueAt( countSum, 0, index);
		}
	}
	
	private class RightTableCellRenderer extends DefaultTableCellRenderer {   
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
}
