package kobic.msb.swing.component;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;

import kobic.msb.common.ImageConstant;
import kobic.msb.server.model.Model;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JMsbFilterDialog;
import kobic.msb.swing.frame.dialog.JMsbAboutDialog;
import kobic.msb.swing.frame.dialog.JMsbJTableSortDialog;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.swing.frame.dialog.JWorkspaceDialog;
import kobic.msb.swing.listener.menu.ImportProjectActionListener;
import kobic.msb.swing.listener.menu.PrintActionListener;
import kobic.msb.swing.listener.menu.ExportProjectActionListener;
import kobic.msb.swing.listener.menu.SaveProjectActionListener;
import kobic.msb.swing.listener.projecttreepanel.NewProjectActionListener;
import kobic.msb.swing.listener.projecttreepanel.QuickNewProjectActionListener;
import kobic.msb.system.SystemEnvironment;
import kobic.msb.system.engine.MsbEngine;

public class JMsbMenuBar extends JMenuBar{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenu					fileMenu;
	private JMenu					helpMenu;
	private JMenu					windowMenu;
	private JMenu					dataMenu;
	private JMsbBrowserMainFrame	frame;

	JMsbMenuBar remote = JMsbMenuBar.this;
	
	public JMsbMenuBar(JMsbBrowserMainFrame frame) {
		this.frame = frame;

		this.fileMenu	= this.createFileMenu();
		this.helpMenu	= this.createHelpMenu();
		this.dataMenu	= this.createDataMenu();
		this.windowMenu	= this.createWindowMenu();

		this.add( this.fileMenu );
		this.add( this.windowMenu );
		this.add( this.dataMenu );
		this.add( this.helpMenu );
	}
	
	public JMenu createDataMenu() {
		JMenu menu = new JMenu("Data");
		
		JMenuItem sortMenuItem		= new JMenuItem("Sort...");
		JMenuItem filterMenuItem	= new JMenuItem("Filter...");
		
		sortMenuItem.setIcon( ImageConstant.sortAscIcon );
		filterMenuItem.setIcon( ImageConstant.filterIcon );
		
		menu.add( sortMenuItem );
		menu.addSeparator();
		menu.add( filterMenuItem );
		
		sortMenuItem.addActionListener( new ActionListener() {

//			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				String projectName = remote.frame.getToolBar().getSelectedProject();
				AbstractDockingWindowObj dockWindow = remote.frame.getAbstractDockingWindowObj( projectName );

				if( dockWindow instanceof AlignmentDockingWindowObj ) {
					AlignmentDockingWindowObj nObj = (AlignmentDockingWindowObj)dockWindow;
					Model model = nObj.getCurrentModel();
				
					JMsbJTableSortDialog dialog = new JMsbJTableSortDialog(nObj, model, "Sort", Dialog.ModalityType.APPLICATION_MODAL);
					dialog.setVisible(true);
				}else {
					JOptionPane.showMessageDialog( remote.frame, "You have to run \"" + projectName + "\" project.", "Notice", JOptionPane.QUESTION_MESSAGE );
				}
			}
		});
		
