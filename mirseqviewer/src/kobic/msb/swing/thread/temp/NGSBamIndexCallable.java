package kobic.msb.swing.thread.temp;
//package kobic.msb.swing.thread;
//
//import java.io.File;
//import java.util.concurrent.Callable;
//
//import kobic.com.MessageConsole;
//import kobic.com.picard.PicardCommandUtil;
//import kobic.com.util.Utilities;
//import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
//import kobic.msb.swing.panel.newproject.JBamFilePreProcessingPanel;
//import kobic.msb.system.catalog.ProjectMapItem;
//import kobic.msb.system.engine.MsbEngine;
//
//public class NGSBamIndexCallable  implements Callable<Void>{
//	private String						PROCESS_ID;
//
////	private MsbEngine					engine;
////	private String						projectName;
//	private String						groupId;
//	private String						sampleId;
//	private String						directory;
//	private JBamFilePreProcessingPanel	remote;
//	private ProjectMapItem				mapItem;
//
//	private boolean canWriteFile;
//	
//	private MessageConsole mc;
//
//	public NGSBamIndexCallable( String pid, MsbEngine engine, String projectName, String groupId, 
//			String sampleId, String directory, JBamFilePreProcessingPanel panel, ProjectMapItem mapItem ) {
//		this.PROCESS_ID		= pid;
////		this.engine			= engine;
////		this.projectName	= projectName;
//		this.groupId		= groupId;
//		this.sampleId		= sampleId;
//		this.directory		= directory;
////		this.remote			= panel;
//		this.mapItem		= mapItem;
//
////		this.mc				= new MessageConsole( remote.getTextPane() );
////		this.mc.redirectOut();
////		this.mc.redirectErr(new java.awt.Color(0, 53, 112), null);
////		this.mc.setMessageLines( 100 );
//
//		this.canWriteFile	= true;
//	}
//	
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
//	private static String doIndexing( String directory, Sample sample, String sortInputFilePath ) throws Exception{
//		String baiFilePath = "";
//
//		try {
//			if( Utilities.nulltoEmpty( sample.getIndexPath() ).equals("") ) {
//				baiFilePath = directory + File.separator + Utilities.getOnlyFileName( sample.getSamplePath() ) + ".bai";
//			}else {
//				if( new File(sample.getIndexPath()).exists() )	baiFilePath = sample.getIndexPath();
//				else											baiFilePath = directory + File.separator + Utilities.getOnlyFileName( sample.getSamplePath() ) + ".bai";
//			}
//		}catch(Exception e) {
//			MsbEngine.logger.error("error", e);
//			throw new RuntimeException(e);
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
//			MsbEngine.logger.error("error : ", indexEx);
//			
//			if( delSrtFile.exists() )	delSrtFile.delete();
//			if( delIdxFile.exists() )	delIdxFile.delete();
//			
//			throw new RuntimeException(indexEx);
//		}
//	}
//	
//	private static String doSorting( String DIRECTORY, Sample ToSortSampleFile ) throws Exception{
//		String sortFilePath = "";
//		
////		String originalSampleFilePath = ToSortSampleFile.getSamplePath();
//
//		try {
//			if( Utilities.nulltoEmpty( ToSortSampleFile.getIndexPath() ).isEmpty() ) {
//				sortFilePath = DIRECTORY + File.separator + "sorted_" + Utilities.replaceIfStart( Utilities.getOnlyFileName( ToSortSampleFile.getSamplePath() ), "sorted_" );
//				MsbEngine.logger.debug( sortFilePath );
//			}else {
//				sortFilePath = Utilities.getOnlyDirectory( ToSortSampleFile.getIndexPath() ) + File.separator + "sorted_" + Utilities.replaceIfStart( Utilities.getOnlyFileName( ToSortSampleFile.getSamplePath() ), "sorted_" );
//				
//				MsbEngine.logger.debug( sortFilePath );
//	
//				File sortFile = new File(sortFilePath);
//				if( sortFile.exists() == false )
//					sortFilePath = DIRECTORY + File.separator + "sorted_" + Utilities.replaceIfStart( Utilities.getOnlyFileName( ToSortSampleFile.getSamplePath() ), "sorted_" );
//			}
//		}catch(Exception e) {
//			MsbEngine.logger.error("error : ", e );
//			
//			throw new RuntimeException(e);
//		}
//		
//		try {
////			MsbEngine.logger.debug("Before picard sorting");
//			String sortedFile = PicardCommandUtil.toSortBAM( ToSortSampleFile.getSamplePath(), sortFilePath );
//			ToSortSampleFile.setSortedPath( sortedFile );
////			double tmp = 32/0;		// Happen exception;
//			return sortedFile;
//		}catch(Exception sortEx ) {
//			File delFile = new File( sortFilePath );
//			ToSortSampleFile.setSortedPath( null );
////			ToSortSampleFile.setSamplePath( originalSampleFilePath );
//			System.err.println( "Sort Exeption Happened : " + delFile.getName() + " : " + sortEx.getMessage() );
////			sortEx.printStackTrace();
//			MsbEngine.logger.error("error : ", sortEx);
//
//			if( delFile.exists() ) delFile.delete();
//			
//			throw new RuntimeException( sortEx );
//		}
//	}
//	
//	@Override
//	public Void call() throws Exception {
//		try {
//			// TODO Auto-generated method stub
//			Sample sample = this.mapItem.getSample( this.groupId, this.sampleId );
//			
//			MsbEngine.logger.debug("DoSorting....");
//			String sortedFilePath = NGSBamIndexCallable.doSorting( this.directory, sample );
//			MsbEngine.logger.debug("Complete DoSorting.... : " + sortedFilePath );
////			this.remote.setProgressBarValue( 50 );
//			MsbEngine.logger.debug( sortedFilePath != null );
//			if( sortedFilePath != null ) {
//				MsbEngine.logger.debug("DoIndexing....");
//				String indexedFilePath = NGSBamIndexCallable.doIndexing( this.directory, sample, sortedFilePath );
//				MsbEngine.logger.debug("Complete DoIndexing.... : " + indexedFilePath );
//
////				this.remote.setProgressBarValue( 50 );
//
//				boolean isThereAsortedFile	= NGSBamIndexCallable.isThereSortFile( sortedFilePath );
//				boolean isThereAindexedFile = NGSBamIndexCallable.isThereIndexFile( indexedFilePath );
//				
//				if( isThereAsortedFile && isThereAindexedFile ) {
//					this.canWriteFile = true;
//				}
//			}else {
////				this.remote.setProgressBarValue( 50 );
//				this.canWriteFile = false;
//				this.done();
//			}
//		}catch(Exception e) {
//			MsbEngine.logger.error("error : ", e);
//
//			this.canWriteFile = false;
//			this.done();
//		}
//
//		this.done();
//
//		return null;
//	}
//
//	public void done() {
////		remote.callback( this.mapItem );
////		remote.refreshBamIndexManager( this.mapItem );
////		
////		this.isDone	= true;
////		
////		remote.doNextStep( this );
//	}
//	
//	public String getProcessId() {
//		return this.PROCESS_ID;
//	}
//}
