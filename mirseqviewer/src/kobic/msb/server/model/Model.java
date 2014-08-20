package kobic.msb.server.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import net.sf.samtools.CigarElement;

import org.apache.http.client.ClientProtocolException;
//
//import com.esotericsoftware.kryo.Kryo;
//import com.esotericsoftware.kryo.io.Output;

import kobic.com.util.Utilities;
import kobic.com.vienna.RNAFold;
import kobic.com.vienna.RNAPlot;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.SwingConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.db.sqlite.vo.HairpinVO;
import kobic.msb.db.sqlite.vo.MatureVO;
import kobic.msb.server.model.jaxb.Msb.Project;
import kobic.msb.server.model.jaxb.Msb.Project.MiRnaList.MiRna;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.obj.GeneralReadObject;
import kobic.msb.server.obj.GenomeReferenceObject;
import kobic.msb.server.obj.HairpinSequenceObject;
import kobic.msb.server.obj.NucleotideObject;
import kobic.msb.server.obj.ReadFragmentByCigar;
import kobic.msb.server.obj.ReadObject;
import kobic.msb.server.obj.RnaSecondaryStructureObj;
import kobic.msb.server.obj.SequenceObject;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;

@SuppressWarnings("unused")
public class Model implements java.io.Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GenomeReferenceObject						referenceSequenceObject;
	private HairpinSequenceObject						prematureSequenceObject;
	private String										mirid;
	private char										strand;
	private String										chr;

	private MatrixObj									readHeatMap;
	private Project										projectInfo;
	private MiRna										mirna;
	private boolean										isNormalized;
	
	private List<MatureVO>								voList;
	private HairpinVO									hairpinVo;
	
	
//	private static Kryo kryo = new Kryo();
	
