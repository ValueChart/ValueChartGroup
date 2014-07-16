import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Vector;


public class IndividualEntryMap {
	String username;
	private LinkedHashMap<String, ChartEntry> entryMap;
	
	public IndividualEntryMap(String filename, Vector<ChartEntry> vector){
		username = filename;
		entryMap = new LinkedHashMap<String, ChartEntry>();
		for (ChartEntry entry : vector)
		    entryMap.put(entry.name, entry);
	}
	
	public double[] getEntryWeights(String attributeName) {
        double[] weights = new double[entryMap.size()];
        int i = 0;
        for (ChartEntry entry : entryMap.values()) {
            AttributeValue value = entry.attributeValue(attributeName);
            if (value != null) {
                weights[i] = value.weight();
            } else {
                weights[i] = 0;
            }
            i++;
        }
        return weights;
    }	
	
	public double getEntryWeight(String attributeName, String entryName) {
	    if (entryMap.containsKey(entryName)) {
	        AttributeValue value = findEntry(entryName).attributeValue(attributeName);
	        if (value != null)
	            return value.weight();
	    }
	    return 0;
	}
	
	public LinkedHashMap<String, ChartEntry> getEntryMap() {
	    return entryMap;
	}
	
	public ChartEntry findEntry(String name) {
	    return entryMap.get(name);
	}
	
}
