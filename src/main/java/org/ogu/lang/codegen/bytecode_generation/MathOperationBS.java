package org.ogu.lang.codegen.bytecode_generation;

import org.objectweb.asm.MethodVisitor;

/**
 * Created by ediaz on 11/2/16.
 */
public class MathOperationBS extends BytecodeSequence {

    private int opcode;

    public MathOperationBS(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public void operate(MethodVisitor mv) {
        mv.visitInsn(this.opcode);
    }
}