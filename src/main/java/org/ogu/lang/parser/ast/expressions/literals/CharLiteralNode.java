package org.ogu.lang.parser.ast.expressions.literals;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.Collections;

/**
 * Char literal (TODO: parse escape sequcence)
 * Created by ediaz on 27-01-16.
 */
public class CharLiteralNode extends ExpressionNode {

    String value; //TODO parse and use char instead

    public CharLiteralNode(String value) {
        // TODO PARSE
        super();
        this.value = value;
    }


    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }
}
