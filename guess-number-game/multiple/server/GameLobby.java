
import java.util.*;

/**
 * this class manages
 * the game clients
 * it adds new client connection
 * into new client handlers and
 * check the game condition
 * and start the game over and over.
 *
 *
 */

public class GameLobby implements Runnable {

    private ArrayList<GameHandler> clients; // stores all client handlers

    private Thread MyThread; // the thread of this running on
    private ArrayList<GameHandler> scoreBoard; // stores game results


    /**
     * constructor
     * initialize the variables
     *
     */

    public GameLobby() {

        MyThread = new Thread(this);
        clients = new ArrayList<>();
        scoreBoard = new ArrayList<>();
        Constant.print("game lobby", "initializes", "finished");
    }

    private boolean isPlaying() {
        boolean k = false;
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getStatus().equals(GAME_STATUS.PLAYING)
                    ||
                    clients.get(i).getStatus().equals(GAME_STATUS.ENDING)) {
                k = true;
                break;
            }
        }
        return k;
    }

    /**
     *
     * to start this more
     * briefly
     */

    public void start() {
        MyThread.start();
        Constant.print("game lobby", "start running", "successfully");
    }

    /**
     * main method
     */
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Constant.err("Thread interrupted error.");
            }
            updateClients();
            checkStartGame();

        }
    }

    /**
     * kill inactive thread clients
     * update the status
     * and the position
     * of the clients
     *
     */

    private void updateClients() {

        eliminateNonActive();
        //
        putToLast(GAME_STATUS.LAST, GAME_STATUS.READY);
        putToLast(GAME_STATUS.PREP, GAME_STATUS.PREP);
        //      reverse();
    }

    /**
     * kill inactive thread clients
     *
     */

    private void eliminateNonActive() {

        boolean k = false;
        int before = clients.size();
        //Remove non-active ones.
        ArrayList<GameHandler> temp = new ArrayList<>();
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getThread().isAlive()) {
                temp.add(clients.get(i));
            } else {
                Constant.print("game lobby",
                        "detected [THREAD] of "
                                + clients.get(i).clientInfo() + "> status =",
                        "INACTIVE");
                k = true;
            }
        }
        clients = temp;
        int after = temp.size();
        if (k) {
            Constant.print("game lobby", "performs", "ELIMINATE INACTIVE GAME HANDLER(s)");
            Constant.print("game lobby", "eliminates [" + (before - after) + "] game handler(s)", "successfully");
            printClients();
        }
    }

    /**
     * put the client with
     * status1
     * to the last position
     * of the client list
     * and edit its status to status2
     * @param status1 origin status
     * @param status2 edited statis
     */

    private void putToLast(GAME_STATUS status1, GAME_STATUS status2) {
        GameHandler temp = null;
        boolean flag = false;
        boolean isPrep = status1.equals(GAME_STATUS.PREP);
        for (int i = 0; i < clients.size(); i++) {
            GameHandler gameHandler = clients.get(i);
            if (gameHandler.getStatus().equals(status1)) {
                temp = gameHandler;
                flag = true;

                temp.setStatus(status2);
                clients.remove(i);
                break;
            }
        }
        if (flag) {
             clients.add(temp);
        }
        if(!isPrep&&flag){
            Constant.print("game lobby", "puts  < " + temp.clientInfo() + " > to last position of", "client list");
            printClients();
        }
     }

    /**
     * check the game round
     * is available to start
     * if yes start the game
     */

    private void checkStartGame() {

        if (!isPlaying()) {
            if (scoreBoard.size() != 0) {
                informRank();
            }
            //calculate the needed data for decision
            ArrayList<String> names = new ArrayList<>();
            int k = clients.size();
            if (k > 3) {
                k = 3;
            }
            int count = 0;
            for (int i = 0; i < k; i++) {
                if (clients.get(i).getStatus().equals(GAME_STATUS.READY)) {
                    names.add(clients.get(i).getName());
                    count++;
                }
            }
            //determine if it is time to start game.
            if (count == k && count != 0) {

                String startNames = "";
                for (int i = 0; i < names.size(); i++) {
                    startNames = startNames + " < " + names.get(i) + " > ";
                }
                String content = "Game starts with [" + names.size() + "] players with these names" + startNames;

                int targetNum = new Random().nextInt(10);

                for (int i = 0; i < k; i++) {
                    clients.get(i).startGame(targetNum, content);
                    /////////////////////////////////////////////////////////////////////
                    scoreBoard.add(clients.get(i));
                    ///////////////////////////////////////////////////////////////////////
                    Constant.print("Game begins with [" + Constant.G(String.valueOf(names.size())) + "] players." + startNames +
                            " target Number = [" + Constant.G(String.valueOf(targetNum)) + "]");
                    printClients();
                }
            }
        }
    }

    /**
     * generate the rank info
     * and send it to clients
     *
     */

    private void informRank() {
        ArrayList<GameHandler> winners = new ArrayList<>();
        ArrayList<GameHandler> badLucks = new ArrayList<>();
        for (int i = 0; i < scoreBoard.size(); i++) {

            if (scoreBoard.get(i).wins()) {
                winners.add(scoreBoard.get(i));
            } else {
                badLucks.add(scoreBoard.get(i));
            }
        }
        Collections.sort(winners, new Comparator<GameHandler>() {
            @Override
            public int compare(GameHandler o1, GameHandler o2) {
                return o2.turns() - o1.turns();
            }
        });
        Collections.sort(badLucks, new Comparator<GameHandler>() {
            @Override
            public int compare(GameHandler o1, GameHandler o2) {
                return o2.turns() - o1.turns();
            }
        });
        String winRank = generateRank(winners);
        String lostRank = generateRank(badLucks);
        String Rank = "@" + Constant.B("================== GAME RESULT ===================") + "@" +
                Constant.Y("<Winners Rank>....................................") + "@" + winRank +
                Constant.Y("<None-winners Rank>...............................") + "@" + lostRank +
                Constant.B("================ END OF RESULT ===================") + "@";
        for (int i = 0; i < scoreBoard.size(); i++) {
            scoreBoard.get(i).informRank(Rank);
        }
        scoreBoard = new ArrayList<>();
    }

    /**
     * generate rank info message
     *
     * @param list contains the game participants info
     * @return final rank message
     */

    private String generateRank(List<GameHandler> list) {
        String winRank = "";
        for (int i = 0; i < list.size(); i++) {
            int k = i + 1;
            winRank = winRank +
                    "Rank <" + k + "> Name <" + list.get(i).getName() + "> " +
                    "Turns left <" + list.get(i).turns() + ">  win = [" + list.get(i).wins() + "]@";
        }
        if (list.size() == 0) {
            winRank = "None@";
        }
        return winRank;
    }

    /**
     * for display content of list
     * test use.
     * @param list the list to display
     */

    private void print(List<GameHandler> list) {
        System.out.println();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
        System.out.println();
    }

    /**
     * add game handlers to client list
     * @param gameHandler is the one to add to the client list
     */

    public void add(GameHandler gameHandler) {
        gameHandler.start();
        clients.add(gameHandler);
        Constant.print("game lobby", "added < " + gameHandler.toString() + " > to", "clients list");
        gameHandler.requestRegister();
        printClients();
    }

    /**
     * show a list of the client detailed info
     */

    private void printClients() {
        System.out.println("Inside client list:");
        System.out.println("\tCOUNTS  \tSTATUS" +
                "\tIP ADDRESS" +
                "\t\tPORT" +
                "\tTAR" +
                "\t\tTURN" +
                "\tALIVE" +
                "\t\tWINS" +
                "\t\tNAME" +
                "\t\tID" +
                "\t\t\t\t\tTHREAD INFO");
        if (clients.size() == 0) {
            System.out.println("   Nothing here right now.");
        } else {
            int k = 1;
            for (int i = 0; i < clients.size(); i++) {
                System.out.println(Constant.Y("\tcounts[" + k + "]" + clients.get(i).showInfo()));
                k++;
            }
        }
        System.out.println("End of client list.");
    }
}
