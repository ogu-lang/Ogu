package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.parser.ast.decls.typedef.TypeParamNode;

import java.util.ArrayList;
import java.util.List;

/**
 * instance T x where ...
 * Created by ediaz on 25-01-16.
 */
public class InstanceDeclarationNode extends ContractDeclarationNode {

    private List<TypeParamNode> params;

    public InstanceDeclarationNode(TypeIdentifierNode name, List<TypeParamNode> params, List<FunctionalDeclarationNode> members, List<DecoratorNode> decoratorNodes) {
        super(name, members, decoratorNodes);
        this.params = new ArrayList<>();
        this.params.addAll(params);
        this.params.forEach((p) -> p.setParent(this));
    }



    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .addAll(params)
                .addAll(members)
                .addAll(decoratorNodes).build();
    }

    @Override
    public String toString() {
        return "InstanceDeclaration{" +
                "name='" + name + '\''+
                ", params=" + params+
                ", members=" + members +
                ", decorators=" + decoratorNodes +
                '}';
    }
}
