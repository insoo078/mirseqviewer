package kobic.msb.system.engine;

import kobic.com.network.HTTPRequester;
import kobic.msb.swing.frame.splash.SplashScreen;
import kobic.msb.swing.thread.callable.JMsbNGSBamIndexSwingWorker;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;
import javax.swing.JWindow;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.rits.cloning.Cloner;

import kobic.msb.db.sqlite.MicroRnaDB;
import kobic.msb.system.PropertiesController;
import kobic.msb.system.SystemEnvironment;
import kobic.msb.system.catalog.AbstractFileLock;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.project.ProjectManager;

public class MsbEngine extends AbstractFileLock{
	private static final int				nrOfProcessors			= Runtime.getRuntime().availableProcessors();

//	public static final MicroRnaDB			_db 					= MsbEngine.initDatabase();
	public static final Logger				logger					= Logger.getLogger( MsbEngine.class );
	private static ExecutorService			es						= Executors.newFixedThreadPool(nrOfProcessors);
	public static final Stack<Future<?>>	thdJobList				= new Stack<Future<?>>();

	public static final Cloner				cloner					= new Cloner();
	
	public static final MsbEngine			engine					= new MsbEngine();
	
	public HTTPRequester					httpRequster;
//	public static List<String>				_MEMORY_HAIRPIN_LIST	= null;

	private Properties						_property;
	private ProjectManager					_managerProject;
	
	private LinkedHashMap<String, String>		organismMap;
	private LinkedHashMap<String, MicroRnaDB>	_dbTable;
	
	private File								lockFile;

	public static MsbEngine getInstance() {
		if( engine != null )
			return engine;
		return new MsbEngine();
	}

