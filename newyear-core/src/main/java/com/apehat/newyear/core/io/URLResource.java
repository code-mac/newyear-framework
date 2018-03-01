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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class URLResource implements InputStreamSource {

    /**
     * The URL of current resource.
     */
    private final URL url;

    public URLResource(URL url) {
        this.url = Objects.requireNonNull(url, "Must specified url.");
    }

    public boolean exists() {
        try {
            if (ResourceUtils.isFileURL(url)) {
                return getFile().exists();
            } else {
                URLConnection con = url.openConnection();
                HttpURLConnection httpCon = (con instanceof HttpURLConnection) ? (HttpURLConnection) con : null;
                if (httpCon != null) {
                    int code = httpCon.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        return true;
                    } else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                        return false;
                    }
                }
                if (con.getContentLength() > 0) {
                    return true;
                }
                if (httpCon != null) {
                    httpCon.disconnect();
                    return false;
                } else {
                    InputStream in = getInputStream();
                    in.close();
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection con = url.openConnection();
        ResourceUtils.useCachesIfNecessary(con);
        try {
            return con.getInputStream();
        } finally {
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();
            }
        }
    }

    public URL getURL() {
        return url;
    }

    public File getFile() throws IOException {
        return ResourceUtils.getFile(getURL());
    }
}
