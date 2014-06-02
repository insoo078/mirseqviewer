package kobic.msb.system.project;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.server.model.jaxb.Msb.Project;
import kobic.msb.swing.frame.splash.SplashScreen;
import kobic.msb.system.catalog.ProjectMap;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

/*************************************************************************
 * All Project Controller
 * 
 * @author lion
 *
 */
public class ProjectManager {
	private MsbEngine	_engine;
	private ProjectMap	_PROJECT_MAP;
	private String		_projectListFile;

	public ProjectManager( MsbEngine engine ) {
		this._engine = engine;
		
		this._PROJECT_MAP			= new ProjectMap();

		// To write project name at list
		this._projectListFile = this._engine.getSystemProperties().getProperty("msb.base.location") + File.separator;
		this._projectListFile += this._engine.getSystemProperties().getProperty("msb.project.list");
	}

	/****************************************************************************************
	 * Modify Project
	 * 
	 * @param msbXmlFileObject
	 * @return
	 * @throws RuntimeException
	 * @throws JAXBException
	 */
//	public boolean modifyProject( Msb msbXmlFileObject ) throws RuntimeException, JAXBException, Exception {
//		return this.createProject( msbXmlFileObject );
//	}
	
	public boolean modifyProject( ProjectMapItem projectMapItem ) throws RuntimeException, JAXBException, Exception {
		return this.createProject( projectMapItem );
	}

	/*****************************************************************************************
	 * To Remove all projects
	 * 
	 * @return
	 */
	public int removeAllProject() throws Exception{
		int result = 0;
		List<String> projectList = this._PROJECT_MAP.getProjectNameList();
		for( String projectName : projectList ) {
			result = this.removeProject( projectName );
		}
		return result;
	}

	/******************************************************************************************
	 * To Remove a project
	 * 
	 * @param projectName
	 * @return
	 */
	public int removeProject( String projectName ) throws Exception{
		this._PROJECT_MAP.remove( projectName );

		this._PROJECT_MAP.writeToFile( _projectListFile );

		String path = this._engine.getSystemProperties().getProperty("msb.project.workspace");

		File projectDirectory = new File( path + File.separator + projectName );
		Utilities.deleteRecursive( projectDirectory );

		return 1;
	}

	public File writeXmlToProject( Msb msbXmlFileObject ) throws Exception {
		Project project	= msbXmlFileObject.getProject();
		String path		= this._engine.getSystemProperties().getProperty("msb.project.workspace");

		File projectDirectory = new File( path + File.separator + project.getProjectName() );

		if( !projectDirectory.exists() )	projectDirectory.mkdir();
		path = path + File.separator + project.getProjectName() + File.separator + "." + project.getProjectName() + ".xml";
		
		if( projectDirectory.isDirectory() ) {
			return ProjectManager.writeXmlFile( msbXmlFileObject, path );
		}
		
		return null;
	}
	
	public File writeXmlToProject( ProjectMapItem projectMapItem ) throws Exception {
		Project project	= projectMapItem.getProjectInfo();
		String path		= this._engine.getSystemProperties().getProperty("msb.project.workspace");

		File projectDirectory = new File( path + File.separator + project.getProjectName() );

		if( !projectDirectory.exists() )	projectDirectory.mkdir();
		path = path + File.separator + project.getProjectName() + File.separator + "." + project.getProjectName() + ".xml";
		
		if( projectDirectory.isDirectory() ) {
			return ProjectManager.writeXmlFile( projectMapItem, path );
		}

		return null;
	}
	
