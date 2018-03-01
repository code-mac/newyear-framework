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

import java.util.regex.Pattern;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class StringUtils {

    private static final Pattern GENERIC_TYPE_NAME_PATTERN = Pattern.compile("<.*>");

    private StringUtils() {
    }

    public static boolean hashLength(CharSequence sequence) {
        return sequence != null && sequence.length() > 0;
    }

    public static boolean hasLength(String str) {
        return str != null && !str.isEmpty();
    }

    public static String nonGenericTypeName(String name) {
        Validation.requireNonNull(name, "Must specified generic type name.");
        return GENERIC_TYPE_NAME_PATTERN.matcher(name).replaceAll("");
    }
}
