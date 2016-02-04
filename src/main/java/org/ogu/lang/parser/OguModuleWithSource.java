package org.ogu.lang.parser;

import org.ogu.lang.parser.ast.modules.ModuleNode;

import java.io.File;

/**
 * A module with source
 * Created by ediaz on 20-01-16.
 */
public class OguModuleWithSource {

    private File source;
    private ModuleNode module;

    public OguModuleWithSource(File source, ModuleNode module) {
        this.source = source;
        this.module = module;
    }

    public File getSource() {
        return source;
    }

    public ModuleNode getModule() {
        return module;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OguModuleWithSource that = (OguModuleWithSource) o;

        return source.equals(that.source) && module.equals(that.module);

    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + module.hashCode();
        return result;
    }
}
