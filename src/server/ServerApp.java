package server;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * This is the class that starts server
 */
public class ServerApp {

    /**
     * Start a server that can listen to both TCP and UDP requests
     * @param args represents port number for TCP and UDP
     * @throws IOException is thrown if receiving request or sending response causes I/O error.
     */
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
           System.out.println( "Please enter command line correctly");
           return;
        }
        ServerLogger logger = new ServerLogger("Server.ServerAoo");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        int tcpPort;
        int udpPort;
        // parse port num
        try {
            tcpPort =  Integer.parseInt(args[0]);
            udpPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            logger.error("Incorrect port number format.   " + timestamp);
            return;
        }

        KeyValue KV = new KeyValue();

        // start two threads, one for TCP and one for UDP
        ServerThread tcpThread = new ServerThread(tcpPort, KV, "Server.TCPHandler");
        ServerThread udpThread = new ServerThread(udpPort, KV, "Server.UDPHandler");
        tcpThread.start();
        udpThread.start();
    }
}
