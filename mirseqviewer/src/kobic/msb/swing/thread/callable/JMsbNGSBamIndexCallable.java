package kobic.msb.swing.thread.callable;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import net.sf.samtools.SAMException;

import org.apache.commons.io.FilenameUtils;

import kobic.com.log.MessageConsole;
import kobic.com.picard.PicardCommandUtil;
import kobic.com.picard.PicardUtilities;
import kobic.com.util.Utilities;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.thread.caller.JMsbNGSBamIndexCaller;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class JMsbNGSBamIndexCallable  implements Callable<Void>{
	private String						PROCESS_ID;

	private JMsbNGSBamIndexCaller		threadModel;
	private String						groupId;
	private String						sampleId;
	private String						directory;
	private ProjectMapItem				mapItem;
	
	private double						progressIncrement;

//	private boolean						canWriteFile;
	private int							id;
	private CountDownLatch				cdl;
	
	private MessageConsole				mc;
	
	JMsbNGSBamIndexCallable remote = JMsbNGSBamIndexCallable.this;

	public JMsbNGSBamIndexCallable( 
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

//		this.mc				= new MessageConsole( this.threadModel.getBamFilePreProcessingPanel().getTextPane() );
//		this.mc.redirectOut( Color.red, System.out );
////		this.mc.redirectErr(new java.awt.Color(0, 53, 112), null);
//		this.mc.setMessageLines( 20 );

//		this.canWriteFile	= true;

		this.progressIncrement	= (50 / this.threadModel.get_NO_THREAD_PROCESS_());
	}

	private static String doIndexing( String directory, Sample sample, String sortInputFilePath ) throws InterruptedException, Exception {
		String baiFilePath = "";

		if( Utilities.nulltoEmpty( sample.getIndexPath() ).equals("") ) {
			baiFilePath = directory + File.separator + Utilities.getOnlyFileName( sample.getSamplePath() ) + ".bai";
//				baiFilePath = directory + File.separator + new File(sample.getSamplePath()).getName() + ".bai";
		}else {
			if( new File(sample.getIndexPath()).exists() )	baiFilePath = sample.getIndexPath();
			else											baiFilePath = directory + File.separator + Utilities.getOnlyFileName( sample.getSamplePath() ) + ".bai";
		}

		try {
			System.out.println("Start indexing.... " + sortInputFilePath );
			PicardCommandUtil.toIndexBAM( sortInputFilePath, baiFilePath );
			System.out.println("Finished indexing.... " + sortInputFilePath );
		}finally{
			sample.setIndexPath( baiFilePath );
		}
		
		return baiFilePath;
	}
	
	private static String doSorting( String DIRECTORY, Sample ToSortSampleFile ) throws InterruptedException, IOException, Exception{
		String sortFilePath = "";

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

		if( !PicardUtilities.isSorted( ToSortSampleFile.getSamplePath() ) ) {
				System.out.println("Start sorting.... " + ToSortSampleFile.getSamplePath() );
				String sortedFile = PicardCommandUtil.toSortBAM( ToSortSampleFile.getSamplePath(), sortFilePath );
				ToSortSampleFile.setSortedPath( sortedFile );
	//			double tmp = 32/0;		// Happen exception;
				ToSortSampleFile.setIndexPath( null );
				System.out.println("End sorting.... " + ToSortSampleFile.getSamplePath() );
				
				return sortedFile;

		}else {
//			System.out.println("Copying " + ToSortSampleFile.getSamplePath() + " to " + sortFilePath + "....");
//
//			MsbEngine.logger.debug( ToSortSampleFile.getSamplePath() + " file copy to " + sortFilePath );
////			Utilities.copyFileUsingChannel( new File(ToSortSampleFile.getSamplePath()), new File(sortFilePath) );
//			Utilities.copyFileUsingStream( new File(ToSortSampleFile.getSamplePath()), new File(sortFilePath) );
//			ToSortSampleFile.setSortedPath( sortFilePath );
//
//			File sourceSampleFile = new File( ToSortSampleFile.getSamplePath() );
//
//			File srcIdxFile	= new File( sourceSampleFile.getParent() + File.separator + sourceSampleFile.getName() + ".bai" );
//
//			if( !srcIdxFile.exists() )	srcIdxFile = new File( sourceSampleFile.getParent() + File.separator + FilenameUtils.removeExtension( sourceSampleFile.getName() ) + ".bai" );
//
//			if( srcIdxFile.exists() ) {
//				File tarIdxFile = new File( new File( sortFilePath ).getParent() + File.separator + FilenameUtils.removeExtension( sourceSampleFile.getName() ) + ".bai" );
//
//				System.out.println("Copying " + srcIdxFile.getAbsolutePath() + " to " + tarIdxFile.getAbsolutePath() + "....");
//				MsbEngine.logger.debug( ToSortSampleFile.getIndexPath() + " file copy to " + tarIdxFile.getAbsolutePath() );
//				Utilities.copyFileUsingStream( srcIdxFile, tarIdxFile );
//				ToSortSampleFile.setIndexPath( tarIdxFile.getAbsolutePath() );
//			}else {
//				ToSortSampleFile.setIndexPath( null );
//			}
			ToSortSampleFile.setSortedPath( ToSortSampleFile.getSamplePath() );
			File sourceSampleFile = new File( ToSortSampleFile.getSamplePath() );
			File srcIdxFile	= new File( sourceSampleFile.getParent() + File.separator + sourceSampleFile.getName() + ".bai" );
			if( !srcIdxFile.exists() )	srcIdxFile = new File( sourceSampleFile.getParent() + File.separator + FilenameUtils.removeExtension( sourceSampleFile.getName() ) + ".bai" );
			if( srcIdxFile.exists() ) {
				ToSortSampleFile.setIndexPath( srcIdxFile.getAbsolutePath() );
			}else {
				ToSortSampleFile.setIndexPath( null );
			}
			sortFilePath = ToSortSampleFile.getSortedPath();
			
			return sortFilePath;
		}
	}

	@Override
	public Void call() throws InterruptedException, Exception{
		try {
			// TODO Auto-generated method stub
			Sample sample = this.mapItem.getSample( this.groupId, this.sampleId );

			MsbEngine.logger.debug("DoSorting....");
			String sortedFilePath = JMsbNGSBamIndexCallable.doSorting( this.directory, sample );
			MsbEngine.logger.debug("Complete DoSorting.... : " + sortedFilePath );

//			this.threadModel.setProcessingProgress( this.threadModel.getProcessingProgress() + this.progressIncrement );

			if( Utilities.nulltoEmpty( sample.getSortedPath() ).isEmpty() == false && Utilities.nulltoEmpty( sample.getIndexPath() ).isEmpty() == true ) {
				MsbEngine.logger.debug("DoIndexing....");
				String indexedFilePath = JMsbNGSBamIndexCallable.doIndexing( this.directory, sample, sortedFilePath );
				MsbEngine.logger.debug("Complete DoIndexing.... : " + indexedFilePath );

//				boolean isThereAsortedFile	= JMsbNGSBamIndexCallable.isThereSortFile( sortedFilePath );
//				boolean isThereAindexedFile = JMsbNGSBamIndexCallable.isThereIndexFile( indexedFilePath );
//
//				this.threadModel.setProcessingProgress( this.threadModel.getProcessingProgress() + this.progressIncrement );
//				if( isThereAsortedFile && isThereAindexedFile )		this.canWriteFile = true;
//				else												this.canWriteFile = false;
//			}else {
//				this.threadModel.setProcessingProgress( this.threadModel.getProcessingProgress() + this.progressIncrement );
//				this.canWriteFile = false;
			}else {
				MsbEngine.logger.debug("DoIndexing....");
				MsbEngine.logger.debug("Complete DoIndexing.... : " + sample.getIndexPath() );
			}
//		}catch(InterruptedException ie) {
////			this.threadModel.callbackInterrupt();
//			throw ie;
//		}catch(IOException ioe) {
////			this.threadModel.callbackByException("Sorting & Indexing Error : ", e);
//			throw ioe;
////
////			this.canWriteFile = false;
		}catch(SAMException e) {
			MsbEngine.logger.error("Error", e);
		}catch(InterruptedException ie){
			if( !MsbEngine.getExecutorService().isShutdown() )	MsbEngine.getExecutorService().shutdownNow();
		}catch(Exception e) {
			MsbEngine.logger.error("Exception stop all threads : ", e);
			this.threadModel.callbackByException( "Exception : " + e.getMessage(), e);
		}finally{
//			this.done();
			this.cdl.countDown();
		}

		return null;
	}

//	public void done() {
//		this.threadModel.callback( this.mapItem, this.id );
//	}

	public String getProcessId() {
		return this.PROCESS_ID;
	}
}
