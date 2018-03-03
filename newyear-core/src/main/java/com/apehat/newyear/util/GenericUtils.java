/*
 * Copyright ApeHat.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apehat.newyear.util;

import com.apehat.newyear.validation.Validation;
import com.apehat.newyear.validation.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class GenericUtils {

    private GenericUtils() {
    }

    /**
     * Get the genetic parameters by specified class and it's superclass (or
     * interface) - a generic.
     * <p>
     * Note: current version, the lambda expression is not be supported
     *
     * @param aClass   the class will find it's generic parameter
     * @param aGeneric a generic class or interface
     *                 //     * @param <T>      the type of class
     * @return the generic parameters. If subclasses hadn't statement, will
     * use default generic parameter (system default is Object.class).
     * @throws NullPointerException     specified class or genetic is null.
     * @throws IllegalArgumentException specified generic isn't a generic type
     */
    public static Class<?>[] getGenericParameters(Class<?> aClass, Class<?> aGeneric) {
        Objects.requireNonNull(aClass, "Must specified class");
        Objects.requireNonNull(aClass, "Must specified a generic type");

        Validation.requireTrue(isGenericType(aClass), "%s isn't generic type.", aGeneric);

        ParameterizedType pt = getParameterizedTypeOf(aClass, aGeneric);

        Class<?>[] parameters = new Class[0];

        if (pt == null) {
            ArrayList<Class<?>> list = new ArrayList<>();
            TypeVariable<? extends Class<?>>[] typeParameters = aGeneric.getTypeParameters();
            for (TypeVariable<? extends Class<?>> variable : typeParameters) {
                Type[] bounds = variable.getBounds();
                for (Type type : bounds) {
                    String typeName = type.getTypeName();
                    // remove generic symbol
                    String className = StringUtils.nonGenericTypeName(typeName);
                    Class<?> clazz = ClassUtils.forName(className);
                    assert clazz != null;
                    list.add(clazz);
                }
            }
            parameters = list.toArray(parameters);
        } else {
            Type[] types = pt.getActualTypeArguments();
            int len;
            if (types != null && (len = types.length) > 0) {
                parameters = new Class[len];

                for (int i = 0; i < len; ++i) {
                    Type type = types[i];
                    Class<?> clazz = ClassUtils.forName(type.getTypeName());
                    parameters[i] = clazz;
                }
            }
        }
        return parameters;
    }

    private static boolean isGenericType(Class<?> aClass) {
        return aClass.getTypeParameters().length > 0;
    }

    private static ParameterizedType findByInterface(Class<?> aClass, Class<?> aGeneric) {
        assert aGeneric.isInterface();

        ParameterizedType pt = null;
        Type[] genericInterfaces = aClass.getGenericInterfaces();

        Class<?>[] interfaces = aClass.getInterfaces();

        // find generic interface by interface
        for (Class<?> aInterface : interfaces) {
            if (aGeneric.isAssignableFrom(aInterface)) {
                // find type by name
                String interfaceName = aInterface.getName();
                for (Type type : genericInterfaces) {
                    String typeName = type.getTypeName();

                    if (Validation.equalsOne(interfaceName, typeName,
                            StringUtils.nonGenericTypeName(typeName)) &&
                            type instanceof ParameterizedType) {
                        return (ParameterizedType) type;
                    }
                }
            }
        }

        for (Class<?> aInterface : interfaces) {
            //noinspection unchecked
            pt = findByInterface(aInterface, aGeneric);
            if (pt != null) {
                break;
            }
        }
        return pt;
    }

    private static ParameterizedType findByClass(Class<?> aClass, Class<?> aGeneric) {
        ParameterizedType pt = null;
        Class<?> superclass = aClass.getSuperclass();
        if (superclass != null && aGeneric.isAssignableFrom(superclass)) {
            // have super class
            // super class is sub of generic type - find generic super class
            Type genericSuperclass = aClass.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                // generic by this super class
                pt = (ParameterizedType) genericSuperclass;
            } else {
                // generic by it's superclass and interfaces
                //noinspection unchecked
                pt = findByClass(superclass, aGeneric);

                // if specified generic type is interface - need find by
                // it's interfaces
                if (pt == null && aGeneric.isInterface()) {
                    // find by interfaces
                    pt = findByInterface(aClass, aGeneric);
                }
            }
        }
        return pt;
    }

    @Nullable("Cannot find parameterized types")
    private static ParameterizedType getParameterizedTypeOf(Class<?> aClass, Class<?> aGeneric) {
        Objects.requireNonNull(aClass, "Must specified subclass");
        Objects.requireNonNull(aGeneric, "Must specified super generic");

        Validation.requireTrue(isGenericType(aClass), "[%s] isn't generic type.", aClass);

        ParameterizedType pt = findByClass(aClass, aGeneric);

        if (pt == null && aGeneric.isInterface()) {
            pt = findByInterface(aClass, aGeneric);
        }
        return pt;
    }


    private static Collection<ParameterizedType> getParameterizedTypes(Class<?> aClass) {
        ArrayList<ParameterizedType> types = new ArrayList<>();

        Type genericSuperclass = aClass.getGenericSuperclass();
        checkParameterizedTypes(types, genericSuperclass);

        Type[] genericInterfaces = aClass.getGenericInterfaces();
        for (Type type : genericInterfaces) {
            checkParameterizedTypes(types, type);
        }
        return types;
    }

    private static void checkParameterizedTypes(Collection<ParameterizedType> collection, Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            collection.add(pt);

            String typeName = pt.getTypeName();
            Class<?> clazz = ClassUtils.forName(typeName);
            if (clazz != null) {
                collection.addAll(getParameterizedTypes(clazz));
            }
        }
    }
}
