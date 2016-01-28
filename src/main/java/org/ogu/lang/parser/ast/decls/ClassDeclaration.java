package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.parser.ast.decls.typedef.ClassParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ediaz on 25-01-16.
 */
public class ClassDeclaration extends ContractDeclaration {

    private List<ClassParam> clasParams;
    private boolean isMutable;

    public ClassDeclaration(OguTypeIdentifier name, List<ClassParam> classParams, boolean isMutable, List<FunctionalDeclaration> members, List<Decorator> decorators) {
        super(name, members, decorators);
        this.isMutable = isMutable;
        this.clasParams = new ArrayList<>();
        this.clasParams = classParams;
        this.clasParams.forEach((p) -> p.setParent(this));
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(name)
                .addAll(clasParams)
                .addAll(members)
                .addAll(decorators).build();
    }

    @Override
    public String toString() {
        return "ClassDeclaration{" +
                "name='" + name + '\''+
                ", isMutable=" + isMutable +
                ", params=" + clasParams+
                ", members=" + members +
                ", decorators=" + decorators +
                '}';
    }

}


