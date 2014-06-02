package kobic.msb.swing.component;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.MSBReadCountTableColumnStructureModel;
import kobic.msb.server.model.MsbRCTColumnModel;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.frame.dialog.JMsbReadCountTableConfigDialog;
import kobic.msb.system.engine.MsbEngine;

public class TreeTransferHandler extends TransferHandler {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DataFlavor nodesFlavor;
    private DataFlavor[] flavors = new DataFlavor[1];
    private DefaultMutableTreeNode[] nodesToRemove;
    private MSBReadCountTableColumnStructureModel columnModel;
    private JMsbReadCountTableConfigDialog dialog;

    public TreeTransferHandler( JMsbReadCountTableConfigDialog dialog ) {
    	this.dialog = dialog;

    	AbstractDockingWindowObj dwo = this.dialog.getDockingWindowObj();
    	if( dwo instanceof AlignmentDockingWindowObj ) {
			AlignmentDockingWindowObj dwo2 = (AlignmentDockingWindowObj) dwo;
			this.columnModel = dwo2.getCurrentModel().getMSBReadCountTableColumnStructureModel();
    	}

        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + javax.swing.tree.DefaultMutableTreeNode[].class.getName() + "\"";
            this.nodesFlavor = new DataFlavor(mimeType);
            this.flavors[0] = this.nodesFlavor;
        } catch(ClassNotFoundException e) {
            System.out.println("ClassNotFound: " + e.getMessage());
        }
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
    	if(!support.isDrop()) {
    		return false;
    	}
    	support.setShowDropLocation(true);
    	if(!support.isDataFlavorSupported(nodesFlavor)) {
    		return false;
    	}
		// Do not allow a drop on the drag source selections.
		JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
		JTree tree = (JTree)support.getComponent();
		int dropRow = tree.getRowForPath(dl.getPath());
		int[] selRows = tree.getSelectionRows();
		for(int i = 0; i < selRows.length; i++) {
			if(selRows[i] == dropRow) {
				return false;
			}
		}

