package org.ogu.lang.parser.ast.typeusage;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;
import org.ogu.lang.parser.ast.OguTypeIdentifier;


/**
 * A type like M1.M2.T
 * Created by ediaz on 23-01-16.
 */
public class QualifiedType extends OguType {

    private OguTypeIdentifier typeId;

    public QualifiedType(OguTypeIdentifier typeId) {
        this.typeId = typeId;
        this.typeId.setParent(this);
    }

    @Override
    public String toString() {
        return typeId.toString();
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(typeId);
    }
}
