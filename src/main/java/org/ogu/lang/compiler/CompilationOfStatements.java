package org.ogu.lang.compiler;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.codegen.bytecode_generation.BytecodeSequence;
import org.ogu.lang.codegen.bytecode_generation.ComposedBytecodeSequence;
import org.ogu.lang.codegen.bytecode_generation.MethodInvocationBS;
import org.ogu.lang.codegen.bytecode_generation.NoOp;
import org.ogu.lang.codegen.jvm.JvmInvocableDefinition;
import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.parser.analysis.exceptions.UnsolvedFunctionException;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.parser.ast.expressions.FunctionCallNode;
import org.ogu.lang.parser.ast.expressions.ReferenceNode;
import org.ogu.lang.parser.ast.expressions.control.FuncDeclExpressionNode;
import org.ogu.lang.symbols.Symbol;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.ogu.lang.compiler.BoxUnboxing.box;

/**
 * Created by ediaz on 10/30/16.
 */
public class CompilationOfStatements {

    private final Compilation compilation;

    public CompilationOfStatements(Compilation compilation) {
        this.compilation = compilation;
    }

    private BytecodeSequence codeToExecuteBeforeReturning;

    BytecodeSequence compile(ExpressionNode expressionNode) {
        if (expressionNode instanceof FunctionCallNode) {
            FunctionCallNode functionCall = (FunctionCallNode) expressionNode;
            functionCall.desugarize(compilation.getResolver());
            BytecodeSequence instancePush = pushInstance(functionCall);
            Optional<JvmMethodDefinition> methodDefinition = compilation.getResolver().findJvmDefinition(functionCall);
            if (!methodDefinition.isPresent()) {
                throw new UnsolvedFunctionException(functionCall);
            }
            BytecodeSequence argumentsPush = adaptAndCompileAllParameters(
                    functionCall.getActualParamValuesInOrder(), methodDefinition.get()
            );
            return new ComposedBytecodeSequence(ImmutableList.<BytecodeSequence>builder()
                        .add(instancePush)
                        .add(argumentsPush)
                        .add(new MethodInvocationBS(methodDefinition.get())).build());
        }
        throw new UnsupportedOperationException(expressionNode.getClass().getCanonicalName());
    }


    BytecodeSequence adaptAndCompileAllParameters(List<ExpressionNode> actualValues, JvmInvocableDefinition invocableDefinition) {
        List<BytecodeSequence> elements = new LinkedList<>();
        for (int i = 0; i < actualValues.size(); i++) {
            ExpressionNode value = actualValues.get(i);
            JvmType formalType = invocableDefinition.getParamType(i);
            elements.add(adaptAndCompile(value, formalType));
        }
        return new ComposedBytecodeSequence(elements);
    }

    private BytecodeSequence adaptAndCompile(ExpressionNode value, JvmType formalType) {
        JvmType actualType = value.calcType().jvmType();
        boolean isPrimitive = actualType.isPrimitive();
        if (isPrimitive && !formalType.isPrimitive()) {
            // boxing
            return compile(box(value, compilation.getResolver()));
        }
        if (isPrimitive && formalType.isPrimitive() && !actualType.equals(formalType)) {
            throw new IllegalArgumentException("tipos incorrectos al invocar función");
        }
        return compile(value);
    }

    BytecodeSequence pushInstance(FunctionCallNode functionCall) {
        ExpressionNode function = functionCall.getFunction();
        if (function instanceof ReferenceNode) {
            ReferenceNode reference = (ReferenceNode) function;
            Symbol declaration = reference.resolve(compilation.getResolver());
            if (declaration instanceof FuncDeclExpressionNode) {
                FuncDeclExpressionNode funcDecl = (FuncDeclExpressionNode) declaration;
                return NoOp.getInstance();
            }
        }
        throw new UnsupportedOperationException(function.getClass().getCanonicalName());
    }


}
