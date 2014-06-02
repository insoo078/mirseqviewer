package kobic.msb.swing.component;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import kobic.msb.common.ImageConstant;
import kobic.msb.server.model.Model;
import kobic.msb.server.model.MsbFilterModel;
import kobic.msb.server.model.MsbFilterModel.FilterModel;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JMsbFindReadDailog;
import kobic.msb.swing.listener.menu.ImportProjectActionListener;
import kobic.msb.swing.listener.menu.PrintActionListener;
import kobic.msb.swing.listener.menu.ExportProjectActionListener;
import kobic.msb.swing.listener.menu.SaveProjectActionListener;
import kobic.msb.swing.listener.projecttreepanel.ExeProjectActionListener;
import kobic.msb.swing.listener.projecttreepanel.NewProjectActionListener;
import kobic.msb.swing.listener.projecttreepanel.QuickNewProjectActionListener;
import kobic.msb.system.engine.MsbEngine;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

@SuppressWarnings("rawtypes")
public class JMsbToolBar extends JToolBar implements Observer{
	private JMsbBrowserMainFrame frame; 
	
	/*** Over JDK 1.7 ***/
//	private JComboBox<String> cmbProjectList;
	/*** Under JDK 1.7 ***/
	private JComboBox cmbProjectList;
	
	
	private JTextField			txtQuery;
	
	/*** Over JDK 1.7 ***/
//	private JComboBox<String>	cmbFilter;
	/*** Under JDK 1.7 ***/
	private JComboBox	cmbFilter;
	/*** Over JDK 1.7 ***/
//	private JComboBox<String>	cmbOperator;
	/*** Under JDK 1.7 ***/
	private JComboBox	cmbOperator;
	
	
	private JButton importButton;
	private JButton newButton;
	private JButton quickPrjButton;
	private JButton saveButton;
	private JButton exportButton;
	private JButton findButton;
	private JButton runButton;
	private JButton printButton;

	private ActionListener projectActionListener;
	
	private JMsbToolBar remote = JMsbToolBar.this;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public JMsbToolBar( JMsbBrowserMainFrame frame ) {
		this.frame = frame;

		/***************************************************
		 * Toolbar Layout
		 */
		FlowLayout layout = new FlowLayout();
		layout.setHgap(5);
		layout.setVgap(0);
		layout.setAlignment( FlowLayout.LEFT );
		this.setLayout( layout);

		this.importButton	= new JMsbNullBorderButton( ImageConstant.importIcon,	"Import" );
		this.newButton		= new JMsbNullBorderButton( ImageConstant.newDocIcon,	"New miRseq project" );
		this.quickPrjButton	= new JMsbNullBorderButton( ImageConstant.quickDocIcon, "Quick miRseq project" );
		this.saveButton		= new JMsbNullBorderButton( ImageConstant.saveIcon, 	"Save" );
		this.exportButton	= new JMsbNullBorderButton( ImageConstant.exportIcon,	"Export" );
		this.findButton		= new JMsbNullBorderButton( ImageConstant.runIcon,		"Filter" );
		this.runButton		= new JMsbNullBorderButton( ImageConstant.findIcon,		"Run" );
		this.printButton	= new JMsbNullBorderButton( ImageConstant.printIcon,	"Print" );
		
		this.projectActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox cb = (JComboBox)e.getSource();

				if( cb.getItemCount() > 0 ) {
					remote.saveButton.setEnabled( true );
					exportButton.setEnabled( true );
					findButton.setEnabled( true );
					runButton.setEnabled( true );
					printButton.setEnabled( true );
//					profileButton.setEnabled(true);
				}else {
					saveButton.setEnabled( false );
					exportButton.setEnabled( false );
					findButton.setEnabled( false );
					runButton.setEnabled( false );
					printButton.setEnabled( false );
//					profileButton.setEnabled(false);
				}

				runButton.doClick();
			}
		};
		
		runButton.setVisible(false);
//		final JButton profileButton	= new JMsbNullBorderButton( ImageConstant.heatMapIcon,	"Profile" );

		/*** Over JDK 1.7 ***/
