package measure.configuration;

import java.io.Serializable;

/**
 * This class contains parameters, which can be changed during simulation. They
 * are global parameters, which means they concern behavior of simulation, or
 * concern all holons.
 * <p/>
 * You don't have to set all parameters (unset parameters will be omitted).
 */
public class GlobalConfiguration implements Serializable {


    /**
     * Commissions sending type. If this parameter is true, commissions are
     * sending in packages (very time consuming), if it's false they are sending
     * one by one.
     */
    private Boolean type;

    /**
     * This parameter determines which holon will get new commission. If you set
     * it to true, there will be more units. If you set it to false, simulation
     * runs in mode where we prefer maximum usage of existing units (we don't
     * take into account costs of potential new holons)
     */
    private Boolean choosingByCost;

    /**
     * How much simulated trading loops we want to run for one commission. If
     * you set it to 0, simulated trading will be disabled
     */
    private Integer simulatedTrading;

    /**
     * It is connected with complexSimmulatedTrading algorithm. It determines
     * maximum number of commissions, which can be exchange between holons
     * (number of declaration like: 'If you carry my commission, I can accept
     * new commission').
     */
    private Integer STDepth;

    /**
     * Determines if worstCommission should be chose by maximum increase of time
     * or distance (calculated based on commissions in schedule)
     */
    private String chooseWorstCommission;

    public String getChooseWorstCommission() {
        return chooseWorstCommission;
    }

    public void setChooseWorstCommission(String chooseWorstCommission) {
        this.chooseWorstCommission = chooseWorstCommission;
    }

    public Boolean isType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public Boolean isChoosingByCost() {
        return choosingByCost;
    }

    public void setChoosingByCost(Boolean choosingByCost) {
        this.choosingByCost = choosingByCost;
    }

    public Integer getSimulatedTrading() {
        return simulatedTrading;
    }

    public void setSimulatedTrading(Integer simulatedTrading) {
        this.simulatedTrading = simulatedTrading;
    }

    public Integer getSTDepth() {
        return STDepth;
    }

    public void setSTDepth(Integer sTDepth) {
        STDepth = sTDepth;
    }
}
