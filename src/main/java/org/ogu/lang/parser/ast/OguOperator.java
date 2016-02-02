package org.ogu.lang.parser.ast;

import java.util.Collections;

/**
 * An op (*, +. -, /, etc...)
 * Created by ediaz on 26-01-16.
 */
public class OguOperator extends OguName {


    public OguOperator(String name) {
        super(name);
    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }


}
