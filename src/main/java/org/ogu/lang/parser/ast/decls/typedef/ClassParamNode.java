package org.ogu.lang.parser.ast.decls.typedef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.typeusage.TypeNode;


/**
 * Param in a class declaration
 * class ID(param:T)
 * Created by ediaz on 25-01-16.
 */
public class ClassParamNode extends Node {

    private IdentifierNode id;
    private TypeNode type;

    public ClassParamNode(IdentifierNode id, TypeNode type) {
        this.id = id;
        this.id.setParent(this);
        this.type = type;
        this.type.setParent(this);
    }


    @Override
    public String toString() {
        return "ClassParam {" +
                "id=" + id +
                ", type=" + type +
                '}';
    }


    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder()
                .add(id)
                .add(type).build();
    }
}
