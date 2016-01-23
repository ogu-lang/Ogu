package org.ogu.lang.parser.ast;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ediaz on 22-01-16.
 */
public class OguIdentifier extends OguName {

    public OguIdentifier(OguTypeIdentifier base, String name) {
        super(base, name);
    }

    public OguIdentifier(String name) {
        super(name);
    }

    public static OguIdentifier create(List<String> base, String name) {
        if (base.isEmpty()) {
           return new OguIdentifier(name);
        }
        return new OguIdentifier(OguTypeIdentifier.create(base), name);
    }

    public static OguIdentifier create(String path) {
        List<String> base = Arrays.stream(path.split("\\.")).collect(Collectors.toList());
        if (base.size() == 1)
            return new OguIdentifier(base.get(0));
        return new OguIdentifier(OguTypeIdentifier.create(base.subList(0, base.size()-1)), base.get(base.size()-1));
    }
}
