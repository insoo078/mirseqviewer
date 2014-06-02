package kobic.msb.swing.component;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;

public class JScrollPopupMenu extends JPopupMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int maximumVisibleRows = 10;

	public JScrollPopupMenu() {
		this(null);
	}

	public JScrollPopupMenu( String label ) {
		super(label);
		this.setLayout(new ScrollPopupMenuLayout());

		super.add( this.getScrollBar() );
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override 
			public void mouseWheelMoved(MouseWheelEvent event) {
				JScrollBar scrollBar = getScrollBar();
				int amount = (event.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL)
						? event.getUnitsToScroll() * scrollBar.getUnitIncrement()
								: (event.getWheelRotation() < 0 ? -1 : 1) * scrollBar.getBlockIncrement();

						scrollBar.setValue(scrollBar.getValue() + amount);
						event.consume();
			}
		});
	}

	private JScrollBar popupScrollBar;
	protected JScrollBar getScrollBar() {
		if(this.popupScrollBar == null) {
			this.popupScrollBar = new JScrollBar(Adjustable.VERTICAL);
			this.popupScrollBar.addAdjustmentListener(new AdjustmentListener() {
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e) {
					doLayout();
					repaint();
				}
			});

			this.popupScrollBar.setVisible(false);
		}

		return this.popupScrollBar;
	}

	public int getMaximumVisibleRows() {
		return maximumVisibleRows;
	}

	public void setMaximumVisibleRows(int maximumVisibleRows) {
		this.maximumVisibleRows = maximumVisibleRows;
	}

	@Override
	public void paintChildren(Graphics g){
		Insets insets = getInsets();
		g.clipRect(insets.left, insets.top, getWidth(), getHeight() - insets.top - insets.bottom);
		super.paintChildren(g);
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		super.addImpl(comp, constraints, index);

		if(maximumVisibleRows < getComponentCount()-1) {
			getScrollBar().setVisible(true);
		}
	}

	@Override
	public void remove(int index) {
		// can't remove the scrollbar
		++index;

		super.remove(index);

		if(maximumVisibleRows >= getComponentCount()-1) {
			getScrollBar().setVisible(false);
		}
	}

	@Override
	public void show(Component invoker, int x, int y){
	    JScrollBar scrollBar = getScrollBar();
	    if(scrollBar.isVisible()){
	        int extent = 0;
	        int max = 0;
	        int i = 0;
	        int unit = -1;
	        for(Component comp : getComponents()) {
	            if(!(comp instanceof JScrollBar)) {
	                int prefHeight = comp.getPreferredSize().height;
	                if(unit < 0){
	                    unit = prefHeight;
	                }
	                if(i++ < maximumVisibleRows) {
	                    extent += prefHeight;
	                }
	                max += prefHeight;
	            }
	        }
	
	        Insets insets = getInsets();
	        int margins = insets.top + insets.bottom;
	        scrollBar.setUnitIncrement(unit);
	        scrollBar.setBlockIncrement(extent);
	        scrollBar.setValues(0, margins + extent, 0, margins + max);
	
	        Dimension popupDim = this.getPreferredSize();
	        popupDim.width = this.getPreferredSize().width + 10;
	        popupDim.height = margins + extent;
	        setPopupSize(popupDim);
	    }
	
	    super.show(invoker, x, y);
	}
	
	protected static class ScrollPopupMenuLayout implements LayoutManager{
	    @Override public void addLayoutComponent(String name, Component comp) {}
	    @Override public void removeLayoutComponent(Component comp) {}
	
	    @Override public Dimension preferredLayoutSize(Container parent) {
	        int visibleAmount = Integer.MAX_VALUE;
	        Dimension dim = new Dimension();
	        for(Component comp :parent.getComponents()){
	            if(comp.isVisible()) {
	                if(comp instanceof JScrollBar){
	                    JScrollBar scrollBar = (JScrollBar) comp;
	                    visibleAmount = scrollBar.getVisibleAmount();
	                }
	                else {
	                    Dimension pref = comp.getPreferredSize();
	                    dim.width = Math.max(dim.width, pref.width);
	                    dim.height += pref.height;
	                }
	            }
	        }
	
	        Insets insets = parent.getInsets();
	        dim.height = Math.min(dim.height + insets.top + insets.bottom, visibleAmount);
	
	        return dim;
	    }
	
	    @Override public Dimension minimumLayoutSize(Container parent) {
	        int visibleAmount = Integer.MAX_VALUE;
	        Dimension dim = new Dimension();
	        for(Component comp : parent.getComponents()) {
	            if(comp.isVisible()){
	                if(comp instanceof JScrollBar) {
	                    JScrollBar scrollBar = (JScrollBar) comp;
	                    visibleAmount = scrollBar.getVisibleAmount();
	                }
	                else {
	                    Dimension min = comp.getMinimumSize();
	                    dim.width = Math.max(dim.width, min.width);
	                    dim.height += min.height;
	                }
	            }
	        }
	
	        Insets insets = parent.getInsets();
	        dim.height = Math.min(dim.height + insets.top + insets.bottom, visibleAmount);
	
	        return dim;
	    }
	
	    @Override 
	    public void layoutContainer(Container parent) {
	        Insets insets = parent.getInsets();
	
	        int width = parent.getWidth() - insets.left - insets.right;
	        int height = parent.getHeight() - insets.top - insets.bottom;
	
	        int x = insets.left;
	        int y = insets.top;
	        int position = 0;
	
	        for(Component comp : parent.getComponents()) {
	            if((comp instanceof JScrollBar) && comp.isVisible()) {
	                JScrollBar scrollBar = (JScrollBar) comp;
	                Dimension dim = scrollBar.getPreferredSize();
	                scrollBar.setBounds(x + width-dim.width, y, dim.width, height);
	                width -= dim.width;
	                position = scrollBar.getValue();
	            }
	        }
	
	        y -= position;
	        for(Component comp : parent.getComponents()) {
	            if(!(comp instanceof JScrollBar) && comp.isVisible()) {
	                Dimension pref = comp.getPreferredSize();
	                comp.setBounds(x, y, width, pref.height);
	                y += pref.height;
	            }
	        }
	    }
	}
}