		return true;
    }

    /**
	* Copy node with all childrens
	*
	* @param node - node to copy
	* @return copy of node
	*/
	private DefaultMutableTreeNode copyEntireNode(DefaultMutableTreeNode node) {
		DefaultMutableTreeNode copy = new DefaultMutableTreeNode( node );

		if (node.getChildCount() > 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) e.nextElement();
				DefaultMutableTreeNode copyChild = copyEntireNode(child);
				copy.add(copyChild);
			}
		}
		return copy;
	}

    protected Transferable createTransferable(JComponent c) {
    	JTree tree = (JTree)c;
    	TreePath[] paths = tree.getSelectionPaths();
    	if(paths != null) {
			// Make up a node array of copies for transfer and
			// another for/of the nodes that will be removed in
			// exportDone after a successful drop.
			List<DefaultMutableTreeNode> copies = new ArrayList<DefaultMutableTreeNode>();
			List<DefaultMutableTreeNode> toRemove = new ArrayList<DefaultMutableTreeNode>();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[0].getLastPathComponent();
			DefaultMutableTreeNode copy = copyEntireNode(node);
			copies.add(copy);
			toRemove.add(node);
			for(int i = 1; i < paths.length; i++) {
				DefaultMutableTreeNode next = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
				// Do not allow higher level nodes to be added to list.
				if(next.getLevel() < node.getLevel()) {
					break;
				} else if(next.getLevel() > node.getLevel()) {  // child node
					copy.add(copy(next));
					// node already contains child
				} else {                                        // sibling
					copies.add(copy(next));
					toRemove.add(next);
				}
			}
			DefaultMutableTreeNode[] nodes = copies.toArray(new DefaultMutableTreeNode[copies.size()]);
			this.nodesToRemove = toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
			return new NodesTransferable(nodes);
    	}
    	return null;
    }

    /** Defensive copy used in createTransferable. */
    private DefaultMutableTreeNode copy(TreeNode node) {
    	return new DefaultMutableTreeNode(node);
    }

    protected void exportDone(JComponent source, Transferable data, int action) {
    	if((action & MOVE) == MOVE) {
    		JTree tree = (JTree)source;
    		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
    		// Remove nodes saved in nodesToRemove in createTransferable.
    		for(int i = 0; i < nodesToRemove.length; i++) {
    			model.removeNodeFromParent(nodesToRemove[i]);
    		}

    		try {
	    		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
	    		for(int i=0; i<root.getChildCount(); i++) {
	    			DefaultMutableTreeNode node = (DefaultMutableTreeNode)root.getChildAt( i );
	    			
	    			String key = node.getUserObject().toString();
	    			MsbRCTColumnModel columnModel = this.columnModel.getColumnModel( key );
	    			if( columnModel.getColumnType().equals( JMsbSysConst.GROUP_HEADER_PREFIX ) && node.getChildCount() == 0 ) {
	    				this.dialog.setChangeNodeByDnD( node );
	    				
	    				for(int j=0; j<root.getChildCount(); j++) {
	    					DefaultMutableTreeNode node2 = (DefaultMutableTreeNode)root.getChildAt( j );
	    					if( node2.getUserObject().toString().equals( node.getUserObject().toString() + JMsbSysConst.SUM_SUFFIX) ) {
	    						this.dialog.setChangeNodeByDnD( node2 );
	    						break;
	    					}
	    				}
	    			}
	    		}
    		}catch(Exception e) {
    			MsbEngine.logger.error( "error : ", e );
    		}

    		SwingUtilities.expandTree( tree );
    	}
    }

    public int getSourceActions(JComponent c) {
    	return COPY_OR_MOVE;
    }

    public boolean importData(TransferHandler.TransferSupport support) {
    	if(!canImport(support)) {
    		return false;
    	}
    	// Extract transfer data.
    	DefaultMutableTreeNode[] nodes = null;
    	try {
    		Transferable t = support.getTransferable();
    		nodes = (DefaultMutableTreeNode[])t.getTransferData(nodesFlavor);
    	} catch(UnsupportedFlavorException ufe) {
    		System.out.println("UnsupportedFlavor: " + ufe.getMessage());
    	} catch(java.io.IOException ioe) {
    		System.out.println("I/O error: " + ioe.getMessage());
    	}
    	// Get drop location info.
    	JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
    	int childIndex = dl.getChildIndex();
    	TreePath dest = dl.getPath();
    	DefaultMutableTreeNode parent = (DefaultMutableTreeNode)dest.getLastPathComponent();
    	JTree tree = (JTree)support.getComponent();
    	DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
    	// Configure for drop mode.
    	int index = childIndex;    // DropMode.INSERT
    	if(childIndex == -1) {     // DropMode.ON
    		index = parent.getChildCount();
    	}

    	String key = nodes[0].getUserObject().toString();
    	if( this.columnModel.getChoosedGroupMap().get( key ) == null ) {
    		/**********************************************************
    		 * If user choice Sample column, 
    		 * sample column can only move to other group or in different position in the group
    		 */

    		try {
	    		MsbRCTColumnModel type = this.columnModel.getOriginalColumnModel( key );
	
	    		if( parent.getUserObject() != null ) {
	    			MsbRCTColumnModel group = this.columnModel.getChoosedGroupMap().get( parent.getUserObject().toString() );
	    			if( group != null && group.getColumnType().equals( JMsbSysConst.GROUP_HEADER_PREFIX ) ) {
	    				for(int i = 0; i < nodes.length; i++) {
	    		    		model.insertNodeInto(nodes[i], parent, index++);
	    		    	}
	    				return true;
	    			}
	    		}else if( type.getColumnType().equals( JMsbSysConst.GROUP_SUM_HEADER_PREFIX ) || type.getColumnType().equals( JMsbSysConst.TOTAL_SUM_HEADER_PREFIX ) ) {
	    			for(int i = 0; i < nodes.length; i++) {
			    		model.insertNodeInto( nodes[i], parent, index++ );
			    	}
					return true;
	    		}
    		}catch(Exception e) {
    			MsbEngine.logger.error( "error : ", e );
    		}
    		return false;
    	}else {
    		/**********************************************************
    		 * If user choice Total_sum, Group_sum or Group column, 
    		 * these columns are can moving to group folder or other position in root node
    		 */
    		if( parent.getUserObject() == null ) {
		    	// Add data to model.
		    	for(int i = 0; i < nodes.length; i++) {
		    		model.insertNodeInto(nodes[i], parent, index++);
		    	}
	
		    	return true;
    		}else {
    			MsbRCTColumnModel group = this.columnModel.getChoosedGroupMap().get( parent.getUserObject().toString() );
    			MsbRCTColumnModel source = this.columnModel.getChoosedGroupMap().get( key );

        		/**********************************************************
        		 * If user choice Total_sum, Group_sum or Group column,
        		 * these columns are can moving to group folder
        		 */
    			if( (source.getColumnType().equals( JMsbSysConst.TOTAL_SUM_HEADER_PREFIX ) || source.getColumnType().equals( JMsbSysConst.GROUP_SUM_HEADER_PREFIX )) && group.getColumnType().equals( JMsbSysConst.GROUP_HEADER_PREFIX ) ) {
    				
    		    	for(int i = 0; i < nodes.length; i++) {
    		    		model.insertNodeInto(nodes[i], parent, index++);
    		    	}
    	
    		    	return true;
    			}

        		return false;
    		}
    	}
    }

    public String toString() {
    	return getClass().getName();
    }

    public class NodesTransferable implements Transferable {
    	DefaultMutableTreeNode[] nodes;

    	public NodesTransferable(DefaultMutableTreeNode[] nodes) {
    		this.nodes = nodes;
    	}

    	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if(!isDataFlavorSupported(flavor))
            	throw new UnsupportedFlavorException(flavor);
            return nodes;
    	}

    	public DataFlavor[] getTransferDataFlavors() {
    		return flavors;
    	}

    	public boolean isDataFlavorSupported(DataFlavor flavor) {
    		return nodesFlavor.equals(flavor);
    	}
    }
}