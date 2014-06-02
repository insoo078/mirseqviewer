package kobic.msb.swing.frame.splash;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BorderLayout borderLayout1 = new BorderLayout();
	private JLabel imageLabel = new JLabel();
	private JPanel southPanel = new JPanel();
	private FlowLayout southPanelFlowLayout = new FlowLayout();
	private JProgressBar progressBar = new JProgressBar();
	private ImageIcon imageIcon;

	public SplashScreen(ImageIcon imageIcon) {
		this.imageIcon = imageIcon;
		try {
			jbInit();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	// note - this class created with JBuilder
	void jbInit() throws Exception {
		imageLabel.setIcon(imageIcon);
		this.getContentPane().setLayout(borderLayout1);
		southPanel.setLayout(southPanelFlowLayout);
		southPanel.setBackground(Color.BLACK);
		this.getContentPane().add(imageLabel, BorderLayout.CENTER);
		this.getContentPane().add(southPanel, BorderLayout.SOUTH);
		southPanel.add(progressBar, null);
		this.pack();
	}

	public void setProgressMax(int maxProgress){
		progressBar.setMaximum(maxProgress);
	}

	public void setProgress(int progress) {
		final int theProgress = progress;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressBar.setValue(theProgress);
			}
		});
	}

	public void setProgress(String message, int progress) {
		final int theProgress = progress;
		final String theMessage = message;
		setProgress(progress);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressBar.setValue(theProgress);
				setMessage(theMessage);
			}
		});
	}

	public void setScreenVisible(boolean b) {
		final boolean boo = b;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setVisible(boo);
			}
		});
	}
	
	public JProgressBar getProgressBar() {
		return this.progressBar;
	}

	private void setMessage(String message) {
		if (message==null){
			message = "";
			progressBar.setStringPainted(false);
		} else {
			progressBar.setStringPainted(true);
		}
		progressBar.setString(message);
	}
}