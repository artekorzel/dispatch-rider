//package dtp.jade.eunit.behaviour;
//
//import dtp.jade.CommunicationHelper;
//import dtp.jade.eunit.ExecutionUnitAgent;
//import jade.core.behaviours.CyclicBehaviour;
//import jade.lang.acl.ACLMessage;
//import jade.lang.acl.MessageTemplate;
//
//public class GetInfoRequestBehaviour extends CyclicBehaviour {
//
//
//    private ExecutionUnitAgent eunitAgent;
//
//    public GetInfoRequestBehaviour(ExecutionUnitAgent agent) {
//
//        this.eunitAgent = agent;
//    }
//
//    public void action() {
//
//        MessageTemplate template = MessageTemplate.MatchPerformative(CommunicationHelper.EUNIT_SEND_INFO);
//        ACLMessage msg = myAgent.receive(template);
//
//        if (msg != null) {
//
//            eunitAgent.sendInfo();
//
//        } else {
//            block();
//        }
//    }
//}
