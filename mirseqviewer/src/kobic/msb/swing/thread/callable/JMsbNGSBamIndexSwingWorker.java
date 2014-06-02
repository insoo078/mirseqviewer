package kobic.msb.swing.thread.callable;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingWorker;

import kobic.com.log.MessageConsole;
import kobic.com.picard.PicardCommandUtil;
import kobic.com.picard.PicardUtilities;
import kobic.com.util.Utilities;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.thread.caller.JMsbNGSBamIndexCaller;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

import org.apache.commons.io.FilenameUtils;

public class JMsbNGSBamIndexSwingWorker extends SwingWorker<Void, Void>{
	private String						PROCESS_ID;

	private JMsbNGSBamIndexCaller		threadModel;
	private String						groupId;
	private String						sampleId;
	private String						directory;
	private ProjectMapItem				mapItem;
	
	private double						progressIncrement;

	private boolean						canWriteFile;
	private int							id;
	private CountDownLatch				cdl;
	
	private MessageConsole				mc;

	public JMsbNGSBamIndexSwingWorker( 
			CountDownLatch latch, String pid, int id, MsbEngine engine, String projectName, String groupId, 
			String sampleId, String directory, JMsbNGSBamIndexCaller threadModel, ProjectMapItem mapItem ) {
		this.cdl			= latch;
		this.PROCESS_ID		= pid;
		this.groupId		= groupId;
		this.sampleId		= sampleId;
		this.directory		= directory;
		this.threadModel	= threadModel;
		this.mapItem		= mapItem;
		this.id				= id;

		this.mc				= new MessageConsole( this.threadModel.getBamFilePreProcessingPanel().getTextPane() );
		this.mc.redirectOut();
//		this.mc.redirectErr(new java.awt.Color(0, 53, 112), null);
		this.mc.setMessageLines( 100 );

		this.canWriteFile	= true;

		this.progressIncrement	= (50 / this.threadModel.get_NO_THREAD_PROCESS_());
	}
	
	
	private static boolean isThereSortFile( String filename ) {
		if( new File(filename).exists())
			return false;
		return true;
	}
	
	private static boolean isThereIndexFile( String filename ) {
		if( new File(filename).exists())
			return false;
		return true;
	}
	
	private static String doIndexing( String directory, Sample sample, String sortInputFilePath ){
		String baiFilePath = "";

//		try {
			if( Utilities.nulltoEmpty( sample.getIndexPath() ).equals("") ) {
				baiFilePath = directory + File.separator + Utilities.getOnlyFileName( sample.getSamplePath() ) + ".bai";
//				baiFilePath = directory + File.separator + new File(sample.getSamplePath()).getName() + ".bai";
			}else {
				if( new File(sample.getIndexPath()).exists() )	baiFilePath = sample.getIndexPath();
				else											baiFilePath = directory + File.separator + Utilities.getOnlyFileName( sample.getSamplePath() ) + ".bai";
			}
//		}catch(Exception e) {
//			MsbEngine.logger.error("error", e);
//			throw new RuntimeException(e);
//		}

		try {
			System.out.println("Start indexing.... " + sortInputFilePath );
			PicardCommandUtil.toIndexBAM( sortInputFilePath, baiFilePath );
			System.out.println("Start indexing.... " + sortInputFilePath );
		}catch(Exception indexEx) {
			MsbEngine.logger.error("Error", indexEx);
		}finally{
			sample.setIndexPath( baiFilePath );
		}
		
		return baiFilePath;
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
	}
	
