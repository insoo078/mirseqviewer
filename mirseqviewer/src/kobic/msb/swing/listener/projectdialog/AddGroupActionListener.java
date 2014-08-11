package kobic.msb.swing.listener.projectdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import kobic.com.util.Utilities;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.swing.comparator.GroupComparable;
import kobic.msb.swing.panel.newproject.JMsvGroupControlPanel;
import kobic.msb.swing.panel.newproject.JNewProjectPanel;

public class AddGroupActionListener implements ActionListener {
	private JMsvGroupControlPanel remote;
	
	public AddGroupActionListener( JMsvGroupControlPanel frame ) {
		this.remote = frame;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object obj = Utilities.nulltoEmpty( remote.getCmbMngGroup().getSelectedItem() );

		List<Group> groupList = remote.getMsb().getProject().getSamples().getGroup();
		Iterator<Group> tmpIter = groupList.iterator();
		while( tmpIter.hasNext() ) {
			Group grp = tmpIter.next();
			if( grp.getGroupId().equals( obj ) ) {
				JOptionPane.showMessageDialog( remote, "Your group id is already exist!!");
				remote.getCmbMngGroup().setSelectedItem("");
				remote.getCmbMngGroup().requestFocus();
				return;
			}
		}

		if( obj.toString().equals("Total") ) {
			JOptionPane.showMessageDialog( remote, "Total is reserved keyword!!");
			remote.getCmbMngGroup().requestFocus();
			return;
		}else if( obj.toString().equals("") ) {
			JOptionPane.showMessageDialog( remote, "Group ID is empty!!");
			remote.getCmbMngGroup().requestFocus();
			return;
		}else {
			Group group = new Group();
			group.setGroupId( remote.getCmbMngGroup().getSelectedItem().toString() );

			groupList.add( group );
			Collections.sort( groupList, new GroupComparable() );
			Iterator<Group> iter = groupList.iterator();
			remote.getCmbMngGroup().removeAllItems();
			if( remote instanceof JNewProjectPanel ) {
				JNewProjectPanel jnpp = (JNewProjectPanel)remote;
				jnpp.getCmbGroupSelect().removeAllItems();
			}

			while( iter.hasNext() ) {
				Group grp = iter.next();
				remote.getCmbMngGroup().addItem( grp.getGroupId() );
				if( remote instanceof JNewProjectPanel ) {
					JNewProjectPanel jnpp = (JNewProjectPanel)remote;
					jnpp.getCmbGroupSelect().addItem( grp.getGroupId() );
				}
			}
			remote.initGroupInfo();

			remote.getCmbMngGroup().revalidate();
			remote.getCmbMngGroup().repaint();
			if( remote instanceof JNewProjectPanel ) {
				JNewProjectPanel jnpp = (JNewProjectPanel)remote;
				jnpp.getCmbGroupSelect().revalidate();
				jnpp.getCmbGroupSelect().repaint();
			}
		}
		remote.getCmbMngGroup().requestFocus();
	}

}
