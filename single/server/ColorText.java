

public class ColorText {

    /**
     * This class is to store constant color parameters.
     *
     */

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    public static String Black(String str){
        return ANSI_BLACK+str+ANSI_RESET;
    }

    public static String R(String str){
        return ANSI_RED+str+ANSI_RESET;
    }

    public static String G(String str){
        return ANSI_GREEN+str+ANSI_RESET;
    }

    public static String Y(String str){
        return ANSI_YELLOW+str+ANSI_RESET;
    }

    public static String B(String str){
        return ANSI_BLUE+str+ANSI_RESET;
    }

    public static String P(String str){
        return ANSI_PURPLE+str+ANSI_RESET;
    }

    public static String C(String str){
        return ANSI_CYAN+str+ANSI_RESET;
    }

    public static String W(String str){
        return ANSI_WHITE+str+ANSI_RESET;
    }

}
