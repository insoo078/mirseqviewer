package kobic.msb.db.sqlite;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kobic.com.util.Utilities;
import kobic.msb.db.sqlite.vo.MatureVO;
import kobic.msb.io.file.Gff3Loader;
import kobic.msb.io.file.GffLoader;
import kobic.msb.io.file.MirBaseLoader;
import kobic.msb.io.file.MirBaseOrganismFileLoader;
import kobic.msb.io.file.obj.mirbase.ChromosomalCoordinate;
import kobic.msb.io.file.obj.mirbase.MirBaseOrganismInfo;
import kobic.msb.io.file.obj.mirbase.MirBaseRnaHairpinInfo;
import kobic.msb.io.file.obj.mirbase.MirBaseRnaMatureInfo;
import kobic.msb.io.file.obj.mirbase.PubmedInfo;
import kobic.msb.io.file.obj.mirbase.RnaSecondaryStructureInfo;
import kobic.msb.swing.filefilter.GffFilenameFilter;
import kobic.msb.system.engine.MsbEngine;

import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;

public class SQLiteDbCreator extends SQLiteDb{
	private String REF_FILE_PATH = "/Users/lion/Documents/workspace-sts-2.8.1.RELEASE/MiRnaSeqBrowser/resources/data/mirbase/mirbase";

	private String version;

	public SQLiteDbCreator(String dbOutputPath, String version, boolean renew) {
		super( dbOutputPath + File.separator + "miRna_miRbase" + version + ".db", renew );
		this.version = version;

		this.REF_FILE_PATH += this.version;
	}
	
	public SQLiteDbCreator(String refPath, String dbOutputPath, String version, boolean renew) {
		super( dbOutputPath + File.separator + "miRna_miRbase" + version + ".db", renew );
		this.version = version;

		this.REF_FILE_PATH = refPath;
	}

	public static String getCreateSecondaryStructureTableQuery() {
		String create_secondarystructure_table = "";
		create_secondarystructure_table += "CREATE TABLE a1_secondaryinfo(";
		create_secondarystructure_table += "	mirid			varchar(40)	NOT NULL PRIMARY KEY,";
		create_secondarystructure_table += "	mirna_acc		varchar(10) NOT NULL,";
		create_secondarystructure_table += "	accession		varchar(10) NOT NULL,";
		create_secondarystructure_table += "	chr				varchar(10) DEFAULT NULL,";
		create_secondarystructure_table += "	strand			enum DEFAULT NULL,";
		create_secondarystructure_table += "	_5moR			varchar(25) DEFAULT NULL,";
		create_secondarystructure_table += "	_5p				varchar(25) DEFAULT NULL,";
		create_secondarystructure_table += "	loop			varchar(25) DEFAULT NULL,";
		create_secondarystructure_table += "	_3p				varchar(25) DEFAULT NULL,";
		create_secondarystructure_table += "	_3moR			varchar(25) DEFAULT NULL,";
		create_secondarystructure_table += "	contig_start	bigint(20) DEFAULT NULL,";
		create_secondarystructure_table += "	contig_end		bigint(20) DEFAULT NULL";
		create_secondarystructure_table += ")";

		return create_secondarystructure_table;
	}

