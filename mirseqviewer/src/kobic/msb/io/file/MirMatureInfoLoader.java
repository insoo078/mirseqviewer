package kobic.msb.io.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kobic.msb.io.file.obj.mirbase.MirBaseRnaHairpinInfo;

public class MirMatureInfoLoader {
	@SuppressWarnings("resource")
	public List<MirBaseRnaHairpinInfo> loadMiRnaDataFromMirBase( String file ) throws FileNotFoundException {
//		String file = "/Users/lion/Downloads/miRNA.dat";

//		List<MirBaseRnaHairpinInfo> list = new ArrayList<MirBaseRnaHairpinInfo>();

		FileReader fr = null;
		try{
			fr = new FileReader( file );	//open
			BufferedReader br = new BufferedReader(fr);
			
			@SuppressWarnings("unused")
			MirBaseRnaHairpinInfo info = new MirBaseRnaHairpinInfo();
			String line = "";
			while( (line = br.readLine()) != null ) {
//				String[] part = line.split("\\t");
//				if( part.length > 8 )
//					System.out.println( part.length + " ===> " + part[0] + " " + part[1] + " " + part[2] + " " + part[3] + " " + part[4] + " " + part[7] + " " + part[8]);
//				else
//					System.out.println( part.length + " ===> " + part[0] + " " + part[1] + " " + part[2] + " " + part[3] + " " + part[4] + " " + part[6] + " " + part[7]);
//				75449	hsa-miR-182-3p	hsa-miR-182*	MIMAT0000260	67	87	not_experimental	\N	MI0001368
				Pattern p = Pattern.compile( "^(\\d+)\\s+(hsa-\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)" );
		        Matcher m = p.matcher( line );

		        while( m.find() ) {
		        	if( m.group(3).startsWith("MIMAT") )
		        		System.out.println( "CASE 1 : " + m.group(3) + " " + m.group(4) + " " + m.group(5) );
		        	else
		        		System.out.println( "CASE 2 : " + m.group(4) + " " + m.group(5) + " " + m.group(6) );
		        }

//
//		        // When keyword is termination characters
//		        if( keyword.isEmpty() && line.startsWith("//") ) {
//		        	list.add( info );
//
//		        	info = new MirBaseRnaHairpinInfo();
//		        }else {
//		        	info = this.getLine( info, keyword, line );
//		        }
		    }
		}catch(Exception e) {
			System.out.println( e.toString() );
		}
		
		return null;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		MirMatureInfoLoader loader = new MirMatureInfoLoader();
		loader.loadMiRnaDataFromMirBase( "/Users/lion/Documents/workspace-sts-2.8.1.RELEASE/MirSeqBrowser/src/kobic/msb/data/mirbase/mirna_mature.txt" );
	}
}
