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
    String texture = "";
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
    public static int DISAGREE_MAX = 33;
    private JPanel sliderPanel = new JPanel();
    public static boolean SHOW_TEXTURE = false;
    
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
        slider.setMaximum(DISAGREE_MAX);
        slider.setEnabled(false);
        slider.setStringPainted(false);
        slider.setMaximumSize(new Dimension(slider.getMaximumSize().width, 10));
        slider.setFont(f);
        
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
        sliderPanel.add(slider);
        
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

    public String getTexture() {
        return texture;
    }

    public void setTextureFile(String texture) {
        this.texture = texture;
        if (texture == null) {
            this.setOpaque(false);
            textLabel.setOpaque(false);
        } else {
            try {
                tileImage = ImageIO.read(new File(texture));
                setOpaque(true);
                textLabel.setBackground(Color.white);
                textLabel.setOpaque(true);
            } catch (IOException ex) {
                textLabel.setOpaque(false);
                setOpaque(false);
            }
        }
    }

    public int getSliderPosition() {
        return slider.getValue();
    }

    public void setSliderPosition(int value) {
        sliderPos = value;
    }
    
    private void addSlider() {
        slider.setMaximum(DISAGREE_MAX);
        if (sliderPos >= SLIDER_MIN) {
            add(sliderPanel, sliderCons);
            if (sliderPos <= DISAGREE_MAX)
                slider.setValue(sliderPos);
            else
                slider.setValue(DISAGREE_MAX);
        }
    }
    
    protected void paintComponent(Graphics g) {
        int height = getHeight();
        if (height < 30) {
            showSmallText = false;
        }
        
        removeAll();
        addTextLabel();
        
        //if using textures
        if (SHOW_TEXTURE) {
            int width = getWidth();
            if (texture == null || texture.isEmpty() ||tileImage == null) {
                super.paintComponent(g);
                return;
            }
            
            int imageW = tileImage.getWidth(this);
            int imageH = tileImage.getHeight(this);
     
            // Tile the image to fill our area.
            for (int x = 0; x < width; x += imageW) {
                for (int y = 0; y < height; y += imageH) {
                    g.drawImage(tileImage, x, y, this);
                }
            }
        } else {
            addSlider();
            validate();
            super.paintComponent(g);
        }
    }
}
