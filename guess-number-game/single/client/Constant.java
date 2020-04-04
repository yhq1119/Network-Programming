
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
    public final static String PING = "PING";
    public static final String C_FIRST_LINK = "C" + REGEX + "LINK" + REGEX;
    public static final String C_REGISTER_INFO = "C" + REGEX + "REGISTER" + REGEX;
    public static final String C_START_GAME = "C" + REGEX + "START" + REGEX;
    public static final String C_GUESS_NUMBER = "C" + REGEX + "GUESS" + REGEX;
    public static final String C_QUITS_GAME = "C" + REGEX + "QUIT" + REGEX;
    public static final String C_FINAL_RESULT = "C" + REGEX + "FINAL" + REGEX;
    public static final String C_PING_BACK = "C" + REGEX + "PINGBACK" + REGEX;
    public final static String R_CLIENT_GUESS = "The client’s guess number ";
    public final static String R_BIGGER = " is bigger than the generated number.";
    public final static String R_SMALLER = " is smaller than the generated number.";
    public final static String R_WIN = "Congratulations! You won!";
    public final static String R_LOST = "Unfortunately, you did not guess out. The target Number is ";

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
        String out = timeStamp() + " [" + G(who) + "] " + Y(does) + "[" + G(what) + "]";
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

    public static void send1(Socket socket, String content) {
        PrintStream out;
        try {
            out = new PrintStream(socket.getOutputStream());
            out.println(content);
        } catch (IOException e) {
            err("when using socket to send message<" + content + ">");
        }
        print("server",
                " sent message<" + content + "> to client at port ",
                socket.getPort() + "");
    }

    public static String receive(Socket socket) {
        BufferedReader in;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String s = null;
            while ((s = in.readLine()) != null) {
                stringBuilder.append(s);
            }

        } catch (IOException e) {
            err("when reading from socket.");
        }
        print("server", " reads message as", stringBuilder.toString());
        return stringBuilder.toString();
    }


}
