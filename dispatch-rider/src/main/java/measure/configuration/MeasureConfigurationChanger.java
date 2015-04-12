package measure.configuration;

import jade.core.AID;
import measure.printer.MeasureData;

import java.util.Map;

/**
 * Interface of configuration changers. All you have to do, is to write class
 * which implements it. Then use it in distributor (now there is used
 * MeasureConfigurationChangerImpl class, so you have to implement it). You can
 * enabling and disabling changing of configuration at runtime, by set attribute
 * confChange (default: false) to true in commissions element in main
 * configuration file.
 */
public interface MeasureConfigurationChanger {

    /**
     * This method is invoking by distributor always before all other methods.
     * You should use this parameter in other methods.
     *
     * @param data
     */
    public void setMeasureData(MeasureData data);

    public Map<AID, HolonConfiguration> getNewHolonsConfigurations();

    public GlobalConfiguration getNewGlobalConfiguration();
}
