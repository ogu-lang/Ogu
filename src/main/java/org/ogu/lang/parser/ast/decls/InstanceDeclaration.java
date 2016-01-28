package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.parser.ast.decls.typedef.TypeParam;

import java.util.ArrayList;
import java.util.List;

/**
 * instance T x where ...
 * Created by ediaz on 25-01-16.
 */
public class InstanceDeclaration extends ContractDeclaration {

    private List<TypeParam> params;

    public InstanceDeclaration(OguTypeIdentifier name, List<TypeParam> params, List<FunctionalDeclaration> members, List<Decorator> decorators) {
        super(name, members, decorators);
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
        return "InstanceDeclaration{" +
                "name='" + name + '\''+
                ", params=" + params+
                ", members=" + members +
                ", decorators=" + decorators +
                '}';
    }
}
