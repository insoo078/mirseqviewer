
import java.awt.EventQueue;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import kobic.com.util.Utilities;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.frame.splash.SplashScreen;
import kobic.msb.system.SystemEnvironment;
import kobic.msb.system.engine.MsbEngine;

public class Main {
	private MsbEngine engine;

	public Main() {
		final SplashScreen screen = this.initSplashScreen();

		try {	
			this.engine = MsbEngine.getInstance();
			this.engine.loadEngine( screen );

			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						JMsbBrowserMainFrame frame = new JMsbBrowserMainFrame();

						frame.setVisible(true);

						MsbEngine.logger.info( "miRseq Viewer main screen open and Splash screen close" );

						screen.dispose();
					} catch (Exception e) {
						MsbEngine.logger.error( "error : ", e );
//						e.printStackTrace();
					}
				}
			});
		}catch(Exception e) {
			if( screen != null )	screen.dispose();

			MsbEngine.logger.error( "error : ", e );

			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
			System.exit(1);
		}
	}

	private SplashScreen initSplashScreen() {
//		String path = SystemEnvironment.getSystemBasePath() + "resources" + File.separator + "images" + File.separator + "splash.gif";
//		String path = JMirnaChoosePanel.class.getResource( "/kobic/msb/resources/images/mirseqbrowser.png" ).getFile();
		String path = SystemEnvironment.getSystemBasePath() + "resources" + File.separator + "images" + File.separator + "mirseqbrowser.png";

		ImageIcon myImage = new ImageIcon( path );
	    SplashScreen screen = new SplashScreen( myImage );
	    screen.setLocationRelativeTo(null);
	    screen.setProgressMax(100);
	    screen.setScreenVisible(true);

	    MsbEngine.logger.info( "Splash screen open" );

	    return screen;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String version = System.getProperty("java.version"); 

		if( Utilities.emptyToNull( version ) == null ) {
			JOptionPane.showMessageDialog( null, "There is no JRE/JDK on your machine.", "JDK verson problem", JOptionPane.ERROR_MESSAGE );
			System.exit(1);
		}else {
			double dVersion = Double.valueOf( version.substring(0, 3) );
			if( dVersion >= 1.6 ){
				Main main = new Main();
			}else {
				JOptionPane.showMessageDialog( null, "This system is running under Java 1.6+ version\nIf you installed old version on your machine\nPlease upgrade JRE/JDK on that.", "JDK verson problem", JOptionPane.ERROR_MESSAGE );
				System.exit(1);
			}
		}
	}

	void addShutdownHook(final ExecutorService serviceToShutdown) {
		// Used to create a thread that is executed by the shutdown hook
		ThreadFactory threadFactory = Executors.defaultThreadFactory();

		Runnable shutdownHook = new Runnable() {
			public void run() {
				serviceToShutdown.shutdownNow();
			}
		};
		Runtime.getRuntime().addShutdownHook(threadFactory.newThread(shutdownHook));
	}
}