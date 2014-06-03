import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CriteriaStatistics {
    
    public static final int SCORE = 1, WEIGHT = 2, UTILITY = 3;
    
    // gives rank weight from 0-8, 8 being the strongest colour
    public static HashMap<String, Integer> rankVarianceWeight(ArrayList<HashMap<String, Double>> listOfWeightMaps) {
        HashMap<String, ArrayList<Double>> attributeVals = new HashMap<String, ArrayList<Double>>();
        HashMap<String, Double> attributeMeans = new HashMap<String, Double>();
        for (HashMap<String, Double> a : listOfWeightMaps) {
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
        HashMap<String, Double> vars = new HashMap<String, Double>();
        for (Map.Entry<String, Double> pair : attributeMeans.entrySet()) {
            double var = 0;
            ArrayList<Double> vals = attributeVals.get(pair.getKey());
            double mean = pair.getValue();
            mean = mean / vals.size();
                    
            for (Double v : vals) {
                var += Math.pow(v-mean, 2);
            }
            var = var / (vals.size()-1);
            vars.put(pair.getKey(), var);
            
            if (var < varMin)
                varMin = var;
            if (var > varMax)
                varMax = var;
        }
        
        if (varMin >= varMax) return null;
        
        HashMap<String, Integer> rankVarWeight = new HashMap<String, Integer>();
        for (Map.Entry<String, Double> var : vars.entrySet()) {
            int rank = (int) Math.round((var.getValue() - varMin) / (varMax - varMin) * 8);
            rankVarWeight.put(var.getKey(), rank);
        }
        
        return rankVarWeight;
    }
    
    public static HashMap<String, Integer> rankVarianceScore(ArrayList<IndividualEntryMap> listOfEntryMaps) {
        
        // for each criteria, for each alternative
        HashMap<String, HashMap<String, Double>> attributeMeans = new HashMap<String, HashMap<String, Double>>();
        HashMap<String, HashMap<String, ArrayList<Double>>> attributeValues = new HashMap<String, HashMap<String, ArrayList<Double>>>();
        
        // for each user
        for (IndividualEntryMap iem : listOfEntryMaps) {
            
            // for each alternative
            for (Map.Entry<String, ChartEntry> entry : iem.getEntryMap().entrySet()) {
                
                // for each criteria
                for (Map.Entry<String, AttributeValue> attrVal : entry.getValue().map.entrySet()) {
                    
                    // get criteria 
                    String key = attrVal.getKey();
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
                    
                    // get alternative
                    key = entry.getKey();
                    ArrayList<Double> values = valuesMap.get(key);
                    
                    // get the weight
                    double val = attrVal.getValue().weight();
                    
                    if (values == null) {
                        values = new ArrayList<Double>();
                        valuesMap.put(key, values);
                    }
                    values.add(val);
                    
                    Double mean = means.get(key);
                    if (mean == null) {
                        means.put(key, val);
                    } else {
                        means.put(key, mean + val);
                    }
                }
            }
        }
        
        double varMin = Double.MAX_VALUE;
        double varMax = Double.MIN_VALUE;
        HashMap<String, Double> vars = new HashMap<String, Double>();
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
                var = var / (vals.size()-1);
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
            
            vars.put(valuesMap.getKey(), var);
        }
        
        if (varMin >= varMax) return null;
        
        HashMap<String, Integer> rankVarScore = new HashMap<String, Integer>();
        for (Map.Entry<String, Double> var : vars.entrySet()) {
            int rank = (int) Math.round((var.getValue() - varMin) / (varMax - varMin) * 8);
            rankVarScore.put(var.getKey(), rank);
        }
        
        return rankVarScore;
    }
    
    public static HashMap<String, Integer> rankVarianceUtility(ArrayList<IndividualAttributeMaps> listOfAttributeMaps) {
        // TODO
        return null;
    }
}