		filterMenuItem.addActionListener( new ActionListener() {

//			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub

				String projectName = remote.frame.getToolBar().getSelectedProject();
				AbstractDockingWindowObj dockWindow = remote.frame.getAbstractDockingWindowObj( projectName );

				if( dockWindow instanceof AlignmentDockingWindowObj ) {
					AlignmentDockingWindowObj nObj = (AlignmentDockingWindowObj)dockWindow;
					Model model = nObj.getCurrentModel();
				
					JMsbFilterDialog dialog = new JMsbFilterDialog(nObj, model, "Filter", Dialog.ModalityType.APPLICATION_MODAL);
					dialog.setVisible(true);
				}else {
					JOptionPane.showMessageDialog( remote.frame, "You have to run \"" + projectName + "\" project.", "Notice", JOptionPane.QUESTION_MESSAGE );
				}
			}
			
		});
		
		return menu;
	}

	public JMenu createWindowMenu() {
		JMenu menu = new JMenu("Window");
		
		JMenuItem minimizeViewItem		= new JMenuItem("Minimize");
		JMenuItem zoomViewItem			= new JMenuItem("Zoom");
		JMenu showViewMenu				= new JMenu("Show View");
		JMenu projectViewMenu			= new JMenu("Project");
		JMenuItem summaryViewItem		= new JMenuItem("Summary report Window");
		JMenuItem pExplorerViewItem 	= new JMenuItem("Project Explorer Window");
		JMenuItem dashBoardViewItem 	= new JMenuItem("Dashboard Window");
		JMenuItem alignmentViewItem 	= new JMenuItem("Alignment Window");
		JMenuItem secondaryViewItem 	= new JMenuItem("Secondary Structure Window");
		JMenuItem readCountViewItem 	= new JMenuItem("Read Count Table Window");
		JMenuItem miRnaSummaryViewItem	= new JMenuItem("miRNA summary Window");
		
		showViewMenu.add( pExplorerViewItem );
		showViewMenu.add( dashBoardViewItem );
		showViewMenu.add( summaryViewItem );
		showViewMenu.add( projectViewMenu );
		projectViewMenu.add( alignmentViewItem );
		projectViewMenu.add( secondaryViewItem );
		projectViewMenu.add( readCountViewItem );
		projectViewMenu.add( miRnaSummaryViewItem );
		
		pExplorerViewItem.setIcon( ImageConstant.packageIcon );
		dashBoardViewItem.setIcon( ImageConstant.dashboardIcon );
		summaryViewItem.setIcon( ImageConstant.reportIcon );
		secondaryViewItem.setIcon( ImageConstant.secondaryStructureIcon );
		alignmentViewItem.setIcon( ImageConstant.alignmentIcon );
		readCountViewItem.setIcon( ImageConstant.tableIcon );
		miRnaSummaryViewItem.setIcon( ImageConstant.reportIcon );

		menu.add( minimizeViewItem );
		menu.add( zoomViewItem );
		menu.add( showViewMenu );
		
		minimizeViewItem.addActionListener( new ActionListener() {
//			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				remote.frame.setState( Frame.ICONIFIED );
			}
		});
		
		zoomViewItem.addActionListener( new ActionListener() {
//			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if( remote.frame.getExtendedState() != (remote.frame.getExtendedState() | JFrame.MAXIMIZED_BOTH) ) { 
					remote.frame.setExtendedState( remote.frame.getExtendedState() | JFrame.MAXIMIZED_BOTH );
				}else {
					remote.frame.setExtendedState( remote.frame.getExtendedState() & JFrame.NORMAL );
				}
			}
		});

		pExplorerViewItem.addActionListener( new ActionListener() {
//			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				View explorerView = remote.frame.getTreeView();
				if( explorerView.isDisplayable() == false || explorerView.isMinimized() ) {
					explorerView.restore();
				}
			}
		});

		dashBoardViewItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				View dashboardView = remote.frame.getDashboardView();
				if( dashboardView.isDisplayable() == false ) {
					TabWindow tw = (TabWindow)remote.frame.getTabWindow();
					tw.addTab( dashboardView );
					remote.frame.getContentRootView().setWindow( tw );
				}else if( dashboardView.isMinimized() ) {
					dashboardView.restore();
				}
			}
		});
		
		summaryViewItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				remote.frame.displaySummaryDockingWindow();
			}
		});

		alignmentViewItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DockingWindow dw = remote.frame.getTabWindow().getSelectedWindow();

				AbstractDockingWindowObj dwo = remote.getDockingWindowObj( dw );

				if( dwo instanceof AlignmentDockingWindowObj ) {
					AlignmentDockingWindowObj dwo2 = (AlignmentDockingWindowObj) dwo;
					View alignmentView = dwo2.getAlignmentDockingWindow();

					if( !alignmentView.isDisplayable() ) {
						alignmentView.restore();
					}
				}
			}
		});

		secondaryViewItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DockingWindow dw = remote.frame.getTabWindow().getSelectedWindow();

				AbstractDockingWindowObj dwo = remote.getDockingWindowObj( dw );

				if( dwo instanceof AlignmentDockingWindowObj ) {
					AlignmentDockingWindowObj dwo2 = (AlignmentDockingWindowObj) dwo;
					View sseView = dwo2.getSecondaryStructureDockingWindow();
	
					if( !sseView.isDisplayable() ) {
						sseView.restore();
					}
				}
			}
		});
		
		readCountViewItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DockingWindow dw = remote.frame.getTabWindow().getSelectedWindow();

				AbstractDockingWindowObj dwo = remote.getDockingWindowObj( dw );
				if( dwo instanceof AlignmentDockingWindowObj ) {
					AlignmentDockingWindowObj dwo2 = (AlignmentDockingWindowObj) dwo;
					View heatMapView = dwo2.getHeatmapDockingWindow();
	
					if( !heatMapView.isDisplayable() ) {
						heatMapView.restore();
					}
				}
			}
		});
		
		miRnaSummaryViewItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DockingWindow dw = remote.frame.getTabWindow().getSelectedWindow();

				AbstractDockingWindowObj dwo = remote.getDockingWindowObj( dw );
				if( dwo instanceof AlignmentDockingWindowObj ) {
					AlignmentDockingWindowObj dwo2 = (AlignmentDockingWindowObj) dwo;
					View summaryView = dwo2.getSummaryDockingWindow();
	
					if( !summaryView.isDisplayable() ) {
						summaryView.restore();
					}
				}
			}
			
		});

		return menu;
	}
	
	public JMenu createHelpMenu() {
		JMenu menu = new JMenu("About");
		JMenuItem helpMenuItem = new JMenuItem("About");
		
		menu.add( helpMenuItem );
		
		helpMenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JMsbAboutDialog helpDialog = new JMsbAboutDialog(remote.frame, Dialog.ModalityType.APPLICATION_MODAL);
				helpDialog.setVisible(true);
			}
			
		});
		
		return menu;
	}
	
	public JMenu createFileMenu() {
		
		JMenu menu = new JMenu("File");
		JMenu newMenu				= new JMenu("New");

		JMenuItem newProjectItem	= new JMenuItem("New miRseq Project");
		JMenuItem quickProjectItem	= new JMenuItem("Quick miRseq Project");
//		JMenuItem newSampleItem		= new JMenuItem("Sample");
//		JMenuItem newGroupItem		= new JMenuItem("Group");

		JMenuItem importMenuItem	= new JMenuItem("Import...");
		JMenuItem saveMenuItem		= new JMenuItem("Save");
		JMenuItem exportMenuItem	= new JMenuItem("Export...");
		JMenuItem printMenuItem		= new JMenuItem("Print");
		JMenuItem closeMenuItem		= new JMenuItem("Close");
		JMenuItem closeAllMenuItem	= new JMenuItem("Close All");
		JMenuItem workspaceMenuItem	= new JMenuItem("Switch Workspace");
		JMenuItem quitMenuItem		= new JMenuItem("Quit miRseqViewer");
		
		JMenuItem eachSmpMenuItem	= new JMenuItem("Each sample");
		JMenuItem multipleSmpMenuItem	= new JMenuItem("Multiple samples");
		
		JMenu preferencesMenu 		= new JMenu("Preferences");
		JMenu lnfMenu				= new JMenu("Look And Feel");
		JMenu screen				= new JMenu("Screen");
		preferencesMenu.add( lnfMenu );
		preferencesMenu.add( screen );

		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			JMenuItem menuItem = new JMenuItem( info.getName() );
			final String className = info.getClassName();
			menuItem.addActionListener( new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					try {
						UIManager.setLookAndFeel( className );
						
						SwingUtilities.updateComponentTreeUI( remote.frame );
						remote.frame.pack();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						MsbEngine.logger.error( "error : ", e1 );
					}
				}
			});
			lnfMenu.add( menuItem );
	    }
		
		screen.add( eachSmpMenuItem );
		screen.add( multipleSmpMenuItem );
		
		eachSmpMenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Map<String, String> map = new HashMap<String, String>();
				map.put("msv.create.new.project.screen",		JProjectDialog.EACH_SAMPLE_FILE_TO_PROJECT );

				/**********
				 * If you want to synchronize the workspace and miRseq browser base directory
				 * 
				 * Actually, the workspace is setted by user when miRseq browser is firstly launched
				 */

				SystemEnvironment.getPropertiesAfterStore( map );
			}
		});
		
		multipleSmpMenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Map<String, String> map = new HashMap<String, String>();
				map.put("msv.create.new.project.screen",		JProjectDialog.MULTIPLE_SAMPLE_FILES_TO_PROJECT );

				/**********
				 * If you want to synchronize the workspace and miRseq browser base directory
				 * 
				 * Actually, the workspace is setted by user when miRseq browser is firstly launched
				 */

				SystemEnvironment.getPropertiesAfterStore( map );
			}
		});
		

		newProjectItem.setIcon( ImageConstant.newDocIcon );
		quickProjectItem.setIcon( ImageConstant.quickDocIcon );
		importMenuItem.setIcon( ImageConstant.importIcon );
		saveMenuItem.setIcon( ImageConstant.saveIcon );
		exportMenuItem.setIcon( ImageConstant.exportIcon );
		printMenuItem.setIcon( ImageConstant.printIcon );
		
		newMenu.add( newProjectItem );
		newMenu.add( quickProjectItem );
