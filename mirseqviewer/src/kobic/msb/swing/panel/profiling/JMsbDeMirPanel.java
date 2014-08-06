package kobic.msb.swing.panel.profiling;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSplitPane;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTabbedPane;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;

import kobic.com.edgeR.BasicFunctions;
import kobic.com.edgeR.DGEList;
import kobic.com.edgeR.EdgeR;
import kobic.com.edgeR.ExactTest;
import kobic.com.edgeR.TopTags;
import kobic.com.log.MessageConsole;
import kobic.com.util.Utilities;
import kobic.msb.common.ImageConstant;
import kobic.msb.common.SwingConst;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.ClusterModel;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.swing.component.UpdatableTableModel;
import kobic.msb.swing.renderer.NumberTableCellRenderer;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class JMsbDeMirPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static int 		_DISP_GRID_RANGE_FROM_		= -6;
	private static int	 	_DISP_GRID_RANGE_TO_ 		= 6;
	private static int 		_GRID_LENGTH_				= 11;
	private static int 		_BIG_COUNT_					= 900;
	private static double	_EXACT_TEST_PRIOR_COUNT_	= 0.125;
	private static double	_LOGRATIO_TRIM_				= 0.3;
	private static double	_SUM_TRIM_					= 0.05;
	private static double	_A_CUT_OFF_					= -1e10;
	private static double 	_NORMAL_P_					= 0.75;
	private static String	_NORMALIZE_METHOD_			= EdgeR._TMM_;
	private static String	_TREND_						= EdgeR._MOVINGAVE_;
	private static String	_TAGWISE_METHOD_			= EdgeR._GRID_;
	private static String	_DISPERSION_				= ExactTest.AUTO;
	private static String	_REJECTION_REGION_			= ExactTest.DOUBLE_TAIL;
	private static String	_ADJ_P_VALUE_				= TopTags._BH_;

	private JTextField txtRefColumn;
	private JTextField txtLogratioTrim;
	private JTextField txtSumTrim;
	private JTextField txtAcutoff;
	private JTextField txtNormalizeP;
	private JTextField txtCommonDispTol;
	private JTextField txtCommonDispRowSumFilter;
	private JTextField txtTagwiseDispPriorDf;
	private JTextField txtTagwiseDispSpan;
	private JTextField txtTagwiseDispGridLength;
	private JTextField txtTagwiseDispGridRangeFrom;
	private JTextField txtTagwiseDispTol;
	private JTextField txtExactTestBigCount;
	private JTextField txtExactTestPriorCount;
	private JTextField txtTagwiseDispGridRangeTo;
	private JTable tblExactTestResult;
	
	private JTextPane logTextPane;
	
	private JCheckBox chkDoWeighting;

	/*** Over JDK 1.7 ***/
