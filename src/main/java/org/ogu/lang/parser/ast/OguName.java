package org.ogu.lang.parser.ast;

import com.google.common.collect.ImmutableList;

import java.util.Collections;

/**
 * An Identifier in Ogú.
 * There are 2: Type Identifier and Simple Identifier (vars, vals and funcs).
 * Created by ediaz on 22-01-16.
 */
public class OguName extends Node {

    protected OguName base;
    protected String name;

    public String getName() {
        return name;
    }


    public OguName(OguName base, String name) {
        super();
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


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        OguIdentifier that = (OguIdentifier) o;

        return base != null ? base.equals(that.base) : that.base == null && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = base != null ? base.hashCode() : 0;
        result = 31 * result + name.hashCode();
        return result;
    }
}
