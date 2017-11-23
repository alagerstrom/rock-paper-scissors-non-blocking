package com.andreas.nonblockingrps.util;

public class Logger {
    private static final boolean LOG = true;
    public static synchronized void log(String message){
        if (LOG){
            System.out.println("[LOG " + Thread.currentThread().getName() +"] " + message);
        }

    }
}
