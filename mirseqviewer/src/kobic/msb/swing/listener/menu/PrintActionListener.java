package kobic.msb.swing.listener.menu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.frame.JMsbBrowserMainFrame;
import kobic.msb.swing.listener.projecttreepanel.ProjectActionListener;

public class PrintActionListener extends ProjectActionListener{
	public PrintActionListener(JMsbBrowserMainFrame frame) {
		super(frame);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String projectName = this.getFrame().getTabWindow().getSelectedWindow().getTitle();

		AbstractDockingWindowObj obj = this.getFrame().getAbstractDockingWindowObj( projectName );
		if( obj != null ) {
			if( obj instanceof AlignmentDockingWindowObj ) {
				AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj) obj;
		
				dwo.print();
			}
		}else {
			JOptionPane.showMessageDialog( this.getFrame(), "<html>There are no executed projects<BR>If you want to print a project, you should execute a project</html>", "Confirm", JOptionPane.INFORMATION_MESSAGE );
		}
		
//		if( projectName != null && this.getFrame().getDockingWindowObjMap().containsKey( projectName ) ) {
//			AbstractDockingWindowObj obj = this.getFrame().getDockingWindowObjMap().get( projectName );
//
//			if( obj instanceof AlignmentDockingWindowObj ) {
//				AlignmentDockingWindowObj dwo = (AlignmentDockingWindowObj) obj;
//		
//				dwo.print();
//			}
//		}
	}
}
