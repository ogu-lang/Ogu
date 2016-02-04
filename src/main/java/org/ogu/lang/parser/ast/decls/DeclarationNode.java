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

    protected List<DecoratorNode> decoratorNodes;

    protected DeclarationNode(List<DecoratorNode> decoratorNodes) {
        this.decoratorNodes = new ArrayList<>();
        this.decoratorNodes.addAll(decoratorNodes);
        this.decoratorNodes.forEach((p) -> p.setParent(this));
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .addAll(decoratorNodes).build();
    }

    @Override
    public String toString() {
        return "Declaration{" +
                "decorators='" + decoratorNodes + '\''+
                '}';
    }
}
