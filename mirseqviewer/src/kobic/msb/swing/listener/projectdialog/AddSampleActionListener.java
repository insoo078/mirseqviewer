package kobic.msb.swing.listener.projectdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import kobic.com.picard.PicardUtilities;
import kobic.com.util.Utilities;
import kobic.msb.common.util.MsvUtilities;
import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.panel.newproject.JNewProjectPanel;

public class AddSampleActionListener implements ActionListener {
	private JNewProjectPanel newProjectPanel;
	
	public AddSampleActionListener( JNewProjectPanel dialog) {
		this.newProjectPanel = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String group		= Utilities.nulltoEmpty( this.newProjectPanel.getCmbGroupSelect().getSelectedItem()	);
		String sampleName	= Utilities.nulltoEmpty( this.newProjectPanel.getTxtSampleName().getText()			);
		String samplePath	= Utilities.nulltoEmpty( this.newProjectPanel.getTxtSamplePath().getText()			);
		
		List<Group> groupList = this.newProjectPanel.getMsb().getProject().getSamples().getGroup();
		if( MsvUtilities.isExistSampleName( groupList, sampleName ) ) {
			JOptionPane.showMessageDialog( this.newProjectPanel, "Your sample name is already exist!!");
			this.newProjectPanel.getTxtSampleName().setText("");
			this.newProjectPanel.getTxtSampleName().requestFocus();
			return;
		}

		File sampleFile = new File( samplePath );
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
		}else if( !sampleFile.exists() ) {
			JOptionPane.showMessageDialog( this.newProjectPanel, "There is not the bam file!!");
			this.newProjectPanel.getTxtSampleName().requestFocus();
			return;
		}

		this.newProjectPanel.getOwnerDialog().getNextButton().setEnabled( true );

		Msb.Project.Samples.Group.Sample smp = new Msb.Project.Samples.Group.Sample();
		smp.setName( sampleName );
		smp.setSamplePath( samplePath );
		smp.setOrder( Integer.toString( this.newProjectPanel.getNumberOfSample() + 1 ) );

		AddSampleActionListener.doSampleIndex( samplePath, smp );

		Iterator<Group> iter = this.newProjectPanel.getMsb().getProject().getSamples().getGroup().iterator();
		while( iter.hasNext() ) {
			Group grp = iter.next();
			if( grp.getGroupId().equals( group ) ) {
				grp.getSample().add( smp );
			}
		}

		this.newProjectPanel.refreshSampleTable( groupList );
		this.newProjectPanel.getTblSampleList().revalidate();
		
		this.newProjectPanel.initSampleInfo();
		
		this.newProjectPanel.setNumberOfSample( this.newProjectPanel.getNumberOfSample() + 1 );
	}

	public static void doSampleIndex( String samplePath, Sample smp ) {
		File sampleFile = new File( samplePath );
		if( PicardUtilities.isSorted( samplePath ) ) {
			File srcIdxFile	= new File( sampleFile.getParent() + File.separator + sampleFile.getName() + ".bai" );
			if( srcIdxFile.exists() && PicardUtilities.usableIndex( samplePath, srcIdxFile.getAbsolutePath() ) ) {
				smp.setIndexPath( srcIdxFile.getAbsolutePath() );
				smp.setSortedPath( samplePath );
			}else {
				smp.setSortedPath( samplePath );
			}
		}
	}
}
