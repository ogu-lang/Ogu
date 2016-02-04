package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OperatorNode;
import org.ogu.lang.parser.ast.typeusage.TypeArgNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Define an op
 * ej:
 *     def (+) -> a -> a -> a
 * Created by ediaz on 26-01-16.
 */
public class OpDeclarationNode extends FunctionalDeclarationNode {

    private List<TypeArgNode> params;

    public OpDeclarationNode(OperatorNode name, List<TypeArgNode> params, List<Decorator> decorators) {
        super(name, decorators);
        this.params = new ArrayList<>();
        this.params.addAll(params);
        this.params.forEach((p) -> p.setParent(this));
    }



}
