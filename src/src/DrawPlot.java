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

    public DrawPlot(final String title,final String x_label,final String y_label,ArrayList<Point> result) {
        super(title);
        final XYDataset dataset = createDataset(result);
        final JFreeChart chart = createChart(dataset, title, x_label, y_label);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    private XYDataset createDataset(ArrayList<Point> result) {
    	final XYSeries series = new XYSeries("GASDM");
        for(int i=0;i<result.size();i++)
        	series.add(result.get(i).x, result.get(i).y);
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
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
                
        return chart;
    }

    public static void draw(final String title,final String x_label,final String y_label,ArrayList<Point> result){        
        final DrawPlot demo = new DrawPlot(title,x_label,y_label,result);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
    public static void main(final String[] args) {

    }

}
