package org.ogu.lang.parser;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.modules.OguModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser
 * Created by ediaz on 20-01-16.
 */
public class Parser {

    private InternalParser internalParser = new InternalParser();

    public OguModule parse(File file, InputStream inputStream) throws IOException {
        return new ParseTreeToAst().toAst(file, internalParser.produceParseTree(inputStream));
    }

    public List<OguModuleWithSource> parseAllIn(File file) throws IOException {
        if (file.isFile()) {
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
