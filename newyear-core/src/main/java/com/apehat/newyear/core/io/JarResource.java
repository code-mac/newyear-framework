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

package com.apehat.newyear.core.io;

import com.apehat.newyear.util.ResourceUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class JarResource extends AbstractResource {

    private final URL url;
    private final JarFile jarFile;
    private final JarEntry jarEntry;

    // 一个本地或远程 Jar 资源
    public JarResource(URL url) {
        try {
            this.url = ResourceUtils.toJarURL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(url + " isn't a jar URL.");
        }

        this.jarFile = null;
        this.jarEntry = null;
    }

    // 一个本地的 JarFile 资源
    public JarResource(JarFile jarFile) {
        this.jarFile = jarFile;
        this.jarEntry = null;
        this.url = null;
    }

    public JarResource(JarEntry jarEntry) {
        this.url = null;
        this.jarEntry = jarEntry;
        this.jarFile = null;
    }

    @Override
    public URL getURL() {
        return url;
    }

    @Override
    public Resource getParent() {
        // 是 entry, 就检查他的名字，返回一个 entry 或 jarFile 的实例
        // 如果是 jarFile， 就返回他所在的文件夹
        return null;
    }

    @Override
    public String getName() {
        if (jarFile != null) {
            return jarFile.getName();
        }
        if (jarEntry != null) {
            return jarEntry.getName();
        }
        // 根据 URL 获取当前名称
        return null;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    /**
     * Determine whether the current res is a jar file.
     *
     * @return true, if current res is a jar file, Otherwise, false.
     * @throws IllegalStateException if the current res not exits.
     */
    public boolean isJarFile() {
        return jarFile != null;

//        if (!exists()) {
//            throw new IllegalStateException("Cannot determine whether the ["
//                    + getName() + "] is jar file, because it does not exists.");
//        }
//         determine by URL
//        URL url = getURL();
//        if (url != null) {
//            return ResourceUtils.isJarURL(url);
//        }
//         will cannot get URL... determine by file type and extension
//        return isFile() && ResourceUtils.JAR_EXTENSION.equalsIgnoreCase(getExtension());
    }
}
