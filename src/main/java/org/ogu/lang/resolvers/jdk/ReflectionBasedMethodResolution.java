package org.ogu.lang.resolvers.jdk;

import org.ogu.lang.codegen.jvm.JvmConstructorDefinition;
import org.ogu.lang.codegen.jvm.JvmNameUtils;
import org.ogu.lang.codegen.jvm.JvmType;
import org.ogu.lang.compiler.AmbiguousCallException;
import org.ogu.lang.definitions.TypeDefinition;
import org.ogu.lang.parser.ast.expressions.ActualParamNode;
import org.ogu.lang.parser.ast.typeusage.UnitTypeNode;
import org.ogu.lang.resolvers.SymbolResolver;
import org.ogu.lang.symbols.FormalParameterSymbol;
import org.ogu.lang.typesystem.ArrayTypeUsage;
import org.ogu.lang.typesystem.PrimitiveTypeUsage;
import org.ogu.lang.typesystem.ReferenceTypeUsage;
import org.ogu.lang.typesystem.TypeUsage;
import org.ogu.lang.util.Logger;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ediaz on 10/31/16.
 */
public class ReflectionBasedMethodResolution {

    private static class MethodOrConstructor {
        private Constructor constructor;
        private Method method;

        public MethodOrConstructor(Constructor constructor) {
            this.constructor = constructor;
        }

        public MethodOrConstructor(Method method) {
            this.method = method;
        }

        public int getParameterCount() {
            if (method != null) {
                return method.getParameterCount();
            } else {
                return constructor.getParameterCount();
            }
        }

        public Class<?> getParameterType(int i) {
            if (method != null) {
                return method.getParameterTypes()[i];
            } else {
                return constructor.getParameterTypes()[i];
            }
        }
    }

    public static List<FormalParameterSymbol> formalParameters(Constructor constructor, SymbolResolver resolver) {
        List<FormalParameterSymbol> formalParameters = new ArrayList<>();
        int i=0;
        for (Type type : constructor.getGenericParameterTypes()) {
            formalParameters.add(new FormalParameterSymbol(toTypeUsage(type, Collections.emptyMap(), resolver), constructor.getParameters()[i].getName()));
            i++;
        }
        return formalParameters;
    }

    public static List<FormalParameterSymbol> formalParameters(Method method, Map<String, TypeUsage> typeVariables, SymbolResolver resolver) {
        List<FormalParameterSymbol> formalParameters = new ArrayList<>();
        int i=0;
        for (Type type : method.getGenericParameterTypes()) {
            formalParameters.add(new FormalParameterSymbol(toTypeUsage(type, typeVariables, resolver), method.getParameters()[i].getName()));
            i++;
        }
        return formalParameters;
    }

