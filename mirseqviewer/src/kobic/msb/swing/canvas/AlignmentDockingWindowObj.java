package kobic.msb.swing.canvas;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.swing.BoundedRangeModel;
import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.filechooser.FileFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import kobic.com.util.Utilities;
import kobic.msb.common.ImageConstant;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.Model;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.infonode.ProjectNameDockingWindowTitleProvider;
import kobic.msb.swing.panel.alignment.JHeatMapPanel;
import kobic.msb.swing.panel.alignment.JMsbBrowserControlPane;
import kobic.msb.swing.panel.alignment.JSecondaryStructurePanel;
import kobic.msb.swing.panel.alignment.JSequenceAlignmentPanel;
import kobic.msb.swing.panel.alignment.JSequenceHistogramPanel;
import kobic.msb.swing.panel.summary.JMsbProjectMiRnaReportPanel;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.View;
import net.infonode.docking.properties.TabWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.title.DockingWindowTitleProvider;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;

public class AlignmentDockingWindowObj extends AbstractDockingWindowObj {
	private DockingWindow				dockWindow;
	private DockingWindow				horizontalSplitWindow;
	private JSecondaryStructurePanel	ssPanel;
	private JSequenceAlignmentPanel		alignmentPane;
	private JHeatMapPanel				heatmapPane;
	private JMsbBrowserControlPane		controlPane;
	private JSequenceHistogramPanel		histogramPane;
	
	private JMsbProjectMiRnaReportPanel	reportPane;

	private boolean						isMousePosFixed;

	private Model						currentModel;
	
	private float						scrollPercentage;

	private int							selectedFieldPos;
	private int							selectedRecordPos;

	private int							_SCROLL_INDEX = 0;
	
	private String						defaultMirid;
	
	private JScrollPane					alignmentScrollPane;
	private JScrollPane					histogramScrollPane;

	private View						alignmentScrollView;
	private View						secondaryStructureView;
	private View						heatmapView;
	private View						summaryView;
	
	private View						alignmentRootView;

	private boolean						isShowTooltip;
	private boolean						isShowMatureBackground;
	
	private DockingWindowTitleProvider	dwtp;
	
	private final AlignmentDockingWindowObj		remote = AlignmentDockingWindowObj.this;
	
//	private ReadWithMatrix				copyTemporaryData;
	
	public AlignmentDockingWindowObj(JMsbBrowserMainFrame frame, String projectName) {
		super(frame, projectName);

//		System.out.println("AlignmentDockingWindowObj creation");
		this.scrollPercentage	= 0;
		
//		this.copyTemporaryData	= null;
		this.isMousePosFixed			= false;
		this.isShowTooltip				= true;
		this.isShowMatureBackground		= true;

		List<String> miridList = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName ).getMiRnaList();

//		System.out.println( "Progressbar = " + frame.getStatusBar().getProgressBar() );
		this.defaultMirid	= miridList.get(0);
		this.currentModel	= MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.getProjectName() ).getProjectModel( this.defaultMirid, frame.getStatusBar().getProgressBar() );
		this.currentModel.initReferenceGenomeSequenceAndSomthing();

		this.ssPanel		= new JSecondaryStructurePanel	(	this,	this.getProjectName(), defaultMirid );
		this.alignmentPane	= new JSequenceAlignmentPanel	(	this,	this.getProjectName(), defaultMirid );
		this.histogramPane	= new JSequenceHistogramPanel	(	this,	this.getProjectName(), defaultMirid );
		this.heatmapPane	= new JHeatMapPanel				(	this,	this.getProjectName(), defaultMirid );
		this.controlPane	= new JMsbBrowserControlPane	(	this,	this.getProjectName(), defaultMirid );
		this.reportPane		= new JMsbProjectMiRnaReportPanel(	this,	this.getProjectName(), defaultMirid );

		this.addObserver( this.ssPanel );
		this.addObserver( this.alignmentPane );
		this.addObserver( this.histogramPane );
		this.addObserver( this.heatmapPane );
		this.addObserver( this.controlPane );
		this.addObserver( this.reportPane );
		

		this.dwtp = new ProjectNameDockingWindowTitleProvider( this.getProjectName() );
		// TODO Auto-generated constructor stub
		
		javax.swing.JPanel panel = new javax.swing.JPanel();
		
		this.alignmentScrollPane = new JScrollPane();
		
		this.histogramScrollPane = new JScrollPane();
		
		this.alignmentScrollPane.setViewportView( this.alignmentPane );
		this.histogramScrollPane.setViewportView( this.histogramPane );

		this.alignmentScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		this.histogramScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		this.histogramScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		
		this.createHorizontalScrollBarModel( alignmentScrollPane, histogramScrollPane );
		this.createVerticalScrollBarModel( alignmentScrollPane, this.heatmapPane.getScrollPane() );

		GroupLayout groupLayout = new GroupLayout( panel );
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
//					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(this.controlPane,		Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 2048, Short.MAX_VALUE)
						.addComponent(histogramScrollPane,	Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 2048, Short.MAX_VALUE)
						.addComponent(alignmentScrollPane,	Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 2048, Short.MAX_VALUE)
					)
