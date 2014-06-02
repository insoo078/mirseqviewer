package kobic.msb.swing.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.MutableTreeNode;

import kobic.msb.common.ImageConstant;

public class JMsbProjectTreeCellRenderer extends DefaultTreeCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public JMsbProjectTreeCellRenderer() {

	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );
		
		// Defer to superclass to create initial version of JLabel and then modify (below).
		JLabel ret = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		// We know that value is a DefaultMutableTreeNode so this downcast is safe.
		MutableTreeNode node = (MutableTreeNode) value;

		if( ((DefaultMutableTreeNode) node).getUserObject() != null ) {
			String strLabel = ((DefaultMutableTreeNode) node).getUserObject().toString();

			DefaultMutableTreeNode parentNode = ((DefaultMutableTreeNode)node.getParent());
			if( parentNode != null && parentNode.getUserObject().toString().equals("Project") ) {
				ret.setIcon( ImageConstant.projectIcon2 );
			}else if( parentNode != null && parentNode.getUserObject().toString().equals("Groups") ) {
				ret.setIcon( ImageConstant.groupIcon );
			}

    	    // Inspect user object and change rendering based on this.
			if( strLabel.equals("Project") ) {
				ret.setIcon( ImageConstant.msb_icon );
			}else if( strLabel.startsWith("Status") && !strLabel.contains("Done") ) {
				ret.setIcon( ImageConstant.sand_clock );
				ret.setText( this.getBoldText( strLabel ) );
			}else if( strLabel.startsWith("Status") && strLabel.contains("Done") ) {
				ret.setIcon( ImageConstant.done );
				ret.setText( this.getBoldText( strLabel ) );
			}else if( ((DefaultMutableTreeNode) node.getParent()).getUserObject().toString().equals("miRna(s)") ) {
				ret.setIcon( ImageConstant.dnaIcon );
			}else if( strLabel.startsWith("Name :") ) {
				ret.setIcon( ImageConstant.organismIcon );
				ret.setText( this.getBoldText( strLabel ) );
			}else if( strLabel.startsWith("File") ) {
				ret.setIcon( ImageConstant.fileIcon );
				ret.setText( this.getBoldText( strLabel ) );
			}else if( strLabel.startsWith("Index :") ) {
				ret.setIcon( ImageConstant.sortAscIcon );
				ret.setText( this.getBoldText( strLabel ) );
			}else if( strLabel.equals("Summary") ) {
				ret.setIcon( ImageConstant.reportIcon );
			}else if( strLabel.startsWith("Sample :") ) {
				ret.setIcon( ImageConstant.sampleIcon );

				String[] divs = strLabel.split("\\:");
				String str = "<html>";
				for(int i=0; i<divs.length; i++) {
					if( i==1 )		str += (" : <b>" + divs[i] + "</b>");
					else if( i==2 )	str += (": <font color='#ad812b'>" + divs[i] + "</font>");
					else		str += divs[i];
				}
				str += "</html>";
				ret.setText( str );
			}else if( strLabel.startsWith("Location :") ) {
				String[] divs = strLabel.split("\\:");
				String str = "<html>";
				for(int i=0; i<divs.length; i++) {
					if( i > 0 && i < divs.length-1 )			str += ("<b>" + divs[i] + ":</b>");
					else if(i > 0 && i == divs.length-1 )		str += ("<b>" + divs[i] + "</b>");
					else										str += divs[i] + " : ";
				}
				str += "</html>";
				ret.setText( str );
			}else {
				ret.setText( strLabel );
			}
		}

	    // Could also inspect whether node is a leaf node, etc.
		return ret;
	}
	
	public String getBoldText( String strLabel ) {
		String[] divs = strLabel.split("\\:");
		String str = "<html>";
		for(int i=0; i<divs.length; i++) {
			if( i==1 )	str += (" : <b>" + divs[i] + "</b>");
			else		str += divs[i];
		}
		str += "</html>";
		return str;
	}
}