package algorithm;

import dtp.commission.Commission;
import dtp.simmulation.SimInfo;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Interface for algorithms, which are responsible for deployment of commissions
 * in local holon (eunit) schedule. It is important that any new algorithm
 * should implement it, because this interface is used in system. Apart from
 * that after implementing new algorithm you should add it in configuration (xsm
 * schedma and configuration parser)
 */
public interface Algorithm extends Serializable {

    /**
     * Main method which is responsible for create new schedule and add
     * commissions. If commission can't be add this method should return null.
     *
     * @param commissionToAdd new commissions to add
     * @param currentLocation current Holon location. It is used in dynamic problems
     * @param currentSchedule current Holon schedule. You have to add new commission to this
     *                        schedule
     * @param timestamp       timestamp
     * @return new schedule, or null if new commission cannot be added to
     * schedule
     */
    Schedule makeSchedule(Commission commissionToAdd,
                          Point2D.Double currentLocation, Schedule currentSchedule,
                          int timestamp);

    void setMaxLoad(double maxLoad);

    void init(double maxLoad, SimInfo simInfo);

    Point2D.Double getDepot();

    SimInfo getSimInfo();
}
