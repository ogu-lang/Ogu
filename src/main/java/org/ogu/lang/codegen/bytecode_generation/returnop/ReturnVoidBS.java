package org.ogu.lang.codegen.bytecode_generation.returnop;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.ogu.lang.codegen.bytecode_generation.BytecodeSequence;

/**
 * Created by ediaz on 11/5/16.
 */
public class ReturnVoidBS extends BytecodeSequence {

    @Override
    public void operate(MethodVisitor mv) {
        mv.visitInsn(Opcodes.RETURN);
    }
}
