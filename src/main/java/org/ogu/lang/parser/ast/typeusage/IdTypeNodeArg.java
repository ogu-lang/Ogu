package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;

/**
 * An id used as parameter for a type
 * Created by ediaz on 23-01-16.
 */
public  class IdTypeNodeArg extends TypeNode {

    private IdentifierNode id;


    public IdTypeNodeArg(IdentifierNode id) {
        super();
        this.id = id;
        this.id.setParent(this);
    }


    @Override
    public String toString() {
        return "TypeArg{"+id+'}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(id);
    }
}
