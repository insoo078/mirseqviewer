package kobic.msb.swing.panel.mainframe;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.server.model.Model;
import kobic.msb.server.model.jaxb.Msb.Project;
import kobic.msb.server.model.jaxb.Msb.Project.MiRnaList.MiRna;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.component.JMsbProjectPopupMenu;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.renderer.JMsbProjectTreeCellRenderer;
import kobic.msb.system.catalog.ProjectMap;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import kobic.msb.system.project.ProjectManager;

public class JMsbProjectTreePanel extends JPanel implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DefaultMutableTreeNode	root;
	private JTree					tree;
	private JMsbBrowserMainFrame	frame;
	
	private JMsbProjectTreePanel	remote = JMsbProjectTreePanel.this;
	
	private final Cursor handCursor  = new java.awt.Cursor( java.awt.Cursor.HAND_CURSOR );

	public JMsbProjectTreePanel(JMsbBrowserMainFrame frame) {
		this.frame = frame;

		this.root = new DefaultMutableTreeNode("Project");
		
		MsbEngine.logger.debug( "Project tree panel creation" );

		try {
			JMsbProjectTreePanel.createNodes( this.root, MsbEngine.getInstance().getProjectManager().getProjectMap() );
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( e );
		}
		
		this.tree = new JTree( this.root );

		this.tree.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					TreePath tp = tree.getPathForLocation( e.getX(), e.getY() );

					if( e.getClickCount() == 1 && javax.swing.SwingUtilities.isRightMouseButton(e) ) {
						int row = tree.getClosestRowForLocation( e.getX(), e.getY() ); 
						tree.setSelectionRow(row);

						final JMsbProjectPopupMenu treePopup = new JMsbProjectPopupMenu( remote.frame, remote, tp );
	
						if( tp != null ) {
							if( tp.getParentPath() == null ) {				// if user coose the root node, does not work the Popup menu
								treePopup.setStatus( JMsbProjectPopupMenu.ROOT_NODE );
								treePopup.show( tree, e.getX(), e.getY() );
							}else {
								treePopup.setStatus( JMsbProjectPopupMenu.PROJECT_NODE );
								treePopup.show( tree, e.getX(), e.getY() );
							}
						}else {
							treePopup.setStatus( JMsbProjectPopupMenu.EMPTY_NODE );
							treePopup.show( tree, e.getX(), e.getY() );
						}
					}else if( e.getClickCount() == 1 && javax.swing.SwingUtilities.isLeftMouseButton(e) ) {
						if( tp != null ) {
							if( tp.getLastPathComponent().toString().startsWith("Accession") ) {
								String accession = tp.getLastPathComponent().toString().split(":")[1].trim();
								SwingUtilities.linkToUrl("http://www.mirbase.org/cgi-bin/mirna_entry.pl?acc=" + accession);
							}else if( tp.getLastPathComponent().toString().startsWith("Summary") ) {
								String projectName = tp.getPath()[1].toString();

//								remote.frame.updateReportPanel(projectName);
								remote.frame.addSummaryReport(projectName);
							}
						}
					}
				}catch(Exception ex) {
					MsbEngine.logger.error( "error", ex );
				}
			}
		});
		
		this.tree.addMouseMotionListener( new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				TreePath tp = tree.getPathForLocation( e.getX(), e.getY() );
				if( tp != null ) {
					if( tp.getLastPathComponent().toString().startsWith("Accession") ) {
						tree.setCursor( remote.handCursor );
					}else {
						tree.setCursor( Cursor.getDefaultCursor() );
					}
				}
			}
		});

	    this.tree.setCellRenderer( new JMsbProjectTreeCellRenderer() );
		
		JScrollPane scPane = new JScrollPane( this.tree );
		this.setLayout( new BorderLayout() );
		this.add( scPane, BorderLayout.CENTER );
	}

	public void refreshProjectTree() {
		SwingUtilities.setWaitCursorFor( this.frame );
		this.root.removeAllChildren();
		
		try {
			// Realod total Project information from .project.lst file to ProjectManager
			MsbEngine.getInstance().getProjectManager().reloadProjectListFromTheSystemObjectFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( e );
//			e.printStackTrace();
			JOptionPane.showMessageDialog( this, "Cannot refresh the project tree", "Error", JOptionPane.ERROR_MESSAGE );
		}

		try {
			JMsbProjectTreePanel.createNodes( this.root, MsbEngine.getInstance().getProjectManager().getProjectMap() );
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( e );
		}

		TreeModel newModel = new DefaultTreeModel( this.root );
		this.tree.setModel( newModel );
		
		SwingUtilities.setDefaultCursorFor( this.frame );
	}

	public static void createNodes( DefaultMutableTreeNode top, ProjectMap projectMap ) throws Exception {
		List<String> projectList = projectMap.getProjectNameList();
		Iterator<String> iter = projectList.iterator();
		while( iter.hasNext() ) {
			String projectName = iter.next();

			ProjectMapItem item = projectMap.getProject( projectName );
			if( item == null ) {
				DefaultMutableTreeNode projectNameNode = new DefaultMutableTreeNode( projectName );

				top.add( projectNameNode );
			}else {
			    DefaultMutableTreeNode projectNameNode		= null;
			    DefaultMutableTreeNode miridRootNode		= null;
			    DefaultMutableTreeNode refGenomeRootNode	= null;
			    DefaultMutableTreeNode groupRootNode 		= null;

			    int PROJECT_STATUS = projectMap.getProject( projectName ).getProjectStatus();
			    Project projectInfo = projectMap.getProject( projectName ).getProjectInfo();
			    
				if( projectInfo == null ) {
					projectNameNode = new DefaultMutableTreeNode( projectName );

					top.add( projectNameNode );
				}else {
				    projectNameNode = new DefaultMutableTreeNode( projectInfo.getProjectName() );
				    top.add( projectNameNode );
				    
				    DefaultMutableTreeNode projectStatus = new DefaultMutableTreeNode("Status : " + ProjectManager.getStrProjectStatus( PROJECT_STATUS ) );
//				    DefaultMutableTreeNode status = new DefaultMutableTreeNode( ProjectManager.getStrProjectStatus( PROJECT_STATUS ) );
//				    projectStatus.add( status );
				    projectNameNode.add( projectStatus );
				    
				    DefaultMutableTreeNode summaryNode = new DefaultMutableTreeNode("Summary");
				    projectNameNode.add( summaryNode );

				    String genome_name = "";
				    String genome_path = "";
				    if( projectInfo.getReferenceGenome() != null ) {
				    	genome_name = MsbEngine.engine.getOrganismName( projectInfo.getReferenceGenome().getGenomeName() );
				    	genome_path = projectInfo.getReferenceGenome().getGenomePath();
				    }
				    refGenomeRootNode = new DefaultMutableTreeNode ( "Reference genome Info" );
				    refGenomeRootNode.add( new DefaultMutableTreeNode( "Name : " + genome_name ) );
				    refGenomeRootNode.add( new DefaultMutableTreeNode( "File Path : " + genome_path ) );
				    projectNameNode.add( refGenomeRootNode );

				    miridRootNode = new DefaultMutableTreeNode("miRna(s)");
				    List<String> mirnaList = item.getMiRnaList();

				    if( mirnaList != null && mirnaList.size() > 0 )  {
					    for( String mirid:mirnaList ) {
					    	DefaultMutableTreeNode miRnaNode = new DefaultMutableTreeNode( mirid );
					    	
					    	ProjectMapItem projectMapItem = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );

				    		Model model = item.getProjectModel( mirid );

					    	HairpinVO vo = null;
					    	if( model.isNovel() ) {
					    		MiRna mirna = model.getMirnaInfo();
					    		
					    		vo = new HairpinVO();
					    		vo.setAccession( mirna.getAccession() );
					    		vo.setChr( mirna.getChromosome() );
					    		vo.setStart( Long.toString( model.getPrematureSequenceObject().getStartPosition() ) );
					    		vo.setEnd( Long.toString( model.getPrematureSequenceObject().getEndPosition() ) );
					    		vo.setStrand( Character.toString( model.getPrematureSequenceObject().getStrand() ) );
					    	}else {
					    		vo = MsbEngine.getInstance().getMiRBaseMap().get( projectMapItem.getMiRBAseVersion() ).getMicroRnaHairpinByMirid2( mirid );
					    	}
					    	if( vo != null ) {
					    		DefaultMutableTreeNode accessionNode	= new DefaultMutableTreeNode( "Accession : " + vo.getAccession() );
					    		DefaultMutableTreeNode locationNode		= new DefaultMutableTreeNode( "Location : " + vo.getChr() + " : " + Utilities.getNumberWithComma(vo.getStart()) + "-" + Utilities.getNumberWithComma( vo.getEnd() ) );
					    		DefaultMutableTreeNode strandRnaNode	= new DefaultMutableTreeNode( "Strand : " + vo.getStrand() );
					    		
					    		miRnaNode.add( accessionNode );
					    		miRnaNode.add( locationNode );
					    		miRnaNode.add( strandRnaNode );
					    	}
					    	miridRootNode.add( miRnaNode );
					    }
				    }else {
				    	miridRootNode.add( new DefaultMutableTreeNode("Empty") );
				    }
				    projectNameNode.add( miridRootNode );

				    groupRootNode = new DefaultMutableTreeNode( "Groups" );
				    List<Group> groupList = projectInfo.getSamples().getGroup();
				    for(Group grp:groupList) {
				    	String groupId = grp.getGroupId();
				    	DefaultMutableTreeNode subRoot = new DefaultMutableTreeNode( groupId );
				    	
				    	List<Sample> sampleList = grp.getSample();
				    	for(Sample smp:sampleList) {
				    		subRoot.add( new DefaultMutableTreeNode( "Sample : " + smp.getName() + " : " + smp.getSamplePath() ) );
				    		DefaultMutableTreeNode indexNode = new DefaultMutableTreeNode();

				    		if( !Utilities.nulltoEmpty( smp.getIndexPath() ).equals("") && new File(smp.getIndexPath()).exists() ) {
				    			indexNode.setUserObject( "Index : " + smp.getIndexPath() );
				    		}else {
				    			indexNode.setUserObject( "Index : <html><font color='green'>Empty</font></html>" );
				    		}
				    		subRoot.add( indexNode );
				    	}
				    	groupRootNode.add( subRoot );
				    }
				    projectNameNode.add( groupRootNode );
				    
				    top.add( projectNameNode );
				}
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}
