package kobic.msb.swing.component;

import java.awt.Graphics;
import javax.swing.JMenuItem;

public class JAutocompleteMenuItem extends JMenuItem{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String keyword;

	public JAutocompleteMenuItem() {
		super();
	}
	
	public JAutocompleteMenuItem(String title) {
		super(title);
	}
	
	public void setKeyword( String keyword ) {
		this.keyword = keyword;
	}

	public String getText() {
		String txt = this.getText();
		txt = txt.replace("<html>", "");
		txt = txt.replace("<b>", "");
		txt = txt.replace("</b>", "");
		txt = txt.replace("<font color='red'>", "");
		txt = txt.replace("</font>", "");
		txt = txt.replace("</html>", "");

		return txt;
	}

	@Override
	public void paintComponent(Graphics g) {
		String txt = "<html><b>";
		txt += this.getText().replace( this.keyword, "<font color='red'>" + this.keyword + "</font>");
		txt += "</b></html>";

		this.setText( txt );

		super.paintComponent(g);
	}
}