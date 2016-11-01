package org.ogu.lang.codegen.bytecode_generation;


import org.objectweb.asm.MethodVisitor;

/**
 * Created by ediaz on 10/30/16.
 */
public abstract class BytecodeSequence {

    public abstract void operate(MethodVisitor mv);
}
