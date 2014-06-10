/*
This class that brings all the classes together
 */
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.*;
import java.io.*;

import acme.misc.*;

import org.icepdf.core.pobjects.OutlineItem;
import org.icepdf.core.pobjects.Outlines;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.OutlineItemTreeNode;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.common.views.DocumentViewControllerImpl;

public class ValueChart extends JPanel {

    private static final long serialVersionUID = 1L;
    static public final int DEFAULT_COL_WIDTH = 60;
    static public final int DEFAULT_USER_COL_WIDTH = 20;
    static public final int DEFAULT_PADDING_WIDTH = 9;//9
    //*%* Changed display height to make it relative to the screen size
//    static public final int DEFAULT_DISPLAY_HEIGHT = 500;
    static public final int DEFAULT_DISPLAY_HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.52);
    //*%*
    static public final int DEFAULT_DISPLAY = 1,
            SIDE_DISPLAY = 2,
            SEPARATE_DISPLAY = 3;
    static public final int COLORFORUSER = 1,
    		COLORFORATTRIBUTE = 2,
    		COLORFORINTENSITY = 3;
    
    int headerWidth = 200; //width of criteria column
    int graphWidth = 100; //width of utility graphs
    int displayType = DEFAULT_DISPLAY;
    int displayHeight = DEFAULT_DISPLAY_HEIGHT;
    int displayPanelHeight;
    int userWidth = DEFAULT_USER_COL_WIDTH;
    int padWidth = DEFAULT_PADDING_WIDTH;
    int colWidth = DEFAULT_COL_WIDTH;
//    int colWidthGroup = padWidth * 2 + userWidth * noOfUsers;
    int colWidthGroup ;
    boolean showAbsoluteRatios = false;
    private JFrame chartFrame = null;
    private String chartTitle;
    TablePane mainPane;
    JPanel mainPaneWithNames;
    EntryNamePanel mainEntryNames;
    DisplayPanel displayPanel;
    EntryNamePanel displayEntryNames;
    JPanel displayWithNames;
    JPanel pnlDisp;
    DisplayDialog dialog;
    Reader initReader = null;
    ResizeHandler resizeHandler;
    Vector<ChartEntry> entryList;    
    ArrayList<String> users = null;
    String filename;
    Vector<JObjective> objs;
    Vector alts;
    private HashMap<String, BaseTableContainer> prims;
    ConstructionView con;
    
    String data;
    double heightScalingConstant = 1.0; //constant for normalizing the heights if total height ratio exceeds 1.0 
    ArrayList<IndividualAttributeMaps> listOfAttributeMaps = new ArrayList<IndividualAttributeMaps>();     //map of attributes for all users
    HashMap<String,Double> maxWeightMap = new HashMap<String,Double>(); //Map of attributes with maximum weights
    HashMap<String,Double> minWeightMap = new HashMap<String,Double>(); //Map of attributes with minimum weights
    LinkedHashMap<String,IndividualEntryMap> listOfEntryMaps = new LinkedHashMap<String,IndividualEntryMap>(); //map of entries for all users
    LinkedHashMap<String,HashMap<String,Double>> listOfWeightMaps = new LinkedHashMap<String,HashMap<String,Double>>();
    HashMap<String,Double> averageAttributeWeights = new HashMap<String,Double>();
    HashMap<String,HashMap<String, Double>> averageAttributeScores = new HashMap<String,HashMap<String, Double>>();
    ArrayList<ChartEntry> averageAttributeValues = new ArrayList<ChartEntry>();
    Set<Color> listOfUserColors = new HashSet<>();
    ArrayList<Color> colorList;
    JPanel averageWeights;
        
    GroupActions pnlGroupActions;
    int colorChoice; //variable to select the purpose of color
    String userToPickAttributeColor; //choose a user to pick his attribute colors
    boolean topChoices = false;
    boolean showAvgWeights = false;
    boolean generateAvgGVC = false;
    boolean davidAvgGVC = false;
    boolean hideNonCompete = false;
    boolean showAvgScores = false;
    
    boolean pump = false;
    boolean pump_increase = true;
    boolean show_graph = true;
    int sa_dir = BaseTableContainer.UP;
    SensitivityAnalysisOptions pnlOpt;
    public DomainValues pnlDom;
    OptionsMenu menuOptions;
    boolean isNew = true;
    LastInteraction last_int;
    LastInteraction next_int;
    private HashMap<String, Integer> rankVarWeight = null;
    private HashMap<String, Integer> rankVarUtil = null;
    private HashMap<String, Integer> rankVarScore = null;
    public static final ColorHeatMap heatMapColors = new ColorHeatMap();
    
    //***Added so that there is one frame that allows display of pdf reports from value chart. This window is not used for each of the attribute/entry reports.
    //Those are contained within the AttributeCell class. Rather, this is for anywhere else on the interface (it started with a need to have one report window to display the report for the criteria details)
    SwingController controller; //Used for each window that controls a pdf document
    JFrame reportWindow; //the windows created for the attribute, these are used to show the report. They are class variables because we need to toggle visibility from different methods
    JPanel viewerComponentPanel; //the panel for which the frame sits on;
    File reportFile; //location of the report, will be null if there is no report, see doRead()
    Outlines outlines; //The bookmark outline
    OutlineItem item = null; //The items in the bookmark

