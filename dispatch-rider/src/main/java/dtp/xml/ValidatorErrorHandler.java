package dtp.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Grzegorz A SAX error handler to perform XSD validation of xml documents to be parsed
 */
public class ValidatorErrorHandler implements ErrorHandler {

    /**
     * Returns a string describing parse exception details
     */
    private String getParseExceptionInfo(SAXParseException e) {
        String info = "URI=" + e.getSystemId() + " Line=" + e.getLineNumber() + ": " + e.getMessage();
        return info;
    }

    // The following methods are standard SAX ErrorHandler methods.

    public void warning(SAXParseException spe) throws SAXException {
        String message = "Warning: " + getParseExceptionInfo(spe);
        throw new SAXException(message);
    }

    public void error(SAXParseException spe) throws SAXException {
        String message = "Error: " + getParseExceptionInfo(spe);
        throw new SAXException(message);
    }

    public void fatalError(SAXParseException spe) throws SAXException {
        String message = "Fatal Error: " + getParseExceptionInfo(spe);
        throw new SAXException(message);
    }
}
