package kobic.msb.io.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kobic.msb.db.RecordWriter;
import kobic.msb.db.RecordsFile;
import kobic.msb.db.RecordsFileException;
import kobic.msb.io.file.obj.mirbase.ChromosomalCoordinate;

public class GffLoader {
	@SuppressWarnings("resource")
	public List<ChromosomalCoordinate> loadGffFileFromMirbase(String file) {
//		String file = "/Users/lion/Desktop/genomes/hsa.gff2";
	
		List<ChromosomalCoordinate> list = new ArrayList<ChromosomalCoordinate>();
	
		FileReader fr = null;
		try{
			fr = new FileReader( file );	//open
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while( (line = br.readLine()) != null ) {
				if( line.startsWith("#") )	continue;

	        	String[] strs = line.split("\t");

	        	ChromosomalCoordinate cco = new ChromosomalCoordinate();

	        	cco.setChromosome(	strs[0] );
	        	cco.setStart(		strs[3] );
	        	cco.setEnd(			strs[4] );
	        	cco.setStrand(		strs[6] );

				Pattern p = Pattern.compile( "ACC=\"(\\S+)\";\\s+ID=\"(\\S+)\";" );
		        Matcher m = p.matcher( line );

		        while( m.find() ) {
		        	cco.setAccession( m.group(1) );
		        	cco.setName( m.group(2) );
		        }
		        list.add( cco );
			}
		}catch(Exception e){
			System.out.println( e.toString() );
		}
		return list;
	}

	public String writeIndexAbout( String directory, String name, List<ChromosomalCoordinate> list ) throws IOException, RecordsFileException {
		String fullpath = directory + File.separator + name + ".db";

		RecordsFile recordsFile = new RecordsFile( fullpath, 64 );

		Iterator<ChromosomalCoordinate> iter = list.iterator();
		while( iter.hasNext() ) {
			ChromosomalCoordinate info = iter.next();

			System.out.println( info.getAccession() + " " + info.getName() + " " + info.getChromosome() + " " + info.getStrand() );
			RecordWriter rw = new RecordWriter( info.getAccession() );
			rw.writeObject( info );
			recordsFile.insertRecord( rw );
		}
		return fullpath;
	}

	public Map<String, ChromosomalCoordinate> makeIndex( List<ChromosomalCoordinate> list ) {
		Iterator<ChromosomalCoordinate> iter = list.iterator();
		
		Map<String, ChromosomalCoordinate> map = new Hashtable<String, ChromosomalCoordinate>();
		while( iter.hasNext() ) {
			ChromosomalCoordinate cco = iter.next();

			String id = cco.getName();

			map.put(id,  cco);
		}
		return map;
	}

	public static void main(String[] args) throws IOException, RecordsFileException {
		GffLoader loader = new GffLoader();

		long a = System.currentTimeMillis();
		List<ChromosomalCoordinate> list = loader.loadGffFileFromMirbase( "/Users/lion/Desktop/genomes/hsa.gff2" );
		Iterator<ChromosomalCoordinate> iter = list.iterator();

		long b = System.currentTimeMillis();
		System.out.println( "read : " + (b-a) + "sec." );
		while( iter.hasNext() ) {
			ChromosomalCoordinate cc = iter.next();
			
			if( cc.getAccession().contains( "MI0022552" ) )
				System.out.println( cc.getAccession() + " " + cc.getName() + " " + cc.getStrand() + " " + cc.getStart() + " " + cc.getEnd() );
		}
		long c = System.currentTimeMillis();
		System.out.println( "find : " + (c-b) + "sec." );
//		String url = loader.writeIndexAbout("/Users/lion/Desktop", "hsa", list);
//		Map<String, ChromosomalCoordinate> map = loader.makeIndex( list );
	}
}