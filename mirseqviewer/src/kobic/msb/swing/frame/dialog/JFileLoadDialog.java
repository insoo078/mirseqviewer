package kobic.msb.swing.frame.dialog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import kobic.msb.common.SwingConst;

import javax.swing.JLabel;

public class JFileLoadDialog extends JDialog implements PropertyChangeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel panel;
	private JProgressBar fileProgressBar;
	private JProgressBar contentProgressBar;
	private JLabel lblFile;
	private JLabel lblContent;

	public JFileLoadDialog() {
		setResizable(false);
		getContentPane().setLayout(null);
		
		TitledBorder groupsTitled = BorderFactory.createTitledBorder( SwingConst.BORDER_LOWERED_ETCHED, "" );
		this.panel = new JPanel();
		panel.setBounds(6, 6, 438, 79);
		panel.setBorder( groupsTitled );
		getContentPane().add(panel);
		panel.setLayout(null);
		
		this.fileProgressBar = new JProgressBar();
		fileProgressBar.setBounds(103, 15, 312, 20);
		panel.add(fileProgressBar);
		
		this.contentProgressBar = new JProgressBar();
		contentProgressBar.setBounds(103, 41, 312, 20);
		panel.add(contentProgressBar);
		
		this.lblFile = new JLabel("(1/1)");
		lblFile.setBounds(23, 17, 61, 16);
		panel.add(lblFile);
		
		this.lblContent = new JLabel("(1/1)");
		lblContent.setBounds(23, 43, 61, 16);
		panel.add(lblContent);
	}
	
	public void setFileProgressBar( String msg, int min, int max, int value ) {
		this.fileProgressBar.setMinimum(min);
		this.fileProgressBar.setMaximum(max);
		this.fileProgressBar.setValue(value);

		if(null != msg) {
			this.fileProgressBar.setString(msg);
		} else {
			this.fileProgressBar.setString("");
		}
	}

	public void setContentProgressBar( String msg, int min, int max, int value ) {
		this.contentProgressBar.setMinimum(min);
		this.contentProgressBar.setMaximum(max);
		this.contentProgressBar.setValue(value);

		if(null != msg) {
			this.contentProgressBar.setString(msg);
		} else {
			this.contentProgressBar.setString("");
		}
	}
	
	public void setFileLabel( String text ) {
		this.lblContent.setText(text);
	}
	
	public void setContentLabel( String text ) {
		this.lblContent.setText(text);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            this.fileProgressBar.setValue( progress );
        }
	}
}