package kobic.msb.swing.control;

import javax.swing.JComponent;
import javax.swing.UIManager;

import kobic.msb.swing.control.ui.BasicJMagnifierUI;
import kobic.msb.swing.control.ui.JMagnifierUI;

public class JMagnifier extends JComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String uiClassID = "JMangifierUI";
	
	protected JMagnifierRangeModel model;
	
	public void setUI(JMagnifierUI ui) {
		super.setUI(ui);
	}
	
	public void updateUI() {
		if (UIManager.get(getUIClassID()) != null) {
			setUI((JMagnifierUI) UIManager.getUI(this));
		} else {
			setUI(new BasicJMagnifierUI());
		}
	}
	
	public JMagnifierUI getUI() {
		return (JMagnifierUI) ui;
	}
	
	public String getUIClassID() {
		return uiClassID;
	}

	public JMagnifierRangeModel getModel() {
		return this.model;
	}

	public JMagnifierRangeModel.Value getValue() {
		return this.model.getValue();
	}

	public void setValue(JMagnifierRangeModel.Value value) {
		JMagnifierRangeModel m = getModel();
		JMagnifierRangeModel.Value oldValue = m.getValue();
		if (value.equals(oldValue)) {
			return;
		}
		m.setValue(value);
	}
}