//					.addGap(0)
				)
		);
		ProjectConfiguration config = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.getProjectName() ).getProjectConfiguration();

		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
//					.addContainerGap()
					.addComponent(this.controlPane, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
//					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(histogramScrollPane, GroupLayout.PREFERRED_SIZE, (int)(this.histogramPane.getTotalTracksHeight() + config.getMargin()) + 10, GroupLayout.PREFERRED_SIZE)
//					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(alignmentScrollPane,	GroupLayout.DEFAULT_SIZE, 4096, Short.MAX_VALUE)
//					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//					.addContainerGap()
				)
		);

		panel.setLayout(groupLayout);
	
		this.alignmentScrollView	= new View("Alignment",							ImageConstant.alignmentIcon,			panel );
		this.secondaryStructureView	= new View("miRNA Secondary Structure",			ImageConstant.secondaryStructureIcon,	this.ssPanel );
		this.heatmapView			= new View("Read Count Table",					ImageConstant.tableIcon,				this.heatmapPane );
		this.summaryView			= new View(JMsbSysConst.MIRNA_REPORT_TITLE, 	ImageConstant.projectIcon2,				this.reportPane );
		
		this.alignmentScrollView.setName( projectName );
		this.secondaryStructureView.setName( projectName );
		this.heatmapView.setName( projectName );
		this.summaryView.setName( projectName );

		DockingWindow vsw = new SplitWindow( false, 0.9f, this.alignmentScrollView, this.summaryView );
		this.horizontalSplitWindow	= new SplitWindow( false, 0.2f, this.secondaryStructureView, this.heatmapView );
		this.dockWindow				= new SplitWindow( true, 0.7f, vsw, this.horizontalSplitWindow );

		this.dockWindow.getWindowProperties().setTitleProvider( this.dwtp );
		this.horizontalSplitWindow.getWindowProperties().setTitleProvider( this.dwtp );

		this.alignmentRootView = new View( this.getProjectName(), ImageConstant.projectIcon2, this.createRootWindow( this.dockWindow ) );
		this.setProjectRootView( this.alignmentRootView );
		this.alignmentRootView.addListener( new DockingWindowAdapter() {
			@Override
			public void windowClosed(DockingWindow arg0) {
				remote.getMainFrame().removeTabbedProject( remote.getProjectName() );
			}
			@Override
			public void windowClosing(DockingWindow window) throws OperationAbortedException {
				if (JOptionPane.showConfirmDialog( remote.getMainFrame(), "Really close project window '" + window + "'?") != JOptionPane.YES_OPTION) 
					throw new OperationAbortedException("Window close was aborted!");
			}
			@Override
			public void windowShown(DockingWindow window){
				String projectName = window.getTitle();
				
				remote.getMainFrame().getToolBar().setSelectProjectName(projectName);
			}
		});
	}

	private void createVerticalScrollBarModel( JScrollPane source, JScrollPane target ) {
		final JScrollBar alignmentScrollBar = source.getVerticalScrollBar();
		final JScrollBar heatmapScrollBar	= target.getVerticalScrollBar();
		
		source.getVerticalScrollBar().setUnitIncrement( 15 );
		target.getVerticalScrollBar().setUnitIncrement( 15 );
		
		alignmentScrollBar.addAdjustmentListener(new AdjustmentListener() {
		    @Override
		    public void adjustmentValueChanged(AdjustmentEvent e) {
		    	if( remote._SCROLL_INDEX != 2 ) {
		    		remote._SCROLL_INDEX = 1;
		    		remote.scrollingVertical( alignmentScrollBar.getModel(), heatmapScrollBar );
		    		remote._SCROLL_INDEX = 0;
		    	}
		    }
		});
		
		heatmapScrollBar.addAdjustmentListener(new AdjustmentListener() {

		    @Override
		    public void adjustmentValueChanged(AdjustmentEvent e) {
		    	if( remote._SCROLL_INDEX != 1 ) {
		    		remote._SCROLL_INDEX = 2;
		    		remote.scrollingVertical( heatmapScrollBar.getModel(), alignmentScrollBar );
		    		remote._SCROLL_INDEX = 0;
		    	}
		    }
		});
	}

	private void scrollingVertical( BoundedRangeModel aModel, JScrollBar target ) {
		int A_value		= aModel.getValue();
		int A_knobSize	= aModel.getExtent();
		int A_max		= aModel.getMaximum();
		
		this.scrollPercentage = (float)A_value / (A_max - A_knobSize);
		
    	int B_knobSize	= target.getModel().getExtent();
		int B_max		= target.getModel().getMaximum();

		target.setValue( (int)(remote.scrollPercentage * (B_max - B_knobSize)) );
	}
	
	
	private void createHorizontalScrollBarModel( JScrollPane source, JScrollPane target ) {
		final JScrollBar alignmentScrollBar = source.getHorizontalScrollBar();
		final JScrollBar histogramScrollBar = target.getHorizontalScrollBar();
		
		alignmentScrollBar.setModel( histogramScrollBar.getModel() );
	}

	public JSecondaryStructurePanel getSsPanel() {
		return ssPanel;
	}

	public void setSsPanel(JSecondaryStructurePanel ssPanel) {
		this.ssPanel = ssPanel;
	}
	
	public JSequenceHistogramPanel getHistogramPanel() {
		return this.histogramPane;
	}

	public JSequenceAlignmentPanel getAlignmentPane() {
		return alignmentPane;
	}

	public void setAlignmentPane(JSequenceAlignmentPanel alignmentPane) {
		this.alignmentPane = alignmentPane;
	}

	public JHeatMapPanel getHeatmapPane() {
		return heatmapPane;
	}

	public void setHeatmapPane(JHeatMapPanel heatmapPane) {
		this.heatmapPane = heatmapPane;
	}
	
	public String getDefaultMirid() {
		return this.defaultMirid;
	}

	public void setMirid( String mirid ) {
		if( !this.defaultMirid.equals(mirid) ) {
			this.defaultMirid = mirid;
			
			this.currentModel = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.getProjectName() ).getProjectModel( mirid, this.getMainFrame().getStatusBar().getProgressBar() );

			this.currentModel.initReferenceGenomeSequenceAndSomthing();

			this.getMainFrame().getToolBar().updateFilters( this.currentModel.getProjectMapItem().getMsbFilterModel() );
		}
		this.setChanged();
		this.notifyObservers( this.currentModel );
	}
	
	public void setModel( Model model ) {
		this.setChanged();
		this.notifyObservers( model );
	}
	
	public void setSelectedFieldPos( int pos ) {
		this.selectedFieldPos = pos;
		
		this.setChanged();
		this.notifyObservers( pos );
	}
	
	public void setSelectedRecordPos( int pos ) {
		this.selectedRecordPos  = pos;
	}
	
	public int getSelectedFieldPos() {
		return this.selectedFieldPos;
	}
	
	public int getSelectedRecordPos() {
		return this.selectedRecordPos;
	}

	public void setIsMousePositionFixed( boolean flag ) {
		this.isMousePosFixed = flag;
	}
	
	public boolean getIsMousePositionFixed() {
		return this.isMousePosFixed;
	}

	public JScrollPane getAlignmentScrollPane() {
		return alignmentScrollPane;
	}

	public void setAlignmentScrollPane(JScrollPane alignmentScrollPane) {
		this.alignmentScrollPane = alignmentScrollPane;
	}

	public JScrollPane getHistogramScrollPane() {
		return histogramScrollPane;
	}

	public void setHistogramScrollPane(JScrollPane histogramScrollPane) {
		this.histogramScrollPane = histogramScrollPane;
	}
	
	public JMsbBrowserControlPane getAlignmentControlPane() {
		return this.controlPane;
	}

