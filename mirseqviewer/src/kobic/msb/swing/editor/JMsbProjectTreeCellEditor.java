package kobic.msb.swing.editor;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

public class JMsbProjectTreeCellEditor extends DefaultTreeCellEditor{
	public JMsbProjectTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
	    super(tree, renderer);
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
	    return renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
	}

	public boolean isCellEditable(EventObject anEvent) {
	    return true; // Or make this conditional depending on the node
	}
}
