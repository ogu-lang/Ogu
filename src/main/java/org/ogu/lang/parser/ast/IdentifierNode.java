package org.ogu.lang.parser.ast;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ediaz on 22-01-16.
 */
public class IdentifierNode extends NameNode {

    public IdentifierNode(TypeIdentifierNode base, String name) {
        super(base, name);
    }

    public IdentifierNode(String name) {
        super(name);
    }

    public static IdentifierNode create(List<String> base, String name) {
        if (base.isEmpty()) {
           return new IdentifierNode(name);
        }
        return new IdentifierNode(TypeIdentifierNode.create(base), name);
    }

    public static IdentifierNode create(String path) {
        List<String> base = Arrays.stream(path.split("\\.")).collect(Collectors.toList());
        if (base.size() == 1)
            return new IdentifierNode(base.get(0));
        return new IdentifierNode(TypeIdentifierNode.create(base.subList(0, base.size()-1)), base.get(base.size()-1));
    }




}
