package dtp.util;

import jade.core.AID;

@SuppressWarnings("rawtypes")
public class AIDsComparator implements java.util.Comparator {

    public int compare(Object arg0, Object arg1) {

        AID aid0 = (AID) arg0;
        AID aid1 = (AID) arg1;

        return AgentIDResolver.getEUnitIDFromName(aid0.getLocalName())
                - AgentIDResolver.getEUnitIDFromName(aid1.getLocalName());
    }

}
