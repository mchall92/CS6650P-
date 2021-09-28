package client;

import java.io.IOException;

/**
 * This is an interface for TCPClient and UDPClient
 */
public interface Client {

    /**
     * Execute to process an operation
     * @param args represents an operation in string array ((PUT/DELETE/GET), (KEY), (VALUE))
     * @throws IOException is thrown if sending I/O error.
     */
    public void execute(String[] args) throws IOException;

    /**
     * Send request to server.
     * @param msg represents an operation in string format (PUT/DELETE/GET)$(KEY)$(VALUE)
     * @throws IOException is thrown if sending I/O error.
     */
    public void request(String msg) throws IOException;

}
