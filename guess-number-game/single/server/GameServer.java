

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("ALL")
public class GameServer {

    /**
     * This class is the entry of sGameClientHandler.
     * It starts server then keeping listening for
     * new socket connection then put it to new thread
     * to implement a concurrent server.
     */


    private static ServerSocket serverSocket; // serverSocket

    /**
     * main method to run server.
     * Two steps: startServer();
     * running();
     */

    public static void main(String[] args) {

        startServer();
        running();
    }

    /**
     * This method initialize the serverSocket
     * with Constant final values.
     */

    @SuppressWarnings("AccessStaticViaInstance")
    private static void startServer() {

        try {
            serverSocket = new ServerSocket(Constant.PORT1);
            String host = serverSocket.getInetAddress().getLocalHost().getHostAddress();
            Constant.print("server", "started at IP [" + host + "] port",
                    String.valueOf(Constant.PORT1));

        } catch (IOException e1) {
            Constant.err("Port already in use. Server quits.");
            System.exit(1);
        } catch (Exception ee) {
            Constant.err("Unknown error. Server quits.");
            System.exit(1);
        }

    }

    /**
     * This method uses a while loop to
     * keep listening for new connected
     * socket and add it to list.
     */

    @SuppressWarnings("InfiniteLoopStatement")
    private static void running() {
        try {
            while (true) {

                Socket clientSocket = serverSocket.accept();
                Constant.print("client", "connected to server " +
                                "with IP address[" + clientSocket.getInetAddress() + "] port=",
                        String.valueOf(clientSocket.getPort()));
                sGameClientHandler sGameClientHandler = new sGameClientHandler(clientSocket);
                Thread thread = new Thread(sGameClientHandler);
                sGameClientHandler.setId(thread.getName());
                thread.start();
                Constant.print("client",
                        "starts running on [" + thread.getName() + "] " +
                                "with thread id =", String.valueOf(thread.getId()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
