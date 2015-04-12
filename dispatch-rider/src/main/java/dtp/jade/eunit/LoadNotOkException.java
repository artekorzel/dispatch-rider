package dtp.jade.eunit;

public class LoadNotOkException extends Exception {

    /**
     *
     */

    private double loadDiffrence;

    /**
     * getter
     *
     * @return the loadDiffrence
     */
    public double getLoadDiffrence() {
        return loadDiffrence;
    }

    /**
     * setter
     *
     * @param loadDiffrence the loadDiffrence to set
     */
    public void setLoadDiffrence(double loadDiffrence) {
        this.loadDiffrence = loadDiffrence;
    }

}
