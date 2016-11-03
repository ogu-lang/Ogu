package org.ogu.lang.compiler;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.parser.ast.expressions.ConstructorNode;
import org.ogu.lang.parser.ast.expressions.ExpressionNode;
import org.ogu.lang.parser.ast.expressions.TypeReferenceNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.typesystem.PrimitiveTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * Created by ediaz on 10/31/16.
 */
public final class BoxUnboxing {
    private BoxUnboxing() {
        // prevent instantiation
    }

    public static ExpressionNode box(ExpressionNode value, SymbolResolver resolver) {
        TypeUsage type = value.calcType();
        if (!type.isPrimitive()) {
            throw new IllegalArgumentException("type is not primitive, cannot box this value");
        }
        PrimitiveTypeUsage typeUsage = type.asPrimitiveTypeUsage();
        if (typeUsage.isInt()) {
            Node parent = value.getParent();
            ConstructorNode creation = new ConstructorNode(TypeReferenceNode.create(Integer.class.getCanonicalName()), ImmutableList.of(new ActualParamNode(value)));
            creation.setParent(parent);
            return creation;
        } else if (typeUsage.isChar()) {
            Node parent = value.getParent();
            ConstructorNode creation = new ConstructorNode(TypeReferenceNode.create(Character.class.getCanonicalName()), ImmutableList.of(new ActualParamNode(value)));
            creation.setParent(parent);
            return creation;
        } else if (typeUsage.isBoolean()) {
            Node parent = value.getParent();
            ConstructorNode creation = new ConstructorNode(TypeReferenceNode.create(Boolean.class.getCanonicalName()), ImmutableList.of(new ActualParamNode(value)));
            creation.setParent(parent);
            return creation;
        } else if (typeUsage.isByte()) {
            Node parent = value.getParent();
            ConstructorNode creation = new ConstructorNode(TypeReferenceNode.create(Byte.class.getCanonicalName()), ImmutableList.of(new ActualParamNode(value)));
            creation.setParent(parent);
            return creation;
        } else if (typeUsage.isShort()) {
            Node parent = value.getParent();
            ConstructorNode creation = new ConstructorNode(TypeReferenceNode.create(Short.class.getCanonicalName()), ImmutableList.of(new ActualParamNode(value)));
            creation.setParent(parent);
            return creation;
        } else if (typeUsage.isLong()) {
            Node parent = value.getParent();
            ConstructorNode creation = new ConstructorNode(TypeReferenceNode.create(Long.class.getCanonicalName()), ImmutableList.of(new ActualParamNode(value)));
            creation.setParent(parent);
            return creation;
        } else if (typeUsage.isFloat()) {
            Node parent = value.getParent();
            ConstructorNode creation = new ConstructorNode(TypeReferenceNode.create(Float.class.getCanonicalName()), ImmutableList.of(new ActualParamNode(value)));
            creation.setParent(parent);
            return creation;
        } else if (typeUsage.isDouble()) {
            Node parent = value.getParent();
            ConstructorNode creation = new ConstructorNode(TypeReferenceNode.create(Double.class.getCanonicalName()), ImmutableList.of(new ActualParamNode(value)));
            creation.setParent(parent);
            return creation;
        } else {
            throw new RuntimeException("Unexpected primitive type: " + typeUsage);
        }
    }
}
