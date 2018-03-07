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

package com.apehat.newyear;

import com.apehat.newyear.protocol.classpath.Handler;
import com.apehat.newyear.util.ClassUtils;
import com.apehat.newyear.util.ResourceUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class SimpleTest {

    @BeforeClass
    public static void setUp() {
//        URL.setURLStreamHandlerFactory(protocol -> "classpath".equals(protocol) ? new Handler() : null);
    }

    @Test
    public void testExpand() throws MalformedURLException {
        String rt = "jar:classpath:/Applications/IntelliJ%20IDEA.app/Contents/lib/idea_rt.jar!/";
        URL url = new URL(null, rt, new Handler());
        System.out.println(url);
        URL url1 = ResourceUtils.expandJarURL(url, "hello");
        System.out.println(url1);

        String a = "../../../../test";
        File file = new File(a);
        System.out.println(file.exists());
        System.out.println(file.getAbsolutePath());
    }

    @Test
    public void testGetResource() {
        ClassLoader cl = ClassUtils.getDefaultClassLoader();

        URL url = cl.getResource("com/apehat/newyear/core/annotation/Starter.class");
        assert url != null;
        String path = url.getPath();
        File file = new File(path);
        assert file.exists();

        System.out.println(url.getFile());
        System.out.println(file.getPath());
    }

    @Test
    public void testIsFile() {
        File file = new File("/123.txt");
        System.out.println(file.exists());
        System.out.println(file.isFile());
    }

    @Test
    public void test() throws IOException {

        String rt = "/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar";
        String cp = "/Users/hanpengfei/apehat-projects/newyear-framework/newyear-core/out/production/classes";

        String rtc = "com.intellij.rt.ant.execution.PacketWriter.class";
        String cpc = "com.apehat.newyear.core.env.Bootstrap.class";

        Class<?>[] classes = ClassUtils.getClassesInLocation(cp);
        for (Class<?> aClass : classes) {
            System.out.println(aClass);
        }
    }

    @Test
    public void testCreateURL() throws MalformedURLException, URISyntaxException {
        String rt = "jar:classpath:/Applications/IntelliJ%20IDEA.app/Contents/lib/idea_rt.jar!/";
        URI uri = new URI(rt);
        System.out.println(uri);
        System.out.println(uri.isAbsolute());
        URL url = uri.toURL();
        System.out.println(url);
    }

    @Test
    public void testGetResources() {
        URL resource = getClass().getClassLoader().getResource("org/slf4j");
        System.out.println(resource);
    }

    @Test
    public void testJarURL() throws MalformedURLException, URISyntaxException {
        String rt = "jar:file:/Applications/IntelliJ%20IDEA.app/Contents/lib/idea_rt.jar!/";
        URL url = new URL(rt);
        URI uri = url.toURI();
        System.out.println(uri.getScheme());
        System.out.println(uri.getSchemeSpecificPart());
        System.out.println(uri.getRawSchemeSpecificPart());
    }
}
