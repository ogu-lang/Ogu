package org.ogu.lang.typesystem;

import org.ogu.lang.definitions.InternalInvocableDefinition;
import org.ogu.lang.parser.ast.expressions.ActualParam;

import java.util.List;
import java.util.Optional;

/**
 * Created by ediaz on 21-01-16.
 */
public interface Invocable {

    Optional<? extends InternalInvocableDefinition> internalInvocableDefinitionFor(List<ActualParam> actualParams);

}
