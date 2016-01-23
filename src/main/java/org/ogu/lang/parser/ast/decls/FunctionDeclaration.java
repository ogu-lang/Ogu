package org.ogu.lang.parser.ast.decls;

import org.ogu.lang.parser.ast.OguIdentifier;
import org.ogu.lang.parser.ast.typeusage.TypeArg;
import org.ogu.lang.symbols.FormalParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * a def declaration
 * def funcion : T -> T -> T
 * Created by ediaz on 23-01-16.
 */
public class FunctionDeclaration extends Declaration {

    List<TypeArg> paramTypes;
    TypeArg returnType;


    public FunctionDeclaration(OguIdentifier id, List<TypeArg> paramTypes) {
        super(id);
        this.paramTypes = new ArrayList<>();
        this.paramTypes.addAll(paramTypes.subList(0, paramTypes.size()-1));
        this.paramTypes.forEach((p) -> p.setParent(this));
        this.returnType = paramTypes.get(paramTypes.size()-1);
        this.returnType.setParent(this);

    }

    @Override
    public String toString() {

        return "FunctionDeclaration{" +
                "id ='" + id + '\'' +
                ", returnType =" + returnType+
                ", paramTypes =" + paramTypes +
                '}';
    }
}
