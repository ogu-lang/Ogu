package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.IdentifierNode;
import org.ogu.lang.parser.ast.NameNode;
import org.ogu.lang.parser.ast.expressions.InvocableExpressionNode;
import org.ogu.lang.parser.ast.typeusage.TypeArgUsageWrapperNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameter;
import org.ogu.lang.typesystem.InvocableReferenceTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * a root for free lets
 * Created by ediaz on 23-01-16.
 */
public class FreeFunctionDeclarationNode extends AbstractFunctionDeclarationNode {




    public FreeFunctionDeclarationNode(NameNode id){
        super(id, new ArrayList<>());
    }




    @Override
    public String toString() {
        return "FreeFunctionDeclaration{" +
                "id ='" + name + '\'' +
                '}';
    }

}
