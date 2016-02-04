package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.TypeIdentifierNode;
import org.ogu.lang.parser.ast.decls.typedef.TypeParam;

import java.util.ArrayList;
import java.util.List;

/**
 * A trait
 * Created by ediaz on 25-01-16.
 */
public class TraitDeclaration extends ContractDeclaration {

    private List<TypeParam> params;
    private boolean isMutable;

    public TraitDeclaration(TypeIdentifierNode name, boolean isMutable, List<TypeParam> params, List<FunctionalDeclaration> members, List<Decorator> decorators) {
        super(name, members, decorators);
        this.isMutable = isMutable;
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
                .addAll(decorators).build();
    }

    @Override
    public String toString() {
        return "TraiDeclaration{" +
                "name='" + name + '\'' +
                ", isMutable=" + isMutable +
                ", params=" + params +
                ", members=" + members +
                ", decorators=" + decorators +
                '}';
    }


}
