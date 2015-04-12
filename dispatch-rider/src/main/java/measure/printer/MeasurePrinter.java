package measure.printer;

import measure.Measure;

import java.io.Serializable;
import java.util.List;

public interface MeasurePrinter extends Serializable {
    void createDocument(String fileName) throws Exception;

    void printColumns(List<String> columns);

    void printNextPart(List<Measure> measures);

    void finish() throws Exception;
}
