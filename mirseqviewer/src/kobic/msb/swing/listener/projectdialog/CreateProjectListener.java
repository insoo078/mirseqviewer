package kobic.msb.swing.listener.projectdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.Model;
import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.server.model.jaxb.Msb.Project;
import kobic.msb.server.model.jaxb.Msb.Project.Samples;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.swing.listener.projecttreepanel.ExeProjectActionListener;
import kobic.msb.swing.panel.newproject.JBamFilePreProcessingPanel;
import kobic.msb.swing.panel.newproject.JMsbMatureChoosePanel;
import kobic.msb.swing.panel.newproject.JMsbProjectInfoPanel;
import kobic.msb.swing.panel.newproject.JMsbSampleTableCommonPanel;
import kobic.msb.swing.panel.newproject.JMsvGroupControlPanel;
import kobic.msb.swing.panel.newproject.JNewProjectPanel;
import kobic.msb.swing.panel.newproject.JMsvNewProjectPanel;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import kobic.msb.system.project.ProjectManager;

public class CreateProjectListener implements ActionListener {
	private JProjectDialog dialog;
	
	public CreateProjectListener( JProjectDialog dialog ) {
		this.dialog = dialog;
	}
	
//	public static Message isOkAboutLoci( String locus ) {
//		final int MAX_RANGE = 1000000;
//
//		if( Utilities.nulltoEmpty(locus).isEmpty() )	return new Message(Message.NORMAL_MESSAGE, "Normal", null);
//		String[] spl = locus.split(":");
//
//		if( spl.length != 2 )	return new Message(Message.ERROR_MESSAGE, "Locus information : chromosome:start pos-end pos (as chr1:123455-123475)", null);
//		else{
//			String chr = spl[0];
//			String[] pos = spl[1].split("-");
//			
//			if( pos.length != 2 )	return new Message(Message.ERROR_MESSAGE, "Locus information : chromosome:start pos-end pos (as chr1:123455-123475)", null);
//			
//			if( Utilities.isNumeric(pos[0]) && Utilities.isNumeric(pos[1]) ) {
//				int start = Integer.parseInt( pos[0] );
//				int end = Integer.parseInt( pos[1] );
//				
//				if( start > end )				return new Message(Message.ERROR_MESSAGE, "End position must be larger than start position", null);
//				if( end - start > MAX_RANGE )	return new Message(Message.ERROR_MESSAGE, "Locus range can not over " + Utilities.getNumberWithComma(MAX_RANGE) + " bases", null);
//
//				SequenceObject locusObj = new SequenceObject();
//				locusObj.setChromosome( chr );
//				locusObj.setStartPosition( start );
//				locusObj.setEndPosition( end );
//				return new Message(Message.NORMAL_MESSAGE, "Normal", locusObj);
//			}else {
//				new Message(Message.ERROR_MESSAGE, "Start and End position must be a numeric value", null);
//			}
//		}
//		return new Message(Message.ERROR_MESSAGE, "Locus information : chromosome:start pos-end pos (as chr1:123455-123475)", null);
//	}
	
	
	private boolean canWeGoSampleProcessingPanel( JMsvGroupControlPanel projectPanel ) {
		if( projectPanel instanceof JMsvNewProjectPanel ) {
			// Check there are empty fields on the table
			DefaultTableModel model = (DefaultTableModel) projectPanel.getTable().getModel();

			if( model.getRowCount() ==0 ) {
				JOptionPane.showMessageDialog( this.dialog, "There are no sample file", "Error", JOptionPane.WARNING_MESSAGE );

				return false;
			}

			Map<String, Group> groupMap = new HashMap<String, Group>();

			for(int i=0; i<model.getRowCount(); i++) {
				Sample sample = new Sample();

				String groupId = "";
				for(int j=0; j<model.getColumnCount(); j++) {
					Object value = model.getValueAt(i, j);

					if( Utilities.emptyToNull(value) == null ) {
						projectPanel.getTable().setRowSelectionInterval(i, i);
						JOptionPane.showMessageDialog( this.dialog, model.getColumnName(j) + " is empty", "Error", JOptionPane.WARNING_MESSAGE );
						return false;
					}
					
					if( model.getColumnName(j).equals("Order") )			sample.setOrder( Utilities.nulltoEmpty( model.getValueAt(i, j) ).toString() );
					else if( model.getColumnName(j).equals("Sample ID") )	sample.setName( Utilities.nulltoEmpty( model.getValueAt(i, j) ).toString() );
					else if( model.getColumnName(j).equals("Path") )		sample.setSamplePath( Utilities.nulltoEmpty( model.getValueAt(i, j) ).toString() );
					else if( model.getColumnName(j).equals("Index File") )	sample.setIndexPath( Utilities.nulltoEmpty( model.getValueAt(i, j) ).toString() );

					if( new File(Utilities.nulltoEmpty( model.getColumnName(j).equals("Index File") )).exists() )	sample.setSortedPath( Utilities.nulltoEmpty( model.getValueAt(i, j) ).toString() );
					
					if( model.getColumnName(j).equals("Group ID") )	groupId = Utilities.nulltoEmpty( model.getValueAt(i, j) ).toString();
				}
				if( groupMap.containsKey(groupId) ) {
					groupMap.get(groupId).getSample().add( sample );
				}else {
					Group newGroup = new Group();
					newGroup.setGroupId(groupId);
					newGroup.setId(groupId);
					newGroup.getSample().add( sample );
					
					groupMap.put(groupId, newGroup);
				}
			}
			projectPanel.getMsb().getProject().getSamples().setGroup( new ArrayList<Group>( groupMap.values() )  );
			
			// If this step is passed, your input are separated to each group
			projectPanel.setNumberOfSample( model.getRowCount() );
			
			return true;
		}
		
		return true;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String projectName = this.dialog.getTxtProjectName().getText();

		JMsbBrowserMainFrame	controllerFrame	= this.dialog.getOwner();

		if( this.dialog.getTabbedPane().getSelectedComponent() instanceof JMsvGroupControlPanel) {
			/*******************************************************************
			 * New project panel
			 * 
			 * user can create empty project in the panel
			 * and create groups and attach sample files to each group
			 */

			try {
				SwingUtilities.setWaitCursorFor( this.dialog );

//				JNewProjectPanel projectPanel = this.dialog.getNewProjectPanel();
				JMsvGroupControlPanel projectPanel = this.dialog.getNewProjectPanel();
				
				if( this.canWeGoSampleProcessingPanel( projectPanel ) ) {
					if( CreateProjectListener.createStep1( projectPanel.getMsb(), projectName, projectPanel, this.dialog.isEditDialog() ) ) {
						controllerFrame.getToolBar().refreshProjectListForToolBar();
						controllerFrame.getTreePanel().refreshProjectTree();
			
						this.dialog.getTabbedPane().setEnabledAt(1, true);
	
						ProjectMapItem item = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );
						if( item != null )	this.dialog.updateCurrentState( item );
					}
				}
			}catch(Exception ex) {
				JOptionPane.showMessageDialog( this.dialog, "Can't create the project", "Error", JOptionPane.ERROR_MESSAGE );

				if( MsbEngine.getInstance().getProjectManager().getProjectMap().isExistProjectName( projectName ) ){
					try {
						MsbEngine.getInstance().getProjectManager().getProjectMap().remove(projectName);
						MsbEngine.getInstance().getProjectManager().getProjectMap().writeToFile( MsbEngine.getInstance().getProjectManager().getSystemFileToGetProjectList() );
						
						controllerFrame.getToolBar().refreshProjectListForToolBar();
						controllerFrame.getTreePanel().refreshProjectTree();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						MsbEngine.logger.error( "error ", ex );
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						MsbEngine.logger.error( "error ", ex );
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						MsbEngine.logger.error( "error ", ex );
					}
				}
				MsbEngine.logger.error( "error ", ex );
			}finally{
				SwingUtilities.setDefaultCursorFor( this.dialog );
			}
		}else if( this.dialog.getTabbedPane().getSelectedComponent() instanceof JMsbProjectInfoPanel ) {
			SwingUtilities.setWaitCursorFor( this.dialog );

			try {
				JMsbProjectInfoPanel	infoPanel		= this.dialog.getProjectInfoPanel();
				String					organismId		= infoPanel.getOrganismInfo();
				String					miRBaseVersion	= infoPanel.getMiRBaseVersion();
				String					bedFilePath		= Utilities.nulltoEmpty( infoPanel.getTxtBedFilePath() );

				ProjectMapItem item = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );

				if( !bedFilePath.isEmpty() )	{
					try {
						item.setBedFilePath( bedFilePath );
						item.readBedFileToProject();
					}catch(Exception iex) {
						MsbEngine.logger.error("Error", iex);

						SwingUtilities.setDefaultCursorFor( this.dialog );
						JOptionPane.showMessageDialog( this.dialog, "Bed file has problems", "Error", JOptionPane.ERROR_MESSAGE );
						this.dialog.getProjectInfoPanel().getTxtBedFile().requestFocus();
	
						return;
					}
				}

				this.dialog.getNextButton().setEnabled( false );

				CreateProjectListener.createStep2( item, organismId, miRBaseVersion );
				
				if( item != null )	this.dialog.updateCurrentState( item );

				this.dialog.getTabbedPane().setEnabledAt(1, true);
				this.dialog.getTabbedPane().setEnabledAt(2, true);
			}catch(Exception ex){
				JOptionPane.showMessageDialog( this.dialog, "Problem was happend!!", "Error", JOptionPane.ERROR_MESSAGE );
				if( MsbEngine.getInstance().getProjectManager().getProjectMap().isExistProjectName( projectName ) ){
					ProjectMapItem item = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );

					if( item != null ) {
						item.setProjectStatus( JMsbSysConst.STS_DONE_CREATE_EMPTY_PROJECT );
						
						try {
							this.dialog.updateCurrentState(item);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							MsbEngine.logger.error("Error : ", e1);
						}
					}
				}

