package client;

import java.io.IOException;

/**
 * This class serves as the abstract class for TCPClient and UDPClient.
 */
abstract class AbstractClient implements Client {

    String host;
    int port;

    /**
     * Construct with port number and host address
     * @param host host address
     * @param port port number
     */
    public AbstractClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Execute operation (PUT/GET/DELETE)
     * @param args operation (PUT/GET/DELETE)
     * @throws IOException if sending request causes IOException
     */
    @Override
    public void execute(String[] args) throws IOException {
        StringBuilder msg = new StringBuilder();
        msg = new StringBuilder();

        // create a string that is (PUT/GET/DELETE)$KEY$(VALUE)
        if (args[0].equalsIgnoreCase("PUT")) {
            msg.append(args[0]);
            msg.append("$");
            msg.append(args[1]);
            msg.append("$");
            msg.append(args[2]);
            request(msg.toString());
        } else if (args[0].equalsIgnoreCase("GET")) {
            msg.append(args[0]);
            msg.append("$");
            msg.append(args[1]);
            request(msg.toString());
        } else if (args[0].equalsIgnoreCase("DELETE")) {
            msg.append(args[0]);
            msg.append("$");
            msg.append(args[1]);
            request(msg.toString());
        }
    }
}
