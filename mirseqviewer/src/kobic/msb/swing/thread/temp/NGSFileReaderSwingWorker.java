package kobic.msb.swing.thread.temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import kobic.com.util.Utilities;
import kobic.msb.common.util.MsvUtilities;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.Model;
import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.server.model.jaxb.Msb.Project;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.server.obj.ReadQuality;
import kobic.msb.server.obj.SAMInfo;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

public class NGSFileReaderSwingWorker extends SwingWorker<Double, Double> {

	private List<Object[]>			fileList;
	private ProjectMapItem			item;
	private JMsbBrowserMainFrame	frame; 

	public NGSFileReaderSwingWorker( JMsbBrowserMainFrame frame, ProjectMapItem item ) {
		this.fileList	= item.getSampleFileList();
		this.item		= item;
		this.frame		= frame;
	}
	
	private List<Object[]> getNewSamFileReader() throws FileNotFoundException {
		List<Object[]> samFileReaders = new ArrayList<Object[]>(); 
		for( Object[] sampleObj : this.fileList ) {
			Sample sample = (Sample)sampleObj[2];

			String groupId	= sampleObj[0].toString();
			String sampleId	= sampleObj[1].toString();

			SAMFileReader inputSam = null;
			if( new File(sample.getIndexPath() ).exists() )	{
				inputSam = new SAMFileReader( new File( sample.getSortedPath() ), new File( sample.getIndexPath() ) );
				samFileReaders.add( new Object[]{ groupId, sampleId, inputSam } );
			}else{
				inputSam = new SAMFileReader( new FileInputStream(new File( sample.getSortedPath() ) ) );
				samFileReaders.add( new Object[]{ groupId, sampleId, inputSam } );
			}
		}
		return samFileReaders;
	} 
	
	private int getFullRecordNumber( List<Object[]> samFileReaders ) throws FileNotFoundException {
		int totalSize = 0;
		for( Object[] inputSamObj:samFileReaders ) {
			SAMFileReader sfr = (SAMFileReader)inputSamObj[2];
			totalSize += MsvUtilities.getTotalReadCountWithIndex( sfr );
			sfr.close();
		}
		return totalSize;
	}

	@Override
	protected Double doInBackground() throws Exception {
		SwingUtilities.setWaitCursorFor( this.frame );

		List<Object[]> samObjList = this.getNewSamFileReader();
		int totalSize = this.getFullRecordNumber( samObjList );
		samObjList = null;

		for(int i=0; i<this.item.getMiRnaList().size(); i++) {
			List<Object[]> samReaders = this.getNewSamFileReader();

			String	mirid = this.item.getMiRnaList().get(i);
			Model	model = this.item.getProjectModel( mirid );

			try {
				List<Object[]> result = this.readAndQuery( samReaders, mirid, model, totalSize );

//				model.getHeatMapObject().doSampleMap( result );
				
				result		= null;
			}catch(Exception e) {
				MsbEngine.logger.error( e );
				e.printStackTrace();
			}

			samReaders	= null;
		}
		this.setProgress(0);

		return Double.valueOf(0);
	}

