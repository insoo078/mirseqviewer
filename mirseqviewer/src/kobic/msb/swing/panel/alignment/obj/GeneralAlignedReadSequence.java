package kobic.msb.swing.panel.alignment.obj;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JComponent;

import kobic.com.util.Utilities;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.Model;
import kobic.msb.server.obj.GeneralReadObject;
import kobic.msb.server.obj.GenomeReferenceObject;
import kobic.msb.server.obj.HairpinSequenceObject;
import kobic.msb.server.obj.NucleotideObject;
import kobic.msb.server.obj.ReadObject;
import kobic.msb.server.obj.ReadQuality;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;

public class GeneralAlignedReadSequence {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Model						model;
	private Rectangle2D.Double			boundary;
	private ReadObject					readObj;
	private ProjectConfiguration		config;
	private double						y;
	private int							mismatchCount;

//	private Rectangle2D.Double			backgroundRect;
//	private List<Line2D.Double>			linkedLine;

	private HashMap<Integer, NucleotideObject> seqMap;

	private List<AlignedReadSequence>	blocks;
	
	private List<AlignedNucleotide>		referenceResult;

	public GeneralAlignedReadSequence( List<AlignedNucleotide> referenceResult, Model model, ProjectConfiguration config, ReadObject readObj, double y ) {
		this.referenceResult	= referenceResult;
		this.model				= model;
		this.config				= config;
		this.readObj			= readObj;
		this.y					= y;
		this.mismatchCount		= 0;
		this.boundary			= new Rectangle2D.Double(0, 0, 0, 0);
		this.blocks 			= new ArrayList<AlignedReadSequence>();
		this.seqMap				= new HashMap<Integer, NucleotideObject>();

//		this.linkedLine			= new ArrayList<Line2D.Double>();
		
		this.initialize();
	}
	
	public int getFragmentSize() {
		return this.blocks.size();
	}

	public List<NucleotideObject> getSequence() {
		return this.readObj.getSequence();
	}
	
	public long getStart() {
		return this.readObj.getStartPosition();
	}
	
	public long getEnd() {
		return this.readObj.getEndPosition();
	}

	public Rectangle2D.Double getCurrentRecordObj( Rectangle2D.Double baseRect ) {
		if( this.boundary == null )	this.boundary = new Rectangle2D.Double( baseRect.getMinX(), this.y, baseRect.getWidth(), this.config.getBlockHeight());
		this.boundary.setFrame(
			baseRect.getMinX(), this.y,
			baseRect.getWidth(), this.config.getBlockHeight()
		);

		return this.boundary;
	}
	
	public double getVerticalPosition() {
		return this.y;
	}

	public void draw( Rectangle2D.Double visibleRect, Graphics2D g2 ) {
		this.drawLinkLine( visibleRect, g2 );
		this.drawReads( visibleRect, g2 );
		this.drawReadBackground(visibleRect, g2);
	}

	public int getMismatchCount() {
		return this.mismatchCount;
	}

