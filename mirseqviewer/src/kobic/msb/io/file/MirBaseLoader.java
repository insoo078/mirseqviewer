package kobic.msb.io.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kobic.msb.db.RecordReader;
import kobic.msb.db.RecordWriter;
import kobic.msb.db.RecordsFile;
import kobic.msb.db.RecordsFileException;
import kobic.msb.io.file.obj.mirbase.MirBaseRnaHairpinInfo;
import kobic.msb.io.file.obj.mirbase.PubmedInfo;

public class MirBaseLoader {
	@SuppressWarnings("resource")
	public List<MirBaseRnaHairpinInfo> loadMiRnaDataFromMirBase( String file ) throws FileNotFoundException {
//		String file = "/Users/lion/Downloads/miRNA.dat";

		List<MirBaseRnaHairpinInfo> list = new ArrayList<MirBaseRnaHairpinInfo>();

		FileReader fr = null;
		try{
			fr = new FileReader( file );	//open
			BufferedReader br = new BufferedReader(fr);
			
			MirBaseRnaHairpinInfo info = new MirBaseRnaHairpinInfo();
			String line = "";
			while( (line = br.readLine()) != null ) {
				Pattern p = Pattern.compile( "^(\\w+)" );
		        Matcher m = p.matcher( line );

		        String keyword = "";
		        while( m.find() ) {
		        	keyword = m.group(1);
		        }

		        // When keyword is termination characters
		        if( keyword.isEmpty() && line.startsWith("//") ) {
		        	list.add( info );

		        	info = new MirBaseRnaHairpinInfo();
		        }else {
		        	info = this.getLine( info, keyword, line );
		        }
		    }
		}catch(Exception e) {
			System.out.println( e.toString() );
		}
		
		return list;
	}
	
	public MirBaseRnaHairpinInfo getLine( MirBaseRnaHairpinInfo info, String keyword, String line ) {
		String regexpress = "";
		Pattern p = null;
		if( keyword.equals("XX") ) {
			return info;
		}else if( keyword.equals("ID") ) {
			regexpress = "^ID\\s+(\\S+)\\s+(\\S+);\\s+(\\S+);\\s+(\\S+);\\s+(\\d+)";
			
			p = Pattern.compile( regexpress );
	        Matcher m = p.matcher( line );
	
	        while( m.find() ) {
		    	String id		= m.group(1);
		    	String std		= m.group(2);
		    	String type		= m.group(3);
		    	String species	= m.group(4);
		    	String basepair	= m.group(5);

		    	info.setId( id );
		    	info.setStd( std );
		    	info.setType( type );
		    	info.setSpecies( species );
		    	info.setBasepair( basepair );
		    }
		}else if( keyword.equals("AC") ) {
			regexpress = "^AC\\s+(\\S+);";
			p = Pattern.compile( regexpress );
	        Matcher m = p.matcher( line );
	
	        while( m.find() ) {
		    	String accession		= m.group(1);

		    	info.setAccession( accession );
		    }
		}else if( keyword.equals("DE") ) {
			regexpress = "^DE\\s+(.+)";
			p = Pattern.compile( regexpress );
	        Matcher m = p.matcher( line );
	
	        while( m.find() ) {
		    	String description		= m.group(1);

		    	info.setDescription( description );
		    }
		}else if( keyword.equals("RN") ) {
			regexpress = "^RN\\s+\\[(\\d+)\\]";
			p = Pattern.compile( regexpress );
	        Matcher m = p.matcher( line );
	
	        while( m.find() ) {
		    	String pubmed_ref_no		= m.group(1);

		    	PubmedInfo pubmedInfo = new PubmedInfo();
		    	pubmedInfo.setOrder( Integer.parseInt( pubmed_ref_no ) );
		    	info.addPubmedInfo( pubmedInfo );
		    }
		}else if( keyword.equals("RX") ) {
			regexpress = "^RX\\s+PUBMED;\\s+(\\S+).";
			p = Pattern.compile( regexpress );
	        Matcher m = p.matcher( line );
	
	        while( m.find() ) {
		    	String pubmed_id		= m.group(1);

		    	PubmedInfo lastPubmedInfo = info.getPubmedInfoList().get( info.getPubmedInfoList().size() -1 );
		    	lastPubmedInfo.setPubmed( pubmed_id );
		    }
		}else if( keyword.equals("RA") ) {
			regexpress = "^RA\\s+(.+)";
			p = Pattern.compile( regexpress );
	        Matcher m = p.matcher( line );
	
	        while( m.find() ) {
		    	String authors		= m.group(1);

		    	PubmedInfo lastPubmedInfo = info.getPubmedInfoList().get( info.getPubmedInfoList().size() -1 );
		    	lastPubmedInfo.setAuthors( (lastPubmedInfo.getAuthors() + " " + authors).trim() );
		    }
		}else if( keyword.equals("RT") ) {
			regexpress = "^RT\\s+(.+)";
			p = Pattern.compile( regexpress );
	        Matcher m = p.matcher( line );
	
	        while( m.find() ) {
		    	String title		= m.group(1);


		    	PubmedInfo lastPubmedInfo = info.getPubmedInfoList().get( info.getPubmedInfoList().size() -1 );
		    	lastPubmedInfo.setTitle( (lastPubmedInfo.getTitle() + " " + title).trim() );
		    }
		}else if( keyword.equals("RL") ) {
			regexpress = "^RL\\s+(.+)";
			p = Pattern.compile( regexpress );
	        Matcher m = p.matcher( line );
	
	        while( m.find() ) {
		    	String journal		= m.group(1);

		    	PubmedInfo lastPubmedInfo = info.getPubmedInfoList().get( info.getPubmedInfoList().size() -1 );
		    	lastPubmedInfo.setJournal( (lastPubmedInfo.getJournal() + " " + journal).trim() );
		    }
		}else if( keyword.equals("DR") ) {
			regexpress = "^DR\\s+(.+)";
			p = Pattern.compile( regexpress );
	        Matcher m = p.matcher( line );
	
	        while( m.find() ) {
		    	String databaseCrossRef		= m.group(1);

		    	info.addDatabaseCrossReference( databaseCrossRef );
		    }
		}else if( keyword.equals("CC") ) {
			regexpress = "^CC\\s+(.+)";
			p = Pattern.compile( regexpress );
	        Matcher m = p.matcher( line );
	
	        while( m.find() ) {
		    	String comment		= m.group(1);

		    	info.setComment( (info.getComment() + "" + comment).trim() );
		    }
		}else if( keyword.equals("FH") ) {
			regexpress = "^FH\\s+(.+)";
			p = Pattern.compile( regexpress );
	        Matcher m = p.matcher( line );
	
	        while( m.find() ) {
		    	String featureTableHeader		= m.group(1);

		    	info.setFeatureTableHeader( (info.getFeatureTableHeader() + "" + featureTableHeader).trim() );
		    }
		}else if( keyword.equals("FT") ) {
			regexpress = "^FT\\s+(.+)";
			p = Pattern.compile( regexpress );
	        Matcher m = p.matcher( line );
	
	        while( m.find() ) {
		    	String featureTableData		= m.group(1);

		    	info.addStrFeatureTableData( featureTableData );
		    }
		}else{
			if( !keyword.equals("SQ") ) {
				regexpress = "^\\s+(.+)\\s+(\\d+)";
				
				p = Pattern.compile( regexpress );
		        Matcher m = p.matcher( line );
		
		        while( m.find() ) {
			    	String sequence		= m.group(1);

			    	info.setSequence( info.getSequence() + sequence.replaceAll(" ", "").toUpperCase() );
			    }
			}else {
				info.setSequenceInfo( line );
			}
		}
		
        return info;
	}

