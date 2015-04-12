package dtp.jade.eunit;

import jade.core.AID;

import java.io.Serializable;

/**
 * Oferta eunita
 *
 * @author kony.pl
 */
public class EUnitOffer implements Serializable, Comparable<EUnitOffer> {

    private AID agent;
    private double value;
    private Integer commissionsCount;

    public EUnitOffer(AID agent, double value, int commissionCount) {
        this.commissionsCount = commissionCount;
        this.agent = agent;
        this.value = value;
    }

    public int compareTo(EUnitOffer o) {
        return new Double(value).compareTo(o.getValue());
    }

    public int getCommissionCount() {
        return commissionsCount;
    }

    public AID getAgent() {

        return agent;
    }

    public void setAgent(AID agent) {

        this.agent = agent;
    }

    public double getValue() {

        return value;
    }

    public void setValue(double value) {

        this.value = value;
    }
}
