package dtp.graph;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GraphPointTest {
    private static final float DELTA = Float.MIN_VALUE;
    GraphPoint g, gp1, gp2;

    @Before
    public void setUp() {
        g = new GraphPoint(1.5, 4.0);
        gp1 = new GraphPoint(2.5, 4.0);
        gp2 = new GraphPoint(1.5, 4.0);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetX() {
        assertEquals(1.5, g.getX(), DELTA);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetY() {
        assertEquals(4.0, g.getY(), DELTA);
    }

    @Test
    public void testSetName() {
        g.setName("graph");
        assertEquals("graph", g.getName());
    }

    @Test
    public void testHasSameCoordinates() {
        assertTrue(g.hasSameCoordinates(gp2));
        assertFalse(g.hasSameCoordinates(gp1));
    }

}
