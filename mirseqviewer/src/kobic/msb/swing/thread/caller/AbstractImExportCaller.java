package kobic.msb.swing.thread.caller;

import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JMsbProgressDialog;

public abstract class AbstractImExportCaller {
	private JMsbBrowserMainFrame		frame;
	private JMsbProgressDialog			dialog;
	
	public AbstractImExportCaller(JMsbBrowserMainFrame frame, JMsbProgressDialog dialog) {
		this.frame = frame;
		this.dialog = dialog;
		
		if( !this.dialog.isVisible() )	this.dialog.setVisible(true);
	}

	public JMsbBrowserMainFrame getFrame() {
		return frame;
	}

	public void setFrame(JMsbBrowserMainFrame frame) {
		this.frame = frame;
	}

	public JMsbProgressDialog getDialog() {
		return dialog;
	}

	public void setDialog(JMsbProgressDialog dialog) {
		this.dialog = dialog;
	}
	
	public abstract void run();
	public abstract boolean isDone();
}
