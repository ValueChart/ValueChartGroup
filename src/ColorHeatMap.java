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
            add(new Color(254,196,79));
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
    }
}