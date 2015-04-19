package dtp.jade.eunit.behaviour;

import dtp.jade.CommunicationHelper;
import dtp.jade.eunit.EUnitOffer;
import dtp.jade.eunit.ExecutionUnitAgent;
import dtp.jade.transport.TransportOffer;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.apache.log4j.Logger;

import java.util.Map;

public class GetTransportOfferBehaviour extends CyclicBehaviour {

    private static Logger logger = Logger.getLogger(GetTransportOfferBehaviour.class);

    private ExecutionUnitAgent agent;

    public GetTransportOfferBehaviour(ExecutionUnitAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.TRANSPORT_OFFER);
        ACLMessage msg = agent.receive(template);

        TransportOffer offer;

        if (msg != null) {

            try {

                offer = (TransportOffer) msg.getContentObject();
                boolean end = false;
                if (agent.getAuction() != null) {
                    end = agent.getAuction().addOffer(offer);
                } else {
                    logger.fatal("AUCTION IS NULL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }

                if (end) {

                    doTheMonkeyBussines();
                    logger.info("it is final offer");
                    TransportOffer[] offers = doTheMonkeyBussines();
                    if (offers != null) {

                        if (agent.getDriver() != null) {
                            TransportOffer[] backupTeam = new TransportOffer[3];
                            backupTeam[0] = agent.getDriver();
                            backupTeam[1] = agent.getTruck();
                            backupTeam[2] = agent.getTrailer();
                            agent.setBackupTeam(backupTeam);
                        }

                        if (offers[0] == null || offers[1] == null || offers[2] == null) {
                            agent.sendOfferToDistributor(new EUnitOffer(agent.getAID(), -1.0, 0));

                        } else {
                            agent.setDriver(offers[0]);
                            agent.setTruck(offers[1]);
                            agent.setTrailer(offers[2]);

                            //MODIFICATION BY LP
                            if (agent.getTrailer() != null && offers[2] != null && offers[2].getTransportElementData() != null) {
                                agent.setMaxLoad(offers[2].getTransportElementData().getCapacity());
                            } else {
                                agent.sendOfferToDistributor(new EUnitOffer(agent.getAID(), -1.0, 0));
                            }
                            //END OF MODIFICATION

                            agent.checkNewCommission(agent.getAuction().getCommission());
                        }
                    } else {
                        agent.sendOfferToDistributor(new EUnitOffer(agent.getAID(), -1.0, 0));
                    }

                }
            } catch (UnreadableException e1) {
                logger.error(this.agent.getLocalName() + " - UnreadableException " + e1.getMessage());
            }
        } else {
            block();
        }
    }

    private TransportOffer[] doTheMonkeyBussines() {
        Map<Integer, TransportOffer[]> teams = agent.getAuction().getBestTeams();
        return teams.get(agent.getInitialData().getDepot());
    }

}
