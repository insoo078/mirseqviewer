package kobic.msb.swing.frame.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import kobic.msb.swing.thread.caller.AbstractImExportCaller;

public class JMsbProgressDialog extends JDialog implements PropertyChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JProgressBar progressBar;

	private final JPanel contentPanel = new JPanel();
	private JLabel lblLabel;

	private AbstractImExportCaller sw;
	
	private JMsbProgressDialog remote = JMsbProgressDialog.this;

	/**
	 * Create the dialog.
	 */
	public JMsbProgressDialog(Frame owner, String title, Dialog.ModalityType modalType, String label){
		super(owner, title, modalType);
//		setUndecorated(true);
		setResizable(false);

		setBounds(100, 100, 450, 93);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		this.progressBar = new JProgressBar(0, 100);
		this.progressBar.setStringPainted(true);
		
		this.lblLabel = new JLabel( label );
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(progressBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
						.addComponent(lblLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		this.addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				if( remote.sw != null ) {
					if( remote.sw.isDone() ) {
						remote.dispose();
					}else {
						JOptionPane.showMessageDialog( remote, "This work is not done yet!!", "Message", JOptionPane.INFORMATION_MESSAGE );
					}
				}
			}
		});
		contentPanel.setLayout(gl_contentPanel);
	}
	
	public void setMinProgress(int min){
		this.progressBar.setMinimum(min);
	}
	
	public void setMaxProgress(int max){
		this.progressBar.setMaximum(max);
	}
	
	public int getMaxProgress() {
		return this.progressBar.getMaximum();
	}
	
	public int getMinProgress() {
		return this.progressBar.getMinimum();
	}
	
	public void setValue(int n) {
		this.progressBar.setValue(n);
	}
	
	public void setIndeterminate(boolean value) {
		this.progressBar.setIndeterminate( value );
	}
	
	public boolean isIndeterminate() {
		return this.progressBar.isIndeterminate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		} 
	}
	
	public void setProgressValue(int value) {
		this.progressBar.setValue( value );
	}
	
	public void setLabelValue( String value ) {
		this.lblLabel.setText( value );
	}
	
	public JProgressBar getProgressBar() {
		return this.progressBar;
	}
	
	public void attachThread(AbstractImExportCaller sw) {
		this.sw = sw;
	}
}