    public static TypeUsage toTypeUsage(Type type, Map<String, TypeUsage> typeVariables, SymbolResolver resolver) {
        if (type instanceof Class) {
            Class clazz = (Class)type;
            if (clazz.getCanonicalName().equals(void.class.getCanonicalName())) {
                return new UnitTypeNode();
            }
            if (clazz.isPrimitive()) {
                return PrimitiveTypeUsage.getByName(clazz.getName());
            }
            if (clazz.isArray()) {
                return new ArrayTypeUsage(toTypeUsage(clazz.getComponentType(), typeVariables, resolver));
            }
            TypeDefinition typeDefinition = new ReflectionBasedTypeDefinition((Class) type, resolver);
            ReferenceTypeUsage referenceTypeUsage = new ReferenceTypeUsage(typeDefinition);
            return referenceTypeUsage;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            TypeDefinition typeDefinition = new ReflectionBasedTypeDefinition((Class) parameterizedType.getRawType(), resolver);
            List<TypeUsage> typeParams = Arrays.stream(parameterizedType.getActualTypeArguments()).map((pt) -> toTypeUsage(pt, typeVariables, resolver)).collect(Collectors.toList());
            return new ReferenceTypeUsage(typeDefinition, typeParams);
        } else if (type instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable)type;
            return toTypeUsage(typeVariable, typeVariables, resolver);
        } else {
            throw new UnsupportedOperationException(type.getClass().getCanonicalName());
        }
    }


    public static JvmConstructorDefinition findConstructorAmong(List<JvmType> argsTypes, SymbolResolver resolver, List<Constructor> constructors) {
        List<MethodOrConstructor> methodOrConstructors = constructors.stream().map((m)->new MethodOrConstructor(m)).collect(Collectors.toList());
        MethodOrConstructor methodOrConstructor = findMethodAmong(argsTypes, resolver, methodOrConstructors, "constructor");
        if (methodOrConstructor == null) {
            throw new RuntimeException("unresolved constructor for " + argsTypes);
        }
        return ReflectionTypeDefinitionFactory.toConstructorDefinition(methodOrConstructor.constructor);
    }

    public static Constructor findConstructorAmongActualParams(List<ActualParamNode> argsTypes, SymbolResolver resolver, List<Constructor> constructors) {
        List<MethodOrConstructor> methodOrConstructors = constructors.stream().map((m)->new MethodOrConstructor(m)).collect(Collectors.toList());
        MethodOrConstructor methodOrConstructor = findMethodAmongActualParams(argsTypes, resolver, methodOrConstructors, "constructor");
        if (methodOrConstructor == null) {
            throw new RuntimeException("unresolved constructor for " + argsTypes);
        }
        return methodOrConstructor.constructor;
    }

    public static Method findMethodAmong(String name, List<JvmType> argsTypes, SymbolResolver resolver, boolean staticContext, List<Method> methods) {
        List<MethodOrConstructor> methodOrConstructors = methods.stream()
                .filter((m) -> Modifier.isStatic(m.getModifiers()) == staticContext)
                .filter((m) -> m.getName().equals(name))
                .map((m) -> new MethodOrConstructor(m)).collect(Collectors.toList());
        MethodOrConstructor methodOrConstructor = findMethodAmong(argsTypes, resolver, methodOrConstructors, name);
        if (methodOrConstructor == null) {
            throw new RuntimeException("unresolved method " + name + " for " + argsTypes);
        }
        return methodOrConstructor.method;
    }

    // EDC: find a method by a Java internal signature, for example: "java.io.PrintStream/println:(Ljava/lang/String;)V"
    public static Optional<Method> findMethodByJvmSignature(String jvmSignature) {
        if (!JvmNameUtils.isMethodSignature(jvmSignature)) {
            throw new RuntimeException("Firma jvm no es un método: "+jvmSignature);
        }
        String[] parts1 = jvmSignature.split(":");
        if (parts1.length != 2) {
            throw new RuntimeException("Firma jvm inválida: "+jvmSignature);
        }
        String[] parts2 = parts1[0].split("/");
        if (parts2.length != 2) {
            throw new RuntimeException("Firma jvm inválida"+jvmSignature);
        }

        try {
            String clazzName = parts2[0];
            String methodName = parts2[1];
            String methodSignature = parts1[1];
            String targetSignature = methodName+":"+methodSignature;
            Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(clazzName);
            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
                String signature = m.getName() +":" + ReflectionTypeDefinitionFactory.calcSignature(m);
                if (targetSignature.equals(signature)) {
                    return Optional.of(m);
                }
            }
            return Optional.empty();
        } catch(ClassNotFoundException e) {
            throw new RuntimeException("No existe la firma que estas buscando: "+jvmSignature);
        }
    }


    // EDC: find a method by a Java internal signature, for example: "java.io.PrintStream/println:(Ljava/lang/String;)V"
    public static Optional<Field> findFieldByJvmSignature(String jvmSignature) {
        String[] parts1 = jvmSignature.split(":");
        if (parts1.length != 2) {
            throw new RuntimeException("Firma jvm inválida: " + jvmSignature);
        }
        String[] parts2 = parts1[0].split("/");
        if (parts2.length != 2) {
            throw new RuntimeException("Firma jvm inválida" + jvmSignature);
        }

        try {
            String clazzName = parts2[0];
            String fieldName = parts2[1];
            String fieldSignature = parts1[1];
            Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(clazzName);
            Field[] fields = clazz.getFields();
            for (Field f : fields) {
                if (fieldName.equals(f.getName()) && fieldSignature.equals(ReflectionTypeDefinitionFactory.calcSignature(f))) {
                    return Optional.of(f);
                }
            }
            return Optional.empty();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No existe la firma que estas buscando: " + jvmSignature);
        }
    }

    public static Optional<Method> findMethodAmongActualParams(String name, List<ActualParamNode> argsTypes, SymbolResolver resolver, List<Method> methods) {
        List<MethodOrConstructor> methodOrConstructors = methods.stream()
                .filter((m) -> m.getName().equals(name))
                .map((m) -> new MethodOrConstructor(m)).collect(Collectors.toList());
        MethodOrConstructor methodOrConstructor = findMethodAmongActualParams(argsTypes, resolver, methodOrConstructors, name);
        if (methodOrConstructor == null) {
            return Optional.empty();
        }
        return Optional.of(methodOrConstructor.method);
    }

    public static Method findMethodAmong(String name, List<JvmType> argsTypes, SymbolResolver resolver,  List<Method> methods) {
        List<MethodOrConstructor> methodOrConstructors = methods.stream()
                .filter((m) -> m.getName().equals(name))
                .map((m) -> new MethodOrConstructor(m)).collect(Collectors.toList());
        MethodOrConstructor methodOrConstructor = findMethodAmong(argsTypes, resolver, methodOrConstructors, name);
        if (methodOrConstructor == null) {
            throw new RuntimeException("unresolved method " + name + " for " + argsTypes);
        }
        return methodOrConstructor.method;
    }


    private static MethodOrConstructor findMethodAmong(List<JvmType> argsTypes, SymbolResolver resolver, List<MethodOrConstructor> methods, String desc) {
        List<MethodOrConstructor> suitableMethods = new ArrayList<>();
        for (MethodOrConstructor method : methods) {
            if (method.getParameterCount() == argsTypes.size()) {
                boolean match = true;
                for (int i = 0; i < argsTypes.size(); i++) {
                }
                if (match) {
                    suitableMethods.add(method);
                }
            }
        }

        if (suitableMethods.size() == 0) {
            return null;
        } else if (suitableMethods.size() == 1) {
            return suitableMethods.get(0);
        } else {
            return findMostSpecific(suitableMethods, new AmbiguousCallException(null, desc, argsTypes), argsTypes, resolver);
        }
    }

    private static MethodOrConstructor findMethodAmongActualParams(List<ActualParamNode> argsTypes, SymbolResolver resolver, List<MethodOrConstructor> methods, String desc) {
        List<MethodOrConstructor> suitableMethods = new ArrayList<>();
        for (MethodOrConstructor method : methods) {
            if (method.getParameterCount() == argsTypes.size()) {
                boolean match = true;
                for (int i = 0; i < argsTypes.size(); i++) {
                    TypeUsage actualType = argsTypes.get(i).getValue().calcType();
                    TypeUsage formalType = ReflectionTypeDefinitionFactory.toTypeUsage(method.getParameterType(i), resolver);
                    if (!actualType.canBeAssignedTo(formalType)) {
                        match = false;
                    }
                }
                if (match) {
                    suitableMethods.add(method);
                }
            }
        }

        if (suitableMethods.size() == 0) {
            return null;
        } else if (suitableMethods.size() == 1) {
            return suitableMethods.get(0);
        } else {
            return findMostSpecific(suitableMethods,
                    new AmbiguousCallException(null, argsTypes, desc),
                    argsTypes.stream().map((ap)->ap.getValue().calcType().jvmType()).collect(Collectors.toList()),
                    resolver);
        }
    }

    private static MethodOrConstructor findMostSpecific(List<MethodOrConstructor> methods, AmbiguousCallException exceptionToThrow,
                                                        List<JvmType> argsTypes, SymbolResolver resolver) {
        MethodOrConstructor winningMethod = methods.get(0);
        for (MethodOrConstructor other : methods.subList(1, methods.size())) {
            if (isTheFirstMoreSpecific(winningMethod, other, argsTypes, resolver)) {
            } else if (isTheFirstMoreSpecific(other, winningMethod, argsTypes, resolver)) {
                winningMethod = other;
            } else if (!isTheFirstMoreSpecific(winningMethod, other, argsTypes, resolver)) {
                // neither is more specific
                throw exceptionToThrow;
            }
        }
        return winningMethod;
    }

    private static boolean isTheFirstMoreSpecific(MethodOrConstructor first, MethodOrConstructor second,
                                                  List<JvmType> argsTypes, SymbolResolver resolver) {
        boolean atLeastOneParamIsMoreSpecific = false;
        if (first.getParameterCount() != second.getParameterCount()) {
            throw new IllegalArgumentException();
        }
        for (int i=0;i<first.getParameterCount();i++){
            Class<?> paramFirst = first.getParameterType(i);
            Class<?> paramSecond = second.getParameterType(i);
            if (isTheFirstMoreSpecific(paramFirst, paramSecond, argsTypes.get(i), resolver)) {
                atLeastOneParamIsMoreSpecific = true;
            } else if (isTheFirstMoreSpecific(paramSecond, paramFirst, argsTypes.get(i), resolver)) {
                return false;
            }
        }

        return atLeastOneParamIsMoreSpecific;
    }

    private static boolean isTheFirstMoreSpecific(Class<?> firstType, Class<?> secondType, JvmType targetType, SymbolResolver resolver) {
        boolean firstIsPrimitive = firstType.isPrimitive();
        boolean secondIsPrimitive = secondType.isPrimitive();
        boolean targetTypeIsPrimitive = targetType.isPrimitive();

        // it is a match or a primitive promotion
        if (targetTypeIsPrimitive && firstIsPrimitive && !secondIsPrimitive) {
            return true;
        }
        if (targetTypeIsPrimitive && !firstIsPrimitive && secondIsPrimitive) {
            return false;
        }

        if (firstType.isPrimitive() || firstType.isArray()) {
            return false;
        }
        if (secondType.isPrimitive() || secondType.isArray()) {
            return false;
        }
        // TODO consider generic parameters?
        ReflectionBasedTypeDefinition firstDef = new ReflectionBasedTypeDefinition(firstType, resolver);
        ReflectionBasedTypeDefinition secondDef = new ReflectionBasedTypeDefinition(secondType, resolver);
        TypeUsage firstTypeUsage = new ReferenceTypeUsage(firstDef);
        TypeUsage secondTypeUsage = new ReferenceTypeUsage(secondDef);
        return firstTypeUsage.canBeAssignedTo(secondTypeUsage) && !secondTypeUsage.canBeAssignedTo(firstTypeUsage);
    }
}
