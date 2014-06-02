package kobic.msb.db.sqlite;

import java.io.File;
import java.util.List;

import kobic.msb.system.engine.MsbEngine;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public abstract class SQLiteDb {
	private SqlJetDb db;
	
	public SQLiteDb() {
		this( "/Users/lion/Desktop/miRna.db", true );
	}

	public SqlJetDb getDb() {
		return this.db;
	}

	public SQLiteDb( String db_path, boolean renew ) {
		File dbFile = new File( db_path );
//		dbFile.delete();
		
		try {
			this.db = SqlJetDb.open( dbFile, true );

//			this.db.getOptions().setAutovacuum( true );
//
//			this.db.beginTransaction( SqlJetTransactionMode.WRITE );
//	
//			this.db.getOptions().setUserVersion(1);
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( "error : ", e );
		}
	}
	
	public abstract <T> void insertTable( String tableName, List<T> list );


	public void createTable( String query ) {
		try {
			this.db.createTable( query );
		} catch (SqlJetException e) {
			this.rollback();
			MsbEngine.logger.error( "error : ", e );
//			e.printStackTrace();
		}

		this.commit();
	}

	public void createIndex( String query ) {
		try {
			this.db.createIndex( query );
		} catch (SqlJetException e) {
			this.rollback();
			MsbEngine.logger.error( "error : ", e );
//			e.printStackTrace();
		}

		this.commit();
	}

	public void rollback() {
		try {
			this.db.rollback();
		} catch (SqlJetException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
			MsbEngine.logger.error( "error : ", e1 );
		}
	}
	
	public void commit() {
		try {
			this.db.commit();
		} catch (SqlJetException e) {
			try {
//				e.printStackTrace();
				MsbEngine.logger.error( "error : ", e );
				this.db.rollback();
			} catch (SqlJetException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
				MsbEngine.logger.error( "error : ", e );
			}
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( "error : ", e );
		}
	}
	
	public void dbClose() {
		try {
			if( this.db != null )	this.db.close();
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( "error : ", e );
		}
	}
}
