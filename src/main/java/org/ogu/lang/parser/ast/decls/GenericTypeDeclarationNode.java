package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.parser.ast.decls.typedef.TypeParamNode;
import org.ogu.lang.parser.ast.typeusage.TypeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * A type declared like
 *     type T a b c = ...
 * (Useful to restrict a data)
 * Created by ediaz on 24-01-16.
 */
public class GenericTypeDeclarationNode extends TypedefDeclarationNode {

    protected List<TypeParamNode> params;

    public GenericTypeDeclarationNode(TypeIdentifierNode name, List<TypeParamNode> params, TypeNode type, List<DecoratorNode> decoratorNodes) {
        super(name, type, decoratorNodes);
        this.params = new ArrayList<>();
        this.params.addAll(params);
        this.params.forEach((p) -> p.setParent(this));
    }



    @Override
    public String toString() {
        return "GenericTypeDeclaration{"+
                "name="+name+
                ", type="+type+
                ", params="+params+
                ", decorators="+ decoratorNodes +
                '}';
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .add(type)
                .addAll(params)
                .addAll(decoratorNodes).build();
    }
}
