package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.parser.ast.typeusage.TypeUsageWrapperNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A val declaration (val id = value) where value is an expression
 * Created by ediaz on 23-01-16.
 */
public class TupleVarDeclarationNode extends VarDeclarationNode {

    private List<IdentifierNode> ids;
    private Map<IdentifierNode, TypeUsageWrapperNode> types;

    public TupleVarDeclarationNode(List<IdentifierNode> ids, Map<IdentifierNode, TypeUsageWrapperNode> types, ExpressionNode value, List<DecoratorNode> decoratorNodes) {
        super(value, decoratorNodes);
        this.ids = new ArrayList<>();
        this.ids.addAll(ids);
        this.ids.forEach((i) -> i.setParent(this));
        this.types = new HashMap<>();
        this.types.putAll(types);
        this.types.keySet().forEach((k) -> k.setParent(this));
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(ids).addAll(types.keySet()).addAll(types.values()).add(value).addAll(decoratorNodes).build();
    }

    @Override
    public String toString() {
        return "TupleVarDeclaration{" +
                "ids='" + ids + '\''+
                ", types="+types+
                ", value=" + value +
                ", decorators" + decoratorNodes +
                '}';
    }

}