	public static ExecutorService getExecutorService() {
		if( MsbEngine.es.isTerminated() ){
			MsbEngine.es = Executors.newFixedThreadPool(nrOfProcessors);
		}
		return MsbEngine.es;
	}
	private void locking() {
		try {
			FileOutputStream lockFileOS = new FileOutputStream( this.lockFile );
			lockFileOS.close();
			this.tryLock( this.lockFile.getAbsolutePath() );
		} catch (Exception e) {
			String message = "An instance of miRseq Viewer is already running.\n";
			message += "If there is a problem, you should delete the \n" + SystemEnvironment.getSystemBasePath() + ".msb.lock file";
			JOptionPane.showMessageDialog(null, message ,"Warning", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}

	private MsbEngine() throws RuntimeException {
		this.lockFile = new File( SystemEnvironment.getSystemBasePath() , ".msb.lock");
		this.locking();
		
		MsbEngine.initLogProperties();
		PropertyConfigurator.configure( SystemEnvironment.getSystemBasePath() + "config/log4j.properties" );

		logger.info( "Loading : engine" );

		this._property				= MsbEngine._initProperties();
		this._managerProject		= new ProjectManager( this );
	}

	private void organismMapInitializeSetting() {
		try {
			this.organismMap = this.httpRequster.getOrganismInfo();
		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, e ,"Network problem", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}
	
	public MicroRnaDB reOpenMirBaseDB( String version ) {
		try {
			if( this._dbTable.containsKey(version) ) {
				if( this._dbTable.get(version).isOpen() ) return this._dbTable.get(version);
				else {
					MicroRnaDB db = this._dbTable.get( version );
					db.close();

					String name = "miRna_" + version.replace("miRbase", "miRbase") + ".db";

					File dbFile = new File( this._property.getProperty( "msb.base.location" ) + File.separator + "/resources/data/mirbase/" + name );
	
					this._dbTable.put( version, new MicroRnaDB( dbFile.getName() ) );
					
					return this._dbTable.get( version );
				}
			}
		}catch(Exception e) {
			MsbEngine.logger.error("DB Connection problem : ", e);
		}
		
		return null;
	}

	private void miRBaseDatabaseInitializeSetting() {
		this._dbTable = new LinkedHashMap<String, MicroRnaDB>();
		try 
		{
			File dir = new File( this._property.getProperty( "msb.base.location" ) + File.separator + "/resources/data/mirbase" );
			if( dir.exists() && dir.isDirectory() ) {
				File[] fileList = dir.listFiles();
				for( int i=0; i<fileList.length; i++ ) {
					String name = fileList[i].getName();
					if( name.endsWith(".db") ) {
						name = name.replace("miRna_", "");
						name = name.replace(".db", "");
						name = name.replace("miRbase", "miRBase");
						
	//					this.miRBaseList.put(name, dir.getAbsolutePath() + File.separator + fileList[i].getName() );
						this._dbTable.put(name, new MicroRnaDB(fileList[i].getName()) );
					}
				}
			}
		}catch(Exception e) {
			MsbEngine.logger.error("DB Connection problem : ", e);
		}
	}

	private static void initLogProperties() throws RuntimeException{
		try {
			File logDir = new File(SystemEnvironment.getSystemBasePath() + "log");
			if( !logDir.exists() ) {
				logDir.mkdir();
			}
			PropertiesController controller = new PropertiesController( SystemEnvironment.getSystemBasePath() + "config/log4j.properties", "UTF-8");
			controller.loadAll();
	
			Map<String, String> map = new HashMap<String, String>();
			map.put("log4j.appender.FILE.File", SystemEnvironment.getSystemBasePath() + "log/msbrowser.log" );
	
			controller.storeAll( map );
		}catch(Exception e) {
			throw new RuntimeException( e );
		}
	}

	public void _initLogProperties() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("java.util.logging.FileHandler.pattern", SystemEnvironment.getSystemBasePath() + "log/msbrowser.log" );
	}

//	private static MicroRnaDB initDatabase() {
//		try {
//			MicroRnaDB db			= new MicroRnaDB();
//			
//			return db;
//		} catch (SqlJetException e) {
//			// TODO Auto-generated catch block
//			MsbEngine.logger.error( "error : ", e );
//			throw new RuntimeException( e );
//		}
//	}

	private static Properties _initProperties() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("msb.base.location",		SystemEnvironment.getSystemBasePath() );

		/**********
		 * If you want to synchronize the workspace and miRseq browser base directory
		 * 
		 * Actually, the workspace is setted by user when miRseq browser is firstly launched
		 */
//		map.put("msb.project.workspace",	SystemEnvironment.getSystemBasePath() );

		return SystemEnvironment.getPropertiesAfterStore( map );
	}

	public void loadEngine( JWindow window ) throws RuntimeException {
		SplashScreen scr = (SplashScreen)window;
		try {
//			MsbEngine._MEMORY_HAIRPIN_LIST = MsbEngine._db.getMicroRnaList( scr );

			String projectListFilePath = this.getProjectListFilePath();

			if( new File( projectListFilePath ).exists() )	{
				// Project List file (.project_list.pjl) load to memory
				long a = System.currentTimeMillis();
				this._managerProject.reloadProjectListFromTheSystemObjectFileForInitialization( scr );
				long b = System.currentTimeMillis();
				
				MsbEngine.logger.debug("1. reloadProjectListFromTheSystemObjectFile in loadEngine : " + ((float)(b-a)/1000) + "sec.");
			}
//			scr.setProgress("Load : project list...", 100);

			long b1 = System.currentTimeMillis();
			this.httpRequster			= HTTPRequester.getInstance();
			long c = System.currentTimeMillis();
			MsbEngine.logger.debug("2. create a instance for Http in loadEngine : " + ((float)(c-b1)/1000) + "sec.");

			this.organismMapInitializeSetting();
			long d = System.currentTimeMillis();
			MsbEngine.logger.debug("3. organismMapInitialization for Http in loadEngine : " + ((float)(d-c)/1000) + "sec.");
			
			this.miRBaseDatabaseInitializeSetting();
			long e = System.currentTimeMillis();
			MsbEngine.logger.debug("4. miRBaseDatabaseInitializeSetting for Http in loadEngine : " + ((float)(e-d)/1000) + "sec.");
		}catch(Exception e) {
			MsbEngine.logger.error( "error : ", e );
//			e.printStackTrace();
			throw new RuntimeException( e );
		}
	}

	public String getProjectListFilePath() {
		return this._property.getProperty( "msb.base.location" ) + this._property.getProperty("msb.project.list");
	}
	
	public ProjectManager getProjectManager() {
		return this._managerProject;
	}

	public Properties getSystemProperties() {
		this._property = SystemEnvironment.getReLoadProperties();

		return this._property;
	}

	public void reloadSystemPropertiesAfterUpdate( Map<String, String> map ) {
		this._property = SystemEnvironment.getPropertiesAfterStore( map );
	}
	
	public LinkedHashMap<String, String> getOrganismMap() {
		return this.organismMap;
	}
	
	public String getOrganismName(String id) {
		if( this.organismMap != null ) {
			Iterator<String> iterOrganism = this.organismMap.keySet().iterator();
	    	while( iterOrganism.hasNext() ) {
	    		String name = iterOrganism.next();
	    		if( this.organismMap.get(name).equals( id ) ) {
	    			return name;
	    		}
	    	}
		}

		return null;
	}

	public LinkedHashMap<String, MicroRnaDB> getMiRBaseMap() {
		return this._dbTable;
	}
	
	public HTTPRequester getHttpRequester(){
		return this.httpRequster;
	}

	public void close() throws Exception{
		MsbEngine.es.shutdownNow();

		MsbEngine.logger.debug("Program Termination");
		this.closeLock();

		this.lockFile.delete();

		this.httpRequster.close();
		for(Iterator<MicroRnaDB> iter = this._dbTable.values().iterator(); iter.hasNext();){
			MicroRnaDB db = iter.next();
			MsbEngine.logger.info( db + " closing" );
			db.close();
		}
//		if( MsbEngine._db != null )	MsbEngine._db.close();
	}
}