//	private MsbSortModel								sortModel;
//	private MsbFilterModel								filterModel;

	private MSBReadCountTableColumnStructureModel		currentRCTColumnStructureModel;
	
	private int											SORT_DIRECTION;

	public Model() {
		this.SORT_DIRECTION		= SwingConst.SORT_DESC;
		this.isNormalized		= false;
	}

	public Model( Project projectInfo, MiRna mirna ) {
		this();

		this.mirna							= mirna;				// MiRNA Information @ XML file
		this.projectInfo					= projectInfo;			// Project Information @ XML file
		
//		this.sortModel						= new MsbSortModel();
//		this.filterModel					= new MsbFilterModel();
//
//		{
//			/*******************************************************************************
//			 * filter such as allowing mis-match count and reverse reads removing is applied 
//			 */
//			this.filterModel.addModel(0, MsbFilterModel.DEFAULT, "mis match", "<=", "2");
//			this.filterModel.addModel(1, MsbFilterModel.AND, "reverse reads", "remove", "true");
//		}

		this.currentRCTColumnStructureModel	= new MSBReadCountTableColumnStructureModel( this.projectInfo );

		this.readHeatMap					= new MatrixObj( this );
		
		this.initializeVariables( this.projectInfo );

		this.currentRCTColumnStructureModel.addObserver( this.readHeatMap );
	}

	public static boolean isExistPremature( String id, String version ) {
//		SequenceObject reference		= MsbEngine._db.getMicroRnaHairpinByMirid( id );
		HairpinSequenceObject premature		= MsbEngine.getInstance().getMiRBaseMap().get(version).getMicroRnaHairpinByMirid( id );
		if( premature != null )	return true;
		return false;
	}

	private void initializeVariables( Project project ) {
		ProjectMapItem item	= MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( project.getProjectName() );

		this.mirid							= this.mirna.getMirid();
		this.chr							= this.mirna.getChromosome();
		
		this.voList							= MsbEngine.getInstance().getMiRBaseMap().get( item.getMiRBAseVersion() ).getMicroRnaMaturesByMirid( this.mirid );
		
		this.hairpinVo						= MsbEngine.getInstance().getMiRBaseMap().get( item.getMiRBAseVersion() ).getMicroRnaHairpinByMirid2( this.mirid );

		if ( !this.mirid.startsWith( JMsbSysConst.NOVEL_MICRO_RNA) )
			this.prematureSequenceObject		= MsbEngine.getInstance().getMiRBaseMap().get( item.getMiRBAseVersion() ).getMicroRnaHairpinByMirid( this.mirid );

//		this.referenceSequenceObject		= this.getReferenceGenomeSequenceObject( this.getProjectMapItem().getOrganism(), this.prematureSequenceObject );
//
//		this.strand							= this.referenceSequenceObject.getStrand();
	}

	public void setPrematureSequenceObject( HairpinSequenceObject prematureSequenceObject ) {
		this.prematureSequenceObject = prematureSequenceObject;
	}
	
	public void initReferenceGenomeSequenceAndSomthing() {
		this.referenceSequenceObject		= this.getInitReferenceGenomeSequenceObject( this.getProjectMapItem().getOrganism(), this.prematureSequenceObject );
		this.strand							= this.referenceSequenceObject.getStrand();
	}
	
	public void reInitReferenceGenomeSequenceAndSomthing(int offset) {
		this.referenceSequenceObject		= this.getReferenceGenomeSequenceObject( this.getProjectMapItem().getOrganism(), offset );
	}

	public void reInitReferenceGenomeSequenceByShift(int offset, int direction) {
		this.referenceSequenceObject		= this.getReferenceGenomeSequenceObjectByShift( this.getProjectMapItem().getOrganism(), direction, offset );
	}

	private GenomeReferenceObject getReferenceGenomeSequenceObject( String orgsm, int byOffset ) {
		int _1_half = byOffset / 2;
		int _2_half = byOffset - _1_half;
		
		String organism	= MsbEngine.getInstance().getOrganismMap().get( orgsm );
		String chr		= this.getReferenceSequenceObject().getChromosome();
		char strand		= this.getReferenceSequenceObject().getStrand();
//		char strand		= '+';
		int start		= (int)this.getReferenceSequenceObject().getStartPosition() + _1_half;
		int end			= (int)this.getReferenceSequenceObject().getEndPosition() - _2_half;

//		if( start > end || (end-start) > (this.prematureSequenceObject.getLength() + 100) )	return this.referenceSequenceObject;

		String version = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.projectInfo.getProjectName() ).getMiRBAseVersion();

		try {
			return this.getRealCommonReferenceGenomeSequenceObjectFromServer(organism, chr, start, end, strand, version);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "It can not connect to Server", "Network problem", JOptionPane.ERROR_MESSAGE );
			MsbEngine.logger.error("Error", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error("Error", e);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error("Error", e);
		} catch(Exception e) {
			MsbEngine.logger.error("Error", e);
		}
		return null;
	}

	private GenomeReferenceObject getReferenceGenomeSequenceObjectByShift( String orgsm, int direction, int byOffset ) {
		String organism	= MsbEngine.getInstance().getOrganismMap().get( orgsm );
		String chr		= this.getReferenceSequenceObject().getChromosome();
		char strand		= this.getReferenceSequenceObject().getStrand();

//		if( strand == '+' ) {
			if( direction == JMsbSysConst.SHIFT_TO_UPSTREAM ) {
				byOffset = -1 * byOffset;
			}
//		}else {
//			if( direction == JMsbSysConst.SHIFT_TO_DOWNSTREAM ) {
//				byOffset = -1 * byOffset;
//			}
//		}

		int start		= (int)this.getReferenceSequenceObject().getStartPosition() + byOffset;
		int end			= (int)this.getReferenceSequenceObject().getEndPosition()	+ byOffset;

		String version = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.projectInfo.getProjectName() ).getMiRBAseVersion();

		try {
			return this.getRealCommonReferenceGenomeSequenceObjectFromServer(organism, chr, start, end, strand, version);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e) {
			MsbEngine.logger.error("Error", e);
		}
		return null;
	}
	
	private GenomeReferenceObject getRealCommonReferenceGenomeSequenceObjectFromServer( String organism, String chr, int start, int end, char strand, String version ) 
	throws ClientProtocolException, IOException, URISyntaxException, Exception{
		GenomeReferenceObject genome = null;

		try {
			String sequence = MsbEngine.getInstance().getHttpRequester().getReferenceSequence( organism, chr, start, end, strand, version );

			// If there are no reference information on the KOBIC server
			// Precursor sequence is going to be reference
			if( sequence.isEmpty() ) {
				chr = this.prematureSequenceObject.getChromosome();
				start = Long.valueOf( this.prematureSequenceObject.getStartPosition() ).intValue();
				end = Long.valueOf( this.prematureSequenceObject.getEndPosition() ).intValue();
				strand = this.prematureSequenceObject.getStrand();
				sequence = this.prematureSequenceObject.getSequenceByString();
			}

			if( this.referenceSequenceObject == null ) {
				genome = new GenomeReferenceObject();
				genome.setChromosome(chr);
				genome.setStartPosition(start);
				genome.setEndPosition(end);
				genome.setStrand(strand);
				genome.setSequence( sequence );
			}else {
				genome = this.referenceSequenceObject;
				genome.setChromosome(chr);
				genome.setStartPosition(start);
				genome.setEndPosition(end);
				genome.setStrand(strand);
				genome.setSequence( sequence );
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
//			JOptionPane.showMessageDialog(null, e ,"Network problem", JOptionPane.WARNING_MESSAGE);
//			MsbEngine.logger.error("Error", e);
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			MsbEngine.logger.error("Error", e);
			throw e;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
//			MsbEngine.logger.error("Error", e);
			throw e;
		}

		return genome;
	}

	private GenomeReferenceObject getInitReferenceGenomeSequenceObject( String orgsm, HairpinSequenceObject prematureSequence ) {
		String organism	= MsbEngine.getInstance().getOrganismMap().get( orgsm );
		String chr		= prematureSequence.getChromosome();
		char strand		= prematureSequence.getStrand();
//		char strand		= '+';
		int start		= (int)prematureSequence.getStartPosition() - JMsbSysConst.EXTEND_SEQ_LENGTH;
		int end			= (int)prematureSequence.getEndPosition() + JMsbSysConst.EXTEND_SEQ_LENGTH;
		
		String version = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.projectInfo.getProjectName() ).getMiRBAseVersion();

		try {
			return this.getRealCommonReferenceGenomeSequenceObjectFromServer( organism, chr, start, end, strand, version );
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e ,"Network problem", JOptionPane.WARNING_MESSAGE);
			MsbEngine.logger.error("Error", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error("Error", e);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			MsbEngine.logger.error("Error", e);
		} catch(Exception e) {
			MsbEngine.logger.error("Error", e);
		}
		return null;
	}

	private static double[][] getRnaSecondaryStructureData( String sequence, char strand ) {
		RNAPlot plot	= new RNAPlot();
		RNAFold folding = new RNAFold();
//		String structure = folding.makeStructure( this.prematureSequenceObject.getSequenceByString() );
//		String structure = folding.makeStructure( strand=='+'?sequence:Utilities.reverseSequence(sequence) );
		String structure = folding.makeStructure( sequence );

//		return plot.getCoordinateData( this.prematureSequenceObject.getSequenceByString(), structure );
		return plot.getCoordinateData( sequence, structure );
	}

