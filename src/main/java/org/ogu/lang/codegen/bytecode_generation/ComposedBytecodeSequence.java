package org.ogu.lang.codegen.bytecode_generation;

import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

/**
 * Created by ediaz on 10/30/16.
 */
public class ComposedBytecodeSequence extends BytecodeSequence {

    private List<BytecodeSequence> components;

    public ComposedBytecodeSequence(BytecodeSequence... components) {
        this(ImmutableList.<BytecodeSequence>builder().add(components).build());
    }

    public ComposedBytecodeSequence(List<BytecodeSequence> components) {
        this.components = components;
    }

    @Override
    public void operate(MethodVisitor mv) {
        for (BytecodeSequence component : components) {
            component.operate(mv);
        }
    }
}