	public static String getCreateHairpinTableQuery(){
		String create_hairpin_table = "";
		create_hairpin_table += "CREATE TABLE a1_hairpin (";
		create_hairpin_table += "	id varchar(50) NOT NULL	PRIMARY KEY,			std varchar(100) DEFAULT NULL,";
		create_hairpin_table += "	type varchar(150) DEFAULT NULL,					species varchar(150) DEFAULT NULL,";
		create_hairpin_table += "	basepair varchar(10) DEFAULT NULL,				accession varchar(20) NOT NULL,";
		create_hairpin_table += "	description varchar(200) DEFAULT NULL,			comment varchar(200) DEFAULT NULL,";
		create_hairpin_table += "	feature_table_header varchar(200) DEFAULT NULL,	sequence_info varchar(200) DEFAULT NULL,";
		create_hairpin_table += "	sequence varchar(200) DEFAULT NULL,				chr varchar(10) DEFAULT NULL,";
		create_hairpin_table += "	strand varchar(1) DEFAULT NULL,					start varchar(20) DEFAULT NULL,";
		create_hairpin_table += "	end varchar(20) DEFAULT NULL,";
		create_hairpin_table += "	CONSTRAINT a1_hairpin_fk1 FOREIGN KEY (id) REFERENCES a1_mature (id),";
		create_hairpin_table += "	CONSTRAINT a1_hairpin_fk2 FOREIGN KEY (id) REFERENCES i1_pubmed (id)";
		create_hairpin_table += ")";
		
		return create_hairpin_table;
	}

	public static String getCreateHairpinIndexQuery() {
		String create_hairpin_index = "";
		create_hairpin_index = "CREATE UNIQUE INDEX a1_hairpin_idx1 on a1_hairpin(accession)";
		
		return create_hairpin_index;
	}

	public static String getCreateMatureTableQuery() {
		String query = "";

		query = "CREATE TABLE a1_mature (";
		query += "	id varchar(20) NOT NULL,	accession varchar(20) NOT NULL,";
		query += "	mirid varchar(20) DEFAULT NULL,			evidence varchar(200) DEFAULT NULL,";
		query += "	experiment varchar(200) DEFAULT NULL,	start varchar(10) DEFAULT NULL,";
		query += "	end varchar(10) DEFAULT NULL,";
		query += "	CONSTRAINT a1_mature_pk PRIMARY KEY(id, accession)";
		query += ")";
		
		return query;
	}
	
	public static String getCreateOrganismTableQuery() {
		String query = "";
		
		query = "CREATE TABLE a1_organism (";
		query += "	organism varchar(5) NOT NULL, division varchar(5) NOT NULL,";
		query += "	name varchar(200) NOT NULL, tree varchar(255) DEFAULT NULL, ";
		query += "	ncbi_taxid varchar(20) DEFAULT NULL, ";
		query += "	CONSTRAINT a1_organism_pk PRIMARY KEY(organism)";
		query += ")";
		
		return query;
	}

	public static String getCreateMatureIndexOneQuery() {
		String create_mature_index = "";
		create_mature_index = "CREATE INDEX a1_mature_idx1 on a1_mature(accession)";
		return create_mature_index;
	}
	
	public static String getCreateMatureIndexTwoQuery() {
		String create_mature_index2 = "";
		create_mature_index2 = "CREATE INDEX a1_mature_idx2 on a1_mature(mirid)";
		return create_mature_index2;
	}

	public static String getCreatePubmedTableQuery() {
		String query = "";

		query += "CREATE TABLE i1_pubmed (";
		query += "	id			varchar(20) NOT NULL,";
		query += "	pubmed_id	varchar(20) NOT NULL,";
		query += "	`order`		varchar(10) DEFAULT NULL,";
		query += "	author		varchar(200) DEFAULT NULL,";
		query += "	title		varchar(200) DEFAULT NULL,";
		query += "	journal		varchar(200) DEFAULT NULL,";
		query += "	CONSTRAINT i1_pubmed_pk PRIMARY KEY(id, pubmed_id)";
		query += ")";

		return query;
	}

	public boolean existTable( String tablename ) {

		try {
			this.getDb().beginTransaction(SqlJetTransactionMode.READ_ONLY);

			ISqlJetTable table = this.getDb().getTable( tablename );

//			ISqlJetCursor cursor = table.open();
			
			ISqlJetCursor cursor = table.lookup( table.getPrimaryKeyIndexName(), "hsa-let-7a-1");

			System.out.println( "Record count : " + cursor.getRowCount() );
			while( !cursor.eof() ) {
				Object[] obj = cursor.getRowValues();
				for(int i=0; i<obj.length; i++) {
					System.out.print( obj[i] + " " );
				}
				System.out.println("");
				cursor.next();
			}
		} catch (SqlJetException e) {
			SqlJetErrorCode code = e.getErrorCode();
			System.out.println( code );
		}
		
		return false;
	}
	
