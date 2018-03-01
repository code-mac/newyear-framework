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

import com.apehat.newyear.lang.NonNull;
import com.apehat.newyear.lang.Nullable;
import com.apehat.newyear.util.ResourceUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class FileSystemResource extends AbstractResource {

    /**
     * The url of the current resource.
     */
    @NonNull
    private final URL url;

    @Nullable
    private final URI uri;

    /**
     * The source of the current resource.
     */
    @Nullable
    private final File file;

    /**
     * The absolute path of the current resource
     */
    @Nullable
    private final String absolutePath;

    protected FileSystemResource(URL url) {
        this.url = url;
        this.uri = null;
        this.file = null;
        this.absolutePath = null;
    }

    protected FileSystemResource(File file) throws MalformedURLException {
        this.file = file;
        this.uri = file.toURI();
        this.url = uri.toURL();
        this.absolutePath = file.getAbsolutePath();
    }

    protected FileSystemResource(URI uri) throws MalformedURLException {
        this(new File(uri));
    }

    /**
     * Returns the absolute path of the current resource.
     *
     * @return the absolute path of the current resource.
     */
    @NonNull
    public String getAbsolutePath() {
        if (absolutePath == null) {
            return ResourceUtils.getRealPath(url);
        }
        return absolutePath;
    }

    /**
     * Determine whether the current resource is exists in the file system.
     *
     * @return true, the current resource exists in the file system;
     * otherwise, false.
     * @see #getFile()
     * @see File#exists()
     */
    @Override
    public boolean exists() {
        return getFile().exists();
    }

    @Override
    public URL getURL() {
        return url;
    }

    /**
     * Returns the parent resource of the current resource.
     *
     * @return the parent resource of the current resource, or null if
     * current resource is "/" (in Unix, Linux, MacOS) (or like "C:\"
     * in Windows).
     */
    @Override
    public FileSystemResource getParent() {
        File parentFile = file.getParentFile();
        try {
            return parentFile != null ? new FileSystemResource(file) : null;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Each file system resource must be a local resource.
     *
     * @return true
     */
    @Override
    public boolean isLocal() {
        return true;
    }

    /**
     * Returns the current resource name, Corresponding to the file name or
     * directory name.
     *
     * @return the name of current resource.
     * @see #getFile()
     * @see File#getName()
     */
    @Override
    public String getName() {
        return getFile().getName();
    }

    /**
     * Returns the file instance of the current resource.
     *
     * @return the file instance of the current resource.
     */
    public File getFile() {
        if (file == null) {
            return new File(getAbsolutePath());
        }
        return file;
    }

    /**
     * Determine whether the current resource is a file. If you need to
     * be accurate, be call the method, should call {@link #exists()},
     * because, if the current resource does not exists, we can't determine
     * whether the current resource is a file.
     *
     * @return true, current resource exists and is a file; or false
     * if the current resource not exists or isn't file..
     * @see #getFile()
     * @see File#isFile()
     */
    public boolean isFile() {
        return getFile().isFile();
    }

    /**
     * Determine whether the current resource is a jar file.
     *
     * @return true, if current resource is a jar file, Otherwise, false.
     * @throws IllegalStateException if the current resource not exits.
     */
    public boolean isJarFile() {
        if (!exists()) {
            return false;
        }
        if (ResourceUtils.isJarFile(getFile())) {
            return true;
        }
        // determine by URL
        URL url = getURL();
        return url != null && ResourceUtils.isJarURL(url);
    }

    /**
     * Returns the extension of the current resource. If current isn't file,
     * will return an empty string. otherwise, for example the name of current
     * resource is example.class, then will return ".class".
     *
     * @return the extension of the current resource, or empty string if the
     * current resource isn't file or does not have extension.
     * @see #getName()
     */
    public String getExtension() {
        if (isFile()) {
            String name = getName();
            int idx = name.lastIndexOf(".");
            return idx == -1 ? name : name.substring(0, idx);
        }
        return "";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!exists()) {
            throw new FileNotFoundException("Cannot get input stream of " + getName() + ", because it not exists");
        }
        return new FileInputStream(getFile());
    }
}
