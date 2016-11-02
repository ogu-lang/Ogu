package org.ogu.lang.codegen.bytecode_generation.pushpop;

import org.objectweb.asm.MethodVisitor;
import org.ogu.lang.codegen.bytecode_generation.BytecodeSequence;

/**
 * Created by ediaz on 11/2/16.
 */
public class PushLocalVar extends BytecodeSequence {

    private int loadType;
    private int index;

    public PushLocalVar(int loadType, int index) {
        this.loadType = loadType;
        this.index = index;
    }

    @Override
    public void operate(MethodVisitor mv) {
        mv.visitVarInsn(loadType, index);
    }
}
