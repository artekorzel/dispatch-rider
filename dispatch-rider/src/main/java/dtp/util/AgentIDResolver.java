package dtp.util;

/**
 * @author kony.pl
 */
public class AgentIDResolver {

    /**
     * Gets EUnit's ID from name (EUnitAgent#13 -> 13)
     *
     * @param name agent's name
     * @return agent's ID
     */
    public static int getEUnitIDFromName(String name) {

        return Integer.valueOf(name.substring(name.indexOf("#") + 1));
    }
}
