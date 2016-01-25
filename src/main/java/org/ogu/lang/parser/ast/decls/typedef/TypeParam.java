package org.ogu.lang.parser.ast.decls.typedef;

import org.ogu.lang.parser.ast.Node;

import java.util.Collections;

/**
 * Used in generic types
 * Created by ediaz on 24-01-16.
 */
public class TypeParam extends Node {

    protected String id;

    public TypeParam(String id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "TypeParam {"+
                "id="+id +
                '}';
    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }
}
