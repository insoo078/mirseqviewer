package kobic.msb.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import kobic.msb.system.engine.MsbEngine;

public class PropertiesController {
	private String		propertiesPath;
	private Properties	properties;
	private String		encodingType;
	
	public PropertiesController(String propertiesPath, String encodingType) {
		this.encodingType= encodingType;
		this.properties = new Properties();
		this.propertiesPath = propertiesPath;
	}

	public PropertiesController(String propertiesPath, Properties properties, String encodingType) {
		this.encodingType= encodingType;
		this.properties = properties;
		this.propertiesPath = propertiesPath;
	}

	public Properties loadAll() {
		try {
			this.properties.load( new BufferedReader( new InputStreamReader( new FileInputStream( this.propertiesPath ), this.encodingType) ) );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( "error : ", e );
		}

		return this.properties;
	}

	public void store( String key, String value ) {
		FileOutputStream output = null;
		if( new File(this.propertiesPath).exists() )	this.loadAll();

		try {
			output = new FileOutputStream( this.propertiesPath );
			this.properties.put(key, value);
			this.properties.store(output, value);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( "error : ", e );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( "error : ", e );
		}finally{
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				MsbEngine.logger.error( "error : ", e );
			}
		}
	}

	public void storeAll( Map<String, String> paramMap ) {
		Iterator<String> iter = paramMap.keySet().iterator();
		while( iter.hasNext() ) {
			String key = iter.next();
			this.store( key, paramMap.get(key) );
		}
	}
	
	public Properties getProperties() {
		return this.properties;
	}
}
