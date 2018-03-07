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

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface PluginLoader {

    /**
     * Returns the parent of this loader.
     *
     * @return the parent loader of current, or null if no parent
     */
    PluginLoader getParent();

    /**
     * Load plugins form specified package
     *
     * @param packageName the package name
     */
    void loadPluginsFrom(String packageName);

    /**
     * Add type to current environment. If {@code AbstractPlugin Auto Scan} be
     * closed, the environment will only have these plugins.
     *
     * @param plugin the type will be add to environment
     */
    void loadPlugin(Plugin plugin);

    /**
     * Returns all plugins of current loader
     *
     * @return the type in this manager
     */
    Plugin getPlugins();
}
