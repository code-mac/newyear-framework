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
import com.apehat.newyear.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class Bootstrap {

    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    private static boolean started = false;

    private Bootstrap() {
    }

    /**
     * Start framework with command line args.
     *
     * @param args the command line args
     */
    public static void start(String... args) {
        if (!started) {
            if (logger.isInfoEnabled()) {
                logger.info("New Year Framework is starting...");
                logger.info("New Year Application is starting.");
            }

            initialize(new CommandLineArgs(args));
        }
    }

    private static void initialize(CommandLineArgs args) {
        // find start entrance
        Class<?> entrance = getEntrance();

        // scan plugins form global
        Plugin[] plugins = getPlugins();
        if (plugins.length > 0) {
            Set<Plugin> unrunPlugin = new HashSet<>(Arrays.asList(plugins));
            runPlugin(unrunPlugin, entrance, args);
        } else if (logger.isDebugEnabled()) {
            logger.debug("No type found.");
        }

        started = true;

        if (logger.isInfoEnabled()) {
            logger.info("New Year Application started successfully!");
        }
    }

    private static Plugin[] getPlugins() {
        Set<Class<? extends Plugin>> pluginClasses = new HashSet<>();
        String[] paths = ClassUtils.getClassPaths();
        for (String path : paths) {
            try {
                Class<?>[] classes = ClassUtils.getClassesInLocation(path);
                for (Class<?> aClass : classes) {
                    @SuppressWarnings("unchecked") Set<Class<?>> supers
                            = (Set<Class<?>>) ClassUtils.getClasses(aClass);
                    if (supers.contains(Plugin.class) && ReflectionUtils.canInstantiated(aClass)) {
                        //noinspection unchecked
                        pluginClasses.add((Class<? extends Plugin>) aClass);
                    }
                }
            } catch (IOException e) {
                throw new InitializationException(e);
            }
        }

        ArrayList<Plugin> plugins = new ArrayList<>();
        for (Class<? extends Plugin> pluginClass : pluginClasses) {
            if (ReflectionUtils.canInstantiated(pluginClass)) {
                try {
                    plugins.add(ClassUtils.newInstance(pluginClass));
                } catch (NoSuchMethodException e) {
                    throw new InitializationException(e);
                }
            }
        }

        pluginClasses.clear();
        return plugins.toArray(new Plugin[plugins.size()]);
    }

    private static void runPlugin(Set<Plugin> unrunPlugins, Class<?> entrance, CommandLineArgs args) {
        assert unrunPlugins != null;

        Set<Plugin> copy = new HashSet<>(unrunPlugins);

        for (Plugin plugin : unrunPlugins) {
            Class<?>[] dependencies = plugin.dependencies();
            if (dependencies == null || dependencies.length == 0) {
                plugin.launch(entrance, args);
                copy.remove(plugin);
            } else {
                Set<Class<?>> depends = new HashSet<>(Arrays.asList(dependencies));
                boolean dependsUnrun = false;
                for (Plugin unrun : unrunPlugins) {
                    Class<? extends Plugin> aClass = unrun.getClass();
                    if (depends.contains(aClass)) {
                        dependsUnrun = true;
                        break;
                    }
                }
                if (!dependsUnrun) {
                    plugin.launch(entrance, args);
                    copy.remove(plugin);
                }
            }
        }

        if (unrunPlugins.size() > 0) {
            runPlugin(copy, entrance, args);
        }
    }

    private static Class<?> getEntrance() {
        return ReflectionUtils.getCallerClass();
    }
}
