package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguName;
import org.ogu.lang.parser.ast.OguTypeIdentifier;
import org.ogu.lang.parser.ast.QualifiedName;

/**
 * Declare an Alias. Syntax is alias <target> = <origin>.
 * In thid case target and origin are type names.
 * Created by ediaz on 22-01-16.
 */
public class TypeAliasDeclaration extends AliasDeclaration {

    //

    private OguTypeIdentifier aliasTarget;
    private OguTypeIdentifier aliasOrigin;


    public TypeAliasDeclaration(OguTypeIdentifier target, OguTypeIdentifier origin) {
        this.aliasTarget = target;
        this.aliasTarget.setParent(this);
        this.aliasOrigin = origin;
        this.aliasOrigin.setParent(this);
    }

    @Override
    public String toString() {
        return "TypeAliasDeclaration{" +
                "aliasTarget=" + aliasTarget +
                ", aliasOrigin=" + aliasOrigin +
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(aliasTarget).add(aliasOrigin).build();
    }
}
