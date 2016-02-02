package org.ogu.lang.compiler.errorhandling;

import org.ogu.lang.parser.ast.Position;

/**
 * Created by ediaz on 20-01-16.
 */

public interface ErrorCollector {

    void recordSemanticError(Position position, String description);

}