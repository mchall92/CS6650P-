package server;

/**
 * This class serves as the abstract class for TCPHandler and UDPHandler.
 */
abstract class AbstractHandler implements Server{

    int port;
    KeyValue KV;
    ServerLogger logger;

    /**
     * Construct the class with port number, KV for map and log string that indicates whether it
     * is TCP or UDP.
     * @param port port number
     * @param KV KeyValue instance to store key-value pairs
     * @param log log string that indicates whether it is TCP or UDP.
     */
    public AbstractHandler(int port, KeyValue KV, String log) {
        this.port = port;
        this.KV = KV;
        this.logger = new ServerLogger(log);
    }
}
