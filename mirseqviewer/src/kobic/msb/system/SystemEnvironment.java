package kobic.msb.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import kobic.msb.common.JMsbSysConst;

public class SystemEnvironment extends PropertiesController {
	public static String	systemRootPath = System.getProperty("user.dir") + File.separator;
	
	public SystemEnvironment() {
		this( JMsbSysConst.ENCODE_MS949 );
	}

	public SystemEnvironment( String encodingType ) {
		this( SystemEnvironment.getSystemBasePath() + "config/system.properties", encodingType );
	}
	
	public static String getSystemBasePath() {
		return SystemEnvironment.systemRootPath;
	}

	public static Properties getPropertiesAfterStore( Map<String, String> map ) {
		String path = SystemEnvironment.getSystemBasePath() + "config/system.properties";

		/****************************
		 * To Store a property is program base directory
		 */
		SystemEnvironment se = new SystemEnvironment( path, JMsbSysConst.ENCODE_MS949 );
		se.storeAll( map );
		/****************************
		 * To Load a System configuration file
		 */
		se.loadAll();
		return se.getProperties();
	}
	
	public SystemEnvironment( String path, String encodingType ) {
		super( path, encodingType );
	}
	
	public String getProperty(String key) {
		return this.getProperties().getProperty(key);
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws FileNotFoundException, IOException {
//		String path = SystemEnvironment.getSystemBasePath() + "config/system.properties";
//		
//		java.io.InputStream is = SystemEnvironment.class.getResourceAsStream("config/system.properties");
//
//		SystemEnvironment se = new SystemEnvironment( path, SysConst.ENCODE_MS949 );
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("msb.base.location", SystemEnvironment.getSystemBasePath() );
//		se.storeAll( map );
//		is.close();
		java.util.ResourceBundle myResources = java.util.ResourceBundle.getBundle(SystemEnvironment.class.getCanonicalName());
	}
}
