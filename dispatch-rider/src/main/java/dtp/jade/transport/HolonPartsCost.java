package dtp.jade.transport;

import dtp.commission.Commission;

import java.util.LinkedList;
import java.util.List;


public class HolonPartsCost implements Comparable<HolonPartsCost> {
    private TransportAgentData[] agents;
    private Double cost;
    private List<Commission> commissions;

    public HolonPartsCost(TransportAgentData[] agents, double cost) {
        this.agents = agents;
        this.cost = cost;
        this.commissions = new LinkedList<Commission>();
    }

    public HolonPartsCost(TransportAgentData[] agents, double cost, Commission commission) {
        this.agents = agents;
        this.cost = cost;
        this.commissions = new LinkedList<Commission>();
        commissions.add(commission);
    }

    public void addCommission(Commission com) {
        commissions.add(com);
    }

    public int getCommissionsCount() {
        return commissions.size();
    }

    public List<Commission> getCommissions() {
        return commissions;
    }

    public void setCommissions(List<Commission> commissions) {
        this.commissions = commissions;
    }

    public TransportAgentData[] getAgents() {
        return agents;
    }

    public void setAgents(TransportAgentData[] agents) {
        this.agents = agents;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int compareTo(HolonPartsCost part) {
        if (new Integer(getCommissionsCount()).compareTo(part.getCommissionsCount()) == 0) {
            return cost.compareTo(part.getCost());
        }
        return new Integer(getCommissionsCount()).compareTo(part.getCommissionsCount()) * (-1);
    }
}
