package kobic.msb.swing.control.ui;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class BasicJMagnifierUI extends JMagnifierUI{
	public static ComponentUI createUI(JComponent c) {
		return new BasicJMagnifierUI();
	}
}
