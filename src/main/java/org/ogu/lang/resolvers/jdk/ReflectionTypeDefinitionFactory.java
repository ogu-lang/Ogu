package org.ogu.lang.resolvers.jdk;

import org.ogu.lang.codegen.jvm.JvmConstructorDefinition;
import org.ogu.lang.codegen.jvm.JvmMethodDefinition;
import org.ogu.lang.codegen.jvm.JvmNameUtils;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.typesystem.ArrayTypeUsage;
import org.ogu.lang.typesystem.PrimitiveTypeUsage;
import org.ogu.lang.typesystem.ReferenceTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by ediaz on 22-01-16.
 */
public class ReflectionTypeDefinitionFactory {

    private static final ReflectionTypeDefinitionFactory INSTANCE = new ReflectionTypeDefinitionFactory();

    public static ReflectionTypeDefinitionFactory getInstance() {
        return INSTANCE;
    }

    public static JvmMethodDefinition toFunctionDefinition(Method method){
        return new JvmMethodDefinition(JvmNameUtils.internalName(method.getDeclaringClass()),
                method.getName(), calcSignature(method), Modifier.isStatic(method.getModifiers()),
                method.getDeclaringClass().isInterface());
    }

    public static String calcSignature(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            switch (clazz.getName()) {
                case "boolean":
                    return "Z";
                case "byte":
                    return "B";
                case "char":
                    return "C";
                case "short":
                    return "S";
                case "int":
                    return "I";
                case "long":
                    return "J";
                case "float":
                    return "F";
                case "double":
                    return "D";
                case "void":
                    return "V";
                default:
                    throw new UnsupportedOperationException(clazz.getCanonicalName());
            }
        } else if (clazz.isArray()){
            return "[" + calcSignature(clazz.getComponentType());
        } else {
            return "L" + clazz.getCanonicalName().replaceAll("\\.", "/") + ";";
        }
    }


    public static String calcSignature(Method method) {
        List<String> paramTypesSignatures = Arrays.stream(method.getParameterTypes()).map((t) -> calcSignature(t)).collect(Collectors.toList());
        return "(" + String.join("", paramTypesSignatures) + ")" + calcSignature(method.getReturnType());
    }


    public static TypeUsage toTypeUsage(Class<?> type, SymbolResolver resolver) {
        if (type.isArray()) {
            return new ArrayTypeUsage(toTypeUsage(type.getComponentType(), resolver));
        } else if (type.isPrimitive()) {
            return PrimitiveTypeUsage.getByName(type.getName());
        } else {
            return new ReferenceTypeUsage(getInstance().getTypeDefinition(type, resolver));
        }
    }

    public TypeDefinition getTypeDefinition(Class<?> clazz, SymbolResolver resolver) {
        return getTypeDefinition(clazz, Collections.emptyList(), resolver);
    }

    public TypeDefinition getTypeDefinition(Class<?> clazz, List<TypeUsage> typeParams, SymbolResolver resolver) {
        if (clazz.isArray()) {
            throw new IllegalArgumentException();
        }
        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException();
        }
        ReflectionBasedTypeDefinition type = new ReflectionBasedTypeDefinition(clazz, resolver);
        for (TypeUsage typeUsage : typeParams) {
            type.addTypeParameter(typeUsage);
        }
        return type;
    }

    public Optional<TypeDefinition> findTypeDefinition(String typeName, SymbolResolver resolver) {
        if (!typeName.startsWith("java.") && !typeName.startsWith("javax.")) {
            return Optional.empty();
        }
        try {
            Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(typeName);
            return Optional.of(getTypeDefinition(clazz, resolver));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }


    public static JvmConstructorDefinition toConstructorDefinition(Constructor constructor) {
        return new JvmConstructorDefinition(JvmNameUtils.canonicalToInternal(constructor.getDeclaringClass().getCanonicalName()), calcSignature(constructor));
    }

    private static String calcSignature(Constructor constructor) {
        List<String> paramTypesSignatures = Arrays.stream(constructor.getParameterTypes()).map((t) -> calcSignature(t)).collect(Collectors.toList());
        return "(" + String.join("", paramTypesSignatures) + ")V";
    }


}