	public <E>void updateTable( String tableName, List<E> list ) {
		try {
			this.getDb().beginTransaction( SqlJetTransactionMode.WRITE );
	
			ISqlJetTable table = this.getDb().getTable( tableName );
			Iterator<E> iter = list.iterator();
	
			if( tableName.equals("A1_HAIRPIN") ) {
				while( iter.hasNext() ) {
					Object obj = iter.next();
					if( obj instanceof ChromosomalCoordinate ) {
						ChromosomalCoordinate info = (ChromosomalCoordinate)obj;

						if( info.getName().startsWith("osa") )
							System.out.println( info.getName() + " => " + info.getChromosome() );
						
						ISqlJetCursor updateCursor = table.lookup( table.getPrimaryKeyIndexName(), info.getName() );
	
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("chr", info.getChromosome());
						map.put("strand", info.getStrand());
						map.put("start", info.getStart());
						map.put("end", info.getEnd());
						
						updateCursor.updateByFieldNames( map );
					}
				}
			}
			this.commit();
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( e );
		}
	}

	@Override
	public <E>void insertTable( String tableName, List<E> list ) {
		// TODO Auto-generated method stub
		try {
			this.getDb().beginTransaction( SqlJetTransactionMode.WRITE );

			ISqlJetTable table = this.getDb().getTable( tableName );
			Iterator<E> iter = list.iterator();

			if( tableName.equals("A1_HAIRPIN") ) {
				while( iter.hasNext() ) {
					Object obj = iter.next();
					if( obj instanceof MirBaseRnaHairpinInfo ) {
						MirBaseRnaHairpinInfo info = (MirBaseRnaHairpinInfo)obj;
		
						table.insert(
								info.getId(),					info.getStd(),			info.getType(),			info.getSpecies()
								, info.getBasepair(),			info.getAccession(),	info.getDescription(),	info.getComment()
								, info.getFeatureTableHeader(),	info.getSequenceInfo(), info.getSequence(),		""
								, "",							"",						""
						);
					}
//					else if( obj instanceof ChromosomalCoordinate ) {
//						ChromosomalCoordinate info = (ChromosomalCoordinate)obj;
//						
//						ISqlJetCursor updateCursor = table.lookup( table.getPrimaryKeyIndexName(), info.getName() );
//
//						Map<String, Object> map = new HashMap<String, Object>();
//						map.put("chr", info.getChromosome());
//						map.put("strand", info.getStrand());
//						map.put("start", info.getStart());
//						map.put("end", info.getEnd());
//						
//						updateCursor.updateByFieldNames( map );
//					}
				}
			}else if( tableName.equals("A1_MATURE") ) {
				while( iter.hasNext() ) {
					Object obj = iter.next();

					if( obj instanceof MirBaseRnaHairpinInfo ) {
						((MirBaseRnaHairpinInfo) obj).reGenerateMatureInfo();

						List<MirBaseRnaMatureInfo> matures = ((MirBaseRnaHairpinInfo) obj).getMatureData();
						Iterator<MirBaseRnaMatureInfo> nIter = matures.iterator();
						while( nIter.hasNext() ) {
							MirBaseRnaMatureInfo info = nIter.next();
							table.insert( 
									((MirBaseRnaHairpinInfo) obj).getId(), info.getAccession(), info.getMirid(), info.getEvidence()
									, info.getExperiment(), info.getStart(), info.getEnd() 
							);
						}
					}
				}
			}else if( tableName.equals("I1_PUBMED") ) {
				while( iter.hasNext() ) {
					Object obj = iter.next();

					if( obj instanceof MirBaseRnaHairpinInfo ) {
						List<PubmedInfo> pubmeds = ((MirBaseRnaHairpinInfo)obj).getPubmedInfoList();
						Iterator<PubmedInfo> nIter = pubmeds.iterator();
						while( nIter.hasNext() ) {
							PubmedInfo info = nIter.next();
							table.insert( 
									((MirBaseRnaHairpinInfo) obj).getId(), info.getPubmed()
									, info.getOrder(), info.getAuthors(), info.getTitle(), info.getJournal()
							);
						}
					}
				}
			}else if( tableName.equals("A1_SECONDARYINFO") ) {
				while( iter.hasNext() ) {
					RnaSecondaryStructureInfo obj = (RnaSecondaryStructureInfo)iter.next();

					if( obj instanceof RnaSecondaryStructureInfo ) {
						table.insert( 
								obj.getMirid(),				obj.getMirna_acc()
								, obj.getAccession(),		obj.getChr()
								, obj.getStrand(),			obj.get_5moR()
								, obj.get_5p(),				obj.getLoop()
								, obj.get_3p(), 			obj.get_3moR()
								, obj.getContig_start(),	obj.getContig_end()
						);
					}
				}
			}else if( tableName.equals("A1_ORGANISM") ) {
				while( iter.hasNext() ) {
					MirBaseOrganismInfo obj = (MirBaseOrganismInfo)iter.next();

					if( obj instanceof MirBaseOrganismInfo ) {
						table.insert( 
								obj.getOrganism(),	obj.getDivision()
								, obj.getName(), 	obj.getTree()
								, obj.getNcbi_taxid()
						);
					}
				}
			}

			this.commit();
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( e );
		}
	}
	
