package org.ogu.lang.parser.ast.virtual;

import org.ogu.lang.symbols.Symbol;
import org.ogu.lang.typesystem.PrimitiveTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;

/**
 * Created by ediaz on 10/31/16.
 */
public class ArrayLength implements Symbol {

    private Symbol array;

    public ArrayLength(Symbol array) {
        // non setting the parent of array on purpose
        this.array = array;
    }

    @Override
    public TypeUsage calcType() {
        return PrimitiveTypeUsage.INT;
    }

}
