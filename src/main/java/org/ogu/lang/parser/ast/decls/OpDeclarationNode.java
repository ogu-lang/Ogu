package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OperatorNode;
import org.ogu.lang.parser.ast.typeusage.TypeArgUsageWrapperNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Define an op
 * ej:
 *     def (+) -> a -> a -> a
 * Created by ediaz on 26-01-16.
 */
public class OpDeclarationNode extends FunctionalDeclarationNode {

    private List<TypeArgUsageWrapperNode> params;

    public OpDeclarationNode(OperatorNode name, List<TypeArgUsageWrapperNode> params, List<DecoratorNode> decoratorNodes) {
        super(name, decoratorNodes);
        this.params = new ArrayList<>();
        this.params.addAll(params);
        this.params.forEach((p) -> p.setParent(this));
    }



}
