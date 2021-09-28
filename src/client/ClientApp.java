package client;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Scanner;

/**
 * This is the class that starts Client
 */
public class ClientApp {

    private static String[] put1, put2, put3, put4, put5, put6, put7;
    private static String[] get1, get2, get3, get4, get5;
    private static String[] del1, del2, del3, del4, del5;

    /**
     * Start a TCP or UDP server.
     * @param args indicates host name, port number and TCP/UDP
     * @throws IOException is thrown if sending request / receiving response error.
     */
    public static void main(String[] args) throws IOException {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        ClientLogger logger = new ClientLogger("Client.ClientApp");
        Client client;
        int port;

        // args is consisted of host name, port number and TCP/UDP in order
        if (args.length != 3) {
            logger.error("Please enter command line correctly.   " + timestamp);
        }

        // get IP
        String host = args[0];

        // parse port number
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e ) {
            logger.error("Incorrect Port Number.   " + timestamp);
            return;
        }

        // determine TCP/UDP
        if (args[2].equalsIgnoreCase("TCP")) {
            client = new TCPClient(host, port);
        } else if (args[2].equalsIgnoreCase("UDP")) {
            client = new UDPClient(host, port);
        } else {
            logger.error("Incorrect indication for TCP or UDP.   " + timestamp);
            return;
        }

        // hard-coded operations
        hardCodeArgs();
        client.execute(put1);
        client.execute(put2);
        client.execute(put3);
        client.execute(put4);
        client.execute(put5);

        client.execute(get1);
        client.execute(get2);
        client.execute(get3);
        client.execute(get4);
        client.execute(get5);

        client.execute(del1);
        client.execute(del2);
        client.execute(del3);
        client.execute(del4);

        client.execute(put6);
        client.execute(del5);
        client.execute(put7);

        // custom operations\
        while (true) {

            Scanner sc= new Scanner(System.in);
            System.out.print("Enter an operation (PUT/GET/DELETE): ");
            String op = sc.nextLine();

            String[] operation = op.split("\\s+");

            // check if operation from user if correct
            // if so, send request, if not, prompt user to input operation again
            // and output instructions
            if (operation.length >= 2) {
                if (operation[0].equalsIgnoreCase("PUT") && operation.length == 3) {
                    client.execute(operation);
                } else if (operation[0].equalsIgnoreCase("GET") && operation.length == 2) {
                    client.execute(operation);
                } else if (operation[0].equalsIgnoreCase("DELETE") && operation.length == 2) {
                    client.execute(operation);
                } else {
                    errorOp();
                }
            } else if (operation[0].equalsIgnoreCase("CLOSE")) {
                break;
            } else {
                errorOp();
            }
        }
    }

    /**
     * Hard-coded operations.
     */
    private static void hardCodeArgs() {
        put1 = new String[]{"put", "A", "1"};
        put2 = new String[]{"put", "B", "2"};
        put3 = new String[]{"put", "C", "3"};
        put4 = new String[]{"put", "D", "4"};
        put5 = new String[]{"put", "A", "5"};

        get1 = new String[]{"get", "A"};
        get2 = new String[]{"get", "B"};
        get3 = new String[]{"get", "C"};
        get4 = new String[]{"get", "D"};
        get5 = new String[]{"get", "A"};

        del1 = new String[]{"delete", "A"};
        del2 = new String[]{"delete", "B"};
        del3 = new String[]{"delete", "C"};
        del4 = new String[]{"delete", "D"};

        put6 = new String[]{"put", "A", "1"};

        del5 = new String[]{"delete", "A"};

        put7 = new String[]{"put", "B", "2"};
    }

    /**
     * Response to user input error.
     */
    private static void errorOp() {
        String msg = "Operation format incorrect, please follow this format:\n"
                + "PUT KEY VAULE\n"
                + "GET KEY\n"
                + "DELETE KEY\n"
                + "If you would like to exit, please enter: close";
        System.out.println(msg);
    }
}