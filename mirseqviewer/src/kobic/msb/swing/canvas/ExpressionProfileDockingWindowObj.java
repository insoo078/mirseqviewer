package kobic.msb.swing.canvas;

import java.awt.Graphics;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.GroupLayout.Alignment;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.View;
import kobic.msb.common.ImageConstant;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.panel.profile.JExpressionProfileControlPanel;
import kobic.msb.swing.panel.profile.JExpressionProfilePanel;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class ExpressionProfileDockingWindowObj extends AbstractDockingWindowObj {
	private JExpressionProfilePanel			expressionPanel;
	private JScrollPane						expressionScrollPane;
	private JExpressionProfileControlPanel	controlPanel;

	private ExpressionProfileDockingWindowObj remote = ExpressionProfileDockingWindowObj.this;

	public ExpressionProfileDockingWindowObj(JMsbBrowserMainFrame frame, String projectName) {
		super(frame, projectName);
		// TODO Auto-generated constructor stub
		
		ProjectMapItem projectMapItem = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.getProjectName() );
		
		this.expressionPanel = new JExpressionProfilePanel( projectMapItem );
		
		this.controlPanel		= new JExpressionProfileControlPanel( this, projectMapItem );
		this.expressionScrollPane = new JScrollPane();
		
		this.expressionScrollPane.setViewportView( this.expressionPanel );

		this.expressionScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		
		this.expressionScrollPane.getVerticalScrollBar().setUnitIncrement( 15 );

		this.addObserver( this.expressionPanel );

		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout( panel );
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(this.controlPanel,			Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 2048, Short.MAX_VALUE)
						.addComponent(this.expressionScrollPane,	Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 2048, Short.MAX_VALUE)
					)
				)
		);

		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(this.controlPanel,	GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
					.addComponent(expressionScrollPane, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
				)
		);
		panel.setLayout(groupLayout);
		
		View profileView = new View( this.getProjectName(), ImageConstant.projectIcon2, panel );
		profileView.addListener( new DockingWindowAdapter() {
			@Override
			public void windowClosed(DockingWindow arg0) {
				remote.getMainFrame().removeTabbedProject( remote.getProjectName() );
			}
		});
		
		this.setProjectRootView( profileView );
	}
	
	public void update() {
		this.setChanged();
		this.notifyObservers();
	} 

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return JMsbSysConst.EXPRESSION_VIEW;
	}
}