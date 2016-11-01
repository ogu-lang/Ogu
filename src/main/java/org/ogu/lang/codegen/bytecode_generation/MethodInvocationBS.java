package org.ogu.lang.codegen.bytecode_generation;

import org.objectweb.asm.MethodVisitor;
import org.ogu.lang.codegen.jvm.JvmMethodDefinition;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by ediaz on 10/31/16.
 */
public class MethodInvocationBS extends BytecodeSequence {

    private JvmMethodDefinition jvmMethodDefinition;

    public MethodInvocationBS(JvmMethodDefinition jvmMethodDefinition) {
        this.jvmMethodDefinition = jvmMethodDefinition;
    }

    @Override
    public void operate(MethodVisitor mv) {
        // ref.: http://zeroturnaround.com/rebellabs/java-bytecode-fundamentals-using-objects-and-calling-methods/
        if (jvmMethodDefinition.isStatic()) {
            mv.visitMethodInsn(INVOKESTATIC, jvmMethodDefinition.getOwnerInternalName(),
                    jvmMethodDefinition.getName(), jvmMethodDefinition.getDescriptor(), false);
        }
        else {
            if (jvmMethodDefinition.isDeclaredOnInterface()) {
                mv.visitMethodInsn(INVOKEINTERFACE, jvmMethodDefinition.getOwnerInternalName(),
                        jvmMethodDefinition.getName(), jvmMethodDefinition.getDescriptor(), true);
            }
            else {
                mv.visitMethodInsn(INVOKEVIRTUAL, jvmMethodDefinition.getOwnerInternalName(),
                        jvmMethodDefinition.getName(), jvmMethodDefinition.getDescriptor(), false);
            }
        }
    }
}
