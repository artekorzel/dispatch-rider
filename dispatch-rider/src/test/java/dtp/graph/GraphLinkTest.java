package dtp.graph;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GraphLinkTest {

    private static final float DELTA = Float.MIN_VALUE;
    GraphLink gl;

    @Before
    public void setUp() throws Exception {
        gl = new GraphLink();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testCost() {
        assertEquals(0, gl.getCost(), DELTA);
        gl.setCost(20);
        assertEquals(20, gl.getCost(), DELTA);
    }

}
