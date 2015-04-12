package measure.printer;

import measure.Measure;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class XLSPrinter implements MeasurePrinter {

    private Workbook wb;
    private Sheet sheet;
    private List<String> columns;
    private int offset = 0;
    private String fileName;
    private CellStyle cellStyle;

    @Override
    public void createDocument(String fileName) {
        this.fileName = fileName + "_measures.xls";
        wb = new HSSFWorkbook();
        sheet = wb.createSheet("sheet");
        columns = new LinkedList<>();
        offset = 0;

        cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    }

    @Override
    public void printColumns(List<String> columns) {
        Row row = sheet.createRow(offset++);
        Cell cell;

        this.columns = columns;

        CellStyle columnStyle = wb.createCellStyle();
        columnStyle.setAlignment(CellStyle.ALIGN_CENTER);
        columnStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        columnStyle.setFont(font);
        for (int colNr = 0; colNr < this.columns.size(); colNr++) {
            cell = row.createCell(colNr);
            cell.setCellValue(this.columns.get(colNr));
            cell.setCellStyle(columnStyle);
        }

    }

    private String[] getSeparators(Measure measure) {
        return new String[]{"timestamp = " + measure.getTimestamp(),
                "comId = " + measure.getComId()};
    }

    @Override
    public void printNextPart(List<Measure> measures) {
        if (measures.size() == 0)
            return;

        Row row = sheet.createRow(offset++);
        Cell cell;
        int colNr = 0;

        Measure measure = measures.get(0);

        for (String separator : getSeparators(measure)) {
            cell = row.createCell(colNr++);
            cell.setCellValue(separator);
            cell.setCellStyle(cellStyle);
        }

        Set<String> aids = new TreeSet<>();
        aids.addAll(measure.getValues().keySet());
        for (String aid : aids) {
            colNr = 0;
            row = sheet.createRow(offset++);
            cell = row.createCell(colNr++);
            cell.setCellValue(Double.parseDouble(aid.split(" ")[0].split("#")[1]));
            cell.setCellStyle(cellStyle);

            for (Measure m : measures) {
                cell = row.createCell(colNr++);
                cell.setCellValue(m.getValues().get(aid));
                cell.setCellStyle(cellStyle);
            }
        }

        offset++;
    }

    @Override
    public void finish() throws IOException {
        for (int colNr = 0; colNr < columns.size(); colNr++)
            sheet.autoSizeColumn(colNr);

        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.write(fileOut);
        fileOut.close();
    }

}
