package kobic.msb.system.catalog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

//import com.esotericsoftware.kryo.Kryo;
//import com.esotericsoftware.kryo.io.Input;

import kobic.com.edgeR.model.CountDataModel;
import kobic.com.util.Utilities;
import kobic.msb.common.JMsbSysConst;
import kobic.msb.io.MsbProgressMonitorInputStream;
import kobic.msb.io.file.BedFileReader;
import kobic.msb.io.file.obj.bed.BedFormat;
import kobic.msb.server.model.ClusterModel;
import kobic.msb.server.model.Model;
import kobic.msb.server.model.MsbFilterModel;
import kobic.msb.server.model.MsbSortModel;
import kobic.msb.server.model.jaxb.Msb;
import kobic.msb.server.model.jaxb.Msb.Project;
import kobic.msb.server.model.jaxb.Msb.Project.Samples;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group;
import kobic.msb.server.model.jaxb.Msb.Project.Samples.Group.Sample;
import kobic.msb.server.obj.GroupSamInfo;
import kobic.msb.server.obj.SequenceObject;
import kobic.msb.system.PropertiesController;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;

public class ProjectMapItem extends Observable implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String					projectName;				// Project Name
	private String					path;						// Project Path;
	private String					projectLoadFilePath;		// Project XML file path
	private int						_STATUS;					// Project Current Status

	private Project					projectInfo;				// Project Information
	private ProjectConfiguration	projectConfig;				// Project Configuration
//	private HashMap<String, Model>	_model;						// Choosed miRNAs for analysis
//	private HashMap<String, Model>	_total_models;				// All of miRNAs in your input BAM files
	private HashMap<String, String>	_model;						// Choosed miRNAs model file path for analysis
	private HashMap<String, String>	_total_models;				// All of miRNAs model file path in your input BAM files
	
	private String					miRBaseVersion;
	private String					organism;
	private SequenceObject			novelMicroRnaLocus;
	private String					bedFilePath;
	
	private BedFormat				bedFormat;

	private Object[]				miRnaTableHeader;
	private List<Object[]>			readedAllObjList;			// all of miRNA from files
	private List<Object[]>			choosedRnaObjList;			// choosed miRNA list;
	
	private ClusterModel			clusterModel;				// miRNA expression profile model
	
	private MsbSortModel			sortModel;
	private MsbFilterModel			filterModel;
	
//	private static Kryo kryo = new Kryo();

	public ProjectMapItem() throws JAXBException {
		this( null, null, null, null );
	}

	public ProjectMapItem( String projectName, String path, String projectLoadFilePath, String projectConfigFilePath ) throws JAXBException {
		this.projectName			= projectName;
		this.path					= path;
		this.projectLoadFilePath	= projectLoadFilePath;
		this.projectInfo			= null;
		this.clusterModel			= null;

//		this.isDoneProcess			= false;
		
		this.bedFormat				= null;
		this.bedFilePath			= null;

		File projectConfig			= null;
		
		
//		Sort & Filter model
		this.sortModel						= new MsbSortModel();
		this.filterModel					= new MsbFilterModel();

		{
			/*******************************************************************************
			 * filter such as allowing mis-match count and reverse reads removing is applied 
			 */
			this.filterModel.addModel(0, MsbFilterModel.DEFAULT, "mis match", "<=", "2");
			this.filterModel.addModel(1, MsbFilterModel.AND, "reverse reads", "remove", "false");
		}
		

		if( projectConfigFilePath == null )	projectConfig = new File( this.path + File.separator + ".project.config" );
		else								projectConfig = new File(projectConfigFilePath);

		this.projectConfig 			= new ProjectConfiguration( projectConfig.getAbsolutePath() );

//		this._model					= new LinkedHashMap<String, Model>();
//		this._total_models			= new LinkedHashMap<String, Model>();
		this._model					= new LinkedHashMap<String, String>();
		this._total_models			= new LinkedHashMap<String, String>();

		this._STATUS				= JMsbSysConst.STS_DONE_CHOOSE_ORGANISM;
	}
	
	public void reloadProjectConfiguration() throws Exception{
		File projectConfig = new File(this.path + File.separator + ".project.config");

		PropertiesController controller = new PropertiesController( projectConfig.getAbsolutePath(), "UTF-8" );
		if( !projectConfig.exists() ) {
			controller.storeAll( ProjectConfiguration.map );
		}
		Properties property = controller.loadAll();
		this.projectConfig = new ProjectConfiguration( property );
	}

	private Project _loadJAXBfromFile( String xmlFilePath ) throws JAXBException {
		File file = new File( xmlFilePath );
//		System.out.println( xmlFilePath );
		if( file.exists() ) {
			JAXBContext jaxbContext = JAXBContext.newInstance( Msb.class );

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Msb msb = (Msb) jaxbUnmarshaller.unmarshal( file );

			return msb.getProject();
		}
		return null;
	}
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getProjectLoadFilePath() {
		return projectLoadFilePath;
	}
	public void setProjectLoadFilePath(String projectLoadFilePath) {
		this.projectLoadFilePath	= projectLoadFilePath;
	}
	public List<String> getMiRnaList() {
		List<String> mirnaList = new ArrayList<String>();

		mirnaList.addAll( this._model.keySet() );

		return mirnaList;
	}
