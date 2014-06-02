package kobic.msb.swing.panel.profile.obj;

import java.awt.Graphics2D;

import kobic.msb.system.config.ProjectConfiguration;

public abstract class ProfileItem {
	private ProjectConfiguration configuration;
	private Object label;
	private int row;
	private int col;
	
	public ProfileItem(Object label, int row, int col, ProjectConfiguration configuration) {
		this.label = label;
		this.row = row;
		this.col = col;
		this.configuration = configuration;
	}

	public abstract void draw(Graphics2D g2);

	public ProjectConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ProjectConfiguration configuration) {
		this.configuration = configuration;
	}

	public Object getLabel() {
		return label;
	}

	public void setLabel(Object label) {
		this.label = label;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
	
	
}