//CONSTRUCTOR
    public ValueChart(ConstructionView c, String file, ArrayList<String> list, int type, int colwd, boolean b, boolean graph,int colorOption, boolean avgGVC, boolean dAvgGVC) {
        super();        
        show_graph = graph;
        con = c;
        isNew = b;
        filename = file;
        users = list;
        generateAvgGVC = avgGVC;
        davidAvgGVC = dAvgGVC;
//        colWidthGroup =  padWidth * 2 + userWidth * users.size();
        if (avgGVC)
            colWidthGroup = userWidth*2;
        else
            colWidthGroup =  userWidth * (list.size()-1) + padWidth;
        colorChoice = colorOption;
        setDisplayType(type);
    	             
		 try {
			 doReadAll(users);
		 } catch (IOException e) {
//		     System.out.println("Error in file: "+ filename +" "+ e.getMessage());
			 System.out.println("Error in file: " + e.getMessage());
		 }
		 
		 heightScalingConstant = getHeightScalingConstant();
//		 System.out.println("Scaling constant: "+heightScalingConstant);
		 
        menuOptions = new OptionsMenu(this);
        menuOptions.createMenu();
        if (!isNew) {
            menuOptions.setVisible(false);
        }
        last_int = new LastInteraction(menuOptions.menuUndo);
        next_int = new LastInteraction(menuOptions.menuRedo);
        setColWidth(colwd);
        showAbsoluteRatios = true;
        try {
            initReader = new FileReader(filename);
            resizeHandler = new ResizeHandler();
            addComponentListener(resizeHandler);
            try {
                read(initReader);
            } catch (IOException e) {
                System.out.println("Error in input: " + e.getMessage());
            }
//            setDisplayHeight(Toolkit.getDefaultToolkit().getScreenSize().height);
            setDisplayHeight(displayHeight);
            setSize(getPreferredWidth(), displayType == SIDE_DISPLAY ? displayHeight * 2 - 50 : displayHeight);
            setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

        showVC();        
        if (isNew) {
            chartFrame.setJMenuBar(menuOptions);
        }
        setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        if (displayType == SEPARATE_DISPLAY) {
            dialog.setLocation(chartFrame.getX(), chartFrame.getHeight());
        }	
    }

//DISPLAY
    public void setDisplayHeight(int h) {
        displayHeight = h;
    }

    public int getPreferredWidth() {
        int w = ((mainPane.getDepth() -1) + entryList.size()) * colWidth;
        if (displayType == SIDE_DISPLAY) {
            w += (2 + entryList.size()) * colWidth;
        }
        return w;
    }

    public int getColWidth() {
        return colWidth;
    }

    public void setColWidth(int w) {
        colWidth = w;
    }

    public void updateMainPane() {
        mainPane.repaint();
    }
    
    public void updateDisplay() {
        displayPanel.repaint();
    }

    public void updateAll() {
        displayPanel.repaint();
        mainPane.repaint();
    }

    public void reorderEntries(BaseTableContainer baseTab) {
        Comparator weightComparator = new WeightComparator(baseTab);
        Collections.sort(entryList, weightComparator);
        mainEntryNames.relabel(entryList);
        displayEntryNames.relabel(entryList);
        displayPanel.repaint();
        mainPane.repaintEntries();
    }

    public void setVisible(boolean b) {
        super.setVisible(b);
        if (dialog != null) {
            dialog.setVisible(b);
        }
    }

//CONNECT CONST/INSP
    void setPrims(TablePane pane) {
        Iterator it;
        for (it = pane.getRows(); it.hasNext();) {
            BaseTableContainer btc = (BaseTableContainer) (it.next());
            if (btc.table instanceof AttributeCell) {
                prims.put(btc.getName(), btc);
                btc.adjustHeaderWidth();//added for rotate
            } else {
                setPrims((TablePane) btc.table);
            }
        }
    }

    public void setConnectingFields() {
        for (int i = 0; i < con.getObjPanel().getPrimitiveObjectives().size(); i++) {
            JObjective obj = (JObjective) con.getObjPanel().getPrimitiveObjectives().get(i);
            BaseTableContainer btc = findPrimitive(obj.getName());
            if (btc != null) {
                AttributeCell ac = (AttributeCell) btc.getTable();
                obj.setDomain(ac.getDomain());
                obj.setUnit(ac.getUnits());
                double pc = btc.getOverallRatio();
                obj.setWeight(String.valueOf(pc));
                //connect ac and obj
                ac.obj = obj;
                obj.acell = ac;
                if (obj.domain_type == JObjective.CONTINUOUS) {
                    if (obj.getUnit().equals("CAD")) {
                        obj.setDecimalFormat("0.00");
                    }
                    if (obj.maxC > 100) {
                        obj.setDecimalFormat("0");
                    } else {
                        obj.setDecimalFormat("0.0");
                    }
                    if (ac.cg != null) {
                        ac.cg.repaint();
                    }
                }
            }
        }
    }

    void setObjs(TablePane pane, String str) {
        Iterator it;
        JObjective obj = null;
        for (it = pane.getRows(); it.hasNext();) {
            BaseTableContainer btc = (BaseTableContainer) (it.next());
            btc.updateHeader();
            if (str.equals("root")) {
                con.getObjPanel().lblRoot.setName(btc.getName());
            } else {
                obj = new JObjective(btc.getName());
                con.getObjPanel().addFromVC(str, obj);
                objs.add(obj);
                if (btc.table instanceof AttributeCell) {
                    AttributeCell ac = (AttributeCell) btc.getTable();
                    obj.setColor(ac.getColor());
                    obj.origin = JObjective.FROM_FILE;
                }
            }
            if (btc.table instanceof TablePane) {
                setObjs((TablePane) btc.table, btc.getName());
            }
        }
    }

    Vector setAlts() {
        Iterator<ChartEntry> it;
        Vector alts = new Vector();
        for (it = entryList.iterator(); it.hasNext();) {
            HashMap datamap = new HashMap();
            ChartEntry entry = it.next();
            datamap.put("name", entry.name);
            datamap.putAll(entry.map);
            for (Iterator<JObjective> it2 = objs.iterator(); it2.hasNext();) {
                JObjective obj = it2.next();
                if (!datamap.containsKey(obj.getName())) {
                    datamap.put(obj.getName(), "0");
                } else {
                    AttributeValue val = (AttributeValue) datamap.get(obj.getName());
                    datamap.put(obj.getName(), val.stringValue());
                    obj.setType(val.domain.getType());
                }
            }
            alts.add(datamap);
        }
        return alts;
    }

    public HashMap<String, BaseTableContainer> getPrims() {
        return prims;
    }
    
    public BaseTableContainer findPrimitive(String name) {
        return prims.get(name);
    }

//READS
    private void read(Reader reader) throws IOException {
        ScanfReader scanReader = new ScanfReader(reader);
        try {
            doread(scanReader);
        } catch (Exception e) {
            throw new IOException(e.getMessage() + ", line " + scanReader.getLineNumber());
        }
       //bulding the construction view straight from vc
        if (!con.init) {
            setConst();
            con.setInit(true);
        }
        con.setChart(this);
//        set prim obj list for rolling up the absolute tree    	
        prims = new HashMap<String, BaseTableContainer>();
        setPrims(mainPane);
        for (BaseTableContainer btc : prims.values()) {
            btc.setRollUp();
        }
        ((BaseTableContainer) mainPane.rowList.get(0)).setAbstractRatios();
        setConnectingFields();
        if (isNew) {
            menuOptions.setSelectedItems();
        }
    }
    
    public void newConst() {
        con = new ConstructionView(ConstructionView.FROM_VC);
        setConst();
    }

    private void setConst() {
        objs = new Vector<JObjective>();
        setObjs(mainPane, "root");
        alts = new Vector();
        alts = setAlts();
        con.getAltPanel().setFileAlternatives(null, alts);
        con.getAltPanel().updateTable();
        con.setChart(this);
        //for vc w/other data
    }
    
    private void doread(ScanfReader scanReader)
            throws IOException {

        entryList = new Vector<ChartEntry>(10);
        HashMap colorList = new HashMap(10);
        boolean attributesRead = false;

        colorList.put("red", Color.red);
        colorList.put("green", Color.green);
        colorList.put("blue", Color.blue);
        colorList.put("cyan", Color.cyan);
        colorList.put("magenta", Color.magenta);
        colorList.put("yellow", Color.yellow);

        while (true) {
            String keyword = null;
            try {
                keyword = scanReader.scanString();
            } catch (EOFException e) {
                break;
            }
            if (keyword.equals("attributes")) {
                mainPane = new TablePane();
                readAttributes(scanReader, mainPane, colorList);
                mainPane.adjustAttributesForDepth(mainPane.getDepth());
                attributesRead = true;
            } else if (keyword.equals("color")) {
                String name = scanReader.scanString();
                float r = scanReader.scanFloat();
                float g = scanReader.scanFloat();
                float b = scanReader.scanFloat();
                colorList.put(name, new Color(r, g, b));
            } else if (keyword.equals("entry")) {
                if (!attributesRead) {
                    throw new IOException("Entry specified before attributes");
                }
                entryList.add(readEntry(scanReader, mainPane));
            } else if (keyword.startsWith("report=")) {
                //Sets the report location for the ValueCharts parent interface (there is another reportFileLocation, which is specific to the AttributeCell bar charts)
                reportFile = new File(keyword.substring(7).replace("$", " ").replace("\\", "\\\\"));
                if (reportFile.exists()) {
                    createReportController();
                } else {
                    //since the report does not exist, throw a system message letting the user know this, and set the report to null
                    System.out.println("The report for the ValueChart is not valid, the system believes the report is located at: ");
                    System.out.println("  " + reportFile.toString());
                    System.out.println("  If this is an error, please verify that you have correctly substituted all spaces and added two slashes between directories.");
                    reportFile = null;
                }
            } else {
                throw new IOException("Unknown keyword " + keyword);
            }
        }
        

        mainPane.fillInEntries(entryList);

        mainPaneWithNames = new JPanel();
        mainPaneWithNames.setLayout(new BoxLayout(mainPaneWithNames, BoxLayout.Y_AXIS));

        mainEntryNames = new EntryNamePanel(entryList, colWidthGroup, mainPane.getDepth(), true, this);
//        mainPaneWithNames.add(Box.createVerticalStrut(40));//-
        JPanel pnlBottom = new JPanel();
        pnlBottom.setLayout(new BoxLayout(pnlBottom, BoxLayout.X_AXIS));
        pnlBottom.add(Box.createHorizontalGlue());
        pnlBottom.add(mainEntryNames);

        
//        JButton xx = new JButton("test");
//        averageWeights = new JPanel();
//        averageWeights.setLayout(new BoxLayout(averageWeights, BoxLayout.X_AXIS));
//        averageWeights.add(xx);
//        averageWeights.add(Box.createHorizontalGlue());
        
        mainPaneWithNames.add(mainPane);
        mainPaneWithNames.add(pnlBottom);
        

        removeAll();

       /*	if (displayType == SIDE_DISPLAY){ 
	         setLayout (new BoxLayout (this, BoxLayout.X_AXIS));
	         add(mainPaneWithNames);
         }
         else{ 
	         setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
	         add(mainPaneWithNames);
	         if (displayType != SEPARATE_DISPLAY){ 
		         add (Box.createVerticalStrut(colWidth));
		         add (Box.createGlue());
	         }
         }*/

        displayPanel = new DisplayPanel(colWidthGroup,this);
        displayPanel.setRootPane(mainPane);
        displayPanel.setEntries(entryList);
        pnlDisp = new JPanel();

        int headerDepth = (mainPane.getDepth()) * headerWidth/2;
        
        pnlDisp.setLayout(new BoxLayout(pnlDisp, BoxLayout.X_AXIS));
        if (displayType == SIDE_DISPLAY) {
            pnlDisp.add(Box.createHorizontalGlue());
        } else {
//            pnlDisp.add(Box.createHorizontalStrut(0));
            if(generateAvgGVC) {
                pnlGroupActions = new GroupActions(this, true);
            } else {
                pnlGroupActions = new GroupActions(this, false);
            }
            pnlDisp.add(pnlGroupActions);
            pnlGroupActions.setPreferredSize(new Dimension(headerDepth + (show_graph ? graphWidth : 0), pnlGroupActions.getPreferredSize().height));
            pnlGroupActions.setMaximumSize(new Dimension(headerDepth, pnlGroupActions.getMaximumSize().height));
            pnlGroupActions.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        pnlDisp.add(Box.createHorizontalGlue());
        pnlDisp.add(displayPanel);
        
        displayWithNames = new JPanel();
        displayWithNames.setLayout(new BoxLayout(displayWithNames, BoxLayout.Y_AXIS));
//        displayWithNames.add(Box.createVerticalGlue());
        displayWithNames.add(pnlDisp);

        //int mainWidth = (entryList.size()+mainPane.getDepth())*colWidth;
        int mainWidth = entryList.size() * colWidthGroup;
///**/	int dispWidth = entryList.size()*colWidth + colWidth;
        int dispWidth = entryList.size() * colWidthGroup + colWidthGroup;

        if (displayType == SIDE_DISPLAY) {
            displayPanel.setMaximumSize(new Dimension(dispWidth, 10000));
            displayPanel.setMinimumSize(new Dimension(dispWidth, 0));
            //mainPaneWithNames.setAlignmentY(0.0f);
        }

        graphWidth = (show_graph ? graphWidth : 0);

        mainPaneWithNames.setMaximumSize(new Dimension(mainWidth + headerDepth + graphWidth, 10000));
        mainPaneWithNames.setMinimumSize(new Dimension(mainWidth + headerDepth + graphWidth, 0));
        mainPane.setPreferredSize(new Dimension(mainPane.getPreferredSize().width, displayHeight));
        mainPaneWithNames.setPreferredSize(
                new Dimension(mainWidth + headerDepth + graphWidth, displayType == SIDE_DISPLAY ? displayHeight * 2 - 50 : displayHeight + EntryNamePanel.ANG_HT));

        int w = ((displayType == SIDE_DISPLAY) ? mainWidth + 40 : dispWidth) + colWidth;//no real reason why it's 40

        
        if (displayType == SIDE_DISPLAY) {
            displayWithNames.setMaximumSize(new Dimension(w, 10000));
            displayWithNames.setMinimumSize(new Dimension(w, 0));
            displayWithNames.setPreferredSize(new Dimension(w, displayHeight * 2 - 50));
//            displayWithNames.setPreferredSize(new Dimension(w, displayHeight));
        } else {
            displayWithNames.setMaximumSize(new Dimension(w + headerDepth + graphWidth, 10000));//- and rev x, y
            displayWithNames.setMinimumSize(new Dimension(w + headerDepth + graphWidth, 0));//- and rev x, y
            displayWithNames.setPreferredSize(new Dimension(w + headerDepth + graphWidth, (int) (displayHeight/heightScalingConstant)));//- and rev x, y
         // seems to work with -50 to get rid of unnecessary padding. TODO resize is weird
        }
        
        mainPane.updateSizesAndHeights();

        if (displayType == SEPARATE_DISPLAY) {
            dialog = new DisplayDialog(chartFrame, "Total Scores", displayWithNames);
        }

        if (displayType == DEFAULT_DISPLAY) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));         
            add(Box.createVerticalGlue());
            add(displayWithNames);
            add(Box.createRigidArea(new Dimension(0, 5)));            
//        }
//        if (displayType == SIDE_DISPLAY) {
//            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
//            add(mainPaneWithNames);
//            add(Box.createHorizontalStrut(40));//-
//            add(Box.createGlue());
//            add(displayWithNames);
//            displayDim.height = mainDim.height;
//        } 
//        else{
            
            add(mainPaneWithNames);
//            displayDim.height = mainDim.height;
//            if (displayType != SEPARATE_DISPLAY) {
//                add (Box.createVerticalStrut(40));
//                add (Box.createGlue());
//            }
        }
        alignDisplayPanel();
    }
    private void readAttributes(ScanfReader scanReader, TablePane pane, HashMap colorList)
            throws IOException {
        String name = null;
        double sum = 0.0;
        while (!(name = scanReader.scanString()).equals("end")) {
            BaseTableContainer container;
            double hr = 0.0;
       
            if (name.equals("attributes")) { //if keyword=attributes, then read abstract name and ratio
                TablePane subpane = new TablePane();
                name = scanReader.scanString();
                if (chartTitle == null) {
                    //this is the first attribute level, so set the title of the window to the main title of the ValueChart
                    chartTitle = name.replace('_', ' ');
                }
//                hr = getMaxHeightRatio(scanReader,name)/heightScalingConstant;
//                hr = getMaxHeightRatio(scanReader,name);
                hr = readHeightRatio(scanReader);
//            	System.out.println(name+" "+hr);
                readAttributes(scanReader, subpane, colorList);
                container = new BaseTableContainer(subpane, name, this, colWidth);
//                container = new BaseTableContainer(subpane, name, this, colWidthGroup);

            } else { //if no keyword is primitive            	
            	hr = getMaxHeightRatio(scanReader,name)/heightScalingConstant;
            	sum += hr;
//            	System.out.println( "sum"  + sum );
//            	
//            	System.out.println( heightScalingConstant );
//            	hr = readHeightRatio(scanReader);
//            	System.out.println(name+" "+hr);
            	AttributeDomain domain = AttributeDomain.read(scanReader);
                AttributeCell cell = new AttributeCell(this, domain);
                cell.setColWidth(colWidthGroup);
                String option = null;

                //This is where the program reads through the valuecharts file (.vc) and collects the properties for each of the attributes
                while (!(option = scanReader.scanString()).equals("end")) {
                    if (option.startsWith("color=")) {
                        String colorName = option.substring(6);
                        Color color = (Color) colorList.get(colorName);
                        if (color == null) {
                            throw new IOException("Unknown color '" + colorName + "'");
                        }
                        cell.setColor(color);
                    } else if (option.startsWith("units=")) {
                        String unitsName = option.substring(6);
                        cell.setUnits(unitsName);
                    } else {
                        throw new IOException("Unknown option '" + option + "'");
                    }
                }
                container = new BaseTableContainer(cell, name, this, colWidth);
            }
            pane.addRow(container);            
            container.setHeightRatio(hr);            
            container.setRollUpRatio(hr);
//            System.out.println( container.getHeight() );
//            System.out.println("Height ratio: "+hr);
        }
    }
    
    private void doReadAll(ArrayList users) throws IOException {
    	
    	for(Object aFile : users){
			String filename = (String) aFile;
			initReader = new FileReader(filename);
			ScanfReader scanReader = new ScanfReader(initReader);
			
	        ArrayList<ChartEntry> listOfEntries = new ArrayList<ChartEntry>();
	        HashMap colorList = new HashMap(10);
	        IndividualAttributeMaps attrMaps = new IndividualAttributeMaps(aFile.toString());
	        
	        boolean attributesRead = false;
	
	        colorList.put("red", Color.red);
	        colorList.put("green", Color.green);
	        colorList.put("blue", Color.blue);
	        colorList.put("cyan", Color.cyan);
	        colorList.put("magenta", Color.magenta);
	        colorList.put("yellow", Color.yellow);       
	        
	        if(!aFile.toString().equals("SuperUser.vc")){	        	
	        	TablePane groupPane = new TablePane();
	        	while (true) {        	
		            String keyword = null;
		            try {
		                keyword = scanReader.scanString();
		            } catch (EOFException e) {
		                break;
		            }		            
		            if(keyword.equals("attributes")){
		            	//read attributes		            	
	            		readAttributesAll(scanReader,groupPane,attrMaps,colorList,filename);	            		
	            		attributesRead = true;    
		                
		            } else if (keyword.equals("color")) {
		                String name = scanReader.scanString();
		                float r = scanReader.scanFloat();
		                float g = scanReader.scanFloat();
		                float b = scanReader.scanFloat();
		                colorList.put(name, new Color(r, g, b));
		            } 
		            else if (keyword.equals("entry")) {
		                if (!attributesRead) {
		                    throw new IOException("Entry specified before attributes");
		                }	
//		                printlistOfAttributeMaps(listOfAttributeMaps);
		                listOfEntries.add(readEntryAll(scanReader, groupPane, filename));
		            } 
		            else if (keyword.startsWith("report=")) {
		                //Sets the report location for the ValueCharts parent interface (there is another reportFileLocation, which is specific to the AttributeCell bar charts)
		                reportFile = new File(keyword.substring(7).replace("$", " ").replace("\\", "\\\\"));
		                if (reportFile.exists()) {
		                    createReportController();
		                } else {
		                    //since the report does not exist, throw a system message letting the user know this, and set the report to null
		                    System.out.println("The report for the ValueChart is not valid, the system believes the report is located at: ");
		                    System.out.println("  " + reportFile.toString());
		                    System.out.println("  If this is an error, please verify that you have correctly substituted all spaces and added two slashes between directories.");
		                    reportFile = null;
		                }
		            } else {
		                throw new IOException("Unknown keyword " + keyword);
		            }
		        }
		        IndividualEntryMap iem = new IndividualEntryMap(filename, listOfEntries);
		        listOfEntryMaps.put(filename,iem);
	        }
    	}
    	
    	// add elements to al, including duplicates
    	LinkedHashSet<IndividualAttributeMaps> hs = new LinkedHashSet<IndividualAttributeMaps>();
    	hs.addAll(listOfAttributeMaps);
    	listOfAttributeMaps.clear();
    	listOfAttributeMaps.addAll(hs);

    	//assign a distinct color for each user
    	generateRandomColor(listOfAttributeMaps);    	
  	
    	AlternativeStatistics.setMaxWeightMap(this);
    	AlternativeStatistics.setAverageScoreMap(this);
    }

    private double readHeightRatio(ScanfReader scanReader)
            throws IOException {
        String frac = scanReader.scanString();
        //if weight = *, set it at -1
        double hr = -1;
        if (!frac.equals("*")) {
            try {
                hr = Double.parseDouble(frac);
            } catch (NumberFormatException e) {
                throw new IOException("Illegal double value " + frac);
            }
        }
        return hr;
    }
    
    private double getMaxHeightRatio(ScanfReader scanReader, String attrName) throws IOException{    	
    	String frac = scanReader.scanString();
    	double hr = -1;
    	if(!frac.equals("*")){
			if(!maxWeightMap.isEmpty() && !davidAvgGVC){
    			if(maxWeightMap.containsKey(attrName)){
    				hr = maxWeightMap.get(attrName);               			
        			return hr;
        		}
    		} else if (!averageAttributeWeights.isEmpty() && davidAvgGVC) {
                if(averageAttributeWeights.containsKey(attrName)){
                    hr = averageAttributeWeights.get(attrName);                        
                    return hr;
                }
    		} else if (maxWeightMap.isEmpty())
        		System.out.println("Weight Map empty");
    	}
    	return hr;
    }

    private double getHeightScalingConstant(){
    	double s = 0.0;
    	if(!maxWeightMap.isEmpty() && !davidAvgGVC){
			for(Map.Entry<String, Double> entry : maxWeightMap.entrySet()){
    			s += entry.getValue();
        	}
		} else if (!averageAttributeWeights.isEmpty() && davidAvgGVC) {
            for (Map.Entry<String, Double> entry : averageAttributeWeights.entrySet()) {
                s += entry.getValue();
            }
		}
    	return s;
    }
    
    private void readAttributesAll(ScanfReader scanReader,TablePane pane, IndividualAttributeMaps iam, HashMap colorList,String fname)
            throws IOException {
    	String name = null;
        while (!(name = scanReader.scanString()).equals("end")) {
        	BaseTableContainerGroup container;
        	double hr=1.0;
        	double hrNotUsing;
        	if (name.equals("attributes")) { //if keyword=attributes, then read abstract name and ratio
        		TablePane groupSubpane = new TablePane();
                name = scanReader.scanString();                                                
                hrNotUsing = readHeightRatio(scanReader);
                readAttributesAll(scanReader,groupSubpane,iam,colorList,fname);
                container = new BaseTableContainerGroup(groupSubpane, name, this);
//                iam.listOfContainers.add(container);
//                listOfContainers.add(container);
//                System.out.println("---------recurse---------");
            }        	
        	else {//if no keyword is primitive        		
        		 hr = readHeightRatio(scanReader);        		 
        		 AttributeDomain domain = AttributeDomain.read(scanReader); 
        		 AttributeCellGroup groupCell = new AttributeCellGroup(this, domain, name);
        		 iam.fillWeightMap(name, hr);
        		 iam.fillDomainMap(name, domain);
        		 String option = null;        		 
                 //This is where the program reads through the valuecharts file (.vc) and collects the properties for each of the attributes
                 while (!(option = scanReader.scanString()).equals("end")) {
                     if (option.startsWith("color=")) {
                         String colorName = option.substring(6);
                         Color color = (Color) colorList.get(colorName);
                         groupCell.setColor(color);
                         iam.fillColorMap(name, color);                                                  
                         if (color == null) {
                             throw new IOException("Unknown color '" + colorName + "'");
                         }                                 
                     } else if (option.startsWith("units=")) {
                         String unitsName = option.substring(6);
                         iam.attributeUnits = unitsName;
                         groupCell.setUnits(unitsName);
                     } else {
                         throw new IOException("Unknown option '" + option + "'");
                     }
                 }
                 container = new BaseTableContainerGroup(groupCell, name, this);
//                 iam.listOfContainers.add(container);
        	}
        	pane.addRowGroup(container);
        	container.setWeight(hr);
        }
        
        //assign a distinct color for each user
//    	iam.userColor = generateRandomColor();
    	
        if(!iam.attributeWeightMap.isEmpty()){
        	listOfWeightMaps.put(iam.userName, iam.attributeWeightMap);
        }       
        listOfAttributeMaps.add(iam);    
//        printlistOfAttributeMaps(listOfAttributeMaps);
//        System.out.println("Attribute cell name: "+pane.getAttributeCellGroup(scanReader.scanString()).getName());
//        iam.printContainerList();
    }
    
   
    //This function returns a list of maps; each map has attributes and their weights for individual user  
    /*private ArrayList<Map<String,Double>> getWeightMap(){
    	ArrayList<Map<String,Double>> listOfWeightMaps = new ArrayList<Map<String,Double>>();
    	if(listOfAttributeMaps.isEmpty()){
    		System.out.println("List of maps is empty");
    	}else{
    		for(IndividualAttributeMaps aMap : listOfAttributeMaps){
    			if(aMap.attributeWeightMap.isEmpty())
    				continue;
    			else
    				listOfWeightMaps.add(aMap.attributeWeightMap);
        	}
    	}
    	return listOfWeightMaps;
    }*/
    
    private double getWeightfromAvg(String name, String attrName) {
		double weight = 0;
    	for(ChartEntry entry : averageAttributeValues){
			if(name.equals(entry.name)){
				AttributeValue val = (AttributeValue) entry.map.get(attrName);
				weight = val.weight();
			}
		}
    	return weight;
	}

	private double getHeightFromAttributeMap(String user, String attrName) {
    	double individualHeight = 1.0;
    	for(IndividualAttributeMaps a : listOfAttributeMaps){
    		
    		if(user.equals(a.userName)){
    			
    			if(!a.attributeWeightMap.isEmpty()){    				
    				individualHeight = a.attributeWeightMap.get(attrName);    				
    			}
    		}
    	}
    	
    	return individualHeight;
	}

	private ArrayList<Map<String,AttributeDomain>> getDomainMap(ArrayList<IndividualAttributeMaps> maps){
    	ArrayList<Map<String,AttributeDomain>> listOfDomainMaps = new ArrayList<Map<String,AttributeDomain>>();
    	if(maps.isEmpty()){
    		System.out.println("List of maps is empty");
    	}else{
    		for(IndividualAttributeMaps aMap : maps){
    			if(aMap.attributeDomainMap.isEmpty())
    				continue;
    			else
    				listOfDomainMaps.add(aMap.attributeDomainMap);
        	}
    	} 
    	return listOfDomainMaps;
    }
    
    public void setMouseOver(String username){
    	for(IndividualAttributeMaps a : listOfAttributeMaps){    		
    		if(username.equals(a.userName)){    			
    			a.mouseOver = true;
    		}
    	}
    }
    
    public boolean getMouseOver(String filename){
    	boolean mo = false;
    	for(IndividualAttributeMaps a : listOfAttributeMaps){    		
    		if(filename.equals(a.userName)){    			
    			mo = a.mouseOver;
    		}    		
		}
    	return mo;
    }
    
    //random color generator for each user
    public void generateRandomColor(ArrayList<IndividualAttributeMaps> iam)
    {
    	colorList = new ArrayList<Color>();
    	/*colorList.add(new Color(165,0,38));
    	colorList.add(new Color(215,48,39));
    	colorList.add(new Color(244,109,67));
    	colorList.add(new Color(253,174,97));
    	colorList.add(new Color(254,224,144));
    	colorList.add(new Color(255,255,191));
    	colorList.add(new Color(224,243,248));
    	colorList.add(new Color(171,217,233));
    	colorList.add(new Color(116,173,209));
    	colorList.add(new Color(69,117,180));
    	colorList.add(new Color(49,54,149));*/
    	
    	
    	colorList.add(new Color(129,15,124)); //dark Purple
    	colorList.add(new Color(0,68,27)); //dark Green
    	colorList.add(new Color(197,27,125)); //Pink
    	colorList.add(new Color(31,120,180)); //Blue
    	colorList.add(new Color(140,81,10));//Brown
    	colorList.add(new Color(244,109,67));//Orange
    	colorList.add(new Color(0,104,55));//dark Green
    	colorList.add(new Color(255,127,0)); //Yellow
    	colorList.add(new Color(251,154,153));//light Pink
    	colorList.add(new Color(2,56,88)); //dark Blue
    	colorList.add(new Color(140,107,177));//light Purple
    	
    	
    	if(iam.size()>11){    		
    		System.out.println("More users than colors in the color map");
    	}
    	else{    
        	for(int i = 0;i<iam.size();i++){
        		if(!listOfAttributeMaps.get(i).attributeDomainMap.isEmpty()){
        			iam.get(i).userColor = colorList.get(i);
        		}
        	}	
    	}
    	
/*		Random random=new Random();
		Color aColor;
		Color aColor = colorList.get(0);
		float red = random.nextFloat();
		float green = random.nextFloat();
		float blue = random.nextFloat();      
		aColor = new Color(red, green, blue);

		for(;;){    	  
		    if(listOfUserColors.add(aColor)){
		    	break;
		    }
		    else{
		    	  red = random.nextFloat();
		          green = random.nextFloat();
		          blue = random.nextFloat();
		          aColor = new Color(red,green,blue);
		    } 
		}            
		return aColor;*/
    }
    
    public Color generateRandomGrayColor()
    {
      Random random=new Random();
      Color aColor;
      // Probably really put this somewhere where it gets executed only once
//      Color aColor = Color.getHSBColor(random.nextFloat(),//random hue, color
//              0.8f,//full saturation, 1.0 for 'colorful' colors, 0.0 for grey
//              0.8f //1.0 for bright, 0.0 for black
//              );
      float intensity = random.nextFloat();      
      aColor = new Color(intensity, intensity, intensity);
//      return new Color(red, green, blue);
      for(;;){    	  
          if(listOfUserColors.add(aColor)){
        	  break;
          }
          else{
        	  intensity = random.nextFloat();              
              aColor = new Color(intensity, intensity, intensity);
          } 
      }            
      return aColor;
    }
    
    private ChartEntry readEntry(ScanfReader scanReader, TablePane pane)
            throws IOException {
        String entryName = scanReader.scanString("%S");
        ChartEntry entry = new ChartEntry(entryName);
        String attributeName;
        while (!(attributeName = scanReader.scanString()).equals("end")) {

            //***
            //Here we have added a special case for the entry, called "report=", this is the PDF file location of the report associated with this entry 
            //PROBLEM: the acme scanReader reads the line in the file, but if the line has any spaces is becomes the next attribute.
            //         so, in the valuecharts file, the user needs to specify the entire line, including the path as one single line
            //         We suggest using "$" to replace spaces
            //         This method also adds a double slash when the user enters a single (necessary for java opening the file)
            if (attributeName.startsWith("report=")) {
                File reportFileLocation = new File(attributeName.substring(7).replace("$", " ").replace("\\", "\\\\"));
                entry.setReport(reportFileLocation);

                //***Now create the windows necessary to hold the pdf in view
                //build a controller
                SwingController controller = new SwingController(); //Used for each window that controls a pdf document
                SwingViewBuilder factory = new SwingViewBuilder(controller);

                // Build a SwingViewFactory configured with the controller
                JPanel viewerComponentPanel = new JPanel(); //the panel for which the frame sits on;
                viewerComponentPanel = factory.buildViewerPanel();
                // add copy keyboard command
                ComponentKeyBinding.install(controller, viewerComponentPanel);
                // add interactive mouse link annotation support via callback
                controller.getDocumentViewController().setAnnotationCallback(new org.icepdf.ri.common.MyAnnotationCallback(controller.getDocumentViewController()));
                //Use the factory to build a JPanel that is pre-configured
                //with a complete, active Viewer UI.
                // Open a PDF document (frame is no yet visible, but the document is opened and ready for interaction)
                controller.openDocument(reportFileLocation.toString());
                //Make it continuous view
                controller.setPageFitMode(org.icepdf.core.views.DocumentViewController.PAGE_FIT_WINDOW_WIDTH, false);
                controller.setPageViewMode(DocumentViewControllerImpl.ONE_COLUMN_VIEW, true);

                // Create a JFrame to display the panel in
                JFrame window = new JFrame("Report for " + entryName); //the windows created for the attribute, these are used to show the report. They are class variables because we need to toggle visibility from different methods
                window.getContentPane().add(viewerComponentPanel);
                window.pack();
                window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); //we only want to hide the window, not close it.
                window.setSize(new Dimension(800,600));
                
                //The Frame and controller are the only aspects of the report frame that need to be modified from elsewhere in the program (particularly AttributeCell, so they become part of the hashmap
                entry.setReportFrame(window);
                entry.setReportController(controller);

                //Now add the outline and bookmark items to the hash map
                OutlineItem entryItem = null;
                Outlines entryOutlines = controller.getDocument().getCatalog().getOutlines();
                if (entryOutlines != null) {
                    entryItem = entryOutlines.getRootOutlineItem();
                }
                if (entryItem != null) {
                    OutlineItemTreeNode outlineItemTreeNode = new OutlineItemTreeNode(entryItem);
                    outlineItemTreeNode.getChildCount();  // Added this line
                    Enumeration depthFirst = outlineItemTreeNode.depthFirstEnumeration();
                    // find the node you need
                }
                
                entry.setOutlineItem(entryItem);
                //entry.map.put("Outlines", entryOutlines); //this doesn't need to be added, it is only the items that are needed for locating
                
                
            } else {
                AttributeCell attributeCell = pane.getAttributeCell(attributeName);
                if (attributeCell == null) {
                    throw new IOException("attribute " + attributeName + " not found");
                }
                AttributeDomain domain = attributeCell.getDomain();
                AttributeValue value;
                if (domain.getType() == AttributeDomain.DISCRETE) {
                    String name = scanReader.scanString("%S");
                    value = new AttributeValue(name, domain);
                } else { // type == AttributeDomain.CONTINUOUS
                    double x = scanReader.scanDouble();
                    value = new AttributeValue(x, domain);
                }
                entry.map.put(attributeName, value);
            }
        }
        return entry;
    }
    
    private ChartEntry readEntryAll(ScanfReader scanReader,TablePane pane, String fname)
            throws IOException {
        String entryName = scanReader.scanString("%S");
        ChartEntry entry = new ChartEntry(entryName);
        String attributeName;        
        while (!(attributeName = scanReader.scanString()).equals("end")) {
//        	System.out.println(attributeName+" "+entryName);
            //***
            //Here we have added a special case for the entry, called "report=", this is the PDF file location of the report associated with this entry 
            //PROBLEM: the acme scanReader reads the line in the file, but if the line has any spaces is becomes the next attribute.
            //         so, in the valuecharts file, the user needs to specify the entire line, including the path as one single line
            //         We suggest using "$" to replace spaces
            //         This method also adds a double slash when the user enters a single (necessary for java opening the file)
            if (attributeName.startsWith("report=")) {
                File reportFileLocation = new File(attributeName.substring(7).replace("$", " ").replace("\\", "\\\\"));
                entry.setReport(reportFileLocation);

                //***Now create the windows necessary to hold the pdf in view
                //build a controller
                SwingController controller = new SwingController(); //Used for each window that controls a pdf document
                SwingViewBuilder factory = new SwingViewBuilder(controller);

                // Build a SwingViewFactory configured with the controller
                JPanel viewerComponentPanel = new JPanel(); //the panel for which the frame sits on;
                viewerComponentPanel = factory.buildViewerPanel();
                // add copy keyboard command
                ComponentKeyBinding.install(controller, viewerComponentPanel);
                // add interactive mouse link annotation support via callback
                controller.getDocumentViewController().setAnnotationCallback(new org.icepdf.ri.common.MyAnnotationCallback(controller.getDocumentViewController()));
                //Use the factory to build a JPanel that is pre-configured
                //with a complete, active Viewer UI.
                // Open a PDF document (frame is no yet visible, but the document is opened and ready for interaction)
                controller.openDocument(reportFileLocation.toString());
                //Make it continuous view
                controller.setPageFitMode(org.icepdf.core.views.DocumentViewController.PAGE_FIT_WINDOW_WIDTH, false);
                controller.setPageViewMode(DocumentViewControllerImpl.ONE_COLUMN_VIEW, true);

                // Create a JFrame to display the panel in
                JFrame window = new JFrame("Report for " + entryName); //the windows created for the attribute, these are used to show the report. They are class variables because we need to toggle visibility from different methods
                window.getContentPane().add(viewerComponentPanel);
                window.pack();
                window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); //we only want to hide the window, not close it.
                window.setSize(new Dimension(800,600));
                
                //The Frame and controller are the only aspects of the report frame that need to be modified from elsewhere in the program (particularly AttributeCell, so they become part of the hashmap
                entry.setReportFrame(window);
                entry.setReportController(controller);

                //Now add the outline and bookmark items to the hash map
                OutlineItem entryItem = null;
                Outlines entryOutlines = controller.getDocument().getCatalog().getOutlines();
                if (entryOutlines != null) {
                    entryItem = entryOutlines.getRootOutlineItem();
                }
                if (entryItem != null) {
                    OutlineItemTreeNode outlineItemTreeNode = new OutlineItemTreeNode(entryItem);
                    outlineItemTreeNode.getChildCount();  // Added this line
                    Enumeration depthFirst = outlineItemTreeNode.depthFirstEnumeration();
                    // find the node you need
                }
                
                entry.setOutlineItem(entryItem);
                //entry.map.put("Outlines", entryOutlines); //this doesn't need to be added, it is only the items that are needed for locating
                
                
            } else {
//            	AttributeCellGroup grpCell = pane.getAttributeCellGroup(attributeName);  
//            	System.out.println(pane.getAttributeCellGroup(attributeName).getName());
//            	System.out.println(pane.rowListGroup.size());
        		for(int i = 0; i < listOfAttributeMaps.size(); i++){
//        			listOfAttributeMaps.get(i).printDomainMap();
        			if(listOfAttributeMaps.get(i).userName.equals(fname)){
//        		if(grpCell == null){
//        			throw new IOException("(For group)attribute " + attributeName + " not found");
//        		}
            	AttributeValue value;
//            	AttributeDomain grpDomain = grpCell.getDomain();
//            	if (grpDomain.getType() == AttributeDomain.DISCRETE) {
//                    String name = scanReader.scanString("%S");
//                    value = new AttributeValue(name, grpDomain);
//                } else { // type == AttributeDomain.CONTINUOUS
//                    double x = scanReader.scanDouble();
//                    value = new AttributeValue(x, grpDomain);
//                }
//                entry.map.put(attributeName, value);
        				if(!listOfAttributeMaps.get(i).attributeDomainMap.isEmpty()){
        					AttributeDomain domain = listOfAttributeMaps.get(i).attributeDomainMap.get(attributeName);
        					if (domain.getType() == AttributeDomain.DISCRETE) {
                                String name = scanReader.scanString("%S");
                                value = new AttributeValue(name, domain);
                            } else { // type == AttributeDomain.CONTINUOUS
                                double x = scanReader.scanDouble();
                                value = new AttributeValue(x, domain);
                            }
        					entry.map.put(attributeName, value);
        					break;
        				}        				
        			}
        		}
    		}                
    	}
        return entry;
    }

    //PRINT 
    
    private void printlistOfAttributeMaps(ArrayList<IndividualAttributeMaps> maps){
    	for(IndividualAttributeMaps iam : maps){
    		iam.printDomainMap();
    		System.out.println();
    		iam.printWeightMap();
    		System.out.println("---------------------------------------------");
    	}
    }
    
    private void printEntryMap(ArrayList<IndividualEntryMap> list){
		if(list.isEmpty()){
			System.out.println("There are no entry maps.");
		}
		else{
			for(int i = 0; i<list.size(); i++){
				System.out.println("Username: " + list.get(i).username + "\n");				
				printChartEntry(list.get(i).getEntryMap());
				System.out.println("\n");
			}
		}
	}
    
    public void printChartEntry(LinkedHashMap<String, ChartEntry> entryMap){
		for (ChartEntry entry : entryMap.values()) {
			System.out.println("Entry: " + entry.name);
			HashMap<String,AttributeValue> map = entry.map;
			for(Map.Entry<String, AttributeValue> e : map.entrySet()){
//				String key = e.toString();			
				System.out.println("Attribute name: " + e.getKey() + " " + "Attribute value: " + e.getValue().weight());
			}
		}	
	}
    
    
    public void printAttributeColorMap(ArrayList<IndividualAttributeMaps> maps){
    	for(IndividualAttributeMaps iam : maps){
//    		iam.printDomainMap();
//    		System.out.println();
    		iam.printColorMap();
    		System.out.println("---------------------------------------------");
    	}
    }
