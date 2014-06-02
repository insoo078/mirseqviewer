package kobic.msb.swing.listener.projectdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

import kobic.com.util.Utilities;
import kobic.msb.common.util.MsvUtilities;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.panel.newproject.JNewProjectPanel;

public class DelSampleActionListener implements ActionListener {
	private JNewProjectPanel newProjectPanel;
	
	public DelSampleActionListener( JNewProjectPanel dialog) {
		this.newProjectPanel = dialog;
	}

	@SuppressWarnings("unused")
	@Override
	public void actionPerformed(ActionEvent e) {
		String sampleNumber	= Utilities.nulltoZero( this.newProjectPanel.getTxtOrder().getText() );
//		remote.nSamples--;

		if( sampleNumber != null && !sampleNumber.equals("") ) {
//			int NUMBER = Integer.parseInt( sampleNumber );
//			DefaultTableModel newModel = new DefaultTableModel(null, col);
			List<Group> groupList = this.newProjectPanel.getMsb().getProject().getSamples().getGroup();
			
			// To find and delete a sample
			Sample deletedSample = MsvUtilities.findTargetToModify( groupList, sampleNumber );

			this.newProjectPanel.refreshSampleTable( groupList );
			this.newProjectPanel.initSampleInfo();
		}else {
			JOptionPane.showMessageDialog( this.newProjectPanel, "Yur can not delete the sample, Please choose it");
			return;
		}
	}
}
