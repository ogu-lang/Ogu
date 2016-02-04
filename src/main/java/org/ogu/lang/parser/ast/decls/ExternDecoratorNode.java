package org.ogu.lang.parser.ast.decls;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;

import java.util.Collections;

/**
 * #{extern "lang" "signature}
 * Allows integration with other languages
 * Created by ediaz on 23-01-16.
 */
public class ExternDecoratorNode extends DecoratorNode {

    public ExternDecoratorNode(String language, String signature) {
        super(ImmutableList.<String>builder().add(language).add(signature).build());
    }

    public String getLanguage() {
        return args.get(0);
    }

    public String getSignature() {
        return args.get(1);
    }
    @Override
    public Iterable<Node> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "ExternDecorator{"+
                "lang="+getLanguage()+
                ", signature="+getSignature()+
                '}';
    }
}