//	private JComboBox<String> cmbTagwiseDispTrend;
//	private JComboBox<String> cmbTagwiseDispMethod;
//	private JComboBox<String> cmbExactTestDisp;
//	private JComboBox<String> cmbExactTestRejectionRegion;
//	private JComboBox<String> cmbNormalizeMethod;
//	private JComboBox<String> cmbAdjustPvalueMethod;
//	
//	private JList<String> lstTotalGroups;
//	private JList<String> lstChoosedGroups;
	/*** Under JDK 1.7 ***/
	private JComboBox 		cmbTagwiseDispTrend;
	private JComboBox 		cmbTagwiseDispMethod;
	private JComboBox 		cmbExactTestDisp;
	private JComboBox 		cmbExactTestRejectionRegion;
	private JComboBox 		cmbNormalizeMethod;
	private JComboBox		cmbAdjustPvalueMethod;
	
	private JList			lstTotalGroups;
	private JList			lstChoosedGroups;
	
	
	private JButton btnTest;
	private JButton btnToRight;
	private JButton btnToLeft;
	
	private JProgressBar progressBar;
	
	
	private static double	_TOLERANCE_			= 1e-06;
	private static int		_ROW_SUM_FILTER_	= 5;
	private static int		_PRIOR_DF_			= 10;

	private ClusterModel 	clusterModel;
	
	private ProjectMapItem projectMapItem;
	
	private String[]				columns;
	private Object[][]				data;
	
	private MessageConsole mc;
	
	private JMsbDeMirPanel	remote = JMsbDeMirPanel.this;
	
	
	/**
	 * @wbp.nonvisual location=349,61
	 */

	/**
	 * Create the panel.
	 */
	public JMsbDeMirPanel(ClusterModel clusterModel, ProjectMapItem projectMapItem) throws Exception{
		this.clusterModel	= clusterModel;
		this.projectMapItem	= projectMapItem;
		
		this.columns	= new String[]{"miRNA", "logFC", "logCPM", "P.value", "Adjusted P.value"};
		this.data		= null;
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(450);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(splitPane)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		
		JPanel normalizeBorderPanel = new JPanel();
		normalizeBorderPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(1), "Normalization" ) );
		
		JPanel commonDispBorderPanel = new JPanel();
		commonDispBorderPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(1), "Estimate Common Negative Binomial Dispersion" ));
		
		JLabel lblTol = new JLabel("tolerance");
		
		txtCommonDispTol = new JTextField();
		txtCommonDispTol.setText( Double.valueOf( JMsbDeMirPanel._TOLERANCE_ ).toString() );
		txtCommonDispTol.setColumns(10);
		
		JLabel lblRowsumfilter = new JLabel("rowsum.filter");
		
		txtCommonDispRowSumFilter = new JTextField();
		txtCommonDispRowSumFilter.setText( Integer.valueOf(_ROW_SUM_FILTER_).toString() );
		txtCommonDispRowSumFilter.setColumns(10);
		GroupLayout gl_commonDispBorderPanel = new GroupLayout(commonDispBorderPanel);
		gl_commonDispBorderPanel.setHorizontalGroup(
			gl_commonDispBorderPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_commonDispBorderPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblTol)
					.addGap(34)
					.addComponent(txtCommonDispTol, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblRowsumfilter, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtCommonDispRowSumFilter, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(45, Short.MAX_VALUE))
		);
		gl_commonDispBorderPanel.setVerticalGroup(
			gl_commonDispBorderPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_commonDispBorderPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_commonDispBorderPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTol)
						.addComponent(txtCommonDispTol, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtCommonDispRowSumFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblRowsumfilter))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		commonDispBorderPanel.setLayout(gl_commonDispBorderPanel);
		
		JPanel tagwiseDispBorderPanel = new JPanel();
		tagwiseDispBorderPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(1), "Estimate Empirical Bayes Tagwise Dispersion" ));
		
		JLabel lblPriorDf = new JLabel("prior df");
		
		txtTagwiseDispPriorDf = new JTextField();
		txtTagwiseDispPriorDf.setText(Integer.valueOf(JMsbDeMirPanel._PRIOR_DF_).toString());
		txtTagwiseDispPriorDf.setColumns(10);
		
		JLabel lblTrend = new JLabel("trend");
		
		JLabel lblSpan = new JLabel("span");
		
		txtTagwiseDispSpan = new JTextField();
		txtTagwiseDispSpan.setColumns(10);
		
		JLabel lblMethod = new JLabel("method");
		
		JLabel lblGridlength = new JLabel("grid.length");

		txtTagwiseDispGridLength = new JTextField();
		txtTagwiseDispGridLength.setText( Integer.valueOf(JMsbDeMirPanel._GRID_LENGTH_).toString() );
		txtTagwiseDispGridLength.setColumns(10);
		
		JLabel lblGridrange = new JLabel("grid.range");
		
		txtTagwiseDispGridRangeFrom = new JTextField();
		txtTagwiseDispGridRangeFrom.setText( Integer.valueOf(JMsbDeMirPanel._DISP_GRID_RANGE_FROM_).toString() );
		txtTagwiseDispGridRangeFrom.setColumns(10);
		
		/*** Over JDK 1.7 ***/
//		this.cmbTagwiseDispTrend = new JComboBox<String>(EdgeR.trends);
//		this.cmbTagwiseDispMethod = new JComboBox<String>(EdgeR.methods);
		/*** Under JDK 1.7 ***/
		this.cmbTagwiseDispTrend = new JComboBox(EdgeR.trends);
		this.cmbTagwiseDispMethod = new JComboBox(EdgeR.methods);
//		this.cmbTagwiseDispTrend = new JComboBox<String>();
//		this.cmbTagwiseDispMethod = new JComboBox<String>();

		JLabel lblTolerance = new JLabel("tolerance");
		
		txtTagwiseDispTol = new JTextField();
		txtTagwiseDispTol.setText( Double.valueOf( JMsbDeMirPanel._TOLERANCE_ ).toString() );
		txtTagwiseDispTol.setColumns(10);
		
		txtTagwiseDispGridRangeTo = new JTextField();
		txtTagwiseDispGridRangeTo.setText( Integer.valueOf(JMsbDeMirPanel._DISP_GRID_RANGE_TO_).toString() );
		txtTagwiseDispGridRangeTo.setColumns(10);
		
		JLabel lblTo = new JLabel("to");
		GroupLayout gl_tagwiseDispBorderPanel = new GroupLayout(tagwiseDispBorderPanel);
		gl_tagwiseDispBorderPanel.setHorizontalGroup(
			gl_tagwiseDispBorderPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_tagwiseDispBorderPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_tagwiseDispBorderPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_tagwiseDispBorderPanel.createSequentialGroup()
							.addGroup(gl_tagwiseDispBorderPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblPriorDf)
								.addComponent(lblSpan, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblGridlength, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_tagwiseDispBorderPanel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(txtTagwiseDispSpan, 0, 0, Short.MAX_VALUE)
								.addComponent(txtTagwiseDispGridLength, 0, 0, Short.MAX_VALUE)
								.addComponent(txtTagwiseDispPriorDf, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_tagwiseDispBorderPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblGridrange, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_tagwiseDispBorderPanel.createParallelGroup(Alignment.TRAILING, false)
									.addComponent(lblMethod, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(lblTrend, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))))
						.addGroup(gl_tagwiseDispBorderPanel.createSequentialGroup()
							.addComponent(lblTolerance, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtTagwiseDispTol, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_tagwiseDispBorderPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(cmbTagwiseDispTrend, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
						.addComponent(cmbTagwiseDispMethod, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_tagwiseDispBorderPanel.createSequentialGroup()
							.addComponent(txtTagwiseDispGridRangeFrom, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblTo, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtTagwiseDispGridRangeTo, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))))
		);
		gl_tagwiseDispBorderPanel.setVerticalGroup(
			gl_tagwiseDispBorderPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_tagwiseDispBorderPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_tagwiseDispBorderPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPriorDf)
						.addComponent(txtTagwiseDispPriorDf, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTrend)
						.addComponent(cmbTagwiseDispTrend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6)
					.addGroup(gl_tagwiseDispBorderPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSpan)
						.addComponent(txtTagwiseDispSpan, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMethod)
						.addComponent(cmbTagwiseDispMethod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6)
					.addGroup(gl_tagwiseDispBorderPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblGridlength)
						.addComponent(txtTagwiseDispGridLength, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblGridrange)
						.addComponent(txtTagwiseDispGridRangeFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTo)
						.addComponent(txtTagwiseDispGridRangeTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_tagwiseDispBorderPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_tagwiseDispBorderPanel.createSequentialGroup()
							.addGap(12)
							.addComponent(lblTolerance))
						.addGroup(gl_tagwiseDispBorderPanel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtTagwiseDispTol, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		tagwiseDispBorderPanel.setLayout(gl_tagwiseDispBorderPanel);
		
		JPanel exactTestBorderPanel = new JPanel();
		exactTestBorderPanel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(1), "Exact Test for Differences between 2 Groups" ));
		
		JLabel lblDispersion = new JLabel("dispersion");
		
		txtExactTestBigCount = new JTextField();
		txtExactTestBigCount.setText( Integer.valueOf( JMsbDeMirPanel._BIG_COUNT_ ).toString() );
		txtExactTestBigCount.setColumns(10);
		
		JLabel lblBigcount = new JLabel("big.count");
		
		txtExactTestPriorCount = new JTextField();
		txtExactTestPriorCount.setText( Double.valueOf( JMsbDeMirPanel._EXACT_TEST_PRIOR_COUNT_).toString() );
		txtExactTestPriorCount.setColumns(10);
		
		/*** Over JDK 1.7 ***/
