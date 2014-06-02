package kobic.msb.swing.panel.config;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import kobic.msb.common.util.SwingUtilities;
import kobic.msb.swing.frame.dialog.JColorChooseDialog;
import kobic.msb.swing.frame.dialog.JFontChooser;
import kobic.msb.system.config.ProjectConfiguration;

public class JMsbBrowserConfigPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField txtSysFontName;
	private JTextField txtSysFontStyle;
	private JTextField txtSysFontSize;

	private JTextField txtSysBoldFontName;
	private JTextField txtSysBoldFontStyle;
	private JTextField txtSysBoldFontSize;
	
	private JTextField txtMargin;
	private JTextField txtOffset;
	private JTextField txtTrackHeight;
	private JTextField txtNtWidth;
	
	private JTextField txtVAlpha;
	private JTextField txtHAlpha;
	private JSlider sliderVAlpha;
	private JSlider sliderHAlpha;
	
	private JPanel hBarColorPanel;
	private JPanel vBarColorPanel;
	
	private JPanel histoGradientColorStartPanel;
	private JPanel histoGradientColorEndPanel;
	
	private Font systemFont;
	private Font systemBoldFont;
	
	private ProjectConfiguration config;
	
	private JMsbBrowserConfigPanel remote = JMsbBrowserConfigPanel.this;
	
	public JMsbBrowserConfigPanel( ProjectConfiguration config ) {
		this.config			= config;
		
		this.systemFont		= config.getSystemFont();
		this.systemBoldFont = config.getSystemBoldFont();
		
		this.setLayout( null );
		
		this.add( this.createFontPanel() );
		this.add( this.createAdjustmentPanel() );
		this.add( this.createTransparencyPanel() );
		this.add( this.createColorPanel() );
	}

	private JPanel createFontPanel() {
		JPanel fontPanel = new JPanel();

		fontPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Font") );
		fontPanel.setBounds(6, 6, 455, 88);
		fontPanel.setLayout(null);

		JButton btnChooseSystemFont = new JButton("System Font");
		btnChooseSystemFont.setBounds(6, 19, 103, 29);
		fontPanel.add(btnChooseSystemFont);

		JButton btnBoldFont = new JButton("Bold Font");
		btnBoldFont.setBounds(6, 53, 103, 29);
		
		txtSysFontName = new JTextField( SwingUtilities.getRealFontName(systemFont.getName()) );
		txtSysFontName.setBounds(113, 19, 141, 28);

		txtSysFontName.setEditable(false);
		txtSysFontName.setColumns(10);
		txtSysBoldFontName = new JTextField( SwingUtilities.getRealFontName(systemBoldFont.getName()) );
		txtSysBoldFontName.setBounds(113, 53, 141, 28);

		txtSysBoldFontName.setColumns(10);
		
		txtSysFontStyle = new JTextField( SwingUtilities.getStyleFromInteger( systemFont.getStyle() ) );
		txtSysFontStyle.setBounds(267, 19, 87, 28);
		
		txtSysFontStyle.setEditable(false);
		txtSysFontStyle.setColumns(10);
		
		txtSysBoldFontStyle = new JTextField( SwingUtilities.getStyleFromInteger( systemBoldFont.getStyle() ) );
		txtSysBoldFontStyle.setBounds(267, 53, 87, 28);
		txtSysBoldFontStyle.setColumns(10);
		
		txtSysFontSize = new JTextField( Integer.toString( systemFont.getSize() ) );
		txtSysFontSize.setBounds(360, 19, 89, 28);
		txtSysFontSize.setEditable(false);
		txtSysFontSize.setColumns(10);
		
		txtSysBoldFontSize = new JTextField( Integer.toString( systemBoldFont.getSize() ) );
		txtSysBoldFontSize.setBounds(360, 53, 89, 28);
		txtSysBoldFontSize.setColumns(10);
		
		fontPanel.add(btnBoldFont);
		fontPanel.add(txtSysFontName);
		fontPanel.add(txtSysFontStyle);
		fontPanel.add(txtSysFontSize);
		fontPanel.add(txtSysBoldFontName);
		fontPanel.add(txtSysBoldFontStyle);
		fontPanel.add(txtSysBoldFontSize);

		btnBoldFont.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFontChooser fontChooser = new JFontChooser( txtSysBoldFontName.getText(), txtSysBoldFontStyle.getText(), txtSysBoldFontSize.getText() );
				if( fontChooser.showDialog( remote ) == 0 ) {;
					remote.systemBoldFont = fontChooser.getSelectedFont();
					int size = fontChooser.getSelectedFontSize();
					int style = fontChooser.getSelectedFontStyle();
					
					txtSysBoldFontName.setText( SwingUtilities.getRealFontName( remote.systemBoldFont.getName() ) );
					txtSysBoldFontStyle.setText( SwingUtilities.getStyleFromInteger( style ) );
					txtSysBoldFontSize.setText( Integer.toString( size ) );
				}
			}
		});

		btnChooseSystemFont.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFontChooser fontChooser = new JFontChooser( txtSysFontName.getText(), txtSysFontStyle.getText(), txtSysFontSize.getText() );
				if( fontChooser.showDialog( remote ) == 0 ) {;
					remote.systemFont = fontChooser.getSelectedFont();
					int size = fontChooser.getSelectedFontSize();
					int style = fontChooser.getSelectedFontStyle();
					
					txtSysFontName.setText( SwingUtilities.getRealFontName(remote.systemFont.getName()) );
					txtSysFontStyle.setText( SwingUtilities.getStyleFromInteger( style ) );
					txtSysFontSize.setText( Integer.toString( size ) );
				}
			}
		});

		return fontPanel;
	}

	private JPanel createAdjustmentPanel() {
		JPanel adjustPanel = new JPanel();
		adjustPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Adjustment Values") );
		adjustPanel.setBounds(6, 96, 455, 82);
		adjustPanel.setLayout(null);

		JLabel lblMargin = new JLabel("Margin");
		lblMargin.setBounds(16, 23, 43, 16);
		
		JLabel lblTrackHeight = new JLabel("Track Height");
		lblTrackHeight.setBounds(16, 51, 81, 16);
		
		txtMargin = new JTextField( Float.toString( config.getMargin()) );
		txtMargin.setBounds(109, 17, 59, 28);
		txtMargin.setColumns(10);
		
		txtTrackHeight = new JTextField( Float.toString( config.getBlockHeight() ) );
		txtTrackHeight.setBounds(109, 45, 59, 28);
		txtTrackHeight.setColumns(10);

		JLabel lblOffset = new JLabel("Track Header Offset");
		lblOffset.setBounds(182, 22, 126, 19);
		
		JLabel lblNtWidth = new JLabel("Nucleotide Width");
		lblNtWidth.setBounds(182, 51, 126, 16);
		
		txtOffset = new JTextField( Float.toString( config.getOffset() ) );
		txtOffset.setBounds(316, 17, 97, 28);
		txtOffset.setColumns(10);
		
		txtNtWidth = new JTextField( Float.toString( config.getBlockWidth() ) );
		txtNtWidth.setBounds(316, 45, 97, 28);
		txtNtWidth.setColumns(10);
		txtNtWidth.setEditable(false);
		txtNtWidth.setEnabled(false);
		
		adjustPanel.add(lblMargin);
		adjustPanel.add(lblTrackHeight);
		adjustPanel.add(txtMargin);
		adjustPanel.add(txtTrackHeight);
		adjustPanel.add(lblOffset);
		adjustPanel.add(lblNtWidth);
		adjustPanel.add(txtOffset);
		adjustPanel.add(txtNtWidth);
		
		return adjustPanel;
	}

	private JPanel createTransparencyPanel() {
		JPanel transparencyPanel = new JPanel();
		transparencyPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Transparency") );
		transparencyPanel.setBounds(6, 182, 455, 82);
		transparencyPanel.setLayout(null);
		
		JLabel lblVeriticalBarTransparency = new JLabel("Alpha for Veritical Bar");
		lblVeriticalBarTransparency.setBounds(16, 25, 136, 16);
		
		txtVAlpha = new JTextField( Float.toString( config.getAlphaForVerticalHilightBar() ) );
		txtVAlpha.setEditable(false);
		txtVAlpha.setBounds(179, 19, 40, 28);
		txtVAlpha.setColumns(10);
		
		sliderVAlpha = new JSlider();
		sliderVAlpha.setBounds(236, 19, 190, 29);
		sliderVAlpha.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				int fps = source.getValue();
				txtVAlpha.setText( Float.toString( (float)fps / 100 ) );
			}
		});
		sliderVAlpha.setValue( (int)(config.getAlphaForVerticalHilightBar() * 100) );
		
		JLabel lblHorizontalBarTransparency = new JLabel("Alpha for Horizontal Bar");
		lblHorizontalBarTransparency.setBounds(16, 48, 151, 16);
		
		txtHAlpha = new JTextField( Float.toString( config.getAlphaForHorizontalHilightBar() ) );
		txtHAlpha.setEditable(false);
		txtHAlpha.setBounds(179, 42, 40, 28);
		txtHAlpha.setColumns(10);
		
		sliderHAlpha = new JSlider();
		sliderHAlpha.setBounds(236, 42, 190, 29);
		sliderHAlpha.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				int fps = source.getValue();
				txtHAlpha.setText( Float.toString( (float)fps / 100 ) );
			}
		});
		sliderHAlpha.setValue( (int)(config.getAlphaForHorizontalHilightBar() * 100) );
		
		transparencyPanel.add(lblVeriticalBarTransparency);
		transparencyPanel.add(txtVAlpha);
		transparencyPanel.add(sliderVAlpha);
		transparencyPanel.add(lblHorizontalBarTransparency);
		transparencyPanel.add(txtHAlpha);
		transparencyPanel.add(sliderHAlpha);
		
		return transparencyPanel;
	}

	private JPanel createColorPanel() {
		JPanel colorPanel = new JPanel();
		colorPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Colors") );
		colorPanel.setBounds(6, 266, 455, 56);
		colorPanel.setLayout(null);
		
		JLabel lblHbarColor = new JLabel("H.Bar Color");
		lblHbarColor.setBounds(16, 23, 71, 16);
		
		hBarColorPanel = new JPanel();
		hBarColorPanel.setBounds(92, 18, 31, 27);
		hBarColorPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );
		hBarColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( hBarColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		hBarColorPanel.setBackground( config.getRecordBarColor() );
		
		JLabel lblVbarColor = new JLabel("V.Bar Color");
		lblVbarColor.setBounds(137, 23, 87, 16);
		
		vBarColorPanel = new JPanel();
		vBarColorPanel.setBounds(212, 18, 31, 26);
		vBarColorPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );
		vBarColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( vBarColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		vBarColorPanel.setBackground( config.getFieldBarColor() );
		
		JLabel lblHistogramColor = new JLabel("Histogram Color");
		lblHistogramColor.setBounds(257, 23, 109, 16);
		
		histoGradientColorStartPanel = new JPanel();
		histoGradientColorStartPanel.setBounds(366, 17, 31, 29);
		histoGradientColorStartPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );
		histoGradientColorStartPanel.setBackground( config.getHistogramBarColorStart() );
		histoGradientColorStartPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( histoGradientColorStartPanel );
					dialog.setVisible( true );
				}
			}
		});
		
		histoGradientColorEndPanel = new JPanel();
		histoGradientColorEndPanel.setBounds(406, 17, 31, 29);
		histoGradientColorEndPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );
		histoGradientColorEndPanel.setBackground( config.getHistogramBarColorEnd() );
		histoGradientColorEndPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( histoGradientColorEndPanel );
					dialog.setVisible( true );
				}
			}
		});
		
		colorPanel.add(lblHbarColor);
		colorPanel.add(hBarColorPanel);
		colorPanel.add(lblVbarColor);
		colorPanel.add(vBarColorPanel);
		colorPanel.add(lblHistogramColor);
		colorPanel.add(histoGradientColorStartPanel);
		colorPanel.add(histoGradientColorEndPanel);
		
		return colorPanel;
	}

	public JTextField getTxtSysFontName() {
		return txtSysFontName;
	}

	public void setTxtSysFontName(JTextField txtSysFontName) {
		this.txtSysFontName = txtSysFontName;
	}

	public JTextField getTxtSysFontStyle() {
		return txtSysFontStyle;
	}

	public void setTxtSysFontStyle(JTextField txtSysFontStyle) {
		this.txtSysFontStyle = txtSysFontStyle;
	}

	public JTextField getTxtSysFontSize() {
		return txtSysFontSize;
	}

	public void setTxtSysFontSize(JTextField txtSysFontSize) {
		this.txtSysFontSize = txtSysFontSize;
	}

	public JTextField getTxtSysBoldFontName() {
		return txtSysBoldFontName;
	}

	public void setTxtSysBoldFontName(JTextField txtSysBoldFontName) {
		this.txtSysBoldFontName = txtSysBoldFontName;
	}

	public JTextField getTxtSysBoldFontStyle() {
		return txtSysBoldFontStyle;
	}

	public void setTxtSysBoldFontStyle(JTextField txtSysBoldFontStyle) {
		this.txtSysBoldFontStyle = txtSysBoldFontStyle;
	}

	public JTextField getTxtSysBoldFontSize() {
		return txtSysBoldFontSize;
	}

	public void setTxtSysBoldFontSize(JTextField txtSysBoldFontSize) {
		this.txtSysBoldFontSize = txtSysBoldFontSize;
	}

	public JTextField getTxtMargin() {
		return txtMargin;
	}

	public void setTxtMargin(JTextField txtMargin) {
		this.txtMargin = txtMargin;
	}

	public JTextField getTxtOffset() {
		return txtOffset;
	}

	public void setTxtOffset(JTextField txtOffset) {
		this.txtOffset = txtOffset;
	}

	public JTextField getTxtTrackHeight() {
		return txtTrackHeight;
	}

	public void setTxtTrackHeight(JTextField txtTrackHeight) {
		this.txtTrackHeight = txtTrackHeight;
	}

	public JTextField getTxtNtWidth() {
		return txtNtWidth;
	}

	public void setTxtNtWidth(JTextField txtNtWidth) {
		this.txtNtWidth = txtNtWidth;
	}

	public JTextField getTxtVAlpha() {
		return txtVAlpha;
	}

	public void setTxtVAlpha(JTextField txtVAlpha) {
		this.txtVAlpha = txtVAlpha;
	}

	public JTextField getTxtHAlpha() {
		return txtHAlpha;
	}

	public void setTxtHAlpha(JTextField txtHAlpha) {
		this.txtHAlpha = txtHAlpha;
	}

	public JSlider getSliderVAlpha() {
		return sliderVAlpha;
	}

	public void setSliderVAlpha(JSlider sliderVAlpha) {
		this.sliderVAlpha = sliderVAlpha;
	}

	public JSlider getSliderHAlpha() {
		return sliderHAlpha;
	}

	public void setSliderHAlpha(JSlider sliderHAlpha) {
		this.sliderHAlpha = sliderHAlpha;
	}

	public Font getSystemFont() {
		return systemFont;
	}

	public void setSystemFont(Font systemFont) {
		this.systemFont = systemFont;
	}

	public Font getSystemBoldFont() {
		return systemBoldFont;
	}

	public void setSystemBoldFont(Font systemBoldFont) {
		this.systemBoldFont = systemBoldFont;
	}

	public JPanel gethBarColorPanel() {
		return hBarColorPanel;
	}

	public void sethBarColorPanel(JPanel hBarColorPanel) {
		this.hBarColorPanel = hBarColorPanel;
	}

	public JPanel getvBarColorPanel() {
		return vBarColorPanel;
	}

	public void setvBarColorPanel(JPanel vBarColorPanel) {
		this.vBarColorPanel = vBarColorPanel;
	}

	public JPanel getHistoGradientColorStartPanel() {
		return histoGradientColorStartPanel;
	}

	public void setHistoGradientColorStartPanel(JPanel histoGradientColorStartPanel) {
		this.histoGradientColorStartPanel = histoGradientColorStartPanel;
	}

	public JPanel getHistoGradientColorEndPanel() {
		return histoGradientColorEndPanel;
	}

	public void setHistoGradientColorEndPanel(JPanel histoGradientColorEndPanel) {
		this.histoGradientColorEndPanel = histoGradientColorEndPanel;
	}

	
}
