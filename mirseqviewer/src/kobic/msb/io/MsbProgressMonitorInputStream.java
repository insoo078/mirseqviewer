package kobic.msb.io;

import java.awt.Component;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JProgressBar;

public class MsbProgressMonitorInputStream extends FilterInputStream
{
    private JProgressBar	monitor;
    private int             nread = 0;
    private int             size = 0;


    /**
     * Constructs an object to monitor the progress of an input stream.
     *
     * @param message Descriptive text to be placed in the dialog box
     *                if one is popped up.
     * @param parentComponent The component triggering the operation
     *                        being monitored.
     * @param in The input stream to be monitored.
     */
    public MsbProgressMonitorInputStream(Component parentComponent,
                                      Object message,
                                      InputStream in, JProgressBar progressBar) {
        super(in);

        try {
        	this.size = in.available();
        }
        catch(IOException ioe) {
        	this.size = 0;
        }
        this.monitor = progressBar;
        
    	this.monitor.setStringPainted(true);
    	this.monitor.setString(null);
        
    	this.monitor.setMinimum(0);
    	this.monitor.setMaximum(this.size);
    }


    /**
     * Get the ProgressMonitor object being used by this stream. Normally
     * this isn't needed unless you want to do something like change the
     * descriptive text partway through reading the file.
     * @return the ProgressMonitor object used by this object
     */
    public JProgressBar getProgressBar() {
        return monitor;
    }


    /**
     * Overrides <code>FilterInputStream.read</code>
     * to update the progress monitor after the read.
     */
    public int read() throws IOException {
        int c = in.read();
        if (c >= 0) this.printSetValue( ++nread ); //monitor.setValue(++nread);
        return c;
    }


    /**
     * Overrides <code>FilterInputStream.read</code>
     * to update the progress monitor after the read.
     */
    public int read(byte b[]) throws IOException {
        int nr = in.read(b);
        if (nr > 0) this.printSetValue( nread += nr ); //monitor.setValue(nread += nr);
        return nr;
    }


    /**
     * Overrides <code>FilterInputStream.read</code>
     * to update the progress monitor after the read.
     */
    public int read(byte b[],
                    int off,
                    int len) throws IOException {
        int nr = in.read(b, off, len);
        if (nr > 0) this.printSetValue( nread += nr ); //monitor.setValue(nread += nr);
        return nr;
    }


    /**
     * Overrides <code>FilterInputStream.skip</code>
     * to update the progress monitor after the skip.
     */
    public long skip(long n) throws IOException {
        long nr = in.skip(n);
        if (nr > 0) this.printSetValue( nread += nr ); //monitor.setValue(nread += nr);
        return nr;
    }


    /**
     * Overrides <code>FilterInputStream.close</code>
     * to close the progress monitor as well as the stream.
     */
    public void close() throws IOException {
        in.close();
    }


    /**
     * Overrides <code>FilterInputStream.reset</code>
     * to reset the progress monitor as well as the stream.
     */
    public synchronized void reset() throws IOException {
        in.reset();
        nread = size - in.available();
        this.printSetValue( nread );
    }
    
    public void printSetValue(int value) {
    	this.monitor.setValue(value);
    }
}