package client;

/**
 * This is a class for client logger
 */
public class ClientLogger  {

    String name;

    /**
     * Construct logger with class name.
     * @param name is the name for the class that instantiate logger.
     */
    protected ClientLogger(String name) {
        this.name = name;
    }

    /**
     * Display debug/log message.
     * @param msg debug/log message
     */
    protected void debug(String msg) {
        System.out.println(name + " LOG: " + msg);
    }

    /**
     * Display error message.
     * @param msg error message
     */
    protected void error(String msg) {
        System.err.println(name + " ERROR: " + msg);
    }

}