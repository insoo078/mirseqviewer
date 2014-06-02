package kobic.msb.swing.panel.profiling;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.apache.commons.math3.linear.RealVector;

import kobic.com.edgeR.DGEList;

public class JMsbDemirGraphPanel extends JPanel{
	private RealVector	aveLogCpm;
	private RealVector	tagwiseDispersion;
	private Double		commonDispersion;

	public JMsbDemirGraphPanel( DGEList dgeList ) {
		if( dgeList != null ) {
			this.aveLogCpm			= dgeList.getAveLogCPM();
			this.tagwiseDispersion	= dgeList.getTagwiseDispersion();
			this.commonDispersion	= dgeList.getCommonDispersion();
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void paintComponents(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		if( this.aveLogCpm != null && this.tagwiseDispersion != null && this.commonDispersion != null ) {
			System.out.println("Hello");
		}
	}
}
