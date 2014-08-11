package kobic.msb.swing.panel.newproject;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.swing.frame.dialog.JCommonNewProjectDialog;

public abstract class JMsvGroupControlPanel extends JMsbSampleTableCommonPanel implements UpdateCurrentStateInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String					choosedGroupNameToEdit;
	
	/*** Over JDK 1.7 ***/
//	private JComboBox<String>		cmbMngGroup;
//	private JComboBox<String>		cmbGroupSelect;
	/*** Under JDK 1.7 ***/
	@SuppressWarnings("rawtypes")
	private JComboBox				cmbMngGroup;
	
	private Msb						msb;
	
	
	private JMsvGroupControlPanel		remote = JMsvGroupControlPanel.this;

	public JMsvGroupControlPanel(JCommonNewProjectDialog dialog) {
		this( dialog, null );
	}

	public JMsvGroupControlPanel(JCommonNewProjectDialog dialog, int[] columns) {
		super(dialog, columns);
		// TODO Auto-generated constructor stub
		
		this.msb			= new Msb();
		
		this.cmbMngGroup = new JComboBox();
		this.cmbMngGroup.setToolTipText("Group ID");
		this.cmbMngGroup.setEditable(true);
		
		this.getCmbMngGroup().addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange() == ItemEvent.SELECTED ) {
					remote.setChoosedGroupNameToEdit( e.getItem().toString() );
				}
			}
		});
	}
	
	public String getChoosedGroupNameToEdit() {
		return choosedGroupNameToEdit;
	}
	public void setChoosedGroupNameToEdit(String choosedGroupNameToEdit) {
		this.choosedGroupNameToEdit = choosedGroupNameToEdit;
	}
	
	public void initGroupInfo() {
		this.cmbMngGroup.setSelectedItem("");
	}

	public JComboBox getCmbMngGroup(){
		return this.cmbMngGroup;
	}
	
	public Msb getMsb() {
		return this.msb;
	}
}