//		this.cmbExactTestDisp = new JComboBox<String>(ExactTest.DISPERTION_TYPES);
//		this.cmbExactTestRejectionRegion = new JComboBox<String>(ExactTest.REJECTION_TYPES);
//		this.cmbAdjustPvalueMethod = new JComboBox<String>(TopTags.pAdustMethods);
		/*** Under JDK 1.7 ***/
		this.cmbExactTestDisp = new JComboBox(ExactTest.DISPERTION_TYPES);
		this.cmbExactTestRejectionRegion = new JComboBox(ExactTest.REJECTION_TYPES);
		this.cmbAdjustPvalueMethod = new JComboBox(TopTags.pAdustMethods);
//		this.cmbExactTestDisp = new JComboBox<String>();
//		this.cmbExactTestRejectionRegion = new JComboBox<String>();
//		this.cmbAdjustPvalueMethod = new JComboBox<String>();
		
		JLabel lblRejectionregion = new JLabel("rejection.region");
		
		JLabel lblPriorcount = new JLabel("prior.count");

		/*** Over JDK 1.7 ***/
//		this.lstTotalGroups = new JList<String>();
//		this.lstChoosedGroups = new JList<String>();
		/*** Under JDK 1.7 ***/
		this.lstTotalGroups = new JList();
		this.lstChoosedGroups = new JList();
		
		JLabel lblChooseTwoGroups = new JLabel("Choose two groups to test");
		
		this.btnToRight = new JButton(ImageConstant.rightArrowIcon);
		btnToRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*** Over JDK 1.7 ***/
//				String item = remote.lstTotalGroups.getSelectedValue();
				/*** Under JDK 1.7 ***/
				String item = remote.lstTotalGroups.getSelectedValue().toString();
				int index = remote.lstTotalGroups.getSelectedIndex();

				/*** Over JDK 1.7 ***/
//				DefaultListModel<String> model = (DefaultListModel<String>) remote.lstTotalGroups.getModel();
				/*** Under JDK 1.7 ***/
				DefaultListModel model = (DefaultListModel) remote.lstTotalGroups.getModel();
				model.remove(index);

				if( remote.lstChoosedGroups.getModel() == null )	{
					/*** Over JDK 1.7 ***/
//					DefaultListModel<String> choosedListModel = new DefaultListModel<String>();
					/*** Under JDK 1.7 ***/
					DefaultListModel choosedListModel = new DefaultListModel();
					choosedListModel.addElement( item );
					remote.lstChoosedGroups.setModel( choosedListModel );
				}else {
					/*** Over JDK 1.7 ***/
//					DefaultListModel<String> choosedListModel = (DefaultListModel<String>) remote.lstChoosedGroups.getModel();
					/*** Under JDK 1.7 ***/
					DefaultListModel choosedListModel = (DefaultListModel) remote.lstChoosedGroups.getModel();
					choosedListModel.addElement( item );
				}
			}
		});
		this.btnToLeft = new JButton(ImageConstant.leftArrowIcon);
		btnToLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*** Over JDK 1.7 ***/
//				String item = remote.lstChoosedGroups.getSelectedValue();
				/*** Under JDK 1.7 ***/
				String item = remote.lstChoosedGroups.getSelectedValue().toString();
				int index = remote.lstChoosedGroups.getSelectedIndex();

				/*** Over JDK 1.7 ***/
