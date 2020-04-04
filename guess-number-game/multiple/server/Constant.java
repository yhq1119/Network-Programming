

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class restores all SERVER side
 * constant values for multiple game.
 * And some frequently used methods.
 */

public class Constant {


    public static final String LOCALHOST = "localhost";
    public static final String SERVER1 = "10.102.128.22";
    public static final int PORT1 = 61119;
    public static final int PORT2 = 61991;
    public static final String REGEX = "--";

    public static final String S_REQUEST_REGISTER = "S" + REGEX + "REGISTER" + REGEX;
    public static final String S_INFORM_STATUS = "S" + REGEX + "INFORM" + REGEX;


    public final static String R_clientGuess = "The client’s guess number ";
    public final static String R_bigger = " is bigger than the generated number.";
    public final static String R_smaller = " is smaller than the generated number.";
    public final static String R_congrats = "Congratulations! You won!";
    public final static String R_sorry = "Unfortunately, you did not guess out. The target Number is ";


    public static String isBigger(int i, int turns) {
        return R_clientGuess + i + R_bigger + " You have [" + turns + "] turns left.";
    }

    public static String isSmaller(int i, int turns) {
        return R_clientGuess + i + R_smaller + " You have [" + turns + "] turn left.";
    }

    public static String congrats(int turns) {
        return R_congrats + " You have [" + turns + "] turn left.";
    }

    public static String lost(int target, int turns) {
        return R_sorry + target + ". You have [" + turns + "] turn left.";
    }

    public static final String S_GUESS_GAME = "S" + REGEX + "INFORM" + REGEX; // header + REGEX + game Status + REGEX + the guess result + the remaining turns

    public static final String S_HINT_INFO = "S" + REGEX + "INFO" + REGEX;
    public static final String S_CLIENT_CAN_PLAY = "S" + REGEX + "CANPLAY" + REGEX;
    public static final String S_START_CLIENT_GAME = "S" + REGEX + "PLAY" + REGEX;
    public static final String S_COMPARE_RESULT = "S" + REGEX + "COMPARE" + REGEX;
    public static final String S_RANK_SCORE = "S" + REGEX + "RANK" + REGEX;
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

    public static void print(String str) {
        String out = Y(timeStamp() + ":") + str;
        System.out.println(out);
    }

    public static void err(String err_msg) {
        String out = timeStamp() + R(" <Error> ") + err_msg;
        System.out.println(out);
    }

    public static String timeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timeStamp = dateFormat.format(new Date(System.currentTimeMillis()));
        return Y(timeStamp);
    }

    public static void send(Socket socket, String content) throws IOException, SocketException {

        PrintStream out = new PrintStream(socket.getOutputStream());
        out.flush();
        out.println(content);
        out.flush();
    }

    public static String receive(Socket socket) throws SocketException, IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while ((line = in.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }


}
