package kobic.msb.swing.listener.projectdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import kobic.com.util.Utilities;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.swing.panel.newproject.JMsvGroupControlPanel;
import kobic.msb.swing.panel.newproject.JNewProjectPanel;

public class DelGroupActionListener implements ActionListener {
private JMsvGroupControlPanel remote;
	
	public DelGroupActionListener( JMsvGroupControlPanel frame ) {
		this.remote = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String currentItem = Utilities.nulltoEmpty( remote.getCmbMngGroup().getSelectedItem() );
		
		Group dTargetGroup = null;
		List<Group> groupList = remote.getMsb().getProject().getSamples().getGroup();
		Iterator<Group> iterGroup = groupList.iterator();
		while( iterGroup.hasNext() ) {
			Group grp = iterGroup.next();
			if( grp.getGroupId().equals( currentItem ) ) {
				if( grp.getSample().size() > 0 ) {
					int confirmed = JOptionPane.showConfirmDialog( remote, "There are some samples in your choosed group?", "User Confirmation", JOptionPane.YES_NO_OPTION);
					if (confirmed != JOptionPane.YES_OPTION) {
						return;
					}else {
						dTargetGroup = grp;
						break;
					}
				}else {
					dTargetGroup = grp;
					break;
				}
			}
		}
		
		if( dTargetGroup != null )	{
//			remote.nSamples -= dTargetGroup.getSample().size();

			groupList.remove( dTargetGroup );
		}
		remote.initGroupInfo();
		
		if( remote instanceof JNewProjectPanel ) {
			JNewProjectPanel jnpp = (JNewProjectPanel)this.remote;
			jnpp.initSampleInfo();
		}

		remote.getCmbMngGroup().removeItem( currentItem );
		
		if( remote instanceof JNewProjectPanel ) {
			JNewProjectPanel jnpp = (JNewProjectPanel)this.remote;
			jnpp.getCmbGroupSelect().removeItem( currentItem );
		}

		remote.getCmbMngGroup().requestFocus();
		
		remote.refreshSampleTable( groupList );
	}

}
