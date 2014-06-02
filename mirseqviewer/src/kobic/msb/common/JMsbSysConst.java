package kobic.msb.common;

public interface JMsbSysConst {
	public final String ENCODE_MS949	= "MS949";
	public final String ENCODE_UTF8		= "UTF8";
	public final String ENCODE_EUC_KR	= "EUC-KR";

//	public static final int READY_TO_SORT				= 1;
//	public static final int RUNNING_SORT				= 2;
//	public static final int DONE_SORT					= 3;
//	public static final int READY_TO_INDEX				= 4;
//	public static final int RUNNING_INDEX				= 5;
//	public static final int DONE_INDEX					= 6;
//	public static final int READY_TO_CHOOSE_MIRNAS		= 7;
//	public static final int DONE_TO_CHOOSE_MIRNAS		= 8;
//	public static final int RUNNING_MODELING			= 9;
//	public static final int DONE						= 10;

	public static final int STS_DONE_CREATE_EMPTY_PROJECT	= 10;

	public static final int	STS_DONE_CHOOSE_ORGANISM		= 20;

	public static final int STS_DONE_INDEX_FILES			= 30;
	public static final int STS_DONE_READ_FILES				= 31;
	public static final int STS_DONE_MAKE_MODELS			= 32;
	public static final int STS_DONE_SUMMARIZE_RNAS			= 33;

	public static final int STS_DONE						= 40;

	public static final String TOTAL_SUM_HEADER_PREFIX	= "T";
	public static final String GROUP_SUM_HEADER_PREFIX	= "S";
	public static final String GROUP_HEADER_PREFIX		= "G";
	public static final String SAMPLE_HEADER_PREFIX		= "P";
	
	public static final String SUM_SUFFIX				= "_sum";
	
	public static final String HEADER_KEY_SEP			= ":";
	
	public static final String TOTAL_SUM_HEADER			= "Total_sum";
	
	public static final String MIRNA_REPORT_TITLE		= "miRNA summary";
	
	public static final String NOVEL_MICRO_RNA				= "Novel-";
	public static final String CANDIDATE_NOVEL_MICRO_RNA	= "cNovel-";
	public static final int ABUNDANCE_FOR_NOVEL_MICRO_RNA	= 10;
	public static final int MAX_NOVEL_PREMATURE_RANGE		= 150;
	public static final int MIN_NOVEL_PREMATURE_RANGE		= 30;
	
	public static final double MISSING_VALUE			= 0d;
	
	public static final int FRACTION_LENGTH	= 2;
	
	public static final int EXTEND_SEQ_LENGTH = 10;
	public static final int MAGNIFIED_SEQ_LENGTH = 10;
	
	public static final int SHIFT_TO_UPSTREAM = 1;
	public static final int SHIFT_TO_DOWNSTREAM = 2;
	
	
	public static final int WITH_GENOME = 1;
	public static final int WITH_MIRBASE = 2;
	
	public static final int ALIGNMENT_VIEW = 1;
	public static final int EXPRESSION_VIEW = 2;
	public static final int SUMMARY_VIEW = 3;
}
