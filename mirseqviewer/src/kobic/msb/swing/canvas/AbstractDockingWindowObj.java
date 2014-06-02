package kobic.msb.swing.canvas;

import java.awt.datatransfer.ClipboardOwner;
import java.awt.print.Printable;

import java.util.Observable;

import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import net.infonode.docking.DockingWindow;

public abstract class AbstractDockingWindowObj extends Observable implements Printable, ClipboardOwner{
	private DockingWindow				rootView;

	private String						projectName;

	private JMsbBrowserMainFrame		frame;

	@SuppressWarnings("unused")
	private final AbstractDockingWindowObj		remote = AbstractDockingWindowObj.this;
	
	public AbstractDockingWindowObj( JMsbBrowserMainFrame frame, String projectName ) {
		this.frame				= frame;
		this.projectName		= projectName;
	}
	
	public String getProjectName() {
		return this.projectName;
	}

	public DockingWindow getProjectRootView() {
		return this.rootView;
	}
	
	public void setProjectRootView(DockingWindow rootView) {
		this.rootView = rootView;
	}
	
	public JMsbBrowserMainFrame getMainFrame() {
		return this.frame;
	}
	
	public abstract int getType();
}
