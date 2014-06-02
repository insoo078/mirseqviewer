package kobic.msb.common;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;

import kobic.msb.system.SystemEnvironment;

public interface SwingConst {
	public static final int		TRACK_HEIGHT			= 20;
	public static final int		EXTRA_IMG_UNIT_WIDTH	= (int)(2.5 * SwingConst.TRACK_HEIGHT);
	public static final int		EXTRA_IMG_HEADER_HEIGHT	= 5 * SwingConst.TRACK_HEIGHT + (SwingConst.TRACK_HEIGHT/2);

	public static final int		TRACK_FIXED			= 1;
	public static final int		TRACK_MOVABLE		= 2;
	
	public static final String	PROCESS_HEADER		= "PID";
	
	
	public static final int		SCALE_TYPE			= 0;
	public static final int		MIRID_TYPE			= 1;
	public static final int		READ_SEQ_TYPE		= 2;
	public static final int		REFERENCE_SEQ_TYPE	= 3;
	public static final int		GENOME_SEQ_TYPE		= 4;
	public static final int		HISTOGRAM_TYPE		= 5;
	public static final int		CUSTOM_SEQ_TYPE		= 6;
	public static final int		OUTER_FILE			= 7;
	
	public static final int		SORT_DESC			= 0;
	public static final int		SORT_ASC			= 1;
	

	public static final Font	SEQUENCE_FONT		= new Font("Arial", Font.PLAIN, 9);
	public static final Font	HEADER_FONT			= new Font("Arial", Font.PLAIN, 11);
	public static final Font	LABEL_FONT			= new Font("Arial", Font.PLAIN, 12);
	public static final Font	_9_FONT				= new Font("Arial", Font.PLAIN, 9);
	public static final Font	_10_FONT			= new Font("Arial", Font.PLAIN, 10);
	public static final Font	_11_FONT			= new Font("Arial", Font.PLAIN, 11);
	public static final Font	_12_FONT			= new Font("Arial", Font.PLAIN, 12);
	public static final Font	_13_FONT			= new Font("Arial", Font.PLAIN, 13);
	public static final Font	_14_FONT			= new Font("Arial", Font.PLAIN, 14);
	public static final Font	DEFAULT_DIALOG_FONT = new Font("Arial", Font.PLAIN, 11);
	public static final Font	DEFAULT_DIALOG_FONT_BOLD = new Font("Arial", Font.BOLD, 11);
	public static final Font	DEFAULT_DIALOG_FONT_TITLE = new Font("Arial", Font.PLAIN, 13);

	public static final int		RED					= 1;
	public static final int		BLUE				= 2;
	public static final int		GREEN				= 3;
	public static final int		ORANGE				= 4;
	public static final int		YELLOW				= 5;
	
	public static final int		SINGLE_CHANNEL		= 1;
	public static final int		TWO_CHANNEL			= 2;
	
	public static final int		ONLY_HEATMAP		= 1;
	public static final int		WITH_COUNT			= 2;
	
	
	public static final String	MiRNA_2ND_STRUCTURE_5PMOR	= "5'-upstream";
	public static final String	MiRNA_2ND_STRUCTURE_5P		= "5'";
	public static final String	MiRNA_2ND_STRUCTURE_LOOP	= "Loop";
	public static final String	MiRNA_2ND_STRUCTURE_3P		= "3'";
	public static final String	MiRNA_2ND_STRUCTURE_3PMOR	= "3'-downstream";
//	public static final String	MiRNA_2ND_STRUCTURE_HAIRPIN	= "Loop";
	public static final String	MiRNA_2ND_STRUCTURE_UNIQUE	= "unique";
	public static final String	MiRNA_2ND_STRUCTURE_ORDER	= "order";
	
	
	public static final Color	ALIGNMENT_TOOLTIP_COLOR	= new Color(253, 255, 207);
	
	public static final int		MAX_LENGTH_TO_VIEW_SEQUENCE = 250;

	//A border that puts 10 extra pixels at the sides and
	//bottom of each pane.

	public static final Border BORDER_PANE_EDGE			= BorderFactory.createEmptyBorder(0,10,10,10);
	public static final Border BORDER_BLACK_LINE		= BorderFactory.createLineBorder(Color.black);
	public static final Border BORDER_RAISED_ETCHED		= BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	public static final Border BORDER_LOWERED_ETCHED	= BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	public static final Border BORDER_RAISED_BEVEL		= BorderFactory.createRaisedBevelBorder();
	public static final Border BORDER_LOWERED_BEVEL		= BorderFactory.createLoweredBevelBorder();
	public static final Border BORDER_EMPTY				= BorderFactory.createEmptyBorder();
	
	public static final Icon OPEN_ICON	= UIManager.getIcon( "Tree.openIcon" );
	public static final Icon NEW_ICON	= UIManager.getIcon( "FileView.fileIcon" );
	public static final Icon RUN_ICON	= new javax.swing.ImageIcon( SystemEnvironment.getSystemBasePath() + "images/run.png" );
	
	public static final String SAMPLE_TABLE_COLUMN[] = { "Order", "Group ID","Sample ID","Path", "Index File" };
	
	public static final DockingWindowsTheme MAIN_INFONODE_THEME = new ShapedGradientDockingTheme();
	
	public enum Status { SELECTED, DESELECTED, INDETERMINATE }
	
	public enum Sorts { 
		LARGEST_TO_SMALLEST(0), SMALLEST_TO_LARGEST(1);
		
		private Integer value;

		private Sorts(int value) {
			this.value = value;
		}
		
		public int getValue() {	return this.value;	}

		public String toString() {
			if( this.value.equals( Integer.valueOf( 0 ) ) ) {
				return "Largest to smallest";
			}
			return "Smallest to largest";
		}
		
		public int getValue(String title) {
			if( title.equals("Largest to smallest") )	return 0;
			return 1;
		}
	}
}
