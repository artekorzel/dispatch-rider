package dtp.jade.agentcalendar;

import dtp.commission.Commission;

import java.util.List;

public interface AgentCalendar {
    List<? extends CalendarAction> getSchedule();

    double addCommission(Commission com, int timestamp);
}