//	public Model getModel() {
////		return MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.getProjectName() ).getProjectModel( this.defaultMirid );
//		return this.currentModel;
//	}

	public View getAlignmentDockingWindow() {
		return this.alignmentScrollView;
	}
	
	public View getSecondaryStructureDockingWindow() {
		return this.secondaryStructureView;
	}
	
	public View getHeatmapDockingWindow() {
		return this.heatmapView;
	}
	
	public View getSummaryDockingWindow() {
		return this.summaryView;
	}
	
	public Model getCurrentModel() {
		return this.currentModel;
	}

	private RootWindow createRootWindow( DockingWindow dock ) {
		ViewMap viewMap = new ViewMap();

		RootWindow rootWindow = DockingUtil.createRootWindow( viewMap, true );
		
		DockingWindowsTheme theme = new ShapedGradientDockingTheme(); 
		rootWindow.getRootWindowProperties().addSuperObject(theme.getRootWindowProperties());

		TabWindowProperties tabWindowProperties = rootWindow.getRootWindowProperties().getTabWindowProperties();
		
		tabWindowProperties.getCloseButtonProperties().setVisible(false);

		rootWindow.getWindowBar( Direction.DOWN ).setEnabled(true);
		rootWindow.setWindow( dock );
		
		return rootWindow;
	}
	
	public void print() {
		PrinterJob printJob = PrinterJob.getPrinterJob();

		PrintService service = printJob.getPrintService();
		if( service != null ) {
			PageFormat pf = printJob.defaultPage();
			pf.setOrientation( PageFormat.LANDSCAPE );
			printJob.setPrintable(this, pf);
			printJob.pageDialog( pf );
	
			if (printJob.printDialog()) {
				try {
					printJob.print();
				} catch(PrinterException pe) {
					MsbEngine.logger.error("Error", pe);
				}
			}
		}else {
//			int result = JOptionPane.showConfirmDialog( this.getMainFrame(), "<html>Your computer is not connected with printer system.<br>Do you want to print to file?</html>", "Print", JOptionPane.YES_NO_OPTION );
//			
//			if( result == JOptionPane.YES_OPTION ) {
//				JFileChooser chooser = new JFileChooser();
//				chooser.setDialogType( JFileChooser.SAVE_DIALOG );
//				chooser.setAcceptAllFileFilterUsed(false);
//
//				chooser.addChoosableFileFilter( new FileFilter() {
//					@Override
//					public boolean accept(File f) {
//						if (f.isDirectory()) {
//				            return true;
//				        }
//				 
//				        String extension = Utilities.getExtension(f);
//				        if (extension != null) {
//				        	if( extension.toUpperCase().equals("PDF") ) {
//				                    return true;
//				            } else {
//				                return false;
//				            }
//				        }
//				 
//				        return false;
//					}
//
//					@Override
//					public String getDescription() {
//						// TODO Auto-generated method stub
//						return "PDF format file";
//					}
//
//				} );
//
//				int returnVal = chooser.showOpenDialog( this.getMainFrame() );
//				if ( returnVal == JFileChooser.APPROVE_OPTION ) {
//	                try {
//	                	String filename = chooser.getSelectedFile().getAbsolutePath();
//	                	if( !filename.toUpperCase().endsWith(".PDF") )	filename += ".pdf";
//
//	                	PDDocument doc = new PDDocument();
//	                    PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
//	                    page.setRotation(90);
//                        doc.addPage(page);
//                        
//                        PDRectangle pageSize = page.findMediaBox();
//                        float width = pageSize.getWidth();
//                        float height = pageSize.getHeight();
//
//                        PDPageContentStream content = new PDPageContentStream(doc, page);
//                        BufferedImage image = new BufferedImage( (int)width*2,(int)height*2, BufferedImage.TYPE_4BYTE_ABGR ); 
//
//                        Graphics2D g = image.createGraphics();
//                        
//                        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//                        g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
//                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//                        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//                        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//
//
//                        this.print(g, image);
//                        PDXObjectImage ximage = new PDPixelMap(doc, image);
//
//                        content.drawXObject( ximage, 0, 0, width, height );
//                        content.close();
//
//                        doc.save( filename );
//                        
//                        doc.close();
//	                }catch(Exception e) {
//	                	MsbEngine.logger.error("Error", e);
//	                }
//				}
//			}
		}
	}
	
	public void print( Graphics graphics, BufferedImage image ) throws PrinterException {
		Graphics2D g2d = (Graphics2D)graphics;
		g2d.translate( image.getMinX(), image.getMinY() );

		Dimension dim = this.getProjectRootView().getSize();

		//Scale the component to fit    
		//Calculate the scale factor to fit the window to the page.
		double scaleX = image.getWidth() / dim.getWidth();
		double scaleY = image.getHeight() / dim.getHeight();
		double scale = Math.min( scaleX, scaleY );

		g2d.scale( scale, scale );

		SwingUtilities.disableDoubleBuffering( this.getProjectRootView() );
		this.getProjectRootView().paint( g2d );
		SwingUtilities.enableDoubleBuffering( this.getProjectRootView() );
		
		graphics.dispose();
	}


	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if (pageIndex > 0) {
			return(NO_SUCH_PAGE);
		} else {
			Graphics2D g2d = (Graphics2D)graphics;			
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

			Dimension dim = this.getProjectRootView().getSize();

			//Scale the component to fit    
			//Calculate the scale factor to fit the window to the page.
			double scaleX = pageFormat.getImageableWidth() / dim.getWidth();
			double scaleY = pageFormat.getImageableHeight() / dim.getHeight();
			double scale = Math.min( scaleX, scaleY );

			g2d.scale( scale, scale );

			SwingUtilities.disableDoubleBuffering( this.getProjectRootView() );
			this.getProjectRootView().paint( g2d );
			SwingUtilities.enableDoubleBuffering( this.getProjectRootView() );
			return(PAGE_EXISTS);
		}
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return JMsbSysConst.ALIGNMENT_VIEW;
	}

	public boolean isShowTooltip() {
		return isShowTooltip;
	}

	public void setShowTooltip(boolean isShowTooltip) {
		this.isShowTooltip = isShowTooltip;
	}

	public boolean isShowMatureBackground() {
		return isShowMatureBackground;
	}

	public void setShowMatureBackground(boolean isShowMatureBackground) {
		this.isShowMatureBackground = isShowMatureBackground;
	}
}
