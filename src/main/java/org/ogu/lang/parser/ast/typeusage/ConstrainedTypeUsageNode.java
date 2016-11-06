package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * A type is an arg.
 * Example:
 *   data Tree a = Leaf a | Branch (Leaf a) (Leaf a)
 *   instance (a:Eq) => Eq (Tree a) where
 *
 * Here a is  ConstrainedTypeNode(a,Eq)
 * Created by ediaz on 04-02-16.
 */
public class ConstrainedTypeUsageNode extends TypeUsageWrapperNode {

    private IdentifierNode id;
    private TypeUsageWrapperNode type;

    public ConstrainedTypeUsageNode(IdentifierNode id, TypeUsageWrapperNode type) {
        this.id = id;
        this.id.setParent(this);
        this.type = type;
        this.type.setParent(this);
    }

    @Override
    public TypeUsageNode copy() {
        return null;
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


    @Override
    public boolean sameType(TypeUsage other) {
        return false;
    }


}