//		this.cmbProjectList = new JComboBox<String>();
		/*** Under JDK 1.7 ***/
		this.cmbProjectList = new JComboBox();
		this.cmbProjectList.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXX");

		this.refreshProjectListForToolBar();
		this.cmbProjectList.setMaximumSize( this.cmbProjectList.getPreferredSize() );

		this.importButton.addActionListener(	new ImportProjectActionListener( this.frame ) );
		this.newButton.addActionListener(	new NewProjectActionListener( this.frame ) );
		this.quickPrjButton.addActionListener( new QuickNewProjectActionListener( this.frame ) );
		this.runButton.addActionListener(	new ExeProjectActionListener(this.frame) );
		this.findButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				AbstractDockingWindowObj obj = remote.frame.getAbstractDockingWindowObj( remote.cmbProjectList.getSelectedItem().toString() );
				
				if( obj instanceof AlignmentDockingWindowObj ) {
					AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj) obj;

					if( remote.cmbFilter.getSelectedItem().equals("reverse reads") ) {
						if( !(remote.txtQuery.getText().toUpperCase().equals("TRUE") || remote.txtQuery.getText().toUpperCase().equals("FALSE") ) ) {
							JOptionPane.showMessageDialog( remote.frame, "You should input the keyword only true/false,\n  when you choose the reverse reads filter", "Error", JOptionPane.ERROR_MESSAGE );
							remote.txtQuery.setText("");
							remote.txtQuery.requestFocus();
							return;
						}
					}
					Model model = dwo.getCurrentModel();
					MsbFilterModel filterModel = model.getProjectMapItem().getMsbFilterModel();
					filterModel.clear();
					filterModel.addModel(0, MsbFilterModel.DEFAULT, remote.cmbFilter.getSelectedItem().toString(), remote.cmbOperator.getSelectedItem().toString(), remote.txtQuery.getText() );

					dwo.setMirid( dwo.getDefaultMirid() );
					
					dwo.setIsMousePositionFixed( false );
					dwo.getSsPanel().setMouseClicked( false );
				}
			}
		});
//		profileButton.addActionListener( new ProfilingProjectActionListener( this.frame, null ) );
		printButton.addActionListener(	new PrintActionListener( this.frame) );
		
		saveButton.addActionListener(	new SaveProjectActionListener( this.frame ) );
		exportButton.addActionListener( new ExportProjectActionListener( this.frame ) );

		final JLabel projectLabel	= new JLabel("Project  ");
		final JLabel filterLabel	= new JLabel("Quick Filter  ");
		
		/*** Over JDK 1.7 ***/
//		this.cmbFilter		= new JComboBox<String>();
		/*** Under JDK 1.7 ***/
		this.cmbFilter		= new JComboBox();
		this.txtQuery		= new JTextField(10);
		/*** Over JDK 1.7 ***/
//		this.cmbOperator	= new JComboBox<String>();
		/*** Under JDK 1.7 ***/
		this.cmbOperator	= new JComboBox();
		
		/*** Over JDK 1.7 ***/
//		this.cmbOperator.setModel(new DefaultComboBoxModel<String>( JMsbFindReadDailog.ITEMS_ABOUT_COUNT ));
//		this.cmbFilter.setModel(new DefaultComboBoxModel<String>( JMsbFindReadDailog.ITEMS_FILTER ) );
		/*** Under JDK 1.7 ***/
		this.cmbOperator.setModel(new DefaultComboBoxModel( JMsbFindReadDailog.ITEMS_ABOUT_COUNT ));
		this.cmbFilter.setModel(new DefaultComboBoxModel( JMsbFindReadDailog.ITEMS_FILTER ) );
		
		this.cmbFilter.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object strCmbFilter = cmbFilter.getSelectedItem();
				if( strCmbFilter != null ) {
					if( cmbFilter.getSelectedItem().equals("sequence") ) {
						/*** Over JDK 1.7 ***/
//						cmbOperator.setModel( new DefaultComboBoxModel<String>( JMsbFindReadDailog.ITEMS_ABOUT_SEQUENCE ) );
						/*** Under JDK 1.7 ***/
						cmbOperator.setModel( new DefaultComboBoxModel( JMsbFindReadDailog.ITEMS_ABOUT_SEQUENCE ) );
					}else if( cmbFilter.getSelectedItem().equals("reverse reads") ) {
						cmbOperator.setModel( new DefaultComboBoxModel( JMsbFindReadDailog.ITEMS_ABOUT_REMOVE ) );
					}else {
						/*** Over JDK 1.7 ***/
//						cmbOperator.setModel( new DefaultComboBoxModel<String>( JMsbFindReadDailog.ITEMS_ABOUT_COUNT ) );
						/*** Under JDK 1.7 ***/
						cmbOperator.setModel( new DefaultComboBoxModel( JMsbFindReadDailog.ITEMS_ABOUT_COUNT ) );
					}
				}else {
					/*** Over JDK 1.7 ***/
//					cmbFilter.setModel(new DefaultComboBoxModel<String>( JMsbFindReadDailog.ITEMS_FILTER ) );
					/*** Under JDK 1.7 ***/
					cmbFilter.setModel(new DefaultComboBoxModel( JMsbFindReadDailog.ITEMS_FILTER ) );
					cmbFilter.setSelectedIndex(0);
				}
			}
		});

		this.add( this.newButton );
		this.add( this.quickPrjButton );
		this.add( this.saveButton );
		this.addSeparator();
		this.add( this.importButton );
		this.add( this.exportButton );
		this.add( this.printButton );
		this.addSeparator();
		this.add( projectLabel );
		this.add( this.cmbProjectList );
