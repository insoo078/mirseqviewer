package kobic.msb.swing.frame.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import kobic.msb.common.ImageConstant;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.common.SwingConst;
import kobic.msb.swing.listener.projectdialog.CreateProjectListener;
import kobic.msb.swing.panel.newproject.JBamFilePreProcessingPanel;
import kobic.msb.swing.panel.newproject.JMsbMatureChoosePanel;
import kobic.msb.swing.panel.newproject.JMsbProjectInfoPanel;
import kobic.msb.swing.panel.newproject.JMsvGroupControlPanel;
import kobic.msb.swing.panel.newproject.JMsvNewProjectPanel;
import kobic.msb.swing.panel.newproject.JNewProjectPanel;
import kobic.msb.swing.thread.ThreadManager;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

public class JProjectDialog extends JCommonNewProjectDialog implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String EACH_SAMPLE_FILE_TO_PROJECT = "each";
	public static final String MULTIPLE_SAMPLE_FILES_TO_PROJECT = "multiple";

	private final JPanel contentPanel = new JPanel();
	private JLabel						lblNewLabel;
	private JLabel						lblCreateANew;
	private JTextField					txtProjectName;
	private JProgressBar				progressBar;
	private JTabbedPane					tabbedPane;

//	private JButton						nextButton;
	private JButton						backButton;
	private JButton						cancelButton;
	private JButton						finishButton;
	
	private JMsbProjectInfoPanel		projectInfoPanel;
//	private JNewProjectPanel			projectPanel;
	private JMsvGroupControlPanel		projectPanel;
	private JBamFilePreProcessingPanel	bamFilePreProcessinPanel;
	private JMsbMatureChoosePanel		chooseMirnaPanel;
	
	private ThreadManager				threadManager;

//	private JMsbBrowserMainFrame		frame;
	
	private ProjectMapItem				projectItem;

	private final JProjectDialog		remote		= JProjectDialog.this;
	
	private boolean						isedit;
	
	private String						step1PanelType;

	private static String getWhichStr(boolean isedit, String trueStr, String falseStr) {
		if(isedit)	return trueStr;
		return falseStr;
	}
	
	public JProjectDialog( Frame owner, String title, boolean isEdit, Dialog.ModalityType modalType, boolean isEditDialog, String nType ) throws Exception{
		this( owner, title, isEdit, modalType, null, isEditDialog, nType );
	}
