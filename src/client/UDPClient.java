package client;

import java.io.IOException;
import java.net.*;
import java.sql.Timestamp;

// Ref: https://github.com/ronak14329/Distributed-Key-Value-Store-Using-Sockets

/**
 * This is the class for client to send UDP request to server.
 */
public class UDPClient extends AbstractClient{

    ClientLogger logger = new ClientLogger("Client.UDPClient");

    /**
     * Construct with host name/address and port number.
     * @param host hostname or IP address
     * @param port port number
     * @throws IOException is thrown if sending request or receiving response error.
     */
    public UDPClient(String host, int port) {
        super(host, port);
    }

    /**
     * Take in a request in a format of string.
     * @param msg represents an operation in string format (PUT/DELETE/GET)$(KEY)$(VALUE)
     * @throws IOException is thrown if sending request or receiving response error.
     */
    @Override
    public void request(String msg) throws IOException {
        DatagramSocket aSocket = null;
        try {
            // Get a communication stream associated with the socket
            aSocket = new DatagramSocket();
            msg += "$!";

            // prepare message in byte
            byte [] m = msg.getBytes();
            InetAddress aHost = InetAddress.getByName(host);
            DatagramPacket request = new DatagramPacket(m, msg.length(), aHost, port);

            // send message to server in byte[]
            aSocket.send(request);

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            logger.debug("Request sent.   " + timestamp);

            // get response from server
            // if response takes more than one second, a SocketTimeoutException will be prompted
            AckFromServer(aSocket);
            aSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Take response from server after sending request.
     * @param client client socket
     */
    private void AckFromServer(DatagramSocket client) {
        try {
            // read in message from server after sending request
            client.setSoTimeout(1000);
            byte[] ackMsgBuffer = new byte[1000];
            DatagramPacket returnMsgPacket = new DatagramPacket(ackMsgBuffer, ackMsgBuffer.length);
            client.receive(returnMsgPacket);
            String msg = new String(returnMsgPacket.getData());
            msg = msg.substring(0, msg.indexOf("!"));
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            // display the message in client log
            logger.debug("UDP- Acknowledgement message: " + msg + "   " + timestamp);
        } catch (SocketTimeoutException e) {
            // catch time out error if client has not received response from server in one second
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            logger.error("UDP- Timeout: Server does not respond within 1000ms.   " + timestamp);
        } catch (IOException e) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            logger.error("UDP- An exception has occurred: " + e + "   " + timestamp);
        } catch (Exception ex) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            logger.error("UDP- Exception: " + ex + "   " + timestamp);
        }
    }
}
