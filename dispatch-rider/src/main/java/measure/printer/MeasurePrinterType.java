package measure.printer;

public enum MeasurePrinterType {

    XLS(XLSPrinter.class),
    XML(XMLPrinter.class);

    private Class<? extends MeasurePrinter> typeClass;

    MeasurePrinterType(Class<? extends MeasurePrinter> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<? extends MeasurePrinter> typeClass() {
        return typeClass;
    }
}
