package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.parser.ast.decls.AliasDeclaration;
import org.ogu.lang.parser.ast.decls.Decorator;

import java.util.List;

/**
 * Declare an Alias. Syntax is alias <target> = <origin>.
 * In thid case target and origin are type names.
 * Created by ediaz on 22-01-16.
 */
public class TypeAliasDeclaration extends AliasDeclaration {

    private OguTypeIdentifier aliasOrigin;


    public TypeAliasDeclaration(OguTypeIdentifier target, OguTypeIdentifier origin, List<Decorator> decorators) {
        super(target, decorators);
        this.aliasOrigin = origin;
        this.aliasOrigin.setParent(this);
    }

    @Override
    public String toString() {
        return "TypeAliasDeclaration{" +
                "aliasTarget=" + name +
                ", aliasOrigin=" + aliasOrigin +
                ", decorators="+ decorators +
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(name).add(aliasOrigin).addAll(decorators).build();
    }
}
