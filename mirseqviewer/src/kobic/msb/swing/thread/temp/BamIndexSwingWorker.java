//package kobic.msb.swing.thread.temp;
//
//import java.io.File;
//
//import javax.swing.SwingWorker;
//
//import kobic.com.MessageConsole;
//import kobic.com.picard.PicardCommandUtil;
//import kobic.com.util.Utilities;
//import kobic.msb.com.JMsbSysConst;
//import kobic.msb.server.model.jaxb.Msb;
//import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
//import kobic.msb.swing.panel.newproject.JBamFilePreProcessingPanel;
//import kobic.msb.system.catalog.ProjectMap;
//import kobic.msb.system.catalog.ProjectMapItem;
//import kobic.msb.system.engine.MsbEngine;
//
//public class BamIndexSwingWorker extends SwingWorker<Void, Void> {
//
//	private String						PROCESS_ID;
//
//	private MsbEngine					engine;
//	private String						projectName;
//	private String						groupId;
//	private String						sampleId;
//	private String						directory;
//	private JBamFilePreProcessingPanel	remote;
//	private ProjectMap					map;
//	private ProjectMapItem				mapItem;
//
//	private boolean canWriteFile;
//	
//	private boolean isDone;
//	
//	private MessageConsole mc;
//	
//	public BamIndexSwingWorker( String pid, MsbEngine engine, String projectName, String groupId, 
//			String sampleId, String directory, JBamFilePreProcessingPanel panel, ProjectMap map ) {
//		this.PROCESS_ID		= pid;
//		this.engine			= engine;
//		this.projectName	= projectName;
//		this.groupId		= groupId;
//		this.sampleId		= sampleId;
//		this.directory		= directory;
//		this.remote			= panel;
//		this.map			= map;
//
//		this.mc				= new MessageConsole( remote.getTextPane() );
//		this.mc.redirectOut();
//		this.mc.redirectErr(java.awt.Color.RED, null);
//		this.mc.setMessageLines( 100 );
//
//		this.canWriteFile	= true;
//		
//		this.isDone			= false;
//	}
//	
//	private static boolean isThereSortFile( String filename ) {
//		if( new File(filename).exists())
//			return false;
//		return true;
//	}
//	
//	private static boolean isThereIndexFile( String filename ) {
//		if( new File(filename).exists())
//			return false;
//		return true;
//	}
//	
//	private static String doIndexing( String directory, Sample sample, String sortInputFilePath ) {
//		String baiFilePath = "";
//
//		if( Utilities.nulltoEmpty( sample.getIndexPath() ).equals("") ) {
//			baiFilePath = directory + File.separator + Utilities.getOnlyFileName( sample.getSamplePath() ) + ".bai";
//		}else {
//			if( new File(sample.getIndexPath()).exists() )	baiFilePath = sample.getIndexPath();
//			else											baiFilePath = directory + File.separator + Utilities.getOnlyFileName( sample.getSamplePath() ) + ".bai";
//		}
//
//		try {
//			String indexedFile = PicardCommandUtil.toIndexBAM( sortInputFilePath, baiFilePath );
//			sample.setIndexPath( indexedFile );
//			
//			return indexedFile;
//		}catch(Exception indexEx) {
//			File delIdxFile = new File( baiFilePath );
//			File delSrtFile	= new File( sortInputFilePath );
//
//			sample.setIndexPath( null );
//			System.err.println("Index Exeption Happened : " + delIdxFile.getName() );
////			indexEx.printStackTrace();
//			
//			if( delSrtFile.exists() )	delSrtFile.delete();
//			if( delIdxFile.exists() )	delIdxFile.delete();
//		}
//
//		return null;
//	}
//	
//	private static String doSorting( String DIRECTORY, Sample ToSortSampleFile ) {
//		String sortFilePath = "";
//		
////		String originalSampleFilePath = ToSortSampleFile.getSamplePath();
//
//		if( Utilities.nulltoEmpty( ToSortSampleFile.getIndexPath() ).isEmpty() ) {
//			sortFilePath = DIRECTORY + File.separator + "sorted_" + Utilities.replaceIfStart( Utilities.getOnlyFileName( ToSortSampleFile.getSamplePath() ), "sorted_" );
//		}else {
//			sortFilePath = Utilities.getOnlyDirectory( ToSortSampleFile.getIndexPath() ) + File.separator + "sorted_" + Utilities.replaceIfStart( Utilities.getOnlyFileName( ToSortSampleFile.getSamplePath() ), "sorted_" );
//
//			File sortFile = new File(sortFilePath);
//			if( sortFile.exists() == false )
//				sortFilePath = DIRECTORY + File.separator + "sorted_" + Utilities.replaceIfStart( Utilities.getOnlyFileName( ToSortSampleFile.getSamplePath() ), "sorted_" );
//		}
//		
//		try {
//			String sortedFile = PicardCommandUtil.toSortBAM( ToSortSampleFile.getSamplePath(), sortFilePath );
//			ToSortSampleFile.setSortedPath( sortedFile );
//			
//			return sortedFile;
//		}catch(Exception sortEx ) {
//			File delFile = new File( sortFilePath );
//			ToSortSampleFile.setSortedPath( null );
////			ToSortSampleFile.setSamplePath( originalSampleFilePath );
//			System.err.println( "Sort Exeption Happened : " + delFile.getName() + " : " + sortEx.getMessage() );
////			sortEx.printStackTrace();
//
//			if( delFile.exists() ) delFile.delete();
//		}
//
//		return null;
//	}
//
//	@Override
//	protected synchronized Void doInBackground() {  
//		try {
//			this.mapItem = this.map.getProject( this.projectName );
//			// TODO Auto-generated method stub
//			Sample sample = this.mapItem.getSample( this.groupId, this.sampleId );
//			
//			String sortedFilePath = BamIndexSwingWorker.doSorting( this.directory, sample );
//
//			this.remote.setProgressBarValue( 50 );
//			if( sortedFilePath != null ) {
//				String indexedFilePath = BamIndexSwingWorker.doIndexing( this.directory, sample, sortedFilePath );
//
//				this.remote.setProgressBarValue( 50 );
//
//				boolean isThereAsortedFile = BamIndexSwingWorker.isThereSortFile( sortedFilePath );
//				boolean isThereAindexedFile = BamIndexSwingWorker.isThereIndexFile( indexedFilePath );
//				
//				if( isThereAsortedFile && isThereAindexedFile ) {
//					this.canWriteFile = true;
//				}
//			}else {
//				this.remote.setProgressBarValue( 50 );
//				this.canWriteFile = false;
//			}
//		}catch(Exception e) {
//			this.canWriteFile = false;
//		}
//
//		return null;
//	}
//
//	@Override
//	public void done() {
//		try {
//			if( this.canWriteFile ) {
//				Msb msb = new Msb();
//				msb.setProject( this.mapItem.getProjectInfo() );
//	//			Utilities.writeXmlFile( msb, this.mapItem.getProjectLoadFilePath() );
//				MsbEngine.engine.getProjectManager().writeXmlToProject( msb );
//		
//				synchronized( this.mapItem ) {
//					this.mapItem.setProjectStatus( JMsbSysConst.READY_TO_CHOOSE_MIRNAS );
//					this.map.putProject( this.projectName, this.mapItem );
//					this.map.writeToFile( this.engine.getProjectManager().getSystemFileToGetProjectList() );
//				}
//			}
//	
//	//		remote.refreshBamIndexManager( this.mapItem );
//			
//			this.isDone	= true;
//		}catch(Exception e) {
//			MsbEngine.logger.error("error : " + e );
//		}
//		
////		remote.doNextStep( this );
//	}
//	
//	public String getProcessId() {
//		return this.PROCESS_ID;
//	}
//	
//	public boolean isThisWorkerDone() {
//		return this.isDone;
//	}
//}
