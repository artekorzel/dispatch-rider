package xml.elements;

import algorithm.Helper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.List;
import java.util.Map;

public class XMLBuilder {

    private final Map<Integer, List<SimmulationData>> data;
    private final Point2D.Double depot;
    private Document dom;

    public XMLBuilder(Map<Integer, List<SimmulationData>> data,
                      Point2D.Double depot) {
        this.data = data;
        this.depot = depot;
        createDocument();
    }

    private void createDocument() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            Element rootEl = dom.createElement("simmulation");
            dom.appendChild(rootEl);

            Element simTimeEl;
            Element holonsEl;
            Element holonEl;
            Element partsEl;
            Element transportEl;
            Element commsEl;
            Element comEl;
            List<SimmulationData> lastSimData = null;
            for (Integer time : data.keySet()) {
                simTimeEl = dom.createElement("simTime");
                simTimeEl.setAttribute("time", time.toString());
                holonsEl = dom.createElement("holons");
                lastSimData = data.get(time);
                for (SimmulationData simData : data.get(time)) {
                    holonEl = dom.createElement("holon");
                    holonEl.setAttribute("id", simData.getHolonId().toString());
                    holonEl.setAttribute("creationTime", simData
                            .getHolonCreationTime().toString());
                    holonEl.setAttribute("locationX", new Double(simData
                            .getLocation().getX()).toString());
                    holonEl.setAttribute("locationY", new Double(simData
                            .getLocation().getY()).toString());
                    partsEl = dom.createElement("parts");
                    partsEl.setAttribute("connector", new Integer(simData
                            .getTruck().getConnectorType()).toString());
                    transportEl = dom.createElement("truck");
                    transportEl.setAttribute("power", new Integer(simData
                            .getTruck().getPower()).toString());
                    transportEl
                            .setAttribute("fuelConsumption", new Integer(
                                    simData.getTruck().getFuelConsumption())
                                    .toString());
                    transportEl.setAttribute("reliability", new Integer(simData
                            .getTruck().getReliability()).toString());
                    transportEl.setAttribute("comfort", new Integer(simData
                            .getTruck().getComfort()).toString());

                    transportEl.setAttribute("id", simData.getTruck().getAID()
                            .getName().split("#")[1].split("@")[0]);
                    partsEl.appendChild(transportEl);
                    transportEl = dom.createElement("trailer");
                    transportEl.setAttribute("capacity", new Integer(simData
                            .getTrailer().getCapacity()).toString());
                    transportEl.setAttribute("mass", new Integer(simData
                            .getTrailer().getMass()).toString());
                    transportEl.setAttribute("cargoType", new Integer(simData
                            .getTrailer().getCargoType()).toString());
                    transportEl.setAttribute("universality", new Integer(
                            simData.getTrailer().getUniversality()).toString());
                    transportEl.setAttribute("id", simData.getTrailer()
                            .getAID().getName().split("#")[1].split("@")[0]);
                    // TODO other trailer properties
                    partsEl.appendChild(transportEl);
                    transportEl = dom.createElement("driver");
                    transportEl.setAttribute("id", simData.getDriver().getAID()
                            .getName().split("#")[1].split("@")[0]);
                    partsEl.appendChild(transportEl);

                    commsEl = dom.createElement("commissions");
                    for (CommissionData com : simData.getCommissions()) {
                        comEl = dom.createElement("commission");
                        comEl.setAttribute("nr", com.comId.toString());
                        comEl.setAttribute("arrivalTime",
                                com.arrivalTime.toString());
                        comEl.setAttribute("departTime",
                                com.departTime.toString());
                        commsEl.appendChild(comEl);
                    }
                    holonEl.appendChild(partsEl);
                    holonEl.appendChild(commsEl);
                    holonsEl.appendChild(holonEl);
                }
                simTimeEl.appendChild(holonsEl);
                rootEl.appendChild(simTimeEl);
            }

            Element baseRetEl = dom.createElement("baseReturns");
            List<CommissionData> commsData;
            CommissionData commData;
            Double dist;
            Double time;
            for (SimmulationData data : lastSimData) {
                holonEl = dom.createElement("holon");
                holonEl.setAttribute("id", data.getHolonId().toString());
                commsData = data.getCommissions();
                commData = commsData.get(commsData.size() - 1);
                dist = Helper.calculateDistance(data.getLocation(), depot);
                time = commData.departTime + dist;
                holonEl.setAttribute("arrivalTime", time.toString());
                baseRetEl.appendChild(holonEl);
            }
            rootEl.appendChild(baseRetEl);
        } catch (ParserConfigurationException pce) {
            System.out
                    .println("Error while trying to instantiate DocumentBuilder "
                            + pce);
            System.exit(1);
        }
    }

    public void save(String fileName) {
        try {
            Source source = new DOMSource(dom);

            File file = new File(fileName);
            Result result = new StreamResult(file);

            Transformer xformer = TransformerFactory.newInstance()
                    .newTransformer();
            xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");

            xformer.transform(source, result);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

}
