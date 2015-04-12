package dtp.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AgentIDResolverTest {
    @Test
    public void testGetEUnitIDFromName() {
        assertEquals(12, AgentIDResolver.getEUnitIDFromName("Agent#12"));
        assertEquals(12, AgentIDResolver.getEUnitIDFromName("12"));
    }
}
