package org.ogu.lang.resolvers;

import org.ogu.lang.parser.ast.modules.OguModule;
import java.util.List;

/**
 * Created by ediaz on 20-01-16.
 */
public class SrcSymbolResolver implements SymbolResolver {

    public SrcSymbolResolver(List<OguModule> modules) {
        // TODO Implements
    }
    @Override
    public SymbolResolver getParent() {
        return null;
    }

    @Override
    public void setParent(SymbolResolver parent) {

    }
}