	public List<Object[]> readAndQuery( List<Object[]> samFileReaders, String mirid, Model model, int totalSize ) throws Exception{
		int currentRecord = 0;
		
		List<Object[]> result			= new ArrayList<Object[]>();

		for( Object[] samReaderObj : samFileReaders ) {
			SAMFileReader samReader = (SAMFileReader)samReaderObj[2];
			String groupId	= samReaderObj[0].toString();
			String sampleId = samReaderObj[1].toString();
			
			samReader.setValidationStringency( SAMFileReader.ValidationStringency.SILENT );

			SAMRecordIterator iter = null;
			if( samReader.hasIndex() ) {
				Project info = this.item.getProjectInfo();
				try{
					iter = samReader.query( mirid, 0, 0, false );
				}catch(Exception e) {
					e.printStackTrace();
				}
			}else {
				iter = samReader.iterator();
			}

			List<SAMInfo> samList = new ArrayList<SAMInfo>();
		    while( iter.hasNext() ) {
		    	SAMRecord rec=iter.next();
		    	currentRecord++;
		    	
		    	int value = (currentRecord * 100 ) / totalSize;
		    	this.setProgress( value > 100 ? 100 : value );
	
		        if(rec.getReadUnmappedFlag())	continue;	// Filter out unmapped read
		        if(rec.getDuplicateReadFlag())	continue;	// Filter out duplicated read
	
		        String reference	= rec.getReferenceName();
		        String readName 	= rec.getReadName();
		        String readSeq		= rec.getReadString();
		        int start			= rec.getAlignmentStart();
		        int end				= rec.getAlignmentEnd();
		        
		        int nh				= (Integer)rec.getAttribute("NH");
		        int mapq			= rec.getMappingQuality();
		        
		        Pattern p = Pattern.compile( "^(.+)_x(\\d+)$" );
		        Matcher m = p.matcher( readName );
	
		        String count = "0";
		        while( m.find() ) {
		        	count = m.group(2);
		        }
	//	        log.debug( reference + " " + readName + " " + count );
		        if( count.equals("0") ) {
		        	p = Pattern.compile( "^(.+)_(\\d+)$" );
			        m = p.matcher( readName );
	
			        count = "0";
			        while( m.find() ) {
			        	count = m.group(2);
			        }
		        }
		        
		        char strand		= '+';
		        String binaryStr = Utilities.lpad( Integer.toBinaryString( rec.getFlags() ), 5, "0" );
	//	        log.debug( binaryStr + " : " + rec.getFlags() );
		        char forward = binaryStr.charAt(4);		// 0 : forward (+), 1: reverse (-)
		        if( forward == '1' ) {
		        	strand = '-';
		        	readSeq = Utilities.getReverseComplementary( readSeq );
	//	        	readSeq = readSeq;
		        }
		        
		        ReadQuality readQual = new ReadQuality( rec );
	
		        SAMInfo info = new SAMInfo( reference, readName, readSeq, strand, start, end, Integer.parseInt( count ), readQual );
		        
		        samList.add( info );
		    }
		    this.bgMakeHeatMapObj( result, samList, model, mirid, groupId, sampleId );
		    
		    iter.close();
		    samReader.close();
		    
		    samList = null;
		}
		return result;
	}

	public void bgMakeHeatMapObj( List<Object[]> result, List<SAMInfo> list, Model model, String mirid, String groupId, String sampleId ) {
		try {
			for( Iterator<SAMInfo> samIter = list.iterator(); samIter.hasNext(); ) {
				SAMInfo info = samIter.next();
				if( info.getChr().equals( mirid ) ) {
					String key = info.getReadSeq() + "^" + info.getChr() + ":" + info.getStart() + "-" + info.getEnd();

					result.add( new Object[]{ key, groupId, sampleId, info.getCount() } );
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
    public void done() {
		try {
			String projectName = this.item.getProjectInfo().getProjectName();
			Msb msb = new Msb();
			msb.setProject( this.item.getProjectInfo() );
	//		Utilities.writeXmlFile( msb, item.getProjectLoadFilePath() );
			MsbEngine.engine.getProjectManager().writeXmlToProject( msb );
			
	//		this.item.setIsDoneProcess( true );
	
			MsbEngine.getInstance().getProjectManager().getProjectMap().putProject( projectName, item );
			MsbEngine.getInstance().getProjectManager().getProjectMap().writeToFile( MsbEngine.getInstance().getProjectManager().getSystemFileToGetProjectList() );
	
			SwingUtilities.setDefaultCursorFor( this.frame );
		}catch(Exception e) {
			MsbEngine.logger.error( "error : " + e );
		}

//		this.frame.addProjectViewToTabWindow( projectName );
    }
}
