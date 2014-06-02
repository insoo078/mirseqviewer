package kobic.msb.io.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import kobic.msb.io.file.obj.mirbase.RnaSecondaryStructureInfo;

public class SecondaryStructureFileLoader {
	@SuppressWarnings("resource")
	public List<RnaSecondaryStructureInfo> readFile(String file ) throws FileNotFoundException {
		List<RnaSecondaryStructureInfo> list = new ArrayList<RnaSecondaryStructureInfo>();

		FileReader fr = null;
		try{
			fr = new FileReader( file );	//open
			BufferedReader br = new BufferedReader(fr);
			
			String line = "";
			while( (line = br.readLine()) != null ) {
				RnaSecondaryStructureInfo info = new RnaSecondaryStructureInfo();
				String[] divs = line.replace("\"", "").replace("NA", "").split("\\|");
				
				info.setMirid(			divs[0] );
				info.setMirna_acc(		divs[1] );
				info.setAccession(		divs[2] );
				info.setChr(			divs[3] );
				info.setStrand(			divs[4] );
				info.set_5moR(			divs[5] );
				info.set_5p(			divs[6] );
				info.setLoop(			divs[7] );
				info.set_3p(			divs[8] );
				info.set_3moR(			divs[9] );
				info.setContig_start(	divs[10] );
				info.setContig_end(		divs[11] );

				if( divs[0].equals("hsa-let-7a-1") )
					System.out.println( divs[0] + " " +divs[1] + " " +divs[2] + " " +divs[3] + " " +divs[4] + " " +divs[5] + " " +divs[6] + " " +divs[7] + " " + divs[8] + " " +divs[9] + " " +divs[10] + " " +divs[11]);
				list.add( info );
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		SecondaryStructureFileLoader loader = new SecondaryStructureFileLoader();
		
		loader.readFile("/Users/lion/Documents/workspace-sts-2.8.1.RELEASE/MirSeqBrowser/src/kobic/msb/data/mirbase/secondstructure.txt");
	}
}
