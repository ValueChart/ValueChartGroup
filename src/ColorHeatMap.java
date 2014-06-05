import java.awt.Color;
import java.util.ArrayList;



class ColorHeatMap extends ArrayList<Color>{  
    private static final long serialVersionUID = 1L;

    ColorHeatMap(int num){
        if (num >= 9)
            add(new Color(255,255,229));
        if (num >= 8)
            add(new Color(255,247,188));
        if (num >= 7)
            add(new Color(254,227,145));
        if (num >= 6)
            add(new Color(254,217,118));
        if (num >= 5)
            add(new Color(254,153,41));
        if (num >= 4)
            add(new Color(236,112,20));
        if (num >= 3)
            add(new Color(204,76,2));
        if (num >= 2)
            add(new Color(153,52,4));
        if (num >= 1)
            add(new Color(102,37,6));
        
        // reds
        /*
        if (num >= 9)
            add(new Color(255,245,240));
        if (num >= 8)
            add(new Color(254,224,210));
        if (num >= 7)
            add(new Color(252,187,161));
        if (num >= 6)
            add(new Color(252,146,114));
        if (num >= 5)
            add(new Color(251,106,74));
        if (num >= 4)
            add(new Color(239,59,44));
        if (num >= 3)
            add(new Color(203,24,29));
        if (num >= 2)
            add(new Color(165,15,21));
        if (num >= 1)
            add(new Color(103,0,13));
         */
    }
}