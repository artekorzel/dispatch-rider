package xml.elements;

import algorithm.Schedule;
import dtp.jade.transport.TransportElementInitialData;
import dtp.jade.transport.TransportElementInitialDataTrailer;
import dtp.jade.transport.TransportElementInitialDataTruck;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class SimmulationData implements Serializable {

    private int holonId;
    private int holonCreationTime;
    private TransportElementInitialDataTrailer trailer;
    private TransportElementInitialDataTruck truck;
    private TransportElementInitialData driver;
    private List<CommissionData> commissions = new LinkedList<CommissionData>();
    private Point2D.Double location;
    private Schedule schedule;

    public Point2D.Double getLocation() {
        return location;
    }

    public void setLocation(Point2D.Double location) {
        this.location = location;
    }

    public Integer getHolonId() {
        return holonId;
    }

    public void setHolonId(int holonId) {
        this.holonId = holonId;
    }

    public Integer getHolonCreationTime() {
        return holonCreationTime;
    }

    public void setHolonCreationTime(int holonCreationTime) {
        this.holonCreationTime = holonCreationTime;
    }

    public TransportElementInitialDataTrailer getTrailer() {
        return trailer;
    }

    public void setTrailer(TransportElementInitialDataTrailer trailer) {
        this.trailer = trailer;
    }

    public TransportElementInitialDataTruck getTruck() {
        return truck;
    }

    public void setTruck(TransportElementInitialDataTruck truck) {
        this.truck = truck;
    }

    public TransportElementInitialData getDriver() {
        return driver;
    }

    public void setDriver(TransportElementInitialData driver) {
        this.driver = driver;
    }

    public List<CommissionData> getCommissions() {
        return commissions;
    }

    public void setCommissions(List<CommissionData> commissions) {
        this.commissions = commissions;
    }

    public void addCommissionData(CommissionData data) {
        commissions.add(data);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("HolonId: ").append(holonId).append("\n");
        buf.append("\tCreationTime: ").append(holonCreationTime).append("\n");
        buf.append("\ttruck: ").append(truck.getPower()).append("\n");
        buf.append("\ttrailer: ").append(trailer.getCapacity()).append("\n");
        buf.append("\tcommissions:\n");
        for (CommissionData com : commissions)
            buf.append("\t\tcomId=").append(com.comId).append(" ")
                    .append("arrival=").append(com.arrivalTime).append(" ")
                    .append("departure=").append(com.departTime).append("\n");
        return buf.toString();
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
