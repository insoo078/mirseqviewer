package kobic.msb.swing.frame.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import kobic.msb.server.model.Model;
import kobic.msb.server.model.MsbFilterModel;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.system.engine.MsbEngine;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class JMsbFindReadDailog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel	contentPanel = new JPanel();
	private JPanel			panel;
	
	private JTextField			txtQuery;
	/*** Over JDK 1.7 ***/
//	private JComboBox<String>	cmbFilter;
	/*** Under JDK 1.7 ***/
	private JComboBox	cmbFilter;
	/*** Over JDK 1.7 ***/
//	private JComboBox<String>	cmbOperator;
	/*** Under JDK 1.7 ***/
	private JComboBox	cmbOperator;
	
	private AbstractDockingWindowObj dockWindow;
	
	private JMsbFindReadDailog remote = JMsbFindReadDailog.this;
	
	public static String[] ITEMS_ABOUT_COUNT 		= new String[] {"<", "<=", "=", ">", ">=", "between"};
	public static String[] ITEMS_ABOUT_SEQUENCE 	= new String[] {"start with", "end with", "equal"};
	public static String[] ITEMS_ABOUT_REMOVE		= new String[] {"remove"};
	public static String[] ITEMS_BOOLEAN			= new String[] {"true", "false"};
	public static String[] ITEMS_FILTER				= new String[] {"count", "mis match", "sequence", "start position", "length", "reverse reads"};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			JMsbFindReadDailog dialog = new JMsbFindReadDailog(null, "test", Dialog.ModalityType.APPLICATION_MODAL );
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
//			e.printStackTrace();
			MsbEngine.logger.error( "error : ", e );
		}
	}

	/**
	 * Create the dialog.
	 */
	@SuppressWarnings("unchecked")
	public JMsbFindReadDailog( AbstractDockingWindowObj dockWindow, String title, Dialog.ModalityType modalType ) {
		super( dockWindow.getMainFrame(), title, modalType );
		
		this.dockWindow = dockWindow;
		this.setResizable(false);

		this.setBounds(100, 100, 634, 177);
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			this.panel = new JPanel();
			this.panel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED) );
			this.panel.setLayout(null);
			
			JLabel lblFilter = new JLabel("Filter");
			lblFilter.setBounds(6, 12, 61, 16);
			panel.add(lblFilter);
			
			/*** Over JDK 1.7 ***/
//			this.cmbFilter		= new JComboBox<String>();
			/*** Under JDK 1.7 ***/
			this.cmbFilter		= new JComboBox();
			this.txtQuery		= new JTextField();
			/*** Over JDK 1.7 ***/
//			this.cmbOperator	= new JComboBox<String>();
			/*** Under JDK 1.7 ***/
			this.cmbOperator	= new JComboBox();
			
			this.txtQuery.setColumns(10);
			this.txtQuery.setBounds(80, 40, 539, 28);
			this.panel.add(this.txtQuery);

			/*** Over JDK 1.7 ***/
//			this.cmbOperator.setModel(new DefaultComboBoxModel<String>( JMsbFindReadDailog.ITEMS_ABOUT_COUNT ));
			/*** Under JDK 1.7 ***/
			this.cmbOperator.setModel(new DefaultComboBoxModel( JMsbFindReadDailog.ITEMS_ABOUT_COUNT ));
			this.cmbOperator.setBounds(247, 8, 155, 27);
			this.panel.add(this.cmbOperator);

			/*** Over JDK 1.7 ***/
//			this.cmbFilter.setModel(new DefaultComboBoxModel<String>( JMsbFindReadDailog.ITEMS_FILTER ) );
			/*** Under JDK 1.7 ***/
			this.cmbFilter.setModel(new DefaultComboBoxModel( JMsbFindReadDailog.ITEMS_FILTER ) );
			this.cmbFilter.setBounds(80, 8, 155, 27);
			this.panel.add(this.cmbFilter);

			this.cmbFilter.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					Object strCmbFilter = cmbFilter.getSelectedItem();
					if( strCmbFilter != null ) {
						if( cmbFilter.getSelectedItem().equals("sequence") ) {
							/*** Over JDK 1.7 ***/
//							cmbOperator.setModel( new DefaultComboBoxModel<String>( JMsbFindReadDailog.ITEMS_ABOUT_SEQUENCE ) );
							/*** Under JDK 1.7 ***/
							cmbOperator.setModel( new DefaultComboBoxModel( JMsbFindReadDailog.ITEMS_ABOUT_SEQUENCE ) );
						}else {
							/*** Over JDK 1.7 ***/
//							cmbOperator.setModel( new DefaultComboBoxModel<String>( JMsbFindReadDailog.ITEMS_ABOUT_COUNT ) );
							/*** Under JDK 1.7 ***/
							cmbOperator.setModel( new DefaultComboBoxModel( JMsbFindReadDailog.ITEMS_ABOUT_COUNT ) );
						}
					}else {
						/*** Over JDK 1.7 ***/
//						cmbFilter.setModel(new DefaultComboBoxModel<String>( JMsbFindReadDailog.ITEMS_FILTER ) );
						/*** Under JDK 1.7 ***/
						cmbFilter.setModel(new DefaultComboBoxModel( JMsbFindReadDailog.ITEMS_FILTER ) );
						cmbFilter.setSelectedIndex(0);
					}
				}
			});
			
			this.initialize();
		}
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(1)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
		);
		
		JLabel lblKeyword = new JLabel("Keyword");
		lblKeyword.setBounds(6, 46, 61, 16);
		panel.add(lblKeyword);
		
		JLabel lblProgress = new JLabel("Progress");
		lblProgress.setBounds(6, 74, 61, 16);
		panel.add(lblProgress);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(80, 74, 538, 20);
		panel.add(progressBar);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				
				okButton.addActionListener( new ActionListener() {

					@SuppressWarnings("unused")
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						String filter	= remote.cmbFilter.getSelectedItem().toString();
						String operator = remote.cmbOperator.getSelectedItem().toString();
						String query 	= remote.txtQuery.getText();
						
						if( remote.dockWindow instanceof AlignmentDockingWindowObj ) {
							AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj) remote.dockWindow;
						
//							Model model = dwo.getModel();
	
							Model model = dwo.getCurrentModel();
							MsbFilterModel filterModel = model.getProjectMapItem().getMsbFilterModel();
							filterModel.addModel(0, MsbFilterModel.AND, remote.cmbFilter.getSelectedItem().toString(), remote.cmbOperator.getSelectedItem().toString(), remote.txtQuery.getText() );
//							model.filter( filter, operator, query );
	
							dwo.setMirid( dwo.getDefaultMirid() );
							
							remote.dispose();
							
							dwo.setIsMousePositionFixed( false );
							dwo.getSsPanel().setMouseClicked( false );
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.addActionListener( new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						remote.dispose();
						
						if( remote.dockWindow instanceof AlignmentDockingWindowObj ) {
							AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj) remote.dockWindow;
						
							dwo.setIsMousePositionFixed( false );
							dwo.getSsPanel().setMouseClicked( false );
						}
					}
				});
				cancelButton.setActionCommand("Close");
				buttonPane.add(cancelButton);
			}
		}
	}

	@SuppressWarnings("unused")
	public void initialize() {
		if( remote.dockWindow instanceof AlignmentDockingWindowObj ) {
			AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj) remote.dockWindow;
			
//			this.cmbFilter.setSelectedItem( dwo.getModel().getFilter() );
//			this.txtQuery.setText( dwo.getModel().getQuery() );
//			this.cmbOperator.setSelectedItem( dwo.getModel().getOperator() );
		}
	}
}
