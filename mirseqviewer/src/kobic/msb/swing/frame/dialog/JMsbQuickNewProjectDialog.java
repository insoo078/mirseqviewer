package kobic.msb.swing.frame.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import java.awt.Color;

import javax.swing.JLabel;

import kobic.msb.common.ImageConstant;
import kobic.msb.swing.listener.projectdialog.quick.CreateQuickNewProjectActionListener;
import kobic.msb.swing.panel.newproject.JMsbMatureChoosePanel;
import kobic.msb.swing.panel.newproject.quick.JMsbQuickNewProjectPanel;
import kobic.msb.system.engine.MsbEngine;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ThreadPoolExecutor;

public class JMsbQuickNewProjectDialog extends JCommonNewProjectDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel				contentPanel = new JPanel();
	private JMsbQuickNewProjectPanel	projectPanel;

	private JMsbQuickNewProjectDialog remote = JMsbQuickNewProjectDialog.this;

	/**
	 * Create the dialog.
	 */
	public JMsbQuickNewProjectDialog( Frame owner, String title, boolean isEdit, Dialog.ModalityType modalType ) throws Exception{
		super( owner, title, isEdit, modalType, "OK", false );

		this.setResizable(false);

		setBounds(100, 100, 525, 504);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 0, 525, 85);
		contentPanel.add(panel);
		panel.setLayout(null);

		JLabel lblNewProjectLogo = new JLabel( ImageConstant.new_project_img );
		lblNewProjectLogo.setLocation(338, 6);
		lblNewProjectLogo.setSize(169, 67);

		panel.add(lblNewProjectLogo);
		{
			JLabel lblCreateANew = new JLabel("New miRseq project");
			lblCreateANew.setFont(new Font("Lucida Grande", Font.BOLD, 13));
			lblCreateANew.setBounds(16, 16, 213, 16);
			panel.add(lblCreateANew);
		}
		{
			JLabel lblCreateANew_1 = new JLabel("Create a new miRseq project");
			lblCreateANew_1.setBounds(26, 38, 232, 16);
			panel.add(lblCreateANew_1);
		}
		{
			projectPanel = new JMsbQuickNewProjectPanel( this );
			projectPanel.setBounds(0, 85, 525, 358);
			contentPanel.add(projectPanel);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = this.getNextButton();
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");

				okButton.addActionListener( new CreateQuickNewProjectActionListener(this.projectPanel) );
				
				buttonPane.add( okButton );
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener( new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						if( MsbEngine.getExecutorService() instanceof java.util.concurrent.ThreadPoolExecutor ) {
							java.util.concurrent.ThreadPoolExecutor tpe = (ThreadPoolExecutor) MsbEngine.getExecutorService();
							
							if( tpe.getActiveCount() > 0 ) {
								int result = JOptionPane.showConfirmDialog( remote, "Are you really want to stop the background process?", "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
								if( result == JOptionPane.OK_OPTION ) {
									MsbEngine.getExecutorService().shutdownNow();
								}
							}else {
								remote.dispose();
							}
						}
					}
				});
				buttonPane.add(cancelButton);
			}
		}
	}

	@Override
	public JTextField getTxtProjectName() {
		// TODO Auto-generated method stub
		return this.projectPanel.getTextProjectName();
	}
	@Override
	public JTextArea getTextScrollPane() {
		return this.projectPanel.getScrollTextPane();
	}

	public void setIndeterminate(boolean value) {
		this.projectPanel.setIndeterminate( value );
	}
	
	public void setProgressBarRange(final int a, final int b) {
		this.projectPanel.setProgressBarRange(a, b);
	}
	
	public int getProgressToGetMiRnas() {
		return this.projectPanel.getProgressToGetMiRnas();
	}

	@Override
	public void setProgressToGetMiRnas(int value) {
		// TODO Auto-generated method stub	
		this.projectPanel.setProgressToGetMiRnas(value);
	}

	@Override
	public void setFocusProjectName() {
		// TODO Auto-generated method stub
		this.projectPanel.setFocusProjectName();
	}

	@Override
	public boolean isIndeterminate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JMsbMatureChoosePanel getMirnaChoosePanel() {
		// TODO Auto-generated method stub
		return null;
	}
}
