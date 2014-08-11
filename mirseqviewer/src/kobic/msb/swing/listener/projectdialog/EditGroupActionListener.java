package kobic.msb.swing.listener.projectdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.swing.comparator.GroupComparable;
import kobic.msb.swing.panel.newproject.JMsvGroupControlPanel;
import kobic.msb.swing.panel.newproject.JNewProjectPanel;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class EditGroupActionListener implements ActionListener {
	private JMsvGroupControlPanel	newProjectPanel;
	
	public EditGroupActionListener( JMsvGroupControlPanel frame ) {
		this.newProjectPanel = frame;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		String selectedGroup	= this.newProjectPanel.getCmbMngGroup().getSelectedItem().toString();
		
		String projectName = this.newProjectPanel.getOwnerDialog().getTxtProjectName().getText();

		if( projectName.equals("") ) {
			JOptionPane.showMessageDialog( this.newProjectPanel, "You must input a project name");
			this.newProjectPanel.getOwnerDialog().getTxtProjectName().requestFocus();
			return;
		}else if( selectedGroup.equals("") ) {
			
		}else {
			ImageIcon icon = new ImageIcon("images/middle.gif");
			String newGroup = (String)JOptionPane.showInputDialog(
					newProjectPanel,
					"Input a new group name to change",
					"New Group name Dialog",
					JOptionPane.PLAIN_MESSAGE,
					icon,
					null,
					selectedGroup
					);
			if ( newGroup != null && !newGroup.equals("") ) {
				ProjectMapItem pi = MsbEngine.engine.getProjectManager().getProjectMap().getProject( projectName );
				if( pi != null ) {
					List<Group> groupList = MsbEngine.engine.getProjectManager().getProjectMap().getProject( projectName ).getProjectInfo().getSamples().getGroup();
					for( Group group : groupList ) {
						if( group.getGroupId().equals( this.newProjectPanel.getChoosedGroupNameToEdit() ) ) {
							group.setGroupId( newGroup );
						}
					}

					Collections.sort( groupList, new GroupComparable() );
					Iterator<Group> iter = groupList.iterator();
					this.newProjectPanel.getCmbMngGroup().removeAllItems();
					if( this.newProjectPanel instanceof JNewProjectPanel ) {
						JNewProjectPanel jnpp = (JNewProjectPanel)this.newProjectPanel;
						jnpp.getCmbGroupSelect().removeAllItems();
					}
	
					while( iter.hasNext() ) {
						Group grp = iter.next();
						this.newProjectPanel.getCmbMngGroup().addItem( grp.getGroupId() );
						
						if( this.newProjectPanel instanceof JNewProjectPanel ) {
							JNewProjectPanel jnpp = (JNewProjectPanel)this.newProjectPanel;
							jnpp.getCmbGroupSelect().addItem( grp.getGroupId() );
						}
					}
					this.newProjectPanel.initGroupInfo();
					this.newProjectPanel.getCmbMngGroup().requestFocus();
					this.newProjectPanel.refreshSampleTable( groupList );
				}else {
					List<Group> groupList = this.newProjectPanel.getMsb().getProject().getSamples().getGroup();

					for( Group group : groupList ) {
						if( group.getGroupId().equals( this.newProjectPanel.getChoosedGroupNameToEdit() ) ) {
							group.setGroupId( newGroup );
						}
					}

					Collections.sort( groupList, new GroupComparable() );
					Iterator<Group> iter = groupList.iterator();
					this.newProjectPanel.getCmbMngGroup().removeAllItems();
					if( this.newProjectPanel instanceof JNewProjectPanel ) {
						JNewProjectPanel jnpp = (JNewProjectPanel)this.newProjectPanel;
						jnpp.getCmbGroupSelect().removeAllItems();
					}
	
					while( iter.hasNext() ) {
						Group grp = iter.next();
						this.newProjectPanel.getCmbMngGroup().addItem( grp.getGroupId() );
						
						if( this.newProjectPanel instanceof JNewProjectPanel ) {
							JNewProjectPanel jnpp = (JNewProjectPanel)this.newProjectPanel;
							jnpp.getCmbGroupSelect().addItem( grp.getGroupId() );
						}
					}
					this.newProjectPanel.initGroupInfo();
					this.newProjectPanel.getCmbMngGroup().requestFocus();
					this.newProjectPanel.refreshSampleTable( groupList );
				}
			}else {
				return;
			}
		}
	}
}
