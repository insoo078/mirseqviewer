package kobic.msb.swing.canvas;

import java.awt.Graphics;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;

import net.infonode.docking.DockingWindow;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.panel.summary.JMsbProjectMiRnaReportPanel;
import kobic.msb.swing.panel.summary.JMsbProjectReportPanel;

@SuppressWarnings("unused")
public class SummaryDockingWindowObj  extends AbstractDockingWindowObj{
	private JMsbProjectReportPanel		reportPane;
	private JMsbProjectMiRnaReportPanel	mirnaReportPane;
	private DockingWindow				dockWindow;
	
	private DockingWindow				rootView;
	
	private DockingWindow				projectReportView;
	private DockingWindow				mirnaReportView;
	
	private SummaryDockingWindowObj		remote = SummaryDockingWindowObj.this;

	public SummaryDockingWindowObj( JMsbBrowserMainFrame frame, String projectName ) {
		super(frame, projectName);
//		// TODO Auto-generated constructor stub
//		this.reportPane			= new JMsbProjectReportPanel( this, projectName );
//		this.mirnaReportPane	= new JMsbProjectMiRnaReportPanel( this, projectName );
//
//		this.projectReportView	= new View( "Report",				ImageConstant.projectIcon2,	this.reportPane );
//		this.mirnaReportView	= new View( "Report of miRNA",	ImageConstant.projectIcon2,	this.mirnaReportPane );
//		TabWindow tab = new TabWindow();
//		tab.addTab( this.projectReportView );
////		tab.addTab( this.mirnaReportView );
//
//		tab.setSelectedTab(0);
//
//		this.dockWindow		= tab;
//
//		this.rootView = this.createRootWindow( this.dockWindow );
//
//		this.setProjectRootView( rootView );
//
//		rootView.addListener( new DockingWindowAdapter() {
//			@Override
//			public void windowClosed(DockingWindow arg0) {
//				remote.getMainFrame().removeTabbedProject( remote.getProjectName() );
//			}
//		});
	}

	@Override
	public int print(Graphics arg0, PageFormat arg1, int arg2)
			throws PrinterException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// TODO Auto-generated method stub
	}
//
//	private RootWindow createRootWindow( DockingWindow dock ) {
//		ViewMap viewMap = new ViewMap();
//
//		RootWindow rootWindow = DockingUtil.createRootWindow( viewMap, true );
//
//		DockingWindowsTheme theme = new ShapedGradientDockingTheme(); 
//		rootWindow.getRootWindowProperties().addSuperObject(theme.getRootWindowProperties());
//
//		TabWindowProperties tabWindowProperties = rootWindow.getRootWindowProperties().getTabWindowProperties();
//		
//		tabWindowProperties.getCloseButtonProperties().setVisible(false);
//
//		rootWindow.getWindowBar( Direction.DOWN ).setEnabled(true);
//		rootWindow.setWindow( dock );
//		
//		return rootWindow;
//	}
//
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return JMsbSysConst.SUMMARY_VIEW;
	}
//	
//	public JMsbProjectMiRnaReportPanel getJMsbProjectMiRnaReportPanel() {
//		return this.mirnaReportPane;
//	}
//	
//	public DockingWindow getJMsbProjectMiRnaReportView() {
//		return this.mirnaReportView;
//	}
//	
//	public DockingWindow getJMsbProjectReportView() {
//		return this.projectReportView;
//	}
}
