package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.IdentifierNode;

/**
 * An id used as parameter for a type
 * Created by ediaz on 23-01-16.
 */
public  class IdTypeArg extends OguType {

    private IdentifierNode id;


    public IdTypeArg(IdentifierNode id) {
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
