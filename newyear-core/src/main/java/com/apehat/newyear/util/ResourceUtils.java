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

package com.apehat.newyear.util;

import com.apehat.newyear.protocol.classpath.Handler;
import com.apehat.newyear.validation.Validation;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Objects;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ResourceUtils {

    /**
     * The standard path separator in Java environment: "/"
     */
    public static final String PATH_SEPARATOR = "/";
    /**
     * URL protocol for a from in classpath on this framework: "classpath"
     */
    private static final String URL_PROTOCOL_CLASSPATH = "classpath";
    /**
     * URL protocol for a file in the file system: "file"
     */
    private static final String URL_PROTOCOL_FILE = "file";
    /**
     * URL protocol for an entry from a jar file: "jar"
     */
    private static final String URL_PROTOCOL_JAR = "jar";
    /**
     * URL protocol for an entry from a war file: "war"
     */
    private static final String URL_PROTOCOL_WAR = "war";
    /**
     * URL protocol for an entry from a zip file: "zip"
     */
    private static final String URL_PROTOCOL_ZIP = "zip";
    /**
     * The default remote URL prefix: "ftp://"
     */
    private static final String DEFAULT_REMOTE_URL_PREFIX = "ftp://";
    /**
     * Custom URL prefix for loading form the class path: "classpath:"
     */
    private static final String CLASSPATH_URL_PREFIX = "classpath:";
    /**
     * URL prefix for loading from the file system: "file:"
     */
    private static final String FILE_URL_PREFIX = "file:";
    /**
     * URL prefix for loading from a jar file: "jar:"
     */
    private static final String JAR_URL_PREFIX = "jar:";
    /**
     * File extension for a regular jar file: ".jar"
     */
    private static final String JAR_EXTENSION = ".jar";
    /**
     * Separator between JAR URL and file path within the JAR: "!/"
     */
    private static final String JAR_URL_SEPARATOR = "!/";
    /**
     * The separator in URL protocol and path: ":"
     */
    private static final String PROTOCOL_PATH_SEPARATOR = ":";

    /**
     * Commons URL encoding scheme "UTF-8"
     */
    private static final String URL_ENCODING_FORMAT = "UTF-8";

    private ResourceUtils() {
    }

    /**
     * Construct a {@link URL} instance by specified {@code String}.
     *
     * @param location a {@code String} that represents the resource
     *                 location.
     * @return a newly {@code URL}, identifies the resource, in specified {@code location}.
     * @throws MalformedURLException error occur when construct URL.
     */
    public static URL getURL(String location) throws MalformedURLException {
        Objects.requireNonNull(location, "Must specify resource location");

        URL url = null;

        try {
            url = new URL(location);
        } catch (MalformedURLException e) {
            if (!location.contains(PATH_SEPARATOR)) {
                // is a package name - try to get by ClassLoader
                String path = location.replace(".", PATH_SEPARATOR);

                ClassLoader clToUse = ClassUtils.getDefaultClassLoader();
                if (clToUse == null) {
                    clToUse = ResourceUtils.class.getClassLoader();
                }

                url = clToUse.getResource(path);
                // if get success, need to convert to jar url - ClassLoader already do this
                // but need try to convert to class path url
            }

            if (url == null) {
                // if failed at there, will throw exception
                url = new File(location).toURI().toURL();

                try {
                    url = toJarURL(url);
                } catch (IllegalArgumentException ignore) {
                    // must not be jar URL
                }
            }

            // try to convert to class path URL
            assert url != null;
            try {
                url = toClassPathURL(url);
            } catch (IllegalArgumentException ignore) {
                // must not be classpath URL
            }
        }
        return url;
    }

    private static URL toClassPathURL(URL url) throws MalformedURLException {
        String s = url.toString();

        // ensure the specified URL is in class path
        String relPath = s.substring(s.indexOf(PATH_SEPARATOR));
        Validation.requireTrue(EnvUtils.isInClassPath(relPath),
                "[%s] cannot be resolved, because does not in classpath.", relPath);

        String us = s.replace(FILE_URL_PREFIX, CLASSPATH_URL_PREFIX);
        return new URL(null, us, new Handler());
    }

    /**
     * Convert specified URL to jar URL, format like: jar:...
     *
     * @param url the url to be convert
     * @return converted url
     * @throws IllegalArgumentException {@code url.toString()} non contains ".jar"
     * @throws MalformedURLException    error occur on construct URL
     */
    public static URL toJarURL(URL url) throws MalformedURLException {
        if (isJarURL(url)) {
            return url;
        }

        String location = url.toString();

        // ensure contains ".jar"
        // cannot ensure end with ".jar", because like "jar:file:/example/xxx.jar!/"
        if (!location.toLowerCase().contains(JAR_EXTENSION)) {
            throw new IllegalArgumentException(location
                    + " cannot be resolved, because it does not point to a jar.");
        }

        try {
            // by construct to clear invalid character
            URI uri = toURI(location);
            String s = uri.getScheme() + PROTOCOL_PATH_SEPARATOR + uri.getSchemeSpecificPart();

            int capacity = JAR_URL_PREFIX.length() + s.length() + JAR_URL_SEPARATOR.length();
            StringBuilder sb = new StringBuilder(capacity);

            sb.append(JAR_URL_PREFIX);

            // no protocol, like "file:", "ftp:", or "http:", etc.
            if (!uri.isAbsolute()) {
                // always have ".", at least belong to ".jar"
                if (s.indexOf(".") > s.indexOf(PATH_SEPARATOR)) {
                    // in local - if in classpath, use "classpath" as scheme, else use "file"
                    String prefix = EnvUtils.isInClassPath(location) ? CLASSPATH_URL_PREFIX : FILE_URL_PREFIX;
                    sb.append(prefix);
                } else {
                    // is remote - like: projects.example.com/xxx.jar
                    // not in local - default use "ftp:" as scheme
                    sb.append(DEFAULT_REMOTE_URL_PREFIX);
                }
            }
            sb.append(s);

            if (!location.contains(JAR_EXTENSION + JAR_URL_SEPARATOR)) {
                sb.append(JAR_URL_SEPARATOR);
            }

            return new URL(sb.toString());
        } catch (URISyntaxException e) {
            throw new MalformedURLException(e.toString());
        }
    }

    /**
     * Determine whether the specified url is file URL.
     * i.e. has protocol "file"
     *
     * @param url the url to check.
     * @return true, the specified url is a file URL, otherwise false.
     * @throws NullPointerException the specified URL is null
     */
    public static boolean isFileURL(URL url) {
        String protocol = url.getProtocol().toLowerCase();
        return URL_PROTOCOL_FILE.equals(protocol) || URL_PROTOCOL_CLASSPATH.equals(protocol);
    }

    /**
     * Determine whether the specified file is a jar file.
     * i.e. file isn't directory, and name end with ".jar"
     *
     * @param file the file to check
     * @return true, the specified file is a jar file; otherwise, false
     * @throws NullPointerException     the specified {@code File} is null
     * @throws IllegalArgumentException the specified file not exists
     */
    public static boolean isJarFile(File file) {
        Validation.requireTrue(file.exists(),
                "Cannot determine the file does not exists, at [%s}", file.getPath());
        return !file.isDirectory() && file.getName().toLowerCase().endsWith(JAR_EXTENSION);
    }

    /**
     * Determine whether the specified URL is a jar URL.
     * i.e. has protocol "jar", "war" or "zip"
     *
     * @param url the URL to check
     * @return true, the specified URL is a jar URL, otherwise, false.
     * @throws NullPointerException the specified URL is null
     */
    public static boolean isJarURL(URL url) {
        String protocol = url.getProtocol().toLowerCase();
        return URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_WAR.equals(protocol) ||
                URL_PROTOCOL_ZIP.equals(protocol);
    }

    /**
     * Determine whether the specified {@code URL} is a jar file url.
     * i.e. is file URL and has extension ".jar"
     *
     * @param url the URL to check
     * @return true, the specified URL is a jar file URL, otherwise, false.
     * @throws NullPointerException the specified URL is null
     * @see #isFileURL(URL)
     */
    public static boolean isJarFileURL(URL url) {
        return isFileURL(url) && url.getPath().toLowerCase().endsWith(JAR_EXTENSION);
    }

    public static URL expandJarURL(URL url, String entryPath) throws MalformedURLException {
        Validation.requireTrue(isJarURL(url), "[%s] isn't a jar URL.", url);
        return new URL(url, entryPath);
    }

    /**
     * Determine whether specified url is a class path url.
     * i.e. has "classpath" protocol, or point to a classpath resource location.
     *
     * @param url the URL to check
     * @return true, specified url is in a class path; otherwise, false.
     * @throws NullPointerException the specified URL is null
     */
    public static boolean isClassPathURL(URL url) {
        String us = url.toString();
        if (URL_PROTOCOL_CLASSPATH.equals(url.getProtocol()) || EnvUtils.isInClassPath(us)) {
            return true;
        }
        String[] protocols = us.substring(us.indexOf(PATH_SEPARATOR)).split(PROTOCOL_PATH_SEPARATOR);
        for (int i = 1; i < protocols.length; ++i) {
            if (URL_PROTOCOL_CLASSPATH.equals(protocols[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the real path of the specified URL.
     * i.e. if the {@code url.toString()} like: {@code file:/example/a}, will
     * return {@code /example/a}; if the {@code url.toString()} like:
     * {@code jar:file:/example/a.jar!/}, will return {@code /example/a.jar}.
     *
     * @param url the url be used to get real path
     * @return the real path.
     * @throws NullPointerException     the specified URL is null
     * @throws IllegalArgumentException the specified URL is jar URL or jar file URL with entry part.
     */
    public static String getRealPath(URL url) {
        String us = url.toString();

        Validation.requireFalse(
                (isJarURL(url) && !us.endsWith(JAR_URL_SEPARATOR)) ||
                        (isJarFileURL(url) && us.endsWith(JAR_EXTENSION)),
                "Does not support to resolve jar URL (or jar file URL) with entry part: [%s]", url);

        String path = us.substring(us.indexOf(PATH_SEPARATOR));

        if (path.startsWith("//")) {
            // is a network URL
            path = path.substring(2);
            // remove query part
            path = path.substring(0, path.indexOf("?"));
        } else if (EnvUtils.isWindows()) {
            path = path.replace(PATH_SEPARATOR, File.separator);
        }
        return path;
    }

    /**
     * Translate an url into {@code application/x-www-form-urlencoded}
     * format using "UTF-8" encoding scheme.
     *
     * @param url {@code URL} to be translate.
     * @return a translated {@code URL}.
     * @throws MalformedURLException construct the newly {@code URL} failure.
     * @see #encode(String)
     */
    public static URL encode(URL url) throws MalformedURLException {
        return new URL(encode(url.toString()));
    }

    /**
     * Translate a string into {@code application/x-www-form-urlencoded}
     * format using "UTF-8" encoding scheme.
     *
     * @param s {@code String} to be translated.
     * @return a translated {@code String}
     * @see URLEncoder#encode(String, String)
     */
    public static String encode(String s) {
        String decoded = null;
        try {
            decoded = URLEncoder.encode(s, URL_ENCODING_FORMAT);
        } catch (UnsupportedEncodingException ignore) {
        }
        return decoded;
    }

    /**
     * Decodes an {@code application/x-www-form-urlencoded} url using
     * "UTF-8" encoding scheme.
     *
     * @param url the {@code URL} to decode.
     * @return the newly decoded {@code URL}
     * @throws MalformedURLException construct the newly {@code URL} failure.
     * @see #decode(String)
     */
    public static URL decode(URL url) throws MalformedURLException {
        return new URL(decode(url.toString()));
    }

    /**
     * Decoded an {@code application/x-www-form-urlencoded} string using
     * "UTF-8" scheme.
     *
     * @param s the {@code String} to decode
     * @return the newly decoded {@code String}.
     * @see URLDecoder#decode(String, String)
     */
    public static String decode(String s) {
        String decoded = null;
        try {
            decoded = URLDecoder.decode(s, URL_ENCODING_FORMAT);
        } catch (UnsupportedEncodingException ignore) {
        }
        return decoded;
    }

    public static File getFile(URL url) {
        Objects.requireNonNull(url, "Must specified resource URL.");
        Validation.requireTrue(isFileURL(url) || isJarFileURL(url),
                "[%s] cannot be resolved to absolute file path, because it isn't a file", url);
        try {
            return new File(toURI(url).getSchemeSpecificPart());
        } catch (URISyntaxException e) {
            return new File(url.getFile());
        }
    }

    private static URI toURI(URL url) throws URISyntaxException {
        return toURI(url.toString());
    }

    private static URI toURI(String location) throws URISyntaxException {
        return new URI(location.replace(" ", "%20"));
    }

    public static void useCachesIfNecessary(URLConnection con) {
        con.setUseCaches(con.getClass().getSimpleName().startsWith("JNLP"));
    }
}
