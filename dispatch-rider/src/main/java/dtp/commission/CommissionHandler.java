package dtp.commission;

import java.io.Serializable;

/**
 * Wrapper, przechowuje zlecenie i czas naplyniecia do systemu tegoz zlecenia.
 */
public class CommissionHandler implements Serializable {

    // zlecenie transportowe
    private Commission commission;

    // czas naplyniecia do systemu
    private int incomeTime;

    public CommissionHandler(Commission commission, int incomeTime) {

        this.commission = commission;
        this.incomeTime = incomeTime;
    }

    public Commission getCommission() {

        return commission;
    }

    public void setCommission(Commission commission) {

        this.commission = commission;
    }

    public int getIncomeTime() {

        return incomeTime;
    }

    public void setIncomeTime(int incomeTime) {

        this.incomeTime = incomeTime;
    }

    public String toString() {

        return "<" + incomeTime + "> " + commission.toString();
    }
}
