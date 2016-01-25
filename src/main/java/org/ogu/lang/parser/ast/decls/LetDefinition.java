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
public class LetDefinition extends ContractMemberDeclaration {

    List<FunctionPatternParam> params;
    List<FunctionNode> body;
    ContractDeclaration contractDeclaration;

    public LetDefinition(OguIdentifier name, List<FunctionPatternParam> params, List<Decorator> decorators) {
        super(name, decorators);
        this.params = new ArrayList<>();
        this.params.addAll(params);
        this.params.forEach((p) -> p.setParent(this));
        this.body = new ArrayList<>();
    }

    public void add(FunctionNode node) {
        this.body.add(node);
        node.setParent(this);
    }

    public void add(Expression expr) {
        this.add(new FunctionNodeExpr(expr));
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
