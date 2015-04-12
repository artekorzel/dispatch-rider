package dtp.util;

import java.text.NumberFormat;

/**
 * @author kony.pl
 */
public class MyNumberFormat {

    public static String formatDouble(double d, int intDigits, int fractDigits) {

        NumberFormat nf = NumberFormat.getInstance();

        nf.setMinimumIntegerDigits(intDigits);

        nf.setMaximumFractionDigits(fractDigits);
        nf.setMinimumFractionDigits(fractDigits);

        nf.setGroupingUsed(false);

        return nf.format(d).replaceAll(",", ".");
    }
}
