package dtp.util;

/**
 * @author kony.pl
 */
public class AgentIDResolver {

    public static int getEUnitIDFromName(String name) {
        return Integer.valueOf(name.substring(name.indexOf("#") + 1));
    }
}