//		this.add( profileButton );
		this.addSeparator();
		this.add( filterLabel );
		this.add( this.cmbFilter );
		this.add( this.cmbOperator );
		this.add( this.txtQuery );
		this.add( this.findButton );
		this.add( this.runButton );
		this.addSeparator();
	}
	
	public String getSelectedProject() {
		if( this.cmbProjectList.getSelectedItem() == null )
			return null;
		return this.cmbProjectList.getSelectedItem().toString();
	}
	
	public void removeComboBoxItem(String project) {
		this.cmbProjectList.removeItem( project );
	}
	
	public void removeComboBoxAllItems() {
		this.cmbProjectList.removeAllItems();
	}

	@SuppressWarnings("unchecked")
	public void refreshProjectListForToolBar() {
		this.cmbProjectList.removeActionListener( this.projectActionListener );
		this.cmbProjectList.removeAllItems();
		List<String> projectList = MsbEngine.getInstance().getProjectManager().getProjectMap().getProjectNameList();
		for(String projectName:projectList) {
			this.cmbProjectList.addItem( projectName );
		}
		this.cmbProjectList.revalidate();
		
		if( this.cmbProjectList.getItemCount() > 0 ){
			remote.saveButton.setEnabled( true );
			exportButton.setEnabled( true );
			findButton.setEnabled( true );
			runButton.setEnabled( true );
			printButton.setEnabled( true );
		}else {
			remote.saveButton.setEnabled( false );
			exportButton.setEnabled( false );
			findButton.setEnabled( false );
			runButton.setEnabled( false );
			printButton.setEnabled( false );
		}
		
		this.cmbProjectList.addActionListener( this.projectActionListener );
	}

	public ActionListener getIndexFileListener() {
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
//				JBamIndexManagerDialog dialog = new JBamIndexManagerDialog( remote.frame, remote.frame.getMsbEngine() );
//				dialog.setVisible( true );
			}
		};

		return actionListener;
	}
	
	public ActionListener getFindButtonActionListener() {
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String projectName = cmbProjectList.getSelectedItem().toString();
//				AbstractDockingWindowObj dockWindow = frame.getDockingWindowObjMap().get( projectName );
				AbstractDockingWindowObj dockWindow = frame.getAbstractDockingWindowObj(projectName);

				if( dockWindow != null ){ 
					JMsbFindReadDailog dialog = new JMsbFindReadDailog( dockWindow, "Find", Dialog.ModalityType.APPLICATION_MODAL);
					dialog.setVisible(true);
				}
			}
		};

		return actionListener;
	}

	@Override
	public void update(java.util.Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
	
	public void setSelectProjectName(String projectName) {
		this.cmbProjectList.setSelectedItem( projectName );
	}
	
	public void updateFilters(MsbFilterModel filterModels) {
		if( filterModels.size() > 0 ) {
			for(int i=0; i<filterModels.size(); i++) {
				FilterModel fModel = filterModels.getFilterModelAt(i);
				if ( fModel.getFilterType() == MsbFilterModel.DEFAULT ) {
					this.cmbFilter.setSelectedItem( fModel.getFilter() );
					this.cmbOperator.setSelectedItem( fModel.getOperator() );
					this.txtQuery.setText( fModel.getKeyword() );
	
					break;
				}
			}
		}else {
			this.cmbFilter.setSelectedIndex(0);
			this.cmbOperator.setSelectedIndex(0);
			this.txtQuery.setText("");
		}
	}
}
