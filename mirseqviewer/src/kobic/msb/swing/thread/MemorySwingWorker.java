package kobic.msb.swing.thread;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

public class MemorySwingWorker extends SwingWorker<Void, String> {

	private JLabel label;

	public MemorySwingWorker(JLabel label ) {
		this.label = label;
	}

	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		while( true ) {
			long total	= Runtime.getRuntime().totalMemory()/(1024 * 1024);
			long free	= Runtime.getRuntime().freeMemory()/(1024 * 1024);
			long used	= total - free;
			
			String memory = "Total : " + total + "MB, Free : " + free + "MB, Used : "  + used +"MB";
			publish( memory );
			Thread.sleep( 10000 );
		}
	}

	
	@Override
    protected void process(List<String> chunks) {
		String label = chunks.get( chunks.size() - 1 );
		this.label.setText( label );
    }
}
