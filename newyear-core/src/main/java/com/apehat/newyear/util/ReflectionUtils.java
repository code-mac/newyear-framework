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

import java.lang.reflect.Modifier;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Returns the caller class.
     *
     * @return null, if the caller of current method is main method.
     * otherwise, the caller class.
     * @throws IllegalStateException load caller class failure.
     */
    public static Class<?> getCallerClass() {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        String caller = stack[1].getClassName();

        for (int i = 2; i < stack.length; i++) {
            String name = stack[i].getClassName();

            if (!caller.equals(name)) {
                ClassLoader cl = ClassUtils.getDefaultClassLoader();
                if (cl == null) {
                    cl = ReflectionUtils.class.getClassLoader();
                }
                Class<?> aClass = ClassUtils.forName(name, cl);
                if (aClass == null) {
                    throw new IllegalStateException("Load " + name + "failure");
                }
                return aClass;
            }
        }
        return null;
    }


}
