package org.ogu.lang.parser.ast.expressions;

import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * An Expression
 * Created by ediaz on 21-01-16.
 */
public abstract class ExpressionNode extends Node {

    public abstract TypeUsage calcType();
}
