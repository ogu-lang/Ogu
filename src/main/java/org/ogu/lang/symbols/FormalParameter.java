package org.ogu.lang.symbols;

import org.ogu.lang.typesystem.TypeUsage;

import java.util.Map;

/**
 * Created by ediaz on 21-01-16.
 */
public interface FormalParameter extends Symbol {

    TypeUsage getType();

    String getName();

    FormalParameter apply(Map<String, TypeUsage> typeParams);
}
