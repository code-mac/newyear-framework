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

package com.apehat.newyear.core.env;

import com.apehat.newyear.util.ClassUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Be used to boot all framework plugins.
 *
 * @author hanpengfei
 * @since 1.0
 */
public final class BootPluginLoader implements PluginLoader {

    private static final String BOOT_PACKAGE_NAME = "com.apehat.newyear";

    private static final Set<Plugin> BOOT_PLUGINS = new HashSet<>();

    public BootPluginLoader() {
        ClassLoader clToUse = ClassUtils.getDefaultClassLoader();
        if (clToUse == null) {
            clToUse = getClass().getClassLoader();
        }

        try {
            Enumeration<URL> resources = clToUse.getResources(BOOT_PACKAGE_NAME);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
            }
        } catch (IOException e) {
            throw new BootException();
        }
    }

    @Override
    public PluginLoader getParent() {
        return null;
    }

    @Override
    public void loadPluginsFrom(String packageName) {
        throw new UnsupportedOperationException(getClass() + " not support manual launch type.");
    }

    @Override
    public void loadPlugin(Plugin plugin) {

    }

    @Override
    public Plugin getPlugins() {
        return null;
    }
}
