package kobic.msb.swing.component;

/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import kobic.msb.swing.thread.MemorySwingWorker;
/**
 * This is a statusbar component with a text control for messages.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class JMsbStatusBar extends JPanel implements PropertyChangeListener{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**
   * Message to display if there is no msg to display. Defaults to a
   * blank string.
   */
	private String _msgWhenEmpty = " ";

  /** Label showing the message in the statusbar. */
	private final JLabel _textLbl = new JLabel();

	private final JProgressBar _progressBar = new JProgressBar();

	private final JPanel _pnlLabelOrProgress = new JPanel();

   /** Constraints used to add new controls to this statusbar. */
	private final GridBagConstraints _gbc = new GridBagConstraints();

	private Font _font;
	
	private JLabel memoryLabel = new JLabel( "Memory : " );

  /**
   * Default ctor.
   */
	public JMsbStatusBar() {
		super(new GridBagLayout());
		this.createGUI();
		
		MemorySwingWorker worker = new MemorySwingWorker( this.memoryLabel );
		
		worker.addPropertyChangeListener( this );
		worker.execute();
	}

	public JLabel getLabel() {
		return this._textLbl;
	}
  /**
   * Set the font for controls in this statusbar.
   *
   * @param font  The font to use.
   *
   * @throws  IllegalArgumentException
   *      Thrown if <TT>null</TT> <TT>Font</TT> passed.
   */
	@Override
	public synchronized void setFont(Font font) {
		if (font == null) {
			throw new IllegalArgumentException("Font == null");
		}
		super.setFont(font);
		this._font = font;
		this.updateSubcomponentsFont(this);
	}

  /**
   * Set the text to display in the message label.
   *
   * @param text  Text to display in the message label.
   */
	public synchronized void setText(String text) {
		String myText = null;
		if (text != null) {
			myText = text.trim();
		}
		if (myText != null && myText.length() > 0) {
			this._textLbl.setText(myText);
		} else {
			this.clearText();
		}
	}

    /**
     * Returns the text label's current value 
     * @return
     */
	public synchronized String getText() {
		return _textLbl.getText();
	}

	public synchronized void clearText() {
		this._textLbl.setText(this._msgWhenEmpty);
	}

	public synchronized void setTextWhenEmpty(String value) {
		final boolean wasEmpty = this._textLbl.getText().equals(this._msgWhenEmpty);
		if (value != null && value.length() > 0) {
			this._msgWhenEmpty = value;
		} else {
			this._msgWhenEmpty = " ";
		}
		if (wasEmpty) {
			clearText();
		}
	}

	public synchronized void addJComponent(JComponent comp) {
		if (comp == null) {
			throw new IllegalArgumentException("JComponent == null");
		}
		comp.setBorder(createComponentBorder());
		if (this._font != null) {
			comp.setFont(this._font);
			updateSubcomponentsFont(comp);
		}
		super.add(comp, this._gbc);
	}

	public static Border createComponentBorder() {
		return BorderFactory.createCompoundBorder( BorderFactory.createBevelBorder(BevelBorder.LOWERED), BorderFactory.createEmptyBorder(0, 4, 0, 4));
	}

	private void createGUI() {
		this.clearText();

		Dimension progSize = this._progressBar.getPreferredSize();
		progSize.height = this._textLbl.getPreferredSize().height;

		this._progressBar.setPreferredSize(progSize);

		this._progressBar.setStringPainted(true);

		this._pnlLabelOrProgress.setLayout(new GridLayout(1,1));
		this._pnlLabelOrProgress.add(this._textLbl);

      // The message area is on the right of the statusbar and takes
    // up all available space.
		this._gbc.anchor = GridBagConstraints.WEST;
		this._gbc.weightx = 1.0;
		this._gbc.fill = GridBagConstraints.HORIZONTAL;
		this._gbc.gridy = 0;
		this._gbc.gridx = 0;
		this.addJComponent( this._pnlLabelOrProgress );

    // Any other components are on the right.
		this._gbc.weightx = 0.0;
		this._gbc.anchor = GridBagConstraints.CENTER;
		this._gbc.gridx = GridBagConstraints.RELATIVE;
		this.addJComponent( this.memoryLabel );
	}

	private void updateSubcomponentsFont(Container cont) {
		Component[] comps = cont.getComponents();
		for (int i = 0; i < comps.length; ++i) {
			comps[i].setFont(this._font);
			if (comps[i] instanceof Container) {
				this.updateSubcomponentsFont((Container)comps[i]);
			}
		}
	}

	public void setStatusBarProgress(String msg, int minimum, int maximum, int value) {
		if(false == this._pnlLabelOrProgress.getComponent(0) instanceof JProgressBar) {
			this._pnlLabelOrProgress.remove(0);
			this._pnlLabelOrProgress.add( this._progressBar );
			this.validate();
		}

		this._progressBar.setMinimum(minimum);
		this._progressBar.setMaximum(maximum);
		this._progressBar.setValue(value);

		if(null != msg) {
			this._progressBar.setString(msg);
		} else {
			this._progressBar.setString("");
		}
	}

	public void setStatusBarProgressFinished() {
		if( this._pnlLabelOrProgress.getComponent(0) instanceof JProgressBar) {
			this._pnlLabelOrProgress.remove(0);
			this._pnlLabelOrProgress.add( this._textLbl );
			this.validate();
			this.repaint();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

		if (evt.getPropertyName().equals("progress")) {            
            // get the % complete from the progress event
            // and set it on the progress monitor
            int progress = ((Integer)evt.getNewValue()).intValue();
            String message = String.format("Completed %d%%.\n", progress);
            this.setStatusBarProgress( message, 0, 100, progress );
		}else if( evt.getPropertyName().equals("state") ) {
			this.setStatusBarProgress(null, 0, 0, 0);
		}
	}
	
	public JProgressBar getProgressBar() {
		return this._progressBar;
	}
}