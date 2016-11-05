package org.ogu.lang.util;

/**
 * Shows messages in stdout to user
 * Created by ediaz on 24-01-16.
 */
public class Feedback {


    public static void message(String msg, Object ... args) {

        message(String.format(msg, args));
    }

    public static void error(String msg) {
        System.err.println(msg);
    }

    public static void message(String msg) {
        System.out.println(msg);
    }

    public static void akarru() {
        System.out.println("       _");
        System.out.println( "  __ _| | ____ _ _ __ _ __ _   _");
        System.out.println(" / _` | |/ / _` | '__| '__| | | |");
        System.out.println( "| (_| |   < (_| | |  | |  | |_| |");
        System.out.println( " \\__,_|_|\\_\\__,_|_|  |_|   \\__,_|");

    }
}
