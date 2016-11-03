package org.ogu.lang.codegen.bytecode_generation.pushpop;

import org.objectweb.asm.MethodVisitor;
import org.ogu.lang.codegen.bytecode_generation.BytecodeSequence;

/**
 * Created by ediaz on 11/2/16.
 */
public class PushFloatConst extends BytecodeSequence {

    private float value;

    public PushFloatConst(float value) {
        this.value = value;
    }

    @Override
    public void operate(MethodVisitor mv) {
        mv.visitLdcInsn(value);
    }
}