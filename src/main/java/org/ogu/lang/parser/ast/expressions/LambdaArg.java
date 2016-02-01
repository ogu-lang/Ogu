package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;
import org.ogu.lang.parser.ast.typeusage.OguType;

/**
 * Argument to a lambda expression
 * Created by ediaz on 01-02-16.
 */
public class LambdaArg extends Node {

    private OguIdentifier id;
    private OguType type;

    public LambdaArg(OguIdentifier id) {
        this.id = id;
        this.id.setParent(this);
    }

    public LambdaArg(OguIdentifier id, OguType type) {
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
