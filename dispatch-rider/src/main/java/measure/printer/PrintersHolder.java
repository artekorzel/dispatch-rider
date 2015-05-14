package measure.printer;

import measure.Measure;
import measure.MeasureCalculator;
import measure.MeasureCalculatorsHolder;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class PrintersHolder implements Serializable {

    private List<MeasurePrinter> printers = new LinkedList<>();

    public void addPrinter(String extension) throws IllegalArgumentException {
        String ext = extension.toUpperCase();
        try {
            MeasurePrinter printer = MeasurePrinterType.valueOf(ext).typeClass().newInstance();
            printers.add(printer);
        } catch (Exception e) {
            throw new IllegalArgumentException("Wrong parameter at 'formats' attribute: " + extension);
        }
    }

    private List<String> getColumns(MeasureCalculatorsHolder holder) {
        List<String> columns = new LinkedList<>();
        columns.add("holonId");
        for (MeasureCalculator calc : holder.getCalculators())
            columns.add(calc.getName());
        return columns;
    }

    public void print(String fileName, MeasureData data,
                      MeasureCalculatorsHolder holder) throws Exception {

        List<String> columns = getColumns(holder);
        for (MeasurePrinter printer : printers) {
            printer.createDocument(fileName);
            printer.printColumns(columns);
            for (List<Measure> measuresList : data) {
                printer.printNextPart(measuresList);
            }
            printer.finish();
        }
    }

    public List<MeasurePrinter> getPrinters() {
        return printers;
    }

    public void setPrinters(List<MeasurePrinter> printers) {
        this.printers = printers;
    }
}