//	public HashMap<String, Model> getModelMap() {
//		return this._model;
//	}
//	public HashMap<String, Model> getTotalModelMap() {
//		return this._total_models;
//	}
//	public Model getProjectModel(String mirid) {
//		return this._model.get( mirid );
//	}
	public HashMap<String, String> getModelMap() {
		return this._model;
	}
	public HashMap<String, String> getTotalModelMap() {
		return this._total_models;
	}
	public Model getProjectModel(String mirid) {
		Model model = null;
//		MsbEngine.logger.debug("mirid = " + mirid );
		try {
			String filePath = this._total_models.get(mirid);
			
			if( filePath != null && new File(filePath).exists() ) {
				FileInputStream fis = new FileInputStream( filePath );
	
	            BufferedInputStream bufferedInputStream = new BufferedInputStream(fis);
	
	//			ProgressMonitorInputStream pmis = new ProgressMonitorInputStream( null, "Loading " + mirid + " model", bufferedInputStream );
	
		        ObjectInputStream ois = new ObjectInputStream( bufferedInputStream );
	
		    	model = (Model)ois.readObject();
	
		        ois.close();
		        bufferedInputStream.close();
	//	        pmis.close();
		        fis.close();
			}
		}catch(Exception e) {
			MsbEngine.logger.error("Load Model Error : ", e);
			MsbEngine.logger.error("Error mirid " + mirid);
		}
        return model;
	}
	public Model getProjectModel(String mirid, JProgressBar progress) {
		Model model = null;
//		MsbEngine.logger.debug("mirid = " + mirid );
		try {
			String filePath = this._total_models.get(mirid);
			FileInputStream fis = new FileInputStream( filePath );

            BufferedInputStream bufferedInputStream = new BufferedInputStream(fis);

            progress.setValue(0);
            MsbProgressMonitorInputStream pmis = new MsbProgressMonitorInputStream(null, "Loading " + mirid + " model", bufferedInputStream, progress );

	        ObjectInputStream ois = new ObjectInputStream( pmis );

	    	model = (Model)ois.readObject();

	        ois.close();
	        pmis.close();
	        fis.close();
		}catch(Exception e) {
			MsbEngine.logger.error("Load Model Error : ", e);
		}
        return model;
	}

//	public Model getProjectModel(String mirid) {
//		Model model = null;
//		try {
//			String filePath = this._total_models.get(mirid);
//			
//			if( filePath != null && new File(filePath).exists() ) {
//				File file = new File(filePath);
//				RandomAccessFile raf = new RandomAccessFile( file, "rw");
//
//				FileInputStream fis = new FileInputStream( filePath );
//				
//				Input input = new Input( fis, 4096 );
//				
//			    model = (Model)kryo.readObject(input, Model.class);
//
//			    input.close();
//			    raf.close();
//			    fis.close();
//			}
//		}catch(Exception e) {
//			MsbEngine.logger.error("Load Model Error : ", e);
//			MsbEngine.logger.error("Error mirid " + mirid);
//		}
//        return model;
//	}

//	public Model getProjectModel(String mirid, JProgressBar progress) {
//		Model model = null;
////		MsbEngine.logger.debug("mirid = " + mirid );
//		try {
//			String filePath = this._total_models.get(mirid);
//			
//			File file = new File(filePath);
//			RandomAccessFile raf = new RandomAccessFile( file, "rw");
//
//			FileInputStream fis = new FileInputStream( filePath );
//			
//			Input input = new Input( fis, 4096 );
//			
//		    model = (Model)kryo.readObject(input, Model.class);
//
//		    input.close();
//		    raf.close();
//		    fis.close();
//		}catch(Exception e) {
//			MsbEngine.logger.error("Load Model Error : ", e);
//		}
//        return model;
//	}

	public void initializeModelMap() {
		this._model.clear();
	}
