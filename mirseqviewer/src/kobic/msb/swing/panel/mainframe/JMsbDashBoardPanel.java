package kobic.msb.swing.panel.mainframe;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import kobic.msb.common.ImageConstant;
import kobic.msb.swing.panel.newproject.JMsbMatureChoosePanel;
import kobic.msb.system.engine.MsbEngine;

public class JMsbDashBoardPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public JMsbDashBoardPanel() {
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		
		JTextPane editorPane = new JTextPane();
		editorPane.setEditable(false);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(editorPane, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addComponent(editorPane, GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
					)
		);
		
		JLabel lblWelcome = new JLabel("Welcome to the miRseq Viewer");
		lblWelcome.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 16));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(14)
					.addComponent(lblWelcome, GroupLayout.PREFERRED_SIZE, 330, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(106, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblWelcome)
					.addContainerGap(38, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		setLayout(groupLayout);

		SimpleAttributeSet attributes = new SimpleAttributeSet();		
		
		// Create the StyleContext, the document and the pane
	    StyleContext sc = new StyleContext();
	    final DefaultStyledDocument doc = new DefaultStyledDocument(sc);
	    doc.setParagraphAttributes(2, 2, attributes, true);
	    
	    editorPane.setStyledDocument( doc );

	    // Create and add the style
	    final Style heading2Style = sc.addStyle("Heading2", null);
	    heading2Style.addAttribute(StyleConstants.Foreground, Color.red);
	    heading2Style.addAttribute(StyleConstants.FontSize, new Integer(16));
	    heading2Style.addAttribute(StyleConstants.FontFamily, "serif");
	    heading2Style.addAttribute(StyleConstants.Bold, new Boolean(true));

	    try {
	    	doc.insertString( 0, "hello", null );
	    	
	    	StyledDocument sdoc = editorPane.getStyledDocument();
	    	Style style = sdoc.addStyle("StyleName", null);
	    	StyleConstants.setIcon(style, ImageConstant.helpIcon);
	    	sdoc.insertString(1, "Test", style);
	    } catch (BadLocationException e) {
	    	// TODO Auto-generated catch block
//	    	e.printStackTrace();
	    	MsbEngine.logger.error( e );
	    }

//	    doc.setParagraphAttributes(0, 1, heading2Style, false);
	}
	
	public static void main(final String args[]) {
	    JFrame frame = new JFrame("Tab Attributes");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    JTextPane textPane = new JTextPane();

//	    int positions[] = { TabStop.ALIGN_BAR, TabStop.ALIGN_CENTER, TabStop.ALIGN_DECIMAL,
//	        TabStop.ALIGN_LEFT, TabStop.ALIGN_RIGHT };
//	    String strings[] = { "\tTabStop.ALIGN_BAR\n", "\tTabStop.ALIGN_CENTER\n",
//	        "\tTabStop.ALIGN_DECIMAL\n", "\tTabStop.ALIGN_LEFT\n", "\tTabStop.ALIGN_RIGHT\n" };
//
//	    SimpleAttributeSet attributes = new SimpleAttributeSet();
//
//	    StyledDocument document = textPane.getStyledDocument();
//	    for (int i = 0, n = positions.length; i < n; i++) {
//	      TabStop tabstop = new TabStop(150, positions[i], TabStop.LEAD_DOTS);
//	      
//	      try {
//	        int position = document.getLength();
//	        document.insertString(position, strings[i], null);
//	        TabSet tabset = new TabSet(new TabStop[] { tabstop });
//
//	        textPane.setCaretPosition( position );
//	        JLabel label = new JLabel( ImageConstant.helpIcon );
//	        label.addMouseListener( new MouseAdapter() {
//	        	@Override
//	        	public void mouseClicked(MouseEvent e) {
//	        		System.out.println("Hello");
//	        	}
//	        });
//	        label.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
//	        textPane.insertComponent( label );
//	        
//	        
//	        StyleConstants.setTabSet(attributes, tabset);
//	        document.setParagraphAttributes(position, 1, attributes, false);
//	      } catch (BadLocationException badLocationException) {
//	        System.err.println("Bad Location");
//	      }
//	    }
	    
	    textPane.setEditable(false);
	    
	    HTMLEditorKit kit = new HTMLEditorKit();

	    textPane.setEditorKit(kit);
	    
	 // add some styles to the html
	    StyleSheet styleSheet = kit.getStyleSheet();
	    styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
	    styleSheet.addRule("h1 {color: blue;}");
	    styleSheet.addRule("h2 {color: #ff0000;}");
	    styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
	    
	    Document doc = kit.createDefaultDocument();
	    textPane.setDocument(doc);
	    
	    String filepath = JMsbMatureChoosePanel.class.getResource( "/kobic/msb/resources/icons/help.png" ).toExternalForm();
	    textPane.setText( "<html><body>Hello<br><img src='"+filepath+"'></body></html>");
	    textPane.insertComponent( new JLabel(ImageConstant.printIcon) );
	    
	    JScrollPane scrollPane = new JScrollPane(textPane);
	    frame.add(scrollPane, BorderLayout.CENTER);

	    frame.setSize(300, 150);
	    frame.setVisible(true);

	  }
}
