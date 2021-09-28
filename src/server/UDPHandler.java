package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.Timestamp;

// Ref: https://github.com/ronak14329/Distributed-Key-Value-Store-Using-Sockets

/**
 * This is the class for server to listen to UDP request from client.
 */
public class UDPHandler extends AbstractHandler {

    ServerLogger logger;

    /**
     * Construct with host name/address and port number and log string that indicates this is
     * a UDP server.
     * @param port port number
     * @param KV KeyVale to store map
     * @param log log string that indicates this is a UDP server
     */
    public UDPHandler(int port, KeyValue KV, String log) {
        super(port, KV, log);
        logger = new ServerLogger("Server.UDPHandler");
    }

    /**
     * Listens to UDP request and respond back to client.
     */
    @Override
    public void execute() {
        DatagramSocket socket = null;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            // Get a communication stream associated with the socket
            socket = new DatagramSocket(port);
            // prepare a byte array to read message body from the request
            byte[] msgBuffer = new byte[1000];
            while (true) {
                DatagramPacket dataPacket = new DatagramPacket(msgBuffer, msgBuffer.length);
                socket.receive(dataPacket);
                String msg = new String(dataPacket.getData());
                msg = msg.substring(0, msg.indexOf("!"));

                // log the message body
                logger.debug("Message from client: " + msg + "   " + timestamp);

                // parse the message to determine which operation is requested
                if (msg.length() > 0) {
                    String[] args = msg.split("\\$");
                    if (args[0].equalsIgnoreCase("PUT") && args.length == 3) {
                        putRequest(socket, dataPacket,args[1], args[2]);
                    } else if (args[0].equalsIgnoreCase("GET") && args.length == 2) {
                        getRequest(socket, dataPacket, args[1]);
                    } else if (args[0].equalsIgnoreCase("DELETE") && args.length == 2) {
                        deleteRequest(socket, dataPacket, args[1]);
                    } else {
                        // if malformed message or incorrect operation, respond failure back to server
                        String failureMsg = "Received a malformed request of length: " + dataPacket.getLength() + " from: "
                                + dataPacket.getAddress() + " at Port: " + dataPacket.getPort();
                        logger.error(failureMsg + "   " + timestamp);
                        sendFailureAckToClient(socket, dataPacket, failureMsg);
                    }
                } else {
                    // if malformed message or incorrect operation, respond failure back to server
                    String failureMsg = "Message content is not present.";
                    logger.error(failureMsg + "   " + timestamp);
                    sendFailureAckToClient(socket, dataPacket, failureMsg);
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Process put request from client
     * @param socket socket for communication
     * @param clientPacket client packet
     * @param key key to be put
     * @param value value to be put
     */
    private void putRequest(DatagramSocket socket, DatagramPacket clientPacket, String key, String value) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // log put request
        logger.debug("Received PUT request from " + clientPacket.getAddress() + " at Port " + clientPacket.getPort()
         + "   " + timestamp);
        // put key-value into map
        if (key.length() > 0) {
            KV.put(key, value);
            logger.debug("PUT request SUCCESS. Put (Key / Value) : (" + key + " / " + value + ")   " + timestamp);
            AckToClient(socket, clientPacket, "PUT", key, value);

        } else {
            // if malformed message or incorrect operation, respond failure back to server
            String failureMsg = "Received a malformed request of length: " + clientPacket.getLength() + " from: "
                        + clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
            logger.error(failureMsg + "   " + timestamp);
            sendFailureAckToClient(socket, clientPacket, failureMsg);
        }
    }

    /**
     * Process get request from client
     * @param socket socket for communication
     * @param clientPacket client packey
     * @param key key
     */
    private void getRequest(DatagramSocket socket, DatagramPacket clientPacket, String key) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // log get request
        logger.debug("Received GET request from " + clientPacket.getAddress() + " at Port " + clientPacket.getPort()
         + "   " + timestamp);
        if (key.length() > 0) {
            if (KV.containsKey(key)) {
                // get value from key
                String value = KV.get(key);
                logger.debug("GET request SUCCESS. Key: " + key + ", maps to: " + value + "   " + timestamp);
                AckToClient(socket, clientPacket, "GET", key, value);
            } else {
                // respond failure back to server because key is not present in map
                String failureMsg = "Received a request key that map does not contain: " + clientPacket.getLength() +
                        " from: " + clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
                logger.error(failureMsg + "   " + timestamp);
                sendFailureAckToClient(socket, clientPacket, failureMsg);
            }
        } else {
            // if malformed message or incorrect operation, respond failure back to server
            String failureMsg = "Received a malformed request of length: " + clientPacket.getLength() + " from: "
                    + clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
            logger.error(failureMsg + "   " + timestamp);
            sendFailureAckToClient(socket, clientPacket, failureMsg);
        }
    }

    /**
     * Process delete request from client.
     * @param socket socket
     * @param clientPacket client packet
     * @param key key
     */
    private void deleteRequest(DatagramSocket socket, DatagramPacket clientPacket, String key) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // log delete request
        logger.debug("Received DELETE request from " + clientPacket.getAddress() + " at Port " + clientPacket.getPort()
         + "   " + timestamp);
        if (key.length() > 0) {
            // delete value from key
            if (KV.containsKey(key)) {
                KV.delete(key);
                logger.debug("DELETE request SUCCESS. Key: " + key + " deleted.   " + timestamp);
                AckToClient(socket, clientPacket, "DELETE", key, "");
            } else {
                // respond failure back to server because key is not present in map
                String failureMsg =
                        "Received a request key that map does not contain. Request length: " + clientPacket.getLength() +
                        " from: " + clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
                logger.error(failureMsg + "   " + timestamp);
                sendFailureAckToClient(socket, clientPacket, failureMsg);
            }
        } else {
            // if malformed message or incorrect operation, respond failure back to server
            String failureMsg = "Received a malformed request of length: " + clientPacket.getLength() + " from: "
                    + clientPacket.getAddress() + " at Port: " + clientPacket.getPort();
            logger.error(failureMsg + "   " + timestamp);
            sendFailureAckToClient(socket, clientPacket, failureMsg);
        }
    }

