import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JLabel;


public class AttributeWeightMap {	
	HashMap map;    
    	
	public AttributeWeightMap (){		
		map = new HashMap<String,Double>();
	}

    public void fillMap(String attribute,Double weight){    	
    	map.put(attribute, weight);    	
    }
}
