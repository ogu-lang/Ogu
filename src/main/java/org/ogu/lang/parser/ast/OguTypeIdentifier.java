package org.ogu.lang.parser.ast;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ediaz on 22-01-16.
 */
public class OguTypeIdentifier extends OguName {

    public OguTypeIdentifier(String name) {
        super(name);
    }

    public OguTypeIdentifier(OguName base, String name) {
        super(base, name);
    }

    public static OguTypeIdentifier create(List<String> base) {
        if (base.isEmpty()) {
            throw new IllegalArgumentException();
        } else if (base.size() == 1) {
            return new OguTypeIdentifier(base.get(0));
        } else {
            return new OguTypeIdentifier(OguTypeIdentifier.create(base.subList(0, base.size() - 1)), base.get(base.size() - 1));
        }
    }

    public static OguTypeIdentifier create(String path) {
        return create(Arrays.stream(path.split("\\.")).collect(Collectors.toList()));
    }

}
