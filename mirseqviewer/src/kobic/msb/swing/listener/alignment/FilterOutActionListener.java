package kobic.msb.swing.listener.alignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import kobic.msb.server.model.Model;
import kobic.msb.server.model.MsbFilterModel;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;

public class FilterOutActionListener implements ActionListener{
	private String filter;
	private String operator;
	private String query;
	private AbstractDockingWindowObj dockWindow;
	
	public FilterOutActionListener( AbstractDockingWindowObj dockWindowObj, String filter, String operator, String query ) {
		this.filter = filter;
		this.operator = operator;
		this.query = query;
		this.dockWindow = dockWindowObj;
	}

	public void actionPerformed(ActionEvent e) {
		if( dockWindow instanceof AlignmentDockingWindowObj ) {
			AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj) dockWindow;
		
//			Model model = dwo.getModel();

			Model model = dwo.getCurrentModel();
			MsbFilterModel filterModel = model.getProjectMapItem().getMsbFilterModel();
			filterModel.clear();
			filterModel.addModel(0, MsbFilterModel.AND, this.filter, this.operator, this.query );
//			model.filter( filter, operator, query );

			dwo.setMirid( dwo.getDefaultMirid() );
			
			dwo.setIsMousePositionFixed( false );
			dwo.getSsPanel().setMouseClicked( false );
		}
	}
}
