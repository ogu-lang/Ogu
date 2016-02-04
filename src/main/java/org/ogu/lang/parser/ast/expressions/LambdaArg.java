package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.typeusage.TypeNode;

/**
 * Argument to a lambda expression
 * Created by ediaz on 01-02-16.
 */
public class LambdaArg extends Node {

    private IdentifierNode id;
    private TypeNode type;

    public LambdaArg(IdentifierNode id) {
        this.id = id;
        this.id.setParent(this);
    }

    public LambdaArg(IdentifierNode id, TypeNode type) {
        this.id = id;
        this.id.setParent(this);
        this.type = type;
        this.type.setParent(this);
    }

    @Override
    public String toString() {
        if (type == null)
            return "LambdaArg{" + id + '}';
        else
            return "LambdaArg{" + id + ':' + type + '}';
    }


    @Override
    public Iterable<Node> getChildren() {
        if (type == null)
            return ImmutableList.<Node>builder().add(id).build();
        else
            return ImmutableList.<Node>builder().add(id).add(type).build();
    }

}
