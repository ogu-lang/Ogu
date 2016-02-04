package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OperatorNode;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionPatternParam;

import java.util.List;

/**
 * Define an op
 * ej:
 *     let a - b = ...
 * Created by ediaz on 26-01-16.
 */
public class OpDefinition extends LetDeclarationNode {

    public OpDefinition(OperatorNode name, List<FunctionPatternParam> params, List<DecoratorNode> decoratorNodes) {
        super(name, params, decoratorNodes);
    }


    @Override
    public String toString() {
        return "OpDefinition{" +
                "op='" + name + '\''+
                ", params=" + params +
                ", body=" + body +
                ", decorators=" + decoratorNodes +
                '}';
    }


}