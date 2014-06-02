package kobic.msb.swing.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.system.engine.MsbEngine;


public class NumberTableCellRenderer extends DefaultTableCellRenderer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	public static DecimalFormat numberFormat = new DecimalFormat("#,###.####");

	@Override
	public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);

		try {
			if( c instanceof JLabel && Utilities.isNumeric( value.toString() ) ) {
				this.setHorizontalAlignment(SwingConstants.RIGHT);
				double val = Double.parseDouble( value.toString() );
	
				JLabel label = (JLabel)c;
				if( !Double.isNaN( val ) ) {
//					String text = NumberTableCellRenderer.numberFormat.format( val );
					String text = SwingUtilities.getNumberWithFractionDigit( val, JMsbSysConst.FRACTION_LENGTH );
					label.setText( text );
				}else {
					label.setText( "NA" );
				}
			}else if( c instanceof JLabel && !Utilities.isNumeric( value.toString() ) ) {
				JLabel label = (JLabel)c;
				if( label.getText().equals("NaN") )	label.setText("NA");
	
				if( value.toString().equals("NA") || value.toString().equals("NaN") ) {
					this.setHorizontalAlignment(SwingConstants.RIGHT);
				}else {
					this.setHorizontalAlignment(SwingConstants.LEFT);
				}
			}
			
			if (isSelected){
	            c.setBackground(jTable.getSelectionBackground());
	            c.setForeground(jTable.getSelectionForeground());
	        }else{
				if (row%2 == 0)	c.setBackground(Color.WHITE);
				else			c.setBackground( new Color(235, 235, 245) );
				c.setForeground( jTable.getForeground() );
	        }
		}catch(Exception e ) {
			MsbEngine.logger.error("error : ", e);
		}
		return c;
	}

}
