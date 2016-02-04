package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.Node;

import java.util.Collections;

/**
 * #{extern "lang" "signature}
 * Allows integration with other languages
 * Created by ediaz on 23-01-16.
 */
public class EntryPointDecoratorNode extends DecoratorNode {

    public EntryPointDecoratorNode() {
        super(Collections.emptyList());
    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "EntryPointDecorator{}";
    }
}
