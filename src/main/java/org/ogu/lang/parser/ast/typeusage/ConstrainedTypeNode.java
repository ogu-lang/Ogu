package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.typedef.TypeParamConstrainedNode;

/**
 * A type is an arg.
 * Example:
 *   data Tree a = Leaf a | Branch (Leaf a) (Leaf a)
 *   instance (a:Eq) => Eq (Tree a) where
 *
 * Here a is  ConstrainedTypeNode(a,Eq)
 * Created by ediaz on 04-02-16.
 */
public class ConstrainedTypeNode extends TypeNode {

    private IdentifierNode id;
    private TypeNode type;

    public ConstrainedTypeNode(IdentifierNode id, TypeNode type) {
        this.id = id;
        this.id.setParent(this);
        this.type = type;
        this.type.setParent(this);
    }


    @Override
    public String toString() {
        return "Constraint ("+id+":"+type+")";
    }

    @Override
    public String getName() {
        return id.getName();
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().add(id).add(type).build();
    }

}
