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

package com.apehat.newyear.core.annotation;

import com.apehat.newyear.util.ClassUtils;
import com.apehat.newyear.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class PluginScanner {

    public static DefinitionPlugin[] scanFrom(String packageName) throws IOException {
        String path = clearPath(packageName);

        ClassLoader clToUse = ClassUtils.getDefaultClassLoader();
        if (clToUse == null) {
            clToUse = PluginScanner.class.getClassLoader();
        }

        ArrayList<DefinitionPlugin> plugins = new ArrayList<>();

        Enumeration<URL> resources = clToUse.getResources(path);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            if (ResourceUtils.isJarURL(url) || ResourceUtils.isFileURL(url)) {
                parseJarFile(url, clToUse);
            } else if (ResourceUtils.isFileURL(url)) {
                // 是一个文件
                String filePath = url.getPath();
                File file = new File(filePath);
                if (!file.exists()) {
                    throw new FileNotFoundException(packageName
                            + " cannot be resolved, because it not exists in file system.");
                }

            }
        }
        return plugins.toArray(new DefinitionPlugin[plugins.size()]);
    }

    private static List<DefinitionPlugin> parseJarFile(URL url, ClassLoader loader) throws IOException {
        assert url != null;

        if (ResourceUtils.isJarFileURL(url)) {
            url = ResourceUtils.toJarURL(url);
        }

        ArrayList<DefinitionPlugin> plugins = new ArrayList<>();

        if (ResourceUtils.isJarURL(url)) {
            JarURLConnection con = (JarURLConnection) url.openConnection();
            JarFile jarFile = con.getJarFile();
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry je = entries.nextElement();
                if (isClassFile(je)) {
                    // is a class file
                    String className = toClassName(je.getName());
                    DefinitionPlugin plugin = to(className, loader);
                    if (plugin != null) {
                        plugins.add(plugin);
                    }
                }
            }
        }
        return plugins;
    }

    private static List<DefinitionPlugin> getClassesInLocation(URL url,
                                                               String packageName,
                                                               ClassLoader loader) throws IOException {
        assert ResourceUtils.isFileURL(url);

        String filePath = url.getPath();
        File file = new File(filePath);

        ArrayList<DefinitionPlugin> plugins = new ArrayList<>();

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; files != null && i < files.length; i++) {
                File child = files[i];
                String name = child.getName();
                URL childURL = createURL(url, name);
                plugins.addAll(getClassesInLocation(childURL, packageName, loader));
            }
        } else {
            String path = file.getAbsolutePath();
            if (path.endsWith(ClassUtils.CLASS_EXTENSION)) {
                // is class file
                // the path only package and class file
                // todo 确定类名称
                String location = "";
//                String location = path.substring(classpath.length() + 1);
                String className = location.substring(0, location.length() - ClassUtils.CLASS_EXTENSION.length())
                        .replaceAll(File.separator, ".");

                DefinitionPlugin plugin = to(className, loader);
                if (plugin != null) {
                    plugins.add(plugin);
                }
            } else if (ResourceUtils.isJarFile(file)) {
                // is jar file
                plugins.addAll(parseJarFile(url, loader));
            }
        }
        return plugins;
    }

    private static DefinitionPlugin to(String className, ClassLoader loader) {
        if (ClassUtils.isAvailable(className)) {
            Class<?> aClass = ClassUtils.forName(className, loader);
            if (aClass != null) {
                Plugin annotation = aClass.getAnnotation(Plugin.class);
                if (annotation != null) {
                    // is a plugin, find plugin depends
                    Class<?>[] dependencies = annotation.dependencies();
                    Constructor<?>[] constructors = aClass.getConstructors();
                    if (constructors.length != 1) {
                        for (Constructor constructor : constructors) {
                            // find starter
                            Annotation starter = constructor.getAnnotation(Starter.class);
                            if (starter != null) {
                                Class[] parameterTypes = constructor.getParameterTypes();
                                return new DefinitionPlugin() {
                                    @Override
                                    public Class<?> type() {
                                        return aClass;
                                    }

                                    @Override
                                    public Class<?>[] dependencies() {
                                        return dependencies;
                                    }

                                    @Override
                                    public Class<?>[] starterArgsType() {
                                        return parameterTypes;
                                    }
                                };
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private static URL createURL(URL url, String name) throws MalformedURLException {
        if (ResourceUtils.isJarFileURL(url)) {
            URL jarURL = ResourceUtils.toJarURL(url);
            return ResourceUtils.expandJarURL(jarURL, name);
        } else if (ResourceUtils.isJarURL(url)) {
            return new URL(url + name);
        } else if (ResourceUtils.isFileURL(url)) {
            String s = url.toString();
            if (!s.endsWith(ResourceUtils.PATH_SEPARATOR)) {
                s += ResourceUtils.PATH_SEPARATOR;
            }
            s += name;
            return new URL(s);
        }
        throw new IllegalArgumentException("Unsupported URL type: " + url);
    }

    private static boolean isClassFile(JarEntry je) {
        return !je.isDirectory() && je.getName().endsWith(ClassUtils.CLASS_EXTENSION);
    }

    private static String toClassName(String name) {
        return name.substring(0, name.length() - ClassUtils.CLASS_EXTENSION.length());
    }

    private static String clearPath(String packageName) {
        packageName = packageName.replace(ClassUtils.PACKAGE_SEPARATOR, ResourceUtils.PATH_SEPARATOR);
        if (packageName.startsWith(ResourceUtils.PATH_SEPARATOR)) {
            packageName = packageName.substring(1);
        }
        return packageName;
    }
}
