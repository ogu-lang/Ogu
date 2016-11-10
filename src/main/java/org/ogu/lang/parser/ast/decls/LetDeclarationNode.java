package org.ogu.lang.parser.ast.decls;

import org.apache.maven.plugin.logging.Log;
import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.codegen.jvm.JvmNameUtils;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.definitions.InternalFunctionDefinition;
import org.ogu.lang.definitions.InternalInvocableDefinition;
import org.ogu.lang.parser.ast.NameNode;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionNode;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionNodeExpr;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionPatternParamNode;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.parser.ast.expressions.InvocableExpressionNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.InvocableReferenceTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Base for LetDefinition and OpDefinition
 * Created by ediaz on 26-01-16.
 */
public abstract class LetDeclarationNode extends FunctionalDeclarationNode {

    List<FunctionPatternParamNode> params;
    List<FunctionNode> body;


    protected LetDeclarationNode(NameNode name, List<FunctionPatternParamNode> params, List<DecoratorNode> decoratorNodes) {
        super(name, decoratorNodes);
        this.params = new ArrayList<>();
        this.params.addAll(params);
        this.params.forEach((p) -> p.setParent(this));
        this.body = new ArrayList<>();
    }

    public void add(FunctionNode node) {
        this.body.add(node);
        node.setParent(this);
    }

    public void add(ExpressionNode expr) {
        this.add(new FunctionNodeExpr(expr));
    }

    public List<FunctionNode> getBody() {
        return body;
    }


    @Override
    public List<? extends FormalParameter> getParameters() {
        return params;
    }


    @Override
    public Optional<Symbol> findSymbol(String name, SymbolResolver resolver) {
        for (FunctionPatternParamNode param : params) {
            if (param.getName().equals(name)) {
                return Optional.of(param);
            }
        }
        return super.findSymbol(name, resolver);
    }


    @Override
    public TypeUsage getReturnType() {
        if (body.size() == 0) {
            throw  new RuntimeException("Cuerpo vacío para declaración de función!");
        } else if (body.size() == 1) {
            FunctionNode expr = body.get(0);
            return expr.calcType();
        } else {
            FunctionNode expr = body.get(body.size()-1);
            return expr.calcType();
        }
    }

    @Override
    public Optional<List<? extends FormalParameter>> findFormalParametersFor(InvocableExpressionNode invokable) {
        return Optional.of(params);
    }

    @Override
    public TypeUsage calcType() {
        InvocableReferenceTypeUsage invokableReferenceTypeUsage = new InvocableReferenceTypeUsage(internalInvocableDefinition());
        return invokableReferenceTypeUsage;
    }


    private InternalInvocableDefinition internalInvocableDefinition() {
        return new InternalFunctionDefinition(INVOKE_METHOD_NAME, params, getReturnType(), jvmMethodDefinition(symbolResolver()));
    }

    public static final String CLASS_PREFIX = "Function_";
    public static final String INVOKE_METHOD_NAME = "invoke";

    protected String getGeneratedClassQualifiedName() {
        String internalName = JvmNameUtils.renameFromOguToJvm(name.qualifiedName());
        String qName = this.contextName() + "." + CLASS_PREFIX + internalName;
        if (!JvmNameUtils.isValidQualifiedName(qName)) {
            throw new IllegalStateException(qName);
        }
        return qName;
    }

    public JvmMethodDefinition jvmMethodDefinition(SymbolResolver resolver) {
        String qName = getGeneratedClassQualifiedName();
        String descriptor = "(" + String.join("", params.stream().map((fp)->fp.getType().jvmType().getDescriptor()).collect(Collectors.toList())) + ")" + getReturnType().jvmType().getDescriptor();
        return new JvmMethodDefinition(JvmNameUtils.canonicalToInternal(qName), INVOKE_METHOD_NAME, descriptor, true, false);
    }

    public boolean match(List<JvmType> argsTypes, SymbolResolver resolver) {
        if (argsTypes.size() != params.size()) {
            return false;
        }
        int i = 0;
        for (FunctionPatternParamNode formalParameter : params) {
            if (!formalParameter.getType().jvmType().isAssignableBy(argsTypes.get(i))) {
                return false;
            }
            i++;
        }
        return true;
    }
}
