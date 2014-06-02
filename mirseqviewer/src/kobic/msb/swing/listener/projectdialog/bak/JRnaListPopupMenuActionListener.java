package kobic.msb.swing.listener.projectdialog.bak;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import kobic.msb.swing.panel.newproject.JNewProjectPanel;

public class JRnaListPopupMenuActionListener implements ActionListener {
	@SuppressWarnings("unused")
	private JNewProjectPanel newProjectPanel;
	
	public JRnaListPopupMenuActionListener( JNewProjectPanel dialog ) {
		this.newProjectPanel = dialog;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
//		// TODO Auto-generated method stub
//		String rnaId = ((JAutocompleteMenuItem)e.getSource()).getText().trim();
////		System.out.println( rnaId );
////		HairpinVO vo = remote.getRnaHairpinInfoAtTable( rnaId );
//		HairpinVO vo = MsbEngine._db.getMicroRnaHairpinByMirid2( rnaId );
//
//		this.newProjectPanel.setRnaIdTextField( vo.getId() );
//		this.newProjectPanel.setRnaAccessionNoTextField( vo.getAccession() );
//		this.newProjectPanel.setChromosomeTextField( vo.getChr() );
//		this.newProjectPanel.setLocationTextField( vo.getChr() + ":" + Utilities.getNumberWidthComma( vo.getStart() ) + "-" + Utilities.getNumberWidthComma( vo.getEnd() ) );
	}
}
