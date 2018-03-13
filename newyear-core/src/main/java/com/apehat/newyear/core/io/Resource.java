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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface Resource extends InputStreamSource {

    /**
     * Returns the URL of the current res.
     *
     * @return the URL of current res, or null if get URL failure.
     */
    URL getURL();

    /**
     * Returns the URI of the current res.
     *
     * @return the URI of the current res, or null if get URI failure.
     */
    default URI getURI() {
        try {
            return getURL().toURI();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * Returns the parent res of the current res. If the current
     * res is at the top, will return null. i.e. if the URL of current
     * is http://www.example.com/index.html, the URL of the parent res
     * for current res is http://www.example.com. But the parent res
     * of http://www.example.com must be null.
     *
     * @return the parent res of the current res, or null if the
     * current res is at the top.
     */
    Resource getParent();

    /**
     * Returns the children resources, if the current res have children. Or
     * 0 length res array, if the current does not have any children.
     *
     * @return the children of the current res.
     * @throws IllegalStateException if the current res cannot expand.
     */
    default Resource[] expand() {
        return new Resource[0];
    }

    /**
     * Determine whether the current res is exists.
     *
     * @return true, current res is exists; otherwise, false.
     */
    default boolean exists() {
        try {
            URLConnection con = getURL().openConnection();
            HttpURLConnection httpCon = (con instanceof HttpURLConnection) ?
                    (HttpURLConnection) con : null;

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
     * Returns the name of the current res. If the current res is an
     * network res, and URL like: http://www.example.com, this method
     * should return www.example.com, as the name of current res. Else if
     * the current res {@code isLocal()}, will return the name of current
     * file of directory.
     *
     * @return the name of current res, or mepty string if current res
     * don't have name.
     */
    String getName();

    /**
     * Determine whether the current res is local res. No matter the
     * current res is {@code exists()}, the method should can determine
     * whether the current res is local.
     *
     * @return true, the current res is local res. otherwise, false.
     */
    boolean isLocal();

    /**
     * Returns the byte data of the current res.
     *
     * @return the byte data of the current res.
     * @throws IOException I/O Exception occur.
     * @see #getInputStream()
     * @see IOUtils#readAllBytes(InputStream)
     */
    default byte[] getByteArray() throws IOException {
        return IOUtils.readAllBytes(getInputStream());
    }

    /**
     * Returns the input stream of the current res.
     *
     * @return the input stream of the current res.
     * @throws IOException the current res is not exists,
     *                     or if an I/O exception occurs.
     */
    @Override
    default InputStream getInputStream() throws IOException {
        if (!exists()) {
            throw new FileNotFoundException("Cannot return the input stream of "
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
