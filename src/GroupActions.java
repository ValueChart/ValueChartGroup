//This class is for picking colors to distinguish users or attributes 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;


public class GroupActions extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	JRadioButton forUsers;
	JRadioButton forAttributes;	
	JRadioButton forIntensity;
	
	JRadioButton none;
	JRadioButton allAgree;
	JRadioButton mostVariedHeights;
	JRadioButton topChoice;
	JRadioButton topTwoChoices;
	JLabel disagreement;
	JCheckBox showAverageAlternatives;
	JCheckBox showAverageWeights;
	JCheckBox hideUncompetitiveAlts;
	
	JButton generateAvgGVC;
	
	JComboBox<String> userList;
	JButton remapColors;
	Vector<String> users;
	ValueChart chart;
	JPanel pnlUserColors;
	UserLegendPanel legendMain;
	JPanel pnlMaster;
	JPanel pnlDetails;
	JPanel pnlUserNColors;
	HashMap<String,Color> tempUserColorMap;
	boolean showLegend = true;

	JComboBox<String> selectVarMode;
	
	public GroupActions(ValueChart ch) {
		chart = ch;
		tempUserColorMap = new HashMap<String,Color>();
		Font font = new Font("Arial", Font.PLAIN, 11);
		users = new Vector<String>();
		for(int i = 0; i<chart.users.size(); i++){
			if(!chart.users.get(i).equals("SuperUser.vc"))
				users.addElement(chart.users.get(i));			
		}		
		ButtonGroup grpColor = new ButtonGroup();
		ButtonGroup grpDetails = new ButtonGroup();
		
		forAttributes = new JRadioButton("Attributes");
		forAttributes.setActionCommand("attributes");
		forAttributes.setEnabled(true);
		forAttributes.addActionListener(this);
		forAttributes.setFont(font);		
        grpColor.add(forAttributes);
        
		forUsers = new JRadioButton("Users");
		forUsers.setActionCommand("users");			
		forUsers.setSelected(true);
		forUsers.setEnabled(true);
		forUsers.addActionListener(this);
		forUsers.setFont(font);
		grpColor.add(forUsers);
        
		userList = new JComboBox<String>(users);
//		userList = new JComboBox<String>(petStrings);
		userList.setActionCommand("selectUser");
		userList.setSelectedIndex(0);
		userList.setEnabled(false);		
		userList.setPreferredSize(new Dimension(65, 10));
		userList.setMaximumSize(new Dimension(65,10));
		userList.addActionListener(this);
//		grpColor.add(userList);
        
        forIntensity = new JRadioButton("Intensity");
        forIntensity.setActionCommand("intensity");        
        forIntensity.setEnabled(true);
        forIntensity.addActionListener(this);
        forIntensity.setFont(font);
        grpColor.add(forIntensity);
        
        remapColors = new JButton("Remap Colors");
        remapColors.setActionCommand("remap");
        remapColors.setEnabled(true);
        remapColors.addActionListener(this);
        remapColors.setFont(font);
        
        legendMain = new UserLegendPanel(chart, this);
        for(int i = 0 ; i < users.size() ; i++){
            tempUserColorMap.put(users.get(i), getUserColorFromAttributeMap(chart, users.get(i)));
        }
                
        
        pnlUserColors = new JPanel();
        pnlUserColors.setLayout(new BoxLayout(pnlUserColors, BoxLayout.Y_AXIS));
        pnlUserColors.add(forUsers);
//        pnlUserColors.add(legendMain);
//        pnlUserColors.add(forAttributes);
//        pnlUserColors.add(userList);
        pnlUserColors.add(forIntensity);
        pnlUserColors.add(remapColors);
        pnlUserColors.add(Box.createVerticalGlue());
//        pnlUserColors.add(Box.createGlue());
        
        none = new JRadioButton("Default");
        none.setActionCommand("none");
        none.setSelected(true);
        none.setEnabled(true);
        none.addActionListener(this);
        none.setFont(font);		
//        none.setAlignmentX(LEFT_ALIGNMENT);
        grpDetails.add(none);
        
        allAgree = new JRadioButton("Most agreed alternative");
        allAgree.setActionCommand("mostAgreed");
        allAgree.setEnabled(true);
        allAgree.addActionListener(this);
        allAgree.setFont(font);	
//        allAgree.setAlignmentX(LEFT_ALIGNMENT);
        grpDetails.add(allAgree);
        
        mostVariedHeights = new JRadioButton("Most controversial alternative");
        mostVariedHeights.setActionCommand("mostAgreed");
        mostVariedHeights.setEnabled(true);
        mostVariedHeights.addActionListener(this);
        mostVariedHeights.setFont(font);		
        grpDetails.add(mostVariedHeights);
        
        topChoice = new JRadioButton("Top choices");
        topChoice.setActionCommand("topChoices");
        topChoice.setEnabled(true);
        topChoice.addActionListener(this);
        topChoice.setFont(font);		
        grpDetails.add(topChoice);
        
        topTwoChoices = new JRadioButton("Top two choices");
        topTwoChoices.setActionCommand("topTwoChoices");
        topTwoChoices.setEnabled(true);
        topTwoChoices.addActionListener(this);
        topTwoChoices.setFont(font);		
        grpDetails.add(topTwoChoices);
        
        disagreement = new JLabel("Criteria Variance HeatMap");
        disagreement.setFont(font);
        String[] selectOpt = {"None", "by Score","by Weight","by Utility"};
        
        selectVarMode = new JComboBox<String>(selectOpt);
        selectVarMode.setSelectedIndex(0);
        selectVarMode.setActionCommand("varSelect");
        selectVarMode.addActionListener(this);
        selectVarMode.setFont(font);
        selectVarMode.setMaximumSize(selectVarMode.getPreferredSize());
        selectVarMode.setAlignmentX(LEFT_ALIGNMENT);
        
        generateAvgGVC = new JButton("Average Group VC");
        generateAvgGVC.setActionCommand("generateAvgGVC");
        generateAvgGVC.setEnabled(true);
        generateAvgGVC.setFont(font);
        generateAvgGVC.addActionListener(this);                
        
        showAverageAlternatives = new JCheckBox("Show Average Alternatives");
        showAverageAlternatives.setActionCommand("showAverageAlternatives");
        showAverageAlternatives.setEnabled(true);
        showAverageAlternatives.setSelected(false);
        showAverageAlternatives.addActionListener(this);
        showAverageAlternatives.setFont(font);
        
        
        showAverageWeights = new JCheckBox("Show Average Weights");
        showAverageWeights.setActionCommand("showAverageWeights");
        showAverageWeights.setEnabled(true);
        showAverageWeights.setSelected(false);
        showAverageWeights.addActionListener(this);
        showAverageWeights.setFont(font);
        
        hideUncompetitiveAlts = new JCheckBox("Hide non-competitive Alternatives");
        hideUncompetitiveAlts.setActionCommand("hideNonCompAlts");
        hideUncompetitiveAlts.setEnabled(true);
        hideUncompetitiveAlts.setSelected(false);
        hideUncompetitiveAlts.addActionListener(this);
        hideUncompetitiveAlts.setFont(font);
        
        
        pnlDetails = new JPanel();        
        pnlDetails.setLayout(new BoxLayout(pnlDetails, BoxLayout.Y_AXIS));
        pnlDetails.add(none);
//        pnlDetails.add(allAgree);
//        pnlDetails.add(mostVariedHeights);
        pnlDetails.add(topChoice);
        pnlDetails.add(generateAvgGVC);
        
//        pnlDetails.add(topTwoChoices);
        pnlDetails.add(Box.createHorizontalStrut(5));
        pnlDetails.add(new JSeparator(SwingConstants.HORIZONTAL));
        pnlDetails.add(Box.createHorizontalStrut(5));
        
        pnlDetails.add(disagreement);
        pnlDetails.add(selectVarMode);
        
        pnlDetails.add(Box.createHorizontalStrut(5));
        pnlDetails.add(new JSeparator(SwingConstants.HORIZONTAL));
        pnlDetails.add(Box.createHorizontalStrut(5));
        
        pnlDetails.add(showAverageAlternatives);
        pnlDetails.add(showAverageWeights);
        pnlDetails.add(hideUncompetitiveAlts);
//        pnlDetails.add(userList);
//        pnlDetails.add(Box.createVerticalGlue());
//        pnlDetails.add(Box.createGlue());
        
        pnlUserColors.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), "Apply color for:", 0, 0, font));
        legendMain.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), "Users:", 0, 0, font));
        pnlDetails.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), "Details", 0, 0, font));
        
        
        pnlUserNColors = new JPanel();
        pnlUserNColors.setLayout(new BoxLayout(pnlUserNColors, BoxLayout.Y_AXIS));
        pnlUserColors.setAlignmentX(CENTER_ALIGNMENT);
        pnlUserNColors.add(pnlUserColors);
        pnlUserNColors.add(legendMain);
        
        pnlMaster = new JPanel();
        pnlMaster.setLayout(new BoxLayout(pnlMaster, BoxLayout.X_AXIS));
        pnlMaster.add(pnlUserNColors);
