package dtp.jade.gui;

import dtp.util.AgentIDResolver;

public class CalendarsHolder {

    private String[] collectedCalendars;

    private int collectedCalendarsNumber;

    public CalendarsHolder(int calendarsNumber) {

        collectedCalendars = new String[calendarsNumber];
        collectedCalendarsNumber = 0;
    }

    public void addCalendar(String agent, String calendar) {

        int eUnitAgentID;

        eUnitAgentID = AgentIDResolver.getEUnitIDFromName(agent);
        collectedCalendars[eUnitAgentID] = calendar;
        collectedCalendarsNumber++;
    }

    public boolean gotAllCalendarStats() {
        return collectedCalendarsNumber == collectedCalendars.length;
    }
}
