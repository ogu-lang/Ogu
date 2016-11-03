package org.ogu.lang.codegen.bytecode_generation.pushpop;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.ogu.lang.codegen.bytecode_generation.BytecodeSequence;

/**
 * Created by ediaz on 11/2/16.
 */
public class PushIntConst extends BytecodeSequence {

    private int value;
    private int operation;
    private boolean useLdcInsn;

    public PushIntConst(int value) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
            useLdcInsn = true;
        } else {
            useLdcInsn = false;
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                this.operation = Opcodes.SIPUSH;
            } else {
                this.operation = Opcodes.BIPUSH;
            }
        }
        this.value = value;
    }

    @Override
    public void operate(MethodVisitor mv) {
        if (useLdcInsn) {
            mv.visitLdcInsn(new Integer(value));
        } else {
            mv.visitIntInsn(operation, value);
        }
    }
}
