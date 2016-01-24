package org.ogu.lang.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * I18N messages
 * Created by ediaz on 23-01-16.
 */
public class Messages {

    static final Locale currentLocale = Locale.getDefault();
    static final ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", currentLocale, new UTF8Control());

    public static String message(String msgKey) {
        return messages.getString(msgKey);
    }
}
