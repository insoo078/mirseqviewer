package kobic.msb.swing.listener.key;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class NumericKeyListener implements KeyListener{
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {
		if ( !(Character.isDigit(e.getKeyChar()) || e.getKeyChar()=='.')) {
			Toolkit.getDefaultToolkit().beep();
			e.consume();
		}
	}
}