package kobic.msb.db.sqlite;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kobic.com.util.Utilities;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.db.sqlite.vo.MatureVO;
import kobic.msb.io.file.obj.mirbase.RnaSecondaryStructureInfo;
import kobic.msb.server.obj.GenomeReferenceObject;
import kobic.msb.server.obj.HairpinSequenceObject;
import kobic.msb.server.obj.SequenceObject;
import kobic.msb.swing.frame.splash.SplashScreen;
import kobic.msb.system.SystemEnvironment;
import kobic.msb.system.engine.MsbEngine;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class MicroRnaDB {
	private SqlJetDb db;
	
	private MicroRnaDB remote = MicroRnaDB.this;

	public static String getDefaultResources( String dbFileName ) {		
		String dbPath = SystemEnvironment.getSystemBasePath() + "resources/data/mirbase/" + dbFileName;

		return dbPath;
	}
	
	public MicroRnaDB(String dbPath, boolean isIndirect) throws SqlJetException {
		File dbFile = null;
		if( isIndirect ) {
			dbFile = new File( MicroRnaDB.getDefaultResources( dbPath ) );
		}else {
			dbFile = new File(dbPath);
		}

		this.db = SqlJetDb.open( dbFile, true );
	}

	public MicroRnaDB(String dbPath) throws SqlJetException {
		File dbFile = new File( MicroRnaDB.getDefaultResources( dbPath ) );

		this.db = SqlJetDb.open( dbFile, true );
	}
	
	public boolean isOpen() {
		return this.db.isOpen();
	}

	public MicroRnaDB() throws SqlJetException {
		File dbFile = new File( MicroRnaDB.getDefaultResources("miRna_miRbase20.db") );

		this.db = SqlJetDb.open( dbFile, true );

//		db.getOptions().setAutovacuum( true );
//		
//		db.beginTransaction( SqlJetTransactionMode.WRITE );
//		try {
//			db.getOptions().setUserVersion(1);
//		}finally{
//			db.commit();
//		}

//		db.close();
	}

	public List<String> getMicroRnaList() throws RuntimeException{
		final List<String> map = new ArrayList<String>();

		try {
			this.db.runReadTransaction( new ISqlJetTransaction() {
				@Override
				public Object run(SqlJetDb arg0) throws SqlJetException {
					arg0.beginTransaction( SqlJetTransactionMode.READ_ONLY );
					ISqlJetTable table = arg0.getTable( "A1_HAIRPIN" );
					ISqlJetCursor cursor = table.open();
		
					do{
						String id			= cursor.getString("ID");
						map.add( id );
					}while( cursor.next() );
					
					arg0.commit();
					if(cursor != null) cursor.close();
					
					return null;
				}
			});
			
		}catch(SqlJetException e) {
			MsbEngine.logger.error( "Error", e );
		}
		return map;
	}

//	public Map<String, HairpinVO> getMicroRnaList( SplashScreen screen ) throws RuntimeException{
	public List<String> getMicroRnaList( final SplashScreen screen ) throws RuntimeException{
		final List<String> map = new ArrayList<String>();

		try {
			this.db.runReadTransaction( new ISqlJetTransaction() {
				@Override
				public Object run(SqlJetDb arg0) throws SqlJetException {
					arg0.beginTransaction( SqlJetTransactionMode.READ_ONLY );
					ISqlJetTable table = arg0.getTable( "A1_HAIRPIN" );
					ISqlJetCursor cursor = table.open();

		//			Map<String, HairpinVO> map = new HashMap<String, HairpinVO>();
					
					long nTotalRow = cursor.getRowCount();
		
					int currentRowIndex = 0;
					do{
						String id			= cursor.getString("ID");
		//				String accession	= cursor.getString("ACCESSION");
		//				String chromosome	= cursor.getString("CHR");
		//				String strand		= cursor.getString("STRAND");
		//				String start		= cursor.getString("START");
		//				String end			= cursor.getString("END");
		//				String sequence		= cursor.getString("SEQUENCE");
		//
		//				HairpinVO vo = new HairpinVO( id, accession, sequence, chromosome, strand, start, end );
		//				map.put( id, vo );
						map.add( id );
						
						currentRowIndex++;
						screen.setProgress( "Load : miRNA list from DB...", (int)((98 * currentRowIndex)/nTotalRow) );
					}while( cursor.next() );
					
					arg0.commit();
					if(cursor != null) cursor.close();

					return null;
				}
			});

			return map;
		}catch( SqlJetException e) {
			MsbEngine.logger.error( "Error", e );
		}
		
		return map;
	}
	
	public List<HairpinVO> getMicroRnaPrematureList( final String organism ) throws RuntimeException{
		final List<HairpinVO> list = new ArrayList<HairpinVO>();

		try {
			this.db.runReadTransaction( new ISqlJetTransaction() {
				@Override
				public Object run(SqlJetDb arg0) throws SqlJetException {
					arg0.beginTransaction( SqlJetTransactionMode.READ_ONLY );
					ISqlJetTable table = arg0.getTable( "A1_HAIRPIN" );
					ISqlJetCursor cursor = table.open();
		
					do{
						String id			= cursor.getString("ID");
						String accession	= cursor.getString("ACCESSION");
						String chromosome	= cursor.getString("CHR");
						String strand		= cursor.getString("STRAND");
						String start		= cursor.getString("START");
						String end			= cursor.getString("END");
						String sequence		= cursor.getString("SEQUENCE");
		
						if( id.startsWith( organism) ) {
							HairpinVO vo = new HairpinVO( id, accession, sequence, chromosome, strand, start, end );
							list.add( vo );
						}
					}while( cursor.next() );
					
					arg0.commit();
					if(cursor != null) cursor.close();
					
					return null;
				}
			});
			
		}catch( SqlJetException e) {
			MsbEngine.logger.error( "Error", e );
		}

		return list; 
	}
	
	public HairpinVO getMicroRnaHairpinByMirid2( final String hairpinId ) throws RuntimeException{
		final HairpinVO vo = new HairpinVO();
		try {
			this.db.runReadTransaction( new ISqlJetTransaction() {
				@Override
				public Object run(SqlJetDb arg0) throws SqlJetException {
					arg0.beginTransaction( SqlJetTransactionMode.READ_ONLY );
					ISqlJetTable table = arg0.getTable( "A1_HAIRPIN" );
					ISqlJetCursor cursor = table.lookup( table.getPrimaryKeyIndexName(), hairpinId );
					do{
						String id			= cursor.getString("ID");
						String accession	= cursor.getString("ACCESSION");
						String chromosome	= cursor.getString("CHR");
						String strand		= cursor.getString("STRAND");
						String start		= cursor.getString("START");
						String end			= cursor.getString("END");
						String sequence		= cursor.getString("SEQUENCE");
		
//						vo = new HairpinVO( id, accession, sequence, chromosome, strand, start, end );
						vo.setId(id);
						vo.setAccession(accession);
						vo.setSequence(sequence);
						vo.setChr(chromosome);
						vo.setStrand(strand);
						vo.setStart(start);
						vo.setEnd(end);
					}while( cursor.next() );
					
					arg0.commit();
					if(cursor != null) cursor.close();
					
					return null;
				}
			});
		}catch( SqlJetException e) {
			MsbEngine.logger.error( "error : ", e );
		}
		
		return vo;
	}
	
	public List<String> getHairpinIdFromMatureId( final String matureId ) {
		final List<String> list = new ArrayList<String>();
		try {
			this.db.runReadTransaction( new ISqlJetTransaction() {
				@Override
				public Object run(SqlJetDb arg0) throws SqlJetException {
					arg0.beginTransaction(SqlJetTransactionMode.READ_ONLY);		
					ISqlJetTable table = arg0.getTable( "A1_MATURE" );
		
					ISqlJetCursor cursor = table.lookup("a1_mature_idx2", matureId );
		//			ISqlJetCursor cursor = table.order( table.get)
		//			MsbEngine.logger.error("mirid id : " + matureId );
					do{
		//				MsbEngine.logger.error("pre-mature id : " + cursor.getString( "ID" ) );
						list.add( cursor.getString( "ID" ) );
						cursor.next();
					}while( !cursor.eof() );
					
					arg0.commit();
					if(cursor != null) cursor.close();

					return null;
				}
			});
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( "error : ", e );
		}

		return list;
	}

	@SuppressWarnings("unused")
	public HairpinSequenceObject getMicroRnaHairpinByMirid( final String mirid ) {
		final List<HairpinSequenceObject> hairpins = new ArrayList<HairpinSequenceObject>();
		try {
			this.db.runReadTransaction( new ISqlJetTransaction() {
				@Override
				public Object run(SqlJetDb arg0) throws SqlJetException {
					arg0.beginTransaction(SqlJetTransactionMode.READ_ONLY);
					ISqlJetTable table = arg0.getTable( "A1_HAIRPIN" );
					ISqlJetCursor cursor = table.lookup( table.getPrimaryKeyIndexName(), mirid );
		
					while( !cursor.eof() ){
						String id			= cursor.getString("ID");
						String accession	= cursor.getString("ACCESSION");
						String sequence		= cursor.getString("SEQUENCE");
						String chr			= cursor.getString("CHR");
						String strand		= cursor.getString("STRAND");
		
						/****************************************************************************
						 * If there are not genomic position and strand information,
						 * we do like that genomic prosition start 0 and strand '+'
						 */
						long startPos = 0;
						long endPos = 0;
						String strStartPos	= cursor.getString("START");
						String strEndPos	= cursor.getString("END");
						if( !strStartPos.isEmpty() )	startPos = Long.parseLong( strStartPos );
						if( !strEndPos.isEmpty() )		endPos = Long.parseLong( strEndPos );
						long length			= sequence.length();
						
						if( strand.isEmpty() )	strand = "+";
		
						HairpinSequenceObject vo = new HairpinSequenceObject( chr, startPos, endPos, length, strand.charAt(0), sequence );
						hairpins.add( vo );
						cursor.next();
					}
					arg0.commit();
					if(cursor != null) cursor.close();
					
					return null;
				}
			});
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( "error : ", e );
		}
		
		if( hairpins.size() == 0 )	return null;
		else						return hairpins.get(0);
	}
//	
//	public List<HairpinVO> getMicroRnaHairpinByLociInfo(String chr, int read_start, int read_end) {
//		List<HairpinVO> list = new ArrayList<HairpinVO>();
//
//		try {
//			this.db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
//			ISqlJetTable table = this.db.getTable( "A1_HAIRPIN" );
//			ISqlJetCursor cursor = table.open();
//
//			do{
//				String id			= cursor.getString("ID");
//				String accession	= cursor.getString("ACCESSION");
//				String chromosome	= cursor.getString("CHR");
//				String strand		= cursor.getString("STRAND");
//				String start		= cursor.getString("START");
//				String end			= cursor.getString("END");
//				String sequence		= cursor.getString("SEQUENCE");
//
//				HairpinVO vo = new HairpinVO( id, accession, sequence, chromosome, strand, start, end );
//				if( !chromosome.equals( chr ) )	cursor.next();
///**
//
//	case 1:
//	------------------------
//				----------------------
//				
//	case 2:
//				----------------------
//	------------------------
// **/
//				if( start == null )			start = "0";
//				else if( start.isEmpty() )	start = "0";
//				if( end == null )			end = "0";
//				else if( end.isEmpty() )	end = "0";
//				
//				if( Integer.parseInt(start) < read_start && Integer.parseInt(end) > read_end )	cursor.next();
//				if( Integer.parseInt(start) > read_start && Integer.parseInt(end) < read_end )	cursor.next();
//				
//				list.add( vo );
//
//				cursor.next();
//			}while( !cursor.eof() );
//		} catch (SqlJetException e) {
//			// TODO Auto-generated catch block
//			MsbEngine.logger.error( "error : ", e );
//		}
//
//		return list;
//	}
	
	public Map<String, List<HairpinVO>> getAllMicroRnaMaturesByChromosome(final String organism) {
		final Map<String, List<HairpinVO>> map = new HashMap<String, List<HairpinVO>>();

		try {
			this.db.runReadTransaction( new ISqlJetTransaction() {
				@Override
				public Object run(SqlJetDb arg0) throws SqlJetException {
					arg0.beginTransaction(SqlJetTransactionMode.READ_ONLY);
					ISqlJetTable table = arg0.getTable( "A1_HAIRPIN" );
					ISqlJetCursor cursor = table.open();
		
					do{
						String id			= cursor.getString("ID");
						String accession	= cursor.getString("ACCESSION");
						String chromosome	= cursor.getString("CHR");
						String strand		= cursor.getString("STRAND");
						String start		= cursor.getString("START");
						String end			= cursor.getString("END");
						String sequence		= cursor.getString("SEQUENCE");
		
						if( id.startsWith( organism ) )	{
							HairpinVO vo = new HairpinVO( id, accession, sequence, chromosome, strand, start, end );
			
							if( map.containsKey( chromosome ) ) {
								map.get(chromosome).add( vo );
							}else {
								List<HairpinVO> list = new ArrayList<HairpinVO>();
								list.add( vo );
								map.put( chromosome, list );
							}
						}
		
						cursor.next();
					}while( !cursor.eof() );
					
					arg0.commit();
					if(cursor != null) cursor.close();
					
					return null;
				}
			});
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( "error : ", e );
		}

		return map;
	}
	
	public Map<String, List<MatureVO>> getAllMicroRnaMaturesByMirid() {
		final Map<String, List<MatureVO>> map = new HashMap<String, List<MatureVO>>();

		try {
			this.db.runReadTransaction( new ISqlJetTransaction() {
				@Override
				public Object run(SqlJetDb arg0) throws SqlJetException {
					// TODO Auto-generated method stub
					ISqlJetCursor cursor = null;
					try{					
						arg0.beginTransaction(SqlJetTransactionMode.READ_ONLY);
						final ISqlJetTable table = arg0.getTable( "A1_MATURE" );
						
						MsbEngine.logger.info( "Database : " + table.getDataBase().getCacheSize() + " & isInTraction : " + arg0.isInTransaction() + " " + arg0.getTransactionMode() + " " + arg0.isOpen() );
	
						cursor = table.order( table.getPrimaryKeyIndexName() );
						do{
							String hairpin_id	= cursor.getString( "ID" );
							String mirid		= cursor.getString("MIRID");
							int start			= Integer.parseInt( Utilities.nulltoOne( cursor.getString("START") ) );
							int end				= Integer.parseInt( Utilities.nulltoOne( cursor.getString("END") ) );
							
							MatureVO mature = new MatureVO();
							mature.setId( hairpin_id );
							mature.setMirid( mirid );
							mature.setStart( start );
							mature.setEnd( end );
	
							if( map.containsKey( hairpin_id ) ) {
								map.get(hairpin_id).add( mature );
							}else {
								List<MatureVO> list = new ArrayList<MatureVO>();
								list.add( mature );
								map.put( hairpin_id, list );
							}
	
							cursor.next();
						}while( !cursor.eof() );
	
						arg0.commit();
						if(cursor != null) cursor.close();
					}catch(Exception e) {
						MsbEngine.logger.error("Error : ", e);
					}

					return null;
				}
			});
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( "error : ", e );
		}

		return map;
	}

	public List<MatureVO> getMicroRnaMaturesByMirid( final String hairpin_id ) {
		final List<MatureVO> matures = new ArrayList<MatureVO>();
		try {
			this.db.runReadTransaction( new ISqlJetTransaction() {
				@Override
				public Object run(SqlJetDb arg0) throws SqlJetException {
					arg0.beginTransaction(SqlJetTransactionMode.READ_ONLY);
					ISqlJetTable table = arg0.getTable( "A1_MATURE" );
		//			ISqlJetCursor cursor = table.open();
//					ISqlJetCursor cursor = table.lookup( table.getPrimaryKeyIndexName(), hairpin_id );
					ISqlJetCursor cursor = table.open();
					
					boolean isContinue = true;
					do{
						String id		= cursor.getString("ID");
						if( id.equals(hairpin_id) ) {
							String mirid		= cursor.getString("MIRID");
							int start			= Integer.parseInt( Utilities.nulltoOne( cursor.getString("START") ) );
							int end				= Integer.parseInt( Utilities.nulltoOne( cursor.getString("END") ) );
		
							MatureVO mature = new MatureVO();
							mature.setId( id );
							mature.setMirid( mirid );
							mature.setStart( start );
							mature.setEnd( end );
							
							matures.add( mature );
						}

						isContinue = cursor.next();
					}while( isContinue );
					
					arg0.commit();
					if(cursor != null) cursor.close();
					
					return null;
				}
			});
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( "error : ", e );
		}

		return this.getMatureList( matures, hairpin_id );
	}
	
	private List<MatureVO> getMatureList(List<MatureVO> list, String hairpinId) {
		for(MatureVO mature:list) {
			HairpinSequenceObject hairpin = remote.getMicroRnaHairpinByMirid( hairpinId );
			int start = mature.getStart();
			int end = mature.getEnd();
			String mature_seq	= hairpin.getSequenceByString().subSequence(start-1, end-1).toString();
			mature.setSequence( mature_seq );
		}
		return list;
	}

	private MatureVO getMatureList(MatureVO mature, String hairpinId) {
		HairpinSequenceObject hairpin = remote.getMicroRnaHairpinByMirid( hairpinId );
		int start = mature.getStart();
		int end = mature.getEnd();
		String mature_seq	= hairpin.getSequenceByString().subSequence(start-1, end-1).toString();
		mature.setSequence( mature_seq );
		return mature;
	}
	
	public MatureVO getMicroRnaMatureByMirid( final String matureId ) {
		final MatureVO mature = new MatureVO();

		try {
			this.db.runReadTransaction( new ISqlJetTransaction() {
				@Override
				public Object run(SqlJetDb arg0) throws SqlJetException {
					arg0.beginTransaction(SqlJetTransactionMode.READ_ONLY);
					ISqlJetTable table = arg0.getTable( "A1_MATURE" );
					ISqlJetCursor cursor = table.lookup( "a1_mature_idx2", matureId );
					
					do{
						String id		= cursor.getString("mirid");
						if( id.equals(matureId) ) {
							String mirid		= cursor.getString("MIRID");
							int start			= Integer.parseInt( Utilities.nulltoOne( cursor.getString("START") ) );
							int end				= Integer.parseInt( Utilities.nulltoOne( cursor.getString("END") ) );
		
							HairpinSequenceObject hairpin = remote.getMicroRnaHairpinByMirid( cursor.getString("ID") );
							String mature_seq	= hairpin.getSequenceByString().subSequence(start-1, end-1).toString();

							mature.setId( cursor.getString("ID") );
							mature.setMirid( mirid );
							mature.setStart( start );
							mature.setEnd( end );
							mature.setSequence( mature_seq );
						}
		
						cursor.next();
					}while( !cursor.eof() );
					
					arg0.commit();
					if(cursor != null) cursor.close();
					
					return null;
				}
			});
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( "error : ", e );
		}
		return mature;
	}

	public RnaSecondaryStructureInfo select2ndStructures(String mirid) {
		ISqlJetCursor cursor = null;
		try {
			this.db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			ISqlJetTable table = this.db.getTable( "A1_SECONDARYINFO" );
			cursor = table.lookup( table.getPrimaryKeyIndexName(), mirid );

			RnaSecondaryStructureInfo info = new RnaSecondaryStructureInfo();
			do{
				String dMirid			= cursor.getString("MIRID");
				if( dMirid.equals(mirid) ) {
					info.setMirid( mirid );
					info.setMirna_acc( 		cursor.getString("MIRNA_ACC")		);
					info.setAccession(		cursor.getString("ACCESSION")		);
					info.setChr(			cursor.getString("CHR")				);
					info.setStrand(			cursor.getString("STRAND")			);
					info.set_5moR(			cursor.getString("_5moR")			);
					info.set_5p(			cursor.getString("_5p")				);
					info.setLoop(			cursor.getString("LOOP")			);
					info.set_3p(			cursor.getString("_3p")				);
					info.set_3moR(			cursor.getString("_3moR")			);
					info.setContig_start(	cursor.getString("CONTIG_START")	);
					info.setContig_end(		cursor.getString("CONTIG_END")		);
				}

				cursor.next();
			}while( !cursor.eof() );
			
			return info;
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( "error : ", e );
		}finally {
			try {
				this.db.commit();
				if(cursor != null) cursor.close();
			} catch (SqlJetException e) {
				// TODO Auto-generated catch block
				MsbEngine.logger.error( "error : ", e );
			}
		}
		return null;
	}
	
	public void close() {
		try {
			this.db.close();
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( "error : ", e );
		}
	}

	public static void main(String[] args) throws SqlJetException, InterruptedException {
		MicroRnaDB db = new MicroRnaDB();
		System.out.println( db.getMicroRnaMaturesByMirid( "hsa-mir-4680").size() );
//		MicroRnaDB db = new MicroRnaDB( "/Users/lion/Desktop/miRna.db", false );
		
//		List<String> list = db.getHairpinIdFromMatureId( "hsa-let-7f-5p" );
//		for(int i=0; i<list.size(); i++) {
//			System.out.println( list.get(i) );
//		}

//		MicroRnaDB[] db = new MicroRnaDB[10];
//
//		for(int i=0; i<db.length; i++) {
//			db[i] = new MicroRnaDB("Users/lion/Documents/workspace-sts-2.8.1.RELEASE/MiRnaSeqBrowser/resources/data/mirbase/miRna.db");
//		}
//		
//		Thread.sleep(5000);
//		for(int i=0; i<db.length; i++) {
//			db[i].close();
//		}
//		db.getMicroRnaHairpinByMirid( keyword );
//		db.getMicroRnaMaturesByMirid( keyword );
//		long a = System.currentTimeMillis();
//		Map<String, HairpinVO> map = db.getMicroRnaList();
//		List list = new ArrayList(map.keySet());
//		Collections.sort( list );
//
//		Iterator<String> iter = list.iterator();
//		
//		while( iter.hasNext() ) {
//			String itr = iter.next();
//			if( itr.contains("hsa-let") )	System.out.println( itr );
//		}
//		long b = System.currentTimeMillis();
//		System.out.println( b - a  + " sec");
//		db.close();
	}
}
