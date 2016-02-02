package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OguName;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionNode;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionNodeExpr;
import org.ogu.lang.parser.ast.decls.funcdef.FunctionPatternParam;
import org.ogu.lang.parser.ast.expressions.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * Base for LetDefinition and OpDefinition
 * Created by ediaz on 26-01-16.
 */
public abstract class LetDeclaration extends FunctionalDeclaration {

    List<FunctionPatternParam> params;
    List<FunctionNode> body;


    protected LetDeclaration(OguName name, List<FunctionPatternParam> params, List<Decorator> decorators) {
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
}
