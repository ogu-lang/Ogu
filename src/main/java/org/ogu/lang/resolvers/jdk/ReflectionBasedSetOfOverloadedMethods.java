package org.ogu.lang.resolvers.jdk;

import jdk.nashorn.internal.ir.FunctionCall;
import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.parser.ast.expressions.FunctionCallNode;
import org.ogu.lang.parser.ast.expressions.InvocableExpressionNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.TypeUsage;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by ediaz on 10/31/16.
 */
public class ReflectionBasedSetOfOverloadedMethods extends ExpressionNode {

    private List<Method> methods;
    private boolean isStatic;
    private Symbol instance;
    private String name;

    public JvmMethodDefinition findMethodFor(List<ActualParamNode> actualParams, SymbolResolver resolver) {
        List<JvmType> argsTypes = actualParams.stream().map((ap)->ap.getValue().calcType().jvmType()).collect(Collectors.toList());
        return ReflectionTypeDefinitionFactory.toFunctionDefinition(ReflectionBasedMethodResolution.findMethodAmong(name, argsTypes, resolver, methods));
    }

    private SymbolResolver symbolResolver;

    public ReflectionBasedSetOfOverloadedMethods(List<Method> methods, Symbol instance, SymbolResolver symbolResolver) {
        this.symbolResolver = symbolResolver;
        if (methods.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.isStatic = Modifier.isStatic(methods.get(0).getModifiers());
        this.name = methods.get(0).getName();
        for (Method method : methods) {
            if (isStatic != Modifier.isStatic(method.getModifiers())) {
                throw new IllegalArgumentException("All methods should be static or non static");
            }
            if (!name.equals(method.getName())) {
                throw new IllegalArgumentException("All methods should be named " + name);
            }
        }
        this.methods = methods;
        this.instance = instance;
    }

    public Symbol getInstance() {
        return instance;
    }

    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }

    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public TypeUsage calcType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<List<? extends FormalParameter>> findFormalParametersFor(InvocableExpressionNode invocable) {
        if (invocable instanceof FunctionCallNode) {
            FunctionCallNode functionCall = (FunctionCallNode)invocable;
            Optional<Method> method = ReflectionBasedMethodResolution.findMethodAmongActualParams(name, invocable.getActualParamNodes(), symbolResolver,  methods);
            return Optional.of(ReflectionBasedMethodResolution.formalParameters(method.get(), Collections.emptyMap(), symbolResolver));
        }
        throw new UnsupportedOperationException(invocable.getClass().getCanonicalName());
        // return ReflectionTypeDefinitionFactory.toMethodDefinition(ReflectionBasedMethodResolution.findMethodAmong(name, argsTypes, resolver, staticContext, methods, this));
    }
}
