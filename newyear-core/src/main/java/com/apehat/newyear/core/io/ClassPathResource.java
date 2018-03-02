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

import com.apehat.newyear.validation.annotation.NonNull;
import com.apehat.newyear.validation.annotation.Nullable;
import com.apehat.newyear.util.ClassUtils;
import com.apehat.newyear.util.ResourceUtils;
import com.apehat.newyear.validation.Validation;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ClassPathResource extends FileSystemResource {

    @NonNull
    private final String classPath;

    @Nullable
    private final URI uri;

    private ClassPathResource(URL url, URI uri, String classPath) {
        super(url);
        this.classPath = classPath;
        this.uri = uri;
    }

    /**
     * Create a class path resource by specified uri.
     *
     * @param uri the uri to be used create a class path resource
     * @return a class path instance
     * @throws MalformedURLException    If a protocol handler for the URL could not
     *                                  be found, or if some other error occurred
     *                                  while constructing the URL
     * @throws IllegalArgumentException the specified uri does not belong to any class path.
     */
    public static ClassPathResource of(URI uri) throws MalformedURLException {
        URL url = uri.toURL();
        Validation.requireTrue(ResourceUtils.isClassPathURL(url),
                "[%s] invalid, because it does not belong to any class path", uri);
        return new ClassPathResource(url, uri, ResourceUtils.getRealPath(url));
    }

    /**
     * Create a class path resource by specified url.
     *
     * @param url the url to be used create a class path resource
     * @return a class path instance
     * @throws IllegalArgumentException the specified url does not belong to any class path.
     */
    public static ClassPathResource of(URL url) {
        Validation.requireTrue(ResourceUtils.isClassPathURL(url),
                "[%s] invalid, because it does not belong to any class path", url);
        return new ClassPathResource(url, null, ResourceUtils.getRealPath(url));
    }

    /**
     * Returns the package name of the current resource, if the current
     * resource {@code isClassFile()}. Before call the method, you should
     * call {@link #isClassFile()}, ensure the current resource is a class
     * file. Otherwise, if the current resource sin't class file, will throw
     * {@link IllegalStateException}.
     *
     * @return the package name of current class file.
     * @throws IllegalStateException the current resource isn't class file.
     * @see #isClassFile()
     */
    public String getPackageName() {
        String name = getName();

        if (!isClassFile()) {
            throw new IllegalStateException(name + " isn't a class file.");
        }

        String absolutePath = getAbsolutePath();
        String classPath = getClassPath();

        if (absolutePath.equals(classPath)) {
            return "";
        }

        int length = classPath.length();

        assert absolutePath.startsWith(classPath);
        assert absolutePath.length() > length;

        String filePath = absolutePath.substring(length);
        String packagePath = filePath.substring(0, name.length() + 1);
        return packagePath.replace(File.separator, ClassUtils.PACKAGE_SEPARATOR);
    }

    /**
     * Determine whether the current resource is a class file.
     *
     * @return true, the current resource is a class file;
     * otherwise, false.
     */
    public boolean isClassFile() {
        return ClassUtils.CLASS_EXTENSION.equalsIgnoreCase(getExtension());
    }

    /**
     * Returns the classpath of the current resource.
     *
     * @return the classpath of current resource
     */
    @NonNull
    public String getClassPath() {
        return classPath;
    }

    @Override
    public URI getURI() {
        if (uri == null) {
            return super.getURI();
        }
        return uri;
    }

    /**
     * Returns the parent resource of the current resource. If the parent resource still
     * in the class path of the current resource, will return a {@code ClassPathResource}
     * instance, otherwise, will return a {@code FileSystemResource} instance.
     * <p>
     * This method in current class will not return a null, because the class path must not
     * is like "/" or "C:\".
     *
     * @return the parent resource of current resource.
     */
    @Override
    public FileSystemResource getParent() {
        FileSystemResource resource = null;
        File parentFile = getFile().getParentFile();

        assert parentFile != null;

        String path = parentFile.getAbsolutePath();

        URL url = null;
        try {
            url = ResourceUtils.getURL(path);
            resource = ClassPathResource.of(url);
        } catch (IllegalArgumentException e) {
            assert url != null;
            return new FileSystemResource(url);
        } catch (MalformedURLException e) {
            // to return a file system instance
        }
        return resource;
    }
}
