package org.ogu.lang.parser;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.*;
import org.ogu.lang.antlr.*;
import org.ogu.lang.compiler.OguVerboseListener;


/**
 * Interface to the Antlr Generated Parser
 * Created by ediaz on 20-01-16.
 */

public class InternalParser {

    public OguParser.ModuleContext produceParseTree(InputStream inputStream) throws IOException {
        CharStream charStream = new ANTLRInputStream(inputStream);
        OguLexer l = new OguLexer(charStream);
        OguParser p = new OguParser(new CommonTokenStream(l));
        OguVerboseListener listener = new OguVerboseListener();
        p.addErrorListener(listener);

        OguParser.ModuleContext moduleContext = p.module();
        if (l._mode != 0) {
            throw new RuntimeException("Lexical error");
        }
        return moduleContext;
    }

}
