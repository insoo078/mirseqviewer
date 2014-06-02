package kobic.msb.swing.thread.callable;

import java.util.Iterator;
import java.util.concurrent.Callable;

import javax.swing.JOptionPane;

import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.server.model.Model;
import kobic.msb.server.model.StoreProjectMapItemModel;
import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.server.model.jaxb.Msb.Project;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.dialog.JMsbProgressDialog;
import kobic.msb.swing.thread.caller.JMsbProjectImportCaller;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import kobic.msb.system.project.ProjectManager;

public class JMsbWriteModelFromMsbFileCallable implements Callable<Void>{
	private StoreProjectMapItemModel	spmim;
	private JMsbProgressDialog			dialog;
	private ProjectMapItem				item;
	private JMsbBrowserMainFrame		frame;
	private JMsbProjectImportCaller		caller;

	public JMsbWriteModelFromMsbFileCallable( JMsbBrowserMainFrame frame, JMsbProgressDialog dialog, StoreProjectMapItemModel spmim ) {
		this.frame	= frame;
		this.dialog = dialog;
		this.spmim	= spmim;
		this.dialog.setLabelValue("Reading... msv file");
	}

	@Override
	public Void call() {
		try {
			this.item	= this.spmim.getProjectMapItem();

	        if( this.item != null ) {
        		Project project		= this.item.getProjectInfo();
        		ProjectManager pm	= MsbEngine.getInstance().getProjectManager();

        		while (true) {
        			if( this.item != null ) {
	        			if(  this.item.getProjectName().equals("") ) {
	        				int result = JOptionPane.showConfirmDialog( this.dialog, "Project Name is empty!! are you sure you cancel importing the project?", "Confirm", JOptionPane.YES_NO_OPTION );
	        				if( result == JOptionPane.YES_OPTION ) {
				        		JOptionPane.showMessageDialog( this.frame, "File importing is canceld");
				        		break;
	        				}else {
	        					this.inputNewProjectName(project, this.item);
	        				}
	        			}else if( !pm.getProjectMap().isExistProjectName( this.item.getProjectName() ) ) {
	        				// no duplicated
	            			break;
	            		}else {
	            			JOptionPane.showMessageDialog( this.frame, "This project name already exist");
	            			// duplicated
				        	this.inputNewProjectName( project, this.item );
	            		}
        			}else {
        				break;
        			}
        		}
	        }
	        this.dialog.setIndeterminate(false);

	        if( this.item != null && !Utilities.nulltoEmpty( this.item.getProjectName() ).isEmpty() ) {
	        	this.writeModelToFile();
				this.changeSystemProperties();
				
		        this.caller.callback( this.item );
	        }
		}catch(Exception e) {
			MsbEngine.logger.error("Error", e);
		}
		return null;
	}

    private void writeModelToFile() {
    	if( this.item != null && !Utilities.nulltoEmpty( this.item.getProjectName() ).isEmpty() ) {
	    	int progress = 0;
			Iterator<String> keyIter = this.item.getModelMap().keySet().iterator();
	
			this.dialog.setLabelValue("Writing model to file...");
			while( keyIter.hasNext() ) {
				String mirid = keyIter.next();
				Model model = this.spmim.getModel( mirid );
				if( model != null ) {
					String modelFilePath = model.writeModelToFile( this.item.getProjectInfo().getProjectName() );

					this.item.getModelMap().put(mirid, modelFilePath);
				}
				int value = (int)(((double)progress / this.item.getModelMap().size()) * 100);
				this.dialog.setProgressValue( value );
				progress++;
			}
    	}
    }
    
	private void inputNewProjectName( Project project, ProjectMapItem pmi ) {
		String newProjectName = JOptionPane.showInputDialog( this.dialog, "New Project Name : ", "New Project Name", JOptionPane.DEFAULT_OPTION );
		pmi.setProjectName( newProjectName );
		project.setProjectName( newProjectName );
		
		return;
	}
    
    private void changeSystemProperties() {
    	if( this.item != null && !Utilities.nulltoEmpty( this.item.getProjectName() ).isEmpty() ) {
	    	ProjectManager pm	= MsbEngine.getInstance().getProjectManager();
			Project project		= this.spmim.getProjectMapItem().getProjectInfo();
			Msb.Project.MiRnaList list = project.getMirnaList();
			project.setMirnaList( list );
			
			this.dialog.setLabelValue("Changing system properties...");
			Msb msb = new Msb();
			msb.setProject( project );
			try {
				pm.writeXmlToProject( msb );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				MsbEngine.logger.error("File write error : ", e);
			}
	
			this.spmim.getProjectMapItem().setProjectStatus( JMsbSysConst.STS_DONE );
			pm.getProjectMap().putProject( this.spmim.getProjectMapItem().getProjectName(), this.spmim.getProjectMapItem() );
			
			MsbEngine.logger.debug("Store ProjectMapItems");
			try {
				ProjectManager.storeProjectMapItem( this.item );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				MsbEngine.logger.error("Error : ", e);
			}
			MsbEngine.logger.debug("Completed ProjectMapItems storing");
    	}
    }
    
	public void patchCaller(JMsbProjectImportCaller caller) {
		this.caller = caller;
	}
	
	public boolean isAlive() {
		return this.isAlive();
	}
}
