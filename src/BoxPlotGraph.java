import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
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
    
    public BoxPlotGraph(ValueChart ch, String attrName) {
        super("Value Function Statistics for " + attrName);

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
        renderer.setMeanVisible(false);
        renderer.setFillBox(false);
        CategoryPlot plot = new CategoryPlot(data, xAxis, yAxis, renderer);  

        JFreeChart chart = new JFreeChart(
            "Value Function Statistics for " + attrName,
            plot
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(width, height));
        setContentPane(chartPanel);
        
        chartReady = true;
    }

    
    public void showGraph() {
        if (!chartReady) return;
        
        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

}