	private static String doSorting( String DIRECTORY, Sample ToSortSampleFile ) throws InterruptedException, Exception{
		String sortFilePath = "";

//		String originalSampleFilePath = ToSortSampleFile.getSamplePath();

//		try {
			if( Utilities.nulltoEmpty( ToSortSampleFile.getIndexPath() ).isEmpty() ) {
				sortFilePath = DIRECTORY + File.separator + "sorted_" + Utilities.replaceIfStart( Utilities.getOnlyFileName( ToSortSampleFile.getSamplePath() ), "sorted_" );
				MsbEngine.logger.debug( sortFilePath );
			}else {
				sortFilePath = Utilities.getOnlyDirectory( ToSortSampleFile.getIndexPath() ) + File.separator + "sorted_" + Utilities.replaceIfStart( Utilities.getOnlyFileName( ToSortSampleFile.getSamplePath() ), "sorted_" );
				
				MsbEngine.logger.debug( sortFilePath );
	
				File sortFile = new File(sortFilePath);
				if( sortFile.exists() == false )
					sortFilePath = DIRECTORY + File.separator + "sorted_" + Utilities.replaceIfStart( Utilities.getOnlyFileName( ToSortSampleFile.getSamplePath() ), "sorted_" );
			}
//		}catch(Exception e) {
//			MsbEngine.logger.error("error : ", e );
//			
//			throw new RuntimeException(e);
//		}

		if( !PicardUtilities.isSorted( ToSortSampleFile.getSamplePath() ) ) {
//		SAMFileReader inputSam = new SAMFileReader( new File( ToSortSampleFile.getSamplePath() ) );
//
//		String header = Utilities.nulltoEmpty( inputSam.getFileHeader().getAttribute("SO") );
//		
//		inputSam.close();
//
//		if( !header.contains("coordinate") ) {
//			try {
	//			MsbEngine.logger.debug("Before picard sorting");
			
				System.out.println("Start sorting.... " + ToSortSampleFile.getSamplePath() );
				String sortedFile = PicardCommandUtil.toSortBAM( ToSortSampleFile.getSamplePath(), sortFilePath );
				ToSortSampleFile.setSortedPath( sortedFile );
	//			double tmp = 32/0;		// Happen exception;
				ToSortSampleFile.setIndexPath( null );
				System.out.println("End sorting.... " + ToSortSampleFile.getSamplePath() );
				
				return sortedFile;
//			}catch(Exception sortEx ) {
//				File delFile = new File( sortFilePath );
//				ToSortSampleFile.setSortedPath( null );
//	//			ToSortSampleFile.setSamplePath( originalSampleFilePath );
//				System.err.println( "Sort Exeption Happened : " + delFile.getName() + " : " + sortEx.getMessage() );
//	//			sortEx.printStackTrace();
//				MsbEngine.logger.error("error : ", sortEx);
//	
//				if( delFile.exists() ) delFile.delete();
//				
//				throw new RuntimeException( sortEx );
//			}
		}else {
			try {
				System.out.println("Copying " + ToSortSampleFile.getSamplePath() + " to " + sortFilePath + "....");
				MsbEngine.logger.debug( ToSortSampleFile.getSamplePath() + " file copy to " + sortFilePath );
//				Utilities.copyFileUsingChannel( new File(ToSortSampleFile.getSamplePath()), new File(sortFilePath) );
				Utilities.copyFileUsingStream( new File(ToSortSampleFile.getSamplePath()), new File(sortFilePath) );
				ToSortSampleFile.setSortedPath( sortFilePath );
				
				File sourceSampleFile = new File( ToSortSampleFile.getSamplePath() );

				File srcIdxFile	= new File( sourceSampleFile.getParent() + File.separator + sourceSampleFile.getName() + ".bai" );
				
				if( !srcIdxFile.exists() )	srcIdxFile = new File( sourceSampleFile.getParent() + File.separator + FilenameUtils.removeExtension( sourceSampleFile.getName() ) + ".bai" );

				if( srcIdxFile.exists() ) {
					File tarIdxFile = new File( new File( sortFilePath ).getParent() + File.separator + FilenameUtils.removeExtension( sourceSampleFile.getName() ) + ".bai" );
					
					System.out.println("Copying " + srcIdxFile.getAbsolutePath() + " to " + tarIdxFile.getAbsolutePath() + "....");
					Utilities.copyFileUsingStream( srcIdxFile, tarIdxFile );
					ToSortSampleFile.setIndexPath( tarIdxFile.getAbsolutePath() );
				}else {
					ToSortSampleFile.setIndexPath( null );
				}
	
				return sortFilePath;
			}catch(Exception e) {
				MsbEngine.logger.error("File copy error : ", e);
			}
		}
		return null;
	}
/*
	private static void copyFile( String workspaceDir, Sample sample ) {
		File sortedBamFile	= new File( Utilities.nulltoEmpty( sample.getSortedPath() ) );
		File indexFile		= new File( Utilities.nulltoEmpty( sample.getIndexPath() ) );

		String newSortFilePath = workspaceDir + File.separator + "sorted_" + Utilities.replaceIfStart( Utilities.getOnlyFileName( sample.getSamplePath() ), "sorted_" );

		AddSampleActionListener.doSampleIndex( sample.getSamplePath(), sample );

		if( sortedBamFile.exists() ) {
			try {
				System.out.println("Copying " + sample.getSortedPath() + " to " + newSortFilePath + "....");
				Utilities.copyFileUsingStream( new File(sample.getSortedPath()), new File(newSortFilePath) );
				sample.setSortedPath( newSortFilePath );
			}catch(Exception e) {
				MsbEngine.logger.error("File copy error : ", e);
			}
		}else {
			String sortedFile = PicardCommandUtil.toSortBAM( sample.getSamplePath(), newSortFilePath );
			sample.setSortedPath( sortedFile );
			sample.setIndexPath("");
		}

		String newBaiFilePath = workspaceDir + File.separator + Utilities.getOnlyFileName( sample.getSortedPath() ) + ".bai";
		if( indexFile.exists() ) {
			try {
				System.out.println("Copying " + sample.getIndexPath() + " to " + newSortFilePath + "....");
				Utilities.copyFileUsingStream( new File(sample.getSortedPath()), new File(newBaiFilePath) );
				sample.setIndexPath( newBaiFilePath );
			}catch(Exception e) {
				MsbEngine.logger.error("File copy error : ", e);
			}
		}else {
			PicardCommandUtil.toIndexBAM( sample.getSortedPath(), newBaiFilePath );
		}
		
		return;
	}
*/	
	@Override
	protected Void doInBackground() throws Exception {
		try {
			// TODO Auto-generated method stub
			Sample sample = this.mapItem.getSample( this.groupId, this.sampleId );

			MsbEngine.logger.debug("DoSorting....");
			String sortedFilePath = JMsbNGSBamIndexSwingWorker.doSorting( this.directory, sample );
			MsbEngine.logger.debug("Complete DoSorting.... : " + sortedFilePath );
			this.threadModel.setProcessingProgress( this.threadModel.getProcessingProgress() + this.progressIncrement );
			MsbEngine.logger.debug( sortedFilePath != null );

			if( sortedFilePath != null && sample.getIndexPath() == null ) {
				MsbEngine.logger.debug("DoIndexing....");
				String indexedFilePath = JMsbNGSBamIndexSwingWorker.doIndexing( this.directory, sample, sortedFilePath );
				MsbEngine.logger.debug("Complete DoIndexing.... : " + indexedFilePath );

				boolean isThereAsortedFile	= JMsbNGSBamIndexSwingWorker.isThereSortFile( sortedFilePath );
				boolean isThereAindexedFile = JMsbNGSBamIndexSwingWorker.isThereIndexFile( indexedFilePath );

				this.threadModel.setProcessingProgress( this.threadModel.getProcessingProgress() + this.progressIncrement );
				if( isThereAsortedFile && isThereAindexedFile )		this.canWriteFile = true;
				else												this.canWriteFile = false;
			}else {
				this.threadModel.setProcessingProgress( this.threadModel.getProcessingProgress() + this.progressIncrement );
				this.canWriteFile = false;
			}

//			JMsbNGSBamIndexCallable.copyFile( this.directory, sample );
		}catch(Exception e) {
			MsbEngine.logger.error("error : ", e);

			this.canWriteFile = false;
		}finally{
//			this.done();
			this.cdl.countDown();
			MsbEngine.logger.debug( "Latch count=" + this.cdl.getCount() );
		}
		return null;
	}

	public void done() {
//		this.threadModel.callback( this.mapItem, this.id );
	}

	public String getProcessId() {
		return this.PROCESS_ID;
	}
}
