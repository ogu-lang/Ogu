package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguIdentifier;

/**
 * A common alias (for vals, defs, lets) not Types
 * Created by ediaz on 22-01-16.
 */
public class IdAliasDeclaration extends AliasDeclaration {

    private OguIdentifier aliasTarget;
    private OguIdentifier aliasOrigin;


    public IdAliasDeclaration(OguIdentifier target, OguIdentifier origin) {
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
