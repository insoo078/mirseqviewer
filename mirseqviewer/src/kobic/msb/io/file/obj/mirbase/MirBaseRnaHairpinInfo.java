package kobic.msb.io.file.obj.mirbase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MirBaseRnaHairpinInfo implements Serializable{
	public List<MirBaseRnaMatureInfo> getMatureData() {
		return matureData;
	}

	public void setMatureData(List<MirBaseRnaMatureInfo> matureData) {
		this.matureData = matureData;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String std;
	private String type;
	private String species;
	private String basepair;
	
	private String accession;
	private String description;
	private List<PubmedInfo> pubmedInfoList;
	private List<String> databaseCrossReference;
	private String comment;
	private String featureTableHeader;
	private List<MirBaseRnaMatureInfo> featureTableData;
	private List<String>	strFeatureTableData;
	private List<MirBaseRnaMatureInfo>	matureData;
	private String sequenceInfo;
	private String sequence;

	public MirBaseRnaHairpinInfo() {
		this.id						= "";
		this.std					= "";
		this.type					= "";
		this.species				= "";
		this.basepair				= "";
		this.accession				= "";
		this.description			= "";
		this.comment				= "";
		this.featureTableHeader		= "";
		this.sequenceInfo			= "";
		this.sequence				= "";

		this.pubmedInfoList			= new ArrayList<PubmedInfo>();
		this.databaseCrossReference	= new ArrayList<String>();
		this.strFeatureTableData	= new ArrayList<String>();
		this.featureTableData		= new ArrayList<MirBaseRnaMatureInfo>();
	}
	
	public String getStd() {
		return std;
	}
	public void setStd(String std) {
		this.std = std;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSpecies() {
		return species;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	public String getBasepair() {
		return basepair;
	}
	public void setBasepair(String basepair) {
		this.basepair = basepair;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	public List<PubmedInfo> getPubmedInfoList() {
		return pubmedInfoList;
	}
	public void addPubmedInfo(PubmedInfo info) {
		this.pubmedInfoList.add( info );
	}
	public List<String> getDatabaseCrossReference() {
		return databaseCrossReference;
	}
	public void addDatabaseCrossReference(String databaseCrossReference) {
		this.databaseCrossReference.add( databaseCrossReference );
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getFeatureTableHeader() {
		return featureTableHeader;
	}
	public void setFeatureTableHeader(String featureTableHeader) {
		this.featureTableHeader = featureTableHeader;
	}
	public List<MirBaseRnaMatureInfo> getFeatureTableData() {
		return featureTableData;
	}
	public void addFeatureTableData(MirBaseRnaMatureInfo featureTableData) {
		this.featureTableData.add( featureTableData );
	}
	public List<String> getStrFeatureTableData() {
		return strFeatureTableData;
	}
	public void addStrFeatureTableData(String strFeatureTableData) {
		this.strFeatureTableData.add( strFeatureTableData );
	}
	public String getSequenceInfo() {
		return sequenceInfo;
	}
	public void setSequenceInfo(String sequenceInfo) {
		this.sequenceInfo = sequenceInfo;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	public void reGenerateMatureInfo() {
		this.matureData = new ArrayList<MirBaseRnaMatureInfo>();

		Iterator<String> iter = this.strFeatureTableData.iterator();

/*
FT   miRNA           17..38
FT                   /accession="MIMAT0000001"
FT                   /product="cel-let-7-5p"
FT                   /evidence=experimental
FT                   /experiment="cloned [1-3,5], Northern [1], PCR [4], Solexa
FT                   [6], CLIPseq [7]"
FT   miRNA           56..80
FT                   /accession="MIMAT0015091"
FT                   /product="cel-let-7-3p"
FT                   /evidence=experimental
FT                   /experiment="CLIPseq [7]"
 */
		MirBaseRnaMatureInfo mature = null;
		while( iter.hasNext() ) {
			String str = iter.next().trim().replace("\"", "");

			if( str.startsWith("miRNA") ) {
				if( mature != null )	this.matureData.add( mature );

				mature = new MirBaseRnaMatureInfo();
				String tmp = str.replace("miRNA", "").trim();
				String[] div = tmp.split("\\.\\.");
				mature.setStart( div[0] );
				mature.setEnd( div[1] );
			}else if( str.startsWith("/accession=") )	mature.setAccession( str.split("=")[1] );
			else if( str.startsWith("/product=") )		mature.setMirid( str.split("=")[1] );
			else if( str.startsWith("/evidence=") )		mature.setEvidence( str.split("=")[1] );
			else if( str.startsWith("/experiment=") )	mature.setExperiment( str.split("=")[1] );
		}
		if( mature != null )	this.matureData.add( mature );
	}
}