//	public static RnaSecondaryStructureObj makeRnaSecondaryStructureObj( String name, String[] arv ) {
//		if( arv == null )	return null;
//
//		int start	= Integer.parseInt( arv[0] );
//		int end		= Integer.parseInt( arv[1] );
//
//		return Model.makeRnaSecondaryStructureObj(name, "", start, end);
//	}
	
	public static RnaSecondaryStructureObj makeRnaSecondaryStructureObj( String name, String type, int start, int end, ProjectConfiguration config ) {
		if( type.equals( SwingConst.MiRNA_2ND_STRUCTURE_5PMOR ) )
//			return new RnaSecondaryStructureObj(name, type, start, end, new Color(0x2EACFF));
			return new RnaSecondaryStructureObj(name, type, start, end, config.get_5p_upstream_color());
		else if( type.equals( SwingConst.MiRNA_2ND_STRUCTURE_5P ) )
//			return new RnaSecondaryStructureObj(name, type, start, end, Color.orange);
			return new RnaSecondaryStructureObj(name, type, start, end, config.get_5p_mature_color());
		else if( type.equals( SwingConst.MiRNA_2ND_STRUCTURE_LOOP ) )
//			return new RnaSecondaryStructureObj(name, type, start, end, new Color(0xB265DC));
			return new RnaSecondaryStructureObj(name, type, start, end, config.get_loop_color() );
		else if( type.equals( SwingConst.MiRNA_2ND_STRUCTURE_3P ) )
//			return new RnaSecondaryStructureObj(name, type, start, end, Color.green);
			return new RnaSecondaryStructureObj(name, type, start, end, config.get_3p_mature_color());
		else if( type.equals( SwingConst.MiRNA_2ND_STRUCTURE_3PMOR ) )
//			return new RnaSecondaryStructureObj(name, type, start, end, Color.blue);
			return new RnaSecondaryStructureObj(name, type, start, end, config.get_3p_downstream_color());
//		else if( type.equals( SwingConst.MiRNA_2ND_STRUCTURE_HAIRPIN ) )
////			return new RnaSecondaryStructureObj(name, type, start, end, Color.pink);
//			return new RnaSecondaryStructureObj(name, type, start, end, config.get_loop_color());
		else if( type.equals( SwingConst.MiRNA_2ND_STRUCTURE_UNIQUE ) )
			return new RnaSecondaryStructureObj(name, type, start, end, Color.yellow);
		else if( type.equals( SwingConst.MiRNA_2ND_STRUCTURE_ORDER ) )
			return new RnaSecondaryStructureObj(name, type, start, end, Color.cyan);
		return null;
	}

	public List<Group> getFullGroupList() {
		return this.projectInfo.getSamples().getGroup();
	}

	public static List<MatureVO> mergeOverlappedMatures( List<MatureVO> matures ) {
		List<MatureVO> newList = new ArrayList<MatureVO>();

		for(Iterator<MatureVO> iter = matures.iterator(); iter.hasNext();) {
			if( newList.size() == 0 )	newList.add( iter.next() );
			else {
				MatureVO vo = iter.next();
	
				for(int i=0; i<newList.size(); i++) {
					if( vo.isOverlaped( newList.get(i) ) ) {
						newList.get(i).setStart( Math.min( vo.getStart(), newList.get(i).getStart()) );
						newList.get(i).setEnd( Math.min( vo.getEnd(), newList.get(i).getEnd()) );
					}else {
						newList.add( vo );
					}
				}
			}
		}
		return newList;
	}

	public static List<RnaSecondaryStructureObj> getSeondaryStructuresByHairpinInfo( HairpinVO hairpinVo, List<MatureVO> voList, ProjectConfiguration config ) {
		List<MatureVO>	matureList	= Model.mergeOverlappedMatures( voList );

		List<RnaSecondaryStructureObj> rnaSSOList = new ArrayList<RnaSecondaryStructureObj>();

		for(Iterator<MatureVO> iter = matureList.iterator(); iter.hasNext();) {
			MatureVO vo = iter.next();

			int start	= vo.getStart();
			int end		= vo.getEnd();

			if( hairpinVo.getStrand().equals("+") ) { 
				if( hairpinVo.getStart() != null && !hairpinVo.getStart().isEmpty() )	start	+= Integer.parseInt( hairpinVo.getStart() ) - 1;
				if( hairpinVo.getEnd() != null && !hairpinVo.getEnd().isEmpty() )		end		+= Integer.parseInt( hairpinVo.getStart() ) - 1;
			}else {
				if( hairpinVo.getEnd() != null && !hairpinVo.getEnd().isEmpty() ) {
					start	= Integer.parseInt( hairpinVo.getEnd() ) - vo.getEnd() + 1;
					end		= Integer.parseInt( hairpinVo.getEnd() ) - vo.getStart() + 1;
				}
//				if( hairpinVo.getStart() != null && !hairpinVo.getStart().isEmpty() )	start	+= Integer.parseInt( hairpinVo.getStart() ) - 1;
//				if( hairpinVo.getEnd() != null && !hairpinVo.getEnd().isEmpty() )		end		+= Integer.parseInt( hairpinVo.getStart() ) - 1;
			}

			String name = vo.getMirid();
			String type = "";
			
			if( matureList.size() == 1 ) {
				if( name.contains("5p") )		type = SwingConst.MiRNA_2ND_STRUCTURE_5P;
				else if( name.contains("3p") )	type = SwingConst.MiRNA_2ND_STRUCTURE_3P;
				else							type = SwingConst.MiRNA_2ND_STRUCTURE_UNIQUE;
			}else if( matureList.size() >= 2 ) {
				if( name.contains("5p") )		type = SwingConst.MiRNA_2ND_STRUCTURE_5P;
				else if( name.contains("3p") )	type = SwingConst.MiRNA_2ND_STRUCTURE_3P;
				else							type = SwingConst.MiRNA_2ND_STRUCTURE_ORDER;
			}

			RnaSecondaryStructureObj rsso = Model.makeRnaSecondaryStructureObj( name, type, start, end, config );
			rnaSSOList.add( rsso );
		}

		return rnaSSOList;
	}
	
	public static List<RnaSecondaryStructureObj> getSeondaryStructuresByHairpinInfo222( String hairpinId, String version, ProjectConfiguration config ) {
		HairpinVO		hairpinVo	= MsbEngine.getInstance().getMiRBaseMap().get( version ).getMicroRnaHairpinByMirid2( hairpinId );
		List<MatureVO> voList = MsbEngine.getInstance().getMiRBaseMap().get( version ).getMicroRnaMaturesByMirid( hairpinId ); 

		return getSeondaryStructuresByHairpinInfo( hairpinVo, voList, config );
	}

