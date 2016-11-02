package org.ogu.lang.symbols;

import org.ogu.lang.typesystem.TypeUsage;

import java.util.List;

/**
 * Created by ediaz on 11/1/16.
 */
public interface InvocableDefinition extends Symbol {

    TypeUsage getReturnType();

    List<? extends FormalParameter> getParameters();

    String getName();
}
