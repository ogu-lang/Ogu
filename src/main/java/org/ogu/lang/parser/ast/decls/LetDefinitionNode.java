package org.ogu.lang.parser.ast.decls;


import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionPatternParamNode;

import java.util.List;

/**
 * let
 * Created by ediaz on 23-01-16.
 */
public class LetDefinitionNode extends LetDeclarationNode {



    public LetDefinitionNode(IdentifierNode name, List<FunctionPatternParamNode> params, List<DecoratorNode> decoratorNodes) {
        super(name, params, decoratorNodes);
    }



    @Override
    public String toString() {
        return "FunctionDefinition{" +
                "name='" + name + '\''+
                ", params=" + params +
                ", body=" + body +
                ", decorators=" + decoratorNodes +
                '}';
    }


}
