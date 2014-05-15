import java.awt.Color;

import javax.swing.JComponent;


public class AttributeCellGroup extends JComponent {
	ValueChart chart;
	AttributeDomain domain;
	private String attributeName;
	private Color color;
	private String units;
	
	public AttributeCellGroup(ValueChart ch, AttributeDomain dom, String attrName){
		this.attributeName = attrName;
		this.chart = ch;
		this.domain = dom;
	}
	
	 public void setColor(Color c) {
	        color = c;
	        float[] rgb = c.getRGBColorComponents(null);
	        new Color(rgb[0] / 3, rgb[1] / 3, rgb[2] / 3);
	    }
	 
    public void setUnits(String name) {
        units = name;
    }
    
    public AttributeDomain getDomain(){
    	return domain;
    }
    
    public String getName(){
    	return attributeName;
    }
}
