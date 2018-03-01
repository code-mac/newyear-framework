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

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class EnvUtils {

    private static final String[] CLASS_PATH_ARRAY = System.getProperty("java.class.path").split(File.pathSeparator);
    private static final Set<String> CLASS_PATH_SET = new HashSet<>(Arrays.asList(CLASS_PATH_ARRAY));

    public static String[] getClassPathArray() {
        return CLASS_PATH_ARRAY;
    }

    public static boolean isInClassPath(String location) {
        if (isWindows()) {
            location = location.replaceAll(ResourceUtils.PATH_SEPARATOR, "\\");
        }

        if (CLASS_PATH_SET.contains(location)) {
            return true;
        }
        for (String classpath : CLASS_PATH_SET) {
            if (location.startsWith(classpath)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWindows() {
        return !"/".equals(File.separator);
    }

}
