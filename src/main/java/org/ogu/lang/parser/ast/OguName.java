package org.ogu.lang.parser.ast;

import com.google.common.collect.ImmutableList;

import java.util.Collections;

/**
 * An Identifier in Ogú.
 * There are 2: Type Identifier and Simple Identifier (vars, vals and funcs).
 * Created by ediaz on 22-01-16.
 */
public class OguName extends Node {

    private OguName base;
    private String name;

    public String getName() {
        return name;
    }


    public OguName(OguName base, String name) {
        this.base = base;
        this.name = name;
    }

    public OguName(String name) {
        this.name = name;
    }

    public boolean isSimpleName() {
        return base == null;
    }

    @Override
    public Iterable<Node> getChildren() {
        if (base == null) {
            return Collections.emptyList();
        } else {
            return ImmutableList.of(base);
        }
    }

    public String qualifiedName() {
        if (base == null) {
            return name;
        } else {
            return base + "." + name;
        }
    }

    @Override
    public String toString() {
        return qualifiedName();
    }


}
