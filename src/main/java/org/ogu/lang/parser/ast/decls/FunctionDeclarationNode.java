package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.typeusage.TypeArgNode;

import java.util.ArrayList;
import java.util.List;

/**
 * a def declaration
 * def funcion : T -> T -> T
 * Created by ediaz on 23-01-16.
 */
public class FunctionDeclarationNode extends FunctionalDeclarationNode {

    private List<TypeArgNode> paramTypes;
    private TypeArgNode returnType;


    public FunctionDeclarationNode(IdentifierNode id, List<TypeArgNode> paramTypes, List<DecoratorNode> decoratorNodes) {
        super(id, decoratorNodes);
        this.paramTypes = new ArrayList<>();
        this.paramTypes.addAll(paramTypes.subList(0, paramTypes.size()-1));
        this.paramTypes.forEach((p) -> p.setParent(this));
        this.returnType = paramTypes.get(paramTypes.size()-1);
        this.returnType.setParent(this);

    }

    @Override
    public String toString() {

        return "FunctionDeclaration{" +
                "id ='" + name + '\'' +
                ", returnType =" + returnType+
                ", paramTypes =" + paramTypes +
                ", decorators = " + decoratorNodes +
                '}';
    }


}
