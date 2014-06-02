package kobic.msb.swing.component;

import java.awt.Dialog;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import kobic.msb.common.ImageConstant;
import kobic.msb.server.model.ReadWithMatrix;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.frame.dialog.JDrawingConfigurationDialog;
import kobic.msb.swing.frame.dialog.JMsbReadManageDialog;

public class JMsbReadEditingPopupMenu extends JPopupMenu{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenuItem editReadMenuItem;
	private JMenuItem addReadMenuItem;
	private JMenuItem delReadMenuItem;
//	private JMenuItem copyReadMenuItem;
//	private JMenuItem pasteReadMenuItem;
//	private JMenuItem findReadMenuItem;
	private JMenuItem configMenuItem;
	private JMenuItem sortReadMenuItem;
	private JMenuItem sortRead3MenuItem;
	
	private JMenuItem showToolTipMenuItem;
	private JMenuItem showMatureBackgroundMenuItem;
	
	private int		currentReadPos;
	private int		currentReadField;
	
	private AlignmentDockingWindowObj dockWindow;
	
	private JMsbReadEditingPopupMenu remote = JMsbReadEditingPopupMenu.this; 

	public JMsbReadEditingPopupMenu( AbstractDockingWindowObj dockWindow, final int currentReadPos, final int currentReadField ) {
		this.currentReadPos = currentReadPos;
		this.currentReadField = currentReadField;

		this.dockWindow = (AlignmentDockingWindowObj) dockWindow;
		
		this.addReadMenuItem	= new JMenuItem("Add Custom Sequence");
		this.editReadMenuItem	= new JMenuItem("Edit");
//		this.findReadMenuItem	= new JMenuItem("Filter");
		this.delReadMenuItem	= new JMenuItem("Delete");
//		this.copyReadMenuItem	= new JMenuItem("Copy");
//		this.pasteReadMenuItem	= new JMenuItem("Paste");
		this.configMenuItem		= new JMenuItem("Properties");
		this.sortReadMenuItem	= new JMenuItem("Sort from 5'");
		this.sortRead3MenuItem	= new JMenuItem("Sort from 3'");

		if( this.dockWindow.isShowMatureBackground())	this.showMatureBackgroundMenuItem	= new JMenuItem("Hide mature background");
		else											this.showMatureBackgroundMenuItem	= new JMenuItem("Show mature background");
		if( this.dockWindow.isShowTooltip() )			this.showToolTipMenuItem			= new JMenuItem("Hide the Read tooltip");
		else											this.showToolTipMenuItem			= new JMenuItem("Show the Read tooltip");
		
		this.addReadMenuItem.setIcon( ImageConstant.addIcon );
		this.delReadMenuItem.setIcon( ImageConstant.trashIcon );
//		this.copyReadMenuItem.setIcon( ImageConstant.copyIcon );
//		this.pasteReadMenuItem.setIcon( ImageConstant.pasteIcon );
		this.sortReadMenuItem.setIcon(ImageConstant.sortAlnIcon24 );
		this.sortRead3MenuItem.setIcon(ImageConstant.sortAlnEndIcon24 );
//		this.findReadMenuItem.setIcon( ImageConstant.findIcon );
		this.editReadMenuItem.setIcon( ImageConstant.editorIcon );

		this.add( editReadMenuItem );
		this.add( new JSeparator() );
		this.add( addReadMenuItem );
		this.add( delReadMenuItem );
		this.add( new JSeparator() );
//		this.add( copyReadMenuItem );
//		this.add( pasteReadMenuItem );
//		this.add( new JSeparator() );
//		this.add( findReadMenuItem );
		this.add( sortReadMenuItem );
		this.add( sortRead3MenuItem );
		this.add( new JSeparator() );
		this.add( this.showToolTipMenuItem );
		this.add( this.showMatureBackgroundMenuItem );
		this.add( new JSeparator() );
		this.add( configMenuItem );

		this.addReadMenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JMsbReadManageDialog dialog = new JMsbReadManageDialog( remote.dockWindow.getMainFrame(), remote.dockWindow, remote.currentReadPos, remote.currentReadField, "Add read information", Dialog.ModalityType.APPLICATION_MODAL, JMsbReadManageDialog.ADD);
				dialog.setVisible(true);
			}
		});
		
