package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;

/**
 * Any Declaration
 * Created by ediaz on 23-01-16.
 */
public abstract class Declaration extends Node {

    protected OguIdentifier id;

    protected Declaration(OguIdentifier id) {
        this.id = id;
        id.setParent(this);
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(id);
    }

    @Override
    public String toString() {
        return "Declaration{" +
                "id='" + id + '\''+
                '}';
    }
}
