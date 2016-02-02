package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OguOperator;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionNode;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionNodeExpr;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionPatternParam;
import org.ogu.lang.parser.ast.expressions.Expression;

import java.util.List;

/**
 * Define an op
 * ej:
 *     let a - b = ...
 * Created by ediaz on 26-01-16.
 */
public class OpDefinition extends LetDeclaration {

    public OpDefinition(OguOperator name, List<FunctionPatternParam> params, List<Decorator> decorators) {
        super(name, params, decorators);
    }


    @Override
    public String toString() {
        return "OpDefinition{" +
                "op='" + name + '\''+
                ", params=" + params +
                ", body=" + body +
                ", decorators=" + decorators +
                '}';
    }


}