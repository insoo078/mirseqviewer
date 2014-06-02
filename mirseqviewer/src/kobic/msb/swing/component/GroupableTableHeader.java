package kobic.msb.swing.component;


import java.util.Enumeration;
import java.util.Vector;

import javax.swing.table.*;

 

/**
  * GroupableTableHeader
  *
  * @version 1.0 10/20/98
  * @author Nobuo Tamemasa
  */

public class GroupableTableHeader extends JTableHeader {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Vector<Object> columnGroups = null;

	public GroupableTableHeader( TableColumnModel model ) {
		super( model );
		this.setUI( new GroupableTableHeaderUI() );
		this.setReorderingAllowed( false );
	}

	@Override
	public void updateUI(){
		this.setUI( new GroupableTableHeaderUI() );
	}
  
	@Override
	public void setReorderingAllowed( boolean b ) {
		this.reorderingAllowed = false;
	}

	public void addColumnGroup( ColumnGroup g ) {
		if (this.columnGroups == null) {
			this.columnGroups = new Vector<Object>();
		}
		this.columnGroups.addElement( g );
	}
	
	public Vector<Object> getColumnGroup() {
		return this.columnGroups;
	}

	public Enumeration<Object> getColumnGroups( TableColumn col ) {
		if (this.columnGroups == null) return null;

		Enumeration<Object> e = this.columnGroups.elements();
		while (e.hasMoreElements()) {
			ColumnGroup cGroup = (ColumnGroup)e.nextElement();
			Vector<Object> v_ret = cGroup.getColumnGroups( col, new Vector<Object>()) ;
			if (v_ret != null) { 
				return v_ret.elements();
			}
		}
		return null;
	}

	public void setColumnMargin() {
		if (this.columnGroups == null) return;

		int columnMargin = getColumnModel().getColumnMargin();
		Enumeration<Object> e = this.columnGroups.elements();
		while (e.hasMoreElements()) {
			ColumnGroup cGroup = (ColumnGroup)e.nextElement();
			cGroup.setColumnMargin(columnMargin);
		}
	}
}