package kobic.msb.swing.component;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import kobic.msb.server.model.Model;
import kobic.msb.server.model.MsbSortModel;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JMsbJTableSortDialog;
import kobic.msb.system.engine.MsbEngine;

public class JMsbSortPopupMenu extends JPopupMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JMenuItem					ascendingMenuItem;
	private JMenuItem					descendingMenuItem;
	private JMenuItem					customSortMenuItem;
	
	private Model						model;
	@SuppressWarnings("unused")
	private JMsbBrowserMainFrame		frame;
	
	private AlignmentDockingWindowObj	dockWindow;
	
	private JMsbSortPopupMenu			remote = JMsbSortPopupMenu.this;
	
	public JMsbSortPopupMenu( AlignmentDockingWindowObj dockWindow, Model model ) {
		this.frame		= dockWindow.getMainFrame();
		this.model		= model;
		this.dockWindow	= dockWindow;

		this.ascendingMenuItem	= new JMenuItem("Ascending");
		this.descendingMenuItem	= new JMenuItem("Descending");
		this.customSortMenuItem	= new JMenuItem("Custom Sort...");
		
		this.add( this.ascendingMenuItem );
		this.add( this.descendingMenuItem );
		this.addSeparator();
		this.add( this.customSortMenuItem );
		
		this.customSortMenuItem.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JMsbJTableSortDialog dialog = new JMsbJTableSortDialog(remote.dockWindow, remote.model, "Sort", Dialog.ModalityType.APPLICATION_MODAL);
				dialog.setVisible(true);
			}
		});
		
		this.ascendingMenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MsbSortModel sModel = remote.model.getProjectMapItem().getMsbSortModel();
				sModel.setAllSortModelAscending();
				
				try {
					remote.dockWindow.getCurrentModel().sortBySortModel( sModel );
					remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
				}catch(Exception exp) {
					MsbEngine.logger.error( "Error : ", exp );
				}
			}
		});
		
		this.descendingMenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				MsbSortModel sModel = remote.model.getProjectMapItem().getMsbSortModel();
				sModel.setAllSortModelDescending();
				
				try {
					remote.dockWindow.getCurrentModel().sortBySortModel( sModel );
					remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
				}catch(Exception exp) {
					MsbEngine.logger.error( "Error : ", exp );
				}
			}
		});
	}
}
