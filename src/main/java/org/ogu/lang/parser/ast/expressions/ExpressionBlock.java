package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.typesystem.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * Expression Block, start with INDENT and stop with DEDENT
 * Created by ediaz on 30-01-16.
 */
public class ExpressionBlock extends Expression{

    List<Expression> block;

    public ExpressionBlock(List<Expression> block) {
        super();
        this.block = new ArrayList<>();
        this.block.addAll(block);
        this.block.forEach((e) -> e.setParent(this));
    }

    @Override
    public TypeUsage calcType() {
        return null;
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.<Node>builder().addAll(block).build();
    }


    @Override
    public String toString() {
        return "Block{" +
                "exprs=" + block +
                '}';
    }
}