	private String writeIndexAboutAccession( String directory, String name, List<MirBaseRnaHairpinInfo> list ) throws IOException, RecordsFileException {
		String fullpath = directory + File.separator + name + ".db";

		RecordsFile recordsFile = new RecordsFile( fullpath, 64 );

		int i = 0;
		Iterator<MirBaseRnaHairpinInfo> iter = list.iterator();
		while( iter.hasNext() ) {
			MirBaseRnaHairpinInfo info = iter.next();

			RecordWriter rw = new RecordWriter( info.getAccession() );
			rw.writeObject( Integer.toString(i) );
			recordsFile.insertRecord( rw );
			i++;
			System.out.println( "Accession ) " + info.getAccession() + " ======> " + Integer.toString(i) );
		}
		return fullpath;
	}

	private String writeIndexAboutId( String directory, String name, List<MirBaseRnaHairpinInfo> list ) throws IOException, RecordsFileException {
		String fullpath = directory + File.separator + name + ".db";

		RecordsFile recordsFile = new RecordsFile( fullpath, 64 );

		int i = 0;
		Iterator<MirBaseRnaHairpinInfo> iter = list.iterator();
		while( iter.hasNext() ) {
			MirBaseRnaHairpinInfo info = iter.next();

			RecordWriter rw = new RecordWriter( info.getId() );
			rw.writeObject( Integer.toString(i) );
			recordsFile.insertRecord( rw );
			i++;
			System.out.println( "ID ) " +  info.getId() + " ======> " + Integer.toString(i) );
		}
		return fullpath;
	}
	
	private String writeIndexAboutBase( String directory, String name, List<MirBaseRnaHairpinInfo> list ) throws IOException, RecordsFileException {
		String fullpath = directory + File.separator + name + ".db";

		RecordsFile recordsFile = new RecordsFile( fullpath, 64 );

		int i = 0;
		Iterator<MirBaseRnaHairpinInfo> iter = list.iterator();
		while( iter.hasNext() ) {
			MirBaseRnaHairpinInfo info = iter.next();

			RecordWriter rw = new RecordWriter( Integer.toString(i) );
			rw.writeObject( info );
			recordsFile.insertRecord( rw );
			i++;
			System.out.println( "Main ) " +  info.getId() + " ======> " + Integer.toString(i) );
		}
		return fullpath;
	}
	
