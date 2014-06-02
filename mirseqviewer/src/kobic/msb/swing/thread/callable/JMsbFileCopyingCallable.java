package kobic.msb.swing.thread.callable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import kobic.com.util.Utilities;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.db.sqlite.vo.MatureVO;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.swing.thread.caller.JMsbQuickNewProjectCaller;
import kobic.msb.system.engine.MsbEngine;

public class JMsbFileCopyingCallable  implements Callable<Void>{
	private File srcBam;
	private File destBam;
	private File srcBai;
	private File destBai;
	private Sample sample;
	private String sampleId;
	private String groupId;
	private List<HairpinVO> hairpinList;
	private Map<String, List<MatureVO>> matureMap;
	
	CountDownLatch latch;
	
	private JMsbQuickNewProjectCaller caller;
	
	public JMsbFileCopyingCallable(CountDownLatch latch, File src, File dest, File srcBai, File destBai, Sample sample, String sampleId, 
			String groupId, List<HairpinVO> lst, Map<String, List<MatureVO>> matureMap, JMsbQuickNewProjectCaller caller) {
		this.srcBam = src;
		this.destBam = dest;
		this.srcBai = srcBai;
		this.destBai = destBai;
		this.caller = caller;
		this.sample = sample;
		this.sampleId = sampleId;
		this.groupId = groupId;
		this.hairpinList = lst;
		this.matureMap = matureMap;
		
		this.latch	= latch;
	}

	@Override
	public Void call() throws InterruptedException, IOException, Exception {
		// TODO Auto-generated method stub
		System.out.println("Loading " + Utilities.getOnlyFileName( this.srcBam.getAbsolutePath() ) );
		System.out.println("Loading " + Utilities.getOnlyFileName( this.destBai.getAbsolutePath() ) );
		Utilities.copyFileUsingStream( this.srcBam, this.destBam );
		Utilities.copyFileUsingStream( this.srcBai, this.destBai );
		
		MsbEngine.logger.debug("Done file copy");

		this.sample.setSortedPath( this.destBam.getAbsolutePath() );
		this.sample.setIndexPath( this.destBai.getAbsolutePath() );

//		this.caller.callbackCopying( this.sample, this.groupId, this.sampleId, this.matureMap, this.hairpinList );
		this.latch.countDown();

		return null;
	}
}
