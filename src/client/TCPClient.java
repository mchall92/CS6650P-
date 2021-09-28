package client;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.Timestamp;

// Ref: https://github.com/ronak14329/Distributed-Key-Value-Store-Using-Sockets

/**
 * This is the class for client to send TCP request to server.
 */
public class TCPClient extends AbstractClient{

    ClientLogger logger = new ClientLogger("Client.TCPClient");

    /**
     * Construct with host name/address and port number.
     * @param host hostname or IP address
     * @param port port number
     * @throws IOException is thrown if sending request or receiving response error.
     */
    public TCPClient(String host, int port) throws IOException {
        super(host, port);
    }

    /**
     * Take in a request in a format of string.
     * @param msg represents an operation in string format (PUT/DELETE/GET)$(KEY)$(VALUE)
     * @throws IOException is thrown if sending request or receiving response error.
     */
    @Override
    public void request(String msg) throws IOException {
        // Get a communication stream associated with the socket
        Socket s1 = new Socket(host, port);

        // prepare request string
        OutputStream s1out = s1.getOutputStream();
        DataOutputStream dos = new DataOutputStream (s1out);

        // send msg as request message
        dos.writeUTF(msg);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        logger.debug("Request sent.   " + timestamp);

        // get response from server
        // if response takes more than one second, a SocketTimeoutException will be prompted
        AckFromServer(s1);

        dos.close();
        s1out.close();
        s1.close();

    }

    /**
     * Take response from server after sending request.
     * @param s client socket
     */
    private void AckFromServer(Socket s) {
        try {
            // read in message from server after sending request
            InputStream s1In = s.getInputStream();
            DataInputStream dis = new DataInputStream(s1In);
            s.setSoTimeout(1000);
            String ackMessage = dis.readUTF();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            // display the message in client log
            logger.debug("TCP- Acknowledgement message: " + ackMessage + "   " + timestamp);
            dis.close();
            s1In.close();
        } catch (SocketTimeoutException e) {
            // catch time out error if client has not received response from server in one second
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            logger.error("TCP- Timeout: Server does not respond within 1000ms.   "  + timestamp);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception ex) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            logger.error("Exception: " + ex + "   " + timestamp);
        }

    }
}
