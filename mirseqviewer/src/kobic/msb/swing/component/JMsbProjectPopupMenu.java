package kobic.msb.swing.component;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.tree.TreePath;

import kobic.com.util.Utilities;
import kobic.msb.common.ImageConstant;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.listener.projecttreepanel.DelProjectActionListener;
import kobic.msb.swing.listener.projecttreepanel.ExeProjectActionListener;
import kobic.msb.swing.listener.projecttreepanel.NewProjectActionListener;
import kobic.msb.swing.listener.projecttreepanel.ProjectConfigActionListener;
import kobic.msb.swing.listener.projecttreepanel.RefreshActionListener;
import kobic.msb.swing.panel.mainframe.JMsbProjectTreePanel;

public class JMsbProjectPopupMenu extends JPopupMenu {
	private JMsbBrowserMainFrame	frame;
	@SuppressWarnings("unused")
	private TreePath				choosedNode;
	private JComponent				comp;
	
	private JMenuItem addProjectMenuItem;
	private JMenuItem modProjectMenuItem;
	private JMenuItem delProjectMenuItem;
	private JMenuItem exeProjectMenuItem;
//	private JMenuItem profileMenuItem;
	private JMenuItem propertiesMenuItem;
	private JMenuItem refreshMenuItem;

	public static final int ROOT_NODE		=	1;
	public static final int PROJECT_NODE	= 	3;
	public static final int SUMMARY_NODE	=	5;
	public static final int EMPTY_NODE		=	99;
	
	@SuppressWarnings("unused")
	private JMsbProjectPopupMenu remote = JMsbProjectPopupMenu.this; 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JMsbProjectPopupMenu( JMsbBrowserMainFrame frame, JComponent comp, TreePath choosedNode ) {
		this.frame = frame;
		this.comp = comp;

		this.addProjectMenuItem	= new JMenuItem("Add Project");
		this.modProjectMenuItem	= new JMenuItem("Edit Project");
		this.delProjectMenuItem	= new JMenuItem("Del Project");
		this.exeProjectMenuItem	= new JMenuItem("Execute");
//		this.profileMenuItem	= new JMenuItem("Profile"); 
		this.propertiesMenuItem	= new JMenuItem("Properties");
		this.refreshMenuItem	= new JMenuItem("Refresh");
		
		this.addProjectMenuItem.setIcon( ImageConstant.newDocIcon );
		this.modProjectMenuItem.setIcon( ImageConstant.editIcon );
		this.delProjectMenuItem.setIcon( ImageConstant.removeIcon );
		this.refreshMenuItem.setIcon( ImageConstant.refreshIcon );
		this.exeProjectMenuItem.setIcon( ImageConstant.runIcon );
		this.propertiesMenuItem.setIcon( ImageConstant.propertiesIcon );
//		this.profileMenuItem.setIcon( ImageConstant.mainTabIcon );

		this.add( this.addProjectMenuItem );
		this.add( this.modProjectMenuItem );
		this.add( new JSeparator() );
		this.add( this.exeProjectMenuItem );
//		this.add( this.profileMenuItem );
		this.add( new JSeparator() );
		this.add( this.delProjectMenuItem );
		this.add( new JSeparator() );
		this.add( this.refreshMenuItem );
		this.add( new JSeparator() );
		this.add( this.propertiesMenuItem );

		String projectName = "";
		if( choosedNode != null )
			projectName = Utilities.nulltoEmpty( SwingUtilities.getSelectedProjectNameFromTreePath( choosedNode ) );

		this.addProjectMenuItem.addActionListener(	new NewProjectActionListener( this.frame )						);
		this.modProjectMenuItem.addActionListener(	new NewProjectActionListener( this.frame, projectName )			);
		this.delProjectMenuItem.addActionListener(	new DelProjectActionListener( this.frame, projectName )			);
		this.exeProjectMenuItem.addActionListener(	new ExeProjectActionListener( this.frame, projectName )			);
//		this.profileMenuItem.addActionListener(		new ProfilingProjectActionListener( this.frame, projectName )	);
		this.refreshMenuItem.addActionListener(		new RefreshActionListener( (JMsbProjectTreePanel)this.comp )	);
		this.propertiesMenuItem.addActionListener(	new ProjectConfigActionListener( this.frame, projectName)		);
	}
	
	public void setStatus( int status ) {
		if( status == JMsbProjectPopupMenu.ROOT_NODE ) {
			this.addProjectMenuItem.setEnabled(	true );
			this.modProjectMenuItem.setEnabled(	false );
			this.delProjectMenuItem.setEnabled(	true );
			this.exeProjectMenuItem.setEnabled(	false );
//			this.profileMenuItem.setEnabled(	false );
			this.refreshMenuItem.setEnabled(	true );
		}else if( status == JMsbProjectPopupMenu.PROJECT_NODE ) {
			this.addProjectMenuItem.setEnabled(	true );
			this.modProjectMenuItem.setEnabled(	true );
			this.delProjectMenuItem.setEnabled(	true );
			this.exeProjectMenuItem.setEnabled(	true );
//			this.profileMenuItem.setEnabled(	true );
			this.refreshMenuItem.setEnabled(	true );
		}else {
			this.addProjectMenuItem.setEnabled(	true );
			this.modProjectMenuItem.setEnabled(	false );
			this.delProjectMenuItem.setEnabled(	false );
			this.exeProjectMenuItem.setEnabled( false );
//			this.profileMenuItem.setEnabled(	false );
			this.refreshMenuItem.setEnabled(	true );
		}
	}
}
