package kobic.msb.swing.component;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class JMsbNullBorderButton extends JButton {

	private JMsbNullBorderButton remote = JMsbNullBorderButton.this;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final Color DEFAULT_BUTTON_BACKGROUND_COLOR	= new Color(227, 227, 227);
	public static final Color HOVERED_BUTTON_BACKGROUND_COLOR	= new Color(221, 221, 221);

	public JMsbNullBorderButton(ImageIcon imageIcon, String tooltip) {
		super(imageIcon);
		
		this.setToolTipText( tooltip );
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				remote.setBorder( BorderFactory.createEmptyBorder(2, 2, 2, 2) );
				remote.setBorderPainted(true);
				remote.setContentAreaFilled(true);
				remote.setBackground( JMsbNullBorderButton.HOVERED_BUTTON_BACKGROUND_COLOR );
				remote.revalidate();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				remote.setBorder( BorderFactory.createEmptyBorder(2, 2, 2, 2) );
				remote.setBorderPainted(true);
				remote.setContentAreaFilled(false);
				remote.setBackground( JMsbNullBorderButton.DEFAULT_BUTTON_BACKGROUND_COLOR );
				remote.revalidate();
			}
		});
		
		this.setBorder( BorderFactory.createEmptyBorder(2, 2, 2, 2) );
		this.setBorderPainted(true);
		this.setContentAreaFilled(false);
		this.setFocusPainted(true);
	}
}
