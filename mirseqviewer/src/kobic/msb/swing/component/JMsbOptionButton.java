package kobic.msb.swing.component;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

import kobic.msb.common.ImageConstant;

public class JMsbOptionButton extends JComponent{

	private JButton mainButton;
	private JButton optionButton;

	private final int PADDING = 2;
	
	private JMsbOptionButton remote = JMsbOptionButton.this;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JMsbOptionButton( ImageIcon icon ) {
		this( icon, null );
	}

	public JMsbOptionButton( ImageIcon icon, String tooltip ) {
		this.setLayout( new FlowLayout(FlowLayout.LEADING, this.PADDING, this.PADDING) );

		this.mainButton		= new JButton( icon );
		this.optionButton	= new JButton( ImageConstant.comboboxIcon );

		this.add( this.mainButton );
		this.add( this.optionButton );

		this.mainButton.setBorder( BorderFactory.createEmptyBorder(2, 2, 2, 2) );
		this.mainButton.setBorderPainted(true);
		this.mainButton.setContentAreaFilled(false);
		this.mainButton.setFocusPainted(true);

		this.optionButton.setBorder( BorderFactory.createEmptyBorder(2, 2, 2, 2) );
		this.optionButton.setBorderPainted(true);
		this.optionButton.setContentAreaFilled(false);
		this.optionButton.setFocusPainted(true);
		
		this.mainButton.addMouseListener( this.createMouseAdapter() );
		this.optionButton.addMouseListener( this.createMouseAdapter() );

		if( tooltip != null ){	this.mainButton.setToolTipText( tooltip );	}
	}
	
	public void addOptionActionListener(ActionListener l) {
		this.optionButton.addActionListener( l );
	}

	public void addActionListener(ActionListener l) {
		this.mainButton.addActionListener( l );
    }
	
	public MouseAdapter createMouseAdapter() {
		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				remote.mainButton.setBorder( BorderFactory.createEmptyBorder(2, 2, 2, 2) );
				remote.mainButton.setBorderPainted(true);
				remote.mainButton.setContentAreaFilled(true);
				remote.mainButton.setBackground( JMsbNullBorderButton.HOVERED_BUTTON_BACKGROUND_COLOR );
				remote.mainButton.revalidate();

				remote.optionButton.setBorder( BorderFactory.createEmptyBorder(2, 2, 2, 2) );
				remote.optionButton.setBorderPainted(true);
				remote.optionButton.setContentAreaFilled(true);
				remote.optionButton.setBackground( JMsbNullBorderButton.HOVERED_BUTTON_BACKGROUND_COLOR );
				remote.optionButton.revalidate();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				remote.mainButton.setBorder( BorderFactory.createEmptyBorder(2, 2, 2, 2) );
				remote.mainButton.setBorderPainted(true);
				remote.mainButton.setContentAreaFilled(false);
				remote.mainButton.setBackground( JMsbNullBorderButton.DEFAULT_BUTTON_BACKGROUND_COLOR );
				remote.mainButton.revalidate();

				remote.optionButton.setBorder( BorderFactory.createEmptyBorder(2, 2, 2, 2) );
				remote.optionButton.setBorderPainted(true);
				remote.optionButton.setContentAreaFilled(false);
				remote.optionButton.setBackground( JMsbNullBorderButton.DEFAULT_BUTTON_BACKGROUND_COLOR );
				remote.optionButton.revalidate();
			}
		};

		return adapter;
	}
}
