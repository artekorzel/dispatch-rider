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

    public String getAllStats() {
        StringBuilder str = new StringBuilder();

        str.append("****************************** CALENDARS ******************************\n");

        for (int i = 0; i < collectedCalendars.length; i++) {

            str.append("ExecutionUnit#").append(i).append(": \n");
            str.append(collectedCalendars[i]).append("\n");
        }

        str.append("***********************************************************************\n");

        return str.toString();
    }
}