	public String getShowAlignmentTooltip( int col ) {
		HairpinSequenceObject premature = this.model.getPrematureSequenceObject();

		ReadQuality readQuality = this.readObj.getReadQuality();

		long startSite = this.readObj.getStartPosition();
		long endSite = this.readObj.getEndPosition();

		String base = this.seqMap.get(col)==null?"":this.seqMap.get(col).getNucleotideType();

		int baseQuality = this.seqMap.get(col)==null?0:this.seqMap.get(col).getBaseQuality();
		 
		String strReadName			= " Read name = "							+ readQuality.getReadName();
		String strLocation			= " Location = "							+ premature.getChromosome() + ":" + Utilities.getNumberWithComma(col);
		String strAlignmentStart	= " Alignment start = "						+ Utilities.getNumberWithComma(startSite) + "(" + readQuality.getOrientation() + ")";
		String strAlignmentEnd		= " Alignment end = "						+ Utilities.getNumberWithComma(endSite) + "(" + readQuality.getOrientation() + ")";
		String strCigar				= " Cigar = "								+ readQuality.getCigar();
		String strMapped			= " Mapped = "								+ readQuality.isMapped();
		String strMapq				= " Mapping quality = "						+ readQuality.getMapq();
		String strBase				= " Base = "								+ base;
		String strBaseQuality		= " Base phred quality = "					+ baseQuality;
		String strX0				= " X0 = "									+ Utilities.nulltoEmpty( readQuality.getX0() );
		String strX1				= " X1 = "									+ Utilities.nulltoEmpty( readQuality.getX1() );
		String strXa				= " XA = "									+ Utilities.nulltoEmpty( readQuality.getXa() );
		String strMd				= " MD = "									+ Utilities.nulltoEmpty( readQuality.getMd() );
		String strXg				= " XG = "									+ Utilities.nulltoEmpty( readQuality.getXg() );
		String strNm				= " NM = "									+ Utilities.nulltoEmpty( readQuality.getNm() );
		String strNH				= " NH = "									+ Utilities.nulltoEmpty( readQuality.getNh() );
		String strXm				= " XM = "									+ Utilities.nulltoEmpty( readQuality.getXm() );
		String strXo				= " XO = "									+ Utilities.nulltoEmpty( readQuality.getXo() );
		String strXt				= " XT = "									+ Utilities.nulltoEmpty( readQuality.getXt() );
		String strCt				= " CT = "									+ Utilities.nulltoEmpty( readQuality.getCt() );
		
		String text = "<HTML>-------------------------------<BR>";
		
		text += strReadName + "   <BR>";
		text += "-------------------------------<BR>";
		text += strLocation + "<BR>";
		text += strAlignmentStart + "   <BR>";
		text += strAlignmentEnd + "   <BR>";
		text += strCigar + "   <BR>";
		text += strMapped + "   <BR>";
		text += strMapq + "   <BR>";
		text += "-------------------------------<BR>";
		text += strBase + "   <BR>";
		text += strBaseQuality + "   <BR>";
		text += "-------------------------------<BR>";

		if( !Utilities.nulltoEmpty( readQuality.getX0() ).isEmpty() )	text += strX0 + "   <BR>";
		if( !Utilities.nulltoEmpty( readQuality.getX1() ).isEmpty() )	text += strX1 + "   <BR>";
		if( !Utilities.nulltoEmpty( readQuality.getXa() ).isEmpty() )	text += strXa + "   <BR>";
		if( !Utilities.nulltoEmpty( readQuality.getMd() ).isEmpty() )	text += strMd + "   <BR>";
		if( !Utilities.nulltoEmpty( readQuality.getXg() ).isEmpty() )	text += strXg + "   <BR>";
		if( !Utilities.nulltoEmpty( readQuality.getNm() ).isEmpty() )	text += strNm + "   <BR>";
		if( !Utilities.nulltoEmpty( readQuality.getNh() ).isEmpty() )	text += strNH + "   <BR>";
		if( !Utilities.nulltoEmpty( readQuality.getXm() ).isEmpty() )	text += strXm + "   <BR>";
		if( !Utilities.nulltoEmpty( readQuality.getXo() ).isEmpty() )	text += strXo + "   <BR>";
		if( !Utilities.nulltoEmpty( readQuality.getXt() ).isEmpty() )	text += strXt + "   <BR>";
		if( !Utilities.nulltoEmpty( readQuality.getCt() ).isEmpty() )	text += strCt + "   <BR>";
		text += "-------------------------------<BR>";
		text += "<HTML>";

		return text;
	}
	
	public ReadObject getReadObject() {
		return this.readObj;
	}

	private void drawLinkLine( Rectangle2D.Double visibleRect, Graphics2D g2 ) {
		Color currentColor = g2.getColor();
		Composite currentComposite = g2.getComposite();
		Stroke currentStroke = g2.getStroke();

		float alpha = 0.2f;
		int type = AlphaComposite.SRC_OVER; 
		AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
		g2.setComposite( composite );
		
		g2.setColor( Color.blue );

		float dash1[] = {1.0f};
		BasicStroke linkLineComposite = new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dash1, 0.0f);
		g2.setStroke(linkLineComposite);

