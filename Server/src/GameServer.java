import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;


/**
 * this class is the entry
 * of the system
 * it starts the serverSocket
 * and keep listening new connections
 * and pass it to gameLobby.
 *
 */


public class GameServer {

    ServerSocket serverSocket = null;
    int PORT = 61119;
    GameLobby gameLobby = null;
    boolean running;

    public void start() {
        init();
        while (running) {
            listen();
        }
    }

    private void init() {
        try {

            Constant.print("server",
                    "starting at <" + InetAddress.getLocalHost().getHostAddress() + ">" +
                            " port", String.valueOf(PORT));
            serverSocket = new ServerSocket(PORT);
            Constant.print("server", "started running", "successfully");

        } catch (SocketException e) {
            Constant.print("server", "initializes", Constant.R("FAILED"));
            running = false;
            ask();

        } catch (IOException e) {
            Constant.err("Port[" + PORT + "] has been used!");
            running = false;
            ask();
        }
        gameLobby = new GameLobby();
        gameLobby.start();
        running = true;
    }

    private void listen() {
        try {
            Constant.print("server", "is listening", "new connection");
            Socket connection = serverSocket.accept();
            Constant.print("server", "accepts connection from",
                    "IP <" + connection.getInetAddress() + "> port <" + (connection.getPort() + ">"));
            gameLobby.add(new GameHandler(connection));

        } catch (IOException e) {
            System.err.println("Invalid server socket!");
            running = false;
            ask();
        }
    }

    private void ask() {
        String str = input("Restart server?<Y/N>");
        switch (str.toUpperCase()) {
            case "Y":
                Constant.print("server", "performs", "restart");
                new GameServer().start();
                break;
            case "N":
                Constant.print("server", "performs", "quit");
                System.exit(1);
            default:

        }
    }

    private String input(String hint) {
        Scanner scanner = new Scanner(System.in);
        String str = null;
        while (str == null) {
            System.out.println(hint);
            str = scanner.nextLine();
            if (!str.toUpperCase().equals("Y") &&
                    !str.toUpperCase().equals("N")) {
                System.err.println("Please input 'Y' or 'N' to continue!");
                str = null;
            }
        }
        return str;
    }
}