//	
//	public JProjectDialog( Frame owner, String title, boolean isEdit, Dialog.ModalityType modalType, ProjectMapItem item, boolean isEditDialog ) throws Exception{
//		this( owner, title, isEdit, modalType, item, isEditDialog, JProjectDialog.EACH_SAMPLE_FILE_TO_PROJECT );
//	}

	/**
	 * Create the dialog.
	 */
	public JProjectDialog( Frame owner, String title, boolean isEdit, Dialog.ModalityType modalType, ProjectMapItem item, boolean isEditDialog, String type ) throws Exception{
		super( owner, title,isEdit, modalType, "Next >", isEditDialog );

		this.setResizable(false);
		
		this.step1PanelType = type;

		this.isedit			= isEdit;
		this.projectItem	= item;

		this.tabbedPane = new JTabbedPane(SwingConstants.TOP);
		this.tabbedPane.setBounds(6, 111, 557, 422);
		this.contentPanel.add( this.tabbedPane );
		this.tabbedPane.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
//				try {
//					remote.updateCurrentState( remote.projectItem );
//				} catch (Exception e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
			}
		});
		
		JLabel lblProjectName = new JLabel("Project Name");
		lblProjectName.setBounds(19, 83, 102, 16);
		this.contentPanel.add(lblProjectName);
		
		this.txtProjectName = new JTextField();
		this.txtProjectName.setBounds(114, 77, 194, 28);
		this.contentPanel.add( this.txtProjectName );
		this.txtProjectName.setColumns(10);
		
		this.progressBar = new JProgressBar();
		this.progressBar.setBounds(6, 545, 557, 20);
		this.contentPanel.add( this.progressBar );

		this.setBounds(100, 100, 573, 640);
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.getContentPane().add( this.contentPanel, BorderLayout.CENTER );
		this.contentPanel.setLayout(null);

		{
			JPanel headerPanel = new JPanel();
			headerPanel.setBounds(0, 0, 573, 65);
			this.contentPanel.add(headerPanel);
			headerPanel.setBackground( Color.WHITE );
			headerPanel.setBorder( BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray ) );
			{
				this.lblNewLabel = new JLabel( JProjectDialog.getWhichStr(this.isedit, "Edit", "New") + " Project" );
			}
			this.lblCreateANew = new JLabel( JProjectDialog.getWhichStr(this.isedit, "Modify", "Create") + " a new miRseq Viewer project." );
			this.lblCreateANew.setFont(new Font("Lucida Grande", Font.PLAIN, 10));

			JLabel lblNewProjectLogo = new JLabel( ImageConstant.new_project_img );
			lblNewProjectLogo.setSize(30, 30);

			GroupLayout gl_headerPanel = new GroupLayout(headerPanel);
			gl_headerPanel.setHorizontalGroup(
				gl_headerPanel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_headerPanel.createSequentialGroup()
						.addGap(31)
						.addGroup(gl_headerPanel.createParallelGroup(Alignment.LEADING)
							.addComponent(lblNewLabel)
							.addGroup(gl_headerPanel.createSequentialGroup()
								.addGap(6)
								.addComponent(lblCreateANew)))
						.addPreferredGap(ComponentPlacement.RELATED, 207, Short.MAX_VALUE)
						.addComponent(lblNewProjectLogo, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
			);
			gl_headerPanel.setVerticalGroup(
				gl_headerPanel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_headerPanel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_headerPanel.createParallelGroup(Alignment.LEADING)
							.addComponent(lblNewProjectLogo, GroupLayout.PREFERRED_SIZE, 52, Short.MAX_VALUE)
							.addGroup(gl_headerPanel.createSequentialGroup()
								.addComponent(lblNewLabel)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(lblCreateANew)))
						.addContainerGap())
			);
			headerPanel.setLayout(gl_headerPanel);
		}

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPane.setBorder( BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray ) );
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				this.backButton = new JButton("< Back");
				this.backButton.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
				this.backButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						remote.tabbedPane.setSelectedIndex( remote.tabbedPane.getSelectedIndex() - 1 );
					}
				});
				{
					this.finishButton = new JButton("Finish");
					this.finishButton.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
					this.finishButton.addActionListener( new CreateProjectListener( this ) );
					this.finishButton.setActionCommand("Finish");
					this.finishButton.setVisible(false);
					buttonPane.add(this.finishButton);
				}
				this.backButton.setActionCommand("Back");
				buttonPane.add(this.backButton);
			}
			buttonPane.add( new JSeparator() );
			{
//				this.nextButton = new JButton("Next >");
//				this.nextButton = this.getNextButton();
				JButton nextButton = this.getNextButton();
				
				nextButton.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
				nextButton.addActionListener( new CreateProjectListener( this ) );
				nextButton.setActionCommand("Next");
				buttonPane.add(nextButton);
				getRootPane().setDefaultButton(nextButton);
			}
			buttonPane.add( Box.createHorizontalStrut(2) );
			buttonPane.add( new JSeparator() );
			buttonPane.add( Box.createHorizontalStrut(2) );
			{
				this.cancelButton = new JButton("Cancel");
				this.cancelButton.setFont( SwingConst.DEFAULT_DIALOG_FONT_BOLD );
				this.cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						remote.doCancelBackgroundWork();
					}
				});
				this.cancelButton.setActionCommand("Cancel");
				buttonPane.add(this.cancelButton);
			}
			buttonPane.add( Box.createHorizontalStrut(2) );
			buttonPane.add( new JSeparator() );
			buttonPane.add( Box.createHorizontalStrut(2) );
			buttonPane.add( Box.createHorizontalStrut(2) );
			buttonPane.add( new JSeparator() );
			buttonPane.add( Box.createHorizontalStrut(2) );
		}

		{
			if( this.step1PanelType.equals( JProjectDialog.EACH_SAMPLE_FILE_TO_PROJECT ) )	this.projectPanel = new JNewProjectPanel( this );
			else																			this.projectPanel = new JMsvNewProjectPanel( this );
			
			this.tabbedPane.addTab( "Project", ImageConstant.projectIcon, this.projectPanel, "User can create a project" );
		}

		{
			this.projectInfoPanel = new JMsbProjectInfoPanel( this );
			
			this.tabbedPane.addTab( "miRNAs Info.", ImageConstant.projectIcon2, this.projectInfoPanel, "Information" );
		}
		
		{
			this.bamFilePreProcessinPanel = new JBamFilePreProcessingPanel( this );

			this.tabbedPane.addTab( "File Preprocessing", ImageConstant.processingIcon, this.bamFilePreProcessinPanel, "File processing in background" );
		}
		
		{
			this.chooseMirnaPanel = new JMsbMatureChoosePanel( this );

			this.tabbedPane.addTab( "miRNA List", ImageConstant.listIcon, this.chooseMirnaPanel, "User can select miRNA(s)" );
		}

		this.threadManager = new ThreadManager( this );

		this.threadManager.getBamIndexCaller().addObserver( this.bamFilePreProcessinPanel );
		this.threadManager.getBamReadCaller().addObserver( this.chooseMirnaPanel );

		this.updateCurrentState( this.projectItem );
	}

	public  void updateCurrentState( ProjectMapItem item ) throws Exception{
		this.projectItem = item;

		JButton nextButton = this.getNextButton();
		if( item == null ) {
			this.tabbedPane.setSelectedIndex( 0 );

			this.backButton.setEnabled(false);
			nextButton.setEnabled( false );
			this.finishButton.setEnabled( false );

			this.tabbedPane.setEnabledAt(0, true);
			this.tabbedPane.setEnabledAt(1, false);
			this.tabbedPane.setEnabledAt(2, false);
			this.tabbedPane.setEnabledAt(3, false);
		}else if( item.getProjectStatus() == JMsbSysConst.STS_DONE_CREATE_EMPTY_PROJECT ) {
			this.tabbedPane.setSelectedIndex( 1 );

			this.backButton.setEnabled( true );
			nextButton.setEnabled( true );
			this.finishButton.setEnabled( true );

			this.tabbedPane.setEnabledAt(0, true);
			this.tabbedPane.setEnabledAt(1, true);
			this.tabbedPane.setEnabledAt(2, false);
			this.tabbedPane.setEnabledAt(3, false);
			
			this.projectPanel.updateCurrentState( this.projectItem );
			this.projectInfoPanel.updateCurrentState( this.projectItem );
		}else if( item.getProjectStatus() == JMsbSysConst.STS_DONE_CHOOSE_ORGANISM ) {
			this.tabbedPane.setSelectedIndex( 2 );

			this.backButton.setEnabled( true );
			nextButton.setEnabled( true );
			this.finishButton.setEnabled( true );

			this.tabbedPane.setEnabledAt(0, true);
			this.tabbedPane.setEnabledAt(1, true);
			this.tabbedPane.setEnabledAt(2, false);
			this.tabbedPane.setEnabledAt(3, false);

			this.projectPanel.updateCurrentState( this.projectItem );
			this.projectInfoPanel.updateCurrentState( this.projectItem );
			this.bamFilePreProcessinPanel.updateCurrentState( this.projectItem );
		}else if( item.getProjectStatus() >= JMsbSysConst.STS_DONE_INDEX_FILES ) {
			this.tabbedPane.setSelectedIndex( 3 );

			this.backButton.setEnabled( false );
			nextButton.setEnabled( false );
			this.finishButton.setEnabled( false );

			this.tabbedPane.setEnabledAt(0, true);
			this.tabbedPane.setEnabledAt(1, true);
			this.tabbedPane.setEnabledAt(2, true);
			this.tabbedPane.setEnabledAt(3, true);

			this.projectPanel.updateCurrentState( this.projectItem );
			this.projectInfoPanel.updateCurrentState( this.projectItem );
			this.bamFilePreProcessinPanel.updateCurrentState( this.projectItem );
			this.chooseMirnaPanel.updateCurrentState( this.projectItem );
		}
	}
	
	public void allActivateButtons() {
		JButton nextButton = this.getNextButton();
		this.backButton.setEnabled( true );
		nextButton.setEnabled( true );
		this.finishButton.setEnabled( true );
	}

	@Override
	public void setProgressToGetMiRnas( final int pos ) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	remote.progressBar.setValue( pos );
	        }
		});
	}
	
	public void setIndeterminate( final boolean value ) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	remote.progressBar.setIndeterminate( value );
	        }
		});
	}
	
	public boolean isIndeterminate() {
		return this.progressBar.isIndeterminate();
	}
	
	public void setProgressBarRange(final int a, final int b) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	remote.progressBar.setMaximum(b);
	        	remote.progressBar.setMinimum(a);
	        }
		});
	}
	
	public int getProgressToGetMiRnas() {
		return remote.progressBar.getValue();
	}

	public JTabbedPane					getTabbedPane()					{	return this.tabbedPane;					}
