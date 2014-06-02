package kobic.msb.swing.panel.profile;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import kobic.msb.swing.canvas.ExpressionProfileDockingWindowObj;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class JExpressionProfileControlPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ExpressionProfileDockingWindowObj profileDockWindowObj;
	
	private JExpressionProfileControlPanel remote = JExpressionProfileControlPanel.this;
	/**
	 * Create the panel.
	 */
	public JExpressionProfileControlPanel(ExpressionProfileDockingWindowObj profileDockWindowObj, final ProjectMapItem projectItem) {
		this.profileDockWindowObj = profileDockWindowObj;

		JButton btnZommIn = new JButton( "+" );
		btnZommIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int unit = (int)(projectItem.getProjectConfiguration().getExpressionProfileBlockHeight() + 1);
				projectItem.getProjectConfiguration().setExpressionProfileBlockHeight( unit );
				projectItem.getProjectConfiguration().setExpressionProfileBlockWidth(  unit );
				projectItem.getProjectConfiguration().writeConfigFile();

				remote.profileDockWindowObj.update();
			}
		});
		
		JButton btnZoomOut = new JButton( "-" );
		btnZoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int unit = (int)(projectItem.getProjectConfiguration().getExpressionProfileBlockHeight() - 1);
				projectItem.getProjectConfiguration().setExpressionProfileBlockHeight( unit );
				projectItem.getProjectConfiguration().setExpressionProfileBlockWidth(  unit );
				projectItem.getProjectConfiguration().writeConfigFile();

				remote.profileDockWindowObj.update();
			}
		});
		
		JButton btnClustering = new JButton("Hierachical Clustering");
		btnClustering.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					projectItem.clusteringExpressionProfile();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					MsbEngine.logger.error(e1);
//					e1.printStackTrace();
				}
				
				remote.profileDockWindowObj.update();
			}
		});

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnZommIn, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnZoomOut, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
					.addGap(43)
					.addComponent(btnClustering)
					.addContainerGap(88, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnZommIn)
						.addComponent(btnZoomOut)
						.addComponent(btnClustering))
					.addContainerGap(61, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
}
