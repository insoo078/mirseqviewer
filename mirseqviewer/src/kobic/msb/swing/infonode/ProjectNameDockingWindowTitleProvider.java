package kobic.msb.swing.infonode;

import java.io.Serializable;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.title.DockingWindowTitleProvider;

public class ProjectNameDockingWindowTitleProvider implements DockingWindowTitleProvider, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String projectName;

	public ProjectNameDockingWindowTitleProvider(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public String getTitle(DockingWindow arg0) {
		// TODO Auto-generated method stub
		return this.projectName;
	}
}