package org.ogu.lang.parser.ast;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.codegen.jvm.JvmNameUtils;

/**
 * Created by ediaz on 21-01-16.
 */
public class ModuleNameDefinition extends Node {

    private String name;

    public ModuleNameDefinition(String name) {
        if (!JvmNameUtils.isValidQualifiedName(name)) {
            throw new IllegalArgumentException(name);
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of();
    }

    @Override
    public String toString() {
        return "ModuleNameDefinition{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleNameDefinition that = (ModuleNameDefinition) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
