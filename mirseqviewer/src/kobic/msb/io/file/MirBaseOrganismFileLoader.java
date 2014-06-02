package kobic.msb.io.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import kobic.msb.io.file.obj.mirbase.MirBaseOrganismInfo;

public class MirBaseOrganismFileLoader {
	@SuppressWarnings("resource")
	public List<MirBaseOrganismInfo> loadOrganismDataFromMirBase( String file ) throws FileNotFoundException {
		List<MirBaseOrganismInfo> list = new ArrayList<MirBaseOrganismInfo>();

		FileReader fr = null;
		try{
			fr = new FileReader( file );	//open
			BufferedReader br = new BufferedReader(fr);

			String line = "";
			while( (line = br.readLine()) != null ) {
				if( line.startsWith("#") )	continue;

				MirBaseOrganismInfo info = new MirBaseOrganismInfo();

				String[] divs = line.split("\t");

				String organism = "";
				String division = "";
				String name= "";
				String tree = "";
				String ncbi_taxid = "";
				if( divs.length >= 4 ) {
					organism		= divs[0];
					division		= divs[1];
					name			= divs[2];
					tree			= divs[3];
				}
				if( divs.length == 5 ) {
					ncbi_taxid	= divs[4];
				}

				info.setOrganism( organism );
				info.setDivision(division);
				info.setName(name);
				info.setTree(tree);
				info.setNcbi_taxid(ncbi_taxid);
				
				list.add( info );
		    }
		}catch(Exception e) {
			System.out.println( e.toString() );
		}
		
		return list;
	}
}
