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

        if (collectedCalendarsNumber == collectedCalendars.length) {

            return true;
        }

        return false;
    }

    public String getAllStats() {

        StringBuilder str;

        str = new StringBuilder();

        str.append("****************************** CALENDARS ******************************\n");

        for (int i = 0; i < collectedCalendars.length; i++) {

            str.append("ExecutionUnit#" + i + ": \n");
            str.append(collectedCalendars[i] + "\n");
        }

        str.append("***********************************************************************\n");

        return str.toString();
    }
}
