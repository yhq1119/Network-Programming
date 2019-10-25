package MClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * this is the game client
 * handler
 * it receives and sends
 * game data and represent
 * the game data to players.
 *
 *
 */


public class MGameHandler implements Runnable {

    private boolean running;
    private final Socket clientSocket;
    private Thread myThread;

    public MGameHandler(Socket clientSocket) {
        this.myThread = new Thread(this);
        this.clientSocket = clientSocket;
  }

    public void start() {
        this.myThread.start();
    }

    @Override
    public void run() {
        running = true;
        init();
        while (running) {
            try {
                action();
            } catch (InterruptedException e) {
                Constant.err("Thread error.");
                running = false;
                return;
            } catch (SocketException e) {
                Constant.err("Connection lost.");
                running = false;
                return;
            } catch (IOException e) {
                Constant.err("Invalid socket.");
                running = false;
                return;
            }
        }
        Constant.print("Client", "", "QUIT");
    }

    void init() {

    }

    private void action() throws InterruptedException, IOException {
        String command = null;

        try {
            command = receive();
        } catch (NullPointerException e) {
            Constant.err("Null pointer error when receive from <client socket>!");
            running = false;
            return;
        }

        if (command == null || command.length() < 1) {
            Constant.err("Handler received message as[" + command + "]");
        } else {
            String[] args = command.split(Constant.REGEX);
            if (args[0].toUpperCase().equals("S")) {
                String sent;
                switch (args[1].toUpperCase()) {
                    case "REGISTER":
                        System.out.println(Constant.C(args[2]));
                        sent = Constant.C_REGISTER_INFO + input("Please register your name to server:");
                        send(sent);
                        break;
                    case "INFORM":
                        readStatusAction(args[2], args[3]);
                        break;
                    case "INFO":
                        System.out.println(Constant.C(args[2].replaceAll("@", "\n")));
                        break;
                    case "RANK":
                        System.out.println(args[2].replaceAll("@", "\n"));
                        break;
                    case "GUESS":
                        printResult(args[1], args[2]);
                        break;
                    case "WAITING":
                        System.out.println(Constant.C(args[1]));
                        break;
                    case "PING":
                        Constant.print("client", " received ping message", args[2]);
                        send(Constant.C_PING_TIME + args[2]);
                        break;
                    default:
                        Constant.err("Not implemented function at case [" + args[1] + "]");
                        break;
                }
            } else {
                Constant.err("Unknown format data [" + command + "]");
            }
        }
    }

    private void printResult(String status, String content) throws InterruptedException, IOException {

        System.out.println(Constant.Y(content));
        if (status.toUpperCase().equals("PLAYING")) {
            inputGuess();
        } else if (status.toUpperCase().equals("END")) {

        }
    }


    private void readStatusAction(String status, String content) throws InterruptedException, IOException {

        switch (status.toUpperCase()) {
            case "WAITING":
                System.out.println(Constant.C(content.replaceAll("@", "\n")));
                break;
            case "READY":
                // Constant.print("READY", "", content);
            case "PLAYING":
                //   Constant.print("PLAYING", "", content);
                System.out.println(Constant.C(content));
                inputGuess();
                break;
            case "ENDING":
                toEnd(content);
                break;
            default:
                System.out.println(status);
                System.out.println(content);
                break;
        }
    }

    private void toEnd(String content) throws IOException {
        Constant.print("END", "", content);
        System.out.println(Constant.C(content));

        String retry;
        boolean k;
        do {
            retry = input("Please input <p> to play again or <q> to quit.");
            k = !retry.toUpperCase().equals("Q") && !retry.toUpperCase().equals("P");
            if (k){
                Constant.err("Please choose <p> or <q> to continue!");
            }
        } while (k);

        if (retry.toUpperCase().equals("P")) {
            send(Constant.C_REPLAY_GAME);
        }
        if (retry.toUpperCase().equals("Q")) {
            send(Constant.C_QUITS_GAME);
            System.exit(1);
        }
    }

    private void inputGuess() throws IOException {

        String hint = "Please input <an Integer> to play or <E/Exit> to quit:";

        String guess;
        boolean isExit;
        int k = -1;
        do {
            guess = input(hint);
            isExit = guess.toUpperCase().equals("E") || guess.toUpperCase().equals("EXIT");
            if (isExit) {
                send(Constant.C_EXIT_GUESS);
                return;
            }
            try {
                k = Integer.parseInt(guess);
                if (k < 0 || k > 9) {
                    Constant.err("Should be from 0 to 9!");
                }
            } catch (NumberFormatException e) {
                Constant.err("Please input an integer from 0 to 9!");
            }
        } while (k < 0 || k > 9);
        send(Constant.C_GUESS_NUMBER + guess);
    }

    private String input(String hint) {

        String s = null;
        while (s == null) {
            Scanner sc = new Scanner(System.in);

            System.out.println(Constant.Y(hint));
            s = sc.nextLine();
            if (s == null || s.length() < 1) {
                Constant.err("Input cannot be empty!");
            }
//            sc.close();
        }
        return s;
    }

    private String receive() throws IOException {
        Constant.print("IN RECEIVE", "", "START TO LISTEN");
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String s = in.readLine();
        Constant.print("READ", "message =", s);
        return s;
    }

    private void send(String content) throws IOException {
        Constant.print("IN SEND", "", "START TO SEND");
        PrintStream out = new PrintStream(clientSocket.getOutputStream());
        out.println(content);
        out.flush();
        Constant.print("SENT", "message =", content);
    }

    private String receiver() throws IOException {
        Constant.print("RECEIVER", "", "LISTEN");
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String s = null;
        StringBuilder builder = new StringBuilder();
        while ((s = in.readLine()) != null) {
            builder.append(s);
        }
        Constant.print("READ", "message =", builder.toString());
        return builder.toString();
    }

}
