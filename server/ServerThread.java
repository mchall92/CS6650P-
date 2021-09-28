package server;

import java.io.IOException;

/**
 * This class extends Thread to create a new thread.
 */
public class ServerThread extends Thread {
    private int port;
    private KeyValue KV;
    private String log;

    /**
     * Construct with port number, KV for map and log string that indicates whether this
     * is a TCPHandler or UDPHandler.
     * @param port port number
     * @param KV KeyValue for map storage
     * @param log log string that indicates whether this is a TCPHandler or UDPHandler.
     */
    public ServerThread(int port, KeyValue KV, String log) {
        super(log);
        this.port = port;
        this.KV = KV;
        this.log = log;
    }

    /**
     * Run new thread to listen to both UDP and TCP requests
     */
    @Override
    public void run() {
        super.run();

        Server tcpHandler;
        Server udpHandler;

        try {
            if (log.equalsIgnoreCase("Server.TCPHandler")) {
                tcpHandler = new TCPHandler(port, KV, log);
                tcpHandler.execute();
            }
            if (log.equalsIgnoreCase("Server.UDPHandler")) {
                udpHandler = new UDPHandler(port, KV, log);
                udpHandler.execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
