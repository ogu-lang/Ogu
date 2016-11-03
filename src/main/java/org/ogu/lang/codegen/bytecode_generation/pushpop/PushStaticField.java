package org.ogu.lang.codegen.bytecode_generation.pushpop;

import org.objectweb.asm.MethodVisitor;
import org.ogu.lang.codegen.bytecode_generation.BytecodeSequence;
import org.ogu.lang.codegen.jvm.JvmFieldDefinition;
import static org.objectweb.asm.Opcodes.*;


/**
 * Created by ediaz on 11/1/16.
 */
public class PushStaticField extends BytecodeSequence {

    private JvmFieldDefinition fieldDefinition;

    public PushStaticField(JvmFieldDefinition fieldDefinition) {
        this.fieldDefinition = fieldDefinition;
    }

    @Override
    public void operate(MethodVisitor mv) {
        if (fieldDefinition.isStatic()) {
            mv.visitFieldInsn(GETSTATIC, fieldDefinition.getOwnerInternalName(), fieldDefinition.getFieldName(), fieldDefinition.getDescriptor());
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
