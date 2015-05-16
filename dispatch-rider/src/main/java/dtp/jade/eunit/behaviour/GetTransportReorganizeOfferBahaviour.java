package dtp.jade.eunit.behaviour;

import dtp.jade.MessageType;
import dtp.jade.eunit.EUnitOffer;
import dtp.jade.eunit.ExecutionUnitAgent;
import dtp.jade.transport.TransportOffer;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

public class GetTransportReorganizeOfferBahaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetTransportReorganizeOfferBahaviour.class);

    private ExecutionUnitAgent agent;

    public GetTransportReorganizeOfferBahaviour(ExecutionUnitAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchConversationId(MessageType.TRANSPORT_REORGANIZE_OFFER.name());
        ACLMessage msg = agent.receive(template);

        TransportOffer offer = null;

        if (msg != null) {
            try {

                offer = (TransportOffer) msg.getContentObject();
                boolean end = false;
                if (agent.getReauction() != null) {
                    end = agent.getReauction().addOfffer(offer);
                }

                //logger.info("got offer " + offer.getRatio() + " " + agent.getReauction().isFinished());
                if (end) {
                    logger.info("it is final offer");
                    TransportOffer[] offers = agent.getReauction().getBestTeam();
                    if (offers != null) {

                        agent.setDriver(offers[0]);
                        agent.setTruck(offers[1]);
                        agent.setTrailer(offers[2]);

                        agent.addReorganizationTime(agent.getReauction().getReorganizationTime());

                        agent.setMaxLoad(agent.getTrailer().getMaxLoad());

                        agent.checkNewCommission(agent.getReauction().getCommission());
                    } else {
                        agent.sendOfferToDistributor(new EUnitOffer(agent.getAID(), -1.0, 0));

                    }

                }
            } catch (UnreadableException e1) {
                logger.error(this.agent.getLocalName() + " - UnreadableException ", e1);
            }

        } else {
            block();
        }

    }

}
