package org.ogu.lang.parser.ast.decls.typedef;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.typeusage.OguType;


/**
 * Param in a class declaration
 * class ID(param:T)
 * Created by ediaz on 25-01-16.
 */
public class ClassParam extends Node {

    private String id;
    private OguType type;

    public ClassParam(String id, OguType type) {
        this.id = id;
        this.type = type;
        this.type.setParent(this);
    }


    @Override
    public String toString() {
        return "ClassParam {"+
                "id="+id +
                ", type="+type+
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(type);
    }
}
