
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;


/**
 * This is a guess number single-player
 * client class. It only sets up two local
 * variables as Socket connect to I/O with
 * server and boolean canPlay to store game status.
 * This class is more like a view with controller,
 * all the game data and model is on server side.
 * This class only sends data and reads response from
 * server to show game play.
 *
 * @auher Haoqian Yang
 */

public class sGameClient {

    private static Socket connect; // for communication use.
    private static boolean canPlay; // store and read game status.
    private final static boolean testMode = false; // for test use.

    public static void main(String[] args) {


        new sGameClient().init();

    }

    /**
     * Display a loop and use a while
     * loop to reshow the menu when input
     * is inappropriate.
     */

    private void menuAction() {

        String command = input(
                ColorText.Y("Please select") +
                        "\n" + "<" + ColorText.C("P") + "> \t\tto Play Game;" +
                        "\n<" + ColorText.C("Q") + "/" + ColorText.C("Quit") + "> \tto Quit Game;" +
                        "\n" + ColorText.Y("Enter your choice:"));
        switch (command.toUpperCase()) {
            case "P":
                play1();
                break;
            case "Q":
            case "QUIT":
                quit();
                break;
            default:
                System.out.println(ColorText.R("Please select from the menu."));
                menuAction();
                break;
        }
    }

