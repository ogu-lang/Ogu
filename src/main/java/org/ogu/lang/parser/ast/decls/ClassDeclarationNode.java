package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.parser.ast.decls.typedef.ClassParamNode;
import org.ogu.lang.parser.ast.decls.typedef.TypeParamNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ediaz on 25-01-16.
 */
public class ClassDeclarationNode extends ContractDeclarationNode {

    private List<TypeParamNode> genParams;
    private List<ClassParamNode> clasParams;
    private boolean isMutable;

    public ClassDeclarationNode(TypeIdentifierNode name, boolean isMutable, List<TypeParamNode> genParams, List<ClassParamNode> classParamNodes, List<FunctionalDeclarationNode> members, List<DecoratorNode> decoratorNodes) {
        super(name, members, decoratorNodes);
        this.isMutable = isMutable;
        this.genParams = new ArrayList<>();
        this.genParams.addAll(genParams);
        this.genParams.forEach((p) -> p.setParent(this));
        this.clasParams = new ArrayList<>();
        this.clasParams = classParamNodes;
        this.clasParams.forEach((p) -> p.setParent(this));
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .addAll(genParams)
                .addAll(clasParams)
                .addAll(members)
                .addAll(decoratorNodes).build();
    }

    @Override
    public String toString() {
        return "ClassDeclaration{" +
                "name='" + name + '\''+
                ", isMutable=" + isMutable +
                ", genParams=" + genParams+
                ", params=" + clasParams+
                ", members=" + members +
                ", decorators=" + decoratorNodes +
                '}';
    }

}


