/*
This class is for the menu options once you open a VC file. 
File Edit View Window etc   
 */
import java.awt.AWTException;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

class OptionsMenu extends JMenuBar implements ActionListener{
	private static final long serialVersionUID = 1L;

	   public static final int SMALL = 30,
            MEDIUM = 40,
            LARGE = 50,
            EXTRALARGE = 60;

    CheckBoxMenuEntry scoreMenuItem; 
    CheckBoxMenuEntry measureMenuItem; 
    CheckBoxMenuEntry absMenuItem; 
    CheckBoxMenuEntry graphMenuItem;
    CheckBoxMenuEntry domMenuItem;
    CheckBoxMenuEntry sepMenuItem;
    CheckBoxMenuEntry sideMenuItem;
    CheckBoxMenuEntry defMenuItem;
    CheckBoxMenuEntry smMenuItem;
    CheckBoxMenuEntry medMenuItem;
    CheckBoxMenuEntry lgMenuItem;
    CheckBoxMenuEntry xlgMenuItem;
    MenuEntry menuItem;  
    MenuEntry menuUndo;
    MenuEntry menuRedo;
    ValueChart chart;
    Font font; 
    
    OptionsMenu(ValueChart ch){
    	chart = ch;    	
    }
    
    void createMenu(){
    	font = new Font("Verdana", Font.PLAIN, 13);	

    	MenuTitle menu;
        menu = new MenuTitle("File");   
        menuItem = new MenuEntry("Open");
        menu.add(menuItem);
        menuItem = new MenuEntry("Reload");
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new MenuEntry("Save"); 
        menu.add(menuItem);
        menuItem = new MenuEntry("Snapshot"); 
        menu.add(menuItem);
        //menuItem = new MenuEntry("Print"); 
        //menu.add(menuItem);
        menu.addSeparator();
        menuItem = new MenuEntry("Close");
        menu.add(menuItem);
        add(menu);
        
        menu = new MenuTitle("Edit");   
        menuUndo = new MenuEntry("Undo");
        menuUndo.setEnabled(false);
		menu.add(menuUndo);		
        menuRedo = new MenuEntry("Redo");
        menuRedo.setEnabled(false);
		menu.add(menuRedo);
		menu.addSeparator();
        menuItem = new MenuEntry("Objectives");  
        menu.add(menuItem);
        menuItem = new MenuEntry("Alternatives"); 
        menu.add(menuItem);
        menuItem = new MenuEntry("Value Function");  
        menu.add(menuItem);
        menuItem = new MenuEntry("Initial Weighting");  
        menu.add(menuItem);  
        add(menu);
        
        MenuTitle submenu;        
        
        menu = new MenuTitle("View");         
        submenu = new MenuTitle("Detail Display");
        
        graphMenuItem = new CheckBoxMenuEntry("Utility Graphs"); 
        submenu.add(graphMenuItem);
        
        absMenuItem = new CheckBoxMenuEntry("Absolute Ratios", true); 
        submenu.add(absMenuItem);
        
        domMenuItem = new CheckBoxMenuEntry("Domain Values");        
        submenu.add(domMenuItem);
        
        scoreMenuItem = new CheckBoxMenuEntry("Total Score");
        scoreMenuItem.setSelected(true);
        submenu.add(scoreMenuItem);      
        
        measureMenuItem = new CheckBoxMenuEntry("Score Measure");  
        submenu.add(measureMenuItem);    
        menu.add(submenu);
        
        submenu = new MenuTitle("Chart Display");
        defMenuItem = new CheckBoxMenuEntry("Vertical");
        submenu.add(defMenuItem);
        sideMenuItem = new CheckBoxMenuEntry("Horizontal");
        submenu.add(sideMenuItem);
        sepMenuItem = new CheckBoxMenuEntry("Separate");
        submenu.add(sepMenuItem);
        menu.add(submenu);
        
        submenu = new MenuTitle("View Size"); 
        smMenuItem = new CheckBoxMenuEntry("Small");
        submenu.add(smMenuItem);
        medMenuItem = new CheckBoxMenuEntry("Medium");
        submenu.add(medMenuItem);
        lgMenuItem = new CheckBoxMenuEntry("Large");
        submenu.add(lgMenuItem);
        xlgMenuItem = new CheckBoxMenuEntry("Extra Large");
        submenu.add(xlgMenuItem);
        menu.add(submenu);       
        
        menuItem = new MenuEntry("Set colors...");  
        menu.add(menuItem);
        
        add(menu);
        
        menu = new MenuTitle("Window");         
        menuItem = new MenuEntry("Open comparison view");
        menu.add(menuItem);
        menuItem = new MenuEntry("Open average group ValueChart");
        menu.add(menuItem);
        /*menuItem = new MenuEntry("Open average group ValueChart (David)");
        menu.add(menuItem);*/
        /*menuItem = new MenuEntry("HeatMap Legend");
        menu.add(menuItem);*/
        menuItem = new MenuEntry("Alternatives Legend");
        menu.add(menuItem);

        add(menu);
        setOpaque(true);

    }    
    
