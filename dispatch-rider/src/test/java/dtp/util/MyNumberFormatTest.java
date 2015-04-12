package dtp.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class MyNumberFormatTest {
    @Test
    public void testFormatDouble() {
        assertEquals(MyNumberFormat.formatDouble(34.2323, 2, 2), "34.23");
        assertEquals(MyNumberFormat.formatDouble(34.2323, 2, 0), "34");
        assertEquals(MyNumberFormat.formatDouble(34, 2, 2), "34.00");
    }
}