//				DefaultListModel<String> model = (DefaultListModel<String>) remote.lstChoosedGroups.getModel();
				/*** Under JDK 1.7 ***/
				DefaultListModel model = (DefaultListModel) remote.lstChoosedGroups.getModel();
				model.remove(index);

				if( remote.lstTotalGroups.getModel() == null )	{
					/*** Over JDK 1.7 ***/
//					DefaultListModel<String> totalListModel = new DefaultListModel<String>();
					/*** Under JDK 1.7 ***/
					DefaultListModel totalListModel = new DefaultListModel();
					totalListModel.addElement( item );
					remote.lstTotalGroups.setModel( totalListModel );
				}else {
					/*** Over JDK 1.7 ***/
//					DefaultListModel<String> totalListModel = (DefaultListModel<String>) remote.lstTotalGroups.getModel();
					/*** Under JDK 1.7 ***/
					DefaultListModel totalListModel = (DefaultListModel) remote.lstTotalGroups.getModel();
					totalListModel.addElement( item );
				}
			}
		});
		
		JLabel lblAdjustPvalueMethod = new JLabel("adj p.val method");

		GroupLayout gl_exactTestBorderPanel = new GroupLayout(exactTestBorderPanel);
		gl_exactTestBorderPanel.setHorizontalGroup(
			gl_exactTestBorderPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_exactTestBorderPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_exactTestBorderPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_exactTestBorderPanel.createSequentialGroup()
							.addComponent(lstTotalGroups, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_exactTestBorderPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(btnToLeft, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnToRight, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))
							.addGap(11)
							.addComponent(lstChoosedGroups, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
						.addGroup(gl_exactTestBorderPanel.createSequentialGroup()
							.addGroup(gl_exactTestBorderPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblRejectionregion, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblDispersion, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblAdjustPvalueMethod))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_exactTestBorderPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_exactTestBorderPanel.createSequentialGroup()
									.addComponent(cmbExactTestDisp, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblBigcount, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(txtExactTestBigCount, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_exactTestBorderPanel.createSequentialGroup()
									.addGroup(gl_exactTestBorderPanel.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(cmbAdjustPvalueMethod, Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(cmbExactTestRejectionRegion, Alignment.LEADING, 0, 103, Short.MAX_VALUE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblPriorcount, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(txtExactTestPriorCount, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))))
						.addComponent(lblChooseTwoGroups))
					.addContainerGap())
		);
		gl_exactTestBorderPanel.setVerticalGroup(
			gl_exactTestBorderPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_exactTestBorderPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_exactTestBorderPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDispersion)
						.addComponent(cmbExactTestDisp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblBigcount)
						.addComponent(txtExactTestBigCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_exactTestBorderPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblRejectionregion)
						.addComponent(cmbExactTestRejectionRegion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPriorcount)
						.addComponent(txtExactTestPriorCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_exactTestBorderPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAdjustPvalueMethod)
						.addComponent(cmbAdjustPvalueMethod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(3)
					.addComponent(lblChooseTwoGroups)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_exactTestBorderPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lstChoosedGroups, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
						.addGroup(gl_exactTestBorderPanel.createSequentialGroup()
							.addComponent(btnToRight)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnToLeft))
						.addComponent(lstTotalGroups, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
					.addContainerGap())
		);
		exactTestBorderPanel.setLayout(gl_exactTestBorderPanel);

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(exactTestBorderPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
						.addComponent(tagwiseDispBorderPanel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 386, Short.MAX_VALUE)
						.addComponent(normalizeBorderPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
						.addComponent(commonDispBorderPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(normalizeBorderPanel, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(commonDispBorderPanel, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tagwiseDispBorderPanel, GroupLayout.PREFERRED_SIZE, 166, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(exactTestBorderPanel, GroupLayout.PREFERRED_SIZE, 227, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		/*** Over JDK 1.7 ***/
//		this.cmbNormalizeMethod = new JComboBox<String>( EdgeR.normalizeMethods );
		/*** Under JDK 1.7 ***/
		this.cmbNormalizeMethod = new JComboBox( EdgeR.normalizeMethods );
//		this.cmbNormalizeMethod = new JComboBox<String>( );
		cmbNormalizeMethod.setEnabled(false);
		this.cmbNormalizeMethod.addItemListener( new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				String selectedMethod = ((JComboBox)e.getSource()).getSelectedItem().toString();

				remote.eventNormalizationMethod( selectedMethod );
			}
		});
		
		if( this.clusterModel.getDGEListObject() != null )	this.cmbNormalizeMethod.setEnabled(false);
		else												this.cmbNormalizeMethod.setEnabled(true);
		
		txtRefColumn = new JTextField();
		txtRefColumn.setEnabled(false);
		txtRefColumn.setColumns(10);
		
		JLabel lblNoRefColumn = new JLabel("method");
		
		JLabel lblRefcolumn = new JLabel("refColumn");
		
		JLabel lblLogratiotrim = new JLabel("logratioTrim");
		
		txtLogratioTrim = new JTextField();
		txtLogratioTrim.setEnabled(false);
		txtLogratioTrim.setText( Double.valueOf(JMsbDeMirPanel._LOGRATIO_TRIM_).toString() );
		txtLogratioTrim.setColumns(10);
		
		JLabel lblSumtrim = new JLabel("sumTrim");
		
		JLabel lblAcutoff = new JLabel("Acutoff");

		chkDoWeighting = new JCheckBox("doWeighting");
		chkDoWeighting.setEnabled(false);
		chkDoWeighting.setSelected(true);
		
		txtSumTrim = new JTextField();
		txtSumTrim.setEnabled(false);
		txtSumTrim.setText( Double.valueOf( JMsbDeMirPanel._SUM_TRIM_).toString() );
		txtSumTrim.setColumns(10);
		
		txtAcutoff = new JTextField();
		txtAcutoff.setEnabled(false);
		txtAcutoff.setText( Double.valueOf( JMsbDeMirPanel._A_CUT_OFF_).toString() );
		txtAcutoff.setColumns(10);
		
		JLabel lblP = new JLabel("p");
		
		txtNormalizeP = new JTextField();
		txtNormalizeP.setEnabled(false);
		txtNormalizeP.setText( Double.valueOf( JMsbDeMirPanel._NORMAL_P_).toString() );
		txtNormalizeP.setColumns(10);
		
		JLabel lblHelp = new JLabel( ImageConstant.helpIcon );
		lblHelp.setToolTipText("Help");
		lblHelp.setCursor( new Cursor(Cursor.HAND_CURSOR) );
		lblHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SwingUtilities.linkToUrl("http://www.bioconductor.org/packages/release/bioc/html/edgeR.html");
			}
		});

		GroupLayout gl_normalizeBorderPanel = new GroupLayout(normalizeBorderPanel);
		gl_normalizeBorderPanel.setHorizontalGroup(
			gl_normalizeBorderPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_normalizeBorderPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_normalizeBorderPanel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblLogratiotrim)
						.addComponent(lblNoRefColumn, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblRefcolumn, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_normalizeBorderPanel.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_normalizeBorderPanel.createSequentialGroup()
							.addGroup(gl_normalizeBorderPanel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(txtLogratioTrim, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtRefColumn, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_normalizeBorderPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSumtrim, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblAcutoff, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_normalizeBorderPanel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(txtAcutoff, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtSumTrim, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblP, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtNormalizeP, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_normalizeBorderPanel.createSequentialGroup()
							.addComponent(cmbNormalizeMethod, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chkDoWeighting, GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblHelp)))
					.addContainerGap())
		);
		gl_normalizeBorderPanel.setVerticalGroup(
			gl_normalizeBorderPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_normalizeBorderPanel.createSequentialGroup()
					.addGroup(gl_normalizeBorderPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_normalizeBorderPanel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_normalizeBorderPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNoRefColumn)
								.addComponent(cmbNormalizeMethod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(chkDoWeighting)))
						.addComponent(lblHelp))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_normalizeBorderPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblRefcolumn)
						.addComponent(txtRefColumn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblSumtrim)
						.addComponent(txtSumTrim, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtNormalizeP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblP))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_normalizeBorderPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblLogratiotrim, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtLogratioTrim, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblAcutoff)
						.addComponent(txtAcutoff, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(6, Short.MAX_VALUE))
		);
		gl_normalizeBorderPanel.linkSize(SwingConstants.HORIZONTAL, new Component[] {txtRefColumn, txtLogratioTrim});
		gl_normalizeBorderPanel.linkSize(SwingConstants.HORIZONTAL, new Component[] {txtSumTrim, txtAcutoff});
		normalizeBorderPanel.setLayout(gl_normalizeBorderPanel);
		panel.setLayout(gl_panel);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setRightComponent(tabbedPane);
		
		JPanel testPanel = new JPanel();
		tabbedPane.addTab("Exact Test", null, testPanel, "Exact Test");

		this.btnTest = new JButton("Test");
		this.btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*** Over JDK 1.7 ***/
