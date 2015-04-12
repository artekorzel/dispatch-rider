package dtp.jade.distributor;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AuctionTest {
    Auction a;

    @Before
    public void setUp() throws Exception {
        a = new Auction();
    }

    @Test
    public void testGetSentOffersNo() {
        assertEquals(0, a.getSentOffersNo());
    }
}
