import java.util.LinkedHashMap;

import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;


public class MinMaxItemLabelGenerator implements CategoryItemLabelGenerator {

    private LinkedHashMap<String, Pair<Double, String>> minLabels = null;
    private LinkedHashMap<String, Pair<Double, String>> maxLabels = null;
    
    
    
    public void setLabels(LinkedHashMap<String, Pair<Double, String>> minLabels, 
                             LinkedHashMap<String, Pair<Double, String>> maxLabels) {
        this.minLabels = minLabels;
        this.maxLabels = maxLabels;
    }

    @Override
    public String generateRowLabel(CategoryDataset dataset, int row) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String generateColumnLabel(CategoryDataset dataset, int column) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String generateLabel(CategoryDataset dataset, int row, int column) {
        if (minLabels == null || maxLabels == null) return "";
        String minMax = dataset.getRowKey(row).toString();
        String xkey = dataset.getColumnKey(column).toString();
        Number yval = dataset.getValue(row, column);
        
        if (minMax.equals("min")) {
            Pair<Double, String> pair = minLabels.get(xkey);
            if (pair != null && pair.first.equals(yval)) {
                return pair.second;
            }
        } else {
            Pair<Double, String> pair = maxLabels.get(xkey);
            if (pair != null && pair.first.equals(yval)) {
                return pair.second;
            }
        }
        return "";
    }

}