//		this.copyReadMenuItem.addActionListener( new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated method stub
//				ReadWithMatrix target = remote.dockWindow.getModel().getReadVectorListByFiltered().get( remote.currentReadPos );
//
//				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//
//				MyObjectSelection dataSelection = new MyObjectSelection( target );
//				clipboard.setContents( dataSelection, remote.dockWindow );
//			}
//		});
//
//		this.pasteReadMenuItem.addActionListener( new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated method stub
//
//				ReadWithMatrix tmpObj = null;
//				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//			    //odd: the Object param of getContents is not currently used
//			    Transferable contents = clipboard.getContents( remote.dockWindow );
//			    DataFlavor[] flavors = contents.getTransferDataFlavors();
//
//			    try {
//					tmpObj = (ReadWithMatrix) contents.getTransferData( flavors[0] );
//				} catch (UnsupportedFlavorException e1) {
//					// TODO Auto-generated catch block
//					MsbEngine.logger.error( "error : ", e1 );
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					MsbEngine.logger.error( "error : ", e1 );
////					e1.printStackTrace();
//				}
//
//
//				if( tmpObj != null ) {
//					remote.dockWindow.getModel().addRead( tmpObj );
//
//					remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
//				}
//			}
//		});

		this.configMenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JDrawingConfigurationDialog dialog = new JDrawingConfigurationDialog( remote.dockWindow.getMainFrame(), remote.dockWindow, remote.dockWindow.getProjectName(), "Configuration", Dialog.ModalityType.APPLICATION_MODAL);
				dialog.setVisible(true);
			}
		});
		
		this.editReadMenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JMsbReadManageDialog dialog = new JMsbReadManageDialog( remote.dockWindow.getMainFrame(), remote.dockWindow, remote.currentReadPos, remote.currentReadField, "Edit read information", Dialog.ModalityType.APPLICATION_MODAL, JMsbReadManageDialog.MODIFY);
//				dialog.setChoosedReadPos( remote.currentReadPos );
				dialog.setVisible(true);
			}
		});
		
//		this.findReadMenuItem.addActionListener( new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated method stub
//				JMsbFindReadDailog dialog = new JMsbFindReadDailog( remote.dockWindow, "Filtering", Dialog.ModalityType.APPLICATION_MODAL);
//				dialog.setVisible(true);
//			}
//		});
		
		this.sortReadMenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				remote.dockWindow.getCurrentModel().sortReadPosition();
				remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
			}
		});

		this.sortRead3MenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				remote.dockWindow.getCurrentModel().sortReadEndPosition();
				remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
			}
		});
		
		this.delReadMenuItem.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				remote.dockWindow.getCurrentModel().removeRead( remote.currentReadPos );
				remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
			}
		});
		
		this.showToolTipMenuItem.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if( remote.dockWindow.isShowTooltip() )	remote.dockWindow.setShowTooltip( false );
				else									remote.dockWindow.setShowTooltip( true );

				remote.dockWindow.setIsMousePositionFixed( false );
				remote.dockWindow.getSsPanel().setMouseClicked( false );
				remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
			}
			
		});
		
		this.showMatureBackgroundMenuItem.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if( remote.dockWindow.isShowMatureBackground() )	remote.dockWindow.setShowMatureBackground( false );
				else												remote.dockWindow.setShowMatureBackground( true );

				remote.dockWindow.setIsMousePositionFixed( false );
				remote.dockWindow.getSsPanel().setMouseClicked( false );
				remote.dockWindow.setMirid( remote.dockWindow.getDefaultMirid() );
			}
			
		});
	}
	
	public static class MyObjectSelection implements Transferable, ClipboardOwner{
		private static DataFlavor dmselFlavor = new DataFlavor( ReadWithMatrix.class, "Test data flavor" );
		private ReadWithMatrix selection;
		
		public MyObjectSelection(ReadWithMatrix selection) {
			this.selection = selection;
		}

		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {
			// TODO Auto-generated method stub
			System.out.println("MyObjectSelection: Lost ownership");
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			// TODO Auto-generated method stub
			DataFlavor[] ret = {dmselFlavor};
			return ret;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			// TODO Auto-generated method stub
			return dmselFlavor.equals(flavor);
		}

		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			// TODO Auto-generated method stub
			if (isDataFlavorSupported(flavor)){
				return this.selection;
			} else {
				throw new UnsupportedFlavorException(dmselFlavor);
			}
		}
	}
}
