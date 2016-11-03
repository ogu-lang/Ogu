package org.ogu.lang.codegen.bytecode_generation;

import org.objectweb.asm.MethodVisitor;

/**
 * Created by ediaz on 10/31/16.
 */
public class NoOp extends BytecodeSequence {
    private static final NoOp INSTANCE = new NoOp();

    private NoOp() {
        // prevent instantiation outside class
    }

    @Override
    public void operate(MethodVisitor mv) {
        // nothing to do
    }

    public static BytecodeSequence getInstance() {
        return INSTANCE;
    }
}
