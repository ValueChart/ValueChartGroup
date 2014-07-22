import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
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


public class BoxPlotGraph extends JFrame implements ActionListener {
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
    private JCheckBox showAll;
    // indices in plot that contain the respective datasets
    private int boxIndex = 0;
    private int minMaxIndex = 1;
    private int usersStartIndex = 2;
    private DefaultCategoryDataset empty = new DefaultCategoryDataset();
    private DefaultCategoryDataset minMaxData = null;
    private LinkedHashMap<String, Pair<Double, String>> minLabels = null;
    private LinkedHashMap<String, Pair<Double, String>> maxLabels = null;
    
    public BoxPlotGraph(ValueChart ch, String attrName) {        
        decimalFormat = new DecimalFormat("#.##");
        chart = ch;
        setDomain(attrName);
    }
    
    public void setDomain(String attrName) {
        chartReady = false;
        
        attributeName = attrName;
        preparePlot();
    }
    
    private void preparePlot() {
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
        
        plot.setDataset(boxIndex, data);
        plot.setRenderer(boxIndex, renderer);
        plot.setDomainAxis(boxIndex, xAxis);
        plot.setRangeAxis(boxIndex, yAxis);

        JFreeChart plotChart = new JFreeChart(
            "Score Statistics for " + attributeName,
            plot
        );
        ChartPanel chartPanel = new ChartPanel(plotChart);
        chartPanel.setPreferredSize(new Dimension(width, height));
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(chartPanel);
        
        // make user panel
        UserLegendPanel legendMain = new UserLegendPanel(chart, this);
        
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        showAll = new JCheckBox("All Users");
        showAll.setActionCommand("showAll");
        showAll.addActionListener(this);
        showAll.setAlignmentX(LEFT_ALIGNMENT);
        pnl.add(showAll);
        pnl.setAlignmentX(CENTER_ALIGNMENT);
        legendMain.add(pnl);

        panel.add(legendMain);
        setContentPane(panel);
        
        plotMinMax();
        
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
    	if (showAll.isSelected()) return;
        ArrayList<IndividualAttributeMaps> listOfAttributeMaps = chart.listOfAttributeMaps;
        
        // key: x-coord
        // value: y-coord
        DefaultCategoryDataset userPlot = new DefaultCategoryDataset();
        for (IndividualAttributeMaps iam : listOfAttributeMaps) {
            if (!Users.removeExtension(iam.userName).equals(user)) continue;
            
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
        
        plot.setDataset(usersStartIndex, userPlot);
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
        
        plot.setRenderer(usersStartIndex, renderer);
    }
    
    public void plotAllUsers() {
        ArrayList<IndividualAttributeMaps> listOfAttributeMaps = chart.listOfAttributeMaps;
        
        // key: x-coord
        // value: y-coord
        int j = usersStartIndex;
        for (IndividualAttributeMaps iam : listOfAttributeMaps) {
        	DefaultCategoryDataset userPlot = new DefaultCategoryDataset();
            String user = iam.userName;
            user = Users.removeExtension(user);
            Color userColor = GroupActions.getUserColorFromAttributeMap(chart, user);
            
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
            plot.setDataset(j, userPlot);
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
            plot.setRenderer(j, renderer);
            
            j++;
        }
    }
    
    public void plotMinMax() {
        
        String minKey = "min";
        String maxKey = "max";
        Integer minIdx = 1;
        Integer maxIdx = 0;

        if (minMaxData == null) {
            minMaxData = new DefaultCategoryDataset();
            ArrayList<IndividualAttributeMaps> listOfAttributeMaps = chart.listOfAttributeMaps;
            
            // key: xkey
            // value: Pair<yval, users>
            minLabels = new LinkedHashMap<String, Pair<Double, String>>();
            maxLabels = new LinkedHashMap<String, Pair<Double, String>>();
            
            for (IndividualAttributeMaps iam : listOfAttributeMaps) {
                String user = Users.removeExtension(iam.userName);
                AttributeDomain dom = iam.attributeDomainMap.get(attributeName);
                if (dom != null) {
                    double[] wts = dom.getWeights();
                    String[] xStr = dom.getElements();
                    double[] xNum = dom.getKnots();
                        
                    for (int i = 0; i < wts.length; i++) {
                        String xkey = (xStr != null ? xStr[i] : decimalFormat.format(xNum[i]));
                        double yval = wts[i];
                        Pair<Double, String> currMin = minLabels.get(xkey);
                        Pair<Double, String> currMax = maxLabels.get(xkey);
                        if (currMin == null) {
                            minLabels.put(xkey, new Pair<Double, String>(yval, user));
                            maxLabels.put(xkey, new Pair<Double, String>(yval, user));
                        } else {
                            if (currMin.first > yval) {
                                minLabels.put(xkey, new Pair<Double, String>(yval, user));
                            } 
                            if (currMin.first.equals(yval)) {
                                if (currMin.second.charAt(currMin.second.length()-1) != '*') {
                                    currMin.second = currMin.second + ", " + user + "*";
                                }
                            }
                            if (currMax.first < yval) {
                                maxLabels.put(xkey, new Pair<Double, String>(yval, user));
                            } 
                            if (currMax.first.equals(yval)) {
                                if (currMax.second.charAt(currMax.second.length()-1) != '*') {
                                    currMax.second = currMax.second + ", " + user + "*";
                                }
                            }
                        }  
                    }
                }
                
            }
            for (Map.Entry<String, Pair<Double, String>> entry : minLabels.entrySet()) {
                minMaxData.addValue(entry.getValue().first, minKey, entry.getKey());
            }
            for (Map.Entry<String, Pair<Double, String>> entry : maxLabels.entrySet()) {
                minMaxData.addValue(entry.getValue().first, maxKey, entry.getKey());
            }
        }
        
        plot.setDataset(minMaxIndex, minMaxData);
        
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        // min
        renderer.setSeriesStroke(minIdx, new BasicStroke(2));
        renderer.setSeriesPaint(minIdx, Color.darkGray);
        Polygon triangle = new Polygon();
        triangle.addPoint(-4, -4);
        triangle.addPoint(4, -4);
        triangle.addPoint(0, 4);
        renderer.setSeriesShape(minIdx, triangle);
        renderer.setSeriesFillPaint(minIdx, Color.lightGray);
        renderer.setSeriesOutlineStroke(minIdx, new BasicStroke(2));
        renderer.setSeriesOutlinePaint(minIdx, Color.darkGray);
        renderer.setUseFillPaint(true);
        renderer.setUseOutlinePaint(true);
        renderer.setSeriesLinesVisible(minIdx.intValue(), false);
        
        //max
        renderer.setSeriesStroke(maxIdx, new BasicStroke(2));
        renderer.setSeriesPaint(maxIdx, Color.darkGray);
        triangle = new Polygon();
        triangle.addPoint(-4, 4);
        triangle.addPoint(4, 4);
        triangle.addPoint(0, -4);
        renderer.setSeriesShape(maxIdx, triangle);
        renderer.setSeriesFillPaint(maxIdx, Color.lightGray);
        renderer.setSeriesOutlineStroke(maxIdx, new BasicStroke(2));
        renderer.setSeriesOutlinePaint(maxIdx, Color.darkGray);
        renderer.setSeriesLinesVisible(maxIdx.intValue(), false);
        
        MinMaxItemLabelGenerator gen = new MinMaxItemLabelGenerator();
        gen.setLabels(minLabels, maxLabels);
        renderer.setBaseItemLabelGenerator(gen);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(new Font("Verdana", Font.PLAIN, 10));
        
        plot.setRenderer(minMaxIndex, renderer);
    }

    public void clearUserAttributePlot() {
    	if (showAll.isSelected()) return;
        for (int i = usersStartIndex; i < plot.getDatasetCount(); i++) {
        	plot.setDataset(i, null);
        }
        repaint();
    }
    
    public void clearMinMaxUserPlot() {
        if (plot.getDatasetCount() > 1) {
            plot.setDataset(minMaxIndex, empty);
        }
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("showAll")) {
			clearUserAttributePlot();
			if (showAll.isSelected())
				plotAllUsers();
		}
	}

}
