package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorator over declarations
 * Created by ediaz on 23-01-16.
 */
public abstract class DecoratorNode extends Node {

    protected List<String> args;

    protected DecoratorNode(List<String> args) {
        this.args = new ArrayList<>();
        this.args.addAll(args);
    }

    @Override
    public String toString() {
        return "Decorator{"+
                "args="+args+
                '}';
    }
}
