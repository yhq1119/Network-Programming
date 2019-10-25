

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Constant {

    /**
     * This class stores the values that
     * used frequently.
     */

    public final static String LOCALHOST = "localhost";
    public final static int PORT1 = 61119;
    public final static int PORT2 = 61991;
    public final static String SERVER1 = "10.102.128.22";

    public final static String PING = "PING";


    /**
     * This class restores all SERVER side
     * constant values for multiple game.
     */

    public static final String REGEX = "--";

    public static final String S_REQUEST_REGISTER = "S" + REGEX + "REGISTER" + REGEX;
    public static final String S_INFORM_STATUS = "S" + REGEX + "INFORM" + REGEX;


    public final static String R_clientGuess = "The client’s guess number ";
    public final static String R_bigger = " is bigger than the generated number.";
    public final static String R_smaller = " is smaller than the generated number.";
    public final static String R_congrate = "Congratulations! You won!";
    public final static String R_sorry = "Unfortunately, you did not guess out. The target Number is ";


    public static final String S_HINT_REGISTER = "S" + REGEX + "RSUCCESS" + REGEX;
    public static final String S_CLIENT_CAN_PLAY = "S" + REGEX + "CANPLAY" + REGEX;
    public static final String S_START_CLIENT_GAME = "S" + REGEX + "PLAY" + REGEX;
    public static final String S_COMPARE_RESULT = "S" + REGEX + "COMPARE" + REGEX;
    public static final String S_FINAL_RESULT = "S" + REGEX + "FINAL" + REGEX;
    public static final String S_SHOW_RESULT = "S" + REGEX + "RESULTS" + REGEX;
    public static final String S_PING_CLIENT = "S" + REGEX + "PING" + REGEX;

    public static final String BIGGER = "BIG";
    public static final String SMALLER = "SML";
    public static final String WINS = "WIN";
    public static final String LOST = "LST";

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

    public static void print(String who, String does, String what) {
        String out = timeStamp() + " [" + G(who) + "] " + Y(does) + " [" + G(what) + "]";
        System.out.println(out);
    }

    public static void err(String err_msg) {
        String out = timeStamp() + R(" <Error> ") + err_msg;
        System.out.println(out);
    }

    public static String timeStamp() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timeStamp = dateFormat.format(date);
        return Y(timeStamp);
    }

    public static void send1(Socket socket, String content) throws IOException {
        PrintStream out;
        out = new PrintStream(socket.getOutputStream());
        out.println(content);
    }

    public static String receive1(Socket socket) throws IOException {
        BufferedReader in;
        String s = null;
        StringBuilder stringBuilder = new StringBuilder();

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while ((s = in.readLine()) != null) {
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }
}