	public String getRefFilePath() {
		return this.REF_FILE_PATH;
	}

	public List<MatureVO> getMicroRnaMaturesByMirid(String hairpin_id) {
		List<MatureVO> matures = new ArrayList<MatureVO>();
		try {
			this.getDb().beginTransaction(SqlJetTransactionMode.READ_ONLY);
			ISqlJetTable table = this.getDb().getTable( "A1_MATURE" );
//			ISqlJetCursor cursor = table.open();
			ISqlJetCursor cursor = table.lookup( table.getPrimaryKeyIndexName(), hairpin_id );
			
			do{
				String id		= cursor.getString("ID");
				if( id.equals(hairpin_id) ) {
					String mirid		= cursor.getString("MIRID");
					int start			= Integer.parseInt( Utilities.nulltoOne( cursor.getString("START") ) );
					int end				= Integer.parseInt( Utilities.nulltoOne( cursor.getString("END") ) );

//					SequenceObject hairpin = this.getMicroRnaHairpinByMirid( id );
//					String mature_seq	= hairpin.getSequenceByString().subSequence(start-1, end-1).toString();
					
					MatureVO mature = new MatureVO();
					mature.setId( id );
					mature.setMirid( mirid );
					mature.setStart( start );
					mature.setEnd( end );
//					mature.setSequence( mature_seq );
					
					matures.add( mature );
				}

				cursor.next();
			}while( !cursor.eof() );
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error( "error : ", e );
		} 
		return matures;
	}

