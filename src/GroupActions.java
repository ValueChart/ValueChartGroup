//This class is for picking colors to distinguish users or attributes 
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
	JCheckBox showAverage;
	
	JComboBox<String> userList;
	JButton remapColors;
	Vector<String> users;
	ValueChart chart;
	JPanel pnlUserColors;
	JPanel legendMain;
	JPanel pnlMaster;
	JPanel pnlDetails;
	JPanel pnlUserNColors;
	HashMap<String,Color> tempUserColorMap;
	int mousedOver = -1;
	boolean showLegend = true;
	
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
		userList.setPreferredSize(new Dimension(65, 25));
		userList.setMaximumSize(new Dimension(65,25));
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
        
        legendMain = new JPanel();
        legendMain.setLayout(new BoxLayout(legendMain, BoxLayout.Y_AXIS));
        for(int i = 0 ; i < users.size() ; i++){
        	Users userLegend = new Users();
            userLegend.setLegendColor(getUserColorFromAttributeMap(users.get(i)));
            userLegend.setLegendFont(font);
            userLegend.setLegendName(users.get(i).substring(0,users.get(i).length()-3));                 
            legendMain.add(userLegend.legend);
            tempUserColorMap.put(users.get(i), getUserColorFromAttributeMap(users.get(i)));
        }
        
//        legendMain.addMouseListener(mh);
        legendMain.add(Box.createVerticalGlue());
//        legendMain.add(Box.createHorizontalGlue());
//        legendMain.add(Box.createGlue());
                
        
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
        
        showAverage = new JCheckBox("Show Average");
        showAverage.setActionCommand("showAverage");
        showAverage.setEnabled(true);
        showAverage.setSelected(false);
        showAverage.addActionListener(this);
        showAverage.setFont(font);
        
        pnlDetails = new JPanel();        
        pnlDetails.setLayout(new BoxLayout(pnlDetails, BoxLayout.Y_AXIS));
        pnlDetails.add(none);
//        pnlDetails.add(allAgree);
//        pnlDetails.add(mostVariedHeights);
        pnlDetails.add(topChoice);
//        pnlDetails.add(topTwoChoices);                
        pnlDetails.add(showAverage);
//        pnlDetails.add(userList);
        pnlDetails.add(Box.createVerticalGlue());
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
        pnlMaster.add(Box.createRigidArea(new Dimension(50, 0)));
        pnlMaster.add(pnlDetails);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));        
        add(pnlMaster);     
	}
	
	//Get a distinct color for every user 
    public Color getUserColorFromAttributeMap(String filename){
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
		else if("showAverage".equals(ae.getActionCommand())){
			if (showAverage.isSelected())
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
	}
	
	public void printMessage(String str){
		System.out.println(str);
	}
	
	
    private class Users extends JComponent{
    	JPanel legend;
    	JLabel legendColor;
        JLabel legendName;
        
        public Users(){
        	legend = new JPanel();
        	legend.setLayout(new BoxLayout(legend, BoxLayout.X_AXIS));
        	legend.setMaximumSize(new Dimension(100, 20));
        	legend.setMinimumSize(new Dimension(75, 20));
        	legend.setPreferredSize(new Dimension(75, 20));
        	legendColor = new JLabel();
        	legendName = new JLabel();
        	setLegendColor();
        	setLegendName();
        	legend.add(legendName);
        	legend.add(legendColor);
        	add(legend);
        	MouseHandler mouseHandler = new MouseHandler();
        	legend.addMouseListener(mouseHandler);
        	legend.addMouseMotionListener(mouseHandler);
        }        
       
		public void setLegendColor(){
        	legendColor.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder()));
        	legendColor.setOpaque(true);
        	legendColor.setMinimumSize(new Dimension(10,10));
            legendColor.setMaximumSize(new Dimension(25,20));
            legendColor.setPreferredSize(new Dimension(10,20));
        }
        
        public void setLegendName(){
        	legendName.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder()));
        	legendName.setMinimumSize(new Dimension(10,10));
            legendName.setMaximumSize(new Dimension(50,20));
            legendName.setPreferredSize(new Dimension(50,20));
        }
        
        public void setLegendFont(Font font){
        	legendName.setFont(font);
        }
        
        public void setLegendColor(Color color){
        	legendColor.setBackground(color);
        }
        
        public void setLegendName(String name){
        	legendName.setText(name);
        }
        
        public void addLegend(){
        	super.add(legend);
        }
        
        public String getLegendName(){
        	return legendName.getText();
        }
        
        public Color getLegendColor(){
        	return legendColor.getBackground();
        }
        
        class MouseHandler extends MouseInputAdapter {
        	int idx = 0;
            public void mousePressed(MouseEvent me) {
            	if ((me.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {   
            		if(me.getClickCount() == 2){
            			//set color of attribute cell of all other users excepted the selected one to gray
//            			Color userColor = getUserColorFromAttributeMap( getLegendName() );
//            			setUserColorInAttributeMap( getLegendName(), userColor);
//            			chart.updateAll();
//            			System.out.println("Mouse pressed; # of clicks: "+getLegendName() +" "+ me.getClickCount());
            		}
            	}
            }
            
            public void mouseMoved(MouseEvent me){
            	legend.setToolTipText("<html><blockquote><left><font size=\"4\">" + getLegendName() +"</left></blockquote></html>");
            	Color userColor = getUserColorFromAttributeMap( getLegendName() );
    			setUserColorInAttributeMap( getLegendName(), userColor);
    			chart.updateAll();    			
            }
            
            public void mouseExited(MouseEvent me) {
                mousedOver = -1;
                setUserColorInAttributeMapBack();
                chart.updateAll();
            }
            
        }
        
        
    }

}
