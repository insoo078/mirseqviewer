//package kobic.msb.swing.thread.temp;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.LinkedHashMap;
//import java.util.List;
//
//import javax.swing.SwingWorker;
//
//import net.sf.samtools.SAMFileReader;
//import net.sf.samtools.SAMRecord;
//import net.sf.samtools.SAMRecordIterator;
//
//import kobic.com.util.Utilities;
//import kobic.msb.com.util.SwingUtilities;
//import kobic.msb.db.sqlite.vo.HairpinVO;
//import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
//import kobic.msb.swing.comparator.MiRnaCountComparator;
//import kobic.msb.swing.frame.dialog.JProjectDialog;
//import kobic.msb.system.catalog.ProjectMapItem;
//import kobic.msb.system.engine.MsbEngine;
//
//public class BamReaderToChooseMiRnasSwingWorker extends SwingWorker<Void, Void> {
//
//	private List<Object[]>					fileList;
//	private JProjectDialog					dialog;
//	private LinkedHashMap<String, String>	map;
//	private ProjectMapItem					projectItem;
//
//	public BamReaderToChooseMiRnasSwingWorker( JProjectDialog dialog, ProjectMapItem item ) {
//		this.projectItem	= item;
//		this.fileList		= item.getSampleFileList();
//		this.dialog			= dialog;
//		this.map			= new LinkedHashMap<String, String>();
//	}
//
//	@Override
//	protected Void doInBackground() throws Exception {
//		SwingUtilities.setWaitCursorFor( this.dialog );
//
//		int totalSize = 0;
//		List< Object[] > samFileReaders = new ArrayList< Object[] >(); 
//		for(Object[] sampleObj : this.fileList) {
//			Sample sample = (Sample)sampleObj[2];
//			String groupId = sampleObj[0].toString();
//			String sampleId = sampleObj[1].toString();
//
//			if( new File(sample.getIndexPath() ).exists() ) {
//				SAMFileReader inputSam = new SAMFileReader( new File( sample.getSamplePath() ), new File( sample.getIndexPath() ) );
//				samFileReaders.add( new Object[]{ groupId, sampleId, inputSam } );
//				
//				totalSize += Utilities.getTotalReadCountWithIndex( inputSam );
//			}else {
//				SAMFileReader inputSam = new SAMFileReader( new FileInputStream(new File( sample.getSamplePath() ) ) );
//				samFileReaders.add( new Object[]{ groupId, sampleId, inputSam } );
//				
//				totalSize += Utilities.getTotalReadCount( inputSam );
//			}
//		}
//
//		int currentRecord = 0;
//		for( Object[] samReaderObj : samFileReaders ) {
//			SAMFileReader samReader = (SAMFileReader)samReaderObj[2];
//			
//			samReader.setValidationStringency( SAMFileReader.ValidationStringency.SILENT );
//
//			SAMRecordIterator iter = samReader.iterator();
//
//		    while( iter.hasNext() ) {
//		    	SAMRecord rec=iter.next();
//		    	currentRecord++;
//		    	
//		    	int value = (currentRecord * 100 ) / totalSize;
//		    	dialog.setProgressToGetMiRnas( value > 100 ? 100 : value );
//	
//		        if(rec.getReadUnmappedFlag())	continue;	// Filter out unmapped read
//		        if(rec.getDuplicateReadFlag())	continue;	// Filter out duplicated read
//	
//		        String mirid 		= rec.getReferenceName();
//
//		        String count = "1";
//		        if( this.map.containsKey( mirid) ) {
//		        	count = Integer.toString( Integer.parseInt( this.map.get( mirid ) ) + 1 );
//		        }
//		        this.map.put( mirid, count );
//		    }
//		    
//		    iter.close();
//		    samReader.close();
//		}
//
//		return null;
//	}
//
//	/// here have to modify 20130529
//	@Override
//    public void done() {
//		List<String> miridList = new ArrayList<String>( this.map.keySet() );
//
//		List<Object[]> readedAllObjList = new ArrayList<Object[]>();
//		List<Object[]> choosedRnaObjList = new ArrayList<Object[]>();
//		for( int i=0; i<miridList.size(); i++) {
//			String mirid = miridList.get(i);
////			HairpinVO ov = this.dialog.getOwner().getMsbEngine().getHairpinListAtMemory().get( mirid );
//			HairpinVO ov = MsbEngine._db.getMicroRnaHairpinByMirid2( mirid );
//
//			if( ov != null ) {
//				if( !this.projectItem.getModelMap().containsKey(mirid) ) {
//					readedAllObjList.add( new Object[]{ new Boolean(false), ov.getAccession(), ov.getId(), ov.getChr() + ":" + ov.getStart() + "-" + ov.getEnd(), ov.getChr(), ov.getStrand(), new Long(this.map.get(mirid)) } );
//				}else {
//					choosedRnaObjList.add( new Object[]{ new Boolean(false), ov.getAccession(), ov.getId(), ov.getChr() + ":" + ov.getStart() + "-" + ov.getEnd(), ov.getChr(), ov.getStrand(), new Long(this.map.get(mirid)) } );
//				}
//			}
//		}
//		Collections.sort( readedAllObjList, new MiRnaCountComparator() );
//
//		this.dialog.getMirnaChoosePanel().setTableModel( readedAllObjList );
//		this.dialog.getMirnaChoosePanel().setListModel( choosedRnaObjList );
//
//		this.map = null;
//
//		SwingUtilities.setDefaultCursorFor( this.dialog );
//    }
//}
