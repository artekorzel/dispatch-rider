package dtp.jade.distributor;

import algorithm.simmulatedTrading.SimmulatdTradingParameters;
import jade.core.AID;

/**
 * Simmulated Trading Auction
 *
 * @author kony.pl
 */
public class AuctionST {

    // AID agenta ktory wyslal zlecenie, ktorego dotyczy ta aukcja ST
    private AID ownerAID;

    // identyfikatory AID EUnitow bioracych udzial w ST
    private AID[] aids;

    // AID obslugiwanego EUnita podczas ST
    private int currentAIDNumber;

    private SimmulatdTradingParameters params;


    AuctionST(AID[] aids) {

        this.aids = aids;
        currentAIDNumber = 0;
    }

    public AID getOwnerAID() {

        return ownerAID;
    }

    public void setOwnerAID(AID ownerAID) {

        this.ownerAID = ownerAID;
    }

    public AID getCurrentAID() {

        return aids[currentAIDNumber];
    }

    public void increaseCurrentAIDNumber() {

        currentAIDNumber++;
    }

    public int getAIDSNumber() {

        return aids.length;
    }

    public boolean allDone() {

        if (currentAIDNumber >= aids.length - 1) {

            return true;
        }

        return false;
    }


    public SimmulatdTradingParameters getParams() {
        return params;
    }

    public void setParams(SimmulatdTradingParameters params) {
        this.params = params;
    }
}
