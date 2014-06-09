import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HeatMapLegend extends JFrame {
    private static final long serialVersionUID = 1L;

    public HeatMapLegend() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        
        ColorHeatMap map = new ColorHeatMap();
        int numColors = map.size()-1;
        double scale = numColors / 0.33;
        int i = numColors; 
        for (Color c : map) {
            JPanel color = new JPanel();
            color.setLayout(new BoxLayout(color, BoxLayout.X_AXIS));
            
            String text = "   ";
            int start = (int) Math.floor(i / scale * 100);
            int end = (int) Math.floor( (i + 1) / scale * 100); 
            if (i == numColors) {
                text += start + "+";
            } else {
                text += start + " - " + end;
            }
            text += "  %";
            JLabel colorlbl = new JLabel(text);
            colorlbl.setBackground(c);
            colorlbl.setForeground(AttributeCell.useBlackForeground(c) ? Color.black : Color.white);
            colorlbl.setOpaque(true);
            colorlbl.setPreferredSize(new Dimension(200,40));
            colorlbl.setMinimumSize(colorlbl.getPreferredSize());
            colorlbl.setMaximumSize(colorlbl.getPreferredSize());
            color.add(colorlbl);
                        
            color.setAlignmentX(LEFT_ALIGNMENT);
            pnl.add(color);
            i--;
        }
        
        getContentPane().add(pnl);
        showFrame();
    }
    
    public void showFrame() {
        setTitle("Disagreement Guide");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
