package kobic.msb.common.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URI;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.RepaintManager;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.infonode.docking.RootWindow;
import net.infonode.docking.properties.TabWindowProperties;
import net.infonode.util.Direction;
import kobic.com.util.Utilities;
import kobic.msb.common.SwingConst;
import kobic.msb.server.obj.GenomeReferenceObject;
import kobic.msb.server.obj.HairpinSequenceObject;
import kobic.msb.server.obj.NucleotideObject;
import kobic.msb.server.obj.RnaStructureObj;
import kobic.msb.server.obj.SequenceObject;
import kobic.msb.swing.panel.alignment.obj.AlignedNucleotide;
import kobic.msb.swing.panel.alignment.obj.AlignedReadSequence;
import kobic.msb.system.engine.MsbEngine;

public class SwingUtilities {
	public static final int ALIGN_LEFT		= 1;
	public static final int ALIGN_RIGHT		= 2;
	public static final int ALIGN_CENTER	= 3;
	
	public static String getStyleFromInteger(int style) {
		if( style == Font.BOLD )
			return "BOLD";
		else if( style == Font.ITALIC )
			return "ITALIC";
		else
			return "PLAIN";
	}
	
	public static String getRealFontName( String fontName ) {
		return fontName.split("-")[0];
	}
	
	public static Dimension getScreenSize() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		return tk.getScreenSize();
	}

	public static Point2D.Double getLabelPositionOnRectangle( FontRenderContext frc, Rectangle2D.Double labelRect, String baseLineLabel, int align ) {
		Font drwFont = new Font("Arial", Font.PLAIN, 11);

		return SwingUtilities.getLabelPositionOnRectangle(frc, drwFont, labelRect, baseLineLabel, align);
	}

	public static Point2D.Double getLabelPositionOnRectangle( FontRenderContext frc, Font drwFont, Rectangle2D.Double labelRect, String baseLineLabel, int align ) {
		Rectangle2D fontRect = drwFont.getStringBounds(baseLineLabel, frc);
		LineMetrics lm = drwFont.getLineMetrics(baseLineLabel, frc);

		double 											newX = labelRect.getMinX() + (labelRect.getWidth()/2)	- (fontRect.getWidth()/2);
		if( align == SwingUtilities.ALIGN_LEFT )		newX = labelRect.getMinX() + 5;
		else if(align == SwingUtilities.ALIGN_RIGHT )	newX = labelRect.getMaxX() - fontRect.getWidth() - 5;

		double newY = (float)(labelRect.getMinY() + (labelRect.getHeight() + fontRect.getHeight())/2 - lm.getDescent());
		
		return new Point2D.Double( newX, newY );
	}

	public static Point2D.Double getLabelPositionOnRectangle( FontRenderContext frc, Font drwFont, RoundRectangle2D.Double labelRect, String baseLineLabel, int align ) {
		Rectangle2D fontRect = drwFont.getStringBounds(baseLineLabel, frc);
		LineMetrics lm = drwFont.getLineMetrics(baseLineLabel, frc);

		double 											newX = labelRect.getMinX() + (labelRect.getWidth()/2)	- (fontRect.getWidth()/2);
		if( align == SwingUtilities.ALIGN_LEFT )		newX = labelRect.getMinX() + 5;
		else if(align == SwingUtilities.ALIGN_RIGHT )	newX = labelRect.getMaxX() - fontRect.getWidth() - 5;

		double newY = (float)(labelRect.getMinY() + (labelRect.getHeight() + fontRect.getHeight())/2 - lm.getDescent());
		
		return new Point2D.Double( newX, newY );
	}
	
	public static Area getArrowFromTo( Point2D.Double from, Point2D.Double to ) {
		double dx = 8;
		double dy = Math.floor(dx/2);
		if( from.getX() < to.getX() )	dx *= -1;

		GeneralPath arrowHeadFrom = new GeneralPath();
		arrowHeadFrom.moveTo(	to.getX() + dx	,	to.getY() - dy		);
		arrowHeadFrom.lineTo(	to.getX()		,	to.getY()			);
		arrowHeadFrom.lineTo(	to.getX() + dx	,	to.getY() + dy		);
		arrowHeadFrom.lineTo(	to.getX() + dx	,	to.getY() + 0.1		);
		arrowHeadFrom.lineTo(	from.getX()		,	from.getY() + 0.1	);
		arrowHeadFrom.lineTo(	from.getX()		,	from.getY() - 0.1	);
		arrowHeadFrom.lineTo(	to.getX() + dx	,	to.getY() - 0.1		);
		arrowHeadFrom.lineTo(	to.getX() + dx	,	to.getY() - dy		);		

		Area b = new Area( arrowHeadFrom );

		return b;
	}
	
	public static Point2D.Double getDrawingPosition(Dimension dim, double width, double height) {
		Point2D.Double point = new Point2D.Double( (dim.getWidth()/2) - (width/2), (dim.getHeight()/2) - (height/2) );

		return point;
	}
	
	public static void setDefaultCursorFor( Component frame ) {
		frame.setCursor( Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR ) );
	}
	
	public static void setWaitCursorFor( Component frame ) {
		frame.setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR ) );
	}
	
	public static void main(String[] args ) {
//		String path = Const.class.getClassLoader().getResource("./resources/images/run.png").toString();
//		System.out.println ( path );
//		javax.swing.ImageIcon icon = new javax.swing.ImageIcon( Const.class.getClassLoader().getResource("../../../../../resources/images/run.png") );
		System.out.println( SwingUtilities.getStringFromColor( Color.white ) );
//		System.out.println( "5 upstream : " + SwingUtilities.getStringFromColor( new Color(0x2EACFF) ) );
//		System.out.println( "5p : " + SwingUtilities.getStringFromColor( Color.orange ) );
//		System.out.println( "loop : " + SwingUtilities.getStringFromColor( new Color(0xB265DC) ) );
//		System.out.println( "3p : " + SwingUtilities.getStringFromColor( Color.green ) );
//		System.out.println( "3 downstream : " + SwingUtilities.getStringFromColor( Color.blue ) );
//		
//		System.out.println( "gradient 1 : " + SwingUtilities.getStringFromColor( new Color(0xF0E2FF) ) );
//		System.out.println( "gradient 2 : " + SwingUtilities.getStringFromColor( new Color(0xA092FF) ) );
		
	}

	public static String getSelectedProjectNameFromTreePath( TreePath choosedNode ) {
		TreePath parentPath = choosedNode.getParentPath();
		if( parentPath == null )			{
			return null;
		}
		String projectName = "";
		Object[] path = choosedNode.getParentPath().getPath();

		if( path.length == 1)	projectName = choosedNode.getLastPathComponent().toString();
		else					projectName = choosedNode.getParentPath().getPath()[1].toString();
		
		return projectName;
	}

	
	public static double getVariableDrawUnit( Rectangle2D.Double baseRect, List<RnaStructureObj> nucleotides ) {
		double unit = 1d;
		Rectangle2D.Double rawDataArea = MsvUtilities.getRect( nucleotides );

		double DRAWING_MARGIN = 20;
		
		double diffWidth	= (baseRect.width - 2*DRAWING_MARGIN) 	/ rawDataArea.width;
		double diffHeight	= (baseRect.height - 2*DRAWING_MARGIN)	/ rawDataArea.height;

		if( diffWidth < diffHeight )
			unit = diffWidth;
		else unit = diffHeight;

		return unit;
	}

	public static Point2D.Double getCenterOfDrawingImage( List<RnaStructureObj> rnaList ) {
		double minX2	=	9999999;
		double minY2	=	9999999;
		double maxX2	=	-9999999;
		double maxY2	=	-9999999;

		Iterator<RnaStructureObj> iter = rnaList.iterator();
		while( iter.hasNext() ) {
			Shape nucleotide = iter.next().getEllipse();
			
			Rectangle2D boundOfNecleotide = nucleotide.getBounds2D();

			if( boundOfNecleotide.getCenterX() < minX2 )	minX2 = boundOfNecleotide.getCenterX();
			if( boundOfNecleotide.getCenterX() > maxX2 )	maxX2 = boundOfNecleotide.getCenterX();
			if( boundOfNecleotide.getCenterY() < minY2 )	minY2 = boundOfNecleotide.getCenterY();
			if( boundOfNecleotide.getCenterY() > maxY2 )	maxY2 = boundOfNecleotide.getCenterY();
		}
		
		double centerX = (minX2 + maxX2) / 2;
		double centerY = (minY2 + maxY2) / 2;

		return new Point2D.Double(centerX, centerY);
	}

	/*********************************************************************************************************************************************
	 * makeNucleotidesInRnaStructure is drawing RNA structure which are not considered image origin
	 * 
	 * @param baseRect		Canvas area
	 * @param nucleotides	to draw nucleotides list
	 * @return	to draw ellipses about nucleotides
	 *********************************************************************************************************************************************/
	public static List<RnaStructureObj> makeNucleotidesInRnaStructure( Rectangle2D.Double baseRect, List<RnaStructureObj> nucleotides, double unit, double radius ) {
		Iterator<RnaStructureObj> iter = nucleotides.iterator();

		while( iter.hasNext() ) {
			RnaStructureObj rna = iter.next();
			double newX = baseRect.getMinX() + rna.getX() * unit;
			double newY = baseRect.getMinY() + (baseRect.getMaxY() - rna.getY()) * unit;

			if( rna.getEllipse() == null )
				rna.setEllipse( new Ellipse2D.Double( newX - radius, newY - radius, 2 * radius, 2 * radius ) );
			else
				rna.getEllipse().setFrame(newX - radius, newY - radius, 2 * radius, 2 * radius );
		}

		return nucleotides;
	}

	
	public static List<RnaStructureObj> getSecondaryStructureInfo( SequenceObject rnaSequenceObject ) {
		List<RnaStructureObj> objList = new ArrayList<RnaStructureObj>();

//		int i = 0;
		List<NucleotideObject> hairpinStructures = rnaSequenceObject.getSequence();
		for( Iterator<NucleotideObject> iter = hairpinStructures.iterator(); iter.hasNext(); ) {
			NucleotideObject nucleotide = iter.next();

			RnaStructureObj obj = new RnaStructureObj();
			obj.setX( nucleotide.getCoordinate().getX() );
			obj.setY( nucleotide.getCoordinate().getY() );
			obj.setType( nucleotide.getNucleotideType() );
			obj.setColor( nucleotide.getColor() );
			obj.setHover( false );
			obj.setPosition( nucleotide.getPosition() );

			objList.add( obj );
//			i++;
		}
		
		return objList;
	}
	
	public static List<AlignedNucleotide> getPrematureSequenceNucleotideObjectListReverse( HairpinSequenceObject premature, List<AlignedNucleotide> genomeResult, double yPos, double widthUnit, double heightUnit ) {
		List<NucleotideObject> preList = premature.getSequence();
		List<AlignedNucleotide> rectObjList = new ArrayList<AlignedNucleotide>();
		for(int i=genomeResult.size()-1; i>=0; i--) {
			int genomePos = genomeResult.get(i).getPosition() ;

			for(int j=preList.size()-1; j>=0; j--) {
				int prematurePos = preList.get(j).getPosition();
				AlignedNucleotide an = genomeResult.get(i);

				if( prematurePos == genomePos ) {
					RoundRectangle2D.Double rect = new RoundRectangle2D.Double(an.getBlock().getX(), yPos, widthUnit, heightUnit, 5, 5);
					AlignedNucleotide anObject = new AlignedNucleotide( preList.get(j), rect );

					rectObjList.add( anObject );
					break;
				}
			}
		}
		return rectObjList;
	}

	public static List<AlignedNucleotide> getReferenceNucleotideObjectList( GenomeReferenceObject reference, double offset, double yPos, double widthUnit, double heightUnit ) {
		List<NucleotideObject> nucleotideObjects = reference.getSequence();
		
		List<AlignedNucleotide> rectObjList = new ArrayList<AlignedNucleotide>();
		for(int i=0; i<nucleotideObjects.size(); i++) {
			double x = offset + ( i * widthUnit );
			double y = yPos;
			RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x, y, widthUnit, heightUnit, 5, 5);
			AlignedNucleotide anObject = new AlignedNucleotide( nucleotideObjects.get(i), rect );

			rectObjList.add( anObject );
		}

		return rectObjList;
	}
	
	public static List<AlignedNucleotide> getPrematureSequenceNucleotideObjectList( HairpinSequenceObject premature, List<AlignedNucleotide> genomeResult, double yPos, double widthUnit, double heightUnit ) {
		List<NucleotideObject> preList = premature.getSequence();
		List<AlignedNucleotide> rectObjList = new ArrayList<AlignedNucleotide>();
		for(int i=0; i<genomeResult.size(); i++) {
			int genomePos = genomeResult.get(i).getPosition() ;
			int minPos = preList.get(0).getPosition();
			int maxPos = preList.get( preList.size() - 1 ).getPosition();

			if( genomePos >= minPos && genomePos <= maxPos ) {
				for(int j=0; j<preList.size(); j++) {
					int prematurePos = preList.get(j).getPosition();
					AlignedNucleotide an = genomeResult.get(i);

					if( prematurePos == genomePos ) {	
						RoundRectangle2D.Double rect = new RoundRectangle2D.Double(an.getBlock().getX(), yPos, widthUnit, heightUnit, 5, 5);
						AlignedNucleotide anObject = new AlignedNucleotide( preList.get(j), rect );

						rectObjList.add( anObject );
						break;
					}
				}
			}
		}
		return rectObjList;
	}

	public static Color getColorFromString(String colorStr) {
	    return new Color(Integer.parseInt(colorStr));
	}

	public static String getStringFromColor(Color color) {
		return Integer.toString( color.getRGB() );
	}

	public static int getIndexAtColumnModel(TableColumnModel model, String name) {
		for(int i=0; i<model.getColumnCount(); i++) {
			String headStr = model.getColumn(i).getHeaderValue().toString();

			if( name.equals( headStr ) ) {
				return i;
			}
		}
		return 0;
	}
	
	public static void disableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(false);
	}

	public static void enableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(true);
	}
	
	public static void expandTree(JTree tree) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
        @SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> e = root.breadthFirstEnumeration();
        while(e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
            if(node.isLeaf()) continue;
            int row = tree.getRowForPath(new TreePath(node.getPath()));
            tree.expandRow(row);
        }
    }
	
	public static Font scaleFont(String text, Rectangle2D.Double rect, Graphics2D g2, Font pFont) {
	    float fontSize = 20.0f;
	    Font font = pFont;

	    font = g2.getFont().deriveFont(fontSize);
	    int width = g2.getFontMetrics(font).stringWidth(text);
	    fontSize = (float) ((rect.width / width ) * fontSize);
	    return g2.getFont().deriveFont(fontSize);
	}
	
	public static String getRealNumberString( double value ) {
		return String.format( "%.1f", value );
	}
	
	public static double getMonitorYCoordinateSystem( double x, Rectangle2D.Double rect ) {
		return rect.getMaxY() - x;
	}
	
	public static boolean isNumericTextField(JTextField text, String message) {
		if( Utilities.isNumeric( text.getText() ) )	return true;
		if( message != null )	JOptionPane.showMessageDialog(text, message, "Input error", JOptionPane.ERROR_MESSAGE );
		text.requestFocus();

		return false;
	}
	
	public static void linkToUrl(String url) {
		Desktop dt = Desktop.getDesktop();
		URI uri = URI.create(url);
		try {
			dt.browse(uri.resolve(uri));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
			MsbEngine.logger.error( "error : ", e1 );
		}
	}
	
	public static String getNumberWithFractionDigit( double value, int fracLength ) {
		NumberFormat nf = NumberFormat.getInstance();
		
		nf.setMaximumFractionDigits(fracLength);
		nf.setMinimumFractionDigits(fracLength);
		
		return nf.format(value);
	}

	public static int getGenomePos( List<NucleotideObject> refList, int pos ) {
		for(int i=0; i<refList.size(); i++){
			NucleotideObject an = refList.get(i);
			if( an.getPosition() == pos )	return i;
		}
		return -1;
	}
	
	public static RootWindow getThemedDockingRootWindow(RootWindow rootWindow) {
		rootWindow.getRootWindowProperties().addSuperObject( SwingConst.MAIN_INFONODE_THEME.getRootWindowProperties() );

		TabWindowProperties tabWindowProperties = rootWindow.getRootWindowProperties().getTabWindowProperties();
		
		tabWindowProperties.getCloseButtonProperties().setVisible(false);

		rootWindow.getWindowBar( Direction.DOWN ).setEnabled(true);
		
		return rootWindow;
	}
}
