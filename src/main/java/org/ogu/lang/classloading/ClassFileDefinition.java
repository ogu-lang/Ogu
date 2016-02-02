package org.ogu.lang.classloading;

/**
 * Final .class output is defined here
 * Created by ediaz on 20-01-16.
 */
public class ClassFileDefinition {

    private String name;
    private byte[] bytecode;

    public String getName() {
        return name;
    }

    public byte[] getBytecode() {
        return bytecode;
    }

    public ClassFileDefinition(String name, byte[] bytecode) {
        if (name.contains("/")) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.bytecode = bytecode;
    }
}