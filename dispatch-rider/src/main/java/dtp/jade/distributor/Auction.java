package dtp.jade.distributor;

import dtp.commission.Commission;
import dtp.jade.eunit.EUnitOffer;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Auction {

    private static Logger logger = Logger.getLogger(Auction.class);

    private Commission commission;

    // liczba ofert wyslanych do EUnitow
    private int sentOffersNo;

    // oferty zgloszone przez EUnity
    private List<EUnitOffer> offers = new LinkedList<>();

    public Auction() {

        this.sentOffersNo = 0;
        offers = new LinkedList<>();
    }

    public Commission getCommission() {

        return commission;
    }

    public void setCommission(Commission commission) {

        this.commission = commission;
    }

    public int getSentOffersNo() {

        return sentOffersNo;
    }

    public void addOffer(EUnitOffer offer) {
        for (EUnitOffer off : offers) {
            if (off.getAgent().equals(offer.getAgent())) {
                logger.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ SAME AGENT ADDED TWICE");
            }
        }
        if (!offers.contains(offer)) offers.add(offer);

    }

    public EUnitOffer[] getOffers() {

        Iterator<EUnitOffer> iter = offers.iterator();
        EUnitOffer[] out = new EUnitOffer[offers.size()];
        int count = 0;

        while (iter.hasNext())
            out[count++] = iter.next();

        return out;
    }

    public boolean gotAllOffers() {
        if (offers.size() == sentOffersNo) {
            Collections.sort(offers);
            return true;
        }
        return false;
    }
}
