package kobic.msb.swing.listener.projectdialog.bak;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import kobic.msb.swing.panel.newproject.bak.JNewProjectPanel;

public class MicroRnaAutocompleteKeyListener extends KeyAdapter {
	@SuppressWarnings("unused")
	private JNewProjectPanel remote;
	
	public MicroRnaAutocompleteKeyListener( JNewProjectPanel dialog ) {
		this.remote = dialog;
	}

	@Override
	public void keyTyped(KeyEvent e) {
//		// TODO Auto-generated method stub
//		String keyword = (remote.getTxtRnaId().getText().toLowerCase() + e.getKeyChar()).trim();
//
//		if( remote.getRnaListPopupMenu() != null)
//			remote.getRnaListPopupMenu().removeAll();
//
//		remote.setRnaListPopupMenu( new JScrollPopupMenu() );
//
//		remote.setRnaAccessionNoTextField("");
//		remote.setChromosomeTextField("");
//		remote.setLocationTextField("");
//
//		if( !keyword.equals("") ) {
//			int numOfChar = 8;
//			if( keyword.startsWith( "mir-") || keyword.startsWith( "let-") )
//				numOfChar = 4;
//
//			try {
//				if( keyword.length() > numOfChar ) {
//					@SuppressWarnings("static-access")
//					Iterator<String> iter = MsbEngine.getInstance()._MEMORY_HAIRPIN_LIST.iterator();
//					
//					while( iter.hasNext() ) {
//						String itr = iter.next();
//						if( itr.contains( keyword ) )	{
//							JAutocompleteMenuItem item = new JAutocompleteMenuItem( itr );
//							item.setKeyword( keyword );
//							item.setForeground( Color.blue );
//							item.setBackground( Color.white );
//							item.addActionListener( new JRnaListPopupMenuActionListener( remote ) );
//							remote.getRnaListPopupMenu().add( item );
//						}
//					}
//				}else {
//					remote.getRnaListPopupMenu().removeAll();
//				}
//				remote.getRnaListPopupMenu().revalidate();
//				remote.getRnaListPopupMenu().repaint();
//
//				remote.getRnaListPopupMenu().show( remote.getTxtRnaId(), 0, 0 + remote.getTxtRnaId().getHeight() );
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
////				e1.printStackTrace();
//				MsbEngine.logger.error( e1 );
//			}
//		}else {
//			remote.getRnaListPopupMenu().setVisible(false);
//		}
//		remote.getTxtRnaId().requestFocus();
	}
}
