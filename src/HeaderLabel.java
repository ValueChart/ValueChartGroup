import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;


public class HeaderLabel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int padding = 10;
    private JLabel textLabel = new JLabel();
    BufferedImage tileImage;
    GridBagConstraints constraints;
    GridBagConstraints sliderCons;
    private String mainText = "";
    private String smallText = "";
    private boolean showSmallText = true;
    private JProgressBar slider = new JProgressBar();
    private int sliderPos = -1;
    public static final int SLIDER_MIN = 0;
    public static final int SLIDER_MAX = 33;
    private JPanel sliderPanel = new JPanel();
    
    
    public HeaderLabel() {
        super();
        textLabel = new JLabel();
        setPanelLayout();
        setSlider();
    }
    
    public HeaderLabel(String text, String smallText) {
        super();
        setPanelLayout();
        setSlider();
        textLabel = new JLabel();
        mainText = text;
        this.smallText = smallText;
        
        textLabel.setLocation(padding, padding);
        textLabel.setHorizontalAlignment(JLabel.LEFT); //Label Alignment
        textLabel.setVerticalAlignment(JLabel.CENTER);
    }
    
    private void setPanelLayout() {
        this.setLayout(new GridBagLayout());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.insets = new Insets(padding/2, padding, padding/2, padding);
        
        sliderCons = new GridBagConstraints();
        sliderCons.fill = GridBagConstraints.HORIZONTAL;
        sliderCons.anchor = GridBagConstraints.FIRST_LINE_START;
        sliderCons.gridx = 0;
        sliderCons.gridy = 1;
        sliderCons.weightx = 1;
        sliderCons.insets = new Insets(0, padding, padding/2, padding);
        
    }
    
    private void setSlider() {
        Font f = new Font("Arial", Font.PLAIN, 10);

        slider.setMinimum(SLIDER_MIN);
        slider.setMaximum(SLIDER_MAX);
        slider.setEnabled(false);
        slider.setString("Disagreement");
        slider.setStringPainted(true);
        slider.setFont(f);
        
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
        JLabel label = new JLabel("Low");
        label.setFont(f);
        sliderPanel.add(label);
        sliderPanel.add(slider);
        label = new JLabel("High");
        label.setFont(f);
        sliderPanel.add(label);
        
    }
    
    public void setMainText(String text) {
        mainText = text;
    }
    
    public String getMainText() {
        if (textLabel == null) return null;
        return textLabel.getText();
    }
    
    public String getSmallText() {
        return smallText;
    }

    public void setSmallText(String smallText) {
        this.smallText = smallText;
    }
    
    private void addTextLabel() {
        if (mainText != null && !mainText.trim().isEmpty()) {
            this.add(textLabel, constraints);
        }
        
        if (showSmallText) {
            textLabel.setText("<html><left>" + mainText + "<br><small>" + smallText + "</small></left><html>");
        } else {
            textLabel.setText("<html><left>" + mainText + "</left><html>");            
        }
    }

    public boolean isShowSmallText() {
        return showSmallText;
    }

    public void setShowSmallText(boolean showText) {
        this.showSmallText = showText;
    }

    public void setFont(Font f) {
        super.setFont(f);
        if (textLabel != null)
            textLabel.setFont(f);   
    }
    
    public Font getFont() {
        if (textLabel == null) return null;
        return textLabel.getFont();
    }
    
    public void setMaximumSize(Dimension dim) {
        super.setMaximumSize(dim);
        if (textLabel != null)
            textLabel.setMaximumSize(new Dimension(dim.width-(2*padding), dim.height-(2*padding)));
    }
    
    public void setMinimumSize(Dimension dim) {
        super.setMinimumSize(dim);
    }
    
    public void setPreferredSize(Dimension dim) {
        super.setPreferredSize(dim);
    }

    public int getSliderPosition() {
        return slider.getValue();
    }

    public void setSliderPosition(int value) {
        if (value > SLIDER_MAX) value = SLIDER_MAX;
        sliderPos = value;
    }
    
    private void addSlider() {
        if (sliderPos >= SLIDER_MIN && sliderPos <= SLIDER_MAX) {
            add(sliderPanel, sliderCons);
            slider.setValue(sliderPos);
        }
    }
    
    protected void paintComponent(Graphics g) {
        int height = getHeight();
        if (height < 30) {
            showSmallText = false;
        }
        
        removeAll();
        addTextLabel();
        addSlider();
        validate();
        super.paintComponent(g);
    }
}
