package dtp.xml;

/**
 * @author Grzegorz Wraps exception thrown by GraphParser.
 */
public class ParseException extends Exception {


    /**
     * Default constructor.
     */
    public ParseException() {
        super();
    }

    /**
     * Constructor with detailed message.
     *
     * @param message detailed information about exception
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