	private synchronized static File writeXmlFile( Msb msbXmlFileObject, String path ) {
		// To Create XML file for PROJECT
		File projectXmlFile = new File( path );
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance( Msb.class );

			Marshaller jaxMarshaller = jaxbContext.createMarshaller();
			jaxMarshaller.marshal( msbXmlFileObject, projectXmlFile );
			
			return projectXmlFile;
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
			MsbEngine.logger.error( "error", e1 );
			throw new RuntimeException( e1.toString() );
		}
	}
	
	private synchronized static File writeXmlFile( ProjectMapItem projectMapItem, String path ) {
		Msb msbXmlFileObject = new Msb();
		msbXmlFileObject.setProject( projectMapItem.getProjectInfo() );
		// To Create XML file for PROJECT
		File projectXmlFile = new File( path );
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance( Msb.class );

			Marshaller jaxMarshaller = jaxbContext.createMarshaller();
			jaxMarshaller.marshal( msbXmlFileObject, projectXmlFile );
			
			return projectXmlFile;
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
			MsbEngine.logger.error( "error", e1 );
			throw new RuntimeException( e1.toString() );
		}
	}

	/******************************************************************************************
	 * To Create a project
	 * 
	 * @param msbXmlFileObject
	 * @return
	 * @throws RuntimeException
	 * @throws JAXBException
	 */
	public boolean createProject( Msb msbXmlFileObject ) throws RuntimeException, JAXBException, Exception{
		Project project	= msbXmlFileObject.getProject();

		File projectXmlFile = this.writeXmlToProject( msbXmlFileObject );

		if( projectXmlFile.exists() ) {
			ProjectMapItem item = new ProjectMapItem( project.getProjectName(), projectXmlFile.getParent(), projectXmlFile.getAbsolutePath(), null );
			item.setProjectInfo(			projectXmlFile.getAbsolutePath()	);
			item.setProjectStatus(			JMsbSysConst.STS_DONE_CREATE_EMPTY_PROJECT	);

			this._PROJECT_MAP.putProject( project.getProjectName(), item );

			// To write project name at list
			this._PROJECT_MAP.writeToFile( this._projectListFile );
		}

		return true;
	}

	public boolean createProject( ProjectMapItem projectMapItem ) throws RuntimeException, JAXBException, Exception{
		Project project = projectMapItem.getProjectInfo();

		File projectXmlFile = this.writeXmlToProject( projectMapItem );
		
		if( projectXmlFile.exists() ) {
			ProjectMapItem item = new ProjectMapItem( project.getProjectName(), projectXmlFile.getParent(), projectXmlFile.getAbsolutePath(), null );
			item.setProjectInfo(			projectXmlFile.getAbsolutePath()	);
			item.setProjectStatus(			JMsbSysConst.STS_DONE_CREATE_EMPTY_PROJECT	);

			this._PROJECT_MAP.putProject( project.getProjectName(), item );

			// To write project name at list
			this._PROJECT_MAP.writeToFile( this._projectListFile );
		}

		return true;
	}

	/**********************************************************************************
	 * To Get a system file path about PROJECT list
	 *  
	 * @return
	 */
	public String getSystemFileToGetProjectList() {
		return this._projectListFile;
	}	

	/**********************************************************************************
	 * To Get project list
	 * 
	 * @return
	 */
	public ProjectMap getProjectMap() {
		return this._PROJECT_MAP;
	}

	/**********************************************************************************
	 * To Check what exist
	 * 
	 * @param key
	 * @return
	 */
	public boolean isExistProjectName(String key) {
		return this._PROJECT_MAP.isExistProjectName(key);
	}

	/**********************************************************************************
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void reloadProjectListFromTheSystemObjectFile( ) throws ClassNotFoundException, IOException, Exception {
		this._PROJECT_MAP.loadProjectListFromObjectFile( this._engine.getProjectListFilePath() );
	}
	
	public void reloadProjectListFromTheSystemObjectFileForInitialization( SplashScreen scr ) throws ClassNotFoundException, IOException, Exception {
		this._PROJECT_MAP.loadProjectListFromObjectFile( scr, this._engine.getProjectListFilePath() );
	}

    public static String getStrProjectStatus( int status ) {
//    	if( status == JMsbSysConst.READY_TO_SORT ) 				{		return "Ready to sort bam file(s)";		}
//    	else if( status == JMsbSysConst.RUNNING_SORT )			{		return "Running to sort";				}
//    	else if( status == JMsbSysConst.DONE_SORT )				{		return "Done sort";						}
//    	else if( status == JMsbSysConst.READY_TO_INDEX)			{		return "Ready to index bam file(s)";	}
//    	else if( status == JMsbSysConst.RUNNING_INDEX)			{		return "Running to inse";				}
//    	else if( status == JMsbSysConst.DONE_INDEX)				{		return "Done index";					}
//    	else if( status == JMsbSysConst.READY_TO_CHOOSE_MIRNAS)	{		return "Ready to choose mirna(s)";		}
//    	else if( status == JMsbSysConst.RUNNING_MODELING)		{		return "Running modeling";				}
//    	else															return "Done";
    	if( status == JMsbSysConst.STS_DONE_CREATE_EMPTY_PROJECT )		{		return "Created a empty project";					}
    	else if( status == JMsbSysConst.STS_DONE_CHOOSE_ORGANISM )		{		return "Choosed the organism & mirBase ver.";		}
    	else if( status == JMsbSysConst.STS_DONE_INDEX_FILES )			{		return "Bam files have indexed";					}
    	else if( status == JMsbSysConst.STS_DONE_READ_FILES )			{		return "Readed all bam files";						}
    	else if( status == JMsbSysConst.STS_DONE_MAKE_MODELS )			{		return "Builded up viewer models";					}
    	else if( status == JMsbSysConst.STS_DONE_SUMMARIZE_RNAS )		{		return "All miRNAs are summarized";					}
    	else																	return "Done";
    }
    
	public static void storeProjectMapItem( ProjectMapItem projectMapItem ) throws Exception{
		String projectName = projectMapItem.getProjectName();
		Msb msb = new Msb();
		msb.setProject( projectMapItem.getProjectInfo() );
		MsbEngine.engine.getProjectManager().writeXmlToProject( msb );

		MsbEngine.engine.getProjectManager().getProjectMap().putProject( projectName, projectMapItem );
		MsbEngine.engine.getProjectManager().getProjectMap().writeToFile( MsbEngine.engine.getProjectManager().getSystemFileToGetProjectList() );
	}
}
