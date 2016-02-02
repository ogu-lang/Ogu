package org.ogu.lang.parser.ast.decls;


import org.ogu.lang.parser.ast.OguIdentifier;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionNode;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionNodeExpr;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionPatternParam;
import org.ogu.lang.parser.ast.expressions.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * let
 * Created by ediaz on 23-01-16.
 */
public class LetDefinition extends LetDeclaration {



    public LetDefinition(OguIdentifier name, List<FunctionPatternParam> params, List<Decorator> decorators) {
        super(name, params, decorators);
    }



    @Override
    public String toString() {
        return "FunctionDefinition{" +
                "name='" + name + '\''+
                ", params=" + params +
                ", body=" + body +
                ", decorators=" + decorators +
                '}';
    }


}
