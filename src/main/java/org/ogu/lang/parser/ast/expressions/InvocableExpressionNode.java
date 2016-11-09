package org.ogu.lang.parser.ast.expressions;

import org.ogu.lang.compiler.errorhandling.ErrorCollector;
import org.ogu.lang.parser.analysis.exceptions.UnsolvedInvocableException;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ediaz on 21-01-16.
 */
public abstract class InvocableExpressionNode extends ExpressionNode {

    protected List<ActualParamNode> actualParamNodes;

    public List<ActualParamNode> getActualParamNodes() {
        return actualParamNodes;
    }

    public InvocableExpressionNode(List<ActualParamNode> actualParamNodes) {
        this.actualParamNodes = new ArrayList<>();
        this.actualParamNodes.addAll(actualParamNodes);
        this.actualParamNodes.forEach((p) -> p.setParent(this));
    }

    protected abstract List<? extends FormalParameter> formalParameters(SymbolResolver resolver);

    public void desugarize(SymbolResolver resolver) {
        if (desugarized) {
            return;
        }
        concreteDesugarize(resolver);
        desugarized = true;
    }

    private boolean desugarized = false;
    private ActualParamNode self = null;

    public abstract boolean isOnOverloaded(SymbolResolver resolver);

    public boolean isMethodFunction() {
        return self != null;
    }


    @Override
    protected boolean specificValidate(SymbolResolver resolver, ErrorCollector errorCollector) {
        return super.specificValidate(resolver, errorCollector);
    }

    // if isMethodFunction must push this object
    public ActualParamNode getSubject() {
        return self;
    }

    private void concreteDesugarize(SymbolResolver resolver) {
        Map<String, ActualParamNode> paramAssigned = new HashMap<>();

        List<? extends FormalParameter> formalParams = formalParameters(resolver);
        formalParams.forEach((fp) -> {
            if (fp.isNode()) {
                fp.asNode().setParent(this);
            }
        });

        // ESTO HAY QUE REVISARLO...
        if (formalParams.size() < actualParamNodes.size()) {
            self = actualParamNodes.get(0);
            actualParamNodes.remove(0);
        }
        if (formalParams.size() != actualParamNodes.size()) {
            throw new IllegalArgumentException("no coincide la cantidad de parámetros al invocar la función");
        }

        int i = 0;
        for (ActualParamNode param : actualParamNodes) {
            if (formalParams.get(i).isNode() && formalParams.get(i).asNode().getParent() == null) {
                throw new IllegalStateException();
            }
           // Logger.debug("param = "+param);
            //Logger.debug("formal = "+formalParams.get(i));

            //Logger.debug("param.getValue() = "+param.getValue());
            TypeUsage actualParamType = param.getValue().calcType();
            TypeUsage formalParamType = formalParams.get(i).getType();

            //Logger.debug("actualParamType = "+actualParamType);
            //Logger.debug("formalParamType = "+formalParamType);

            if (!actualParamType.canBeAssignedTo(formalParamType)) {
                Logger.debug("NO PUEDO ASIGNAR "+actualParamType+" a "+formalParamType);
                throw new UnsolvedInvocableException(this);
            }
            paramAssigned.put(formalParams.get(i).getName(), param);
            i++;
        }

        // check all formal parameters assigned
        for (FormalParameter formalParameter : formalParams) {
            if (!paramAssigned.containsKey(formalParameter.getName())) {
                throw new IllegalArgumentException("parametro no asignado: "+formalParameter.getName());
            }
        }
    }
}
