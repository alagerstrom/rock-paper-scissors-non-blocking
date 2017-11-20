package main.java.com.andreas.rockpaperscissors.util;

public class Logger {
    private static final boolean LOG = true;
    public static synchronized void log(String message){
        if (LOG){
            System.out.println("[LOG] " + message);
        }

    }
}
