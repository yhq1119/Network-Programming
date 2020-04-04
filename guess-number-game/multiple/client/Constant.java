package MClient;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Constant {

    /**
     * This class restores all SERVER side
     * constant values for multiple game.
     */

    public static final String LOCALHOST = "localhost";
    public static final String SERVER1 = "10.102.128.22";
    public static final int PORT1 = 61119;
    public static final int PORT2 = 61991;
    public static final String REGEX = "--";

    public static final String C_PING_TIME = "C" + REGEX + "PING" + REGEX;
    public static final String C_REGISTER_INFO = "C" + REGEX + "REGISTER" + REGEX;
    public static final String C_REPLAY_GAME = "C" + REGEX + "REPLAY" + REGEX;
    public static final String C_GUESS_NUMBER = "C" + REGEX + "GUESS" + REGEX;
    public static final String C_QUITS_GAME = "C" + REGEX + "QUIT" + REGEX;
    public static final String C_EXIT_GUESS = "C" + REGEX + "EXIT" + REGEX;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";


    public static String R(String red) {
        return ANSI_RED + red + ANSI_RESET;
    } // Red word

    public static String G(String green) {
        return ANSI_GREEN + green + ANSI_RESET;
    } // Green word

    public static String Y(String yellow) {
        return ANSI_YELLOW + yellow + ANSI_RESET;
    } // yellow

    public static String B(String blue) {
        return ANSI_BLUE + blue + ANSI_RESET;
    } // blue

    public static String P(String purple) {
        return ANSI_PURPLE + purple + ANSI_RESET;
    } // purple

    public static String C(String cyan) {
        return ANSI_CYAN + cyan + ANSI_RESET;
    } // cyan

    public static String W(String white) {
        return ANSI_WHITE + white + ANSI_RESET;
    } // white

    public static String Black(String black) {
        return ANSI_BLACK + black + ANSI_RESET;
    } // black word


    private static boolean testMode = false; // test mode

    public static void print(String who, String does, String what) {
        String out = timeStamp() + " [" + G(who) + "] " + Y(does) + "[" + G(what) + "]";
        if (testMode) {
            System.out.println( out);
        }
    }

    public static void err(String err_msg) {
        String out = timeStamp() + R(" <Error> ") + err_msg;
        System.out.println( out);
    }

    public static String timeStamp() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timeStamp = dateFormat.format(date);
        return Y(timeStamp);
    }

    public static void send(Socket socket, String content) throws IOException {
        print("Inside send", "", "START SEND");

        PrintStream out;
        out = new PrintStream(socket.getOutputStream());
        out.print(content + "\n");
        print("client",
                " sent message<" + content + "> to server at port ",
                socket.getPort() + "");
    }

    public static String receive(Socket socket) throws IOException, NullPointerException {

        print("Inside receive", "", "START RECEIVE");
        BufferedReader in;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while ((line = in.readLine()) != null) {
            stringBuilder.append(line);
        }
        print("client", " reads message as", stringBuilder.toString());
        return stringBuilder.toString();
    }


}