//BOOLS
    public void showEditView(int idx) {
        setConnectingFields();
        con.constPane.setSelectedIndex(idx);
        con.frame.setVisible(true);
    }

    public boolean isPumpSelected() {
        return pump;
    }

    public int getDirectionSA() {
        return sa_dir;
    }
    
    public String getChartTitle(){
        if (chartTitle == null){
            return "__no title given__";
        } else {
            return chartTitle;
            
        }
    }
    
    /////////////////////
    // GROUP ACTIONS

    public void setTopChoices(boolean tc){
    	topChoices = tc;
    }
    
    public void setHideNonCompeting(boolean h) {
        hideNonCompete = h;
    }
    
    public void showAverageWeights(boolean b) {
    	showAvgWeights = b;
	}
    
    public void showAverageScores(boolean b) {
        showAvgScores = b;
    }
    
    // gives rank weight from 0-8, 8 being the strongest colour
    public HashMap<String, Integer> rankVarianceScore() {
        if (rankVarScore != null) return rankVarScore;
        
        rankVarScore = CriteriaStatistics.rankVarianceScore(this);
        return rankVarScore;
    }
    
    // gives rank weight from 0-8, 8 being the strongest colour
    public HashMap<String, Integer> rankVarianceWeight() {
        if (rankVarWeight != null) return rankVarWeight;
        
        rankVarWeight = CriteriaStatistics.rankVarianceWeight(listOfWeightMaps);
        return rankVarWeight;
    }
    
 // gives rank weight from 0-8, 8 being the strongest colour
    public HashMap<String, Integer> rankVarianceUtility() {
        if (rankVarUtil != null) return rankVarUtil;
        
        rankVarUtil = CriteriaStatistics.rankVarianceUtility(listOfAttributeMaps);
        return rankVarUtil;
    }
    
    public void setCriteriaHightlight(int type) {
        HashMap<String, Integer> ranks = null;
        if (type == CriteriaStatistics.SCORE)
            ranks = rankVarianceScore();
        else if (type == CriteriaStatistics.WEIGHT)
            ranks = rankVarianceWeight();
        else if (type == CriteriaStatistics.UTILITY)
            ranks = rankVarianceUtility();
        
        if (ranks == null) return;
        for (BaseTableContainer base : prims.values()) {
            Integer rank = ranks.get(base.getName());
            if (rank != null) {
                base.setHighLight(rank);
            }
        }
    }
    
    public void clearCriteriaHightlight() {
        for (BaseTableContainer base : prims.values()) {
            base.setHighLight(-1);
        }
    }

