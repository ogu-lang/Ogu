package org.ogu.lang.parser.ast.expressions;

import com.google.common.collect.ImmutableList;
import org.ogu.lang.parser.ast.Node;

/**
 * Parameters passed to a function call
 * Created by ediaz on 21-01-16.
 */
public class ActualParam extends Node {

    private String name;
    private Expression value;

    public String getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActualParam)) return false;

        ActualParam that = (ActualParam) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return value.equals(that.value);

    }


    @Override
    public String toString() {
        return "ActualParam{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + value.hashCode();
        return result;
    }

    public ActualParam(Expression value) {
        this.value = value;
        this.value.setParent(this);
    }

    public ActualParam(Expression value, boolean asterisk) {
        this.value = value;
        this.value.setParent(this);
    }

    public ActualParam(String name, Expression value) {
        this.name = name;
        this.value = value;
        this.value.setParent(this);
    }

    @Override
    public Iterable<Node> getChildren() {
        return ImmutableList.of(value);
    }

    public boolean isNamed() {
        return name != null;
    }

    public ActualParam toUnnamed() {
        return new ActualParam(value);
    }

}
