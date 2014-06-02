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
    
    public static HashMap<String, Integer> rankVarianceScore(ArrayList<HashMap<String, Double>> listOfWeightMaps, HashMap<String, Double> maxWeightMap) {
        HashMap<String, ArrayList<Double>> attributeVals = new HashMap<String, ArrayList<Double>>();
        HashMap<String, Double> attributeMeans = new HashMap<String, Double>();
        
        double totalScale = 0;
        for (Double w : maxWeightMap.values()) {
            totalScale += w;
        }
        
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
            double maxWeight = maxWeightMap.get(pair.getKey());
            double scale = maxWeight / totalScale;
            double var = 0;
            ArrayList<Double> vals = attributeVals.get(pair.getKey());
            double mean = pair.getValue();
            mean = mean / vals.size();
                    
            for (Double v : vals) {
                var += Math.pow(v-mean, 2);
            }
            var = var / (vals.size()-1) * scale;
            vars.put(pair.getKey(), var);
            
            if (var < varMin)
                varMin = var;
            if (var > varMax)
                varMax = var;
        }
        
        if (varMin >= varMax) return null;
        
        HashMap<String, Integer> rankVarScore = new HashMap<String, Integer>();
        for (Map.Entry<String, Double> var : vars.entrySet()) {
            int rank = (int) Math.round((var.getValue() - varMin) / (varMax - varMin) * 8);
            rankVarScore.put(var.getKey(), rank);
        }
        
        return rankVarScore;
    }
}
