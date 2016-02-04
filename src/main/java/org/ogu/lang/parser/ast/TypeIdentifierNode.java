package org.ogu.lang.parser.ast;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ediaz on 22-01-16.
 */
public class TypeIdentifierNode extends NameNode {

    public TypeIdentifierNode(String name) {
        super(name);
    }

    public TypeIdentifierNode(NameNode base, String name) {
        super(base, name);
    }

    public static TypeIdentifierNode create(List<String> base) {
        if (base.isEmpty()) {
            throw new IllegalArgumentException();
        } else if (base.size() == 1) {
            return new TypeIdentifierNode(base.get(0));
        } else {
            return new TypeIdentifierNode(TypeIdentifierNode.create(base.subList(0, base.size() - 1)), base.get(base.size() - 1));
        }
    }

    public static TypeIdentifierNode create(String path) {
        return create(Arrays.stream(path.split("\\.")).collect(Collectors.toList()));
    }

}
