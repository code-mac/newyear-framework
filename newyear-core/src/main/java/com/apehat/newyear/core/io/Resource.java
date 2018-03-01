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

import com.apehat.newyear.util.IOUtils;
import com.apehat.newyear.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface Resource extends InputStreamSource {

    /**
     * Returns the URL of the current resource.
     *
     * @return the URL of current resource, or null if get URL failure.
     */
    URL getURL();

    /**
     * Returns the URI of the current resource.
     *
     * @return the URI of the current resource, or null if get URI failure.
     */
    default URI getURI() {
        try {
            return getURL().toURI();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * Returns the parent resource of the current resource. If the current
     * resource is at the top, will return null. i.e. if the URL of current
     * is http://www.example.com/index.html, the URL of the parent resource
     * for current resource is http://www.example.com. But the parent resource
     * of http://www.example.com must be null.
     *
     * @return the parent resource of the current resource, or null if the
     * current resource is at the top.
     */
    Resource getParent();

    /**
     * Determine whether the current resource is exists.
     *
     * @return true, current resource is exists; otherwise, false.
     */
    default boolean exists() {
        try {
            URLConnection con = getURL().openConnection();
            HttpURLConnection httpCon = (con instanceof HttpURLConnection) ? (HttpURLConnection) con : null;

            if (httpCon != null) {
                int responseCode = httpCon.getResponseCode();
                httpCon.disconnect();
                return HttpURLConnection.HTTP_OK == responseCode;
            }

            if (con != null) {
                return con.getContentLength() > 0;
            }

            getInputStream().close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Returns the name of the current resource. If the current resource is an
     * network resource, and URL like: http://www.example.com, this method
     * should return www.example.com, as the name of current resource. Else if
     * the current resource {@code isLocal()}, will return the name of current
     * file of directory.
     *
     * @return the name of current resource.
     */
    String getName();

    /**
     * Determine whether the current resource is local resource. No matter the
     * current resource is {@code exists()}, the method should can determine
     * whether the current resource is local.
     *
     * @return true, the current resource is local resource. otherwise, false.
     */
    boolean isLocal();

    /**
     * Returns the byte data of the current resource.
     *
     * @return the byte data of the current resource.
     * @throws IOException I/O Exception occur.
     * @see #getInputStream()
     * @see IOUtils#toByteArray(InputStream)
     */
    default byte[] getByteArray() throws IOException {
        return IOUtils.toByteArray(getInputStream());
    }

    /**
     * Returns the input stream of the current resource.
     *
     * @return the input stream of the current resource.
     * @throws IOException the current resource is not
     *                     exists in file system, or I/O
     *                     Exception occur.
     */
    @Override
    default InputStream getInputStream() throws IOException {
        if (!exists()) {
            throw new IOException("Cannot return the input stream of "
                    + getName() + ", because it's not exists.");
        }
        URLConnection con = getURL().openConnection();
        ResourceUtils.useCachesIfNecessary(con);
        try {
            return con.getInputStream();
        } finally {
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();
            }
        }
    }
}
