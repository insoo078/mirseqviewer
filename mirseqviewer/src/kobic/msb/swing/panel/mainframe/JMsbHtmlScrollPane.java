package kobic.msb.swing.panel.mainframe;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.sun.javafx.application.PlatformImpl;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import kobic.msb.common.ImageConstant;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.system.engine.MsbEngine;

public class JMsbHtmlScrollPane extends JScrollPane{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String homeUrl = MsbEngine.getInstance().getSystemProperties().getProperty("msb.http.url");

	private WebView		browser;  
	private JFXPanel	jfxPanel;
	private WebEngine	webEngine;
	private JPanel		rootPanel;
	
	private JMsbBrowserMainFrame frame;

	private JLabel		lblStatus = new JLabel();
    private JButton		btnGo	= new JButton( ImageConstant.homeIcon );
    private JTextField	txtURL	= new JTextField( this.homeUrl );
    
    private JTextField	txtURL2	= new JTextField();

	public JMsbHtmlScrollPane( JMsbBrowserMainFrame frame ){
		this.frame = frame;
		this.initComponents();

		Platform.setImplicitExit(false);
	}

    private void initComponents(){  
    	this.rootPanel	= new JPanel();
    	this.jfxPanel	= new JFXPanel();

    	this.createScene();

    	ActionListener al = new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			Platform.runLater(new Runnable() {
    				@Override
    				public void run() {
//    					webEngine.reload();
    					loadURL( homeUrl );
    				}
    			});
    		}
        };

        btnGo.addActionListener(al);
//        txtURL.addActionListener(al);
        
        txtURL.getDocument().addDocumentListener( new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				updateLabel(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				updateLabel(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				updateLabel(e);
			}
        });
        
        txtURL.addKeyListener( new KeyAdapter() {
        	public void keyPressed(KeyEvent e) {
        		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
        			String newUrl = txtURL2.getText();
                    loadURL( newUrl );
                }
        	}
        });

        JPanel topBar = new JPanel(new BorderLayout(5, 0));
        topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        topBar.add(txtURL, BorderLayout.CENTER);
        topBar.add(btnGo, BorderLayout.EAST);

        JPanel statusBar = new JPanel(new BorderLayout(5, 0));
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        statusBar.add(lblStatus, BorderLayout.CENTER);

        rootPanel.setLayout( new BorderLayout() );
        rootPanel.add(topBar,		BorderLayout.NORTH);
        rootPanel.add(jfxPanel,		BorderLayout.CENTER);
        rootPanel.add(statusBar,	BorderLayout.SOUTH);
        
        this.getViewport().add( this.rootPanel );
    }
    
    private void updateLabel(DocumentEvent e) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                txtURL2.setText( txtURL.getText() );
            }
        });
    }
     
    /** 
     * createScene 
     * 
     * Note: Key is that Scene needs to be created and run on "FX user thread" 
     *       NOT on the AWT-EventQueue Thread 
     * 
     */  
    private void createScene(){  
    	PlatformImpl.startup(new Runnable() {  
    		@Override
    		public void run() {
                 
                // Set up the embedded browser:
    			browser = new WebView();
    			webEngine = browser.getEngine();
    			
//    			URL urlHello = this.getClass().getResource("/kobic/msb/resources/html/msb.html");
//    			webEngine.load( urlHello.toExternalForm() );
//    			webEngine.load( "http://mirgator.kobic.re.kr" );
//    			webEngine.load( "http://210.218.222.211/msb/" );
    			webEngine.load( txtURL.getText() );
    			
    			webEngine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
                    @Override public void handle(final WebEvent<String> event) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override public void run() {
                                lblStatus.setText(event.getData());
                            }
                        });
                    }
                });

    			webEngine.locationProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov, String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override public void run() {
                                txtURL.setText(newValue);
                            }
                        });
                    }
                });

    			webEngine.getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override 
                            public void run() {
//                                progressBar.setValue(newValue.intValue());
                            	frame.getStatusBar().setStatusBarProgress(null, 0, 100, newValue.intValue()<100?newValue.intValue():0);
                            }
                        });
                    }
                });
    			
    			jfxPanel.setScene(new Scene(browser));
    		}
    	});  
    }

    public void loadURL(final String url) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                String tmp = toURL(url);

                if (tmp == null) {
                    tmp = toURL("http://" + url);
                }

                webEngine.load(tmp);
            }
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
                return null;
        }
    }
}