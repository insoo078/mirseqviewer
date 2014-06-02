package kobic.msb.swing.frame.dialog;

import javax.swing.JDialog;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import kobic.msb.common.ImageConstant;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.panel.config.JMsbAlignmentConfigPanel;
import kobic.msb.swing.panel.config.JMsbBrowserConfigPanel;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;

import javax.swing.JLabel;

public class JDrawingConfigurationDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JDrawingConfigurationDialog remote = JDrawingConfigurationDialog.this;
	
	private ProjectConfiguration		config;
	
	private JMsbBrowserConfigPanel		configPanel;
	private JMsbAlignmentConfigPanel	alignmentPanel;
	
	private JTabbedPane					tabbedPane;
	
	private AbstractDockingWindowObj	dockWindow;
	
	public JTabbedPane createTabbedPanel() {
		JTabbedPane tabbedPane = new JTabbedPane( SwingConstants.TOP );

		this.configPanel	= new JMsbBrowserConfigPanel( this.config );
		this.alignmentPanel = new JMsbAlignmentConfigPanel( this.config );
		
		tabbedPane.add( "Viwer", 	this.configPanel );
		tabbedPane.add( "Alignment", this.alignmentPanel );

		return tabbedPane;
	}

	public JDrawingConfigurationDialog( Frame owner, AbstractDockingWindowObj dockWindow, String projectName, String title, Dialog.ModalityType modalType ) {
		super( owner, title, modalType );

		this.dockWindow	= dockWindow;
		this.config		= MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName ).getProjectConfiguration();

		this.setResizable(false);
		
		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(Color.WHITE);
		
		tabbedPane = createTabbedPanel();
		
