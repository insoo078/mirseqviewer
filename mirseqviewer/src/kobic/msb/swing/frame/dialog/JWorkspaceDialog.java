package kobic.msb.swing.frame.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextField;

import kobic.com.util.Utilities;
import kobic.msb.common.SwingConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.system.engine.MsbEngine;

public class JWorkspaceDialog extends JDialog implements SwingConst{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JPanel panel;
	private JTextField textField;

	private final int WORKSPACE_DIALOG_WIDTH = 450;
	private final int WORKSPACE_DIALOG_HEIGHT = 192;
	
	private MsbEngine engine;
	
	private JWorkspaceDialog remote = JWorkspaceDialog.this;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			JWorkspaceDialog dialog = new JWorkspaceDialog(null, "Test", null);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
//			e.printStackTrace();
			MsbEngine.logger.error( "error : ", e );
		}
	}
	
	public JWorkspaceDialog() {
		this( null, null, Dialog.ModalityType.APPLICATION_MODAL );
	}

	/**
	 * Create the dialog.
	 */
	public JWorkspaceDialog(Frame owner, String title, Dialog.ModalityType modalityType) {
		super(owner, title, modalityType);
		
		this.engine =  MsbEngine.getInstance();
		
		this.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );

		Point2D.Double point = SwingUtilities.getDrawingPosition( SwingUtilities.getScreenSize(), this.WORKSPACE_DIALOG_WIDTH, this.WORKSPACE_DIALOG_HEIGHT );
		this.setBounds( (int)point.getX(), (int)point.getY(), this.WORKSPACE_DIALOG_WIDTH, this.WORKSPACE_DIALOG_HEIGHT );
		
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(this.contentPanel, BorderLayout.CENTER);{
			this.panel = new JPanel();
			this.panel.setBackground(Color.WHITE);
		}
		JLabel lblWorkspace = new JLabel("Workspace :");
		lblWorkspace.setFont( SwingConst.DEFAULT_DIALOG_FONT );

		String value = this.engine.getSystemProperties().getProperty("msb.project.workspace");
		this.textField = new JTextField( value );
		this.textField.setColumns(10);

		final JButton btnChoose = new JButton("Browse...");
		btnChoose.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		btnChoose.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JWorkspaceDialog remote = JWorkspaceDialog.this;
				// TODO Auto-generated method stub
				//Handle open button action.
		        if (e.getSource() == btnChoose) {
		            int returnVal = fc.showOpenDialog(JWorkspaceDialog.this);
		 
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                String workspacePath = fc.getSelectedFile().getAbsolutePath();
		                remote.textField.setText( workspacePath );
		            }
		        //Handle save button action.
		        }
			}
		});

		this.addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				remote.checkWorkspace();
			}
		});
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblWorkspace)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(this.textField, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnChoose)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addComponent(this.panel, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(this.panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(this.textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnChoose)
						.addComponent(lblWorkspace))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		JLabel lblSelectWorkspace = new JLabel("Select a workspace");
		lblSelectWorkspace.setFont(SwingConst.DEFAULT_DIALOG_FONT_TITLE);
		
		JLabel lblNewLabel = new JLabel("miRseq Viewer stores your projects in a folder called a workspace");
		lblNewLabel.setFont(SwingConst.DEFAULT_DIALOG_FONT);
		
		JLabel lblNewLabel_1 = new JLabel("Choose a workspace folder to use for this session");
		lblNewLabel_1.setFont(SwingConst.DEFAULT_DIALOG_FONT);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblSelectWorkspace)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(6)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel_1)
								.addComponent(lblNewLabel))))
					.addContainerGap(316, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblSelectWorkspace)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel_1)
					.addContainerGap(13, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final JButton okButton = new JButton("OK");
				okButton.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				
				okButton.addActionListener( new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JWorkspaceDialog remote = JWorkspaceDialog.this;
						// TODO Auto-generated method stub
						//Handle open button action.
				        if (e.getSource() == okButton) {
				        	if( remote.textField.getText().length() > 0 ) {
				        		File nf = new File( remote.textField.getText() );
				        		if( nf.canWrite() ) {
						    		Map<String, String> map = new HashMap<String, String>();
						    		map.put("msb.project.workspace", remote.textField.getText() );
		
						    		remote.engine.reloadSystemPropertiesAfterUpdate( map );
	
						    		try {
							    		File file = new File( remote.textField.getText() );
							    		if( !file.exists() ) {
							    			file.mkdir();
							    		}
		
							    		remote.dispose();
						    		}catch(Exception exp) {
						    			MsbEngine.logger.error( "Error : ", exp );
						    			JOptionPane.showMessageDialog( remote, "We can not make workspace directory.", "Error", JOptionPane.ERROR_MESSAGE );
						    		}
				        		}else {
				        			JOptionPane.showMessageDialog( remote, "You have not got a write permission in this directory", "Warning", JOptionPane.WARNING_MESSAGE );
					        		return;
				        		}
				        	}else {
				        		JOptionPane.showMessageDialog( remote, "You have to set the project workspace", "Warning", JOptionPane.WARNING_MESSAGE );
				        		return;
				        	}
				        }
					}
					
				});
			}
			{
				final JButton cancelButton = new JButton("Cancel");
				cancelButton.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				
				cancelButton.addActionListener( new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JWorkspaceDialog remote = JWorkspaceDialog.this;
						// TODO Auto-generated method stub
						//Handle open button action.
				        if (e.getSource() == cancelButton) {
				    		remote.checkWorkspace();
				        }
					}
					
				});
			}
		}
	}
	
	public void checkWorkspace() {
		String workspace = Utilities.nulltoEmpty( remote.engine.getSystemProperties().getProperty("msb.project.workspace") );
		if( workspace.isEmpty() ) {
			JOptionPane.showMessageDialog( remote, "The workspace did not specified", "Message", JOptionPane.INFORMATION_MESSAGE );
			return;
		}
		remote.dispose();
	}
}
