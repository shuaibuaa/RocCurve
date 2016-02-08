package src;

import java.awt.Color;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.*;
//import org.jfree.ui.Spacer;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DrawPlot extends ApplicationFrame {

    public DrawPlot(final String title,final String x_label,final String y_label,ArrayList<Point> gasdm,ArrayList<Point> nphgs,ArrayList<Point> eventtree) {
        super(title);
        final XYDataset dataset = createDataset(gasdm, nphgs, eventtree);
        final JFreeChart chart = createChart(dataset, title, x_label, y_label);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    private XYDataset createDataset(ArrayList<Point> gasdm, ArrayList<Point> nphgs, ArrayList<Point> eventtree) {
    	final XYSeries series1 = new XYSeries("GASDM");
        for(int i=0;i<gasdm.size();i++)
        	series1.add(gasdm.get(i).x, gasdm.get(i).y);
        
        final XYSeries series2 = new XYSeries("NPHGS");
        for(int i=0;i<nphgs.size();i++)
        	series2.add(nphgs.get(i).x, nphgs.get(i).y);
        
        final XYSeries series3 = new XYSeries("EventTree");
        for(int i=0;i<eventtree.size();i++)
        	series3.add(eventtree.get(i).x, eventtree.get(i).y);
        
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
        return dataset;
    }

    private JFreeChart createChart(final XYDataset dataset, final String title,final String x_label,final String y_label) {
        
        final JFreeChart chart = ChartFactory.createXYLineChart(
            title,      // chart title
            x_label,  	// x axis label
            y_label,    // y axis label
            dataset,                  // data
            PlotOrientation.VERTICAL,
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

        final NumberAxis domainAxis = new NumberAxis("X-Axis");
        domainAxis.setRange(0.0,1.0);
        domainAxis.setTickUnit(new NumberTickUnit(1.0));
        final NumberAxis rangeAxis = new NumberAxis("Y-Axis");
        rangeAxis.setRange(0.0,1.0);
        rangeAxis.setTickUnit(new NumberTickUnit(0.1));
       
        final XYPlot plot = chart.getXYPlot();
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);
                
        return chart;
    }

    public static void draw(final String title,final String x_label,final String y_label,ArrayList<Point> gasdm, ArrayList<Point> nphgs, ArrayList<Point> eventtree){        
        final DrawPlot demo = new DrawPlot(title,x_label,y_label,gasdm, nphgs, eventtree);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
    public static void main(final String[] args) {

    }

}
