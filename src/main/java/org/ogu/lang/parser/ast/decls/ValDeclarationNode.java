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
public class ValDeclarationNode extends FunctionalDeclarationNode {

    protected ExpressionNode value;
    protected TypeNode type;

    protected ValDeclarationNode() {
        super(Collections.emptyList());
    }

    protected ValDeclarationNode(TypeNode type, ExpressionNode value, List<DecoratorNode> decoratorNodes) {
        super(decoratorNodes);
        this.type = type;
        this.type.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    protected ValDeclarationNode(ExpressionNode value, List<DecoratorNode> decoratorNodes) {
        super(decoratorNodes);
        this.value = value;
        this.value.setParent(this);
    }


    public ValDeclarationNode(IdentifierNode id, ExpressionNode value, List<DecoratorNode> decoratorNodes) {
        super(id, decoratorNodes);
        this.value = value;
        this.value.setParent(this);
    }

    public ValDeclarationNode(IdentifierNode id, TypeNode returnType, ExpressionNode value, List<DecoratorNode> decoratorNodes) {
        super(id, decoratorNodes);
        this.type = returnType;
        this.type.setParent(this);
        this.value = value;
        this.value.setParent(this);
    }

    @Override
    public Iterable<Node> getChildren() {
        if (type == null)
            return ImmutableList.<Node>builder().add(name).add(value).addAll(decoratorNodes).build();
        else
            return ImmutableList.<Node>builder().add(name).add(type).add(value).addAll(decoratorNodes).build();

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
