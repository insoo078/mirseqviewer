package kobic.msb.swing.panel.mainframe;

//import java.awt.Color;
//import java.awt.GradientPaint;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;
//
import javax.swing.JPanel;
//import javax.swing.GroupLayout;
//import javax.swing.GroupLayout.Alignment;
//import javax.swing.JEditorPane;
//import javax.swing.text.Document;
//import javax.swing.text.html.HTMLEditorKit;
//import javax.swing.text.html.StyleSheet;
//
//import kobic.msb.com.ImageConstant;
//import kobic.msb.system.engine.MsbEngine;
//
//import javax.swing.JLabel;
//import javax.swing.LayoutStyle.ComponentPlacement;
//import javax.swing.SwingConstants;

//import java.awt.Font;
//import java.io.IOException;

public class JMsbWelcomePanel extends JPanel{
	
	public JMsbWelcomePanel() {
//		try {
//			PlatformInit.getInstance().initLogging(false);
//			PlatformInit.getInstance().init(false, false);
//			
//			FramePanel framePanel = new FramePanel();
//			this.add(framePanel);
//			framePanel.navigate("http://lobobrowser.org/browser/home.jsp");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
//	private JPanel editorPane;
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	public JMsbWelcomePanel() {
//		
//		this.editorPane = new JPanel() {
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected void paintComponent( Graphics g ) {
//				super.paintComponent(g);
//
//				Graphics2D g2d = (Graphics2D) g;
//				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//				Color sC = new Color(62, 170, 255);
//				Color eC = new Color(228, 227, 255);
//				GradientPaint gp = new GradientPaint(0, 0, sC, 0, getHeight(), eC);
//
//				g2d.setPaint( gp );
//				g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
//			}
//		};
//		GroupLayout groupLayout = new GroupLayout(this);
//		groupLayout.setHorizontalGroup(
//			groupLayout.createParallelGroup(Alignment.LEADING)
//				.addGroup(groupLayout.createSequentialGroup()
//					.addContainerGap()
//					.addComponent(editorPane, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
//					.addContainerGap())
//		);
//		groupLayout.setVerticalGroup(
//			groupLayout.createParallelGroup(Alignment.LEADING)
//				.addGroup(groupLayout.createSequentialGroup()
//					.addContainerGap()
//					.addComponent(editorPane, GroupLayout.PREFERRED_SIZE, 288, Short.MAX_VALUE)
//					.addContainerGap())
//		);
//		
//		JLabel lblNewLabel = new JLabel("Create Project");
//		lblNewLabel.setForeground(Color.WHITE);
//		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 20));
//		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		
//		JLabel lblTutorial = new JLabel("Tutorial");
//		lblTutorial.setForeground(Color.WHITE);
//		lblTutorial.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 20));
//		lblTutorial.setHorizontalAlignment(SwingConstants.CENTER);
//		
//		JLabel lblNewLabel_1 = new JLabel( ImageConstant.msb_logo );
//		
//		JLabel lblNewLabel_2 = new JLabel( ImageConstant.ercsb_logo );
//		
//		JLabel lblNewLabel_3 = new JLabel( ImageConstant.kobic_logo );
//		
//		JLabel lblCopyrightkobic = new JLabel("Copyright@Korean Bioinformation Center(KOBIC) All Rights Reserved");
//		GroupLayout gl_editorPane = new GroupLayout(editorPane);
//		gl_editorPane.setHorizontalGroup(
//			gl_editorPane.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_editorPane.createSequentialGroup()
//					.addGroup(gl_editorPane.createParallelGroup(Alignment.LEADING)
//						.addGroup(gl_editorPane.createSequentialGroup()
//							.addContainerGap()
//							.addComponent(lblCopyrightkobic, GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
//							.addPreferredGap(ComponentPlacement.UNRELATED)
//							.addComponent(lblNewLabel_3)
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addComponent(lblNewLabel_2))
//						.addGroup(gl_editorPane.createSequentialGroup()
//							.addGap(299)
//							.addGroup(gl_editorPane.createParallelGroup(Alignment.TRAILING)
//								.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
//								.addComponent(lblTutorial, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
//							.addGap(294))
//						.addGroup(gl_editorPane.createSequentialGroup()
//							.addGap(19)
//							.addComponent(lblNewLabel_1)))
//					.addContainerGap())
//		);
//		gl_editorPane.setVerticalGroup(
//			gl_editorPane.createParallelGroup(Alignment.LEADING)
//				.addGroup(gl_editorPane.createSequentialGroup()
//					.addContainerGap()
//					.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
//					.addGap(182)
//					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
//					.addGap(31)
//					.addComponent(lblTutorial, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
//					.addPreferredGap(ComponentPlacement.RELATED, 263, Short.MAX_VALUE)
//					.addGroup(gl_editorPane.createParallelGroup(Alignment.TRAILING)
//						.addGroup(gl_editorPane.createParallelGroup(Alignment.LEADING)
//							.addComponent(lblNewLabel_2)
//							.addComponent(lblNewLabel_3))
//						.addComponent(lblCopyrightkobic))
//					.addContainerGap())
//		);
//		editorPane.setLayout(gl_editorPane);
//		setLayout(groupLayout);
//		
////		this.init();
//	}
	
//	private void init() {
//		HTMLEditorKit kit = new HTMLEditorKit();
//		
//		StyleSheet ss = kit.getStyleSheet();
//        ss.addRule("h2 {color : green; font-weight: bold;}");
//        ss.addRule("#mytag {color :  #bb0022; font-style:italic;}");
//        ss.addRule(".mytag {color : rgb(0,128,25); font-weight: bold;}");
//        ss.addRule("[mytag]{color : purple; font-family: monospace;}"); // not selected
//        ss.addRule("mytag {color : purple; text-size:14pt;}");  // not selected
//
//		editorPane.setEditorKit(kit);
//
//	    Document doc = kit.createDefaultDocument();
//	    editorPane.setDocument(doc);
//		String filepath = JMirnaChoosePanel.class.getResource( "/kobic/msb/resources/icons/help.png" ).toExternalForm();
//		
//		String textPaneString = "<html><body>Hello<br><img src='"+filepath+"'></body></html>";
//		
//		editorPane.setText( textPaneString );
//	}
}
