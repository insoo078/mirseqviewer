package kobic.msb.swing.frame;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.Application;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.DockingWindowListener;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.properties.DockingWindowProperties;
import net.infonode.docking.properties.TabWindowProperties;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;
import kobic.msb.common.ImageConstant;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.SwingConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.canvas.ExpressionProfileDockingWindowObj;
import kobic.msb.swing.component.JMsbMenuBar;
import kobic.msb.swing.component.JMsbToolBar;
import kobic.msb.swing.component.JMsbStatusBar;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.swing.frame.dialog.JWorkspaceDialog;
import kobic.msb.swing.infonode.ProjectNameDockingWindowTitleProvider;
import kobic.msb.swing.panel.mainframe.JMsbHtmlScrollPane;
import kobic.msb.swing.panel.mainframe.JMsbProjectTreePanel;
import kobic.msb.swing.panel.summary.JMsbProjectReportPanel;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class JMsbBrowserMainFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/********************************************************************
	 * Initial layout
	 * 	Left : Tree Menu Panel (projectTreeView)
	 *  Center : Main Content Panel (contentRootWindow);
	 */
	private JMenuBar								menuBar;
	private JMsbStatusBar							statusBar;
	private JMsbToolBar								toolBar;
	
	private RootWindow 								contentRootWindow;			// #1 window
	private DockingWindow							mainDockingSplitWindow;		// #2 window
	private JMsbProjectTreePanel					projectTreePanel;			// #3 left window

	private JMsbHtmlScrollPane						htmlScrollPane;
	private JScrollPane								alignmentScrollPane;

	private RootWindow								mainRootWindow;
	private TabWindow								tabWindow;
	private View									projectTreeView;
	private View									contentView;
	private View									dashboardView;
	private View									infoView;
	
	private SplitWindow								reportSplit;

	private float									WINDOW_SCALE_RATIO;
	
	private RootWindow								summaryRootWindow;
	private TabWindow								summaryTabWindow;

	private Map<String, AbstractDockingWindowObj>	currentOpenedDockWindowDocMap;
	
	Application macApplication;

	private final JMsbBrowserMainFrame remote = JMsbBrowserMainFrame.this;

	public JMsbBrowserMainFrame() throws MalformedURLException, IOException {
		super();
		
		this.setIconImage( ImageConstant.msb_icon48.getImage() );
		// 1. System Configuration (L&F, Size and so on)
		this.initSystemConfiguration();

		this.currentOpenedDockWindowDocMap	= new HashMap<String, AbstractDockingWindowObj>();

		long a = System.currentTimeMillis();
		this.projectTreePanel				= new JMsbProjectTreePanel( this );	// Left TreePanel
		this.menuBar						= new JMsbMenuBar( this );			// Top Menubar
		this.statusBar						= new JMsbStatusBar();				// Bottom Statubar
		this.toolBar						= new JMsbToolBar( this );			// Top Toolbar
		long b = System.currentTimeMillis();
		
		MsbEngine.logger.debug("new instance of menu, panels, toolbar and so on : " + ((float)(b-a)/1000) + "sec.");

		// To register Observers
//		MsbEngine.getInstance().getProjectManager().getProjectMap().addObserver( this.projectTreePanel );
//		MsbEngine.getInstance().getProjectManager().getProjectMap().addObserver( this.toolBar );

		this.setJMenuBar( this.menuBar );

		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		Container content = this.getContentPane();
		content.add( this.toolBar,		BorderLayout.PAGE_START );
		content.add( this.statusBar,	BorderLayout.PAGE_END );

		this.projectTreeView		= new View( "Project Explorer",	ImageConstant.packageIcon, this.projectTreePanel );

		
		long c = System.currentTimeMillis();
		ViewMap viewMap = new ViewMap();
		this.htmlScrollPane = new JMsbHtmlScrollPane( this );
		long d = System.currentTimeMillis();
		MsbEngine.logger.debug("htmlScrollPanel : " + ((float)(d-c)/1000) + "sec.");

		this.dashboardView = new View("Dashboard", ImageConstant.dashboardIcon, this.htmlScrollPane );
//		this.dashboardView = new View("Dashboard", ImageConstant.dashboardIcon, new javax.swing.JPanel() );
//		this.dashboardView = new View("Welcome", ImageConstant.dashboardIcon, new JMsbWelcomePanel() );

//		JRViewer jrview = new JMsbProjectReport().build();
		
		this.summaryRootWindow = SwingUtilities.getThemedDockingRootWindow( DockingUtil.createRootWindow( viewMap, true ) );

		this.summaryTabWindow = new TabWindow();
		this.summaryRootWindow.setWindow( this.summaryTabWindow );
		this.infoView		= new View("Summary report", ImageConstant.reportIcon, this.summaryRootWindow );

		this.tabWindow = new TabWindow( this.dashboardView );
		
		this.tabWindow.addListener( new DockingWindowAdapter() {
			@Override
			public void viewFocusChanged(View arg0, View arg1) {
				// TODO Auto-generated method stub
				String tabTitle = remote.tabWindow.getSelectedWindow().getTitle();

				int index = -1;
				for(int i=0; i<remote.summaryTabWindow.getChildWindowCount(); i++) {
					View view = (View) summaryTabWindow.getChildWindow(i);
					String title = view.getTitle();
					if( title.equals(tabTitle) )	index = i;
				}
				
				remote.summaryTabWindow.setSelectedTab(index);
			}
		});
		
		this.summaryTabWindow.addListener( new DockingWindowAdapter() {
			@Override
			public void viewFocusChanged(View arg0, View arg1) {
				// TODO Auto-generated method stub
				String tabTitle = remote.summaryTabWindow.getSelectedWindow().getTitle();

				int index = -1;
				for(int i=0; i<remote.tabWindow.getChildWindowCount(); i++) {
					View view = (View) tabWindow.getChildWindow(i);
					String title = view.getTitle();
					if( title.equals(tabTitle) )	index = i;
				}
				
				remote.tabWindow.setSelectedTab(index);
			}
		});

		this.contentRootWindow = DockingUtil.createRootWindow( viewMap, true );

		this.contentRootWindow.setWindow( this.tabWindow );

		this.contentRootWindow.getRootWindowProperties().addSuperObject( SwingConst.MAIN_INFONODE_THEME.getRootWindowProperties() );

		DockingWindowProperties dwp = new DockingWindowProperties();
		dwp.setTitleProvider( new ProjectNameDockingWindowTitleProvider( "Content" ) );
		dwp.setCloseEnabled( false );
		
		this.contentView = new View( "Content", ImageConstant.mainTabIcon, contentRootWindow );

		this.contentView.getWindowProperties().addSuperObject( dwp );

		TabWindowProperties tabWindowProperties = this.contentRootWindow.getRootWindowProperties().getTabWindowProperties();
		tabWindowProperties.getCloseButtonProperties().setVisible(false);

		this.mainRootWindow = DockingUtil.createRootWindow( viewMap, true );

//		this.reportSplit = new SplitWindow(false, 1f, this.contentView, this.infoView);
		this.reportSplit = new SplitWindow(false, 1f, this.projectTreeView, this.infoView);

//		this.mainDockingSplitWindow = new SplitWindow(true, 0.12f, projectTreeView, this.reportSplit );
		this.mainDockingSplitWindow = new SplitWindow(true, 0.12f, this.reportSplit, this.contentView );
		this.mainRootWindow.setWindow( this.mainDockingSplitWindow );

		this.mainRootWindow.getRootWindowProperties().addSuperObject( SwingConst.MAIN_INFONODE_THEME.getRootWindowProperties() );

		this.mainRootWindow.getWindowBar( Direction.DOWN ).setEnabled(true);

		this.mainRootWindow.getRootWindowProperties().getViewProperties().getViewTitleBarProperties().setVisible(false);

		content.add( this.mainRootWindow, BorderLayout.CENTER );
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if( MsbEngine.getInstance().getSystemProperties().getProperty( "msb.project.workspace") == null || MsbEngine.getInstance().getSystemProperties().getProperty( "msb.project.workspace").isEmpty() ) {
					JWorkspaceDialog dialog = new JWorkspaceDialog(remote, "Workspace Launcher", Dialog.ModalityType.APPLICATION_MODAL);
					dialog.setVisible(true);
				}
			}
		});

		this.addWindowListener( new WindowAdapter() {
			@Override
		    public void windowClosing(WindowEvent windowEvent) {
				super.windowClosing( windowEvent );

				remote.windowsClose();
			}
		});

		/********************************************************************************************
		 * Browser window size and location
		 */
		Dimension dim = kobic.msb.common.util.SwingUtilities.getScreenSize();
		int defaultWindowWidth	= (int)(dim.getWidth() * this.WINDOW_SCALE_RATIO);
		int defaultWindowHeight	= (int)(dim.getHeight() * this.WINDOW_SCALE_RATIO);

		Point2D.Double mainWindowPosition = SwingUtilities.getDrawingPosition( dim, defaultWindowWidth, defaultWindowHeight );
		this.setBounds( (int)mainWindowPosition.getX(), (int)mainWindowPosition.getY(), defaultWindowWidth, defaultWindowHeight);
		this.setPreferredSize( new Dimension( defaultWindowWidth, defaultWindowHeight ) );
		this.pack();
	}

	private void initSystemConfiguration() {
		/****************************
		 * To Set System LAF(Look And Feel)
		 */
		this.initSystemLookAndFeel();
		this.setTitle( MsbEngine.getInstance().getSystemProperties().getProperty( "msb.window.title" ) );
		this.WINDOW_SCALE_RATIO = Float.parseFloat( MsbEngine.getInstance().getSystemProperties().getProperty("msb.window.scale") );
	}

	private void initSystemLookAndFeel() {
		try {
			if( System.getProperty("os.name").startsWith( "Mac") ) {
				System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
				System.setProperty("apple.laf.useScreenMenuBar", "true");
	        	System.setProperty("com.apple.mrj.application.apple.menu.about.name", "miRseq Viewer");
	        	
	        	this.macApplication = Application.getApplication();

	        	this.macApplication.setQuitHandler( new QuitHandler(){

					@Override
					public void handleQuitRequestWith(QuitEvent arg0, QuitResponse arg1) {
						// TODO Auto-generated method stub
						remote.windowsClose();
					}
                });
			}

        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

//			UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( "error : ", e );
			try {
//				UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
				UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				MsbEngine.logger.error( "error : ", e1 );
			}
		}
	}

	private AbstractDockingWindowObj newProjectViewWindow( String projectName, int viewType ) {
		if( viewType == JMsbSysConst.EXPRESSION_VIEW )
			return new ExpressionProfileDockingWindowObj( remote, projectName );
//		else if( viewType == JMsbSysConst.ALIGNMENT_VIEW )
		return new AlignmentDockingWindowObj( remote, projectName );
	}

	public JMsbProjectTreePanel getTreePanel() {
		return this.projectTreePanel;
	}

	public JScrollPane getContentScrollPane() {
		return this.alignmentScrollPane;
	}
	
	public JMsbStatusBar getStatusBar() {
		return this.statusBar;
	}
	
	public JMsbToolBar getToolBar() {
		return this.toolBar;
	}
	
	public View getDashboardView() {
		return this.dashboardView;
	}
	
	public View getTreeView() {
		return this.projectTreeView;
	}
	
	public TabWindow getTabWindow() {
		return this.tabWindow;
	}
	
	public RootWindow getRootDockingWindow() {
		return this.mainRootWindow;
	}

	public JMsbHtmlScrollPane getHtmlScrollPane() {
		return this.htmlScrollPane;
	}
	
	public AbstractDockingWindowObj getAbstractDockingWindowObj(String projectName) {
		return this.currentOpenedDockWindowDocMap.get(projectName);
	}

	public void removeAllAbstractDockingWindowObj() {
		this.currentOpenedDockWindowDocMap.clear();
	}

	public void windowsClose(){
//		int confirmed = JOptionPane.showConfirmDialog( remote, "Are you sure you want to exit?", "User Confirmation", JOptionPane.YES_NO_OPTION, ImageConstant.msb_icon );
		int confirmed = JOptionPane.showConfirmDialog( remote, "Are you sure you want to exit?", "User Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, ImageConstant.msb_icon48 );

		if (confirmed == JOptionPane.YES_OPTION) {
			WindowListener[] wl = this.getListeners(WindowListener.class);
			for(int i=0; i<wl.length; i++) {
				this.removeWindowListener(wl[i]);  
			}

			try {
				if( MsbEngine.getInstance() != null )		MsbEngine.getInstance().close();
			}catch(Exception e) {
				MsbEngine.logger.error("Error", e);
			}finally{
				System.gc();
				System.exit(0);
			}
		}
	}

	public void removeTabbedProject( String projectName ) {
		// remove tab from alignment view panel
		for(int i=0; i<tabWindow.getChildWindowCount(); i++) {
			View view = (View) tabWindow.getChildWindow(i);
			String title = view.getTitle();
			if( title.equals(projectName) )	view.close();
		}
		
		// remove tab from summary view panel
		for(int i=0; i<this.summaryTabWindow.getChildWindowCount(); i++) {
			View view = (View) summaryTabWindow.getChildWindow(i);
			String title = view.getTitle();
//			if( title.equals( JMsbSysConst.MIRNA_REPORT_TITLE ) )	view.close();
			if( title.equals(projectName) )	view.close();
		}

		this.currentOpenedDockWindowDocMap.remove( projectName );
	}
	
	public void removeAllTabbedProject() {
		this.currentOpenedDockWindowDocMap.clear();
	}

	public RootWindow getContentRootView() {
		return this.contentRootWindow;
	}

	public void addProjectViewToTabWindow( final String projectName, int viewType ) throws Exception{
		if( this.contentRootWindow.getWindow() == null ) {
			this.contentRootWindow.setWindow( this.tabWindow );
		}

		if( this.currentOpenedDockWindowDocMap.containsKey( projectName) ) {
			AbstractDockingWindowObj dwo = this.currentOpenedDockWindowDocMap.get( projectName );

			if( dwo.getProjectRootView().isDisplayable() )	dwo.getProjectRootView().restoreFocus();
			else											dwo.getProjectRootView().restore();
		}else {
			ProjectMapItem projectMapItem = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );

			if( viewType == JMsbSysConst.EXPRESSION_VIEW ) {
				if( projectMapItem.getClusterModel() != null ) {
					JOptionPane.showMessageDialog( this, "Sorry! You did not load miRNA(s)", "Error", JOptionPane.ERROR_MESSAGE );
					return;
				}
			}else if( viewType == JMsbSysConst.ALIGNMENT_VIEW ){
				List<String> miridList = projectMapItem.getMiRnaList();
				if( !(miridList != null && miridList.size() > 0) )	{
					JOptionPane.showMessageDialog( this, "Sorry! You did not choose miRNA(s)", "Error", JOptionPane.ERROR_MESSAGE );
					
					String nType = MsbEngine.getInstance().getSystemProperties().getProperty("msv.create.new.project.screen");
					JProjectDialog dialog = new JProjectDialog( this, "Edit Project", true, Dialog.ModalityType.APPLICATION_MODAL, true, nType);
					dialog.updateCurrentState(projectMapItem);
					dialog.setVisible(true);

					return;
				}
			}

			AbstractDockingWindowObj dwo = this.newProjectViewWindow( projectName, viewType );		// To add project panel
			this.currentOpenedDockWindowDocMap.put( projectName, dwo );

			this.tabWindow.addTab( dwo.getProjectRootView() );

//			this.updateReportPanel( projectName );
			this.addSummaryReport(projectName);
		}
	}

	public void displaySummaryDockingWindow() {
		if( !this.infoView.isDisplayable() ){
			this.infoView.restore();

//			this.reportSplit = new SplitWindow(false, 1f, this.contentView, this.infoView);
			this.reportSplit = new SplitWindow(false, 1f, this.projectTreeView, this.infoView);

//			this.mainDockingSplitWindow = new SplitWindow(true, 0.2f, projectTreeView, this.reportSplit );
			this.mainDockingSplitWindow = new SplitWindow(true, 0.2f, this.reportSplit, this.contentView );
			this.mainRootWindow.setWindow( this.mainDockingSplitWindow );
		}
	}

	public void addProjectViewToTabWindow( final String projectName ) throws Exception{
		this.addProjectViewToTabWindow( projectName, JMsbSysConst.ALIGNMENT_VIEW );
	}

	private boolean isTabbedSummary(final String projectName) {
		boolean result = false;
		for(int i=0; i<this.summaryTabWindow.getChildWindowCount(); i++) {
			View view = (View) summaryTabWindow.getChildWindow(i);
			String title = view.getTitle();
			if( title.equals(projectName) )	return true;
		}
		return result;
	}

	public void addSummaryReport( final String projectName ) {
		if( !this.isTabbedSummary(projectName) ) {
			this.displaySummaryDockingWindow();
	
			JMsbProjectReportPanel reportPane			= new JMsbProjectReportPanel( this, projectName );
	
			View projectReportView = new View( projectName, ImageConstant.projectIcon2, reportPane );
			
//			MsbEngine.logger.info("summaryTabWindow isDisplayable : " + this.summaryTabWindow.isDisplayable() );
	
			this.summaryTabWindow.addTab( projectReportView );
	
			this.summaryRootWindow.setWindow( this.summaryTabWindow );
			this.infoView.setComponent( this.summaryRootWindow );

			this.reportSplit.setDividerLocation(0.5f);
			
			if( this.mainDockingSplitWindow instanceof SplitWindow ) {
				SplitWindow sWindow = (SplitWindow)this.mainDockingSplitWindow;
				sWindow.setDividerLocation( 0.2f );
			}
		}
	}

//	public void updateReportPanel( final String projectName ) {
//		if( this.infoView.isDisplayable() ){
//			ProjectNameDockingWindowTitleProvider dwtp = new ProjectNameDockingWindowTitleProvider( projectName + " Summary report" );
//			this.displaySummaryDockingWindow();
//	
//			this.infoView.getWindowProperties().setTitleProvider( dwtp );
//			
//			JMsbProjectReportPanel reportPane			= new JMsbProjectReportPanel( this, projectName );
//
//			View projectReportView = new View( "Report", ImageConstant.projectIcon2, reportPane );
//
//			this.summaryTabWindow = new TabWindow();
//			this.summaryTabWindow.addTab( projectReportView );
//
//			this.summaryRootWindow.setWindow( this.summaryTabWindow );
//			this.infoView.setComponent( this.summaryRootWindow );
//	
//			this.reportSplit.setDividerLocation(0.5f);
//		}
//	}
}