//				DefaultListModel<String> lstModel = (DefaultListModel<String>) remote.lstChoosedGroups.getModel();
				/*** Under JDK 1.7 ***/
				DefaultListModel lstModel = (DefaultListModel) remote.lstChoosedGroups.getModel();
				if( lstModel.getSize() != 2 ) {
					JOptionPane.showMessageDialog( remote, "We have to choose 2 groups", "Groups Error", JOptionPane.ERROR_MESSAGE );
					return;
				}
				remote.btnTest.setEnabled(false);

				remote.mc				= new MessageConsole( remote.logTextPane );
				remote.mc.redirectOut();
				remote.mc.redirectErr(java.awt.Color.RED, null);
				remote.mc.setMessageLines( 100 );

				if( remote.cmbNormalizeMethod.getSelectedItem().equals(EdgeR._TMM_) ) {
					if( remote.txtRefColumn.getText().trim().length() > 0 ) {
						if( SwingUtilities.isNumericTextField( remote.txtRefColumn,			"refColumn is only numeric" )		== false )	return;
					}

					if( SwingUtilities.isNumericTextField( remote.txtSumTrim,				"sumTrim is only numeric" )			== false )	return;
					if( SwingUtilities.isNumericTextField( remote.txtLogratioTrim,			"logratioTrim is only numeric" )	== false )	return;
					if( SwingUtilities.isNumericTextField( remote.txtAcutoff,				"ACutoff is only numeric" )			== false )	return;
				}else if( remote.cmbNormalizeMethod.getSelectedItem().equals(EdgeR._UPPERQUARTILE_) ) {
					if( SwingUtilities.isNumericTextField( remote.txtNormalizeP,			"p is only numeric" )				== false )	return;
				}
				
				if( SwingUtilities.isNumericTextField( remote.txtCommonDispTol,				"tolerence is only numeric" )		== false )	return;
				if( SwingUtilities.isNumericTextField( remote.txtCommonDispRowSumFilter,	"rowsum.filter is only numeric" )	== false )	return;

				if( remote.txtRefColumn.getText().trim().length() > 0 ) {
					if(SwingUtilities.isNumericTextField( remote.txtTagwiseDispSpan,		"span is only numeric" )			== false )	return;
				}
				
				if( SwingUtilities.isNumericTextField( remote.txtTagwiseDispPriorDf,		"prior df is only numeric" )		== false )	return;
				if( SwingUtilities.isNumericTextField( remote.txtTagwiseDispGridLength,		"grid.length is only numeric" )		== false )	return;
				if( SwingUtilities.isNumericTextField( remote.txtTagwiseDispGridRangeFrom,	"grid.range is only numeric" )		== false )	return;
				if( SwingUtilities.isNumericTextField( remote.txtTagwiseDispGridRangeTo,	"grid.range is only numeric" )		== false )	return;

				if( SwingUtilities.isNumericTextField( remote.txtExactTestBigCount,			"big.count is only numeric" )		== false )	return;
				if( SwingUtilities.isNumericTextField( remote.txtExactTestPriorCount,		"prior.count is only numeric" )		== false )	return;

				String		method			= remote.cmbNormalizeMethod.getSelectedItem().toString();
				Integer		refColumn		= remote.txtRefColumn.getText().isEmpty()?null:Integer.valueOf(remote.txtRefColumn.getText());
				Double		logRatioTrim	= Double.valueOf( remote.txtLogratioTrim.getText() );
				Double		sumTrim			= Double.valueOf( remote.txtSumTrim.getText() );
				Boolean		doWeighting		= remote.chkDoWeighting.isSelected();
				Double		aCutOff			= Double.valueOf( remote.txtAcutoff.getText() );
				Double		p				= Double.valueOf( remote.txtNormalizeP.getText() );
				
				Long		rowSumFilter	= Long.valueOf( remote.txtCommonDispRowSumFilter.getText() );
				Double		commonDispTol	= Double.valueOf( remote.txtCommonDispTol.getText() );

				try {
					DGEList dge = null;
					if( remote.clusterModel.getDGEListObject() != null )	
						dge = remote.clusterModel.getDGEListObject();
					else {
						// To impute NA value to user input
						remote.clusterModel.imputationWithDialog( remote );
						
						dge = new DGEList( remote.clusterModel.getCountModel() );
						
//						dge.doFilter( 3 );
	
						dge = EdgeR.calcNormFactors( dge, method, refColumn, logRatioTrim, sumTrim, doWeighting, aCutOff, p );
						remote.progressBar.setValue(15);						
						dge = EdgeR.estimateCommonDisp( dge, commonDispTol, rowSumFilter );
						
						remote.clusterModel.setDGEListObject( dge );
					}
					
//					java.io.BufferedWriter out = new java.io.BufferedWriter(new java.io.FileWriter("/Users/lion/Desktop/mat.txt"));
//
//					remote.clusterModel.imputationWithDialog( remote );
////					out.write(s); out.newLine();
////					out.write(s); out.newLine();
//					RealMatrix matrix = remote.clusterModel.getCountModel().getCountData();
//					for(int i=0; i<matrix.getRowDimension(); i++) {
//						if( i==0 ) {
//							out.write("miRNA,");
//							for(int j=0; j<remote.clusterModel.getColNames().length; j++) {
//								out.write( remote.clusterModel.getColNames()[j] );
//								if( j < remote.clusterModel.getColNames().length - 1 )	out.write(",");
//							}
//							out.newLine();
//						}
//						out.write(remote.clusterModel.getRowNames()[i] + ",");
//						for(int j=0; j<matrix.getColumnDimension(); j++) {
//							out.write( Double.toString(matrix.getEntry(i, j)) );
//							if( j < matrix.getColumnDimension() -1 )	out.write(",");
//						}
//						out.newLine();
//					}
//					out.close();
					
					
					remote.progressBar.setValue(25);
					RealVector normalizedFactor = dge.getNormFactors();
					System.out.println("=============================================");
					System.out.println("Library Size & Normalization Factor");
					System.out.println("Sample Name\tGroup\tLib Size\tNormalization Factor");
					for(int i=0; i<normalizedFactor.getDimension(); i++) {
						System.out.println( remote.clusterModel.getColNames()[i] + "\t" + dge.getGroupNames()[i] + "\t" + dge.getLibSize().getEntry(i) + "\t" + normalizedFactor.getEntry(i) );
					}
					System.out.println("=============================================");

					String		trend			= remote.cmbTagwiseDispTrend.getSelectedItem().toString();
					String		tagMethod		= remote.cmbTagwiseDispMethod.getSelectedItem().toString();
					Integer		priorDF			= Double.valueOf(remote.txtTagwiseDispPriorDf.getText()).intValue();
					Double		span			= remote.txtTagwiseDispSpan.getText().isEmpty()?null:Double.valueOf(remote.txtTagwiseDispSpan.getText());
					Integer		gridLength		= Double.valueOf( remote.txtTagwiseDispGridLength.getText() ).intValue();
					Integer		gridRangeFrom	= Double.valueOf(remote.txtTagwiseDispGridRangeFrom.getText()).intValue();
					Integer		gridRangeTo		= Double.valueOf( remote.txtTagwiseDispGridRangeTo.getText() ).intValue();
					Double		tagTol			= Double.valueOf( remote.txtTagwiseDispTol.getText() );

					System.out.println("Estimate Tagwise Dispersion");
					dge = EdgeR.estimateTagwiseDisp( dge, priorDF, trend, span, tagMethod, gridLength, new Integer[]{gridRangeFrom, gridRangeTo}, tagTol );
					remote.progressBar.setValue(50);
					System.out.println("=============================================");


					String[] pair = new String[lstModel.getSize()];
					for(int i=0; i<lstModel.getSize(); i++) {
						/*** Over JDK 1.7 ***/
//						pair[i] = lstModel.getElementAt(i);
						/*** Under JDK 1.7 ***/
						pair[i] = lstModel.getElementAt(i).toString();
					}
					
					String dispersionMethod = remote.cmbExactTestDisp.getSelectedItem().toString();
					String rejectionRegion = remote.cmbExactTestRejectionRegion.getSelectedItem().toString();
					Integer bigCount		= Double.valueOf( remote.txtExactTestBigCount.getText() ).intValue();
					Double priorCount		= Double.valueOf( remote.txtExactTestPriorCount.getText() );

					System.out.println("ExactTest for different two groups");
					Map<String, RealVector> deMir = ExactTest.exactTest( dge, pair, dispersionMethod, rejectionRegion, bigCount, priorCount );
					RealVector pvalueVec	= deMir.get("PValue");
					RealVector aveLogCpmVec = deMir.get("logCPM");
					RealVector logFCVec		= deMir.get("logFC");
					remote.progressBar.setValue(75);
					System.out.println("=============================================");
					
					System.out.println("Adjusting p.value");
					RealVector adjustP		= TopTags.adjustPValue( pvalueVec, remote.cmbAdjustPvalueMethod.getSelectedItem().toString() );
					
					RealVector order		= BasicFunctions.order( adjustP );
					System.out.println("=============================================");
					
//					"miRNA", "logFC", "logCPM", "P.value", "Adjusted P.value"
					UpdatableTableModel tableModel = (UpdatableTableModel)remote.tblExactTestResult.getModel();
					for(int i=0; i<order.getDimension(); i++) {
						double pval		= pvalueVec.getEntry( (int)order.getEntry(i) );
						double logCPM	= aveLogCpmVec.getEntry( (int)order.getEntry(i) );
						double logFC	= logFCVec.getEntry( (int)order.getEntry(i) );
						double adjP		= adjustP.getEntry( (int)order.getEntry(i) );
						String gene		= dge.getRowNames()[ (int)order.getEntry(i) ];
						
						tableModel.addRow( new Object[]{gene, logFC, logCPM, pval, adjP} );
					}

					remote.progressBar.setValue(100);
					
					remote.clusterModel.doRefreshData();
					remote.btnTest.setEnabled(true);
				}catch(Exception ex) {
					remote.clusterModel.setDGEListObject(null);
					remote.clusterModel.doRefreshData();
					remote.btnTest.setEnabled(true);
//					ex.printStackTrace();
					MsbEngine.logger.error(ex);
				}
			}
		});

		JScrollPane exactTestResultScrollPane = new JScrollPane();
		
		JScrollPane logScrollPane = new JScrollPane();
		
		this.progressBar = new JProgressBar();
		GroupLayout gl_panel_4 = new GroupLayout(testPanel);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.TRAILING)
						.addComponent(logScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
						.addComponent(exactTestResultScrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
						.addComponent(btnTest, Alignment.LEADING)
						.addComponent(progressBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_4.setVerticalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnTest)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(exactTestResultScrollPane, GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(logScrollPane, GroupLayout.PREFERRED_SIZE, 262, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		this.logTextPane = new JTextPane();
		this.logTextPane.setEditable(false);
		this.logTextPane.setFont( SwingConst._9_FONT );
		logScrollPane.setViewportView(logTextPane);

		UpdatableTableModel tblModel = new UpdatableTableModel( this.data, this.columns );
		tblExactTestResult = new JTable( tblModel );
		this.tblExactTestResult.setDefaultRenderer( Object.class, new NumberTableCellRenderer() );
		exactTestResultScrollPane.setViewportView(tblExactTestResult);
		testPanel.setLayout(gl_panel_4);
		
		JMsbDemirGraphPanel graphPanel = new JMsbDemirGraphPanel( this.clusterModel.getDGEListObject() );
		tabbedPane.addTab("Graph", null, graphPanel, null);
		setLayout(groupLayout);

		/******************************************************************************************
		 * To initialize 2 JLists
		 */
		String[] groupNames = BasicFunctions.unique(this.clusterModel.getCountModel().getGroupNames());
		/*** Over JDK 1.7 ***/
//		DefaultListModel<String> listModel = new DefaultListModel<String>();
		/*** Under JDK 1.7 ***/
		DefaultListModel listModel = new DefaultListModel();
		for(int i=0; i<groupNames.length; i++) {
			listModel.addElement( groupNames[i] );
		}
		this.lstTotalGroups.setModel( listModel );

		/*** Over JDK 1.7 ***/
//		DefaultListModel<String> choosedListModel = new DefaultListModel<String>();
		/*** Under JDK 1.7 ***/
		DefaultListModel choosedListModel = new DefaultListModel();
		this.lstChoosedGroups.setModel( choosedListModel );

		/******************************************************************************************
		 * To initialize other all fileds with default value
		 */
		this.initComponent();
	}

	private void eventNormalizationMethod( String selectedMethod ) {
		// TODO Auto-generated method stub
		if( selectedMethod.equals( EdgeR._TMM_ ) ) {
			txtRefColumn.setEnabled(true);
			txtSumTrim.setEnabled(true);
			txtLogratioTrim.setEnabled(true);
			txtAcutoff.setEnabled(true);
			chkDoWeighting.setEnabled(true);

			txtNormalizeP.setEnabled(false);
		}else if(selectedMethod.equals(EdgeR._UPPERQUARTILE_) ) {
			txtRefColumn.setEnabled(false);
			txtSumTrim.setEnabled(false);
			txtLogratioTrim.setEnabled(false);
			txtAcutoff.setEnabled(false);
			chkDoWeighting.setEnabled(false);

			txtNormalizeP.setEnabled(true);
		}else {
			txtRefColumn.setEnabled(false);
			txtSumTrim.setEnabled(false);
			txtLogratioTrim.setEnabled(false);
			txtAcutoff.setEnabled(false);
			chkDoWeighting.setEnabled(false);
			txtNormalizeP.setEnabled(false);
		}
	}
	
	public void initComponent() {
		if( this.clusterModel.getDGEListObject() == null ) {
			this.cmbNormalizeMethod.setSelectedItem( JMsbDeMirPanel._NORMALIZE_METHOD_ );
			this.eventNormalizationMethod( JMsbDeMirPanel._NORMALIZE_METHOD_ );
		}
		this.cmbAdjustPvalueMethod.setSelectedItem( JMsbDeMirPanel._ADJ_P_VALUE_ );
		this.cmbExactTestDisp.setSelectedItem( JMsbDeMirPanel._DISPERSION_ );
		this.cmbExactTestRejectionRegion.setSelectedItem( JMsbDeMirPanel._REJECTION_REGION_ );
		this.cmbTagwiseDispMethod.setSelectedItem( JMsbDeMirPanel._TAGWISE_METHOD_ );
		this.cmbTagwiseDispTrend.setSelectedItem( JMsbDeMirPanel._TREND_ );
		
		this.cmbTagwiseDispMethod.setSelectedItem( EdgeR._GRID_ );
		this.cmbTagwiseDispTrend.setSelectedItem( EdgeR._MOVINGAVE_ );
		this.cmbExactTestDisp.setSelectedItem( ExactTest.AUTO );
		this.cmbExactTestRejectionRegion.setSelectedItem( ExactTest.DOUBLE_TAIL );

		this.txtAcutoff.setText( Double.valueOf(JMsbDeMirPanel._A_CUT_OFF_).toString() );
		this.txtCommonDispRowSumFilter.setText( Integer.valueOf(JMsbDeMirPanel._ROW_SUM_FILTER_).toString() );
		this.txtCommonDispTol.setText( Double.valueOf(JMsbDeMirPanel._TOLERANCE_).toString() );
		this.txtExactTestBigCount.setText( Double.valueOf(JMsbDeMirPanel._BIG_COUNT_).toString() );
		this.txtExactTestPriorCount.setText( Double.valueOf(JMsbDeMirPanel._EXACT_TEST_PRIOR_COUNT_).toString() );
		this.txtLogratioTrim.setText( Double.valueOf(JMsbDeMirPanel._LOGRATIO_TRIM_).toString() );
		this.txtNormalizeP.setText( Double.valueOf(JMsbDeMirPanel._NORMAL_P_).toString() );
		this.txtRefColumn.setText("");
		this.txtSumTrim.setText( Double.valueOf(JMsbDeMirPanel._SUM_TRIM_).toString() );
		this.txtTagwiseDispGridLength.setText( Double.valueOf(JMsbDeMirPanel._GRID_LENGTH_).toString() );
		this.txtTagwiseDispGridRangeFrom.setText( Double.valueOf(JMsbDeMirPanel._DISP_GRID_RANGE_FROM_).toString() );
		this.txtTagwiseDispGridRangeTo.setText( Double.valueOf(JMsbDeMirPanel._DISP_GRID_RANGE_TO_).toString() );
		this.txtTagwiseDispPriorDf.setText( Double.valueOf(JMsbDeMirPanel._PRIOR_DF_).toString() );
		this.txtTagwiseDispTol.setText( Double.valueOf(JMsbDeMirPanel._TOLERANCE_).toString() );
		this.chkDoWeighting.setSelected(true);
	}
}
