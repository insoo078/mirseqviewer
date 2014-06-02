package kobic.msb.swing.frame.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JColorChooseDialog extends JDialog implements ChangeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private JColorChooser tcc;
	private JPanel buttonPanel;
	
	private JButton okButton;
	private JButton cancelButton;
	
	private Color	choosedColor;
	
	private JColorChooseDialog remote = JColorChooseDialog.this;

	public JColorChooseDialog( JPanel panel ) {
		super(null, "Color Chooser", Dialog.ModalityType.APPLICATION_MODAL);

		this.setResizable(false);
		this.panel = panel;
		this.tcc = new JColorChooser(panel.getForeground());
        this.tcc.getSelectionModel().addChangeListener(this);
        
        getContentPane().add(tcc, BorderLayout.CENTER);
        this.setBounds(10, 10, 500, 300);
        
        this.buttonPanel = new JPanel();
        this.okButton = new JButton("Ok");
        this.cancelButton = new JButton("Cancel");
        this.buttonPanel.add( this.cancelButton );
        this.buttonPanel.add( this.okButton );
        
        this.okButton.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
		        remote.panel.setBackground( remote.choosedColor );
		        remote.panel.repaint();
		        
		        remote.dispose();
			}
        });
        
        this.cancelButton.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				remote.dispose();
			}
        });
        
        getContentPane().add( this.buttonPanel, BorderLayout.PAGE_END );
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		this.choosedColor = tcc.getColor();
	}
}