    public void setSelectedItems(){
    	if (chart.showAbsoluteRatios)
    		absMenuItem.setSelected(true);
    	if (chart.show_graph)
    		graphMenuItem.setSelected(true);
    	switch (chart.displayType){
    		case ValueChart.SEPARATE_DISPLAY:{
    			sepMenuItem.setSelected(true); break;} 
    		case ValueChart.SIDE_DISPLAY:{
    			sideMenuItem.setSelected(true); break;}  
    		case ValueChart.DEFAULT_DISPLAY:{
    			defMenuItem.setSelected(true); break;} 
    	}
    	switch (chart.colWidth){
			case SMALL:{
				smMenuItem.setSelected(true); break;}  
			case MEDIUM:{
				medMenuItem.setSelected(true); break;}   
			case LARGE:{
				lgMenuItem.setSelected(true); break;}  
                        case EXTRALARGE:{
                                xlgMenuItem.setSelected(true); break;} 
    	}
    }
    
	public void actionPerformed(ActionEvent ae) {
		//menubar commands		
		if ("Snapshot".equals(ae.getActionCommand())){
	        String image_name = (String)JOptionPane.showInputDialog(this, "Image name: ", "Save Snapshot", 
	        		JOptionPane.PLAIN_MESSAGE, null, null, ".jpg");			
			try {
				createSnapshot(image_name);
			} catch (AWTException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if ("Open".equals(ae.getActionCommand())){
			ValueChartsPlus.chart = chart;
			ValueChartsPlus.showStartView();
		}
		
        if ("Reload".equals(ae.getActionCommand())) {
            if (ae.getSource() instanceof MenuEntry) 
                ((MenuEntry)(ae.getSource())).setEnabled(false);
            chart.resetDisplay(chart.displayType, chart.colWidth, false, chart.show_graph,ValueChart.COLORFORUSER,null);
        }
		
		if ("Save".equals(ae.getActionCommand())){
			saveFile();
		}	
		
		if ("Close".equals(ae.getActionCommand())){
			System.exit(0);
		}		


		if ("Undo".equals(ae.getActionCommand())){
			chart.last_int.setRedo(chart.next_int);
			chart.last_int.undo();
		}
		
		else if ("Redo".equals(ae.getActionCommand())){
			chart.next_int.setRedo(chart.last_int);
			chart.next_int.undo();
		}	
		
		else if ("Objectives".equals(ae.getActionCommand())){
			chart.showEditView(0);		}
		else if ("Alternatives".equals(ae.getActionCommand())){
			chart.showEditView(1);
		}
		else if ("Value Function".equals(ae.getActionCommand())){
			chart.showEditView(2);
		}
		else if ("Initial Weighting".equals(ae.getActionCommand())){
			chart.showEditView(3);
		}		
		else if ("Vertical".equals(ae.getActionCommand())){
			chart.resetDisplay(1, chart.getColWidth(), true, chart.show_graph, ValueChart.COLORFORUSER,null);		
		}		
		else if ("Horizontal".equals(ae.getActionCommand())){
			chart.resetDisplay(2, chart.getColWidth(), true, chart.show_graph,ValueChart.COLORFORUSER,null);	
		}
		
		else if ("Separate".equals(ae.getActionCommand())){
			chart.resetDisplay(3, chart.getColWidth(), true, chart.show_graph,ValueChart.COLORFORUSER,null);	
		}
		
		else if ("Utility Graphs".equals(ae.getActionCommand())){
			if (graphMenuItem.isSelected()){
				chart.show_graph = true;
			}
			else
				chart.show_graph = false;
			chart.resetDisplay(chart.displayType, chart.colWidth, true, chart.show_graph,ValueChart.COLORFORUSER,null);
		}
		
		else if ("Absolute Ratios".equals(ae.getActionCommand())){
			if (absMenuItem.isSelected())
				chart.showAbsoluteRatios = true;
			else
				chart.showAbsoluteRatios = false;
			changeHeaders(chart.mainPane);
		}		
		else if ("Domain Values".equals(ae.getActionCommand())){
			if (domMenuItem.isSelected())
				for(int j=0; j<chart.chartData.getEntryList().size(); j++)
				    chart.chartData.getEntryList().get(j).setShowFlag(true);
			else
				for(int j=0; j<chart.chartData.getEntryList().size(); j++)
				    chart.chartData.getEntryList().get(j).setShowFlag(false);
			chart.updateAll();	
		}				
		else if ("Total Score".equals(ae.getActionCommand())){
			if (scoreMenuItem.isSelected())
				chart.getDisplayPanel().setScore(true);
			else
				chart.getDisplayPanel().setScore(false);
			chart.updateDisplay();
		}		
		else if ("Score Measure".equals(ae.getActionCommand())){
			if (measureMenuItem.isSelected())
				chart.getDisplayPanel().setRuler(true);
			else
				chart.getDisplayPanel().setRuler(false);
			chart.updateDisplay();
		}			
		else if ("Small".equals(ae.getActionCommand())){
			chart.resetDisplay(chart.displayType, 30, true, chart.show_graph,ValueChart.COLORFORUSER,null);
		}	
		else if ("Medium".equals(ae.getActionCommand())){
			chart.resetDisplay(chart.displayType, 40, true, chart.show_graph,ValueChart.COLORFORUSER,null);			
		}		
		else if ("Large".equals(ae.getActionCommand())){
			chart.resetDisplay(chart.displayType, 50, true, chart.show_graph,ValueChart.COLORFORUSER,null);			
		}	
		else if ("Extra Large".equals(ae.getActionCommand())){
			chart.resetDisplay(chart.displayType, 60, true, chart.show_graph,ValueChart.COLORFORUSER,null);			
		}       
		else if ("Open comparison view".equals(ae.getActionCommand())){
			chart.setConnectingFields();
			chart.compareDisplay(chart.displayType, chart.colWidth);
		}
		else if ("Open average group ValueChart".equals(ae.getActionCommand())){
		    chart.avgGVCDisplay(chart.displayType, ValueChart.DEFAULT_USER_COL_WIDTH,true, true);
        }
		/*else if ("Open average group ValueChart (David)".equals(ae.getActionCommand())){
            chart.avgGVCDisplay(chart.displayType, ValueChart.DEFAULT_USER_COL_WIDTH,true, true);
        }*/
		else if ("HeatMap Legend".equals(ae.getActionCommand())){
		    new TextureHeatMapLegend();
        }
		else if ("Alternatives Legend".equals(ae.getActionCommand())){
            DetailsViewPanel altPanel = new DetailsViewPanel(chart);
            JFrame altFrame = new JFrame();
            altFrame.setContentPane(altPanel);
            altFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            altFrame.pack();
            altFrame.setVisible(true);
        }

	}
	
	void saveFile(){
		File file;
		int ans = JOptionPane.YES_OPTION;
		chart.setConnectingFields();
	    String filename = (String)JOptionPane.showInputDialog(this, "ValueChart name: ", "Save ValueChart", 
	    		JOptionPane.PLAIN_MESSAGE, null, null, ".vc");
	    if ((filename != null) && (filename.length() > 0)) {
	    	file = new File(filename);
	    	if (file.exists()){
	    		ans = JOptionPane.showConfirmDialog(
	                    this, "Replace existing file??",
	                    "File overwrite",
	                    JOptionPane.YES_NO_OPTION);	        			     	    		        
	    	}
	    	if (ans == JOptionPane.YES_OPTION){
		        chart.con.createDataFile(filename);
		        }
	    	else 
	    		saveFile();
	    }
	}
	
	void changeHeaders(TablePane pane){
		Iterator it;    	
		for (it = pane.getRows(); it.hasNext();){
			BaseTableContainer btc = (BaseTableContainer)(it.next());
			btc.updateHeader();
			if (btc.table instanceof TablePane) 
				changeHeaders((TablePane)btc.table); 
		}
	}
	
	void createSnapshot(String name) throws AWTException, IOException {
		try{
			JFrame frame = (JFrame)chart.getFrame();
			BufferedImage image = new Robot().createScreenCapture( 
					   new Rectangle(frame.getX() + 4, frame.getY() + 149, 
					   		frame.getWidth() - 7, frame.getHeight() - 152));

			File file = new File(name);
			ImageIO.write(image, "jpg", file);
		}catch (Exception e){}
	}

	class MenuEntry extends JMenuItem{
		private static final long serialVersionUID = 1L;
		MenuEntry(String str){
			this.setFont(font);
			this.setActionCommand(str);
			this.setText(str);
			this.addActionListener(OptionsMenu.this);
		}		
	}
	
	class CheckBoxMenuEntry extends JCheckBoxMenuItem{	
		private static final long serialVersionUID = 1L;
		CheckBoxMenuEntry(String str, boolean b){
			this (str);
			setSelected(b);			
		}
		CheckBoxMenuEntry(String str){
			setFont(font);
			setActionCommand(str);
			setText(str);
			addActionListener(OptionsMenu.this);
		}
	}
	
	class MenuTitle extends JMenu{
		private static final long serialVersionUID = 1L;

		MenuTitle(String str){
			this.setFont(font);
			this.setText(str);
		}
	}
	
}