//	public static List<RnaSecondaryStructureObj> getSeondaryStructures( String mirid, String version ) {
////		RnaSecondaryStructureInfo info = MsbEngine._db.select2ndStructures( mirid );
//		RnaSecondaryStructureInfo info = MsbEngine.getInstance().getMiRBaseMap().get(version).select2ndStructures( mirid );
//		
//	
//		List<RnaSecondaryStructureObj> rnaSSOList = new ArrayList<RnaSecondaryStructureObj>();
//
//		if( info.getStrand() == null )	info.setStrand("+");
//		
//		if( info.getStrand().equals( "+" ) ) {
//			RnaSecondaryStructureObj obj5moR	= makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_5PMOR,	Utilities.getSplitStringByComma( info.get_5moR()	) );
//			RnaSecondaryStructureObj obj5P		= makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_5P,		Utilities.getSplitStringByComma( info.get_5p()	) );
//			RnaSecondaryStructureObj obj3P		= makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_3P,		Utilities.getSplitStringByComma( info.get_3p()	) );
//			RnaSecondaryStructureObj obj3moR	= makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_3PMOR,	Utilities.getSplitStringByComma( info.get_3moR()	) );
//			RnaSecondaryStructureObj objLoop	= makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_LOOP,	Utilities.getSplitStringByComma( info.getLoop()	) );
//			if( obj5P == null || obj3P == null )
//				objLoop	= makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_HAIRPIN,		Utilities.getSplitStringByComma( info.getLoop()	) );
//			
//			if( obj5moR	!= null )	rnaSSOList.add( obj5moR );
//			if( obj5P	!= null )	rnaSSOList.add( obj5P );
//			if( objLoop	!= null )	rnaSSOList.add( objLoop );
//			if( obj3P	!= null )	rnaSSOList.add( obj3P );
//			if( obj3moR	!= null )	rnaSSOList.add( obj3moR );
//		}else {
//			RnaSecondaryStructureObj obj5moR	= makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_5PMOR,	Utilities.getSplitStringByComma( info.get_3moR()	) );
//			RnaSecondaryStructureObj obj5P		= makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_5P,		Utilities.getSplitStringByComma( info.get_3p()	) );
//			RnaSecondaryStructureObj obj3P		= makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_3P,		Utilities.getSplitStringByComma( info.get_5p()	) );
//			RnaSecondaryStructureObj obj3moR	= makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_3PMOR,	Utilities.getSplitStringByComma( info.get_5moR()	) );
//			RnaSecondaryStructureObj objLoop	= makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_LOOP,	Utilities.getSplitStringByComma( info.getLoop()	) );
//
//			if( obj5P == null || obj3P == null )
//				objLoop	= makeRnaSecondaryStructureObj( SwingConst.MiRNA_2ND_STRUCTURE_HAIRPIN,		Utilities.getSplitStringByComma( info.getLoop()	) );
//			
//			if( obj5moR	!= null )	rnaSSOList.add( obj5moR );
//			if( obj5P	!= null )	rnaSSOList.add( obj5P );
//			if( objLoop	!= null )	rnaSSOList.add( objLoop );
//			if( obj3P	!= null )	rnaSSOList.add( obj3P );
//			if( obj3moR	!= null )	rnaSSOList.add( obj3moR );
//		}
//
//		return rnaSSOList;
//	}
	
