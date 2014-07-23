package kobic.msb.swing.panel.summary;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;

import kobic.msb.common.util.SwingUtilities;
import kobic.msb.server.model.Model;
import kobic.msb.server.obj.GeneralReadObject;
import kobic.msb.server.obj.NucleotideObject;
import kobic.msb.server.obj.ReadFragmentByCigar;
import kobic.msb.server.obj.ReadObject;
import kobic.msb.server.obj.RnaSecondaryStructureObj;
import kobic.msb.swing.canvas.AbstractDockingWindowObj;
import kobic.msb.swing.canvas.AlignmentDockingWindowObj;
import kobic.msb.swing.panel.alignment.obj.AlignedNucleotide;
import kobic.msb.swing.panel.alignment.obj.AlignedReadSequence;
import kobic.msb.swing.panel.alignment.obj.GeneralAlignedReadSequence;
import kobic.msb.swing.panel.track.TrackItem;
import kobic.msb.system.catalog.ProjectMapItem;
import kobic.msb.system.config.ProjectConfiguration;
import kobic.msb.system.engine.MsbEngine;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class JMsbProjectMiRnaReportPanel extends JPanel implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String						projectName;
	private AbstractDockingWindowObj	dockWindow;
	private ProjectMapItem				projectMapItem;
	private Model						model;
	private ProjectConfiguration		config;
	
	private Map<String, Integer>		countMap;
	private Map<Integer, Integer>		misMatchMap;
	private Map<Integer, Number>		readLengthMap;
	private Map<String, Integer>		enrichedMatureMap;
	
	private ChartPanel 					nucleotidePercentChartPanel;
	private ChartPanel					misMatchDistPanel;
	private ChartPanel					readLengthPanel;
	private ChartPanel					enrichedMaturePanel;
	
