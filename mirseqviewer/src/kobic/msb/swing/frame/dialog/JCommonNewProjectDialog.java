package kobic.msb.swing.frame.dialog;

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.panel.newproject.JMsbMatureChoosePanel;

public abstract class JCommonNewProjectDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMsbBrowserMainFrame		frame;
	private JButton						nextButton;
	private boolean						isEditDialog;
	
	public JCommonNewProjectDialog( Frame owner, String title, boolean isEdit, Dialog.ModalityType modalType, String value, boolean isEditDialog ) {
		super( owner, title, modalType );

		this.frame			= (JMsbBrowserMainFrame)owner;
		this.nextButton 	= new JButton( value );
		this.isEditDialog	= isEditDialog;
	}
	
	public JButton getNextButton() {
		return this.nextButton;
	}
	
	@Override
	public JMsbBrowserMainFrame			getOwner()						{	return this.frame;						}
	
	
	
	public boolean isEditDialog() {
		return isEditDialog;
	}

	public abstract JMsbMatureChoosePanel	getMirnaChoosePanel();
	public abstract JTextField				getTxtProjectName();
	public abstract JTextArea				getTextScrollPane();
//	public abstract JProgressBar			getProgressBar();
	public abstract void					setProgressToGetMiRnas( int value );
	public abstract void					setFocusProjectName();
	public abstract void					setIndeterminate(boolean value);
	public abstract boolean 				isIndeterminate();
	public abstract void					setProgressBarRange(final int a, final int b);
	public abstract int						getProgressToGetMiRnas();
}