	@SuppressWarnings("unused")
	public List<RnaSecondaryStructureInfo> getMicroRnaHairpinByMirid() {
		ISqlJetCursor cursor = null;

		List<RnaSecondaryStructureInfo> list = new ArrayList<RnaSecondaryStructureInfo>();
		try {
			this.getDb().beginTransaction( SqlJetTransactionMode.READ_ONLY );
			ISqlJetTable table = this.getDb().getTable( "A1_HAIRPIN" );
			cursor = table.open();

			do{
				String id			= cursor.getString("ID");
				String accession	= cursor.getString("ACCESSION");
				String chromosome	= cursor.getString("CHR");
				String strand		= cursor.getString("STRAND");
				String start		= cursor.getString("START");
				String end			= cursor.getString("END");
				String sequence		= cursor.getString("SEQUENCE");

				List<MatureVO> matureList = this.getMicroRnaMaturesByMirid( id );

				int contig_start	= Integer.parseInt( Utilities.emptyToZero( Utilities.nulltoZero(start) ) );
				int contig_end		= Integer.parseInt( Utilities.emptyToZero( Utilities.nulltoZero(end) ) );
				int half = contig_start + (contig_end - contig_start)/2;

				if( matureList.size() == 1 ) {
					MatureVO mature = matureList.get(0);

					System.out.println( id + " ==> " + mature.getMirid() );
					if( strand.equals("+") ) {
						int _5p_start = mature.getStart() + contig_start - 1;
						int _5p_end = mature.getEnd() + contig_start - 1;

						int _3p_start =mature.getStart() + contig_start - 1;
						int _3p_end =mature.getEnd() + contig_start - 1;

						int k1 = Math.abs(half-_5p_start);
						int k2 = Math.abs(_3p_end - half);
						
						System.out.println( k1 + ", " + k2 + "("+half+")"  + " =========>("+mature.getStart() + "-" + mature.getEnd() +") (" + (contig_start-contig_start+1) + "-" + (contig_end - contig_start +1) + ")");
					}else {
						int _5p_start =  contig_end - mature.getEnd() + 1;
						int _5p_end = contig_end - mature.getStart() + 1;

						int _3p_start =  contig_end - mature.getEnd() + 1;
						int _3p_end = contig_end - mature.getStart() + 1;
					}
				}
//				System.out.println( id + " ==> " + matureList.size() );
//				RnaSecondaryStructureInfo info = new RnaSecondaryStructureInfo();
			}while( cursor.next() );
		}catch( SqlJetException e) {
			MsbEngine.logger.error( "Error", e );
			throw new RuntimeException(e);
		}finally {
			try {
				this.getDb().commit();
				if(cursor != null) cursor.close();
				MsbEngine.logger.debug( "Close cursor" );
			} catch (SqlJetException e) {
				// TODO Auto-generated catch block
				MsbEngine.logger.error( "Error", e );
				throw new RuntimeException(e);
			}
		}
		
		return list;
//
//		try {
//			this.getDb().beginTransaction(SqlJetTransactionMode.READ_ONLY);
//			ISqlJetTable table = this.getDb().getTable( "A1_HAIRPIN" );
//			ISqlJetCursor cursor = table.lookup( table.getPrimaryKeyIndexName(), mirid );
//
//			List<SequenceObject> hairpins = new ArrayList<SequenceObject>();
//
//			while( !cursor.eof() ){
//				String id			= cursor.getString("ID");
//				String accession	= cursor.getString("ACCESSION");
//				String sequence		= cursor.getString("SEQUENCE");
//				String chr			= cursor.getString("CHR");
//				String strand		= cursor.getString("STRAND");
//
//				/****************************************************************************
//				 * If there are not genomic position and strand information,
//				 * we do like that genomic prosition start 0 and strand '+'
//				 */
//				long startPos = 0;
//				String strStartPos	= cursor.getString("START");
//				if( !strStartPos.isEmpty() )	startPos = Long.parseLong( strStartPos );
//				long length			= sequence.length();
//				
//				if( strand.isEmpty() )	strand = "+";
//
//				SequenceObject vo = new SequenceObject( chr, startPos, length, strand.charAt(0), sequence );
//				hairpins.add( vo );
//				cursor.next();
//			}
//			if( hairpins.size() == 0 )	return null;
//			else						return hairpins.get(0);
//		} catch (SqlJetException e) {
//			// TODO Auto-generated catch block
//			MsbEngine.logger.error( "error : ", e );
//		}
//		return null;
	}
	
