import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class HeaderLabel extends JPanel {
    private static final long serialVersionUID = 1L;
    String texture = "";
    private static final int padding = 10;
    private JLabel textLabel = new JLabel();
    BufferedImage tileImage;
    GridBagConstraints constraints;
    private String mainText = "";
    private String smallText = "";
    private boolean showSmallText = true;
    
    public HeaderLabel() {
        super();
        textLabel = new JLabel();
        setPanelLayout();
    }
    
    public HeaderLabel(String text, String smallText) {
        super();
        setPanelLayout();
        textLabel = new JLabel();
        mainText = text;
        this.smallText = smallText;
        setTextLabel();
        
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
        constraints.insets = new Insets(padding, padding, padding, padding);
    }
    
    public void setMainText(String text) {
        mainText = text;
        setTextLabel();
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
        setTextLabel();
    }
    
    private void setTextLabel() {
        removeAll();
        if (mainText != null && !mainText.trim().isEmpty()) {
            this.add(textLabel, constraints);
        } else {
            validate();
            return;
        }
        
        if (showSmallText) {
            textLabel.setText("<html><left>" + mainText + "<br><small>" + smallText + "</small></left><html>");
        } else {
            textLabel.setText("<html><left>" + mainText + "</left><html>");            
        }
        validate();
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
    
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        if (height < 30) {
            showSmallText = false;
        }
        setTextLabel();
        
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
    }
}
