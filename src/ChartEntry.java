import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JFrame;

import org.icepdf.core.pobjects.OutlineItem;
import org.icepdf.ri.common.SwingController;

//This class is the basis of ChartEntry. To my knowledge, this class has little to do with he interface itself. One thing to note though.
//is that HashMap is the datatype for a Chart Entry. I am not sure if this is a good idea rather than using an object. Since HashMap has certain
//problems to maintaining consistency and maybe hard to organize for interface presentation
public class ChartEntry
{
	String name;
	// key: criteria name
	// value: associated discrete name/continuous x value and AttributeDomain
	HashMap<String, AttributeValue> map;
	private HashSet<AttributeCell> maskingAttributes;
	/**/private boolean showFlag;
	/**/private boolean isMarked;
	private JFrame reportFrame = null;
	private SwingController reportController = null;
	private OutlineItem outlineItem = null;
	private File report;

	public ChartEntry (String name){
		this.name = name;
		map = new HashMap<String, AttributeValue>();
		maskingAttributes = new HashSet<AttributeCell>();
		/**/showFlag=false;
	}
	
	public boolean isMasked(){
		return !maskingAttributes.isEmpty();
	}

	/*
//	public boolean addMaskingAttribute (AttributeCell attr){
	public boolean addMaskingAttribute (String attr){
		return maskingAttributes.add (attr);
	}

//	public boolean removeMaskingAttribute (AttributeCell attr){
	public boolean removeMaskingAttribute (String attr){
	   return maskingAttributes.remove (attr);
	}
	*/

	public AttributeValue attributeValue (String name){
	   return map.get(name);
	}

	/**/
	public void setShowFlag(boolean b){
		showFlag = b;		
	}
	
	public boolean getShowFlag(){
		return showFlag;		
	}

	public void setIsMarked(boolean b){
		isMarked = b;		
	}
	
	public boolean getIsMarked(){
		return isMarked;		
	}

	public JFrame getReportFrame() {
        return reportFrame;
    }

    public void setReportFrame(JFrame reportFrame) {
        this.reportFrame = reportFrame;
    }

    public SwingController getReportController() {
        return reportController;
    }

    public void setReportController(SwingController reportController) {
        this.reportController = reportController;
    }

    public OutlineItem getOutlineItem() {
        return outlineItem;
    }

    public void setOutlineItem(OutlineItem outlineItem) {
        this.outlineItem = outlineItem;
    }

    public File getReport() {
        return report;
    }

    public void setReport(File report) {
        this.report = report;
    }
}
