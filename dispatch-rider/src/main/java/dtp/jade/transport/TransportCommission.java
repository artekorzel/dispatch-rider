package dtp.jade.transport;

import jade.core.AID;

import java.io.Serializable;

/**
 * Commission data for transport element
 *
 * @author Michal Golacki
 */
public class TransportCommission implements Serializable {

    /**
     * Id of sender of this commission
     */
    AID senderId;

    /**
     * Load to be carried
     */
    int load;

    /**
     * @return the senderId
     */
    public AID getSenderId() {
        return senderId;
    }

    /**
     * @param senderId the senderId to set
     */
    public void setSenderId(AID senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the load
     */
    public int getLoad() {
        return load;
    }

    /**
     * @param load the load to set
     */
    public void setLoad(int load) {
        this.load = load;
    }

}