    /**
     * Respond to client after receiving requests
     * @param socket socket for communication
     * @param request request packet from client
     * @param requestType (PUT/GET/DELETE)
     * @param key key
     * @param value value
     */
    private void AckToClient(DatagramSocket socket, DatagramPacket request, String requestType, String key,
                                    String value) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        logger.debug("Sending acknowledgement to client...   " + timestamp);
        // send a response back to server if operation is processed
        try {
            byte[] ackMessage = new byte[1000];
            if (requestType != null && requestType.equalsIgnoreCase("PUT")) {
                ackMessage = ("PUT request SUCCESS. Put (Key / Value) : (" + key + " / " + value + ")!").getBytes();
            } else if (requestType != null && requestType.equalsIgnoreCase("GET")) {
                ackMessage = ("GET request SUCCESS. Key: " + key + ", maps to: " + value + "!").getBytes();
            } else if (requestType != null && requestType.equalsIgnoreCase("DELETE")) {
                ackMessage = ("DELETE request SUCCESS. Key: " + key + " deleted!").getBytes();
            }
            DatagramPacket ackMsgPacket = new DatagramPacket(ackMessage, ackMessage.length, request.getAddress(),
                                                             request.getPort());
            socket.send(ackMsgPacket);

        } catch (IOException e) {
            logger.error("Caught exception: " + e + "   " + timestamp);
        }
    }

    /**
     * Send failure operation back to client
     * @param socket socket for communication
     * @param request request packet
     * @param returnMsg failure message
     */
    private void sendFailureAckToClient(DatagramSocket socket, DatagramPacket request, String returnMsg) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        logger.debug("Sending an acknowledgement to client for FAILURE...   " + timestamp);
        try {
            byte[] ackMessage = new byte[1000];
            ackMessage = ("Request FAILED: " + returnMsg + "!").getBytes();
            DatagramPacket ackMsgPacket = new DatagramPacket(ackMessage, ackMessage.length, request.getAddress(),
                                                             request.getPort());
            socket.send(ackMsgPacket);
        } catch (IOException e) {
            logger.error("Caught exception: " + e + "   " + timestamp);
        }
    }
}
