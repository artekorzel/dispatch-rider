package dtp.jade.agentcalendar;

public interface CalendarAction {
    String getType();

    int getCommissionID();

    int getSourceCommissionID();

    void print();
}
