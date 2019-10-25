

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Random;

@SuppressWarnings("ALL")
public class sGameClientHandler implements Runnable {

    /**
     * This class implements Runnable interface in order to
     * handle client socket in thread. On server side, it only
     * needs to keep listening from client and make proper response
     * when needed.
     */

    private PrintStream toClient;
    private BufferedReader fromClient;
    private Socket connect; // to communicate with client.
    private String id; // the id of its thread.
    private String clientInfo; // including socket info and thread id.
    private boolean running; // to store the running status of current thread.

    private int Turns; // to store game remaining turns.
    private boolean win; // to store and read game status.
    private int targetNum; //to store and read every round target game.


    public sGameClientHandler(Socket connect) { // constructor to initialize the socket
        this.connect = connect;
        running = true;
    }

    /**
     * This method is the start of the handler
     * and here uses it to run through all methods.
     */

    @Override
    public void run() {

        startUp();

        running = true;

        while (running) {

            try {
                action();
            } catch (IOException e) {
                Constant.err("Error when send message to client! Invalid socket.");
            }
        }
        // there let the run method ends naturally. Avoid using anything like stop()
    }

    private void action() throws IOException {
        String read;
        read = receive();
        if (read != null && read.length() > 0) {

            String[] args = read.split(Constant.REGEX);

            switch (args[0].toUpperCase()) {
                case "Q":
                    quit();
                    break;
                case "GS": // request game status(can play ?)
                    sendStatus();
                    break;
                case "PING": // request a ping to server
                    ping();
                    break;
                case "P": // set a game
                    if (!canPlay()) resetGame();
                    break;
                case "GN": // guess a number(play)
                    play(args[1]);
                    break;
            }
        }
    }

    private void quit() {
        // Q to quit
        running = false;
        try {
            connect.close();
        } catch (IOException e) {
            Constant.err("Failed to close socket! Invalid socket.");
        }
        Constant.print("Client", "closed at port", "" + connect.getPort());
    }


    private String receive() throws IOException {
        Constant.print("client handler", "is now", "listening");
        String s;
        s = fromClient.readLine();
        Constant.print("client handler",
                "received message from " +
                        "client at port<" + connect.getPort() + "> as", s);
        return s;
    }

    /**
     * This method is the core game function.
     * It tries parse the String str into integer.
     * If fails, show a error message. If succeed,
     * pass it to guess(int) method.
     *
     * @param str the string of the guess number.
     */

    private void play(String str) throws IOException {
        try {
            int guess = Integer.parseInt(str);
//                                System.out.println(read);// test use.
            guessGame(guess);
        } catch (NumberFormatException e) {
            Constant.err("Error when parse Integer from client input.");
            e.printStackTrace();
        }
    }

    /**
     * A setter to set the thread Id to
     * the local variable id.
     *
     * @param s the value that used to set id
     */

    public void setId(String s) {

        id = s;
        clientInfo = Constant.Y("Client[") +
                Constant.G(connect.toString()) +
                Constant.Y("] in ") +
                Constant.B(getId());
    }

    /**
     * A getter to get Id String.
     *
     * @return String id
     */

    private String getId() {
        return this.id;
    }

    /**
     * A method to send current game status
     * to client with representing 'can' with '1'
     * and 'cannot' with '0';
     */

    private void sendStatus() throws IOException {
        String status;
        if (canPlay()) {
            status = "1";
        } else {
            status = "0";
        }
        // System.out.println(status); // test code.
        send(status);
        Constant.print("client handler",
                "send to  <" + clientInfo + "> as game status info =",
                (String.valueOf(canPlay())));
    }

    /**
     * This method initialize the socket
     * and the bufferedReader fromClient
     * and the PrintStream toClient.
     */


    private void startUp() {

        resetGame();
        try {
            // to read content sent by client.
            fromClient = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            // to write content to client.
            toClient = new PrintStream(connect.getOutputStream());
        } catch (IOException e) {
            Constant.err("Failed to get InputStream from client socket.");
        }
    }

    /**
     * This method takes the guess number and
     * compare it with target number and send
     * the compared result to client.
     *
     * @param guess the number that client guesses
     */

    private void guessGame(int guess) throws IOException {

        if (canPlay()) {
            Turns--;
            String sent = Constant.REGEX + guess + Constant.REGEX + Turns;
            if (Turns > 0) {
                if (guess > targetNum) {
                    // guess is larger than target
                    send("LAR" + sent);
                } else if (guess < targetNum) {
                    // guess is smaller than target
                    send("SML" + sent);
                } else {
                    // guess correctly
                    win = true;
                    send("CON" + sent);
                }
            } else {
                if (guess == targetNum) {
                    // guess correctly
                    win = true;
                    send("CON" + sent);
                } else {// not guess out message.
                    send("ER" + Constant.REGEX + targetNum + Constant.REGEX + Turns);
                }
            }
        }
        Constant.print("client", "info=[" + clientInfo + "] guessed [" + guess + "] " +
                "with remaining turns[" + Turns + "]", String.valueOf(targetNum));
    }

    /**
     * set the game to 'ready to start' status
     * by set Turns to 4 and boolean win to false.
     * Then print proper info of this action.
     */

    private void resetGame() {

        Turns = 4;
        win = false;
        targetNum = new Random().nextInt(10);
        Constant.print("client handler", "reset " +
                        "game at port<" + connect.getPort() + "> with target number",
                String.valueOf(targetNum));

    }

    /**
     * This method tells if the game is
     * at 'ready to start' status.
     *
     * @return true if it is ready. Otherwise false.
     */

    private boolean canPlay() {
        return !win && Turns >= 1;
    }

    /**
     * This method runs after receiving String PING
     * sending "SUCCESS" to client and print message
     * about this action.
     */

    private void ping() throws IOException {
        send("SUCCESS");

        Constant.print("client handler",
                "pings client at port <" + connect.getPort() + "> with message",
                "SUCCESS");
    }

    /**
     * This method send the String str to
     * the client.
     *
     * @param str the String that needs to be sent.
     */

    private void send(String str) throws IOException {
        toClient.println(str);
        Constant.print("client handler", "sends message to " +
                "client at port <" + connect.getPort() + "> with message", str);
    }


}
