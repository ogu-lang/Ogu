package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Any Declaration
 * Created by ediaz on 23-01-16.
 */
public abstract class DeclarationNode extends Node {

    protected List<Decorator> decorators;

    protected DeclarationNode(List<Decorator> decorators) {
        this.decorators = new ArrayList<>();
        this.decorators.addAll(decorators);
        this.decorators.forEach((p) -> p.setParent(this));
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .addAll(decorators).build();
    }

    @Override
    public String toString() {
        return "Declaration{" +
                "decorators='" +decorators + '\''+
                '}';
    }
}
