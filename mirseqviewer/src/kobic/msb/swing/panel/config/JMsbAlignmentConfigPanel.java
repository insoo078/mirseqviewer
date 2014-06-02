package kobic.msb.swing.panel.config;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import kobic.msb.swing.frame.dialog.JColorChooseDialog;
import kobic.msb.system.config.ProjectConfiguration;

public class JMsbAlignmentConfigPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel _5pUSColorPanel;
	private JPanel _5pColorPanel;
	private JPanel loopColorPanel;
	private JPanel _3pColorPanel;
	private JPanel _3pDSColorPanel;
	
	private JPanel adenosineColorPanel;
	private JPanel thymidineColorPanel;
	private JPanel guanosineColorPanel;
	private JPanel cytidineColorPanel;
	private JPanel unkowonNucleotideColorPanel;
	
	private JPanel misForegroundColorPanel;
	private JPanel misBackgroundColorPanel;
	
	private JPanel misABackgroundColorPanel;
	private JPanel misTBackgroundColorPanel;
	private JPanel misGBackgroundColorPanel;
	private JPanel misCBackgroundColorPanel;
	private JPanel misXBackgroundColorPanel;
	
	private ProjectConfiguration config;

	private JMsbAlignmentConfigPanel remote = JMsbAlignmentConfigPanel.this;
	
	public JMsbAlignmentConfigPanel( ProjectConfiguration config ) {
		this.config = config;
		
		this.setLayout( null );
		
		this.add( this.createSecondaryStructureColorPanel() );
		this.add( this.createNucleotideBackgroundColorPanel() );
		this.add( this.createMismatchedNucleotideColorPanel() );
	}
	
	private JPanel createMismatchedNucleotideColorPanel() {
		JPanel mismatchedColorPanel = new JPanel();
		mismatchedColorPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Color for Mis-matched Nucleotide") );
		mismatchedColorPanel.setBounds(6, 171, 455, 123);
		mismatchedColorPanel.setLayout(null);
		
		JLabel lblForeground = new JLabel("Foreground");
		lblForeground.setBounds(17, 26, 85, 16);
		mismatchedColorPanel.add(lblForeground);
		
		this.misForegroundColorPanel = new JPanel();
		misForegroundColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		misForegroundColorPanel.setBackground( this.config.getMisForegroundColor() );
		misForegroundColorPanel.setBounds(101, 22, 30, 25);
		misForegroundColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( misForegroundColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		mismatchedColorPanel.add(misForegroundColorPanel);
		
		JLabel label_1 = new JLabel("Background");
		label_1.setBounds(154, 26, 80, 16);
		mismatchedColorPanel.add(label_1);
		
		this.misBackgroundColorPanel = new JPanel();
		misBackgroundColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		misBackgroundColorPanel.setBackground( this.config.getMisBackgroundColor() );
		misBackgroundColorPanel.setBounds(500, 17, 30, 25);
		misBackgroundColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( misBackgroundColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		mismatchedColorPanel.add(misBackgroundColorPanel);
		
		JLabel lblA_1 = new JLabel("A");
		lblA_1.setBounds(17, 58, 20, 16);
		mismatchedColorPanel.add(lblA_1);
		
		this.misABackgroundColorPanel = new JPanel();
		misABackgroundColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		misABackgroundColorPanel.setBackground( this.config.getMisABackgroundColor() );
		misABackgroundColorPanel.setBounds(49, 54, 30, 25);
		misABackgroundColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( misABackgroundColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		mismatchedColorPanel.add(misABackgroundColorPanel);
		
		JLabel lblTu_1 = new JLabel("T,U");
		lblTu_1.setBounds(91, 58, 30, 16);
		mismatchedColorPanel.add(lblTu_1);
		
		this.misTBackgroundColorPanel = new JPanel();
		misTBackgroundColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		misTBackgroundColorPanel.setBackground(this.config.getMisTBackgroundColor());
		misTBackgroundColorPanel.setBounds(123, 54, 30, 25);
		misTBackgroundColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( misTBackgroundColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		mismatchedColorPanel.add(misTBackgroundColorPanel);
		
		JLabel lblG_1 = new JLabel("G");
		lblG_1.setBounds(172, 58, 20, 16);
		mismatchedColorPanel.add(lblG_1);
		
		this.misGBackgroundColorPanel = new JPanel();
		misGBackgroundColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		misGBackgroundColorPanel.setBackground(this.config.getMisGBackgroundColor());
		misGBackgroundColorPanel.setBounds(204, 54, 30, 25);
		misGBackgroundColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( misGBackgroundColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		mismatchedColorPanel.add(misGBackgroundColorPanel);
		
		JLabel lblC_1 = new JLabel("C");
		lblC_1.setBounds(244, 58, 20, 16);
		mismatchedColorPanel.add(lblC_1);
		
		this.misCBackgroundColorPanel = new JPanel();
		misCBackgroundColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		misCBackgroundColorPanel.setBackground(this.config.getMisCBackgroundColor());
		misCBackgroundColorPanel.setBounds(276, 54, 30, 25);
		misCBackgroundColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( misCBackgroundColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		mismatchedColorPanel.add(misCBackgroundColorPanel);
		
		JLabel lblX_1 = new JLabel("X");
		lblX_1.setBounds(318, 58, 20, 16);
		mismatchedColorPanel.add(lblX_1);
		
		this.misXBackgroundColorPanel = new JPanel();
		misXBackgroundColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		misXBackgroundColorPanel.setBackground(this.config.getMisXBackgroundColor());
		misXBackgroundColorPanel.setBounds(350, 54, 30, 25);
		misXBackgroundColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( misXBackgroundColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		mismatchedColorPanel.add(misXBackgroundColorPanel);
		
		return mismatchedColorPanel;
	}
	
	private JPanel createNucleotideBackgroundColorPanel() {
		JPanel nucleotideColorPanel = new JPanel();
		nucleotideColorPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Background Color for Nucleotide") );
		nucleotideColorPanel.setBounds(6, 102, 455, 66);
		nucleotideColorPanel.setLayout(null);
		
		JLabel lblA = new JLabel("A");
		lblA.setBounds(19, 30, 21, 16);
		nucleotideColorPanel.add(lblA);
		
		this.adenosineColorPanel = new JPanel();
		adenosineColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		adenosineColorPanel.setBackground( this.config.getAdenosineColor() );
		adenosineColorPanel.setBounds(48, 26, 30, 25);
		adenosineColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( adenosineColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		nucleotideColorPanel.add(adenosineColorPanel);
		
		JLabel lblTu = new JLabel("T,U");
		lblTu.setBounds(92, 30, 30, 16);
		nucleotideColorPanel.add(lblTu);
		
		this.thymidineColorPanel = new JPanel();
		thymidineColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		thymidineColorPanel.setBackground( this.config.getThymidineColor() );
		thymidineColorPanel.setBounds(121, 26, 30, 25);
		thymidineColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( thymidineColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		nucleotideColorPanel.add(thymidineColorPanel);
		
		JLabel lblG = new JLabel("G");
		lblG.setBounds(176, 30, 21, 16);
		nucleotideColorPanel.add(lblG);
		
		this.guanosineColorPanel = new JPanel();
		guanosineColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		guanosineColorPanel.setBackground( this.config.getGuanosineColor() );
		guanosineColorPanel.setBounds(205, 26, 30, 25);
		guanosineColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( guanosineColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		nucleotideColorPanel.add(guanosineColorPanel);
		
		JLabel lblC = new JLabel("C");
		lblC.setBounds(247, 30, 21, 16);
		nucleotideColorPanel.add(lblC);
		
		this.cytidineColorPanel = new JPanel();
		cytidineColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		cytidineColorPanel.setBackground( this.config.getCytidineColor() );
		cytidineColorPanel.setBounds(276, 26, 30, 25);
		cytidineColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( cytidineColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		nucleotideColorPanel.add(cytidineColorPanel);
		
		this.unkowonNucleotideColorPanel = new JPanel();
		unkowonNucleotideColorPanel.setBorder(BorderFactory.createEtchedBorder( EtchedBorder.RAISED ));
		unkowonNucleotideColorPanel.setBackground( this.config.getUnkowonNucleotideColor() );
		unkowonNucleotideColorPanel.setBounds(356, 26, 30, 25);
		unkowonNucleotideColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( unkowonNucleotideColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		nucleotideColorPanel.add(unkowonNucleotideColorPanel);
		
		JLabel lblX = new JLabel("X");
		lblX.setBounds(323, 30, 21, 16);
		nucleotideColorPanel.add(lblX);

		return nucleotideColorPanel;
	}

	private JPanel createSecondaryStructureColorPanel() {
		JPanel secondaryStrColorPanel = new JPanel();
		secondaryStrColorPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ), "Color of Secondary Structure") );
		secondaryStrColorPanel.setBounds(6, 6, 455, 96);
		secondaryStrColorPanel.setLayout(null);
		
		JLabel lblupstream = new JLabel("5'-Upstream");
		lblupstream.setBounds(19, 30, 85, 16);
		
		JLabel lbl3pMature = new JLabel("3'-Mature");
		lbl3pMature.setBounds(19, 57, 85, 16);
		
		JLabel lbl5pMature = new JLabel("5'-Mature");
		lbl5pMature.setBounds(158, 30, 85, 16);
		
		JLabel lbldownstream = new JLabel("3'-Downstream");
		lbldownstream.setBounds(158, 57, 104, 16);

		JLabel lblLoop = new JLabel("Loop");
		lblLoop.setBounds(312, 30, 44, 16);
		
		_5pUSColorPanel = new JPanel();
		_5pUSColorPanel.setBackground( config.get_5p_upstream_color() );
		_5pUSColorPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );
		_5pUSColorPanel.setBounds(116, 26, 30, 25);
		_5pUSColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( _5pUSColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		
		_5pColorPanel = new JPanel();
		_5pColorPanel.setBackground( config.get_5p_mature_color() );
		_5pColorPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );
		_5pColorPanel.setBounds(262, 26, 30, 25);
		_5pColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( _5pColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		
		loopColorPanel = new JPanel();
		loopColorPanel.setBackground( config.get_loop_color() );
		loopColorPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );
		loopColorPanel.setBounds(354, 26, 30, 25);
		loopColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( loopColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		
		_3pColorPanel = new JPanel();
		_3pColorPanel.setBackground( config.get_3p_mature_color() );
		_3pColorPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );
		_3pColorPanel.setBounds(116, 53, 30, 25);
		_3pColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( _3pColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		
		_3pDSColorPanel = new JPanel();
		_3pDSColorPanel.setBackground( config.get_3p_downstream_color() );
		_3pDSColorPanel.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );
		_3pDSColorPanel.setBounds(262, 53, 30, 25);
		_3pDSColorPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( javax.swing.SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && !e.isConsumed() ) {
					JColorChooseDialog dialog = new JColorChooseDialog( _3pDSColorPanel );
					dialog.setVisible( true );
				}
			}
		});
		
		secondaryStrColorPanel.add(lblupstream);
		secondaryStrColorPanel.add(_5pUSColorPanel);
		secondaryStrColorPanel.add(lbl3pMature);
		secondaryStrColorPanel.add(_3pColorPanel);
		secondaryStrColorPanel.add(lbl5pMature);
		secondaryStrColorPanel.add(_5pColorPanel);
		secondaryStrColorPanel.add(lbldownstream);
		secondaryStrColorPanel.add( _3pDSColorPanel );
		secondaryStrColorPanel.add(lblLoop);
		secondaryStrColorPanel.add(loopColorPanel);
		
		return secondaryStrColorPanel;
	}

	public JPanel get_5pUSColorPanel() {
		return _5pUSColorPanel;
	}

	public void set_5pUSColorPanel(JPanel _5pUSColorPanel) {
		this._5pUSColorPanel = _5pUSColorPanel;
	}

	public JPanel get_5pColorPanel() {
		return _5pColorPanel;
	}

	public void set_5pColorPanel(JPanel _5pColorPanel) {
		this._5pColorPanel = _5pColorPanel;
	}

	public JPanel getLoopColorPanel() {
		return loopColorPanel;
	}

	public void setLoopColorPanel(JPanel loopColorPanel) {
		this.loopColorPanel = loopColorPanel;
	}

	public JPanel get_3pColorPanel() {
		return _3pColorPanel;
	}

	public void set_3pColorPanel(JPanel _3pColorPanel) {
		this._3pColorPanel = _3pColorPanel;
	}

	public JPanel get_3pDSColorPanel() {
		return _3pDSColorPanel;
	}

	public void set_3pDSColorPanel(JPanel _3pDSColorPanel) {
		this._3pDSColorPanel = _3pDSColorPanel;
	}

	public JPanel getAdenosineColorPanel() {
		return adenosineColorPanel;
	}

	public void setAdenosineColorPanel(JPanel adenosineColorPanel) {
		this.adenosineColorPanel = adenosineColorPanel;
	}

	public JPanel getThymidineColorPanel() {
		return thymidineColorPanel;
	}

	public void setThymidineColorPanel(JPanel thymidineColorPanel) {
		this.thymidineColorPanel = thymidineColorPanel;
	}

	public JPanel getGuanosineColorPanel() {
		return guanosineColorPanel;
	}

	public void setGuanosineColorPanel(JPanel guanosineColorPanel) {
		this.guanosineColorPanel = guanosineColorPanel;
	}

	public JPanel getCytidineColorPanel() {
		return cytidineColorPanel;
	}

	public void setCytidineColorPanel(JPanel cytidineColorPanel) {
		this.cytidineColorPanel = cytidineColorPanel;
	}

	public JPanel getUnkowonNucleotideColorPanel() {
		return unkowonNucleotideColorPanel;
	}

	public void setUnkowonNucleotideColorPanel(JPanel unkowonNucleotideColorPanel) {
		this.unkowonNucleotideColorPanel = unkowonNucleotideColorPanel;
	}

	public JPanel getMisForegroundColorPanel() {
		return misForegroundColorPanel;
	}

	public void setMisForegroundColorPanel(JPanel misForegroundColorPanel) {
		this.misForegroundColorPanel = misForegroundColorPanel;
	}

	public JPanel getMisBackgroundColorPanel() {
		return misBackgroundColorPanel;
	}

	public void setMisBackgroundColorPanel(JPanel misBackgroundColorPanel) {
		this.misBackgroundColorPanel = misBackgroundColorPanel;
	}

	public JPanel getMisABackgroundColorPanel() {
		return misABackgroundColorPanel;
	}

	public void setMisABackgroundColorPanel(JPanel misABackgroundColorPanel) {
		this.misABackgroundColorPanel = misABackgroundColorPanel;
	}

	public JPanel getMisTBackgroundColorPanel() {
		return misTBackgroundColorPanel;
	}

	public void setMisTBackgroundColorPanel(JPanel misTBackgroundColorPanel) {
		this.misTBackgroundColorPanel = misTBackgroundColorPanel;
	}

	public JPanel getMisGBackgroundColorPanel() {
		return misGBackgroundColorPanel;
	}

	public void setMisGBackgroundColorPanel(JPanel misGBackgroundColorPanel) {
		this.misGBackgroundColorPanel = misGBackgroundColorPanel;
	}

	public JPanel getMisCBackgroundColorPanel() {
		return misCBackgroundColorPanel;
	}

	public void setMisCBackgroundColorPanel(JPanel misCBackgroundColorPanel) {
		this.misCBackgroundColorPanel = misCBackgroundColorPanel;
	}

	public JPanel getMisXBackgroundColorPanel() {
		return misXBackgroundColorPanel;
	}

	public void setMisXBackgroundColorPanel(JPanel misXBackgroundColorPanel) {
		this.misXBackgroundColorPanel = misXBackgroundColorPanel;
	}

}