		Line2D.Double emptyLink = new Line2D.Double( visibleRect.getMinX(), this.getCurrentRecordObj(visibleRect).getCenterY(), visibleRect.getMaxX(), this.getCurrentRecordObj(visibleRect).getCenterY() );
		Hashtable<Integer, Line2D.Double> shapeHash = new Hashtable<Integer, Line2D.Double>();

		// Draw the read direction shape
		for(int i=0; i<this.blocks.size(); i++) {
			AlignedReadSequence first = this.blocks.get(i);
			for(int j=i+1; j<this.blocks.size()&j==i+1; j++) {
				AlignedReadSequence second = this.blocks.get(j);

				Line2D.Double line = null;
				if( first.getType()==AlignedReadSequence.OUT_LEFT_BLOCK && second.getType()==AlignedReadSequence.IN_BLOCK )	{
					Rectangle2D.Double sr = second.getBoundary();
					line = new Line2D.Double( visibleRect.getMinX(), this.getCurrentRecordObj(visibleRect).getCenterY(), sr.getMinX(), this.getCurrentRecordObj(visibleRect).getCenterY() );
				}else if( first.getType()==AlignedReadSequence.IN_BLOCK && second.getType()==AlignedReadSequence.OUT_LEFT_BLOCK )	{
					Rectangle2D.Double fr = first.getBoundary();
					line = new Line2D.Double( visibleRect.getMinX(), this.getCurrentRecordObj(visibleRect).getCenterY(), fr.getMinX(), this.getCurrentRecordObj(visibleRect).getCenterY() );
				}else if( first.getType()==AlignedReadSequence.IN_BLOCK && second.getType()==AlignedReadSequence.OUT_RIGHT_BLOCK ) {
					Rectangle2D.Double fr = first.getBoundary();
					line = new Line2D.Double( fr.getMaxX(), this.getCurrentRecordObj(visibleRect).getCenterY(), visibleRect.getMaxX(), this.getCurrentRecordObj(visibleRect).getCenterY() );
				}else if( first.getType()==AlignedReadSequence.OUT_RIGHT_BLOCK && second.getType()==AlignedReadSequence.IN_BLOCK ){
					Rectangle2D.Double fr = second.getBoundary();
					line = new Line2D.Double( fr.getMaxX(), this.getCurrentRecordObj(visibleRect).getCenterY(), visibleRect.getMaxX(), this.getCurrentRecordObj(visibleRect).getCenterY() );
				}else if( first.getType()==AlignedReadSequence.IN_BLOCK && second.getType()==AlignedReadSequence.IN_BLOCK ) {
					Rectangle2D.Double fr = first.getBoundary();
					Rectangle2D.Double sr = second.getBoundary();

					if( fr.getMaxX() > sr.getMinX() ) {
						line = new Line2D.Double( sr.getMaxX(), this.getCurrentRecordObj(visibleRect).getCenterY(), fr.getMinX(), this.getCurrentRecordObj(visibleRect).getCenterY() );
					}else {
						line = new Line2D.Double( fr.getMaxX(), this.getCurrentRecordObj(visibleRect).getCenterY(), sr.getMinX(), this.getCurrentRecordObj(visibleRect).getCenterY() );
					}
				}else if( first.getType()==AlignedReadSequence.OUT_LEFT_BLOCK && second.getType()==AlignedReadSequence.OUT_RIGHT_BLOCK ) {
					line = emptyLink;
				}else if( first.getType()==AlignedReadSequence.OUT_RIGHT_BLOCK && second.getType()==AlignedReadSequence.OUT_LEFT_BLOCK ) {
					line = emptyLink;
				}

				if( line != null ) {
					double leftSide = visibleRect.getMinX() + this.config.getOffset();
					if( line.getX1() < leftSide )	line.setLine( leftSide, line.getY1(), line.getX2(), line.getY2());
	
					if( !shapeHash.contains( line.hashCode() ) )	shapeHash.put(line.hashCode(), line);
				}
			}
		}
		Collection<Line2D.Double> collections = shapeHash.values();
		for(Line2D.Double line:collections) {
			g2.draw( line );
		}

