import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;


public class AlternativeStatistics {
    
    public static void setMaxWeightMap(ValueChart chart){    
        LinkedHashMap<String,HashMap<String,Double>> listOfWeightMaps = chart.listOfWeightMaps;
        HashMap<String,Double> maxWeightMap = chart.maxWeightMap;
        HashMap<String,Double> minWeightMap = chart.minWeightMap;
        HashMap<String, Double> averageAttributeWeights = new HashMap<String, Double>();
        
        if(listOfWeightMaps.isEmpty()){
            System.out.println("List of weight maps is empty");
            return;
        }
        
        if(listOfWeightMaps.size() > 1) {       
            // for each user
            for(HashMap<String, Double> aMap : listOfWeightMaps.values()){
                // for each alternative
                for(Map.Entry<String,Double> entry : aMap.entrySet()){
                    
                    String srcKey = entry.getKey();
                    double srcValue = entry.getValue();
                    if(maxWeightMap.containsKey(srcKey) && maxWeightMap.get(srcKey) < srcValue){
                        maxWeightMap.put(srcKey, srcValue);                             
                    }
                    if(minWeightMap.containsKey(srcKey) && minWeightMap.get(srcKey) > srcValue) {
                        minWeightMap.put(srcKey, srcValue);                             
                    }
                    if(averageAttributeWeights.containsKey(srcKey)){
                        double destValue = averageAttributeWeights.get(srcKey);
                        destValue += srcValue;
                        averageAttributeWeights.put(srcKey,destValue);
                    }
                    else{
                        maxWeightMap.put(srcKey, srcValue);
                        minWeightMap.put(srcKey, srcValue);
                        averageAttributeWeights.put(srcKey,srcValue);
                    }
                        
                }
            } 
        } else if(listOfWeightMaps.size() == 1){
            maxWeightMap.putAll(listOfWeightMaps.get(0));
            minWeightMap.putAll(listOfWeightMaps.get(0));
            averageAttributeWeights.putAll(listOfWeightMaps.get(0));  
        }
        
        for(String key : averageAttributeWeights.keySet()){           
            double srcValue = averageAttributeWeights.get(key)/(listOfWeightMaps.size());
            averageAttributeWeights.put(key, srcValue);
        }
        
        chart.averageAttributeWeights = averageAttributeWeights;
    }
    
    public static void setAverageScoreMap(ValueChart chart){
        LinkedHashMap<String,IndividualEntryMap> listOfEntryMaps = chart.listOfEntryMaps;
        LinkedHashMap<String,HashMap<String,Double>> listOfWeightMaps = chart.listOfWeightMaps;
        HashMap<String,Double> maxWeightMap = chart.maxWeightMap;
        // for each criteria, for each alternative
        HashMap<String, HashMap<String, Double>> averageAttributeScores = new HashMap<String, HashMap<String, Double>>();
        
        // for each user
        for (Map.Entry<String, IndividualEntryMap> iem : listOfEntryMaps.entrySet()) {
            
            // for each alternative
            for (Map.Entry<String, ChartEntry> entry : iem.getValue().getEntryMap().entrySet()) {
                
                // for each criteria
                for (Map.Entry<String, AttributeValue> attrVal : entry.getValue().map.entrySet()) {
                    
                    // get criteria 
                    String critkey = attrVal.getKey();
                    HashMap<String, Double> means = averageAttributeScores.get(critkey);
                    if (means == null) {
                        means = new HashMap<String, Double>();
                        averageAttributeScores.put(critkey, means);
                    } 
                    
                    // get alternative
                    String attrkey = entry.getKey();
                    
                    // get the weight
                    double val = attrVal.getValue().weight();
                    // scale it relative to the maximum assigned weight
                    double scale = listOfWeightMaps.get(iem.getKey()).get(critkey);
                    scale /= maxWeightMap.get(critkey);
                    val *= scale;
                    
                    Double mean = means.get(attrkey);
                    if (mean == null) {
                        means.put(attrkey, val);
                    } else {
                        means.put(attrkey, mean + val);
                    }
                }
            }
        }
        
        // for each criteria
        for (Map.Entry<String, HashMap<String, Double>> meansMap : averageAttributeScores.entrySet()) {
            // for alternative
            for (Map.Entry<String, Double> means : meansMap.getValue().entrySet()) {
                double mean = means.getValue() / listOfEntryMaps.size();
                means.setValue(mean);
            }
        }
        
        chart.averageAttributeScores = averageAttributeScores;
    }
}
