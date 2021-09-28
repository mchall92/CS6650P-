package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;

// Ref: https://github.com/ronak14329/Distributed-Key-Value-Store-Using-Sockets

/**
 * This is the class for server to listen to TCP request from client.
 */
public class TCPHandler extends AbstractHandler{

    ServerSocket socket;

    /**
     * Construct with host name/address and port number and log string that indicates this is
     * a TCP server.
     * @param port port number
     * @param KV KeyVale to store map
     * @param log log string that indicates this is a UDP server
     */
    public TCPHandler(int port, KeyValue KV, String log) {
        super(port, KV, log);
    }

    /**
     * Listens to TCP request and respond back to client.
     */
    @Override
    public void execute() {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            logger.debug("Listening...   " + timestamp);
            // Get a communication stream associated with the socket
            socket = new ServerSocket(port);
            // prepare a byte array to read message body from the request
            while (true) {
                Socket s1 = socket.accept();
                // Get input stream
                InputStream s1In = s1.getInputStream();
                DataInputStream dis = new DataInputStream(s1In);
                String msg = dis.readUTF();

                // parse the message to determine which operation is requested
                if (msg.length() > 0) {
                    String[] args = msg.split("\\$");
                    if (args[0].equalsIgnoreCase("PUT") && args.length == 3) {
                        putRequest(s1, args[1], args[2]);
                    } else if (args[0].equalsIgnoreCase("GET") && args.length == 2) {
                        getRequest(s1, args[1]);
                    } else if (args[0].equalsIgnoreCase("DELETE") && args.length == 2) {
                        deleteRequest(s1, args[1]);
                    } else {
                        // if incorrect operation, respond failure back to server
                        timestamp = new Timestamp(System.currentTimeMillis());
                        logger.error("Request content incorrect, no operation done:" +
                                     "request from " + s1.getInetAddress() + " at Port " + s1.getPort() + "   " + timestamp);
                        AckToClient(s1, "ERROR", "", "Request FAILED: Operation Incorrect");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Process put request from client
     * @param client socket for communication
     * @param key key to be put
     * @param value value to be put
     */
    private void putRequest(Socket client, String key, String value) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // log put request
        logger.debug("PUT request received from " + client.getInetAddress() + " at Port " + client.getPort()
                             + "   " + timestamp);
        // put key-value into map
        if (key.length() > 0) {
            KV.put(key, value);
            logger.debug("PUT request SUCCESS. Put (Key / Value) : (" + key + " / " + value + ")   " + timestamp);
            AckToClient(client, "PUT", key, value);

            // close socket
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.error("Request content incorrect, no operation done:" +
                                 "request from " + client.getInetAddress() + " at Port " + client.getPort()
             + "   " + timestamp);
            AckToClient(client, "ERROR", "", "Request FAILED: Empty Key");
        }
    }

    /**
     * Process get request from client
     * @param client socket for communication
     * @param key key
     */
    private void getRequest(Socket client, String key) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        logger.debug("GET request received from " + client.getInetAddress() + " at Port " + client.getPort()
         + "   " + timestamp);
        String value;
        // get mapped value from key
        if (KV.containsKey(key)) {
            value = KV.get(key);
            logger.debug("GET request SUCCESS. Key: " + key + ", maps to: " + value + "   " + timestamp);
            AckToClient(client, "GET", key, value);
        } else {
            logger.error("Request FAILED: Map does not contain key: " + key);
            AckToClient(client, "ERROR", "", "Map does not contain key: " + key + "   " + timestamp);
        }

        // close socket
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Process delete request from client.
     * @param client socket
     * @param key key
     */
    private void deleteRequest(Socket client, String key) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // log get request
        logger.debug("DELETE request received from " + client.getInetAddress() + " at Port " + client.getPort()
         + "   " + timestamp);
        String value;
        // delete key in map
        if (KV.containsKey(key)) {
            KV.delete(key);
            logger.debug("DELETE request SUCCESS. Key: " + key + " deleted.   " + timestamp);
            AckToClient(client, "DELETE", key, "");
        } else {
            logger.error("Request FAILED: Map does not contain key: " + key + "   " + timestamp);
            AckToClient(client, "ERROR", "", "Map does not contain key: " + key);
        }

        // close socket
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Respond to client after receiving requests
     * @param client socket for communication
     * @param requestType (PUT/GET/DELETE)
     * @param key key
     * @param returnMsg return message
     */
    private void AckToClient(Socket client, String requestType, String key, String returnMsg) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        logger.debug("Sending acknowledgement to client...   " + timestamp);
        // send a response back to server if operation is processed
        try {
            OutputStream s1out = client.getOutputStream();
            DataOutputStream dos = new DataOutputStream (s1out);

            if (requestType != null && requestType.equalsIgnoreCase("PUT")) {
                dos.writeUTF("PUT request SUCCESS. Put (Key / Value) : (" + key + " / " + returnMsg + ")");
            } else if (requestType != null && requestType.equalsIgnoreCase("GET")) {
                dos.writeUTF("GET request SUCCESS. Key: " + key + ", maps to: " + returnMsg);
            } else if (requestType != null && requestType.equalsIgnoreCase("DELETE")) {
                dos.writeUTF("DELETE request SUCCESS. Key: " + key + " deleted");
            } else {
                // incorrect request
                // RequestType is not "PUT" nor "GET" nor "DELETE" but anything else
                // including ERROR
                dos.writeUTF("Request FAILED. " + returnMsg);
            }
            dos.close();
            s1out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
