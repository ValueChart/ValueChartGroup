import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.text.html.HTMLDocument.Iterator;


public class IndividualAttributeMaps {
	String userName;
//	String attributeName;
//	ArrayList<Double> attributeWeights;
//	ArrayList<AttributeDomain> attributeDomains;
//	Color attributeColor;
	String attributeUnits;
	Color userColor;
	//When you mouseover a user, this boolean is set to true. 
	//It is used in DisplayPanel to fade the scores of those users other than the mouseovered user.
	boolean mouseOver = false; 
	HashMap<String,Double> attributeWeightMap;
	HashMap<String,AttributeDomain> attributeDomainMap;	
	HashMap<String,Color> attributeColorMap;
	String topAlternative;
	
	ArrayList<BaseTableContainerGroup> listOfContainers = new ArrayList<BaseTableContainerGroup>();
		
	public IndividualAttributeMaps(String filename){
		this.userName = filename;
		attributeWeightMap = new HashMap();
		attributeDomainMap = new HashMap();		
		attributeColorMap = new HashMap();
//		attributeWeights = new ArrayList<Double>();
//		attributeDomains = new ArrayList<AttributeDomain>();
	}
	
	public void fillWeightMap(String name,double weight){
		attributeWeightMap.put(name,weight);
	}
	
	public void fillDomainMap(String name,AttributeDomain domain){
		attributeDomainMap.put(name, domain);		
	}
	
	public void fillColorMap(String name, Color color){
		attributeColorMap.put(name, color);
	}
	
	public void setTopAlternative(String alt){
		topAlternative = alt;
	}
	
	/*public void fillWeightVector(double weight){
		attributeWeights.add(weight);
	}
	
	public void fillDomainVector(AttributeDomain domain){
		attributeDomains.add(domain);
	}
	*/
	public void printWeightMap(){	
		System.out.println("Printing weight maps"+"\n");
		for (Object a : attributeWeightMap.keySet()) {
			String key = a.toString();
			String value = attributeWeightMap.get(key).toString();
		    System.out.println(key + "-" + value);  
		}
		if (attributeWeightMap.isEmpty()){
            System.out.println("-- attributeWeightMap empty --");
		}
	}
	
	public void printDomainMap(){
		System.out.println("Printing domain maps"+ "\n");
		for (Object attrName : attributeDomainMap.keySet()) {
			String key = attrName.toString();
			AttributeDomain domain = attributeDomainMap.get(key);
			if(domain.getType() == AttributeDomain.DISCRETE){
				System.out.println(key);
				String[] elements = ((DiscreteAttributeDomain) domain).getElements();
		        double[] weights = ((DiscreteAttributeDomain) domain).getWeights();
				for(int i =0; i < elements.length;i++){
					System.out.println(elements[i] + " " + weights[i]);
				}
			}else{
				System.out.println(key);
				double[] elements = ((ContinuousAttributeDomain) domain).getKnots();
		        double[] weights = ((ContinuousAttributeDomain) domain).getWeights();
				for(int i =0; i < elements.length;i++){
					System.out.println(elements[i] + " " + weights[i]);
				}
			}		     
		}
		 if (attributeDomainMap.isEmpty()){
             System.out.println("-- attributeDomainMap empty --");
		 }
	}
	
	public void printColorMap(){	
		System.out.println("Printing color maps"+"\n");
		if (attributeColorMap.isEmpty()){
            System.out.println("-- attributeColorMap empty --");
		}
		for (Object a : attributeColorMap.keySet()) {
			String key = a.toString();
			String value = attributeColorMap.get(key).toString();
		    System.out.println(key + "-" + value);  
		}
	}
	
	public void printContainerList(){
		System.out.println("Printing containers "+"\n");
		if (listOfContainers.isEmpty()){
            System.out.println("-- Container list empty --");
		}
		for (BaseTableContainerGroup a : listOfContainers) {
			if(a.table instanceof TablePane){
				System.out.println("Attribute Level high: "+a.getName());
			}
			else if(a.table instanceof AttributeCellGroup){
				System.out.println("Attribute Level low: "+a.getName());
				System.out.println("Weights: "+a.getHeightRatio());
				System.out.print("Domain: ");			
				AttributeCellGroup cell = (AttributeCellGroup) a.table;
				AttributeDomain domain = cell.getDomain();
				if(domain.getType() == AttributeDomain.DISCRETE){
					System.out.println(cell.getName());
					String[] elements = ((DiscreteAttributeDomain) domain).getElements();
			        double[] weights = ((DiscreteAttributeDomain) domain).getWeights();
					for(int i =0; i < elements.length;i++){
						System.out.println(elements[i] + " " + weights[i]);
					}
				}else{
					System.out.println(cell.getName());
					double[] elements = ((ContinuousAttributeDomain) domain).getKnots();
			        double[] weights = ((ContinuousAttributeDomain) domain).getWeights();
					for(int i =0; i < elements.length;i++){
						System.out.println(elements[i] + " " + weights[i]);
					}
				}
			}						
		}
	}
}

