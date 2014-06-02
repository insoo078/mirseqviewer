package kobic.msb.swing.panel.newproject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import kobic.com.util.Utilities;
import kobic.msb.common.SwingConst;
import kobic.msb.db.sqlite.MicroRnaDB;
import kobic.msb.io.file.BedFileReader;
import kobic.msb.swing.filefilter.BedFileFilter;
import kobic.msb.swing.frame.dialog.JProjectDialog;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.engine.MsbEngine;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;

import net.sf.samtools.util.CollectionUtil;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JMsbProjectInfoPanel extends CommonAbstractNewProjectPanel implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel			mirGroupPanel;
	private JPanel			mirbasePanel;
	
	private JComboBox		cmbVersion;
	private JComboBox		cmbOrganism;
	private JTextField		txtBedFile;
	
	private JTextArea		textArea;
	
	private JMsbProjectInfoPanel remote = JMsbProjectInfoPanel.this;
	/**
	 * Create the panel.
	 */
	public JMsbProjectInfoPanel(JProjectDialog owner) {
		super(owner);

		this.mirGroupPanel = new JPanel();
		this.mirGroupPanel.setBorder( BorderFactory.createTitledBorder( SwingConst.BORDER_LOWERED_ETCHED, "miRNA Reference" ) );

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(mirGroupPanel, GroupLayout.PREFERRED_SIZE, 438, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(mirGroupPanel, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		this.mirbasePanel = new JPanel();
		this.mirbasePanel.setBorder( BorderFactory.createTitledBorder( SwingConst.BORDER_LOWERED_ETCHED, "miRBase Info." ) );
		
		JLabel lblNoteMirnasIn = new JLabel("Note: miRNAs in miRBase are included by default");

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder( SwingConst.BORDER_LOWERED_ETCHED, "Novel miRNA Info. (Optional)" ));
		
		JLabel lblBed = new JLabel("BED File");
		
		txtBedFile = new JTextField();
		txtBedFile.setEditable(false);
		txtBedFile.setColumns(10);
		
		JButton btnFile = new JButton("File");
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter( new BedFileFilter() );

	            int returnVal = fc.showOpenDialog( remote.getOwnerDialog() );

	            if ( returnVal == JFileChooser.APPROVE_OPTION ) {
	                String bedFilePath = fc.getSelectedFile().getAbsolutePath();
	                remote.txtBedFile.setText( bedFilePath );
	                
	                try {
	                	remote.loadBedFile( bedFilePath );
	                }catch(IOException ioe) {
	                	MsbEngine.logger.error("Error", ioe);
	                }
	            }
			}
		});

		this.textArea = new JTextArea();
		this.textArea.setEditable(false);
		JScrollPane textAreaScrollPane = new JScrollPane( this.textArea );
		
		JLabel lblOptionForUnannotated = new JLabel("Option for unannotated miRNA candidates provide genomic region of interest");
		lblOptionForUnannotated.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remote.textArea.setText("");
				remote.txtBedFile.setText("");
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(textAreaScrollPane, GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblBed)
							.addGap(18)
							.addComponent(txtBedFile, GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnFile)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnClear, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblOptionForUnannotated, GroupLayout.PREFERRED_SIZE, 457, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblBed)
						.addComponent(txtBedFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnClear)
						.addComponent(btnFile))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textAreaScrollPane, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
					.addGap(9)
					.addComponent(lblOptionForUnannotated))
		);
		panel.setLayout(gl_panel);
		
		GroupLayout gl_mirGroupPanel = new GroupLayout(mirGroupPanel);
		gl_mirGroupPanel.setHorizontalGroup(
			gl_mirGroupPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_mirGroupPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_mirGroupPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_mirGroupPanel.createSequentialGroup()
							.addComponent(lblNoteMirnasIn)
							.addContainerGap(198, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, gl_mirGroupPanel.createSequentialGroup()
							.addComponent(mirbasePanel, GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
							.addContainerGap())))
		);
		gl_mirGroupPanel.setVerticalGroup(
			gl_mirGroupPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_mirGroupPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(mirbasePanel, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNoteMirnasIn)
					.addContainerGap())
		);
		
		JLabel lblVersion = new JLabel("Version");
		
		this.cmbVersion = new JComboBox();
		
		JLabel lblOrganism = new JLabel("Organism");
		
		this.cmbOrganism = new JComboBox();
		GroupLayout gl_mirbasePanel = new GroupLayout(mirbasePanel);
		gl_mirbasePanel.setHorizontalGroup(
			gl_mirbasePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_mirbasePanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_mirbasePanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblVersion)
						.addComponent(lblOrganism))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_mirbasePanel.createParallelGroup(Alignment.LEADING)
						.addComponent(cmbVersion, GroupLayout.PREFERRED_SIZE, 122, GroupLayout.PREFERRED_SIZE)
						.addComponent(cmbOrganism, GroupLayout.PREFERRED_SIZE, 246, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(83, Short.MAX_VALUE))
		);
		gl_mirbasePanel.setVerticalGroup(
			gl_mirbasePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_mirbasePanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_mirbasePanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblVersion)
						.addComponent(cmbVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_mirbasePanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblOrganism)
						.addComponent(cmbOrganism, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(24, Short.MAX_VALUE))
		);
		mirbasePanel.setLayout(gl_mirbasePanel);
		mirGroupPanel.setLayout(gl_mirGroupPanel);
		setLayout(groupLayout);
		
		
		this.initialize();
	}
	
	public void initialize() {
		LinkedHashMap<String, String>		organMap		= MsbEngine.getInstance().getOrganismMap();
		LinkedHashMap<String, MicroRnaDB>	dbVersionMap	= MsbEngine.getInstance().getMiRBaseMap();

		Iterator<String> iter = organMap.keySet().iterator();
		while( iter.hasNext() ) {
			String organism = iter.next();
			this.cmbOrganism.addItem( organism );
		}
		this.cmbOrganism.setSelectedItem("human");

		List<String> keyList = new ArrayList<String>(dbVersionMap.keySet());
		Collections.sort( keyList, new Comparator<String>() {
		    public int compare(String o1, String o2) {
		        Integer i1 = Integer.parseInt( o1.replace("miRBase", "") );
		        Integer i2 = Integer.parseInt( o2.replace("miRBase", "") );
		        return (i1 > i2 ? -1 : (i1 == i2 ? 0 : 1));
		    }
		});
		
		iter = keyList.iterator();
		while( iter.hasNext() ) {
			String version = iter.next();
			this.cmbVersion.addItem( version );
		}
		this.cmbVersion.setSelectedIndex(0);
	}

	public String getOrganismInfo() {
		return this.cmbOrganism.getSelectedItem().toString();
	}
	
	public String getMiRBaseVersion() {
		return this.cmbVersion.getSelectedItem().toString();
	}
	
	
	
	public String getTxtBedFilePath() {
		return this.txtBedFile.getText();
	}

	public JTextField getTxtBedFile() {
		return this.txtBedFile;
	}
	
	private void loadBedFile( String filename ) throws IOException {
		File file = new File( filename );
		if( file.exists() ) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				this.textArea.append( line + "\n" );
			}
			br.close();
		}
	}
	
	@Override
	public void updateCurrentState(ProjectMapItem projectMapItem) throws Exception {
		// TODO Auto-generated method stub
		if( projectMapItem != null ) {
			if( !Utilities.nulltoEmpty( projectMapItem.getOrganism() ).isEmpty() )			this.cmbOrganism.setSelectedItem( projectMapItem.getOrganism() );
			if( !Utilities.nulltoEmpty( projectMapItem.getMiRBAseVersion() ).isEmpty() )	this.cmbVersion.setSelectedItem( projectMapItem.getMiRBAseVersion() );
			if( projectMapItem.getBedFilePath() != null )	{
				String bedFilePath = projectMapItem.getBedFilePath();
				this.txtBedFile.setText( bedFilePath );

				this.loadBedFile( bedFilePath );
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}