//SHOWS/FRAMES
    
    public boolean createReportController() {
        //entries is the number of entries/scenarios/alternatives

        //get the report location if associated with the entries

        controller = new SwingController();
        //From: http://greenxgene.blogspot.ca/2012/10/how-to-open-pdf-file-from-your-java.html

        //build a controller
        //SwingController controller = new SwingController(); //this is now a class variable
        //Above is now done at class level

        // Build a SwingViewFactory configured with the controller
        SwingViewBuilder factory = new SwingViewBuilder(controller);
        viewerComponentPanel = factory.buildViewerPanel();

        // add copy keyboard command
        ComponentKeyBinding.install(controller, viewerComponentPanel);

        // add interactive mouse link annotation support via callback
        controller.getDocumentViewController().setAnnotationCallback(
                new org.icepdf.ri.common.MyAnnotationCallback(
                controller.getDocumentViewController()));

        //Use the factory to build a JPanel that is pre-configured
        //with a complete, active Viewer UI.
        // Open a PDF document to view
        controller.openDocument(reportFile.toString());

        //Make if continuous view
        controller.setPageFitMode(org.icepdf.core.views.DocumentViewController.PAGE_FIT_WINDOW_WIDTH, false);
        controller.setPageViewMode(DocumentViewControllerImpl.ONE_COLUMN_VIEW, true);
        
        //Set the outline variable so its contents can be quickly searched
        outlines = controller.getDocument().getCatalog().getOutlines();

        //Now create the window
        reportWindow = new JFrame("Details on Criteria Used in Report"); //the windows created for the attribute, these are used to show the report. They are class variables because we need to toggle visibility from different methods
        reportWindow.getContentPane().add(viewerComponentPanel);
        reportWindow.pack();
        reportWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); //we only want to hide the window, not close it.
        reportWindow.setSize(new Dimension(800,600));
        //If it the swingworkes weren't already created, they are now!
        return true;
    }

    //if the bookmarkTitle exists in the report, then it will show the window, zoom to the location and return a value >= 0
    public Integer criteriaBookmarkExistsInReport(String bookmarkTitle) {

        //This method finds the associated bookmark
        if (controller != null) {
            //From: http://www.icesoft.org/JForum/posts/list/13433.page
            //Get the bookmarks (icepdf calls them outline items)


            if (outlines != null) {
                item = outlines.getRootOutlineItem();
            }
            if (item != null) {
                OutlineItemTreeNode outlineItemTreeNode = new OutlineItemTreeNode(item);
                outlineItemTreeNode.getChildCount();  // Added this line
                Enumeration depthFirst = outlineItemTreeNode.depthFirstEnumeration();
                // find the node you need


                //Loop through the bookmarks looking for a matching title
                //The index of the bookmark that we want to navigate to
                Integer bookMark = 0;
                Integer numDocBookmarks = item.getSubItemCount();
                String bookMarkName; //this is to store the name of the item, from outlines, which are the bookmarks that the computer sees in the PDF document

                //Bookmarks in the PDF tend to have spaces added onto the end of the name, so that is why the replace and trim methods are used
                while (bookMark < numDocBookmarks) {
                    bookMarkName = item.getSubItem(bookMark).getTitle().replace("?", "").trim();
                    //System.out.println("|" + bookMarkName + "/" + bookmarkTitle.toString() + "|");
                    if (bookMarkName.equals(bookmarkTitle.toString()) || bookMarkName.equals(bookmarkTitle.toString() + " ") || bookMarkName.equals(bookmarkTitle.toString().replace('_', ' ')) || bookMarkName.equals(bookmarkTitle.toString().replace('_', ' ') + " ")) {
                        break;
                    }
                    bookMark++;
                    //TODO: Return if bookmark not found
                }

                if (bookMark >= numDocBookmarks) {
                    //System.out.println("There is no bookmark in the document for the given attribute");
                    return -1;
                } else {
                    return bookMark;
                }
            } else {
                //the report does not exist anyway, so no controller was created, return -1
                return -1;
            }
        } else {
            //the report does not exist anyway, so no controller was created, return -1
            return -1;
        }
    }          
    
    //zoom the the location in the report
    public void zoomToReport(String bookmarkTitle) {
        //go to the location
        if (!reportWindow.isVisible()) {
            reportWindow.setVisible(true);
        }
        //go the the location in the document
        controller.followOutlineItem(item.getSubItem(criteriaBookmarkExistsInReport(bookmarkTitle)));
    }
    
    //allows other classes to verify if the report does exist
    public boolean reportExists() {
        if (reportFile == null) {
            return false;
        } else if (reportFile.exists()) {
            return true;
        } else {
            return false;
        }

    }
                    
    public void gotoBookmark(Integer bookMark) {
        //Since the window is initially hidden, or when the user closes the window is become hidden (not closed), we need to check this first
        //But only show the window if the request has been such

    }
    
    public DisplayPanel getDisplayPanel() {
        return displayPanel;
    }

    public void resetDisplay(int type, int colwd, boolean close, boolean graph,int colorOption, String user) {
    	ValueChart ch = new ValueChart(con, filename, users, type, colwd, true, graph,colorOption,false, false);
    	ch.showAbsoluteRatios = this.showAbsoluteRatios;   
    	ch.userToPickAttributeColor = user;
        ch.pump = pump;
        ch.pump_increase = pump_increase;
        ch.sa_dir = sa_dir;
        ch.getDisplayPanel().setScore(getDisplayPanel().score);
        ch.getDisplayPanel().setRuler(getDisplayPanel().ruler);        
        for (int j = 0; j < entryList.size(); j++) {
            if (((ChartEntry) entryList.get(j)).getShowFlag()) {
                ((ChartEntry) (ch.entryList.get(j))).setShowFlag(true);
                ch.updateAll();
            }
        }
        ValueChartsPlus.chart = ch;
        if (close) {
            closeChart();
        } else {
            chartFrame.setTitle(chartFrame.getTitle() + " (past)");
            
        }
    }

    /* not used
     * 
    //To paint the chart using colors from attributes
    public void resetDisplayUsingColorForAttribute(int type, int colwd, boolean close, boolean graph,int colorOption, String user) {
    	ValueChart ch = new ValueChart(con, filename, users, type, colwd, true, graph,colorOption, false);
    	ch.userToPickAttributeColor = user;
    	ch.showAbsoluteRatios = this.showAbsoluteRatios;    	
        ch.pump = pump;
        ch.pump_increase = pump_increase;
        ch.sa_dir = sa_dir;
        ch.getDisplayPanel().setScore(getDisplayPanel().score);
        ch.getDisplayPanel().setRuler(getDisplayPanel().ruler);
        for (int j = 0; j < entryList.size(); j++) {
            if (((ChartEntry) entryList.get(j)).getShowFlag()) {
                ((ChartEntry) (ch.entryList.get(j))).setShowFlag(true);
                ch.updateAll();
            }
        }
        closeChart();
    }*/
    
    public void compareDisplay(int type, int colwd) {
        ValueChart ch = new ValueChart(con, filename, users, type, colwd, false, show_graph,ValueChart.COLORFORUSER,false, false);
        ch.showAbsoluteRatios = this.showAbsoluteRatios; 
        ch.pump = pump;
        ch.pump_increase = pump_increase;
        ch.sa_dir = sa_dir;
        ch.getDisplayPanel().setScore(getDisplayPanel().score);
        ch.getDisplayPanel().setRuler(getDisplayPanel().ruler);
        for (int j = 0; j < entryList.size(); j++) {
            if (((ChartEntry) entryList.get(j)).getShowFlag()) {
                ((ChartEntry) (ch.entryList.get(j))).setShowFlag(true);
                ch.updateAll();
            }
        }
    }
    
    public void avgGVCDisplay(int type, int colwd, boolean avgGVC, boolean dAvgGVC) {
        ValueChart ch = new ValueChart(con, filename, users, type, colwd, false, show_graph,ValueChart.COLORFORUSER,avgGVC, dAvgGVC);
        ch.showAbsoluteRatios = this.showAbsoluteRatios; 
        ch.pump = pump;
        ch.pump_increase = pump_increase;
        ch.sa_dir = sa_dir;
        ch.getDisplayPanel().setScore(getDisplayPanel().score);
        ch.getDisplayPanel().setRuler(getDisplayPanel().ruler);
        for (int j = 0; j < entryList.size(); j++) {
            if (((ChartEntry) entryList.get(j)).getShowFlag()) {
                ((ChartEntry) (ch.entryList.get(j))).setShowFlag(true);
                ch.updateAll();
            }
        }
    }

    public void setDisplayType(int type) {
        displayType = type;
    }

    public void closeChart() {
        if (displayType == SEPARATE_DISPLAY) {
            dialog.dispose();
        }
        chartFrame.dispose();
    }

    void showVC() {
        chartFrame = new JFrame("ValueChart for " + chartTitle);
        chartFrame.getContentPane().setLayout(new BoxLayout(chartFrame.getContentPane(), BoxLayout.Y_AXIS));
//        if (displayType == SIDE_DISPLAY) {
//            pnlOpt = new SensitivityAnalysisOptions(this);
//            chartFrame.getContentPane().add(pnlOpt);
//        }
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                for (int i = 0; i < chartFrame.getContentPane().getComponentCount(); i++) {
                    if (chartFrame.getContentPane().getComponent(i) instanceof ValueChart) {
                        ValueChart ch = (ValueChart) chartFrame.getContentPane().getComponent(i);
                        if (ValueChartsPlus.chart == ch) {
                            System.exit(0);
                        }
                    }
                }
            }
        };
        chartFrame.addWindowListener(exitListener);
        chartFrame.getContentPane().add(this);
