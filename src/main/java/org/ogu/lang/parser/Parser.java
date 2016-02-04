package org.ogu.lang.parser;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.compiler.Options;
import org.ogu.lang.parser.ast.modules.ModuleNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parser
 * Takes .ogu files and generates an AST for each one
 * Created by ediaz on 20-01-16.
 */
public class Parser {

    private Options options;
    private InternalParser internalParser = new InternalParser();

    public Parser(Options options) {
        this.options = options;
    }

    public ModuleNode parse(File file, InputStream inputStream) throws IOException {
        return new ParseTreeToAst().toAst(file, internalParser.produceParseTree(file, inputStream));
    }

    private void parseOnly(File file, InputStream inputStream) throws IOException {
        internalParser.produceParseTree(file, inputStream);
    }

    public List<OguModuleWithSource> parseAllIn(File file) throws IOException {
        if (file.isFile()) {
            if (options.isParseOnly()) {
                parseOnly(file, new FileInputStream(file));
                return Collections.emptyList();
            }
            return ImmutableList.of(new OguModuleWithSource(file, parse(file, new FileInputStream(file))));
        } else if (file.isDirectory()) {
            List<OguModuleWithSource> result = new ArrayList<>();
            for (File child : file.listFiles()) {
                result.addAll(parseAllIn(child));
            }
            return result;
        } else {
            throw new IllegalArgumentException("Mi sako la ñoña. Archivo no existe: " + file.getPath());
        }
    }

}
