package kobic.msb.swing.listener.projecttreepanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import kobic.msb.swing.panel.mainframe.JMsbProjectTreePanel;

public class RefreshActionListener implements ActionListener {
	private JMsbProjectTreePanel panel;

	public RefreshActionListener( JMsbProjectTreePanel panel ) {
		this.panel = panel;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		this.panel.refreshProjectTree();
	}
}