	private List<MirBaseRnaHairpinInfo> reGenerateMatureInfo( List<MirBaseRnaHairpinInfo> list ) {
		Iterator<MirBaseRnaHairpinInfo> iter = list.iterator();
		while( iter.hasNext() ) {
			MirBaseRnaHairpinInfo info = iter.next();
			info.reGenerateMatureInfo();
		}

		return list;
	}

	@SuppressWarnings("unused")
	public String writeToFile( String directory, String filename, List<MirBaseRnaHairpinInfo> list) throws IOException, RecordsFileException {
		this.reGenerateMatureInfo(list);

		String idxAccessionUrl	= this.writeIndexAboutAccession(	directory, filename+"_accession",	list );
		String idxIdUrl			= this.writeIndexAboutId(			directory, filename+"_id",			list );
		String idxBaseUrl		= this.writeIndexAboutBase(			directory, filename,				list );

		return directory;
	}

	public MirBaseRnaHairpinInfo readFromFile( String directory, String name, String keyword ) throws IOException, ClassNotFoundException, RecordsFileException {
		String fullpath = directory + File.separator + name;
		RecordsFile recordsFile = new RecordsFile( fullpath + "_accession.db", "r" );
		
		RecordReader rr = null;

		long start = System.currentTimeMillis();
		try {
			long s1 = System.currentTimeMillis();
			rr = recordsFile.readRecord( keyword );
			long s2 = System.currentTimeMillis();
			System.out.println( "1) " + (s2-s1) + "sec" );
		}catch(Exception e) {
			try {
				RecordsFile recordsIdFile = new RecordsFile( fullpath + "_id.db", "r" );
				
				rr = recordsIdFile.readRecord( keyword );
			}catch(Exception ie) {
				return null;
			}
		}

		if( rr!= null ) {
			try {
				long s1 = System.currentTimeMillis();
				String id = (String)rr.readObject();
				long s2 = System.currentTimeMillis();
				System.out.println( "2) " + (s2-s1) + "sec" );
				
				s1 = System.currentTimeMillis();
				RecordsFile recordsBaseFile = new RecordsFile( fullpath + ".db", "r" );
				s2 = System.currentTimeMillis();
				System.out.println( "3) " + (s2-s1) + "sec" );
				
				s1 = System.currentTimeMillis();
				rr = recordsBaseFile.readRecord( id );
				s2 = System.currentTimeMillis();
				System.out.println( "4) " + (s2-s1) + "sec" );
				
				long end = System.currentTimeMillis();
				System.out.println("Total : " + (end-start) + "sec");
				 
				return (MirBaseRnaHairpinInfo)rr.readObject();
			}catch(Exception iie) {
				return null;
			}
		}else
			return null;
	}

	/*
    ID - identification             (begins each entry; 1 per entry)
    AC - accession number           (>=1 per entry)
    PR - project identifier         (0 or 1 per entry)
    DT - date                       (2 per entry)
    DE - description                (>=1 per entry)
    KW - keyword                    (>=1 per entry)
    OS - organism species           (>=1 per entry)
    OC - organism classification    (>=1 per entry)
    OG - organelle                  (0 or 1 per entry)
    RN - reference number           (>=1 per entry)
    RC - reference comment          (>=0 per entry)
    RP - reference positions        (>=1 per entry)
    RX - reference cross-reference  (>=0 per entry)
    RG - reference group            (>=0 per entry)
    RA - reference author(s)        (>=0 per entry)
    RT - reference title            (>=1 per entry)
    RL - reference location         (>=1 per entry)
    DR - database cross-reference   (>=0 per entry)
    CC - comments or notes          (>=0 per entry)
    AH - assembly header            (0 or 1 per entry)   
    AS - assembly information       (0 or >=1 per entry)
    FH - feature table header       (2 per entry)
    FT - feature table data         (>=2 per entry)    
    XX - spacer line                (many per entry)
    SQ - sequence header            (1 per entry)
    CO - contig/construct line      (0 or >=1 per entry) 
    bb - (blanks) sequence data     (>=1 per entry)
	*/

	public static void main(String[] args) throws IOException, ClassNotFoundException, RecordsFileException {
//		String input = "";
//		String output = "";
//		for(int i=0; i<args.length; i+=2) {
//			if( args[i].equals("-i") )		input	= args[i+1];
//			else if( args[i].equals("-o") )	output	= args[i+1];
//		}
////		System.out.println( input + " ==> " + output );
//
		MirBaseLoader loader = new MirBaseLoader();

		List<MirBaseRnaHairpinInfo> list = loader.loadMiRnaDataFromMirBase( "/Users/lion/Downloads/miRNA.dat" );

		loader.writeToFile( "/Users/lion/Desktop", "mirbase", list);

		MirBaseRnaHairpinInfo info = loader.readFromFile( "/Users/lion/Desktop", "mirbase", "MI0000002" );
		
		System.out.println( info.getAccession() + " " + info.getBasepair() + " " + info.getComment() + " " + info.getSequenceInfo() );
	}
}
