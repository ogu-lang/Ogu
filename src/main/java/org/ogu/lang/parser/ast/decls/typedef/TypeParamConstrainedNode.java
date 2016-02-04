package org.ogu.lang.parser.ast.decls.typedef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.typeusage.TypeNode;


/**
 * A constraint of type
 *       type (n:Ord) => List n = ...
 * Created by ediaz on 24-01-16.
 */
public class TypeParamConstrainedNode extends TypeParamNode {

    private TypeNode constraint;

    public TypeParamConstrainedNode(String id, TypeNode constraint) {
        super(id);
        this.constraint = constraint;
        this.constraint.setParent(this);
    }

    @Override
    public String toString() {
        return "TypeParamConstrained {" +
                "id="+id +
                "constraint="+constraint+
                '}';
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(constraint);
    }
}
