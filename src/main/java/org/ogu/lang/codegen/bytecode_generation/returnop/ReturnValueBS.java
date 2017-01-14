package org.ogu.lang.codegen.bytecode_generation.returnop;

import org.objectweb.asm.MethodVisitor;
import org.ogu.lang.codegen.bytecode_generation.BytecodeSequence;

/**
 * Created by ediaz on 1/14/17.
 */
public class ReturnValueBS extends BytecodeSequence {

    private int returnType;
    private BytecodeSequence pushValue;

    public ReturnValueBS(int returnType, BytecodeSequence pushValue) {
        this.returnType = returnType;
        this.pushValue = pushValue;
    }

    @Override
    public void operate(MethodVisitor mv) {
        pushValue.operate(mv);
        mv.visitInsn(returnType);
    }

}