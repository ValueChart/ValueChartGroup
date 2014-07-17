//This class is for picking colors to distinguish users or attributes 
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;


public class GroupActions extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	JRadioButton none;
	JRadioButton allAgree;
	JRadioButton mostVariedHeights;
	JRadioButton topChoice;
	JRadioButton topTwoChoices;
	JLabel disagreement;
	JCheckBox showAverageAlternatives;
	JCheckBox showAverageWeights;
	JCheckBox showAverageScores;
    JCheckBox hideUncompetitiveAlts;
	
	JComboBox<String> userList;
	Vector<String> users;
	ValueChart chart;
	UserLegendPanel legendMain;
	JPanel pnlMaster;
	JPanel pnlDetails;
	JPanel pnlUserNColors;
	HashMap<String,Color> tempUserColorMap;
	boolean showLegend = true;
	boolean averageChart = false;

	JComboBox<String> selectVarMode;
	
	public GroupActions(ValueChart ch, boolean avgChart) {
		chart = ch;
		averageChart = avgChart;
		tempUserColorMap = new HashMap<String,Color>();
		Font font = new Font("Arial", Font.PLAIN, 11);
		users = new Vector<String>();
		for(int i = 0; i<chart.users.size(); i++){
			if(!chart.users.get(i).equals("SuperUser.vc"))
				users.addElement(chart.users.get(i));			
		}		
		ButtonGroup grpDetails = new ButtonGroup();

		userList = new JComboBox<String>(users);
		userList.setActionCommand("selectUser");
		userList.setSelectedIndex(0);
		userList.setEnabled(false);		
		userList.setPreferredSize(new Dimension(65, 10));
		userList.setMaximumSize(new Dimension(65,10));
		userList.addActionListener(this);
        
		if (!averageChart) {
            legendMain = new UserLegendPanel(chart, this);
            for(int i = 0 ; i < users.size() ; i++){
                tempUserColorMap.put(users.get(i), getUserColorFromAttributeMap(chart, users.get(i)));
            }
        
            none = new JRadioButton("Default");
            none.setActionCommand("none");
            none.setSelected(true);
            none.setEnabled(true);
            none.addActionListener(this);
            none.setFont(font);		
            grpDetails.add(none);
            
            allAgree = new JRadioButton("Most agreed alternative");
            allAgree.setActionCommand("mostAgreed");
            allAgree.setEnabled(true);
            allAgree.addActionListener(this);
            allAgree.setFont(font);	
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
		}
		
        disagreement = new JLabel("Criteria Disagreement");
        disagreement.setFont(font);
        String[] selectOpt = {"None", "by Product","by Weight","by Score"};
        
        selectVarMode = new JComboBox<String>(selectOpt);
        selectVarMode.setSelectedIndex(0);
        selectVarMode.setActionCommand("varSelect");
        selectVarMode.addActionListener(this);
        selectVarMode.setFont(font);
        selectVarMode.setMaximumSize(selectVarMode.getPreferredSize());
        selectVarMode.setAlignmentX(LEFT_ALIGNMENT);              
        
        if (!averageChart) {
            showAverageAlternatives = new JCheckBox("Show Average Overall Ranking");
            showAverageAlternatives.setActionCommand("showAverageAlternatives");
            showAverageAlternatives.setEnabled(true);
            showAverageAlternatives.setSelected(false);
            showAverageAlternatives.addActionListener(this);
            showAverageAlternatives.setFont(font);
        }
        
        showAverageWeights = new JCheckBox("Show Average Weights");
        showAverageWeights.setActionCommand("showAverageWeights");
        showAverageWeights.setEnabled(true);
        showAverageWeights.setSelected(false);
        showAverageWeights.addActionListener(this);
        showAverageWeights.setFont(font);
        
        showAverageScores = new JCheckBox("Show Average Scores");
        showAverageScores.setActionCommand("showAverageScores");
        showAverageScores.setEnabled(true);
        showAverageScores.setSelected(false);
        showAverageScores.addActionListener(this);
        showAverageScores.setFont(font);
        
        if (!averageChart) {
            hideUncompetitiveAlts = new JCheckBox("Highlight Favourite Alternatives");
            hideUncompetitiveAlts.setActionCommand("hideNonCompAlts");
            hideUncompetitiveAlts.setEnabled(true);
            hideUncompetitiveAlts.setSelected(false);
            hideUncompetitiveAlts.addActionListener(this);
            hideUncompetitiveAlts.setFont(font);
        }
        
        pnlDetails = new JPanel();        
        pnlDetails.setLayout(new BoxLayout(pnlDetails, BoxLayout.Y_AXIS));
        
        if (!averageChart) {
            pnlDetails.add(none);
            pnlDetails.add(topChoice);
            
            pnlDetails.add(Box.createHorizontalStrut(5));
            pnlDetails.add(new JSeparator(SwingConstants.HORIZONTAL));
            pnlDetails.add(Box.createHorizontalStrut(5));
        }
        else {
            pnlDetails.add(Box.createVerticalGlue());
        }
        
        pnlDetails.add(disagreement);
        pnlDetails.add(selectVarMode);
        pnlDetails.add(Box.createVerticalGlue());
        
        pnlDetails.add(Box.createHorizontalStrut(5));
        pnlDetails.add(new JSeparator(SwingConstants.HORIZONTAL));
        if (!averageChart) {
            pnlDetails.add(Box.createHorizontalStrut(5));
        
            pnlDetails.add(showAverageAlternatives);
        }
        
        if (!averageChart || (averageChart && !chart.davidAvgGVC)) {
            pnlDetails.add(showAverageWeights);
        }
        
        if (!averageChart) {
            pnlDetails.add(showAverageScores);
            pnlDetails.add(hideUncompetitiveAlts);
        }
        
        if (!averageChart) {
            legendMain.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), "Users:", 0, 0, font));
        }
        pnlDetails.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(1), "Details", 0, 0, font));
        
        if (!averageChart) {
            pnlUserNColors = new JPanel();
            pnlUserNColors.setLayout(new BoxLayout(pnlUserNColors, BoxLayout.Y_AXIS));
            pnlUserNColors.add(legendMain);
        }
        
        pnlMaster = new JPanel();
        pnlMaster.setLayout(new BoxLayout(pnlMaster, BoxLayout.X_AXIS));
        if (!averageChart) {
            pnlMaster.add(pnlUserNColors);
        }
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
    		if(!user.equals(
    		        (chart.con.type == ConstructionView.FROM_VC ? a.userName.substring(0, a.userName.length()-3) :
    		            a.userName.substring(0, a.userName.length()-4)) )){    			
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
		if("selectUser".equals(ae.getActionCommand())){
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
		else if("showAverageScores".equals(ae.getActionCommand())){
            if (showAverageScores.isSelected())
                chart.showAverageScores(true);
            else
                chart.showAverageScores(false);
            chart.updateAll();
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
		    } 
	        // Popoviciu's inequality: variance <= (max-min)^2/4
	        // if max = 100 : var <= 0.25, stdev <= 0.5
		    // if max = 50 (ie. it's really bad if users disagree by 50+%) : var <= 0.0625, stdev <= 0.25
		    else if (s.equals("by Product")) {
		        HeaderLabel.DISAGREE_MAX = 25;
		        chart.setCriteriaHightlight(CriteriaStatistics.PRODUCT);
		    } else if (s.equals("by Weight")) {
		        HeaderLabel.DISAGREE_MAX = 25;
	            chart.setCriteriaHightlight(CriteriaStatistics.WEIGHT);
	        } else if (s.equals("by Score")) {
	            HeaderLabel.DISAGREE_MAX = 33;
	            chart.setCriteriaHightlight(CriteriaStatistics.SCORE);
	        }
		    chart.mainPane.updateSizesAndHeights();
            chart.updateMainPane();
            
		}
	}
	
	public void printMessage(String str){
		System.out.println(str);
	}

}