//	public JNewProjectPanel				getNewProjectPanel()			{	return this.projectPanel;				}
	public JMsvGroupControlPanel		getNewProjectPanel()			{	return this.projectPanel;				}
	public JBamFilePreProcessingPanel	getBamFilePreProcessingPanel()	{	return this.bamFilePreProcessinPanel;	}
	public JMsbMatureChoosePanel		getMirnaChoosePanel()			{	return this.chooseMirnaPanel;			}
	public JMsbProjectInfoPanel			getProjectInfoPanel()			{	return this.projectInfoPanel;			}
	@Override
	public JTextField					getTxtProjectName()				{	return this.txtProjectName;				}
	public ProjectMapItem				getProjectMapItem()				{	return this.projectItem;				}
	public ThreadManager				getThreadManager()				{	return this.threadManager;				}
	
//	public JProgressBar					getProgressBar()				{	return this.progressBar;				}
	

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
	}
	@Override
	public JTextArea getTextScrollPane() {
		// TODO Auto-generated method stub
		return this.bamFilePreProcessinPanel.getTextPane();
	}
	@Override
	public void setFocusProjectName() {
		// TODO Auto-generated method stub
		this.txtProjectName.requestFocus();
	}
	
	private void doCancelBackgroundWork() {
		if( MsbEngine.getExecutorService() instanceof java.util.concurrent.ThreadPoolExecutor ) {
			java.util.concurrent.ThreadPoolExecutor tpe = (ThreadPoolExecutor) MsbEngine.getExecutorService();
			
			if( tpe.getActiveCount() > 0 ) {
				int result = JOptionPane.showConfirmDialog( remote, "Are you really want to stop the background process?", "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
				if( result == JOptionPane.OK_OPTION ) {
					MsbEngine.getExecutorService().shutdownNow();
				}
			}else {
				remote.dispose();
			}
		}
	}
}
