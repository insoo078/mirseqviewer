package kobic.msb.swing.panel.alignment;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import kobic.com.util.Utilities;
import kobic.msb.common.ImageConstant;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.SwingConst;
import kobic.msb.server.model.Model;
import kobic.msb.server.obj.GenomeReferenceObject;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JMsbBrowserControlPane extends JPanel implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField					txtLocation;
	private JTextField					txtLength;
	private JTextField					txtStrand;
	/*** Over JDK 1.7 ***/
//	private JComboBox<String>	comboBox;
	/*** Under JDK 1.7 ***/
	private JComboBox					comboBox;
//	private JCheckBox					chckbxReadTooltipOnOff;
	private AbstractDockingWindowObj	dockWindow;
	private String						projectName;
	
	private JMsbBrowserSliderBarPanel	sliderBar;
	
	private ItemListener itemAction;

	JMsbBrowserControlPane		remote = JMsbBrowserControlPane.this;
	
	private ProjectConfiguration config;

	/**
	 * Create the panel.
	 */
	public JMsbBrowserControlPane( AbstractDockingWindowObj dockingWindow, String projectName, String mirid ) {
		this.dockWindow 	= dockingWindow;
		this.projectName	= projectName;
		
		this.itemAction = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( remote.comboBox.getSelectedItem() != null ) {
					String mirid = remote.comboBox.getSelectedItem().toString();
	
					if( remote.dockWindow instanceof AlignmentDockingWindowObj) {
						AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj)remote.dockWindow;
						dwo.setMirid( mirid );
						
						// update slider position
						remote.sliderBar.init();
					}
				}
			}
		};
		
		this.config = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( remote.projectName ).getProjectConfiguration();

		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(null);
		
		JLabel lblLocationPosition = new JLabel("Location");
		lblLocationPosition.setBounds(18, 10, 41, 25);
		rootPanel.add(lblLocationPosition);
		lblLocationPosition.setFont( SwingConst.DEFAULT_DIALOG_FONT );
		
		this.txtLocation = new JTextField();
		txtLocation.setEnabled(false);
		txtLocation.setBounds(64, 10, 200, 25);
		rootPanel.add(txtLocation);
		this.txtLocation.setColumns(10);
		this.txtLocation.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
		
		JLabel lblLength = new JLabel("Length");
		lblLength.setBounds(270, 10, 33, 25);
		rootPanel.add(lblLength);
		lblLength.setFont( SwingConst.DEFAULT_DIALOG_FONT );
		
		this.txtLength = new JTextField();
		txtLength.setEnabled(false);
		txtLength.setBounds(310, 10, 58, 25);
		rootPanel.add(txtLength);
		this.txtLength.setColumns(10);
		this.txtLength.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
		
		JLabel lblStrand = new JLabel("Strand");
		lblStrand.setBounds(373, 10, 32, 25);
		rootPanel.add(lblStrand);
		lblStrand.setFont( SwingConst.DEFAULT_DIALOG_FONT );
		
		this.txtStrand = new JTextField();
		txtStrand.setEnabled(false);
		txtStrand.setBounds(410, 10, 33, 25);
		rootPanel.add(txtStrand);
		this.txtStrand.setColumns(10);
		this.txtStrand.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
		
		JLabel lblZoom = new JLabel("Zoom");
		lblZoom.setBounds(18, 45, 28, 25);
		rootPanel.add(lblZoom);
		lblZoom.setFont( SwingConst.DEFAULT_DIALOG_FONT );
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(rootPanel, GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(rootPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		
		/*** Over JDK 1.7 ***/
//		this.comboBox = new JComboBox<String>();
		/*** Under JDK 1.7 ***/
		this.comboBox = new JComboBox();
		this.comboBox.addItemListener( this.itemAction );
		this.comboBox.setBounds(452, 10, 216, 25);
		rootPanel.add(this.comboBox);
		
		this.sliderBar = new JMsbBrowserSliderBarPanel( this.dockWindow, this.config );
		this.sliderBar.setBounds(169, 42, 160, 33);
		rootPanel.add(sliderBar);
		
		JButton btnZoomOut = new JButton( ImageConstant.zoomOutIcon );
		btnZoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remote.sliderBar.zoomOut();
			}
		});
		btnZoomOut.setBounds(115, 40, 50, 35);
		rootPanel.add(btnZoomOut);
		
		JButton btnInc = new JButton( ImageConstant.zoomInIcon );
		btnInc.setBounds(333, 40, 48, 35);
		btnInc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remote.sliderBar.zoomIn();
			}
		});
		rootPanel.add(btnInc);
		
		JButton btnMoveUpstream = new JButton( ImageConstant.leftArrowIcon );
		btnMoveUpstream.setBounds(64, 40, 50, 35);
		btnMoveUpstream.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remote.moveUpstream();
			}
		});
		rootPanel.add(btnMoveUpstream);
		
		JButton btnMoveDownstream = new JButton( ImageConstant.rightArrowIcon );
		btnMoveDownstream.setBounds(383, 40, 48, 35);
		btnMoveDownstream.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remote.moveDownstream();
			}
		});
		rootPanel.add(btnMoveDownstream);
		
