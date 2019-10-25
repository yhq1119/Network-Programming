
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;


/**
 * This class implements most of the
 * functionality of the game client
 * by using thread.
 *
 *
 */



public class GameHandler implements Runnable {


    private GAME_STATUS status; // game status
    private String name; // player name
    private long id; // a generated id

    private Socket socket; // socket
    private PrintStream out; // output stream of the socket
    private BufferedReader in; // input stream of the socket
    private String address; // ip address of the socket
    private String port; // port number of the socket
    private Thread MyThread; // the thread of which this client running on
    private int targetNum; // the guessing number
    private int turns; // game turns left
    private boolean wins; // if game wins

    private boolean running; // keep loop alive
    private final String REGEX = Constant.REGEX; // a constant to form sending message
    private Timer timer; // use to test the connection using a timer
    private int socketTimeout = 1000*30; // setting timeout value
    private int connectionTestPeriod = 1000*10; // setting checking connection period value

    //constructor

    /**
     * Constructor
     *
     * @param socket is the passing in client socket
     */
    public GameHandler(Socket socket) {
        this.timer = new Timer();
        this.running = true;
        this.socket = socket;
        this.id = System.nanoTime();
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            Constant.print("Failed to get I/O stream from socket.");
        }
        this.status = GAME_STATUS.PREP;
        this.address = socket.getInetAddress().toString();
        this.port = String.valueOf(socket.getPort());
        this.MyThread = new Thread(this);
        Constant.print("game handler port=<" + port + ">", "created and initialized", "successfully");
    }

    /**
     * An inner class for test
     * the connection
     *
     */
    protected class Timer implements Runnable {

        Thread thisThread;

        private Timer() {
            this.thisThread = new Thread(this);
        }

        public void start() {
            this.thisThread.start();
        }

        @Override
        public void run() {
            try {
                socket.setKeepAlive(true);
                Constant.print("client handler","sets socket at","KEEP ALIVE MODE");
                socket.setSoTimeout(socketTimeout);
                Constant.print("client handler","sets socket time out = ",String.valueOf(socketTimeout));


                Constant.print("client handler port=<" + port + ">", "is checking", "connection");
                socket.sendUrgentData(0xFF); //
                Constant.print("client handler port=<" + port + ">", "check connection result = ", "ALIVE");
                new Thread().sleep(connectionTestPeriod);

            } catch (Exception e) {
                Constant.err(clientInfo() + " connection lost! Client handler exits.");
                informHint("Your connection has been closed due to long time no response.");
                running = false;
            }
        }
    }

    /**
     * can start this in
     * a very brief way
     *
     */

    public void start() {
        MyThread.start();
        timer.start();
        Constant.print("game handler port=<" + port + ">", "starts [Thread]", "successfully");
    }

    /**
     * Main method
     *
     */

    @Override
    public void run() {
        Constant.print("game handler port=<" + port + ">", "starts run()", "successfully");
        running = true;
        while (running) {
            try {
                action();
            } catch (SocketException e) {
                Constant.err(clientInfo() + " connection reset!");
                running = false;
                break;
            } catch (IOException e) {
                Constant.err(clientInfo() + " cannot read write to client socket!");
                informHint("Too long waiting for message. Connection closed.");
                running = false;
                break;
            }
        }
    }

    /**
     * This method
     * connects all
     * the functions of the client handler
     * It listens message from client
     * and responses by the game data.
     *
     * @throws IOException when lost I/O stream of the socket
     */

    private void action() throws IOException {

        String command = receive();
        if (command != null && command.length() > 0) {
            String[] args = command.split(Constant.REGEX);

            if (args[0].toUpperCase().equals("C")) {// to tell if it is client info
                switch (args[1].toUpperCase()) {
                    case "REGISTER":// register  name && be ready for game // tell name
                        register(args[2]);
                        break;
                    case "GUESS": // guess number // tell guess number // tell remaining turns
                        guess(args[2]);
                        break;
                    case "REPLAY": // after game, choose to replay
                        this.setStatus(GAME_STATUS.LAST);
                        // tell client to wait for score and game restart
                        informHint("Please wait for your game result and the restart of game.");
                        break;
                    case "EXIT": // quit guess // nothing else needed
                        exitGuess();
                        break;
                    case "QUIT": // quit game // nothing else needed
                        quitGame();
                        break;
                }
            }
        }
    }

    /**
     * This method is the core
     * method of the game.
     * It compares the passing in
     * number and gives prosper
     * response to the client
     *
     * @param args
     */


    private void guess(String args) {  // compare the number // minus turns // form result and other info // send info
        int guess = Integer.parseInt(args);
        String sent = Constant.S_INFORM_STATUS;
        //   String content = "";
       if (canPlay()) {
            turns--;

            if (turns > 0) {
                if (guess > targetNum) {
                    // guess is larger than target
                    send(sent + status() + REGEX + Constant.isBigger(guess, turns));
                } else if (guess < targetNum) {
                    // guess is smaller than target
                    send(sent + status() + REGEX + Constant.isSmaller(guess, turns));
                } else {
                    // guess correctly

                    wins = true;

                    send(sent + status() + REGEX + Constant.congrats(turns));
                }
            } else {
                if (guess == targetNum) {
                    // guess correctly
                    wins = true;
                    send(sent + status() + REGEX + Constant.congrats(turns));
                } else {// not guess out message.
                    send(sent + status() + REGEX + Constant.lost(targetNum, turns));
                }
            }
        }
        Constant.print("game handler port=<" + port + ">",
                "detected Name=[" + name + "] Id =[" + id + "] guessed [" + guess + "] " +
                        "with remaining turns[" + turns + "] target number =", String.valueOf(targetNum));
    }

    /**
     * update and return
     * current status
     *
     * @return current status
     */


    private GAME_STATUS status() {
        if (status.equals(GAME_STATUS.PLAYING)) {
            if (canPlay()) {
            } else {
                setStatus(GAME_STATUS.ENDING);
            }
        }
        return status;
    }

    /**
     * just returns
     * the status.
     *
     * @return
     */

    public GAME_STATUS getStatus() {
        return this.status;
    }

    public void setStatus(GAME_STATUS status) {
        this.status = status;
    }

    public void updateStatus() {
        if (getStatus().equals(GAME_STATUS.PLAYING)) {
            if (!canPlay()) {
                setStatus(GAME_STATUS.ENDING);
            }
        }

    }

    /**
     * Receives message from
     * the client by using
     * BufferedReader.
     *
     * @return received string
     * @throws IOException when lost the I/O stream
     */


    private String receive() throws IOException {
        Constant.print("game handler port=<" + port + ">", "begins to", "RECEIVE");
        String content = null;
        content = in.readLine();
        Constant.print("game handler port=<" + port + ">", "finished", "RECEIVE");
        Constant.print("game handler port=<" + port + ">", "received message", content);

        return content;
    }

    /**
     * send message
     * to client by using
     * PrintStream
     *
     * @param content the message that needs to be sent
     */

    private void send(String content) {
        Constant.print("game handler port=<" + port + ">", "begins to", "SEND");
        Constant.print("game handler port=<" + port + ">", "is sending message", content.replaceAll("@", "\n"));
        out.flush();
        out.println(content);
        out.flush();
        Constant.print("game handler port=<" + port + ">", "has", "SENT");

    }

    /**
     * setup game
     * and other parameters
     * and send the client
     * message to tell game
     * is started.
     *
     * @param targetNum the target number to guess
     * @param names the participants of the game.
     */

    public void startGame(int targetNum, String names) {
        this.targetNum = targetNum;
        this.turns = 4;
        this.wins = false;
        this.setStatus(GAME_STATUS.PLAYING);
        send(Constant.S_GUESS_GAME + status + REGEX + names);
    }


    /**
     * sending message
     * with rank info
     * header
     *
     * @param info
     */


    public void informRank(String info) {
        send(Constant.S_RANK_SCORE + info);
    }

    /**
     * inform the client
     * to register player name
     *
     */

    public void requestRegister() {
        send(Constant.S_REQUEST_REGISTER + "Connected to server port [" + port + "] and needs to register your name.");
    }

    /**
     * performs the register
     * action by edit name
     * and regenerate the id.
     *
     * @param name
     */

    private void register(String name) { // set name and // set status to ready
        this.name = name;
        fillName();
        this.id = System.nanoTime();
        setStatus(GAME_STATUS.LAST);
        informHint("You have been registered to game lobby. Please wait for game start.");
    }

    /**
     * for alignment use
     *
     */
    private void fillName() {
        if (name.length() <= 4) {
            name += "   ";
        }
    }

    /**
     * tells if
     * the game still
     * continues
     *
     * @return true if still can guess game
     */
    private boolean canPlay() {
        return !wins && turns >= 1;
    }

    /**
     * setting status
     * and inform client with
     * hints
     */

    private void exitGuess() { // set game status to end // and return to play again?
        setStatus(GAME_STATUS.ENDING);
        //       setScore();
        send(Constant.S_INFORM_STATUS +
                GAME_STATUS.ENDING + Constant.REGEX +
                "You enforced to end game. You lose and have [" + turns + "] turns left.");
    }

    /**
     * quit game
     * amd close the socket
     * and the thread
     *
     */

    private void quitGame() { // remove self from clients// and quit
        Constant.print("client",
                "with name[" + this.name + "] " +
                        "id[" + this.id + "] " +
                        "IP Address[" + this.address + "] " +
                        "Port[" + this.port + "] ", "QUIT.");
        try {
            socket.close();
            running = false;
        } catch (IOException e) {
            Constant.err("Error when " +
                    "client name[" + this.name + "] id[" + this.id + "] closing socket.");
        }
    }

    /**
     * a detailed info
     * string of the client
     *
     * @return info in the form of string
     */
    @Override
    public String toString() {
        return "{" +
                "status=" + status +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", address='" + address + '\'' +
                ", port='" + port + '\'' +
                ", MyThread=" + MyThread +
                ", targetNum=" + targetNum +
                ", turns=" + turns +
                ", wins=" + wins +
                '}';
    }

    /**
     *
     * @return brief info of the client
     */

    public String clientInfo() {
        return "[game handler] port=<" + port + "> Id=<" + id + "> Name=<" + name + ">";
    }

    public String showInfo() {


        return
                "\t" + status +
                        "\t" + address +
                        "\t" + port +
                        "\t" + targetNum +
                        "\t\t" + turns +
                        "\t\t" + MyThread.isAlive() +
                        "\t\t" + wins +
                        "\t\t" + name +
                        "\t\t" + id +
                        "\t\t" + MyThread;
    }

    /**
     *
     * getters and setters
     *
     */

    public Thread getThread() {
        return MyThread;
    }

    public String getName() {
        return name;
    }

    public boolean wins() {
        return wins;
    }

    public int turns() {
        return turns;
    }

    public void informHint(String str) {
        send(Constant.S_HINT_INFO + str);
    }
}
