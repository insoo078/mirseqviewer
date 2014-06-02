package kobic.msb.swing.component;

import javax.swing.table.DefaultTableModel;

import kobic.msb.server.model.Model;

public class SortableTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Model model;
	
	public SortableTableModel( Model model ) {
		this.model = model;
	}
	
	public Model getModel() {
		return this.model;
	}
}
