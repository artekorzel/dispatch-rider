package algorithm;


import dtp.commission.Commission;
import dtp.jade.agentcalendar.AgentCalendarWithoutGraph;
import dtp.jade.agentcalendar.CalendarAction;
import dtp.jade.agentcalendar.CalendarActionWithoutGraph;
import jade.core.AID;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class Helper {
    public static double getRatio(double distance, Commission commission) {
        return 0.01 * distance + (distance / 100) * commission.getLoad();
    }

    public static double calculateDistance(Point2D.Double com1, Point2D.Double com2) {
        if (com1 == null || com2 == null) return 0.0;
        return Point2D.distance(com1.x, com1.y, com2.x, com2.y);
    }

    public static double calculateCost(Map<Integer, AgentCalendarWithoutGraph> holons, Point2D.Double depot) {
        double result = 0.0;
        for (AgentCalendarWithoutGraph calendar : holons.values())
            result += calculateCost(calendar, depot);
        return result;
    }

    public static double calculateCost(AgentCalendarWithoutGraph calendar, Point2D.Double depot) {
        double result = 0.0;
        Point2D.Double currentLocation = depot;
        Point2D.Double nextLocation;
        for (CalendarAction action : calendar.getSchedule()) {
            if (action.getCommissionID() < 0) continue;
            nextLocation = ((CalendarActionWithoutGraph) action).getSource();
            result += calculateDistance(currentLocation, nextLocation);
            currentLocation = nextLocation;
        }
        result += calculateDistance(currentLocation, depot);
        return result;
    }

    public static double calculateCalendarCost(Map<?, Schedule> calendar, Point2D.Double depot) {
        double result = 0.0;
        for (Schedule schedule : calendar.values()) {
            //result+=schedule.calculateTime(depot);
            result += schedule.getDistance(depot);
        }
        return result;
    }

    public static Map<AID, Schedule> copyAID(Map<AID, Schedule> map) {
        Map<AID, Schedule> result = new HashMap<>();
        for (AID key : map.keySet()) {
            result.put(key, Schedule.copy(map.get(key)));
        }
        return result;
    }
}
