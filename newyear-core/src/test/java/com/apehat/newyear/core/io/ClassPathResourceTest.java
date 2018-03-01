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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ClassPathResourceTest {

    private String jarFileURLPath = "jar:file:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar!/com/intellij" +
            "/rt/ant/execution/PacketWriter.class";

    private String fileURLPath = "file:/Users/hanpengfei/apehat-projects/newyear-framework/newyear-core/out/production" +
            "/classes/com/apehat/newyear/core/env/Bootstrap.class";

    private URL jarFileURL;

    private URL fileURL;

    @BeforeMethod
    public void setUp() throws Exception {
        jarFileURL = new URL(jarFileURLPath);
        fileURL = new URL(fileURLPath);
    }

    @Test
    public void testOf() throws Exception {
        Resource resource = ClassPathResource.of(fileURL);
        boolean exists = resource.exists();
        System.out.println(exists);
    }

    @Test
    public void testOf1() throws Exception {
    }

    @Test
    public void testGetPackageName() throws Exception {
    }

    @Test
    public void testIsClassFile() throws Exception {
    }

    @Test
    public void testGetClassPath() throws Exception {
    }

    @Test
    public void testGetURI() throws Exception {
    }

    @Test
    public void testGetParent() throws Exception {
    }
}