//	private static RnaSecondaryStructureObj getStructure( List<RnaSecondaryStructureObj> list, String type ) {
//		if( list != null ) {
//			for(int i=0; i<list.size(); i++) {
//	//			if( list.get(i).getName().equals(type) )
//	//				return list.get(i);
//				if( list.get(i).getType().equals(type))
//					return list.get(i);
//			}
//		}
//		return null;
//	}
	
	public Color getColorFrom(NucleotideObject nucleic, List<RnaSecondaryStructureObj> secStructureList) {
		if( secStructureList != null ) {
			for(int i=0; i<secStructureList.size(); i++) {
				RnaSecondaryStructureObj rso = secStructureList.get(i);
	
				if( nucleic.getPosition() >= rso.getStart() && nucleic.getPosition() <= rso.getEnd() )
					return rso.getColor();
			}
		}
		return new Color(210, 210, 210);
	}

	public SequenceObject getDemoStructureSequence( String version ) {
		SequenceObject refObj			= new HairpinSequenceObject();
		List<NucleotideObject> list 	= new ArrayList<NucleotideObject>();
		double[][] coordinate			= Model.getRnaSecondaryStructureData( this.getPrematureSequenceObject().getSequenceByString(), this.getPrematureSequenceObject().getStrand() );
//		double[][] coordinate			= Model.getRnaSecondaryStructureData( this.getReferenceSequenceObject().getSequenceByString() );

		String projectName = this.projectInfo.getProjectName();
		ProjectConfiguration config = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName ).getProjectConfiguration();

		List<RnaSecondaryStructureObj> secStructureList = null;
		if( !this.isNovel() ) {
//			secStructureList = Model.getSeondaryStructuresByHairpinInfo( this.mirid, version, config );
			secStructureList = Model.getSeondaryStructuresByHairpinInfo( this.getHairpinVo(), this.voList, config );
		}

//		RnaSecondaryStructureObj _5mature = Model.getStructure( secStructureList, SwingConst.MiRNA_2ND_STRUCTURE_5P );
//		RnaSecondaryStructureObj _3mature = Model.getStructure( secStructureList, SwingConst.MiRNA_2ND_STRUCTURE_3P );

