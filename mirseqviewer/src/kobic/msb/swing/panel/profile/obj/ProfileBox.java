package kobic.msb.swing.panel.profile.obj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import kobic.com.util.Utilities;
import kobic.msb.system.config.ProjectConfiguration;

public class ProfileBox extends ProfileItem{
	public ProfileBox(Object label, int row, int col, ProjectConfiguration configuration) {
		super( label, row, col, configuration );
	}

	public void draw(Graphics2D g2) {
		ProjectConfiguration config = this.getConfiguration();

		double x = config.getExpressionProfileLabelOffset() + (this.getCol() * config.getExpressionProfileBlockWidth());
		double y = (this.getRow() * config.getExpressionProfileBlockHeight());
		
		double value = 0;
		if( Utilities.isNumeric( this.getLabel().toString() ) ) {
			value = Double.parseDouble( this.getLabel().toString() );
		}
		Rectangle2D.Double rect = new Rectangle2D.Double( x, y, config.getExpressionProfileBlockWidth(), config.getExpressionProfileBlockHeight() );
		
		Color color = new Color(255, 0, 0);
		if( value < 255 )	color = new Color( 255, (int)(255-value), (int)(255-value) );
		
		g2.setColor( color );
		g2.fill( rect );
	}
}
