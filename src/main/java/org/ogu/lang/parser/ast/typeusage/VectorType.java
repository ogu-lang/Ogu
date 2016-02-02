package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;

/**
 * [Type]
 * Created by ediaz on 30-01-16.
 */
public class VectorType extends OguType {

    private OguType base;

    public VectorType(OguType base) {
        this.base = base;
        this.base.setParent(this);
    }


    @Override
    public String toString() {
        return "["+base+']';
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(base);
    }
}
