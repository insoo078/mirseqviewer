package kobic.msb.io.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kobic.com.util.Utilities;
import kobic.msb.io.file.obj.bed.BedBrowser;
import kobic.msb.io.file.obj.bed.BedFormat;
import kobic.msb.io.file.obj.bed.BedFragment;
import kobic.msb.io.file.obj.bed.BedTrack;

public class BedFileReader extends BufferedReader{
	public BedFileReader( Reader in ) {
		super(in);
		// TODO Auto-generated constructor stub
	}
	
	public BedFormat readBedFile() throws NumberFormatException, IOException{
		BedBrowser browser = new BedBrowser();
		BedTrack track = new BedTrack();
		
		BedFormat bedObj = new BedFormat();

		String line = "";
		while ((line = this.readLine()) != null) {
			if( line.startsWith("#") ) {
				continue;
			}else if( line.startsWith("browser") ) {
				String[] divs = line.split("\\s");
				if( divs.length == 3 )	browser.addAttributes( divs[1], divs[2] );
			}else if( line.startsWith("track") ) {
				String txtReg = "=\"([\\w+\\s+]+)\"";
				String valReg = "=([\\w+\\s+]+)";
				
				String[] words = new String[]{"name", "description", "type", "visibility", "color", "itemRgb", "colorByStrand", 
						"group", "priority", "db", "offset", "maxItems", "url", "htmlUrl", "bigDataUrl"
				};
				Pattern pattern = null;
				for(int i=0; i<words.length; i++) {
					if( words[i].equals("name") || words[i].equals("description") )	pattern = Pattern.compile( words[i] + txtReg );
					else															pattern = Pattern.compile( words[i] + valReg );

					Matcher matcher = pattern.matcher( line );
					while( matcher.find() ) {
						track.addAttributes( words[i], matcher.group(1) );
					}
				}
			}else {
				BedFragment bedFrag = new BedFragment();
				line = line.replace("  ", " ");
				String[] divs = line.split("\\s");
				for(int i=0 ;i<divs.length; i++) {
					if( i==0 )		bedFrag.setChrom( divs[0] );
					else if( i==1 )	bedFrag.setChromStart( Integer.parseInt( Utilities.nulltoZero( divs[1] ) ) );
					else if( i==2 )	bedFrag.setChromEnd( Integer.parseInt( Utilities.nulltoZero( divs[2] ) ) );
					else if( i==3 )	bedFrag.setName( divs[3] );
					else if( i==4 )	bedFrag.setScore( Integer.parseInt( Utilities.nulltoZero( divs[4] ) ) );
					else if( i==5 )	bedFrag.setStrand( divs[5] );
					else if( i==6 )	bedFrag.setThickStart( divs[6] );
					else if( i==7 )	bedFrag.setThickEnd( divs[7] );
					else if( i==8 )	bedFrag.setItemRgb(  divs[8] );
				}
				bedObj.addBedFragment( bedFrag );
			}
		}
		bedObj.setBrowser( browser );
		bedObj.setTrack( track );

		return bedObj;
	}
	
//	public static void main(String[] args) throws Exception {
//		BedFileReader bfr = new BedFileReader( new FileReader( "/Users/lion/Desktop/test.bed") );
//		
//		BedFormat format = bfr.readBedFile();
//
//		System.out.println( format.toString() );
//		bfr.close();
//	}
}
