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

import org.testng.annotations.Test;

import java.util.*;

/**
 * @author hanpengfei
 * @since 1.0
 */
@SuppressWarnings("SuspiciousMethodCalls")
public class ClassUtilsTest {

    private static final ArrayList<Class<?>> SORT_EXPECTED = new ArrayList<>(8);

    static {
        SORT_EXPECTED.add(PlainClass.class);
        SORT_EXPECTED.add(SuperclassA.class);
        SORT_EXPECTED.add(SuperclassB.class);
        SORT_EXPECTED.add(SuperclassC.class);
        SORT_EXPECTED.add(InterfaceA.class);
        SORT_EXPECTED.add(InterfaceB.class);
        SORT_EXPECTED.add(InterfaceC.class);
        SORT_EXPECTED.add(Object.class);
    }

    @Test
    public void testGetDefaultClassLoader() {
        assert ClassUtils.getDefaultClassLoader() != null;
    }

    @Test
    public void testGetInterfaces() {
        Set<Class<? super PlainClass>> interfaces = ClassUtils.getInterfaces(PlainClass.class);

        assert interfaces.size() == 3;
        assert interfaces.contains(InterfaceA.class);
        assert interfaces.contains(InterfaceB.class);
        assert interfaces.contains(InterfaceC.class);
    }

    @Test
    public void testGetSuperclasses() {
        Set<Class<? super PlainClass>> superclasses = ClassUtils.getSuperclasses(PlainClass.class);

        assert superclasses.size() == 4;
        assert superclasses.contains(SuperclassA.class);
        assert superclasses.contains(SuperclassB.class);
        assert superclasses.contains(SuperclassC.class);
        assert superclasses.contains(Object.class);
    }

    @Test
    public void testGetClasses() {
        Set<Class<? super PlainClass>> supers = ClassUtils.getClasses(PlainClass.class);

        assert supers.size() == 7;

        assert supers.contains(SuperclassA.class);
        assert supers.contains(SuperclassB.class);
        assert supers.contains(SuperclassC.class);

        assert supers.contains(InterfaceA.class);
        assert supers.contains(InterfaceB.class);
        assert supers.contains(InterfaceC.class);

        assert supers.contains(Object.class);
    }

    @Test
    public void testGetInterfacesWithinBounds() {
        Set<Class<InterfaceC>> interfaces = ClassUtils.getInterfacesWithinBounds(PlainClass.class, InterfaceC.class);

        assert interfaces.size() == 2;
        assert interfaces.contains(InterfaceA.class);
        assert interfaces.contains(InterfaceB.class);
        assert !interfaces.contains(InterfaceC.class);
    }

    @Test
    public void testGetSuperclassesWithinBoundsByInterface() {
        Set<Class<InterfaceB>> superclasses = ClassUtils.getSuperclassesWithinBounds(PlainClass.class, InterfaceB.class);

        assert superclasses.size() == 2;
        assert superclasses.contains(SuperclassA.class);
        assert superclasses.contains(SuperclassB.class);
    }

    @Test
    public void testGetSuperclassesWithinBoundsByClass() {
        Set<Class<SuperclassC>> superclasses = ClassUtils.getSuperclassesWithinBounds(PlainClass.class, SuperclassC.class);

        assert superclasses.size() == 2;
        assert superclasses.contains(SuperclassA.class);
        assert superclasses.contains(SuperclassB.class);
    }

    @Test
    public void testGetClassesWithinBoundsByInterface() {
        Set<Class<InterfaceB>> supers = ClassUtils.getClassesWithinBounds(PlainClass.class, InterfaceB.class);

        assert supers.size() == 3;
        assert supers.contains(SuperclassA.class);
        assert supers.contains(SuperclassB.class);
        assert supers.contains(InterfaceA.class);
    }

    @Test
    public void testGetClassesWithinBoundsByClass() {
        Set<Class<SuperclassC>> supers = ClassUtils.getClassesWithinBounds(PlainClass.class, SuperclassC.class);

        assert supers.size() == 2;
        assert supers.contains(SuperclassA.class);
        assert supers.contains(SuperclassB.class);
    }

    @Test
    public void testSortByCollection() {
        Collection<Class<? super PlainClass>> supers = ClassUtils.getClasses(PlainClass.class);
        supers.add(PlainClass.class);
        Collection<Class<?>> sort = ClassUtils.sort(supers);

        assert Objects.deepEquals(sort, SORT_EXPECTED);
    }

    @Test
    public void testSortByArray() {
        Collection<Class<? super PlainClass>> supers = ClassUtils.getClasses(PlainClass.class);
        supers.add(PlainClass.class);

        ArrayList<Class> classes = new ArrayList<>(supers.size());
        classes.addAll(supers);

        Class[] array = classes.toArray(new Class[0]);
        Class<?>[] sortedArray = ClassUtils.sort(array);

        assert Arrays.deepEquals(SORT_EXPECTED.toArray(new Class[0]), sortedArray);
    }

    @Test
    public void testIsAvailable() {
        assert ClassUtils.isAvailable(getClass().getName());
        assert !ClassUtils.isAvailable(getClass().getName() + "ForTest");
    }

    /* interfaces */
    interface InterfaceA extends InterfaceB, InterfaceC {
    }

    interface InterfaceB extends InterfaceC {
    }

    interface InterfaceC {
    }

    /* subclass */
    private class PlainClass extends SuperclassA implements InterfaceC {
    }

    /* superclasses */
    class SuperclassA extends SuperclassB implements InterfaceA {
    }

    class SuperclassB extends SuperclassC implements InterfaceB {
    }

    class SuperclassC {
    }
}