//        pnlMaster.add(Box.createRigidArea(new Dimension(20, 0)));
        pnlMaster.add(pnlDetails);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));        
        add(pnlMaster);     
	}
		
	//Get a distinct color for every user 
    public static Color getUserColorFromAttributeMap(ValueChart chart, String filename){
    	Color aColor = Color.BLACK;
    	for(IndividualAttributeMaps a : chart.listOfAttributeMaps){
    		
    		if(filename.equals(a.userName)){
    			aColor = a.userColor;
    		}
    	}
    	return aColor;
    }    
    
    public void setUserColorInAttributeMap(String user, Color color){
    	for(IndividualAttributeMaps a : chart.listOfAttributeMaps){
    		if(!user.equals(a.userName.substring(0, a.userName.length()-3))){    			
    			a.userColor = Color.GRAY; 
    			chart.setMouseOver(a.userName);
    		}
    	}
    }
    
    public void setUserColorInAttributeMapBack(){
    	for(Map.Entry<String,Color> entry : tempUserColorMap.entrySet()){
    		for(IndividualAttributeMaps a : chart.listOfAttributeMaps){
    			a.mouseOver = false;
    			if(entry.getKey().equals(a.userName)){
    				a.userColor = entry.getValue();    				
    			}    		    			
    		}
    	}
    }
    
    /*public void getTopChoices(){
    	for(Map.Entry<String, String> entry : chart.getDisplayPanel().topAltforUsers.entrySet()){
    		for(IndividualAttributeMaps a : chart.listOfAttributeMaps){    		
    			if(!(entry.getKey().equals(a.userName) && entry.getValue().equals(a.topAlternative))){
    				System.out.println(entry.getKey()+" "+entry.getValue());
    				System.out.println(a.userName+" "+a.topAlternative);
    				a.userColor = Color.GRAY; 
//        			chart.setMouseOver(a.userName);
        		}
    		}    		
    	}
    }*/
    
    @Override
	public void actionPerformed(ActionEvent ae) {
		if ("users".equals(ae.getActionCommand())){
//			chart.coloredUser = true;
			forUsers.setSelected(true);
			remapColors.setEnabled(true);
			userList.setEnabled(false);
		}					
		else if ("attributes".equals(ae.getActionCommand())){
//			chart.coloredUser = false;
			forAttributes.setSelected(true);
			remapColors.setEnabled(false);
			userList.setEnabled(true);						
		}
		else if("intensity".equals(ae.getActionCommand())){
			userList.setEnabled(false);
			remapColors.setEnabled(false);
		}		
		else if("remap".equals(ae.getActionCommand())){
			chart.resetDisplay(chart.displayType, chart.colWidth, true, chart.show_graph,ValueChart.COLORFORUSER,null);
		}
		else if("selectUser".equals(ae.getActionCommand())){
			JComboBox cb = (JComboBox)ae.getSource();
			String aUser = (String)cb.getSelectedItem();
			chart.updateAll();
//			chart.resetDisplay(chart.displayType, chart.colWidth, true, chart.show_graph,ValueChart.COLORFORATTRIBUTE,aUser);
//			chart.resetDisplay(chart.displayType, chart.colWidth, true, chart.show_graph,ValueChart.COLORFORATTRIBUTE);
		}
		else if("showAverageAlternatives".equals(ae.getActionCommand())){
			if (showAverageAlternatives.isSelected())
				chart.getDisplayPanel().setShowAvg(true);
			else
				chart.getDisplayPanel().setShowAvg(false);
			chart.updateDisplay();
		}
		else if("topChoices".equals(ae.getActionCommand())){
			topChoice.setSelected(true);
//			getTopChoices();
			chart.setTopChoices(true);
			chart.updateAll();
		}
		else if("none".equals(ae.getActionCommand())){
			none.setSelected(true);
			chart.setTopChoices(false);
			setUserColorInAttributeMapBack();
			chart.updateAll();
		}
		else if("topTwoChoices".equals(ae.getActionCommand())){
			topTwoChoices.setSelected(true);
			userList.setEnabled(true);
		}
		else if("showAverageWeights".equals(ae.getActionCommand())){
			if (showAverageWeights.isSelected())
				chart.showAverageWeights(true);
			else
				chart.showAverageWeights(false);
			chart.updateAll();
		}
		else if("generateAvgGVC".equals(ae.getActionCommand())){
			chart.avgGVCDisplay(chart.displayType, ValueChart.DEFAULT_USER_COL_WIDTH,true);
		}
		else if ("hideNonCompAlts".equals(ae.getActionCommand())) {
		    if (hideUncompetitiveAlts.isSelected())
		        chart.setHideNonCompeting(true);
		    else
		        chart.setHideNonCompeting(false);
            chart.updateAll();
        }
		else if ("varSelect".equals(ae.getActionCommand())) {
		    String s = (String)(((JComboBox<String>) ae.getSource()).getSelectedItem());
		    if (s.equals("None")) {
                chart.clearCriteriaHightlight();
		    } else if (s.equals("by Score")) {
		        chart.setCriteriaHightlight(CriteriaStatistics.SCORE);
		    } else if (s.equals("by Weight")) {
	            chart.setCriteriaHightlight(CriteriaStatistics.WEIGHT);
	        } else if (s.equals("by Utility")) {
	            chart.setCriteriaHightlight(CriteriaStatistics.UTILITY);
	        }
            chart.updateMainPane();
            
		}
	}
	
	public void printMessage(String str){
		System.out.println(str);
	}

}