		shapeHash = null;
		g2.setComposite(currentComposite);
		g2.setStroke(currentStroke);
		g2.setColor( currentColor );
	}

	private void drawReads( Rectangle2D.Double visibleRect, Graphics2D g2 ) {
		Font currentFont	= g2.getFont();

		Font newFont = this.config.getSystemFont();

		g2.setFont( newFont );

		FontRenderContext	frc					= g2.getFontRenderContext();
		Composite			currentComposite	= g2.getComposite();
		Color currentColor	= g2.getColor();
		Color systemColor	= g2.getColor();

		for( int idx=0; idx<this.blocks.size(); idx++ ) {
			AlignedReadSequence readSequence = this.blocks.get( idx );

			if( readSequence.isDrawable() == false )	continue;

			// Draw direction of read
//			this.drawReadDirection( g2, readSequence, idx, this.blocks.size() );

			AlignedNucleotide chk = readSequence.getAlignedNucleotide( 0 );
			RoundRectangle2D.Double rectChk = chk.getBlock();

			// Draw only records
			float alpha = 0.2f;
			int type = AlphaComposite.SRC_OVER; 
			AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
			g2.setComposite( composite );

			// Draw nucleotides with String
			for(int i=0; i<readSequence.getSequenceLength(); i++) {
				AlignedNucleotide an = readSequence.getAlignedNucleotide( i );
				RoundRectangle2D.Double rect = an.getBlock();
				String strNucleotide	= an.getNucleotideType();

				if( visibleRect.getMinX()-rectChk.getWidth() <= rect.getMinX() && visibleRect.getMaxX()+rectChk.getWidth() >= rect.getMaxX() ) {
					Color bgColor = this.getBackgroundColorByNucleotide( strNucleotide, an.isDifferentWithRef() );

					if( bgColor.getRGB() < Color.white.getRGB() ) {
						g2.setColor( bgColor );
						g2.fill( rect );
					}

					// Text Area
					alpha = 0.99f; 
					composite = AlphaComposite.getInstance(type, alpha);
					g2.setComposite( composite );
					g2.setColor( this.getForegroundColorByNucleotide( an.isDifferentWithRef() ) );
					Point2D.Double newLabelPosition = SwingUtilities.getLabelPositionOnRectangle(frc, g2.getFont(), rect, strNucleotide, SwingUtilities.ALIGN_CENTER);

//					g2.setFont(   );
//					g2.setFont( newFont );
					g2.drawString( strNucleotide, (float)newLabelPosition.getX(), (float)newLabelPosition.getY());
					g2.setColor( systemColor );
				}
			}
		}
		g2.setColor(currentColor);
		g2.setComposite(currentComposite);
		g2.setFont( currentFont );
	}
	
	private static Polygon getForward(RoundRectangle2D.Double sRect, RoundRectangle2D.Double eRect) {
		Polygon bxPolygon = new Polygon();
		
		bxPolygon.addPoint( (int)(sRect.getMinX()), (int)sRect.getMinY() );
		bxPolygon.addPoint( (int)(eRect.getMaxX()), (int)sRect.getMinY() );
		bxPolygon.addPoint( (int)(eRect.getMaxX() + 7), (int)sRect.getCenterY() );
		bxPolygon.addPoint( (int)(eRect.getMaxX()), (int)sRect.getMaxY() );
		bxPolygon.addPoint( (int)(sRect.getMinX()), (int)sRect.getMaxY() );
		
		return bxPolygon;
	}

	private static Polygon getBackward(RoundRectangle2D.Double sRect, RoundRectangle2D.Double eRect) {
		Polygon bxPolygon = new Polygon();
		
		bxPolygon.addPoint( (int)(sRect.getMinX() - 7), (int)sRect.getCenterY() );
		bxPolygon.addPoint( (int)(sRect.getMinX()), (int)sRect.getMinY() );
		bxPolygon.addPoint( (int)(eRect.getMaxX()), (int)sRect.getMinY() );
		bxPolygon.addPoint( (int)(eRect.getMaxX()), (int)sRect.getMaxY() );
		bxPolygon.addPoint( (int)(sRect.getMinX()), (int)sRect.getMaxY() );
		
		return bxPolygon;
	}
	
	private static Polygon getNormal(RoundRectangle2D.Double sRect, RoundRectangle2D.Double eRect) {
		Polygon bxPolygon = new Polygon();
		
		bxPolygon.addPoint( (int)(sRect.getMinX()), (int)sRect.getMaxY() );
		bxPolygon.addPoint( (int)(sRect.getMinX()), (int)sRect.getMinY() );
		bxPolygon.addPoint( (int)(eRect.getMaxX()), (int)sRect.getMinY() );
		bxPolygon.addPoint( (int)(eRect.getMaxX()), (int)sRect.getMaxY() );
		bxPolygon.addPoint( (int)(sRect.getMinX()), (int)sRect.getMaxY() );
		
		return bxPolygon;
	}
	
	private static Polygon getForward( Rectangle2D.Double rect ) {
		if( rect == null )	return null;

		Polygon bxPolygon = new Polygon();

		bxPolygon.addPoint( (int)(rect.getMinX()-2), (int)rect.getMinY() );
		bxPolygon.addPoint( (int)(rect.getMaxX()), (int)rect.getMinY() );
		bxPolygon.addPoint( (int)(rect.getMaxX() + 7), (int)rect.getCenterY() );
		bxPolygon.addPoint( (int)(rect.getMaxX()), (int)rect.getMaxY() );
		bxPolygon.addPoint( (int)(rect.getMinX()-2), (int)rect.getMaxY() );
		
		return bxPolygon;
	}

	private static Polygon getBackward(Rectangle2D.Double rect) {
		if( rect == null )	return null;

		Polygon bxPolygon = new Polygon();
		
		bxPolygon.addPoint( (int)(rect.getMinX() - 7), (int)rect.getCenterY() );
		bxPolygon.addPoint( (int)(rect.getMinX()), (int)rect.getMinY() );
		bxPolygon.addPoint( (int)(rect.getMaxX()+2), (int)rect.getMinY() );
		bxPolygon.addPoint( (int)(rect.getMaxX()+2), (int)rect.getMaxY() );
		bxPolygon.addPoint( (int)(rect.getMinX()), (int)rect.getMaxY() );
		
		return bxPolygon;
	}
	
	private void drawReadDirection( Graphics2D g2, AlignedReadSequence readSequence, int idx, int size ) {
		Color currentColor = g2.getColor();
		Composite currentComposite = g2.getComposite();

		Color color = new Color(235, 235, 235);
		Polygon bxPolygon = null;
		RoundRectangle2D.Double sRect = readSequence.getAlignedNucleotide(0).getBlock();
		RoundRectangle2D.Double eRect = readSequence.getAlignedNucleotide( readSequence.getSequence().size()-1 ).getBlock();

//		if( readSequence.getStrand().equals("-") ) {
//			if( idx == 0 )		bxPolygon = GeneralAlignedReadSequence.getBackward(sRect, eRect);
//			else				bxPolygon = GeneralAlignedReadSequence.getNormal(sRect, eRect);
//		}else {
//			if( idx == size-1 )	bxPolygon = GeneralAlignedReadSequence.getForward(sRect, eRect);
//			else				bxPolygon = GeneralAlignedReadSequence.getNormal(sRect, eRect);
//		}
		bxPolygon = GeneralAlignedReadSequence.getNormal(sRect, eRect);

		g2.setColor( color );
		g2.fill( bxPolygon );

		float alpha = 0.5f;
		int type = AlphaComposite.SRC_OVER; 
		AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
		g2.setComposite( composite );

		g2.setColor( Color.gray );
		g2.draw( bxPolygon );

		g2.setColor( currentColor );
		g2.setComposite( currentComposite );
	}

	private void drawReadBackground( Rectangle2D.Double visibleRect, Graphics2D g2 ) {
		Composite oldComposite = g2.getComposite();
		float alpha = 0.01f;
		int type = AlphaComposite.SRC_OVER; 
		AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
		g2.setComposite( composite );
		
		Rectangle2D.Double readRect = this.getReadBaseRect( visibleRect );
		
		Polygon bxPolygon = null;
		if( this.readObj.getReadQuality().getOrientation().equals("+") )	bxPolygon = GeneralAlignedReadSequence.getForward( readRect );
		else																bxPolygon = GeneralAlignedReadSequence.getBackward( readRect );

		if( bxPolygon != null ){
			g2.setColor( new Color(107, 187, 251) );
			g2.fill( readRect );

			alpha = 0.8f;
			composite = AlphaComposite.getInstance(type, alpha);
			g2.setComposite( composite );
			g2.setColor( new Color(97, 124, 162) );
			g2.draw( bxPolygon );
		}
		g2.setComposite( oldComposite );
	}

	private void initialize() {
		int ntIdx = 0;
		if( readObj instanceof GeneralReadObject ) {
			GeneralReadObject gro = (GeneralReadObject)readObj;
//			for(ReadFragmentByCigar cigar:gro.getMsvSamRecord().getCigarElements() ) {

			for( ReadObject read:gro.getRecordElements() ) {
//				AlignedReadSequence readAlignedToGenomeSequence	= new AlignedReadSequence( gro.getReadQuality().getOrientation(), cigar.getStart(), cigar.getEnd() );
//
//				this.createDrawingObjects( cigar, gro.getStrand(), readAlignedToGenomeSequence, this.y, ntIdx );				
//
//				this.blocks.add( readAlignedToGenomeSequence );
//				ntIdx += cigar.getReadSeq().length();
				
				AlignedReadSequence readAlignedToGenomeSequence	= new AlignedReadSequence( gro.getReadQuality().getOrientation(), (int)read.getStartPosition(), (int)read.getEndPosition() );

//				this.createDrawingObjects( cigar, gro.getStrand(), readAlignedToGenomeSequence, this.y, ntIdx );
				this.createDrawingObjects( read, readAlignedToGenomeSequence, y, ntIdx );

				this.blocks.add( readAlignedToGenomeSequence );
				ntIdx += read.getSequence().size();
			}
		}else {
			AlignedReadSequence readAlignedToGenomeSequence	= new AlignedReadSequence( readObj.getReadQuality().getOrientation(), (int)readObj.getStartPosition(), (int)readObj.getEndPosition() );
			boundary = this.createDrawingObjects( readObj, readAlignedToGenomeSequence, y, ntIdx);
			this.blocks.add( readAlignedToGenomeSequence );
		}
	}

	private void getInBlockNucleotideWithRectangle(ReadObject read, AlignedReadSequence readAlignedToGenomeSequence, double y, int ntIdx) {
		List<NucleotideObject> nucleotide = read.getSequence();				// read
		
		final int ROUND_OFFSET = 0;
		GenomeReferenceObject ref = this.model.getReferenceSequenceObject();

		for( NucleotideObject nObj:nucleotide ) {
			int originalPos = nObj.getPosition();

			if( read.getReadQuality().getBasePhredQuality() == null )				nObj.setBaseQuality( (byte)0 );
			else if( read.getReadQuality().getBasePhredQuality().length == 0 )		nObj.setBaseQuality( (byte)0 );
			else if( read.getReadQuality().getBasePhredQuality().length < ntIdx )	nObj.setBaseQuality( (byte)0 );
			else																	nObj.setBaseQuality( read.getReadQuality().getBasePhredQuality()[ntIdx] );

			int newPos = SwingUtilities.getGenomePos( ref.getSequence(), originalPos );

			if( newPos >= 0 && newPos < this.referenceResult.size() ) {
				AlignedNucleotide refNT = this.referenceResult.get( newPos );
	
				RoundRectangle2D.Double nRect = refNT.getBlock();
				
				RoundRectangle2D.Double rect = new RoundRectangle2D.Double( nRect.getMinX(), y, this.config.getBlockWidth(), this.config.getBlockHeight(), ROUND_OFFSET, ROUND_OFFSET );
				
				AlignedNucleotide anObject = new AlignedNucleotide( nObj, rect );
				
				if( nObj.getNucleotideType().equals( refNT.getNucleotideType() ) )							anObject.setDifferentWithRef( false );
				else if( nObj.getNucleotideType().equals("U") && refNT.getNucleotideType().equals("T") )	anObject.setDifferentWithRef( false );
				else if( nObj.getNucleotideType().equals("T") && refNT.getNucleotideType().equals("U") )	anObject.setDifferentWithRef( false );
				else																						{
					anObject.setDifferentWithRef( true );
					this.mismatchCount++;
				}

				readAlignedToGenomeSequence.add( anObject );
				this.seqMap.put( nObj.getPosition(), nObj );
			}
		}
	}
	
	private Rectangle2D.Double createDrawingObjectsReverse( ReadObject read, AlignedReadSequence readAlignedToGenomeSequence, double y, int ntIdx ) {
		GenomeReferenceObject ref = this.model.getReferenceSequenceObject();
	
		// Loop NT element in read
		if( ref.getEndPosition() < readAlignedToGenomeSequence.getStart() ) {
			readAlignedToGenomeSequence.setType(AlignedReadSequence.OUT_LEFT_BLOCK );
		}else if( ref.getStartPosition() > readAlignedToGenomeSequence.getEnd() ) {
			readAlignedToGenomeSequence.setType(AlignedReadSequence.OUT_RIGHT_BLOCK );
		}else {
			this.getInBlockNucleotideWithRectangle(read, readAlignedToGenomeSequence, y, ntIdx);
	
			if( readAlignedToGenomeSequence.isDrawable() )	readAlignedToGenomeSequence.setType(AlignedReadSequence.IN_BLOCK);
			else											readAlignedToGenomeSequence.setType(AlignedReadSequence.OUT_LEFT_BLOCK);
		}
			
		return readAlignedToGenomeSequence.getBoundary();
	}
	
	private Rectangle2D.Double createDrawingObjectsNormal( ReadObject read, AlignedReadSequence readAlignedToGenomeSequence, double y, int ntIdx ) {
		GenomeReferenceObject ref = this.model.getReferenceSequenceObject();
	
		// Loop NT element in read
		if( ref.getStartPosition() > readAlignedToGenomeSequence.getEnd() ) {
			readAlignedToGenomeSequence.setType(AlignedReadSequence.OUT_LEFT_BLOCK );
		}else if( ref.getEndPosition() < readAlignedToGenomeSequence.getStart() ) {
			readAlignedToGenomeSequence.setType(AlignedReadSequence.OUT_RIGHT_BLOCK );
		}else {
			this.getInBlockNucleotideWithRectangle(read, readAlignedToGenomeSequence, y, ntIdx);
	
			if( readAlignedToGenomeSequence.isDrawable() )	readAlignedToGenomeSequence.setType(AlignedReadSequence.IN_BLOCK);
			else											readAlignedToGenomeSequence.setType(AlignedReadSequence.OUT_LEFT_BLOCK);
		}
	
		return readAlignedToGenomeSequence.getBoundary();
	}

	private Rectangle2D.Double createDrawingObjects( ReadObject read, AlignedReadSequence readAlignedToGenomeSequence, double y, int ntIdx ) {
		if( read.getStrand() == '+' )
			return this.createDrawingObjectsNormal(read, readAlignedToGenomeSequence, y, ntIdx);
		return this.createDrawingObjectsReverse(read, readAlignedToGenomeSequence, y, ntIdx);
	}
	
	private Color getBackgroundColorByNucleotide( String nucleotide, boolean isDifferentWithRef ) {
		if( isDifferentWithRef ) {
//			return config.getMisBackgroundColor();
			if( nucleotide.toUpperCase().equals("A") )
				return config.getMisABackgroundColor();
			else if( nucleotide.toUpperCase().equals("T") || nucleotide.toUpperCase().equals("U") )
				return config.getMisTBackgroundColor();
			else if( nucleotide.toUpperCase().equals("G") )
				return config.getMisGBackgroundColor();
			else if( nucleotide.toUpperCase().equals("C") )
				return config.getMisCBackgroundColor();
			return config.getMisXBackgroundColor();
		}else {
			if( nucleotide.toUpperCase().equals("A") )
				return config.getAdenosineColor();
			else if( nucleotide.toUpperCase().equals("T") || nucleotide.toUpperCase().equals("U") )
				return config.getThymidineColor();
			else if( nucleotide.toUpperCase().equals("G") )
				return config.getGuanosineColor();
			else if( nucleotide.toUpperCase().equals("C") )
				return config.getCytidineColor();
			return config.getUnkowonNucleotideColor();
		}
	}

	private Color getForegroundColorByNucleotide( boolean isDifferentWithRef ) {
		if( isDifferentWithRef ) {
			return config.getMisForegroundColor();
		}
		return Color.black;
	}

	private Rectangle2D.Double getReadBaseRect( Rectangle2D.Double visibleRect ) {
		if( this.blocks.size() == 1 ) {
			if( this.blocks.get(0).getType() == AlignedReadSequence.IN_BLOCK )	return this.blocks.get(0).getBoundary();
			else																return null;
		}else {
			AlignedReadSequence first	= this.blocks.get(0);
			AlignedReadSequence second	= this.blocks.get( this.blocks.size() - 1 );
			
			double leftLimit = this.config.getOffset() + this.config.getMargin();

			if( first.getType()==AlignedReadSequence.OUT_LEFT_BLOCK && second.getType()==AlignedReadSequence.IN_BLOCK )	{
				Rectangle2D.Double sr = second.getBoundary();
				return new Rectangle2D.Double( leftLimit, sr.getMinY(), sr.getMaxX() - leftLimit, sr.getHeight() );
			}else if( first.getType()==AlignedReadSequence.IN_BLOCK && second.getType()==AlignedReadSequence.OUT_LEFT_BLOCK )	{
				Rectangle2D.Double fr = first.getBoundary();
				return new Rectangle2D.Double( leftLimit, fr.getMinY(), fr.getMaxX() - leftLimit, fr.getHeight() );
			}else if( first.getType()==AlignedReadSequence.IN_BLOCK && second.getType()==AlignedReadSequence.OUT_RIGHT_BLOCK ) {
				Rectangle2D.Double fr = first.getBoundary();
				return new Rectangle2D.Double( fr.getMinX(), fr.getMinY(), visibleRect.getMaxX() - fr.getMinX(), fr.getHeight() );
			}else if( first.getType()==AlignedReadSequence.OUT_RIGHT_BLOCK && second.getType()==AlignedReadSequence.IN_BLOCK ){
				Rectangle2D.Double fr = second.getBoundary();
				return new Rectangle2D.Double( fr.getMinX(), fr.getMinY(), visibleRect.getMaxX() - fr.getMinX(), fr.getHeight() );
			}else if( first.getType()==AlignedReadSequence.IN_BLOCK && second.getType()==AlignedReadSequence.IN_BLOCK ) {
				Rectangle2D.Double fr = first.getBoundary();
				Rectangle2D.Double sr = second.getBoundary();

				if( fr.getMaxX() > sr.getMinX() )
					return new Rectangle2D.Double( sr.getMinX(), sr.getMinY(), fr.getMaxX() - sr.getMinX(), fr.getHeight() );
				else
					return new Rectangle2D.Double( fr.getMinX(), fr.getMinY(), sr.getMaxX() - fr.getMinX(), fr.getHeight() );
			}else if( first.getType()==AlignedReadSequence.OUT_LEFT_BLOCK && second.getType()==AlignedReadSequence.OUT_RIGHT_BLOCK ) {
				return new Rectangle2D.Double( leftLimit, this.getCurrentRecordObj(visibleRect).getMinY(), visibleRect.getMaxX() - leftLimit, this.config.getBlockHeight() );
			}else if( first.getType()==AlignedReadSequence.OUT_RIGHT_BLOCK && second.getType()==AlignedReadSequence.OUT_LEFT_BLOCK ) {
				return new Rectangle2D.Double( leftLimit, this.getCurrentRecordObj(visibleRect).getMinY(), visibleRect.getMaxX() - leftLimit, this.config.getBlockHeight() );
			}
		}
		return null;
	}
}
