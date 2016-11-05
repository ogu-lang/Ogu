package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;

import java.util.List;

/**
 * Declare an Alias. Syntax is alias <target> = <origin>.
 * In thid case target and origin are type names.
 * Created by ediaz on 22-01-16.
 */
public class TypeAliasDeclarationNode extends  AliasDeclarationNode {

    private TypeIdentifierNode aliasOrigin;


    public TypeAliasDeclarationNode(TypeIdentifierNode target, TypeIdentifierNode origin, List<DecoratorNode> decoratorNodes) {
        super(target, decoratorNodes);
        this.aliasOrigin = origin;
        this.aliasOrigin.setParent(this);
    }

    @Override
    public String toString() {
        return "TypeAliasDeclaration{" +
                "aliasTarget=" + name +
                ", aliasOrigin=" + aliasOrigin +
                ", decorators="+ decoratorNodes +
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(name).add(aliasOrigin).addAll(decoratorNodes).build();
    }
}