//		long diff = this.prematureSequenceObject.getStartPosition();
//		if( this.strand == '-' )
//			diff = this.prematureSequenceObject.getEndPosition();

		Iterator<NucleotideObject> iter = this.prematureSequenceObject.getSequence().iterator();
//		Iterator<NucleotideObject> iter = this.referenceSequenceObject.getSequence().iterator();

		int i = 0;
		while( iter.hasNext() ) {
			NucleotideObject nucleic = iter.next();

			nucleic.setColor( this.getColorFrom(nucleic, secStructureList) );
//		while( iter.hasNext() ) {
//			NucleotideObject nucleic = iter.next();
//			if( this.strand == '-' ) {
//				if( _5mature != null && _3mature != null ) {
//					long start1 = (diff - (int)_5mature.getEnd())-1;
//					long end1	= (diff - (int)_5mature.getStart());
//					
//					long start2 = (diff - (int)_3mature.getEnd())-1;
//					long end2	= (diff - (int)_3mature.getStart());
//					
//					if( i >= start1 && i < end1 )			nucleic.setColor( Color.orange 		);
//					else if( i >= start2 && i < end2 ) 		nucleic.setColor( new Color(0, 255, 0)	);
//					else 									nucleic.setColor( new Color(210, 210, 210)	);
//				}else if( _5mature == null && _3mature != null ) {
//					long start2	= (diff - (int)_3mature.getEnd())-1;
//					long end2	= (diff - (int)_3mature.getStart());
//
//					if( i >= start2 && i < end2 )			nucleic.setColor( new Color(0, 255, 0) 		);
//					else 									nucleic.setColor( new Color(210, 210, 210)	);
//				}else if( _5mature != null && _3mature == null ) {
//					long start1	= (diff - (int)_5mature.getEnd())-1;
//					long end1	= (diff - (int)_5mature.getStart());
//					
//					if( i >= start1 && i < end1 )			nucleic.setColor( Color.orange 		);
//					else 									nucleic.setColor( new Color(210, 210, 210)	);
//				}else {
//					nucleic.setColor( new Color(210, 210, 210)	);
//				}
//			}else {
//				if( _5mature != null && _3mature != null ) {
//					long start1	= ((int)_5mature.getStart()  - diff);
//					long end1	= ((int)_5mature.getEnd() - diff) + 1;
//					
//					long start2	= ((int)_3mature.getStart() - diff);
//					long end2	= ((int)_3mature.getEnd() - diff)+1;
//					
//					if( i >= start1 && i < end1 )			nucleic.setColor( Color.orange 		);
//					else if( i >= start2 && i < end2 ) 		nucleic.setColor( new Color(0, 255, 0)	);
//					else 									nucleic.setColor( new Color(210, 210, 210)	);
//				}else if( _5mature == null && _3mature != null ) {
//					long start2	= ((int)_3mature.getStart() - diff);
//					long end2	= ((int)_3mature.getEnd() - diff)+1;
//					
//					if( i >= start2 && i < end2 ) 		nucleic.setColor( new Color(0, 255, 0)	);
//					else 									nucleic.setColor( new Color(210, 210, 210)	);
//				}else if( _5mature != null && _3mature == null ) {
//					long start1	= ((int)_5mature.getStart()  - diff);
//					long end1	= ((int)_5mature.getEnd() - diff) + 1;
//					
//					if( i >= start1 && i < end1 )			nucleic.setColor( Color.orange 		);
//					else 									nucleic.setColor( new Color(210, 210, 210)	);
//				}else {
//					nucleic.setColor( new Color(210, 210, 210)	);
//				}
//			}

			nucleic.setCoordinate( new Point2D.Double(coordinate[i][0], coordinate[i][1]) );
			list.add( nucleic );
			i++;
		}
		secStructureList = null;
		refObj.setStartPosition( (int)this.prematureSequenceObject.getStartPosition() );
		refObj.setEndPosition( (int)this.prematureSequenceObject.getEndPosition() );

		refObj.setSequence( list );
		
		return refObj;
	}
	
	public long[] getHistogramBinVector() {
		return this.getHistogramBin();
	}

	private long[] getHistogramBin() {
		int length = this.referenceSequenceObject.getSequence().size();

		long[] histogramBin = new long[ length ];
		List<ReadObject> readList = this.getReadList();

		if( readList != null ) {
			for( ReadObject read:readList ) {
				if( read instanceof GeneralReadObject ) {
					GeneralReadObject gro = (GeneralReadObject)read;

//					for( ReadFragmentByCigar cigar : gro.getMsvSamRecord().getCigarElements() ) {
//						ReadObject tro = new ReadObject( cigar.getStart(), cigar.getEnd(), cigar.getReadSeq(), gro.getStrand() );
					for( ReadObject tro: gro.getRecordElements() ) {
						List<NucleotideObject> nucleotide = tro.getSequence();

						if( tro.getStartPosition() < this.referenceSequenceObject.getStartPosition() && tro.getEndPosition() < this.referenceSequenceObject.getStartPosition() )
							continue;
						if( tro.getStartPosition() > this.referenceSequenceObject.getEndPosition() && tro.getEndPosition() > this.referenceSequenceObject.getEndPosition() )
							continue;

						// Each cigar string compare with genome location
						for(int i=0; i<nucleotide.size(); i++) {
							NucleotideObject nObj = nucleotide.get(i);

							int newPos = SwingUtilities.getGenomePos( this.referenceSequenceObject.getSequence(), nObj.getPosition() );
//							int newPos = (int)(nObj.getPosition() - this.referenceSequenceObject.getStartPosition());
	
							if( newPos >= 0 && newPos < this.referenceSequenceObject.getSequence().size() ) {
								histogramBin[ newPos ]++;
							}
						}
					}
				}else {
					List<NucleotideObject> nucleotide = read.getSequence();
	
					// Each cigar string compare with genome location
					for(int i=0; i<nucleotide.size(); i++) {
						NucleotideObject nObj = nucleotide.get(i);
//						int newPos = (int)(nObj.getPosition() - this.referenceSequenceObject.getStartPosition());
						int newPos = SwingUtilities.getGenomePos( this.referenceSequenceObject.getSequence(), nObj.getPosition() );
						
						if( newPos >= 0 && newPos < this.referenceSequenceObject.getSequence().size() ) {
							if( newPos < 0 || newPos > length - 1 )	continue;

							if( newPos < histogramBin.length ) {
								histogramBin[ newPos ]++;
							}
						}
					}
				}
			}
		}
		return histogramBin;
	}
	
	public List<ReadObject> getReadList() {
		return this.readHeatMap.getReadList();
	}

	public List<ReadObject> getReadList( int from, int to ) {
		return this.readHeatMap.getReadList(from, to);
	}
	
	public HairpinSequenceObject getPrematureSequenceObject() {
		return this.prematureSequenceObject;
	}
	
	public GenomeReferenceObject getReferenceSequenceObject() {
		return this.referenceSequenceObject;
	}
	
	public MiRna getMirnaInfo() {
		return this.mirna;
	}

	public double[][] getExpressionProfile( boolean isNormalized ) {
		return this.readHeatMap.getExpressionProfile(isNormalized);
	}

	public void normalizeReadProfile(String normalizationMethod, double imputeVal ) {
		this.isNormalized = true;
		this.readHeatMap.normalizeReadProfile(normalizationMethod, imputeVal);
	}
	
	public void rawReadProfile() {
		this.isNormalized = false;
	}

	public MatrixObj getHeatMapObject() {
		return this.readHeatMap;
	}
	