//	public void addProjectModel( String mirid, Model model ) {
//		if( this._model == null )			this._model = new LinkedHashMap<String, Model>();
//		this._model.put(mirid, model);
//	}
//	public void addProjectAllModel( String mirid, Model model ) {
//		if( this._total_models == null )	this._total_models = new LinkedHashMap<String, Model>();
//		this._total_models.put(mirid, model);
//	}
	public void addProjectModel( String mirid, String filePath ) {
		if( this._model == null )			this._model = new LinkedHashMap<String, String>();
		this._model.put(mirid, filePath);
	}
	public void addProjectAllModel( String mirid, String filePath ) {
		if( this._total_models == null )	this._total_models = new LinkedHashMap<String, String>();
		this._total_models.put(mirid, filePath);
	}
	public Project getProjectInfo() {
		return this.projectInfo;
	}
	public void setProjectInfo( String xmlPath ) throws JAXBException {
		this.projectInfo	= this._loadJAXBfromFile( xmlPath );
	}
	public ProjectConfiguration getProjectConfiguration() {
		return this.projectConfig;
	}
	public void setProjectconfiguration(ProjectConfiguration config) {
		this.projectConfig = config;
	}
	public void setProjectStatus(int status) {
		this._STATUS = status;
	}
	public int getProjectStatus() {
		return this._STATUS;
	}
	
	public List<Object[]> getSampleFileList() {
		Samples samples = this.projectInfo.getSamples();

		List<Object[]> fileList = new ArrayList<Object[]>();
		if( samples != null ) {
			for( Iterator<Group> groupIterator = samples.getGroup().iterator(); groupIterator.hasNext(); ) {
				Group group = groupIterator.next();

				for( Iterator<Sample> sampleIterator = group.getSample().iterator(); sampleIterator.hasNext(); ) {
					Sample sample = sampleIterator.next();
					fileList.add( new Object[]{group.getGroupId(), sample.getName(), sample } );
				}
			}
		}
		return fileList;
	}

	public Sample getSample( String groupId, String sampleId ) {
		for(Group group:this.projectInfo.getSamples().getGroup()) {
			if( group.getGroupId().equals( groupId ) ) {
				for(Sample sample:group.getSample()) {
					if( sample.getName().equals( sampleId ) ) {
						return sample;
					}
				}
			}
		}
		return null;
	}

	public void setExpressionProfile( JFrame frame, Object[][] profile ) throws Exception{
		String[] groupNames = Utilities.toConvertFromObjectToStringArray( ProjectMapItem.getGroupMap( this.projectInfo ).toArray() );
		CountDataModel countDataModel = new CountDataModel( profile, groupNames, 0, 0 );

		this.clusterModel = new ClusterModel( countDataModel );
	}

	public void clusteringExpressionProfile() throws Exception {
		this.clusterModel.clusteringExpressionProfile();
	}
	
	public ClusterModel getClusterModel() {
		return this.clusterModel;
	}
	
	private static List<String> getGroupMap( Project projectInfo ) {
		List<String> groupNames = new ArrayList<String>();
		List<Group> info = projectInfo.getSamples().getGroup();
		Iterator<Group> iter = info.iterator();
		while( iter.hasNext() ) {
			Group group = iter.next();
			List<Sample> sampleList = group.getSample();
			for(int i=0; i<sampleList.size(); i++) {
				groupNames.add( group.getGroupId() );
			}
		}
		return groupNames;
	}
	
	public void reset() {
		this._total_models.clear();
		this._model.clear();
		
		this.clusterModel = null;
	}
	
	public void setOrganism(String organism) {
		this.organism = organism;
	}
	
	public void setMiRBaseVersion(String version) {
		this.miRBaseVersion = version;
	}
	
	public String getOrganism() {
		return this.organism;
	}
	
	public String getMiRBAseVersion() {
		return this.miRBaseVersion;
	}

	
	/** 20131202 added by insu jang **/
	public Object[] getMiRnaTableHeader() {
		return miRnaTableHeader;
	}

	public void setMiRnaTableHeader(Object[] miRnaTableHeader) {
		this.miRnaTableHeader = miRnaTableHeader;
	}

	public List<Object[]> getReadedAllObjList() {
		return readedAllObjList;
	}

	public void setReadedAllObjList(List<Object[]> readedAllObjList) {
		this.readedAllObjList = readedAllObjList;
	}

	public List<Object[]> getChoosedRnaObjList() {
		return choosedRnaObjList;
	}

	public void setChoosedRnaObjList(List<Object[]> choosedRnaObjList) {
		this.choosedRnaObjList = choosedRnaObjList;
	}
	
	public void setNovelMicroRnaLocus(SequenceObject locusObj) {
		this.novelMicroRnaLocus = locusObj;
	}
	
	public SequenceObject getNovelMicroRnaLocus() {
		return this.novelMicroRnaLocus;
	}

	public String getBedFilePath() {
		return bedFilePath;
	}

	public void setBedFilePath(String bedFilePath) {
		this.bedFilePath = bedFilePath;
	}
	
	public void readBedFileToProject() throws NumberFormatException, IOException {
		File file = new File( this.bedFilePath );
		if( file.exists() ) {
			BedFileReader bfr = new BedFileReader( new FileReader( this.bedFilePath ) );
			this.bedFormat = bfr.readBedFile();
			bfr.close();
		}
	}

	public BedFormat getBedFormat() {
		return bedFormat;
	}

	public void setBedFormat(BedFormat bedFormat) {
		this.bedFormat = bedFormat;
	}
	
	public MsbSortModel getMsbSortModel() {
		return this.sortModel;
	}

	public MsbFilterModel getMsbFilterModel() {
		return this.filterModel;
	}
}