	public static void main(String[] args) {
		SQLiteDbCreator sdc = new SQLiteDbCreator("/Users/lion/git/mirseqviewer/mirseqviewer/resources/data/mirbase/mirbase18", "/Users/lion/Desktop", "18", false);
		
//		sdc.getMicroRnaHairpinByMirid();

		MirBaseLoader loader = new MirBaseLoader();

		try {
			System.out.println("Reading miRNA.data file...");
			List<MirBaseRnaHairpinInfo> list = loader.loadMiRnaDataFromMirBase( sdc.getRefFilePath() + File.separator + "miRNA.dat" );
			
			{
				System.out.println("A1_HAIRPIN Table creating...");
				// A1_HAIRPIN table 생성
				sdc.createTable( SQLiteDbCreator.getCreateHairpinTableQuery() );

				System.out.println("Insert records to A1_HAIRPIN table");
				sdc.insertTable( "A1_HAIRPIN", list );
				
				System.out.println("Index A1_HAIRPIN Table");
				// A1_HAIRPIN_IDX1 인덱스 생성
				sdc.createIndex( SQLiteDbCreator.getCreateHairpinIndexQuery() );

				File dir = new File( sdc.getRefFilePath() + File.separator + "genomes" );
									 
				System.out.println("Reading gff files and update A1_HAIRPIN table");
				if( dir.isDirectory() ) {
					String ext = "gff3";
					if( Integer.valueOf( sdc.getVersion() ) < 18 )
						ext = "gff2";

					File[] fileList = dir.listFiles( new GffFilenameFilter(ext) );
					GffLoader loader2 = new GffLoader();
					Gff3Loader loader3 = new Gff3Loader();

					for(int i=0; i<fileList.length; i++) {
						if( fileList[i].getAbsolutePath().endsWith(".gff3") ) {
							List<ChromosomalCoordinate> list3 = loader3.loadGffFileFromMirbase( fileList[i].getAbsolutePath() );
							sdc.updateTable( "A1_HAIRPIN", list3 );
						}else if( fileList[i].getAbsolutePath().endsWith(".gff2") ) {
							List<ChromosomalCoordinate> list2 = loader2.loadGffFileFromMirbase( fileList[i].getAbsolutePath() );
							sdc.updateTable( "A1_HAIRPIN", list2 );
						}
					}
				}
			}
			
			{
				sdc.createTable( SQLiteDbCreator.getCreateMatureTableQuery() );
				sdc.insertTable( "A1_MATURE", list);
				sdc.createIndex( SQLiteDbCreator.getCreateMatureIndexOneQuery() );
				sdc.createIndex( SQLiteDbCreator.getCreateMatureIndexTwoQuery() );
				System.out.println( "A1_MATURE is created");
			}

			{
				sdc.createTable( SQLiteDbCreator.getCreatePubmedTableQuery() );
				
				sdc.insertTable("I1_PUBMED", list);
				System.out.println( "I1_PUBMED is created");
			}

			{
//				SecondaryStructureFileLoader loader3 = new SecondaryStructureFileLoader();
//				List<RnaSecondaryStructureInfo> list3 = loader3.readFile( sdc.getRefFilePath() + File.separator + "secondarystructure.txt" );
//				sdc.createTable( SQLiteDbCreator.getCreateSecondaryStructureTableQuery() );
				
//				sdc.insertTable("A1_SECONDARYINFO", list3);
				
				

			}
			
			{
				MirBaseOrganismFileLoader loader4 = new MirBaseOrganismFileLoader();
				List<MirBaseOrganismInfo> list4 = loader4.loadOrganismDataFromMirBase( sdc.getRefFilePath() + File.separator + "organisms.txt" );
				
				sdc.createTable( SQLiteDbCreator.getCreateOrganismTableQuery() );
				
				sdc.insertTable("A1_ORGANISM",  list4);
				System.out.println( "A1_ORGANISM is created");
			}

			sdc.existTable( "A1_HAIRPIN" );
			sdc.existTable( "A1_SECONDARYINFO" );
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			MsbEngine.logger.error( e );
		}

		sdc.dbClose();
	}
	
	public String getVersion() {
		return this.version;
	}
}