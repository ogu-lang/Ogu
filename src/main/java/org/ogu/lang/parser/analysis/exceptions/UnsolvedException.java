package org.ogu.lang.parser.analysis.exceptions;

/**
 * Created by ediaz on 10/30/16.
 */
public abstract class UnsolvedException extends RuntimeException {

    public UnsolvedException(String message) {
        super(message);
    }
}
