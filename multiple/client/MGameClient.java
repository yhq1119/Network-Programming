package MClient;

import java.io.IOException;
import java.net.Socket;

/**
 * this class connects
 * to the socket
 * with the hard coded
 * information and
 * start a handler
 * to communicate with server
 *
 */



public class MGameClient {

    private Socket socket = null;
    private String SERVER_ADDRESS = Constant.LOCALHOST;
    private String SERVER_ADDRESS2 = "192.168.0.88";
    private int PORT = Constant.PORT1;

    public static void main(String[] args) {


        new MGameClient().init();

        Constant.print("client", "running status =", "stopped");
    }

    private void init() {

        try {
            Constant.print("client", "attempts to connect",
                    " <" + SERVER_ADDRESS2 + "> port < " + PORT + " > ");
            socket = new Socket(SERVER_ADDRESS2, PORT);
            Constant.print("client", "connect", "successfully");
        } catch (IOException e) {
            Constant.err("Failed to link to server " +
                    "at <" + SERVER_ADDRESS2 + "> " +
                    "with port <" + PORT + ">");
            return;
        }

        new MGameHandler(socket).start();

        Constant.print("client", " connected to server with socket ",
                socket.getInetAddress().toString() + " Port: " + socket.getLocalPort());

    }


}
