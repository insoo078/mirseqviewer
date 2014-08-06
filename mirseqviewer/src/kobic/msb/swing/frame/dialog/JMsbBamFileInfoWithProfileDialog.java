package kobic.msb.swing.frame.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTabbedPane;


import kobic.msb.server.model.ClusterModel;
import kobic.msb.swing.panel.profiling.JMsbDeMirPanel;
import kobic.msb.swing.panel.profiling.JMsbProfileDialogFileInfoPanel;
import kobic.msb.swing.panel.profiling.JMsbProfilePanel;
import kobic.msb.system.catalog.ProjectMapItem;

public class JMsbBamFileInfoWithProfileDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	
	private JMsbProfileDialogFileInfoPanel	fileInfoTitledPanel;
	private JMsbProfilePanel	profilePanel;
	private JTabbedPane			tabbedPane;
	
	private ClusterModel		clusterModel;
	private ProjectMapItem		projectItem;

	/**
	 * Create the dialog.
	 */
	public JMsbBamFileInfoWithProfileDialog( JFrame frame, String title, ClusterModel model, ProjectMapItem projectItem ) throws Exception{
		super( frame, title, Dialog.ModalityType.APPLICATION_MODAL );

		this.setBounds(100, 100, 1024, 768);
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.getContentPane().add(this.contentPanel, BorderLayout.CENTER);

		this.clusterModel = model;
		this.projectItem = projectItem;

		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		this.fileInfoTitledPanel	= new JMsbProfileDialogFileInfoPanel( this.clusterModel, this.projectItem );
		this.profilePanel			= new JMsbProfilePanel( this.clusterModel, this.projectItem );

		this.tabbedPane.addTab( "Sample info", this.fileInfoTitledPanel );
		this.tabbedPane.addTab( "miRNA info & Expression Profile", this.profilePanel );
		this.tabbedPane.addTab( "Differential expression of miRNAs", new JMsbDeMirPanel( this.clusterModel, this.projectItem ) );
		this.tabbedPane.addTab( "Clustering", new JPanel() );
		this.tabbedPane.addTab( "Visualize", new JPanel() );

		GroupLayout gl_contentPanel = new GroupLayout( this.contentPanel );
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 1002, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
					.addContainerGap())
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