//		newMenu.add( newSampleItem );
//		newMenu.add( newGroupItem );
		
		menu.add( newMenu );
		menu.add( saveMenuItem );
		menu.addSeparator();
		menu.add( closeMenuItem );
		menu.add( closeAllMenuItem );
		menu.addSeparator();
		menu.add( importMenuItem );
		menu.add( exportMenuItem );
		menu.addSeparator();
		menu.add( workspaceMenuItem );
		menu.addSeparator();
		menu.add( printMenuItem );
		menu.addSeparator();
		menu.add( preferencesMenu );
		menu.addSeparator();
		menu.add( quitMenuItem );
		
		newProjectItem.addActionListener( new NewProjectActionListener(this.frame) );
		quickProjectItem.addActionListener( new QuickNewProjectActionListener( this.frame ) );
		
		importMenuItem.addActionListener( new ImportProjectActionListener(this.frame) );
		
		quitMenuItem.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JMsbMenuBar remote = JMsbMenuBar.this;
				remote.frame.windowsClose();
			}
			
		});
		
		workspaceMenuItem.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JMsbMenuBar remote = JMsbMenuBar.this;
				// TODO Auto-generated method stub
				JWorkspaceDialog dialog = new JWorkspaceDialog(remote.frame, "Workspace Launcher", Dialog.ModalityType.APPLICATION_MODAL);
				dialog.setVisible(true);
			}
			
		});

		exportMenuItem.addActionListener( new ExportProjectActionListener( this.frame ) );

		saveMenuItem.addActionListener( new SaveProjectActionListener(this.frame) );
		
		closeMenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String projectName = remote.frame.getTabWindow().getSelectedWindow().getTitle();

				remote.frame.removeTabbedProject( projectName );
				remote.frame.getTabWindow().getSelectedWindow().close();
			}
		});
		
		closeAllMenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				do{
					DockingWindow dock = remote.frame.getContentRootView().getWindow();
					for(int i=0; i<dock.getChildWindowCount(); i++) {
						DockingWindow dock2 = dock.getChildWindow(i);
						dock2.close();
					}
				}while( remote.frame.getContentRootView().getWindow() != null );

//				remote.frame.getDockingWindowObjMap().clear();
				remote.frame.removeAllAbstractDockingWindowObj();
			}
		});

		printMenuItem.addActionListener( new PrintActionListener( this.frame ) );
		
		return menu;
	}

	public AbstractDockingWindowObj getDockingWindowObj( DockingWindow dw ) {
		String projectName = "";

		if( dw instanceof SplitWindow )		projectName = dw.getWindowProperties().getTitleProvider().getTitle( dw );
		else if( dw instanceof View )		projectName = dw.getTitle();

//		AbstractDockingWindowObj dwo = remote.frame.getDockingWindowObjMap().get( projectName );
		AbstractDockingWindowObj dwo	= remote.frame.getAbstractDockingWindowObj(projectName);
		
		return dwo;
	}
}
