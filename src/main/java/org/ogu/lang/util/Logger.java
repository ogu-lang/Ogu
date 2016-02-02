package org.ogu.lang.util;

import org.ogu.lang.compiler.Options;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;

/**
 * Created by ediaz on 23-01-16.
 */
public class Logger {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Ogu");

    public static void debug(String message) {
        logger.log(Level.FINEST, message);
    }

    public static void configure(Options options) throws IOException {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);

        if (options.isDebug())
            logger.setLevel(Level.FINEST);
    }
}

