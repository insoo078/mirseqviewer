package kobic.msb.swing.panel.newproject;

import javax.swing.JPanel;

import kobic.msb.swing.frame.dialog.JProjectDialog;

public abstract class CommonAbstractNewProjectPanel extends JPanel implements UpdateCurrentStateInterface{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JProjectDialog			owner;
	
	public CommonAbstractNewProjectPanel( JProjectDialog dialog ) {
		this.owner = dialog;
	}
	
	public JProjectDialog getOwnerDialog() {
		return this.owner;
	}
}
