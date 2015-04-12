package gui.map;

import java.awt.*;

public class ColorCreator {

    private static Integer maxID = 0;

    public static void setMaxID(Integer id) {
        if (id > maxID)
            maxID = id;
    }

    public static Color createColor(Integer holonId) {
        Integer r;
        r = (int) (((float) (holonId + 1) / (maxID + 2)) * Integer.MAX_VALUE);
        return new Color(r);//,r,r);
    }

}
