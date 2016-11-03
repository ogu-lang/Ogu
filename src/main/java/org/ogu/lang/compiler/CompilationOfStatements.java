package org.ogu.lang.compiler;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.codegen.bytecode_generation.*;
import org.ogu.lang.codegen.bytecode_generation.pushpop.*;
import org.ogu.lang.codegen.jvm.JvmInvocableDefinition;
import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.codegen.jvm.JvmTypeCategory;
import org.ogu.lang.parser.analysis.exceptions.UnsolvedFunctionException;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.decls.AliasJvmInteropDeclarationNode;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.parser.ast.expressions.FunctionCallNode;
import org.ogu.lang.parser.ast.expressions.MathOpExpressionNode;
import org.ogu.lang.parser.ast.expressions.ReferenceNode;
import org.ogu.lang.parser.ast.expressions.literals.DoubleLiteralNode;
import org.ogu.lang.parser.ast.expressions.literals.IntLiteralNode;
import org.ogu.lang.parser.ast.expressions.literals.StringLiteralNode;
import org.ogu.lang.resolvers.jdk.ReflectionBasedField;
import org.ogu.lang.resolvers.jdk.ReflectionBasedSetOfOverloadedMethods;
import org.ogu.lang.resolvers.jdk.ReflectionBasedTypeDefinition;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.PrimitiveTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.ogu.lang.codegen.bytecode_generation.OpcodesUtils.loadTypeFor;
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
        return pushExpression(expressionNode);
    }

    BytecodeSequence pushExpression(ExpressionNode expressionNode) {
        if (expressionNode instanceof IntLiteralNode) {
            return new PushIntConst(((IntLiteralNode) expressionNode).getValue());
        }
        else if (expressionNode instanceof DoubleLiteralNode) {
            return new PushDoubleConst(((DoubleLiteralNode) expressionNode).getValue());
        }
        else if (expressionNode instanceof StringLiteralNode) {
            return new PushStringConst(((StringLiteralNode) expressionNode).getValue());
        }
        else if (expressionNode instanceof ReferenceNode) {
            ReferenceNode reference = (ReferenceNode)  expressionNode;
            Optional<Integer> index = compilation.getLocalVarsSymbolTable().findIndex(reference.getName());
            if (index.isPresent()) {
                TypeUsage type = compilation.getLocalVarsSymbolTable().findDeclaration(reference.getName()).get().calcType();
                return new PushLocalVar(loadTypeForTypeUsage(type), index.get());
            } else if (compilation.getLocalVarsSymbolTable().hasAlias(reference.getName())) {
                return compilation.getLocalVarsSymbolTable().getAlias(reference.getName());
            } else {
                return push(reference.resolve(compilation.getResolver()));
            }
        }
        else if (expressionNode instanceof MathOpExpressionNode) {
            MathOpExpressionNode mathOperation = (MathOpExpressionNode) expressionNode;
            // TODO do proper conversions
            if (!mathOperation.getLeft().calcType().sameType(PrimitiveTypeUsage.INT)) {
                throw new UnsupportedOperationException();
            }
            if (!mathOperation.getRight().calcType().sameType(PrimitiveTypeUsage.INT)) {
                throw new UnsupportedOperationException();
            }
            JvmTypeCategory leftTypeCategory = mathOperation.getLeft().calcType().jvmType().typeCategory();
            return new ComposedBytecodeSequence(ImmutableList.of(
                    pushExpression(mathOperation.getLeft()),
                    pushExpression(mathOperation.getRight()),
                    BytecodeUtils.createMathOperation(leftTypeCategory, mathOperation.getOperator())));

        }
        else if (expressionNode instanceof FunctionCallNode) {
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
        Logger.debug("function @ pushInstance = "+function+ " fcal="+functionCall);
        if (function instanceof ReferenceNode) {
            ReferenceNode reference = (ReferenceNode) function;
            Symbol declaration = reference.resolve(compilation.getResolver());
            if (declaration instanceof ReflectionBasedSetOfOverloadedMethods) {
                ReflectionBasedSetOfOverloadedMethods methods = (ReflectionBasedSetOfOverloadedMethods) declaration;
                if (methods.isStatic()) {
                    return NoOp.getInstance();
                } else {
                    return push(methods.getInstance());
                }
            } else if (declaration instanceof AliasJvmInteropDeclarationNode) {
                if (functionCall.isMethodFunction()) {
                    return push(functionCall.getObjectParam().getValue());
                } else {
                    return NoOp.getInstance();
                }
            }
        }
        throw new UnsupportedOperationException(function.getClass().getCanonicalName());
    }

    BytecodeSequence push(Symbol symbol) {
        if (symbol.isNode()) {
            return push(symbol.asNode());
        } else if (symbol instanceof ReflectionBasedField) {
            ReflectionBasedField reflectionBaseField = (ReflectionBasedField) symbol;
            if (reflectionBaseField.isStatic()) {
                return new PushStaticField(reflectionBaseField.toJvmField(compilation.getResolver()));
            } else {
                throw new UnsupportedOperationException();
            }
        } else if (symbol instanceof ReflectionBasedTypeDefinition) {
        }

            throw new UnsupportedOperationException();
    }


    BytecodeSequence push(Node node) {
        Logger.debug("push node = "+node+" "+node.toString());
        if (node instanceof ExpressionNode) {
            return pushExpression((ExpressionNode) node);
        }
        else if (node instanceof AliasJvmInteropDeclarationNode) {
            //
            AliasJvmInteropDeclarationNode alias = (AliasJvmInteropDeclarationNode) node;
            return new PushStaticField(alias.getStaticField());
        }
        throw new UnsupportedOperationException();
    }


    int loadTypeForTypeUsage(TypeUsage type) {
        return loadTypeFor(type.jvmType());
    }
}
