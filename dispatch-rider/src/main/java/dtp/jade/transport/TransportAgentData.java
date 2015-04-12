package dtp.jade.transport;

import jade.core.AID;
import jade.util.leap.Serializable;

public class TransportAgentData implements Serializable {


    private TransportElementInitialData data;
    private AID aid;

    public TransportAgentData(TransportElementInitialData data, AID aid) {
        this.aid = aid;
        setData(data);
    }

    public TransportElementInitialData getData() {
        return data;
    }

    public void setData(TransportElementInitialData data) {
        this.data = data;
        this.data.setAID(aid);
    }

    public AID getAid() {
        return aid;
    }

    public void setAid(AID aid) {
        this.aid = aid;
    }
}
