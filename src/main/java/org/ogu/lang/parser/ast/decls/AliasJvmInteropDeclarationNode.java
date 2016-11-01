package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.NameNode;

import java.util.List;

/**
 * Created by ediaz on 11/1/16.
 */
public class AliasJvmInteropDeclarationNode extends AliasDeclarationNode {

    private String jvmSignature;


    public AliasJvmInteropDeclarationNode(NameNode id, List<DecoratorNode> decoratorNodes, String jvmSignature) {
        super(id, decoratorNodes);
        this.jvmSignature = jvmSignature;
    }

}
