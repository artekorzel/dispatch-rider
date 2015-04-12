package measure.visualization;

import measure.Measure;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

//VS4E -- DO NOT REMOVE THIS LINE!
public class MeasureVisualizationPanel extends JPanel {

    public static final String avgSeriesName = "Avg value";
    private final Map<String, XYSeries> holonSeries = new TreeMap<String, XYSeries>();
    private final XYSeriesCollection dataset = new XYSeriesCollection();
    private String name = "None";
    private JFreeChart freeChart;

    public MeasureVisualizationPanel(String name) {
        this.name = name;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(100, 200));
        this.add(InitChart(), BorderLayout.CENTER);
    }

    private ChartPanel InitChart() {

        // Generate the graph
        freeChart = ChartFactory.createXYLineChart(name, // Title
                "timestamp", // x-axis Label
                "measure value", // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                true, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
        );

        return new ChartPanel(freeChart);
    }

    public synchronized void updateChart(Measure measure) {
        XYSeries avgSeries = null;
        double timestamp = measure.getTimestamp();
        int size = measure.getValues().size();
        if (size > 0) {
            avgSeries = holonSeries.get(avgSeriesName);
            if (avgSeries == null) {
                avgSeries = new XYSeries(avgSeriesName);
                holonSeries.put(avgSeriesName, avgSeries);
                dataset.addSeries(avgSeries);
            }
        }
        double sum = 0;
        for (String holon : measure.getValues().keySet()) {
            XYSeries series = holonSeries.get(holon);
            if (series == null) {
                series = new XYSeries(holon);
                holonSeries.put(holon, series);
                dataset.addSeries(series);
            }
            series.add(timestamp, measure.getValues().get(holon));
            sum += measure.getValues().get(holon);
        }
        if (avgSeries != null) {
            avgSeries.add(timestamp, sum / size);
        }
        freeChart.fireChartChanged();
    }

    @SuppressWarnings("unchecked")
    public synchronized void hide(String holon) {
        String name = holon;
        List<Object> seriesList = new LinkedList<Object>(dataset.getSeries());
        int index = 0;
        for (Object series : seriesList) {
            if (((XYSeries) series).getKey().equals(name))
                dataset.removeSeries(index);
            index++;
        }

        freeChart.fireChartChanged();
    }

    @SuppressWarnings("unchecked")
    public synchronized void show(String holon) {
        List<Object> seriesList = new LinkedList<Object>(dataset.getSeries());
        dataset.removeAllSeries();
        int refNr = getHolonNr(holon);
        boolean added = false;
        XYSeries xySeries;
        for (Object series : seriesList) {
            xySeries = (XYSeries) series;
            if (getHolonNr((String) xySeries.getKey()) < refNr) {
                dataset.addSeries(xySeries);
            } else {
                if (added == false) {
                    dataset.addSeries(holonSeries.get(holon));
                    dataset.addSeries(xySeries);
                    added = true;
                } else {
                    dataset.addSeries(xySeries);
                }
            }
        }

        if (added == false) {
            dataset.addSeries(holonSeries.get(holon));
        }

        freeChart.fireChartChanged();
    }

    private int getHolonNr(String str) {
        if (str.equals(avgSeriesName)) {
            return 0;
        }
        return Integer.parseInt(str.split("#")[1]) + 1;
    }
}
