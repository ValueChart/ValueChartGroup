import java.awt.Font;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;


public class UserLegendPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    ValueChart chart;
    Font font = new Font("Arial", Font.PLAIN, 11);;
    GroupActions groupActions = null;
    BoxPlotGraph boxPlot = null;
    
    public UserLegendPanel(ValueChart ch, GroupActions groupAct) {
        chart = ch;
        groupActions = groupAct;
        
        if (chart.con.type == ConstructionView.FROM_VC)
            populateUsers();
        else
            populateUsersXML();
    }
    
    public UserLegendPanel(ValueChart ch, BoxPlotGraph box) {
        chart = ch;        
        boxPlot = box;
        
        if (chart.con.type == ConstructionView.FROM_VC)
            populateUsers();
        else
            populateUsersXML();
    }
    
    private void populateUsers() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        Vector<String> users = new Vector<String>();
        for(int i = 0; i<chart.users.size(); i++){
            if(!chart.users.get(i).equals("SuperUser.vc"))
                users.addElement(chart.users.get(i));           
        }   
        
        for(int i = 0 ; i < users.size() ; i++){
            Users userLegend = new Users(this);
            userLegend.setLegendColor(GroupActions.getUserColorFromAttributeMap(chart, users.get(i)));
            userLegend.setLegendFont(font);
            userLegend.setLegendName(Users.removeExtension(users.get(i)));                 
            add(userLegend.legend);
        }
        
        if (groupActions != null)
            add(Box.createVerticalGlue());
    }
    
    private void populateUsersXML() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        Vector<String> users = new Vector<String>();
        for(int i = 0; i<chart.users.size(); i++){
            if(!chart.users.get(i).equals("SuperUser.xml"))
                users.addElement(chart.users.get(i));           
        }   
        
        for(int i = 0 ; i < users.size() ; i++){
            Users userLegend = new Users(this);
            userLegend.setLegendColor(GroupActions.getUserColorFromAttributeMap(chart, users.get(i)));
            userLegend.setLegendFont(font);
            userLegend.setLegendName(Users.removeExtension(users.get(i)));                 
            add(userLegend.legend);
        }
        
        if (groupActions != null)
            add(Box.createVerticalGlue());
    }
}
