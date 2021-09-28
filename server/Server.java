package server;

import java.io.IOException;

/**
 * This is an interface for TCPHandler and UDPHandler
 */
interface Server {

    /**
     * Execute to listen to requests
     * @throws IOException is thrown if receiving request or sending response causes I/O error.
     */
    public void execute() throws IOException;
}