    /**
     * Send 'Q' to server and quits.
     */
    private void quit() {

        writeToServer("Q");
        System.out.println(ColorText.Y("Exiting...Done."));
        try {
            connect.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    /**
     * Loop to read game status and
     * player's input to play game.
     */

    private void play1() {

        writeToServer("P");
        readGameStatus();

        while (canPlay) {
            guess();
            readGameStatus();
        }
    }

    /**
     * This method sends the guess number to
     * server and reads and shows the server
     * response.
     */


    private void guess() {

        String s = null;
        int k;
        do {
            s = input(ColorText.C("Please guess the number[Enter <e/exit> to quit playing]:"));
            if (s.toUpperCase().equals("E") || s.toUpperCase().equals("EXIT")) {
                menuAction();
                return;
            } else {
                try {
                    k = Integer.parseInt(s);
                    if (k >= 0 && k <= 9) {
                        writeToServer("GN" + Constant.REGEX + k);
                        readResult(); // read result
                        return;
                    } else {
                        System.out.println(ColorText.R("Should between 0 to 9!"));
                    }
                } catch (NumberFormatException e) {
                    System.out.println(ColorText.R("Not a number!"));
                }
            }
        } while (true);

    }

    /**
     * This method reads the result
     * from the server and displays it.
     */


    private void readResult() {
        String s = readFromServer();
//            System.out.println(s); // test use code.
        String[] result = s.split(Constant.REGEX);
        if (result.length >= 2) {
            String turnsRemain;

            turnsRemain = ColorText.Y("You have [") + result[2] + ColorText.Y("] turns left.");

            switch (result[0].toUpperCase()) {

                case "LAR":
                    System.out.println(ColorText.Y(Constant.R_CLIENT_GUESS) +
                            ColorText.G(result[1]) +
                            ColorText.Y(Constant.R_BIGGER) + turnsRemain);
                    break;
                case "SML":
                    System.out.println(ColorText.Y(Constant.R_CLIENT_GUESS) +
                            ColorText.G(result[1]) +
                            ColorText.Y(Constant.R_SMALLER) + turnsRemain);
                    break;
                case "CON":
                    System.out.println(ColorText.Y(Constant.R_WIN) + turnsRemain);
                    break;
                case "ER":
                    System.out.println(ColorText.Y(Constant.R_LOST) +
                            ColorText.G(result[1] + ". ") + turnsRemain);
                    break;
            }
        } else {
            System.out.println(ColorText.R("Unknown parameter[") +
                    ColorText.G(s) +
                    ColorText.R("]"));
        }
    }

    /**
     * This method request current game
     * status from server and update boolean canPlay
     * to match current game status.
     * The parameter determines whether show the info
     * or not.
     */

    private void readGameStatus() {

        writeToServer("GS");
        String read = readFromServer();
//        System.out.println(read); // test use code.
        if (read.toUpperCase().equals("1")) {
            canPlay = true;
        } else if (read.toUpperCase().equals("0")) {
            canPlay = false;
        }
        if (testMode) {
            System.out.println(ColorText.Y("Current Game Status can play = [") +
                    ColorText.G(String.valueOf(canPlay)) +
                    ColorText.Y("]"));
        }

    }

    /**
     * This method request a response from the
     * server and calculate the time from sending
     * to receiving. It also displays the ping
     * result.
     */

    private void ping() {

        double start = System.nanoTime();
        writeToServer(Constant.PING);
        String string;
        if ((string = readFromServer()) != null) {
            if (string.toUpperCase().equals("SUCCESS")) {
                System.out.println(
                        ColorText.C("Respond = [") +
                                ColorText.G("True") +
                                ColorText.C("],Time used = [") +
                                ColorText.G(((System.nanoTime() - start) / 1000000) + " ms") +
                                ColorText.C("]")
                );
            } else {
                System.out.println(ColorText.R(string));
            }
        }
    }

    /**
     * This method initializes the socket connect
     * with user's input.
     */

    private void init() {
        String input = input(ColorText.Y(
                "Please enter <server IP> to connect,<exit> to quit:"
        ));
        if (input.length() == 0) {
            input = Constant.LOCALHOST; // localhost test use.
        }
        if (input.equals("exit")) {
            System.exit(1);
        }
        if (input.equals("1")) {
            input = Constant.SERVER1;
        }
        try {
            connect = new Socket(input, Constant.PORT1);
            System.out.println(
                    ColorText.Y("Connected to [") +
                            ColorText.G(connect.toString()) +
                            ColorText.Y("]"));
        } catch (SocketException e) {
            System.out.println(ColorText.R("Socket error."));
            main(null);
        } catch (IOException e) {
            System.out.println(ColorText.R("Socket I/O error."));
            main(null);
        }
        while (true) {

            menuAction();
        }
    }

    /**
     * This method returns String that
     * contains the data read from the server.
     *
     * @return the read data from server. Otherwise null.
     */

    private String readFromServer() {
        BufferedReader reader = null;
        String string = null;

        try {
            reader = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            string = reader.readLine();
        } catch (SocketException e) {
            rerun();
            e.printStackTrace();
        } catch (IOException e) {
            rerun();
            e.printStackTrace();
        }
        if (testMode) {
            Constant.print("client", "reads message", string);
        }

        return string;
    }

    /**
     * Display a warning message and
     * rerun the main method.
     */

    private void rerun() {
        System.out.println(ColorText.R("Connection might closed. Reboot."));
        try {
            connect.close();
        } catch (IOException e) { // rerun.
        }
        main(null);
    }

    /**
     * Dis play the message in the passing in
     * parameters and returns the String of
     * user input.
     *
     * @param hint the message that displays when use input.
     * @return String value from user input. Otherwise return null.
     */

    private String inputNum(String hint) {
        String s = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (s == null) {
            try {
                System.out.print(hint);
                s = in.readLine();

                int k = Integer.parseInt(s);
                if (k < 0 || k > 9) {
                    System.out.println(ColorText.R("Please input [an Integer] from 0 to 9!"));
                    s = null;
                }

            } catch (Exception e) {
                s = input(hint);
            }
        }
        return s;
    }

    private String input(String hint) {
        String s = null;
        Scanner in = new Scanner(System.in);

        while (s == null || s.length() < 1) {
            System.out.println(ColorText.R(hint));

            s = in.nextLine();
            if (s == null || s.length() < 1) {
                System.out.println(ColorText.R("Input something!"));
            }
        }
        return s;
    }

    /**
     * This method creates new PrintStream each time
     * to write passing in String content to server.
     *
     * @param content the String that needs to be sent to server.
     */

    private void writeToServer(String content) {

        try {
            PrintStream printStream = new PrintStream(connect.getOutputStream());
            printStream.println(content);
            printStream.flush();
        } catch (IOException e) {
            Constant.err("Failed to read from server.");
        }
        if (testMode) {
            Constant.print("client", "sends message", content);
        }
    }
}
