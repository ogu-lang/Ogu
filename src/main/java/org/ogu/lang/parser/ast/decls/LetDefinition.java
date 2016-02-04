package org.ogu.lang.parser.ast.decls;


import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionPatternParam;

import java.util.List;

/**
 * let
 * Created by ediaz on 23-01-16.
 */
public class LetDefinition extends LetDeclarationNode {



    public LetDefinition(IdentifierNode name, List<FunctionPatternParam> params, List<DecoratorNode> decoratorNodes) {
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
