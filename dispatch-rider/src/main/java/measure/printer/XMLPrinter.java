package measure.printer;

import measure.Measure;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class XMLPrinter implements MeasurePrinter {

    private String fileName;
    private Document dom;
    private List<String> columns;
    private Element rootEl;

    @Override
    public void createDocument(String fileName) throws Exception {
        this.fileName = fileName + "_measures.xml";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = dbf.newDocumentBuilder();
        dom = db.newDocument();

        rootEl = dom.createElement("simulation_measures");
        dom.appendChild(rootEl);

        columns = new LinkedList<>();
    }

    @Override
    public void printColumns(List<String> columns) {
        this.columns = columns;
    }

    @Override
    public void printNextPart(List<Measure> measures) {
        Element measuresEl = dom.createElement("measures");

        Measure measure = measures.get(0);

        measuresEl.setAttribute("timestamp",
                Integer.toString(measure.getTimestamp()));
        measuresEl.setAttribute("comId",
                Integer.toString(measure.getComId()));

        Set<String> aids = new TreeSet<String>();

        Element holonEl;
        aids.addAll(measure.getValues().keySet());
        Element measureEl;

        int colNr;

        for (String aid : aids) {
            holonEl = dom.createElement("holon");
            holonEl.setAttribute("id", aid.split(" ")[0].split("#")[1]);
            colNr = 1;
            for (Measure m : measures) {
                measureEl = dom.createElement("measure");
                measureEl.setAttribute("name", columns.get(colNr++));
                measureEl.setTextContent(m.getValues().get(aid)
                        .toString());
                holonEl.appendChild(measureEl);
            }
            measuresEl.appendChild(holonEl);
        }
        rootEl.appendChild(measuresEl);
    }

    @Override
    public void finish() throws Exception {

        Source source = new DOMSource(dom);

        File file = new File(fileName);
        Result result = new StreamResult(file);

        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
                "4");

        xformer.transform(source, result);

    }

}