				MsbEngine.logger.error( "error : ", ex);
			}finally{
				SwingUtilities.setDefaultCursorFor( this.dialog );
			}
		}else if( this.dialog.getTabbedPane().getSelectedComponent() instanceof JBamFilePreProcessingPanel ) {
			/*******************************************************************
			 * JBamFilePreProcessingPanel panel
			 * 
			 * user can do sorting and index BAM files (by multi threading)
			 * it is automatically doing but, if exception is happen in there
			 * user can not go next step
			 */
			try {
				this.dialog.getNextButton().setEnabled( false );

				ProjectMapItem item = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );

				if( item != null )	this.dialog.getThreadManager().goSortingAndIndexing( item );

				controllerFrame.getToolBar().refreshProjectListForToolBar();
				controllerFrame.getTreePanel().refreshProjectTree();
				
				this.dialog.getTabbedPane().setEnabledAt( 1, true );
				this.dialog.getTabbedPane().setEnabledAt( 2, true );
				this.dialog.getTabbedPane().setEnabledAt( 3, true );
			}catch(Exception ex) {
				MsbEngine.logger.error( "error", ex );
			}
		}else if( this.dialog.getTabbedPane().getSelectedComponent() instanceof JMsbMatureChoosePanel ) {
			SwingUtilities.setWaitCursorFor( this.dialog );
			try {
				UpdatableTableModel newModel = (UpdatableTableModel)this.dialog.getMirnaChoosePanel().getMirnaTable().getModel();
				for(int i=0; i<newModel.getRowCount(); i++) {
					Boolean bool = (Boolean)newModel.getValueAt(i, 0);
					String mirid = (String)newModel.getValueAt(i, 1);

					if( bool ){
						Object[] val = this.dialog.getMirnaChoosePanel().getDefaultMiRna().get( mirid );
						this.dialog.getMirnaChoosePanel().getDefaultMiRna().remove( mirid );
						this.dialog.getMirnaChoosePanel().getChoosedMiRna().put(mirid, val);
					}
				}

				HashMap<String, Object[]> map = this.dialog.getMirnaChoosePanel().getChoosedMiRna();

				boolean canDispose = CreateProjectListener.createStep4(map, projectName, controllerFrame);

				if( canDispose ) this.dialog.dispose();
				else {
					ProjectMapItem item = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );
					this.dialog.updateCurrentState( item );
				}
			}catch(Exception ex) {
				MsbEngine.logger.error( "error", ex );
			}finally{
				SwingUtilities.setDefaultCursorFor( this.dialog );
			}
		}
	}

	public static boolean createStep1( Msb msb, String projectName, JMsbSampleTableCommonPanel remote, boolean isEditable ) {
		ProjectManager mngProjectObj	= MsbEngine.getInstance().getProjectManager();

		if( projectName.equals("") ) {
			SwingUtilities.setDefaultCursorFor( remote );
			JOptionPane.showMessageDialog( remote, "Project name is empty!!");
			remote.setFocusProjectName();
			return false;
		}

		if( remote.getNumberOfSample() == 0 ) {
			SwingUtilities.setDefaultCursorFor( remote );
			JOptionPane.showMessageDialog( remote, "There is no sample!!");
			return false;
		}
		
		if( mngProjectObj.getProjectMap().isExistProjectName( projectName ) && !isEditable ) {
			SwingUtilities.setDefaultCursorFor( remote );
			JOptionPane.showMessageDialog( remote, projectName + " project already exist!!");
			return false;
		}
		Project newProject = msb.getProject();
		newProject.setProjectName( projectName );

		try {
			mngProjectObj.createProject( msb );
			
			ProjectMapItem item = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );

			item.setProjectStatus( JMsbSysConst.STS_DONE_CREATE_EMPTY_PROJECT );
			MsbEngine.getInstance().getProjectManager().getProjectMap().writeToFile( MsbEngine.getInstance().getProjectManager().getSystemFileToGetProjectList() );
			
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error("Error", e);
			JOptionPane.showMessageDialog( null, "<HTML>Can't write a project file!! : " + e.getMessage() + "<BR>" + "Please check your worspace!</HTML>", "Error", JOptionPane.ERROR_MESSAGE );
		}
		
		return false;
	}

	public static boolean createStep2( ProjectMapItem item, String organismId, String miRBaseVersion ) throws Exception {
		item.setOrganism( organismId );
		item.setMiRBaseVersion( miRBaseVersion );
		item.setBedFilePath( null );

		Project newProject = item.getProjectInfo();
		Msb.Project.ReferenceGenome rg = new Msb.Project.ReferenceGenome();
		rg.setGenomeName( MsbEngine.getInstance().getOrganismMap().get( organismId ) );
		rg.setGenomePath( "ONLINE" );
		newProject.setReferenceGenome( rg );

		item.setProjectStatus( JMsbSysConst.STS_DONE_CHOOSE_ORGANISM );

		MsbEngine.getInstance().getProjectManager().getProjectMap().writeToFile( MsbEngine.getInstance().getProjectManager().getSystemFileToGetProjectList() );
		
		return true;
	}
	
	public static boolean createStep4( HashMap<String, Object[]> map, String projectName, JMsbBrowserMainFrame controllerFrame ) throws Exception {
		ProjectMapItem item = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );
		item.initializeModelMap();

		Msb.Project.MiRnaList list = new Msb.Project.MiRnaList();
		Project project		= item.getProjectInfo();

		for( java.util.Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
			String mature_id = iter.next();

			List<String> hairpindIds = MsbEngine.getInstance().getMiRBaseMap().get(item.getMiRBAseVersion()).getHairpinIdFromMatureId( mature_id );

			if( !mature_id.startsWith( JMsbSysConst.NOVEL_MICRO_RNA ) ) {
				for(int i=0; i<hairpindIds.size(); i++) {
					String mirid = hairpindIds.get(i);

					if( !item.getModelMap().containsKey( mirid ) ) {
						Model model = item.getProjectModel( mirid );
						if( model != null ) {
							list.getMirnaList().add( model.getMirnaInfo() );
							
							item.addProjectModel( mirid, item.getTotalModelMap().get( mirid ) );
						}
					}
				}
			}else {
				String mirid = mature_id;
				Model model = item.getProjectModel( mirid );
				if( model != null ) {
					list.getMirnaList().add( model.getMirnaInfo() );
					
					item.addProjectModel( mirid, item.getTotalModelMap().get( mirid ) );
				}
			}
		}
		project.setMirnaList( list );

		ProjectManager manager = MsbEngine.getInstance().getProjectManager();

		Msb msb = new Msb();
		msb.setProject( project );
		manager.writeXmlToProject( msb );

//		manager.getProjectMap().writeToFile( manager.getSystemFileToGetProjectList() );

		// When project creation finished normally, this project is automatically execute
		if( item != null && item.getModelMap().size() > 0 && item.getProjectStatus() == JMsbSysConst.STS_DONE_SUMMARIZE_RNAS ) {
			item.setProjectStatus( JMsbSysConst.STS_DONE );

			MsbEngine.logger.debug("Store ProjectMapItems");
			ProjectManager.storeProjectMapItem( item );
			MsbEngine.logger.debug("Completed ProjectMapItems storing");

			ExeProjectActionListener.execute( controllerFrame, item );

			controllerFrame.getToolBar().refreshProjectListForToolBar();
			controllerFrame.getTreePanel().refreshProjectTree();

			return true;
		}
		
		controllerFrame.getToolBar().refreshProjectListForToolBar();
		controllerFrame.getTreePanel().refreshProjectTree();
		
		return false;
	}
}
