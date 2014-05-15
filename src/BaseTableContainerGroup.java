import java.util.Iterator;

import javax.swing.JComponent;


public class BaseTableContainerGroup extends JComponent{
	public JComponent table;
	private String name;
	private double heightScale = 1.0;
	private double heightRatio = -1.0;	
	private ValueChart chart;
	
	public BaseTableContainerGroup(JComponent table, String name, ValueChart chart){
		this.table = table;
        this.name = name;
        this.chart = chart;		
	}

	public void setWeight(double hr) {
		heightRatio = hr;
		if(table instanceof TablePane)
			propagateWeight();		
	}
	
	private void propagateWeight(){
		for (Iterator it = ((TablePane) table).getRows(); it.hasNext();) {
            BaseTableContainerGroup comp = (BaseTableContainerGroup) it.next();
            comp.setWeightScale(heightScale * heightRatio);
		}
	}
    private void setWeightScale(double s) {
        heightScale = s;
        propagateWeight();
    }
    
    public double getHeightRatio() {
        return heightRatio;
    }
    
    public String getName() {
        return name;
    }

	public JComponent getTable() {
		return table;
	}   


}