//        chartFrame.getContentPane().setSize(chartFrame.getContentPane().getSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
        chartFrame.validate();
        chartFrame.pack();
        chartFrame.setVisible(true);
    }
    
    private class DisplayDialog extends JDialog {

        private static final long serialVersionUID = 1L;

        public DisplayDialog(Frame aFrame, String title, JPanel panel) {
            super(aFrame, false);
            setTitle(title);
            setModal(false);
            setContentPane(panel);
            setDefaultCloseOperation(HIDE_ON_CLOSE);
            pack();
        }
    }

    private class ResizeHandler extends ComponentAdapter {

        public void componentResized(ComponentEvent e) {
            if (mainPane != null) {
                mainPane.updateSizesAndHeights();
                mainPane.revalidate();
                alignDisplayPanel();
                pnlDisp.revalidate();
            }
        }
    }

    public JFrame getFrame() {
        return chartFrame;
    }

    public void showDomainVals(int i) {
        ChartEntry c = (ChartEntry) entryList.get(i);
        pnlDom.showData(c);
    }

    public class LastInteraction {

        public static final int NO_UNDO = 0,
                SLIDE = 1,
                PUMP = 2,
                UTIL = 3;
        int type;
        int delY;
        int dragY;
        BaseTableContainer base;
        boolean pump;
        boolean east;
        JMenuItem menu;
        String elt;
        double weight;
        double knot;
        JPanel pnlUtil;
        AttributeDomain domain;

        LastInteraction(JMenuItem m) {
            type = NO_UNDO;
            menu = m;
        }

        void reset() {
            type = NO_UNDO;
            menu.setEnabled(false);
        }

        void setUndoUtil(JPanel p, String e, double k, double wt, AttributeDomain ad) {
            type = UTIL;
            menu.setEnabled(true);
            next_int.menu.setEnabled(false);
            pnlUtil = p;
            elt = e;
            knot = k;
            weight = wt;
            domain = ad;
        }

        void setUndoSlide(BaseTableContainer b, int dy, int ry, boolean e) {
            type = SLIDE;
            menu.setEnabled(true);
            next_int.menu.setEnabled(false);
            base = b;
            dragY = ry;
            delY = dy;
            east = e;
        }

        void setUndoPump(BaseTableContainer b, boolean p) {
            type = PUMP;
            menu.setEnabled(true);
            next_int.menu.setEnabled(false);
            base = b;
            pump = p;
        }

        void setRedo(LastInteraction last) {
            last.menu.setEnabled(true);
            last.type = type;
            last.dragY = dragY;
            last.delY = -delY;
            last.pump = pump ? false : true;
            last.base = base;
            last.pnlUtil = pnlUtil;
            last.knot = knot;
            last.elt = elt;
            last.domain = domain;
            if (domain != null) {
                if (domain.getType() == AttributeDomain.DISCRETE) {
                    DiscreteAttributeDomain d = domain.getDiscrete();
                    last.weight = d.getEntryWeight(elt);
                } else {
                    ContinuousAttributeDomain c = domain.getContinuous();
                    last.weight = c.weight(knot);
                }
            }

        }

        void undo() {
            switch (type) {
                case SLIDE: {
                    base.dragY = dragY;
                    //if (east)
                    base.mouseHandler.setEastRollup(base);
                 /*   	    			else
                     base.mouseHandler.setWestRollup(base);*/
                    base.mouseHandler.eastRollupStretchDrag(delY);
                    updateAll();
                    break;
                }
                case PUMP: {
                    base.mouseHandler.pump(base, pump);
                    break;
                }
                case UTIL: {
                    if (pnlUtil instanceof ContGraph) {
                        ContGraph cg = (ContGraph) pnlUtil;
                        cg.cdomain.changeWeight(knot, weight);
                        cg.plotPoints();
                    } else if (pnlUtil instanceof DiscGraph) {
                        DiscGraph dg = (DiscGraph) pnlUtil;
                        dg.ddomain.changeWeight(elt, weight);
                        dg.plotPoints();
                    } else if (pnlUtil instanceof ContinuousUtilityGraph) {
                        ContinuousUtilityGraph cug = (ContinuousUtilityGraph) pnlUtil;
                        domain.getContinuous().changeWeight(knot, weight);
                        cug.acell.cg.plotPoints();
                    } else if (pnlUtil instanceof DiscreteUtilityGraph) {
                        DiscreteUtilityGraph dug = (DiscreteUtilityGraph) pnlUtil;
                        domain.getDiscrete().changeWeight(elt, weight);
                        dug.acell.dg.plotPoints();
                    }
                    updateAll();
                }
            }
            reset();
        }
    }
    

    public void alignDisplayPanel() {
        int height = (int) (mainPane.getHeight()/this.heightScalingConstant);
        int width = mainPane.getPreferredSize().width;

        displayPanel.setPrefHeight(height);
        displayPanel.setMaximumSize(displayPanel.getPreferredSize());
        displayPanel.setMinimumSize(displayPanel.getPreferredSize());
        
        pnlDisp.setPreferredSize(new Dimension(width, height));
        pnlDisp.setMaximumSize(pnlDisp.getPreferredSize());
        pnlDisp.setMinimumSize(pnlDisp.getPreferredSize());

//        displayPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 10));
//        displayWithNames.setBorder(BorderFactory.createLineBorder(Color.CYAN, 10));
//        pnlDisp.setBorder(BorderFactory.createLineBorder(Color.RED, 10));

    }
    
/*    public void getDisplayPanelHeight(){
    	int numEntries = entryList.size();
    	Vector cellList = new Vector(16);
        mainPane.getAttributeCells(cellList);
        int cellHeight;
        double maxAttributeWeight;
        int totalDisplayHeight = 0;
    	for (int i = 0; i < numEntries; i++) {
    		for(IndividualEntryMap e : this.listOfEntryMaps){
    			for (Iterator it = cellList.iterator(); it.hasNext();) {
    				totalDisplayHeight = 0;
    				AttributeCell cell = (AttributeCell) it.next();
    				cellHeight = cell.getHeight(); 
    				double individualHeightRatio = cell.getHeightFromAttributeMap(e.username,cell.getName()); //get heightRatio for each rectangle from the attribute weight maps
    				maxAttributeWeight = this.maxWeightMap.get(cell.getName());
    				int individualHeight = (int) Math.round(individualHeightRatio*cellHeight/maxAttributeWeight);
    				totalDisplayHeight += individualHeight;
    			}
    		}
    		System.out.println(totalDisplayHeight);
    	}
    }*/

}
