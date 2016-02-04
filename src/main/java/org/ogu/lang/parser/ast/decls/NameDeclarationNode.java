package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.NameNode;
import java.util.List;

/**
 * Any Declaration
 * Created by ediaz on 23-01-16.
 */
public abstract class NameDeclarationNode extends DeclarationNode {

    protected NameNode name;

    protected NameDeclarationNode(List<DecoratorNode> decoratorNodes) {
        super(decoratorNodes);
    }

    protected NameDeclarationNode(NameNode name, List<DecoratorNode> decoratorNodes) {
        super(decoratorNodes);
        this.name = name;
        this.name.setParent(this);
    }

    public String getName() {
        if (name == null)
            return null;
        return name.getName();
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .addAll(decoratorNodes).build();
    }

    @Override
    public String toString() {
        return "Declaration{" +
                "name='" + name + '\''+
                ", decorators=" + decoratorNodes +
                '}';
    }
}
