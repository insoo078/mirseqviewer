package kobic.msb.swing.renderer;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import kobic.msb.system.config.ProjectConfiguration;

public class HeatMapTableCellColorRenderer  extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final long COLOR_RANGE = 255;

	private double			maxValue;
	private double			minValue;
	private double			unitValue;
	private int				totalColumnIndex;
	private List<Integer>	groupIndexes;
	
	private ProjectConfiguration	config;

	public HeatMapTableCellColorRenderer( double minValue, double maxValue, int totalColumnIndex, List<Integer> groupIndexes, ProjectConfiguration config ) {
		this.maxValue			= maxValue;
		if( minValue < 0 )		this.minValue = 0;
		else					this.minValue = minValue;
		this.totalColumnIndex	= totalColumnIndex;
		this.groupIndexes		= groupIndexes;
		this.config				= config;
		
		this.unitValue =  HeatMapTableCellColorRenderer.COLOR_RANGE / (this.maxValue - this.minValue);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if( isSelected == false ) {
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			String s = value.toString();

			this.setHorizontalAlignment( SwingConstants.RIGHT );

			double cellValue = this.minValue;
			try {
				cellValue = Double.parseDouble( s.replace(",", "") );
			}catch(Exception e) {
				cellValue = this.minValue;
			}

			double val = (this.unitValue * cellValue) - this.minValue;

			if( val >= HeatMapTableCellColorRenderer.COLOR_RANGE )		val = HeatMapTableCellColorRenderer.COLOR_RANGE;
			if( (int)val <= 10 )										val = 0;

			Color heatMapColor	= Color.red;
			Color fontColor		= Color.black;

			if( row == table.getRowCount() - 1 ) {
				// Rocord total sum
				heatMapColor	= this.config.get_total_sum_color_();
			}else if( s.equals("NaN") ){
				heatMapColor = this.config.get_missing_value_color_();
			}else {
				if( this.totalColumnIndex == column ) {
					// Total sum
					heatMapColor	= this.config.get_total_sum_color_();
				}else if( this.groupIndexes.contains( column ) ) {
					// Group sum column
					heatMapColor	= this.config.get_group_sum_color_();
				}else {
					// Real count data
					heatMapColor = new Color(255, 255-(int)val, 255-(int)val );

					if(	Math.abs(val) >= 100	)	fontColor = Color.white;
				}
			}

			comp.setBackground( heatMapColor );
			comp.setForeground( fontColor );
		  
			return( comp );
		}else {
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	} 
}