//	public void filter(String filter, String operator, String query) {
//		this.readHeatMap.setFilterConditions( filter, operator, query );
//	}

	public List<ReadWithMatrix> getReadVectorListByFiltered() {
		return this.readHeatMap.getReadVectorListByFiltered();
	}

	public void sortReadPosition() {
		this.readHeatMap.sortReadPosition();
		this.SORT_DIRECTION = SwingConst.SORT_DESC;
	}
	
	public void sortReadEndPosition() {
		this.readHeatMap.sortReadEndPosition();
		this.SORT_DIRECTION = SwingConst.SORT_DESC;
	}

	public void sortReadCountAt( int pos ) {
		this.readHeatMap.sortReadCountAt( pos, this.SORT_DIRECTION );

		if( this.SORT_DIRECTION == SwingConst.SORT_ASC )	this.SORT_DIRECTION = SwingConst.SORT_DESC;
		else												this.SORT_DIRECTION = SwingConst.SORT_ASC;
	}

	public void sortDirectionWithReadCount( int pos, int direction ) {
		this.SORT_DIRECTION = direction;

		this.readHeatMap.sortReadCountAt( pos, this.SORT_DIRECTION );
	}
	
	public void sortBySortModel( MsbSortModel sortModel ) throws Exception {
		this.readHeatMap.sortReadCountBySortModel( sortModel );
	}

	public void addRead(ReadWithMatrix addItem) {
		this.readHeatMap.addRecord( addItem );
	}
	
	public void addRead(ReadWithMatrix addItem, int pos) {
		this.readHeatMap.addRecord( addItem, pos );
	}
	
	public void removeRead( int pos ) {
		this.readHeatMap.removeRecord( pos );
	}

	public ReadWithMatrix getRead( int pos ) {
		return this.readHeatMap.getRecord( pos );
	}
	
	public ReadWithMatrix getReadWithMatrix( int pos ) {
		return this.readHeatMap.getReadWithMatrixList( pos );
	}

	/*******************************************************
	 * To get column structre (it is Tree with HashMap)
	 * 
	 * @return operator
	 */
	public MSBReadCountTableColumnStructureModel getMSBReadCountTableColumnStructureModel() {
		return this.currentRCTColumnStructureModel;
	}

