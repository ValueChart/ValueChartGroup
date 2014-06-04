import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.*;
import org.jfree.ui.RefineryUtilities;


public class BoxPlotGraph extends JFrame {
    private static final long serialVersionUID = 1L;

    private boolean isDiscrete;
    private DecimalFormat decimalFormat;
    private int width = 500;
    private int height = 500;
    private ValueChart chart;
    private String attributeName;
    private boolean chartReady = false;
    private DefaultBoxAndWhiskerCategoryDataset data = null;
    private CategoryPlot plot;
    
    public BoxPlotGraph(ValueChart ch, String attrName) {        
        decimalFormat = new DecimalFormat("#.##");
        chart = ch;
        setDomain(attrName);
    }
    
    public void setDomain(String attrName) {
        chartReady = false;
        
        attributeName = attrName;
        ArrayList<IndividualAttributeMaps> listOfAttributeMaps = chart.listOfAttributeMaps;
        
        // key: x-coord
        // value: y-coord
        LinkedHashMap<String, ArrayList<Double>> utilityValues = new LinkedHashMap<String, ArrayList<Double>>();
        for (IndividualAttributeMaps iam : listOfAttributeMaps) {
            AttributeDomain dom = iam.attributeDomainMap.get(attributeName);
            if (dom != null) {
                isDiscrete = (dom.getType() == AttributeDomain.DISCRETE ? true : false);        
                
                double[] wts = dom.getWeights();
                String[] xStr = dom.getElements();
                double[] xNum = dom.getKnots();
                    
                for (int i = 0; i < wts.length; i++) {
                    String xkey = (xStr != null ? xStr[i] : decimalFormat.format(xNum[i]));
                    
                    if (!utilityValues.containsKey(xkey)) {
                        ArrayList<Double> yList = new ArrayList<Double>();
                        yList.add(wts[i]);
                        utilityValues.put(xkey, yList);
                    } else {
                        utilityValues.get(xkey).add(wts[i]);
                    }
                    
                }
            }
        }
        
        data = new DefaultBoxAndWhiskerCategoryDataset();
        for (Map.Entry<String, ArrayList<Double>> coord : utilityValues.entrySet()) {
            BoxAndWhiskerItem box = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(coord.getValue());
            data.add(box, attributeName, coord.getKey());
        }

        CategoryAxis xAxis = new CategoryAxis("");
        NumberAxis yAxis = new NumberAxis("");
        yAxis.setAutoRangeIncludesZero(false);
        BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setSeriesPaint(0, Color.black);
        renderer.setMeanVisible(false);
        renderer.setFillBox(false);
        plot = new CategoryPlot();  
        
        plot.setDataset(0, data);
        plot.setRenderer(0, renderer);
        plot.setDomainAxis(0, xAxis);
        plot.setRangeAxis(0, yAxis);

        JFreeChart plotChart = new JFreeChart(
            "Value Function Statistics for " + attrName,
            plot
        );
        ChartPanel chartPanel = new ChartPanel(plotChart);
        chartPanel.setPreferredSize(new Dimension(width, height));
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(chartPanel);
        
        // make user panel
        UserLegendPanel legendMain = new UserLegendPanel(chart, this);
        panel.add(legendMain);
        
        setContentPane(panel);
        
        chartReady = true;
    }

    
    public void showGraph() {
        if (!chartReady) return;
        
        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void plotUserAttributeGraph(String user, Color userColor) {
        ArrayList<IndividualAttributeMaps> listOfAttributeMaps = chart.listOfAttributeMaps;
        
        // key: x-coord
        // value: y-coord
        DefaultCategoryDataset userPlot = new DefaultCategoryDataset();
        for (IndividualAttributeMaps iam : listOfAttributeMaps) {
            if (!iam.userName.equals(user + ".vc")) continue;
            
            AttributeDomain dom = iam.attributeDomainMap.get(attributeName);
            if (dom != null) {
                isDiscrete = (dom.getType() == AttributeDomain.DISCRETE ? true : false);        
                
                double[] wts = dom.getWeights();
                String[] xStr = dom.getElements();
                double[] xNum = dom.getKnots();
                    
                for (int i = 0; i < wts.length; i++) {
                    String xkey = (xStr != null ? xStr[i] : decimalFormat.format(xNum[i]));
                    userPlot.addValue(wts[i], user, xkey);
                }
            }
        }
        
        plot.setDataset(1, userPlot);
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(2));
        renderer.setSeriesPaint(0, userColor);
        Shape shape = new Ellipse2D.Float(-6, -6, 12, 12);
        renderer.setSeriesShape(0, shape);
        renderer.setSeriesFillPaint(0, Color.lightGray);
        renderer.setSeriesOutlineStroke(0, new BasicStroke(3));
        renderer.setSeriesOutlinePaint(0, userColor);
        renderer.setUseFillPaint(true);
        renderer.setUseOutlinePaint(true);
        
        if (isDiscrete) {
            renderer.setSeriesLinesVisible(0, false);
        } 
        
        plot.setRenderer(1, renderer);
    }

    public void clearUserAttributePlot() {
        if (plot.getDatasetCount() > 1) {
            plot.setDataset(1, null);
        }
        repaint();
    }

}
