package kobic.msb.swing.panel.profile;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import kobic.com.apporiented.algorithm.clustering.visualization.ClusterComponent;
import kobic.msb.server.model.ClusterModel;
import kobic.msb.swing.panel.profile.obj.ProfileBox;
import kobic.msb.swing.panel.profile.obj.ProfileItem;
import kobic.msb.swing.panel.profile.obj.ProfileTrack;
import kobic.msb.system.catalog.ProjectMapItem;

public class JExpressionProfilePanel extends JPanel implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<ProfileTrack> profileTracks;
	private ProjectMapItem projectItem;
	
	private ClusterModel clusterModel;
	
	final static BasicStroke solidStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
	private Color lineColor = Color.BLACK;
	
    private boolean showDistanceValues = false;
    private boolean showScale = true;
    private int borderTop = 20;
    private int borderLeft = 20;
    private int borderRight = 20;
    private int borderBottom = 20;
    private int scalePadding = 10;
    private int scaleTickLength = 4;
    private int scaleTickLabelPadding = 4;
    private double scaleValueInterval = 0;
    private int scaleValueDecimals = 0;
	
	public JExpressionProfilePanel( ProjectMapItem projectItem ) {
		this.projectItem = projectItem;
		this.profileTracks = new ArrayList<ProfileTrack>();

		this.clusterModel = projectItem.getClusterModel();

		this.initialize();
	}
	
	public void initialize() {
		double[][] profile = this.clusterModel.getOriginalData();
		String[] names = this.clusterModel.getRowNames();
		
		for(int i=0; i<profile.length; i++) {
			ProfileTrack track = new ProfileTrack( i, names[i], this.projectItem.getProjectConfiguration() );
			for(int j=0; j<profile[0].length; j++) {
				ProfileItem item = new ProfileBox( profile[i][j], i, j, this.projectItem.getProjectConfiguration() );

				track.add( item );
			}
			this.profileTracks.add( track );
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;        
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = (int)(this.profileTracks.get(0).getTrackWidth());
		int height = (int)(this.projectItem.getProjectConfiguration().getExpressionProfileBlockHeight() * this.profileTracks.size());

		for(int i=0; i<this.profileTracks.size(); i++) {
			ProfileTrack track = this.profileTracks.get(i);

			track.draw( g2 );
		}
		
		Rectangle2D.Double baseRect = new Rectangle2D.Double( this.projectItem.getProjectConfiguration().getExpressionProfileLabelOffset(), 0, width - this.projectItem.getProjectConfiguration().getExpressionProfileLabelOffset(), height );
		
		g2.setColor( Color.LIGHT_GRAY );
		g2.draw( baseRect );

		this.setPreferredSize( new Dimension(width, height) );
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		this.repaint();
	}
}
