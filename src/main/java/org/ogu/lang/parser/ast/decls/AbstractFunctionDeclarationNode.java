package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.codegen.jvm.JvmNameUtils;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.compiler.errorhandling.SemanticErrorException;
import org.ogu.lang.definitions.InternalFunctionDefinition;
import org.ogu.lang.definitions.InternalInvocableDefinition;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.NameNode;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionPatternParamNode;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.parser.ast.expressions.InvocableExpressionNode;
import org.ogu.lang.parser.ast.typeusage.TypeArgUsageWrapperNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.InvocableReferenceTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * a def declaration
 * def funcion : T -> T -> T
 * Created by ediaz on 23-01-16.
 */
public abstract class AbstractFunctionDeclarationNode extends FunctionalDeclarationNode {


    public AbstractFunctionDeclarationNode(NameNode id, List<DecoratorNode> decoratorNodes) {
        super(id, decoratorNodes);
    }

    private List<FunctionalDeclarationNode> decls = new ArrayList<>();

    public void add(FunctionalDeclarationNode letDecl) {
        decls.add(letDecl);
    }


    @Override
    public Optional<List<? extends FormalParameter>> findFormalParametersFor(InvocableExpressionNode invocable) {
        for (FunctionalDeclarationNode decl : decls )  {
            Optional<List<? extends FormalParameter>> params = decl.findFormalParametersFor(invocable);
            if (params.isPresent()) {
                if (matchParams(params.get(), invocable.getActualParamNodes())) {
                    return params;
                }
            }

        }
        return Optional.empty();
    }

    private boolean matchParams(List<? extends FormalParameter> formalParams, List<ActualParamNode> actualParamNodes) {
        if (formalParams.size() != actualParamNodes.size())
            return false;
        for (int i = 0; i < formalParams.size(); i++) {
            FormalParameter fp = formalParams.get(i);
            ActualParamNode ap = actualParamNodes.get(i);
            if (!fp.getType().jvmType().isAssignableBy(ap.getValue().calcType().jvmType()))
                return false;
        }
        return true;

    }


    public static final String CLASS_PREFIX = "Function_";
    public static final String INVOKE_METHOD_NAME = "invoke";

    public JvmMethodDefinition jvmMethodDefinition(List<JvmType> argsTypes, SymbolResolver resolver) {
        for (FunctionalDeclarationNode decl : decls )  {
            if (decl instanceof LetDefinitionNode) {
                LetDefinitionNode letDecl = (LetDefinitionNode) decl;
                if (letDecl.match(argsTypes, resolver))
                    return letDecl.jvmMethodDefinition(resolver);
            }
        }
        return null;
    }


    @Override
    public TypeUsage calcType() {
        validateReturnType();
        return returnTypeUsage;
    }


    TypeUsage returnTypeUsage;
    private void validateReturnType() {
        for (FunctionalDeclarationNode decl : decls)  {
            if (returnTypeUsage == null) {
                returnTypeUsage = decl.getReturnType();
            }
            if (!decl.getReturnType().equals(returnTypeUsage)) {
                throw new SemanticErrorException(decl, "Declaración debe mantener el tipo de retorno");
            }
        }
    }


}
