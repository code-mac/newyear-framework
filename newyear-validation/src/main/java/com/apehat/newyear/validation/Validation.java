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

package com.apehat.newyear.validation;

import com.apehat.newyear.validation.exception.IllegalConflictException;

import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class Validation {

    private Validation() {
    }

    public static boolean equalsOne(Object o1, Object... o) {
        if (o == null || o.length == 0) {
            return false;
        }
        for (Object obj : o) {
            if (Objects.equals(o1, obj)) {
                return true;
            }
        }
        return false;
    }

    public static void requireTrue(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }

    public static void requireTrue(boolean condition, String message, Object... args) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static void requireFalse(boolean condition) {
        if (condition) {
            throw new IllegalArgumentException();
        }
    }

    public static void requireFalse(boolean condition, String message, Object... args) {
        if (condition) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static void requireNotEquals(Object o1, Object o2) {
        if (Objects.equals(o1, o2)) {
            throw new IllegalConflictException();
        }
    }

    public static void requireNotEquals(Object o1, Object o2, String message, Object... args) {
        if (Objects.equals(o1, o2)) {
            throw new IllegalConflictException(String.format(message, args));
        }
    }

    public static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public static <T> T requireNonNull(T obj, String message, Object... args) {
        if (obj == null) {
            throw new NullPointerException(String.format(message, args));
        }
        return obj;
    }
}
