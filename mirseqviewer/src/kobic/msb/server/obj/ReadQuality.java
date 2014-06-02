package kobic.msb.server.obj;

import net.sf.samtools.SAMRecord;
import kobic.com.util.Utilities;

public class ReadQuality implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String readName;
	private String nh;
	private int mapq;
	private String cigar;
	private String x0;
	private String x1;
	private String xa;
	private String md;
	private String xg;
	private String nm;
	private String xm;
	private String xo;
	private String xt;
	private String qt;
	private String q2;
	private String ct;
	private char	strand;
	private byte[] basePhredQuality;
	private boolean isMapped;

	public ReadQuality() {
		this.nh = "";
		this.mapq = 0;
		this.cigar = "";
		this.x0 = "";
		this.x1 = "";
		this.xa = "";
		this.md = "";
		this.xg = "";
		this.nm = "";
		this.xm = "";
		this.xo = "";
		this.xt = "";
		this.qt = "";
		this.q2 = "";
		this.ct = "";
		this.strand = '+';
		this.isMapped			= true;
	}

	public ReadQuality(SAMRecord record) {
	    Object nhObj 			= record.getAttribute("NH");

	    this.readName			= record.getReadName();
	    this.nh					= nhObj!=null?nhObj.toString():"0";
	    this.mapq				= record.getMappingQuality();
	    this.isMapped			= !((Boolean)record.getReadUnmappedFlag()==null?false:record.getReadUnmappedFlag());
	    this.cigar				= record.getCigarString();
	    this.x0 				= Utilities.nulltoEmpty( record.getAttribute("X0") );
	    this.x1					= Utilities.nulltoEmpty( record.getAttribute("X1") );
	    this.xa					= Utilities.nulltoEmpty( record.getAttribute("XA") );
	    this.md 				= Utilities.nulltoEmpty( record.getAttribute("MD") );
	    this.xg					= Utilities.nulltoEmpty( record.getAttribute("XG") );
	    this.nm					= Utilities.nulltoEmpty( record.getAttribute("NM") );
	    this.xm					= Utilities.nulltoEmpty( record.getAttribute("XM") );
	    this.xo					= Utilities.nulltoEmpty( record.getAttribute("XO") );
	    this.xt					= Utilities.nulltoEmpty( record.getAttribute("XT") );
	    this.qt					= Utilities.nulltoEmpty( record.getAttribute("QT") );
	    this.q2					= Utilities.nulltoEmpty( record.getAttribute("Q2") );
	    this.ct					= Utilities.nulltoEmpty( record.getAttribute("CT") );
	    
	    String binaryStr = Utilities.lpad( Integer.toBinaryString( record.getFlags() ), 5, "0" );
        this.strand				= binaryStr.charAt(0);		// 0 : forward (+), 1: reverse (-)
	    
	    this.basePhredQuality	= record.getBaseQualities();
	}

	public String getNh() {
		return nh;
	}

	public void setNh(String nh) {
		this.nh = nh;
	}

	public int getMapq() {
		return mapq;
	}

	public void setMapq(int mapq) {
		this.mapq = mapq;
	}

	public String getCigar() {
		return cigar;
	}

	public void setCigar(String cigar) {
		this.cigar = cigar;
	}

	public String getX0() {
		return x0;
	}

	public void setX0(String x0) {
		this.x0 = x0;
	}

	public String getX1() {
		return x1;
	}

	public void setX1(String x1) {
		this.x1 = x1;
	}

	public String getMd() {
		return md;
	}

	public void setMd(String md) {
		this.md = md;
	}

	public String getXg() {
		return xg;
	}

	public void setXg(String xg) {
		this.xg = xg;
	}

	public String getNm() {
		return nm;
	}

	public void setNm(String nm) {
		this.nm = nm;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getXo() {
		return xo;
	}

	public void setXo(String xo) {
		this.xo = xo;
	}

	public String getXt() {
		return xt;
	}

	public void setXt(String xt) {
		this.xt = xt;
	}

	public String getQt() {
		return qt;
	}

	public void setQt(String qt) {
		this.qt = qt;
	}

	public String getQ2() {
		return q2;
	}

	public void setQ2(String q2) {
		this.q2 = q2;
	}

	public boolean isMapped() {
		return isMapped;
	}

	public void setMapped(boolean isMapped) {
		this.isMapped = isMapped;
	}

	public String getReadName() {
		return readName;
	}

	public void setReadName(String readName) {
		this.readName = readName;
	}

	public byte[] getBasePhredQuality() {
		return basePhredQuality;
	}

	public void setBasePhredQuality(byte[] basePhredQuality) {
		this.basePhredQuality = basePhredQuality;
	}

	public String getXa() {
		return xa;
	}

	public void setXa(String xa) {
		this.xa = xa;
	}

	public String getCt() {
		return ct;
	}

	public void setCt(String ct) {
		this.ct = ct;
	}

	public char getStrand() {
		return strand;
	}

	public void setStrand(char strand) {
		this.strand = strand;
	}
	
	public String getOrientation() {
		if( this.strand == '0' )	return "+";
		
		return "-";
	}
}
