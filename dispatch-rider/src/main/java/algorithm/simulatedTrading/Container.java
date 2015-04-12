package algorithm.simulatedTrading;

import dtp.commission.Commission;

public class Container implements Comparable<Container> {
    public double cost;
    public Commission commission;

    public Container(double cost, Commission commission) {
        this.commission = commission;
        this.cost = cost;
    }

    public int compareTo(Container c) {
        return Double.compare(cost, c.cost);
    }
}
