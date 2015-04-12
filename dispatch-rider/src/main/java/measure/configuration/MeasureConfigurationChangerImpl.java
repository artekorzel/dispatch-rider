package measure.configuration;

import jade.core.AID;
import measure.printer.MeasureData;

import java.util.Map;

public class MeasureConfigurationChangerImpl implements
        MeasureConfigurationChanger {

    @SuppressWarnings("unused")
    private MeasureData data;

    @Override
    public void setMeasureData(MeasureData data) {
        this.data = data;
    }

    @Override
    public Map<AID, HolonConfiguration> getNewHolonsConfigurations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GlobalConfiguration getNewGlobalConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }
}
