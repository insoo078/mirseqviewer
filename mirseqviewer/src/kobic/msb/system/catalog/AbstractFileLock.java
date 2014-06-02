package kobic.msb.system.catalog;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class AbstractFileLock {
	private FileLock fLock;
	private FileChannel channel;
	
	public void closeLock() {
        try { this.fLock.release();  }
        catch (Exception e) {  }
        try { this.channel.close(); }
        catch (Exception e) {  }
    }
	
	@SuppressWarnings("resource")
	public void tryLock( String filePath ) throws Exception{
		this.channel = new RandomAccessFile( filePath, "rw").getChannel();

		this.fLock = this.channel.tryLock();

		if( this.fLock == null )	throw new Exception("Unable to obtain lock");
	}
}
