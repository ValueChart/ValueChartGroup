import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class TextureHeatMapLegend extends JFrame {
    private static final long serialVersionUID = 1L;

    private static String[] LABELS = {"High Disagreement","Moderate Disagreement","Low Disagreement"};
    
    public TextureHeatMapLegend() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        
        TextureHeatMap map = new TextureHeatMap();
        int numColors = map.size()-1;
        int i = numColors; 
        for (String c : map) {
            JPanel color = new JPanel();
            color.setLayout(new BoxLayout(color, BoxLayout.X_AXIS));
            
            String text = "";
            /*int start = (int) Math.floor(i / scale * 100);
            int end = (int) Math.floor( (i + 1) / scale * 100); 
            if (i == numColors) {
                text += start + "+";
            } else {
                text += start + " - " + end;
            }
            text += "  %";*/
            if (i == numColors) {
                text += LABELS[0];
            } else if (i == numColors/2) {
                text += LABELS[1];
            } else if (i == 0) {
                text += LABELS[2];
            }
            HeaderLabel colorlbl = new HeaderLabel(text, "");
            colorlbl.setTextureFile(ValueChart.IMG_DIR + c);
            colorlbl.setOpaque(true);
            colorlbl.setPreferredSize(new Dimension(200,40));
            colorlbl.setMinimumSize(colorlbl.getPreferredSize());
            colorlbl.setMaximumSize(colorlbl.getPreferredSize());
            color.add(colorlbl);
                        
            color.setAlignmentX(LEFT_ALIGNMENT);
            pnl.add(color);
            pnl.add(new JSeparator(SwingConstants.HORIZONTAL));
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