//		this.chckbxReadTooltipOnOff = new JCheckBox("Read tooltip on/off");
//		this.chckbxReadTooltipOnOff.setSelected(true);
//		this.chckbxReadTooltipOnOff.setBounds(452, 44, 183, 23);
//		rootPanel.add(this.chckbxReadTooltipOnOff);

		this.initComboBox(projectName, mirid);
		this.initTextFields( mirid );

		this.setLayout(groupLayout);
	}
	
	public void initComboBox( String projectName, String mirid ) {
		ProjectMapItem item = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );
		List<String> mirnaList = item.getMiRnaList();

		for(String id:mirnaList) {
			this.comboBox.addItem( id );
		}

		this.comboBox.setSelectedItem( mirid );
	}
	
	public void initTextFields( String mirid ) {
		if( this.dockWindow instanceof AlignmentDockingWindowObj ) {
			AlignmentDockingWindowObj obj = (AlignmentDockingWindowObj)this.dockWindow;

			Model model = obj.getCurrentModel();
			
			GenomeReferenceObject seqObj = model.getReferenceSequenceObject();
			String location = seqObj.getChromosome() + ":" + Utilities.getNumberWithComma( seqObj.getStartPosition() ) + "-" + Utilities.getNumberWithComma( seqObj.getEndPosition() );
			
			this.txtLength.setText( seqObj.getSequence().size() + "bp" );
			this.txtLocation.setText( location );
			this.txtStrand.setText( Character.toString( seqObj.getStrand() ) );
		}
//		HairpinVO vo = MsbEngine.getInstance().getMiRBaseMap().get( item.getMiRBAseVersion() ).getMicroRnaHairpinByMirid2( mirid );
//		String location = vo.getChr() + ":" + Utilities.getNumberWidthComma( vo.getStart() ) + "-" + Utilities.getNumberWidthComma( vo.getEnd() );
	}
	
	public void moveUpstream() {
		if( remote.dockWindow instanceof AlignmentDockingWindowObj) {
			String mirid = remote.comboBox.getSelectedItem().toString();

			AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj)remote.dockWindow;

			GenomeReferenceObject reference = dwo.getCurrentModel().getReferenceSequenceObject();
			if( reference.getStrand() == '+')
				dwo.getCurrentModel().reInitReferenceGenomeSequenceByShift( 10, JMsbSysConst.SHIFT_TO_UPSTREAM );
			else
				dwo.getCurrentModel().reInitReferenceGenomeSequenceByShift( 10, JMsbSysConst.SHIFT_TO_DOWNSTREAM );
			
			dwo.setMirid( mirid );
		}
	}
	
	public void moveDownstream() {
		if( remote.dockWindow instanceof AlignmentDockingWindowObj) {
			String mirid = remote.comboBox.getSelectedItem().toString();

			AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj)remote.dockWindow;

			GenomeReferenceObject reference = dwo.getCurrentModel().getReferenceSequenceObject();
			if( reference.getStrand() == '+')
				dwo.getCurrentModel().reInitReferenceGenomeSequenceByShift( 10, JMsbSysConst.SHIFT_TO_DOWNSTREAM );
			else
				dwo.getCurrentModel().reInitReferenceGenomeSequenceByShift( 10, JMsbSysConst.SHIFT_TO_UPSTREAM );
			
			dwo.setMirid( mirid );
		}
	}
	
//	public boolean isOnTooltip() {
//		return this.chckbxReadTooltipOnOff.isSelected();
//	}

	@Override
	public void update(Observable o, Object arg) {
		if( arg instanceof Model ) {
			// TODO Auto-generated method stub
			Model model = (Model)arg;

			boolean isChangedMicroRna = false;

			String mirid = model.getMirnaInfo().getMirid();
			
			if( !this.comboBox.getSelectedItem().toString().equals( mirid ) )	isChangedMicroRna = true;

			this.comboBox.removeItemListener( this.itemAction );
			if( this.comboBox.getItemCount() > 0 )	this.comboBox.removeAllItems();

			this.initTextFields( mirid );
			this.initComboBox( model.getProjectMapItem().getProjectName(), mirid );
			this.comboBox.addItemListener( this.itemAction );

//			// update slider position
//			if( isChangedMicroRna )	this.sliderBar.init();
		}
	}
}
