
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import java.util.*;

import javax.swing.event.MouseInputAdapter;

//This class is the overall ranking panel. The most important function in this
//class is the paint function. All the rankings are done through paint.
//If you are rotating the interface 90 degrees, you need to change the paint function
//of x to y.
//See below comments
public class DisplayPanel extends JComponent {

    private static final long serialVersionUID = 1L;
//    boolean score = false;
    boolean score = true;
    boolean ruler = false;  
    boolean showAvg = false;
//    boolean topChoices = false;
    private TablePane rootPane;
    private int colWidth = ValueChart.DEFAULT_COL_WIDTH;
    private Vector<ChartEntry> entryList;
    private int userWidth = ValueChart.DEFAULT_USER_COL_WIDTH;
    private int padding = ValueChart.DEFAULT_PADDING_WIDTH;
    private ValueChart chart;//created ValueChart object to access list of users
    private int noOfUsers;    
    public HashMap<String,Double> maxTotalScores; //<User,MaxScore>
    public HashMap<String,Double> averageTotalScores; //<Alternative,AverageScore>
    public HashMap<String,String> topAltforUsers; //<User,TopChoice>
    
    JPopupMenu domainMeta = new JPopupMenu();
    

    public DisplayPanel(int col, ValueChart chart) {    	
    	colWidth = col;
    	this.chart = chart;
    	noOfUsers = chart.listOfEntryMaps.size();   
    	maxTotalScores = new HashMap<String,Double>();
    	averageTotalScores = new HashMap<String,Double>();
    	topAltforUsers = new HashMap<String,String>();
        MouseHandler mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);        
    }

    //this function calculates the max alternative total scores for each user and stores it in a map. 
    //this map is later used in paint function to highlight the highest valued alternative for each user 
    private void fillMaxTotalScores() {
    	int numEntries = entryList.size();
    	Vector cellList = new Vector(16);
    	rootPane.getAttributeCells(cellList);
    	double[] weights;	//array of entry weights (values)
        double[] accumulatedRatios = new double[numEntries * noOfUsers];
        int j = 0;
    	for (int i = 0; i < numEntries; i++) {//for each alternative    	
    		int users=0;
    		double avgAlternativeScore = 0;
    		for(IndividualEntryMap e : chart.listOfEntryMaps){//for each user
    			for (Iterator it = cellList.iterator(); it.hasNext();) {//for each attribute
    				AttributeCell cell = (AttributeCell) it.next();
         			weights = e.getEntryWeights(cell.getName());
         			double or = cell.getHeightFromAttributeMap(e.username,cell.getName());
                    accumulatedRatios[j] += or * weights[i];
    			}    			
    			String srcKey = e.username;
				double srcValue = accumulatedRatios[j];
				
//				maxTotalScores2.put(srcKey, srcValue);
				if(maxTotalScores.containsKey(srcKey)){
					double destValue = maxTotalScores.get(srcKey);
					if(destValue <= srcValue){
						maxTotalScores.remove(srcKey);
						topAltforUsers.remove(srcKey);
						
						maxTotalScores.put(srcKey, srcValue);   
						topAltforUsers.put(srcKey, e.entryList.get(i).name);
					}
				}else{
					maxTotalScores.put(srcKey, srcValue);
					topAltforUsers.put(srcKey, e.entryList.get(i).name);
				}
                avgAlternativeScore += accumulatedRatios[j];
                j++;
                users++;
    		}
    		averageTotalScores.put(entryList.get(i).name, avgAlternativeScore/users);    		
    	}
		
    	
    	for(IndividualAttributeMaps a : chart.listOfAttributeMaps){
    		for(Map.Entry<String, String> m : topAltforUsers.entrySet()){
    			if(a.userName.equals(m.getKey())){
    				a.setTopAlternative(m.getValue());
    			}
    		}
//    		System.out.println(a.userName+" "+a.topAlternative);
    	}
//		System.out.println(maxTotalScores2);
//    	System.out.println(averageTotalScores);
//    	System.out.println(topAltforUsers);
	}
    
    
	public int getColWidth() {
        return colWidth;
    }

    /*	public void setColWidth (int w){ 
     colWidth = w;
     }
     */
    public void setRootPane(TablePane pane) {
        rootPane = pane;
    }

    public void setEntries(Vector list) {
        entryList = list;
        Dimension dim = getPreferredSize();

        dim.width = colWidth * entryList.size();
        setPreferredSize(dim);
        setMaximumSize(new Dimension(colWidth * entryList.size() + colWidth * 2, 10000));
    }

    public Dimension getPreferredSize() {
        return new Dimension(colWidth * entryList.size() + colWidth * 2, getHeight());
    }

    public void setScore(boolean s) {
        score = s;
    }

    public void setRuler(boolean r) {
        ruler = r;
    }
    
    public void setShowAvg(boolean avg){
    	showAvg = avg;
    }
    
    
    
  public void paint(Graphics g) {
	  	fillMaxTotalScores();
        int numEntries = entryList.size();
        int totalWidth = getWidth();
        int totalHeight = getHeight();
        g.setColor(Color.white);
        int align_right = 0;//totalWidth - numEntries*colWidth;//
//        g.fillRect(align_right + colWidth, 0, numEntries * colWidth, totalHeight);
        g.fillRect(align_right + colWidth, 0, numEntries * colWidth, totalHeight);
        if (rootPane == null) {
            return;
        }
        Vector cellList = new Vector(16);
        rootPane.getAttributeCells(cellList);
        double[] weights;	//array of entry weights (values)
        double[] accumulatedRatios = new double[numEntries * noOfUsers];
        int[] ypos = new int[numEntries * noOfUsers];	//position of x, starts all at 0
       
      //for each objective, get the (array)domain value of each alternative
        int h = 0, x = align_right + colWidth;
        int avgH = 0;
        int j = 0;
        //for each alternative
        for (int i = 0; i < numEntries; i++) { 
        	x+=padding/3;
            g.setColor(Color.white);
            ((Graphics2D) g).setStroke(new BasicStroke());
            g.drawLine(x-3, 0, x-3, totalHeight - 1);            
    		if(!chart.listOfEntryMaps.isEmpty()){
    			//for each user    			
    			for(IndividualEntryMap e : chart.listOfEntryMaps){    				
    				//for each attribute
    				for (Iterator it = cellList.iterator(); it.hasNext();) { 
        				AttributeCell cell = (AttributeCell) it.next();
             			weights = e.getEntryWeights(cell.getName());
             			double or = cell.getHeightFromAttributeMap(e.username,cell.getName());
	                    accumulatedRatios[j] += or * weights[i];
	                    h = (int) Math.round(accumulatedRatios[j] * totalHeight) - ypos[j];
	                    ChartEntry entry = (ChartEntry) entryList.get(i);
	                    if (e.entryList.get(i).isMasked()) {
	                        g.setColor(Color.lightGray);
	                    } else {
//	                        g.setColor(cell.getUserColorFromAttributeMap(e.username));
//	                    	g.setColor(cell.chooseColor(chart.colorChoice,e.username,cell.getName(),chart.userToPickAttributeColor));
                    		g.setColor(cell.chooseColor(e.username,cell.getName(),entry.name,chart.userToPickAttributeColor));
	                    }	
	                    g.fillRect(x, totalHeight - h - ypos[j], userWidth, h);
	                    g.setColor(Color.lightGray);
	                    g.drawLine(x, totalHeight - h - ypos[j], x + userWidth - 1, totalHeight - h - ypos[j]);
	                    g.setColor(Color.lightGray);
        	            g.drawLine(x-1, 0, x-1, getHeight() - 1);
        	            
        	            //Draw average line
        	            if(showAvg){
        	            	g.setColor(Color.BLACK);
            				avgH = (int) Math.round(averageTotalScores.get(e.entryList.get(i).name)*totalHeight);
            				g.drawLine(x-1, totalHeight - avgH, x + userWidth -1, totalHeight - avgH);
        	            }
	                    ypos[j] += h;
	                    if (x > totalWidth) {
	                        break;
	                    }
    				}
    				//scores
    	            if (score) {
    	            	int xpos = x + userWidth/4;
    	            	double maxTotalScore = accumulatedRatios[j];
//        				if(chart.topChoices){
//    						for(IndividualAttributeMaps iam : chart.listOfAttributeMaps){
//    							if(e.entryList.get(i).name.equals(iam.topAlternative) && e.username.equals(iam.userName)){
//    								g.setFont(new Font("DEFAULT",Font.BOLD,12));    
//                					g.setColor(Color.RED);
//    							}
//    							else{
//    								g.setFont(new Font("DEFAULT",Font.PLAIN,12));    
//                					g.setColor(Color.lightGray);
//    							}
//    						}
//    					}
//        				else{
        	            	for(Map.Entry<String, Double> entry : maxTotalScores.entrySet()){
                    			if(entry.getKey().equals(e.username)){
                    				if(chart.getMouseOver(e.username)){
                    					g.setFont(new Font("DEFAULT",Font.PLAIN,12));    
                    					g.setColor(Color.lightGray);
                    				}
                					else{
                						if(maxTotalScore == entry.getValue()){//if score is the max score, bold it
                        					g.setFont(new Font("DEFAULT",Font.BOLD,12));    
                        					g.setColor(Color.RED);
                        				}
                        				else{
                        					g.setColor(Color.darkGray);
                        					g.setFont(new Font("DEFAULT",Font.PLAIN,12));
                        				}
                					}
                        		}
                        	}
//        				}
    	            	g.drawString(String.valueOf((Math.round(accumulatedRatios[j] * 100))), xpos, totalHeight - ypos[j] - 5);
	                    xpos = xpos + userWidth/4;
    	            }
    				j++;
    	            x += userWidth;
                }
    			h=0;
			}
    		 //To separate a bundle of users within an alternative - bold black lines
    		g.setColor(Color.lightGray);
            g.drawLine(x-1, 0, x-1, getHeight() - 1);
            
            x+=padding/3;            
            g.setColor(Color.white);
            ((Graphics2D) g).setStroke(new BasicStroke());
            g.drawLine(x-3, 0, x-3, totalHeight - 1);
           
            x+=padding/3;
            g.setColor(Color.BLACK);
            ((Graphics2D) g).setStroke(new BasicStroke());
            g.drawLine(x-3, 0, x-3, totalHeight - 1);            
        }            
        	//ruler
            if (ruler) {
                g.setColor(Color.gray);
                g.drawRect(userWidth*noOfUsers-22, 0, userWidth*2, totalHeight - 1);

                for (int i = 1; i < 10; i++) {
                    g.drawLine(userWidth*noOfUsers+16, i * totalHeight / 10, userWidth*noOfUsers+18, i * totalHeight / 10);
                    g.setFont(new Font("Verdana", Font.PLAIN, 8));
                    g.setColor(Color.BLACK);
                    g.drawString(String.valueOf((10 - i) * 10), userWidth*noOfUsers+4, i * totalHeight / 10 + 3);
                }
            }
            /*boolean gridline = false;
            if (gridline) {
                g.setColor(Color.red);
                for (int i = 1; i < 10; i++) {
                    g.drawLine(i * totalWidth / 10, userWidth - 2, i * totalWidth / 10, totalHeight);
                }
            }*/
        
    }

    private class MouseHandler extends MouseInputAdapter {

        private final int OFF = 0;
        private final int NEARBY = 1;
        private final int MOVING = 2;
        private int dragMode = OFF;
        private int dragY;//-
        private int thresholdPos = 0;
        Cursor movingCursor = new Cursor(Cursor.MOVE_CURSOR);
        Cursor defaultCursor = null;
        boolean nearThreshold = false;

        @Override
        public void mouseExited(MouseEvent e) {
            //do something, like get rid of the popup
            int blah = 1;
        }

        @Override
        public void mousePressed(MouseEvent e) {

            if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
                //If the context menu is up and now the user clicks away from the popup menu, then hide the popup
                /*
                 JPopupMenu popup = getEntryPopup(); //This needs to be a static number in order for the loop to work
                 if (popup.isVisible()) {
                 popup.setVisible(false);

                 while (popup.getComponents().length > 1) {
                 popup.remove(popup.getComponents().length - 1);
                 //remove all the context menu items, except for the default domain value
                 }
                 }*/
            } else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
                
                domainMeta.removeAll();
                JMenuItem item = new JMenuItem("Test");
                item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent b){
                            System.out.println("Test Clicked");
                        }
                    });
                //domainMeta.add(item);
                //domainMeta.show(e.getComponent(), e.getX() + 5, e.getY() + 5);
                /*    

                 JMenuItem detailMenuItem = new JMenuItem("Report Details");
                 detailMenuItem.addActionListener(new ActionListener() {
                 @Override
                 public void actionPerformed(ActionEvent b){
                 System.out.print("Report Clicked");
                 }
                 });
                 attributeMeta.add(detailMenuItem);
                 attributeMeta.setSelected(detailMenuItem);
                    
                 detailMenuItem = new JMenuItem("Images");
                 detailMenuItem.addActionListener(new ActionListener() {
                 @Override
                 public void actionPerformed(ActionEvent b){
                 System.out.print("Images Clicked");
                 }
                 });
                 attributeMeta.add(detailMenuItem);
                 Point loc = AttributeCell.this.getLocationOnScreen();
                 attributeMeta.setLocation(loc.x + (index + 1) * colWidth - 5, loc.y + e.getY());
                 attributeMeta.setVisible(true);
                 }
                 * */
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
                //do something                        
            } else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
                /*                    if (attributeMeta.isVisible()) {
                 attributeMeta.removeAll();
                 }
                 */
            }
        }
    }
}