package org.ogu.lang.codegen.jvm;


/**
 * Created by ediaz on 21-01-16.
 */
public class JvmMethodDefinition extends JvmInvocableDefinition{

    private boolean _static;
    private boolean _declaredOnInterface;

    public JvmMethodDefinition(String ownerInternalName, String methodName,
                               String descriptor, boolean _static,
                               boolean _declaredOnInterface) {
        super(ownerInternalName, methodName, descriptor);
        this._static = _static;
        this._declaredOnInterface = _declaredOnInterface;
    }

    public boolean isStatic() {
        return _static;
    }

    public boolean isDeclaredOnInterface() {
        return _declaredOnInterface;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JvmMethodDefinition)) return false;

        JvmMethodDefinition that = (JvmMethodDefinition) o;

        return _static == that._static
                && _declaredOnInterface == that._declaredOnInterface
                && ownerInternalName.equals(that.ownerInternalName)
                && name.equals(that.name)
                && descriptor.equals(that.descriptor);

    }

    @Override
    public int hashCode() {
        int result = (_static ? 1 : 0);
        result = 31*result + (_declaredOnInterface ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JvmMethodDefinition{" +
                "ownnternalName="+ownerInternalName+
                ", name="+name+
                ", descriptor="+descriptor+
                "_static=" + _static +
                ", _declaredOnInterface=" + _declaredOnInterface +
                '}';
    }
}
