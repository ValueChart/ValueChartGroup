import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class CriteriaStatistics {
    
    public static final int SCORE = 1, WEIGHT = 2, UTILITY = 3;
    
    // gives rank weight from 0-8, 8 being the strongest colour
    public static HashMap<String, Integer> rankVarianceWeight(LinkedHashMap<String, HashMap<String, Double>> listOfWeightMaps) {
        HashMap<String, ArrayList<Double>> attributeVals = new HashMap<String, ArrayList<Double>>();
        HashMap<String, Double> attributeMeans = new HashMap<String, Double>();
        for (HashMap<String, Double> a : listOfWeightMaps.values()) {
            for (Map.Entry<String, Double> pair : a.entrySet()) {
                String key = pair.getKey();
                Double value = pair.getValue();
                
                if (attributeVals.containsKey(key)) {
                    ArrayList<Double> vals = attributeVals.get(key);
                    vals.add(value);
                    
                    Double sum = attributeMeans.get(key);
                    sum += value;
                    attributeMeans.put(key, sum);
                } else {
                    ArrayList<Double> vals = new ArrayList<Double>();
                    vals.add(value);
                    attributeVals.put(key, vals);
                    attributeMeans.put(key, value);
                }
            }
        }
        
        double varMin = Double.MAX_VALUE;
        double varMax = Double.MIN_VALUE;
        HashMap<String, Double> stdevs = new HashMap<String, Double>();
        for (Map.Entry<String, Double> pair : attributeMeans.entrySet()) {
            double var = 0;
            ArrayList<Double> vals = attributeVals.get(pair.getKey());
            double mean = pair.getValue();
            mean = mean / vals.size();
                    
            for (Double v : vals) {
                var += Math.pow(v-mean, 2);
            }
            var = var / vals.size();
            stdevs.put(pair.getKey(), Math.sqrt(var));
            
            if (var < varMin)
                varMin = var;
            if (var > varMax)
                varMax = var;
        }
        
        if (varMin >= varMax) return null;
        
        double sdMin = Math.sqrt(varMin);
        double sdMax = Math.sqrt(varMax);
        
        HashMap<String, Integer> rankVarWeight = new HashMap<String, Integer>();
        for (Map.Entry<String, Double> sd : stdevs.entrySet()) {
            int rank = (int) Math.round((sd.getValue() - sdMin) / (sdMax - sdMin) * 8);
            rankVarWeight.put(sd.getKey(), rank);
        }
        
        return rankVarWeight;
    }
    
    public static HashMap<String, Integer> rankVarianceScore(ValueChart chart) {
        LinkedHashMap<String,IndividualEntryMap> listOfEntryMaps = chart.listOfEntryMaps;
        HashMap<String,Double> maxWeightMap = chart.maxWeightMap;
        LinkedHashMap<String,HashMap<String,Double>> listOfWeightMaps = chart.listOfWeightMaps;
        
        // for each criteria, for each alternative
        HashMap<String, HashMap<String, Double>> attributeMeans = new HashMap<String, HashMap<String, Double>>();
        HashMap<String, HashMap<String, ArrayList<Double>>> attributeValues = new HashMap<String, HashMap<String, ArrayList<Double>>>();
        
        // for each user
        for (Map.Entry<String, IndividualEntryMap> iem : listOfEntryMaps.entrySet()) {
            
            // for each alternative
            for (Map.Entry<String, ChartEntry> entry : iem.getValue().getEntryMap().entrySet()) {
                
                // for each criteria
                for (Map.Entry<String, AttributeValue> attrVal : entry.getValue().map.entrySet()) {
                    
                    // get criteria 
                    String critkey = attrVal.getKey();
                    HashMap<String, Double> means = attributeMeans.get(critkey);
                    HashMap<String, ArrayList<Double>> valuesMap = attributeValues.get(critkey);
                    if (means == null) {
                        means = new HashMap<String, Double>();
                        attributeMeans.put(critkey, means);
                    } 
                    if (valuesMap == null) {
                        valuesMap = new HashMap<String, ArrayList<Double>>();
                        attributeValues.put(critkey, valuesMap);
                    }
                    
                    // get alternative
                    String attrkey = entry.getKey();
                    ArrayList<Double> values = valuesMap.get(attrkey);
                    
                    // get the weight
                    double val = attrVal.getValue().weight();
                    // scale it relative to the maximum assigned weight
                    double scale = listOfWeightMaps.get(iem.getKey()).get(critkey);
                    scale /= maxWeightMap.get(critkey);
                    val *= scale;
                    
                    if (values == null) {
                        values = new ArrayList<Double>();
                        valuesMap.put(attrkey, values);
                    }
                    values.add(val);
                    
                    Double mean = means.get(attrkey);
                    if (mean == null) {
                        means.put(attrkey, val);
                    } else {
                        means.put(attrkey, mean + val);
                    }
                }
            }
        }
        
        double varMin = Double.MAX_VALUE;
        double varMax = Double.MIN_VALUE;
        HashMap<String, Double> stdevs = new HashMap<String, Double>();
        // for each criteria
        for (Map.Entry<String, HashMap<String, ArrayList<Double>>> valuesMap : attributeValues.entrySet()) {
            // a variance is calculated for each criteria, each alternative
            // will take the median variance to get variance for each criteria
            ArrayList<Double> varMed = new ArrayList<Double>(); 
            
            // for alternative
            for (Map.Entry<String, ArrayList<Double>> values : valuesMap.getValue().entrySet()) {
                
                double var = 0;
                ArrayList<Double> vals = values.getValue();
                Double mean = attributeMeans.get(valuesMap.getKey()).get(values.getKey());
                mean = mean / vals.size();
                        
                for (Double v : vals) {
                    var += Math.pow(v-mean, 2);
                }
                var = var / vals.size();
                varMed.add(var);
            }
            
            int idx = varMed.size()/2;
            double var = varMed.get(idx);
            if (varMed.size() % 2 == 0) {
                var += varMed.get(idx+1);
                var /= 2;
            }
            
            if (var < varMin)
                varMin = var;
            if (var > varMax)
                varMax = var;
            
            stdevs.put(valuesMap.getKey(), Math.sqrt(var));
        }
        
        if (varMin >= varMax) return null;
        
        double sdMin = Math.sqrt(varMin);
        double sdMax = Math.sqrt(varMax);
        
        HashMap<String, Integer> rankVarScore = new HashMap<String, Integer>();
        for (Map.Entry<String, Double> sd : stdevs.entrySet()) {
            int rank = (int) Math.round((sd.getValue() - sdMin) / (sdMax - sdMin) * 8);
            rankVarScore.put(sd.getKey(), rank);
        }
        
        return rankVarScore;
    }
    
    public static HashMap<String, Integer> rankVarianceUtility(ArrayList<IndividualAttributeMaps> listOfAttributeMaps) {
        // for each criteria, for each x-coordinate (if discrete, name; if continuous, string of x-coord)
        HashMap<String, HashMap<String, Double>> attributeMeans = new HashMap<String, HashMap<String, Double>>();
        HashMap<String, HashMap<String, ArrayList<Double>>> attributeValues = new HashMap<String, HashMap<String, ArrayList<Double>>>();
        
        // for each user
        for (IndividualAttributeMaps iam : listOfAttributeMaps) {
            
            // for each criteria
            for (Map.Entry<String, AttributeDomain> attrDom : iam.attributeDomainMap.entrySet()) {
                
                AttributeDomain dom = attrDom.getValue();
                double[] yval = dom.getWeights();
                String[] xStr = dom.getElements();
                double[] xNum = dom.getKnots();
                
                // for each coordinate point
                for (int i = 0; i < yval.length; i++) {
                    DecimalFormat df = new DecimalFormat("#.############"); // in case of roundoff error
                    String xkey = (xStr != null ? xStr[i] : df.format(xNum[i]));
                    
                    // get criteria 
                    String key = attrDom.getKey();
                    HashMap<String, Double> means = attributeMeans.get(key);
                    HashMap<String, ArrayList<Double>> valuesMap = attributeValues.get(key);
                    if (means == null) {
                        means = new HashMap<String, Double>();
                        attributeMeans.put(key, means);
                    } 
                    if (valuesMap == null) {
                        valuesMap = new HashMap<String, ArrayList<Double>>();
                        attributeValues.put(key, valuesMap);
                    }
                    
                    // get coordinate
                    ArrayList<Double> values = valuesMap.get(xkey);
                    
                    // get the y-coordinate
                    double val = yval[i];
                    
                    if (values == null) {
                        values = new ArrayList<Double>();
                        valuesMap.put(xkey, values);
                    }
                    values.add(val);
                    
                    Double mean = means.get(xkey);
                    if (mean == null) {
                        means.put(xkey, val);
                    } else {
                        means.put(xkey, mean + val);
                    }
                }
            }
        }
        
        double varMin = Double.MAX_VALUE;
        double varMax = Double.MIN_VALUE;
        HashMap<String, Double> stdevs = new HashMap<String, Double>();
        // for each criteria
        for (Map.Entry<String, HashMap<String, ArrayList<Double>>> valuesMap : attributeValues.entrySet()) {
            // a variance is calculated for each criteria, each x-coordinate
            // will take the median variance to get variance for each criteria
            ArrayList<Double> varMed = new ArrayList<Double>(); 
            
            // for x-coord
            for (Map.Entry<String, ArrayList<Double>> values : valuesMap.getValue().entrySet()) {
                
                double var = 0;
                ArrayList<Double> vals = values.getValue();
                Double mean = attributeMeans.get(valuesMap.getKey()).get(values.getKey());
                mean = mean / vals.size();
                        
                for (Double v : vals) {
                    var += Math.pow(v-mean, 2);
                }
                var = var / vals.size();
                varMed.add(var);
            }
            
            int idx = varMed.size()/2;
            double var = varMed.get(idx);
            if (varMed.size() % 2 == 0) {
                var += varMed.get(idx+1);
                var /= 2;
            }
            
            if (var < varMin)
                varMin = var;
            if (var > varMax)
                varMax = var;
            
            stdevs.put(valuesMap.getKey(), Math.sqrt(var));
        }
        
        if (varMin >= varMax) return null;
        
        double sdMin = Math.sqrt(varMin);
        double sdMax = Math.sqrt(varMax);
        
        HashMap<String, Integer> rankVarScore = new HashMap<String, Integer>();
        for (Map.Entry<String, Double> sd : stdevs.entrySet()) {
            int rank = (int) Math.round((sd.getValue() - sdMin) / (sdMax - sdMin) * 8);
            rankVarScore.put(sd.getKey(), rank);
        }
        
        return rankVarScore;
    }
}
