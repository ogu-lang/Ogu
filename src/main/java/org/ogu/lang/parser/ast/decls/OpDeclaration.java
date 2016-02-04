package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OperatorNode;
import org.ogu.lang.parser.ast.typeusage.TypeArg;

import java.util.ArrayList;
import java.util.List;

/**
 * Define an op
 * ej:
 *     def (+) -> a -> a -> a
 * Created by ediaz on 26-01-16.
 */
public class OpDeclaration extends FunctionalDeclaration {

    private List<TypeArg> params;

    public OpDeclaration(OperatorNode name, List<TypeArg> params, List<Decorator> decorators) {
        super(name, decorators);
        this.params = new ArrayList<>();
        this.params.addAll(params);
        this.params.forEach((p) -> p.setParent(this));
    }



}
