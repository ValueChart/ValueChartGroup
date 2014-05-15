import java.awt.Color;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class DisplayObjectives extends Box{
	JPanel panel = new JPanel();
	private static final long serialVersionUID = 1L;
	int preferredHeaderWidth = 0;
	
	public DisplayObjectives(Map<String,Double> maxWeightMap){  
		super(BoxLayout.X_AXIS);
    	for(Map.Entry<String, Double> e : maxWeightMap.entrySet()){
    		JLabel header = new JLabel(".00");
    		header.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    		header.setText(e.getKey());
        	panel.add(header);
        	Dimension dimhead = header.getPreferredSize();
        	preferredHeaderWidth = dimhead.width;
            header.setMaximumSize(dimhead);
            header.setMinimumSize(dimhead);
            header.setPreferredSize(dimhead);                        
            header.setOpaque(false);
            header.setHorizontalAlignment(JLabel.LEFT); //Label Alignment
            header.setVerticalAlignment(JLabel.CENTER);
    	}
    }

}
