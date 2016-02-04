package org.ogu.lang.parser.ast.decls;


import org.ogu.lang.parser.ast.NameNode;

import java.util.List;

/**
 * All exportable declarations: Types, Funcs, Vals and Vars
 * Created by ediaz on 24-01-16.
 */
public abstract class ExportableDeclarationNode extends NameDeclarationNode {

    protected ExportableDeclarationNode(List<DecoratorNode> decoratorNodes) {
        super(decoratorNodes);
    }


    protected ExportableDeclarationNode(NameNode name, List<DecoratorNode> decoratorNodes) {
        super(name, decoratorNodes);
    }
}