//	private JFreeChart pieChart;

	/**
	 * Create the panel.
	 */
	public JMsbProjectMiRnaReportPanel( AbstractDockingWindowObj dockingWindow, String projectName, String mirid ) {
		try {
			this.dockWindow 	= dockingWindow;
			this.projectName	= projectName;

			this.projectMapItem = MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName );
			if( this.dockWindow instanceof AlignmentDockingWindowObj ) {
				AlignmentDockingWindowObj adw = (AlignmentDockingWindowObj) this.dockWindow;
				this.model			= adw.getCurrentModel();
			}
	
			this.config			= MsbEngine.getInstance().getProjectManager().getProjectMap().getProject( projectName ).getProjectConfiguration();

			this.countMap						= this.getPropensityTable( this.model.getReferenceSequenceObject().getSequence().get(0).getPosition() );
			this.misMatchMap					= this.getMismatchCountTable();
			this.readLengthMap					= this.getReadLengthTable();
			this.enrichedMatureMap				= this.getEnrichedMatureTable();
	
			JFreeChart ntPercentPieChart		= this.createNtPercentPieChart( this.model.getReferenceSequenceObject().getSequence().get(0).getPosition() );
			JFreeChart misMatchDistBarChart 	= this.createMismatchDistBarChart();
			JFreeChart readLengthBarChart		= this.createReadLengthDistBarChart();
			JFreeChart enrichedMatureChart		= this.createMatureMapDistBarChart();
	
			this.nucleotidePercentChartPanel	= new ChartPanel( ntPercentPieChart );
			this.misMatchDistPanel				= new ChartPanel( misMatchDistBarChart );
			this.readLengthPanel				= new ChartPanel( readLengthBarChart );
			this.enrichedMaturePanel			= new ChartPanel( enrichedMatureChart );
	
			GroupLayout groupLayout = new GroupLayout(this);
			groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.TRAILING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addComponent(readLengthPanel, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
							.addComponent(nucleotidePercentChartPanel, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
							.addComponent(enrichedMaturePanel, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
							.addComponent(misMatchDistPanel, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
						.addGap(10))
			);
			groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(misMatchDistPanel, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
							.addComponent(nucleotidePercentChartPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addComponent(readLengthPanel, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
							.addComponent(enrichedMaturePanel, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(55, Short.MAX_VALUE))
			);
			setLayout(groupLayout);
		}catch(Exception e) {
			MsbEngine.logger.error("Error : ", e);
		}
	}
	
	private Map<String, Integer> getEnrichedMatureTable() throws Exception{
		Map<String, Integer> enrichedMatureMap = new HashMap<String, Integer>();

		if( !this.model.isNovel() ) {
//			List<RnaSecondaryStructureObj> ssOList		= Model.getSeondaryStructuresByHairpinInfo( this.model.getMirnaInfo().getMirid(), this.projectMapItem.getMiRBAseVersion(), this.config );
			List<RnaSecondaryStructureObj> ssOList		= Model.getSeondaryStructuresByHairpinInfo( this.model.getHairpinVo(), this.model.getVoList(), this.config );
	
			for(int idx=0; idx<ssOList.size(); idx++) {
				RnaSecondaryStructureObj obj = ssOList.get(idx);
				long start	= obj.getStart();
				long end	= obj.getEnd();
	
				List<ReadObject> readObj = this.model.getReadList();
				for(int i=0; i<readObj.size(); i++) {
					if( readObj.get(i) instanceof GeneralReadObject ) {
						GeneralReadObject gro = (GeneralReadObject) readObj.get(i);
						
						for(long cnt=gro.getStartPosition(); cnt<=gro.getEndPosition(); cnt++) {
							if( cnt >= start && cnt <= end ) {
								if( enrichedMatureMap.containsKey( obj.getName() ) )	enrichedMatureMap.put( obj.getName(), enrichedMatureMap.get(obj.getName()) + 1 );
								else													enrichedMatureMap.put( obj.getName(), 1 );
	
								break;
							}
						}
					}
				}
			}
		}
		return enrichedMatureMap;
	}

	private Map<Integer, Integer> getMismatchCountTable() throws Exception{
		Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
		if( this.dockWindow instanceof AlignmentDockingWindowObj ) {
			AlignmentDockingWindowObj adw = (AlignmentDockingWindowObj) this.dockWindow;
			List<GeneralAlignedReadSequence> list = adw.getAlignmentPane().getReadSequenceList();
			for(int i=0; i<list.size(); i++) {
//				List<AlignedNucleotide> list2 = list.get(i).getSequence();
//				int count = 0;
//				for(int j=0; j<list2.size(); j++)	if( list2.get(j).isDifferentWithRef() )	count++;
				int count = list.get(i).getMismatchCount();
				if( countMap.containsKey(count) )	countMap.put(count, countMap.get(count) + 1);
				else								countMap.put(count, 1);
			}
		}
		return countMap;
	}

	private Map<Integer, Number> getReadLengthTable() throws Exception {
		List<ReadObject> readObj = this.model.getReadList();
		
		Map<Integer, Number> readLengthMap = new HashMap<Integer, Number>();
		for(int i=0; i<readObj.size(); i++) {
			int length = readObj.get(i).getLength();
			if( readLengthMap.containsKey(length) )	readLengthMap.put(length, readLengthMap.get(length).doubleValue() + 1);
			else									readLengthMap.put(length, 1);
		}
//		Iterator<Integer> iter = readLengthMap.keySet().iterator();
//		while( iter.hasNext() ) {
//			Integer key = iter.next();
//			readLengthMap.put( key, Math.log( readLengthMap.get(key).doubleValue() ) / Math.log(2) );
//		}
		return readLengthMap;
	}

	public Map<String, Integer> getPropensityTable( int pos ) throws Exception {
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		countMap.put( "A", 0);
		countMap.put( "T", 0);
		countMap.put( "G", 0);
		countMap.put( "C", 0);

		List<ReadObject> readObj = this.model.getReadList();
		for(int i=0; i<readObj.size(); i++) {
//			List<NucleotideObject> list = readObj.get(i).getSequence();
//			for(int j=0; j<list.size(); j++) {
//				int originalPos = (int) (list.get(j).getPosition() + this.model.getPrematureSequenceObject().getStartPosition()) - 1;	// original position with reference
//				if(this.model.getReferenceSequenceObject().getStrand() == '-' )
//					originalPos = (int) (this.model.getPrematureSequenceObject().getEndPosition() - list.get(j).getPosition()) + 1;
//
//				if( originalPos == arg ) {
//					String type = list.get(j).getNucleotideType();
//					if( countMap.containsKey( type ) )	countMap.put(type, countMap.get(type) + 1);
//					else								countMap.put(type, 1);
//					break;
//				}
//			}
			if( readObj.get(i) instanceof GeneralReadObject ) {
				GeneralReadObject gro = (GeneralReadObject)readObj.get(i);
//				for(ReadFragmentByCigar cigar : gro.getMsvSamRecord().getCigarElements()) {
				for(ReadObject tro:gro.getRecordElements()) {
//					if( cigar.getStart() <= pos && cigar.getEnd() >= pos ) {
//						ReadObject tro = new ReadObject( cigar.getStart(), cigar.getEnd(), cigar.getReadSeq(), gro.getStrand() );
					if( tro.getStartPosition() <= pos && tro.getEndPosition() >= pos ) {
//						ReadObject tro = new ReadObject( cigar.getStart(), cigar.getEnd(), cigar.getReadSeq(), gro.getStrand() );
						List<NucleotideObject> list = tro.getSequence();
						for(int j=0; j<list.size(); j++) {
							if( pos == list.get(j).getPosition() )	{
								String type = list.get(j).getNucleotideType();
								if( countMap.containsKey( type ) )	countMap.put(type, countMap.get(type) + 1);
								else								countMap.put(type, 1);
								break;
							}
						}
					}
				}
			}else {
				List<NucleotideObject> list = readObj.get(i).getSequence();
				for(int j=0; j<list.size(); j++) {
					if( pos == list.get(j).getPosition() )	{
						String type = list.get(j).getNucleotideType();
						if( countMap.containsKey( type ) )	countMap.put(type, countMap.get(type) + 1);
						else								countMap.put(type, 1);
						break;
					}
				}
			}
		}

		return countMap;
	}

	private JFreeChart createNtPercentPieChart( int pos ) throws Exception {
        DefaultPieDataset data = new DefaultPieDataset();
        Iterator<String> keySetIter = this.countMap.keySet().iterator();
        while( keySetIter.hasNext() ) {
        	String key = keySetIter.next();
        	int value = this.countMap.get(key);
        	data.setValue(key, value);
        }
        JFreeChart chart = ChartFactory.createPieChart("Propensity of nucleotide at " + pos, data, true, true, false);
        
        PiePlot piePlot = (PiePlot) chart.getPlot();

        List<Comparable> keys = data.getKeys();

        piePlot.setBackgroundAlpha(0.2f);
        piePlot.setBackgroundPaint(Color.white);
        for (int i = 0; i < keys.size(); i++) {
        	Color color = TrackItem.getColorByNucleotideFixed( keys.get(i).toString() );
//        	Color color = config.getCytidineColor();
//        	if( keys.get(i).toString().equals("A") ){		color = config.getAdenosineColor(); }
//        	else if( keys.get(i).toString().equals("T") ){	color = config.getThymidineColor();	}
//        	else if( keys.get(i).toString().equals("G") ){	color = config.getGuanosineColor();	}

        	piePlot.setSectionPaint(keys.get(i), color);
        } 

        chart.setAntiAlias(true);
        chart.setBackgroundPaint( Color.white );

        return chart;
    }

	private JFreeChart createMatureMapDistBarChart() throws Exception {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(Iterator<String> iter = this.enrichedMatureMap.keySet().iterator(); iter.hasNext(); ) {
			String series = iter.next();
			int value = this.enrichedMatureMap.get(series);
			dataset.addValue(value, series, "");
		}

        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(
            "Distribution of enriched read for each mature miRNA",
            "Mature miRNA",
            "Frequency",
            dataset,
            PlotOrientation.VERTICAL,
            true,     // include legend
            true,
            false
        );

        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setNoDataMessage("NO DATA!");
        plot.setBackgroundPaint( Color.white );
        plot.setForegroundAlpha(0.4f);
        
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(true);
        renderer.setShadowVisible(false);
        renderer.setBarPainter( new StandardBarPainter() );

        if( this.enrichedMatureMap.size() > 0 ) {
	        if( this.enrichedMatureMap.keySet().toArray()[0].toString().contains("5p") ) {
	        	renderer.setSeriesPaint(0, Color.orange);
	        	renderer.setSeriesPaint(1, Color.green);
	        }else if( this.enrichedMatureMap.keySet().toArray()[0].toString().contains("3p") ) {
	        	renderer.setSeriesPaint(0, Color.green);
	        	renderer.setSeriesPaint(1, Color.orange);
	        }else {
		        renderer.setSeriesPaint(0, Color.red);
		        renderer.setSeriesPaint(1, Color.blue);
	        }
        }

        renderer.setSeriesPaint(2, Color.green);
        renderer.setSeriesPaint(3, Color.yellow);
        renderer.setSeriesPaint(4, Color.orange);
        renderer.setSeriesPaint(5, Color.cyan);
        renderer.setSeriesPaint(6, Color.magenta);
        renderer.setSeriesPaint(7, Color.lightGray);
        renderer.setSeriesPaint(8, Color.pink);
        renderer.setSeriesPaint(9, Color.red);

        renderer.setBaseItemLabelsVisible(true);
        final ItemLabelPosition p = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 45.0);
        renderer.setBasePositiveItemLabelPosition(p);
        plot.setRenderer(renderer);

        // change the margin at the top of the range axis...
        final ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLowerMargin(0.15);
        rangeAxis.setUpperMargin(0.15);
        
        chart.setAntiAlias(true);
        chart.setBackgroundPaint( Color.white );

        return chart;
    }

	private JFreeChart createReadLengthDistBarChart() throws Exception {
        XYSeries series1 = new XYSeries("Series 1");
        Iterator<Integer> iter = this.readLengthMap.keySet().iterator();
        while( iter.hasNext() ) {
        	Integer key = iter.next();
        	series1.add( key, this.readLengthMap.get(key) );
        }
        IntervalXYDataset dataset = new XYBarDataset(new XYSeriesCollection(series1), 1d);
        
        JFreeChart chart = ChartFactory.createXYBarChart("Distribution of read length", "Length", false, "Frequency", dataset, PlotOrientation.VERTICAL, false, false, false);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint( Color.white );

        final XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(true);
        renderer.setShadowXOffset(0.2d);
        renderer.setShadowYOffset(0.2d);
        renderer.setMargin(0.1);
        renderer.setSeriesPaint(0, new Color(235, 235, 235) );
        renderer.setBarPainter( new StandardXYBarPainter() );

        chart.setAntiAlias(true);
        chart.setBackgroundPaint( Color.white );

        return chart;
    }
	
	private JFreeChart createMismatchDistBarChart() throws Exception {
        XYSeries series1 = new XYSeries("Series 1");
        Iterator<Integer> iter = this.misMatchMap.keySet().iterator();
        while( iter.hasNext() ) {
        	Integer key = iter.next();
        	series1.add( key, this.misMatchMap.get(key) );
        }
        IntervalXYDataset dataset = new XYBarDataset(new XYSeriesCollection(series1), 1d);
        
        JFreeChart chart = ChartFactory.createXYBarChart("Distribution of mismatch", "No. of mismatch", false, "Frequency", dataset, PlotOrientation.VERTICAL, false, false, false);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);

        final XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(true);
        renderer.setShadowXOffset(0.2d);
        renderer.setShadowYOffset(0.2d);
        renderer.setMargin(0.1);
        renderer.setSeriesPaint(0, new Color(235, 235, 235) );
        renderer.setBarPainter( new StandardXYBarPainter() );

        chart.setAntiAlias(true);
        chart.setBackgroundPaint( Color.white );

        return chart;
    }

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		this.setBackground( Color.white );
	}

	@Override
	public void update(Observable o, Object arg) {
		try {
			// TODO Auto-generated method stub
			if( arg instanceof Model ) {
				// TODO Auto-generated method stub
				this.model = (Model)arg;
				
				this.misMatchMap		= this.getMismatchCountTable();
				this.readLengthMap		= this.getReadLengthTable();
				this.enrichedMatureMap	= this.getEnrichedMatureTable();
				
				JFreeChart misMatchBarChart		= this.createMismatchDistBarChart();
				JFreeChart readLengthBarChart	= this.createReadLengthDistBarChart();
				JFreeChart enrichedMatureChart	= this.createMatureMapDistBarChart();
				
				this.misMatchDistPanel.setChart( misMatchBarChart );
				this.readLengthPanel.setChart( readLengthBarChart );
				this.enrichedMaturePanel.setChart( enrichedMatureChart );
	
				this.misMatchDistPanel.repaint();
				this.readLengthPanel.repaint();
				this.enrichedMaturePanel.repaint();
			}else if( arg instanceof Integer  ) {
				this.countMap = null;
				this.countMap = this.getPropensityTable( (Integer)arg );
	
				JFreeChart pieChart = createNtPercentPieChart( (Integer)arg );
	
				this.nucleotidePercentChartPanel.setChart( pieChart );
				this.nucleotidePercentChartPanel.repaint();
			}
		}catch(Exception e) {
			MsbEngine.logger.error("Error : ", e);
		}
	}
}
