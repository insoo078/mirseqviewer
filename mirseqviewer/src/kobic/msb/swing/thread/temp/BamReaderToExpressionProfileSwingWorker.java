package kobic.msb.swing.thread.temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

import kobic.com.util.Utilities;
import kobic.msb.db.sqlite.vo.MatureVO;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class BamReaderToExpressionProfileSwingWorker extends SwingWorker<Void, Void> {
//	private static Map<String, List<MatureVO>> matureMap = MsbEngine._db.getAllMicroRnaMaturesByMirid();

	private List<Object[]>							fileList;
	private ProjectMapItem							projectItem;
	private JMsbBrowserMainFrame					frame;
    private Map<String, HashMap<String, Integer>>	profileMap;
    
    private Map<String, List<MatureVO>>				matureMap;

	public BamReaderToExpressionProfileSwingWorker( JMsbBrowserMainFrame frame, ProjectMapItem projectItem, Map<String, List<MatureVO>>	matureMap ) {
		this.projectItem	= projectItem;
		this.fileList		= projectItem.getSampleFileList();;
		this.frame			= frame;
		
		this.profileMap = new HashMap<String, HashMap<String, Integer>>();
		
		this.matureMap	= matureMap;
	}

	public void getExpressionProfile() throws FileNotFoundException {
		List< Object[] > samFileReaders = new ArrayList< Object[] >(); 
		for(Object[] sampleObj : this.fileList) {
			Sample sample = (Sample)sampleObj[2];
			String groupId = sampleObj[0].toString();
			String sampleId = sampleObj[1].toString();

			if( new File(sample.getIndexPath() ).exists() ) {
				SAMFileReader inputSam = new SAMFileReader( new File( sample.getSamplePath() ), new File( sample.getIndexPath() ) );
				samFileReaders.add( new Object[]{ groupId, sampleId, inputSam } );
			}else {
				SAMFileReader inputSam = new SAMFileReader( new FileInputStream(new File( sample.getSamplePath() ) ) );
				samFileReaders.add( new Object[]{ groupId, sampleId, inputSam } );
			}
		}
        
		int fileIndex = 0;
		for( Object[] samReaderObj : samFileReaders ) {
			// TODO Auto-generated method stub
			SAMFileReader	samReader	= (SAMFileReader)samReaderObj[2];
			String			sampleId	= samReaderObj[1].toString();
			
			samReader.setValidationStringency( SAMFileReader.ValidationStringency.SILENT );
	
			SAMRecordIterator iter = samReader.iterator();
			
			fileIndex++;

			int value = (int) (100 * ((double)fileIndex / samFileReaders.size()));
	    	frame.getStatusBar().setStatusBarProgress("", 0, 100, value );
	
		    while( iter.hasNext() ) {
		    	SAMRecord rec=iter.next();

		        if(rec.getReadUnmappedFlag())	continue;	// Filter out unmapped read
		        if(rec.getDuplicateReadFlag())	continue;	// Filter out duplicated read
	
		        String hairpin_id	= rec.getReferenceName();
		        int startPos 		= rec.getAlignmentStart();
		        int endPos			= rec.getAlignmentEnd();
		        
		        List<MatureVO> matures = matureMap.get( hairpin_id );

		        int overlapSize = 0;
		        if( matures != null ) {
			        for(int i=0; i<matures.size(); i++) {
			        	MatureVO mature = matures.get(i);
			        	int start	= mature.getStart();
			        	int end		= mature.getEnd();

			        	int localOverlapSize = Utilities.getLengthOfOverlap(start, end, startPos, endPos);

			        	if( overlapSize < localOverlapSize ) {
			        		String key = mature.getMirid();
			        		
			        		if( this.profileMap.containsKey( key ) )	{
			        			if( this.profileMap.get(key).containsKey( sampleId ) ) {
				        			int cnt = this.profileMap.get(key).get( sampleId );
				        			this.profileMap.get(key).put( sampleId,  cnt+1 );
			        			}else {
			        				this.profileMap.get(key).put( sampleId, 1);
			        			}
			        		}else {
			        			HashMap<String, Integer> subMap = new HashMap<String, Integer>();
			        			subMap.put( sampleId, 1);
			        			this.profileMap.put( key, subMap );
			        		}
				        	overlapSize = localOverlapSize;	
			        	}
			        }
		        }
		    }

		    iter.close();
		    samReader.close();
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
//		if( this.projectItem.getExpressionProfile() == null ) 
			this.getExpressionProfile();

		return null;
	}
	
	@Override
    public void done() {
//		if( this.projectItem.getExpressionProfile() == null ) {
			Object[][] profile = new Object[ profileMap.keySet().size() + 1 ][ this.fileList.size() + 1 ];
		
			int index = 1;
			profile[0][0] = "ID";
			for(Object[] sampleObj : this.fileList) {
				profile[0][index++] = sampleObj[1].toString();
			}
	
			int i = 1;
		    Iterator<String> miridKeyIter = profileMap.keySet().iterator();
		    while( miridKeyIter.hasNext() ) {
		    	String mirid = miridKeyIter.next();
	
		    	profile[i][0] = mirid;
		    	int j = 1;
		    	for(Object[] sampleObj : this.fileList) {
					String sampleId = sampleObj[1].toString();
					int cnt = 0;
					if( profileMap.get(mirid).containsKey( sampleId ) )
						cnt = profileMap.get( mirid ).get( sampleId );
		
			    	profile[i][j] = cnt;
					j++;
		    	}
		    	i++;
		    }
	
		    this.profileMap = null;
	
		    try {
		    	this.projectItem.setExpressionProfile( this.frame, profile );
		    }catch(Exception e) {
		    	e.printStackTrace();
		    	MsbEngine.logger.error( e.getMessage() );
		    }
//		}
//	    this.frame.addProjectViewToTabWindow( this.projectItem.getProjectName(), true );
	}
}
