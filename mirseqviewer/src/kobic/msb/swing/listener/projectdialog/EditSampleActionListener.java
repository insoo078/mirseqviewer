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

public class EditSampleActionListener implements ActionListener {
	private JNewProjectPanel newProjectPanel;
	
	public EditSampleActionListener( JNewProjectPanel dialog) {
		this.newProjectPanel = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String group		= Utilities.nulltoEmpty( this.newProjectPanel.getCmbGroupSelect().getSelectedItem()	);
		String sampleName	= Utilities.nulltoEmpty( this.newProjectPanel.getTxtSampleName().getText()			);
		String samplePath	= Utilities.nulltoEmpty( this.newProjectPanel.getTxtSamplePath().getText()			);
		String sampleNumber	= Utilities.nulltoZero( this.newProjectPanel.getTxtOrder().getText() 					);

		if( sampleName.equals("") ){
			JOptionPane.showMessageDialog( this.newProjectPanel, "SampleName is empty!!");
			this.newProjectPanel.getTxtSampleName().requestFocus();
			return;
		}else if ( samplePath.equals("") ) {
			JOptionPane.showMessageDialog( this.newProjectPanel, "Sample file path is empty!!");
			this.newProjectPanel.getTxtSamplePath().requestFocus();
			return;
		}else if( group.equals("") ) {
			JOptionPane.showMessageDialog( this.newProjectPanel, "Group ID is empty!!");
			this.newProjectPanel.getCmbGroupSelect().requestFocus();
			return;
		}else if( sampleNumber.equals("") ) {
			JOptionPane.showMessageDialog( this.newProjectPanel, "Sample number is empty!! You should choice a sample at Table");
			this.newProjectPanel.getTblSampleList().requestFocus();
			return;
		}

		List<Group> groupList = this.newProjectPanel.getMsb().getProject().getSamples().getGroup();
		Sample sampleToModify = MsvUtilities.findTargetToModify( groupList, sampleNumber );
		sampleToModify.setName( sampleName );
		sampleToModify.setOrder( sampleNumber );
		sampleToModify.setSamplePath( samplePath );

		groupList = MsvUtilities.assignSampleToGroup( sampleToModify, group, groupList );

		this.newProjectPanel.refreshSampleTable( groupList );
		this.newProjectPanel.initSampleInfo();
	}
}
