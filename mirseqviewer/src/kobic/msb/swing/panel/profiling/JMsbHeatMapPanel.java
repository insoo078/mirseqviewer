package kobic.msb.swing.panel.profiling;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import kobic.com.util.Utilities;
import kobic.msb.server.model.ClusterModel;

public class JMsbHeatMapPanel extends JPanel implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public double[][] data;

	public JMsbHeatMapPanel( Object[][] data ) {
		this.data = JMsbHeatMapPanel.getMatrixFrom( data );
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		double width = this.getWidth();
		double height = this.getHeight();
		double max = Utilities.getMax( this.data );

		int increment = (int) Math.ceil( ((double)this.data.length / height) );

		double boxHeight = ((double)increment / this.data.length) * height;
		
		if( this.data.length > height ) {

			width = width / this.data[0].length;

			for(int i=0; i<this.data[0].length; i++) {
				int idx = 0;
				for(int j=0; j<this.data.length; j+=increment) {
					double boxY = idx * boxHeight;

					double ave = JMsbHeatMapPanel.getAveIntensity( this.data, j, increment, i);
					Rectangle2D.Double rect = new Rectangle2D.Double(i * width, boxY, width, boxHeight);
					idx++;

					if( Double.valueOf( ave ).equals( Double.NaN ) ) {
						g2.setColor( new Color(177, 177, 177) );
					}else {
						double value = ((ave * 255) / max);
						
						if( value > 255 )	value = 255;
						
						Color color = new Color(255, 0, 0);
						if( value < 255 )	color = new Color( 255, (int)(255-value), (int)(255-value) );
						g2.setColor( color );

					}
					g2.fill( rect );
				}
			}
		}else if( this.data.length < height ) {
			width = width / this.data[0].length;
			boxHeight = height / this.data.length;

			for(int i=0; i<this.data[0].length; i++) {
				for(int j=0; j<this.data.length; j++) {
					double ave = this.data[j][i];
					Rectangle2D.Double rect = new Rectangle2D.Double(i * width, j*boxHeight, width, boxHeight);

					if( Double.valueOf( ave ).equals( Double.NaN ) ) {
						g2.setColor( new Color(177, 177, 177) );
					}else {
						double value = ((ave * 255) / max);
						
						if( value > 255 )	value = 255;
						
						Color color = new Color(255, 0, 0);
						if( value < 0 )		value = 0;
						if( value > 255 )	value = 255;

						color = new Color( 255, (int)(255-value), (int)(255-value) );

						g2.setColor( color );
					}
					g2.fill( rect );
				}
			}
		}
	}

	public static double getAveIntensity( double[][] a, int from, int length, int columnIndex ) {
		double sum = 0;
		double cnt = 0;
		int nanCnt = 0;
		for(int i=from; i<(from+length) && i<a.length; i++) {			
			if( Double.valueOf( a[i][columnIndex] ).equals( Double.NaN ) == false ) {
				sum += a[i][columnIndex];
				cnt++;
			}else {
				nanCnt++;
			}
		}
		
		if( nanCnt == length )	return Double.NaN;

		return sum/cnt;
	}
	
	public static double[][] getMatrixFrom( Object[][] data ) {
		double[][] matrix = new double[ data.length ][data[0].length - 2];
		
		for(int i=0; i<data.length; i++) {
			for(int j=2; j<data[0].length; j++) {
				/*** Over JDK 1.7 ***/
//				if( data[i][j] instanceof Number )	matrix[i][j-2] = (double)data[i][j];
				/*** Under JDK 1.7 ***/
				if( data[i][j] instanceof Number )	matrix[i][j-2] = Double.valueOf( data[i][j].toString() );
				else								matrix[i][j-2] = Double.NaN;
			}
		}
		return matrix;
	}

	public void setData( Object[][] data) {
		this.data = JMsbHeatMapPanel.getMatrixFrom( data );
		this.repaint();
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		ClusterModel model = (ClusterModel)arg;
		this.data = model.getOriginalData();
		this.repaint();
	}
}
