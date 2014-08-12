package kobic.msb.swing.thread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import kobic.com.network.HTTPDownloadUtil;
import kobic.msb.swing.panel.newproject.JMsvNewProjectPanel;
import kobic.msb.system.engine.MsbEngine;

public class DownloadTask extends SwingWorker<Void, Void> {
    private static final int BUFFER_SIZE = 4096;   
    private String downloadURL;
    private String saveDirectory;
    private JMsvNewProjectPanel gui;
     
    public DownloadTask(JMsvNewProjectPanel gui, String downloadURL, String saveDirectory) {
    	this.gui = gui;
    	this.downloadURL = downloadURL;
    	this.saveDirectory = saveDirectory;
    }

    /**
     * Executed in background thread
     */
    @Override
    protected Void doInBackground() throws Exception {
    	try {
    		HTTPDownloadUtil util = new HTTPDownloadUtil();
            util.downloadFile(downloadURL);

            String saveFilePath = saveDirectory + File.separator + util.getFileName();
 
            InputStream inputStream = util.getInputStream();
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            long totalBytesRead = 0;
            int percentCompleted = 0;
            long fileSize = util.getContentLength();
 
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                percentCompleted = (int) (totalBytesRead * 100 / fileSize);
 
                setProgress(percentCompleted);         
            }
            outputStream.write("eoureoufafd".getBytes());
 
            outputStream.close();
 
            util.disconnect();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(gui, "Error downloading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);           
            MsbEngine.logger.error("error : ", ex);
            setProgress(0);
            cancel(true);          
        }
        return null;
    }
 
    /**
     * Executed in Swing's event dispatching thread
     */
    @Override
    protected void done() {
    	if (!isCancelled()) {
    		JOptionPane.showMessageDialog(gui, "File has been downloaded successfully!", "Message", JOptionPane.INFORMATION_MESSAGE);
    	}
    }  
}