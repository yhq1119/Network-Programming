/**
 * This enum stores
 * the game status information
 * to tell the status of clients
 */



public enum GAME_STATUS {

    PLAYING, // means is playing
    PREP, //  means needs to register name
    READY, // means ready to start game , might be waiting
    ENDING, // means just finished game. before choose to replay
    LAST, // means to remind lobby to move it to last and set to ready.
    MOVED // this only used in move preparing ones to last.

}
