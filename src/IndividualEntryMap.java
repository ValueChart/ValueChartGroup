import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


public class IndividualEntryMap {
	String username;
	ArrayList<ChartEntry> entryList;
	
	public IndividualEntryMap(String filename,ArrayList<ChartEntry> entries){
		this.username = filename;
		this.entryList = entries;
	}
	
	public double[] getEntryWeights(String attributeName) {
        double[] weights = new double[entryList.size()];
        int i = 0;
        for (Iterator it = entryList.iterator(); it.hasNext();) {
            AttributeValue value = ((ChartEntry) it.next()).attributeValue(attributeName);
            if (value != null) {
                weights[i] = value.weight();
            } else {
                weights[i] = 0;
            }
            i++;
        }
        return weights;
    }	
	
}
