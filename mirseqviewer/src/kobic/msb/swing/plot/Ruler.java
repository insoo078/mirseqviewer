package kobic.msb.swing.plot;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.List;

//import com.sun.javafx.geom.PathIterator;
//import com.sun.javafx.geom.RectBounds;
//import com.sun.javafx.geom.Shape;
//import com.sun.javafx.geom.transform.BaseTransform;

@SuppressWarnings("unused")
public class Ruler implements Shape{
	private Line2D.Double 		line;
	private List<Line2D.Double> radiations;
	
	private Scale scale;

	@Override
	public boolean contains(Point2D p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Rectangle2D r) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(double x, double y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle2D getBounds2D() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		// TODO Auto-generated method stub
		return false;
	}
	
//	public Ruler( Scale scale ) {
//		this.scale = scale;
//		if( this.scale.getDirection() == Scale.DIR_VERTICAL )
//			this.line = new Line2D.Double( 0, scale.getFrom(), 0, scale.getTo() );
//		else
//			this.line = new Line2D.Double( scale.getFrom(), 0, scale.getTo(), 0 );
//	}
//	
//	@Override
//	public boolean contains(float arg0, float arg1) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean contains(float arg0, float arg1, float arg2, float arg3) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public Shape copy() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public RectBounds getBounds() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public PathIterator getPathIterator(BaseTransform arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public PathIterator getPathIterator(BaseTransform arg0, float arg1) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean intersects(float arg0, float arg1, float arg2, float arg3) {
//		// TODO Auto-generated method stub
//		return false;
//	}
}
