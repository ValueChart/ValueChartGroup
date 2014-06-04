import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

public class Users extends JComponent{
    private static final long serialVersionUID = 1L;
    
    JPanel legend;
    JLabel legendColor;
    JLabel legendName;
    
    UserLegendPanel userLegendPnl;
    
    public Users(UserLegendPanel ulp){
        userLegendPnl = ulp;
        
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
//                      Color userColor = getUserColorFromAttributeMap( getLegendName() );
//                      setUserColorInAttributeMap( getLegendName(), userColor);
//                      chart.updateAll();
//                      System.out.println("Mouse pressed; # of clicks: "+getLegendName() +" "+ me.getClickCount());
                }
            }
        }
        
        public void mouseMoved(MouseEvent me){
            legend.setToolTipText("<html><blockquote><left><font size=\"4\">" + getLegendName() +"</left></blockquote></html>");
            Color userColor = GroupActions.getUserColorFromAttributeMap(userLegendPnl.chart, getLegendName() + ".vc" );
            if (userLegendPnl.groupActions != null) {
                userLegendPnl.groupActions.setUserColorInAttributeMap( getLegendName(), userColor);
                userLegendPnl.chart.updateAll();              
            } else if (userLegendPnl.boxPlot != null) {
                userLegendPnl.boxPlot.plotUserAttributeGraph(getLegendName(), userColor);
            }
        }
        
        public void mouseExited(MouseEvent me) {
            if (userLegendPnl.groupActions != null) {
                userLegendPnl.groupActions.setUserColorInAttributeMapBack();
                userLegendPnl.chart.updateAll();
            } else if (userLegendPnl.boxPlot != null) {
                userLegendPnl.boxPlot.clearUserAttributePlot();
            }
        }
        
    }
}