//		JLabel lblNewProjectLogo = new JLabel( ImageConstant.new_project_img );
//		lblNewProjectLogo.setSize(30, 30);
		
		JPanel buttonPanel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(titlePanel, GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
				.addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(titlePanel, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))
		);
		titlePanel.setLayout(null);
		
		JLabel lblConfiguration = new JLabel("Configuration");
		lblConfiguration.setBounds(6, 6, 137, 16);
		titlePanel.add(lblConfiguration);
		
		JLabel lblNewLabel = new JLabel( ImageConstant.new_project_img );
		lblNewLabel.setBounds(334, 6, 160, 65);
		titlePanel.add(lblNewLabel);
		
		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if( Float.parseFloat( remote.configPanel.getTxtMargin().getText() ) < 0 || Float.parseFloat( remote.configPanel.getTxtMargin().getText() ) > 50 ) {
					JOptionPane.showMessageDialog( remote, "Sorry! marginal range is restricted from 1 to 50", "Dialog", JOptionPane.ERROR_MESSAGE);
					remote.configPanel.getTxtMargin().requestFocus();
					return;
				}

				if( Float.parseFloat( remote.configPanel.getTxtOffset().getText() ) < 0 ) {
					JOptionPane.showMessageDialog( remote, "Track header offset > 0", "Dialog", JOptionPane.ERROR_MESSAGE);
					remote.configPanel.getTxtOffset().requestFocus();
					return;
				}
				
				if( Float.parseFloat( remote.configPanel.getTxtTrackHeight().getText() ) < 0 ) {
					JOptionPane.showMessageDialog( remote, "Track height > 0", "Dialog", JOptionPane.ERROR_MESSAGE);
					remote.configPanel.getTxtTrackHeight().requestFocus();
					return;
				}
				
				if( Float.parseFloat( remote.configPanel.getTxtNtWidth().getText() ) < 1 ) {
					JOptionPane.showMessageDialog( remote, "Nucleotide width > 0", "Dialog", JOptionPane.ERROR_MESSAGE);
					remote.configPanel.getTxtNtWidth().requestFocus();
					return;
				}
				
				remote.config.setMargin(						Float.parseFloat( remote.configPanel.getTxtMargin().getText() )			);
				remote.config.setOffset(						Float.parseFloat( remote.configPanel.getTxtOffset().getText() )			);
				remote.config.setSystemFont(					remote.configPanel.getSystemFont()										);
				remote.config.setSystemBoldFont(				remote.configPanel.getSystemBoldFont()									);
				remote.config.setAlphaForHorizontalHilightBar(	Float.parseFloat( remote.configPanel.getTxtHAlpha().getText() )			);
				remote.config.setAlphaForVerticalHilightBar(	Float.parseFloat( remote.configPanel.getTxtVAlpha().getText() )			);
				remote.config.setFieldBarColor(					remote.configPanel.getvBarColorPanel().getBackground()					);
				remote.config.setRecordBarColor(				remote.configPanel.gethBarColorPanel().getBackground()					);
				remote.config.setHistogramBarColorStart(		remote.configPanel.getHistoGradientColorStartPanel().getBackground()	);
				remote.config.setHistogramBarColorEnd(			remote.configPanel.getHistoGradientColorEndPanel().getBackground()		);

				remote.config.set_5p_upstream_color(			remote.alignmentPanel.get_5pUSColorPanel().getBackground()				);
				remote.config.set_5p_mature_color(				remote.alignmentPanel.get_5pColorPanel().getBackground()				);
				remote.config.set_loop_color(					remote.alignmentPanel.getLoopColorPanel().getBackground()				);
				remote.config.set_3p_mature_color(				remote.alignmentPanel.get_3pColorPanel().getBackground()				);
				remote.config.set_3p_downstream_color(			remote.alignmentPanel.get_3pDSColorPanel().getBackground()				);

				remote.config.setAdenosineColor(				remote.alignmentPanel.getAdenosineColorPanel().getBackground()			);
				remote.config.setThymidineColor(				remote.alignmentPanel.getThymidineColorPanel().getBackground()			);
				remote.config.setGuanosineColor(				remote.alignmentPanel.getGuanosineColorPanel().getBackground()			);
				remote.config.setCytidineColor(					remote.alignmentPanel.getCytidineColorPanel().getBackground()			);
				remote.config.setUnkowonNucleotideColor(		remote.alignmentPanel.getUnkowonNucleotideColorPanel().getBackground()	);
				
				remote.config.setMisForegroundColor(			remote.alignmentPanel.getMisForegroundColorPanel().getBackground()		);
				remote.config.setMisBackgroundColor(			remote.alignmentPanel.getMisBackgroundColorPanel().getBackground()		);
				
				remote.config.setMisABackgroundColor(			remote.alignmentPanel.getMisABackgroundColorPanel().getBackground()		);
				remote.config.setMisTBackgroundColor(			remote.alignmentPanel.getMisTBackgroundColorPanel().getBackground()		);
				remote.config.setMisGBackgroundColor(			remote.alignmentPanel.getMisGBackgroundColorPanel().getBackground()		);
				remote.config.setMisCBackgroundColor(			remote.alignmentPanel.getMisCBackgroundColorPanel().getBackground()		);
				remote.config.setMisXBackgroundColor(			remote.alignmentPanel.getMisXBackgroundColorPanel().getBackground()		);
				
				remote.config.setBlockHeight(					Float.parseFloat( remote.configPanel.getTxtTrackHeight().getText() )	);
				remote.config.setBlockWidth(					Float.parseFloat( remote.configPanel.getTxtNtWidth().getText() )		);

				remote.config.writeConfigFile();
				
				remote.config.reloadConfiguration();

				if( remote.dockWindow instanceof AlignmentDockingWindowObj ) {
					AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj) remote.dockWindow;
					dwo.setIsMousePositionFixed( false );
					dwo.getSsPanel().setMouseClicked( false );
	
					dwo.setMirid( dwo.getDefaultMirid() );
				}

				remote.dispose();
			}
		});
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( remote.dockWindow instanceof AlignmentDockingWindowObj ) {
					AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj) remote.dockWindow;
					dwo.setIsMousePositionFixed( false );
					dwo.getSsPanel().setMouseClicked( false );
				}

				remote.dispose();
			}
		});
		GroupLayout gl_buttonPanel = new GroupLayout(buttonPanel);
		gl_buttonPanel.setHorizontalGroup(
			gl_buttonPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_buttonPanel.createSequentialGroup()
					.addContainerGap(401, Short.MAX_VALUE)
					.addComponent(btnClose)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnApply)
					.addContainerGap())
		);
		gl_buttonPanel.setVerticalGroup(
			gl_buttonPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_buttonPanel.createSequentialGroup()
					.addContainerGap(19, Short.MAX_VALUE)
					.addGroup(gl_buttonPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnApply)
						.addComponent(btnClose))
					.addGap(14))
		);
		buttonPanel.setLayout(gl_buttonPanel);
		getContentPane().setLayout(groupLayout);
		
		setBounds(100, 100, 500, 647);
	}
}
