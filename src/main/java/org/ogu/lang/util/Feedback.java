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
        System.out.println(msg);;
    }
}
