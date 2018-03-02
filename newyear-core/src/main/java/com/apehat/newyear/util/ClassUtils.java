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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ClassUtils {

    /**
     * The extension of class file: ".class"
     */
    public static final String CLASS_EXTENSION = ".class";

    /**
     * The separator of package: "."
     */
    public static final String PACKAGE_SEPARATOR = ".";

    /**
     * Default class comparator. The order in which they are sorted,
     * like: plain class - abstract class - interface - Object.class
     */
    private static final Comparator<Class<?>> CLASS_COMPARATOR = (o1, o2) -> {
        if (o1.equals(o2)) {
            return 0;
        } else if (subOf(o2, o1)) {
            // o1 is the superclass of o2
            return 1;
        } else if (subOf(o1, o2)) {
            return -1;
        } else if (o1.isInterface() || o2.isInterface()) {
            // for two classes that have no human relation -
            // always ensure (abstract) class is before interface
            if (o1.isInterface()) {
                return 1;
            }
            return -1;
        } else if (Modifier.isAbstract(o1.getModifiers())) {
            // abstract class always after than plain class
            return 1;
        } else {
            return -1;
        }
    };

    private ClassUtils() {
    }

    /**
     * Returns the current class path array.
     *
     * @return current class path array.
     */
    public static String[] getClassPaths() {
        return System.getProperty("java.class.path").split(File.pathSeparator);
    }

    /**
     * Returns the default class loader.
     *
     * @return null, when default cannot be get;
     * otherwise, a class loader.
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;

        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ignore) {
            // cannot access thread context ClassLoader - filling back...
        }
        if (cl == null) {
            // No ClassLoader of thread context - use ClassLoader of this class
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ignore) {
                    // Cannot access System ClassLoader. Oh, well... may be the caller can live with null...
                }
            }
        }
        return cl;
    }

    /**
     * Construct a instance with-non argument of specified class.
     *
     * @param aClass the class to construct.
     * @param <T>    the type of specified class.
     * @return the instance of class
     * @throws NoSuchMethodException cannot find not argument construct.
     * @throws LinkageError          assess construct failure.
     */
    public static <T> T newInstance(Class<T> aClass) throws NoSuchMethodException {
        try {
            Constructor<T> constructor = Validation.requireNonNull(aClass).getConstructor();
            return constructor.newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new LinkageError(e.getMessage());
        }
    }

    /**
     * Returns the package name of specified class.
     *
     * @param aClass the class will to be get it's package name
     * @return the package name of specified class
     * @throws NullPointerException specified class is null
     */
    public static String getPackageName(Class<?> aClass) {
        return Objects.requireNonNull(aClass).getPackage().getName();
    }

    /**
     * Returns the superclasses of specified class.
     *
     * @param aClass a subclass
     * @param <T>    the type of subclass
     * @return the class array, contains all super classes of specified class.
     * @throws NullPointerException specified class is null
     * @see #getSuperclassesWithinBounds(Class, Class)
     */
    public static <T> Set<Class<? super T>> getSuperclasses(Class<T> aClass) {
        Objects.requireNonNull(aClass, "Must specified a class");

        Set<Class<? super T>> superclasses = new HashSet<>();
        Class<? super T> currentSuper = aClass.getSuperclass();
        while (currentSuper != null) {
            superclasses.add(currentSuper);
            currentSuper = currentSuper.getSuperclass();
        }
        return superclasses;
    }

    /**
     * Get all superclasses within upper. i.e. all return value not only is
     * the superclass of lower bound, but also is the subclass of upper bound.
     * <p>
     * If upper bound hadn't specified, by default upper bound will be set to
     * {@code Object.class}.
     *
     * @param lower the lower bound. had implemented specified upper bound
     * @param upper the upper bound, had be implemented by specified lower
     *              bound
     * @param <T>   the type of upper bound
     * @return the classes set (don't include lower and upper bounds).
     * The value of set is super of {@code lower}, and is sub of {@code upper}.
     * If the classes not exists, what had be extended by {@code lower}, and
     * it had extended (or implemented) {@code upper}, will return an empty set.
     * @throws NullPointerException specified lower bound is null
     */
    public static <T> Set<Class<T>> getSuperclassesWithinBounds(Class<? extends T> lower, Class<T> upper) {
        Objects.requireNonNull(lower, "Must specified a class");

        if (upper == null) {
            // After type erasures, upper is Class<Object>, So Object.class always can safety convert
            //noinspection unchecked
            upper = (Class<T>) Object.class;
        }

        Set<Class<T>> superClasses = new HashSet<>();

        Class<?> currentSuperclass = lower.getSuperclass();
        while (subOf(currentSuperclass, upper)) {
            // the current superclass is sub of upper. So, at there can safety convert
            @SuppressWarnings("unchecked") Class<T> inBounds = (Class<T>) currentSuperclass;
            superClasses.add(inBounds);
            currentSuperclass = currentSuperclass.getSuperclass();
        }
        return superClasses;
    }


    /**
     * Returns the interfaces of specified class.
     *
     * @param aClass a class
     * @return the interfaces set of specified class. If specified had not
     * implemented any interface. will return a empty set
     * @throws NullPointerException specified class is null
     */
    public static <T> Set<Class<? super T>> getInterfaces(Class<T> aClass) {
        Objects.requireNonNull(aClass, "Must specified a class");

        // The interfaces class of specified class,
        // always is super of specified class
        // so can safety convert
        @SuppressWarnings("unchecked") Class<? super T>[] currentInterfaces =
                (Class<? super T>[]) aClass.getInterfaces();

        Set<Class<? super T>> interfaces = new HashSet<>(Arrays.asList(currentInterfaces));

        for (Class<? super T> currentInterface : currentInterfaces) {
            interfaces.addAll(getInterfaces(currentInterface));
        }
        // find interfaces of superclasses
        for (Class<? super T> superclass : getSuperclasses(aClass)) {
            interfaces.addAll(getInterfaces(superclass));
        }
        return interfaces;
    }

    /**
     * Returns all interfaces of specified subclass, and every interface had
     * implemented specified superinterface.
     *
     * @param lower a subclass
     * @param upper a interface class
     * @param <T>   the type of specified superclass
     * @return the interfaces of specified subclass.
     * @throws NullPointerException     specified subclass or interface is null
     * @throws IllegalArgumentException specified interface class doesn't belong
     *                                  to a interface;
     */
    public static <T> Set<Class<T>> getInterfacesWithinBounds(Class<? extends T> lower, Class<T> upper) {
        Objects.requireNonNull(lower, "Must specified a class");
        Objects.requireNonNull(upper, "Must specified upper");

        if (!Object.class.equals(upper) && !upper.isInterface()) {
            throw new IllegalArgumentException(
                    "Bounds must is an interface class (or Object.class)");
        }

        Set<Class<T>> interfaces = new HashSet<>();

        Class<?>[] currentInterfaces = lower.getInterfaces();
        for (int i = 0, len = currentInterfaces.length; i < len && subOf(currentInterfaces[i], upper); i++) {
            @SuppressWarnings("unchecked") Class<T> inBounds = (Class<T>) currentInterfaces[i];
            interfaces.add(inBounds);
            interfaces.addAll(getInterfacesWithinBounds(inBounds, upper));
        }

        // find interfaces by superclass
        for (Class<T> superclass : getSuperclassesWithinBounds(lower, upper)) {
            interfaces.addAll(getInterfacesWithinBounds(superclass, upper));
        }

        return interfaces;
    }

    /**
     * Returns all super class and interfaces of specified class
     *
     * @param aClass a subclass
     * @param <T>    the type of subclass
     * @return all superclass and interfaces of specified subclass
     * @throws NullPointerException specified class is null
     */
    public static <T> Set<Class<? super T>> getClasses(Class<T> aClass) {
        Objects.requireNonNull(aClass, "Must specified a class");

        Set<Class<? super T>> classes = new HashSet<>(getInterfaces(aClass));
        classes.addAll(getSuperclasses(aClass));
        return classes;
    }

    /**
     * Get all superclasses and interface within bounds. i.e. all return
     * value not only is the superclass of specified class, but also is the
     * subclass of specified upper.
     * <p>
     * If upper hadn't specified, by default upper will be set to
     * {@code Object.class}.
     *
     * @param lower the lower bound. had implemented specified upper bound
     * @param upper the upper bound, had be implemented by specified lower
     *              bound
     * @param <T>   the type of upper bound
     * @return the classes set (don't include lower and upper bounds).
     * The value of set is super of {@code lower}, and is sub of {@code upper}.
     * If the classes not exists, what had be implemented {@code lower}, and
     * it had implemented {@code upper}, will return an empty set.
     * @throws NullPointerException specified lower bound is null
     */
    public static <T> Set<Class<T>> getClassesWithinBounds(Class<? extends T> lower, Class<T> upper) {
        Objects.requireNonNull(lower, "Must specified subclass");

        if (upper == null) {
            // After type erasures, upper is Class<Object>, So Object.class always can safety convert
            //noinspection unchecked
            upper = (Class<T>) Object.class;
        }

        if (lower == upper) {
            return Collections.emptySet();
        }
        Set<Class<T>> classes = getSuperclassesWithinBounds(lower, upper);
        if (Object.class.equals(upper) || upper.isInterface()) {
            classes.addAll(getInterfacesWithinBounds(lower, upper));
        }
        return classes;
    }

    /**
     * Load a class by specified class name and {@code defaultClassLoader}.
     *
     * @param name the global qualified name of class (contains package)
     * @return null, if the class of specified name is not available.
     * otherwise, the class of specified name.
     * @throws NullPointerException specified name is null.
     * @see #getDefaultClassLoader()
     * @see #forName(String, ClassLoader)
     */
    public static Class<?> forName(String name) {
        return forName(name, getDefaultClassLoader());
    }

    /**
     * Load a class by specified class name and and specified classloader.
     * If class loader is null, will use {@code defaultClassLoader}.
     *
     * @param name the global qualified name of class (contains package)
     * @return null, if the class of specified name is not available.
     * otherwise, the class of specified name.
     * @throws NullPointerException specified name is null.
     * @see #getDefaultClassLoader()
     * @see #forName(String)
     */
    public static Class<?> forName(String name, @Nullable ClassLoader loader) {
        Objects.requireNonNull(name, "Must specific a class name");

        if (loader == null) {
            loader = getDefaultClassLoader();
        }

        Class<?> aClass = null;
        if (isAvailable(name)) {
            try {
                aClass = loader.loadClass(name);
            } catch (ClassNotFoundException ignore) {
                return null;
            }
        }
        return aClass;
    }

    /**
     * Sort classes by {@link #CLASS_COMPARATOR}. Specified array will be changed.
     *
     * @param classes the classes array, will be sorted
     * @return sorted class array
     */
    public static Class<?>[] sort(Class<?>[] classes) {
        Class<?>[] copy = Arrays.copyOf(classes, classes.length);
        Arrays.sort(copy, CLASS_COMPARATOR);
        return copy;
    }

    /**
     * Sort classes by {@link #CLASS_COMPARATOR}. Specified collection will not
     * be changed.
     *
     * @param classes the class collection, will be sorted.
     * @return sorted class collection
     */
    public static Collection<Class<?>> sort(Collection<? extends Class<?>> classes) {
        ArrayList<Class<?>> list = new ArrayList<>(classes.size());
        list.addAll(classes);
        list.sort(CLASS_COMPARATOR);
        return list;
    }

    /**
     * Sort classes by specified comparator.
     *
     * @param classes    the class will be sort.
     * @param comparator the comparator to compare classes
     * @return sorted classes
     * @throws NullPointerException specified classes or comparator is null.
     */
    public static <T> Collection<Class<T>> sort(
            Collection<Class<T>> classes, Comparator<? super Class<T>> comparator) {
        Objects.requireNonNull(classes, "Must specific class collection");
        Objects.requireNonNull(comparator, "Must specific comparator");

        ArrayList<Class<T>> list = new ArrayList<>(classes.size());
        list.addAll(classes);
        list.sort(comparator);
        return list;
    }

    /**
     * Determine whether the class of specified class name is available.
     *
     * @param className the class name to be checkPermission
     * @return true, the class of specified class name can be used;
     * otherwise, false.
     * @throws NullPointerException specified className is null.
     */
    public static boolean isAvailable(String className) {
        Objects.requireNonNull(className, "Must specified a class name.");

        try {
            ClassLoader loader = getDefaultClassLoader();
            loader.loadClass(className);
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns all available class in specified location
     *
     * @param location the location will be scan.
     * @return all classes, in specified location. If non any classes, will return a
     * empty class array.
     * @throws NullPointerException specified location is null.
     * @throws IOException          specified location not exists in file system.
     */
    public static Class<?>[] getClassesInLocation(String location) throws IOException {
        Objects.requireNonNull(location, "Must specified location");

        File file = new File(location);
        if (!file.exists()) {
            throw new FileNotFoundException("Cannot find classes by "
                    + location + ", because is not exists is file system.");
        }
        ArrayList<Class<?>> classes = new ArrayList<>();

        if (file.isDirectory()) {
            classes.addAll(Arrays.asList(getClassesInLocation(location, file)));
        } else if (ResourceUtils.isJarFile(file)) {
            classes.addAll(Arrays.asList(parseJarFile(file)));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private static Class<?>[] parseJarFile(File file) throws IOException {
        assert file != null;
        assert ResourceUtils.isJarFile(file);

        ArrayList<Class<?>> classes = new ArrayList<>();

        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
                String entryName = entry.getName();
                if (entryName.endsWith(CLASS_EXTENSION)) {
                    String className = entryName.substring(0, entryName.length() - CLASS_EXTENSION.length())
                            .replaceAll(File.separator, PACKAGE_SEPARATOR);
                    Class<?> aClass = forName(className);
                    if (aClass != null) {
                        classes.add(aClass);
                    }
                }
            }
        }
        return classes.toArray(new Class[0]);
    }

    private static Class<?>[] getClassesInLocation(String classpath, File file) throws IOException {
        ArrayList<Class<?>> classes = new ArrayList<>();

        if (ResourceUtils.isJarFile(file)) {
            classes.addAll(Arrays.asList(parseJarFile(file)));
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; files != null && i < files.length; i++) {
                File child = files[i];
                classes.addAll(Arrays.asList(getClassesInLocation(classpath, child)));
            }
        } else {
            String path = file.getAbsolutePath();
            if (path.endsWith(CLASS_EXTENSION)) {
                // is class file
                // the path only package and class file
                String location = path.substring(classpath.length() + 1);
                String className = location.substring(0, location.length() - CLASS_EXTENSION.length())
                        .replaceAll(File.separator, PACKAGE_SEPARATOR);
                Class<?> aClass = forName(className);
                if (aClass != null) {
                    classes.add(aClass);
                }
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private static boolean subOf(Class<?> expectSub, Class<?> expectSuper) {
        return expectSub != null && expectSuper != null
                && !Objects.equals(expectSub, expectSuper)
                && expectSuper.isAssignableFrom(expectSub);
    }
}