//	/*******************************************************
//	 * To get filter (read count, sequence, start with and so on)
//	 * 
//	 * @return filter
//	 */
//	public String getFilter() {
//		return this.readHeatMap.getFilter();
//	}
//
//	/*******************************************************
//	 * To get operator (<, <=, =, >=, > and so on)
//	 * 
//	 * @return operator
//	 */
//	public String getOperator() {
//		return this.readHeatMap.getOperator();
//	}
//
//	/*******************************************************
//	 * To get query to filter
//	 * 
//	 * @return operator
//	 */
//	public String getQuery() {
//		return this.readHeatMap.getQuery();
//	}

	/*******************************************************
	 * To get Index in the table header about header name
	 * 
	 * @return index of header
	 */
	public int getIndexOfHeader( String headerName ) {
		List<String> header = this.currentRCTColumnStructureModel.getHeatMapHeader();
		int pos = 0;
		for(int i=0; i<header.size(); i++) {
			if( header.get(i).equals( headerName ) ) {
				pos = i;
				break;
			}
		}
		return pos;
	}

	/*******************************************************
	 * To get Maximum count in the Read Count Table
	 * 
	 * @return maximum value
	 */
	public double maxCountValue() {
		return this.readHeatMap.maxCountValue();
	}

	/*******************************************************
	 * To get Minimum count in the Read Count Table
	 * 
	 * @return minimum value
	 */
	public double minCountValue() {
		return this.readHeatMap.minCountValue();
	}
	
	public boolean isNormalized() {
		return this.isNormalized;
	}
	
//	public MsbSortModel getMsbSortModel() {
//		return this.sortModel;
//	}
//	
//	public MsbFilterModel getMsbFilterModel() {
//		return this.filterModel;
//	}
	
//	public String writeModelToFile( String projectName ) {
//		FileOutputStream fout;
//
//		String root_dir = MsbEngine.getInstance().getSystemProperties().getProperty("msb.project.workspace") + File.separator + projectName;
//		String dir 		= root_dir + File.separator + "models";
//		String file		= dir + File.separator + this.getMirnaInfo().getMirid() + ".mdl";
//
//		File rootDir = new File( root_dir );
//		if( !rootDir.exists() )	rootDir.mkdir();
//		
//		File dirObj = new File( dir );
//		if( !dirObj.exists() )	dirObj.mkdir();
//
//		try {
//			RandomAccessFile raf = new RandomAccessFile( file, "rw");
//
//			fout = new FileOutputStream(raf.getFD());
//			Output output = new Output( fout, 4096 );
//			
//			kryo.writeObject(output, this );
//
//			output.flush();
//			output.close();
//			raf.close();
//			fout.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
////			e.printStackTrace();
//			MsbEngine.logger.error( "Model write Exception", e);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
////			e.printStackTrace();
//			MsbEngine.logger.error( "Model write Exception", e);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
////			e.printStackTrace();
//			MsbEngine.logger.error( "Model write error : ", e );
//		}
//
//		return file;
//	}
	
	public String writeModelToFile( String projectName ) {
		FileOutputStream fout;

		String root_dir = MsbEngine.getInstance().getSystemProperties().getProperty("msb.project.workspace") + File.separator + projectName;
		String dir 		= root_dir + File.separator + "models";
		String file		= dir + File.separator + this.getMirnaInfo().getMirid() + ".mdl";

		File rootDir = new File( root_dir );
		if( !rootDir.exists() )	rootDir.mkdir();
		
		File dirObj = new File( dir );
		if( !dirObj.exists() )	dirObj.mkdir();

		try {
			fout = new FileOutputStream( file );

			ObjectOutputStream oos = new ObjectOutputStream( fout );
			oos.writeObject( this );

			oos.flush();
			oos.close();
			fout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( "Model write Exception", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( "Model write Exception", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			MsbEngine.logger.error( "Model write error : ", e );
		}

		return file;
	}
	
	public ProjectMapItem getProjectMapItem() {
		return MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( this.projectInfo.getProjectName() );
	}
	
	public boolean isNovel() {
		return this.mirid.startsWith("Novel")?true:false;
	}

	public List<MatureVO> getVoList() {
		return voList;
	}

	public void setVoList(List<MatureVO> voList) {
		this.voList = voList;
	}

	public HairpinVO getHairpinVo() {
		return hairpinVo;
	}

	public void setHairpinVo(HairpinVO hairpinVo) {
		this.hairpinVo = hairpinVo;
	}
}
