package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.parser.ast.typeusage.TypeNode;

import java.util.Collections;
import java.util.List;

/**
 * A val declaration (val id = value) where value is an expression
 * Created by ediaz on 23-01-16.
 */
public class VarDeclarationNode extends FunctionalDeclarationNode {

    protected ExpressionNode value;
    protected TypeNode type;

    protected VarDeclarationNode() {
        super(Collections.emptyList());
    }

    protected VarDeclarationNode(TypeNode type, ExpressionNode value, List<DecoratorNode> decoratorNodes) {
        super(decoratorNodes);
        this.type = type;
        this.type.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    protected VarDeclarationNode(ExpressionNode value, List<DecoratorNode> decoratorNodes) {
        super(decoratorNodes);
        this.value = value;
        this.value.setParent(this);
    }


    public VarDeclarationNode(IdentifierNode id, ExpressionNode value, List<DecoratorNode> decoratorNodes) {
        super(id, decoratorNodes);
        this.value = value;
        this.value.setParent(this);
    }

    public VarDeclarationNode(IdentifierNode id, TypeNode type, List<DecoratorNode> decoratorNodes) {
        super(id, decoratorNodes);
        this.type = type;
        this.type.setParent(this);
    }

    public VarDeclarationNode(IdentifierNode id, TypeNode type, ExpressionNode value, List<DecoratorNode> decoratorNodes) {
        super(id, decoratorNodes);
        this.type = type;
        this.type.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    @Override
    public Iterable<Node> getChildren() {
        if (type == null) {
            return ImmutableList.<Node>builder().add(name).add(value).addAll(decoratorNodes).build();
        } else {
            if (value == null)
                return ImmutableList.<Node>builder().add(name).add(type).addAll(decoratorNodes).build();

            else
                return ImmutableList.<Node>builder().add(name).add(value).add(type).addAll(decoratorNodes).build();
        }
    }

    @Override
    public String toString() {
        return "ValDeclaration{" +
                "id='" + name + '\''+
                ", type="+type+
                ", value=" + value +
                ", decorators" + decoratorNodes +
                '}';
    }
}
